package ar.edu.uns.cs.ed.tdas.tdalista;

import java.util.Iterator;

import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.excepciones.BoundaryViolationException;
import ar.edu.uns.cs.ed.tdas.excepciones.EmptyListException;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidPositionException;

public class ListaDoblementeEnlazada<E> implements PositionList<E> {

    private DNodo<E> cabeza;
    private DNodo<E> cola;
    private int cantElementos;

    public ListaDoblementeEnlazada() {
        cabeza = new DNodo<E>(null);
        cola = new DNodo<E>(null);
        cabeza.setSiguiente(cola);
        cola.setPrevio(cabeza);
        cantElementos = 0;
    }

    @Override
    public int size() {
        return cantElementos;
    }

    @Override
    public boolean isEmpty() {
        return cantElementos == 0;
    }

    @Override
    public Position<E> first() {
        if (isEmpty()) {
            throw new EmptyListException("No puedes pedir el primero de una lista vacía");
        }
        return cabeza.getSiguiente();
    }

    @Override
    public Position<E> last() {
        if (isEmpty()) {
            throw new EmptyListException("No puedes pedir el último de una lista vacía");
        }
        return cola.getPrevio();
    }

    @Override
    public Position<E> next(Position<E> p) {
        if (isEmpty()) {
            throw new InvalidPositionException("Imposible obtener el siguiente de una lista vacía");
        }
        DNodo<E> nodo = checkPosition(p);
        if (nodo.getSiguiente() == cola) {
            throw new BoundaryViolationException("La posición pasada por parámetro corresponde al último elemento de la lista");
        }
        return nodo.getSiguiente();
    }

    @Override
    public Position<E> prev(Position<E> p) {
        if (isEmpty()) {
            throw new InvalidPositionException("Imposible obtener el anterior de una lista vacía");
        }
        DNodo<E> nodo = checkPosition(p);
        if (nodo.getPrevio() == cabeza) {
            throw new BoundaryViolationException("La posición pasada por parámetro corresponde al primer elemento de la lista");
        }
        return nodo.getPrevio();
    }

    @Override
    public void addFirst(E element) {
        DNodo<E> nodoNuevo = new DNodo<E>(element);
        nodoNuevo.setPrevio(cabeza);
        nodoNuevo.setSiguiente(cabeza.getSiguiente());
        cabeza.getSiguiente().setPrevio(nodoNuevo);
        cabeza.setSiguiente(nodoNuevo);
        cantElementos++;
    }

    @Override
    public void addLast(E element) {
        DNodo<E> nodoNuevo = new DNodo<E>(element);
        nodoNuevo.setPrevio(cola.getPrevio());
        nodoNuevo.setSiguiente(cola);
        cola.getPrevio().setSiguiente(nodoNuevo);
        cola.setPrevio(nodoNuevo);
        cantElementos++;
    }

    @Override
    public void addAfter(Position<E> p, E element) {
        DNodo<E> pCasteado = checkPosition(p);
        DNodo<E> nodoNuevo = new DNodo<E>(element);
        nodoNuevo.setPrevio(pCasteado);
        nodoNuevo.setSiguiente(pCasteado.getSiguiente());
        pCasteado.setSiguiente(nodoNuevo);
        nodoNuevo.getSiguiente().setPrevio(nodoNuevo);
        cantElementos++;
    }

    @Override
    public void addBefore(Position<E> p, E element) {
        DNodo<E> pCasteado = checkPosition(p);
        DNodo<E> nodoNuevo = new DNodo<E>(element);
        nodoNuevo.setPrevio(pCasteado.getPrevio());
        nodoNuevo.setSiguiente(pCasteado);
        pCasteado.getPrevio().setSiguiente(nodoNuevo);
        pCasteado.setPrevio(nodoNuevo);
        cantElementos++;
    }

    @Override
    public E remove(Position<E> p) {
        DNodo<E> pCasteado = checkPosition(p);
        E elementoRemovido = pCasteado.element();

        DNodo<E> previo = pCasteado.getPrevio();
        DNodo<E> siguiente = pCasteado.getSiguiente();

        previo.setSiguiente(siguiente);
        siguiente.setPrevio(previo);

        pCasteado.setPrevio(null);
        pCasteado.setSiguiente(null);
        cantElementos--;
        return elementoRemovido;
    }

