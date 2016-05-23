
public class Edge {
	private double xDiff;
	private double yDiff;
 
 public Edge(double[] p1, double[] p2){
	 //this.xDiff = Math.max(p1[0], p2[0]) - Math.min(p1[0], p2[0]);
	 //this.yDiff = Math.max(p1[1], p2[1]) - Math.min(p1[1], p2[1]);
	 
	 this.xDiff = p1[0] - p2[0];
	 this.yDiff = p1[1] - p2[1];
	 System.out.println("Diffs: " + xDiff + " " + yDiff);
 }
 

 //returns perpendicular value
	 public double[] perp(){
		 double[] perp = {xDiff, yDiff - (yDiff *2)};
		 return perp;
	 }
	 
//gets angle of edge
	 public double angleRad(){
		 return Math.atan2(yDiff, xDiff);
	 }
//gets angle of edge in degrees
	 public double angleDeg(){
		 return Math.toDegrees(angleRad());
	 }

	public double perpAngleDeg(){
		return Math.toDegrees(Math.atan2(yDiff - (yDiff * 2), xDiff));
				
	}
}
