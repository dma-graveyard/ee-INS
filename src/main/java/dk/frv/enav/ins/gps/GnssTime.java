/*
 * Copyright 2011 Danish Maritime Safety Administration. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Safety Administration.
 * 
 */
package dk.frv.enav.ins.gps;

import java.util.Date;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.enav.ins.nmea.NmeaSensor;
import dk.frv.enav.ins.nmea.SensorType;

public class GnssTime extends MapHandlerChild implements IGnssTimeListener {
	
	private static final Logger LOG = Logger.getLogger(GnssTime.class);
	
	private long offset = 0;
	private NmeaSensor nmeaSensor = null;
	
	private static GnssTime instance = null;
	
	private GnssTime() {
		
	}

	@Override
	public synchronized void receive(GnssTimeMessage gnssTimeMessage) {		
		if (gnssTimeMessage == null) {
			return;
		}
		offset = (new Date()).getTime() - gnssTimeMessage.getTime().getTime();
		LOG.debug("New GPS time offset: " + offset);		
	}
	
	public synchronized Date getDate() {
		return new Date((new Date()).getTime() - offset);		 
	}
	
	public static void init() {
		synchronized (GnssTime.class) {
			if (instance == null) {
				instance = new GnssTime();
			}			
		}
	}
	
	public static GnssTime getInstance() {
		synchronized (GnssTime.class) {
			return instance;
		}
	}
	
	@Override
	public void findAndInit(Object obj) {
		if (nmeaSensor != null) {
			return;
		}
		if (obj instanceof NmeaSensor) {
			NmeaSensor sensor = (NmeaSensor)obj;
			if (sensor.isSensorType(SensorType.GPS)) {
				nmeaSensor = sensor;
				nmeaSensor.addGnssTimeListener(this);
			}
		}
	}
	
	@Override
	public void findAndUndo(Object obj) {
		if (obj == nmeaSensor) {
			nmeaSensor.removeGnssTimeListener(this);
		}
	}

	

}
