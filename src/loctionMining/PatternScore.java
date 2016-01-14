package loctionMining;

import java.util.ArrayList;
import java.util.HashSet;

public class PatternScore implements Comparable<PatternScore> {
	Double case1 = new Double(0.0);
	Double case2 = new Double(0.0);
	Double case3 = new Double(0.0);
	Double c1t = new Double(0.0);
	Double c2o = new Double(0.0);
	HashSet<String> ths = new HashSet<String>();
	HashSet<String> ohs = new HashSet<String>();
	Double sumOfPatternT = new Double(0.0);
	Double sumOfPatternO = new Double(0.0);
	Double sumcase1ferq = new Double(0.0);
	
	PatternTable tempt = new PatternTable();
	PatternTable tempo = new PatternTable();
	int osize = 0;
//	ArrayList<Double> case1ferqO = new ArrayList<Double>();
	int wayToCompare = 0;
	public PatternScore() {
		
	}
	@SuppressWarnings("unchecked")
	public PatternScore(PatternTable training, PatternTable o, HashSet<String> ths, HashSet<String> ohs) {
		tempt = training;
		tempo = o;
		this.osize = o.patternArrayList.size();
		this.ths =  (HashSet<String>) ths.clone();
		this.ohs =  (HashSet<String>) ohs.clone();
		for (Pattern x : training.patternMap.values()) {
			this.sumOfPatternT += x.frequency;
		}
		for (Pattern x : o.patternMap.values()) {
			this.sumOfPatternO += x.frequency;
		}
		for (String patternTraining : training.patternMap.keySet()) {
			if (o.patternMap.containsKey(patternTraining)) {
				this.case1+= (Math.abs(training.patternMap.get(patternTraining).frequency
								- o.patternMap.get(patternTraining).frequency));
				if (training.patternMap.get(patternTraining).frequency< o.patternMap.get(patternTraining).frequency){
					sumcase1ferq += (training.patternMap.get(patternTraining).frequency);
					c1t +=  (training.patternMap.get(patternTraining).frequency);
				}else{
					sumcase1ferq += (o.patternMap.get(patternTraining).frequency);
					c2o += (o.patternMap.get(patternTraining).frequency);
    			}
			} else {
				this.case2 += training.patternMap.get(patternTraining).frequency;
			}
		}
		for (String patternB : o.patternMap.keySet()) {
			if (!training.patternMap.containsKey(patternB)) {
				this.case3 += o.patternMap.get(patternB).frequency;
			}
		}
	}
		
