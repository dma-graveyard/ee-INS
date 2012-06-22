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
package dk.frv.enav.ins.layers.nogo;

import java.util.Date;
import java.util.List;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.enav.common.xml.nogo.types.NogoPolygon;
import dk.frv.enav.ins.nogo.DynamicNogoHandler;

public class DynamicNogoLayer extends OMGraphicHandlerLayer {
	private static final long serialVersionUID = 1L;

	private DynamicNogoHandler nogoHandler = null;

	private OMGraphicList graphics = new OMGraphicList();

	public DynamicNogoLayer() {

	}

	public void doUpdate(boolean completed) {
		
		
		Date validFrom = nogoHandler.getValidFrom();
		Date validTo = nogoHandler.getValidTo();
		double draught = nogoHandler.getDraughtOwn();

		graphics.clear();
		if (completed) {
			// Get polygons
			List<NogoPolygon> polygons = nogoHandler.getPolygons();

			if (nogoHandler.getNogoFailed()) {
				nogoHandler.setNogoFailed(false);
				NogoGraphic nogoGraphic = new NogoGraphic(null, validFrom, validTo, draught,
						"Connection to shore timed out - NoGo request failed. Please try again in a few minutes", null,
						null, -1, true);
				graphics.add(nogoGraphic);

			} else {

				if (nogoHandler.getNoGoErrorCode() == 17) {
					NogoGraphic nogoGraphic = new NogoGraphic(null, null, null, draught,
							"No data available for requested area", null, null, nogoHandler.getNoGoErrorCode(), true);
					graphics.add(nogoGraphic);
				}

				if (nogoHandler.getNoGoErrorCode() == 18) {
					for (NogoPolygon polygon : polygons) {
						NogoGraphic nogoGraphic = new NogoGraphic(polygon, validFrom, validTo, draught, "",
								nogoHandler.getNorthWestPointOwn(), nogoHandler.getSouthEastPointOwn(),
								nogoHandler.getNoGoErrorCode(), false);
						graphics.add(nogoGraphic);
					}
					
					addFrame("", validFrom, validTo, draught, nogoHandler.getNoGoErrorCode());
				}

				if (nogoHandler.getNoGoErrorCode() == 0) {
					
					for (NogoPolygon polygon : polygons) {
						NogoGraphic nogoGraphic = new NogoGraphic(polygon, validFrom, validTo, draught, "",
								nogoHandler.getNorthWestPointOwn(), nogoHandler.getSouthEastPointOwn(),
								nogoHandler.getNoGoErrorCode(), false);
						graphics.add(nogoGraphic);
					}

					if (polygons.size() == 0) {
						NogoGraphic nogoGraphic = new NogoGraphic(null, validFrom, validTo, draught,
								"The selected area is Go", nogoHandler.getNorthWestPointOwn(),
								nogoHandler.getSouthEastPointOwn(), 1, true);
						graphics.add(nogoGraphic);
					}else{
						addFrame("", validFrom, validTo, draught, nogoHandler.getNoGoErrorCode());
					}
					
					

				}

				// We have selected an area outside of the available data - send
				// appropiate message
				// if (polygons.size() == 0) {
				//
				// } else {
				// // Data available, go through each polygon and draw them
				// for (NogoPolygon polygon : polygons) {
				// NogoGraphic nogoGraphic = new NogoGraphic(polygon, validFrom,
				// validTo, draught, "", nogoHandler.getNorthWestPoint(),
				// nogoHandler.getSouthEastPoint());
				// graphics.add(nogoGraphic);
				// }
				// }
			}
		} else {
			// We have just sent a nogo request - display a message telling the
			// user to standby
			
			addFrame("NoGo area requested - standby", validFrom, validTo, draught, 1);
			
//			NogoGraphic nogoGraphic = new NogoGraphic(null, validFrom, validTo, draught,
//					"NoGo area requested - standby", nogoHandler.getNorthWestPointOwn(), nogoHandler.getSouthEastPointOwn(),
//					1, true);
//			graphics.add(nogoGraphic);
		}

		doPrepare();
	}
	
	public void addFrame(String message, Date validFrom, Date validTo, Double draught, int errorCode){
		NogoGraphic nogoGraphic = new NogoGraphic(null, validFrom, validTo, draught, message, nogoHandler.getNorthWestPointOwn(), nogoHandler.getSouthEastPointOwn(),
				errorCode, true);
		graphics.add(nogoGraphic);
	}

	@Override
	public synchronized OMGraphicList prepare() {
		graphics.project(getProjection());
		return graphics;
	}

	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof DynamicNogoHandler) {
			nogoHandler = (DynamicNogoHandler) obj;
		}
	}

}
