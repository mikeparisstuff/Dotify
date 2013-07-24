import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;



public class Canvas extends JPanel implements Dijkstra.StippleListener{
	private ArrayList<Vertex> stipples;
	private Vertex stip;
	private int prefX;
	private int prefY;
	private canvasListener cCallback;
	private BufferedImage image;
	private boolean firstTime;
	private int dotSize;

	public Canvas(canvasListener application) {
		stipples = new ArrayList<Vertex>();
        setBorder(BorderFactory.createLineBorder(Color.black));
        firstTime = true;
        stip = null;
        prefX = 500;
        prefY = 500;
        
        //By Default
        dotSize = 1;
        try {
        	cCallback = (canvasListener)application;
        } catch( ClassCastException e) {
        	e.printStackTrace();
        }
    }
	
	/*
	 * Interface to allow callback to get color
	 */
	public interface canvasListener {
		public Color getColor(int x, int y);
		public boolean isColorMode();
		public void resize();
	}
	
	public void drawImageOnUpload(BufferedImage image)
	{
		this.image = image;
		prefX = image.getWidth();
		prefY = image.getHeight();
		this.setSize(prefX, prefY);
		cCallback.resize();
	}
	
	public void reDraw()
	{
		repaint();
	}
	
	public void drawImage(int width, int height, ArrayList<Vertex> stipples) {
		System.out.println("About to draw stipples");
		this.stipples = stipples;
		image = null;
		repaint();
	}
	
	public void paintStipple(Vertex v) {
		stipples.add(v);
		image = null;
		repaint();
		try {
			Thread.sleep(8);
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

    public Dimension getPreferredSize() {
        return new Dimension(prefX,prefY);
    }

    public void clearStipples() {
    	stipples.clear();
    }
    
    public void setDotSize(int size) {
    	dotSize = size;
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);       

        // Draw Text
        Graphics2D graphics = (Graphics2D)g;
        if( image != null ) {
        	graphics.drawImage(image, 0, 0, null);
        }
        
        if( firstTime ) {
        	String s = "Dotify...";
        	graphics.drawString(s, 50, 50);
        	firstTime = false;
        }
        
        
        try {
	        Color c; 
	        if( image == null ) {
		        if( cCallback.isColorMode() ) {
					if( stipples != null ) {
						for( Vertex v : stipples ) {
							c = cCallback.getColor(v.x, v.y);
							graphics.setColor(c);
							graphics.fillRect(v.x/2, v.y/2, dotSize, dotSize);
						}
					}
		        }
		        else {
		        	c = Color.black;
		        	if( stipples != null ) {
						for( Vertex v : stipples ) {
							graphics.setColor(c);
							graphics.fillRect(v.x/2, v.y/2, dotSize, dotSize);
						}
					}
		        }
	        }
        }
        catch( ConcurrentModificationException e ) {
		}
    }  

}
