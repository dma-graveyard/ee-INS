package dk.frv.enav.ins.layers.ais;

import java.net.URL;

import javax.swing.ImageIcon;

import dk.frv.enav.ins.EeINS;

/**
 * MSI mouse over info
 */
public class HighlightInfoPanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    public HighlightInfoPanel() {
        super(load("images/ais/highlight.png"));
    }

    /**
     * Show the image
     */
    public void displayHighlight(int x, int y) {
        setPos(x, y);
        showImage();
    }

    static ImageIcon load(String imgpath) {
        URL url = EeINS.class.getClassLoader().getResource(imgpath);
        return new ImageIcon(url);
    }
}
