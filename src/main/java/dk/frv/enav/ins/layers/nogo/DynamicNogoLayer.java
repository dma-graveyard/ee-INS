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

import java.awt.Color;
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

	/**
	 * Errorcode -1 means server experinced a timeout 
	 * Errorcode 0 means everything went ok 
	 * Errorcode 1 is the standby message 
	 * Errorcode 17 means no data 
	 * Errorcode 18 means no tide data
	 * 
	 * @param completed
	 */
	public void doUpdate(boolean completed) {

		Date validFrom = nogoHandler.getValidFromOwn();
		Date validTo = nogoHandler.getValidToOwn();
		double draught = nogoHandler.getDraughtOwn();

		if (completed) {

			// Clean up
			 graphics.clear();

			// Get polygons
			List<NogoPolygon> polygonsOwn = nogoHandler.getNogoPolygonsOwn();
			List<NogoPolygon> polygonsTarget = nogoHandler.getNogoPolygonsTarget();
			
			
			if (nogoHandler.getNogoFailed()) {
				nogoHandler.setNogoFailed(false);
				NogoGraphic nogoGraphic = new NogoGraphic(
						null,
						validFrom,
						validTo,
						draught,
						"Connection to shore timed out - NoGo request failed. Please try again in a few minutes",
						nogoHandler.getNorthWestPointOwn(), nogoHandler.getSouthEastPointOwn(), -1, true, Color.RED);
				graphics.add(nogoGraphic);
				
				nogoGraphic = new NogoGraphic(
						null,
						validFrom,
						validTo,
						draught,
						"Connection to shore timed out - NoGo request failed. Please try again in a few minutes",
						nogoHandler.getNorthWestPointTarget(), nogoHandler.getSouthEastPointTarget(), -1, true, Color.RED);
				graphics.add(nogoGraphic);
				
			} else {

				if (nogoHandler.getNoGoErrorCodeOwn() == 17 && nogoHandler.getNoGoErrorCodeTarget() == 17) {
					NogoGraphic nogoGraphic = new NogoGraphic(null, null, null,
							draught, "No data available for requested area",
							null, null, nogoHandler.getNoGoErrorCodeOwn(), true, Color.RED);
					graphics.add(nogoGraphic);
				}

				if (nogoHandler.getNoGoErrorCodeOwn() == 18 && nogoHandler.getNoGoErrorCodeTarget() == 18) {
					
					//Own graphics
					for (NogoPolygon polygon : polygonsOwn) {
						NogoGraphic nogoGraphic = new NogoGraphic(polygon,
								validFrom, validTo, draught, "",
								nogoHandler.getNorthWestPointOwn(),
								nogoHandler.getSouthEastPointOwn(),
								nogoHandler.getNoGoErrorCodeOwn(), false, Color.RED);
						graphics.add(nogoGraphic);
					}
					
					//Target graphics
					for (NogoPolygon polygon : polygonsTarget) {
						NogoGraphic nogoGraphic = new NogoGraphic(polygon,
								validFrom, validTo, draught, "",
								nogoHandler.getNorthWestPointTarget(),
								nogoHandler.getSouthEastPointTarget(),
								nogoHandler.getNoGoErrorCodeTarget(), false, Color.ORANGE);
						graphics.add(nogoGraphic);
					}

					addFrame("", validFrom, validTo, draught,
							nogoHandler.getNoGoErrorCodeOwn());
				}

				if (nogoHandler.getNoGoErrorCodeOwn() == 0 && nogoHandler.getNoGoErrorCodeTarget() == 0) {

					//Own graphics
					for (NogoPolygon polygon : polygonsOwn) {
						NogoGraphic nogoGraphic = new NogoGraphic(polygon,
								validFrom, validTo, draught, "",
								nogoHandler.getNorthWestPointOwn(),
								nogoHandler.getSouthEastPointOwn(),
								nogoHandler.getNoGoErrorCodeOwn(), false, Color.RED);
						graphics.add(nogoGraphic);
					}
					
					//Target graphics
					for (NogoPolygon polygon : polygonsTarget) {
						NogoGraphic nogoGraphic = new NogoGraphic(polygon,
								validFrom, validTo, draught, "",
								nogoHandler.getNorthWestPointTarget(),
								nogoHandler.getSouthEastPointTarget(),
								nogoHandler.getNoGoErrorCodeTarget(), false, Color.ORANGE);
						graphics.add(nogoGraphic);
					}

					if (polygonsOwn.size() == 0) {
						NogoGraphic nogoGraphic = new NogoGraphic(null,
								validFrom, validTo, draught,
								"The selected area is Go",
								nogoHandler.getNorthWestPointOwn(),
								nogoHandler.getSouthEastPointOwn(), 1, true, Color.RED);
						graphics.add(nogoGraphic);
					} else {
						addFrame("", validFrom, validTo, draught,
								nogoHandler.getNoGoErrorCodeOwn());
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

			addFrame("NoGo area requested - standby", validFrom, validTo,
					draught, 1);

			// NogoGraphic nogoGraphic = new NogoGraphic(null, validFrom,
			// validTo, draught,
			// "NoGo area requested - standby",
			// nogoHandler.getNorthWestPointOwn(),
			// nogoHandler.getSouthEastPointOwn(),
			// 1, true);
			// graphics.add(nogoGraphic);
		}

		doPrepare();
	}

	public void addFrame(String message, Date validFrom, Date validTo,
			Double draught, int errorCode) {
		NogoGraphic nogoGraphic = new NogoGraphic(null, validFrom, validTo,
				draught, message, nogoHandler.getNorthWestPointOwn(),
				nogoHandler.getSouthEastPointOwn(), errorCode, true, Color.RED);
		graphics.add(nogoGraphic);
		
		NogoGraphic nogoGraphicTarget = new NogoGraphic(null, validFrom, validTo,
				draught, message, nogoHandler.getNorthWestPointTarget(),
				nogoHandler.getSouthEastPointTarget(), errorCode, true, Color.ORANGE);
		
		graphics.add(nogoGraphic);
		graphics.add(nogoGraphicTarget);
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
