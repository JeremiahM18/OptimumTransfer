package com.optimumtransfer.application.config;

import com.optimumtransfer.application.SolveMode;
import com.optimumtransfer.application.SolverRequest;
import com.optimumtransfer.constraints.TransferConstraint;
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
import java.util.Properties;

public class SolverRequestPropertiesLoader {
    public SolverRequest load(Path path) throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(path)) {
            properties.load(inputStream);
        }
        return fromProperties(properties);
    }

    public SolverRequest fromProperties(Properties properties) {
        int[] capacities = parseIntArray(required(properties, "capacities"));
        int[] startVolumes = parseIntArray(required(properties, "startVolumes"));
        if (capacities.length != startVolumes.length) {
            throw new IllegalArgumentException("capacities and startVolumes must have the same length.");
        }

        GoalCondition goal = buildGoal(properties, capacities.length);
        Heuristic heuristic = buildHeuristic(properties);
        List<TransferConstraint> constraints = buildConstraints(properties);
        SolveMode solveMode = SolveMode.valueOf(properties.getProperty("solveMode", SolveMode.SHORTEST_PATH.name()).trim().toUpperCase());
        int maxDepth = Integer.parseInt(properties.getProperty("maxDepth", String.valueOf(Integer.MAX_VALUE)).trim());

        return new SolverRequest(capacities, startVolumes, goal, constraints, heuristic, solveMode, maxDepth);
    }

    private GoalCondition buildGoal(Properties properties, int containerCount) {
        String goalType = properties.getProperty("goal.type", "EXACT_MATCH").trim().toUpperCase();
        return switch (goalType) {
            case "EXACT_MATCH" -> new ExactMatchGoal(parseIntArray(required(properties, "goal.targetVolumes")));
            case "SINGLE_CONTAINER" -> new SingleContainerGoal(
                    Integer.parseInt(required(properties, "goal.containerIndex")),
                    Integer.parseInt(required(properties, "goal.desiredVolume"))
            );
            case "EVEN_DISTRIBUTION" -> new EvenDistributionGoal();
            case "EXPRESSION" -> SimpleGoalParser.parse(required(properties, "goal.expression"), containerCount);
            default -> throw new IllegalArgumentException("Unsupported goal.type: " + goalType);
        };
    }

    private Heuristic buildHeuristic(Properties properties) {
        String heuristicType = properties.getProperty("heuristic.type", "ZERO").trim().toUpperCase();
        return switch (heuristicType) {
            case "ZERO" -> new ZeroHeuristic();
            case "EVEN_DISTRIBUTION" -> new EvenDistributionHeuristic();
            case "SINGLE_CONTAINER" -> new SingleContainerHeuristic(
                    Integer.parseInt(required(properties, "heuristic.targetIndex")),
                    Integer.parseInt(required(properties, "heuristic.targetVolume"))
            );
            case "TOTAL_VOLUME" -> new TotalVolumeHeuristic(Integer.parseInt(required(properties, "heuristic.goalSum")));
            default -> throw new IllegalArgumentException("Unsupported heuristic.type: " + heuristicType);
        };
    }

    private List<TransferConstraint> buildConstraints(Properties properties) {
        List<TransferConstraint> constraints = new ArrayList<>();

        String blockRoutes = properties.getProperty("constraints.blockRoutes", "").trim();
        if (!blockRoutes.isEmpty()) {
            for (String route : splitCsv(blockRoutes)) {
                String[] parts = route.split(">");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid blocked route: " + route);
                }
                int from = Integer.parseInt(parts[0].trim());
                int to = Integer.parseInt(parts[1].trim());
                constraints.add((state, f, t, amt) -> !(f == from && t == to));
            }
        }

        String maxTransfer = properties.getProperty("constraints.maxTransfer");
        if (maxTransfer != null && !maxTransfer.isBlank()) {
            int max = Integer.parseInt(maxTransfer.trim());
            constraints.add((state, f, t, amt) -> amt <= max);
        }

        String minTransfer = properties.getProperty("constraints.minTransfer");
        if (minTransfer != null && !minTransfer.isBlank()) {
            int min = Integer.parseInt(minTransfer.trim());
            constraints.add((state, f, t, amt) -> amt >= min);
        }

        String blockedReceivers = properties.getProperty("constraints.blockReceiving", "").trim();
        if (!blockedReceivers.isEmpty()) {
            for (String receiver : splitCsv(blockedReceivers)) {
                int blocked = Integer.parseInt(receiver.trim());
                constraints.add((state, f, t, amt) -> t != blocked);
            }
        }

        if (Boolean.parseBoolean(properties.getProperty("constraints.onlyEvenSenders", "false"))) {
            constraints.add((state, f, t, amt) -> f % 2 == 0);
        }

        return constraints;
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
}
