/*
 * Copyright 2011 Danish Maritime Authority. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Authority ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Authority.
 * 
 */
package dk.frv.enav.ins.gui.route;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;

import dk.frv.enav.common.xml.metoc.MetocDataTypes;
import dk.frv.enav.common.xml.metoc.MetocForecast;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RouteMetocSettings;

/**
 * Dialog with METOC settings 
 */
public class RouteMetocDialog extends JDialog implements ActionListener, FocusListener  {

	private static final long serialVersionUID = 1L;
	
	private JTextField windLimit;
	private JTextField currentLimit;
	private JTextField waveLimit;
	private JCheckBox showCheckbox;
	private JLabel currentLabel;
	private JLabel currentMetocDataLbl;
	private JLabel intervalLbl;
	private JComboBox intervalDb;
	private JButton requestBtn;
	private JCheckBox windCb;
	private JCheckBox currentCb;
	private JCheckBox wavesCb;
	private JCheckBox seaLevelCb;
	private JCheckBox densityCb;
	private JLabel windLimitLbl;
	private JLabel currentLimitLbl;
	private JLabel waveLimitLbl;
	private JPanel statusPanel;
	private JPanel typesPanel;
	private JPanel warnLimitsPanel;
	private JButton closeBtn;
	
	private RouteManager routeManager;
	private Route route;

	public RouteMetocDialog(Window parent, RouteManager routeManager, int routeId) {
		super(parent, "Route METOC properties", Dialog.ModalityType.APPLICATION_MODAL);
		
		this.routeManager = routeManager;
		if (routeManager.isActiveRoute(routeId)) {
			route = routeManager.getActiveRoute();
		} else {
			route = routeManager.getRoute(routeId);
		}
		
		setSize(270, 460);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
                
        initGui();
        updateFields();
	}
	
	private void updateFields() {
		if (route.getRouteMetocSettings() == null) {
			// Set default settings
			route.setRouteMetocSettings(routeManager.getDefaultRouteMetocSettings());
		}
		RouteMetocSettings metocSettings = route.getRouteMetocSettings();
		MetocForecast metocForecast = route.getMetocForecast();
		
		// Enabled or not
		if (metocForecast == null) {
			currentMetocDataLbl.setText("None");
		} else {
			currentMetocDataLbl.setText(Formatter.formatLongDateTime(metocForecast.getCreated()));			
		}
		showCheckbox.setSelected(metocSettings.isShowRouteMetoc());
		
		// Interval
		intervalDb.getModel().setSelectedItem(Integer.toString(metocSettings.getInterval()));
		
		// METOC data
		windCb.setSelected(metocSettings.getDataTypes().contains(MetocDataTypes.WI));
		currentCb.setSelected(metocSettings.getDataTypes().contains(MetocDataTypes.CU));
		wavesCb.setSelected(metocSettings.getDataTypes().contains(MetocDataTypes.WA));
		seaLevelCb.setSelected(metocSettings.getDataTypes().contains(MetocDataTypes.SE));
		densityCb.setSelected(metocSettings.getDataTypes().contains(MetocDataTypes.DE));
		
		// Warn limits
		windLimit.setText(String.format("%.2f", metocSettings.getWindWarnLimit()));
		windLimit.addFocusListener(this);
		currentLimit.setText(String.format("%.2f", metocSettings.getCurrentWarnLimit()));
		currentLimit.addFocusListener(this);
		waveLimit.setText(String.format("%.2f", metocSettings.getWaveWarnLimit()));
		waveLimit.addFocusListener(this);
	}
	
	private void saveValues() {
		RouteMetocSettings metocSettings = route.getRouteMetocSettings();
		
		metocSettings.setShowRouteMetoc(showCheckbox.isSelected());
		metocSettings.setInterval(Integer.parseInt((String)intervalDb.getSelectedItem()));
		
		Set<MetocDataTypes> dataTypes = metocSettings.getDataTypes();
		dataTypes.clear();
		if (windCb.isSelected()) dataTypes.add(MetocDataTypes.WI);
		if (currentCb.isSelected()) dataTypes.add(MetocDataTypes.CU);
		if (wavesCb.isSelected()) dataTypes.add(MetocDataTypes.WA);
		if (seaLevelCb.isSelected()) dataTypes.add(MetocDataTypes.SE);
		if (densityCb.isSelected()) dataTypes.add(MetocDataTypes.DE);
		
		metocSettings.setWindWarnLimit(parseFieldVal(windLimit, metocSettings.getWindWarnLimit()));
		metocSettings.setCurrentWarnLimit(parseFieldVal(currentLimit, metocSettings.getCurrentWarnLimit()));
		metocSettings.setWaveWarnLimit(parseFieldVal(waveLimit, metocSettings.getWaveWarnLimit()));
	}
	
