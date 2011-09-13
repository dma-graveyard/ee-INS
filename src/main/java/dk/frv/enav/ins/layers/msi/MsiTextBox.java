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
package dk.frv.enav.ins.layers.msi;

import java.awt.Color;
import java.awt.Font;
import java.util.Locale;

import com.bbn.openmap.omGraphics.OMRect;
import com.bbn.openmap.omGraphics.OMText;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.common.StringTools;
import dk.frv.enav.ins.msi.MsiHandler.MsiMessageExtended;

public class MsiTextBox extends MsiSymbolPosition {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MsiTextBox(MsiMessageExtended message) {
		super(message);
	}
	
	public void createSymbol(GeoLocation pos) {	
		// Make text box with ENC text if now acknowledged
		if (acknowledged || msiMessage.getEncText() == null || msiMessage.getEncText().length() == 0) {
			return;
		}

		// TODO Make rounded box and wrap text as described in spec
		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 10);
		OMText label = new OMText(0, 0, -160, -40, "", font, OMText.JUSTIFY_LEFT);
		label.setLat(pos.getLatitude());
		label.setLon(pos.getLongitude());
		//label.setY(20);
		label.setTextMatteColor(Color.WHITE);
		String msiMessageString = msiMessage.getEncText();
		StringTools st = new StringTools();
		String msiMessageStringWrapped = st.wordWrap(msiMessageString, 23, Locale.ENGLISH);		
		label.setData(msiMessageStringWrapped);
		add(label);
		OMRect msiBox = new OMRect(pos.getLatitude(), pos.getLongitude(), -15, -15, -170, -60);
		msiBox.setFillPaint(Color.white);
		add(msiBox);
	}
}
