import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;


public class Dotify extends JFrame implements Dijkstra.StippleListener, Canvas.canvasListener {

	private JPanel contentPane;
	private Image image;
	private Dimension size;
	private BufferedImage gray;
	private ArrayList<Vertex> stipples;
	private int iw = 500;
	private int ih = 500;
	private String imagePath;
	private JTextPane pathText;
	private Canvas panel;
	private ProgressMonitor pMonitor;
	private int numDots;
	private BufferedImage in;
	private JCheckBox chckbxColor;
	private JComboBox comboBox;
	private JLabel lblNumberOfDots;
	private JCheckBox chckbxConnectTheDots;
	private JComboBox comboBoxSize;
	private JLabel lblDotSize;
	private BackgroundTask bTask;
	private JCheckBox chckbxWatchLive;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Dotify frame = new Dotify();
					frame.setLocationRelativeTo(null);
					frame.setLocation(0, 0);
					frame.pack();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initializes all GUI elements in the Application
	 */
	public Dotify() {
		setTitle("Dotify");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		
		JButton btnRun = new JButton("Dotify");
		menuBar.add(btnRun);
		
		pathText = new JTextPane();
		pathText.setText("Select an Image");
		menuBar.add(pathText);
		
		gray = null;
		
		String[] numDots = {"A few", "Some More", "A lot"};
		
		lblDotSize = new JLabel("Dot Size");
		menuBar.add(lblDotSize);
		
		String[] sa = {"small", "medium", "large"};
		comboBoxSize = new JComboBox(sa);
		menuBar.add(comboBoxSize);
		
		lblNumberOfDots = new JLabel("Number of dots:");
		menuBar.add(lblNumberOfDots);
		comboBox = new JComboBox(numDots);
		menuBar.add(comboBox);
		
		chckbxColor = new JCheckBox("Color");
		menuBar.add(chckbxColor);
		
		chckbxWatchLive = new JCheckBox("Watch Live!");
		menuBar.add(chckbxWatchLive);
		
		JButton btnUploadImage = new JButton("Choose Image");
		btnUploadImage.setHorizontalAlignment(SwingConstants.RIGHT);
		menuBar.add(btnUploadImage);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		panel = new Canvas(this);
		contentPane.add(panel);
		btnUploadImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Handle the update button clicked event
				uploadButtonClicked(e);
			}
		});
		btnRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Create the Lattice and draw it to the jPanel..
				if( gray != null ) {
					//Run the application
					
					pMonitor = new ProgressMonitor(Dotify.this, 
							"Do you know how many pixels are in this image?",
							null, 0, 100);
					pMonitor.setProgress(0);
					bTask = new BackgroundTask();
					bTask.addPropertyChangeListener(new PropertyChangeListener(){
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if (pMonitor.isCanceled() || bTask.isDone()) {
				                if (pMonitor.isCanceled()) {
				                    bTask.cancel(true);
				                }
				            }
						}
					});
					bTask.execute();
				}
				else {
					JOptionPane.showMessageDialog(contentPane,
						    "Please choose an image first!",
						    "I need an Image!",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	
	}

	/*
	 * Handles creating the dot image and puts in on screen
	 */
	public void dotify() {
		Lattice8 graph = new Lattice8(gray);
		iw = graph.getWidth();
		ih = graph.getHeight();

		//Normalize the number of dots used in images with different overall darknesses
		double dark = graph.getDarknessSum();
		int num = 1;
		String item = (String)comboBox.getSelectedItem();
		if( dark <= 100000 ) { 
			num = 1;
		}else if( dark > 100000 ) {
			num = 2;
		}else if (dark > 200000) {
			num = 3;
		}else if( dark > 300000) {
			num = 4;
		}else if( dark > 400000) {
			num = 5;
		}else if( dark > 500000) {
			num = 6;
		}
		
		
		//Allow the user to choose how many dots to generate
		if( item.equals("A few"))
			num *= 5;
		else if( item.equals("Some More"))
			num *= 3;
		else if( item.equals("A lot")) 
			num *= 1;
		
		int dSize = 1;
		if( comboBoxSize.getSelectedItem().equals("small")) {
			panel.setDotSize(1);
		}
		else if( comboBoxSize.getSelectedItem().equals("medium")) {
			panel.setDotSize(2);
		}
		else if( comboBoxSize.getSelectedItem().equals("large")){
			panel.setDotSize(3);
		}

		//Dijkstra Difficulties
		Dijkstra dijkstra = new Dijkstra(gray.getWidth(), gray.getHeight(), num/dark, panel, chckbxWatchLive.isSelected());
		Vertex[][] lattice = graph.getGraph();

		dijkstra.DijkExecute(lattice[0][0]);
		if(! chckbxWatchLive.isSelected() ) {
			stipples = dijkstra.getStipples();
			panel.drawImage(this.getDimensions()[0], this.getDimensions()[1], stipples);
		}
	}
	
	/*
	 * Toggle the numdots JComboBox
	 * To connect the dots, we use a specified level of dots to improve peformance
	 */
	private void toggleNumDots() {
		if( comboBox.isEnabled() )
			comboBox.setEnabled(false);
		else
			comboBox.setEnabled(true);
	}
	
	
	/*
	 * Handles the uploading of images
	 */
	private void uploadButtonClicked(ActionEvent evt) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showOpenDialog(null);
		File mFile = fileChooser.getSelectedFile();
		try {
			imagePath = mFile.toString();
			String[] imageName = imagePath.split("/");
			pathText.setText(imageName[imageName.length -1]);
		}catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		//If the file name is null then error getting file and return
		if( imagePath == null ) {
			return;
		}

		
		//Create the gray-scale version of the image
		in = null;
		try {
			in = ImageIO.read(mFile);
			
			//Scale the image to half size to fit on screen
			int w = in.getWidth()/2;
			int h = in.getHeight()/2;
			BufferedImage scaled = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = scaled.createGraphics();
			g.drawImage(in, 0, 0,w, h, null);
			g.dispose();
			panel.drawImageOnUpload(scaled);
		}catch(IOException e) {
			e.printStackTrace();
		}
		catch(NullPointerException e) {
			promptForValidImage();
		}
		if( in != null) {
			gray = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		}
		else {
			System.out.println("Error getting image");
		}
		
		/*
		 * Draws the Image into the BufferedImage
		 */
		if( gray != null ) {
			Graphics g = gray.getGraphics();
			g.drawImage(in, 0,0, null);
	
			//Release resources
			g.dispose();
		}
	}
	
	private void promptForValidImage() {
		JOptionPane.showMessageDialog(panel, "Please choose a valid image file","Need a valid image.", JOptionPane.INFORMATION_MESSAGE);
		pathText.setText("Choose an image");
	}
	
	
	/*
	 * For optional color attribute, will get the real color of the pixel in the original 
	 * and will draw its corresponding dot using that color
	 */
	public Color getColor(int x, int y)
	{
		if( in != null)
		{
			Color c = new Color(in.getRGB(x, y));
			return c;
		}
		return null;
	}
	
	/*
	 * Return true if the color JCheckBox is check and false otherwise
	 */
	public boolean isColorMode()
	{
		return chckbxColor.isSelected();
	}
	
	/*
	 * Resize the jFrame using the dimensions given
	 */
	public void resize()
	{
		this.pack();
		this.repaint();
		panel.reDraw();
	}
	
	/*
	 * Getters for the final images width and height
	 */
	public int[] getDimensions() {
		int[] a = new int[2];
		a[0] = iw;
		a[1] = ih;
		return a;
	}

	/*
	 * Code here that will handle the drawing of the dots in live time
	 * OBSOLETE
	 */
	public void paintStipple(Vertex v) {
//		panel.paintStipple(v);
	}
	
	class BackgroundTask extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			dotify();
			return null;
		}
		
		@Override
		public void done() {
			System.out.println("Done");
			panel.clearStipples();
		}
		
	}
	
}
