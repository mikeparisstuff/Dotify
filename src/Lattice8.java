import java.awt.Color;
import java.awt.image.BufferedImage;


public class Lattice8 {

	Vertex[][] graph;
	double gradientSum;
	double darknessSum;
	
	
	/*
	 * Constructor to make an 8 connected Lattice with one Vertex node for each
	 * pixel in the image.
	 */
	public Lattice8( BufferedImage image) {
		//Creates a 2-d array of vertex objects
		graph = makeVertexs(image);
		//Creates an 8 connected lattice of vertex objects
		connectLattice();
		
		//Sets the importance factor for each
		setImportanceFactors();
		
	}

	
	/*
	 * Creates the 2-d array of Vertexes from the image using the intensity values given by
	 * any one of the  RGB values of the pixels.. R == G == B so no average is needed.
	 * For efficiency purposes, I am only looking every other pixel.. When it took in every
	 * pixel, it limited the size of the images I could process due to restraints on memory.
	 */
	private Vertex[][] makeVertexs(BufferedImage image) {
		Vertex[][] vertexs = new Vertex[image.getWidth()/2][image.getHeight()/2];
		for(int i = 0; i < image.getWidth()-1; i += 2) {
			for( int j = 0; j < image.getHeight()-1; j += 2) {
				Color c = new Color(image.getRGB(i, j));
				double intensity = ((double)c.getBlue()) / 255;
				Vertex v = new Vertex(intensity, i, j);
				vertexs[i/2][j/2] = v;
			}
		}
		return vertexs;
	}
	
	
	/*
	 * Return the graph of vertexes to be used in Dijkstra's theorem
	 */
	public Vertex[][] getGraph() {
		return graph;
	}
	
	private void setImportanceFactors() {
		for(int row = 0; row < graph.length; row++) {
			for(int col = 0; col < graph[row].length; col++) {
				//Set the gradient value for each node = gradient
				graph[row][col].setGradient(findGradient(graph[row][col]));
				
				//Set the gradient sum for the importance equilibrium
				graph[row][col].setGradientEquil(gradientSum);
				
				//Set the darkness sum for the importance equilibrium
				graph[row][col].setDarknessEquil(darknessSum);
			}
		}
	}
	
	private double getEdgeImportance(int x1, int y1, int x2, int y2) {
		return (( graph[x1][y1].getImportance() + graph[x2][y2].getImportance() ) / 2 );
	}
	
	
	/*
	 * Get the gradient value given a Vertex
	 */
	private double findGradient(Vertex v1) {
		double squaredsum = 0;
		//From north
		if( v1.n != null ) {
			squaredsum += (v1.n.getDarkness() - v1.getDarkness()) * (v1.n.getDarkness() - v1.getDarkness());
		}
		//From NE
		if( v1.ne != null ) {
			squaredsum += (v1.ne.getDarkness() - v1.getDarkness()) * (v1.ne.getDarkness() - v1.getDarkness());
		}
		//From E
		if( v1.e != null ) {
			squaredsum += (v1.e.getDarkness() - v1.getDarkness()) * (v1.e.getDarkness() - v1.getDarkness());
		}
		//From SE
		if( v1.se != null ) {
			squaredsum += (v1.se.getDarkness() - v1.getDarkness()) * (v1.se.getDarkness() - v1.getDarkness());
		}
		//From S
		if( v1.s != null ) {
			squaredsum += (v1.s.getDarkness() - v1.getDarkness()) * (v1.s.getDarkness() - v1.getDarkness());
		}
		//From SW
		if( v1.sw != null ) {
			squaredsum += (v1.sw.getDarkness() - v1.getDarkness()) * (v1.sw.getDarkness() - v1.getDarkness());
		}
		//From W
		if( v1.w != null ) {
			squaredsum += (v1.w.getDarkness() - v1.getDarkness()) * (v1.w.getDarkness() - v1.getDarkness());
		}
		//From NW
		if( v1.nw != null ) {
			squaredsum += (v1.nw.getDarkness() - v1.getDarkness()) * (v1.nw.getDarkness() - v1.getDarkness());
		}
		return Math.sqrt(squaredsum);
	}
	
	
	/*
	 * Go through 2d array and connect each vertex to its 8 neighbors if they exist
	 */
	private void connectLattice() {
		// A double to hold the sums of all gradients in the graph
		double gradSum = 0;
		//A double to hold the sums of all the darknesses in the graph
		double darkSum = 0;
		
		for(int row = 0; row < graph.length; row++) {
			for(int col = 0; col < graph[row].length; col++) {
				
				//If in first row, do not connect northward
				if(! (row==0) ) {
					//Connect North
					(graph[row][col]).n = (graph[row-1][col]);
				}
				//If in first row or last column, do not connect ne
				if(! (row==0 || col==graph[row].length-1)) {
					//Connect NE
					(graph[row][col]).ne = (graph[row-1][col+1]);
				}
				//If in last col, do not connect east
				if(! (col == graph[row].length-1)) {
					//Connect E
					(graph[row][col]).e = (graph[row][col+1]);
				}
				//If in last row, or last col do not connect SE
				if(! ((row == graph.length-1) || (col == graph[row].length-1)) ) {
					//Connect SE
					(graph[row][col]).se = (graph[row+1][col+1]);
				}
				//If in the last row, do not connect south
				if(! (row == graph.length-1)) {
					//Connect S
					(graph[row][col]).s = (graph[row+1][col]);
				}
				//If in the first col or the last row, do not connect SW
				if(! ((row == graph.length-1) || (col == 0))) {
					//Connect SW
					(graph[row][col]).sw = (graph[row+1][col-1]);
				}
				//If in the first column, do not connect West
				if(! (col == 0)) {
					//Connect W
					(graph[row][col]).w = (graph[row][col-1]);
				}
				//If in first row or first column, do not connect NW
				if(! ((row == 0) || (col == 0))) {
					//Connect NW
					(graph[row][col]).nw = (graph[row-1][col-1]);
				}
				
				
				// Iterate to get the gradient sum and the darkness sum for equilization
				gradSum += findGradient(graph[row][col]);;
				darkSum += graph[row][col].getDarkness();
				
			}
		}
		
		//Set the fields of Lattice8 to hold the gradient and darkness sums
		this.gradientSum = gradSum;
		this.darknessSum = darkSum;
	}
	

	public double getGradientSum() {
		return gradientSum;
	}

	public double getDarknessSum() {
		return darknessSum;
	}
	
	/*
	 * Getters to give graph width and height
	 */
	public int getWidth() {
		return graph.length;
	}
	
	public int getHeight() {
		return graph[1].length;
	}

	@Override
	public String toString() {
		String res = "";
		for(int row = 0; row < graph.length; row++) {
			for(int col = 0; col < graph[row].length; col++) {
				res += graph[row][col].getDarkness() + " ";
			}
			res += "\n";
		}
		return res;
	}
	
	
}
