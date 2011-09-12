package dk.frv.enav.ins.example;

import java.awt.event.MouseEvent;

import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.event.NavMouseMode;
import com.bbn.openmap.event.SelectMouseMode;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;

public class SimpleExampleLayer extends OMGraphicHandlerLayer implements MapMouseListener {

	private static final long serialVersionUID = 1L;

	private OMCircle circle;
	private OMGraphicList graphics = new OMGraphicList();

	public SimpleExampleLayer() {
		circle = new OMCircle(55f, 11f, 0, 0, 18, 18);
		graphics.add(circle);
		graphics.project(getProjection(), true);
	}

	@Override
	public synchronized OMGraphicList prepare() {
		graphics.project(getProjection(), true);
		return graphics;
	}

	public MapMouseListener getMapMouseListener() {
		return this;
	}

	@Override
	public String[] getMouseModeServiceList() {
		String[] ret = new String[1];
		ret[0] = NavMouseMode.modeID;
		ret[1] = SelectMouseMode.modeID;
		return ret; 
	}

	@Override
	public boolean mouseClicked(MouseEvent e) {
		OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(), 5.0f);
		for (OMGraphic omGraphic : allClosest) {
			if (omGraphic instanceof OMCircle) {
				System.out.println("Mouse clicked on omGraphic: " + omGraphic);
				// Consumed by this
				return true;
			}			
		}
		return false;
	}

	@Override
	public boolean mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved() {

	}

	@Override
	public boolean mouseMoved(MouseEvent e) {
		OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(), 5.0f);
		for (OMGraphic omGraphic : allClosest) {
			if (omGraphic instanceof OMCircle) {
				System.out.println("Mouse over omGraphic: " + omGraphic);
				// Consumed by this
				return true;
			}			
		}
		return false;
	}

	@Override
	public boolean mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
