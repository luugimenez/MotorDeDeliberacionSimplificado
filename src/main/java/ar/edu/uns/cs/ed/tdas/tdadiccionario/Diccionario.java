package ar.edu.uns.cs.ed.tdas.tdadiccionario;

import ar.edu.uns.cs.ed.tdas.Entrada;
import ar.edu.uns.cs.ed.tdas.Entry;
import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidEntryException;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidKeyException;
import ar.edu.uns.cs.ed.tdas.tdalista.ListaDoblementeEnlazada;
import ar.edu.uns.cs.ed.tdas.tdalista.PositionList;

public class Diccionario<K,V> implements Dictionary<K,V> {

	private PositionList<Entrada<K,V>> [] A;
	private int N; // Cantidad de cubetas (tamaño del arreglo)
	private int n; // Cantidad de entradas
	private static final double FC = 0.7;
    
    @SuppressWarnings("unchecked")
    public Diccionario() {
		N = 11;
		n = 0;
		A = new PositionList[N];
		for (int i = 0; i < A.length; i++) {
			A[i] = new ListaDoblementeEnlazada<>();
		}
    }
    
    @Override
    public int size() {
        return n;
    }

    @Override
    public boolean isEmpty() {
        return n == 0;
    }

    @Override
    public Entry<K, V> find(K key) {
        checkKey(key);
        int h = funcionHash(key);
        for (Entrada<K,V> e : A[h]) {
            if (e.getKey().equals(key)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public Iterable<Entry<K, V>> findAll(K key) {
        PositionList<Entry<K,V>> lista = new ListaDoblementeEnlazada<>();
        checkKey(key);
        int h = funcionHash(key);
        for (Entrada<K,V> e : A[h]) {
            if (e.getKey().equals(key))
                lista.addLast(e);
        }
        return lista;
    }

    @Override
    public Entry<K, V> insert(K key, V value) {
        checkKey(key);
        Entrada<K,V> entrada = new Entrada<K,V>(key, value);
        int h = funcionHash(key);
        A[h].addLast(entrada);
        n++;
        if (n / N >= FC) {
			rehash();
			h = funcionHash(key);
		}
        return entrada;
    }    

    @Override
    public Entry<K, V> remove(Entry<K, V> e) {
        if (e == null || e.getKey() == null)
            throw new InvalidEntryException("La entrada no es válida");
        int h = funcionHash(e.getKey());
        for (Position<Entrada<K, V>> p : A[h].positions()) {
            Entrada<K,V> actual = p.element();
            if (actual.getKey().equals(e.getKey()) && actual.getValue().equals(e.getValue())) {
                Entrada<K,V> eliminada = actual;
                A[h].remove(p);
                n--;
                return eliminada;
            }
        }
        throw new InvalidEntryException("No se encontro la entrada en el diccionario");
    }

    @Override
    public Iterable<Entry<K, V>> entries() {
		PositionList<Entry<K,V>> listaEntradas = new ListaDoblementeEnlazada<>();
		for (PositionList<Entrada<K,V>> cubeta : A) {
			for (Entrada<K,V> e : cubeta) {
				listaEntradas.addLast(e);
			}
		}
		return listaEntradas;
    }

    private void checkKey(K key) {
	if (key == null)
		throw new InvalidKeyException("La clave es nula");
	}

    private int funcionHash(K key) {
        return Math.abs(key.hashCode()) % N;
    }

	@SuppressWarnings("unchecked")
	private void rehash() {
		int nuevoN = siguientePrimo(N * 2);
		PositionList<Entrada<K,V>> [] viejoA = A;

		// Creo un nuevo arreglo de cubetas con mayor capacidad
		A = new PositionList[nuevoN];
		for (int i = 0; i < nuevoN; i++) {
			A[i] = new ListaDoblementeEnlazada<>();
		}

		N = nuevoN;
		n = 0; 

		// Vuelvo a insertar las entradas en el nuevo arreglo
		for (PositionList<Entrada<K,V>> cubeta : viejoA) {
			for (Entrada<K,V> e : cubeta) {
				this.insert(e.getKey(), e.getValue());
			}
		}
	}

	private int siguientePrimo(int num) {
		while(!esPrimo(num)) {
			num++;
		}
		return num;
	}

	private boolean esPrimo(int num) {
		if (num <= 1) return false;
		for (int i = 2; i <= Math.sqrt(num); i++) {
			if (num % i == 0) {
				return false;
			}
		}
		return true;
	}
}
