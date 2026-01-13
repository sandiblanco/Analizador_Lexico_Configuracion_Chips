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
    public void imprimir(String sangria) {
        System.out.println(sangria + "|-- " + nombre);
        for (Nodo hijo : hijos) {
            hijo.imprimir(sangria + "    ");
        }
    }
}
