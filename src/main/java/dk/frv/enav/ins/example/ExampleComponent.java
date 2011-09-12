package dk.frv.enav.ins.example;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

public class ExampleComponent extends MapHandlerChild {
	
	private static final Logger LOG = Logger.getLogger(ExampleComponent.class);
	
	public ExampleComponent() {
		// Called when class is created
	}
	
	@Override
	public void findAndInit(Object obj) {
		LOG.info("findAndInit obj.getClass(): " + obj.getClass()); 
	}
	
	@Override
	public void findAndUndo(Object obj) {
		// Unregister other components
	}
	
}
