/*
CURSO: Compiladores e Intérpretes
PROYECTO #1: Análisis Léxico
ESTUDIANTES: Sebastián Sandí Blanco y Melany Bustos Cerdas
ARCHIVO: GeneradorScanner.java

OBJETIVO: Automatizar la generación de las clases Java a partir de archivos .flex y .cup
ENTRADA: Archivos 'Lexer.flex' y 'parser.cup'
SALIDA: Clases 'Scanner.java', 'parser.java' y 'sym.java' en la carpeta 'generados/'
RESTRICCIONES:
- Requiere las librerías jflex-full-1.9.1.jar y java-cup-11b.jar en la carpeta 'lib/'
 */
import java.io.File;

public class GeneradorScanner {

    public static void main(String[] args) throws Exception {

        // Nombre de los archivos generados
        String archivoLexerGen = "generados/Lexer.java";
        String archivoParserGen = "generados/parser.java";
        String archivoSymGen = "generados/sym.java";

        // Limpieza
        eliminarSiExiste(archivoLexerGen);
        eliminarSiExiste(archivoParserGen);
        eliminarSiExiste(archivoSymGen);

        System.out.println("--- Iniciando generación de archivos ---");

        // Ejecución de comandos
        ejecutarComando("java", "-jar", "lib/jflex-full-1.9.1.jar", "-d", "generados/", "src/Lexer.flex");
        ejecutarComando("java", "-jar", "lib/java-cup-11b.jar", "-destdir", "generados/", "src/parser.cup");

        System.out.println("--- Proceso finalizado con éxito ---");
    }


    private static void eliminarSiExiste(String nombreArchivo) {
        File archivo = new File(nombreArchivo);
        if (archivo.exists()) {
            if (archivo.delete()) {
                System.out.println("Archivo antiguo eliminado: " + nombreArchivo);
            } else {
                System.err.println("No se pudo eliminar: " + nombreArchivo);
            }
        }
    }

    private static void ejecutarComando(String... comando) throws Exception {

        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.inheritIO(); // muestra salida en consola

        Process p = pb.start();
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

