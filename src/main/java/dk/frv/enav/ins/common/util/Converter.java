package dk.frv.enav.ins.common.util;

public class Converter {
	
	private static final int NM_IN_METERS = 1852;

	public static double metersToNm(double meters) {
		return meters / (double)NM_IN_METERS;
	}
	
	public static double nmToMeters(double nm) {
		return nm * NM_IN_METERS;
	}
	
}
