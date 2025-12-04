import java.nio.file.Path;
import java.nio.file.Paths;
public class Main {
    public static void main(String[] args) {
        Path p = Paths.get("graph.txt");

        WelshPowell graphColoring = new WelshPowell();
        graphColoring.readGraphFromFile("graph.txt");
        graphColoring.welshPowell();

}   }
