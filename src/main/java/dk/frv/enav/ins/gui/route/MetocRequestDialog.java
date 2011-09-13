/*
 * Copyright 2011 Danish Maritime Safety Administration. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
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
 * either expressed or implied, of Danish Maritime Safety Administration.
 * 
 */
package dk.frv.enav.ins.gui.route;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import dk.frv.enav.common.xml.metoc.MetocForecast;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RoutesUpdateEvent;
import dk.frv.enav.ins.services.shore.ShoreServiceException;

public class MetocRequestDialog extends JDialog implements Runnable, ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private RouteManager routeManager;
	private Route route;
	private Window parent;
	private JLabel statusLbl;
	private JButton cancelBtn;
	private Boolean cancelReq = false;
	
	public MetocRequestDialog(Window parent, RouteManager routeManager, Route route) {
		super(parent, "Request METOC");
		this.routeManager = routeManager;
		this.route = route;
		this.parent = parent;
		
        initGui();		
	}
	
	public static void requestMetoc(Window parent, RouteManager routeManager, Route route) {
		MetocRequestDialog metocRequestDialog = new MetocRequestDialog(parent, routeManager, route);
		metocRequestDialog.doRequestMetoc();
		metocRequestDialog = null;
	}
	
	private void doRequestMetoc() {
		// Start thread
		(new Thread(this)).start();
		
		// Set dialog visible
		setVisible(true);
	}
	
	@Override
	public void run() {
		ShoreServiceException error = null;
		try {
			routeManager.requestRouteMetoc(route);
		} catch (ShoreServiceException e) {
			error = e;
		}
		
		if (isCancelReq()) {
			route.removeMetoc();
			routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_METOC_CHANGED);
			return;
		}
		
		if (error == null) {
			routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_METOC_CHANGED);
		}
		
		// Close dialog		
		setVisible(false);		
		
		// Give response		
		if (error != null) {
			String text = error.getMessage();
			if (error.getExtraMessage() != null) {
				text += ": " + error.getExtraMessage();
			}
			JOptionPane.showMessageDialog(parent, text, "Shore service error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			MetocForecast metocForecast = route.getMetocForecast();
			JOptionPane.showMessageDialog(parent, "Received " + metocForecast.getForecasts().size() + " METOC forecast points", "Shore service result",
					JOptionPane.INFORMATION_MESSAGE);
		}		
	}
	
	private void initGui() {
		setSize(280, 130);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        getContentPane().setLayout(null);
        
        cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(96, 58, 80, 23);
        getContentPane().add(cancelBtn);
        cancelBtn.addActionListener(this);
        
        statusLbl = new JLabel("Getting METOC from shore server ...");
        statusLbl.setHorizontalAlignment(SwingConstants.CENTER);
        statusLbl.setBounds(10, 23, 244, 14);
        getContentPane().add(statusLbl);
	}
	
	private boolean isCancelReq() {
		synchronized (cancelReq) {
			return cancelReq.booleanValue();
		}
	}
	
	private void setCancelReq(boolean cancel) {
		synchronized (cancelReq) {
			this.cancelReq = cancel;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelBtn) {
			setCancelReq(true);
			setVisible(false);
			route.removeMetoc();
			routeManager.notifyListeners(RoutesUpdateEvent.METOC_SETTINGS_CHANGED);
		}
	}

}
