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
package dk.frv.enav.ins;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandler;
import com.bbn.openmap.PropertyConsumer;

import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.gui.MainFrame;
import dk.frv.enav.ins.gui.route.RouteManagerDialog;
import dk.frv.enav.ins.msi.MsiHandler;
import dk.frv.enav.ins.nmea.NmeaFileSensor;
import dk.frv.enav.ins.nmea.NmeaSensor;
import dk.frv.enav.ins.nmea.NmeaSerialSensor;
import dk.frv.enav.ins.nmea.NmeaStdinSensor;
import dk.frv.enav.ins.nmea.NmeaTcpSensor;
import dk.frv.enav.ins.nmea.SensorType;
import dk.frv.enav.ins.nogo.DynamicNogoHandler;
import dk.frv.enav.ins.nogo.NogoHandler;
import dk.frv.enav.ins.risk.RiskHandler;
import dk.frv.enav.ins.route.MonaLisaRouteExchange;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.service.EnavServiceHandler;
import dk.frv.enav.ins.service.communication.ais.AisServices;
import dk.frv.enav.ins.service.communication.enavcloud.EnavCloudHandler;
import dk.frv.enav.ins.service.communication.webservice.ShoreServices;
import dk.frv.enav.ins.settings.SensorSettings;
import dk.frv.enav.ins.settings.Settings;
import dk.frv.enav.ins.util.OneInstanceGuard;
import dk.frv.enav.ins.util.UpdateCheckerThread;

/**
 * Main class with main method.
 * 
 * Starts up components, bean context and GUI.
 * 
 */
public class EeINS {

    private static String VERSION;
    private static String MINORVERSION;
    private static Logger LOG;
    private static MainFrame mainFrame;
    private static MapHandler mapHandler;
    private static Settings settings;
    static Properties properties = new Properties();
    private static NmeaSensor aisSensor;
    private static NmeaSensor gpsSensor;
    private static GpsHandler gpsHandler;
    private static AisHandler aisHandler;
    private static RiskHandler riskHandler;
    private static RouteManager routeManager;
    private static ShoreServices shoreServices;
    private static MonaLisaRouteExchange monaLisaRouteExchange;
    private static AisServices aisServices;
    private static MsiHandler msiHandler;
    private static NogoHandler nogoHandler;
    private static EnavServiceHandler enavServiceHandler;
    private static EnavCloudHandler enavCloudHandler;
    private static DynamicNogoHandler dynamicNoGoHandler;
    private static UpdateCheckerThread updateThread;
    private static ExceptionHandler exceptionHandler;

    public static void main(String[] args) throws IOException {
        Path home = Paths.get(System.getProperty("user.home"), ".eeins");
        new Bootstrap().run();

        // Set up log4j logging
        LOG = Logger.getLogger(EeINS.class);

        // Set default exception handler
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);

        // Determine version
        Package p = EeINS.class.getPackage();
        MINORVERSION = p.getImplementationVersion();
        LOG.info("Starting ee-INS version " + MINORVERSION);
        LOG.info("Copyright (C) 2011 Danish Maritime Authority");
        LOG.info("This program comes with ABSOLUTELY NO WARRANTY.");
        LOG.info("This is free software, and you are welcome to redistribute it under certain conditions.");
        LOG.info("For details see LICENSE file.");
        if (MINORVERSION == null) {
            MINORVERSION = "?";
            VERSION = "?";
        } else {
            VERSION = MINORVERSION.split("[-]")[0];
        }

        // Create the bean context (map handler)
        mapHandler = new MapHandler();

        // Load settings or get defaults and add to bean context
        if (args.length > 0) {
            settings = new Settings(args[0]);
        } else {

            settings = new Settings(home.resolve("settings.properties").toString());
        }
        LOG.info("Using settings file: " + settings.getSettingsFile());
        settings.loadFromFile();
        mapHandler.add(settings);

