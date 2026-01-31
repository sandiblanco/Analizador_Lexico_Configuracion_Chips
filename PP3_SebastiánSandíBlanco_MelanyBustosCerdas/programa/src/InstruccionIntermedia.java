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


     //Imprime la instrucción en formato de código intermedio
    public void imprimir(){
        System.out.println(resultado + " = " + argumento1 + operador + argumento2);
    }
}
