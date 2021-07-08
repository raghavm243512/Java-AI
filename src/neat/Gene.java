package neat;

public class Gene {
	protected int innovationNumber;

	public int getInnovationNumber() {
		return innovationNumber;
	}

	public void setInnovationNumber(int innovationNumber) {
		this.innovationNumber = innovationNumber;
	}

	public Gene(int innovation) {
		this.innovationNumber = innovation;
	}

	public Gene() {

	}
}
