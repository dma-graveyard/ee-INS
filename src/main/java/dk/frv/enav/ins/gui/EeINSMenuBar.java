package dk.frv.enav.ins.gui;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.beans.beancontext.BeanContextMembershipEvent;
import java.beans.beancontext.BeanContextMembershipListener;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import com.bbn.openmap.Environment;
import com.bbn.openmap.I18n;
import com.bbn.openmap.LightMapHandlerChild;
import com.bbn.openmap.PropertyConsumer;
import com.bbn.openmap.gui.WindowSupport;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.layers.nogo.NogoLayer;
import dk.frv.enav.ins.msi.MsiHandler;
import dk.frv.enav.ins.nogo.NogoHandler;

public class EeINSMenuBar extends JMenuBar implements PropertyConsumer,
		BeanContextChild, BeanContextMembershipListener, LightMapHandlerChild {

	private static final long serialVersionUID = 1L;

	protected I18n i18n = Environment.getI18n();

	protected int orientation = SwingConstants.HORIZONTAL;

	protected boolean isolated = false;

	protected BeanContextChildSupport beanContextChildSupport = new BeanContextChildSupport(
			this);

	private MainFrame mainFrame;
	private TopPanel topPanel;
	private NogoHandler nogoHandler;
	private MsiHandler msiHandler;

	private JCheckBoxMenuItem lock;
	private JCheckBoxMenuItem autoFollow;
	private JCheckBoxMenuItem aisLayer;
	private JCheckBoxMenuItem encLayer;
	private JCheckBoxMenuItem nogoLayer = new JCheckBoxMenuItem("NoGo Layer");;
	private JCheckBoxMenuItem newRoute;
	
	public EeINSMenuBar() {
		super();
	}

	private void initMenuBar() {
		boolean showRiskAndNogo = !EeINS.getSettings().getGuiSettings()
				.isRiskNogoDisabled();
		
		JMenu file = new JMenu("File");
		this.add(file);

		// Create a menu item
		JMenuItem setup = new JMenuItem("Setup");
		file.add(setup);

		setup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SetupDialog setupDialog = new SetupDialog(mainFrame);
				setupDialog.loadSettings(EeINS.getSettings());
				setupDialog.setVisible(true);
			}
		});

		setup.setIcon(toolbarIcon("images/toolbar/wrench.png"));
		
		lock = new JCheckBoxMenuItem("Unlock");
		file.add(lock);
		lock.setSelected(true);
		lock.setIcon(toolbarIcon("images/toolbar/lock-unlock.png"));
		
		
		lock.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem m = (JCheckBoxMenuItem) e.getSource();
				mainFrame.getDockableComponents().toggleFrameLock();
				if (m.isSelected()) {
					m.setText("Unlock");
					m.setIcon(toolbarIcon("images/toolbar/lock-unlock.png"));
				} else {
					m.setText("Lock");
					m.setIcon(toolbarIcon("images/toolbar/lock.png"));
				}
			}
		});

		JMenuItem exit = new JMenuItem("Exit");
		file.add(exit);
		exit.setIcon(toolbarIcon("images/toolbar/cross-circle.png"));

		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.windowClosing(null);
			}
		});

		JMenu interact = new JMenu("Interact");
		this.add(interact);

		JMenuItem zoomIn = new JMenuItem("Zoom in : Shortcut Numpad +");
		interact.add(zoomIn);
		zoomIn.setIcon(toolbarIcon("images/toolbar/magnifier-zoom-in.png"));

		zoomIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.getChartPanel().doZoom(0.5f);
			}
		});

		JMenuItem zoomOut = new JMenuItem("Zoom out : Shortcut Numpad -");
		interact.add(zoomOut);
		zoomOut.setIcon(toolbarIcon("images/toolbar/magnifier-zoom-out.png"));

		zoomOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.getChartPanel().doZoom(2f);
			}
		});

		JCheckBoxMenuItem centerOnShip = new JCheckBoxMenuItem(
				"Centre on ship : Shortcut C");
		interact.add(centerOnShip);
		centerOnShip.setIcon(toolbarIcon("images/toolbar/arrow-in.png"));

		centerOnShip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.getChartPanel().centreOnShip();
			}
		});

		autoFollow = new JCheckBoxMenuItem("Auto follow own ship");
		interact.add(autoFollow);
		autoFollow.setSelected(EeINS.getSettings().getNavSettings().isAutoFollow());
		autoFollow.setIcon(toolbarIcon("images/toolbar/arrow-curve-000-double.png"));

		autoFollow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				topPanel.getAutoFollowBtn().setSelected(
						!topPanel.getAutoFollowBtn().isSelected());
				
				EeINS.getSettings()
						.getNavSettings()
						.setAutoFollow(topPanel.getAutoFollowBtn().isSelected());

				if (topPanel.getAutoFollowBtn().isSelected()) {
					mainFrame.getChartPanel().autoFollow();
				}
			}
		});

		JMenu layers = new JMenu("Layers");
		this.add(layers);

		aisLayer = new JCheckBoxMenuItem("AIS Layer");
		layers.add(aisLayer);
		aisLayer.setSelected(EeINS.getSettings().getAisSettings().isVisible());

		aisLayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				topPanel.getAisBtn().setSelected(!topPanel.getAisBtn().isSelected());
				EeINS.getSettings().getAisSettings().setVisible(topPanel.getAisBtn().isSelected());
				mainFrame.getChartPanel().aisVisible(topPanel.getAisBtn().isSelected());
			}
		});

		encLayer = new JCheckBoxMenuItem("ENC Layer");
		layers.add(encLayer);
		encLayer.setSelected(EeINS.getSettings().getMapSettings().isEncVisible());
		
		encLayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				topPanel.getEncBtn().setSelected(!topPanel.getEncBtn().isSelected());
				EeINS.getSettings().getMapSettings().setEncVisible(topPanel.getEncBtn().isSelected());
				mainFrame.getChartPanel().encVisible(topPanel.getEncBtn().isSelected());
			}
		});

