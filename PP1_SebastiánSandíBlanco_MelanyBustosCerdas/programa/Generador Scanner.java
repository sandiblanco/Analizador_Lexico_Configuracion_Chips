import java.io.*;

public class GeneradorScanner {

    public static void main(String[] args) throws Exception {

        ejecutarComando("java", "-jar", "JFlex_Cup/jflex-full-1.9.1.jar", "Lexer.flex");
        ejecutarComando("java", "-jar", "JFlex_Cup/java-cup-11b.jar", "Scanner.cup");

        System.out.println("Archivos generados correctamente.");
    }

    private static void ejecutarComando(String... comando) throws Exception {
        ProcessBuilder jflexCup = new ProcessBuilder(comando);
        jflexCup.inheritIO(); // muestra salida en consola
        Process p = jflexCup.start();
        p.waitFor();
    }
}

