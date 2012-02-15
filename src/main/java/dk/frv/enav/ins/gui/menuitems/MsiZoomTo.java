/*
 * Copyright 2011 Danish Maritime Authority. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Authority ``AS IS'' 
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
 * either expressed or implied, of Danish Maritime Authority.
 * 
 */
package dk.frv.enav.ins.gui.menuitems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.layers.msi.MsiLayer;
import dk.frv.enav.ins.msi.MsiHandler.MsiMessageExtended;

public class MsiZoomTo extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MsiLayer msiLayer;
	private MsiMessageExtended msiMessageExtended;

	public MsiZoomTo(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		msiLayer.zoomTo(msiMessageExtended.msiMessage);
	}
	
	public void setMsiLayer(MsiLayer msiLayer) {
		this.msiLayer = msiLayer;
	}
	
	public void setMsiMessageExtended(MsiMessageExtended msiMessageExtended) {
		this.msiMessageExtended = msiMessageExtended;
	}

}
