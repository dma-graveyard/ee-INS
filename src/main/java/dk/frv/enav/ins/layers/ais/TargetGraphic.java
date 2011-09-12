package dk.frv.enav.ins.layers.ais;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Projection;

import dk.frv.enav.ins.ais.AisTarget;

public abstract class TargetGraphic extends OMGraphicList {

	private static final long serialVersionUID = 1L;
	
	public TargetGraphic() {
		//setVague(true);
	}
		
	public abstract void update(AisTarget aisTarget);

	public abstract void setMarksVisible(Projection projection);
}
