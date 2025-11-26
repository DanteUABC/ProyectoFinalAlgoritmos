package grafoponderadodirigido;

/**
 *
 * @author dnt6
 */

import java.util.ArrayList;

public class ArbolBalanceado<T extends Comparable<T>>
{
    private Nodo<T> raiz;

    public ArbolBalanceado()
    {
        this.raiz = null;
    }

    private int altura(Nodo<T> nodo)
    {
        return (nodo == null) ? 0 : nodo.getAltura();
    }

    private int max(int a, int b)
    {
        return (a > b) ? a : b;
    }

    private void actualizarAltura(Nodo<T> nodo)
    {
        if (nodo != null)
            nodo.setAltura(1 + max(altura(nodo.getIzq()), altura(nodo.getDer())));
    }

    private int getFactorEquilibrio(Nodo<T> nodo) {
        if (nodo == null) {
            return 0;
        }
        return altura(nodo.getIzq()) - altura(nodo.getDer());
    }
    private Nodo<T> rotacionDerecha(Nodo<T> y)
    {
        Nodo<T> x = y.getIzq();
        Nodo<T> T2 = x.getDer();

        x.setDer(y);
        y.setIzq(T2);

        actualizarAltura(y);
        actualizarAltura(x);

        return x;
    }

    private Nodo<T> rotacionIzquierda(Nodo<T> x) {
        Nodo<T> y = x.getDer();
        Nodo<T> T2 = y.getIzq();

        y.setIzq(x);
        x.setDer(T2);

        actualizarAltura(x);
        actualizarAltura(y);

        return y;
    }

    public void insertar(T dato)
    {
        this.raiz = insertarRecursivo(this.raiz, dato);
    }

    private Nodo<T> insertarRecursivo(Nodo<T> nodo, T dato)
    {
        if (nodo == null) {
            return (new Nodo(dato));
        }

        int comparacion = dato.compareTo(nodo.getDato());

        if (comparacion < 0)
            nodo.setIzq(insertarRecursivo(nodo.getIzq(), dato));
        else if (comparacion > 0)
            nodo.setDer(insertarRecursivo(nodo.getDer(), dato));
        else
            return nodo;

        actualizarAltura(nodo);

        int factorEquilibrio = getFactorEquilibrio(nodo);


        // Caso LL
        if (factorEquilibrio > 1 && dato.compareTo(nodo.getIzq().getDato()) < 0) {
            return rotacionDerecha(nodo);
        }

        // Caso RR
        if (factorEquilibrio < -1 && dato.compareTo(nodo.getDer().getDato()) > 0) {
            return rotacionIzquierda(nodo);
        }

        // Caso LR
        if (factorEquilibrio > 1 && dato.compareTo(nodo.getIzq().getDato()) > 0) {
            nodo.setIzq(rotacionIzquierda(nodo.getIzq()));
            return rotacionDerecha(nodo);
        }

        // Caso RL
        if (factorEquilibrio < -1 && dato.compareTo(nodo.getDer().getDato()) < 0) {
            nodo.setDer(rotacionDerecha(nodo.getDer()));
            return rotacionIzquierda(nodo);
        }

        return nodo;
    }

    public Nodo<T> getNodoMinimo(Nodo<T> nodo)
    {
        Nodo<T> actual = nodo;
        while (actual.getIzq() != null) {
            actual = actual.getIzq();
        }
        return actual;
    }

    public T eliminar(T dato)
    {
        T datoEliminado = buscar(dato);
        if (datoEliminado != null) this.raiz = eliminarRecursivo(this.raiz, dato);
        return datoEliminado;
    }

