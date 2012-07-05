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
	public static Color clickedColor = new Color(123, 123, 123);
	public static Color standardColor = new Color(173, 173, 173);
	public static Color borderColor =  new Color(45, 45, 45);
	
	public ButtonLabel(String text){
		super(text);
		styleButton(this);
	}
	
	
	public void styleButton(final JLabel label){

		label.setPreferredSize(new Dimension(80, 25));
		label.setFont(defaultFont);
		label.setForeground(textColor);
		label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,borderColor));
		label.setBackground(standardColor);
		label.setOpaque(true);
		
		label.setHorizontalAlignment(JLabel.CENTER);

		label.addMouseListener(new MouseAdapter() {  
		    public void mousePressed(MouseEvent e) {
		    	if (label.isEnabled()){
		    	label.setBackground(clickedColor);
		    	}
		    }
			
		    public void mouseReleased(MouseEvent e) {
		    	if (label.isEnabled()){
		    		
		    	label.setBackground(standardColor);
		    	}
		    }
		});
	}

	
}
