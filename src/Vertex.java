import java.util.ArrayList;
import java.util.List;


public class Vertex {
	public Vertex n, ne, e, se, s, sw, w, nw;
	int x;
	int y;
	final private String position;
	
	private double darkness;
	private double gradient;
	private double gradientEquil;
	private double darknessEquil;
	double distance;
	
	
	/*
	 * Constructor:
	 * intensity: The double value found from 255/r where r is any 
	 * 		of the red, green, or blue attributes of the grey_scale color
	 * 
	 */
	public Vertex(double intensity, int x, int y) {
		darkness = 1 - intensity;
		this.x = x;
		this.y = y;
		darknessEquil = 1;
		gradientEquil = 1;
		position = Integer.toString(x) + Integer.toString(y);
		distance = Double.MAX_VALUE;
		
		//Begin with all initialized to null and fill in connections in the Lattice8 constructor
		n = null;
		ne = null;
		e = null;
		se = null;
		s = null;
		sw = null;
		w = null;
		nw = null;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if( obj instanceof Vertex ) {
			Vertex v = (Vertex)obj;
			return ( this.x == v.x && this.y == v.y);
		}else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int result = 1;
		result = 31*result + position.hashCode();
//		String hash = position;
//		while( hash.length() > 1) {
//			result = 31 * result + hash.charAt(0);
//			hash = hash.substring(1);
//		}
		return result;
	}
	

	//Getters and Setters
	public double getDarkness() {
		return darkness;
	}


	public void setDarkness(double darkness) {
		this.darkness = darkness;
	}


	public double getGradient() {
		return gradient;
	}


	public void setGradient(double gradient) {
		this.gradient = gradient;
	}


	public double getGradientEquil() {
		return gradientEquil;
	}


	public void setGradientEquil(double gradientEquil) {
		this.gradientEquil = gradientEquil;
	}


	public double getDarknessEquil() {
		return darknessEquil;
	}


	public void setDarknessEquil(double darknessEquil) {
		this.darknessEquil = darknessEquil;
	}
	
	public double getImportance() {
		return ((darkness / darknessEquil) + (gradient / gradientEquil));
	}
	
	

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

//	@Override
//	public int compareTo(Object o) {
//		if( o instanceof Vertex ) {
//			Vertex v = (Vertex)o;
//			if (this.getDistance() - v.getDistance() > 0) {
//				return -1;
//			}
//			else if(this.x == v.x && this.y == v.y ) {
//				return 0;
//			}
//			else {
//				return 1;
//			}
//		}
//		else {
//			return -1;
//		}
//	}
	
	

}
