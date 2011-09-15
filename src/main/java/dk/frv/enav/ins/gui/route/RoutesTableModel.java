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

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteManager;

/**
 * Table model for RouteManagerDialog
 */
public class RoutesTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(RoutesTableModel.class);
	
	private static final String[] COLUMN_NAMES = {"Name", "Destination", "Visible"};
	
	private RouteManager routeManager;
	
	public RoutesTableModel(RouteManager routeManager) {
		super();
		this.routeManager = routeManager;
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
		return routeManager.getRouteCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Route route = routeManager.getRoutes().get(rowIndex);
		switch (columnIndex) {
		case 0: return Formatter.formatString(route.getName());
		case 1: return Formatter.formatString(route.getDestination());
		case 2: return route.isVisible();
		default:
			LOG.error("Unknown column " + columnIndex);
			return new String("");
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Route route = routeManager.getRoutes().get(rowIndex);		
		switch (columnIndex) {
		case 2:
			route.setVisible((Boolean)aValue);
			if (rowIndex == routeManager.getActiveRouteIndex()) {
				routeManager.getActiveRoute().setVisible((Boolean)aValue);
			}
			fireTableCellUpdated(rowIndex, columnIndex);
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		//return (columnIndex == 2 && rowIndex != routeManager.getActiveRouteIndex());
		return (columnIndex == 2);
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	}

}
