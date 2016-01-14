package loctionMining;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;

public class LocationHMM {
	private static ArrayList<Date> timeTable;
	
	public static void locatinoHMM(
			ArrayList<WeekDayRawData> allUsersWeekRawData, int userIndex, int day) {
		
//        String fileName =
//                "output_"
//                        + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
//                                .format(new Date()) + ".txt";
//        File f = new File(fileName);
//        if (!f.exists()) {
//            try {
//				f.createNewFile();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        }
//        final PrintStream ps = System.out; // Backup System.out
//        // Set file as System.out
//        try {
//			System.setOut(new PrintStream(new FileOutputStream(f, true)));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
///////////////////////////////////////////////////////////////  Data separating
       
		UserLocationRawData userData = new UserLocationRawData();
		UserLocationRawData otherValData = new UserLocationRawData();
		UserLocationRawData otherTestData = new UserLocationRawData();
		UserLocationRawData tempTestUser = new UserLocationRawData();
		UserLocationRawData tempTraining = new UserLocationRawData();
		for (int i = 0; i < allUsersWeekRawData.get(userIndex).week.get(day).size(); i++) {
			userData.add(allUsersWeekRawData.get(userIndex).week.get(day)
					.get(i));
		}
		
		Random rn = new Random();
		Integer rIndex = new Integer(rn.nextInt(14));
		for (int j = 0; j < allUsersWeekRawData.size(); j++) {
			if(j != userIndex){
				otherValData.add(allUsersWeekRawData.get(j).week.get(day).get(rIndex));
				otherTestData.add(allUsersWeekRawData.get(j).week.get(day).get(rIndex+1));
			}
		}
		
		LinkedHashSet index;
		int totleData = userData.size();
		index = new LinkedHashSet();
		while (index.size()<(int)(2*Math.log(totleData))){
			index.add(rn.nextInt(totleData));
		}
		
		for (int j = 0; j <userData.size(); j++){
			if (index.contains(j)){
				tempTestUser.add(userData.get(j));
			}else{
				tempTraining.add(userData.get(j));
			}
		}
	//  day         24hours      records	
		ArrayList<ArrayList<ArrayList<String>>> trainingData = new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<ArrayList<ArrayList<String>>> testUser = new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<ArrayList<ArrayList<String>>> testOther = new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<ArrayList<ArrayList<String>>> valData = new ArrayList<ArrayList<ArrayList<String>>>();
		
		trainingData = separteData(tempTraining);
		testUser = separteData(tempTestUser);
		testOther = separteData(otherTestData);
		valData = separteData(otherValData);
		
/////////////////////////////////////////////////////////////////////// transform data into HMM type	
//         hour      day        records
		ArrayList<ArrayList<ArrayList<ObservationInteger>>> hMMtrainingData = new ArrayList<ArrayList<ArrayList<ObservationInteger>>>();
		ArrayList<ArrayList<ArrayList<ObservationInteger>>> hMMtestUser = new ArrayList<ArrayList<ArrayList<ObservationInteger>>>();
		ArrayList<ArrayList<ArrayList<ObservationInteger>>> hMMtestOther = new ArrayList<ArrayList<ArrayList<ObservationInteger>>>();
		ArrayList<ArrayList<ArrayList<ObservationInteger>>> hMMvalData = new ArrayList<ArrayList<ArrayList<ObservationInteger>>>();
		
		ArrayList<Double> threshold = new ArrayList<Double>();
		
		double tp = 0;
		double fn = 0;
		double fp = 0;
		double tn = 0;
		
	//	ArrayList<Hmm<ObservationInteger>> allHMMModels = new ArrayList<Hmm<ObservationInteger>>();
		ArrayList<Double> lastUserScoreDiffList = new ArrayList<Double>();
		ArrayList<Double> lastOtherScoreDiffList = new ArrayList<Double>();
		
		
		//Find all locations for 24 hour
		for(int t =0; t<24;t++){
		    Map<String,Integer> entity2Id = new HashMap<String,Integer>();
		    List<Locations> entityLocation = new ArrayList<Locations>();
		    
			for(int d =0; d<trainingData.size();d++){ // find all locations
				for(String entity: trainingData.get(d).get(t)){
					int entityId;
					if(entity2Id.containsKey(entity)){
						entityId = entity2Id.get(entity);
						entityLocation.set(entityId, new Locations(entity, entityLocation.get(entityId).counter +1));
			        } else {
			            entityId = entity2Id.size();
			            entity2Id.put(entity, entityId);
			            entityLocation.add(new Locations(entity));
			        }
				}
			}
			Collections.sort(entityLocation);
			entity2Id = new HashMap<String,Integer>();
			if (entityLocation.size()<50){//10
				for(int i = 0; i<entityLocation.size() ;i++){
					entity2Id.put(entityLocation.get(i).location, i);
				}
			}else{
				for(int i = 0; i<50 ;i++){//10
					entity2Id.put(entityLocation.get(i).location, i);
				}
			}

			
			//transform them into HMM type
			ArrayList<ArrayList<ObservationInteger>> tempHourData = new ArrayList<ArrayList<ObservationInteger>>();
			for(int d =0; d<trainingData.size();d++){
				ArrayList<ObservationInteger> seq = new ArrayList<ObservationInteger>();
				for(String entity: trainingData.get(d).get(t)){
					int entityId;
					if(entity2Id.containsKey(entity)){
						entityId = entity2Id.get(entity);
					}else{
						entityId = entity2Id.size();
					}
					seq.add(new ObservationInteger(entityId));
				}
				if (seq.size()>3){
					tempHourData.add(seq);// contain multiple days
				}
			}
			hMMtrainingData.add(tempHourData);
			
			tempHourData = new ArrayList<ArrayList<ObservationInteger>>();
			for(int d =0; d<testUser.size();d++){
				ArrayList<ObservationInteger> seq = new ArrayList<ObservationInteger>();
				for(String entity: testUser.get(d).get(t)){
					int entityId;
					if(entity2Id.containsKey(entity)){
						entityId = entity2Id.get(entity);
					}else{
						entityId = entity2Id.size();
					}
					seq.add(new ObservationInteger(entityId));
				}
				if (seq.size()>3){
					tempHourData.add(seq);// contain multiple days
				}
			}
			hMMtestUser.add(tempHourData);
			
			tempHourData = new ArrayList<ArrayList<ObservationInteger>>();
			for(int d =0; d<testOther.size();d++){
				ArrayList<ObservationInteger> seq = new ArrayList<ObservationInteger>();
				for(String entity: testOther.get(d).get(t)){
					int entityId;
					if(entity2Id.containsKey(entity)){
						entityId = entity2Id.get(entity);
					}else{
						entityId = entity2Id.size();
					}
					seq.add(new ObservationInteger(entityId));
				}
				if (seq.size()>3){
					tempHourData.add(seq);// contain multiple days
				}
			}
			hMMtestOther.add(tempHourData);
			
			tempHourData = new ArrayList<ArrayList<ObservationInteger>>();
			for(int d =0; d<valData.size();d++){
				ArrayList<ObservationInteger> seq = new ArrayList<ObservationInteger>();
				for(String entity: valData.get(d).get(t)){
					int entityId;
					if(entity2Id.containsKey(entity)){
						entityId = entity2Id.get(entity);
					}else{
						entityId = entity2Id.size();
					}
					seq.add(new ObservationInteger(entityId));
				}
				if (seq.size()>3){
					tempHourData.add(seq);// contain multiple days
				}
			}
			hMMvalData.add(tempHourData);
			
			
			ArrayList<Double> kcScoreList = new ArrayList<Double>();
			ArrayList<ArrayList<ObservationInteger>> tempTrain;
			double lowestKCScore=1.0;
			Double temp;
			for(int i = 0;i<hMMtrainingData.get(t).size();i++){
				tempTrain = new ArrayList<ArrayList<ObservationInteger>>();
				for(int j=0;j<hMMtrainingData.get(t).size();j++){
					if (i!=j){
						if (hMMtrainingData.get(t).get(j).size() < 3){
						//	System.out.println("Size 0 in KC");
						}else{
							tempTrain.add(hMMtrainingData.get(t).get(j));
						}
					}
				}
				if (tempTrain.size() ==0){
				//	System.out.println("Size 0 in KC learning");
				}else{
					Hmm<ObservationInteger> tempModel = HMM.buildHmm(tempTrain,51,200);//11
				
					if (hMMtrainingData.get(t).get(i).size() != 0){
						temp = tempModel.probability((hMMtrainingData.get(t).get(i)));
					}else{
						temp = 0.0;
					}
					if ( temp<lowestKCScore&& temp !=0){
						lowestKCScore = temp;
					}
					if (temp !=0){
						kcScoreList.add(temp);
					}
				}
			}
			threshold.add(lowestKCScore);
			
			if (hMMtrainingData.get(t).size() == 0){
		//		System.out.println("no sequence for training");
			}else{
				Hmm<ObservationInteger> hmmModel = HMM.buildHmm(hMMtrainingData.get(t),51,200);//11
				
				//allHMMModels.add(hmmModel);
				
				ArrayList<Double> userScoreList = new ArrayList<Double>();
				ArrayList<Double> otherScoreList = new ArrayList<Double>();
				ArrayList<Double> valScoreList = new ArrayList<Double>();
				for (ArrayList<ObservationInteger> testU: hMMtestUser.get(t)){
					userScoreList.add(hmmModel.probability(testU));
				}
				for (ArrayList<ObservationInteger> testO: hMMtestOther.get(t)){
					otherScoreList.add(hmmModel.probability(testO));
				}
//			for (ArrayList<ObservationInteger> val: hMMvalData.get(t)){
//				valScoreList.add(hmmModel.probability(val));
//			}
//			for(double val: valScoreList){
//				
//			}
				
//				ArrayList<Rank> tempTest4 = new ArrayList<Rank>();
//				ArrayList<Roc> roc1 = new ArrayList<Roc>();
//				for (int i = 0; i<10;i++){
//					roc1.add(new Roc(0.0, 0.0));
//				}
//				ArrayList<Roc> tempRoc1 = new ArrayList<Roc>();
//					for(Double user: userScoreList){
//							tempTest4.add(new Rank(1 , user));
//					}
////					for(Double user: kcScoreList){
////						tempTest4.add(new Rank(1 , user));
////					}
//					for(Double other: otherScoreList){
//						tempTest4.add(new Rank(0 , other));
//					}
//				tempRoc1.addAll(Main.GiveTPRandFPRforROC(tempTest4, (tempTest4.size()- otherScoreList.size()), otherScoreList.size()));
//				
//				for(Roc te:tempRoc1){
//					System.out.print("	,	"+te.scores/te.counter);
//				}System.out.println("");
				
				lastUserScoreDiffList = new ArrayList<Double>();
				lastOtherScoreDiffList = new ArrayList<Double>();
				System.out.println("time equals to:	"+t+ "user size: "+ userScoreList.size()+"other size : "+otherScoreList.size());
				for(int u = 0; u< userScoreList.size();u++){
					if (u >= threshold.get(t) && userScoreList.get(u) != 0){
						tn++;
					}else{
						if (t > 0){
							if (Math.abs(lastUserScoreDiffList.get(u) - (threshold.get(t)-userScoreList.get(u))) < 0.1){
								tn++;
							}else{
								fp++;
							}
						}else{
							fp++;
						}
					}
					
					System.out.println("user:	"+(threshold.get(t) - userScoreList.get(u)));
					
					lastUserScoreDiffList.add(threshold.get(t)-userScoreList.get(u));
				}
				for(int o=0 ; o< otherScoreList.size();o++){
					if (o <= threshold.get(t)){
						tp++;
					}else{
						if (t > 0){
							if (Math.abs(lastOtherScoreDiffList.get(o) - (threshold.get(t) - otherScoreList.get(o))) < 0.1){
								tp++;
							}else{
								fn++;
							}
						}else{
							fp++;
						}
					}
					System.out.println("other:	"+(threshold.get(t)	-	otherScoreList.get(o)));
					lastOtherScoreDiffList.add(threshold.get(t) -  otherScoreList.get(o));
				}
			}
		}
//////////////////////////////////////////////////////////////////////////////
		System.out.println("tpr:	"+ tp/(tp+fn)+"	fpr:	"+fp/(fp+tn));
		
////////////////////////////////////////////////////////////////////////////learn data


//        System.out.println("Print in file.");
//        System.out.close(); // Close file
//        System.setOut(ps); // Recover System.out
//        System.out.println("Print in console");
	}
	
	
	public static ArrayList<ArrayList<ArrayList<String>>> separteData(UserLocationRawData allData){
		ArrayList<ArrayList<ArrayList<String>>> returnAllData = new ArrayList<ArrayList<ArrayList<String>>>();
		for(DayOrderedRawData oneDayData: allData){
			timeTable =new ArrayList<Date>();
			setTimeTable(oneDayData);
			returnAllData.add(separteOneDay(oneDayData));
		}
		return returnAllData;
	}
	
