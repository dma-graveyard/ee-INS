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
package dk.frv.enav.ins.risk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dk.frv.enav.common.xml.risk.response.Risk;
import dk.frv.enav.common.xml.risk.response.RiskList;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.service.communication.webservice.ShoreServiceException;

public class RiskHandler implements Runnable {

	private static final Logger LOG = Logger.getLogger(RiskHandler.class);

	public enum RiskLevel {
		HIGH, MEDIUM, LOW, UNKNOWN
	};

	private Map<Long, RiskList> riskListMap = new HashMap<Long, RiskList>();
	private static final Object mutex = new Object();

	public RiskHandler() {
		super();
		EeINS.startThread(this, "RiskHandler");
	}

	@Override
	public void run() {

		while (EeINS.getSettings().getAisSettings().isShowRisk()) {
			//VesselTarget ownShip = EeINS.getAisHandler().getOwnShip();
			List<RiskList> riskLists = new ArrayList<RiskList>();
			try {
				riskLists = EeINS.getShoreServices().getRiskIndexes(54.75, 56.0, 10.65, 11.25);
			} catch (ShoreServiceException e) {
				LOG.warn("cannot get risk indexes", e);
			}
			synchronized (mutex) {
				riskListMap.clear();
				for (RiskList list : riskLists) {
					riskListMap.put(list.getMmsi().longValue(), list);
				}
			}
			EeINS.sleep(10000);
		}

	}

	public void toggleRiskHandler(boolean onOff) {

		EeINS.getSettings().getAisSettings().setShowRisk(onOff);

		if (onOff) {
			// start a new one
			EeINS.startRiskHandler();
		} else {
			// stopping, clear the index map as it wont be updated any longer.
			riskListMap.clear();
		}

	}

	public RiskList getRiskList(Long mmsi) {
		return riskListMap.get(mmsi);

	}

	public Risk getRiskLevel(Long mmsi) {
		
		RiskList list = riskListMap.get(mmsi);
		
		if (list == null || list.getRisks().isEmpty()) {
			return null;
		}
		/*
		 * get total risk
		 */
		for (Risk risk : list.getRisks()) {
			
			if(risk.getAccidentType().equals("MACHINERYFAILURE")){
				/*
				 * MACHINERYFAILURE is total risk for all incident type 
				 * 
				 */
				return risk;
				
			}
		}
		return null;
	}
}
