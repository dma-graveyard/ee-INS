package dk.frv.enav.ins.gui.ComponentPanels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.frv.enav.common.xml.nogo.types.NogoPolygon;
import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.gui.Panels.NoGoPanel;
import dk.frv.enav.ins.nogo.NogoHandler;

public class NoGoComponentPanel extends OMComponentPanel {

	private static final long serialVersionUID = 1L;
	private AisHandler aisHandler = null;
	private NogoHandler nogoHandler = null;

	private final NoGoPanel nogoPanel = new NoGoPanel();

	private JLabel statusLabel;
	private JLabel statLabel1;
	private JLabel statLabel2;
	private JLabel statLabel3;
	private JLabel statLabel4;

	public NoGoComponentPanel() {
		super();

		this.setMinimumSize(new Dimension(10, 195));

		nogoPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setBorder(null);

		setLayout(new BorderLayout(0, 0));
		add(nogoPanel, BorderLayout.NORTH);
		
		statusLabel = nogoPanel.getStatusLabel();
		statusLabel.setText("Inactive");
		
		statLabel1 = nogoPanel.getStatLabel1();
		statLabel2 = nogoPanel.getStatLabel2();
		statLabel3 = nogoPanel.getStatLabel3();
		statLabel4 = nogoPanel.getStatLabel4();
		
		statusLabel.setEnabled(false);
		statLabel1.setEnabled(false);
		statLabel2.setEnabled(false);
		statLabel3.setEnabled(false);
		statLabel4.setEnabled(false);

	}
	
	public void newRequest(){
		statusLabel.setEnabled(true);
		statLabel1.setEnabled(true);
		statLabel2.setEnabled(true);
		statLabel3.setEnabled(true);
		statLabel4.setEnabled(true);
		
		statusLabel.setText("Connecting...");
		statusLabel.setForeground(Color.YELLOW);
		statLabel1.setText("Requesting NoGo");
		statLabel2.setText("");
		statLabel3.setText("Please standby");
		statLabel4.setText("");
	}

	
	/**
	 * Errorcode -1 means server experinced a timeout 
	 * Errorcode 0 means everything went ok 
	 * Errorcode 1 is the standby message 
	 * Errorcode 17 means no data 
	 * Errorcode 18 means no tide data
	 * @param nogoFailed 
	 * @param errorCode Own
	 * @param errorCode  Target
	 * @param polygons own
	 * @param polygons target
	 * @param valid from 
	 * @param valid to 
	 * @param own draught 
	 * @param target draught 
	 * 
	 * @param completed
	 */
	public void requestCompleted(boolean nogoFailed, int errorCodeOwn, List<NogoPolygon> polygonsOwn, Date validFrom, Date validTo, Double draught){
		if (nogoFailed){
			statusLabel.setText("Failed");
			statusLabel.setForeground(Color.RED);
			statLabel1.setText("Connection to shore timed out");
			statLabel2.setText("Retrying");
			statLabel3.setText("in a few minutes");
			statLabel4.setText("");
		}else{
			if (errorCodeOwn == 17){
				statusLabel.setText("Failed");
				statusLabel.setForeground(Color.RED);
				statLabel1.setText("No data for region");
				statLabel2.setText("");
				statLabel3.setText("");
				statLabel4.setText("");
				return;
			}
			
			if (errorCodeOwn == 18){
				statusLabel.setText("Limited");
				statusLabel.setForeground(Color.YELLOW);
				statLabel1.setText("No tide data available for region");
				statLabel2.setText("");
				statLabel3.setText("");
				statLabel4.setText("");
				return;
			}

			if (errorCodeOwn == 0){
				statusLabel.setText("Success");
				statusLabel.setForeground(Color.GREEN);
				statLabel1.setText("Valid from " + validFrom);
				statLabel2.setText("Valid to " + validTo);
				statLabel3.setText("For draught: " + draught);
				statLabel4.setText("");
				return;
			}

			if (polygonsOwn.size() == 0){
				statusLabel.setText("Success, Entire region is Go");
				statusLabel.setForeground(Color.GREEN);
				statLabel1.setText("Valid from " + validFrom);
				statLabel2.setText("Valid to " + validTo);
				statLabel3.setText("For draught: " + draught);
				return;
			}
		}
	}
	
	public void inactive(){
		statusLabel.setEnabled(false);
		statLabel1.setEnabled(false);
		statLabel2.setEnabled(false);
		statLabel3.setEnabled(false);
		statLabel4.setEnabled(false);

		statusLabel.setText("Inactive");
		statLabel1.setText("");
		statLabel2.setText("");
		statLabel3.setText("");
		statLabel4.setText("");
	}
			
			
	
	@Override
	public void findAndInit(Object obj) {

		if (aisHandler == null && obj instanceof AisHandler) {
			aisHandler = (AisHandler) obj;
		}
		if (nogoHandler == null && obj instanceof NogoHandler) {
			nogoHandler = (NogoHandler) obj;
		}
	}

}
