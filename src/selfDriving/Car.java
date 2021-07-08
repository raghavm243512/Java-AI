package selfDriving;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import main.GeneticClient;
import main.NeuralNetwork;

public class Car implements GeneticClient {
	NeuralNetwork n;
	double x;
	double y;
	int maxspeed, currentSpeed;
	double cx;
	double cy;
	int w, h;
	double angle;
	Image car;
	double[] direction;
	double hypot;
	double exteriorAngle, interiorAngle;
	Track track;
	double[][] rayEnd = new double[2][5];
	boolean alive = true;
	ArrayList<Line2D> passed = new ArrayList<Line2D>();
	int score = 0;
	double tempx, tempy;

	public Car(int x, int y, int w, int h, Track t) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		n = new NeuralNetwork(5, 4, 4, 2);
		hypot = Math.hypot(w / 2, h / 2);
		angle = 0;
		interiorAngle = Math.atan2((h / 2), (w / 2));
		exteriorAngle = Math.PI / 2 - interiorAngle;
		direction = new double[2];
		car = getImage("car.jpg");
		setCenter();
		tempx = cx;
		tempy = cy;
		currentSpeed = 0;
		updateDirection();
		track = t;
	}

	public void reset(int x, int y) {
		this.x = x;
		this.y = y;
		angle = 0;
		direction = new double[2];
		setCenter();
		currentSpeed = 0;
		updateDirection();
		alive = true;
		passed = new ArrayList<Line2D>();
		score=0;
	}

	public void update() {
		if (alive) {
			collide();
			checkpoint();
			double[] d = computeRays(track);
			double[] output = n.run(d[0], d[1], d[2], d[3], d[4]);
			accelerate((output[0]) * 3);
			turn((output[1] - 0.5) / 8);
		}
	}

	public double[] computeRays(Track t) {
		double[] dist = new double[5];
		for (int i = 0; i < dist.length; i++) {
			dist[i] = 250;
		}
		for (Line2D.Float i : t.in) {
			// System.out.println(t.in.size());
			for (int j = 0; j < 5; j++) {
				if (lineOfSight(cx, cy, cx + 250 * Math.cos(angle - 1 + j * 0.5),
						cy + 250 * Math.sin(angle - 0.75 + j * 0.375), i.getX1(), i.getY1(), i.getX2(), i.getY2(), j,
						dist[j])) {
					dist[j] = Math.hypot(rayEnd[0][j] - cx, rayEnd[1][j] - cy);
					// System.out.println(dist[j]);
				}
			}
		}
		for (Line2D.Float i : t.out) {
			// System.out.println(t.in.size());
			for (int j = 0; j < 5; j++) {
				if (lineOfSight(cx, cy, cx + 250 * Math.cos(angle - 0.75 + j * 0.375),
						cy + 250 * Math.sin(angle - 0.75 + j * 0.375), i.getX1(), i.getY1(), i.getX2(), i.getY2(), j,
						dist[j])) {
					dist[j] = Math.hypot(rayEnd[0][j] - cx, rayEnd[1][j] - cy);
					// System.out.println(dist[j]);
				}
			}
		}
		for (int i = 0; i < dist.length; i++) {
			if (dist[i] == 250) {
				rayEnd[0][i] = cx + 250 * Math.cos(angle - 0.75 + i * 0.375);
				rayEnd[1][i] = cy + 250 * Math.sin(angle - 0.75 + i * 0.375);
			}
		}
		return dist;
	}

	private void updateDirection() {
		direction[0] = Math.cos(angle);
		direction[1] = Math.sin(angle);
	}

	private void setCenter() {
		// TODO Auto-generated method stub
		cx = x + w / 2;
		cy = y + h / 2;
	}

	public void raycast(Graphics g) {
		g.setColor(Color.ORANGE);
		for (int i = 0; i < 5; i++) {
			g.drawLine((int) cx, (int) cy, (int) rayEnd[0][i], (int) rayEnd[1][i]);
		}
		// g.drawLine((int)cx, (int)cy, (int)(cx+250*Math.cos(angle-0.75)),
		// (int)(cy+250*Math.sin(angle-0.75)));
		// g.drawLine((int)cx, (int)cy, (int)(cx+250*Math.cos(angle-0.375)),
		// (int)(cy+250*Math.sin(angle-0.375)));
		// g.drawLine((int)cx, (int)cy, (int)(cx+250*Math.cos(angle)),
		// (int)(cy+250*Math.sin(angle)));
		// g.drawLine((int)cx, (int)cy, (int)(cx+250*Math.cos(angle+0.375)),
		// (int)(cy+250*Math.sin(angle+0.373)));
		// g.drawLine((int)cx, (int)cy, (int)(cx+250*Math.cos(angle+0.75)),
		// (int)(cy+250*Math.sin(angle+0.75)));

	}

	boolean lineOfSight(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, int i,
			double dist) {

		// calculate the direction of the lines
		double uA = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));
		double uB = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));

		// if uA and uB are between 0-1, lines are colliding
		if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {
			// optionally, draw a circle where the lines meet
			if (dist > Math.hypot(x1 + (uA * (x2 - x1)) - cx, y1 + (uA * (y2 - y1)) - cy)) {
				rayEnd[0][i] = x1 + (uA * (x2 - x1));
				rayEnd[1][i] = y1 + (uA * (y2 - y1));
				return true;
			}
			return false;
		}
		return false;
	}

	public double[][] getRect() {
		double[][] a = new double[2][4];
		a[0][0] = cx + hypot * Math.cos(angle - interiorAngle);
		a[1][0] = cy + hypot * Math.sin(angle - interiorAngle);
		a[0][1] = cx + hypot * Math.cos(angle - interiorAngle - 2 * exteriorAngle);
		a[1][1] = cy + hypot * Math.sin(angle - interiorAngle - 2 * exteriorAngle);
		a[0][2] = cx + hypot * Math.cos(angle - 3 * interiorAngle - 2 * exteriorAngle);
		a[1][2] = cy + hypot * Math.sin(angle - 3 * interiorAngle - 2 * exteriorAngle);
		a[0][3] = cx + hypot * Math.cos(angle - 3 * interiorAngle - 4 * exteriorAngle);
		a[1][3] = cy + hypot * Math.sin(angle - 3 * interiorAngle - 4 * exteriorAngle);
		// System.out.println(exteriorAngle + " " + angle + " " + interiorAngle);
		return a;
	}

	private Image getImage(String path) {
		Image tempImage = null;
		try {
			URL imageURL = Car.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}

	public void draw(Graphics g) {
		// Graphics2D g2 = (Graphics2D) g;
		drawImage(g, car, angle, x, y, w, h);
		g.drawLine((int) cx, (int) cy, (int) cx + (int) (50 * direction[0]), (int) cy + (int) (50 * direction[1]));
		double[][] d = getRect();
		g.drawLine((int) cx, (int) cy, (int) (d[0][0]), (int) (d[1][0]));
		g.drawLine((int) cx, (int) cy, (int) (d[0][1]), (int) (d[1][1]));
		g.drawLine((int) cx, (int) cy, (int) (d[0][2]), (int) (d[1][2]));
		g.drawLine((int) cx, (int) cy, (int) (d[0][3]), (int) (d[1][3]));
		if (alive)
			raycast(g);
	}

	public void drawImage(Graphics g, Image img, double t, double x, double y, double w, double h) {
		Graphics2D g2 = (Graphics2D) g;
		int[] wh = { img.getWidth(null), img.getHeight(null) };
		AffineTransform tx = AffineTransform.getTranslateInstance(x, y);
		tx.rotate(t, w / 2, h / 2);
		tx.scale(w / wh[0], h / wh[1]);
		g2.drawImage(img, tx, null);
	}

	public void turn(double delta) {
		angle += delta; // remember to put limit
		updateDirection();
		setCenter();
	}

	public void checkpoint() {
		double[][] d = getRect();
		if (passed.size() == 25) {
			passed.clear();
		}
		for (Line2D l : track.checkpoints) {
			if (!passed.contains(l) && (l.intersectsLine(new Line2D.Double(d[0][0], d[1][0], d[0][1], d[1][1]))
					|| l.intersectsLine(new Line2D.Double(d[0][1], d[1][1], d[0][2], d[1][2]))
					|| l.intersectsLine(new Line2D.Double(d[0][2], d[1][2], d[0][3], d[1][3]))
					|| l.intersectsLine(new Line2D.Double(d[0][0], d[1][0], d[0][3], d[1][3])))) {
				passed.add(l);
				score++;
				// System.out.println("Passed");
			}
		}
	}

	public void collide() {
		double[][] d = getRect();
		for (Line2D l : track.in) {
			if (l.intersectsLine(new Line2D.Double(d[0][0], d[1][0], d[0][1], d[1][1]))
					|| l.intersectsLine(new Line2D.Double(d[0][1], d[1][1], d[0][2], d[1][2]))
					|| l.intersectsLine(new Line2D.Double(d[0][2], d[1][2], d[0][3], d[1][3]))
					|| l.intersectsLine(new Line2D.Double(d[0][0], d[1][0], d[0][3], d[1][3])))
				alive = false;
		}
		for (Line2D l : track.out) {
			if (l.intersectsLine(new Line2D.Double(d[0][0], d[1][0], d[0][1], d[1][1]))
					|| l.intersectsLine(new Line2D.Double(d[0][1], d[1][1], d[0][2], d[1][2]))
					|| l.intersectsLine(new Line2D.Double(d[0][2], d[1][2], d[0][3], d[1][3]))
					|| l.intersectsLine(new Line2D.Double(d[0][0], d[1][0], d[0][3], d[1][3])))
				alive = false;
		}
	}

	public void accelerate(double input) { // convert to real physics later
		x += direction[0] * 4 * input;
		y += direction[1] * 4 * input;
		// System.out.println(x+" "+y);
		setCenter();
	}

	@Override
	public NeuralNetwork getNetwork() {
		// TODO Auto-generated method stub
		return n;
	}

	@Override
	public double getScore() {
		// TODO Auto-generated method stub
		return score;
	}
	// private Dimension getImgDimension() {
	// URL imageURL = Car.class.getResource("car.jpg");
	// try (ImageInputStream in = ImageIO
	// .createImageInputStream(new
	// FileInputStream(imageURL.toString().substring(5)))) {
	// final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
	// if (readers.hasNext()) {
	// ImageReader reader = readers.next();
	// try {
	// reader.setInput(in);
	// return new Dimension(reader.getWidth(0), reader.getHeight(0));
	// } finally {
	// reader.dispose();
	// }
	// }
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return new Dimension();
	// }
}