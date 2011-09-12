package dk.frv.enav.ins.layers.msi;

import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.ins.gui.InfoPanel;

public class MsiInfoPanel extends InfoPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MsiInfoPanel() {
		super();
	}
	
	public void showMsiInfo(MsiMessage message) {
		String encText = message.getEncText();
		showText(encText);
	}
}
