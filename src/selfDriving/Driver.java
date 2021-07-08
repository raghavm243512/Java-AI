package selfDriving;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import main.GeneticAlgorithm;
import main.GeneticClient;
import main.NeuralNetwork;

public class Driver extends JPanel implements ActionListener, MouseListener, KeyListener {

	NeuralNetwork n;
	Timer t;
	ArrayList<Car> c = new ArrayList<Car>();
	Track track;
	Car c2;
	GeneticAlgorithm g;
	Point[] starts = new Point[4];
	boolean speed = false;
	int frame;
	boolean display = false;

	public Driver() {
		starts[0] = new Point(150, 130);
		starts[1] = new Point(1100, 833);
		starts[2] = new Point(333, 544);
		starts[3] = new Point(1061, 135);
		g = new GeneticAlgorithm();
		g.survivors = 16;
		g.mutationrate = 0.2;
		g.mutationstrength = 0.1;
		track = new Track();
		c2 = new Car(starts[2].x, starts[2].y, 75, 38, track);
		try {
			FileInputStream fi = new FileInputStream(new File("").getAbsolutePath() + "/src/selfDriving/ai.txt");
			ObjectInputStream oi = new ObjectInputStream(fi);
			NeuralNetwork temp = (NeuralNetwork) oi.readObject();
			c2.n = temp;
			n = temp;
			oi.close();
			fi.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < 64; i++) {
			c.add(new Car(starts[2].x, starts[2].y, 75, 38, track));
			if (i % 6 == 0)
				c.get(i).n = n;
			// g.evolve(c);
		}
		JFrame j = new JFrame();
		j.setSize(1400, 1000);
		j.add(this);
		j.setVisible(true);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.addMouseListener(this);
		j.addKeyListener(this);
		// n = new NeuralNetwork(3, 8, 8);
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
		track.draw(g);
		if (!display) {
			int living = 0;
			for (Car i : c) {
				if (i.alive) {
					i.draw(g);
					living++;
				}
			}
			g.setColor(Color.BLACK);
			g.drawString("" + living, 100, 800);
		} else {
			c2.draw(g);
			Graphics2D g2 = (Graphics2D) g;
			for (int i = 0; i < c2.n.networkSize; i++) {
				for (int j = 0; j < c2.n.layers[i]; j++) {
					if (i < c2.n.networkSize - 1) {
						for (int k = 0; k < c2.n.layers[i + 1]; k++) {
							if (c2.n.weights[i + 1][k][j] >= 0)
								g2.setColor(Color.BLUE);
							else
								g2.setColor(Color.RED);

							g2.setStroke(new BasicStroke(
									(int) (8 * (sigmoid(Math.abs(c2.n.weights[i + 1][k][j])) - 0.5) + 1)));
							g2.drawLine(62 + (i) * 75, 612 + (j) * 75, 62 + (i + 1) * 75, 612 + k * 75);
						}
					}
					if (i == 0) {
						g.setColor(new Color(0, (int) (c2.n.activation[i][j]), 0));
						g.fillOval(50 + i * 75, 600 + j * 75, 25, 25);
					} else {
						g.setColor(new Color(0, (int) (255 * c2.n.activation[i][j]), 0));
						g.fillOval(50 + i * 75, 600 + j * 75, 25, 25);
					}

				}
			}

		}

	}

	public void update() {
		int dead = 0;
		for (Car i : c) {
			i.update();
			if (!i.alive)
				dead++;
		}
		if (dead == 64) {
			// allDead=false;
			g.evolve(c);
			for (Car i : c) {
				i.reset(starts[2].x, starts[2].y);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (!display) {
			update();
			if (speed) {
				update();
				update();
				update();
				update();
				update();
				update();
				update();
				update();
				update();
				update();
				update();
				update();
				update();
				update();
				update();
				update();
				update();
				update();
				update();
			}
			if (!speed)
				repaint();
		} else {
			c2.update();
			repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println(arg0.getX() + "," + arg0.getY());
		if (!display) {
			g.evolve(c);
			for (Car i : c) {
				i.reset(starts[2].x, starts[2].y);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		// if (e.getKeyCode()==KeyEvent.VK_LEFT) {
		// c2.turn(-0.1);
		// }
		// if (e.getKeyCode()==KeyEvent.VK_RIGHT) {
		// c2.turn(0.1);
		// }
		// if (e.getKeyCode()==KeyEvent.VK_UP) {
		// c2.accelerate(0.5);
		// }
		// g.evolve(c);
		// for (Car i : c) {
		// i.reset(starts[2].x, starts[2].y);
		// }
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			speed = !speed;
			// System.out.println(speed);
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			saveAI(c);
		}
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			display = !display;
		}
	}

	public <T extends GeneticClient> void saveAI(ArrayList<T> clients) {
		clients.sort(new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {

				if (o1.getScore() > o2.getScore())
					return -1;
				if (o2.getScore() > o1.getScore())
					return 1;
				return 0;

			}
		});
		FileOutputStream f;
		try {
			f = new FileOutputStream(new File("").getAbsolutePath() + "/src/selfDriving/ai.txt", false);
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(clients.get(0).getNetwork());
			o.close();
			f.close();
			for (Car i : c) {
				i.reset(starts[2].x, starts[2].y);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println(speed);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
