package dk.frv.enav.ins.layers.msi;

import java.awt.Image;
import java.awt.Toolkit;

import com.bbn.openmap.omGraphics.OMRaster;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.ins.EeINS;

public class MsiSymbolGraphic extends MsiSymbolPosition {
	private static final long serialVersionUID = 1L;
	
	public MsiSymbolGraphic(MsiMessage msiMessage) {
		super(msiMessage, false);
		setVague(true);
	}
	
	public void createSymbol(GeoLocation pos) {
		Image msiSymbolImage = Toolkit.getDefaultToolkit().getImage(EeINS.class.getResource("/images/msi/msi_symbol_32.png"));
		OMRaster msiSymbol = new OMRaster(pos.getLatitude(), pos.getLongitude(), -16, -16, msiSymbolImage);
		//msiSymbol.scaleTo(32, 32, OMRasterObject.SMOOTH_SCALING);
		add(msiSymbol);
		
		// TODO Consider using OMScalingIcon
	}
	
}
