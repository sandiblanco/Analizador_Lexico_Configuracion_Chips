package src;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        escribirData();
        escribirText();
        writer.close();
    }

    /* ===================== .data ===================== */
    private void escribirData() {
        writer.println(".data");
        for (InstruccionIntermedia ins : instrucciones) {
            switch (ins.operador) {
                case "ENTERO":
                case "BOOLEANO":
                    writer.println("    " + ins.resultado + ": .word 0");
                    break;
                case "FLOTANTE":
                    writer.println("    " + ins.resultado + ": .float 0.0");
                    break;
                case "STRING":
                    writer.println("    " + ins.resultado + ": .asciiz \"\"");
                    break;
                case "CARACTER":
                    writer.println("    " + ins.resultado + ": .byte 0");
                    break;
                case "=":
                    generarInstruccion(ins);
            }
        }
        writer.println();
    }

    /* ===================== .text ===================== */
    private void escribirText() {

        boolean dentroDeMain = false;
        boolean dentroFuncion = false;
        writer.println(".text");
        writer.println(".globl main\n");

        for (InstruccionIntermedia ins : instrucciones) {

            switch (ins.operador) {

                case "MAIN":
                    writer.println("main:");
                    dentroDeMain = true;
                    break;

                case "FUNCIÓN":
                    if (!ins.operador.equals("MAIN")) {
                        writer.println(ins.resultado + ":");
                        writer.println("    subu $sp, $sp, 4");
                        writer.println("    sw $ra, 0($sp)");
                        dentroFuncion = true;
                        indiceArgumento = 0; //reiniciar argumentos
                    }
                    break;

                case "FIN DE FUNCIÓN":
                    writer.println("    lw $ra, 0($sp)");
                    writer.println("    addu $sp, $sp, 4");
                    writer.println("    jr $ra");
                    writer.println();
                    dentroFuncion = false;
                    break;

                case "FIN DE MAIN":
                    writer.println("    li $v0, 10");
                    writer.println("    syscall");
                    writer.println();
                    break;

                case "ARGUMENTO":

                    if (indiceArgumento < 4) {
                        cargarEnRegistro("$a" + indiceArgumento, ins.argumento1);
                    } else {
                        // argumentos extra por pila
                        cargarEnRegistro("$t0", ins.resultado);
                        writer.println("    subu $sp, $sp, 4");
                        writer.println("    sw $t0, 0($sp)");
                    }

                    indiceArgumento++;
                    break;

                case "=":
                case "+":
                case "-":
                case "*":
                case "/":
                case "JUMP":
                case "JUMP IF FALSE":
                case "CALL":
                case "RETURN":
                case "SHOW":

                    if (!dentroDeMain && !dentroFuncion) {
                        break;
                    }
                    generarInstruccion(ins);
                    break;

                case "ETIQUETA INICIAL":
                case "ETIQUETA FINAL":
                case "SIGUIENTE CASO":
                    if (dentroDeMain) {
                        writer.println(ins.resultado + ":");
                    }
                    break;
            }
        }
    }

    private void generarInstruccion(InstruccionIntermedia ins) {
        switch (ins.operador) {

            case "=":
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

            case "JUMP":
                writer.println("    j " + ins.resultado);
                break;

            case "JUMP IF FALSE":
                cargarEnRegistro("$t0", ins.argumento1);
                writer.println("    beq $t0, $zero, " + ins.resultado);
                break;

            case "CALL":
                writer.println("    jal " + ins.resultado);
                writer.println("    sw $v0, " + obtenerDireccion(ins.argumento2));
                break;

            case "RETURN":
                cargarEnRegistro("$v0", ins.argumento1);
                break;

            case "SHOW":
                cargarEnRegistro("$a0", ins.resultado);
                writer.println("    li $v0, 1");
                writer.println("    syscall");
                break;
        }
    }
    /* ===================== HELPERS ===================== */

    private void generarAritmetica(String op, InstruccionIntermedia ins) {
        cargarEnRegistro("$t0", ins.argumento1);
        cargarEnRegistro("$t1", ins.argumento2);
        writer.println("    " + op + " $t2, $t0, $t1");
        writer.println("    sw $t2, " + obtenerDireccion(ins.resultado));
    }

    private void generarDivision(InstruccionIntermedia ins) {
        cargarEnRegistro("$t0", ins.argumento1);
        cargarEnRegistro("$t1", ins.argumento2);
        writer.println("    div $t0, $t1");
        writer.println("    mflo $t2");
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
            String label = registrarFloat(valor); // .float en .data
            writer.println("    l.s " + reg + ", " + label);
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

        // VARIABLE (por defecto)
        writer.println("    lw " + reg + ", " + obtenerDireccion(valor));
    }

    private String obtenerDireccion(String id) {
        if (!tablaOffsets.containsKey(id)) {
            stackOffsetActual -= 4;
            tablaOffsets.put(id, stackOffsetActual);
        }
        return tablaOffsets.get(id) + "($sp)";
    }


    //REGISTRAR STRINGS Y FLOTANTES
    private String registrarString(String valor) {

        // Evitar duplicados
        if (stringPool.containsKey(valor)) {
            return stringPool.get(valor);
        }

        String label = "_str" + stringCount++;
        stringPool.put(valor, label);

        // Quitar comillas
        String contenido = valor.substring(1, valor.length() - 1);

        // Escapar caracteres básicos
        contenido = contenido
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\"", "\"");

        writer.println("    " + label + ": .asciiz \"" + contenido + "\"");

        return label;
    }

    private String registrarFloat(String valor) {

        // Evitar duplicados
        if (floatPool.containsKey(valor)) {
            return floatPool.get(valor);
        }

        String label = "_flt" + floatCount++;
        floatPool.put(valor, label);

        writer.println("    " + label + ": .float " + valor);

        return label;
    }
}
