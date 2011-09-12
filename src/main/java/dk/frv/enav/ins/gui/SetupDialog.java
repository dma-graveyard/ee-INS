package dk.frv.enav.ins.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.gui.setupTabs.AisTab;
import dk.frv.enav.ins.gui.setupTabs.ENavTab;
import dk.frv.enav.ins.gui.setupTabs.MapTab;
import dk.frv.enav.ins.gui.setupTabs.NavigationTab;
import dk.frv.enav.ins.gui.setupTabs.SensorTab;
import dk.frv.enav.ins.settings.Settings;

public class SetupDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JButton btnOk;
	private JButton btnCancel;
	private AisTab aisTab;
	private ENavTab enavTab;
	private NavigationTab navTab;
	private SensorTab sensorTab;
	private MapTab mapTab;
	private Settings settings;
	
	public SetupDialog(JFrame parent) {
		super(parent, "Setup", true);
		
		setSize(462, 556);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(parent);
		
//		Component comp = EeINS.getMainFrame().getChartPanel().getS52Layer().getGUI();
//		getContentPane().add(comp);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		aisTab = new AisTab();
		tabbedPane.addTab("AIS", null, aisTab, null);
		
		enavTab = new ENavTab();
		tabbedPane.addTab("E-Nav Services", null, enavTab, null);
		
		navTab = new NavigationTab();
		tabbedPane.addTab("Navigation", null, navTab, null);
		
		sensorTab = new SensorTab();
		tabbedPane.addTab("Sensor", null, sensorTab, null);
		
		mapTab = new MapTab();
		tabbedPane.addTab("Map", null, mapTab, null);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		btnOk = new JButton("OK");
		btnOk.addActionListener(this);
		panel.add(btnOk);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		panel.add(btnCancel);
//		comp.setVisible(true);
	}
	
	public void loadSettings(Settings settings) {
		this.settings = settings;
		aisTab.loadSettings(settings.getAisSettings());
		enavTab.loadSettings(settings.getEnavSettings());
		navTab.loadSettings(settings.getNavSettings());
		sensorTab.loadSettings(settings.getSensorSettings());
		mapTab.loadSettings(settings.getMapSettings());
	}
	
	public void saveSettings() {
		aisTab.saveSettings();
		enavTab.saveSettings();
		navTab.saveSettings();
		sensorTab.saveSettings();
		mapTab.saveSettings();
		settings.saveToFile();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnOk){
			saveSettings();
			this.setVisible(false);
			int choice = JOptionPane.showOptionDialog(EeINS.getMainFrame(), "A restart of the program is required to apply settings.\nWould you like to restart now?", "Restart required", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			if(choice == JOptionPane.YES_OPTION) {
				EeINS.closeApp();
			}
		}
		if(e.getSource() == btnCancel){
			this.setVisible(false);
		}
	}
}