    @Override
    public E set(Position<E> p, E element) {
        DNodo<E> pCasteado = checkPosition(p);
        E elementoRemovido = pCasteado.element();
        pCasteado.setElement(element);
        return elementoRemovido;
    }

    @Override
    public Iterator<E> iterator() {
        return new ElementIterator<E>(this);
    }

    @Override
    public Iterable<Position<E>> positions() {
        PositionList<Position<E>> listaPosiciones = new ListaDoblementeEnlazada<Position<E>>();
        if (cantElementos != 0) {
            DNodo<E> n = cabeza.getSiguiente();
            while (n != cola) {
                listaPosiciones.addLast(n);
                n = n.getSiguiente();
            }
        }
        return listaPosiciones;
    }

    private DNodo<E> checkPosition(Position<E> p) throws InvalidPositionException, BoundaryViolationException {
        if (cantElementos == 0) {
            throw new InvalidPositionException("La lista está vacía");
        }
        if (p == null) {
            throw new InvalidPositionException("La posición pasada por parámetro es nula");
        }
        if (p == cabeza || p == cola) {
            throw new BoundaryViolationException("La posición pasada por parámetro es un centinela");
        }
        DNodo<E> nodo = null;
        try {
            nodo = (DNodo<E>) p;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return nodo;
    }
    
    /**
     * Convenciones tomadas:
     * - Si la lista esta vacía, se agrega primero a e2 al inicio y luego a e1 al final
     * - Si la lista tiene un solo elemento se lanza IllegalStateException
     * - Si la lista tiene más de un elemento se insertan de manera normal
     * @param e1 segundo elemento a agregar
     * @param e2 ante-último elemento a agregar
     */
    
    public void insertarSegundoYAnteultimo(E e1, E e2) {
    	if (cantElementos == 1) {
    		throw new IllegalStateException("No se puede cumplir con lo solicitado si la lista tiene un solo elemento");
    	}
    	DNodo<E> segundo = new DNodo<E>(e1);
    	DNodo<E> anteUltimo = new DNodo<E>(e2);
    	if (cantElementos == 0) {
    		cabeza.setSiguiente(anteUltimo);
    		cola.setPrevio(segundo);
    		anteUltimo.setSiguiente(segundo);
    		segundo.setPrevio(anteUltimo);
    	}
    	else {
    		DNodo<E> primero = cabeza.getSiguiente();
    		DNodo<E> ultimo = cola.getPrevio();
    		// inserto el segundo
    		segundo.setSiguiente(primero.getSiguiente());
    		segundo.getSiguiente().setPrevio(segundo);
    		segundo.setPrevio(primero);
    		primero.setSiguiente(segundo);
    		// inserto el ante-último
    		anteUltimo.setPrevio(ultimo.getPrevio());
    		anteUltimo.getPrevio().setSiguiente(anteUltimo);
    		anteUltimo.setSiguiente(ultimo);
    		ultimo.setPrevio(anteUltimo);
    	}
    	cantElementos += 2;
    }
    
    public PositionList<Integer> intercalacionOrdenada(PositionList<Integer> L1, PositionList<Integer> L2) {
    	PositionList<Integer> lRet = new ListaDoblementeEnlazada<>();
    	Position<Integer> cursorL1 = L1.first();
    	Position<Integer> cursorL2 = L2.first();
    	while(cursorL1 != L1.last() && cursorL2 != L2.last()) {
    		if(cursorL1.element() < cursorL2.element()) {
    			lRet.addLast(cursorL1.element());
    			cursorL1 = L1.next(cursorL1);
    		} else if(cursorL2.element() < cursorL1.element()) {
    			lRet.addLast(cursorL2.element());
    			cursorL2 = L2.next(cursorL2);
    		} else if (cursorL1.element() == cursorL2.element()) {
    			lRet.addLast(cursorL1.element());
    			cursorL1 = L1.next(cursorL1);
    			cursorL2 = L2.next(cursorL2);
    		}
    	}
    	while(cursorL1 != L1.last()) {
    		lRet.addLast(cursorL1.element());
    		cursorL1 = L1.next(cursorL1);
    	}
    	while(cursorL2 != L2.last()) {
    		lRet.addLast(cursorL2.element());
    		cursorL2 = L2.next(cursorL2);
    	}
    	return lRet;
    }
    
}
