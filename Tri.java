import java.util.Comparator;

class Vortex {
    int id; // Identifiant du sommet (indice de la matrice)
    int degree; // Degré du sommet
    int color; // Couleur attribuée (initialisée à -1 ou 0)

    public Vortex(int id, int degree) {
        this.id = id;
        this.degree = degree;
        this.color = -1; // -1 signifie non coloré
    }

    // Comparator pour trier par degré décroissant
    public static Comparator<Vortex> DegreeComparator = new Comparator<Vortex>() {
        @Override
        public int compare(Vortex v1, Vortex v2) {
            return Integer.compare(v2.degree, v1.degree); // Décroissant
        }
    };
}