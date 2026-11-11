package ar.edu.uns.cs.ed.tdas.tdamapeo;

import ar.edu.uns.cs.ed.tdas.Entrada;
import ar.edu.uns.cs.ed.tdas.Entry;
import ar.edu.uns.cs.ed.tdas.Position;
import ar.edu.uns.cs.ed.tdas.excepciones.InvalidKeyException;
import ar.edu.uns.cs.ed.tdas.tdalista.ListaDoblementeEnlazada;
import ar.edu.uns.cs.ed.tdas.tdalista.PositionList;

public class Mapeo<K,V> implements Map<K,V> {

	private PositionList<Entrada<K,V>> [] A;
	private int N; // Cantidad de cubetas (tamaño del arreglo)
	private int n; // Cantidad de entradas
	private static final double FC = 0.7;
	
	@SuppressWarnings("unchecked")
	public Mapeo() {
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
		return size() == 0;
	}

	@Override
	public V get(K key) {
		checkKey(key);
		int h = funcionHash(key);
		for (Entrada<K,V> e : A[h]) {
			if (e.getKey().equals(key)) {
				return e.getValue();
			}
		}
		return null;
	}

	@Override
	public V put(K key, V value) {
		checkKey(key);
		int k = funcionHash(key);
		
		for (Entrada<K,V> e : A[k]) {
			if (e.getKey().equals(key)) {
				V valorViejo = e.getValue();
				e.setValue(value);
				return valorViejo;
			}
		}
		
		if (n / N >= FC) {
			rehash();
			k = funcionHash(key);
		}
		
		A[k].addLast(new Entrada<K,V>(key,value));
		n++;
		return null;
	}

	@Override
	public V remove(K key) {
		checkKey(key);
		int k = funcionHash(key);
		for (Position<Entrada<K,V>> e : A[k].positions()) {
			if (e.element().getKey().equals(key)) {
				V valorViejo = e.element().getValue();
				A[k].remove(e);
				n--;
				return valorViejo;
			}
		}
		return null;
	}

	@Override
	public Iterable<K> keys() {
		PositionList<K> listaClaves = new ListaDoblementeEnlazada<>();
		for (PositionList<Entrada<K,V>> cubeta : A) {
			for (Entrada<K,V> e : cubeta) {
				listaClaves.addLast(e.getKey());
			}
		}
		return listaClaves;
	}

	@Override
	public Iterable<V> values() {
		PositionList<V> listaValores = new ListaDoblementeEnlazada<>();
		for (PositionList<Entrada<K,V>> cubeta : A) {
			for (Entrada<K,V> e : cubeta) {
				listaValores.addLast(e.getValue());
			}
		}
		return listaValores;
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
				this.put(e.getKey(), e.getValue());
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
