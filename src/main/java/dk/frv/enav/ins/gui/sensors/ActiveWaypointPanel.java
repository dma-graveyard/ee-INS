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
package dk.frv.enav.ins.gui.sensors;

import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.route.ActiveRoute;
import dk.frv.enav.ins.route.RouteManager;

public class ActiveWaypointPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel wptTitleLabel;
	private JLabel brgTitleLabel;
	private JLabel rngTitleLabel;
	private JLabel ttgLegTitleLabel;
	private JLabel ttgRouteTitleLabel;
	private JLabel etaNextTitleLabel;
	private JLabel etaRouteTitleLabel;
	private JLabel etaRouteLabel;
	private JLabel etaNextLabel;
	private JLabel ttgRouteLabel;
	private JLabel ttgLegLabel;
	private JLabel rngLabel;
	private JLabel brgLabel;
	private JLabel wptLabel;
	private RouteManager routeManager;

	public ActiveWaypointPanel() {
		JLabel lblActiveWaypoint = new JLabel("Active Waypoint");
		lblActiveWaypoint.setHorizontalAlignment(SwingConstants.CENTER);
		lblActiveWaypoint.setFont(new Font("Segoe UI", Font.BOLD, 14));
		
		wptTitleLabel = new JLabel("WPT");
		wptTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		brgTitleLabel = new JLabel("BRG");
		brgTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		rngTitleLabel = new JLabel("RNG");
		rngTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		ttgLegTitleLabel = new JLabel("TTG leg");
		ttgLegTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		ttgRouteTitleLabel = new JLabel("TTG route");
		ttgRouteTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		etaNextTitleLabel = new JLabel("ETA next");
		etaNextTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		etaRouteTitleLabel = new JLabel("ETA route");
		etaRouteTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		etaRouteLabel = new JLabel("N/A");
		etaRouteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		etaNextLabel = new JLabel("N/A");
		etaNextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		ttgRouteLabel = new JLabel("N/A");
		ttgRouteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		ttgLegLabel = new JLabel("N/A");
		ttgLegLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		rngLabel = new JLabel("N/A");
		rngLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		brgLabel = new JLabel("N/A");
		brgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		wptLabel = new JLabel("N/A");
		wptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(wptTitleLabel)
						.addComponent(brgTitleLabel))
					.addGap(38)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(wptLabel)
						.addComponent(brgLabel))
					.addContainerGap())
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(10)
					.addComponent(lblActiveWaypoint, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
					.addGap(10))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(etaNextTitleLabel)
						.addComponent(etaRouteTitleLabel)
						.addComponent(ttgRouteTitleLabel)
						.addComponent(ttgLegTitleLabel)
						.addComponent(rngTitleLabel))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(etaNextLabel)
						.addComponent(etaRouteLabel)
						.addComponent(rngLabel)
						.addComponent(ttgLegLabel)
						.addComponent(ttgRouteLabel))
					.addContainerGap(355, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblActiveWaypoint)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(wptLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(wptTitleLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(brgLabel)
						.addComponent(brgTitleLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(rngTitleLabel)
						.addComponent(rngLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(ttgLegTitleLabel)
						.addComponent(ttgLegLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(ttgRouteTitleLabel)
						.addComponent(ttgRouteLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(etaNextTitleLabel)
						.addComponent(etaNextLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(etaRouteTitleLabel)
						.addComponent(etaRouteLabel))
					.addContainerGap(124, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
	
	public void updateActiveNavData(){
		if (routeManager == null) return;
		if(!routeManager.isRouteActive()){
			return;
		}
		ActiveRoute activeRoute = routeManager.getActiveRoute();
		wptLabel.setText(activeRoute.getActiveWp().getName());
		brgLabel.setText(Formatter.formatDegrees(activeRoute.getActiveWpBrg(), 1));
		rngLabel.setText(Formatter.formatDistNM(activeRoute.getActiveWpRng()));
		ttgLegLabel.setText(Formatter.formatTime(activeRoute.getActiveWpTtg()));
		ttgRouteLabel.setText(Formatter.formatTime(activeRoute.getTtg()));
		etaNextLabel.setText(Formatter.formatShortDateTime(activeRoute.getActiveWaypointEta()));
		etaRouteLabel.setText(Formatter.formatShortDateTime(activeRoute.getEta()));
	}
	
	public void setRouteManager(RouteManager routeManager) {
		this.routeManager = routeManager;
	}
}