        // Determine if instance already running and if that is allowed
        OneInstanceGuard guard = new OneInstanceGuard(home.resolve("eeins.lock").toString());
        if (!settings.getGuiSettings().isMultipleInstancesAllowed() && guard.isAlreadyRunning()) {
            JOptionPane.showMessageDialog(null,
                    "One application instance already running. Stop instance or restart computer.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Start sensors
        startSensors();

        // start riskHandler
        startRiskHandler();

        // Enable GPS timer by adding it to bean context
        GnssTime.init();
        mapHandler.add(GnssTime.getInstance());

        // Start position handler and add to bean context
        gpsHandler = new GpsHandler();
        mapHandler.add(gpsHandler);

        // Start AIS target monitoring
        aisHandler = new AisHandler();
        aisHandler.loadView();
        mapHandler.add(aisHandler);

        // Load routeManager and register as GPS data listener
        routeManager = RouteManager.loadRouteManager();
        mapHandler.add(routeManager);

        // Create shore services
        shoreServices = new ShoreServices(getSettings().getEnavSettings());
        mapHandler.add(shoreServices);

        // Create mona lisa route exchange
        monaLisaRouteExchange = new MonaLisaRouteExchange();
        mapHandler.add(monaLisaRouteExchange);

        // Create AIS services
        aisServices = new AisServices();
        mapHandler.add(aisServices);

        // Create MSI handler
        msiHandler = new MsiHandler(getSettings().getEnavSettings());
        mapHandler.add(msiHandler);

        // Create NoGo handler
        nogoHandler = new NogoHandler(getSettings().getEnavSettings());
        mapHandler.add(nogoHandler);

        // Create dynamic NoGo handler
        // Create NoGo handler
        dynamicNoGoHandler = new DynamicNogoHandler(getSettings().getEnavSettings());
        mapHandler.add(dynamicNoGoHandler);

        // Create EnavServiceHandler
        enavServiceHandler = new EnavServiceHandler();
        mapHandler.add(enavServiceHandler);

        // Create enav cloud handler
        enavCloudHandler = new EnavCloudHandler(settings.getEnavSettings());
        mapHandler.add(enavCloudHandler);

        // Create plugin components
        createPluginComponents();

        // Create and show GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

        // Start thread to handle software updates
        updateThread = new UpdateCheckerThread();
        mapHandler.add(updateThread);

        // must be set after logging is enabled
        exceptionHandler = new ExceptionHandler();
    }

    private static void startSensors() {
        SensorSettings sensorSettings = settings.getSensorSettings();
        switch (sensorSettings.getAisConnectionType()) {
        case NONE:
            aisSensor = new NmeaStdinSensor();
            break;
        case TCP:
            aisSensor = new NmeaTcpSensor(sensorSettings.getAisHostOrSerialPort(), sensorSettings.getAisTcpPort());
            break;
        case SERIAL:
            aisSensor = new NmeaSerialSensor(sensorSettings.getAisHostOrSerialPort());
            break;
        case FILE:
            aisSensor = new NmeaFileSensor(sensorSettings.getAisFilename(), sensorSettings);
            break;
        default:
            LOG.error("Unknown sensor connection type: " + sensorSettings.getAisConnectionType());
        }

        if (aisSensor != null) {
            aisSensor.addSensorType(SensorType.AIS);
        }

        switch (sensorSettings.getGpsConnectionType()) {
        case NONE:
            gpsSensor = new NmeaStdinSensor();
            break;
        case TCP:
            gpsSensor = new NmeaTcpSensor(sensorSettings.getGpsHostOrSerialPort(), sensorSettings.getGpsTcpPort());
            break;
        case SERIAL:
            gpsSensor = new NmeaSerialSensor(sensorSettings.getGpsHostOrSerialPort());
            break;
        case FILE:
            gpsSensor = new NmeaFileSensor(sensorSettings.getGpsFilename(), sensorSettings);
            break;
        case AIS_SHARED:
            gpsSensor = aisSensor;
            break;
        default:
            LOG.error("Unknown sensor connection type: " + sensorSettings.getAisConnectionType());
        }

        if (gpsSensor != null) {
            gpsSensor.addSensorType(SensorType.GPS);
        }

        if (aisSensor != null) {
            aisSensor.setSimulateGps(sensorSettings.isSimulateGps());
            aisSensor.setSimulatedOwnShip(sensorSettings.getSimulatedOwnShip());
            aisSensor.start();
            // Add ais sensor to bean context
            mapHandler.add(aisSensor);
        }
        if (gpsSensor != null && gpsSensor != aisSensor) {
            gpsSensor.setSimulateGps(sensorSettings.isSimulateGps());
            gpsSensor.setSimulatedOwnShip(sensorSettings.getSimulatedOwnShip());
            gpsSensor.start();
            // Add gps sensor to bean context
            mapHandler.add(gpsSensor);
        }

    }

    public static void startRiskHandler() {
        riskHandler = new RiskHandler();
    }

    static void loadProperties() {
        InputStream in = EeINS.class.getResourceAsStream("/eeins.properties");
        try {
            if (in == null) {
                throw new IOException("Properties file not found");
            }
            properties.load(in);
            in.close();
        } catch (IOException e) {
            LOG.error("Failed to load resources: " + e.getMessage());
        }
    }

    private static void createAndShowGUI() {
        // Set the look and feel.
        initLookAndFeel();

        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the main window
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);

        // Create keybinding shortcuts
        makeKeyBindings();

    }

