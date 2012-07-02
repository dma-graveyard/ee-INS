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

import java.awt.Color;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.gui.TopPanel;
import dk.frv.enav.ins.layers.ais.AisLayer;
import dk.frv.enav.ins.layers.ais.VesselTargetGraphic;

public class AisTargetDetails extends JMenuItem implements IMapMenuAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TopPanel topPanel;
	private AisLayer aisLayer;
	private long MMSI;	
	private VesselTargetGraphic vesselTargetGraphic;
	
	public AisTargetDetails(String text) {
		super();
		this.setText(text);
	}
	
	@Override
	public void doAction() {
		System.out.println("Select it!");
		System.out.println("Selected :" + MMSI);
		
//		aisLayer.setSelectedMMSI(MMSI);
		vesselTargetGraphic.setLinePaint(Color.red);
		aisLayer.targetUpdated(vesselTargetGraphic.getVesselTarget());
		
		
//		if (topPanel != null && topPanel.getAisDialog() != null) {
//			topPanel.getAisDialog().setSelection(this.MMSI, true);
//			topPanel.getAisDialog().setVisible(true);
//		}
//		
		
	}

	public void setTopPanel(TopPanel topPanel) {
		this.topPanel = topPanel;
	}
	
	public void setMSSI(long MSSI) {
		this.MMSI = MSSI;
	}	

	public void setAisLayer(AisLayer aisLayer){
		this.aisLayer = aisLayer;
	}
	
	public void setVesselTargetGraphic(VesselTargetGraphic vesselTargetGraphic) {
		setColor(this.vesselTargetGraphic);
		this.vesselTargetGraphic = vesselTargetGraphic;
	}
	
	public void setColor(VesselTargetGraphic vesselTargetGraphic){
		if (vesselTargetGraphic != null){
			vesselTargetGraphic.setLinePaint(new Color(74, 97, 205, 255));
			aisLayer.targetUpdated(vesselTargetGraphic.getVesselTarget());			
		}

	}

	
}
