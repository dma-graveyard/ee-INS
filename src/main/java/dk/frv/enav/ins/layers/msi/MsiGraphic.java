package dk.frv.enav.ins.layers.msi;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.ins.msi.MsiHandler.MsiMessageExtended;

public class MsiGraphic extends OMGraphicList {
	private static final long serialVersionUID = 1L;
	MsiMessageExtended message;
	private MsiMessage msiMessage;
	//private MsiTextBox msiTextBox;
	
	public MsiGraphic(MsiMessageExtended message) {
		super();
		this.message = message; 
		this.msiMessage = message.msiMessage;
		
		// Create text box and hide it
//		msiTextBox = new MsiTextBox(msiMessage, message.acknowledged);
//		add(msiTextBox);
//		msiTextBox.setVisible(false);
		
		// Create symbol graphic
		MsiSymbolGraphic msiSymbolGrahic = new MsiSymbolGraphic(msiMessage);
		add(msiSymbolGrahic);
		
		// Create location grahic
		MsiLocationGraphic msiLocationGraphic = new MsiLocationGraphic(msiMessage);
		add(msiLocationGraphic);
	}
	
//	public void showTextBox(){
//		msiTextBox.setVisible(true);
//	}
//	
//	public void hideTextBox(){
//		msiTextBox.setVisible(false);
//	}
//	
//	public boolean getTextBoxVisible(){
//		return msiTextBox.isVisible();
//	}
	
	public MsiMessageExtended getMessage() {
		return message;
	}
}
