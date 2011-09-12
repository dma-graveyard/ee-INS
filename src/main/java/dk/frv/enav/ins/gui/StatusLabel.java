package dk.frv.enav.ins.gui;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.status.ComponentStatus;
import dk.frv.enav.ins.status.IStatusComponent;

public class StatusLabel extends JLabel {
	
	private static final long serialVersionUID = 1L;	
	private static Map<ComponentStatus.Status, ImageIcon> imageMap = new HashMap<ComponentStatus.Status, ImageIcon>();	
	static {		
		imageMap.put(ComponentStatus.Status.OK, new ImageIcon(EeINS.class.getResource("/images/status/OK.png")));
		imageMap.put(ComponentStatus.Status.ERROR, new ImageIcon(EeINS.class.getResource("/images/status/ERROR.png")));
		imageMap.put(ComponentStatus.Status.PARTIAL, new ImageIcon(EeINS.class.getResource("/images/status/PARTIAL.png")));
		imageMap.put(ComponentStatus.Status.UNKNOWN, new ImageIcon(EeINS.class.getResource("/images/status/UNKNOWN.png")));
	}
	
	private static final Font font = new Font("Tahoma", Font.PLAIN, 9);
	
	public StatusLabel(String name) {		
		super(name);
		setFont(font);
		setHorizontalTextPosition(SwingConstants.LEFT);
		setIcon(imageMap.get(ComponentStatus.Status.UNKNOWN));
	}
	
	public void updateStatus(IStatusComponent statusComponent) {
		ComponentStatus componentStatus = statusComponent.getStatus();
		setIcon(imageMap.get(componentStatus.getStatus()));
		String shortStatusText = componentStatus.getShortStatusText();
		setToolTipText(shortStatusText);
	}
	
}
