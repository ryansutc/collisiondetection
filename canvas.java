import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JPanel;


public class Canvas extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private Graphics2D g2;
	private AffineTransform at = new AffineTransform();
	
	private int dx = 50;
	private int dy = 50;
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		g2 = (Graphics2D) g;
		
		
		//manipulate these values to test various types of intersections
		//need to explicitly close polygons, ie. last values same as first
		//int[] xArray = {0, 25, 25, 0};
		//int[] yArray = {0, 0, 50, 0};
		
		int[] xArray = {0, 15, 30, 25, 10, 0};
		int[] yArray = {30, 0, 30, 30, 10, 30};
		
		int[] xArray2 = {0, 15, 30, 0};
		int[] yArray2 = {0, 30, 10, 0};
		
		Polygon poly1 = new Polygon(xArray, yArray, xArray.length);
		poly1.translate(dx+15, dy+25);			
		g2.setColor(Color.blue);
		g2.fill(poly1);
		Path2D poly1p2d = new Path2D.Double(poly1); //create path from polygon to allow rotations
		poly1p2d.closePath();
		
		Polygon poly2 = new Polygon(xArray2, yArray2, xArray2.length);
		poly2.translate(dx,dy);
		g2.setColor(Color.red);
		g2.fill(poly2);
		Path2D poly2p2d = new Path2D.Double(poly2);
		poly2p2d.closePath();
		g2.setColor(Color.black);
		g2.draw(poly2p2d);
		
		at.rotate(Math.toRadians(0), dx, dy);
		poly1p2d.transform(at);
		poly2p2d.transform(at);
		
		//g2.setColor(Color.yellow);
		//g2.draw(poly1p2d);
		//System.out.println(poly1p2d.getBounds().getMinX() + " " + poly1p2d.getBounds().getMaxX());
		//getMinMax(poly1p2d);
		
		System.out.println(" Collide method returns a result of: " + collide(poly1p2d, poly2p2d));
	}
	private Set<Double> getAxes(Path2D poly1, Path2D poly2){
		Edge curedge;
		ArrayList<Double> perpedge = new ArrayList<Double>();
		
		double vertices[][] = getVertices(poly1);
		for (int i=0; i < vertices.length; i++){
			double[] p1 = {vertices[i][0], vertices[i][1]};
			double[] p2 = {vertices[i + 1 == vertices.length ? 0 : i + 1][0], 
					vertices[i + 1 == vertices.length ? 0 : i + 1][1]};
			System.out.println("Poly1 Edge (curEdge) from: " + p1[0] + " " + p1[1] + " to " + p2[0] + " " + p2[1]);
			if (p1[0] == p2[0] && p1[1] == p2[1]){
				continue; //don't bother with vertices that are actually points
			}
			curedge = new Edge(p1, p2);
			System.out.println("Edge " + i + " : " + curedge.angleDeg());
			perpedge.add(curedge.perpAngleDeg());
			
		}
		vertices = getVertices(poly2);
		for (int i=0; i < vertices.length; i++){
			double[] p1 = {vertices[i][0], vertices[i][1]};
			double[] p2 = {vertices[i + 1 == vertices.length ? 0 : i + 1][0], 
					vertices[i + 1 == vertices.length ? 0 : i + 1][1]};
			System.out.println("Poly2 Edge (curEdge) from: " + p1[0] + " " + p1[1] + " to " + p2[0] + " " + p2[1]);
			if (p1[0] == p2[0] && p1[1] == p2[1]){
				continue; //don't bother with vertices that are actually points
			}
			curedge = new Edge(p1, p2);
			System.out.println("Edge " + i + " : " + curedge.angleDeg());
			perpedge.add(curedge.perpAngleDeg());
			//perpedge.add(Math.toDegrees(curedge.angleRad(curedge.perp()[0], curedge.perp()[1])));
		}
		
		Set<Double> uniqAxes = new HashSet<Double>(perpedge);
		return uniqAxes;
	}
	
	public boolean collide(Path2D poly1, Path2D poly2){
		//Edge curedge;
		//double perpedge;
		//double vertices[][] = getVertices(poly1);
		//AffineTransform atOld = g2.getTransform();
		for (Double axis : getAxes(poly1, poly2)){
			
			System.out.println("Testing Axis: " + axis);
			at.rotate(Math.toRadians(axis), dx, dy);
			Path2D poly1Copy = new Path2D.Double(poly1);
			Path2D poly2Copy = new Path2D.Double(poly2);
			
			poly1Copy.transform(at);
			poly2Copy.transform(at); //needed?
			//g2.transform(at);
			g2.setColor(Color.yellow);
			g2.draw(poly1Copy);
			g2.draw(poly2Copy);
//			try {
//			    Thread.sleep(1000);                 //1000 milliseconds is one second.
//			} catch(InterruptedException ex) {
//			    Thread.currentThread().interrupt();
//			}			
			if (!axisIntersect(poly1Copy, poly2Copy)){ //shapes dont intersect if 1 axis does not		
				System.out.println("they dont intersect");
				return false;
			}
			else {
				System.out.println("they intersect");
			}
			//poly1Copy.reset();
			
			//g2.setTransform(atOld);
		}
		//all axises intersected.
		
		return true;
	}
	
	public double[][] getVertices(Path2D poly){
		
		int vCnt = countVertices(poly);
		double[][] vertices = new double[vCnt][vCnt]; //this will need optimization for non-set length array
		double[] coordinates = new double[6]; //each segment has 6 properties which are temp stored here
		int i = 0;
		
		PathIterator pi = poly.getPathIterator(null);
		while (!pi.isDone()) {

			if (pi.currentSegment(coordinates) == PathIterator.SEG_LINETO ||
					pi.currentSegment(coordinates) == PathIterator.SEG_MOVETO){

				vertices[i][0] = (Double) coordinates[0];
				vertices[i][1] = (Double) coordinates[1];
			}
			i++;
			pi.next();
		}
		return vertices;
	}
	
	//count vertices so we know how large to create array to store them
	private int countVertices(Path2D poly){
		PathIterator pi = poly.getPathIterator(null);
		double[] coords = new double[6]; //each segment has 6 properties temp stored here
		int i = 0;
		while (!pi.isDone()) {
			
			if (pi.currentSegment(coords) == PathIterator.SEG_LINETO ||
					pi.currentSegment(coords) == PathIterator.SEG_MOVETO){
					i++;
			}

			pi.next();
		}
		return i;
	}
	
	public double[] getMinMax(Path2D poly){
		double minX = poly.getBounds().getMinX();
		double maxX = poly.getBounds().getMaxX();
		double minY = poly.getBounds().getMinY();
		double maxY = poly.getBounds().getMaxY();
		//System.out.println("Bounds X: " + minX + " " + maxX);
		System.out.println("Bounds Y: " + minY + " " + maxY);
		double[] extent = {minX, maxX, minY, maxY};
		return extent;
	}
	

	
	//idea based on youtube video here: https://www.youtube.com/watch?v=NZHzgXFKfuY
	private boolean rangeIntersect(double min0, double max0, double min1, double max1){
		return Math.max(min0, max0) > Math.min(min1, max1) &&
		Math.min(min0, max0) < Math.max(min1, max1);
		
	}

	//note that for axis comparison we only care about y values?
	private boolean axisIntersect(Path2D poly1, Path2D poly2){
		//return rangeIntersect(r0.y, r0.y + r0.getWidth(), r1.y, r1.y + r1.getWidth());
		
		double[] poly1Extents = getMinMax(poly1); //0 = xmin, 1=xmax, 2=ymin, 3=ymax
		//System.out.println("xmin = " + poly1Extents[0] + " xmax = " + poly1Extents[1]);
		double[] poly2Extents = getMinMax(poly2);
		return rangeIntersect(poly1Extents[2], poly1Extents[3], poly2Extents[2], poly2Extents[3]); //2, 3 miny, maxy
		
	}

	//test only. Test rotate
	public void drawLine(){
		Line2D myline = new Line2D.Double(90,90,180,90);
		Path2D mylineP2d = new Path2D.Double(myline);
		
		g2.draw(mylineP2d);
		rotateLine(mylineP2d, myline.getP1());
		rotateLine(mylineP2d, myline.getP1());
		rotateLine(mylineP2d, myline.getP1());
		rotateLine(mylineP2d, myline.getP1());

	}
	public void rotateLine(Path2D line, Point2D point){
		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians(45),point.getX(), point.getY());
		line.transform(at);
		g2.setColor(Color.magenta);
		g2.draw(line);
		line.reset();
		
		
	}
}
