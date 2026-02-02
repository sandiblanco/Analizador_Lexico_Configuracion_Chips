/**
 CURSO: Compiladores e Intérpretes
 PROYECTO #3
 ESTUDIANTES: Sebastián Sandí Blanco y Melany Bustos Cerdas
 ARCHIVO: CodigoIntermedio.java

 Clase responsable de convertir todas las instrucciones del código fuente a código intermedio
 */
package src;

import java.util.ArrayList;
import java.util.List;

public class CodigoIntermedio {

    private List<InstruccionIntermedia> instrucciones;
    private int contadorTemporales;
    private int contadorParametro;
    private int contadorEtiqueta;

    public CodigoIntermedio(){
        this.instrucciones = new ArrayList<>();
    }

    public String lecturaArbol(Nodo nodo) {

        InstruccionIntermedia instruccion;
        String izquierda, derecha, etiquetaInicio, etiquetaCierre, casosDecide, temporal = "";
        switch (nodo.getTipo()){

            //NODOS DE FLUJO: Se recorren sus hijos, no es necesario crear una cuádrupla
            case FLUJO:
                for (Nodo hijo : nodo.getHijos())
                    temporal = lecturaArbol(hijo);
                return temporal;

            //FOR
            case FOR:
                //Asignación inicial i = 0
                lecturaArbol(nodo.getHijos().get(0));

                //Etiqueta inicial L1
                contadorEtiqueta++;
                etiquetaInicio = "L"+contadorEtiqueta;
                contadorEtiqueta++;
                etiquetaCierre = "L"+contadorEtiqueta;
                instruccion = new InstruccionIntermedia("ETIQUETA INICIAL", "", "", etiquetaInicio);
                instrucciones.add(instruccion);

                //Condición del for i < 10
                temporal = lecturaArbol(nodo.getHijos().get(1));

                //SALTO
                instruccion = new InstruccionIntermedia("JUMP IF FALSE", temporal, "", etiquetaCierre);
                instrucciones.add(instruccion);

                //Bloque de sentencias
                if(nodo.getHijos().get(3) != null)
                    lecturaArbol(nodo.getHijos().get(3)); //el bloque del for

                //Atualización i++
                lecturaArbol(nodo.getHijos().get(2));

                //JUMP FINAL
                instruccion = new InstruccionIntermedia("JUMP", "", "", etiquetaInicio);
                instrucciones.add(instruccion);

                //Etiqueta de cierre
                instruccion = new InstruccionIntermedia("ETIQUETA FINAL", "", "", etiquetaCierre);
                instrucciones.add(instruccion);

                return "";

            //LOOP
            case LOOP:
                Nodo bloqueLoop = nodo.getHijos().getFirst();
                Nodo condicionSalida = nodo.getHijos().getLast();

                //Etiqueta inicial L1
                contadorEtiqueta++;
                etiquetaInicio = "L"+contadorEtiqueta;
                contadorEtiqueta++;
                etiquetaCierre = "L"+contadorEtiqueta;
                instruccion = new InstruccionIntermedia("ETIQUETA INICIAL", "", "", etiquetaInicio);
                instrucciones.add(instruccion);

                //Condición del LOOP i < 10
                temporal = lecturaArbol(condicionSalida);

                //SALTO
                instruccion = new InstruccionIntermedia("JUMP IF FALSE", temporal, "", etiquetaCierre);
                instrucciones.add(instruccion);

                //Bloque de sentencias
                if(bloqueLoop != null)
                    lecturaArbol(bloqueLoop); //el bloque del LOOP

                //JUMP FINAL
                instruccion = new InstruccionIntermedia("JUMP", "", "", etiquetaInicio);
                instrucciones.add(instruccion);

                //Etiqueta de cierre
                instruccion = new InstruccionIntermedia("ETIQUETA FINAL", "", "", etiquetaCierre);
                instrucciones.add(instruccion);

                return temporal;

//IF
            case IF:
                casosDecide = lecturaArbol(nodo.getHijos().getFirst());

                Nodo ultimoHijo = nodo.getHijos().getLast();
                boolean tieneElse = ultimoHijo.getTipo() == TipoNodo.CASO_ELSE;

                //If simple (un solo caso, sin else)
                if (nodo.getHijos().size() == 1) {
                    lecturaArbol(nodo.getHijos().getFirst());
                    instruccion = new InstruccionIntermedia("ETIQUETA FINAL IF", "", "", "");
                    instrucciones.add(instruccion);
                    return "";
                }

                //If con varios casos
                lecturaArbol(nodo.getHijos().getFirst());

                //If con else
                if (tieneElse) {
                    instruccion = new InstruccionIntermedia("ELSE", "", "", "");
                    instrucciones.add(instruccion);
                    lecturaArbol(ultimoHijo);
                }

                instruccion = new InstruccionIntermedia("ETIQUETA FINAL IF", "", "", "");
                instrucciones.add(instruccion);
                return "";


            case CASO:

                String expresionRelacional = lecturaArbol(nodo.getHijos().getFirst());

                //ETIQUETA DE SIGUIENTE CASO
                String etiquetaCaso = generarEtiqueta();

                //JUMP
                instruccion = new InstruccionIntermedia("JUMP IF FALSE", expresionRelacional, "", etiquetaCaso);
                instrucciones.add(instruccion);

                //Recorrer el bloque
                lecturaArbol(nodo.getHijos().getLast());

                //JUMP al final del IF
                instruccion = new InstruccionIntermedia("IF END", "", "", etiquetaCaso);
                instrucciones.add(instruccion);

                //Inicio deL siguiente caso con una etiqueta
                instruccion = new InstruccionIntermedia("SIGUIENTE CASO", "", "", etiquetaCaso);
                instrucciones.add(instruccion);

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

            case ASIGNACION_GLOBAL:
                izquierda = lecturaArbol(nodo.getHijos().get(0));
                derecha = lecturaArbol(nodo.getHijos().get(1));
                temporal = generarTemporal();
                instruccion = new InstruccionIntermedia("ASIGNACIÓN GLOBAL", izquierda, derecha, temporal);
                instrucciones.add(instruccion);
                return temporal;

            case NEGACION:
                String expresion = lecturaArbol(nodo.getHijos().getFirst()); //obtener la expresión luego de la negación
                temporal = generarTemporal();
                instruccion = new InstruccionIntermedia("NEGACION", expresion, "", temporal);
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
                return "";

            case LLAMADA_FUNCION:
                //Procesar argumentos (ponerlos en la lista de parámetros)
                int contadorArgumentos = 0;
                Nodo listaArgs = nodo.getHijos().get(0);
                for (Nodo argumento : listaArgs.getHijos()) {
                    String tempArg = lecturaArbol(argumento);
                    instrucciones.add(new InstruccionIntermedia("ARGUMENTO", tempArg, "", ""));
                    contadorArgumentos++;
                }
                //Generar el CALL
                temporal = generarTemporal();
                instrucciones.add(new InstruccionIntermedia("CALL", ""+contadorArgumentos, temporal, nodo.getName()));
                return temporal;

            case RETURN:
                String valorRetorno = lecturaArbol(nodo.getHijos().getFirst());
                instrucciones.add(new InstruccionIntermedia("RETURN", valorRetorno, "", ""));
                return "";

            case MAIN:

                Nodo bloque = nodo.getHijos().getFirst();
                //_func_begin_main:
                instrucciones.add(new InstruccionIntermedia("MAIN", "", "", ""));
                lecturaArbol(bloque);
                instrucciones.add(new InstruccionIntermedia("FIN DE MAIN", "", "", ""));
                return "";

            case SHOW:
                expresion = lecturaArbol(nodo.getHijos().getFirst());
                instruccion = new InstruccionIntermedia("SHOW","", "", expresion);
                instrucciones.add(instruccion); //leer todos los parámetros de la función

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

            case ERROR:
                instruccion = new InstruccionIntermedia("ERROR", "", "", nodo.getName());
                instrucciones.add(instruccion);
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

    public String generarEtiqueta(){
        String temporal = "L"+contadorEtiqueta;
        contadorEtiqueta++;
        return temporal;
    }

    public List<InstruccionIntermedia> getInstrucciones() {
        return instrucciones;
    }
}
