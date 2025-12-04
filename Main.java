import java.nio.file.Path;
import java.nio.file.Paths;
public class Main {
    public static void main(String[] args) {
        Path p = Paths.get("graph.txt");

        WelshPowellColoring graphColoring = new WelshPowellColoring();
        graphColoring.readGraphFromFile("graph.txt");
        graphColoring.welshPowellColoring();

}   }