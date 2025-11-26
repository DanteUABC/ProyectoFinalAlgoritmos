
package grafoponderadodirigido;
import grafoponderadodirigido.Arista;
import java.util.ArrayList;
import java.util.HashMap;

public class Grafo {
    /**
     * Cada vertice se encuentra dentro del hashmap, Pero no todos los vertices
     * tienen aristas dirigidas hacia otros vertices.
     */
    private HashMap<String,ArrayList<Arista>> listaDeAdyacencia;

    public Grafo() {
        listaDeAdyacencia = new HashMap<>();
    }

    /**
     * Se agrega una arista entre dos vertices, si estos no existen se crean.
     * @param origen Vertice de donde se origina la arista.
     * @param destino Vertice donde se dirige.
     * @param costo El Costo que tiene la arista.
     * @param tipo El tipo de arista.
     */
    public void addArista(String origen, String destino,double costo,String tipo) {
        listaDeAdyacencia.putIfAbsent(origen, new ArrayList<>());
        listaDeAdyacencia.putIfAbsent(destino, new ArrayList<>());

        listaDeAdyacencia.get(origen).add(new Arista(destino,costo,tipo));
    }

    /**
     * Agrega un vertice sin ninguna arista.
     * @param nombre Nombre del vertice.
     */
    public void addVertice(String nombre) {
        listaDeAdyacencia.putIfAbsent(nombre, new ArrayList<>());
    }

    /**
     * Accede a la lista de adyacencia.
     * @return Lista de adyacencia.
     */
    public HashMap<String,ArrayList<Arista>> getListaDeAdyacencia() {
        return listaDeAdyacencia;
    }

    @Override
    public String toString(){
        String resultado = "";
        for(String origen : listaDeAdyacencia.keySet()){
            for (Arista a : listaDeAdyacencia.get(origen)) {
                resultado += origen + "," + a + "\n";
            }
        }
        return resultado;
    }

    /**
     * Muestra todos los vertices con sus aristas (se omiten las que no tienen aristas).
     * @return Cadena con la lista de adyacencia.
     */
    public String showListaDeAdyacencia(){
        String resultado = "";
        for(String origen : listaDeAdyacencia.keySet()){
            if(listaDeAdyacencia.get(origen).size()>0){
                resultado += origen + "->";
                for (Arista a : listaDeAdyacencia.get(origen)) {
                    resultado += a + "->";
                }
                resultado += "\n";
            }
        }
        return resultado;
    }

}