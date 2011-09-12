package dk.frv.enav.ins.settings;

import java.io.Serializable;
import java.util.Properties;

import com.bbn.openmap.util.PropUtils;

public class AisSettings implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "ais.";
	
	private boolean visible = true;
	private double cogVectorLength = 6; // minutes
	private boolean strict = true; // Strict timeout rules
	private int minRedrawInterval = 5; // 5 sec
	private boolean allowSending = true;
	private boolean broadcastIntendedRoute = true;
	private boolean showIntendedRouteByDefault = false;
	private int intendedRouteMaxWps = 16;
	private int intendedRouteMaxTime = 0; // In minutes 0 = inf
	private int sartPrefix = 970;
	private boolean showNameLabels = true;
	private int showMinuteMarksAISTarget = 200;
	
	public AisSettings() {
		
	}
	
	public void readProperties(Properties props) {
		visible = PropUtils.booleanFromProperties(props, PREFIX + "visible", visible);
		cogVectorLength = PropUtils.doubleFromProperties(props, PREFIX + "cogVectorLength", cogVectorLength);
		strict = PropUtils.booleanFromProperties(props, PREFIX + "strict", strict);
		minRedrawInterval = PropUtils.intFromProperties(props, PREFIX + "minRedrawInterval", minRedrawInterval);
		allowSending = PropUtils.booleanFromProperties(props, PREFIX + "allowSending", allowSending);
		broadcastIntendedRoute = PropUtils.booleanFromProperties(props, PREFIX + "broadcastIntendedRoute", broadcastIntendedRoute);
		showIntendedRouteByDefault = PropUtils.booleanFromProperties(props, PREFIX + "showIntendedRouteByDefault", showIntendedRouteByDefault);
		intendedRouteMaxWps = PropUtils.intFromProperties(props, PREFIX + "intendedRouteMaxWps", intendedRouteMaxWps);
		intendedRouteMaxTime = PropUtils.intFromProperties(props, PREFIX + "intendedRouteMaxTime", intendedRouteMaxTime);
		sartPrefix = PropUtils.intFromProperties(props, PREFIX + "sartPrefix", sartPrefix);
		showNameLabels = PropUtils.booleanFromProperties(props, PREFIX + "showNameLabels", showNameLabels);
		showMinuteMarksAISTarget = PropUtils.intFromProperties(props, PREFIX + "showMinuteMarksAISTarget", showMinuteMarksAISTarget);
	}
	
	public void setProperties(Properties props) {
		props.put(PREFIX + "visible", Boolean.toString(visible));
		props.put(PREFIX + "cogVectorLength", Double.toString(cogVectorLength));
		props.put(PREFIX + "strict", Boolean.toString(strict));
		props.put(PREFIX + "minRedrawInterval", Integer.toString(minRedrawInterval));
		props.put(PREFIX + "allowSending", Boolean.toString(allowSending));
		props.put(PREFIX + "broadcastIntendedRoute", Boolean.toString(broadcastIntendedRoute));
		props.put(PREFIX + "showIntendedRouteByDefault", Boolean.toString(showIntendedRouteByDefault));
		props.put(PREFIX + "intendedRouteMaxWps", Integer.toString(intendedRouteMaxWps));
		props.put(PREFIX + "intendedRouteMaxTime", Integer.toString(intendedRouteMaxTime));
		props.put(PREFIX + "sartPrefix", Integer.toString(sartPrefix));
		props.put(PREFIX + "showNameLabels", Boolean.toString(showNameLabels));
		props.put(PREFIX + "showMinuteMarksAISTarget", Float.toString(showMinuteMarksAISTarget));
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public double getCogVectorLength() {
		return cogVectorLength;
	}

	public void setCogVectorLength(double cogVectorLength) {
		this.cogVectorLength = cogVectorLength;
	}
	
	public boolean isStrict() {
		return strict;
	}
	
	public void setStrict(boolean strict) {
		this.strict = strict;
	}
	
	public int getMinRedrawInterval() {
		return minRedrawInterval;
	}
	
	public void setMinRedrawInterval(int minRedrawInterval) {
		this.minRedrawInterval = minRedrawInterval;
	}

	public boolean isAllowSending() {
		return allowSending;
	}

	public void setAllowSending(boolean allowSending) {
		this.allowSending = allowSending;
	}

	public boolean isBroadcastIntendedRoute() {
		return broadcastIntendedRoute;
	}

	public void setBroadcastIntendedRoute(boolean broadcastIntendedRoute) {
		this.broadcastIntendedRoute = broadcastIntendedRoute;
	}

	public boolean isShowIntendedRouteByDefault() {
		return showIntendedRouteByDefault;
	}

	public void setShowIntendedRouteByDefault(boolean showIntendedRouteByDefault) {
		this.showIntendedRouteByDefault = showIntendedRouteByDefault;
	}
	
	public int getIntendedRouteMaxWps() {
		return intendedRouteMaxWps;
	}
	
	public void setIntendedRouteMaxWps(int intendedRouteMaxWps) {
		this.intendedRouteMaxWps = intendedRouteMaxWps;
	}
	
	public int getIntendedRouteMaxTime() {
		return intendedRouteMaxTime;
	}
	
	public void setIntendedRouteMaxTime(int intendedRouteMaxTime) {
		this.intendedRouteMaxTime = intendedRouteMaxTime;
	}
	
	public String getSartPrefix() {
		return Integer.toString(sartPrefix);
	}
	
	public void setSartPrefix(String sartPrefix) {
		this.sartPrefix = new Integer(sartPrefix);
	}
	
	public boolean isShowNameLabels() {
		return showNameLabels;
	}
	
	public void setShowNameLabels(boolean showNameLabels) {
		this.showNameLabels = showNameLabels;
	}
	
	public int getShowMinuteMarksAISTarget() {
		return showMinuteMarksAISTarget;
	}

	public void setShowMinuteMarksAISTarget(int showMinuteMarksAISTarget) {
		this.showMinuteMarksAISTarget = showMinuteMarksAISTarget;
	}
}
