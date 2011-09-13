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
package dk.frv.enav.ins.services.shore;

import java.util.HashMap;

public class ShoreServiceErrorCode {
	
	public static final int OK = 0;
	public static final int NO_CONTACT = 1;
	public static final int SERVER_ERROR = 2;
	public static final int NO_OWN_SHIP_DATA = 3;
	public static final int NO_VALID_GPS_DATA = 4;
	public static final int INTERNAL_ERROR = 5;
	public static final int NO_CONNECTION_TO_SERVER = 6;
	public static final int INVALID_RESPONSE = 7;
	public static final int SERVICE_ERROR = 8;	
		
	private static ShoreServiceErrorCode instance = null;
	
	private HashMap<Integer, String> errorMsg = new HashMap<Integer, String>();
	
	private ShoreServiceErrorCode() {
		errorMsg.put(OK, "OK");
		errorMsg.put(NO_CONTACT, "No contact to shore server");
		errorMsg.put(SERVER_ERROR, "Error on shore server");
		errorMsg.put(NO_OWN_SHIP_DATA, "No own ship data available");
		errorMsg.put(NO_VALID_GPS_DATA, "No valid GPS data");
		errorMsg.put(INTERNAL_ERROR, "Internal system error");
		errorMsg.put(NO_CONNECTION_TO_SERVER, "No connection to shore");
		errorMsg.put(INVALID_RESPONSE, "Invalid response");
		errorMsg.put(SERVICE_ERROR, "Service error");
	}
	
	public static String getErrorMessage(int errorCode) {
		ShoreServiceErrorCode errorCodes = getInstance();
		if (errorCodes.errorMsg.containsKey(errorCode)) {
			return errorCodes.errorMsg.get(errorCode);
		}
		return "Unknown error";
	}
	
	public static ShoreServiceErrorCode getInstance() {
		synchronized (ShoreServiceErrorCode.class) {
			if (instance == null) {
				instance = new ShoreServiceErrorCode();
			}
			return instance;			
		}
	}

}
