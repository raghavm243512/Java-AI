package neat;

import java.io.Serializable;

public class Client implements Serializable {
	private Genome genome;

	private double Score;
	private Species species;
	private Calculator calc;

	public void createCalc() {
		calc = new Calculator(genome);
	}
	public double distance(Client other) {
		return genome.distance(other.getGenome());
	}
	public void mutate() {
		genome.mutate();
	}
	public double[] calculate(double... a) {
		if (calc == null)
			createCalc();
		return calc.calculate(a);
	}
	public Genome getGenome() {
		return genome;
	}

	public void setGenome(Genome genome) {
		this.genome = genome;
	}

	public double getScore() {
		return Score;
	}

	public void setScore(double score) {
		Score = score;
	}

	public Species getSpecies() {
		return species;
	}

	public void setSpecies(Species species) {
		this.species = species;
	}

	public Calculator getCalc() {
		return calc;
	}
}
