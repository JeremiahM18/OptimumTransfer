import java.util.*;

public class GridPathSolver {

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
            //Door A B C D  E  F  G  H  I  J  K  L  M  N  F
            {0, 2, 3, 4, 8,10,10,10,10,10,10,10,10,10,10,10}, // Door
            {2, 0, 5, 3, 7,10,10,10,10,10,10,10,10,10,10,10}, // A
            {3, 5, 0, 6,10, 6,10,10,10,10,10,10,10,10,10,10}, // B
            {4, 3, 6, 0, 5, 6, 4, 5,10,10,10,10,10,10,10,10}, // C
            {8, 7,10, 5, 0, 3,10,10,10,10,10,10,10,10,10,10}, // D
            {10,10, 6, 6, 3, 0, 5, 3, 4,10,10,10,10,10,10,10}, // E
            {10,10,10, 4,10, 5, 0, 3, 5,10,10,10,10,10,10,10}, // F
            {10,10,10, 5,10, 3, 3, 0, 3, 4,10,10,10,10,10,10}, // G
            {10,10,10,10,10, 4, 5, 3, 0, 3, 4,10,10,10,10,10}, // H
            {10,10,10,10,10,10,10, 4, 3, 0, 3, 4,10,10,10,10}, // I
            {10,10,10,10,10,10,10,10, 4, 3, 0, 3, 4,10,10,10}, // J
            {10,10,10,10,10,10,10,10,10, 4, 3, 0, 3, 4,10,10}, // K
            {10,10,10,10,10,10,10,10,10,10, 4, 3, 0, 3, 4,10}, // L
            {10,10,10,10,10,10,10,10,10,10,10, 4, 3, 0, 3, 4}, // M
            {10,10,10,10,10,10,10,10,10,10,10,10, 4, 3, 0, 3}, // N
            {10,10,10,10,10,10,10,10,10,10,10,10,10, 4, 3, 0} // Fridge

    };

    public static void findShortestPath(String name, int maxJump, boolean probabableFall){
        Map<String, Integer> dist = new HashMap<>();
        Map<String, List<String>> paths = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>();

        for(String label : labels){
            dist.put(label, Integer.MAX_VALUE);
            dist.put("Door", 0);
            pq.add((new Node("Door", 0, new ArrayList<>())));
        }

        while (!pq.isEmpty()){
            Node cur = pq.poll();
            int idx = getIndex(cur.name);

            if(cur.cost > dist.get(cur.name)){
                continue;
            }

            for(int i = 0; i < labels.length; i++){
                int d = distances[idx][i];
                if(d <= maxJump && d < 10){
                    int risk = (probabableFall && d > 5.5) ? 1 : 0;
                    int newCost = cur.cost + risk + d;
                    if(newCost < dist.get(labels[i])){
                        dist.put(labels[i], newCost);
                        pq.add(new Node(labels[i], newCost, cur.path));
                        paths.put(labels[i], new ArrayList<>(cur.path));
                    }
                }
            }
        }

        System.out.println("\n=== " + name + " ===");
        System.out.println("Shortest Cost to Fridge: " + dist.get("Fridge"));
        List<String> fullPath = new ArrayList<>(paths.getOrDefault("Fridge", new ArrayList<>()));
        fullPath.add("Fridge");
        System.out.println("Path: " + String.join(" -> ", fullPath));

        if(probabableFall){
            List<String> path = paths.get("Fridge");
            path.add("Fridge");
            int riskyJumps = 0;
            for(int i = 1; i < path.size(); i++){
                int from = getIndex(path.get(i -1));
                int to = getIndex(path.get(i));
                if(distances[from][to] > 5.5) riskyJumps++;
            }
            double probNoFall = Math.pow(0.8, riskyJumps);
            System.out.printf("Probability Blake makes it: %4f\n", probNoFall);
        }
    }

    private static int getIndex(String s){
        for(int i = 0; i < labels.length; i++){
            if(s.equals(labels[i])) return i;
        }
        throw new IllegalArgumentException("Invalid label: " + s);
    }

    public static void main(String[] args) {
        findShortestPath("Ariane", 7, false);
        findShortestPath("Blake", 9, true);
    }
}
