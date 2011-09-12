package dk.frv.enav.ins.layers;

import java.awt.event.MouseEvent;

import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;

import dk.frv.enav.ins.event.NavigationMouseMode;
import dk.frv.enav.ins.gui.MapMenu;

public class GeneralLayer extends OMGraphicHandlerLayer implements
		MapMouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MapMenu mapMenu;

	public MapMouseListener getMapMouseListener() {
        return this;
    }
	
	@Override
	public String[] getMouseModeServiceList() {
        String[] ret = new String[1];
        ret[0] = NavigationMouseMode.modeID; // "Gestures"
        return ret;
    }

	@Override
	public boolean mouseClicked(MouseEvent arg0) {
		if(arg0.getButton() == MouseEvent.BUTTON3){
			mapMenu.generalMenu(true);
			mapMenu.setVisible(true);
			mapMenu.show(this, arg0.getX()-2, arg0.getY()-2);
			return true;
		}
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
	
	@Override
	public void findAndInit(Object obj) {
		if(obj instanceof MapMenu){
			mapMenu = (MapMenu) obj;
		}
	}

}
