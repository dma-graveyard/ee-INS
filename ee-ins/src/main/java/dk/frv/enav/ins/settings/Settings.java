/*
 * Copyright 2011 Danish Maritime Authority. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Authority ``AS IS'' 
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
 * either expressed or implied, of Danish Maritime Authority.
 * 
 */
package dk.frv.enav.ins.settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * Settings class
 */
public class Settings implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(Settings.class);

    private String settingsFile = "settings.properties";

    private final GuiSettings guiSettings = new GuiSettings();
    private final MapSettings mapSettings = new MapSettings();
    private final SensorSettings sensorSettings = new SensorSettings();
    private final NavSettings navSettings = new NavSettings();
    private final AisSettings aisSettings = new AisSettings();
    private final EnavSettings enavSettings = new EnavSettings();

    public Settings() {

    }

    public Settings(String settingsFile) {
        this.settingsFile = settingsFile;
    }

    public void loadFromFile() {
        // Open properties file
        Properties props = new Properties();
        //
        try {
            props.load(new FileInputStream(settingsFile));
        } catch (FileNotFoundException e) {
            LOG.error("No settings file found");
            return;
        } catch (IOException e) {
            LOG.error("Settings file could not be loaded");
            return;
        }
        LOG.info("Settings file loaded, path=" + settingsFile);

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
                keys.add((String) key);
            }
            for (String key : keys) {
                out.println(key + "=" + props.getProperty(key));
            }
            out.close();
            LOG.error("Settings file updated, path=" + settingsFile);
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
