package grafoponderadodirigido;
import java.util.ArrayList;

public class Data implements Comparable<Data> {
    private int id;
    private ArrayList<String> values;
    public Data(int id, ArrayList<String> values) {
        this.values = values;
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getValues() {
        return values;
    }
    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    /**
     * Solo compara la id de los elementos que se comparan.
     * @param o Dato a comparar.
     * @return Valor de la comparación.
     */
    @Override
    public int compareTo(Data o) {
        return Integer.compare(this.id, o.getId());
    }

    @Override
    public String toString() {
        return id + "," + values;
    }

}
