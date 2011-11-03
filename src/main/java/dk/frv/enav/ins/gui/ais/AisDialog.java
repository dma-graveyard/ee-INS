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
package dk.frv.enav.ins.gui.ais;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage;
import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.ais.AisHandler.AisMessageExtended;
import dk.frv.enav.ins.ais.AisTarget;
import dk.frv.enav.ins.ais.IAisTargetListener;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.ais.VesselTarget.AisClass;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.gui.ComponentFrame;
import dk.frv.enav.ins.layers.ais.AisLayer;

/**
 * AIS targets dialog
 */
public class AisDialog extends ComponentFrame implements ListSelectionListener, ActionListener, IAisTargetListener {
	private static final long serialVersionUID = 1L;

	private AisLayer aisLayer;
	private AisHandler aisHandler;
	
	private JButton closeBtn;
	private JButton gotoBtn;
	
	private JTable aisTable;
	private JTable aisTableDetails;
	private JScrollPane aisScrollPane;
	
	private JScrollPane detailsScrollPane; 
	
	private AisTableModel aisTableModel;
	private ListSelectionModel aisSelectionModel;
	
	private JPanel detailsPanel;
	
	public AisDialog(Window parent) {
		super();
		setTitle("AIS Vessel Target");
		setSize(580, 437);
		
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setLocationRelativeTo(parent);
	}

	private void initGui() {
	
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);
        gotoBtn = new JButton("Goto");
        gotoBtn.addActionListener(this);
        
		detailsPanel = new JPanel();
        //detailsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        
        String[] columnNames = {"Type", "Details"};
        
        Object[][] data = {
        	    {" MMSI", ""},
        	    {" AIS", ""},
        	    {" Name", ""},
        	    {" Call Sign", ""},
        	    {" Length", ""},
        	    {" Width", ""},
        	    {" Draught", ""},
        	    {" Nav status", ""},
        	    {" Type", ""},
        	    {" Cargo", ""},
        	    {" Lat", ""},
        	    {" Long", ""},
        	    {" Last Recieved", ""},
        	    {" Destination", ""},
        	    {" ETA", ""},
        	    {" Heading", ""},
        	    {" COG", ""},
        	    {" SOG", ""},
        	    {" ROT", ""}
        	};
        
        
        aisTableDetails = new JTable(data, columnNames);
        
        aisTableDetails.setBorder(new LineBorder(new Color(0, 0, 0)));
        aisTableDetails.setShowHorizontalLines(false);
        aisTableDetails.setEnabled(false);
        
        
        
        //detailsScrollPane = new JScrollPane(aisDetails);
        detailsScrollPane = new JScrollPane(aisTableDetails);
        detailsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        detailsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        //detailsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        
        aisTable = new JTable();
        
        aisTable.setBorder(new LineBorder(new Color(0, 0, 0)));
        aisTable.setShowHorizontalLines(false);
        aisTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        aisScrollPane = new JScrollPane(aisTable);
        aisScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		aisScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);		
		aisTable.setFillsViewportHeight(true);
		
		JLabel tagetsLabel = new JLabel("Targets");
		JLabel detailsLabel = new JLabel("Details");
		
		
		
	
		aisTableModel = new AisTableModel(aisHandler);		

		aisTable.setModel(aisTableModel);
		aisSelectionModel = aisTable.getSelectionModel();
		aisTable.setSelectionModel(aisSelectionModel);
