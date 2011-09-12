package dk.frv.enav.ins.gps;

import java.util.Date;

public class GnssTimeMessage {
	
	private Date time;
	
	public GnssTimeMessage() {
		
	}
	
	public GnssTimeMessage(Date time) {
		this.time = time;
	}
	
	public Date getTime() {
		return time;
	}
	
	public void setTime(Date time) {
		this.time = time;
	}
	
}
