import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Graphe non orienté représenté par une matrice d'adjacence.
 * Sert à calculer un circuit eulérien, en doublant des arêtes si nécessaire.
 */
public class Graphe2 {

    private int[][] matrice; // matrice d'adjacence
    private int n;           // nombre de sommets

    // ---------- Constructeur ----------

    public Graphe2(int[][] matrice) {
        this.matrice = matrice;
        this.n = matrice.length;
    }

    public int getNombreSommets() {
        return n;
    }

    public int[][] getMatrice() {
        return matrice;
    }

    // ---------- Lecture depuis un fichier texte ----------

    /**
     * Format attendu du fichier :
     *
     * 0 1 1 0
     * 1 0 1 0
     * 1 1 0 1
     * 0 0 1 0
     *
     * Chaque ligne = une ligne de la matrice.
     * Séparateur = espaces.
     */
    public static Graphe2 lireDepuisFichier(String chemin) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {
            List<int[]> lignes = new ArrayList<>();
            String ligne;

            while ((ligne = br.readLine()) != null) {
                ligne = ligne.trim();
                if (ligne.isEmpty()) continue;
                String[] tokens = ligne.split("\\s+");
                int[] row = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    row[i] = Integer.parseInt(tokens[i]);
                }
                lignes.add(row);
            }

            if (lignes.isEmpty()) {
                throw new IOException("Fichier vide ou invalide.");
            }

            int n = lignes.get(0).length;
            int[][] matrice = new int[n][n];

            for (int i = 0; i < n; i++) {
                if (lignes.get(i).length != n) {
                    throw new IOException("La matrice doit être carrée.");
                }
                matrice[i] = lignes.get(i);
            }

            return new Graphe2(matrice);
        }
    }

    // ---------- Fonctions de base ----------

    // degré du sommet i
    public int degre(int i) {
        int d = 0;
        for (int j = 0; j < n; j++) {
            d += matrice[i][j];
        }
        return d;
    }

    // Le graphe est-il connexe (en ignorant les sommets isolés) ?
    public boolean estConnexe() {
        boolean[] visite = new boolean[n];

        // trouver un premier sommet avec degré > 0
        int depart = -1;
        for (int i = 0; i < n; i++) {
            if (degre(i) > 0) {
                depart = i;
                break;
            }
        }

        // s'il n'y a aucune arête, on considère le graphe connexe (cas trivial)
        if (depart == -1) {
            return true;
        }

        // DFS/BFS
        Deque<Integer> pile = new ArrayDeque<>();
        pile.push(depart);
        visite[depart] = true;

        while (!pile.isEmpty()) {
            int u = pile.pop();
            for (int v = 0; v < n; v++) {
                if (matrice[u][v] > 0 && !visite[v]) {
                    visite[v] = true;
                    pile.push(v);
                }
            }
        }

        // tous les sommets avec degré > 0 doivent être visités
        for (int i = 0; i < n; i++) {
            if (degre(i) > 0 && !visite[i]) {
                return false;
            }
        }
        return true;
    }

    // Tous les degrés pairs et graphe connexe -> eulérien
    public boolean estEulerien() {
        if (!estConnexe()) {
            return false;
        }

        for (int i = 0; i < n; i++) {
            if (degre(i) % 2 != 0) {
                return false;
            }
        }
        return true;
    }

    // ---------- Sommets impairs & BFS ----------

    // liste des sommets de degré impair
    public List<Integer> sommetsDeDegreImpair() {
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (degre(i) % 2 != 0) {
                res.add(i);
            }
        }
        return res;
    }

    // BFS sur la matrice → tableau des prédécesseurs
    private int[] bfsPredecesseurs(int source) {
        int[] pred = new int[n];
        Arrays.fill(pred, -1);

        Queue<Integer> file = new ArrayDeque<>();
        file.add(source);
        pred[source] = source; // racine

        while (!file.isEmpty()) {
            int u = file.poll();
            for (int v = 0; v < n; v++) {
                if (matrice[u][v] > 0 && pred[v] == -1) {
                    pred[v] = u;
                    file.add(v);
                }
            }
        }
        return pred;
    }

    // ---------- Rendre le graphe eulérien ----------

    /**
     * Rend le graphe eulérien en doublant des arêtes le long
     * de plus courts chemins (en nombre d'arêtes, via BFS).
     * Algo simple, pas forcément optimal, mais suffisant pour TP.
     */
    public void rendreEulerienEnDoublantDesAretes() {
        List<Integer> impairs = sommetsDeDegreImpair();

        if (impairs.isEmpty()) {
            System.out.println("Tous les sommets sont déjà de degré pair, rien à doubler.");
            return;
        }

        System.out.println("Sommets de degré impair avant correction : " + impairs);

        // Tant qu'il reste des sommets impairs, on les apparie par 2
        while (impairs.size() > 1) {
            int u = impairs.remove(0);
            int v = impairs.remove(0);

            System.out.println("On apparie " + u + " et " + v + " et on double un chemin entre eux.");

            // BFS depuis u
            int[] pred = bfsPredecesseurs(u);
            if (pred[v] == -1) {
                throw new IllegalStateException("Pas de chemin entre " + u + " et " + v + " alors que le graphe devrait être connexe.");
            }

            // Reconstruire le chemin v -> u avec pred[]
            List<Integer> chemin = new ArrayList<>();
            int x = v;
            while (x != u) {
                chemin.add(x);
                x = pred[x];
            }
            chemin.add(u);
            Collections.reverse(chemin);

            System.out.println("Chemin choisi pour duplication : " + chemin);

            // Dupliquer toutes les arêtes du chemin (non orienté)
            for (int i = 0; i < chemin.size() - 1; i++) {
                int a = chemin.get(i);
                int b = chemin.get(i + 1);
                matrice[a][b]++;
                matrice[b][a]++;
            }
        }

        // Afficher les degrés après
        List<Integer> impairsApres = sommetsDeDegreImpair();
        System.out.println("Sommets de degré impair après correction : " + impairsApres);
    }

    // ---------- Circuit eulérien (Hierholzer) ----------

    public List<Integer> circuitEulerien(int sommetDepart) {
        if (!estEulerien()) {
            throw new IllegalStateException("Le graphe n'est pas eulérien.");
        }

        // Copie de la matrice pour pouvoir "consommer" les arêtes
        int[][] mat = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(matrice[i], 0, mat[i], 0, n);
        }

        Deque<Integer> pile = new ArrayDeque<>();
        List<Integer> circuit = new ArrayList<>();

        pile.push(sommetDepart);

        while (!pile.isEmpty()) {
            int u = pile.peek();

            // chercher un voisin avec une arête restante
            int voisin = -1;
            for (int i = 0; i < n; i++) {
                if (mat[u][i] > 0) {
                    voisin = i;
                    break;
                }
            }

            if (voisin != -1) {
                // consommer l'arête u-voisin
                mat[u][voisin]--;
                mat[voisin][u]--;
                pile.push(voisin);
            } else {
                // plus d'arêtes incidentes -> on ajoute au circuit
                circuit.add(pile.pop());
            }
        }

        // le circuit est construit à l'envers
        Collections.reverse(circuit);
        return circuit;
    }

    // ---------- Affichage ----------

    public void afficherMatrice() {
        System.out.println("Matrice d'adjacence :");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(matrice[i][j] + " ");
            }
            System.out.println();
        }
    }
}
