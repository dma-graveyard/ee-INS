package dk.frv.enav.ins.risk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.risk.response.RiskIndexes;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.services.shore.ShoreServiceException;

public class RiskHandler implements Runnable {

	private static final Logger LOG = Logger.getLogger(RiskHandler.class);

	private Map<Long, RiskIndexes> indexMap = new HashMap<Long, RiskIndexes>();
	private static final Object mutex = new Object();

	public RiskHandler() {
		super();
		EeINS.startThread(this, "RiskHandler");
	}

	@Override
	public void run() {

		while (EeINS.getSettings().getAisSettings().isShowRisk()) {
			VesselTarget ownShip = EeINS.getAisHandler().getOwnShip();
			List<RiskIndexes> indexList = new ArrayList<RiskIndexes>();
			try {
				indexList = EeINS.getShoreServices().getRiskIndexes(54.75, 56.0, 10.65, 11.25);
			} catch (ShoreServiceException e) {
				LOG.warn("cannot get risk indexes", e);
			}
			synchronized (mutex) {
				indexMap.clear();
				for (RiskIndexes riskIndex : indexList) {
					indexMap.put(riskIndex.getMmsi().longValue(), riskIndex);
				}
			}
			EeINS.sleep(10000);
		}
	
	}
	
	public void toggleRiskHandler(boolean onOff){
		
		EeINS.getSettings().getAisSettings().setShowRisk(onOff);
		
		if(onOff){
			//start a new one
			EeINS.startRiskHandler();
		}else{
			//stopping, clear the index map as it wont be updated any longer.
			indexMap.clear();
		}
		
	}

	public RiskIndexes getRiskIndex(Long mmsi) {
		return indexMap.get(mmsi);
		
	}

}
