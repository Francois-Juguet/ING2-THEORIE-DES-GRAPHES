import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

class Graphe {
    private int nombreDeNoeuds;
    private List<List<Arete>> adjacence;

    public Graphe(int nombreDeNoeuds) {
        this.nombreDeNoeuds = nombreDeNoeuds;
        this.adjacence = new ArrayList<>(nombreDeNoeuds);
        for (int i = 0; i < nombreDeNoeuds; i++) {
            this.adjacence.add(new ArrayList<>());
        }
    }

    public void ajouterArete(int depart, int arrivee, int poids) {
        // Graphe non orienté: ajouter l'arête dans les deux sens
        adjacence.get(depart).add(new Arete(arrivee, poids));
        adjacence.get(arrivee).add(new Arete(depart, poids));
    }

    // Modifié pour retourner les distances ET les prédécesseurs
    public ResultatDijkstra trouverCheminPlusCourtDijkstra(int depart) {
        int nombreDeNoeuds = this.nombreDeNoeuds;
        int[] distances = new int[nombreDeNoeuds];
        int[] predecesseurs = new int[nombreDeNoeuds]; // Tableau pour stocker le prédécesseur

        Arrays.fill(distances, Integer.MAX_VALUE);
        Arrays.fill(predecesseurs, -1);
        distances[depart] = 0;
        predecesseurs[depart] = depart;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.add(new int[]{0, depart});

        while (!pq.isEmpty()) {
            int[] courant = pq.poll();
            int u = courant[1];

            if (courant[0] > distances[u]) {
                continue;
            }

            for (Arete arete : adjacence.get(u)) {
                int v = arete.destination;
                int poids = arete.poids;

                if (distances[u] != Integer.MAX_VALUE && (long)distances[u] + poids < distances[v]) {
                    distances[v] = distances[u] + poids;
                    predecesseurs[v] = u; // Enregistrement du prédécesseur
                    pq.add(new int[]{distances[v], v});
                }
            }
        }
        return new ResultatDijkstra(distances, predecesseurs);
    }

    public List<Integer> reconstruireChemin(int depart, int arrivee, int[] predecesseurs) {
        LinkedList<Integer> chemin = new LinkedList<>();
        int courant = arrivee;

        if (predecesseurs[arrivee] == -1 && arrivee != depart) {
            return chemin;
        }

        while (courant != -1 && courant != depart) {
            chemin.addFirst(courant);
            courant = predecesseurs[courant];
        }

        if (courant == depart) {
            chemin.addFirst(depart);
        }

        return chemin;
    }


}