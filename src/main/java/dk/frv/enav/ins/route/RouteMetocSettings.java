package dk.frv.enav.ins.route;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import dk.frv.enav.common.xml.metoc.MetocDataTypes;

public class RouteMetocSettings implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private boolean showRouteMetoc = false;
	private int interval = 15;
	private Set<MetocDataTypes> dataTypes = new HashSet<MetocDataTypes>();
	private Double windWarnLimit;
	private Double currentWarnLimit;
	private Double waveWarnLimit;
	
	public RouteMetocSettings() {
		for (MetocDataTypes dataType : MetocDataTypes.allTypes()) {
			dataTypes.add(dataType);
		}
	}

	public boolean isShowRouteMetoc() {
		return showRouteMetoc;
	}

	public void setShowRouteMetoc(boolean showRouteMetoc) {
		this.showRouteMetoc = showRouteMetoc;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public Set<MetocDataTypes> getDataTypes() {
		return dataTypes;
	}

	public void setDataTypes(Set<MetocDataTypes> dataTypes) {
		this.dataTypes = dataTypes;
	}

	public Double getWindWarnLimit() {
		return windWarnLimit;
	}

	public void setWindWarnLimit(Double windWarnLimit) {
		this.windWarnLimit = windWarnLimit;
	}

	public Double getWaveWarnLimit() {
		return waveWarnLimit;
	}

	public void setWaveWarnLimit(Double waveWarnLimit) {
		this.waveWarnLimit = waveWarnLimit;
	}

	public Double getCurrentWarnLimit() {
		return currentWarnLimit;
	}

	public void setCurrentWarnLimit(Double currentWarnLimit) {
		this.currentWarnLimit = currentWarnLimit;
	}

}
