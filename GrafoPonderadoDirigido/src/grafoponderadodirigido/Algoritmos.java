package grafoponderadodirigido;

import java.util.*;

public class Algoritmos {


    public ArrayList<String> dijkstra(HashMap<String, ArrayList<Arista>> grafo, String origen, String destino){
        /**
         * Se inician los valores necesarios. Las distancias son infinitas.
         */
        PriorityQueue<NodoDistancia> queue = new PriorityQueue<>();
        HashMap<String, Double> dist = new HashMap<>();
        HashMap<String, String> padres = new HashMap<>();
        for (String key : grafo.keySet()) {
            dist.put(key, Double.POSITIVE_INFINITY);
            padres.put(key, null);
        }
        /**
         * La distancia del origen es 0.
         */
        dist.put(origen, 0.0);
        queue.add(new NodoDistancia(origen, 0));
        /**
         * Se visitan de una a una las aristas que se van insertando
         * dentro de la cola de prioridad hasta encontrar el vertice de destino.
         */
        while (!queue.isEmpty()){
            NodoDistancia u = queue.poll();

            if(u.getNombre().equals(destino)){
                /**
                 * Se regresa la lista necesaria para recrear la ruta.
                 */
                return reconstruirRuta(padres,origen,destino);
            }else{
                for(Arista a: grafo.get(u.getNombre())){
                    double nuevaDistancia = u.getDistancia() + a.getCosto();
                    if (dist.get(a.getDestino()) > nuevaDistancia){
                        dist.put(a.getDestino(), nuevaDistancia);
                        padres.put(a.getDestino(), u.getNombre());

                        queue.add(new NodoDistancia(a.getDestino(), nuevaDistancia));
                    }
                }
            }
        }
        /**
         * Si no se encuentra se regresa una lista vacia.
         */
        return new ArrayList<>();
    }

    /**
     * Reconstruye la lista de vertices visitados en base a los padres, el origen y destino.
     * @param padres Los padres de los vertices visitados.
     * @param origen El inicio de la ruta.
     * @param destino El final de la ruta.
     * @return La ruta en si.
     */
    private ArrayList<String> reconstruirRuta(HashMap<String,String> padres, String origen, String destino){
        ArrayList<String> ruta = new ArrayList<>();
        String actual = destino;
        while(actual != null){
            ruta.addFirst(actual);
            actual = padres.get(actual);
        }
        return ruta;
    }

    /**
     * Clase que contiene las distancias recorridas en nodos.
     */
    public class NodoDistancia implements Comparable<NodoDistancia>{
        private String nombre;
        private double distancia;
        public NodoDistancia(String nombre,double distancia){
            this.nombre=nombre;
            this.distancia=distancia;
        }
        public String getNombre() {
            return nombre;
        }
        public double getDistancia() {
            return distancia;
        }
        @Override
        public int compareTo(NodoDistancia o) {
            return Double.compare(distancia,o.distancia);
        }
    }

    //METODO DE PRIM

    /**
     * Metodo de prim regresa el arbol minimo de expansion dentro de un grafo, es decir,
     * regresar el grafo sin ciclos y con las aristas de menor costo.
     * @param grafo Grafo original.
     * @param inicio De donde se empezara a construir el arbol.
     * @return Arbol de expansion minima.
     */
    public Grafo prim(HashMap<String, ArrayList<Arista>> grafo, String inicio){
        /**
         * Valores necesarios para el metodo.
         */
        HashMap<String, Double> vertice = new HashMap<>();
        HashMap<String, String> padres = new HashMap<>();
        PriorityQueue<NodoDistancia> queue = new PriorityQueue<>();
        for(String u: grafo.keySet()){
            vertice.put(u, Double.POSITIVE_INFINITY);
            padres.put(u, null);
            queue.add(new NodoDistancia(u, Double.POSITIVE_INFINITY));
        }

        /**
         * Retorna el grafo vacio si el vertice de inicio no existe.
         */
        if(!vertice.containsKey(inicio)){
            return new Grafo();
        }

        /**
         * El vertice de inicio tiene una distancia de 0.
         */
        vertice.put(inicio, 0.0);
        queue.remove(new NodoDistancia(inicio,Double.POSITIVE_INFINITY));
        queue.add(new NodoDistancia(inicio,0.0));

        /**
         * Se visitan todas las aristas, por lo que se busca el camino mas corto entre estas.
         */
        while(!queue.isEmpty()){
            NodoDistancia u = queue.poll();
            if(grafo.containsKey(u.getNombre())){
                for(Arista a: grafo.get(u.getNombre())){
                    if(vertice.containsKey(a.getDestino()) && a.getCosto() < vertice.get(a.getDestino())){
                        padres.put(a.getDestino(), u.getNombre());
                        vertice.put(a.getDestino(), a.getCosto());

                        queue.remove(new NodoDistancia(a.getDestino(), u.getDistancia()));
                        queue.add(new NodoDistancia(a.getDestino(), a.getCosto()));
                    }
                }
            }
        }
        /**
         * Se construye el grafo.
         */
        return construirGrafo(grafo,padres);
    }

