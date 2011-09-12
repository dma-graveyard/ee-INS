package dk.frv.enav.ins.event;
/**
 * A synchronized singleton made for only keeping track of clicks once for all layers
 * @author jsa
 *
 */
public class ClickTimer {
	
	private static ClickTimer clickTimer;
	private long startTime;
	private int interval;
	
	private ClickTimer() {
		// TODO Auto-generated constructor stub
	}
	
	public synchronized static ClickTimer getClickTimer(){
		if(clickTimer == null){
			clickTimer = new ClickTimer();
		}
		return clickTimer;
	}
	
	public void startTime(){
		startTime = System.currentTimeMillis();
	}
	
	public boolean isIntervalExceeded(){
		long endTime = System.currentTimeMillis();
		long difference = endTime - startTime;
		return difference > interval;
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
	}
}
