package com.optimumtransfer.extras;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class GridPathSolver {
    private static final String START = "Door";
    private static final String DESTINATION = "Fridge";
    private static final int BLOCKED_DISTANCE = 10;
    private static final double RISKY_JUMP_DISTANCE = 5.5;
    private static final double SAFE_JUMP_PROBABILITY = 0.8;
    private static final int UNREACHABLE_COST = Integer.MAX_VALUE;

    static class Node implements Comparable<Node> {
        String name;
        int cost;
        List<String> path;

        Node(String n, int c, List<String> p) {
            name = n;
            cost = c;
            path = new ArrayList<>(p);
            path.add(name);
        }

        @Override
        public int compareTo(Node o) {
            return Integer.compare(cost, o.cost);
        }
    }

    private static final String[] labels = {
            "Door", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "Fridge"
    };

    private static final int[][] distances = {
            {0, 2, 3, 4, 8,10,10,10,10,10,10,10,10,10,10,10},
            {2, 0, 5, 3, 7,10,10,10,10,10,10,10,10,10,10,10},
            {3, 5, 0, 6,10, 6,10,10,10,10,10,10,10,10,10,10},
            {4, 3, 6, 0, 5, 6, 4, 5,10,10,10,10,10,10,10,10},
            {8, 7,10, 5, 0, 9, 3, 6, 3, 9,10,10,10,10,10,10},
            {10,10, 6, 6, 9, 0, 6, 4,10, 5, 9,10,10,10,10,10},
            {10,10,10, 4, 3, 6, 0, 2, 4,10, 5,10,10,10,10,10},
            {10,10,10, 5, 6, 4, 2, 0,10, 3, 6,10,10,10,10,10},
            {10,10,10,10, 3,10, 4,10, 0, 3, 5, 6, 5,10,10,10},
            {10,10,10,10, 9, 5,10, 3, 3, 0, 5,10,10, 6,10,10},
            {10,10,10,10,10, 9, 5, 6, 5, 5, 0, 7, 4, 3,10,10},
            {10,10,10,10,10,10,10,10, 6,10, 7, 0, 5,10, 6, 2},
            {10,10,10,10,10,10,10,10, 5,10, 4, 5, 0, 5, 4, 6},
            {10,10,10,10,10,10,10,10,10, 6, 3,10, 5, 0, 7,10},
            {10,10,10,10,10,10,10,10,10,10,10, 6, 4, 7, 0, 7},
            {10,10,10,10,10,10,10,10,10,10,10, 2, 6,10, 7, 0}
    };

    public static void findShortestPath(String name, int maxJump, boolean reportFallProbability) {
        Map<String, Integer> dist = new HashMap<>();
        Map<String, List<String>> paths = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>();

        for (String label : labels) {
            dist.put(label, UNREACHABLE_COST);
        }
        dist.put(START, 0);
        pq.add(new Node(START, 0, new ArrayList<>()));

        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            int idx = getIndex(cur.name);

            if (cur.cost > dist.get(cur.name)) {
                continue;
            }

            for (int i = 0; i < labels.length; i++) {
                int d = distances[idx][i];
                if (d <= maxJump && d < BLOCKED_DISTANCE) {
                    int newCost = cur.cost + d;
                    if (newCost < dist.get(labels[i])) {
                        dist.put(labels[i], newCost);
                        pq.add(new Node(labels[i], newCost, cur.path));
                        paths.put(labels[i], new ArrayList<>(cur.path));
                    }
                }
            }
        }

        System.out.println("\n=== " + name + " ===");
        int shortestCost = dist.get(DESTINATION);
        if (shortestCost == UNREACHABLE_COST) {
            System.out.println("No path found to " + DESTINATION + ".");
            return;
        }

        System.out.println("Shortest Cost to Fridge: " + shortestCost);
        List<String> fullPath = new ArrayList<>(paths.getOrDefault(DESTINATION, new ArrayList<>()));
        fullPath.add(DESTINATION);
        System.out.println("Path: " + String.join(" -> ", fullPath));

        if (reportFallProbability) {
            int riskyJumps = countRiskyJumps(fullPath);
            double probabilityMakesIt = Math.pow(SAFE_JUMP_PROBABILITY, riskyJumps);
            double probabilityFalls = 1 - probabilityMakesIt;
            System.out.printf("Probability Blake falls on this path: %4f%n", probabilityFalls);
        }
    }

    private static int countRiskyJumps(List<String> path) {
        int riskyJumps = 0;
        for (int i = 1; i < path.size(); i++) {
            int from = getIndex(path.get(i - 1));
            int to = getIndex(path.get(i));
            if (distances[from][to] > RISKY_JUMP_DISTANCE) {
                riskyJumps++;
            }
        }
        return riskyJumps;
    }

    private static int getIndex(String s) {
        for (int i = 0; i < labels.length; i++) {
            if (s.equals(labels[i])) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid label: " + s);
    }

    public static void main(String[] args) {
        findShortestPath("Ariane", 7, false);
        findShortestPath("Blake", 9, true);
    }
}
