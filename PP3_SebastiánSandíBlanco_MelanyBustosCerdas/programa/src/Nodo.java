/**
 CURSO: Compiladores e Intérpretes
 PROYECTO #3: Análisis Semántico
 ESTUDIANTES: Sebastián Sandí Blanco y Melany Bustos Cerdas
 ARCHIVO: Nodo.java

 OBJETIVO: Clase base para representar los nodos del Árbol Sintáctico y gestionar el análisis semántico..
 ENTRADA: Nombre del nodo y sus hijos y datos requeridos para el análisis semántico.
 SALIDA: Estructura jerárquica del programa.
 */

package src;

import java.util.ArrayList;
import java.util.List;

public class Nodo {
    private String nombre;
    private TipoNodo tipo;
    private List<Nodo> hijos;
    private String tipoDato;
    private Integer valor; // para tamaños, columnas de arreglos.
    
 public void setTipoDato(String t) {
        this.tipoDato = t;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setValor(Integer v) {
        this.valor = v;
    }

    public Integer getValor() {
        return this.valor;
    }

    // Códigos ANSI para colores
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";

    public Nodo(String nombre, TipoNodo tipo) {
        this.nombre = nombre;
        this.tipo = tipo; //variable, constante, etc
        this.hijos = new ArrayList<>();
    }

    // Constructor con tipo por defecto
    public Nodo(String nombre) {
        this.nombre =  nombre;
        this.tipo = TipoNodo.FLUJO;   // default
        this.hijos = new ArrayList<>();
    }

    public void agregarHijo(Nodo hijo) {
        if (hijo != null) {
            hijos.add(hijo);
        }
    }

    // Imprimir el árbol con sangrías
    public void imprimir(String prefijo, boolean esUltimo) {

        String color = RESET;

        // Lógica de colores según el contenido del nombre
        if (nombre.contains("ERROR")) {
            color = RED;
        } else if (nombre.contains("DECLARACIONES GLOBALES Y FUNCIONES")) {
            color = GREEN;
        } else if (nombre.contains("Función Main")) {
            color = YELLOW;
        } else if (nombre == "Función") {
            color = PURPLE;
        } else if (nombre == "FOR" || nombre == "LOOP" || nombre == "IF") {
            color = BLUE;
        } else if (nombre.contains("PROGRAMA")) {
            color = CYAN;
            System.out.println("\n");
        }

        System.out.println(prefijo + (esUltimo ? "└── " : "├── ") + color + nombre + RESET);

        for (int i = 0; i < hijos.size(); i++) {
            boolean ultimoHijo = (i == hijos.size() - 1);
            // Si no es el último hijo del padre entonces la línea sigue hacia abajo
            String nuevoPrefijo = prefijo + (esUltimo ? "    " : "│   ");
            hijos.get(i).imprimir(nuevoPrefijo, ultimoHijo);
        }
    }

    public String getName(){
     return this.nombre;
    }

    public TipoNodo getTipo(){
        return this.tipo;
    }

    public List<Nodo> getHijos(){
        return this.hijos;
    }

}
