package dk.frv.enav.ins.ais;

import javax.swing.ImageIcon;

import dk.frv.enav.ins.common.graphics.CenterRaster;
import dk.frv.enav.ins.layers.ais.SarTargetGraphic;

public class SartGraphic extends CenterRaster {
	private static final long serialVersionUID = 1L;
	
	private SarTargetGraphic sarTargetGraphic;

	public SartGraphic(double lat, double lon, int i, int j, ImageIcon imageIcon, SarTargetGraphic sarTargetGraphic) {
		super(lat, lon, i, j, imageIcon);
		this.sarTargetGraphic = sarTargetGraphic;
	}
	
	public SarTargetGraphic getSarTargetGraphic() {
		return sarTargetGraphic;
	}

}
