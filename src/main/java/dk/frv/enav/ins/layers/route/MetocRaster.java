package dk.frv.enav.ins.layers.route;

import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.common.graphics.CenterRaster;

public abstract class MetocRaster extends OMGraphicList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MetocRaster() {
		super();
		setVague(true);
	}
	
	/**
	 * Places the raster correctly.
	 * Requirements for the raster:
	 * -Width and height must be uneven
	 * -If image is asymmetric (eg. an arrow with origin in center of image), 
	 * an equal amount of empty pixels (+1) must exist on the opposite side of the image
	 * @param rasterURI Location of the raster
	 * @param lat GeoLocation of the raster's vertical center
	 * @param lon GeoLocation of the raster's horizontal center
	 * @param angle Rotational angle in radians
	 */
	public void addRaster(String rasterURI, double lat, double lon, double angle){
		ImageIcon imageIcon = new ImageIcon(EeINS.class.getResource(rasterURI));
		int imageWidth = imageIcon.getIconWidth();
		int imageHeight = imageIcon.getIconHeight();
		CenterRaster rasterMark = new CenterRaster(lat, lon, imageWidth, imageHeight, imageIcon);
//		rasterMark.setStroke(new BasicStroke());
//		rasterMark.setSelected(true);
		rasterMark.setRotationAngle(angle);
		add(rasterMark);
	}
	
}
