import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DijkstraOriente {

    private static final int INFINI = Integer.MAX_VALUE / 2;

    private final int[][] matriceAdjacence;
    private final int nbSommets;

    private int[] distance;
    private int[] predecesseur;
    private boolean[] visite;

    public DijkstraOriente(int[][] matriceAdjacence) {
        this.matriceAdjacence = matriceAdjacence;
        this.nbSommets = matriceAdjacence.length;
        this.distance = new int[nbSommets];
        this.predecesseur = new int[nbSommets];
        this.visite = new boolean[nbSommets];
    }

    public void calculerPlusCourtsChemins(int source) {
        for (int i = 0; i < nbSommets; i++) {
            distance[i] = INFINI;
            predecesseur[i] = -1;
            visite[i] = false;
        }
        distance[source] = 0;

        for (int k = 0; k < nbSommets; k++) {
            int u = choisirSommetMinNonVisite();
            if (u == -1) break;
            visite[u] = true;

            for (int v = 0; v < nbSommets; v++) {
                int poids = matriceAdjacence[u][v];
                if (!visite[v] && poids < INFINI) {
                    int nv = distance[u] + poids;
                    if (nv < distance[v]) {
                        distance[v] = nv;
                        predecesseur[v] = u;
                    }
                }
            }
        }
    }

    private int choisirSommetMinNonVisite() {
        int min = INFINI;
        int indice = -1;
        for (int i = 0; i < nbSommets; i++) {
            if (!visite[i] && distance[i] < min) {
                min = distance[i];
                indice = i;
            }
        }
        return indice;
    }

    public int getDistance(int sommet) {
        return distance[sommet];
    }

    public List<Integer> reconstruireChemin(int source, int destination) {
        List<Integer> chemin = new ArrayList<>();
        if (distance[destination] >= INFINI) return chemin;
        int courant = destination;
        while (courant != -1) {
            chemin.add(courant);
            if (courant == source) break;
            courant = predecesseur[courant];
        }
        Collections.reverse(chemin);
        return chemin;
    }
}
