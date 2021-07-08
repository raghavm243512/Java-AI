package neat;

import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Comparable<Node>, Serializable {
	private double x;
	private double activation;
	private ArrayList<Connection> connections = new ArrayList<>();

	@Override
	public int compareTo(Node o) {
		// TODO Auto-generated method stub
		if (this.x > o.x)
			return -1;
		if (this.x < o.x)
			return 1;
		return 0;
	}

	public Node(double x) {
		this.x = x;
	}

	public double getX() {
		return x;
	}

	public void calculate() {
		double sum = 0;
		for (Connection c : connections) {
			if (c.isEnabled())
				sum += c.getWeight() * c.getFrom().getActivation();
		}
		activation = sigmoid(sum);
	}

	private double sigmoid(double x) {
		return 1d / (1 + Math.exp(-x));
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getActivation() {
		return activation;
	}

	public void setActivation(double activation) {
		this.activation = activation;
	}

	public ArrayList<Connection> getConnections() {
		return connections;
	}

	public void setConnections(ArrayList<Connection> connections) {
		this.connections = connections;
	}

}
