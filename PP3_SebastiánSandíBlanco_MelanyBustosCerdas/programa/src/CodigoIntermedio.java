package src;

import java.util.ArrayList;
import java.util.List;

public class CodigoIntermedio {

    private List<InstruccionIntermedia> instrucciones;
    private int contadorTemporales;

    public CodigoIntermedio(){
        this.contadorTemporales = 0;
        this.instrucciones = new ArrayList<>();
    }

    public String lecturaArbol(Nodo nodo) {

        InstruccionIntermedia instruccion;
        String izquierda, derecha, temporal;
        switch (nodo.getTipo()){

            //NODOS DE FLUJO: Se recorren sus hijos
            case FLUJO:
                for (Nodo hijo : nodo.getHijos())
                    lecturaArbol(hijo);
                return "";

            //OPERADORES: Se recorre el hijo izquierdo y derecho
            case OPERADOR:
                izquierda = lecturaArbol(nodo.getHijos().get(0));
                derecha = lecturaArbol(nodo.getHijos().get(1));
                temporal = generarTemporal();
                instruccion = new InstruccionIntermedia(nodo.getName(), izquierda, derecha, temporal);
                instrucciones.add(instruccion);
                return temporal;

            //ASIGNACIÓN: Aquí el resultado de la instrucción va a ser el hijo izquierdo
            case ASIGNACION:
                izquierda = lecturaArbol(nodo.getHijos().get(0));
                derecha = lecturaArbol(nodo.getHijos().get(1));
                instruccion = new InstruccionIntermedia("", "", derecha, izquierda);
                instrucciones.add(instruccion);
                return izquierda;

            //VARIABLES Y CONSTANTES: se retornan porque son las hojas
            case VARIABLE:
                return nodo.getName();
            case CONSTANTE:
                return nodo.getName();}
        return "";
    }

    public String generarTemporal(){
        String temporal = "t"+contadorTemporales;
        contadorTemporales++;
        return temporal;
    }

    public void imprimirCodigo(){

        for (InstruccionIntermedia instruccion : instrucciones) {
            instruccion.imprimir();
        }
    }
}
