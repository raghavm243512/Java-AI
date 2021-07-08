package dataStructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import neat.Gene;

public class RandomHashSet<T> implements Serializable {
	HashSet<T> set;
	ArrayList<T> data;

	public RandomHashSet() {
		set = new HashSet<>();
	}

	public boolean contains(T o) {
		return set.contains(o);
	}

	public T randomElement() {
		if (set.size() > 0)
			return data.get((int) (Math.random() * size()));
		return null;
	}

	public int size() {
		return data.size();
	}

	public void add(T o) {
		if (!set.contains(o)) {
			set.add(o);
			data.add(o);
		}
	}

	public void addSorted(Gene object) {
		for (int i = 0; i < this.size(); i++) {
			int innovation = ((Gene) data.get(i)).getInnovationNumber();
			if (object.getInnovationNumber() < innovation) {
				data.add(i, (T) object);
				set.add((T) object);
				return;
			}

		}
		data.add((T) object);
		set.add((T) object);
	}

	public void clear() {
		set.clear();
		data.clear();
	}

	public T get(int i) {
		if (i < 0 || i >= size()) {
			return null;
		}
		return data.get(i);
	}

	public void remove(int i) {
		if (i < 0 || i >= size()) {
			return;
		}
		set.remove(data.get(i));
		data.remove(i);
	}

	public void remove(T o) {
		set.remove(o);
		data.remove(o);
	}

	public ArrayList<T> getData() {
		return data;
	}

}
