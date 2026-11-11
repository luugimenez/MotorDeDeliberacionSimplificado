package ar.edu.uns.cs.ed.tdas.tdaarbol; 

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.excepciones.BoundaryViolationException;
import ar.edu.uns.cs.ed.tdas.excepciones.EmptyListException;
import ar.edu.uns.cs.ed.tdas.excepciones.EmptyTreeException;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidOperationException;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidPositionException;
import ar.edu.uns.cs.ed.tdas.tdalista.ListaDoblementeEnlazada;
import ar.edu.uns.cs.ed.tdas.tdalista.PositionList;

public class Arbol<E> implements Tree<E>{

    private TNodo<E> raiz;
    private int size;

    public Arbol() {
        raiz = null;
        size = 0;
    }
    
    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new ElementIterator<E>(this);
    }

    @Override
    public Iterable<Position<E>> positions() {
        List<Position<E>> positions = new ArrayList<>();
        if (raiz == null)
            return positions;

        Deque<TNodo<E>> pending = new ArrayDeque<>();
        pending.push(raiz);
        while (!pending.isEmpty()) {
            TNodo<E> current = pending.pop();
            positions.add(current);

            List<TNodo<E>> children = new ArrayList<>();
            for (TNodo<E> hijo : current.getHijos())
                children.add(hijo);
            for (int i = children.size() - 1; i >= 0; i--)
                pending.push(children.get(i));
        }
        return positions;
    }

    @Override
    public E replace(Position<E> v, E e) {
        TNodo<E> n = checkPosition(v);
        E replaced = n.element();
        n.setElement(e);
        return replaced;
    }

    @Override
    public Position<E> root() {
        if(size == 0)
            throw new EmptyTreeException("El árbol está vacío");
        return raiz;
    }

    @Override
    public Position<E> parent(Position<E> v) {
        TNodo<E> n = checkPosition(v);
        if (n == raiz)
            throw new BoundaryViolationException("La posición pasada es la raíz");
        return n.getPadre();
    }

    @Override
    public Iterable<Position<E>> children(Position<E> v) {
        TNodo<E> n = checkPosition(v);
        PositionList<Position<E>> hijos = new ListaDoblementeEnlazada<>();
        for (TNodo<E> hijo : n.getHijos())
            hijos.addLast(hijo);
        return hijos;
    }

    @Override
    public boolean isInternal(Position<E> v) {
        TNodo<E> n = checkPosition(v);
        return !n.getHijos().isEmpty();
    }

    @Override
    public boolean isExternal(Position<E> v) {
        TNodo<E> n = checkPosition(v);
        return n.getHijos().isEmpty();
    }

    @Override
    public boolean isRoot(Position<E> v) {
        TNodo<E> n = checkPosition(v);
        return n == raiz;
    }

    @Override
    public void createRoot(E e) {
        if (raiz != null)
            throw new InvalidOperationException("El árbol ya tiene una raíz");
        raiz = new TNodo<E>(e);
        size++;
    }

    @Override
    public Position<E> addFirstChild(Position<E> p, E e) {
        if(size == 0)
            throw new InvalidPositionException("No se puede agregar un hijo a un árbol vacío");
        TNodo<E> padre = checkPosition(p);
        TNodo<E> hijo = new TNodo<>(e);
        hijo.setPadre(padre);
        padre.getHijos().addFirst(hijo);
        size++;
        return hijo;
    }

    @Override
    public Position<E> addLastChild(Position<E> p, E e) {
        if(size == 0)
            throw new InvalidPositionException("No se puede agregar un hijo a un árbol vacío");
        TNodo<E> padre = checkPosition(p);
        TNodo<E> hijo = new TNodo<>(e);
        hijo.setPadre(padre);
        padre.getHijos().addLast(hijo);
        size++;
        return hijo;
    }

    @Override
    public Position<E> addBefore(Position<E> p, Position<E> rb, E e) {
        if (size == 0)
            throw new InvalidPositionException("No se puede agregar un hijo a un árbol vacío");

        TNodo<E> padre = checkPosition(p);
        TNodo<E> hijoDerecho = checkPosition(rb);
        Position<TNodo<E>> posDerecho = encontrarHijo(padre, hijoDerecho);
        if (posDerecho == null)
            throw new InvalidPositionException("La posición rb no corresponde a un hijo del nodo padre");

        TNodo<E> hijoNuevo = new TNodo<>(e);
        hijoNuevo.setPadre(padre);
        padre.getHijos().addBefore(posDerecho, hijoNuevo);
        size++;
        return hijoNuevo;
    }

    @Override
    public Position<E> addAfter(Position<E> p, Position<E> lb, E e) {
        if (size == 0)
            throw new InvalidPositionException("No se puede agregar un hijo a un árbol vacío");

        TNodo<E> padre = checkPosition(p);
        TNodo<E> hijoIzquierdo = checkPosition(lb);
        Position<TNodo<E>> posIzquierdo = encontrarHijo(padre, hijoIzquierdo);
        if (posIzquierdo == null)
            throw new InvalidPositionException("La posición lb no corresponde a un hijo del nodo padre");

        TNodo<E> hijoNuevo = new TNodo<>(e);
        hijoNuevo.setPadre(padre);
        padre.getHijos().addAfter(posIzquierdo, hijoNuevo);
        size++;
        return hijoNuevo;
    }

    @Override
    public void removeExternalNode(Position<E> p) {
        TNodo<E> n = checkPosition(p);
        if (!n.getHijos().isEmpty())
            throw new InvalidPositionException("La posición no corresponde a un nodo externo");
        removeNode(p);
    }

    @Override
    public void removeInternalNode(Position<E> p) {
        TNodo<E> n = checkPosition(p);
        if (n.getHijos().isEmpty())
            throw new InvalidPositionException("La posición no corresponde a un nodo interno");
        removeNode(p);
    }

    @Override
    public void removeNode(Position<E> p) {
	if(size == 0)
		throw new InvalidPositionException("No se puede eliminar de un arbol vacio");
	TNodo<E> n = checkPosition(p);
	try {
		if(n == raiz) // el nodo que se pretende eliminar es la raiz
			if(raiz.getHijos().size() == 1) //la raiz tiene un solo hijo
			{
				Position<TNodo<E>> rN = raiz.getHijos().first();
				rN.element().setPadre(null);
				raiz.getHijos().remove(rN);
				raiz = rN.element();
			}
			else
				if (size == 1) //el arbol tiene un unico nodo 
					raiz = null;
				else // se quiere eliminar la raiz pero no es posible por la estructura del arbol
					  throw new InvalidPositionException("Solo se puede eliminar la raiz si es el unico elemento o si tiene un solo hijo");
		else // Se quiere eliminar un nodo interno o un nodo hoja.
		{ 
			TNodo<E> padre = n.getPadre();
			PositionList<TNodo<E>> hPadre = padre.getHijos(); //hijos del padre (hermanos de n)
			PositionList<TNodo<E>> hN = n.getHijos(); //hijos de n
				
			//buscar a n dentro de los hijos del padre
				Position<TNodo<E>>posDeN;
				Position<TNodo<E>> cursor = hPadre.first();
				while(cursor.element() != n && cursor != null){
					if (cursor == hPadre.last())
						cursor = null;
					else
						cursor = hPadre.next(cursor);}
				if(cursor != null)	
					posDeN = cursor;
				else
					throw new InvalidPositionException("La estructura no corresponde a un arbol valido");
				
			//si n tiene hijos, se recorren e insertan ordenados en el lugar del padre
				
			while(!hN.isEmpty())
			{
				Position<TNodo<E>> hijoN = hN.first();
				hPadre.addBefore(posDeN,hijoN.element());
				hijoN.element().setPadre(padre);
				hN.remove(hijoN);
			}
			//eliminamos a n de la lista
			hPadre.remove(posDeN);
		}
		//decrementamos el tamaño de la estructura
		size--;
		} catch (EmptyListException | BoundaryViolationException e) {
			throw new InvalidPositionException("La estructura no corresponde a un arbol valido");
		}
    }

    private Position<TNodo<E>> encontrarHijo(TNodo<E> padre, TNodo<E> hijo) {
        if (padre == null || hijo == null)
            return null;

        for (Position<TNodo<E>> pos : padre.getHijos().positions()) {
            if (pos.element() == hijo)
                return pos;
        }
        return null;
    }

    private TNodo<E> checkPosition(Position<E> p) {
        if (p == null)
            throw new InvalidPositionException("La posición pasada es nula");
        TNodo<E> n = null;
        try {
            n = (TNodo<E>) p;
        } catch (ClassCastException e) {
            throw new InvalidPositionException("La posición no corresponde a un nodo del arbol");
        }
        return n;
    }

}
