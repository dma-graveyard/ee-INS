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
package dk.frv.enav.ins.layers.ais;

import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.proj.Projection;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.ais.AisTarget;
import dk.frv.enav.ins.ais.SarTarget;
import dk.frv.enav.ins.ais.VesselPositionData;

public class SarTargetGraphic extends TargetGraphic {
	private static final long serialVersionUID = 1L;

	private SarTarget sarTarget;

	private SartGraphic newSartMark;
	private SartGraphic oldSartMark;
	private Font font = new Font(Font.SANS_SERIF, Font.BOLD, 11);
	private OMText label = new OMText(0, 0, 0, 0, "", font, OMText.JUSTIFY_CENTER);
	private boolean warningIssued = false;

	@Override
	public void update(AisTarget aisTarget) {
		sarTarget = (SarTarget) aisTarget;
		VesselPositionData posData = sarTarget.getPositionData();
		// VesselStaticData staticData = sarTarget.getStaticData();
		GeoLocation pos = posData.getPos();

		double lat = pos.getLatitude();
		double lon = pos.getLongitude();

		if (size() == 0) {
			createGraphics();
		}
		
		if (sarTarget.isOld()) {
			oldSartMark.setVisible(true);
			newSartMark.setVisible(false);
			oldSartMark.setLat(lat);
			oldSartMark.setLon(lon);
		} else {
			newSartMark.setVisible(true);
			oldSartMark.setVisible(false);
			newSartMark.setLat(lat);
			newSartMark.setLon(lon);
		}

		label.setLat(lat);
		label.setLon(lon);
		label.setY(30);
		label.setData("AIS SART");
		int result = 1;
		if (!warningIssued) {
			result = JOptionPane.showOptionDialog(EeINS.getMainFrame(), "AIS SART transponder active! Zoom to location?",
					"AIS SART Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
		}
		if (result == JOptionPane.YES_OPTION) {
			EeINS.getMainFrame().getChartPanel().getMap().setCenter(lat, lon);
		}
		warningIssued = true;
	}

	private void createGraphics() {
		ImageIcon newSartIcon = new ImageIcon(EeINS.class.getResource("/images/ais/aisSart.png"));
		newSartMark = new SartGraphic(0, 0, newSartIcon.getIconWidth(), newSartIcon.getIconHeight(), newSartIcon, this);
		add(newSartMark);
		newSartMark.setVisible(false);

		ImageIcon oldSartIcon = new ImageIcon(EeINS.class.getResource("/images/ais/aisSartOld.png"));
		oldSartMark = new SartGraphic(0, 0, oldSartIcon.getIconWidth(), oldSartIcon.getIconHeight(), oldSartIcon, this);
		add(oldSartMark);
		oldSartMark.setVisible(false);
		
		add(label);
	}

	@Override
	public void setMarksVisible(Projection projection) {

	}
	
	public SarTarget getSarTarget() {
		return sarTarget;
	}

}
