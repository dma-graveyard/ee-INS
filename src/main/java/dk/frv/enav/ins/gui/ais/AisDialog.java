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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.StringUtils;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage;
import dk.frv.enav.common.xml.msi.MsiLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.common.xml.msi.MsiPoint;
import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.ais.AisHandler.AisMessageExtended;
import dk.frv.enav.ins.ais.AisTarget;
import dk.frv.enav.ins.ais.IAisTargetListener;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.gui.ComponentFrame;
import dk.frv.enav.ins.gui.msi.MsiTableCellRenderer;
import dk.frv.enav.ins.layers.ais.AisLayer;
import dk.frv.enav.ins.layers.msi.MsiLayer;
import dk.frv.enav.ins.msi.IMsiUpdateListener;
import dk.frv.enav.ins.msi.MsiHandler;
import dk.frv.enav.ins.msi.MsiHandler.MsiMessageExtended;

/**
 * MSI dialog
 */
public class AisDialog extends ComponentFrame implements ListSelectionListener, ActionListener, IAisTargetListener {
	private static final long serialVersionUID = 1L;

	private MsiHandler msiHandler;
	private AisHandler aisHandler;
	
	private JButton closeBtn;
	private JButton gotoBtn;
	
	private JTable aisTable;
	private JScrollPane aisScrollPane;
	
	private JTextPane aisDetails;
	private JScrollPane detailsScrollPane; 
	
	//private MsiTableModel msiTableModel;
	private AisTableModel aisTableModel;
	private ListSelectionModel aisSelectionModel;
	
	private JPanel detailsPanel;
	
	public AisDialog(Window parent) {
		super();
		setTitle("Maritime Safety Information");
		setSize(580, 560);
		
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setLocationRelativeTo(parent);
	}
	
	public void showMessage(int msgId) {
		setVisible(true);
		List<AisMessageExtended> messages = aisTableModel.getShips();
		for (int i = 0; i < messages.size(); i++) {
			AisMessageExtended message = messages.get(i);

				return;
			}			
		}
	
	
	private void initGui() {
	
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);
        gotoBtn = new JButton("Goto");
        gotoBtn.addActionListener(this);
        
		detailsPanel = new JPanel();
        //detailsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        
        aisDetails = new JTextPane();
        aisDetails.setContentType("text/html");
        aisDetails.setEditable(true);
        
        detailsScrollPane = new JScrollPane(aisDetails);
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
		//aisTable.getColumnModel().getColumn(0).setPreferredWidth(50);
		//aisTable.getColumnModel().getColumn(1).setPreferredWidth(50);
		//aisTable.getColumnModel().getColumn(2).setPreferredWidth(50);
		//aisTable.getColumnModel().getColumn(0).setCellRenderer(new MsiTableCellRenderer(msiHandler));
		aisSelectionModel = aisTable.getSelectionModel();
		aisTable.setSelectionModel(aisSelectionModel);
		aisTable.setAutoCreateRowSorter(true);
		aisSelectionModel = aisTable.getSelectionModel();
		aisSelectionModel.addListSelectionListener(this);
		aisTable.setSelectionModel(aisSelectionModel);		
		
		//int preSelected = msiHandler.getFirstNonAcknowledged();
		//setSelected(preSelected);


        
        
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
	
	private void setSelected(int selectedRow) {
		aisSelectionModel.setSelectionInterval(selectedRow, selectedRow);
		aisTable.scrollRectToVisible(aisTable.getCellRect(selectedRow, -1, true));
	}
	
	private void updateTable() {
		int selectedRow = aisTable.getSelectedRow();
		Long selectedMMSI = 0L;
		if (selectedRow >=0){
			selectedMMSI = (Long) aisTable.getValueAt(selectedRow, 1);
		}
			
		
		aisTableModel.updateShips();
		// Update table
		aisTableModel.fireTableDataChanged();
		if (selectedRow >= 0 && selectedRow < aisTable.getRowCount()) {
			setSelected(selectedRow);
		} else {
			if (selectedRow >= 0) {
				selectedRow = aisTable.getRowCount() - 1;
				setSelected(selectedRow);
			}
		}
		updateDetails();
		
		setSelection(selectedMMSI);
	}
	
	private void updateDetails() {
		//System.out.println("detailsSet");
		int selected = aisTable.getSelectedRow();
		if (selected >= 0 && aisHandler.getVesselTargets() != null){
			Object mmsi = aisTable.getValueAt(selected, 1);
			setDetails(aisHandler.getVesselTargets().get(mmsi));
		}
	}

