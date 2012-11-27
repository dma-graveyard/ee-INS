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
package dk.frv.enav.ins.layers.ais;

import java.text.DecimalFormat;

import dk.frv.ais.message.AisMessage;
import dk.frv.enav.common.xml.risk.response.Risk;
import dk.frv.enav.common.xml.risk.response.RiskList;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.ais.VesselPositionData;
import dk.frv.enav.ins.ais.VesselStaticData;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.ais.VesselTarget.AisClass;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.gui.InfoPanel;

/**
 * AIS target mouse over info
 */
public class AisTargetInfoPanel extends InfoPanel implements Runnable {
	private static final long serialVersionUID = 1L;

	private VesselTarget vesselTarget;

	public AisTargetInfoPanel() {
		super();
		(new Thread(this)).start();
	}

	public void showAisInfo(VesselTarget vesselTarget) {
		this.vesselTarget = vesselTarget;
		VesselStaticData staticData = vesselTarget.getStaticData();
		String name = null;
		String callsign = null;
		if (staticData != null) {
			name = AisMessage.trimText(staticData.getName());
			callsign = AisMessage.trimText(staticData.getCallsign());
		}
		VesselPositionData positionData = vesselTarget.getPositionData();
		String cog = "N/A";
		String sog = "N/A";

		if (positionData != null) {
			cog = Formatter.formatDegrees((double) positionData.getCog(), 0);
			sog = Formatter.formatSpeed((double) positionData.getSog());
		}

		StringBuilder str = new StringBuilder();
		str.append("<html>");
		if (name != null) {
			str.append(name + " (" + vesselTarget.getMmsi() + ")");
		} else {
			str.append(vesselTarget.getMmsi());
		}
		if (vesselTarget.getAisClass() == AisClass.B) {
			str.append(" [<b>B</b>]");
		}
		str.append("<br/>");

		/*
		 * Get the risk object
		 */
		RiskList riskList = EeINS.getRiskHandler().getRiskList(vesselTarget.getMmsi());
//		if( riskList!=null && !riskList.getRisks().isEmpty()){
//			Risk risk = riskList.getRisks().iterator().next();
//			str.append("length : " + risk.getLength() + " m " );
//			
//		}
		

		if (callsign != null) {
			str.append(callsign + "<br/>");
		}
		str.append("COG " + cog + "  SOG " + sog + "<br/>");

		Risk riskIndex = EeINS.getRiskHandler().getRiskLevel(vesselTarget.getMmsi());
		if (riskIndex != null) {
			DecimalFormat fmt = new DecimalFormat("#.####");
			str.append("risk index : " + fmt.format(riskIndex.getRiskNorm()) + " (" +fmt.format(riskIndex.getProbability()* riskIndex.getConsequence() * 1000000) + "$) <br/>");
		}

		if (riskList != null) {
			double total = 0;
			for (Risk risk : riskList.getRisks()) {
				if (risk.getAccidentType().equals("MACHINERYFAILURE")) {
					continue;
				}
				total += risk.getRiskNorm();
			}
			for (Risk risk : riskList.getRisks()) {
				if (risk.getAccidentType().equals("MACHINERYFAILURE")) {
					continue;
				}
				str.append(risk.getAccidentType() + " : " + (int) (risk.getRiskNorm() / total * 100) + " % <br/>");
			}
		}
		str.append("</html>");

		showText(str.toString());
	}

	@Override
	public void run() {
		while (true) {
			EeINS.sleep(10000);
			if (this.isVisible() && vesselTarget != null) {
				showAisInfo(vesselTarget);
			}
		}
	}
}
