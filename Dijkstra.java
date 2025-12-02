import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dijkstra {

    private static final int INFINI = Integer.MAX_VALUE / 2; // evite les depassements // debug

    private final int[][] mAA;
    private final int nbSommets;

    private int[] distance;       // distance minimale depuis la source
    private boolean[] visite;     // sommets deja traites
    private int[] predecesseur;   // pour reconstruire les chemins

   
    public Dijkstra(int[][] mAA) {
        this.mAA = mAA;
        this.nbSommets = mAA.length;
        this.distance = new int[nbSommets];
        this.visite = new boolean[nbSommets];
        this.predecesseur = new int[nbSommets];
    }
    
    public void calculerPlusCourtsChemins(int source) {
        // Initialisation
        for (int i = 0; i < nbSommets; i++) {
            distance[i] = INFINI;
            visite[i] = false;
            predecesseur[i] = -1;
        }
        distance[source] = 0;

        // On repete nbSommets fois
        for (int compteur = 0; compteur < nbSommets; compteur++) {
            int u = choisirSommetMinNonVisite();
            if (u == -1) {
                break;
            }

            visite[u] = true;

            // Relaxation des voisins du sommet ->debug
            for (int v = 0; v < nbSommets; v++) {
                int poidsUV = mAA[u][v];
                if (!visite[v] && poidsUV < INFINI) {
                    int newdistance = distance[u] + poidsUV;
                    if (newdistance < distance[v]) {
                        distance[v] = newdistance;
                        predecesseur[v] = u;
                    }
                }
            }
        }
    }

    
    public int getDistance(int sommet) {
        return distance[sommet];
    }
    public List<Integer> reconstruireChemin(int source, int destination) {
        List<Integer> chemin = new ArrayList<>();

        if (distance[destination] == INFINI) {
            // aucun chemin atteignable
            return chemin;
        }

        int courant = destination;
        while (courant != -1) {
            chemin.add(courant);
            if (courant == source) {
                break;
            }
            courant = predecesseur[courant];
        }

        Collections.reverse(chemin);
        return chemin;
    }

    //Choisit le sommet non visite avec la distance minimale courante.
    private int choisirSommetMinNonVisite() {
        int minDist = INFINI;
        int sommetMin = -1;

        for (int i = 0; i < nbSommets; i++) {
            if (!visite[i] && distance[i] < minDist) {
                minDist = distance[i];
                sommetMin = i;
            }
        }
        return sommetMin;
    }

    // Petite methode utilitaire pour savoir si un sommet est atteignable
    public boolean estAtteignable(int sommet) {
        return distance[sommet] < INFINI;
    }
}
