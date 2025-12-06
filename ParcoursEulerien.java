import java.io.IOException;
import java.util.List;

public class ParcoursEulerien {

    private static void afficherCircuit(List<Integer> circuit) {
        if (circuit == null || circuit.isEmpty()) {
            System.out.println("Aucun circuit trouvé.");
            return;
        }
        StringBuilder sb = new StringBuilder("Circuit eulérien : ");
        for (int i = 0; i < circuit.size(); i++) {
            sb.append(circuit.get(i));
            if (i < circuit.size() - 1) {
                sb.append(" -> ");
            }
        }
        System.out.println(sb.toString());
    }
}
