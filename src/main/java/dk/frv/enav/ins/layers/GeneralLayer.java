/*
 * Copyright 2011 Danish Maritime Safety Administration. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Safety Administration.
 * 
 */
package dk.frv.enav.ins.layers;

import java.awt.event.MouseEvent;

import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;

import dk.frv.enav.ins.event.NavigationMouseMode;
import dk.frv.enav.ins.gui.MapMenu;

/**
 * General layer for handling mouse right click 
 */
public class GeneralLayer extends OMGraphicHandlerLayer implements MapMouseListener {
	
	private static final long serialVersionUID = 1L;
	
	private MapMenu mapMenu;

	public MapMouseListener getMapMouseListener() {
		return this;
	}

	@Override
	public String[] getMouseModeServiceList() {
		String[] ret = new String[1];
		ret[0] = NavigationMouseMode.modeID; // "Gestures"
		return ret;
	}

	@Override
	public boolean mouseClicked(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON3) {
			mapMenu.generalMenu(true);
			mapMenu.setVisible(true);
			mapMenu.show(this, arg0.getX() - 2, arg0.getY() - 2);
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(MouseEvent arg0) {
		return false;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved() {
	}

	@Override
	public boolean mouseMoved(MouseEvent arg0) {
		return false;
	}

	@Override
	public boolean mousePressed(MouseEvent arg0) {
		return false;
	}

	@Override
	public boolean mouseReleased(MouseEvent arg0) {
		return false;
	}

	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof MapMenu) {
			mapMenu = (MapMenu) obj;
		}
	}

}
