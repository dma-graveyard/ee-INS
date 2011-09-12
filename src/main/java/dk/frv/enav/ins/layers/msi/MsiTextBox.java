package dk.frv.enav.ins.layers.msi;

import java.awt.Color;
import java.awt.Font;
import java.util.Locale;

import com.bbn.openmap.omGraphics.OMRect;
import com.bbn.openmap.omGraphics.OMText;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.ins.common.StringTools;

public class MsiTextBox extends MsiSymbolPosition {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MsiTextBox(MsiMessage msiMessage, boolean acknowledged) {
		super(msiMessage, acknowledged);
	}
	
	public void createSymbol(GeoLocation pos) {	
		// Make text box with ENC text if now acknowledged
		if (acknowledged || msiMessage.getEncText() == null || msiMessage.getEncText().length() == 0) {
			return;
		}

		// TODO Make rounded box and wrap text as described in spec
		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 10);
		OMText label = new OMText(0, 0, -160, -40, "", font, OMText.JUSTIFY_LEFT);
		label.setLat(pos.getLatitude());
		label.setLon(pos.getLongitude());
		//label.setY(20);
		label.setTextMatteColor(Color.WHITE);
		String msiMessageString = msiMessage.getEncText();
		StringTools st = new StringTools();
		String msiMessageStringWrapped = st.wordWrap(msiMessageString, 23, Locale.ENGLISH);		
		label.setData(msiMessageStringWrapped);
		add(label);
		OMRect msiBox = new OMRect(pos.getLatitude(), pos.getLongitude(), -15, -15, -170, -60);
		msiBox.setFillPaint(Color.white);
		add(msiBox);
	}
}
