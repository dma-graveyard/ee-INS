package dk.frv.enav.ins.services.shore;

public class ShoreServiceException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	private int errroCode;
	private String extraMessage;
	
	public ShoreServiceException(int errorCode, String extraMessage) {
		this(errorCode);
		this.extraMessage = extraMessage;
	}
	
	public ShoreServiceException(int errorCode) {
		super(ShoreServiceErrorCode.getErrorMessage(errorCode));
		this.errroCode = errorCode;
	}
	
	public int getErrroCode() {
		return errroCode;
	}
	
	public void setErrroCode(int errroCode) {
		this.errroCode = errroCode;
	}
	
	public void setExtraMessage(String extraMessage) {
		this.extraMessage = extraMessage;
	}
	
	public String getExtraMessage() {
		return extraMessage;
	}

}
