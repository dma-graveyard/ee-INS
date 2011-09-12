package dk.frv.enav.ins.layers.ais;

import com.bbn.openmap.omGraphics.OMRect;
import com.bbn.openmap.proj.Projection;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.ais.AisTarget;
import dk.frv.enav.ins.ais.AtoNTarget;

public class AtonTargetGraphic extends TargetGraphic {

	private static final long serialVersionUID = 1L;
	
	private OMRect rect = new OMRect(0, 0, 0, 0, 10, 10);
	
	public AtonTargetGraphic() {
		super();
		add(rect);
	}
	
	@Override
	public void update(AisTarget aisTarget) {
		AtoNTarget atonTarget = (AtoNTarget)aisTarget;
		GeoLocation pos = atonTarget.getPos();
		float lat = (float)pos.getLatitude();
		float lon = (float)pos.getLongitude();
		
		rect.setLocation(lat, lon, -5, -5, 5, 5);
		 
	}

	@Override
	public void setMarksVisible(Projection projection) {
		
		
	}

}
