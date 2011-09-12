package dk.frv.enav.ins.services.ais;

import dk.frv.ais.reader.SendRequest;

public class AisIntendedRouteSendThread extends AisSendThread {

	public AisIntendedRouteSendThread(SendRequest sendRequest, AisServices aisServices) {
		super(sendRequest, aisServices);
	}
	
	@Override
	public void run() {
		super.run();
		
		if (abk != null && abk.isSuccess()) {
			aisServices.setLastIntendedRouteBroadcast();
		}
		
	}

}
