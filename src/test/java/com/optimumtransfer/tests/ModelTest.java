package com.optimumtransfer.tests;

import com.optimumtransfer.application.SolveMode;
import com.optimumtransfer.application.SolverRequest;
import com.optimumtransfer.model.Container;
import com.optimumtransfer.model.State;
import com.optimumtransfer.model.Transfer;

import java.util.List;

public class ModelTest implements TestCase {
    @Override
    public String name() {
        return "Model objects protect state and validate invariants";
    }

    @Override
    public void run() {
        int[] volumes = {3, 1, 0};
        State state = new State(volumes);
        volumes[0] = 99;

        TestSupport.assertArrayEquals(new int[]{3, 1, 0}, state.getVolumes(), "State should clone constructor input.");

        int[] returned = state.getVolumes();
        returned[1] = 88;
        TestSupport.assertArrayEquals(new int[]{3, 1, 0}, state.getVolumes(), "State should not expose internal array.");

        Container container = new Container(5, 2);
        container.setCurrentVol(4);
        TestSupport.assertEquals(4, container.getCurrentVol(), "Container should update to a valid volume.");

        TestSupport.assertThrows(IllegalArgumentException.class, () -> new Container(3, 4), "Container should reject starting volume above capacity.");
        TestSupport.assertThrows(IllegalArgumentException.class, () -> container.setCurrentVol(6), "Container should reject updates above capacity.");

        Transfer transfer = new Transfer(0, 2, 3, 3);
        TestSupport.assertEquals(0, transfer.getFromContainer(), "Transfer should expose source container.");
        TestSupport.assertEquals(2, transfer.getToContainer(), "Transfer should expose destination container.");
        TestSupport.assertEquals(3, transfer.getAmount(), "Transfer should expose amount.");
        TestSupport.assertEquals(3, transfer.getWeight(), "Transfer should expose weight.");
        TestSupport.assertTrue(transfer.toString().contains("Transfer 3 units from container 0 to container 2"), "Transfer should provide readable output.");

        SolverRequest request = new SolverRequest(
                new int[]{4, 2},
                new int[]{4, 0},
                targetState -> true,
                List.of(),
                ignored -> 0,
                SolveMode.SHORTEST_PATH,
                SolverRequest.UNBOUNDED_DEPTH
        );
        TestSupport.assertEquals(SolverRequest.UNBOUNDED_DEPTH, request.getMaxDepth(), "SolverRequest should expose the shared unbounded depth constant.");
        TestSupport.assertEquals(SolverRequest.DEFAULT_MAX_SOLUTIONS, request.getMaxSolutions(), "SolverRequest should expose the default solution limit.");
        TestSupport.assertThrows(IllegalArgumentException.class,
                () -> new SolverRequest(new int[]{4, 2}, new int[]{4}, targetState -> true, List.of(), ignored -> 0, SolveMode.SHORTEST_PATH, 1),
                "SolverRequest should reject mismatched capacities and start volumes.");
        TestSupport.assertThrows(IllegalArgumentException.class,
                () -> new SolverRequest(new int[]{4, 2}, new int[]{4, 0}, targetState -> true, List.of(), ignored -> 0, SolveMode.SHORTEST_PATH, 1, 0),
                "SolverRequest should reject non-positive maxSolutions.");
    }
}
