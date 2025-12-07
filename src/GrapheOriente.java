public class GrapheOriente {

    private final int nbSommets;
    private final int[][] matriceAdjacence;

    public GrapheOriente(int nbSommets) {
        this.nbSommets = nbSommets;
        this.matriceAdjacence = new int[nbSommets][nbSommets];
        for (int i = 0; i < nbSommets; i++) {
            for (int j = 0; j < nbSommets; j++) {
                if (i == j) {
                    matriceAdjacence[i][j] = 0;
                } else {
                    matriceAdjacence[i][j] = Integer.MAX_VALUE / 2;
                }
            }
        }
    }

    public void ajouterArc(int u, int v, int poids) {
        matriceAdjacence[u][v] = poids;
    }

    public int getPoids(int u, int v) {
        return matriceAdjacence[u][v];
    }

    public int[][] getMatriceAdjacence() {
        return matriceAdjacence;
    }

    public int getNbSommets() {
        return nbSommets;
    }
}
