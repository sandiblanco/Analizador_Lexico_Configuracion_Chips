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
        switch (nodo.getTipo()){

            //NODOS DE FLUJO: Se recorren sus hijos
            case FLUJO:
                for (Nodo hijo : nodo.getHijos())
                    lecturaArbol(hijo);
                return "";

            //OPERADORES: Se recorre el hijo izquierdo y derecho
            case OPERADOR:
                String izquierda = lecturaArbol(nodo.getHijos().get(0));
                String derecha = lecturaArbol(nodo.getHijos().get(1));
                String temporal = generarTemporal();
                instruccion = new InstruccionIntermedia(nodo.getName(), izquierda, derecha, temporal);
                instrucciones.add(instruccion);

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
