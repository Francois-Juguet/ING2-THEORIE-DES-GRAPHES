import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GrapheMST {

    public static class Arete {
        public int u;       // sommet 1
        public int v;       // sommet 2
        public int poids;   // poids de l'arête

        public Arete(int u, int v, int poids) {
            this.u = u;
            this.v = v;
            this.poids = poids;
        }

        @Override
        public String toString() {
            return "(" + u + " - " + v + ", poids=" + poids + ")";
        }
    }

    private int n; // nombre de sommets
    private List<Arete> aretes;

    // ---------- Constructeur ----------
    public GrapheMST(int n) {
        this.n = n;
        this.aretes = new ArrayList<>();
    }

    public int getNombreSommets() {
        return n;
    }

    public List<Arete> getAretes() {
        return aretes;
    }

    public void ajouterArete(int u, int v, int poids) {
        aretes.add(new Arete(u, v, poids));
    }

    public static GrapheMST lireDepuisFichier(String chemin) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {
            String ligne = br.readLine();
            if (ligne == null) {
                throw new IOException("Fichier vide.");
            }

            ligne = ligne.trim();
            if (ligne.isEmpty()) {
                throw new IOException("Première ligne vide (nombre de sommets attendu).");
            }

            int n = Integer.parseInt(ligne);
            GrapheMST g = new GrapheMST(n);

            while ((ligne = br.readLine()) != null) {
                ligne = ligne.trim();
                if (ligne.isEmpty()) continue;
                String[] tokens = ligne.split("\\s+");
                if (tokens.length < 3) {
                    throw new IOException("Ligne invalide (u v poids) : " + ligne);
                }
                int u = Integer.parseInt(tokens[0]);
                int v = Integer.parseInt(tokens[1]);
                int poids = Integer.parseInt(tokens[2]);
                g.ajouterArete(u, v, poids);
            }

            return g;
        }
    }

    // ---------- Union-Find (DSU) pour Kruskal ----------
    private static class UnionFind {
        int[] parent;
        int[] rang;

        UnionFind(int n) {
            parent = new int[n];
            rang = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rang[i] = 0;
            }
        }

        int trouver(int x) {
            if (parent[x] != x) {
                parent[x] = trouver(parent[x]); // compression de chemin
            }
            return parent[x];
        }

        boolean union(int x, int y) {
            int rx = trouver(x);
            int ry = trouver(y);
            if (rx == ry) return false;

            // union par rang
            if (rang[rx] < rang[ry]) {
                parent[rx] = ry;
            } else if (rang[rx] > rang[ry]) {
                parent[ry] = rx;
            } else {
                parent[ry] = rx;
                rang[rx]++;
            }
            return true;
        }
    }

    // ---------- Calcul du MST (Kruskal) ----------

    public List<Arete> calculerMSTKruskal() {
        // trier les arêtes par poids croissant
        List<Arete> aretesTriees = new ArrayList<>(aretes);
        aretesTriees.sort(Comparator.comparingInt(a -> a.poids));

        UnionFind uf = new UnionFind(n);
        List<Arete> mst = new ArrayList<>();

        for (Arete e : aretesTriees) {
            if (uf.union(e.u, e.v)) {
                mst.add(e);
                // optionnel : on peut s'arrêter si on a n-1 arêtes
                if (mst.size() == n - 1) {
                    break;
                }
            }
        }
        return mst;
    }

    public int poidsTotal(List<Arete> mst) {
        int somme = 0;
        for (Arete e : mst) {
            somme += e.poids;
        }
        return somme;
    }
}
