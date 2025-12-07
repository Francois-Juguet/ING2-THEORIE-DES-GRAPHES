import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GrapheReader {

    // Conteneur pour toutes les données du graphe
    public static class GrapheData {
        public Graphe graphe;
        public int depart;
        public List<Integer> obligatoires;
        public int nombreDeNoeuds;

        public GrapheData(Graphe g, int d, List<Integer> o, int n) {
            this.graphe = g;
            this.depart = d;
            this.obligatoires = o;
            this.nombreDeNoeuds = n;
        }
    }

    public static GrapheData lireFichier(String nomFichier) throws IOException, IllegalArgumentException {

        try (BufferedReader reader = new BufferedReader(new FileReader(nomFichier))) {

            // Ligne 1: Nombre de nœuds
            String line = reader.readLine();
            if (line == null) throw new IllegalArgumentException("Fichier vide ou format incorrect.");
            int N_NOEUDS = Integer.parseInt(line.trim());
            Graphe graphe = new Graphe(N_NOEUDS);

            // Ligne 2: Point de Départ
            line = reader.readLine();
            if (line == null) throw new IllegalArgumentException("Point de départ manquant.");
            int POINT_DEPART = Integer.parseInt(line.trim());

            // Ligne 3: Points Obligatoires (séparés par des virgules ou espaces)
            line = reader.readLine();
            if (line == null) throw new IllegalArgumentException("Liste des points obligatoires manquante.");

            List<Integer> POINTS_OBLIGATOIRES = Arrays.stream(line.split("[,\\s]+"))
                    .filter(s -> !s.trim().isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            // Lignes restantes: Arêtes (Départ, Arrivée, Poids)
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Diviser par espace ou virgule
                String[] parties = line.trim().split("[,\\s]+");

                if (parties.length != 3) {
                    throw new IllegalArgumentException("Format darete incorrect: " + line);
                }

                try {
                    int depart = Integer.parseInt(parties[0]);
                    int arrivee = Integer.parseInt(parties[1]);
                    int poids = Integer.parseInt(parties[2]);

                    graphe.ajouterArete(depart, arrivee, poids);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Poids ou nœud de l'arête neest pas un nombre: " + line);
                }
            }

            return new GrapheData(graphe, POINT_DEPART, POINTS_OBLIGATOIRES, N_NOEUDS);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Erreur de format de nombre dans le fichier: " + e.getMessage());
        }
    }
}