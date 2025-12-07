import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TourneeOptimaleOriente {

    private static final int INFINI = Integer.MAX_VALUE / 2;

    private final int[][] matriceAdjacence;
    private final int nbSommets;

    public TourneeOptimaleOriente(int[][] matriceAdjacence) {
        this.matriceAdjacence = matriceAdjacence;
        this.nbSommets = matriceAdjacence.length;
    }

    public List<Integer> calculerCircuitOptimal(int depot, int[] pointsCollecte) {
        List<Integer> points = new ArrayList<>();
        points.add(depot);
        for (int s : pointsCollecte) {
            if (!points.contains(s)) points.add(s);
        }

        int k = points.size();
        int[][] distEntrePoints = calculerMatriceDistances(points);

        int tailleEtat = 1 << k;
        int[][] dp = new int[tailleEtat][k];
        int[][] precedent = new int[tailleEtat][k];

        for (int m = 0; m < tailleEtat; m++) {
            Arrays.fill(dp[m], INFINI);
            Arrays.fill(precedent[m], -1);
        }

        dp[1][0] = 0;

        for (int masque = 0; masque < tailleEtat; masque++) {
            for (int i = 0; i < k; i++) {
                if ((masque & (1 << i)) == 0) continue;
                if (dp[masque][i] >= INFINI) continue;

                for (int j = 0; j < k; j++) {
                    if ((masque & (1 << j)) != 0) continue;
                    int nouveauMasque = masque | (1 << j);
                    int nouveauCout = dp[masque][i] + distEntrePoints[i][j];
                    if (nouveauCout < dp[nouveauMasque][j]) {
                        dp[nouveauMasque][j] = nouveauCout;
                        precedent[nouveauMasque][j] = i;
                    }
                }
            }
        }

        int masqueComplet = (1 << k) - 1;
        int meilleurCout = INFINI;
        int dernierPoint = -1;

        for (int i = 0; i < k; i++) {
            if (i == 0) continue;
            int coutTotal = dp[masqueComplet][i] + distEntrePoints[i][0];
            if (coutTotal < meilleurCout) {
                meilleurCout = coutTotal;
                dernierPoint = i;
            }
        }

        List<Integer> ordrePoints = new ArrayList<>();
        int masque = masqueComplet;
        int courant = dernierPoint;

        while (courant != -1) {
            ordrePoints.add(courant);
            int prec = precedent[masque][courant];
            masque = masque & ~(1 << courant);
            courant = prec;
        }

        if (ordrePoints.get(ordrePoints.size() - 1) != 0) ordrePoints.add(0);
        Collections.reverse(ordrePoints);
        ordrePoints.add(0);

        List<Integer> circuit = new ArrayList<>();
        for (int idx : ordrePoints) {
            circuit.add(points.get(idx));
        }
        return circuit;
    }

    private int[][] calculerMatriceDistances(List<Integer> points) {
        int k = points.size();
        int[][] dist = new int[k][k];

        for (int i = 0; i < k; i++) {
            int source = points.get(i);
            DijkstraOriente d = new DijkstraOriente(matriceAdjacence);
            d.calculerPlusCourtsChemins(source);
            for (int j = 0; j < k; j++) {
                int cible = points.get(j);
                dist[i][j] = d.getDistance(cible);
            }
        }
        return dist;
    }
}
