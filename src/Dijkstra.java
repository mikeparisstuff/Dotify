import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


public class Dijkstra {
	
	private int imageWidth;
	private int imageHeight;
	
	//Sets to hold the unknown and known vertexs
	/**
	 * A set of the known vertices.  Implemented as a hash to improve performance on accesses
	 */
	private Set<Vertex> knowns;
	/**
	 * Used by the modified Dijkstra's algorithm to determine where to place stipples.
	 * When the path length being calculated surpasses that of the threshold, a stipple is
	 * placed and the distance of that new vertex is set to 0 so that the algorithm will then
	 * radiate out from there.
	 */
	private double threshold;
	/**
	 * A heap to hold the unknown elements that gives priority to nodes of smaller distances
	 */
	private PriorityQueue<Vertex> uHeap;
	/**
	 * A heap that holds the same contents as the uHeap, but gives priority to elements with
	 * higher gradient values.  The gradient value helps the algorithm to find the edges
	 * of pictures which is where we would ideally place stipples.
	 */
	private PriorityQueue<Vertex> gHeap;
	private StippleListener mCallback;
	private boolean isLive;
	
	//The variation of Dijkstra's algorithm requires that when the 
	//threshhold is passed that you draw a dot and recall dijkstra's
	//starting at that point.. points will hold all the points to draw
	//after the finish of the algorithm
	private ArrayList<Vertex> points;
	
	public Dijkstra(int iw, int ih, double thresh, StippleListener callback, boolean isLive) {
		int mapSize = (int)(iw*ih*1.5);
		imageWidth = iw;
		imageHeight = ih;
//		unknowns = new HashSet<Vertex>(iw*ih);
		knowns = new HashSet<Vertex>(mapSize);
		threshold = thresh;
//		distance = new HashMap<Vertex, Double>(mapSize);
		points = new ArrayList<Vertex>();
		this.isLive = isLive;
		try {
			mCallback = (StippleListener)callback;
		}catch (ClassCastException e) {
			throw new ClassCastException( "App must implement StippleListener");
		}
		uHeap = new PriorityQueue<Vertex>(10000, new Comparator<Vertex>() {
			@Override
			public int compare(Vertex o1, Vertex o2) {
				int result = 0;
					if( o1.getDistance() < o2.getDistance() ) {
						result = -1;
					}
					else if( o1.getDistance() == o2.getDistance() ) {
						result = 0;
					}
					else {
						result = 1;
					}
				
				return result;
			}
		});
		gHeap = new PriorityQueue<Vertex>(10000, new Comparator<Vertex>() {
			@Override
			public int compare(Vertex o1, Vertex o2) {
				int result = 0;
				if( o1.getGradient() < o2.getGradient()) {
					result =  1;
				}
				else if( o1.getGradient() == o2.getGradient() ) {
					result = 0;
				}
				else {
					result = -1;
				}
				return result;
			}
		});
		
	}
	
	//Interface to allow live drawing of the dots
	public interface StippleListener {
		public void paintStipple(Vertex v);
	}
	
	public void DijkExecute(Vertex s) {
		Vertex v;
		setCurrDist(s, 0.0);
		
		uHeap.add(s);
		gHeap.add(s);
//		unknowns.add(s);
		
		//While there are unknown vertices
		while( uHeap.size() > 0 && gHeap.size() > 0 ) {    //unknowns.size() > 0 ) {
//			System.out.println("Execute Called");
//			v = getMin(unknowns);
//			System.out.println("Min: " + v.x + "," + v.y);
			v = uHeap.peek();
			knowns.add(v);
			
			
//			unknowns.remove(v);
			uHeap.remove();
			gHeap.remove();
			
			//For each adjacent node
			for( Vertex w : getAdjacent(v) ) {
//				System.out.println("Going through adjacents");
				if( ! isKnown(w) ) {
//					System.out.println("Current v: " + getCurrDist(v) + ", Current w: " + getCurrDist(w));
					if( getCurrDist(v) + getEdgeImportance(v,w) < getCurrDist(w)) {
						double newdist = getCurrDist(v) + getEdgeImportance(v,w);
						setCurrDist(w, newdist);
						uHeap.add(w);
						gHeap.add(w);
//						unknowns.add(w);
//						System.out.println("Adding to unknown");
						if( newdist >= threshold ) {
//							System.out.println("Adding Stipple");
							addNewStipple();
						}
					}
				}
			}
		}
	}
	
	
	//After the algorithm has run to completion, we should have a 
	//list of points that we need to draw to the canvas
	//return that list here
	/**
	 * Getter method to return the private list of stipples
	 * @return The stipple list generated from the Dijkstra algorithm
	 */
	public ArrayList<Vertex> getStipples() {
		return points;
	}

