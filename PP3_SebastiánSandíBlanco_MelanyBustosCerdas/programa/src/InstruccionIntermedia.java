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

        switch (this.operador){

            //asignación
            case "=":
                System.out.println(argumento1 + " " + operador + " " + argumento2); //ejemplo x = t1
                return;

            //casos donde son operaciones aritméticas
            case "+":
            case "-":
            case "*":
            case "/":
            case "//":
            case "%":
            case "^":
                System.out.println(resultado + " = " + argumento1 + operador + argumento2);
                return;

            //cuando se recibe una función
            case "FUNCIÓN":
                System.out.println("func begin " + resultado);
                return;

            //Parámetro
            case "PARÁMETRO":
                System.out.println(resultado + " = " + "param[" + argumento1 + "]");
                return;


            //DECLARACIONES
            case "ENTERO":
                System.out.println("dataInt " + resultado + " default 0");
                return;

            case "FLOTANTE":
                System.out.println("dataFloat " + resultado + " default 0.0");
                return;

            case "CARACTER":
                System.out.println("dataChar " + resultado + " default '\\0'");
                return;

            case "BOOLEANO":
                System.out.println("dataBool " + resultado + " default true");
                return;

            case "STRING":
                System.out.println("dataString " + resultado + " default null");
                return;


        }
        System.out.println(resultado + " = " + argumento1 + operador + argumento2);
    }
}
