
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.*;  
import java.awt.event.*;  
import javax.swing.JButton;
import javax.swing.SwingUtilities;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author HP
 */
public class Transform extends JFrame implements MouseListener, ActionListener
{
    FFT fft = null;
    int width; int height;
    int count = 0;
    JFrame frame3 = new JFrame();
    public void actionPerformed(ActionEvent e)
    {
        applyInverse();
    }
    public void mouseClicked(MouseEvent e) 
    {
        count++;
        Point point = e.getPoint();
        int x = (int) point.getX();
        int y = (int) point.getY();
        System.out.println(x - 5);
        System.out.println(y-35);
        x = x-5;
        y = y - 35;
        int region = 1;
        int inputSize = fft.intermediate.size/2;
        System.out.println("Size is" + inputSize);
        if((x<=inputSize) && (y<=inputSize))
            region = 1;
        else if((x>inputSize) && (y<inputSize))
            region = 2;
        else if((x<=inputSize) && (y>=inputSize))
            region = 3;
        else if((x>inputSize) && (y>inputSize))
            region = 4;       
        filter(fft.intermediate, x, y, 20, region);
        
    }  
    public void mouseEntered(MouseEvent e) {}  
    public void mouseExited(MouseEvent e) {  }  
    public void mousePressed(MouseEvent e) {}  
    public void mouseReleased(MouseEvent e) {}  
    
     public Transform()
     {
          this.addMouseListener(this);
     }
    public static void main(String[] args)
    {
        Transform test = new Transform();
        test.doIt();
    }
    
    public void doIt()
    {
        Image img = null;
        try 
        {
            img = ImageIO.read(new File("noisykidkid.jpg"));
        } 
        catch (IOException e) 
        {
        }
//          img = img.getScaledInstance(256, 256, Image.SCALE_SMOOTH);
//        JFrame frame = new JFrame();
//        ImageIcon imgIcon = new ImageIcon(img);
//        JLabel label = new JLabel();
//        label.setIcon(imgIcon);
//        frame.getContentPane().add(label, BorderLayout.CENTER);
//        frame.pack();
//        frame.setSize(600, 600);
//        frame.setVisible(true);
        
        width = img.getWidth(null);
        height = img.getHeight(null);
        
        int[] orig = new int[width*height];
        PixelGrabber grabber = new PixelGrabber(img, 0, 0, width, height, orig, 0, width);
		try {
			grabber.grabPixels();
		}
		catch(InterruptedException e2) {
			System.out.println("error: " + e2);
		}
        fft = new FFT();
        fft.progress = 0;
        fft = new FFT(orig, width, height);
//        fft.intermediate = FreqFilter.filter(fft.intermediate, true, 38);
        int size = 20;
        JButton button = new JButton("Apply IFT");
        button.addActionListener(this);
        Image foutput = createImage(new MemoryImageSource(width, height, fft.getPixels(), 0, width));
        ImageIcon imgIcon = new ImageIcon(foutput);
        JLabel label = new JLabel();
        label.setIcon(imgIcon);
        this.getContentPane().add(label, BorderLayout.NORTH);
        this.pack();
        this.setSize(600, 600);
        this.setVisible(true);
        this.getContentPane().add(button, BorderLayout.SOUTH);
        
//        while(start)
//        {
//            fft.intermediate = FreqFilter.filter2(fft.intermediate, 90, 100, size, 1);
//            fft.intermediate = FreqFilter.filter2(fft.intermediate, 155, 108, size, 2);
//            fft.intermediate = FreqFilter.filter2(fft.intermediate, 90, 141, size, 3);
//            fft.intermediate = FreqFilter.filter2(fft.intermediate, 155, 141, size, 4);
//        }
        
//        JFrame frame3 = new JFrame();
//        Image foutput3 = createImage(new MemoryImageSource(width, height, fft.getPixels(), 0, width));
//        ImageIcon imgIcon3 = new ImageIcon(foutput3);
//        JLabel label3 = new JLabel();
//        label3.setIcon(imgIcon3);
//        frame3.getContentPane().add(label3, BorderLayout.NORTH);
//        frame3.pack();
//        frame3.setSize(600, 600);
//        frame3.setVisible(true);
        
//        InverseFFT inverse = new InverseFFT();
//        TwoDArray output = fft.intermediate;
        
        
        
////        TwoDArray output = inverse.transform(fft.output);					
//	final Image invFourier = createImage(new MemoryImageSource(output.width, output.height, inverse.getPixels(output), 0, output.width));
        
//        ComplexNumber[][] values = output.values;
        
        
//        TwoDArray output2 = inverse.transform(fft.intermediate);				
//	final Image invFourier = createImage(new MemoryImageSource(output2.width, output2.height, inverse.getPixels(output2), 0, output2.width));
//        
//        
//        
//        JFrame frame2 = new JFrame();
//        ImageIcon imgIcon2 = new ImageIcon(invFourier);
//        JLabel label2 = new JLabel();
//        label2.setIcon(imgIcon2);
//        frame2.getContentPane().add(label2);
//        frame2.pack();
//        frame2.setSize(600, 600);
//        frame2.setVisible(true);
    }
    
    public void filter(TwoDArray input, int x, int y, int size, int region)
    {      
        fft.intermediate = FreqFilter.filter2(input, x, y, size, region);
    }
    
    public void applyInverse()
    {
        Image foutput3 = createImage(new MemoryImageSource(width, height, fft.getPixels(), 0, width));
        ImageIcon imgIcon3 = new ImageIcon(foutput3);
        JLabel label3 = new JLabel();
        label3.setIcon(imgIcon3);
        frame3.remove(label3);
        frame3.getContentPane().add(label3, BorderLayout.NORTH);
        frame3.pack();
        frame3.setSize(600, 600);
        frame3.setVisible(true);
        
        InverseFFT inverse = new InverseFFT();
        TwoDArray output = fft.intermediate;
        
        TwoDArray output2 = inverse.transform(fft.intermediate);				
	final Image invFourier = createImage(new MemoryImageSource(output2.width, output2.height, inverse.getPixels(output2), 0, output2.width));
           
        JFrame frame2 = new JFrame();
        ImageIcon imgIcon2 = new ImageIcon(invFourier);
        JLabel label2 = new JLabel();
        label2.setIcon(imgIcon2);
        frame2.getContentPane().add(label2);
        frame2.pack();
        frame2.setSize(600, 600);
        frame2.setVisible(true);
        System.out.println("Hello");
    }
    
    public BufferedImage convertToImage(int[][] data)
    {
        int height = data.length;
        int width = data[0].length;
        int[][] pixels = new int[height][width];
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                pixels[i][j] = data[i][j] * 0x10101;
            }
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                image.setRGB(x, y, pixels[x][y]);
        return image;
    }
}
