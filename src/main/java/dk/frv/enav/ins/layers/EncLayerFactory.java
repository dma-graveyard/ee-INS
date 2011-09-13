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
package dk.frv.enav.ins.layers;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.bbn.openmap.Layer;
import com.bbn.openmap.util.PropUtils;

import dk.frv.enav.ins.settings.MapSettings;

public class EncLayerFactory {
	
	private static final Logger LOG = Logger.getLogger(EncLayerFactory.class);
	private Properties encProps = new Properties();
	private MapSettings mapSettings;
	private Layer encLayer = null;
	
	public EncLayerFactory(MapSettings mapSettings) {
		this.mapSettings = mapSettings;
		// Use ENC?
		if (!mapSettings.isUseEnc()) {
			return;
		}				
		// Try to load ENC props
		if (!PropUtils.loadProperties(encProps, ".", "enc.properties")) {
			LOG.error("No enc.properties file found");
			return;
		}
		
		// Make layer instance
		String classProperty = "enc.class";
		String className = encProps.getProperty(classProperty);
		if (className == null) {
			LOG.error("Failed to locate property " + classProperty);
			return;
		}
		try {
			Object obj = java.beans.Beans.instantiate(null, className);
			Layer layer = (Layer)obj;
			layer.setProperties("enc", encProps);
			layer.setAddAsBackground(true);
			layer.setVisible(true);
			encLayer = layer;
		} catch (ClassNotFoundException e) {
			LOG.error("Layer class not found: \"" + className + "\"");
		} catch (IOException e) {
			LOG.error("IO Exception instantiating class \"" + className + "\"");
		}

	}
	
	public Layer getEncLayer() {
		return encLayer;
	}
	
	public void setMapSettings() {
		if (encLayer == null) {
			return;
		}
		
		// Try to set Navicon settings
		if (setNaviconSettings())  {
			return;
		}
		
	}

	private boolean setNaviconSettings() {		
		Properties marinerSettings = new Properties();
		
		// Determine if Navicon layer
		if (!encLayer.getClass().getName().contains("navicon")) {
			return false;
		}

		Class<?>[] argTypes = new Class<?>[0];
		Object[] arguments = new Object[0];
		try {
			// Get settings
			Method method = encLayer.getClass().getDeclaredMethod("getS52MarinerSettings", argTypes);
			Object obj = method.invoke(encLayer, arguments);
			marinerSettings = (Properties)obj;

			// Set settings from configuration
			marinerSettings.setProperty("MARINER_PARAM.S52_MAR_SHOW_TEXT", Boolean.toString(mapSettings.isS52ShowText()));
			marinerSettings.setProperty("MARINER_PARAM.S52_MAR_SHALLOW_PATTERN", Boolean.toString(mapSettings.isS52ShallowPattern()));
			marinerSettings.setProperty("MARINER_PARAM.S52_MAR_SHALLOW_CONTOUR", Integer.toString(mapSettings.getS52ShallowContour()));
			marinerSettings.setProperty("MARINER_PARAM.S52_MAR_SAFETY_DEPTH", Integer.toString(mapSettings.getS52SafetyDepth()));
			marinerSettings.setProperty("MARINER_PARAM.S52_MAR_SAFETY_CONTOUR", Integer.toString(mapSettings.getS52SafetyContour()));
			marinerSettings.setProperty("MARINER_PARAM.S52_MAR_DEEP_CONTOUR", Integer.toString(mapSettings.getS52DeepContour()));
			marinerSettings.setProperty("MARINER_PARAM.useSimplePointSymbols", Boolean.toString(mapSettings.isUseSimplePointSymbols()));
			marinerSettings.setProperty("MARINER_PARAM.usePlainAreas", Boolean.toString(mapSettings.isUsePlainAreas()));
			marinerSettings.setProperty("MARINER_PARAM.S52_MAR_TWO_SHADES", Boolean.toString(mapSettings.isS52TwoShades()));
			
			// Set settings on layer
			argTypes = new Class<?>[1];
			argTypes[0] = Properties.class;
			arguments = new Object[1];
			arguments[0] = marinerSettings;
			method = encLayer.getClass().getDeclaredMethod("setS52MarinerSettings", argTypes);
			method.invoke(encLayer, arguments);
			
			return true;
		} catch (Exception e) {
			LOG.error("Failed to set mariner settings on Navicon ENC layer: " + e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}

}

