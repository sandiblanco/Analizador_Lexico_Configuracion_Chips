/**
 CURSO: Compiladores e Intérpretes
 PROYECTO #3
 ESTUDIANTES: Sebastián Sandí Blanco y Melany Bustos Cerdas
 ARCHIVO: GeneradorMIPS.java

 Clase responsable de convertir todas las instrucciones de código intermedio a código MIPS
 */
package src;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class GeneradorMIPS {

    private List<InstruccionIntermedia> instrucciones;
    private PrintWriter writer;
    private Map<String, Integer> tablaOffsets;
    private int stackOffsetActual;
    private int stringCount = 0;
    private int floatCount = 0;
    private int indiceArgumento = 0;

    private Map<String, String> stringPool = new HashMap<>();
    private Map<String, String> floatPool = new HashMap<>();

    // Variables globales para .data
    private List<String> seccionData = new ArrayList<>();
    private boolean procesandoData = true;

    //CONSTRUCTOR
    public GeneradorMIPS(List<InstruccionIntermedia> instrucciones, String nombreArchivo) {
        this.instrucciones = instrucciones;
        this.tablaOffsets = new HashMap<>();
        this.stackOffsetActual = 0;
        try {
            this.writer = new PrintWriter(new FileWriter(nombreArchivo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generarCodigo() {
        preprocesarData();
        escribirData();
        escribirText();
        writer.close();
    }

    /* ===================== PREPROCESAR .data ===================== */
    private void preprocesarData() {
        for (InstruccionIntermedia ins : instrucciones) {
            switch (ins.operador) {
                case "ENTERO":
                case "BOOLEANO":
                    seccionData.add("    " + ins.resultado + ": .word 0");
                    break;
                case "FLOTANTE":
                    seccionData.add("    " + ins.resultado + ": .float 0.0");
                    break;
                case "STRING":
                    seccionData.add("    " + ins.resultado + ": .asciiz \"\"");
                    break;
                case "CARACTER":
                    seccionData.add("    " + ins.resultado + ": .byte 0");
                    break;
                case "ASIGNACIÓN GLOBAL":
                    seccionData.add(generarAsignacionGlobal(ins));
                    break;
            }
        }
    }

    /* ===================== .data ===================== */
    private void escribirData() {
        writer.println(".data");
        for (String linea : seccionData) {
            writer.println(linea);
        }
        // Agregar los strings y floats
        for (Map.Entry<String, String> entry : stringPool.entrySet()) {
            // Ya se escribieron en el preprocesamiento
        }
        writer.println();
    }

    /* ===================== .text ===================== */
    private void escribirText() {
        boolean dentroDeMain = false;
        boolean dentroFuncion = false;
        String nombreFuncionActual = "";

        writer.println(".text");
        writer.println(".globl main\n");

        for (InstruccionIntermedia ins : instrucciones) {
            switch (ins.operador) {
                case "MAIN":
                    writer.println("main:");
                    writer.println("    # Inicio del programa principal");
                    writer.println("    subu $sp, $sp, 32");  //Reservar espacio para variables locales
                    writer.println("    move $fp, $sp");
                    dentroDeMain = true;
                    stackOffsetActual = 28;  //Empezar desde el tope del espacio reservado
                    break;

                case "FUNCIÓN":
                    nombreFuncionActual = ins.resultado;
                    writer.println("\n" + ins.resultado + ":");
                    writer.println("    # Prólogo de función");
                    writer.println("    subu $sp, $sp, 32");  //Reservar espacio suficiente
                    writer.println("    sw $ra, 28($sp)");
                    writer.println("    sw $fp, 24($sp)");
                    writer.println("    move $fp, $sp");
                    dentroFuncion = true;
                    indiceArgumento = 0;
                    stackOffsetActual = 20; //Empezar en offset 20
                    break;

                case "PARÁMETRO":
                    if (dentroFuncion) {
                        //Guardar parámetro en el stack
                        int indice = Integer.parseInt(ins.argumento1);
                        if (indice < 4) {
                            //Parámetro entre $a0-$a3
                            if (!tablaOffsets.containsKey(ins.resultado)) {
                                tablaOffsets.put(ins.resultado, stackOffsetActual);
                                stackOffsetActual -= 4;
                            }
                            writer.println("    # Parámetro " + ins.resultado);
                            writer.println("    sw $a" + indice + ", " + tablaOffsets.get(ins.resultado) + "($fp)");
                        } else {
                            //Parámetros adicionales vienen del stack del que llama
                            writer.println("    # Parámetro " + ins.resultado + " desde stack");
                            int offset = 32 + ((indice - 4) * 4); // Después del frame de la función
                            writer.println("    lw $t0, " + offset + "($fp)");
                            if (!tablaOffsets.containsKey(ins.resultado)) {
                                tablaOffsets.put(ins.resultado, stackOffsetActual);
                                stackOffsetActual -= 4;
                            }
                            writer.println("    sw $t0, " + tablaOffsets.get(ins.resultado) + "($fp)");
                        }
                    }
                    break;

                case "FIN DE FUNCIÓN":
                    writer.println(nombreFuncionActual + "_end:");
                    writer.println("    # Epílogo de función");
                    writer.println("    move $sp, $fp");
                    writer.println("    lw $fp, 24($sp)");
                    writer.println("    lw $ra, 28($sp)");
                    writer.println("    addu $sp, $sp, 32");
                    writer.println("    jr $ra");
                    writer.println();
                    dentroFuncion = false;
                    nombreFuncionActual = "";
                    stackOffsetActual = 0;
                    tablaOffsets.clear();
                    break;

                case "FIN DE MAIN":
                    writer.println("    # Finalizar programa");
                    writer.println("    li $v0, 10");
                    writer.println("    syscall");
                    writer.println();
                    dentroDeMain = false;
                    break;

                case "ARGUMENTO":
                    if (indiceArgumento < 4) {
                        writer.println("    # Argumento " + indiceArgumento);
                        cargarEnRegistro("$a" + indiceArgumento, ins.argumento1);
                    } else {
                        // Argumentos extra por pila
                        writer.println("    # Argumento " + indiceArgumento + " via stack");
                        cargarEnRegistro("$t0", ins.argumento1);
                        writer.println("    subu $sp, $sp, 4");
                        writer.println("    sw $t0, 0($sp)");
                    }
                    indiceArgumento++;
                    break;

                case "CALL":
                    writer.println("    # Llamada a función " + ins.resultado);
                    writer.println("    jal " + ins.resultado);
                    // Restaurar stack si había argumentos extra
                    int numArgs = Integer.parseInt(ins.argumento1);
                    if (numArgs > 4) {
                        int bytesExtra = (numArgs - 4) * 4;
                        writer.println("    addu $sp, $sp, " + bytesExtra);
                    }
                    // Guardar valor de retorno si hay temporal
                    if (!ins.argumento2.isEmpty()) {
                        writer.println("    sw $v0, " + obtenerDireccion(ins.argumento2));
                    }
                    indiceArgumento = 0;
                    break;

                case "RETURN":
                    writer.println("    # Return");
                    cargarEnRegistro("$v0", ins.argumento1);
                    writer.println("    j " + nombreFuncionActual + "_end");
                    break;

                case "=":
                case "+":
                case "-":
                case "*":
                case "/":
                case ">":
                case "<":
                case ">=":
                case "<=":
                case "==":
                case "!=":
                case "AND":
                case "OR":
                case "NEGACION":
                    if (dentroDeMain || dentroFuncion) {
                        generarInstruccion(ins);
                    }
                    break;

                case "SHOW":
                    if (dentroDeMain || dentroFuncion) {
                        generarShow(ins);
                    }
                    break;

                case "JUMP":
                    writer.println("    j " + ins.resultado);
                    break;

                case "JUMP IF FALSE":
                    cargarEnRegistro("$t0", ins.argumento1);
                    writer.println("    beq $t0, $zero, " + ins.resultado);
                    break;

                case "ETIQUETA INICIAL":
                case "ETIQUETA FINAL":
                case "SIGUIENTE CASO":
                    if (dentroDeMain || dentroFuncion) {
                        writer.println(ins.resultado + ":");
                    }
                    break;

                case "IF END":
                    if (dentroDeMain || dentroFuncion) {
                        writer.println("    j endF");
                    }
                    break;

                case "ELSE":
                    if (dentroDeMain || dentroFuncion) {
                        writer.println("else:");
                    }
                    break;

                case "ETIQUETA FINAL IF":
                    if (dentroDeMain || dentroFuncion) {
                        writer.println("endF:");
                    }
                    break;
            }
        }
    }

    /* ===================== GENERAR INSTRUCCIONES ===================== */
    private void generarInstruccion(InstruccionIntermedia ins) {
        switch (ins.operador) {
            case "=":
                writer.println("    # Asignación: " + ins.argumento1 + " = " + ins.argumento2);
                cargarEnRegistro("$t0", ins.argumento2);
                writer.println("    sw $t0, " + obtenerDireccion(ins.argumento1));
                break;

            case "+":
                generarAritmetica("add", ins);
                break;

            case "-":
                generarAritmetica("sub", ins);
                break;

            case "*":
                generarAritmetica("mul", ins);
                break;

            case "/":
                generarDivision(ins);
                break;

            case ">":
                generarComparacion("sgt", ins);
                break;

            case "<":
                generarComparacion("slt", ins);
                break;

            case ">=":
                generarComparacion("sge", ins);
                break;

            case "<=":
                generarComparacion("sle", ins);
                break;

            case "==":
                generarComparacion("seq", ins);
                break;

            case "!=":
                generarComparacion("sne", ins);
                break;

            case "AND":
                generarLogica("and", ins);
                break;

            case "OR":
                generarLogica("or", ins);
                break;

            case "NEGACION":
                writer.println("    # Negación lógica");
                cargarEnRegistro("$t0", ins.argumento1);
                writer.println("    seq $t1, $t0, $zero");  // t1 = (t0 == 0)
                writer.println("    sw $t1, " + obtenerDireccion(ins.resultado));
                break;
        }
    }

    /* ===================== SHOW ===================== */
    private void generarShow(InstruccionIntermedia ins) {
        String valor = ins.resultado;
        writer.println("    # SHOW: " + valor);

        // Determinar el tipo de dato
        if (valor.matches("-?\\d+")) {
            // Entero
            cargarEnRegistro("$a0", valor);
            writer.println("    li $v0, 1");
            writer.println("    syscall");
        } else if (valor.matches("-?\\d+\\.\\d+")) {
            // Float
            cargarEnRegistro("$f12", valor);
            writer.println("    li $v0, 2");
            writer.println("    syscall");
        } else if (valor.startsWith("\"") && valor.endsWith("\"")) {
            // String
            String label = registrarString(valor);
            writer.println("    la $a0, " + label);
            writer.println("    li $v0, 4");
            writer.println("    syscall");
        } else if (valor.matches("'(.)'")) {
            // Char
            char c = valor.charAt(1);
            writer.println("    li $a0, " + (int) c);
            writer.println("    li $v0, 11");
            writer.println("    syscall");
        } else {
            // Variable
            cargarEnRegistro("$a0", valor);
            writer.println("    li $v0, 1");
            writer.println("    syscall");
        }
    }

    /* ===================== HELPERS ===================== */
    private void generarAritmetica(String op, InstruccionIntermedia ins) {
        writer.println("    # " + ins.resultado + " = " + ins.argumento1 + " " + ins.operador + " " + ins.argumento2);
        cargarEnRegistro("$t0", ins.argumento1);
        cargarEnRegistro("$t1", ins.argumento2);
        writer.println("    " + op + " $t2, $t0, $t1");
        writer.println("    sw $t2, " + obtenerDireccion(ins.resultado));
    }

    private void generarDivision(InstruccionIntermedia ins) {
        writer.println("    # " + ins.resultado + " = " + ins.argumento1 + " / " + ins.argumento2);
        cargarEnRegistro("$t0", ins.argumento1);
        cargarEnRegistro("$t1", ins.argumento2);
        writer.println("    div $t0, $t1");
        writer.println("    mflo $t2");
        writer.println("    sw $t2, " + obtenerDireccion(ins.resultado));
    }

    private void generarComparacion(String op, InstruccionIntermedia ins) {
        writer.println("    # " + ins.resultado + " = " + ins.argumento1 + " " + ins.operador + " " + ins.argumento2);
        cargarEnRegistro("$t0", ins.argumento1);
        cargarEnRegistro("$t1", ins.argumento2);
        writer.println("    " + op + " $t2, $t0, $t1");
        writer.println("    sw $t2, " + obtenerDireccion(ins.resultado));
    }

    private void generarLogica(String op, InstruccionIntermedia ins) {
        writer.println("    # " + ins.resultado + " = " + ins.argumento1 + " " + ins.operador + " " + ins.argumento2);
        cargarEnRegistro("$t0", ins.argumento1);
        cargarEnRegistro("$t1", ins.argumento2);
        writer.println("    " + op + " $t2, $t0, $t1");
        writer.println("    sw $t2, " + obtenerDireccion(ins.resultado));
    }

    private void cargarEnRegistro(String reg, String valor) {
        // BOOLEAN
        if (valor.equals("true")) {
            writer.println("    li " + reg + ", 1");
            return;
        }
        if (valor.equals("false")) {
            writer.println("    li " + reg + ", 0");
            return;
        }

        // ENTERO
        if (valor.matches("-?\\d+")) {
            writer.println("    li " + reg + ", " + valor);
            return;
        }

        // FLOAT
        if (valor.matches("-?\\d+\\.\\d+")) {
            String label = registrarFloat(valor);
            if (reg.startsWith("$f")) {
                writer.println("    l.s " + reg + ", " + label);
            } else {
                writer.println("    lwc1 " + reg + ", " + label);
            }
            return;
        }

        // CHAR
        if (valor.matches("'(.)'")) {
            char c = valor.charAt(1);
            writer.println("    li " + reg + ", " + (int) c);
            return;
        }

        // STRING
        if (valor.startsWith("\"") && valor.endsWith("\"")) {
            String label = registrarString(valor);
            writer.println("    la " + reg + ", " + label);
            return;
        }

        // VARIABLE o TEMPORAL
        writer.println("    lw " + reg + ", " + obtenerDireccion(valor));
    }

    private String generarAsignacionGlobal(InstruccionIntermedia instruccion) {
        String valor = instruccion.argumento2;

        // BOOLEAN
        if (valor.equals("true")) {
            return "    " + instruccion.argumento1 + ": .word 1";
        }
        if (valor.equals("false")) {
            return "    " + instruccion.argumento1 + ": .word 0";
        }

        // ENTERO
        if (valor.matches("-?\\d+")) {
            return "    " + instruccion.argumento1 + ": .word " + valor;
        }

        // FLOAT
        if (valor.matches("-?\\d+\\.\\d+")) {
            return "    " + instruccion.argumento1 + ": .float " + valor;
        }

        // CHAR
        if (valor.matches("'(.)'")) {
            char c = valor.charAt(1);
            return "    " + instruccion.argumento1 + ": .word " + (int) c;
        }

        // STRING
        if (valor.startsWith("\"") && valor.endsWith("\"")) {
            String contenido = valor.substring(1, valor.length() - 1);
            contenido = contenido
                    .replace("\\n", "\n")
                    .replace("\\t", "\t")
                    .replace("\\\"", "\"");
            return "    " + instruccion.argumento1 + ": .asciiz \"" + contenido + "\"";
        }

        return "    " + instruccion.argumento1 + ": .word 0";
    }

    private String obtenerDireccion(String id) {
        // Si es una variable global, usar su etiqueta directamente
        for (String linea : seccionData) {
            if (linea.trim().startsWith(id + ":")) {
                return id;
            }
        }

        // Si es variable local o temporal, usar offset en stack con $fp
        if (!tablaOffsets.containsKey(id)) {
            stackOffsetActual -= 4;
            tablaOffsets.put(id, stackOffsetActual);
        }
        return tablaOffsets.get(id) + "($fp)";
    }

    /* ===================== REGISTRO DE STRINGS Y FLOATS ===================== */
    private String registrarString(String valor) {
        //Evitar duplicados
        if (stringPool.containsKey(valor)) {
            return stringPool.get(valor);
        }

        String label = "_str" + stringCount++;
        stringPool.put(valor, label);

        //Quitar comillas
        String contenido = valor.substring(1, valor.length() - 1);

        //Escapar caracteres básicos
        contenido = contenido
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");

        //Agregar a la sección data
        seccionData.add("    " + label + ": .asciiz \"" + contenido + "\"");

        return label;
    }

    private String registrarFloat(String valor) {
        //Evitar duplicados
        if (floatPool.containsKey(valor)) {
            return floatPool.get(valor);
        }

        String label = "_flt" + floatCount++;
        floatPool.put(valor, label);

        //Agregar a la sección data
        seccionData.add("    " + label + ": .float " + valor);

        return label;
    }
}