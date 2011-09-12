package dk.frv.enav.ins.layers.ais;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.enav.ins.common.graphics.RotationalPoly;

public class VesselTargetTriangle extends OMGraphicList {
	private static final long serialVersionUID = 1L;
	
	private RotationalPoly vessel;
	private Paint paint = new Color(74, 97, 205, 255);
	private Stroke stroke = new BasicStroke(2.0f);
	private VesselTargetGraphic vesselTarget;
	
	public VesselTargetTriangle() {
		int[] vesselX = {0,5,-5,0};
		int[] vesselY = {-10,5,5,-10};
		vessel = new RotationalPoly(vesselX, vesselY, stroke, paint);
		add(vessel);
		this.setVague(true);
	}
	
	public void update(double lat, double lon, int units, double heading, VesselTargetGraphic vesselTarget){
		this.vesselTarget = vesselTarget;
		vessel.setLocation(lat, lon, units, heading);
	}
	
	public VesselTargetGraphic getVesselTargetGraphic() {
		return vesselTarget;
	}
}
