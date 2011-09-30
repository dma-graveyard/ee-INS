package dk.frv.enav.ins.gui.msi;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.msi.MsiHandler;
import dk.frv.enav.ins.msi.MsiHandler.MsiMessageExtended;

/**
 * Cell coloring for MSI messages
 */
public class MsiTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;
	
	private MsiHandler msiHandler;
	
	public MsiTableCellRenderer(MsiHandler msiHandler) {
		this.msiHandler = msiHandler;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if (column == 0) {
			boolean acked = false;
			if (row >= 0) {
				MsiMessageExtended msg;
				if(EeINS.getSettings().getEnavSettings().isMsiFilter()) {
					msg = msiHandler.getFilteredMessageList().get(row);
				} else {
					msg = msiHandler.getMessageList().get(row);
				}
				if (msg != null) {
					acked = msg.acknowledged;
				}
			}
			
			if (!acked) {
				if (isSelected) {
					cell.setBackground(new Color(128, 0, 0));
				} else {
					cell.setBackground(Color.RED);
				}
				cell.setForeground(Color.WHITE);
			} else {
				if (isSelected) {
					cell.setBackground(new Color(0, 128, 0));
				} else {
					cell.setBackground(Color.GREEN);
				}
				cell.setForeground(Color.WHITE);
			}
		}
		
		return this;
	}

}
