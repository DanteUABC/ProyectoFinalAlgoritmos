package grafoponderadodirigido;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class LectorDeArbol {

    /**
     * Lee un archivo y inserta los elementos dentro del arbol parametrizado.
     * @param file Dirección del archivo que se esta leyendo.
     * @param arbol Arbol donde se insertan los elementos.
     */
    public void loadArbol(String file, ArbolBalanceado<Data> arbol) {
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(file));
            reader.readLine();
            String line = "";
            while((line = reader.readLine()) != null){
                String[] datos =  line.split(",");
                ArrayList<String> values = new ArrayList<>();
                for(int i = 1; i < datos.length; i++)values.add(datos[i]);
                arbol.insertar(new Data(Integer.parseInt(datos[0]),values));
            }
        }catch(Exception e){
            System.out.println(e);
        }finally{
            if(reader != null){
                try {
                    reader.close();
                }catch(Exception e){
                    System.out.println(e);
                }
            }
        }
    }
}
