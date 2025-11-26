/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package grafoponderadodirigido;
public class Nodo<T extends Comparable<T>>
{

    private Nodo<T> izq, der;
    private T dato;
    private int altura;

    public Nodo()
    {
        this.altura = 0;
    }

    public Nodo(T dato)
    {
        this.dato = dato;
        this.altura = 1;
        this.izq = null;
        this.der = null;
    }

    public void setIzq(Nodo<T> izq)
    {
        this.izq = izq;
    }

    public void setDer(Nodo<T> der)
    {
        this.der = der;
    }

    public Nodo<T> getIzq()
    {
        return izq;
    }

    public Nodo<T> getDer()
    {
        return der;
    }

    public T getDato() {
        return dato;
    }

    public void setDato(T dato)
    {
        this.dato = dato;
    }

    public int getAltura()
    {
        return altura;
    }

    public void setAltura(int altura)
    {
        this.altura = altura;
    }
}