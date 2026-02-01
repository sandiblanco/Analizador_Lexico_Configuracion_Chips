/**
 CURSO: Compiladores e Intérpretes
 PROYECTO #3
 ESTUDIANTES: Sebastián Sandí Blanco y Melany Bustos Cerdas
 ARCHIVO: InstruccionIntermedia.java

 Clase responsable de representar una instrucción intermedia en formato de cuádrupla o tripleta
 */
package src;

public class InstruccionIntermedia {

    String operador;
    String argumento1;
    String argumento2;
    String resultado;

    public InstruccionIntermedia(String op, String arg1, String arg2, String result){
        //Constructor inicial
        operador = op;
        argumento1 = arg1;
        argumento2 = arg2;
        resultado = result;
    }


     //Imprime la instrucción en formato de código intermedio dependiendo del tipo de nodo
    public void imprimir(){
        
        String lineaDeCodigo = "";

        switch (this.operador){

            //asignación
            case "=":
                lineaDeCodigo = argumento1 + " " + operador + " " + argumento2; //ejemplo x = t1
                break;

            //casos donde son operaciones aritméticas
            case "+":
            case "-":
            case "*":
            case "/":
            case "//":
            case "%":
            case "^":
            case ">":
            case "<":
            case "<=":
            case ">=":
            case "==":
            case "!=":
            case "AND":
            case "OR":
                lineaDeCodigo = resultado + " = " + argumento1 + operador + argumento2;
                break;

            //cuando se recibe una función
            case "FUNCIÓN":
                lineaDeCodigo = "func begin " + resultado;
                break;

            //La instrucción final de la función
            case "FIN DE FUNCIÓN":
                lineaDeCodigo = "func end";
                break;

            //Parámetro
            case "PARÁMETRO":
                lineaDeCodigo = resultado + " = " + "param[" + argumento1 + "]";
                break;

            //SENTENCIA FOR
            case "ETIQUETA INICIAL":
                lineaDeCodigo = resultado + ":";
                break;
            case "ETIQUETA FINAL":
                lineaDeCodigo = resultado + ":";
                break;
            case "JUMP":
                lineaDeCodigo = "goto " + resultado;
                break;
            case "JUMP IF FALSE":
                lineaDeCodigo = "ifFalse " + argumento1 + " goto " + resultado;
                break;


            //DECLARACIONES
            case "ENTERO":
                lineaDeCodigo = "dataInt " + resultado + " default 0";
                break;

            case "FLOTANTE":
                lineaDeCodigo = "dataFloat " + resultado + " default 0.0";
                break;

            case "CARACTER":
                lineaDeCodigo = "dataChar " + resultado + " default '\\0'";
                break;

            case "BOOLEANO":
                lineaDeCodigo = "dataBool " + resultado + " default true";
                break;

            case "STRING":
                lineaDeCodigo = "dataString " + resultado + " default null";
                break;

            case "NEGACION":
                lineaDeCodigo = resultado + " = !" + argumento1;
                break;


        }
        System.out.println(lineaDeCodigo);
    }
}
