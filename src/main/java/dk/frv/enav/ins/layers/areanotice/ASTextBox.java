package dk.frv.enav.ins.layers.areanotice;

import java.awt.Color;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMRect;
import com.bbn.openmap.omGraphics.OMText;

/**
 * Simple text box to display AreaNotice message/message type
 */
public class ASTextBox extends OMGraphicList {

	private static final long serialVersionUID = 1L;

	public ASTextBox(int xpos, int ypos, int width, int height, java.lang.String text) {
		super();
		super.clear();
		OMRect outline = new OMRect(xpos, ypos, xpos + width, ypos + height);

		outline.setFillPaint(Color.orange.brighter());
		OMText antext = new OMText(xpos + 10, ypos + 15, text, OMText.JUSTIFY_LEFT);

		super.add(antext);
		super.add(outline);
	}

}