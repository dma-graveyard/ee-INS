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
package dk.frv.enav.ins.layers.route;

import java.awt.Graphics;
import java.awt.geom.Point2D;

import com.bbn.openmap.omGraphics.awt.AbstractShapeDecoration;

public class SuggestedRouteDecoration extends AbstractShapeDecoration {
	
	public SuggestedRouteDecoration(float length, float width, int orientation) {
		super(length, width, orientation);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw(Graphics g, Point2D[] points, boolean complete) {
		setGraphics(g);

        int nbpts = points.length;

        double xcoord1 = points[0].getX();
        double ycoord1 = points[0].getY();
        double xcoord2 = points[nbpts - 1].getX();
        double ycoord2 = points[nbpts - 1].getY();

        g.drawLine((int) xcoord1, (int) ycoord1, (int) xcoord2, (int) ycoord2);

        if (complete) {
            int orient = getOrientation() == LEFT ? -1 : 1;

            // Compute cosinus and sinus of rotation angle
            double dx = xcoord2 - xcoord1;
            double dy = ycoord2 - ycoord1;
            double norm = Math.sqrt(dx * dx + dy * dy);
            double rcos = dx / norm;
            double rsin = dy / norm;

            // Compute vertices
            double r = getLength() / 2.0; // x radius before rotation
            double w = orient * getWidth(); // y radius before
                                            // rotation
            // rotate

            int x2 = (int) (xcoord1 + r * rcos);
            int y2 = (int) (ycoord1 + r * rsin);
            int x1 = (int) (x2 - w * rsin);
            int y1 = (int) (y2 + w * rcos);
            int x3 = (int) (xcoord2 + r * rcos);
            int y3 = (int) (ycoord2 + r * rsin);
            int x4 = (int) (x3 - w * rsin);
            int y4 = (int) (y3 +w * rcos);

            //g.drawLine((int) x2, (int) y2, (int) x1, (int) y1);
            g.drawLine(x1, y1, x4, y4);
        }

        restoreGraphics(g);
	}

}
