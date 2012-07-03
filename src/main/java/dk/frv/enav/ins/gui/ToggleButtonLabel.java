package dk.frv.enav.ins.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class ToggleButtonLabel extends ButtonLabel {

	private static final long serialVersionUID = 1L;
	private static boolean toggled = false;

	public ToggleButtonLabel(String text) {
		super(text);
		styleButton(this);

	}

	public void doClick() {
		System.out.println("Do click");
		// this.processMouseEvent(null);
	}

	public static void styleButton(final JLabel label) {

		label.setPreferredSize(new Dimension(80, 25));
		label.setFont(defaultFont);
		label.setForeground(textColor);
		// label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(
				45, 45, 45)));
		label.setBackground(new Color(128, 128, 128));
		label.setOpaque(true);

		label.setHorizontalAlignment(JLabel.CENTER);

		label.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				if (label.isEnabled()) {

					if (toggled) {
						label.setBackground(new Color(168, 168, 168));
						System.out.println("It is toggled");
						toggled = false;
					} else {
						label.setBackground(new Color(128, 128, 128));
						
						System.out.println("It is not toggled");
						toggled = true;
					}

//					toggled = !toggled;

				}
			}

			public void mouseReleased(MouseEvent e) {

			}
		});
	}

	public void setSelected(boolean selected) {

		if (selected) {
			this.setBackground(new Color(168, 168, 168));
		} else {
			this.setBackground(new Color(128, 128, 128));
		}
		toggled = selected;

	}

	public boolean isSelected() {
		return toggled;
	}

}
