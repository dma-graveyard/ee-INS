package dk.frv.enav.ins.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JLabel textLabel = new JLabel();

	public InfoPanel() {
		super();
		FlowLayout flowLayout = new FlowLayout();
		setLayout(flowLayout);
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		add(textLabel);
		setVisible(false);
	}
	
	public void showText(String text) {
		textLabel.setText(text);
		resizeAndShow();
	}
	
	public void resizeAndShow() {
		validate();
		Dimension d = textLabel.getSize(); 
		this.setSize(d.width + 6, d.height + 2);
		setVisible(true);
	}
	
	public void setPos(int x, int y) {
		Rectangle rect = getBounds();
		setBounds(x, y, (int)rect.getWidth(), (int)rect.getHeight());
	}
	
}
