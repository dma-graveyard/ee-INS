package dk.frv.enav.ins.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class RotationalChart extends JPanel{

	
	  @Override
	  public void paintComponent(Graphics g){
	         Graphics2D g2d=(Graphics2D)g; // Create a Java2D version of g.
	         g2d.translate(1150, -500); // Translate the center of our coordinates.
	         g2d.rotate(1);  // Rotate the image by 1 radian.
	    }
	
}
