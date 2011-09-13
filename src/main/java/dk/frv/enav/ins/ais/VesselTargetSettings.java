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

import java.io.Serializable;

/**
 * Class holding settings for vessel targets
 */
public class VesselTargetSettings implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private boolean hide = false;
	private boolean showRoute = false;
	
	/**
	 * Empty constructor
	 */
	public VesselTargetSettings() {
		
	}
	
	/**
	 * Copy constructor
	 * @param settings
	 */
	public VesselTargetSettings(VesselTargetSettings settings) {
		this.hide = settings.hide;
		this.showRoute = settings.showRoute;
	}

	/**
	 * Is the target hidden on the display or not
	 * @return
	 */
	public boolean isHide() {
		return hide;
	}

	/**
	 * Set visibility
	 * @param hide
	 */
	public void setHide(boolean hide) {
		this.hide = hide;
	}

	/**
	 * Will the intended route be shown for the target if it is available
	 * @return
	 */
	public boolean isShowRoute() {
		return showRoute;
	}

	/**
	 * Set visibility of intended route
	 * @param showRoute
	 */
	public void setShowRoute(boolean showRoute) {
		this.showRoute = showRoute;
	}
	
}
