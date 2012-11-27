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
package dk.frv.enav.ins.gui;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.status.ComponentStatus;
import dk.frv.enav.ins.status.IStatusComponent;

/**
 * A status label with status indication icon 
 */
public class StatusLabel extends JLabel {
	
	private static final long serialVersionUID = 1L;	
	private static Map<ComponentStatus.Status, ImageIcon> imageMap = new HashMap<ComponentStatus.Status, ImageIcon>();	
	static {		
		imageMap.put(ComponentStatus.Status.OK, new ImageIcon(EeINS.class.getResource("/images/status/OK.png")));
		imageMap.put(ComponentStatus.Status.ERROR, new ImageIcon(EeINS.class.getResource("/images/status/ERROR.png")));
		imageMap.put(ComponentStatus.Status.PARTIAL, new ImageIcon(EeINS.class.getResource("/images/status/PARTIAL.png")));
		imageMap.put(ComponentStatus.Status.UNKNOWN, new ImageIcon(EeINS.class.getResource("/images/status/UNKNOWN.png")));
	}
	
	private static final Font font = new Font("Tahoma", Font.PLAIN, 9);
	
	public StatusLabel(String name) {		
		super(name);
		setFont(font);
		setHorizontalTextPosition(SwingConstants.LEFT);
		setIcon(imageMap.get(ComponentStatus.Status.UNKNOWN));
	}
	
	public void updateStatus(IStatusComponent statusComponent) {
		ComponentStatus componentStatus = statusComponent.getStatus();
		setIcon(imageMap.get(componentStatus.getStatus()));
		String shortStatusText = componentStatus.getShortStatusText();
		setToolTipText(shortStatusText);
	}
	
}
