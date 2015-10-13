package loctionMining;

import java.util.ArrayList;
import java.util.HashSet;

public class LocationDecisionModel {
	ArrayList<PatternTable> hourModel;
	ArrayList<PatternTable> hourModelShift;
	ArrayList<PatternTable> oneDayModel;
	HashSet<String> locs = new HashSet<String>();
	public LocationDecisionModel() {
		super();
		this.hourModel = new ArrayList<PatternTable>();
		this.hourModelShift = new ArrayList<PatternTable>();
		this.oneDayModel = new ArrayList<PatternTable>();
	}
	public LocationDecisionModel(LocationDecisionModel ldm) {
		super();
		this.hourModel = new ArrayList<PatternTable>();
		this.hourModelShift = new ArrayList<PatternTable>();
		this.oneDayModel = new ArrayList<PatternTable>();
		for (PatternTable hm :ldm.hourModel){
			this.hourModel.add(new PatternTable(hm));
		}
		for (PatternTable hms :ldm.hourModelShift){
			this.hourModelShift.add(new PatternTable(hms));
		}
		for (PatternTable odm :ldm.oneDayModel){
			this.oneDayModel.add(new PatternTable(odm));
		}
	}
}
