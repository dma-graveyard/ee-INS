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
package dk.frv.enav.ins.ais;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import dk.frv.ais.message.AisMessage;

public class NameCache implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String FILENAME = ".aiscache";
	private static final int MAX_ELEMENTS = 1000;
	
	private static final Logger LOG = Logger.getLogger(NameCache.class);
	
	private Map<Long, String> names = new HashMap<Long, String>(); 
	
	public NameCache() {
		
	}
	
	public synchronized void setName(long mmsi, String name) {
		names.put(mmsi, AisMessage.trimText(name));
	}
	
	public synchronized String getName(long mmsi) {
		return names.get(mmsi);
	}
	
	public synchronized void saveToFile() {
		Properties props = new Properties();
		List<Long> mmsis = new ArrayList<Long>();
		for (Long mmsi : names.keySet()) {
			mmsis.add(mmsi);			
		}
		if (mmsis.size() > MAX_ELEMENTS) {
			Collections.shuffle(mmsis);
			long deleteCount =  mmsis.size() - MAX_ELEMENTS;			
			for (int i=0; i < deleteCount; i++) {
				mmsis.remove(0);
			}
		}
		for (Long mmsi : mmsis) {
			props.put(Long.toString(mmsi), names.get(mmsi));
		}
		try {
			FileOutputStream out = new FileOutputStream(FILENAME);
			props.store(out, "Cache of AIS vessel target names");
			out.close();
		} catch (IOException e) {
			LOG.error("Failed to save AIS name cache: " + e.getMessage());
		}
	}
	
	public synchronized void loadFromFile() {
		Properties props = new Properties();
		try {
			FileInputStream in = new FileInputStream(FILENAME);
			props.load(in);
			in.close();
			for (Object key : props.keySet()) {
				Long mmsi = Long.parseLong((String)key);
				names.put(mmsi, props.getProperty((String)key));
			}
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			LOG.error("Failed to load AIS name cache: " + e.getMessage());
		
		}
	}
	

}
