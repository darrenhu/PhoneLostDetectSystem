package loctionMining;

public class Pattern {
	String locationList;
	double frequency;

	public Pattern(String locationList, double frequency) {
		super();
		this.locationList = locationList;
		this.frequency = frequency;
	}
	@Override
	public String toString() {
		return locationList+" "+frequency;
	}
}