//		JCheckBoxMenuItem msiLayer = new JCheckBoxMenuItem("MSI Layer");
//		layers.add(msiLayer);
//		msiLayer.setSelected(EeINS.getSettings().getMapSettings().);
//		
//		encLayer.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				topPanel.getEncBtn().setSelected(!topPanel.getEncBtn().isSelected());
//				EeINS.getSettings().getMapSettings().setEncVisible(topPanel.getEncBtn().isSelected());
//				mainFrame.getChartPanel().encVisible(topPanel.getEncBtn().isSelected());
//			}
//		});
//		
		if (showRiskAndNogo){
			layers.add(nogoLayer);
		}
		
		

		
		nogoLayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nogoHandler.toggleLayer();
			}
		});
//		nogoHandler.toggleLayer();

		JCheckBoxMenuItem riskLayer = new JCheckBoxMenuItem("Risk Layer");
		if (showRiskAndNogo){
			layers.add(riskLayer);
		}

		aisLayer.setSelected(EeINS.getSettings().getAisSettings().isStrict());

		riskLayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EeINS.getRiskHandler().toggleRiskHandler(!EeINS.getSettings().getAisSettings().isShowRisk());
			}
		});
		
		JMenu tools = new JMenu("Tools");
		this.add(tools);

		newRoute = new JCheckBoxMenuItem("New Route | Ctrl n");
		tools.add(newRoute);
		newRoute.setIcon(toolbarIcon("images/toolbar/marker--plus.png"));
		
		newRoute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				topPanel.getNewRouteBtn().setSelected(true);
				topPanel.newRoute();
			}
		});

		JMenuItem dynamicNoGo = new JMenuItem("Dynamic NoGo");
		tools.add(dynamicNoGo);

		JCheckBoxMenuItem msiFilter = new JCheckBoxMenuItem("MSI Filtering");
		tools.add(msiFilter);
		
		msiFilter.setSelected(EeINS.getSettings().getEnavSettings().isMsiFilter());

		msiFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EeINS.getSettings().getEnavSettings().setMsiFilter(!
						EeINS.getSettings().getEnavSettings().isMsiFilter());
				msiHandler.notifyUpdate();
			}
		});
		
		
		this.add(mainFrame.getDockableComponents().createDockableMenu());

		JMenu help = new JMenu("Help");
		this.add(help);

		JMenuItem aboutEeINS = new JMenuItem("About the EeINS");
		help.add(aboutEeINS);
		aboutEeINS.setIcon(toolbarIcon("images/appicon.png"));
		
		final ImageIcon icon = createImageIcon();
		
		aboutEeINS.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(mainFrame,
					    "The e-Navigation enhanced INS (ee-INS) is developed by the Danish Maritime Authority (www.dma.dk). \n The user manual is available from service.e-navigation.net",
					    "About the EeINS",
					    JOptionPane.OK_OPTION, icon);
			}
		});
		
	}
	
	protected static ImageIcon createImageIcon() {
        java.net.URL imgURL = EeINS.class.getResource("/images/appicon.png");;
        if (imgURL != null) {
        	
    		ImageIcon icon = new ImageIcon(imgURL);
    		Image img = icon.getImage();
    		Image newimg = img.getScaledInstance(30, 30, java.awt.Image.SCALE_DEFAULT);
    		ImageIcon newImage = new ImageIcon(newimg);
    		return newImage;

        } else {
            System.err.println("Couldn't find file");
            return null;
        }
    }

	public JCheckBoxMenuItem getLock() {
		return lock;
	}

	public void setLock(JCheckBoxMenuItem lock) {
		this.lock = lock;
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
		if (obj instanceof TopPanel) {
			topPanel = (TopPanel) obj;
		}
		if (obj instanceof NogoHandler) {
			nogoHandler = (NogoHandler) obj;

		}
		if (obj instanceof MainFrame) {
			mainFrame = (MainFrame) obj;
			initMenuBar();
		}
		if (obj instanceof NogoLayer) {
			nogoLayer.setSelected( ((NogoLayer) obj).isVisible());
		}
		if (obj instanceof MsiHandler) {
			msiHandler = (MsiHandler) obj;
		}
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

	public void addVetoableChangeListener(String propertyName,
			VetoableChangeListener in_vcl) {
		beanContextChildSupport.addVetoableChangeListener(propertyName, in_vcl);
	}

	public void removeVetoableChangeListener(String propertyName,
			VetoableChangeListener in_vcl) {
		beanContextChildSupport.removeVetoableChangeListener(propertyName,
				in_vcl);
	}

	public void fireVetoableChange(String name, Object oldValue, Object newValue)
			throws PropertyVetoException {
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

	public JCheckBoxMenuItem getAutoFollow() {
		return autoFollow;
	}

	public JCheckBoxMenuItem getAisLayer() {
		return aisLayer;
	}

	public JCheckBoxMenuItem getEncLayer() {
		return encLayer;
	}

	public JCheckBoxMenuItem getNogoLayer() {
		return nogoLayer;
	}

	public JCheckBoxMenuItem getNewRoute() {
		return newRoute;
	}

	public ImageIcon toolbarIcon(String imgpath) {
		ImageIcon icon = new ImageIcon(imgpath);
		Image img = icon.getImage();
		Image newimg = img.getScaledInstance(16, 16, java.awt.Image.SCALE_DEFAULT);
		ImageIcon newImage = new ImageIcon(newimg);
		return newImage;
	}
	
	
}
