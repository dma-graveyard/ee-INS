package dk.frv.enav.ins.gui.mainFramePanels;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import dk.frv.enav.ins.EeINS;

public class LogoPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JLabel euBalticLogo = new JLabel("");
	private final JLabel efficienseaLogo = new JLabel("");
	


	public LogoPanel(){
		setBorder(null);
		
		efficienseaLogo.setIcon(new ImageIcon(EeINS.class.getResource("/images/sensorPanel/efficiensea.png")));
		euBalticLogo.setIcon(new ImageIcon(EeINS.class.getResource("/images/sensorPanel/euBaltic.png")));
//		GroupLayout groupLayout = new GroupLayout(this);
//		groupLayout.setHorizontalGroup(
//			groupLayout.createParallelGroup(Alignment.LEADING)
//				.addGroup(groupLayout.createSequentialGroup()
//					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
//						.addComponent(euBalticLogo, Alignment.TRAILING)
//						.addComponent(efficienseaLogo, Alignment.TRAILING))
//					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
//		);
//		groupLayout.setVerticalGroup(
//			groupLayout.createParallelGroup(Alignment.TRAILING)
//				.addGroup(groupLayout.createSequentialGroup()
//					.addContainerGap(730, Short.MAX_VALUE)
//					.addComponent(efficienseaLogo)
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addComponent(euBalticLogo))
//		);
//		setLayout(groupLayout);
//		
		
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(euBalticLogo)
						.addComponent(efficienseaLogo))
					.addContainerGap(183, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap(153, Short.MAX_VALUE)
					.addComponent(efficienseaLogo)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(euBalticLogo))
		);
		this.setLayout(groupLayout);
		
	}
}
