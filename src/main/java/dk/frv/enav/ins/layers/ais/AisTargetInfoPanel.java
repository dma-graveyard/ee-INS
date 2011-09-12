package dk.frv.enav.ins.layers.ais;

import dk.frv.ais.message.AisMessage;
import dk.frv.enav.ins.ais.VesselPositionData;
import dk.frv.enav.ins.ais.VesselStaticData;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.ais.VesselTarget.AisClass;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.gui.InfoPanel;

public class AisTargetInfoPanel extends InfoPanel {
	private static final long serialVersionUID = 1L;

	public AisTargetInfoPanel() {
		super();
	}
	
	public void showAisInfo(VesselTarget vesselTarget){
		VesselStaticData staticData = vesselTarget.getStaticData();
		String name = null;
		String callsign = null; 
		if(staticData != null){
			name = AisMessage.trimText(staticData.getName());
			callsign = AisMessage.trimText(staticData.getCallsign());
		}
		VesselPositionData positionData = vesselTarget.getPositionData();
		String cog = "N/A";
		String sog = "N/A";
		if(positionData != null){
			cog = Formatter.formatDegrees((double)positionData.getCog(), 0);
			sog = Formatter.formatSpeed((double)positionData.getSog());
		}
		
		StringBuilder str = new StringBuilder();
		str.append("<html>");
		if(name != null){
			str.append(name + " (" + vesselTarget.getMmsi() + ")");
		} else {
			str.append(vesselTarget.getMmsi());
		}
		if (vesselTarget.getAisClass() == AisClass.B) {
			str.append(" [<b>B</b>]");
		}
		str.append("<br/>");		
		
		if(callsign != null) {
			str.append(callsign + "<br/>");
		}
		str.append("COG " + cog + "  SOG " + sog + "<br/>");
		str.append("</html>");
		
		showText(str.toString());
	}
}