    public Nodo<T> eliminarRecursivo(Nodo<T> nodo, T dato) {
        if (nodo == null)
            return nodo;

        // Busca el nodo
        if (dato.compareTo(nodo.getDato()) < 0) {
            nodo.setIzq(eliminarRecursivo(nodo.getIzq(), dato));
        } else if (dato.compareTo(nodo.getDato()) > 0) {
            nodo.setDer(eliminarRecursivo(nodo.getDer(), dato));
            // Lo encuentra
        } else {

            // Si tiene 0 o 1 hijo(s)
            if (nodo.getIzq() == null || nodo.getDer() == null) {
                Nodo<T> temp = (nodo.getIzq() != null) ? nodo.getIzq() : nodo.getDer();

                // 0 hijos
                if (temp == null) {
                    nodo = null;
                }
                // 1 hijo
                else {
                    nodo = temp;
                }
                // Tiene 2 hijos
            } else {
                Nodo<T> temp = getNodoMinimo(nodo.getDer());

                nodo.setDato(temp.getDato());

                nodo.setDer(eliminarRecursivo(nodo.getDer(), temp.getDato()));
            }
        }

        // Regresa null si el árbol queda vacío (es decir, no tenía hijos)
        if (nodo == null) {
            return nodo;
        }

        // Actualizar altura
        actualizarAltura(nodo);

        int factorEquilibrio = getFactorEquilibrio(nodo);

        // Balancear
        // Caso LL
        if (factorEquilibrio > 1 && getFactorEquilibrio(nodo.getIzq()) >= 0) {
            return rotacionDerecha(nodo);
        }

        // Caso LR
        if (factorEquilibrio > 1 && getFactorEquilibrio(nodo.getIzq()) < 0) {
            nodo.setIzq(rotacionIzquierda(nodo.getIzq()));
            return rotacionDerecha(nodo);
        }

        // Caso RR
        if (factorEquilibrio < -1 && getFactorEquilibrio(nodo.getDer()) <= 0) {
            return rotacionIzquierda(nodo);
        }

        // Caso RL
        if (factorEquilibrio < -1 && getFactorEquilibrio(nodo.getDer()) > 0) {
            nodo.setDer(rotacionDerecha(nodo.getDer()));
            return rotacionIzquierda(nodo);
        }

        return nodo;
    }

    public T buscar(T dato) {
        Nodo<T> actual = this.raiz;

        while (actual != null)
            if (dato.compareTo(actual.getDato()) == 0) {
                return actual.getDato();
            } else if (dato.compareTo(actual.getDato()) < 0) {
                actual = actual.getIzq();
            } else {
                actual = actual.getDer();
            }

        return null;
    }

    public ArrayList<T> mostrarInorden() {
        ArrayList<T> lista = new ArrayList<>();
        mostrarInordenRecursivo(this.raiz, lista);
        return lista;
    }

    private void mostrarInordenRecursivo(Nodo<T> nodo,ArrayList<T> lista) {
        if (nodo != null)
        {
            mostrarInordenRecursivo(nodo.getIzq(),lista);
            lista.add(nodo.getDato());
            mostrarInordenRecursivo(nodo.getDer(),lista);
        }
    }

    public ArrayList<T> mostrarPreorden() {
        ArrayList<T> lista = new ArrayList<>();
        mostrarPreordenRecursivo(this.raiz, lista);
        return lista;
    }

    private void mostrarPreordenRecursivo(Nodo<T> nodo,ArrayList<T> lista) {
        if (nodo != null) {
            lista.add(nodo.getDato());
            mostrarPreordenRecursivo(nodo.getIzq(),lista);
            mostrarPreordenRecursivo(nodo.getDer(),lista);
        }
    }

    public ArrayList<T> mostrarPostorden() {
        ArrayList<T> lista = new ArrayList<>();
        mostrarPostordenRecursivo(this.raiz, lista);
        return lista;
    }

    private void mostrarPostordenRecursivo(Nodo<T> nodo,ArrayList<T> lista) {
        if (nodo != null) {
            mostrarPostordenRecursivo(nodo.getIzq(),lista);
            mostrarPostordenRecursivo(nodo.getDer(),lista);
            lista.add(nodo.getDato());
        }
    }

}