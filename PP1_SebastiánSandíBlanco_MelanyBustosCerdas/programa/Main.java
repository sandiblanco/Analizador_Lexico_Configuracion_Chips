import java.io.*;
import java_cup.runtime.Symbol;

public class main {
    public static void main(String[] args) throws Exception {

        //archivos de texto
        String archivoFuente = "archivoFuente.txt";
        String archivoSalida = "tokens_encontrados.txt";

        try {
            Reader lectorArchivo = new BufferedReader(
                    new InputStreamReader(new FileInputStream("entrada.txt"), "UTF-8"));
            Scanner scanner = new Scanner(lectorArchivo); // Clase generada por JFlex
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream("tokens_encontrados.txt"), "UTF-8")
            );

            System.out.println("Iniciando analisis lexico de: " + archivoFuente);
            writer.write("REPORTE DE TOKENS ENCONTRADOS\n");
            writer.write("====================================\n");

        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        //Lexer lexer = new Lexer(lectorArchivo);

        //while (lexer.next_token().sym != sym.EOF) {
        //}

        System.out.println("Fin del Análisis léxico");
    }
}
