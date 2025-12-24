/*
CURSO: Compiladores e Intérpretes
PROYECTO #1: Análisis Léxico
ESTUDIANTES: Sebastián Sandí Blanco y Melany Bustos Cerdas
*/

import java.io.*;
import java_cup.runtime.Symbol;
import generados.Scanner;
import generados.sym;

public class Main {
    public static void main(String[] args) throws Exception {

        //archivos de texto
        String archivoFuente = "lectura/archivoFuente.txt";
        String archivoSalida = "lectura/tokens_encontrados.txt";

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

            while (true) {
                Symbol token = scanner.next_token();

                // sym.EOF es el fin de archivo
                if (token.sym == sym.EOF) {
                    break;
                }

                // Obtener nombre del token desde la clase sym usando reflexión o mapeo manual
                String nombreToken = obtenerNombreToken(token.sym);
                String lexema = (token.value != null)? token.value.toString() : "N/A";

                String resultado = String.format("Token: %-15s | Lexema: %-15s | Línea: %d | Columna: %d",
                        nombreToken, lexema, token.left + 1, token.right + 1);

                System.out.println(resultado);
                writer.write(resultado + "\n");
            }

            writer.close();
            System.out.println("\nAnalisis finalizado. Resultados guardados en: " + archivoSalida);

        } catch (FileNotFoundException e) {
            System.err.println("Error: No se encontro el archivo fuente.");
        } catch (IOException e) {
            System.err.println("Error de lectura/escritura: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

    //Traducir el ID numérico al nombre del token
    private static String obtenerNombreToken(int id) {
        try {
            java.lang.reflect.Field[] fields = sym.class.getFields();
            for (java.lang.reflect.Field field : fields) {
                if (field.getInt(null) == id) return field.getName();
            }
        } catch (Exception e) { return "UNKNOWN"; }
        return "UNKNOWN";
    }
}

