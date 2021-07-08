package neat;

import java.io.Serializable;

import dataStructures.RandomHashSet;

public class Genome implements Serializable {
	private RandomHashSet<ConnectionGene> connections = new RandomHashSet<>();
	private RandomHashSet<NodeGene> nodes = new RandomHashSet<>();

	private Neat neat;


	public Genome(Neat n) {
		neat = n;
	}

	public double distance(Genome g2) {
		Genome g1 = this;
        int highest_innovation_gene1 = 0;
        if(g1.getConnections().size() != 0){
            highest_innovation_gene1 = g1.getConnections().get(g1.getConnections().size()-1).getInnovationNumber();
        }

        int highest_innovation_gene2 = 0;
        if(g2.getConnections().size() != 0){
            highest_innovation_gene2 = g2.getConnections().get(g2.getConnections().size()-1).getInnovationNumber();
        }

        if(highest_innovation_gene1 < highest_innovation_gene2){
            Genome g = g1;
            g1 = g2;
            g2 = g;
        }

        int index_g1 = 0;
        int index_g2 = 0;

        int disjoint = 0;
        int excess = 0;
        double weight_diff = 0;
        int similar = 0;


        while(index_g1 < g1.getConnections().size() && index_g2 < g2.getConnections().size()){

            ConnectionGene gene1 = g1.getConnections().get(index_g1);
            ConnectionGene gene2 = g2.getConnections().get(index_g2);

            int in1 = gene1.getInnovationNumber();
            int in2 = gene2.getInnovationNumber();

            if(in1 == in2){
                //similargene
                similar ++;
                weight_diff += Math.abs(gene1.getWeight() - gene2.getWeight());
                index_g1++;
                index_g2++;
            }else if(in1 > in2){
                //disjoint gene of b
                disjoint ++;
                index_g2++;
            }else{
                //disjoint gene of a
                disjoint ++;
                index_g1 ++;
            }
        }

        weight_diff /= Math.max(1,similar);
        excess = g1.getConnections().size() - index_g1;

        double N = Math.max(g1.getConnections().size(), g2.getConnections().size());
        if(N < 20){
            N = 1;
        }

        return neat.getC1()  * disjoint / N + neat.getC2() * excess / N + neat.getC3() * weight_diff / N;
	}

	public static Genome crossover(Genome g1, Genome g2) {
		Neat neat = g1.getNeat();

        Genome genome = neat.emptyGenome();

        int index_g1 = 0;
        int index_g2 = 0;

        while(index_g1 < g1.getConnections().size() && index_g2 < g2.getConnections().size()){

            ConnectionGene gene1 = g1.getConnections().get(index_g1);
            ConnectionGene gene2 = g2.getConnections().get(index_g2);

            int in1 = gene1.getInnovationNumber();
            int in2 = gene2.getInnovationNumber();

            if(in1 == in2){
                if(Math.random() > 0.5){
                    genome.getConnections().add(neat.getConnection(gene1));
                }else{
                    genome.getConnections().add(neat.getConnection(gene2));
                }
                index_g1++;
                index_g2++;
            }else if(in1 > in2){
                //genome.getConnections().add(neat.getConnection(gene2));
                //disjoint gene of b
                index_g2++;
            }else{
                //disjoint gene of a
                genome.getConnections().add(neat.getConnection(gene1));
                index_g1 ++;
            }
        }

        while(index_g1 < g1.getConnections().size()){
            ConnectionGene gene1 = g1.getConnections().get(index_g1);
            genome.getConnections().add(neat.getConnection(gene1));
            index_g1++;
        }

        for(ConnectionGene c:genome.getConnections().getData()){
            genome.getNodes().add(c.getFrom());
            genome.getNodes().add(c.getTo());
        }
        
        return genome;
	}

	public void mutate() {
		if (neat.getProbability_LinkMutation() > Math.random()) {
			mutatelink();
		}
		if (neat.getProbability_NodeMutation() > Math.random()) {
			mutatenode();
		}
		if (neat.getProbability_WeightShift() > Math.random()) {
			mutateWeightshift();
		}
		if (neat.getProbability_WeightRandom() > Math.random()) {
			mutateWeightrandom();
		}
		if (neat.getProbability_ToggleLink() > Math.random()) {
			mutateLinktoggle();
		}
	}

	public void mutatelink() {
		for(int i = 0; i < 100; i++){

            NodeGene a = nodes.randomElement();
            NodeGene b = nodes.randomElement();

            if(a == null || b == null) continue;
            if(a.getX() == b.getX()){
                continue;
            }

            ConnectionGene con;
            if(a.getX() < b.getX()){
                con = new ConnectionGene(a,b);
            }else{
                con = new ConnectionGene(b,a);
            }

            if(connections.contains(con)){
                continue;
            }

            con = neat.getConnection(con.getFrom(), con.getTo());
            con.setWeight((Math.random() * 2 - 1) * neat.getWeightrandomstrength());

            connections.addSorted(con);
            return;
        }
	}

	public void mutatenode() {
		ConnectionGene con = connections.randomElement();
        if(con == null) return;

        NodeGene from = con.getFrom();
        NodeGene to = con.getTo();

        int replaceIndex = neat.getReplaceIndex(from,to);
        NodeGene middle;
        if(replaceIndex == 0){
            middle = neat.getNode();
            middle.setX((from.getX() + to.getX()) / 2);
            middle.setY((from.getY() + to.getY()) / 2 + Math.random() * 0.1 - 0.05);
            neat.setReplaceIndex(from, to, middle.getInnovationNumber());
        }else{
            middle = neat.getNode(replaceIndex);
        }

        ConnectionGene con1 = neat.getConnection(from, middle);
        ConnectionGene con2 = neat.getConnection(middle, to);

        con1.setWeight(1);
        con2.setWeight(con.getWeight());
        con2.setEnabled(con.isEnabled());

        connections.remove(con);
        connections.add(con1);
        connections.add(con2);

        nodes.add(middle);
	}

	public void mutateWeightshift() {
		ConnectionGene con = connections.randomElement();
		if (con != null) {
			con.setWeight(con.getWeight() + (Math.random() * 2 - 1) * neat.getWeightshiftstrength());
		}
	}

	public void mutateWeightrandom() {
		ConnectionGene con = connections.randomElement();
		if (con != null) {
			con.setWeight((Math.random() * 2 - 1) * neat.getWeightrandomstrength());
		}
	}

	public void mutateLinktoggle() {
		ConnectionGene con = connections.randomElement();
		if (con != null) {
			con.setEnabled(!con.isEnabled());
		}
	}

	public RandomHashSet<ConnectionGene> getConnections() {
		return connections;
	}

	public RandomHashSet<NodeGene> getNodes() {
		return nodes;
	}

	public Neat getNeat() {
		return neat;
	}

}
