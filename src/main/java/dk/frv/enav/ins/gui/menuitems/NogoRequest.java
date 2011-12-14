package dk.frv.enav.ins.gui.menuitems;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.gui.MainFrame;
import dk.frv.enav.ins.gui.SetupDialog;
import dk.frv.enav.ins.gui.TopPanel;
import dk.frv.enav.ins.gui.nogo.NogoDialog;
import dk.frv.enav.ins.nogo.NogoHandler;
import dk.frv.ais.geo.GeoLocation;

public class NogoRequest extends JMenuItem implements IMapMenuAction , Runnable{
	private static final long serialVersionUID = 1L;
	private NogoHandler nogoHandler = null;
	private MainFrame mainFrame = null;
	private AisHandler aisHandler = null;
	
	public void setAisHandler(AisHandler aisHandler){
		this.aisHandler = aisHandler;
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public void setNogoHandler(NogoHandler nogoHandler) {
		this.nogoHandler = nogoHandler;
	}

	public NogoRequest(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		//JOptionPane.showMessageDialog(null, "To be implemented... - Request sent");
		//nogoHandler.updateNogo();
		//nogoHandler.setNogoRequest(true);
//		topPanel.activateNogoButton();

		
		NogoDialog nogoDialog = new NogoDialog(mainFrame, nogoHandler, aisHandler);
		nogoDialog.setVisible(true);
		
		
		
		//(new Thread(this)).start();
		//Request the handler to get info from land, and handle that somehow...
	}
	
	@Override
	public void run() {
//		nogoHandler.setNorthWestPoint(new GeoLocation(55.070, 11.668));
//		nogoHandler.setSouthEastPoint(new GeoLocation(55.170, 11.868));
//		nogoHandler.updateNogo();
		
//		NogoDialog nogoDialog = new NogoDialog(mainFrame, nogoHandler, aisHandler);
//		nogoDialog.setVisible(true);
	}
	

	
}

