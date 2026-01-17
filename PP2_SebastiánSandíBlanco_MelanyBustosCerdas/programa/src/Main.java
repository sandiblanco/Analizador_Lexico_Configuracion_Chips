/*
CURSO: Compiladores e Intérpretes
PROYECTO #2: Análisis Sintáctico
ESTUDIANTES: Sebastián Sandí Blanco y Melany Bustos Cerdas
ARCHIVO: Main.java

OBJETIVO: Coordinar el análisis léxico, leer el archivo fuente y generar el reporte de tokens
ENTRADA: Archivo 'lectura/archivoFuente.txt' o 'lectura/pruebaErrores.txt'
SALIDA: Archivo 'lectura/tokens_encontrados.txt' con el detalle de lexemas, líneas y columnas
RESTRICCIONES:
- Requiere que los archivos generados (Scanner, parser y sym) estén en la carpeta "generados"
*/

import java.io.*;
import java_cup.runtime.Symbol;
import generados.Scanner;
import generados.parser;
import generados.sym;
import src.Nodo;

public class Main {

    //archivos de texto
    private static final String archivoFuente = "lectura/archivoFuente.txt";
    private static final String archivoDeErrores = "lectura/pruebaErrores.txt";
    private static final String archivoSalida = "lectura/tokens_encontrados.txt";

    public static void main(String[] args) throws Exception {
        menuOpciones();
    }

    private static void analisisLexico() {

        try {
            Reader lectorArchivo = new BufferedReader(
                    new InputStreamReader(new FileInputStream(archivoFuente), "UTF-8"));
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(archivoSalida), "UTF-8")
            );

            Scanner scanner = new Scanner(lectorArchivo); // Clase generada por JFlex
            parser parser = new parser(scanner);
            //Nodo raiz = (Nodo) parser.parse().value; // Ejecuta el análisis sintáctico

            System.out.println("Iniciando análisis léxico de: " + archivoFuente);
            writer.write("REPORTE DE TOKENS ENCONTRADOS\n");
            writer.write("====================================\n");

            int contadorLexemas = 0;

            while (true) {
                Symbol token = scanner.next_token();

                // sym.EOF es el fin de archivo
                if (token.sym == sym.EOF) {
                    break;
                }

                // Obtener nombre del token desde la clase sym
                String nombreToken = obtenerNombreToken(token.sym);
                String lexema = (token.value != null) ? token.value.toString() : "N/A";

                String resultado = String.format("Token: %-15s | Lexema: %-15s | Línea: %d | Columna: %d",
                        nombreToken, lexema, token.left + 1, token.right + 1);

                System.out.println(resultado);
                writer.write(resultado + "\n");

                //Aumentar el contador de lexemas
                contadorLexemas++;
            }

            writer.close();

            System.out.println("\nCantidad de lexemas encontrados: " + contadorLexemas);
            System.out.println("Análisis finalizado. Resultados guardados en: " + archivoSalida);

        } catch (FileNotFoundException e) {
            System.err.println("Error: No se encontró el archivo fuente.");
        } catch (IOException e) {
            System.err.println("Error de lectura/escritura: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

//    private static void analisisSintactico(){
//        try {
//            Reader lectorArchivo = new BufferedReader(
//                    new InputStreamReader(new FileInputStream(archivoFuente), "UTF-8")
//            );
//
//            Scanner scanner = new Scanner(lectorArchivo);
//            parser parser = new parser(scanner);
//
//            parser.parse();
//
//            System.out.println("\nAnálisis sintáctico finalizado correctamente");
//            System.out.println("Tabla de símbolos generada:");
//            parser.imprimirTablaSimbolos();
//
//        } catch (Exception e) {
//            System.err.println("Error en análisis sintáctico:");
//        }
////        parser p = new parser(scanner);
////        Nodo raiz = (Nodo) p.parse().value; // Ejecuta el análisis sintáctico
//
//    }

    private static void arbolSintactico(){
        try {
            // Reiniciamos el lector para que el parser tenga tokens que leer
            Reader lector = new BufferedReader(new InputStreamReader(new FileInputStream(archivoFuente), "UTF-8"));
            generados.Scanner lexer = new generados.Scanner(lector);
            generados.parser p = new generados.parser(lexer);

            System.out.println("\n--- INICIANDO ANÁLISIS SINTÁCTICO ---");
            Nodo raiz = (Nodo) p.parse().value;

            if (raiz != null) {
                System.out.println("[ÉXITO] Estructura válida según la gramática.");
                raiz.imprimir("");
            }
        } catch (Exception e) {
            System.err.println("Error sintáctico: " + e.getMessage());
        }

    }


    private static void menuOpciones(){
        java.util.Scanner teclado = new java.util.Scanner(System.in);
        int opcion;
        do {
            System.out.println("--- COMPILADOR CHIPS ---\n");
            System.out.println("1. Análisis Léxico (Lista de Tokens)");
            System.out.println("2. Análisis Sintáctico (Validación y Árbol)");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = teclado.nextInt();

            switch (opcion) {
                case 1:
                    analisisLexico();
                    break;
                case 2:
                    arbolSintactico();
                    break;
                case 3:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcion != 3);
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
