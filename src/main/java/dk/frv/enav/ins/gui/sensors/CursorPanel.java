package dk.frv.enav.ins.gui.sensors;

import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

public class CursorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JLabel lblCursor = new JLabel("Cursor");
	private final JLabel curLatTitlelabel = new JLabel("LAT");
	private final JLabel curLonTitleLabel = new JLabel("LON");
	private final JLabel curLatLabel = new JLabel("N/A");
	private final JLabel curLonLabel = new JLabel("N/A");
	private final JLabel curCursTitleLabel = new JLabel("BRG");
	private final JLabel curDistTitleLabel = new JLabel("RNG");
	private final JLabel curCursLabel = new JLabel("N/A");
	private final JLabel curDistLabel = new JLabel("N/A");
	
	public CursorPanel(){
		lblCursor.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblCursor.setHorizontalAlignment(SwingConstants.CENTER);
		curLatTitlelabel.setHorizontalAlignment(SwingConstants.LEFT);
		curLatTitlelabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		curLatLabel.setHorizontalAlignment(SwingConstants.CENTER);
		curLatLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		curLonTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		curLonTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		curLonLabel.setHorizontalAlignment(SwingConstants.CENTER);
		curLonLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		curCursTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		curCursTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		curCursLabel.setHorizontalAlignment(SwingConstants.CENTER);
		curCursLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		curDistTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		curDistTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		curDistLabel.setHorizontalAlignment(SwingConstants.CENTER);
		curDistLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblCursor, GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(curLatTitlelabel)
								.addComponent(curLonTitleLabel)
								.addComponent(curCursTitleLabel)
								.addComponent(curDistTitleLabel))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(curDistLabel)
								.addComponent(curCursLabel)
								.addComponent(curLonLabel)
								.addComponent(curLatLabel))))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblCursor)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(curLatTitlelabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(curLonTitleLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(curCursTitleLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(curDistTitleLabel))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(curLatLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(curLonLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(curCursLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(curDistLabel)))
					.addContainerGap(190, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
	
	public JLabel getCurLatLabel() {
		return curLatLabel;
	}
	
	public JLabel getCurLonLabel() {
		return curLonLabel;
	}
	
	public JLabel getCurCursLabel() {
		return curCursLabel;
	}
	
	public JLabel getCurDistLabel() {
		return curDistLabel;
	}
}
