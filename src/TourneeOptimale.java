import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class TourneeOptimale {

    // Structure pour stocker le coût et le chemin complet d'un segment entre deux points obligatoires
    private static class CheminSegment {
        long cout;
        List<Integer> noeuds; // Chemin complet (Départ -> Arrivée)

        public CheminSegment(long cout, List<Integer> noeuds) {
            this.cout = cout;
            this.noeuds = noeuds;
        }
    }

    public String trouverTourneeMinimale(String[] nomsNoeuds, Graphe graphe, int depart, List<Integer> obligatoires) {

        // 1. Définir l'ensemble des points à visiter (départ + obligatoires)
        List<Integer> pointsAVisiter = new ArrayList<>();
        pointsAVisiter.add(depart);
        for (int p : obligatoires) {
            if (p != depart) {
                pointsAVisiter.add(p);
            }
        }

        int N = pointsAVisiter.size();
        if (N <= 1) {
            return "Il n'y a pas de points de passage obligatoires ou la tournée est triviale. Coût: 0. Chemin: [" + nomsNoeuds[depart] + "]";
        }

        // 2. Calculer toutes les distances ET CHEMINS minimaux entre chaque paire de points
        CheminSegment[][] cheminsEntrePoints = new CheminSegment[N][N];

        // Stocker tous les résultats de Dijkstra pour éviter de les recalculer
        Map<Integer, ResultatDijkstra> dijkstraResults = new HashMap<>();
        for(int p : pointsAVisiter) {
            dijkstraResults.put(p, graphe.trouverCheminPlusCourtDijkstra(p));
        }

        for (int i = 0; i < N; i++) {
            int noeudDepart = pointsAVisiter.get(i);
            ResultatDijkstra resultat = dijkstraResults.get(noeudDepart);

            for (int j = 0; j < N; j++) {
                int noeudArrivee = pointsAVisiter.get(j);
                int distance = resultat.distances[noeudArrivee];

                if (distance == Integer.MAX_VALUE) {
                    return "ERREUR: Un chemin n'existe pas entre le noeud " + nomsNoeuds[noeudDepart] + " et le noeud " + nomsNoeuds[noeudArrivee] + ". La tournée est impossible.";
                }

                // Reconstruction du chemin segment (segment: noeudDepart -> noeudArrivee)
                List<Integer> chemin = graphe.reconstruireChemin(noeudDepart, noeudArrivee, resultat.predecesseurs);
                cheminsEntrePoints[i][j] = new CheminSegment(distance, chemin);
            }
        }

        // 3. Appliquer la force brute (permutations) sur les points obligatoires
        List<Integer> noeudsAPermuter = pointsAVisiter.subList(1, N);

        AtomicLong coutMinimal = new AtomicLong(Long.MAX_VALUE);
        AtomicReference<List<Integer>> meilleurCheminComplet = new AtomicReference<>();

        genererPermutations(noeudsAPermuter, 0, new ArrayList<>(), (permutation) -> {

            List<Integer> itineraireObligatoire = new ArrayList<>();
            itineraireObligatoire.add(depart);
            itineraireObligatoire.addAll(permutation);
            itineraireObligatoire.add(depart); // Retour au point de départ

            long coutCourant = 0;
            List<Integer> cheminCompletCourant = new ArrayList<>();

            // Calculer le coût et assembler le chemin complet
            for (int i = 0; i < itineraireObligatoire.size() - 1; i++) {
                int u = itineraireObligatoire.get(i);
                int v = itineraireObligatoire.get(i + 1);

                int i_idx = pointsAVisiter.indexOf(u);
                int j_idx = pointsAVisiter.indexOf(v);

                CheminSegment segment = cheminsEntrePoints[i_idx][j_idx];

                coutCourant += segment.cout;

                // Ajouter tous les nœuds du segment SAUF le nœud de départ du segment (u)
                if (i > 0) {
                    cheminCompletCourant.addAll(segment.noeuds.subList(1, segment.noeuds.size()));
                } else {
                    // Premier segment: inclure le départ
                    cheminCompletCourant.addAll(segment.noeuds);
                }
            }

            // Mettre à jour le chemin minimal
            if (coutCourant < coutMinimal.get()) {
                coutMinimal.set(coutCourant);
                meilleurCheminComplet.set(cheminCompletCourant);
            }
        });


        // 4. Formater la sortie (utilise le chemin complet)
        if (meilleurCheminComplet.get() == null) {
            return "Tournée trouvée. Coût: 0. Chemin: [" + nomsNoeuds[depart] + "]";
        }

        List<String> cheminFinalNomme = meilleurCheminComplet.get().stream()
                .map(i -> nomsNoeuds[i])
                .collect(Collectors.toList());

        return String.format(
                "Tournée minimale trouvée.\nCoût total: %d\nChemin: %s",
                coutMinimal.get(),
                String.join(" -> ", cheminFinalNomme)
        );
    }

    private void genererPermutations(List<Integer> liste, int k, List<Integer> permutationCourante, java.util.function.Consumer<List<Integer>> callback) {
        if (k == liste.size()) {
            callback.accept(new ArrayList<>(permutationCourante));
            return;
        }

        for (int i = 0; i < liste.size(); i++) {
            if (!permutationCourante.contains(liste.get(i))) {
                permutationCourante.add(liste.get(i));
                genererPermutations(liste, k + 1, permutationCourante, callback);
                permutationCourante.remove(permutationCourante.size() - 1);
            }
        }
    }
}