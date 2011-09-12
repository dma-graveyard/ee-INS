package dk.frv.enav.ins.event;

import java.awt.Cursor;

public class MSIFilterMouseMode extends AbstractCoordMouseMode {
	private static final long serialVersionUID = 1L;
	public final static transient String modeID = "MSIFilter";
	
	/**
     * Construct a NavMouseMode. Sets the ID of the mode to the modeID, the
     * consume mode to true, and the cursor to the crosshair.
     */
    public MSIFilterMouseMode() {
        this(true);
    }
    
    public MSIFilterMouseMode(boolean shouldConsumeEvents) {
        super(modeID, shouldConsumeEvents);
        // override the default cursor
        setModeCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

}
