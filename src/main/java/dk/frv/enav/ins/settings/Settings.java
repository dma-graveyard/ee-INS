package dk.frv.enav.ins.settings;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.bbn.openmap.util.PropUtils;

public class Settings implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(Settings.class);
	
	private String settingsFile = "settings.properties";
	
	private GuiSettings guiSettings = new GuiSettings();
	private MapSettings mapSettings = new MapSettings();
	private SensorSettings sensorSettings = new SensorSettings();
	private NavSettings navSettings = new NavSettings();
	private AisSettings aisSettings = new AisSettings();
	private EnavSettings enavSettings = new EnavSettings();
	
	public Settings() {
		
	}
	
	public Settings(String settingsFile) {
		this.settingsFile = settingsFile;
	}
		
	public void loadFromFile() {
		// Open properties file
		Properties props = new Properties();
		if (!PropUtils.loadProperties(props, ".", settingsFile)) {
			LOG.info("No settings file found");
			return;
		}
		aisSettings.readProperties(props);
		enavSettings.readProperties(props);
		guiSettings.readProperties(props);
		mapSettings.readProperties(props);
		navSettings.readProperties(props);
		sensorSettings.readProperties(props);
	}
	
	public void saveToFile() {
		Properties props = new Properties();
		aisSettings.setProperties(props);
		enavSettings.setProperties(props);
		guiSettings.setProperties(props);
		mapSettings.setProperties(props);
		navSettings.setProperties(props);
		sensorSettings.setProperties(props);
		try {
			FileWriter outFile = new FileWriter(settingsFile);
			PrintWriter out = new PrintWriter(outFile);
			out.println("# ee-INS settings saved: " + new Date());
			TreeSet<String> keys = new TreeSet<String>();
			for (Object key : props.keySet()) {
				keys.add((String)key);
			}
			for (String key : keys) {
				out.println(key + "=" + props.getProperty(key));
			}						
			out.close();
		} catch (IOException e) {
			LOG.error("Failed to save settings file");
		}
	}
	
	public GuiSettings getGuiSettings() {
		return guiSettings;
	}
	
	public MapSettings getMapSettings() {
		return mapSettings;
	}
	
	public SensorSettings getSensorSettings() {
		return sensorSettings;
	}
	
	public NavSettings getNavSettings() {
		return navSettings;
	}
	
	public AisSettings getAisSettings() {
		return aisSettings;
	}
	
	public EnavSettings getEnavSettings() {
		return enavSettings;
	}
	
	public String getSettingsFile() {
		return settingsFile;
	}
	
}
