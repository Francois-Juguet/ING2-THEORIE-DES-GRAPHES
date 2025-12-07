import java.util.Comparator;

class Tri {
    int id; // Identifiant du sommet (indice de la matrice)
    int degree; // Degré du sommet
    int color; // Couleur attribuée (initialisée à -1 ou 0)

    public Tri(int id, int degree) {
        this.id = id;
        this.degree = degree;
        this.color = -1; // -1 signifie non coloré
    }

    // Comparator pour trier par degré décroissant
    public static Comparator<Tri> DegreeComparator = new Comparator<Tri>() {
        @Override
        public int compare(Tri v1, Tri v2) {
            return Integer.compare(v2.degree, v1.degree); // Décroissant
        }
    };
}