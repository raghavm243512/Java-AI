package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Driver extends JPanel implements ActionListener, MouseListener {
	double[][] input = { { 0, 0, 0 }, { 0, 0, 1 }, { 0, 1, 0 }, { 0, 1, 1 }, { 1, 0, 0 }, { 1, 0, 1 }, { 1, 1, 0 },
			{ 1, 1, 1 } };
	double[][] target = { { 1, 0, 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 0, 0, 0, 0 }, { 0, 0, 1, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 1, 0, 0, 0, 0 }, { 0, 0, 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 1, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 0, 0, 0, 1 },

	};
	NeuralNetwork n;
	Timer t;

	public Driver() {
		JFrame j = new JFrame();
		j.setSize(1000, 800);
		j.add(this);
		j.setVisible(true);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.addMouseListener(this);
		n = new NeuralNetwork(3, 8, 8);
		t = new Timer(16, this);
		t.start();
	}

	public static void main(String[] args) {
		new Driver();
	}

	public double sigmoid(double x) {
		return 1d / (1 + Math.exp(-x));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		for (int i = 0; i < n.networkSize; i++) {
			for (int j = 0; j < n.layers[i]; j++) {
				if (i < n.networkSize - 1) {
					for (int k = 0; k < n.layers[i + 1]; k++) {
						g2.setColor(Color.BLUE);
						g2.setStroke(new BasicStroke((int) (4 * (sigmoid(n.weights[i + 1][k][j])) + 1)));
						g2.drawLine(62 + (i) * 75, 62 + (j) * 75, 62 + (i + 1) * 75, 62 + k * 75);
					}
				}
				g.setColor(new Color(0, (int) (255 * n.activation[i][j]), 0));
				g.fillOval(50 + i * 75, 50 + j * 75, 25, 25);

			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		String s = JOptionPane.showInputDialog("Enter 3 digit binary number");
		double[] d = new double[3];
		d[0] = Integer.parseInt(s.substring(0, 1));
		d[1] = Integer.parseInt(s.substring(1, 2));
		d[2] = Integer.parseInt(s.substring(2, 3));
		System.out.println(Arrays.toString(n.run(d)));
		// System.out.println(Arrays.toString(n.run(input[1])));
		// System.out.println(Arrays.toString(n.run(input[2])));
		// System.out.println(Arrays.toString(n.run(input[3])));
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

		// System.out.println(Arrays.toString(n.run(input[4])));

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		// System.out.println(Arrays.toString(n.run(input[3])));
		for (int i = 0; i < 750; i++) {
			n.train(input[i % 8], target[i % 8], 0.3);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
