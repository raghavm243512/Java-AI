package neat;

import java.io.Serializable;

public class NodeGene extends Gene implements Serializable {
	private double x, y;
	
	public NodeGene(int innovation) {
		super(innovation);
	}
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}


	public boolean equals(Object o) {
		if(!(o instanceof NodeGene)) return false;
        return innovationNumber == ((NodeGene) o).getInnovationNumber();
	}

	public int hashCode() {
		return innovationNumber;
	}
}
