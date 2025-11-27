package grafoponderadodirigido;

import grafoponderadodirigido.ArbolBalanceado;
import grafoponderadodirigido.Grafo;
import grafoponderadodirigido.Arista;
import grafoponderadodirigido.LectorDeGrafo; 
import grafoponderadodirigido.LectorDeArbol; 
import grafoponderadodirigido.Data;   
import grafoponderadodirigido.Algoritmos;      

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*; 
import javafx.scene.paint.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class ProyectoFinalController {

    @FXML private Canvas canvas;
    @FXML private ChoiceBox<String> choiceProblemaResolver;
    @FXML private TableView<List<String>> tablaDatos;
    @FXML private TextField textRuta1;
    @FXML private TextField textRuta2;
    @FXML private Label tituloTabla;
    @FXML private Button realizarBoton; 

    private GraphicsContext gc;

    private Grafo grafoLogistica;
    private ArbolBalanceado<Data> arbolInventario; 
    private Algoritmos misAlgoritmos; 

    private Map<String, double[]> coordenadasNodos;
    private List<String> headersGrafo;
    private List<String> headersInventario;
    
    private Queue<String> colaDatosInventario;
    private List<Data> historialInsercion;

    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        coordenadasNodos = new HashMap<>();
        headersGrafo = new ArrayList<>();
        headersInventario = new ArrayList<>();
        colaDatosInventario = new LinkedList<>(); 
        historialInsercion = new ArrayList<>();
        misAlgoritmos = new Algoritmos(); 

        choiceProblemaResolver.getItems().addAll(
            "1. Ruta más corta",
            "2. Conectividad total",
            "3. Matriz de costos mínimos",
            "4. Insertar",
            "5. Consulta",
            "6. Recorrido en inorden",
            "7. Recorrido en postorden",
            "8. Recorrido en preorden"
        );

        // 2. Acción del BOTÓN
        realizarBoton.setOnAction(event -> {
            String opcionSeleccionada = choiceProblemaResolver.getValue();
            if (opcionSeleccionada != null) {
                ejecutarLogica(opcionSeleccionada);
            } else {
                mostrarMensaje("Por favor, seleccione un problema a resolver.");
            }
        });
        
        tituloTabla.setText("Tabla de datos");
    }

    private void ejecutarLogica(String opcion) {
        if (opcion == null) return;
        
        limpiarCanvas();
        tituloTabla.setText(opcion);

        String rutaGrafo = textRuta1.getText().isEmpty() ? "rutas.csv" : textRuta1.getText();
        String rutaInv = textRuta2.getText().isEmpty() ? "inventario.csv" : textRuta2.getText();

        try {
            char seleccion = opcion.charAt(0);
            
            if (seleccion == '1' || seleccion == '2' || seleccion == '3') {
                cargarGrafoUsandoLector(rutaGrafo);
                
                if (grafoLogistica != null && !grafoLogistica.getListaDeAdyacencia().isEmpty()) {
                    
                    if (seleccion == '1') {
                        mostrarGrafoEnTablaDinamica(this.grafoLogistica); 
                        realizarDijkstraInteractivo();
                    } 
                    else if (seleccion == '2') {
                        realizarFloydWarshallInteractivo();
                    }
                    else if (seleccion == '3') {
                        realizarPrimInteractivo();
                    }
                }
            }
            
            else if (seleccion == '4' || seleccion == '5' || seleccion == '6' || seleccion == '7' || seleccion == '8') {
                
                if (arbolInventario == null) {
                    arbolInventario = new ArbolBalanceado<>();
                    cargarDatosInventarioEnCola(rutaInv);
                }

                List<Data> datosParaTabla = new ArrayList<>();

                if (seleccion == '4') {
                    if (!colaDatosInventario.isEmpty()) {
                        insertarSiguienteDato();
                    } else {
                        if (!arbolInventario.mostrarInorden().isEmpty()) {
                            mostrarMensaje("Todos los datos del CSV ya han sido insertados.");
                        } else {
                            mostrarMensaje("No hay datos para cargar.");
                        }
                    }
                    datosParaTabla = new ArrayList<>(historialInsercion);
                } 
                else if (arbolInventario != null) {
                    switch (seleccion) {
                        case '7': 
                            datosParaTabla = arbolInventario.mostrarPostorden(); 
                            break;
                        case '8': 
                            datosParaTabla = arbolInventario.mostrarPreorden(); 
                            break;
                        default: 
                            datosParaTabla = arbolInventario.mostrarInorden(); 
                            break;
                    }
                }

                mostrarInventarioEnTablaDinamica(datosParaTabla); 
                
                if (arbolInventario != null) {
                    dibujarArbolGenerico(arbolInventario.mostrarInorden(), null);
                }

                if (seleccion == '5') {
                     realizarBusquedaPorID();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error crítico: " + e.getMessage());
        }
    }

    private void cargarDatosInventarioEnCola(String ruta) {
        headersInventario = new ArrayList<>();
        colaDatosInventario.clear();
        historialInsercion.clear();
        
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea = br.readLine();
            if (linea != null) {
                String[] partes = linea.split(",");
                for (String p : partes) headersInventario.add(p.trim());
            }
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    colaDatosInventario.add(linea);
                }
            }
        } catch (Exception e) {
            mostrarMensaje("No se pudo leer el archivo de inventario: " + ruta);
        }
    }

    private void insertarSiguienteDato() {
        String linea = colaDatosInventario.poll();
        if (linea != null) {
            procesarEInsertarLinea(linea);
        }
    }
    
    private void procesarEInsertarLinea(String linea) {
        try {
            String[] datos = linea.split(",");
            ArrayList<String> values = new ArrayList<>();
            for(int i = 1; i < datos.length; i++) values.add(datos[i].trim());
            
            int id = Integer.parseInt(datos[0].trim());
            
            Data nuevoDato = new Data(id, values);
            
            arbolInventario.insertar(nuevoDato);
            
            historialInsercion.add(nuevoDato);
            
        } catch (Exception e) {
            System.out.println("Error al procesar línea: " + linea);
        }
    }

    private void realizarFloydWarshallInteractivo() {
        try {
            HashMap<String, HashMap<String, Double>> matriz = misAlgoritmos.floydWarshall(grafoLogistica.getListaDeAdyacencia());
            mostrarMatrizEnTabla(matriz);
            dibujarGrafoComoArbol(null, null);
        } catch (Exception e) {
            mostrarMensaje("Error al calcular: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarMatrizEnTabla(HashMap<String, HashMap<String, Double>> matriz) {
        tablaDatos.getColumns().clear();
        tablaDatos.getItems().clear();

        if (matriz == null || matriz.isEmpty()) return;

        List<String> nodos = new ArrayList<>(matriz.keySet());
        Collections.sort(nodos);

        crearColumna("Origen \\ Destino", 0);

        for (int i = 0; i < nodos.size(); i++) {
            crearColumna(nodos.get(i), i + 1); 
        }

        for (String origen : nodos) {
            List<String> fila = new ArrayList<>();
            fila.add(origen); 

            HashMap<String, Double> distanciasDesdeOrigen = matriz.get(origen);
            
            for (String destino : nodos) {
                Double costo = distanciasDesdeOrigen.get(destino);
                
                if (costo == Double.POSITIVE_INFINITY) {
                    fila.add("Inf");
                } else {
                    if (costo % 1 == 0) {
                        fila.add(String.valueOf(costo.intValue()));
                    } else {
                        fila.add(String.format("%.1f", costo));
                    }
                }
            }
            tablaDatos.getItems().add(fila);
        }
    }


    private void realizarPrimInteractivo() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Prim");
        dialog.setHeaderText("Conexiones mínimas");
        dialog.setContentText("Introduzca el nodo de inicio:");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;

        String inicio = result.get().trim();

        if (!grafoLogistica.getListaDeAdyacencia().containsKey(inicio)) {
            mostrarMensaje("Error: El nodo " + inicio + " no existe en la red.");
            dibujarGrafoComoArbol(null, null);
            return;
        }

        try {
            Grafo mst = misAlgoritmos.prim(grafoLogistica.getListaDeAdyacencia(), inicio);

            if (mst.getListaDeAdyacencia().isEmpty()) {
                mostrarMensaje("No se pudo generar el árbol de expansión.");
            } else {
                mostrarGrafoEnTablaDinamica(mst);
                dibujarGrafoComoArbol(null, inicio);
            }

        } catch (Exception e) {
            mostrarMensaje("Error al calcular: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void realizarDijkstraInteractivo() {
        TextInputDialog dialogOrig = new TextInputDialog();
        dialogOrig.setTitle("Camino más corto");
        dialogOrig.setHeaderText("Origen");
        dialogOrig.setContentText("Introduzca el nombre del origen:");
        
        Optional<String> resOrig = dialogOrig.showAndWait();
        if (!resOrig.isPresent()) return; 
        
        String origen = resOrig.get().trim();

        TextInputDialog dialogDest = new TextInputDialog();
        dialogDest.setTitle("Camino más corto");
        dialogDest.setHeaderText("Destino");
        dialogDest.setContentText("Introduzca el nombre del destino:");

        Optional<String> resDest = dialogDest.showAndWait();
        if (!resDest.isPresent()) return;

        String destino = resDest.get().trim();

        try {
            if (!grafoLogistica.getListaDeAdyacencia().containsKey(origen) || 
                !grafoLogistica.getListaDeAdyacencia().containsKey(destino)) {
                mostrarMensaje("Error: Uno de los nodos no existe en el grafo.");
                dibujarGrafoComoArbol(null, null);
                return;
            }

            ArrayList<String> ruta = misAlgoritmos.dijkstra(grafoLogistica.getListaDeAdyacencia(), origen, destino);

            if (ruta.isEmpty()) {
                mostrarMensaje("No se puede llegar al destino desde este nodo.");
                dibujarGrafoComoArbol(null, null);
            } else {
                mostrarMensaje("Ruta más corta: " + ruta.toString());
                dibujarGrafoComoArbol(ruta, origen);
            }

        } catch (Exception e) {
            mostrarMensaje("Error al calcular: " + e.getMessage());
        }
    }

    private void realizarBusquedaPorID() {
        if (arbolInventario == null) {
            mostrarMensaje("El inventario está vacío. Cargue datos primero.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Búsqueda de Inventario");
        dialog.setHeaderText("Consulta");
        dialog.setContentText("Intrduzca el ID:");

        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(idString -> {
            try {
                int idBusqueda = Integer.parseInt(idString.trim());
                Data key = new Data(idBusqueda, new ArrayList<>());
                Data encontrado = arbolInventario.buscar(key);
                
                if (encontrado != null) {
                    mostrarResultadoUnicoEnTabla(encontrado);
                    tituloTabla.setText("Producto encontrado.");
                } else {
                    mostrarMensaje("No se encontró ningún producto.");
                }
            } catch (Exception e) {
                mostrarMensaje("Error al buscar: " + e.getMessage());
            }
        });
    }

    private void mostrarResultadoUnicoEnTabla(Data d) {
        tablaDatos.getItems().clear(); 
        List<String> fila = new ArrayList<>();
        List<String> valoresExtraidos = parsearDataDinamico(d);
        for (String val : valoresExtraidos) fila.add(val);
        while (fila.size() < tablaDatos.getColumns().size()) fila.add("");
        tablaDatos.getItems().add(fila);
    }

    private void cargarGrafoUsandoLector(String ruta) {
        grafoLogistica = new Grafo();
        headersGrafo = leerEncabezadosCSV(ruta);
        
        LectorDeGrafo lector = new LectorDeGrafo();
        lector.loadGrafo(ruta, grafoLogistica);

        if (grafoLogistica.getListaDeAdyacencia().isEmpty()) {
            mostrarMensaje("El grafo está vacío o no se encontró el archivo: " + ruta);
            return;
        }
    }
    
    private List<String> leerEncabezadosCSV(String ruta) {
        List<String> headers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea = br.readLine();
            if (linea != null) {
                String[] partes = linea.split(",");
                for (String p : partes) headers.add(p.trim());
            }
        } catch (Exception e) {
            System.out.println("No se pudieron leer encabezados de: " + ruta);
        }
        return headers;
    }

    private void mostrarGrafoEnTablaDinamica(Grafo grafoAMostrar) {
        if (grafoAMostrar == null) return;
        tablaDatos.getColumns().clear();
        tablaDatos.getItems().clear();

        if (headersGrafo.isEmpty()) {
            headersGrafo.addAll(Arrays.asList("Origen", "Destino", "Costo", "Tipo"));
        }
        
        for (int i = 0; i < headersGrafo.size(); i++) {
            crearColumna(headersGrafo.get(i), i);
        }

        HashMap<String, ArrayList<Arista>> adj = grafoAMostrar.getListaDeAdyacencia();
        
        for (Map.Entry<String, ArrayList<Arista>> entry : adj.entrySet()) {
            String origen = entry.getKey();
            ArrayList<Arista> aristas = entry.getValue();

            for (Arista arista : aristas) {
                List<String> fila = new ArrayList<>();
                fila.add(origen);
                DatosArista datos = extraerDatosArista(arista);
                fila.add(datos.destino);
                fila.add(String.valueOf(datos.costo));
                fila.add(datos.tipo);
                while (fila.size() < headersGrafo.size()) fila.add("-");
                tablaDatos.getItems().add(fila);
            }
        }
    }

    private void mostrarInventarioEnTablaDinamica(List<Data> datos) {
        tablaDatos.getColumns().clear();
        tablaDatos.getItems().clear();

        if (headersInventario.isEmpty()) {
            headersInventario.addAll(Arrays.asList("ID", "Detalles"));
        }

        for (int i = 0; i < headersInventario.size(); i++) {
            crearColumna(headersInventario.get(i), i);
        }
        
        for (Data d : datos) {
            List<String> fila = new ArrayList<>();
            List<String> valoresExtraidos = parsearDataDinamico(d);
            for (String val : valoresExtraidos) fila.add(val);
            while (fila.size() < headersInventario.size()) fila.add("");
            tablaDatos.getItems().add(fila);
        }
    }

    private List<String> parsearDataDinamico(Data d) {
        List<String> resultados = new ArrayList<>();
        String raw = d.toString(); 
        String limpio = raw.replace("[", "").replace("]", "");
        String[] tokens = limpio.split(",");
        for (String t : tokens) resultados.add(t.trim());
        return resultados;
    }

    private void crearColumna(String titulo, int indiceLista) {
        TableColumn<List<String>, String> col = new TableColumn<>(titulo);
        col.setCellValueFactory(data -> {
            if (indiceLista < data.getValue().size()) {
                return new SimpleStringProperty(data.getValue().get(indiceLista));
            }
            return new SimpleStringProperty("");
        });
        tablaDatos.getColumns().add(col);
    }

    private static class DatosArista {
        String destino; double costo; String tipo;
        DatosArista(String d, double c, String t) { this.destino = d; this.costo = c; this.tipo = t; }
    }

    private DatosArista extraerDatosArista(Arista a) {
        try {
            String d = (String) a.getClass().getMethod("getDestino").invoke(a);
            double c = 0;
            try { c = (double) a.getClass().getMethod("getCosto").invoke(a); } 
            catch (Exception ex) { c = (double) a.getClass().getMethod("getPeso").invoke(a); }
            String t = (String) a.getClass().getMethod("getTipo").invoke(a);
            return new DatosArista(d, c, t);
        } catch (Exception e) {
            String s = a.toString();
            String[] parts = s.split(",");
            if (parts.length >= 3) {
                try { return new DatosArista(parts[0].trim(), Double.parseDouble(parts[1].trim()), parts[2].trim()); } 
                catch (NumberFormatException nfe) { return new DatosArista(parts[0], 0.0, "Error"); }
            }
            return new DatosArista(s, 0.0, "N/A");
        }
    }
    
    private String extraerIdDeData(Object data) {
        String s = data.toString();
        if (s.contains(",")) return s.split(",")[0].trim();
        return (s.length() > 5) ? s.substring(0, 5) : s;
    }

    private void dibujarGrafoComoArbol(List<String> rutaResaltada, String nodoRaiz) {
        if (grafoLogistica == null) return;
        
        Set<String> nodosSet = grafoLogistica.getListaDeAdyacencia().keySet();
        List<String> listaNodos = new ArrayList<>(nodosSet);
        
        if (nodoRaiz != null && listaNodos.contains(nodoRaiz)) {
            listaNodos.remove(nodoRaiz);
            Collections.sort(listaNodos); 
            int mid = listaNodos.size() / 2;
            listaNodos.add(mid, nodoRaiz);
        } else {
            Collections.sort(listaNodos);
        }

        dibujarArbolGenerico(listaNodos, rutaResaltada);
    }

    private void dibujarArbolGenerico(List<?> datos, List<String> rutaResaltada) {
        if (datos == null || datos.isEmpty()) return;
        dibujarRamaRecursiva(datos, 0, datos.size() - 1, canvas.getWidth() / 2, 40, canvas.getWidth() / 4, rutaResaltada);
    }

    private void dibujarRamaRecursiva(List<?> datos, int inicio, int fin, double x, double y, double offsetH, List<String> rutaResaltada) {
        if (inicio > fin) return;

        int medio = (inicio + fin) / 2;
        String textoNodo = extraerIdDeData(datos.get(medio));

        boolean resaltar = false;
        if (rutaResaltada != null) {
            for (String n : rutaResaltada) {
                if (n.trim().equalsIgnoreCase(textoNodo.trim())) {
                    resaltar = true;
                    break;
                }
            }
        }

        Color colorBorde = resaltar ? Color.RED : Color.WHITE;
        
        dibujarNodoVisual(textoNodo, x, y, Color.FORESTGREEN, colorBorde);

        double nextY = y + 60; 
        if (inicio <= medio - 1) {
            double nextX = x - offsetH;
            gc.setStroke(Color.GRAY);
            gc.strokeLine(x, y + 15, nextX, nextY - 15);
            dibujarRamaRecursiva(datos, inicio, medio - 1, nextX, nextY, offsetH / 2, rutaResaltada);
        }
        if (medio + 1 <= fin) {
            double nextX = x + offsetH;
            gc.setStroke(Color.GRAY);
            gc.strokeLine(x, y + 15, nextX, nextY - 15);
            dibujarRamaRecursiva(datos, medio + 1, fin, nextX, nextY, offsetH / 2, rutaResaltada);
        }
    }

    private void dibujarNodoVisual(String texto, double x, double y, Color colorFondo, Color colorBorde) {
        gc.setFill(colorFondo);
        gc.fillOval(x - 15, y - 15, 30, 30);
        
        gc.setStroke(colorBorde);
        if (colorBorde == Color.RED) {
            gc.setLineWidth(3.0);
        } else {
            gc.setLineWidth(1.0);
        }
        
        gc.strokeOval(x - 15, y - 15, 30, 30);
        gc.setLineWidth(1.0); 
        
        gc.setFill(Color.BLACK);
        gc.fillText(texto, x - (texto.length() * 3), y + 4);
    }

    private void mostrarMensaje(String msg) {
        tablaDatos.getColumns().clear();
        tablaDatos.getItems().clear();
        crearColumna("Mensaje del Sistema", 0);
        List<String> fila = new ArrayList<>();
        fila.add(msg);
        tablaDatos.getItems().add(fila);
    }

    private void limpiarCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.WHITE);
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
    }
}
