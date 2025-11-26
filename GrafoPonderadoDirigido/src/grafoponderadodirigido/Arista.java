package grafoponderadodirigido;

public class Arista {
    private String destino;
    private double costo;
    private String tipo;

    public Arista(String destino, double costo, String tipo) {
        this.destino = destino;
        this.costo = costo;
        this.tipo = tipo;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return destino + "," + costo + "," + tipo;
    }
}