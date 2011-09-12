package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.ins.msi.MsiHandler;

public class MsiAcknowledge extends JMenuItem implements IMapMenuAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MsiHandler msiHandler = null;
	private MsiMessage msiMessage;

	public MsiAcknowledge(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		msiHandler.setAcknowledged(msiMessage);
	}

	public void setMsiHandler(MsiHandler msiHandler) {
		this.msiHandler = msiHandler;
	}

	public void setMsiMessage(MsiMessage msiMessage) {
		this.msiMessage = msiMessage;
		
	}
}
