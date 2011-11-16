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
package dk.frv.enav.ins.layers.nogo;

import java.util.Date;
import java.util.List;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.enav.common.xml.nogo.types.NogoPolygon;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.gui.MainFrame;
import dk.frv.enav.ins.gui.MapMenu;
import dk.frv.enav.ins.gui.TopPanel;
import dk.frv.enav.ins.msi.MsiHandler;
import dk.frv.enav.ins.nogo.NogoHandler;

public class NogoLayer extends OMGraphicHandlerLayer  {	
	private static final long serialVersionUID = 1L;

	private MsiHandler msiHandler = null;
	
	private NogoHandler nogoHandler = null;
	
	private OMGraphicList graphics = new OMGraphicList();
	private MapBean mapBean = null;
	private TopPanel topPanel = null;
	private MainFrame mainFrame = null;	
//	private MsiInfoPanel msiInfoPanel = null;	
	private OMGraphic closest = null;
	private OMGraphic selectedGraphic;
	private MapMenu msiMenu;

	private MouseDelegator mouseDelegator;
	private LatLonPoint mousePosition;
	
	public NogoLayer() {
		
	}
	
	public void doUpdate() {
		graphics.clear();
		Date now = GnssTime.getInstance().getDate();
		
		// Get polygons
		List<NogoPolygon> polygons = nogoHandler.getPolygons();

		for (NogoPolygon polygon : polygons) {
//			System.out.println("We found a polygon");
			
			// Create Nogo graphic
			NogoGraphic nogoGraphic = new NogoGraphic(polygon);
			graphics.add(nogoGraphic);
			
		}
		
		doPrepare();
		/**
		// Get messages
		List<MsiHandler.MsiMessageExtended> messages = msiHandler.getMessageList();
		for (MsiHandler.MsiMessageExtended message : messages) {
			
			// Not able to show messages without location
			if (message.msiMessage.getLocation() == null) {
				continue;
			}
			
			// Is it valid now
			if (!message.isValidAt(now)) {
				continue;
			}
			
			// Filtering begins here
			if(EeINS.getSettings().getEnavSettings().isMsiFilter()){
				// It is set to be visible
				if(!message.visible) {
					if(mousePosition == null) {
						continue;
					}
				}
				
				// Check proximity to current location (free navigation mode)
				if(mousePosition != null && !message.visible) {
					double distance = distanceToShip(message);
					if(distance > EeINS.getSettings().getEnavSettings().getMsiVisibilityFromNewWaypoint()){
						continue;
					}
				}
			}
			**/
		/*
			// Create MSI graphic
			MsiGraphic msiGraphic = new MsiGraphic(message);
			graphics.add(msiGraphic);
			
			if(mapBean != null && message.relevant){
				MsiDirectionalIcon direction = new MsiDirectionalIcon(mapBean);
				direction.setMarker(message);
				graphics.add(direction);
			}
		}
		doPrepare();
		**/
	}
	

	@Override
	public synchronized OMGraphicList prepare() {
		graphics.project(getProjection());
		return graphics;
	}
	
	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof MsiHandler) {
			msiHandler = (MsiHandler)obj;
		}
		if (obj instanceof NogoHandler) {
			nogoHandler = (NogoHandler)obj;
		}		
		if (obj instanceof MapBean){
			mapBean = (MapBean)obj;
		}
		if (obj instanceof TopPanel) {
			topPanel = (TopPanel)obj;
		}
		if (obj instanceof MapMenu){
			msiMenu = (MapMenu) obj;
		}
		if (obj instanceof MouseDelegator) {
			mouseDelegator = (MouseDelegator) obj;
		}
	}

}
