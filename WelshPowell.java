import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class WelshPowell {

    private int numVertices;
    private int[][] adjMatrix;
    private List<Vortex> vertices;

    /**
     * Charge la matrice d'adjacence à partir du fichier.
     * Le fichier est supposé contenir N lignes, chacune avec N entiers (0 ou 1) séparés par des espaces.
     * La première ligne du fichier peut contenir le nombre de sommets (facultatif si la matrice est carrée).
     */
    public void readGraphFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                // Ignore les lignes vides ou de commentaires
                if (!line.trim().isEmpty() && !line.trim().startsWith("#")) {
                    lines.add(line.trim());
                }
            }

            if (lines.isEmpty()) {
                System.out.println("Le fichier est vide ou mal formaté.");
                return;
            }

            // Détermine le nombre de sommets (taille de la matrice)
            // On suppose que la matrice est carrée, donc le nombre de lignes est le nombre de sommets.
            this.numVertices = lines.size();
            this.adjMatrix = new int[numVertices][numVertices];
            this.vertices = new ArrayList<>(numVertices);

            for (int i = 0; i < numVertices; i++) {
                StringTokenizer st = new StringTokenizer(lines.get(i), " ");
                int degree = 0;
                for (int j = 0; j < numVertices; j++) {
                    if (st.hasMoreTokens()) {
                        int value = Integer.parseInt(st.nextToken());
                        adjMatrix[i][j] = value;
                        if (value == 1) {
                            degree++;
                        }
                    } else {
                        throw new IOException("Erreur: Ligne " + (i + 1) + " dans le fichier n'a pas assez d'éléments.");
                    }
                }
                vertices.add(new Vortex(i, degree));
            }

            System.out.println("Graphe chargé avec " + numVertices + " sommets.");

        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Erreur de format de nombre dans la matrice: " + e.getMessage());
        }
    }

    /**
     * Applique l'algorithme de coloration de Welsh-Powell.
     */
    public void welshPowellColoring() {
        if (vertices == null || vertices.isEmpty()) {
            System.out.println("Le graphe n'a pas été chargé.");
            return;
        }

        // Étape 1: Trier les sommets par ordre de degré décroissant
        Collections.sort(vertices, Vortex.DegreeComparator);
        System.out.println("\nSommets triés par degré décroissant:");
        for (Vortex v : vertices) {
            System.out.println("Sommet " + v.id + " (Degré: " + v.degree + ")");
        }

        int currentColor = 1;
        int coloredCount = 0;

        // Étape 2: Coloration séquentielle
        while (coloredCount < numVertices) {
            // Trouver le premier sommet non coloré
            Vortex startVortex = null;
            for (Vortex v : vertices) {
                if (v.color == -1) {
                    startVortex = v;
                    break;
                }
            }

            if (startVortex == null) break; // Tous les sommets sont colorés

            // Attribuer la couleur actuelle au sommet de départ
            startVortex.color = currentColor;
            coloredCount++;

            // Parcourir les autres sommets dans l'ordre trié
            for (Vortex current : vertices) {
                // Si le sommet n'est pas encore coloré
                if (current.color == -1) {
                    boolean isAdjacentToSameColor = false;

                    // Vérifier s'il est adjacent à un sommet ayant déjà 'currentColor'
                    for (Vortex colored : vertices) {
                        // On vérifie seulement les sommets déjà colorés avec 'currentColor'
                        if (colored.color == currentColor) {
                            // current.id et colored.id sont les indices de la matrice
                            if (adjMatrix[current.id][colored.id] == 1) {
                                isAdjacentToSameColor = true;
                                break;
                            }
                        }
                    }

                    // Si non adjacent, on lui attribue la même couleur
                    if (!isAdjacentToSameColor) {
                        current.color = currentColor;
                        coloredCount++;
                    }
                }
            }

            // Passer à la couleur suivante pour la prochaine itération
            currentColor++;
        }

        displayResults(currentColor - 1);
    }
    
    private void displayResults(int totalColors) {
        System.out.println("\n--- Résultat de la Coloration Welsh-Powell ---");
        System.out.println("Nombre chromatique trouvé: " + totalColors);
        for (Vortex v : vertices) {
            System.out.println("Sommet " + v.id + " -> Couleur " + v.color);
        }
        System.out.println("---------------------------------------------");
    }

}
