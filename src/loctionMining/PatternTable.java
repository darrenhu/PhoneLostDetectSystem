package loctionMining;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

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
	public PatternTable (){
		
	}
	public PatternTable (PatternTable somePatternTable){
		
		for(Entry<String, Pattern> entry : somePatternTable.patternMap.entrySet()){
			Pattern pp = new Pattern(entry.getValue().locationList, entry.getValue().frequency);
			this.patternMap.put(entry.getKey(), pp);
			this.patternArrayList.add(pp);
		}
	}
}
