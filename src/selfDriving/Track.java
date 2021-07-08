package selfDriving;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Track {
	ArrayList<Line2D.Float> in = new ArrayList<Line2D.Float>();
	ArrayList<Line2D.Float> out = new ArrayList<Line2D.Float>();
	ArrayList<Line2D.Float> checkpoints = new ArrayList<Line2D.Float>();

	public Track() {
		ArrayList<Point> inner = new ArrayList<Point>();
		ArrayList<Point> outer = new ArrayList<Point>();
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(new File("").getAbsolutePath() + "/src/selfDriving/track.txt"));
			String s = br.readLine();
			while (s != null) {
				inner.add(new Point(Integer.parseInt(s.substring(0, s.indexOf(","))),
						Integer.parseInt(s.substring(s.indexOf(",") + 1))));
				s = br.readLine();
				if (s.contains("o")) {
					s = br.readLine();
					break;
				}
			}
			while (s != null) {
				outer.add(new Point(Integer.parseInt(s.substring(0, s.indexOf(","))),
						Integer.parseInt(s.substring(s.indexOf(",") + 1))));
				s = br.readLine();
				if (s.contains("c")) {
					s = br.readLine();
					break;
				}
			}
			while (s != null) {
				checkpoints.add(new Line2D.Float(Integer.parseInt(s.substring(0, s.indexOf(","))),
						Integer.parseInt(s.substring(s.indexOf(",") + 1, s.indexOf("|"))),
						Integer.parseInt(s.substring(s.indexOf("|") + 1, s.lastIndexOf(","))),
						Integer.parseInt(s.substring(s.lastIndexOf(",") + 1))));
				s = br.readLine();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 1; i < inner.size(); i++) {
			in.add(new Line2D.Float(inner.get(i - 1).x, inner.get(i - 1).y, inner.get(i).x, inner.get(i).y));
		}
		for (int i = 1; i < outer.size(); i++) {
			out.add(new Line2D.Float(outer.get(i - 1).x, outer.get(i - 1).y, outer.get(i).x, outer.get(i).y));
		}
	}

	public void draw(Graphics g) {
		for (Line2D l : in) {
			g.drawLine((int) l.getX1(), (int) l.getY1(), (int) l.getX2(), (int) l.getY2());
		}
		for (Line2D l : out) {
			g.drawLine((int) l.getX1(), (int) l.getY1(), (int) l.getX2(), (int) l.getY2());
		}
		g.setColor(Color.PINK);
		for (Line2D l : checkpoints) {
			g.drawLine((int) l.getX1(), (int) l.getY1(), (int) l.getX2(), (int) l.getY2());
		}
	}
}
