/**
 CURSO: Compiladores e Intérpretes
 PROYECTO #2: Análisis Sintáctico
 ESTUDIANTES: Sebastián Sandí Blanco y Melany Bustos Cerdas
 ARCHIVO: Nodo.java

 OBJETIVO: Clase base para representar los nodos del Árbol Sintáctico.
 ENTRADA: Nombre del nodo y sus hijos.
 SALIDA: Estructura jerárquica del programa.
 */

package src;

import java.util.ArrayList;
import java.util.List;

public class Nodo {
    private String nombre;
    private List<Nodo> hijos;

    public Nodo(String nombre) {
        this.nombre = nombre;
        this.hijos = new ArrayList<>();
    }

    public void agregarHijo(Nodo hijo) {
        if (hijo != null) {
            hijos.add(hijo);
        }
    }

    // Imprimir el árbol con sangrías
    public void imprimir(String prefijo, boolean esUltimo) {
        System.out.println(prefijo + (esUltimo ? "└── " : "├── ") + nombre);

        for (int i = 0; i < hijos.size(); i++) {
            boolean ultimoHijo = (i == hijos.size() - 1);
            // Si no es el último hijo del padre entonces la línea sigue hacia abajo
            String nuevoPrefijo = prefijo + (esUltimo ? "    " : "│   ");
            hijos.get(i).imprimir(nuevoPrefijo, ultimoHijo);
        }
    }
}
