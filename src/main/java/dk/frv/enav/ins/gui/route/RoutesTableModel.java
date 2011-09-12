package dk.frv.enav.ins.gui.route;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteManager;

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
