package dk.frv.enav.ins.gui.ComponentPanels;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.Locale;

import javax.swing.border.EtchedBorder;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.gui.OMComponentPanel;

import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.gui.ChartPanel;
import dk.frv.enav.ins.gui.Panels.ScalePanel;

public class ScaleComponentPanel extends OMComponentPanel implements Runnable, ProjectionListener  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ScalePanel scalePanel = new ScalePanel();
	private GnssTime gnssTime = null;
	private ChartPanel chartPanel;
	
	public ScaleComponentPanel(){
		super();
		
//		this.setMinimumSize(new Dimension(10, 25));
		
		scalePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setBorder(null);
		setLayout(new BorderLayout(0, 0));
		add(scalePanel, BorderLayout.NORTH);
		new Thread(this).start();
		
		
	}
	

	@Override
	public void projectionChanged(ProjectionEvent arg0) {
		setScale(chartPanel.getMap().getProjection().getScale());
	}
	
	public void setScale(float scale){
		scalePanel.getScaleLabel().setText("Scale: " + String.format(Locale.US, "%3.0f", scale));
	}


	@Override
	public void run() {
		while (true) {
			if (gnssTime != null) {
				Date now = gnssTime.getDate();
				scalePanel.getTimeLabel().setText(Formatter.formatLongDateTime(now));
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) { }
		}
		
	}

	
	@Override
	public void findAndInit(Object obj) {
		if (gnssTime == null && obj instanceof GnssTime) {
			gnssTime = (GnssTime)obj;
		}
		if (obj instanceof ChartPanel) {
			chartPanel = (ChartPanel)obj;
			chartPanel.getMap().addProjectionListener(this);
			return;
		}
	}
	

}
