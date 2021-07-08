package neat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import dataStructures.RandomHashSet;

public class Calculator implements Serializable {
	private ArrayList<Node> inputnodes = new ArrayList<Node>();
	private ArrayList<Node> hiddennodes = new ArrayList<Node>();
	private ArrayList<Node> outputnodes = new ArrayList<Node>();

	public Calculator(Genome g) {
		RandomHashSet<NodeGene> nodes = g.getNodes();
		RandomHashSet<ConnectionGene> connections = g.getConnections();

		HashMap<Integer, Node> nodemap = new HashMap<>();

		for (NodeGene n : nodes.getData()) {
			Node node = new Node(n.getX());
			nodemap.put(n.getInnovationNumber(), node);
			if (n.getX() <= 0.1) {
				inputnodes.add(node);
			} else if (n.getX() >= 0.9) {
				outputnodes.add(node);
			} else {
				hiddennodes.add(node);
			}
		}
		hiddennodes.sort(new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2);
			}

		});
		for (ConnectionGene c : connections.getData()) {
			NodeGene from = c.getFrom();
			NodeGene to = c.getTo();

			Node fromnode = nodemap.get(from.getInnovationNumber());
			Node tonode = nodemap.get(to.getInnovationNumber());

			Connection con = new Connection(fromnode, tonode);
			con.setWeight(c.getWeight());
			con.setEnabled(c.isEnabled());

			tonode.getConnections().add(con);
		}
	}

	public double[] calculate(double... input) {
		if (inputnodes.size() != input.length)
			return null;
		for (int i = 0; i < input.length; i++) {
			inputnodes.get(i).setActivation(input[i]);
		}
		for (Node n : hiddennodes) {
			n.calculate();
		}
		double output[] = new double[outputnodes.size()];
		for (int i = 0; i < output.length; i++) {
			outputnodes.get(i).calculate();
			output[i] = outputnodes.get(i).getActivation();
		}
		return output;
	}
	public int inputsize() {
		return inputnodes.size();
	}
	public int outputsize() {
		return outputnodes.size();
	}
	public double inputActivation(int i) {
		return inputnodes.get(i).getActivation();
	}
	public double outputActivation(int i) {
		return outputnodes.get(i-inputnodes.size()).getActivation();
	}
	public double hiddenActivation(int i) {
		return hiddennodes.get(i-inputnodes.size()-outputnodes.size()).getActivation();
	}
}
