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
import dk.frv.enav.ins.ais.SartGraphic;
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
