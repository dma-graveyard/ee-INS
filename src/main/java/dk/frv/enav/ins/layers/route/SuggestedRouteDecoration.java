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
