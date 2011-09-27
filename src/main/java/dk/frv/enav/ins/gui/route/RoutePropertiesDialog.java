/*
 * Copyright 2011 Danish Maritime Safety Administration. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
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
 * either expressed or implied, of Danish Maritime Safety Administration.
 * 
 */
package dk.frv.enav.ins.gui.route;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.common.util.ParseUtils;
import dk.frv.enav.ins.route.ActiveRoute;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RoutesUpdateEvent;

/**
 * Dialog with route properties   
 */
public class RoutePropertiesDialog extends JDialog implements ActionListener, Runnable, ListSelectionListener, TableModelListener, FocusListener, WindowListener {

    private static final long serialVersionUID = 1L;
    
    private Route route;
	private JPanel propertiesPanel;
	private JPanel waypointsPanel;
	private JButton closeBtn;
	private JLabel lblStartTime;
	private JLabel lblDestination;
	private JLabel lblTtg;
	private JTextField nameField;
	private JTextField departField;
	private JTextField totalDistField;
	private JTextField startTimeField;
	private JTextField destinationField;
	private JLabel lblEta;
	private JTextField ttgField;
	private JTextField etaField;

	private JLabel nameLabel;

	private JLabel departFromLabel;

	private JLabel totalDistLabel;
	private JButton btnZoomTo;
	private JButton btnDelete;
	private JButton btnActivate;

	private JScrollPane wptScrollPane;
	private JTable wptTable;
	private WptTableModel wptTableModel;
	private RouteManager routeManager;
	private ActiveRoute activeRoute = null;
    
    public RoutePropertiesDialog(Window parent, RouteManager routeManager, int routeId) {        
        super(parent, "Route Properties", Dialog.ModalityType.APPLICATION_MODAL);
        
        this.routeManager = routeManager;
        
        if (routeManager.isActiveRoute(routeId)) {
        	this.route = routeManager.getActiveRoute();        	
        	activeRoute = (ActiveRoute)this.route;
        } else {
        	this.route = routeManager.getRoute(routeId);
        }
        
        setSize(900, 500);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        
        initGui();
        
        initValues();
        
        (new Thread(this)).start();
        
        addWindowListener(this);
    }
    
    private void initValues() {
    	nameField.setText(route.getName());
    	departField.setText(route.getDeparture());
    	destinationField.setText(route.getDestination());    	
    	
    	updateDynamicValues();
    	
    	nameField.addFocusListener(this);
    	departField.addFocusListener(this);
    	destinationField.addFocusListener(this);
    	startTimeField.addFocusListener(this);
    }
    
    
    private void updateDynamicValues() {
    	// Get start time or default now
    	route.adjustStartTime();
    	Date starttime = route.getStarttime();
    	if (!startTimeField.hasFocus()) {
    		startTimeField.setText(Formatter.formatLongDateTime(starttime));
    	}
    	ttgField.setText(Formatter.formatTime(route.getTtg()));
    	etaField.setText(Formatter.formatShortDateTime(route.getEta(starttime)));
    	totalDistField.setText(Formatter.formatDistNM(route.getDtg()));
    	
    	if (activeRoute != null) {
    		activeRoute.reCalcRemainingWpEta();
    	}
    }
    
    private void updateButtons() {
    	boolean wpSelected = (wptTable.getSelectedRow() >= 0);
    	    	
    	btnZoomTo.setEnabled(wpSelected);
    	btnDelete.setEnabled(wpSelected);
    	btnActivate.setEnabled(wpSelected);
    	btnActivate.setVisible(activeRoute != null);
    	startTimeField.setEnabled(activeRoute == null);
    	
    }
    
    private void updateTable() {
		// Update wp table
    	wptTableModel.fireTableDataChanged();
		updateButtons();
	}
    
