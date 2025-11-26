package grafoponderadodirigido;

import java.io.BufferedReader;
import java.io.FileReader;

public class LectorDeGrafo {

    /**
     * Los archivos enviados contienen la informacion de las aristas.
     * El lector interpreta las aristas y agrega a un grafo.
     * @param file Direccion del archivo.
     * @param grafo El Grafo donde se almacenara.
     */
    public void loadGrafo(String file, Grafo grafo) {
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(file));
            reader.readLine();
            String line = "";
            while((line = reader.readLine()) != null){
                String[] data =  line.split(",");
                grafo.addArista(data[0], data[1], Double.parseDouble(data[2]), data[3]);
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
