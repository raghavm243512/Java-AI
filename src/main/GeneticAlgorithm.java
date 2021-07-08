package main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class GeneticAlgorithm {

	public double mutationrate = 0.1;
	public double mutationstrength = 0.1;
	public double survivors = 1;

	public <T extends GeneticClient> void evolve(ArrayList<T> clients) {

		clients.sort(new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {

				if (o1.getScore() > o2.getScore())
					return -1;
				if (o2.getScore() > o1.getScore())
					return 1;
				return 0;

			}
		});

		ArrayList<T> selection = this.selection(clients);

		crossover(clients, selection);

		clients.addAll(0, selection);

		mutate(clients);
	}

	private <T extends GeneticClient> ArrayList<T> selection(ArrayList<T> clients) {
		ArrayList<T> selection = new ArrayList<>();

		for (int i = 0; i < Math.min(survivors, clients.size()); i++) {
			selection.add(clients.get(i));
		}

		for (T o : selection) {
			clients.remove(o);
		}
		return selection;
	}

	private <T extends GeneticClient> void crossover(ArrayList<T> newborns, ArrayList<T> survivors) {

		for (T newborn : newborns) {

			T parentA = survivors.get((int) (Math.random() * survivors.size()));

			for (int i = 1; i < newborn.getNetwork().networkSize; i++) {
				newborn.getNetwork().weights[i] = copyArray(parentA.getNetwork().weights[i]);
				newborn.getNetwork().bias[i] = copyArray(parentA.getNetwork().bias[i]);
			}

		}

	}

	private <T extends GeneticClient> void mutate(ArrayList<T> clients) {
		for (T c : clients) {
			for (int i = 1; i < c.getNetwork().networkSize; i++) {
				mutateArray(c.getNetwork().weights[i], mutationrate, mutationstrength);
				mutateArray(c.getNetwork().bias[i], mutationrate, mutationstrength);
			}

		}
	}

	public static void mutateArray(double[][] vals, double rate, double strength) {
		Random random = new Random();
		for (int i = 0; i < vals.length; i++) {
			for (int n = 0; n < vals[0].length; n++) {
				if (Math.random() < rate) {
					vals[i][n] += random.nextGaussian() * strength;
				}
			}
		}
	}

	public static void mutateArray(double[] vals, double rate, double strength) {
		Random random = new Random();
		for (int i = 0; i < vals.length; i++) {
			if (Math.random() < rate) {
				vals[i] += random.nextGaussian() * strength;
			}
		}
	}

	public static double[][] copyArray(double[][] vals) {
		double[][] out = new double[vals.length][vals[0].length];
		for (int i = 0; i < vals.length; i++) {
			for (int n = 0; n < vals[0].length; n++) {
				out[i][n] = vals[i][n];
			}
		}
		return out;
	}

	public static double[] copyArray(double[] vals) {
		double[] out = new double[vals.length];
		for (int i = 0; i < vals.length; i++) {
			out[i] = vals[i];
		}
		return out;
	}

}
