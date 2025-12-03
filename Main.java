import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.util.stream.Collectors;

public class Main {

    private static String[] genererNomsNoeuds(int N, List<Integer> obligatoires) {
        String[] noms = new String[N];

        // Étape 1: Nommer les nœuds non obligatoires (J, A, B, C, etc.)
        for (int i = 0; i < N; i++) {
            if (i == 0) {
                noms[i] = "J"; // Départ
            } else if (i <= 'I' - 'A' + 1) {
                noms[i] = String.valueOf((char) ('A' + i - 1));
            } else {
                noms[i] = String.valueOf(i); // Utilise l'index comme nom par défaut pour les autres
            }
        }

        // Étape 2: Remplacer les noms par P1, P2, P3... pour les nœuds obligatoires
        for (int i = 0; i < obligatoires.size(); i++) {
            int index = obligatoires.get(i);
            if (index < N) {
                noms[index] = "P" + (i + 1);
            }
        }

        return noms;
    }


    public static void main(String[] args) {

        String nomFichier = "graphe.txt";

        try {
            // 1. Lecture du Fichier et construction des données
            GrapheReader.GrapheData data = GrapheReader.lireFichier(nomFichier);

            Graphe g = data.graphe;
            int POINT_DEPART = data.depart;
            List<Integer> POINTS_OBLIGATOIRES = data.obligatoires;
            int N_NOEUDS = data.nombreDeNoeuds;

            // CORRECTION: Générer le tableau de noms dynamiquement
            String[] NOM_NOEUDS_DYN = genererNomsNoeuds(N_NOEUDS, POINTS_OBLIGATOIRES);


            // 2. Exécution de la résolution
            TourneeOptimale solveur = new TourneeOptimale();
            // ATTENTION: Passage du tableau de noms en paramètre
            String resultat = solveur.trouverTourneeMinimale(NOM_NOEUDS_DYN, g, POINT_DEPART, POINTS_OBLIGATOIRES);

            // 3. Affichage Console
            System.out.println("\nResolution du problem HO1 ( graphe non oriente) point de collecte : ");
            System.out.println("=================================================");
            System.out.printf("Nombre de sommets du Graphe : %d\n", N_NOEUDS);

            String nomDepart = NOM_NOEUDS_DYN[POINT_DEPART];

            List<String> nomsObligatoires = POINTS_OBLIGATOIRES.stream()
                    .map(i -> NOM_NOEUDS_DYN[i])
                    .collect(Collectors.toList());

            System.out.printf("Point de Depart/Arrivee: %s\n", nomDepart);
            System.out.printf("Points de Passage Obligatoires: %s\n", nomsObligatoires);
            System.out.println("---");
            System.out.println(resultat);
            System.out.println("=================================================");

        } catch (IOException e) {
            System.err.println("Erreur: Impossible de lire le fichier " + nomFichier + ".");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur de format dans le fichier: " + e.getMessage());
        }
    }
}