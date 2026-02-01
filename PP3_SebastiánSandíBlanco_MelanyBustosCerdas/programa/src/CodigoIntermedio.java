package src;

import java.util.ArrayList;
import java.util.List;

public class CodigoIntermedio {

    private List<InstruccionIntermedia> instrucciones;
    private int contadorTemporales;
    private int contadorParametro;

    public CodigoIntermedio(){
        this.instrucciones = new ArrayList<>();
    }

    public String lecturaArbol(Nodo nodo) {

        InstruccionIntermedia instruccion;
        String izquierda, derecha, temporal;
        switch (nodo.getTipo()){

            //NODOS DE FLUJO: Se recorren sus hijos, no es necesario crear una cuádrupla
            case FLUJO:
                for (Nodo hijo : nodo.getHijos())
                    lecturaArbol(hijo);
                return "";



            //OPERADORES o ASIGNACIONES: Se recorre el hijo izquierdo y derecho
            case OPERADOR:
            case ASIGNACION:
                izquierda = lecturaArbol(nodo.getHijos().get(0));
                derecha = lecturaArbol(nodo.getHijos().get(1));
                temporal = generarTemporal();
                instruccion = new InstruccionIntermedia(nodo.getName(), izquierda, derecha, temporal);
                instrucciones.add(instruccion);
                return temporal;

            //FUNCION:
            case FUNCION:
                //Añadir primero la instrucción de función para hacer "func begin" antes del bloque
                instruccion = new InstruccionIntermedia("FUNCIÓN", "", "", nodo.getName());
                instrucciones.add(instruccion);
                izquierda = lecturaArbol(nodo.getHijos().get(0)); //Estos son los parámetros
                derecha = lecturaArbol(nodo.getHijos().get(1)); //Este es el bloque de la función
                //instrucción final
                InstruccionIntermedia instruccionFinal = new InstruccionIntermedia("FIN DE FUNCIÓN", "", "", "");
                instrucciones.add(instruccionFinal);
                return "";

            case PARAMETROS:
                for (Nodo parametro : nodo.getHijos()) {
                    instruccion = new InstruccionIntermedia("PARÁMETRO", String.valueOf(contadorParametro), "", parametro.getName());
                    instrucciones.add(instruccion); //leer todos los parámetros de la función
                    contadorParametro++; //simula crear un nuevo espacio en la pila
                }
                contadorParametro = 0; //reiniciar el contador porque es independiente por cada función

            //TIPO: sirve para las declaraciones sin asignación
            case TIPO:
                String identificador = nodo.getHijos().get(0).getName();

                // Lógica de declaraciones según el tipo del identificador
                if (nodo.getName().contains("int")) {
                    instruccion = new InstruccionIntermedia("ENTERO", "", "", identificador);
                    instrucciones.add(instruccion);
                } else if (nodo.getName().contains("float")) {
                    instruccion = new InstruccionIntermedia("FLOTANTE", "", "", identificador);
                    instrucciones.add(instruccion);
                } else if (nodo.getName().contains("bool")) {
                    instruccion = new InstruccionIntermedia("BOOLEANO", "", "", identificador);
                    instrucciones.add(instruccion);
                } else if (nodo.getName().contains("string")) {
                    instruccion = new InstruccionIntermedia("STRING", "", "", identificador);
                    instrucciones.add(instruccion);
                } else if (nodo.getName().contains("char")) {
                    instruccion = new InstruccionIntermedia("CARACTER", "", "", identificador);
                    instrucciones.add(instruccion);
                }
                return "";

            //VARIABLES Y CONSTANTES: se retornan porque son las hojas
            case VARIABLE:
                return nodo.getName();
            case CONSTANTE:
                return nodo.getName();}
        return "";
    }


    //Imprimir todas las instrucciones guardadas
    public void imprimirCodigo(){

        for (InstruccionIntermedia instruccion : instrucciones) {
            instruccion.imprimir();
        }
    }

    // GENERACIÓN
    public String generarTemporal(){
        String temporal = "t"+contadorTemporales;
        contadorTemporales++;
        return temporal;
    }

}
