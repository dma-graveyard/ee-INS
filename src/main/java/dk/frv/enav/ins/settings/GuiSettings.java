package dk.frv.enav.ins.settings;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;
import java.util.Properties;

import com.bbn.openmap.util.PropUtils;

public class GuiSettings implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "gui.";

	private boolean maximized = false;
	private Point appLocation = new Point(10, 10);
	private Dimension appDimensions = new Dimension(1280, 800);
	private boolean multipleInstancesAllowed = false;

	public GuiSettings() {

	}

	public void readProperties(Properties props) {
		maximized = PropUtils.booleanFromProperties(props, PREFIX + "maximized", maximized);
		double x = PropUtils.doubleFromProperties(props, PREFIX + "appLocation_x", appLocation.getX());
		double y = PropUtils.doubleFromProperties(props, PREFIX + "appLocation_y", appLocation.getY());
		appLocation.setLocation(x, y);
		double w = PropUtils.doubleFromProperties(props, PREFIX + "appDimensions_w", appDimensions.getWidth());
		double h = PropUtils.doubleFromProperties(props, PREFIX + "appDimensions_h", appDimensions.getHeight());
		appDimensions.setSize(w, h);
		multipleInstancesAllowed = PropUtils.booleanFromProperties(props, PREFIX + "multipleInstancesAllowed", multipleInstancesAllowed);
	}

	public void setProperties(Properties props) {
		props.put(PREFIX + "maximized", Boolean.toString(maximized));
		props.put(PREFIX + "appLocation_x", Double.toString(appLocation.getX()));
		props.put(PREFIX + "appLocation_y", Double.toString(appLocation.getY()));
		props.put(PREFIX + "appDimensions_w", Double.toString(appDimensions.getWidth()));
		props.put(PREFIX + "appDimensions_h", Double.toString(appDimensions.getHeight()));
		props.put(PREFIX + "multipleInstancesAllowed", Boolean.toString(multipleInstancesAllowed));
	}

	public Point getAppLocation() {
		return appLocation;
	}

	public void setAppLocation(Point appLocation) {
		this.appLocation = appLocation;
	}

	public Dimension getAppDimensions() {
		return appDimensions;
	}

	public void setAppDimensions(Dimension appDimensions) {
		this.appDimensions = appDimensions;
	}

	public boolean isMaximized() {
		return maximized;
	}

	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}
	
	public boolean isMultipleInstancesAllowed() {
		return multipleInstancesAllowed;
	}
	
	public void setMultipleInstancesAllowed(boolean multipleInstancesAllowed) {
		this.multipleInstancesAllowed = multipleInstancesAllowed;
	}

}
