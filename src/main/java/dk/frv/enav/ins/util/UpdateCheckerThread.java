package dk.frv.enav.ins.util;

import java.awt.Desktop;
import java.net.URI;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.common.HttpRequest;

public class UpdateCheckerThread extends MapHandlerChild implements Runnable {
	
	private static final Logger LOG = Logger.getLogger(UpdateCheckerThread.class);
	
	private HttpRequest httpRequest;
	private Desktop desktop;
	private Boolean newVersion = null;
	private String newestVersion = null;
	private boolean hasNotified = false;
	
	public UpdateCheckerThread() {		
		if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
		(new Thread(this)).start();
	}
	
	public void run(){
		while (true) {
			// Sleep 10 secs
			EeINS.sleep(10000);
			
			// Get the newest version
			getNewestVersion();
			
			// Maybe notify
			if (newVersion != null && newVersion && !hasNotified) {
				notifyNewVersion();
			}
			
			// Sleep 6 hours
			EeINS.sleep(6 * 60 * 60 * 1000);
		}
	}
	
	private void notifyNewVersion() {
		int choice = JOptionPane.showConfirmDialog(EeINS.getMainFrame(), 
				"A newer version is available.\nDo you want to close the application and open the download website?", 
				"Version " + newestVersion + " available", 
				JOptionPane.YES_NO_OPTION);
		hasNotified = true;
		if(choice == JOptionPane.YES_OPTION){
			if (desktop.isSupported(Desktop.Action.BROWSE)){
				try {
					desktop.browse(new URI(EeINS.getSettings().getEnavSettings().getUpdateServer()));
				} catch (Exception e) {
					LOG.error("Failed to open browser with new version: " + e.getMessage());
					return;
				}
		    }
			EeINS.sleep(1000);
			EeINS.closeApp();
		}
	}
	
	private void getNewestVersion() {
		newVersion = null;
		httpRequest = new HttpRequest("/eeins/version.txt", EeINS.getSettings().getEnavSettings());
		httpRequest.init();
        try {
			httpRequest.makeRequest();
			newestVersion = new String(httpRequest.getResponseBody());
			if(new Float(newestVersion) > new Float(EeINS.getVersion())){
				newVersion = true;				
			} else {
				newVersion = false;
			}
		} catch (Exception e) {
			LOG.error("Failed to get newest version number: " + e.getMessage());
		}
	}
}