    /**
     * Se reconstruye el grafo en base al grafo original y los padres de los vertices.
     * @param grafoOriginal Grafo original.
     * @param padres Padres de los vertices visitados
     * @return Arbol de expansion minima
     */
    private Grafo construirGrafo(HashMap<String, ArrayList<Arista>> grafoOriginal, HashMap<String,String> padres){
        Grafo grafo = new Grafo();
        for(String u: grafoOriginal.keySet()){
            grafo.addVertice(u);
        }
        for(String u: padres.keySet()){
            String v = padres.get(u);
            if(v != null && grafoOriginal.containsKey(v)){
                for(Arista a: grafoOriginal.get(v)){
                    if(a.getDestino().equals(u)){
                        grafo.addArista(v,a.getDestino(),a.getCosto(),a.getTipo());
                        break;
                    }
                }
            }
        }

        return grafo;
    }

    //METODO DE FLOYD-WARSHALL

    /**
     * Genera una matriz en forma de Hashmap, cuyo hashmap contiene tanto el nombre del vertice, junto con
     * todas las distancias/costos necesarios para llegar a otros vertices (De no haber ninguna, el costo sera
     * infinito)
     * @param grafo Grafo de donde se obtendra la matriz.
     * @return La matriz de distancias necesarias.
     */
    public HashMap<String,HashMap<String,Double>> floydWarshall(HashMap<String, ArrayList<Arista>> grafo){
        /**
         * Valores iniciales iniciados.
         */
        HashMap<String,Integer> verticesIndex = new HashMap<>();
        ArrayList<String> vertices = new ArrayList<>();
        int index = 0;
        for(String u: grafo.keySet()){
            if(!verticesIndex.containsKey(u))vertices.add(u);
            verticesIndex.putIfAbsent(u, index++);
        }
        double[][] dist = new double[vertices.size()][vertices.size()];

        /**
         * Por cada vertice, se verifican todas las aristas del grafo hasta encontrar
         * la ruta mas corta.
         * Si el destino es igual al origen, el resultado es 0, si por el contrario,
         * no hay ninguna ruta posible, sera infinito.
         */
        for(int i = 0; i < vertices.size(); i++){
            for(int j = 0; j < vertices.size(); j++){
                String u = vertices.get(i);
                String v = vertices.get(j);
                if(i == j) dist[i][j] = 0.0;
                else dist[i][j] = Double.POSITIVE_INFINITY;
                if(grafo.containsKey(u)){
                    for(Arista a: grafo.get(u)){
                        if(a.getDestino().equals(v)){
                            dist[i][j] = a.getCosto();
                            break;
                        }
                    }
                }
            }
        }

        /**
         * Se calculan las distancias finales.
         */
        for(int k = 0; k < vertices.size(); k++){
            for(int i = 0; i < vertices.size(); i++){
                for(int j = 0; j < vertices.size(); j++){
                    if(dist[i][k] != Double.POSITIVE_INFINITY && dist[k][j] != Double.POSITIVE_INFINITY){
                        if(dist[i][j] > dist[i][k] + dist[k][j]){
                            dist[i][j] = dist[i][k] + dist[k][j];
                        }
                    }
                }
            }
        }

        /**
         * Se recrea la matriz en forma de un hashmap que contiene un hashmap dentro,
         * que contiene la informacion de los vertices y su distancia minima.
         */
        HashMap<String,HashMap<String,Double>> matriz = new HashMap<>();
        for(int i = 0; i < vertices.size(); i++){
            String u = vertices.get(i);
            HashMap<String,Double> distancias = new HashMap();
            for(int j = 0; j < vertices.size(); j++){
                String destino = vertices.get(j);
                double distancia = dist[i][j];
                distancias.put(destino, distancia);
            }
            matriz.put(u,distancias);
        }
        return matriz;

    }
}