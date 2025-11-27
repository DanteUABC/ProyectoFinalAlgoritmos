package grafoponderadodirigido;

import java.util.*;

public class Algoritmos {

    public ArrayList<String> dijkstra(HashMap<String, ArrayList<Arista>> grafo, String origen, String destino){
        PriorityQueue<NodoDistancia> queue = new PriorityQueue<>();
        HashMap<String, Double> dist = new HashMap<>();
        HashMap<String, String> padres = new HashMap<>();
        
        for (String key : grafo.keySet()) {
            dist.put(key, Double.POSITIVE_INFINITY);
            padres.put(key, null);
        }
        
        dist.put(origen, 0.0);
        queue.add(new NodoDistancia(origen, 0.0));

        while (!queue.isEmpty()){
            NodoDistancia u = queue.poll();

            if (u.getDistancia() > dist.get(u.getNombre())) {
                continue;
            }

            if(u.getNombre().equals(destino)){
                return reconstruirRuta(padres, origen, destino);
            }

            if (grafo.containsKey(u.getNombre())) {
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
        return new ArrayList<>();
    }

    private ArrayList<String> reconstruirRuta(HashMap<String,String> padres, String origen, String destino){
        ArrayList<String> ruta = new ArrayList<>();
        String actual = destino;
        while(actual != null){
            ruta.add(0, actual);
            if (actual.equals(origen)) break;
            actual = padres.get(actual);
        }
        return ruta;
    }

    public Grafo prim(HashMap<String, ArrayList<Arista>> grafo, String inicio){
        HashMap<String, Double> minCosto = new HashMap<>();
        HashMap<String, String> padres = new HashMap<>();
        PriorityQueue<NodoDistancia> queue = new PriorityQueue<>();
        HashSet<String> visitados = new HashSet<>();

        for(String u: grafo.keySet()){
            minCosto.put(u, Double.POSITIVE_INFINITY);
            padres.put(u, null);
        }

        if(!minCosto.containsKey(inicio)){
            return new Grafo();
        }

        minCosto.put(inicio, 0.0);
        queue.add(new NodoDistancia(inicio, 0.0));

        while(!queue.isEmpty()){
            NodoDistancia u = queue.poll();
            String uNombre = u.getNombre();

            if (visitados.contains(uNombre)) continue;
            visitados.add(uNombre);

            if(grafo.containsKey(uNombre)){
                for(Arista a: grafo.get(uNombre)){
                    String v = a.getDestino();
                    double costoArista = a.getCosto();

                    if (!visitados.contains(v) && costoArista < minCosto.get(v)){
                        minCosto.put(v, costoArista);
                        padres.put(v, uNombre);
                        queue.add(new NodoDistancia(v, costoArista));
                    }
                }
            }
        }
        return construirGrafo(grafo, padres);
    }

    private Grafo construirGrafo(HashMap<String, ArrayList<Arista>> grafoOriginal, HashMap<String,String> padres){
        Grafo grafoMST = new Grafo();
        
        for(String u: grafoOriginal.keySet()){
            grafoMST.addVertice(u);
        }

        for(String hijo : padres.keySet()){
            String padre = padres.get(hijo);
            
            if(padre != null && grafoOriginal.containsKey(padre)){
                for(Arista a : grafoOriginal.get(padre)){
                    if(a.getDestino().equals(hijo)){
                        grafoMST.addArista(padre, hijo, a.getCosto(), a.getTipo());
                        break;
                    }
                }
            }
        }
        return grafoMST;
    }

    public HashMap<String,HashMap<String,Double>> floydWarshall(HashMap<String, ArrayList<Arista>> grafo){
        HashMap<String,Integer> verticesIndex = new HashMap<>();
        ArrayList<String> vertices = new ArrayList<>();
        int index = 0;
        
        for(String u: grafo.keySet()){
            if(!verticesIndex.containsKey(u)){
                vertices.add(u);
                verticesIndex.put(u, index++);
            }
        }
        
        int n = vertices.size();
        double[][] dist = new double[n][n];

        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(i == j) dist[i][j] = 0.0;
                else dist[i][j] = Double.POSITIVE_INFINITY;
            }
        }

        for(int i = 0; i < n; i++){
            String u = vertices.get(i);
            if(grafo.containsKey(u)){
                for(Arista a: grafo.get(u)){
                    String v = a.getDestino();
                    if (verticesIndex.containsKey(v)) {
                        int j = verticesIndex.get(v);
                        // Floyd-Warshall usa la arista más corta si hay múltiples
                        dist[i][j] = Math.min(dist[i][j], a.getCosto());
                    }
                }
            }
        }
        
        for(int k = 0; k < n; k++){
            for(int i = 0; i < n; i++){
                for(int j = 0; j < n; j++){
                    if(dist[i][k] != Double.POSITIVE_INFINITY && dist[k][j] != Double.POSITIVE_INFINITY){
                        if(dist[i][j] > dist[i][k] + dist[k][j]){
                            dist[i][j] = dist[i][k] + dist[k][j];
                        }
                    }
                }
            }
        }

        HashMap<String,HashMap<String,Double>> matriz = new HashMap<>();
        for(int i = 0; i < n; i++){
            String origen = vertices.get(i);
            HashMap<String,Double> distancias = new HashMap<>();
            for(int j = 0; j < n; j++){
                String destino = vertices.get(j);
                distancias.put(destino, dist[i][j]);
            }
            matriz.put(origen, distancias);
        }
        return matriz;
    }

    public class NodoDistancia implements Comparable<NodoDistancia>{
        private String nombre;
        private double distancia;
        public NodoDistancia(String nombre, double distancia){
            this.nombre = nombre;
            this.distancia = distancia;
        }
        public String getNombre() { return nombre; }
        public double getDistancia() { return distancia; }
        
        @Override
        public int compareTo(NodoDistancia o) {
            return Double.compare(this.distancia, o.distancia);
        }
    }
}
