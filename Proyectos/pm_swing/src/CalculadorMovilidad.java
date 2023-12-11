import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class CalculadorMovilidad implements Precio {

        // Variables
        private int diasEstimados;
        private int viajesPrevistos;
        private String colectivo;

    // HASHMAP de precios
    //Pago por viaje (billete simple) -2.75€ / Viajes ilimitados durante 7 días - 33€ / Viajes ilimitados durante 30 días 127€
        private final HashMap<String,Double> precios = new HashMap<>(){{
            this.put("simple",2.75);
            this.put("7d", 33.0);
            this.put("30d", 127.0);
        }};

        //  Campo descuento que indica el porcentaje de descuento a aplicar
        HashMap<Integer, Double> descuentos = new HashMap<>(){{
            this.put(1,1.0);
            this.put(2,0.5);
            this.put(3, 0.4);
            this.put(4, 0.25);
            this.put(5,0.2);
        }};
        //Constructor
        public CalculadorMovilidad(int diasEstimados, int viajesPrevistos, String colectivo) {
            this.diasEstimados = diasEstimados;
            this.viajesPrevistos = viajesPrevistos;
            this.colectivo = colectivo;

        }

        // Getters
        public int getDiasEstimados() {return diasEstimados;}
        public int getViajesPrevistos() {return viajesPrevistos;}
        public String getColectivo() {return colectivo;}

        // Métodos



        //precioIlimitado7d()
        /*
        Descripción:  Devuelve un ArrayList con los precios con descuento para cada colectivo para un bono de 7 días
        Output:  ArrayList<Double>
         */
        public ArrayList<Double> precioIlimitado7d(){
            ArrayList<Double> precios_desc = new ArrayList<>();
            Double n_bonos;
            Double temp;
            Double precio_viaj;
            for (int i =1;i<=descuentos.size();i++){
                n_bonos = Math.ceil(getDiasEstimados()/7.0);
                temp = n_bonos*precios.get("7d");
                precio_viaj = temp/getViajesPrevistos()*descuentos.get(i);
                precios_desc.add(Math.round(precio_viaj*100.0)/100.0);
            }
            return precios_desc;
        }


        //precioIlimitado30d()
        /*
        Descripción: Devuelve un ArrayList con los precios con descuento para cada colectivo para un bono de 30 días
        Output:  ArrayList<Double>
         */


        public ArrayList<Double> precioIlimitado30d(){
            ArrayList<Double> precios_desc = new ArrayList<>();
            Double n_bonos;
            Double temp;
            Double precio_viaj;
            for (int i =1;i<=descuentos.size();i++){
                n_bonos = Math.ceil(getDiasEstimados()/30.0);
                temp = n_bonos*precios.get("30d");
                precio_viaj = temp/getViajesPrevistos()*descuentos.get(i);
                precios_desc.add(Math.round(precio_viaj*100.0)/100.0);
            }

            return precios_desc;
        }



        //precioSimple()
        /*
        Descripcion:  Devuelve un ArrayList con los precios con descuento para cada colectivo para un billete
        Output:  ArrayList<Double>
         */


        public ArrayList<Double>  precioSimple(){
            ArrayList<Double> precios_desc = new ArrayList<>();
            Double resu;
            for (int i =1;i<=descuentos.size();i++){
                resu = precios.get("simple")*descuentos.get(i);
                precios_desc.add(Math.round(resu*100.0)/100.0);
            }
            return precios_desc;
        }



        //calculaPreciosViaje()
        /*
        Descripcion: Llama a los metodos precioIlimitado7d() , precioIlimitado30d() y precioSimple()
                     devuelve un ArrayList de 15 valores de todos los precios de cada colectivo
        Output:  ArrayList<Double> [15]
         */

        public ArrayList<Double> calculaPreciosViaje(){
            ArrayList<Double> preciosCompl = new ArrayList<>();
            Iterator a = precioSimple().iterator();
            Iterator b = precioIlimitado7d().iterator();
            Iterator c = precioIlimitado30d().iterator();
            while(a.hasNext()){
                preciosCompl.add((Double) a.next());
            }
            while(b.hasNext()){
                preciosCompl.add((Double) b.next());
            }
            while(c.hasNext()){
                preciosCompl.add((Double) c.next());
            }
            return preciosCompl;
        }

        //mejorOpcion()
        /*
        Descripcion:  Recibe el ArrayList de 15 valores con todos lo precios por colectivo y calcula la mejor opción
                      de entre los 15 valores. Y devulve un String con la mejor opción por colectivo según el colectivo
                      elegido. Recomendado que bono interesa al usuario   según  el precio por viaje para el número de
                      viajes y días.
        Input:  ArrayList<Double> a
        Output:  String
         */
        public String mejorOpcion(ArrayList<Double> a){
            ArrayList<Double> sub = new ArrayList<>();
            String temp = "";
            int start=0;
            switch (getColectivo()){
                case "1":

                    break;
                case "2":
                   start=1;
                    break;
                case "3":
                    start=2;
                    break;
                case "4":
                     start=3;
                    break;
                case "5":
                     start=4;
                    break;
            }
            for (int j = start; j < a.size(); j += 5) {
                sub.add(a.get(j));
            }
            Double min = Collections.min(sub);
            String bono;
            int index = sub.indexOf(min);
            if (index==0){
                bono = "Billete suelto";
            } else if (index==1){
                bono = "Bono para 7 días";
            } else {
                bono = "Bono para 30 días";
            }

           switch (getColectivo()){

               case "1":
                   temp+="(Sin descuento)";
                   break;
               case "2":
                   temp+="(Jubilado)";
                   break;
               case "3":
                   temp+="(Parado)";
                   break;
               case "4":
                   temp+="(Discapacitado)";
                   break;
               case "5":
                   temp+="(Estudiante)";
                   break;
           }

            temp += " Debería escoger la opción de "+bono;
            temp += "\n("+min+"€/viaje)";
            return temp;
        }
}