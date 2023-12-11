import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Menu {
    public static void main(String[] args){
        String negri = "\033[0;1m";
        String off = "\033[0m";
        Scanner teclado = new Scanner(System.in);
        System.out.println();
        System.out.println(negri+"\t\t\tAPLICACIÓN MOVILIDAD\n"+off);
        String col="";
        int dias;
        int viajes;
        ArrayList<String> memo = new ArrayList<>();
        while(!col.equals("0")){
            System.out.print("Introduzca el colectivo (0- Salir, 1-Sin descuento, 2-Jubilado, 3-Parado, 4-\n" +
                    "Discapacitado, 5-Estudiante): ");
            col = teclado.nextLine();
            switch (col){
                case "0":
                    Iterator<String> it = memo.iterator();
                    System.out.println();
                    while (it.hasNext()){
                        System.out.println(it.next());
                    }
                    System.out.println();
                    System.out.println(negri+"¡¡¡HEMOS TERMINADO EL DÍA!!!");
                    break;
                default:
                    System.out.print("Introduzca el número de días estimado: ");
                    dias = teclado.nextInt();
                    teclado.nextLine();
                    System.out.print("Introduzca el número de viajes: ");
                    viajes = teclado.nextInt();
                    teclado.nextLine();
                    System.out.println();
                    CalculadorMovilidad calc = new CalculadorMovilidad(dias,viajes,col);
                    ArrayList<Double> a = calc.calculaPreciosViaje();
                    System.out.println(a);
                    memo.add("colectivo: "+negri+calc.getColectivo()+" - "+off+
                            "días: "+negri+calc.getDiasEstimados()+" - "+off+
                            "viajes: "+negri+calc.getViajesPrevistos()+" - "
                            +calc.mejorOpcion(a)+off);
                    System.out.println(negri+calc.mejorOpcion(a)+off);
                    System.out.println();}



        }
    }
}