	public static ArrayList<ArrayList<String>> separteOneDay(DayOrderedRawData rawdata){
		ArrayList<ArrayList<String>> hourlyRawData = new ArrayList<ArrayList<String>>();//24 hours
		Calendar calSPD = Calendar.getInstance();
		int timeTableIndex = 0;
		for (int i=0;i<24;i++){
			hourlyRawData.add(new ArrayList<String>());
		}
		for (int o=0;o<rawdata.size();){
			calSPD.setTime(rawdata.get(o).date);
		//	System.out.println(hourlyIndex+","+shiftHourlyIndex+","+timeTableIndex+","+rawdata.get(o).date+","+timeTable.get(timeTableIndex));
			if (rawdata.get(o).date.before(timeTable.get(timeTableIndex))){
				hourlyRawData.get(timeTableIndex).add(rawdata.get(o).loca);
				o++;
			}else{
				if (hourlyRawData.get(timeTableIndex).size()>100){
			//		System.out.println(timeTableIndex+" - t befor 100 - "+hourlyRawData.get(timeTableIndex).size());
					hourlyRawData.set(timeTableIndex, boundaryProcess(hourlyRawData.get(timeTableIndex)));
			//		System.out.println(timeTableIndex+" - t after 100 - "+hourlyRawData.get(timeTableIndex).size());
				}
				timeTableIndex++;
			}
		}
		return hourlyRawData;
	}
	
	public static ArrayList<String> boundaryProcess(ArrayList<String> loca){
		ArrayList<String> returnLoca = new ArrayList<String>();
		int sizeOfLoca = 0;
		if (loca.size()%60>30){
			sizeOfLoca = (loca.size()/60)+1;
		}else{
			sizeOfLoca = (loca.size()/60);
		}
		for (int i = 0;i < loca.size();i++){
			if (i%sizeOfLoca == 0){
				returnLoca.add(loca.get(i));
			}
		}	
		return returnLoca;
	}
	
	public static void setTimeTable(DayOrderedRawData rawdata){
//		for(LocationRawData eachRecord: rawdata){
//			timespar.add(eachRecord.date);
//		}
		Calendar calSTT = Calendar.getInstance();
		calSTT.setTime(rawdata.get(0).date);
		calSTT.set(Calendar.HOUR_OF_DAY, 0);
		calSTT.set(Calendar.MINUTE, 0);
		calSTT.set(Calendar.SECOND, 0);
		for (int i = 0; i< 24; i++){
			calSTT.add(Calendar.MINUTE, 60);
			timeTable.add(calSTT.getTime());
		}
	}
}
