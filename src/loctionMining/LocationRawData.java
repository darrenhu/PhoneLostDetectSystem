package loctionMining;

import java.util.Date;

public class LocationRawData {
	Date date = null;
	String loca = null;
	public LocationRawData(Date date, String loca) {
		super();
		this.date = date;
		this.loca = loca;
	}
	@Override
	public String toString() {
		return date+" - "+loca;
	}
	
}
