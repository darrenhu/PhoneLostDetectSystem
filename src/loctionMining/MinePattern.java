package loctionMining;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class MinePattern {
	private ArrayList<Date> timeTable;
	private ArrayList<Date> timespar;
	public PatternTable patternTable;
	public int patternSize = 3;
	public int patternThreshold = 5;
	public int gapSize = 5;
	private ArrayList<DayOrderedRawData> hourlyRawData;
	private ArrayList<DayOrderedRawData> shiftedHourlyRawData;
	private ArrayList<DayOrderedRawData> sixHourRawData;
	
	public LocationDecisionModel minePattern(DayOrderedRawData rawdata) {
		LocationDecisionModel locationDecisionModel = new LocationDecisionModel();
		hourlyRawData = new ArrayList<DayOrderedRawData>();
		for (int i=0;i<24;i++){
			hourlyRawData.add(new DayOrderedRawData());
		}
		shiftedHourlyRawData = new ArrayList<DayOrderedRawData>();
		for (int i=0;i<25;i++){
			shiftedHourlyRawData.add(new DayOrderedRawData());
		}
		sixHourRawData = new ArrayList<DayOrderedRawData>();
		for (int i=0;i<4;i++){
			sixHourRawData.add(new DayOrderedRawData());
		}
		//patternTable = new PatternTable();
		timeTable=new ArrayList<Date>();
		setTimeTable(rawdata);
		separteData(rawdata);

		for (DayOrderedRawData hrd: hourlyRawData){
			timespar = new ArrayList<Date>();
			patternTable = new PatternTable();
			LinkedHashMap<String, LocationRecord> allLocations = findAllLocation(hrd);
			//	System.out.println(allLocations.toString());
			dFS(new ArrayList<String>(), allLocations, 1);
			locationDecisionModel.hourModel.add(patternTable);
		}
		for (DayOrderedRawData shrd: shiftedHourlyRawData){
			timespar = new ArrayList<Date>();
			patternTable = new PatternTable();
			LinkedHashMap<String, LocationRecord> allLocations = findAllLocation(shrd);
			//	System.out.println(allLocations.toString());
			dFS(new ArrayList<String>(), allLocations, 1);
			locationDecisionModel.hourModelShift.add(patternTable);
		}
		for (DayOrderedRawData shmd: sixHourRawData){
			timespar = new ArrayList<Date>();
			patternTable = new PatternTable();
			LinkedHashMap<String, LocationRecord> allLocations = findAllLocation(shmd);
			//	System.out.println(allLocations.toString());
			dFS(new ArrayList<String>(), allLocations, 1);
			locationDecisionModel.oneDayModel.add(patternTable);
		}
		return locationDecisionModel;
	}
	public void setTimeTable(DayOrderedRawData rawdata){
//		for(LocationRawData eachRecord: rawdata){
//			timespar.add(eachRecord.date);
//		}
		Calendar calSTT = Calendar.getInstance();
		calSTT.setTime(rawdata.get(0).date);
		calSTT.set(Calendar.HOUR_OF_DAY, 0);
		calSTT.set(Calendar.MINUTE, 0);
		calSTT.set(Calendar.SECOND, 0);
		for (int i = 0; i< 48; i++){
			calSTT.add(Calendar.MINUTE, 30);
			timeTable.add(calSTT.getTime());
		}
	}
	public void separteData(DayOrderedRawData rawdata){
		Calendar calSPD = Calendar.getInstance();
		int timeTableIndex = 0;
		int hourlyIndex = 0;
		int shiftHourlyIndex = 0;
		for (int o=0;o<rawdata.size();){
			calSPD.setTime(rawdata.get(o).date);
		//	System.out.println(hourlyIndex+","+shiftHourlyIndex+","+timeTableIndex+","+rawdata.get(o).date+","+timeTable.get(timeTableIndex));
			if (rawdata.get(o).date.before(timeTable.get(timeTableIndex))){
				hourlyRawData.get(hourlyIndex).add(rawdata.get(o));
				shiftedHourlyRawData.get(shiftHourlyIndex).add(rawdata.get(o));
				o++;
			}else{
				if (timeTableIndex%2 == 0){
					shiftHourlyIndex++;	
				}else{
					hourlyIndex++;
				}
				timeTableIndex++;
				if (rawdata.get(o).date.before(timeTable.get(timeTableIndex))){
					hourlyRawData.get(hourlyIndex).add(rawdata.get(o));
					shiftedHourlyRawData.get(shiftHourlyIndex).add(rawdata.get(o));
					o++;
				}
			}
			
		}
		for (int p=0;p<24;p+=6){
			DayOrderedRawData tempSPD = new DayOrderedRawData();
			tempSPD.addAll(hourlyRawData.get(p));
			tempSPD.addAll(hourlyRawData.get(p+1));
			tempSPD.addAll(hourlyRawData.get(p+2));
			tempSPD.addAll(hourlyRawData.get(p+3));
			tempSPD.addAll(hourlyRawData.get(p+4));
			tempSPD.addAll(hourlyRawData.get(p+5));
			sixHourRawData.add(tempSPD);
		}
	}
	
	public void dFS(ArrayList<String> fatherLoca,
			LinkedHashMap<String, LocationRecord> allLocations, int level) {
		if (level <= patternSize) {
			for (String loca : allLocations.keySet()) {
				ArrayList<String> nextFatherLoca = new ArrayList<String>(fatherLoca);
				Pattern currentPattern = null;
				if (level != 1) {
					nextFatherLoca.add(loca);
					currentPattern = merge(fatherLoca, loca, allLocations);
					//System.out.println(currentPattern.locationList+" "+currentPattern.frequency);
				} else {
					nextFatherLoca.add(loca);
				}
				if (currentPattern != null && currentPattern.frequency > patternThreshold) {
					patternTable.add(currentPattern);
				}
				// System.out.println("the level are in " +
				// nextFatherLoca.loca);
				dFS(nextFatherLoca, allLocations, level + 1);
			}
		}
	}
	public Pattern merge(ArrayList<String> father, String a, LinkedHashMap<String, LocationRecord> allLocations){
		ArrayList<String> templist = new ArrayList<String>(father);
		templist.add(a);
		BitSet result = new BitSet();
		for (int j = 0; j< timespar.size();j++){
			if (allLocations.get(templist.get(0)).record.get(j)){
				
				List<BitSet> mergeBS = new LinkedList<BitSet>();
				for(String b: templist){
				//	System.out.println(templist.toString());
					mergeBS.add(getBitset(j, allLocations.get(b).record));
				}
				BitSet tempWindow = mergeBitSet(mergeBS);
				for (int n=0;n<= tempWindow.size();n++){
					if (tempWindow.get(n)){
					//	System.out.println("1111111111111111");
						result.set(n+j);
					}
				}
			}
		}
		Pattern retrunPattern = new Pattern(templist.toString(), result.cardinality());
	//	System.out.println(result.cardinality());
		return retrunPattern;
		
	}
	public BitSet mergeBitSet(List<BitSet> bitSetList){
		List<BitSet> temp = new LinkedList<BitSet>();
		for(int m = 0;m < bitSetList.size();m++){
			temp.add((BitSet) bitSetList.get(m).clone());
		//	System.out.println(bitSetList.get(m).toString());
		}//System.out.println("/////////////////////////////////////");
		int remainingGap;
		for (int l=1;l<bitSetList.size();l++){
			remainingGap = 0;
			for (int i = 0; i < bitSetList.get(0).length(); i++) { // S-step
				if (remainingGap > 0) {
					if (temp.get(0).get(i)) {
						temp.get(0).set(i);
						remainingGap = gapSize;
					} else {
						temp.get(0).set(i);
						remainingGap--;
					}
				} else {
					if (temp.get(0).get(i)) {
						temp.get(0).clear(i);
						remainingGap = gapSize;
					}
				}
			}
			temp.get(0).and(temp.get(1));
			temp.remove(1);
		}
		
		return temp.get(0);
	}
	
	public BitSet getBitset(int indexi, BitSet bs){
		//System.out.println(bs.toString());
		BitSet tempbs = new BitSet();
		timespar.get(indexi);
		Calendar calGBS = Calendar.getInstance();
		calGBS.setTime(timespar.get(indexi));
		calGBS.add(Calendar.HOUR, 1);
		for (int k = indexi;k <timespar.size();k++){
			if(0 >= timespar.get(k).compareTo(calGBS.getTime())){
				tempbs.set(k, bs.get(k));
			}else{
				break;
			}
		}
		//System.out.println(tempbs);
		return tempbs;
	}

//	public LocationRecord merge(LocationRecord a, LocationRecord b) {
//		LocationRecord temp = new LocationRecord(a.loca + "-" + b.loca);
//		temp.record = (BitSet) a.record.clone();
//		int remainingGap = 0;
//		for (int i = 0; i < temp.record.length(); i++) { // S-step
//			if (remainingGap > 0) {
//				if (temp.record.get(i)) {
//					temp.record.set(i);
//					remainingGap = gapSize;
//				} else {
//					temp.record.set(i);
//					remainingGap--;
//				}
//			} else {
//				if (temp.record.get(i)) {
//					temp.record.clear(i);
//					remainingGap = gapSize;
//				}
//			}
//		}
//		temp.record.and(b.record); // merge bitset
//		return temp;
//	}

	public LinkedHashMap<String, LocationRecord> findAllLocation(
			DayOrderedRawData rawdata) {
		LinkedHashMap<String, LocationRecord> allLocations = new LinkedHashMap<String, LocationRecord>();
		// for (LocationRawData oneLocation:
		// rawdata){///////////////////////////////xuexi
		for (int i = 0; i < rawdata.size(); i++) {
			LocationRawData oneLocation = rawdata.get(i);
			if (!allLocations.containsKey(oneLocation.loca)) {
				LocationRecord temploc = new LocationRecord(oneLocation.loca);
				allLocations.put(oneLocation.loca, temploc);
			}
			allLocations.get(oneLocation.loca).record.set(i, true);
			timespar.add(rawdata.get(i).date);
		}
		return allLocations;
	}
	public static LocationDecisionModel mergeDecisionModel(LocationDecisionModel a, LocationDecisionModel b){
		if (a.hourModel.isEmpty())return b;
		LocationDecisionModel resultMDM = new LocationDecisionModel();
		for (int q = 0; q< 24;q++){
			resultMDM.hourModel.add(mergePatternTable(a.hourModel.get(q),b.hourModel.get(q)));
			resultMDM.hourModelShift.add(mergePatternTable(a.hourModelShift.get(q),b.hourModelShift.get(q)));
		}resultMDM.hourModelShift.add(mergePatternTable(a.hourModelShift.get(24),b.hourModelShift.get(24)));
		for (int q = 0; q< 5;q++){
			resultMDM.oneDayModel.add(mergePatternTable(a.oneDayModel.get(q),b.oneDayModel.get(q)));
		}
		return resultMDM;
	}
	public static PatternTable mergePatternTable(PatternTable a, PatternTable b) {
		PatternTable result = new PatternTable(b);
		for (String patternA : a.patternMap.keySet()) {
			if (result.patternMap.containsKey(patternA)) {
				result.patternMap.get(patternA).frequency = a.patternMap
						.get(patternA).frequency
						+ result.patternMap.get(patternA).frequency;
			} else {
				result.add(a.patternMap.get(patternA));
			}
		}
		return result;
	}
}
