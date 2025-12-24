import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Reader lectorArchivo = new FileReader("archivoFuente.txt");

        Lexer lexer = new Lexer(lectorArchivo);

        while (lexer.next_token().sym != sym.EOF) {
        }

        System.out.println("Fin del Análisis léxico");
    }
}
