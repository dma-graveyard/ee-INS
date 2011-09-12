package dk.frv.enav.ins.status;

public abstract class ComponentStatus {
	
	public enum Status {
		OK, ERROR, UNKNOWN, PARTIAL
	}
	
	protected Status status = Status.UNKNOWN;
	protected String name = "Component";
	protected String shortStatusText = null;
	
	public ComponentStatus(String name) {
		this.name = name;		
	}
	
	public ComponentStatus(String name, Status status) {
		this(name);
		this.status = status;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public String getShortStatusText() {
		return shortStatusText;
	}
	
	public void setShortStatusText(String shortStatusText) {
		this.shortStatusText = shortStatusText;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public abstract String getStatusHtml();
	
}
