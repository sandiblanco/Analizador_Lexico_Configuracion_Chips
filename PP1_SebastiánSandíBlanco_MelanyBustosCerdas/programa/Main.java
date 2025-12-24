/*
CURSO: Compiladores e Intérpretes
PROYECTO #1: Análisis Léxico
ESTUDIANTES: Sebastián Sandí Blanco y Melany Bustos Cerdas
*/

import java.io.*;
import java_cup.runtime.Symbol;

public class Main {
    public static void main(String[] args) throws Exception {

        //archivos de texto
        String archivoFuente = "programa/archivoFuente.txt";
        String archivoSalida = "tokens_encontrados.txt";

        try {
            Reader lectorArchivo = new BufferedReader(
                    new InputStreamReader(new FileInputStream(archivoFuente), "UTF-8"));
            Scanner scanner = new Scanner(lectorArchivo); // Clase generada por JFlex
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(archivoSalida), "UTF-8")
            );

            System.out.println("Iniciando analisis lexico de: " + archivoFuente);
            writer.write("REPORTE DE TOKENS ENCONTRADOS\n");
            writer.write("====================================\n");
            writer.close();

        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Fin del Análisis léxico");
    }
}
