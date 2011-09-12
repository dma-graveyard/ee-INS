package dk.frv.enav.ins.gui.sensors;

import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

public class GPSPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel gpsTitleLabel = new JLabel("GPS");
	private JLabel latTitleLabel = new JLabel("LAT");
	private JLabel latLabel = new JLabel("N/A");
	private JLabel lonTitleLabel = new JLabel("LON");
	private JLabel lonLabel = new JLabel("N/A");
	private JLabel sogTitleLabel = new JLabel("SOG");
	private JLabel sogLabel = new JLabel("N/A");
	private JLabel cogLabel = new JLabel("N/A");
	private JLabel cogTitleLabel = new JLabel("COG");
	private JLabel hdgLabel = new JLabel("N/A");
	private JLabel hdgTitleLabel = new JLabel("HDG");
	
	public GPSPanel() {
		
		gpsTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gpsTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

		latTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		latTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		latLabel.setHorizontalAlignment(SwingConstants.CENTER);
		latLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		lonTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		lonTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				
		lonLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lonLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				
		sogTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		sogTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);

		sogLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		sogLabel.setHorizontalAlignment(SwingConstants.RIGHT);
				
		cogTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		cogTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		cogLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		cogLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		hdgTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		hdgTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		hdgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		hdgLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(gpsTitleLabel, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(hdgTitleLabel)
								.addComponent(cogTitleLabel)
								.addComponent(sogTitleLabel)
								.addComponent(lonTitleLabel)
								.addComponent(latTitleLabel))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(hdgLabel)
								.addComponent(cogLabel)
								.addComponent(latLabel)
								.addComponent(lonLabel)
								.addComponent(sogLabel))))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(gpsTitleLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(latTitleLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lonTitleLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(sogTitleLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(cogTitleLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(hdgTitleLabel))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(latLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lonLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(sogLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(cogLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(hdgLabel)))
					.addContainerGap(190, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
	
	public JLabel getLatLabel() {
		return latLabel;
	}
	
	public JLabel getLonLabel() {
		return lonLabel;
	}
	
	public JLabel getSogLabel() {
		return sogLabel;
	}
	
	public JLabel getCogLabel() {
		return cogLabel;
	}
	
	public JLabel getHdgLabel() {
		return hdgLabel;
	}
}
