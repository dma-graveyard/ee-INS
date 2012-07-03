package dk.frv.enav.ins.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class ButtonLabel extends JLabel{

	private static final long serialVersionUID = 1L;
	public static Font defaultFont = new Font("Arial", Font.PLAIN, 11);
	public static Color textColor = new Color(237, 237, 237);
	
	public ButtonLabel(String text){
		super(text);
		styleButton(this);
	}
	
	
	public static void styleButton(final JLabel label){

		label.setPreferredSize(new Dimension(80, 25));
//		generalSettings.setSize(new Dimension(76, 30));
		label.setFont(defaultFont);
		label.setForeground(textColor);
		//label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(45, 45, 45)));
		label.setBackground(new Color(128, 128, 128));
		label.setOpaque(true);
		
		label.setHorizontalAlignment(JLabel.CENTER);

		label.addMouseListener(new MouseAdapter() {  
		    public void mousePressed(MouseEvent e) {
		    	
		    	if (label.isEnabled()){
		    	
		    	label.setBackground(new Color(168, 168, 168));
		    	
		    	}
		    }
			
		    public void mouseReleased(MouseEvent e) {
		    	label.setBackground(new Color(128, 128, 128));
		    }
		});
	}

	
}
