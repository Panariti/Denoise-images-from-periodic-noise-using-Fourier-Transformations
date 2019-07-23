import java.awt.*;
import java.awt.image.*;
import java.applet.*;
import java.net.*;
import java.io.*;
import java.lang.Math;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JApplet;
import javax.imageio.*;
import javax.swing.event.*;



public class fourierDemo extends JApplet {
	
	Image edgeImage, accImage, outputImage;
	MediaTracker tracker = null;
	PixelGrabber grabber = null;
	int width = 0, height = 0;
	String fileNames[] = {"noisykid.jpg", "noisykid.jpg", "ImageB.jpg", "drawing.png", "film.png"};

	javax.swing.Timer timer;
	static final int RADIUS_MIN = 5;
	static final int RADIUS_MAX = 100;
	static final int RADIUS_INIT = 100;
	int radius=RADIUS_INIT;

	int imageNumber=0;
	static int progress=0;
	public int orig[] = null;
	public float origFloat[] = null;
	public FFT fft = new FFT();
	public InverseFFT inverse = new InverseFFT();
	
	Image image[] = new Image[fileNames.length];
	
	JProgressBar progressBar;
	JPanel controlPanel,filterPanel, imagePanel, progressPanel;
	JLabel origLabel, outputLabel,filterLabel,fourierLabel,comboLabel,radiusLabel,processing;
	JSlider radiusSlider;
	JRadioButton highRadio, lowRadio;
	JComboBox imSel;
	ButtonGroup radiogroup;
	boolean lowpass = true;
	 
	 
	   	// Applet init function	
	public void init() {
		
		tracker = new MediaTracker(this);
		for(int i = 0; i < fileNames.length; i++) {
			image[i] = getImage(this.getCodeBase(),fileNames[i]);
			image[i] = image[i].getScaledInstance(512, 512, Image.SCALE_SMOOTH);
			tracker.addImage(image[i], i);
		}
		try {
			tracker.waitForAll();
		}
		catch(InterruptedException e) {
			System.out.println("error: " + e);
		}
		
		Container cont = getContentPane();
		cont.removeAll();
		cont.setBackground(Color.black);
		cont.setLayout(new BorderLayout());
		
		controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(2,3,15,0));
		controlPanel.setBackground(new Color(192,204,226));
		imagePanel = new JPanel();
		imagePanel.setBackground(new Color(192,204,226));
		progressPanel = new JPanel();
		progressPanel.setBackground(new Color(192,204,226));
		progressPanel.setLayout(new GridLayout(2,1));
		filterPanel = new JPanel();
		filterPanel.setBackground(new Color(192,204,226));
		filterPanel.setLayout(new GridLayout(2,1));

		comboLabel = new JLabel("IMAGE");
		comboLabel.setHorizontalAlignment(JLabel.CENTER);
		controlPanel.add(comboLabel);
		
		filterLabel = new JLabel("FILTER");
		filterLabel.setHorizontalAlignment(JLabel.CENTER);
		controlPanel.add(filterLabel);
	
		radiusLabel = new JLabel("RADIUS = "+RADIUS_INIT);
		radiusLabel.setHorizontalAlignment(JLabel.CENTER);
		controlPanel.add(radiusLabel);


		processing = new JLabel("Processing...");
		processing.setHorizontalAlignment(JLabel.LEFT);
		progressBar = new JProgressBar(0,100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true); //get space for the string
        progressBar.setString("");          //but don't paint it
		progressPanel.add(processing);
		progressPanel.add(progressBar);
		
		width = image[imageNumber].getWidth(null);
		height = image[imageNumber].getHeight(null);

		imSel = new JComboBox(fileNames);
		imageNumber = imSel.getSelectedIndex();
		imSel.addActionListener( 
			new ActionListener() {  
				public void actionPerformed(ActionEvent e) {
					imageNumber = imSel.getSelectedIndex();
					origLabel.setIcon(new ImageIcon(image[imageNumber]));	
					processImage();
				}
			}
		);
		controlPanel.add(imSel, BorderLayout.PAGE_START);

