package loctionMining;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PatternTable {
	LinkedHashMap<String, Pattern> patternMap = new LinkedHashMap<String, Pattern>();
	ArrayList<Pattern> patternArrayList = new ArrayList<Pattern>();
	public void add(Pattern newPattern){
		patternMap.put(newPattern.locationList, newPattern);
		patternArrayList.add(newPattern);
	}
	@Override
	public String toString() {
		return patternMap.values().toString();
	}
}
