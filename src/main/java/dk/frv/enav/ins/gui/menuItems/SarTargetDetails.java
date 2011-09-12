package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.ais.SarTarget;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.gui.MainFrame;
import dk.frv.enav.ins.gui.ais.SartDetailsDialog;

public class SarTargetDetails extends JMenuItem implements IMapMenuAction {
	private static final long serialVersionUID = 1L;
	
	private MainFrame mainFrame;
	private GpsHandler gpsHandler;
	private SarTarget sarTarget;

	public SarTargetDetails(String text) {
		super();
		this.setText(text);
	}
	
	@Override
	public void doAction() {
		 new SartDetailsDialog(mainFrame, sarTarget, gpsHandler);
	}
	
	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	
	public void setGpsHandler(GpsHandler gpsHandler) {
		this.gpsHandler = gpsHandler;
	}
	
	public void setSarTarget(SarTarget sarTarget) {
		this.sarTarget = sarTarget;
	}

}