	public Double toDouble() {
		if (this.sumOfPatternT==0 || this.sumOfPatternO==0){
//			System.out.println(this.sumOfPatternT+" , "+this.sumOfPatternO+" , "+case1+" , "+case2+" , "+case3+" , "+ohs.size()+" , "+ths.size());
			return 2.0;//(score/sumOfPattern)/ (hs.size() /ths.size());
		}else{
//			if ( (this.sumOfPatternT+this.sumOfPatternO) == 0){
//			System.out.println(this.sumOfPatternT+" , "+this.sumOfPatternO+" , "+case1+" , "+case2+" , "+case3+" , "+ohs.size()+" , "+ths.size()+" , "+ this.tempt.patternArrayList.size()+" , "+this.tempo.patternArrayList.size());
//			}
			return (this.case1+this.case2+this.case3) / (this.sumOfPatternT + this.sumOfPatternO);
		}
	}
	
//	public Double toDouble2() {
//		if (this.tempt.patternArrayList.size()==0 || this.tempo.patternArrayList.size()==0){
//			return 2.0;//(score/sumOfPattern)/ (hs.size() /ths.size());
//		}else{
//			if ((this.sumOfPatternT + this.sumOfPatternO - case2 - case3) == 0){
//				return 1.0;
//			}else{
////				if (this.sumOfPatternT==0 || this.sumOfPatternO ==0 ||(this.sumOfPatternT + this.sumOfPatternO - case2 - case3) == 0){
////					System.out.println(this.sumOfPatternT+" , "+this.sumOfPatternO+" , "+case1+" , "+case2+" , "+case3+" , "+ohs.size()+" , "+ths.size());
////				}
//				return (3*(case1 / (this.sumOfPatternT + this.sumOfPatternO - case2 - case3))
//					+ case2/this.sumOfPatternT + case3/this.sumOfPatternO )/ 5;
//			}
//		}
//	}
	public Double toDouble2() {
		if (this.sumOfPatternT==0 || this.sumOfPatternO==0){
//			System.out.println(this.sumOfPatternT+" , "+this.sumOfPatternO+" , "+case1+" , "+case2+" , "+case3+" , "+ohs.size()+" , "+ths.size());
			return 2.0;//(score/sumOfPattern)/ (hs.size() /ths.size());
		}else{
//			if ( (this.sumOfPatternT+this.sumOfPatternO) == 0){
//			System.out.println(this.sumOfPatternT+" , "+this.sumOfPatternO+" , "+case1+" , "+case2+" , "+case3+" , "+ohs.size()+" , "+ths.size()+" , "+ this.tempt.patternArrayList.size()+" , "+this.tempo.patternArrayList.size());
//			}
			return (1- (((this.sumcase1ferq/this.sumOfPatternT)+(this.sumcase1ferq/this.sumOfPatternO))/2.0));
		}
	}
	
	public Double toDouble3() {
		if (this.sumOfPatternT==0 || this.sumOfPatternO==0){
			return 2.0;//(score/sumOfPattern)/ (hs.size() /ths.size());
		}else{
			if ((this.sumOfPatternT + this.sumOfPatternO - case2 - case3) == 0){
				return 1.0;
			}else{
//				if (this.sumOfPatternT==0 || this.sumOfPatternO ==0 ||(this.sumOfPatternT + this.sumOfPatternO - case2 - case3) == 0){
//					System.out.println(this.sumOfPatternT+" , "+this.sumOfPatternO+" , "+case1+" , "+case2+" , "+case3+" , "+ohs.size()+" , "+ths.size());
//				}
				return ((sumcase1ferq / (this.sumOfPatternT + this.sumOfPatternO - case2 - case3))
					+ case2/this.sumOfPatternT + case3/this.sumOfPatternO )/ 3;
			}
		}
	}
	public Double toDouble4() {
		if (this.sumOfPatternT==0 || this.sumOfPatternO==0){
//			System.out.println(this.sumOfPatternT+" , "+this.sumOfPatternO+" , "+case1+" , "+case2+" , "+case3+" , "+ohs.size()+" , "+ths.size());
			return 2.0;//(score/sumOfPattern)/ (hs.size() /ths.size());
		}else{
			if ((this.sumOfPatternT + this.sumOfPatternO - case2 - case3) == 0){
				return 1.0;
			}else{
				return (sumcase1ferq / (this.sumOfPatternT + this.sumOfPatternO - case2 - case3));
			}
		}
	}
	
	@Override
	public int compareTo(PatternScore o) {
		//return (int) (this.results.get(3) * 10000 - o.results.get(3) * 10000);
		Double result = new Double(0.0);
		 switch (wayToCompare) {
         case 1:
        	 result = (o.sumcase1ferq - this.sumcase1ferq)*10000;
        	 break;
         case 2:
        	 result = (this.case2 - o.case2)*10000;
        	 break;
         case 3:
        	 result = (this.case3 - o.case3)*10000;
        	 break;
         default: 
        	 result = (((this.case1+this.case2+this.case3)/ (this.sumOfPatternT+this.sumOfPatternO)) 
        			 - ((o.case1+o.case2+o.case3)/(o.sumOfPatternT+o.sumOfPatternO)))*10000;
		 }
		return result.intValue();
		}
	
}