	@SuppressWarnings("static-access")
	private void setDetails(VesselTarget vesselTarget) {
		if (vesselTarget == null) {
			aisDetails.setText("");
			return;
		}
		StringBuilder buf = new StringBuilder();
		GeoLocation aisLocation = vesselTarget.getPositionData().getPos();
		
		String name = "N/A";	
		String callsign = "N/A";
		String imo = "unknown";
		String type = "unknown";
		String destination = "unknown";
		String draught = "N/A";
		String trueHeading = "N/A";
		String length = "N/A";
		String width = "N/A";
		String navStatus = "N/A";
		String lastRecieved = "N/A";
		String eta = "N/A";
		Date currentDate = new Date();
		AisMessage aisMessage = null;
		
		if (vesselTarget.getStaticData() != null ){
			name = aisMessage.trimText(vesselTarget.getStaticData().getName());
			callsign = vesselTarget.getStaticData().getCallsign();
			imo = Long.toString(vesselTarget.getStaticData().getImo());
			type = Integer.toString(vesselTarget.getStaticData().getShipType());
			destination = aisMessage.trimText(vesselTarget.getStaticData().getDestination());
			draught = Float.toString(vesselTarget.getStaticData().getDraught());
			trueHeading = Float.toString(vesselTarget.getPositionData().getTrueHeading());
			length = Integer.toString(vesselTarget.getStaticData().getDimBow() + vesselTarget.getStaticData().getDimStern()) + " M";
			width = Integer.toString(vesselTarget.getStaticData().getDimPort() + vesselTarget.getStaticData().getDimStarboard()) + " M";
			navStatus = Integer.toString(vesselTarget.getPositionData().getNavStatus());
			lastRecieved = Long.toString((currentDate.getTime() - vesselTarget.getLastReceived().getTime()) / 60) + " seconds ago";
			eta = Long.toString(vesselTarget.getStaticData().getEta());
			
		}		

		buf.append("<table>");		
		buf.append("<tr><td><b>MMSI:</b></td><td>" + vesselTarget.getMmsi() + "</td></tr>");
		buf.append("<tr><td><b>AIS Unit:</b></td><td>" + "Class " + vesselTarget.getAisClass() + "</td></tr>");
		buf.append("<tr><td><b>Name:</b></td><td>" + name + "</td></tr>");
		buf.append("<tr><td><b>Call sign:</b></td><td>" + callsign + "</td></tr>");
		buf.append("<tr><td><b>Length:</b></td><td>" + length + "</td></tr>");
		buf.append("<tr><td><b>Width:</b></td><td>" + width + "</td></tr>");
		buf.append("<tr><td><b>Draught:</b></td><td>" + draught + "</td></tr>");		
		buf.append("<tr><td><b>Nav status:</b></td><td>" + navStatus + "</td></tr>");
		buf.append("<tr><td><b>Type:</b></td><td>" + type + "</td></tr>");
		buf.append("<tr><td><b>Position:</b></td><td>" + aisLocation.getLatitude() + aisLocation.getLongitude() + "</td></tr>");
		buf.append("<tr><td><b>Last Recieved:</b></td><td>" + lastRecieved + "</td></tr>");
		//buf.append("<tr><td><b>IMO:</b></td><td>" + imo + "</td></tr>");
		buf.append("<tr><td><b>Destination:</b></td><td>" + destination + "</td></tr>");
		buf.append("<tr><td><b>ETA::</b></td><td>" + eta + "</td></tr>");
		
		buf.append("<tr><td><b>Heading:</b></td><td>" + trueHeading + "</td></tr>");
		buf.append("<tr><td><b>COG:</b></td><td>" + vesselTarget.getPositionData().getCog() + "</td></tr>");
		buf.append("<tr><td><b>SOG:</b></td><td>" + vesselTarget.getPositionData().getSog() + "</td></tr>");
		buf.append("<tr><td><b>ROT:</b></td><td>" + vesselTarget.getPositionData().getRot() + "</td></tr>");

		buf.append("</table>");
		
		aisDetails.setText(buf.toString());
		
		aisDetails.setSelectionStart(0);
		aisDetails.setSelectionEnd(0);
	}


	public AisMessageExtended getMessage(int i) {
		List<AisMessageExtended> messages = aisTableModel.getShips();
		return messages.get(i);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	if (e.getSource() == gotoBtn) {
			//msiLayer.zoomTo(getMessage(msiTable.getSelectedRow()).AisMessage);
		System.out.println("gotoBtm clicked - not implemented yet");
		} else if (e.getSource() == closeBtn) {
			setVisible(false);

		}		
	}

	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof MsiHandler) {
			msiHandler = (MsiHandler)obj;
		}
		if (obj instanceof AisHandler) {
			aisHandler = (AisHandler)obj;
			aisHandler.addListener(this);
			initGui();
		}		
	}

	public void setSelection(long mmsi) {
		setSelected(getMMSISelection(mmsi));
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
		updateTable();	
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		updateDetails();
	}
	
}
	
