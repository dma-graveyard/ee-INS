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
package dk.frv.enav.ins.util.route;

import java.util.ArrayList;
import java.util.List;

import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.proj.Length;


public class ApproxRouteGenerator extends SimpleRouteGenerator {

	private double tolleranceMeters = 185.2; // 1/10 nm
	private List<Geo> geoPoints = new ArrayList<Geo>();
	
	@Override
	public List<TimePoint> generateRoute(List<TimePoint> track) {
		this.track = track;
		// Create geo points for each track points
		System.out.println("\tCreating geo points");
		for (TimePoint point : track) {
			geoPoints.add(new Geo(point.getLatitude(), point.getLongitude()));
		}
		
		setTolleranceMeters(100);
		
		// Generate route by divide and conquer
		route.add(track.get(0));
		_generate(0, track.size() - 1);
		route.add(track.get(track.size() - 1));
		
		return route;
	}
	
	private void _generate(int start, int end) {
		if (end - start == 1) {
			return;
		}
		if (end - start < 1) {
			System.out.println("ERROR: Should not happen");
			return;
		}
		// Find point with maximum distance to line from start to end
		Geo x1 = geoPoints.get(start);
		Geo x2 = geoPoints.get(end);
		double x1x2Length = x1.distance(x2);
		
		double maxDistance = -1;
		int maxPoint = -1;
		for (int i=start; i <= end; i++) {
			Geo x0 = geoPoints.get(i);
			Geo a = x0.subtract(x1);
			Geo b = x0.subtract(x2);
			double distMeters = Length.KM.fromRadians(a.crossLength(b) / x1x2Length) * 1000.0;
			if (distMeters > maxDistance) {
				maxDistance = distMeters;
				maxPoint = i;
			}			
		}
		
		if (maxDistance > tolleranceMeters) {
			_generate(start, maxPoint);
			route.add(track.get(maxPoint));
			_generate(maxPoint, end);
		}		
	}
	
	public double getTolleranceMeters() {
		return tolleranceMeters;
	}
	
	public void setTolleranceMeters(double tolleranceMeters) {
		this.tolleranceMeters = tolleranceMeters;
	}

}
