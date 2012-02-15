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
package dk.frv.enav.ins.gui.ais;

import java.text.NumberFormat;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.ais.AisHandler.AisMessageExtended;

/**
 * Table model for MSI dialog
 */
public class AisTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	private static final String[] COLUMN_NAMES = {"Name", "MMSI", "HDG", "DST"};
	
	private AisHandler aisHandler;
	private List<AisHandler.AisMessageExtended> ships;
	
	
	public AisTableModel(AisHandler aisHandler) {
		super();
		this.aisHandler = aisHandler;
	}
	
	public void updateShips() {
		//Get new list from store/handler
		ships = aisHandler.getShipList();
	}
	
	public List<AisMessageExtended> getShips() {
		if (ships != null) {
		return ships;
		}
		else{
			//updateShips();
			return ships;
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public int getRowCount() {
		if (ships == null) {
			updateShips();
		}
		return ships.size();
		//return 0;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Object value = getValueAt(0, columnIndex);
		if (value == null) {
			return String.class;
		}
		return value.getClass();
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		AisMessageExtended ship = ships.get(rowIndex);

		switch (columnIndex) {
		case 0:
			//return "Name"; 
			return ship.name;
		case 1:
			//return "MMSI";
			return ship.MMSI;
		case 2:
			//return "???";
			return ship.hdg;
		case 3:
		      NumberFormat nf = NumberFormat.getInstance();  
		      nf.setMaximumFractionDigits(2);// set as you need  
		      //String dst = nf.format(ship.dst);  
			return ship.dst;
			//return "DST";
		default:
			return "";
				
		}
	}
	
}
