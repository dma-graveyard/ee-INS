package dk.frv.enav.ins.gui;


import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.displayer.DisplayerDockBorder;
import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.util.UIBridge;
import bibliothek.gui.dock.util.UIValue;

public class BorderMod implements
		UIBridge<BorderModifier, UIValue<BorderModifier>> {	
	
	/* our first modifier creates a red border */
	private BorderModifier borderModi = new BorderModifier() {
		public Border modify(Border border) {
			return BorderFactory.createLineBorder(new Color(240, 240, 240));
		}
	};

	public BorderMod() {
	}

	public void destroy() {
	}


	/*
	 * Tells whether we should pay attention to some border. We only pay
	 * attention to those borders which are shown directly on a
	 * SplitDockStation. Note that we can cast uiValue to
	 * DisplayerDockBorder because of the restrictions we applied when
	 * "publishing" the bridge on line 50.
	 */
	private boolean shouldManage(UIValue<BorderModifier> uiValue) {
		DisplayerDockBorder displayer = (DisplayerDockBorder) uiValue;
		return displayer.getDisplayer().getStation() instanceof SplitDockStation;
	}

	public void remove(String id, UIValue<BorderModifier> uiValue) {
		
	}

	/* This method may be called any time for installed listeners. */
	public void set(String id, BorderModifier value,
			UIValue<BorderModifier> uiValue) {
		if (shouldManage(uiValue)) {
				uiValue.set(borderModi);
		} else {
			uiValue.set(value);
		}
	}

	@Override
	public void add(String arg0, UIValue<BorderModifier> arg1) {
		// TODO Auto-generated method stub
		
	}
}
