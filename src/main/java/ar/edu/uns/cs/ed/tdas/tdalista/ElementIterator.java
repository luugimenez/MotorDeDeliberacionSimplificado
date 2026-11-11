package ar.edu.uns.cs.ed.tdas.tdalista;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.excepciones.BoundaryViolationException;
import ar.edu.uns.cs.ed.tdas.excepciones.EmptyListException;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidPositionException;

public class ElementIterator<E> implements Iterator<E> {

    protected PositionList<E> lista;
    protected Position<E> cursor;

    public ElementIterator (PositionList<E> l) {
        lista = l;
        if (lista.isEmpty()) {
            cursor = null;
        } else {
            try {
                cursor = lista.first();
            } catch (EmptyListException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**    (non-Javadoc)
     * Indica si existe un siguiente elemento al elemento actual.
     * @return true, si existe un siguiente elemento al elemento actual en la lista iterable.
     * @return false, si no existe un siguiente elemento al elemento actual en la lista iterable.
     * @see java.util.Iterator#hasNext()
     */

    public boolean hasNext() {
        return cursor != null;
    }

    /**    (non-Javadoc)
     * @return el siguiente elemento en la lista iterable.
     * @throws NoSuchElementException si no hay siguiente elemento al elemento actual en la lista.
     * @see java.util.Iterator#next()
     */
    
    public E next() throws NoSuchElementException {
        if (cursor == null) {
            throw new NoSuchElementException("No hay siguiente elemento");
        }
        E toReturn = cursor.element();
        try {
            cursor = (cursor == lista.last()) ? null : lista.next(cursor);
        } catch (InvalidPositionException | BoundaryViolationException | EmptyListException e) {
            e.printStackTrace();
        }
        return toReturn;
    }
}
