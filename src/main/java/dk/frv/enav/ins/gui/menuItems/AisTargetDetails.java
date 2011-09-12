package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class AisTargetDetails extends JMenuItem implements IMapMenuAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AisTargetDetails(String text) {
		super();
		this.setText(text);
	}
	
	@Override
	public void doAction() {
		JOptionPane.showMessageDialog(null, "To be implemented...");
	}

}
