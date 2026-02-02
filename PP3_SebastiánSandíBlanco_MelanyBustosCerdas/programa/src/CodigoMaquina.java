///**
// CURSO: Compiladores e Intérpretes
// PROYECTO #3
// ESTUDIANTES: Sebastián Sandí Blanco y Melany Bustos Cerdas
// ARCHIVO: CodigoMaquina.java
//
// Clase responsable de convertir código intermedio a código máquina (MIPS)
// */
//
//import src.InstruccionIntermedia;
//
//import java.util.List;
//
//public class CodigoMaquina
//{
//    private List<InstruccionIntermedia> listaCuadruplas;
//
//    public CodigoMaquina(List<InstruccionIntermedia> listaInstrucciones){
//        listaCuadruplas = listaInstrucciones;
//    }
//
//    public void convertirInstruccionesACodigoMaquina(){
//        String operador, arg1, arg2, resultado;
//        for (InstruccionIntermedia instruccion : listaCuadruplas) {
//
//            operador = instruccion.getOperador();
//            switch (operador) {
//                case "+":
//                    escribirMIPS("lw $t0, " + buscarOffset(c.arg1));
//                    escribirMIPS("lw $t1, " + buscarOffset(c.arg2));
//                    escribirMIPS("add $t2, $t0, $t1");
//                    escribirMIPS("sw $t2, " + buscarOffset(c.res));
//                    break;
//
//                case "GOTO":
//                    escribirMIPS("j " + c.res); // res contiene el nombre de la etiqueta
//                    break;
//
//                case "IF_FALSE":
//                    escribirMIPS("lw $t0, " + buscarOffset(c.arg1));
//                    escribirMIPS("beq $t0, $zero, " + c.res);
//                    break;
//
//                case "LABEL":
//                    escribirMIPS(c.arg1 + ":");
//                    break;
//            }
//        }
//    }
//}
