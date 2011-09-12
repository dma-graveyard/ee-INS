package dk.frv.enav.ins.event;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.AbstractMouseMode;
import com.bbn.openmap.proj.coords.LatLonPoint;

public abstract class AbstractCoordMouseMode extends AbstractMouseMode implements PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Set<IMapCoordListener> coordListeners = new HashSet<IMapCoordListener>();

    public AbstractCoordMouseMode() {
    	this("", true);
    }

    /**
     * @param modeID the id for the mouse mode.
     * @param shouldConsumeEvents the mode setting, where the mousemode should
     *        pass the events on to other listeners or not, depending if one of
     *        the listeners used it or not.
     */
    public AbstractCoordMouseMode(String modeID, boolean shouldConsumeEvents) {
        super(modeID, shouldConsumeEvents);
    }

    /**
     * Fires a mouse location to the InformationDelegator, and then calls the
     * super class method which calls the MouseSupport method.
     * 
     * @param e MouseEvent to be handled
     */
    public void mouseMoved(MouseEvent e) {
        fireMouseLocation(e);
        super.mouseMoved(e);
    }

    /**
     * Fires a mouse location to the InformationDelegator, and then calls the
     * super class method which calls the MouseSupport method.
     * 
     * @param e mouse event.
     */
    public void mouseDragged(MouseEvent e) {
        fireMouseLocation(e);
        /* disabled because it interferes with route editing and zooming */
        //super.mouseDragged(e);
    }

    /**
     * If the MouseMode has been made inactive, clean out any input that might
     * have been made to the info line.
     */
    public void setActive(boolean active) {
        
    }

    /**
     * Sends the mouse event location, x/y and lat/lon, to the
     * InformationDelegator.
     */
    public void fireMouseLocation(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        LatLonPoint llp = null;

        if (coordListeners.size() > 0) {
            if (e.getSource() instanceof MapBean) {
                llp = ((MapBean) e.getSource()).getProjection().inverse(x, y);
                for (IMapCoordListener listener : coordListeners) {
					listener.recieveCoord(llp);
				}
            }
        }
    }

    /**
     * Called when a CoordMouseMode is added to a BeanContext, or when another
     * object is added to the BeanContext after that. The CoordMouseMode looks
     * for an InformationDelegator to use to fire the coordinate updates. If
     * another InforationDelegator is added when one is already set, the later
     * one will replace the current one.
     * 
     * @param someObj an object being added to the BeanContext.
     */
    public void findAndInit(Object someObj) {
    	if (someObj instanceof IMapCoordListener) {
            this.coordListeners.add((IMapCoordListener)someObj);
        }
    }

    /**
     * BeanContextMembershipListener method. Called when objects have been
     * removed from the parent BeanContext. If an InformationDelegator is
     * removed from the BeanContext, and it's the same one that is currently
     * held, it will be removed.
     * 
     * @param someObj an object being removed from the BeanContext.
     */
    public void findAndUndo(Object someObj) {
    	if (someObj instanceof IMapCoordListener) {
            this.coordListeners.remove((IMapCoordListener)someObj);
        }
    }

    public void setProperties(String prefix, Properties props) {
        super.setProperties(prefix, props);
    }

    public Properties getProperties(Properties props) {
        props = super.getProperties(props);
        return props;
    }

    public Properties getPropertyInfo(Properties props) {
        props = super.getPropertyInfo(props);
        return props;
    }

    public void propertyChange(PropertyChangeEvent evt) {
    	
    }
	
}
