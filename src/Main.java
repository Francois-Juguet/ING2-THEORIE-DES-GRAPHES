import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {


    private static String nomSommet(int i) {
        return switch (i) {
            case 0 -> "J";
            case 1 -> "A";
            case 2 -> "B";
            case 3 -> "C";
            case 4 -> "D";
            case 5 -> "E";
            case 6 -> "F";
            case 7 -> "G";
            case 8 -> "H";
            case 9 -> "I";
            default -> "?";
        };
    }

    private static void afficherChemin(List<Integer> chemin) {
        for (int i = 0; i < chemin.size(); i++) {
            System.out.print(nomSommet(chemin.get(i)));
            if (i < chemin.size() - 1) System.out.print(" -> ");
        }
        System.out.println();
    }

    private static void afficherMST(List<GrapheMST.Arete> mst, int poidsTotal) {
        System.out.println("\n-------MST ------------");
        for (GrapheMST.Arete e : mst) {
            System.out.println("  " + e);
        }
        System.out.println("Poids total = " + poidsTotal);
        System.out.println("-------------------------");
    }

    private static String[] genererNomsNoeuds(int N, List<Integer> obligatoires) {
        String[] noms = new String[N];

        // Étape 1: Nommer les nœuds  (J, A, B, C, ....)
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

    private static void afficherCircuit(List<Integer> circuit) {
        if (circuit == null || circuit.isEmpty()) {
            System.out.println("Aucun circuit trouve.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Circuit eulerien : ");
        for (int i = 0; i < circuit.size(); i++) {
            sb.append(circuit.get(i));
            if (i < circuit.size() - 1) {
                sb.append(" -> ");
            }
        }
        System.out.println(sb.toString());
    }


    public static void main(String[] args) {
        System.out.println("(1)graphe non oriente \n(2)graphe oriente");
        Scanner sc1 = new Scanner(System.in);
        int N = sc1.nextInt();
        if (N  == 1) {


            Scanner sc = new Scanner(System.in);

            System.out.println("cas graphe non oriente");
            System.out.println("Choix : \n(1)itininaire entre point de collecte \n(2)Parcourir tout le graphe \n(3) Coloration des zones \n(4)MST");
            int choix = sc.nextInt();

            if (choix == 1) {

                String nomFichier = "graphe.txt";

                try {
                    GrapheReader.GrapheData data = GrapheReader.lireFichier(nomFichier);

                    Graphe g = data.graphe;
                    int POINT_DEPART = data.depart;
                    List<Integer> POINTS_OBLIGATOIRES = data.obligatoires;
                    int N_NOEUDS = data.nombreDeNoeuds;
                    String[] NOM_NOEUDS_DYN = genererNomsNoeuds(N_NOEUDS, POINTS_OBLIGATOIRES);


                    // 2. Exécution de la résolution
                    TourneeOptimale solveur = new TourneeOptimale();
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
            if (choix == 2) {
                String chemin = "graphe2.txt";
                int sommetDepart = 0;

                if (args.length >= 1) {
                    chemin = args[0];
                }
                if (args.length >= 2) {
                    try {
                        sommetDepart = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("Sommet de départ invalide, j'utilise 0.");
                        sommetDepart = 0;
                    }
                }

                System.out.println("Fichier utilisé : " + chemin);
                System.out.println("Sommet de départ : " + sommetDepart);
                System.out.println();

                try {
                    Graphe2 g = Graphe2.lireDepuisFichier(chemin);
                    int n = g.getNombreSommets();

                    if (sommetDepart < 0 || sommetDepart >= n) {
                        System.out.println("Sommet de départ invalide (doit être entre 0 et " + (n - 1) + ").");
                        return;
                    }

                    g.afficherMatrice();
                    System.out.println();
                    System.out.println("Connexe ? " + g.estConnexe());
                    System.out.println("Eulerien ? " + g.estEulerien());
                    System.out.println();

                    if (!g.estEulerien()) {
                        System.out.println("Le graphe n'est pas eulerien, on double des aretes");
                        g.rendreEulerienEnDoublantDesAretes();
                        System.out.println();
                        g.afficherMatrice();
                        System.out.println("Eulérien après correction ? " + g.estEulerien());
                        System.out.println();
                    }

                    if (!g.estEulerien()) {
                        System.out.println("Erreur : le graphe n est toujours pas eulerien après correction.");
                        return;
                    }

                    List<Integer> circuit = g.circuitEulerien(sommetDepart);
                    afficherCircuit(circuit);

                    if (!circuit.isEmpty() && circuit.get(0).equals(circuit.get(circuit.size() - 1))) {
                        System.out.println("On revient bien au sommet de depart.");
                    } else {
                        System.out.println("Attention : le circuit ne revient pas au sommet de départ.");
                    }

                } catch (IOException e) {
                    System.out.println("Erreur de lecture du fichier : " + e.getMessage());
                } catch (IllegalStateException e) {
                    System.out.println("Erreur : " + e.getMessage());
                }
            }

            if(choix == 3){


                // ---------- 1) COLORATION AVEC WELSH-POWELL ----------
                Path p = Paths.get("graph.txt");

                WelshPowell graphColoring = new WelshPowell();
                graphColoring.readGraphFromFile("graph.txt");
                graphColoring.welshPowell();

            }


            if(choix == 4){
                // ---------- 2) MST (KRUSKAL) AVEC GrapheMST ----------
                String fichierMST = "graphe_mst.txt";

                try {
                    GrapheMST g = GrapheMST.lireDepuisFichier(fichierMST);
                    System.out.println("Nombre de sommets : " + g.getNombreSommets());
                    System.out.println("Nombre d'arêtes  : " + g.getAretes().size());

                    List<GrapheMST.Arete> mst = g.calculerMSTKruskal();
                    int poids = g.poidsTotal(mst);

                    afficherMST(mst, poids);

                } catch (IOException e) {
                    System.out.println("Erreur de lecture du fichier pour le MST : " + e.getMessage());
                }
            }

        } if (N==2 ){
            int n = 10;
            GrapheOriente g = new GrapheOriente(n);

            g.ajouterArc(0, 2, 200);
            g.ajouterArc(0, 1, 230);
            g.ajouterArc(1, 9, 70);
            g.ajouterArc(9, 8, 100);
            g.ajouterArc(8, 7, 130);
            g.ajouterArc(7, 6, 180);
            g.ajouterArc(6, 4, 400);
            g.ajouterArc(4, 3, 300);
            g.ajouterArc(3, 2, 90);
            g.ajouterArc(2, 5, 70);
            g.ajouterArc(5, 6, 50);
            g.ajouterArc(5, 8, 50);
            g.ajouterArc(5, 7, 70);

            int[][] matrice = g.getMatriceAdjacence();

            System.out.println("Problematique 1 : plus court chemin oriente");
            DijkstraOriente d = new DijkstraOriente(matrice);
            d.calculerPlusCourtsChemins(0);
            List<Integer> chemin = d.reconstruireChemin(0, 6);
            afficherChemin(chemin);

            System.out.println("Problematique 2 : tournee optimale sur points de collecte");
            int[] pointsCollecte = {1, 5, 6, 8};
            TourneeOptimaleOriente t = new TourneeOptimaleOriente(matrice);
            List<Integer> circuit = t.calculerCircuitOptimal(0, pointsCollecte);
            afficherChemin(circuit);

            System.out.println("Problematique 3 : circuit eulerien oriente ");
            ParcoursEulerienOriente p = new ParcoursEulerienOriente();
            List<Integer> euler = p.trouverCircuitEulerien(matrice, 0);
            afficherChemin(euler);
        }

    }
}
