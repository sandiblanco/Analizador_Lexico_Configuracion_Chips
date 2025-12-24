public class GeneradorScanner {

    public static void main(String[] args) throws Exception {

        ejecutarComando("java", "-jar", "programa/JFlex_Cup/jflex-1.9.2.jar", "programa/Lexer.flex");
        ejecutarComando("java", "-jar", "programa/JFlex_Cup/java-cup-11b.jar", "programa/parser.cup");
    }


    private static void ejecutarComando(String... comando) throws Exception {
        ProcessBuilder jflexCup = new ProcessBuilder(comando);
        jflexCup.inheritIO(); // muestra salida en consola
        Process p = jflexCup.start();
        int respuesta = p.waitFor();

        if (respuesta != 0) {
            throw new RuntimeException(
                    "Error al ejecutar comando: " + String.join(" ", comando)
            );
        }
        else{
            System.out.println("Comando ejecutado correctamente: " + String.join(" ", comando));
        }
    }
}

