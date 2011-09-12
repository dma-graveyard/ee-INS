package dk.frv.enav.ins.gui.sensors;

import java.awt.Color;
import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class ScalePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JLabel scaleLabel = new JLabel("Scale N/A");
	private final JLabel timeLabel = new JLabel("N/A");
	
	public ScalePanel(){
		super();
		
		setBorder(new LineBorder(Color.GRAY));
		timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scaleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scaleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(10)
							.addComponent(timeLabel, GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(scaleLabel, GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addComponent(timeLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scaleLabel)
					.addContainerGap(246, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
	
	public JLabel getTimeLabel() {
		return timeLabel;
	}
	
	public JLabel getScaleLabel() {
		return scaleLabel;
	}
	
}
