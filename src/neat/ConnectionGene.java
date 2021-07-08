package neat;

import java.io.Serializable;

public class ConnectionGene extends Gene implements Serializable{
	private NodeGene from;
	private NodeGene to;
	private double weight;
	private boolean enabled = true;
	private int replaceIndex;

	public ConnectionGene(NodeGene from, NodeGene to) {
		this.from = from;
		this.to = to;
	}

	public NodeGene getFrom() {
		return from;
	}

	public void setFrom(NodeGene from) {
		this.from = from;
	}

	public NodeGene getTo() {
		return to;
	}

	public void setTo(NodeGene to) {
		this.to = to;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getReplaceIndex() {
		return replaceIndex;
	}

	public void setReplaceIndex(int replaceIndex) {
		this.replaceIndex = replaceIndex;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean equals(Object o) {
		if (!(o instanceof ConnectionGene))
			return false;
		ConnectionGene c = (ConnectionGene) o;
		return (from.equals(c.from) && to.equals(c.to));
	}

	public int hashCode() {
		return from.getInnovationNumber() * Neat.MAXNODES + to.getInnovationNumber();
	}

}
