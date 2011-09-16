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
package dk.frv.enav.ins.settings;

import java.io.Serializable;
import java.util.Properties;

import com.bbn.openmap.util.PropUtils;

/**
 * Navigational settings
 */
public class NavSettings implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "nav.";
		
	private boolean autoFollow = false;
	private int autoFollowPctOffTollerance = 10;
	private boolean lookAhead = false;
	private double cogVectorLength = 6; // minutes
	private float showArrowScale = 450000;
	private int showMinuteMarksSelf = 200;
	private double defaultSpeed = 10.0;
	private double defaultTurnRad = 0.5;
	private double defaultXtd = 0.1;
	private double minWpRadius = 0.2;
	private boolean relaxedWpChange = true;
	
	public NavSettings() {
	}

	public void readProperties(Properties props) {
		autoFollow = PropUtils.booleanFromProperties(props, PREFIX + "autoFollow", autoFollow);
		autoFollowPctOffTollerance = PropUtils.intFromProperties(props, PREFIX + "autoFollowPctOffTollerance", autoFollowPctOffTollerance);
		lookAhead = PropUtils.booleanFromProperties(props, PREFIX + "lookAhead", lookAhead);
		cogVectorLength = PropUtils.doubleFromProperties(props, PREFIX + "cogVectorLength", cogVectorLength);
		showArrowScale = PropUtils.floatFromProperties(props, PREFIX + "showArrowScale", showArrowScale);
		showMinuteMarksSelf = PropUtils.intFromProperties(props, PREFIX + "showMinuteMarksSelf", showMinuteMarksSelf);
		defaultSpeed = PropUtils.doubleFromProperties(props, PREFIX + "defaultSpeed", defaultSpeed);
		defaultTurnRad = PropUtils.doubleFromProperties(props, PREFIX + "defaultTurnRad", defaultTurnRad);
		defaultXtd = PropUtils.doubleFromProperties(props, PREFIX + "defaultXtd", defaultXtd);
		minWpRadius = PropUtils.doubleFromProperties(props, PREFIX + "minWpRadius", minWpRadius);
		relaxedWpChange = PropUtils.booleanFromProperties(props, PREFIX + "relaxedWpChange", relaxedWpChange);
	}
	
	public void setProperties(Properties props) {
		props.put(PREFIX + "autoFollow", Boolean.toString(autoFollow));
		props.put(PREFIX + "autoFollowPctOffTollerance", Integer.toString(autoFollowPctOffTollerance));
		props.put(PREFIX + "lookAhead", Boolean.toString(lookAhead));
		props.put(PREFIX + "cogVectorLength", Double.toString(cogVectorLength));
		props.put(PREFIX + "showArrowScale", Float.toString(showArrowScale));
		props.put(PREFIX + "showMinuteMarksSelf", Float.toString(showMinuteMarksSelf));
		props.put(PREFIX + "defaultSpeed", Double.toString(defaultSpeed));
		props.put(PREFIX + "defaultTurnRad", Double.toString(defaultTurnRad));
		props.put(PREFIX + "defaultXtd", Double.toString(defaultXtd));
		props.put(PREFIX + "minWpRadius", Double.toString(minWpRadius));
		props.put(PREFIX + "relaxedWpChange", Boolean.toString(relaxedWpChange));
	}
	
	public boolean isAutoFollow() {
		return autoFollow;
	}
	
	public void setAutoFollow(boolean autoFollow) {
		this.autoFollow = autoFollow;
	}
	
	public double getCogVectorLength() {
		return cogVectorLength;
	}
	
	public void setCogVectorLength(double cogVectorLength) {
		this.cogVectorLength = cogVectorLength;
	}
	
	public boolean isLookAhead() {
		return lookAhead;
	}
	
	public void setLookAhead(boolean lookAhead) {
		this.lookAhead = lookAhead;
	}
	
	public int getAutoFollowPctOffTollerance() {
		return autoFollowPctOffTollerance;
	}
	
	public void setAutoFollowPctOffTollerance(int autoFollowPctOffTollerance) {
		this.autoFollowPctOffTollerance = autoFollowPctOffTollerance;
	}
	
	public float getShowArrowScale() {
		return showArrowScale;
	}
	
	public void setShowArrowScale(float showArrowScale) {
		this.showArrowScale = showArrowScale;
	}

	public int getShowMinuteMarksSelf() {
		return showMinuteMarksSelf;
	}

	public void setShowMinuteMarksSelf(int showMinuteMarksSelf) {
		this.showMinuteMarksSelf = showMinuteMarksSelf;
	}

	public double getDefaultSpeed() {
		return defaultSpeed;
	}

	public void setDefaultSpeed(double defaultSpeed) {
		this.defaultSpeed = defaultSpeed;
	}

	public double getDefaultTurnRad() {
		return defaultTurnRad;
	}

	public void setDefaultTurnRad(double defaultTurnRad) {
		this.defaultTurnRad = defaultTurnRad;
	}

	public double getDefaultXtd() {
		return defaultXtd;
	}

	public void setDefaultXtd(double defaultXtd) {
		this.defaultXtd = defaultXtd;
	}
	
	public double getMinWpRadius() {
		return minWpRadius;
	}
	
	public boolean isRelaxedWpChange() {
		return relaxedWpChange;
	}
	
	public void setRelaxedWpChange(boolean relaxedWpChange) {
		this.relaxedWpChange = relaxedWpChange;
	}
	
}
