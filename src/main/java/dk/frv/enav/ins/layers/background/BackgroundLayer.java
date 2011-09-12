package dk.frv.enav.ins.layers.background;

import java.awt.event.MouseEvent;

import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.shape.ShapeLayer;

import dk.frv.enav.ins.event.NavigationMouseMode;

public class BackgroundLayer extends ShapeLayer implements MapMouseListener {
	
	private static final long serialVersionUID = 1L;
	
	public BackgroundLayer() {
		super();
	}

	@Override
	public String[] getMouseModeServiceList() {
	    String[] ret = new String[1];
        ret[0] = NavigationMouseMode.modeID; // "Gestures"
        return ret;
	}
	
	@Override
	public MapMouseListener getMapMouseListener() {
        return this;
    }

	@Override
	public boolean mouseClicked(MouseEvent arg0) {
		System.out.println("Background mouse clicked");
		return false;
	}

	@Override
	public boolean mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
