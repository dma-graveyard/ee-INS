package dk.frv.enav.ins.gui.msi;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import dk.frv.enav.common.xml.msi.MsiLocation;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.msi.MsiHandler;

public class MsiTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	private static final String[] COLUMN_NAMES = {"ID", "Ver", "Priority", "Updated", "Main Area", "Message", "Valid from", "Valid until"};
	
	private MsiHandler msiHandler;
	private List<MsiHandler.MsiMessageExtended> messages;
	
	public MsiTableModel(MsiHandler msiHandler) {
		super();
		this.msiHandler = msiHandler;
		updateMessages();
	}
	
	public void updateMessages() {
		messages = msiHandler.getMessageList();
	}
	
	public List<MsiHandler.MsiMessageExtended> getMessages() {
		return messages;
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
		return msiHandler.getMessages().size();
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
		MsiHandler.MsiMessageExtended message = messages.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return message.msiMessage.getId();
		case 1:
			return message.msiMessage.getVersion();
		case 2:
			return message.msiMessage.getPriority();
		case 3:
			Date updated = message.msiMessage.getUpdated(); 
			if (updated == null) {
				updated = message.msiMessage.getCreated();
			}
			return Formatter.formatShortDateTime(updated);
		case 4:
			MsiLocation location = message.msiMessage.getLocation();
			if (location != null) {
				return location.getArea();
			}
			return "";
		case 5:
			String msgShort = message.msiMessage.getMessage();
			if (msgShort == null) {
				msgShort = "";
			}
			if (msgShort.length() > 32) {
				msgShort = msgShort.substring(0, 28) + " ...";
			}
			return msgShort;
		case 6:
			return Formatter.formatShortDateTime(message.msiMessage.getValidFrom());
		case 7:
			return Formatter.formatShortDateTime(message.msiMessage.getValidTo());
		default:
			return "";
				
		}
	}
	
}
