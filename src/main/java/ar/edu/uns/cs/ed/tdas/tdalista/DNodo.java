package ar.edu.uns.cs.ed.tdas.tdalista;

import ar.edu.uns.cs.ed.tdas.Position;

public class DNodo <E> implements Position <E> {

    private E element;
    private DNodo <E> previo;
    private DNodo <E> siguiente;
    
    public DNodo (E e) {
        element = e;
        previo = null;
        siguiente = null;
    }

    public void setElement(E e) {
        element = e;
    }

    public DNodo <E> getPrevio() {
        return previo;
    }

    public void setPrevio (DNodo <E> p) {
        previo = p;
    }

    public DNodo <E> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente (DNodo <E> s) {
        siguiente = s;
    }
    
    @Override
    public E element() {
        return element;   
    }

}
