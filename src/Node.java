import java.util.List;

public class Node implements Comparable<Node> {
    State state;
    List<Transfer> path;
    int cost;

    Node(State s, List<Transfer> p, int c){
        state = s;
        path = p;
        cost = c;
    }

    public State getState(){
        return state;
    }

    public List<Transfer> getPath(){
        return path;
    }

    public int getCost(){
        return cost;
    }

    public int compareTo(Node n){
        return Integer.compare(cost, n.getCost());
    }
}