	/**
	 * Getter method to return the adjacent list of the Vertex parameter v
	 * @param v The Vertex of which to get the adjacent Vertexs
	 * @return The List<Vertex> of v's adjacent vertex
	 */
	private List<Vertex> getAdjacent(Vertex v) {
		ArrayList<Vertex> adjacent = new ArrayList<Vertex>();
		if( v.n != null && !isKnown(v.n)) {
			adjacent.add(v.n);
		}
		if( v.ne != null && !isKnown(v.ne)) {
			adjacent.add(v.ne);
		}
		if( v.e != null && !isKnown(v.e)) {
			adjacent.add(v.e);
		}
		if( v.se != null &&  !isKnown(v.se)) {
			adjacent.add(v.se);
		}
		if( v.s != null &&  !isKnown(v.s)) {
			adjacent.add(v.s);
		}
		if( v.sw != null &&  !isKnown(v.sw)) {
			adjacent.add(v.sw);
		}
		if( v.w != null && !isKnown(v.w) ) {
			adjacent.add(v.w);
		}
		if( v.nw != null && !isKnown(v.nw)) {
			adjacent.add(v.nw);
		}
		return adjacent;
	}
	
	
	//For both unknowns and keeping track of the elements with the
	//highest gradient values, I should later implement a heap...
	
	
	//When the max length exceeds a threshold, length we need 
	//to place a new stipple at the location of the node with
	//highest gradient value
	/**
	 * When the current path length exceeds the threshold, add the stipple of max gradient to the stipple list
	 * Uses a call to findMaxGradient() to get the top element in the gHeap. This returns the
	 * Vertex of highest gradient, and adds it to the stipple list, points.
	 * The distance of the new stipple is then set to 0 so that my modified Dijkstra's will 
	 * continue to flow out from that new point.
	 */
	private void addNewStipple() {
//		Vertex site = findMaxGradient(unknowns);
		Vertex site = findMaxGradient();
		points.add(site);
		
		if( isLive )
			mCallback.paintStipple(site);
		//Additionally we set the distance of the location site to 0
		//so that the next wave of Dijkstra's will start to flow out 
		//from this location
		setCurrDist(site, 0.0);
		uHeap.remove(site);
		uHeap.add(site);
	}
	
	/**
	 * This method will return the Vertex with the highest gradient value that we have seen.
	 * To improve efficiency, I implemented another heap, gHeap, that will keep vertex's of the 
	 * highest gradient value at the top.  Finding the maximum value, takes only a O(1) call to pop
	 * off the top of the heap.
	 * @return The vertex of highest gradient value that we have seen thus far.
	 */
	private Vertex findMaxGradient() {
		return gHeap.peek();
//		Vertex max = null;
//		for( Vertex v : unknowns ) {
//			if( max == null ) {
//				max = v;
//			}
//			else {
//				if ( v.getGradient() > max.getGradient() ) {
//					max = v;
//				}
//			}
//		}
//		return max;
	}
	
	/**
	 * Returns the importance (length) of the edge between Vertex v and Vertex w.
	 * Calculates the edge importance, by taking the average of the importance of the nodes
	 * on either side of the edge.
	 * @param v The first Vertex of the edge
	 * @param w The second Vertex of the edge
	 * @return A double value representing the importance (length) of the edge.
	 */
	private double getEdgeImportance(Vertex v, Vertex w) {
		return ( (v.getImportance() + w.getImportance()) / 2);
	}
	
	/**
	 * This method is obsolete after implementing the performance increasing solution of
	 * using a heap to keep track of the minimum distances which is an O(1) operation.
	 * The previous solution was O(n) where n was the number of elements in the unknown
	 * set.
	 * @param unknowns The set of unknown elements
	 * @return the element of lowest distance
	 */
	private Vertex getMin(Set<Vertex> unknowns) {
////		Vertex min = null;
////		for( Vertex v : unknowns ) {
////			if( min == null ) {
////				min = v;
////			}
////			else {
////				if ( getCurrDist(v) < getCurrDist(min) ) {
////					min = v;
////				}
////			}
////		}
////		return min;
		return null;
	}
	
	/**
	 * This method will set the current distance of the v parameter with the value supplied in dist
	 * @param v The vertex to set the new distance on
	 * @param dist The distance to set for the v vertex
	 */
	private void setCurrDist(Vertex v, double dist) {
		v.setDistance(dist);
	}
	
	//The first if checks to see if the distance has ever been initialized before
	//and if not it returns the max value of double as the INFINITY generally
	//used in Dijkstra's algorithm
	/**
	 * Return the currently set distance of the vertex v.
	 * @param v The vertex to get the current distance of
	 * @return a double representing the distance value of the vertex v
	 */
	private double getCurrDist(Vertex v) {
		return v.getDistance();
//		Double current = distance.get(v);
//		if( current == null ) {
//			return Double.MAX_VALUE;
//		}
//		else {
//			return current;
//		}
	}
	
	/**
	 * This method check to see if the Vertex V is currently in the set of known vertices.
	 * @param v The vertex to check its presence
	 * @return True if v is in the knowns set and false otherwise
	 */
	private boolean isKnown(Vertex v) {
		return knowns.contains(v);
	}
}
