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
package dk.frv.enav.ins.gui.ais;

import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.ais.SarTarget;
import dk.frv.enav.ins.common.Heading;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.common.util.Calculator;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.gps.GpsData;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.gui.MainFrame;

public class SartDetailsDialog extends JDialog implements Runnable {
	private static final long serialVersionUID = 1L;

	private JLabel detailsLbl;
	private SarTarget sarTarget;
	private MainFrame mainFrame;
	private GpsHandler gpsHandler;

	public SartDetailsDialog(MainFrame mainFrame, SarTarget sarTarget, GpsHandler gpsHandler) {
		super();
		this.mainFrame = mainFrame;
		this.sarTarget = sarTarget;
		this.gpsHandler = gpsHandler;
		setResizable(false);
		setTitle("SART details");
		setSize(275, 130);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setAlwaysOnTop(true);
		setLocationRelativeTo(mainFrame);

		initGui();

		showDetails();
		
		position();

		(new Thread(this)).start();
	}

	private void showDetails() {
		StringBuilder str = new StringBuilder();
		Date now = GnssTime.getInstance().getDate();
		Date lastReceived = sarTarget.getLastReceived();
		Date firstReceived = sarTarget.getFirstReceived();
		long elapsedLast = now.getTime() - lastReceived.getTime();
		long elapsedFirst = now.getTime() - firstReceived.getTime();
		str.append("<html><b>AIS SART - MMSI " + sarTarget.getMmsi() + "</b><br/>");
		
		GeoLocation sarPos = null;
		if (sarTarget.getPositionData() != null) {
			sarPos = sarTarget.getPositionData().getPos();
		}
		
		if (sarPos != null) {
			str.append(Formatter.latToPrintable(sarPos.getLatitude()) + " ");
			str.append(Formatter.lonToPrintable(sarPos.getLongitude()) + "<br/>");
		}

		str.append("Last reception  " + Formatter.formatTime(elapsedLast) + " [" + Formatter.formatLongDateTime(lastReceived)
				+ "]<br/>");
		str.append("First reception " + Formatter.formatTime(elapsedFirst) + " [" + Formatter.formatLongDateTime(firstReceived)
				+ "]<br/>");
		Double dst = null;
		Double hdg = null;
		Long ttg = null;
		Date eta = null;
		if (gpsHandler != null) {
			GpsData gpsData = gpsHandler.getCurrentData();
			if (gpsData != null && !gpsData.isBadPosition()) {
				GeoLocation pos = gpsData.getPosition();				
				if (pos != null && sarPos != null) {
					dst = Calculator.range(pos, sarPos, Heading.RL);
					hdg = Calculator.bearing(pos, sarPos, Heading.RL);
					if (gpsData.getSog() != null && gpsData.getSog() > 1) {
						ttg = Math.round((dst / gpsData.getSog()) * 60 * 60 * 1000);
						eta = new Date(now.getTime() + ttg);
					}
				}
			}
		}
		str.append("RNG " + Formatter.formatDistNM(dst, 2) + " - BRG " + Formatter.formatDegrees(hdg, 0) + "<br/>");
		str.append("TTG " + Formatter.formatTime(ttg) + " - ETA " + Formatter.formatLongDateTime(eta));

		str.append("</html>");

		detailsLbl.setText(str.toString());
	}

	private void initGui() {
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		detailsLbl = new JLabel("");
		detailsLbl.setHorizontalAlignment(SwingConstants.LEFT);
		detailsLbl.setVerticalAlignment(SwingConstants.TOP);
		getContentPane().add(detailsLbl);		
	}
	
	private void position() {
		validate();
		Rectangle rect = mainFrame.getBounds();
		int x = (int)rect.getX() + 20;
		int y = (int)rect.getY() + 70;
		setLocation(x, y);		
		setVisible(true);
	}

	@Override
	public void run() {
		while (true) {
			EeINS.sleep(5000);
			showDetails();
		}
	}
}
