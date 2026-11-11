package ar.edu.uns.cs.ed.tdas.tdaarbol;

import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.tdalista.ListaDoblementeEnlazada;
import ar.edu.uns.cs.ed.tdas.tdalista.PositionList;

public class TNodo<E> implements Position<E>{

    private E element;
    private TNodo<E> padre;
    private PositionList<TNodo<E>> hijos;
    
    public TNodo (E element) {
        this.element = element;
        this.padre = null;
        this.hijos = new ListaDoblementeEnlazada<TNodo<E>>();
    }

    public void setPadre(TNodo<E> padre) {
        this.padre = padre;
    }

    public TNodo<E> getPadre() {
        return padre;
    }
    
    public void setElement(E element) {
        this.element = element;
    }
    
    @Override
    public E element() {
        return element;
    }

    public PositionList<TNodo<E>> getHijos() {
        return hijos;
    }
}
