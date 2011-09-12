package dk.frv.enav.ins.nmea;

import dk.frv.ais.message.AisMessage;

/**
 * Interface to implement to allow receiption of AIS messages 
 * @author obo
 *
 */
public interface IAisListener {
	
	void receive(AisMessage aisMessage);
	
	void receiveOwnMessage(AisMessage aisMessage);

}