    private void close() {
    	dispose();
    }
    

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() == nameField) {
			route.setName(nameField.getText());
		} else if (e.getSource() == departField) {
			route.setDeparture(departField.getText());
		} else if (e.getSource() == destinationField) {
			route.setDestination(destinationField.getText());
		} else if (e.getSource() == startTimeField) {
			Date d = ParseUtils.parseVariuosDateTime(startTimeField.getText());
			if (d == null) {
				JOptionPane.showMessageDialog(this, "Date in wrong format", "Date error", JOptionPane.ERROR_MESSAGE);
			} else {
				route.setStarttime(d);
				updateDynamicValues();
			}
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
	}
    
    private void activateWp() {
    	int index = wptTable.getSelectedRow();
    	if (index < 0) return;    	
    	routeManager.changeActiveWp(index);    	
    	updateTable();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeBtn) {
        	close();
        } else if (e.getSource() == btnActivate) {
        	activateWp();
        } else if (e.getSource() == btnDelete) {
        	int index = wptTable.getSelectedRow();
        	if (index < 0) return;    	
        	route.deleteWaypoint(index);
        	routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_WAYPOINT_DELETED);
        	updateTable();
        }
    }
    
    @Override
    public void run() {
//    	while (true) {
//			EeINS.sleep(10000);
//			updateDynamicValues();
//			wptTableModel.fireTableDataChanged();			
//		}
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e) {
    	updateButtons();    	
    }
    
    @Override
    public void tableChanged(TableModelEvent e) {
    	updateDynamicValues();
    	updateButtons();    	
    }
        
    private void initGui() {
    	propertiesPanel = new JPanel();
        propertiesPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Properties", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        waypointsPanel = new JPanel();
        waypointsPanel.setBorder(new TitledBorder(null, "Waypoints", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);
        
        wptTable = new JTable();
        wptTable.setShowHorizontalLines(false);
        wptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        wptScrollPane = new JScrollPane(wptTable);        
        wptScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		wptScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);		
		wptTable.setFillsViewportHeight(true);
		// Uncomment these lines to be able to edit with WindowBuilder
		wptTableModel = new WptTableModel(this, routeManager);
		wptTableModel.addTableModelListener(this);
		wptTableModel.setRoute(route);
		wptTable.setModel(wptTableModel);
		wptTable.getSelectionModel().addListSelectionListener(this);
		
		btnZoomTo = new JButton("Zoom to");
		btnZoomTo.addActionListener(this);
        btnDelete = new JButton("Delete");
        btnDelete.addActionListener(this);
        btnActivate = new JButton("Activate");
        btnActivate.addActionListener(this);
        
        nameLabel = new JLabel("Name");        
        departFromLabel = new JLabel("Depart from");        
        totalDistLabel = new JLabel("Total dist");
        lblStartTime = new JLabel("Start time");
        lblDestination = new JLabel("Destination");
        lblTtg = new JLabel("TTG");
        nameField = new JTextField();
        nameField.setColumns(10);
        departField = new JTextField();
        departField.setColumns(10);
        totalDistField = new JTextField();
        totalDistField.setEnabled(false);
        totalDistField.setEditable(false);
        totalDistField.setColumns(10);
        startTimeField = new JTextField();
        startTimeField.setColumns(10);
        destinationField = new JTextField();
        destinationField.setColumns(10);
        lblEta = new JLabel("ETA");
        ttgField = new JTextField();
        ttgField.setEditable(false);
        ttgField.setEnabled(false);
        ttgField.setColumns(10);
        etaField = new JTextField();
        etaField.setEditable(false);
        etaField.setEnabled(false);
        etaField.setColumns(10);
        
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
        	groupLayout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        				.addComponent(propertiesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
        					.addComponent(closeBtn))
        				.addComponent(waypointsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        			.addContainerGap())
        );
        groupLayout.setVerticalGroup(
        	groupLayout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(propertiesPanel, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(waypointsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        			.addGap(13)
        			.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(closeBtn))
        			.addContainerGap())
        );
                
                
        GroupLayout gl_waypointsPanel = new GroupLayout(waypointsPanel);
        gl_waypointsPanel.setHorizontalGroup(
        	gl_waypointsPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_waypointsPanel.createSequentialGroup()
        			.addComponent(btnZoomTo)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(btnDelete, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(btnActivate, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
        			.addGap(597))
        		.addComponent(wptScrollPane, GroupLayout.DEFAULT_SIZE, 852, Short.MAX_VALUE)
        );
        gl_waypointsPanel.setVerticalGroup(
        	gl_waypointsPanel.createParallelGroup(Alignment.TRAILING)
        		.addGroup(gl_waypointsPanel.createSequentialGroup()
        			.addComponent(wptScrollPane, GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
        			.addGap(15)
        			.addGroup(gl_waypointsPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(btnZoomTo)
        				.addComponent(btnDelete)
        				.addComponent(btnActivate, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)))
        );
        
        
        waypointsPanel.setLayout(gl_waypointsPanel);        
        
        GroupLayout gl_propertiesPanel = new GroupLayout(propertiesPanel);
        gl_propertiesPanel.setHorizontalGroup(
        	gl_propertiesPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_propertiesPanel.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(gl_propertiesPanel.createParallelGroup(Alignment.TRAILING, false)
        				.addComponent(totalDistLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        				.addComponent(nameLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        				.addComponent(departFromLabel, Alignment.LEADING))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_propertiesPanel.createParallelGroup(Alignment.LEADING, false)
        				.addComponent(nameField, GroupLayout.PREFERRED_SIZE, 229, GroupLayout.PREFERRED_SIZE)
        				.addComponent(departField, 229, 229, 229)
        				.addComponent(totalDistField))
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addGroup(gl_propertiesPanel.createParallelGroup(Alignment.LEADING, false)
        				.addComponent(lblDestination)
        				.addComponent(lblStartTime, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        				.addComponent(lblTtg, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_propertiesPanel.createParallelGroup(Alignment.LEADING)
        				.addGroup(gl_propertiesPanel.createParallelGroup(Alignment.TRAILING, false)
        					.addComponent(destinationField, Alignment.LEADING)
        					.addComponent(startTimeField, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 192, GroupLayout.PREFERRED_SIZE))
        				.addGroup(gl_propertiesPanel.createSequentialGroup()
        					.addComponent(ttgField, GroupLayout.PREFERRED_SIZE, 192, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addComponent(lblEta, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
        					.addGap(4)
        					.addComponent(etaField, GroupLayout.PREFERRED_SIZE, 209, GroupLayout.PREFERRED_SIZE)))
        			.addContainerGap(32, Short.MAX_VALUE))
        );
        gl_propertiesPanel.setVerticalGroup(
        	gl_propertiesPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_propertiesPanel.createSequentialGroup()
        			.addGroup(gl_propertiesPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(nameLabel)
        				.addComponent(lblStartTime)
        				.addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(startTimeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_propertiesPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(departFromLabel)
        				.addComponent(lblDestination)
        				.addComponent(departField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(destinationField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_propertiesPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(totalDistLabel)
        				.addComponent(lblTtg)
        				.addComponent(totalDistField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(ttgField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(lblEta)
        				.addComponent(etaField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addContainerGap(17, Short.MAX_VALUE))
        );
        propertiesPanel.setLayout(gl_propertiesPanel);
        getContentPane().setLayout(groupLayout);
        
        updateButtons();        
    }

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		routeManager.validateMetoc(route);
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}
}
