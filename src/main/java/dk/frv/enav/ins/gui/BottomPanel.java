package dk.frv.enav.ins.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.ais.AisTargets;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.gui.msi.MsiDialog;
import dk.frv.enav.ins.msi.IMsiUpdateListener;
import dk.frv.enav.ins.msi.MsiHandler;
import dk.frv.enav.ins.msi.MsiHandler.MsiMessageExtended;
import dk.frv.enav.ins.services.shore.ShoreServices;
import dk.frv.enav.ins.status.IStatusComponent;

public class BottomPanel extends OMComponentPanel implements IMsiUpdateListener, MouseListener, Runnable {

	private static final long serialVersionUID = 1L;
	private MsiHandler msiHandler;
	private MsiDialog msiDialog;
	private ShoreServices shoreServices;
	private AisTargets aisTargets;
	private GpsHandler gpsHandler;
	private StatusLabel gpsStatus;
	private StatusLabel aisStatus;
	private StatusLabel shoreServiceStatus;
	private BlinkingLabel msiIcon;
	private int notifyMsgId = -1;
	private JToolBar toolBar;
	private List<IStatusComponent> statusComponents = new ArrayList<IStatusComponent>();
	
	public BottomPanel() {
		super();
		setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, new Color(255, 255, 255)), new MatteBorder(1, 0, 0, 0, new Color(192, 192, 192))));
		FlowLayout flowLayout = (FlowLayout) getLayout();
		flowLayout.setVgap(2);
		flowLayout.setHgap(3);
		flowLayout.setAlignment(FlowLayout.RIGHT);
		
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		add(toolBar);
		
		gpsStatus = new StatusLabel("GPS");
		addToolbarComponent(gpsStatus);
		
		aisStatus = new StatusLabel("AIS");
		addToolbarComponent(aisStatus);
		
		shoreServiceStatus = new StatusLabel("Shore services");
		addToolbarComponent(shoreServiceStatus);
		
		ImageIcon[] msiAnim = new ImageIcon[2];
		msiAnim[0] = new ImageIcon(EeINS.class.getResource("/images/msi/msi_symbol_16.png"));
		msiAnim[1] = new ImageIcon(EeINS.class.getResource("/images/msi/blank.png"));
		msiIcon = new BlinkingLabel(400, msiAnim);
		addToolbarComponent(msiIcon);		
		
		new Thread(this).start();
	}
	
	private void addToolbarComponent(Component component) {
		Component horizontalStrut = Box.createHorizontalStrut(5);
		toolBar.add(horizontalStrut);
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator);		
		horizontalStrut = Box.createHorizontalStrut(5);
		toolBar.add(horizontalStrut);
		toolBar.add(component);
		component.addMouseListener(this);
	}

	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof MsiHandler) {
			msiHandler = (MsiHandler)obj;
			msiHandler.addListener(this);
		} else if (obj instanceof MsiDialog) {
			msiDialog = (MsiDialog)obj;
		} else if (obj instanceof AisTargets) {
			aisTargets = (AisTargets)obj;
			statusComponents.add(aisTargets);
		} else if (obj instanceof GpsHandler) {
			gpsHandler = (GpsHandler)obj;
			statusComponents.add(gpsHandler);
		} else if (obj instanceof ShoreServices) {
			shoreServices = (ShoreServices)obj;
			statusComponents.add(shoreServices);
		}
		
	}

	@Override
	public void msiUpdate() {
		if (msiHandler.isPendingImportantMessages()) {
			msiIcon.setBlink(true);
			int firstUnAck = msiHandler.getFirstNonAcknowledged();
			MsiMessageExtended msiMessage = msiHandler.getMessageList().get(firstUnAck);
			notifyMsgId = msiMessage.msiMessage.getMessageId();
			msiIcon.setToolTipText(msiMessage.msiMessage.getEncText());
		} else {
			notifyMsgId = -1;
			msiIcon.setBlink(false);
			msiIcon.setToolTipText(null); 			
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == msiIcon) {
			if (notifyMsgId > 0) {
				msiDialog.showMessage(notifyMsgId);
			} else {
				msiDialog.setVisible(true);
			}
		} else if (e.getSource() instanceof StatusLabel) {
			StatusDialog statusDialog = new StatusDialog();
			statusDialog.showStatus(statusComponents);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	@Override
	public void run() {
		while (true) {
			updateStatus();
			EeINS.sleep(3000);
		}
	}
	
	private void updateStatus() {
		if (gpsHandler != null) {
			gpsStatus.updateStatus(gpsHandler);
		}
		if (aisTargets != null) {
			aisStatus.updateStatus(aisTargets);
		}
		if (shoreServices != null) {
			shoreServiceStatus.updateStatus(shoreServices);
		}
	}

}
