package grafoponderadodirigido;

import grafoponderadodirigido.ArbolBalanceado;
import grafoponderadodirigido.Grafo;
import grafoponderadodirigido.Arista;
import grafoponderadodirigido.LectorDeGrafo; 
import grafoponderadodirigido.LectorDeArbol; 
import grafoponderadodirigido.Data;          

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

    private GraphicsContext gc;

    private Grafo grafoLogistica;
    private ArbolBalanceado<Data> arbolInventario; 

    private Map<String, double[]> coordenadasNodos;
    
    private List<String> headersGrafo;
    private List<String> headersInventario;

    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        coordenadasNodos = new HashMap<>();
        headersGrafo = new ArrayList<>();
        headersInventario = new ArrayList<>();

        choiceProblemaResolver.getItems().addAll(
            "1. Ruta más corta",
            "2. Conectividad total",
            "3. Red Fibra Óptica",
            "4. Insertar",
            "5. Consulta",
            "6. Recorridos WIP"
        );

        choiceProblemaResolver.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> ejecutarLogica(newVal)
        );
        
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
                if (grafoLogistica == null) cargarGrafoUsandoLector(rutaGrafo);
                mostrarGrafoEnTablaDinamica(); 
                dibujarGrafoComoArbol();
                
                if (seleccion == '1') System.out.println("Listo para Dijkstra");
            }
            
            else if (seleccion == '4' || seleccion == '5' || seleccion == '6') {
                if (seleccion == '4' || arbolInventario == null) {
                    arbolInventario = new ArbolBalanceado<>();
                    cargarInventarioUsandoLector(rutaInv);
                }
                
                mostrarInventarioEnTablaDinamica(); 
                
                if (arbolInventario != null) {
                    dibujarArbolGenerico(arbolInventario.mostrarInorden());
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


    private void realizarBusquedaPorID() {
        if (arbolInventario == null) {
            mostrarMensaje("El inventario está vacío.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Búsqueda de Inventario");
        dialog.setHeaderText("Consulta");
        dialog.setContentText("Ingrese el ID");

        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(idString -> {
            try {
                int idBusqueda = Integer.parseInt(idString.trim());
                
                Data key = new Data(idBusqueda, new ArrayList<>());
                
                Data encontrado = arbolInventario.buscar(key);
                
                if (encontrado != null) {
                    mostrarResultadoUnicoEnTabla(encontrado);
                    tituloTabla.setText("Producto encontrado: ID " + idBusqueda);
                } else {
                    mostrarMensaje("No se encontró ningún producto con el ID: " + idBusqueda);
                }
                
            } catch (NumberFormatException e) {
                mostrarMensaje("El ID debe ser un número entero válido.");
            } catch (Exception e) {
                mostrarMensaje("Error al buscar: " + e.getMessage());
            }
        });
    }

    private void mostrarResultadoUnicoEnTabla(Data d) {
        tablaDatos.getItems().clear();
        
        List<String> fila = new ArrayList<>();
        List<String> valoresExtraidos = parsearDataDinamico(d);
        
        for (String val : valoresExtraidos) {
            fila.add(val);
        }
        
        while (fila.size() < tablaDatos.getColumns().size()) {
            fila.add("");
        }
        
        tablaDatos.getItems().add(fila);
    }

    private void cargarGrafoUsandoLector(String ruta) {
        headersGrafo = leerEncabezadosCSV(ruta);
        grafoLogistica = new Grafo();
        LectorDeGrafo lector = new LectorDeGrafo();
        System.out.println("Cargando grafo desde: " + ruta);
        lector.loadGrafo(ruta, grafoLogistica);

        if (grafoLogistica.getListaDeAdyacencia().isEmpty()) {
            mostrarMensaje("El grafo está vacío o no se encontró el archivo.");
            return;
        }

        coordenadasNodos.clear();
        for (String nodo : grafoLogistica.getListaDeAdyacencia().keySet()) {
            coordenadasNodos.putIfAbsent(nodo, generarCoordsRandom());
        }
        mostrarMensaje("Grafo cargado correctamente.");
    }

    private void cargarInventarioUsandoLector(String ruta) {
        headersInventario = leerEncabezadosCSV(ruta);
        LectorDeArbol lector = new LectorDeArbol();
        System.out.println("Cargando árbol desde: " + ruta);
        lector.loadArbol(ruta, arbolInventario);
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

    private void mostrarGrafoEnTablaDinamica() {
        if (grafoLogistica == null) return;
        tablaDatos.getColumns().clear();
        tablaDatos.getItems().clear();

        if (headersGrafo.isEmpty()) {
            headersGrafo.addAll(Arrays.asList("Origen", "Destino", "Costo", "Tipo"));
        }
        
        for (int i = 0; i < headersGrafo.size(); i++) {
            crearColumna(headersGrafo.get(i), i);
        }

        HashMap<String, ArrayList<Arista>> adj = grafoLogistica.getListaDeAdyacencia();
        
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

    private void mostrarInventarioEnTablaDinamica() {
        if (arbolInventario == null) return;
        tablaDatos.getColumns().clear();
        tablaDatos.getItems().clear();

        if (headersInventario.isEmpty()) {
            headersInventario.addAll(Arrays.asList("ID", "Detalles"));
        }

        for (int i = 0; i < headersInventario.size(); i++) {
            crearColumna(headersInventario.get(i), i);
        }

        ArrayList<Data> lista = arbolInventario.mostrarInorden();

        for (Data d : lista) {
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

    private void dibujarGrafoComoArbol() {
        if (grafoLogistica == null) return;
        Set<String> nodosSet = grafoLogistica.getListaDeAdyacencia().keySet();
        List<String> listaNodos = new ArrayList<>(nodosSet);
        Collections.sort(listaNodos);
        dibujarArbolGenerico(listaNodos);
    }

    private void dibujarArbolGenerico(List<?> datos) {
        if (datos == null || datos.isEmpty()) return;
        dibujarRamaRecursiva(datos, 0, datos.size() - 1, canvas.getWidth() / 2, 40, canvas.getWidth() / 4);
    }

    private void dibujarRamaRecursiva(List<?> datos, int inicio, int fin, double x, double y, double offsetH) {
        if (inicio > fin) return;

        int medio = (inicio + fin) / 2;
        String textoNodo = extraerIdDeData(datos.get(medio));

        dibujarNodoVisual(textoNodo, x, y, Color.FORESTGREEN);

        double nextY = y + 60; 
        if (inicio <= medio - 1) {
            double nextX = x - offsetH;
            gc.setStroke(Color.GRAY);
            gc.strokeLine(x, y + 15, nextX, nextY - 15);
            dibujarRamaRecursiva(datos, inicio, medio - 1, nextX, nextY, offsetH / 2);
        }
        if (medio + 1 <= fin) {
            double nextX = x + offsetH;
            gc.setStroke(Color.GRAY);
            gc.strokeLine(x, y + 15, nextX, nextY - 15);
            dibujarRamaRecursiva(datos, medio + 1, fin, nextX, nextY, offsetH / 2);
        }
    }

    private void dibujarNodoVisual(String texto, double x, double y, Color colorFondo) {
        gc.setFill(colorFondo);
        gc.fillOval(x - 15, y - 15, 30, 30);
        gc.setStroke(Color.WHITE);
        gc.strokeOval(x - 15, y - 15, 30, 30);
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

    private double[] generarCoordsRandom() {
        double margin = 50;
        double x = margin + Math.random() * (canvas.getWidth() - 2 * margin);
        double y = margin + Math.random() * (canvas.getHeight() - 2 * margin);
        return new double[]{x, y};
    }

    private void limpiarCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.WHITE);
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
    }
}