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
