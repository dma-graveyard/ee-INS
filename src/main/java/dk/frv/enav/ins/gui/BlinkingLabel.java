package dk.frv.enav.ins.gui;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import dk.frv.enav.ins.EeINS;

public class BlinkingLabel extends JLabel implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean show = false, blink = false;
	int period;
	ImageIcon images[];

	public BlinkingLabel(int period, ImageIcon images[]) {
		this.period = period;
		this.images = images;
		this.setVisible(true);
		new Thread(this).start();
	}

	public void run() {
		while(true){
			setIcon(images[0]);
			while (blink) {
				show = !show;
				if (show)
					this.setIcon(images[0]);
				else
					this.setIcon(images[1]);
	
				repaint();
				EeINS.sleep(period);
			}
			EeINS.sleep(5000);
		}
	}

	public boolean isBlink() {
		return blink;
	}

	public void setBlink(boolean blink) {
		this.blink = blink;
	}

}