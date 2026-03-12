package com.optimumtransfer.application.config;

import com.optimumtransfer.application.SolveMode;
import com.optimumtransfer.application.SolverRequest;
import com.optimumtransfer.constraints.TransferConstraint;
import com.optimumtransfer.constraints.TransferConstraints;
import com.optimumtransfer.goals.EvenDistributionGoal;
import com.optimumtransfer.goals.ExactMatchGoal;
import com.optimumtransfer.goals.GoalCondition;
import com.optimumtransfer.goals.SimpleGoalParser;
import com.optimumtransfer.goals.SingleContainerGoal;
import com.optimumtransfer.heuristics.EvenDistributionHeuristic;
import com.optimumtransfer.heuristics.Heuristic;
import com.optimumtransfer.heuristics.SingleContainerHeuristic;
import com.optimumtransfer.heuristics.TotalVolumeHeuristic;
import com.optimumtransfer.heuristics.ZeroHeuristic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class SolverRequestPropertiesLoader {
    private static final String CAPACITIES_KEY = "capacities";
    private static final String START_VOLUMES_KEY = "startVolumes";
    private static final String SOLVE_MODE_KEY = "solveMode";
    private static final String MAX_DEPTH_KEY = "maxDepth";
    private static final String GOAL_TYPE_KEY = "goal.type";
    private static final String GOAL_TARGET_VOLUMES_KEY = "goal.targetVolumes";
    private static final String GOAL_CONTAINER_INDEX_KEY = "goal.containerIndex";
    private static final String GOAL_DESIRED_VOLUME_KEY = "goal.desiredVolume";
    private static final String GOAL_EXPRESSION_KEY = "goal.expression";
    private static final String HEURISTIC_TYPE_KEY = "heuristic.type";
    private static final String HEURISTIC_TARGET_INDEX_KEY = "heuristic.targetIndex";
    private static final String HEURISTIC_TARGET_VOLUME_KEY = "heuristic.targetVolume";
    private static final String HEURISTIC_GOAL_SUM_KEY = "heuristic.goalSum";
    private static final String BLOCK_ROUTES_KEY = "constraints.blockRoutes";
    private static final String MAX_TRANSFER_KEY = "constraints.maxTransfer";
    private static final String MIN_TRANSFER_KEY = "constraints.minTransfer";
    private static final String BLOCK_RECEIVING_KEY = "constraints.blockReceiving";
    private static final String ONLY_EVEN_SENDERS_KEY = "constraints.onlyEvenSenders";
    private static final String DEFAULT_GOAL_TYPE = "EXACT_MATCH";
    private static final String DEFAULT_HEURISTIC_TYPE = "ZERO";

    public SolverRequest load(Path path) throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(path)) {
            properties.load(inputStream);
        }
        return fromProperties(properties);
    }

    public SolverRequest fromProperties(Properties properties) {
        int[] capacities = parseIntArray(required(properties, CAPACITIES_KEY));
        int[] startVolumes = parseIntArray(required(properties, START_VOLUMES_KEY));
        if (capacities.length != startVolumes.length) {
            throw new IllegalArgumentException("capacities and startVolumes must have the same length.");
        }

        GoalCondition goal = buildGoal(properties, capacities.length);
        Heuristic heuristic = buildHeuristic(properties);
        List<TransferConstraint> constraints = buildConstraints(properties);
        SolveMode solveMode = SolveMode.valueOf(readUpperCase(properties, SOLVE_MODE_KEY, SolveMode.SHORTEST_PATH.name()));
        int maxDepth = Integer.parseInt(properties.getProperty(MAX_DEPTH_KEY, String.valueOf(SolverRequest.UNBOUNDED_DEPTH)).trim());

        return new SolverRequest(capacities, startVolumes, goal, constraints, heuristic, solveMode, maxDepth);
    }

    private GoalCondition buildGoal(Properties properties, int containerCount) {
        String goalType = readUpperCase(properties, GOAL_TYPE_KEY, DEFAULT_GOAL_TYPE);
        return switch (goalType) {
            case "EXACT_MATCH" -> new ExactMatchGoal(parseIntArray(required(properties, GOAL_TARGET_VOLUMES_KEY)));
            case "SINGLE_CONTAINER" -> new SingleContainerGoal(
                    Integer.parseInt(required(properties, GOAL_CONTAINER_INDEX_KEY)),
                    Integer.parseInt(required(properties, GOAL_DESIRED_VOLUME_KEY))
            );
            case "EVEN_DISTRIBUTION" -> new EvenDistributionGoal();
            case "EXPRESSION" -> SimpleGoalParser.parse(required(properties, GOAL_EXPRESSION_KEY), containerCount);
            default -> throw new IllegalArgumentException("Unsupported goal.type: " + goalType);
        };
    }

    private Heuristic buildHeuristic(Properties properties) {
        String heuristicType = readUpperCase(properties, HEURISTIC_TYPE_KEY, DEFAULT_HEURISTIC_TYPE);
        return switch (heuristicType) {
            case "ZERO" -> new ZeroHeuristic();
            case "EVEN_DISTRIBUTION" -> new EvenDistributionHeuristic();
            case "SINGLE_CONTAINER" -> new SingleContainerHeuristic(
                    Integer.parseInt(required(properties, HEURISTIC_TARGET_INDEX_KEY)),
                    Integer.parseInt(required(properties, HEURISTIC_TARGET_VOLUME_KEY))
            );
            case "TOTAL_VOLUME" -> new TotalVolumeHeuristic(Integer.parseInt(required(properties, HEURISTIC_GOAL_SUM_KEY)));
            default -> throw new IllegalArgumentException("Unsupported heuristic.type: " + heuristicType);
        };
    }

    private List<TransferConstraint> buildConstraints(Properties properties) {
        List<TransferConstraint> constraints = new ArrayList<>();

        String blockRoutes = properties.getProperty(BLOCK_ROUTES_KEY, "").trim();
        if (!blockRoutes.isEmpty()) {
            for (String route : splitCsv(blockRoutes)) {
                String[] parts = route.split(">");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid blocked route: " + route);
                }
                int from = Integer.parseInt(parts[0].trim());
                int to = Integer.parseInt(parts[1].trim());
                constraints.add(TransferConstraints.blockRoute(from, to));
            }
        }

        addThresholdConstraint(properties, MAX_TRANSFER_KEY, constraints, TransferConstraints::maxTransfer);
        addThresholdConstraint(properties, MIN_TRANSFER_KEY, constraints, TransferConstraints::minTransfer);

        String blockedReceivers = properties.getProperty(BLOCK_RECEIVING_KEY, "").trim();
        if (!blockedReceivers.isEmpty()) {
            for (String receiver : splitCsv(blockedReceivers)) {
                constraints.add(TransferConstraints.blockReceiver(Integer.parseInt(receiver)));
            }
        }

        if (Boolean.parseBoolean(properties.getProperty(ONLY_EVEN_SENDERS_KEY, "false"))) {
            constraints.add(TransferConstraints.onlyEvenSenders());
        }

        return constraints;
    }

    private void addThresholdConstraint(Properties properties,
                                        String key,
                                        List<TransferConstraint> constraints,
                                        java.util.function.IntFunction<TransferConstraint> factory) {
        String value = properties.getProperty(key);
        if (value != null && !value.isBlank()) {
            constraints.add(factory.apply(Integer.parseInt(value.trim())));
        }
    }

    private int[] parseIntArray(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private List<String> splitCsv(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .toList();
    }

    private String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required property: " + key);
        }
        return value.trim();
    }

    private String readUpperCase(Properties properties, String key, String defaultValue) {
        return properties.getProperty(key, defaultValue).trim().toUpperCase(Locale.ROOT);
    }
}
