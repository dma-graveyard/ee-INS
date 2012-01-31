package dk.frv.enav.ins.risk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dk.frv.enav.common.xml.risk.response.Risk;
import dk.frv.enav.common.xml.risk.response.RiskList;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.services.shore.ShoreServiceException;

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
