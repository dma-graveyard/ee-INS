package dk.frv.enav.ins.gui.menuitems;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class NogoRequest extends JMenuItem implements IMapMenuAction {
	private static final long serialVersionUID = 1L;

	public NogoRequest(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		JOptionPane.showMessageDialog(null, "To be implemented...");
		
		//Request the handler to get info from land, and handle that somehow...
	}


	
}