//		aisTable.setAutoCreateRowSorter(true);
		aisSelectionModel = aisTable.getSelectionModel();
		aisSelectionModel.addListSelectionListener(this);
		aisTable.setSelectionModel(aisSelectionModel);		
		aisTable.setSelectionMode(0);
		
        
		GroupLayout gl_detailsPanel =  new GroupLayout(getContentPane());

		
		gl_detailsPanel.setHorizontalGroup(
				gl_detailsPanel.createParallelGroup(Alignment.TRAILING)
					.addGroup(gl_detailsPanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_detailsPanel.createParallelGroup(Alignment.TRAILING)
							.addGroup(gl_detailsPanel.createSequentialGroup()
								.addComponent(gotoBtn)
								.addPreferredGap(ComponentPlacement.RELATED, 420, Short.MAX_VALUE)
								.addComponent(closeBtn))
							.addGroup(gl_detailsPanel.createSequentialGroup()
								.addGroup(gl_detailsPanel.createParallelGroup(Alignment.LEADING)
									.addComponent(aisScrollPane, GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
									.addComponent(tagetsLabel))
								.addGap(14)
								.addGroup(gl_detailsPanel.createParallelGroup(Alignment.LEADING)
									.addComponent(detailsLabel)
									.addComponent(detailsScrollPane, GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))))
						.addContainerGap())
			);
		
		gl_detailsPanel.setVerticalGroup(
				gl_detailsPanel.createParallelGroup(Alignment.TRAILING)
					.addGroup(gl_detailsPanel.createSequentialGroup()
						.addGroup(gl_detailsPanel.createParallelGroup(Alignment.BASELINE)
							.addComponent(tagetsLabel)
							.addComponent(detailsLabel))
						.addGap(9)
						.addGroup(gl_detailsPanel.createParallelGroup(Alignment.TRAILING)
							.addComponent(detailsScrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
							.addComponent(aisScrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(gl_detailsPanel.createParallelGroup(Alignment.LEADING)
							.addComponent(gotoBtn)
							.addComponent(closeBtn))
						.addContainerGap())
			);

        detailsPanel.setLayout(gl_detailsPanel);
        getContentPane().setLayout(gl_detailsPanel);
                
	}
	
	private void setSelected(int selectedRow, boolean opening) {
		aisSelectionModel.setSelectionInterval(selectedRow, selectedRow);
		if (opening){
			aisTable.scrollRectToVisible(aisTable.getCellRect(selectedRow, -1, true));
		}
	}
	
	private void updateTable() throws InterruptedException {
		int selectedRow = -1;
		if (aisTable.getSize().height > 0){
			selectedRow = aisTable.getSelectedRow();
		}else{
			//System.out.println("Possible error occured");
			Thread.sleep(1);
		}
//		@SuppressWarnings("rawtypes")
//		RowSorter rs = aisTable.getRowSorter();

		Long selectedMMSI = 0L;
		if (selectedRow >=0){
			selectedMMSI = (Long) aisTable.getValueAt(selectedRow, 1);
		}
			
		
		aisTableModel.updateShips();
		// Update table
		aisTableModel.fireTableDataChanged();
		if (selectedRow >= 0 && selectedRow < aisTable.getRowCount()) {
			setSelected(selectedRow, false);
		} else {
			if (selectedRow >= 0) {
				selectedRow = aisTable.getRowCount() - 1;
				setSelected(selectedRow, false);
			}
		}
		updateDetails();
//		rs.allRowsChanged();
		setSelection(selectedMMSI, false);
	}
	
	private void updateDetails() {
		int selected = aisTable.getSelectedRow();
		if (selected >= 0 && aisHandler.getVesselTargets() != null){
			Object mmsi = aisTable.getValueAt(selected, 1);
			if (aisHandler.getVesselTargets().get(mmsi) != null) {
			setDetails(aisHandler.getVesselTargets().get(mmsi));
			}
		}
	}

	@SuppressWarnings("static-access")
	private void setDetails(VesselTarget vesselTarget) {

		GeoLocation aisLocation = vesselTarget.getPositionData().getPos();
		
		String name = "N/A";	
		String callsign = "N/A";
//		String imo = "unknown";
		String type = "unknown";
		String destination = "unknown";
		String draught = "N/A";
		String trueHeading = "N/A";
		String length = "N/A";
		String width = "N/A";
		String navStatus = "N/A";
		String lastRecieved = "N/A";
		String eta = "N/A";
		String cargo = "unknown";
		Date currentDate = new Date();
		AisMessage aisMessage = null;
		

		if (vesselTarget.getStaticData() != null ){
			name = aisMessage.trimText(vesselTarget.getStaticData().getName());
			callsign = aisMessage.trimText(vesselTarget.getStaticData().getCallsign());
//			imo = Long.toString(vesselTarget.getStaticData().getImo());
			type = vesselTarget.getStaticData().getShipType().prettyType();
			cargo = vesselTarget.getStaticData().getShipType().prettyCargo();
			destination = aisMessage.trimText(vesselTarget.getStaticData().getDestination());
			if (destination == null){
				destination = "unknown";
			}
			draught = Float.toString(vesselTarget.getStaticData().getDraught());
			trueHeading = Float.toString(vesselTarget.getPositionData().getTrueHeading());
			length = Integer.toString(vesselTarget.getStaticData().getDimBow() + vesselTarget.getStaticData().getDimStern()) + " M";
			width = Integer.toString(vesselTarget.getStaticData().getDimPort() + vesselTarget.getStaticData().getDimStarboard()) + " M";
			navStatus = vesselTarget.getPositionData().getEnumNavStatus().prettyStatus();
			lastRecieved = Long.toString((currentDate.getTime() - vesselTarget.getLastReceived().getTime()) / 1000) + " seconds ago";

			eta = Long.toString(vesselTarget.getStaticData().getEta());
		}
		
		updateTable(vesselTarget.getMmsi(), vesselTarget.getAisClass(), name, callsign, length, 
				width, draught, navStatus, type, cargo, Formatter.latToPrintable(aisLocation.getLatitude()), 
						Formatter.lonToPrintable(aisLocation.getLongitude()), lastRecieved, destination, 
								eta, trueHeading, vesselTarget.getPositionData().getCog(), 
								vesselTarget.getPositionData().getSog(), vesselTarget.getPositionData().getRot());
	}


	private void updateTable(long mmsi, AisClass aisClass, String name, String callSign,
			String length, String width, String draught, String navStatus, String type,
			String cargo, String lat, String longi, String lastRecieved, String destination,
			String eta, String trueHeading, float cog, float sog, float rot){
		
		if (!compare(aisTableDetails.getValueAt(0, 1), mmsi)){
		aisTableDetails.setValueAt(mmsi, 0, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(1, 1), "Class " + aisClass)){		
		aisTableDetails.setValueAt("Class " + aisClass, 1, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(2, 1), name)){
		aisTableDetails.setValueAt(name, 2, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(3, 1), callSign)){
		aisTableDetails.setValueAt(callSign, 3, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(4, 1), length)){
		aisTableDetails.setValueAt(length, 4, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(5, 1), width)){
		aisTableDetails.setValueAt(width, 5, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(6, 1), draught)){
		aisTableDetails.setValueAt(draught, 6, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(7, 1), navStatus)){
		aisTableDetails.setValueAt(navStatus, 7, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(8, 1), type)){
		aisTableDetails.setValueAt(type, 8, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(9, 1), cargo)){
		aisTableDetails.setValueAt(cargo, 9, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(10, 1), lat)){
		aisTableDetails.setValueAt(lat, 10, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(11, 1), callSign)){
		aisTableDetails.setValueAt(callSign, 11, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(12, 1), lastRecieved)){
		aisTableDetails.setValueAt(lastRecieved, 12, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(13, 1), destination)){
		aisTableDetails.setValueAt(destination, 13, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(14, 1), eta)){
		aisTableDetails.setValueAt(eta, 14, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(15, 1), trueHeading)){
		aisTableDetails.setValueAt(trueHeading, 15, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(16, 1), cog)){
		aisTableDetails.setValueAt(cog, 16, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(17, 1), sog)){
		aisTableDetails.setValueAt(sog, 17, 1);
		}
		
		if (!compare(aisTableDetails.getValueAt(18, 1), rot)){
		aisTableDetails.setValueAt(rot, 18, 1);
		}
		
	}
	
private boolean compare(Object value1, Object value2){
	if (value1.toString().equals(value2.toString())){
		return true;
	}
	return false;
}

	public AisMessageExtended getMessage(int i) {
		List<AisMessageExtended> messages = aisTableModel.getShips();
		return messages.get(i);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	if (e.getSource() == gotoBtn) {
		int selectedRow = aisTable.getSelectedRow();
		long selectedMMSI = (Long) aisTable.getValueAt(selectedRow, 1);
		
		aisLayer.zoomTo(aisHandler.getVesselTargets().get(selectedMMSI).getPositionData().getPos());
		} else if (e.getSource() == closeBtn) {
			setVisible(false);
		}		
	}

	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof AisLayer) {
			aisLayer = (AisLayer)obj;
		}
		if (obj instanceof AisHandler) {
			aisHandler = (AisHandler)obj;
			aisHandler.addListener(this);
			initGui();
		}		
	}

	public void setSelection(long mmsi, boolean opening) {
		setSelected(getMMSISelection(mmsi), opening);
	}
	
	public int getMMSISelection(long mmsi){
		for (int i = 0; i < aisTable.getRowCount(); i++){
			Long currentValue = (Long) aisTable.getValueAt(i, 1);
			if (currentValue == mmsi){
				return i;
			}
		}
		return 0;
	}

	@Override
	public void targetUpdated(AisTarget aisTarget) {
		try {
			updateTable();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		updateDetails();
	}
	
}
	
