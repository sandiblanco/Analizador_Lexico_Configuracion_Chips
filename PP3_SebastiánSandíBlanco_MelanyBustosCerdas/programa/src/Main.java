/*
CURSO: Compiladores e Intérpretes
PROYECTO #3: Análisis Semántico
ESTUDIANTES: Sebastián Sandí Blanco y Melany Bustos Cerdas
ARCHIVO: Main.java

OBJETIVO: Coordinar el análisis léxico, leer el archivo fuente, generar el reporte de tokens y mostrar análisis sintáctico y semántico
ENTRADA: Archivo 'lectura/archivoFuente.txt' o 'lectura/pruebaErrores.txt'
SALIDA: Archivo 'lectura/tokens_encontrados.txt' con el detalle de lexemas, líneas y columnas, análisis sintáctico y semántico
RESTRICCIONES:
- Requiere que los archivos generados (Scanner, parser y sym) estén en la carpeta "generados"
*/

import java.io.*;
import java.util.List;

import java_cup.runtime.Symbol;
import generados.Scanner;
import generados.parser;
import generados.sym;
import src.CodigoIntermedio;
import src.GeneradorMIPS;
import src.InstruccionIntermedia;
import src.Nodo;

public class Main {

    //archivos de texto
    private static final String archivoFuente = "lectura/archivoFuente.txt";
    private static final String archivoDeErrores = "lectura/pruebaErrores.txt";
    private static final String archivoSalida = "lectura/tokens_encontrados.txt";
    private static final String codigoMIPS = "lectura/codigoMIPS.asm";

    // Colores
    public static final String RESET = "\u001B[0m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    static CodigoIntermedio intermedio;

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

     private static void analisisSintactico() {
        try {
            Reader lectorArchivo = new BufferedReader(
                    new InputStreamReader(new FileInputStream(archivoFuente), "UTF-8")
            );

            Scanner scanner = new Scanner(lectorArchivo);
            parser parser = new parser(scanner);

            System.out.println("\n--- INICIANDO ANÁLISIS SINTÁCTICO Y SEMÁNTICO ---\n");

            parser.parse();

            //Imprime errores después del parse
            parser.imprimirErrores();

            if (parser.errorDetectado) {
                System.out.println("\n[ERROR] Se detectaron errores en el programa.");
            } else {
                System.out.println("\n[ÉXITO] Análisis finalizado sin errores.");
            }

            System.out.println("\n--- TABLA DE SÍMBOLOS ---");
            parser.imprimirTablaSimbolos();

            System.out.println("\n--- FIN DEL ANÁLISIS ---");

        } catch (Exception e) {
            System.err.println("Error crítico durante el análisis:");
            e.printStackTrace();
        }
    }

    private static void arbolSintactico(){
        try {
            // Reiniciamos el lector para que el parser tenga tokens que leer
            Reader lector = new BufferedReader(new InputStreamReader(new FileInputStream(archivoFuente), "UTF-8"));
            generados.Scanner lexer = new generados.Scanner(lector);
            generados.parser p = new generados.parser(lexer);

            System.out.println("\n--- IMPRIMIENDO ÁRBOL SINTÁCTICO ---\n");
            Nodo raiz = (Nodo) p.parse().value;

            if (raiz != null) {
                raiz.imprimir("", true);
            }
        } catch (Exception e) {
            System.err.println("Error sintáctico: " + e.getMessage());
        }

    }

    private static void generarCodigoIntermedio(){
        try {
            // Reiniciamos el lector para que el parser tenga tokens que leer
            Reader lector = new BufferedReader(new InputStreamReader(new FileInputStream(archivoFuente), "UTF-8"));
            generados.Scanner lexer = new generados.Scanner(lector);
            generados.parser p = new generados.parser(lexer);

            Nodo raiz = (Nodo) p.parse().value;
            intermedio = new CodigoIntermedio();

            if (raiz != null)
                intermedio.lecturaArbol(raiz);
        } catch (Exception e) {
            System.err.println("Error generando código intermedio: " + e.getMessage());
        }
    }

    private static void imprimirCodigoIntermedio(){
        System.out.println(GREEN + "\n--- CÓDIGO INTERMEDIO ---" + RESET);
        intermedio.imprimirCodigo();

    }

    private static void generarCodigoMaquina(){
        try {
            // Preparar los archivos de escritura y lectura
            Reader lector = new BufferedReader(new InputStreamReader(new FileInputStream(archivoFuente), "UTF-8"));

            List<InstruccionIntermedia> instrucciones = intermedio.getInstrucciones();
            GeneradorMIPS codigoMaquina = new GeneradorMIPS(instrucciones, codigoMIPS);
            codigoMaquina.generarCodigo();

            System.out.println("Generación completada. Resultados guardados en: " + codigoMIPS);

        } catch (FileNotFoundException e) {
            System.err.println("Error: No se encontró el archivo fuente.");
        } catch (IOException e) {
            System.err.println("Error de lectura/escritura: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error generando código intermedio: " + e.getMessage());
        }
    }


    private static void menuOpciones(){

        java.util.Scanner teclado = new java.util.Scanner(System.in);
        int opcion = -1;
        do {
            System.out.println("\n" + YELLOW + "========================================" + RESET);
            System.out.println(CYAN + "          --- COMPILADOR CHIPS ---" + RESET);
            System.out.println(YELLOW + "========================================" + RESET);
            System.out.println("1. " + GREEN + "Análisis Léxico" + RESET + " (Lista de Tokens)");
            System.out.println("2. " + GREEN + "Análisis Sintáctico y Semántico" + RESET + " (Tabla de símbolos)");
            System.out.println("3. " + GREEN + "Árbol Sintáctico" + RESET);
            System.out.println("4. " + GREEN + "Imprimir código intermedio" + RESET);
            System.out.println("5. " + GREEN + "Convertir a código máquina" + RESET);
            System.out.println("6. Salir");
            System.out.print("\nSeleccione una opción: ");
            try {
                opcion = teclado.nextInt();
            } catch (Exception e) {
                System.out.println("Por favor, ingrese un número válido.");
                teclado.nextLine(); // Limpiar el buffer
                continue;
            }

            switch (opcion) {
                case 1:
                    analisisLexico();
                    esperarRegreso(teclado);
                    break;
                case 2:
                    analisisSintactico();
                    esperarRegreso(teclado);
                    break;
                case 3:
                    arbolSintactico();
                    esperarRegreso(teclado);
                    break;
                case 4:
                    generarCodigoIntermedio();
                    imprimirCodigoIntermedio();
                    esperarRegreso(teclado);
                    break;
                case 5:
                    generarCodigoIntermedio();
                    generarCodigoMaquina();
                    esperarRegreso(teclado);
                    break;
                case 6:
                    System.out.println("Cerrando el sistema...");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcion != 6);
    }

    // Función para que el usuario tenga que presionar 0 para volver al menú
    private static void esperarRegreso(java.util.Scanner teclado) {
        int volver = -1;
        System.out.println("\n" + YELLOW + "----------------------------------------" + RESET);
        System.out.print("Presione " + CYAN + "0" + RESET + " para volver al menú principal: ");

        while (volver != 0) {
            try {
                volver = teclado.nextInt();
                if (volver != 0) System.out.print("Entrada incorrecta. Presione 0: ");
            } catch (Exception e) {
                System.out.print("Entrada incorrecta. Presione 0: ");
                teclado.nextLine();
            }
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
