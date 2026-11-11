package ar.edu.uns.cs.ed.tdas.tdaarbol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.tdapila.PilaConArreglo;
import ar.edu.uns.cs.ed.tdas.tdapila.Stack;

public class ElementIterator<E> implements Iterator<E>{
    private Tree<E> tree;
    private Stack<Position<E>> stack;
    
    public ElementIterator(Tree<E> t) {
        if (t == null) {
            throw new IllegalArgumentException("El árbol no puede ser nulo");
        }
        this.tree = t;
        this.stack = new PilaConArreglo<>();
        if (!tree.isEmpty()) {
            stack.push(tree.root());
        }
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No hay siguiente elemento");
        }

        Position<E> current = stack.pop();
        List<Position<E>> children = new ArrayList<>();
        for (Position<E> child : tree.children(current)) {
            children.add(child);
        }
        for (int i = children.size() - 1; i >= 0; i--) {
            stack.push(children.get(i));
        }

        return current.element();
    }
    
}
