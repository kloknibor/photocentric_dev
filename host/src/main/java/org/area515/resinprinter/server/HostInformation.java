package org.area515.resinprinter.server;

public class HostInformation {
	private String deviceName;
	private String manufacturer;
	private String hardwareRevision;
	private String serialNo;
	
	private HostInformation() {}
	
	public HostInformation(String deviceName, String manufacturer, String hardwareRevision, String serialNo) {
		this.deviceName = deviceName;
		this.manufacturer = manufacturer;
		this.hardwareRevision = hardwareRevision;
		this.serialNo = serialNo;
	}

	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	public String getHardwareRevision() {
		return hardwareRevision;
	}
	public void setHardwareRevision(String hardwareRevision) {
		this.hardwareRevision = hardwareRevision;
	}
	
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
}
