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

    public void imprimir(){
        System.out.println(resultado + " = " + argumento1 + operador + argumento2);
    }
}
