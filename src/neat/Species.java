package neat;

import java.util.Comparator;

import dataStructures.RandomHashSet;

public class Species {
	private RandomHashSet<Client> clients = new RandomHashSet<>();
	private Client rep;
	private double score;
	public Species(Client rep) {
		this.rep=rep;
		this.rep.setSpecies(this);
		clients.add(rep);
	}
	public boolean put(Client c) {
		if(c.distance(rep) < rep.getGenome().getNeat().getMaxdist()) {
			c.setSpecies(this);
			clients.add(c);
			return true;
		}
		return false;
	}
	public void forcePut(Client c) {
		c.setSpecies(this);
		clients.add(c);
	}
	public void goExtinct() {
		for (Client c : clients.getData()) {
			c.setSpecies(null);
		}
	}
	public void score() {
		double x = 0;
		for (Client c : clients.getData()) {
			x+=c.getScore();
		}
		score=x/clients.size();
	}
	public void reset() {
		clients.getData().sort(new Comparator<Client>() {
			@Override
			public int compare(Client o1, Client o2) {
				return Double.compare(o2.getScore(), o1.getScore());
			}
		});
		rep=clients.get((int)(Math.random()*3));
		for (Client c : clients.getData()) {
			c.setSpecies(null);
		}
		clients.clear();
		clients.add(rep);
		rep.setSpecies(this);
		score=0;
	}
	public void kill(double percent) {
		clients.getData().sort(new Comparator<Client>() {
			@Override
			public int compare(Client o1, Client o2) {
				return Double.compare(o1.getScore(), o2.getScore());
			}
		});
		double amount = clients.size()*percent;
		for (int i = 0; i < amount; i++) {
			clients.get(0).setSpecies(null);
			clients.remove(0);
		}
	}
	public Genome breed() {
		Client c1 = clients.randomElement();
		Client c2 = clients.randomElement();
		
		if(c1.getScore()>c2.getScore()) return Genome.crossover(c1.getGenome(), c2.getGenome());
		return Genome.crossover(c2.getGenome(), c1.getGenome());
	}
	public int size() {
		return clients.size();
	}
	public RandomHashSet<Client> getClients() {
		return clients;
	}
	public Client getRep() {
		return rep;
	}
	public double getScore() {
		return score;
	}
	
}
