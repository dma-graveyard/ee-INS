package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.gui.TopPanel;
import dk.frv.enav.ins.layers.msi.MsiSymbolGraphic;

public class MsiDetails extends JMenuItem implements IMapMenuAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TopPanel topPanel;
	private MsiSymbolGraphic msiSymbolGraphic;

	public MsiDetails(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		if (topPanel != null && topPanel.getMsiDialog() != null) {
			topPanel.getMsiDialog().showMessage(msiSymbolGraphic.getMsiMessage().getMessageId());
		}
	}

	public void setTopPanel(TopPanel topPanel) {
		this.topPanel = topPanel;
	}
	
	public void setMsiSymbolGraphic(MsiSymbolGraphic msiSymbolGraphic) {
		this.msiSymbolGraphic = msiSymbolGraphic;
	}
	
}
