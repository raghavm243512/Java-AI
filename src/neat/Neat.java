package neat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import dataStructures.RandomHashSet;
import dataStructures.RandomSelector;

public class Neat implements Serializable {
	public static final int MAXNODES = (int) Math.pow(2, 20);
	private HashMap<ConnectionGene, ConnectionGene> allConnections = new HashMap<>();
	private ArrayList<NodeGene> allNodes = new ArrayList<>();
	private int inputSize;
	private int outputSize;
	private int maxClients;
	private double c1 = 1, c2 = 1, c3 = 1;
	private double weightshiftstrength = 2;
	private double weightrandomstrength = 1;
	private double probability_LinkMutation = 0.1;
	private double probability_NodeMutation = 0.05;
	private double probability_WeightShift = 0.4;
	private double probability_WeightRandom = 0.2;
	private double probability_ToggleLink = 0.04;
	private double maxdist = 4;
	private double survivors = 0.5;
	private RandomHashSet<Client> clients = new RandomHashSet<>();
	private RandomHashSet<Species> species = new RandomHashSet<>();

	public Neat(int inputs, int outputs, int clients) {
		reset(inputs, outputs, clients);
	}
	public void setReplaceIndex(NodeGene n1, NodeGene n2, int index) {
		allConnections.get(new ConnectionGene(n1,n2)).setReplaceIndex(index);
	}
	public int getReplaceIndex(NodeGene n1, NodeGene n2) {
		ConnectionGene con = new ConnectionGene(n1,n2);
		ConnectionGene data = allConnections.get(con);
		if(data==null) return 0;
		return data.getReplaceIndex();
	}
	public Genome emptyGenome() {
		Genome g = new Genome(this);
		for (int i = 0; i < inputSize + outputSize; i++) {
			g.getNodes().add(getNode(i + 1));
		}
		return g;
	}

	public void reset(int inputs, int outputs, int clients) {
		inputSize = inputs;
		outputSize = outputs;
		maxClients = clients;
		allConnections.clear();
		allNodes.clear();
		this.clients.clear();
		for(int i = 0;i < inputSize; i++){
            NodeGene n = getNode();
            n.setX(0.1);
            n.setY((i + 1) / (double)(inputSize + 1));
        }

        for(int i = 0; i < outputSize; i++){
            NodeGene n = getNode();
            n.setX(0.9);
            n.setY((i + 1) / (double)(outputSize + 1));
        }

        for(int i = 0; i < maxClients; i++){
            Client c = new Client();
            c.setGenome(emptyGenome());
            c.createCalc();
            this.clients.add(c);
        }
	}
	public Client getClient(int i) {
		return clients.get(i);
	}
	public ConnectionGene getConnection(ConnectionGene c) {
		ConnectionGene con = new ConnectionGene(c.getFrom(), c.getTo());
		con.setInnovationNumber(c.getInnovationNumber());
		con.setWeight(c.getWeight());
		con.setEnabled(c.isEnabled());
		return con;
	}

	public ConnectionGene getConnection(NodeGene n1, NodeGene n2) {
		ConnectionGene c = new ConnectionGene(n1, n2);
		if (allConnections.containsKey(c)) {
			c.setInnovationNumber(allConnections.get(c).getInnovationNumber());
		} else {
			c.setInnovationNumber(allConnections.size() + 1);
			allConnections.put(c, c);
		}
		return c;
	}

	public NodeGene getNode() {
		NodeGene n = new NodeGene(allNodes.size() + 1);
		allNodes.add(n);
		return n;
	}
	
	public void setSurvivors(double survivors) {
		this.survivors = survivors;
	}

	public void evolve() {
		generateSpecies();
		kill();
		removeExtinct();
		reproduce();
		mutate();
		for(Client c : clients.getData()) {
			c.createCalc();
		}
	}

	private void mutate() {
		// TODO Auto-generated method stub
		for(Client c : clients.getData())
			c.mutate();
	}

	private void reproduce() {
		// TODO Auto-generated method stub
		RandomSelector<Species> selector = new RandomSelector<>();
		for(Species s : species.getData()) {
			selector.add(s, s.getScore());
		}
		for(Client c : clients.getData()) {
			if(c.getSpecies()==null) {
				Species s = selector.random();
				c.setGenome(s.breed());
				s.forcePut(c);
			}
		}
	}

	private void removeExtinct() {
		// TODO Auto-generated method stub
		for (int i = species.size()-1; i >=0; i--) {
			if(species.get(i).size()<=1) {
				species.get(i).goExtinct();
				species.remove(i);
			}
		}
	}

	private void generateSpecies() {
		// TODO Auto-generated method stub
		for(Species s : species.getData()) {
			s.reset();
		}
		for(Client c : clients.getData()) {
			if(c.getSpecies()!=null) continue;
			boolean found = false;
			for(Species s : species.getData()) {
				if(s.put(c)) {
					found=true;
					break;
				}
			}
			if(!found) {
				species.add(new Species(c));
			}
		}
		for(Species s : species.getData()) {
			s.score();
		}
	}
	private void kill() {
		for(Species s : species.getData()) {
			s.kill(1-survivors);
		}
	}

	public NodeGene getNode(int id) {
		if (id <= allNodes.size())
			return allNodes.get(id - 1);
		return getNode();
	}
	public Client getBestClient() {
	    int index = 0;
	    for(int i = 0; i < clients.size(); i++){
	         if(clients.get(index).getScore() < clients.get(i).getScore()){
	              index = i;
	         }
	    }

	    return clients.get(index);
	}

	public double getC1() {
		return c1;
	}

	public double getC2() {
		return c2;
	}

	public double getC3() {
		return c3;
	}

	public double getWeightshiftstrength() {
		return weightshiftstrength;
	}

	public double getWeightrandomstrength() {
		return weightrandomstrength;
	}

	public void setWeightshiftstrength(double weightshiftstrength) {
		this.weightshiftstrength = weightshiftstrength;
	}

	public void setWeightrandomstrength(double weightrandomstrength) {
		this.weightrandomstrength = weightrandomstrength;
	}

	public double getProbability_LinkMutation() {
		return probability_LinkMutation;
	}

	public void setProbability_LinkMutation(double probability_LinkMutation) {
		this.probability_LinkMutation = probability_LinkMutation;
	}

	public double getProbability_NodeMutation() {
		return probability_NodeMutation;
	}

	public void setProbability_NodeMutation(double probability_NodeMutation) {
		this.probability_NodeMutation = probability_NodeMutation;
	}

	public double getProbability_WeightShift() {
		return probability_WeightShift;
	}

	public void setProbability_WeightShift(double probability_WeightShift) {
		this.probability_WeightShift = probability_WeightShift;
	}

	public double getProbability_WeightRandom() {
		return probability_WeightRandom;
	}

	public void setProbability_WeightRandom(double probability_WeightRandom) {
		this.probability_WeightRandom = probability_WeightRandom;
	}

	public double getProbability_ToggleLink() {
		return probability_ToggleLink;
	}

	public void setProbability_ToggleLink(double probability_ToggleLink) {
		this.probability_ToggleLink = probability_ToggleLink;
	}
	public double getMaxdist() {
		return maxdist;
	}

	public void setMaxdist(double maxdist) {
		this.maxdist = maxdist;
	}
	public RandomHashSet<Client> getClients() {
		return clients;
	}
}
