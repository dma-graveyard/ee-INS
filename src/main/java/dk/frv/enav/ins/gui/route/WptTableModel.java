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

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import dk.frv.enav.ins.common.FormatException;
import dk.frv.enav.ins.common.Heading;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.common.util.ParseUtils;
import dk.frv.enav.ins.route.ActiveRoute;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteLeg;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RouteWaypoint;
import dk.frv.enav.ins.route.RoutesUpdateEvent;

/**
 * Table model for the list waypoints in route RoutePropertiesDialog
 */
public class WptTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(WptTableModel.class);

	private static final String[] COLUMN_NAMES = { "Name", "Lat", "Lon", "Rng", "Brg", "Heading", "Rad", "ROT", "XTD P", "XTD S",
			"SOG", "WP TTG", "WP ETA" };

	private Route route;
	private JDialog dialog;
	private RouteManager routeManager;

	public WptTableModel(JDialog dialog, RouteManager routeManager) {
		super();
		this.dialog = dialog;
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
		return route.getWaypoints().size();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (rowIndex == getRowCount() - 1) {
			return (columnIndex == 0 || columnIndex == 1 || columnIndex == 2);
		} else {
			return !(columnIndex == 3 || columnIndex == 4 || columnIndex == 7 || columnIndex == 11 || columnIndex == 12);
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		RouteWaypoint wpt = route.getWaypoints().get(rowIndex);
		RouteLeg outLeg = wpt.getOutLeg();

		try {
		switch (columnIndex) {
		case 0:
			wpt.setName((String) aValue);
			break;
		case 1:
			wpt.getPos().setLatitude(parseLat((String)aValue));
			break;
		case 2:
			wpt.getPos().setLongitude(parseLon((String)aValue));
			break;
		case 5:
			String head = (String)aValue;
			if (head != null && head.equalsIgnoreCase("GC")) {
				outLeg.setHeading(Heading.GC);
			} else {
				outLeg.setHeading(Heading.RL);
			}
			break;
		case 6:
			wpt.setTurnRad(parseDouble((String)aValue));
			break;
		case 8:
			outLeg.setXtdPort(parseDouble((String)aValue) / 1852.0);
			break;
		case 9:
			outLeg.setXtdStarboard(parseDouble((String)aValue) / 1852.0);
			break;
		case 10:
			outLeg.setSpeed(parseDouble((String)aValue));
			break;
		}
		
		} catch (FormatException e) {
			JOptionPane.showMessageDialog(this.dialog, "Error in entered value", "Input error", JOptionPane.ERROR_MESSAGE);
			return;
		}
				
		if (route instanceof ActiveRoute) {
			ActiveRoute activeRoute = (ActiveRoute)route;
			activeRoute.calcValues(true);
			routeManager.changeActiveWp(activeRoute.getActiveWaypointIndex());
		} else {
			route.calcValues(true);
			routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_CHANGED);
		}
		fireTableDataChanged();				
	}
	
	private static double parseDouble(String str) throws FormatException {
		str = str.replaceAll(",", ".");
		String[] parts = StringUtils.split(str, " ");
		return ParseUtils.parseDouble(parts[0]);
	}
	
	private static double parseLat(String latStr) throws FormatException {
		return ParseUtils.parseLatitude(latStr);
	}
	
	private static double parseLon(String lonStr) throws FormatException {
		return ParseUtils.parseLongitude(lonStr);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// Get wpt
		RouteWaypoint wpt = route.getWaypoints().get(rowIndex);
		if (wpt == null) {
			LOG.error("Unknown WPT with id " + rowIndex);
			return new String("");
		}
		RouteLeg outLeg = wpt.getOutLeg();

		switch (columnIndex) {
		case 0:
			return Formatter.formatString(wpt.getName());
		case 1:
			return Formatter.latToPrintable(wpt.getPos().getLatitude());
		case 2:
			return Formatter.lonToPrintable(wpt.getPos().getLongitude());
		case 3:
			return Formatter.formatDistNM(route.getWpRng(rowIndex));
		case 4:
			return Formatter.formatDegrees(route.getWpBrg(wpt), 2);
		case 5:
			return Formatter.formatHeading(wpt.getHeading());
		case 6:
			return Formatter.formatDistNM(wpt.getTurnRad());
			// case 7: return Formatter.formatRot(wpt.getRot());
		case 7:
			return Formatter.formatRot(null);
		case 8:
			return Formatter.formatMeters((outLeg != null) ? outLeg.getXtdPortMeters() : null);
		case 9:
			return Formatter.formatMeters((outLeg != null) ? outLeg.getXtdStarboardMeters() : null);
		case 10:
			return Formatter.formatSpeed((outLeg != null) ? outLeg.getSpeed() : null);
		case 11:
			return Formatter.formatTime(route.getWpTtg(rowIndex));
		case 12:
			return Formatter.formatShortDateTime(route.getWpEta(rowIndex));
		default:
			break;
		}

		return new String("");
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Object value = getValueAt(0, columnIndex);
		if (value == null) {
			return String.class;
		}
		return value.getClass();
	}

	public void setRoute(Route route) {
		this.route = route;
	}

}
