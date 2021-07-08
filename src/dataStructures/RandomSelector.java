package dataStructures;

import java.util.ArrayList;

public class RandomSelector<T> {
	private ArrayList<T> objects = new ArrayList<>();
	private ArrayList<Double> scores = new ArrayList<Double>();
	private double totalScore = 0;

	public void add(T o, double s) {
		objects.add(o);
		scores.add(s);
		totalScore += s;
	}

	public T random() {
		double v = Math.random() * totalScore;
		double c = 0;
		for (int i = 0; i < objects.size(); i++) {
			c += scores.get(i);
			if (c >= v) {
				return objects.get(i);
			}
		}
		return null;
	}
	public void reset() {
        objects.clear();
        scores.clear();
        totalScore = 0;
    }
}
