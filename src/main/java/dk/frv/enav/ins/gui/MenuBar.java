package dk.frv.enav.ins.gui;

import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.beans.beancontext.BeanContextMembershipEvent;
import java.beans.beancontext.BeanContextMembershipListener;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;

import com.bbn.openmap.Environment;
import com.bbn.openmap.I18n;
import com.bbn.openmap.LightMapHandlerChild;
import com.bbn.openmap.PropertyConsumer;
import com.bbn.openmap.gui.WindowSupport;

public class MenuBar extends JMenuBar implements PropertyConsumer, BeanContextChild, BeanContextMembershipListener,
LightMapHandlerChild {


	private static final long serialVersionUID = 1L;

	protected I18n i18n = Environment.getI18n();

	protected int orientation = SwingConstants.HORIZONTAL;

	protected boolean isolated = false;

	protected BeanContextChildSupport beanContextChildSupport = new BeanContextChildSupport(this);
	
	public MenuBar(){
		super();
		
		JMenu menu = new JMenu("Menu Label");
		this.add(menu);

		
		JMenu menu2 = new JMenu("Menu Label");
		this.add(menu2);

		JMenu menu3 = new JMenu("Menu Label");
		this.add(menu3);

		JMenu menu4 = new JMenu("Menu Label");
		this.add(menu4);

		
		// Create a menu item
		JMenuItem item = new JMenuItem("Item Label");
		menu.add(item);
	}

	protected WindowSupport windowSupport;

	public void setWindowSupport(WindowSupport ws) {
		windowSupport = ws;
	}

	public WindowSupport getWindowSupport() {
		return windowSupport;
	}

	protected String propertyPrefix = null;

	public void setProperties(java.util.Properties props) {
		setProperties(getPropertyPrefix(), props);
	}

	public void setProperties(String prefix, java.util.Properties props) {
		setPropertyPrefix(prefix);

		// String realPrefix =
		// PropUtils.getScopedPropertyPrefix(prefix);
	}

	public Properties getProperties(Properties props) {
		if (props == null) {
			props = new Properties();
		}
		return props;
	}

	public Properties getPropertyInfo(Properties list) {
		if (list == null) {
			list = new Properties();
		}
		return list;
	}

	public void setPropertyPrefix(String prefix) {
		propertyPrefix = prefix;
	}

	public String getPropertyPrefix() {
		return propertyPrefix;
	}

	public void findAndInit(Object obj) {
	}

	public void findAndUndo(Object obj) {
	}

	public void findAndInit(Iterator<?> it) {
		while (it.hasNext()) {
			findAndInit(it.next());
		}
	}

	public void childrenAdded(BeanContextMembershipEvent bcme) {
		if (!isolated || bcme.getBeanContext().equals(getBeanContext())) {
			findAndInit(bcme.iterator());
		}
	}

	public void childrenRemoved(BeanContextMembershipEvent bcme) {
		Iterator<?> it = bcme.iterator();
		while (it.hasNext()) {
			findAndUndo(it.next());
		}
	}

	public BeanContext getBeanContext() {
		return beanContextChildSupport.getBeanContext();
	}

	public void setBeanContext(BeanContext in_bc) throws PropertyVetoException {

		if (in_bc != null) {
			if (!isolated || beanContextChildSupport.getBeanContext() == null) {
				in_bc.addBeanContextMembershipListener(this);
				beanContextChildSupport.setBeanContext(in_bc);
				findAndInit(in_bc.iterator());
			}
		}
	}

	public void addVetoableChangeListener(String propertyName, VetoableChangeListener in_vcl) {
		beanContextChildSupport.addVetoableChangeListener(propertyName, in_vcl);
	}

	public void removeVetoableChangeListener(String propertyName, VetoableChangeListener in_vcl) {
		beanContextChildSupport.removeVetoableChangeListener(propertyName, in_vcl);
	}

	public void fireVetoableChange(String name, Object oldValue, Object newValue) throws PropertyVetoException {
		beanContextChildSupport.fireVetoableChange(name, oldValue, newValue);
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public boolean isIsolated() {
		return isolated;
	}

	public void setIsolated(boolean isolated) {
		this.isolated = isolated;
	}
}
