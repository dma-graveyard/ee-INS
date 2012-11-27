package dk.frv.enav.ins.layers.ais;

import javax.swing.ImageIcon;


/**
 * MSI mouse over info
 */
public class HighlightInfoPanel extends InfoPanel {

	private static final long serialVersionUID = 1L;
	
	public HighlightInfoPanel() {
		super(new ImageIcon("images/ais/highlight.png"));
	}

	/**
	 * Show the image
	 */
	public void displayHighlight(int x, int y){
		setPos(x, y);
		showImage();
	}

}
