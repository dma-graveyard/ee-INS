package dk.frv.enav.ins.gui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class ToggleButtonLabel extends ButtonLabel {

	private static final long serialVersionUID = 1L;
	private boolean toggled = false;

	public ToggleButtonLabel(String text) {
		super(text);
//		styleButton(this);

	}

	public void styleButton(final JLabel label) {

		label.setPreferredSize(new Dimension(80, 25));
		label.setFont(defaultFont);
		label.setForeground(textColor);
		// label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor));
		label.setBackground(standardColor);
		label.setOpaque(true);

		label.setHorizontalAlignment(JLabel.CENTER);

		label.addMouseListener(new MouseAdapter() {
			
			
			
			public void mousePressed(MouseEvent e) {

			}

			public void mouseReleased(MouseEvent e) {
				if (label.isEnabled()) {
					if (toggled) {
						//Untoggle it
						label.setBackground(standardColor);
//						toggled = false;
					} else {
						//Toggle it
						label.setBackground(clickedColor);
//						toggled = true;
					}

					toggled = !toggled;

				}
			}
		});
	}

	public void setSelected(boolean selected) {
		if (selected && this.isEnabled()) {
			this.setBackground(clickedColor);
		} else {
			this.setBackground(standardColor);
		}
		toggled = selected;

	}

	public boolean isSelected() {
		return toggled;
	}

}
