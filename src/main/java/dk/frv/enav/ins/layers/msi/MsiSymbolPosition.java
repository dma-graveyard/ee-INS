package dk.frv.enav.ins.layers.msi;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.msi.MsiLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.common.xml.msi.MsiPoint;

public abstract class MsiSymbolPosition extends OMGraphicList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected MsiMessage msiMessage;
	protected boolean acknowledged;

	public MsiSymbolPosition(MsiMessage msiMessage, boolean acknowledged) {
		super();
		this.msiMessage = msiMessage;
		this.acknowledged = acknowledged;
		
		MsiLocation msiLocation = msiMessage.getLocation();
		
		// Determine where to place MSI symbols
		switch (msiLocation.getLocationType()) {
		case POINT:
		case POINTS:
			/*
			 * Place symbol in each point 
			 */
			for (MsiPoint point : msiLocation.getPoints()) {
				createSymbol(new GeoLocation(point.getLatitude(), point.getLongitude()));
			}			
			break;
		case POLYGON:
			/*
			 * Place symbol in center of polygon
			 */
			createSymbol(msiLocation.getCenter());
			break;
		case POLYLINE:
			/*
			 * Place a symbol in middle point 
			 */
			MsiPoint middle =  msiLocation.getPoints().get(msiLocation.getPoints().size() / 2);
			createSymbol(new GeoLocation(middle.getLatitude(), middle.getLongitude()));
			break;
		default:
			break;
		}
	}

	public abstract void createSymbol(GeoLocation geoLocation);
	
	public MsiMessage getMsiMessage() {
		return msiMessage;
	}
}