	private void requestMetoc() {
		showCheckbox.setSelected(true);
		saveValues();		
		MetocRequestDialog.requestMetoc(this, routeManager, route);
		updateFields();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == closeBtn) {
			saveValues();
			dispose();
		} else if (e.getSource() == requestBtn) {
			requestMetoc();			
		}
		
	}
	
	@Override
	public void focusLost(FocusEvent e) {
		if (!(e.getSource() instanceof JTextField)) {
			return;
		}
		RouteMetocSettings metocSettings = route.getRouteMetocSettings();
		if (e.getSource() == windLimit) {
			parseFieldVal(windLimit, metocSettings.getWindWarnLimit());
		}
		if (e.getSource() == currentLimit) {
			parseFieldVal(currentLimit, metocSettings.getCurrentWarnLimit());
		}
		if (e.getSource() == waveLimit) {
			parseFieldVal(waveLimit, metocSettings.getWaveWarnLimit());
		}
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		
	}
	
	private static Double parseFieldVal(JTextField field, Double defaultVal) {
		Double val = defaultVal;
		String strVal = field.getText();
		// Be relaxed
		strVal = strVal.replace(',', '.');
		try {
			val = Double.parseDouble(strVal);						
		} catch (NumberFormatException e) { }
		field.setText(String.format("%.2f", val));
		return val; 
	}
	
	
	private void initGui() {
        showCheckbox = new JCheckBox("Show route METOC (if available)");
        showCheckbox.setSelected(true);
        showCheckbox.setEnabled(true);  
        currentLabel = new JLabel("Current METOC data:");        
        currentMetocDataLbl = new JLabel("None");
        currentMetocDataLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));        
        intervalLbl = new JLabel("Point interval (min)");        
        intervalDb = new JComboBox();
        intervalDb.setModel(new DefaultComboBoxModel(new String[] {"15", "30", "45", "60"}));
        intervalDb.setSelectedIndex(0);
        intervalDb.setMaximumRowCount(4);        
        requestBtn = new JButton("Request METOC");
        requestBtn.addActionListener(this);
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);
        
        windCb = new JCheckBox("Wind");
        currentCb = new JCheckBox("Current");
        wavesCb = new JCheckBox("Waves");
        seaLevelCb = new JCheckBox("Sea Level");
        densityCb = new JCheckBox("Density");
        

        windLimitLbl = new JLabel("Wind speed m/s");
        currentLimitLbl = new JLabel("Current speed kn");
        waveLimitLbl = new JLabel("Mean wave height m");
        windLimit = new JTextField();
        windLimit.setColumns(10);
        currentLimit = new JTextField();
        currentLimit.setColumns(10);
        waveLimit = new JTextField();
        waveLimit.setColumns(10);

        statusPanel = new JPanel();
        statusPanel.setBorder(new TitledBorder(null, "METOC status", TitledBorder.LEADING, TitledBorder.TOP, null, null));        
        typesPanel = new JPanel();
        typesPanel.setBorder(new TitledBorder(null, "METOC data", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));        
        warnLimitsPanel = new JPanel();
        warnLimitsPanel.setBorder(new TitledBorder(null, "Warn limits", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
        			.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
        				.addComponent(warnLimitsPanel, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 251, Short.MAX_VALUE)
        				.addComponent(typesPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
        				.addComponent(statusPanel, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 251, Short.MAX_VALUE))
        			.addGap(3))
        		.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
        			.addContainerGap(173, Short.MAX_VALUE)
        			.addComponent(closeBtn, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap())
        );
        groupLayout.setVerticalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addGap(7)
        			.addComponent(statusPanel, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(typesPanel, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(warnLimitsPanel, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        			.addComponent(closeBtn)
        			.addGap(17))
        );
        
        GroupLayout gl_warnLimitsPanel = new GroupLayout(warnLimitsPanel);
        gl_warnLimitsPanel.setHorizontalGroup(
        	gl_warnLimitsPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_warnLimitsPanel.createSequentialGroup()
        			.addGroup(gl_warnLimitsPanel.createParallelGroup(Alignment.LEADING)
        				.addGroup(gl_warnLimitsPanel.createParallelGroup(Alignment.LEADING)
        					.addGroup(gl_warnLimitsPanel.createSequentialGroup()
        						.addContainerGap()
        						.addComponent(windLimitLbl)
        						.addGap(41))
        					.addGroup(gl_warnLimitsPanel.createSequentialGroup()
        						.addContainerGap()
        						.addComponent(currentLimitLbl, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        						.addPreferredGap(ComponentPlacement.RELATED)))
        				.addGroup(gl_warnLimitsPanel.createSequentialGroup()
        					.addContainerGap()
        					.addComponent(waveLimitLbl, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED)))
        			.addGroup(gl_warnLimitsPanel.createParallelGroup(Alignment.LEADING, false)
        				.addComponent(waveLimit, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
        				.addComponent(currentLimit, 0, 0, Short.MAX_VALUE)
        				.addComponent(windLimit, 0, 0, Short.MAX_VALUE))
        			.addGap(78))
        );
        gl_warnLimitsPanel.setVerticalGroup(
        	gl_warnLimitsPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_warnLimitsPanel.createSequentialGroup()
        			.addGroup(gl_warnLimitsPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(windLimitLbl)
        				.addComponent(windLimit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_warnLimitsPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(currentLimit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(currentLimitLbl))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_warnLimitsPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(waveLimit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(waveLimitLbl))
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        warnLimitsPanel.setLayout(gl_warnLimitsPanel);
        
        GroupLayout gl_typesPanel = new GroupLayout(typesPanel);
        gl_typesPanel.setHorizontalGroup(
        	gl_typesPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_typesPanel.createSequentialGroup()
        			.addGroup(gl_typesPanel.createParallelGroup(Alignment.LEADING)
        				.addGroup(gl_typesPanel.createSequentialGroup()
        					.addComponent(windCb, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(currentCb, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
        				.addGroup(gl_typesPanel.createSequentialGroup()
        					.addComponent(wavesCb, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(seaLevelCb, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
        				.addComponent(densityCb, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
        			.addContainerGap(72, Short.MAX_VALUE))
        );
        gl_typesPanel.setVerticalGroup(
        	gl_typesPanel.createParallelGroup(Alignment.TRAILING)
        		.addGroup(Alignment.LEADING, gl_typesPanel.createSequentialGroup()
        			.addGroup(gl_typesPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(windCb)
        				.addComponent(currentCb))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_typesPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(wavesCb)
        				.addComponent(seaLevelCb))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(densityCb)
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        typesPanel.setLayout(gl_typesPanel);
        
        GroupLayout gl_statusPanel = new GroupLayout(statusPanel);
        gl_statusPanel.setHorizontalGroup(
        	gl_statusPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_statusPanel.createSequentialGroup()
        			.addGroup(gl_statusPanel.createParallelGroup(Alignment.LEADING)
        				.addComponent(showCheckbox)
        				.addGroup(gl_statusPanel.createSequentialGroup()
        					.addComponent(intervalLbl)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(intervalDb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        				.addGroup(gl_statusPanel.createSequentialGroup()
        					.addComponent(currentLabel)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(currentMetocDataLbl))
        				.addGroup(gl_statusPanel.createSequentialGroup()
        					.addGap(63)
        					.addComponent(requestBtn)))
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_statusPanel.setVerticalGroup(
        	gl_statusPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_statusPanel.createSequentialGroup()
        			.addComponent(showCheckbox)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_statusPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(intervalLbl)
        				.addComponent(intervalDb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addGroup(gl_statusPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(currentLabel)
        				.addComponent(currentMetocDataLbl))
        			.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        			.addComponent(requestBtn)
        			.addContainerGap())
        );
        statusPanel.setLayout(gl_statusPanel);
        getContentPane().setLayout(groupLayout);
        
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				closeBtn.requestFocus();
			}
		});
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveValues();
			}
		});
        
	}
}