        timer = new javax.swing.Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                progressBar.setValue((fft.getProgress() + inverse.getProgress()) / 4);
	
            }
        });

		origLabel = new JLabel("Original Image", new ImageIcon(image[imageNumber]), JLabel.CENTER);
		origLabel.setVerticalTextPosition(JLabel.BOTTOM);
		origLabel.setHorizontalTextPosition(JLabel.CENTER);
		origLabel.setForeground(Color.blue);
		imagePanel.add(origLabel);
		
		fourierLabel = new JLabel("Fourier Transform", new ImageIcon(image[imageNumber]), JLabel.CENTER);
		fourierLabel.setVerticalTextPosition(JLabel.BOTTOM);
		fourierLabel.setHorizontalTextPosition(JLabel.CENTER);
		fourierLabel.setForeground(Color.blue);
		imagePanel.add(fourierLabel);
		
		outputLabel = new JLabel("Inverse Fourier", new ImageIcon(image[imageNumber]), JLabel.CENTER);
		outputLabel.setVerticalTextPosition(JLabel.BOTTOM);
		outputLabel.setHorizontalTextPosition(JLabel.CENTER);
		outputLabel.setForeground(Color.blue);
		imagePanel.add(outputLabel);
	

		highRadio = new JRadioButton("High Pass");
    	highRadio.setActionCommand("high");
		highRadio.setBackground(new Color(192,204,226));
		highRadio.setHorizontalAlignment(SwingConstants.CENTER);
		lowRadio = new JRadioButton("Low Pass");
    	lowRadio.setActionCommand("low");
		lowRadio.setHorizontalAlignment(SwingConstants.CENTER);
		lowRadio.setBackground(new Color(192,204,226));
    	lowRadio.setSelected(true);
	    radiogroup = new ButtonGroup();
	    radiogroup.add(lowRadio);
	    radiogroup.add(highRadio);
	    lowRadio.addActionListener(new radiolistener());
	    highRadio.addActionListener(new radiolistener());
		filterPanel.add(highRadio);
		filterPanel.add(lowRadio);
		controlPanel.add(filterPanel);

		radiusSlider = new JSlider(JSlider.HORIZONTAL, RADIUS_MIN, RADIUS_MAX, RADIUS_INIT);
		radiusSlider.addChangeListener(new radiusListener());
		radiusSlider.setMajorTickSpacing(10);
		radiusSlider.setMinorTickSpacing(2);
		radiusSlider.setPaintTicks(true);
		radiusSlider.setPaintLabels(true);
		radiusSlider.setBackground(new Color(192,204,226));
		controlPanel.add(radiusSlider);

		cont.add(controlPanel, BorderLayout.NORTH);
		cont.add(imagePanel, BorderLayout.CENTER);
		cont.add(progressPanel, BorderLayout.SOUTH);

		processImage();

	}
	class radiolistener implements ActionListener{
	    public void actionPerformed(ActionEvent e) {

	    	String mode = e.getActionCommand();

	    	if(mode == "high") {
	    		lowpass = false;

	    	} else {
		    	lowpass = true;
		    }
			processImage();
	    }
	
	}

	class radiusListener implements ChangeListener {
	    public void stateChanged(ChangeEvent e) {
	        JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting()) {
				System.out.println("radius="+source.getValue());
				radius=source.getValue();
				radiusLabel.setText("RADIUS = "+source.getValue());
				processImage();
	        }    
	    }
	}

	private void processImage(){
		fft.progress = 0;
				
		inverse.progress = 0;	
		
		orig=new int[width*height];
		PixelGrabber grabber = new PixelGrabber(image[0], 0, 0, width, height, orig, 0, width);
		try {
			grabber.grabPixels();
		}
		catch(InterruptedException e2) {
			System.out.println("error: " + e2);
		}
		
		
		origFloat = new float[width * height];
		
		for(int i = 0; i < width * height;  i++) {
			
			origFloat[i] = (float)orig[i];
		}
		
		progressBar.setMaximum(width);

		processing.setText("Processing...");

		radiusSlider.setEnabled(false);
		imSel.setEnabled(false);
		highRadio.setEnabled(false);
		lowRadio.setEnabled(false);
		
		timer.start();
		new Thread()
                {
			public void run(){
				
				fft = new FFT(orig, width, height);
				fft.intermediate = FreqFilter.filter(fft.intermediate, 256, 15);
//                                fft.intermediate = FreqFilter.filter2(fft.intermediate, 100, 100);
//                                int[] test = new int[fft.getPixels().length];
//                                test = fft.getPixels();
//                                for(int i = 0; i < test.length; i++)
//                                {
//                                    System.out.println(test[i]);
//                                }
				final Image foutput = createImage(new MemoryImageSource(width, height, fft.getPixels(), 0, width));
//                                 JFrame frame = new JFrame();
//                                 ImageIcon imgIcon = new ImageIcon(foutput);
//                                 JLabel label = new JLabel();
//                                 label.setIcon(imgIcon);
//                                 frame.getContentPane().add(label, BorderLayout.CENTER);
//                                 frame.pack();
//                                 frame.setSize(600,600);
//                                 frame.setVisible(true);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						
						TwoDArray output = inverse.transform(fft.intermediate);
						
						final Image invFourier = createImage(new MemoryImageSource(output.width, output.height, inverse.getPixels(output), 0, output.width));
						outputLabel.setIcon(new ImageIcon(invFourier));	
						fourierLabel.setIcon(new ImageIcon(foutput));	
						processing.setText("Done");
					
						radiusSlider.setEnabled(true);
						imSel.setEnabled(true);
						highRadio.setEnabled(true);
						lowRadio.setEnabled(true);
					}
				});
			}
		}.start();
	}

}