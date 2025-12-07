import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ParcoursEulerienOriente {

    public List<Integer> trouverCircuitEulerien(int[][] matriceAdjacence, int depart) {
        int n = matriceAdjacence.length;
        int[][] reste = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(matriceAdjacence[i], 0, reste[i], 0, n);
        }

        Stack<Integer> pile = new Stack<>();
        List<Integer> circuit = new ArrayList<>();

        pile.push(depart);

        while (!pile.isEmpty()) {
            int u = pile.peek();
            int v = trouverVoisinAvecArc(reste, u);
            if (v == -1) {
                circuit.add(u);
                pile.pop();
            } else {
                reste[u][v] = 0;
                pile.push(v);
            }
        }

        return circuit;
    }

    private int trouverVoisinAvecArc(int[][] reste, int u) {
        for (int v = 0; v < reste.length; v++) {
            if (reste[u][v] != 0 && reste[u][v] < Integer.MAX_VALUE / 2) {
                return v;
            }
        }
        return -1;
    }
}
