package dk.frv.enav.ins.status;

import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.gps.GpsData;

public class GpsStatus extends ComponentStatus {
	
	private GpsData currentData;

	public GpsStatus(GpsData currentData) {
		super("GPS");
		this.currentData = currentData;
		if (!currentData.isBadPosition()) {
			setStatus(ComponentStatus.Status.OK);
			setShortStatusText("Position OK");
			return;
		}
		long elapsed = System.currentTimeMillis() - currentData.getLastUpdated().getTime();
		if (elapsed > 10000) {
			setStatus(ComponentStatus.Status.ERROR);
			setShortStatusText("No GPS data");
			return;
		}
		setStatus(ComponentStatus.Status.PARTIAL);
		setShortStatusText("Position unknown");
	}
	
	@Override
	public String getStatusHtml() {
		StringBuilder buf = new StringBuilder();
		buf.append("Position: " + status.name() + "<br/>");
		buf.append("Last GPS data: " + Formatter.formatLongDateTime(currentData.getLastUpdated()));
		return buf.toString();
	}

}
