/*
 * Copyright 2011 Danish Maritime Authority. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Authority ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Authority.
 * 
 */
package dk.frv.enav.ins.util;

import java.awt.Desktop;
import java.net.URI;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.common.HttpRequest;

/**
 * Thread to check for new version
 */
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
