package loctionMining;

import java.util.BitSet;

public class LocationRecord {
	String loca;
	BitSet record;
	public LocationRecord(String loca) {
		super();
		this.loca = loca;
		this.record = new BitSet();
	}
	@Override
	public String toString() {
		return loca+" - "+record.toString();
	}
}
