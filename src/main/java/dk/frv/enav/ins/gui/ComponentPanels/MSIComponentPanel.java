package dk.frv.enav.ins.gui.ComponentPanels;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.gui.BlinkingLabel;
import dk.frv.enav.ins.gui.Panels.MSIPanel;
import dk.frv.enav.ins.msi.IMsiUpdateListener;
import dk.frv.enav.ins.msi.MsiHandler;

public class MSIComponentPanel extends OMComponentPanel implements
IMsiUpdateListener {

	private static final long serialVersionUID = 1L;
	private MsiHandler msiHandler = null;
	
	private final MSIPanel msiPanel = new MSIPanel();
	private BlinkingLabel msiIcon;
	private JLabel msgLabel;
	private JLabel filterLabel;
	
	
	public MSIComponentPanel() {
		super();
		msiPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setBorder(null);
		
		msiIcon = msiPanel.getMsiIcon();
		msgLabel = msiPanel.getMsgLabel();
		filterLabel = msiPanel.getFilter();
		
		setLayout(new BorderLayout(0, 0));
		add(msiPanel, BorderLayout.NORTH);
	}

	
	@Override
	public void findAndInit(Object obj) {

		if (msiHandler == null && obj instanceof MsiHandler) {
			msiHandler = (MsiHandler)obj;
			msiUpdate();
		}
	}


	@Override
	public void msiUpdate() {
		
		if (EeINS.getSettings().getEnavSettings().isMsiFilter()) {
			msgLabel.setText(Integer.toString(msiHandler.getUnAcknowledgedFilteredMSI()));
		}else{
			msgLabel.setText(Integer.toString(msiHandler.getUnAcknowledgedMSI()));
		}
		
		if (EeINS.getSettings().getEnavSettings().isMsiFilter()) {
			filterLabel.setText("On");
		}else{
			filterLabel.setText("Off");
		}
//			int firstUnAckFiltered = msiHandler.getFirstNonAcknowledgedFiltered();
//			// There are no MSI to acknowledge
//			if (firstUnAckFiltered != -1) {
//				MsiMessageExtended msiMessageFiltered = msiHandler.getFilteredMessageList().get(firstUnAckFiltered);
////				notifyMsgId = msiMessageFiltered.msiMessage.getMessageId();
//				encText = msiMessageFiltered.msiMessage.getEncText();
//			}
//
//		} else {
//			int firstUnAck = msiHandler.getFirstNonAcknowledged();
//			MsiMessageExtended msiMessage = msiHandler.getMessageList().get(firstUnAck);
////			notifyMsgId = msiMessage.msiMessage.getMessageId();
//			encText = msiMessage.msiMessage.getEncText();
//		}
//		
		

		if (msiHandler.isPendingImportantMessages()) {
			msiIcon.setVisible(true);
			msiIcon.setBlink(true);
	
		} else {
			msiIcon.setBlink(false);
		}
		
		
	}
	

}
