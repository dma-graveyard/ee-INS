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
package dk.frv.enav.ins.gui.msi;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

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
				MsiMessageExtended msg = msiHandler.getMessageList().get(row);
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