    private static void makeKeyBindings() {
        JPanel content = (JPanel) mainFrame.getContentPane();
        InputMap inputMap = content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        @SuppressWarnings("serial")
        Action zoomIn = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getChartPanel().doZoom(0.5f);
            }
        };

        @SuppressWarnings("serial")
        Action zoomOut = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getChartPanel().doZoom(2f);
            }
        };

        @SuppressWarnings("serial")
        Action centreOnShip = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getChartPanel().centreOnShip();
            }
        };

        @SuppressWarnings("serial")
        Action newRoute = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                // newRouteBtn.requestFocusInWindow();
                mainFrame.getTopPanel().activateNewRouteButton();
            }
        };

        @SuppressWarnings("serial")
        Action routes = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                RouteManagerDialog routeManagerDialog = new RouteManagerDialog(mainFrame);
                routeManagerDialog.setVisible(true);
            }
        };

        @SuppressWarnings("serial")
        Action msi = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getTopPanel().getMsiDialog().setVisible(true);
            }
        };

        @SuppressWarnings("serial")
        Action ais = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getTopPanel().getAisDialog().setVisible(true);
            }
        };

        @SuppressWarnings("serial")
        Action panUp = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getChartPanel().pan(1);
            }
        };
        @SuppressWarnings("serial")
        Action panDown = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getChartPanel().pan(2);
            }
        };

        @SuppressWarnings("serial")
        Action panLeft = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getChartPanel().pan(3);
            }
        };
        @SuppressWarnings("serial")
        Action panRight = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.getChartPanel().pan(4);
            }
        };

        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ADD, 0), "ZoomIn");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SUBTRACT, 0), "ZoomOut");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, 0), "centre");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, 0), "panUp");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, 0), "panDown");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT, 0), "panLeft");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT, 0), "panRight");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_UP, 0), "panUp");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_DOWN, 0), "panDown");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_LEFT, 0), "panLeft");
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_KP_RIGHT, 0), "panRight");
        inputMap.put(KeyStroke.getKeyStroke("control N"), "newRoute");
        inputMap.put(KeyStroke.getKeyStroke("control R"), "routes");
        inputMap.put(KeyStroke.getKeyStroke("control M"), "msi");
        inputMap.put(KeyStroke.getKeyStroke("control A"), "ais");

        content.getActionMap().put("ZoomOut", zoomOut);
        content.getActionMap().put("ZoomIn", zoomIn);
        content.getActionMap().put("centre", centreOnShip);
        content.getActionMap().put("newRoute", newRoute);
        content.getActionMap().put("routes", routes);
        content.getActionMap().put("msi", msi);
        content.getActionMap().put("ais", ais);
        content.getActionMap().put("panUp", panUp);
        content.getActionMap().put("panDown", panDown);
        content.getActionMap().put("panLeft", panLeft);
        content.getActionMap().put("panRight", panRight);

    }

    private static void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOG.error("Failed to set look and feed: " + e.getMessage());
        }

    }

    public static void closeApp() {
        closeApp(false);
    }

    public static void closeApp(boolean restart) {
        // Shutdown routine
        mainFrame.saveSettings();
        settings.saveToFile();
        routeManager.saveToFile();
        msiHandler.saveToFile();
        aisHandler.saveView();
        LOG.info("Closing ee-INS");
        System.exit(restart ? 2 : 0);
    }

    private static void createPluginComponents() {
        Properties props = getProperties();
        String componentsValue = props.getProperty("eeins.plugin_components");
        if (componentsValue == null) {
            return;
        }
        String[] componentNames = componentsValue.split(" ");
        for (String compName : componentNames) {
            String classProperty = compName + ".class";
            String className = props.getProperty(classProperty);
            if (className == null) {
                LOG.error("Failed to locate property " + classProperty);
                continue;
            }
            // Create it if you do...
            try {
                Object obj = java.beans.Beans.instantiate(null, className);
                if (obj instanceof PropertyConsumer) {
                    PropertyConsumer propCons = (PropertyConsumer) obj;
                    propCons.setProperties(compName, props);
                }
                mapHandler.add(obj);
            } catch (IOException e) {
                LOG.error("IO Exception instantiating class \"" + className + "\"");
            } catch (ClassNotFoundException e) {
                LOG.error("Component class not found: \"" + className + "\"");
            }
        }
    }

    public static Properties getProperties() {
        return properties;
    }

    public static String getVersion() {
        return VERSION;
    }

    public static String getMinorVersion() {
        return MINORVERSION;
    }

    public static Settings getSettings() {
        return settings;
    }

    public static NmeaSensor getAisSensor() {
        return aisSensor;
    }

    public static NmeaSensor getGpsSensor() {
        return gpsSensor;
    }

    public static GpsHandler getGpsHandler() {
        return gpsHandler;
    }

    public static MainFrame getMainFrame() {
        return mainFrame;
    }

    public static AisHandler getAisHandler() {
        return aisHandler;
    }

    public static RouteManager getRouteManager() {
        return routeManager;
    }

    public static MapHandler getMapHandler() {
        return mapHandler;
    }

    public static ShoreServices getShoreServices() {
        return shoreServices;
    }

    public static MonaLisaRouteExchange getMonaLisaRouteExchange() {
        return monaLisaRouteExchange;
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
    }

    public static Thread startThread(Runnable t, String name) {
        Thread thread = new Thread(t);
        thread.setName(name);
        thread.start();
        return thread;
    }

    public static double elapsed(long start) {
        double elapsed = System.nanoTime() - start;
        return elapsed / 1000000.0;
    }

    public static RiskHandler getRiskHandler() {
        return riskHandler;
    }

}
