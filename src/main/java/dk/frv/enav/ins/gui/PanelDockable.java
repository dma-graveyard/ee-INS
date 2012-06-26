package dk.frv.enav.ins.gui;


import java.awt.Color;

import javax.swing.JPanel;

import bibliothek.gui.dock.DefaultDockable;

public class PanelDockable extends DefaultDockable{
	private JPanel panel;
	
	public PanelDockable( String title, Color color ){
		
	}
	
	public PanelDockable( String title, Color color, float brightness ){
		setTitleText( title );

		if( brightness != 1.0 ){
			float[] hsb = Color.RGBtoHSB( color.getRed(), color.getGreen(), color.getBlue(), null );
			
			hsb[1] = Math.min( 1.0f, hsb[1] / brightness );
			hsb[2] = Math.min( 1.0f, hsb[2] * brightness );
			
			color = Color.getHSBColor( hsb[0], hsb[1], hsb[2] );
		}

		setColor( color );
	}
	
	public void setColor( Color color ){
		if( panel == null ){
			panel = new JPanel();
			panel.setOpaque( true );
			add( panel );
		}
		
		panel.setBackground( color );

	}
	
	public Color getColor(){
		return panel.getBackground();
	}
}
