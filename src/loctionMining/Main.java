package loctionMining;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

//	public static int gapSize = 5;
//	public static int patternSize = 3;
//	public static int patternThreshold = 5;
//	public static ArrayList<Double> decisionThreshold = new ArrayList<Double>();
	public static int gapSize = 1;
	public static int patternSize = 3;
	public static int patternThreshold = 1;
	public static ArrayList<Double> decisionThreshold = new ArrayList<Double>();

	public static int printOrNot = 0;

	public static void main(String[] arges) throws IOException {
        String fileName =
                "F:\\output-"
                        + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
                                .format(new Date()) + ".txt";
        File f = new File(fileName);
        if (!f.exists()) {
            try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        final PrintStream ps = System.out; // Backup System.out
        // Set file as System.out
        try {
			System.setOut(new PrintStream(new FileOutputStream(f, true)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// /////////////////////////////////Reading
		ArrayList<UserLocationRawData> allUsersRawData = new ArrayList<UserLocationRawData>();
		ArrayList<WeekDayRawData> allUsersWeekRawData = new ArrayList<WeekDayRawData>();
		for (int i = 4; i <= 106; i++) {
			String filename = "F:\\locs\\locs_" + i + ".txt";
//			String filename = "F:\\app\\app_" + i + ".txt";
			//String filename = "F:\\activity\\activity_" + i + ".txt";
			UserLocationRawData user = DataReader.read(filename);
			if (!user.isEmpty() && user.size() > 120) {
				System.out.println(filename + " " + user.size());
				allUsersRawData.add(user);
				WeekDayRawData tempWeekData = new WeekDayRawData(user);
				allUsersWeekRawData.add(tempWeekData);
			}
		}
//		for (int userIndex = 0; userIndex < allUsersWeekRawData.size(); userIndex++) {
//			testActivityData(allUsersWeekRawData, userIndex, Calendar.MONDAY);
//		}

		// System.out.println(allUsersRawData.size());
		// System.out.println(allUsersWeekRawData.size());
		// Calendar cal = Calendar.getInstance();
		 System.out.println("MONDAY");
		 for (int userIndex = 0; userIndex < allUsersWeekRawData.size();
		 userIndex++) {
		 testLocation(allUsersWeekRawData, userIndex, Calendar.MONDAY);
		 }
//		 System.out.println("TUESDAY");
//		 for (int userIndex = 0; userIndex < allUsersWeekRawData.size();
//		 userIndex++) {
//		 testLocation(allUsersWeekRawData, userIndex, Calendar.TUESDAY);
//		 }		 
//		 System.out.println("WEDNESDAY");
//		 for (int userIndex = 0; userIndex < allUsersWeekRawData.size();
//		 userIndex++) {
//		 testLocation(allUsersWeekRawData, userIndex, Calendar.WEDNESDAY);
//		 }
//		 System.out.println("THURSDAY");
//		 for (int userIndex = 0; userIndex < allUsersWeekRawData.size();
//		 userIndex++) {
//		 testLocation(allUsersWeekRawData, userIndex, Calendar.THURSDAY);
//		 }
//		 System.out.println("FRIDAY");
//		 for (int userIndex = 0; userIndex < allUsersWeekRawData.size();
//		 userIndex++) {
//		 testLocation(allUsersWeekRawData, userIndex, Calendar.FRIDAY);
//		 }
//		 System.out.println("SATURDAY");
//		 for (int userIndex = 0; userIndex < allUsersWeekRawData.size();
//		 userIndex++) {
//		 testLocation(allUsersWeekRawData, userIndex, Calendar.SATURDAY);
//		 }
//		 System.out.println("SUNDAY");
//		 for (int userIndex = 0; userIndex < allUsersWeekRawData.size();
//		 userIndex++) {
//		 testLocation(allUsersWeekRawData, userIndex, Calendar.SUNDAY);
//		 }
		 
		System.out.println("--the end--");
        System.out.close(); // Close file
        System.setOut(ps); // Recover System.out
System.out.println("--the end--");
	}

	public static void testActivityData(
			ArrayList<WeekDayRawData> allUsersWeekRawData, int userIndex,
			int day) {
		// ////////////////////////////////split train, v, test data.
		UserLocationRawData activityTraining = new UserLocationRawData();
		UserLocationRawData activityValidation = new UserLocationRawData();
		UserLocationRawData activityTest = new UserLocationRawData();
		for (int i = 0; i < (allUsersWeekRawData.get(userIndex).week.get(day)
				.size() - 3); i++) {
			activityTraining.add(allUsersWeekRawData.get(userIndex).week.get(
					day).get(i));
		}
		for (int j = 0; j < allUsersWeekRawData.size(); j++) {
			activityValidation.add(allUsersWeekRawData.get(j).week.get(day)
					.get(allUsersWeekRawData.get(j).week.get(day).size() - 2));
			activityTest.add(allUsersWeekRawData.get(j).week.get(day).get(
					allUsersWeekRawData.get(j).week.get(day).size() - 1));
		}
		DalyActivityChart activityTrainModel = new DalyActivityChart();
		// for (LocationRawData ooo:activityTraining.get(0)){
		// System.out.println("1111111111111111111111");
		// }
		for (DayOrderedRawData oneDay : activityTraining) {
			activityTrainModel = mergeChart(CountChart(oneDay),
					activityTrainModel);
		}

		ArrayList<DalyActivityChart> activityValModels = new ArrayList<DalyActivityChart>();
		for (DayOrderedRawData tempVal : activityValidation) {
			activityValModels.add(CountChart(tempVal));
		}

		ArrayList<DalyActivityChart> activityTestModels = new ArrayList<DalyActivityChart>();
		for (DayOrderedRawData tempTest : activityTest) {
			activityTestModels.add(CountChart(tempTest));
		}

		// System.out.println("1111111111111111111111");
		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// for (int i = 0; i < 1440; i++) {
		// if (trainChart.frequency.get(i) != 0) {
		// System.out.println(df.format(trainChart.time.get(i)) + ","
		// + trainChart.frequency.get(i));
		// }
		// }
		// ///////////////////////////////////////Set decisionThreshold
		ArrayList<ArrayList<Double>> activityValscorelist = getActivityScoreList(
				activityValModels, activityTrainModel);
		decisionThreshold = new ArrayList<Double>();
		// Double closestHigherScore;
		Double closestLowerScore;
		for (int t = 0; t < 22; t++) {
			closestLowerScore = 99999999999999999.0;
			for (int i = 0; i < activityValscorelist.size(); i++) {
				if (closestLowerScore > activityValscorelist.get(i).get(t)
						&& activityValscorelist.get(userIndex).get(t) < activityValscorelist
								.get(i).get(t)) {
					closestLowerScore = activityValscorelist.get(i).get(t);
				}
			}
			if (closestLowerScore == 99999999999999999.0) {
				closestLowerScore = activityValscorelist.get(userIndex).get(t);
			}
			// System.out.println(valscorelist.get(userIndex).get(t));
			// System.out.println(activityValscorelist.get(userIndex).get(t)+",,,,,,,,,"+closestLowerScore);
			decisionThreshold
					.add((activityValscorelist.get(userIndex).get(t) + closestLowerScore) / 2);
			// System.out.println((activityValscorelist.get(userIndex).get(t) +
			// closestLowerScore) / 2 +" ,,,,,,,, "+decisionThreshold.get(t));
		}
		// System.out.println(decisionThreshold);

		// ////////////////////give all score.
		ArrayList<ArrayList<Double>> testscorelist = getActivityScoreList(
				activityTestModels, activityTrainModel);
		// /////////////////////////////////////////////////////////////////////
//		ArrayList<Double> valSort;
//		ArrayList<Double> testSort;
//		for (int t = 0; t < 24; t++) {
//			valSort = new ArrayList<Double>();
//			testSort = new ArrayList<Double>();
//			for (int i = 0; i < activityValscorelist.size(); i++) {
//				valSort.add(activityValscorelist.get(i).get(t));
//				testSort.add(activityValscorelist.get(i).get(t));
//			}
//			valSort.sort(null);
//			testSort.sort(null);
//			System.out.println("-------------------------------   " + t);
//			System.out.println("Threshold :	" + decisionThreshold.get(t));
//			System.out.println("Valation :"
//					+ activityValscorelist.get(userIndex).get(t));
//			System.out.println(valSort);
//			System.out.println("Test :" + testscorelist.get(userIndex).get(t));
//			System.out.println(testSort);
//			System.out.println("-------------------------------");
//			System.out.println("-------------------------------");
//		}
		// ////////////////////////////////////////////////////////////////////

		Double tp = 0.0;
		Double fp = 0.0;
		Double tn = 0.0;
		Double fn = 0.0;
		for (int t = 7; t < 19; t++) {
			for (int i = 0; i < testscorelist.size(); i++) {
				// System.out.print(testscorelist.get(i));
				if (i == userIndex) {
					// System.out.print("this is user it's self");
					if (testscorelist.get(i).get(t) <= decisionThreshold.get(t)) {
						tn++;
					} else {
						fp++;
					}
				} else {
					if (testscorelist.get(i).get(t) > decisionThreshold.get(t)) {
						tp++;
					} else {
						fn++;
					}
				}
				// System.out.println(" "+(testscorelist.get(i) >=
				// decisionThreshold?"T":"F"));
			}
		}
		// System.out.println("User " + userIndex + " accuracy is : " + accuracy
		// / testscorelist.size());
		// System.out.println("Accurcy:"+ (tn+tp) / testscorelist.size());
		System.out.println("TPR:	" + tp / (tp + fn) + "	FPR:	" + fp / (fp + tn)
				+ "	F1:	" + 2 * tp / (2 * tp + fp + fn) + " Tp: " + tp
				+ "	Fn:	" + fn + " Fp: " + fp + " Tn: " + tn);
		// System.out.println("FPR:"+ fp /(fp+tn));
		// System.out.println("F1:"+ 2*tp /(2*tp+fp+fn));
		// System.out.println(tp + "	" + fn);
		// System.out.println(fp + "	" + tn);
		// System.out
		// .println("//////////////////////////////////////////////////");
	}

	public static ArrayList<ArrayList<Double>> getActivityScoreList(
			ArrayList<DalyActivityChart> Models, DalyActivityChart trainModel) {
		ArrayList<ArrayList<Double>> scorelist = new ArrayList<ArrayList<Double>>();
		for (DalyActivityChart valModel : Models) {
			scorelist.add(giveActivityScore(trainModel, valModel));
		}
		return scorelist;
	}

	public static ArrayList<Double> giveActivityScore(DalyActivityChart train,
			DalyActivityChart b) {
		ArrayList<Double> returnGAS = new ArrayList<Double>();
		ArrayList<Double> returnGAS1 = new ArrayList<Double>();
		for (int i = 0; i < 24; i++) {
			returnGAS.add(giveChartScore(
					train.frequency.subList(60 * i, 60 * (i + 1)),
					b.frequency.subList(60 * i, 60 * (i + 1))));
		}
		for (int i = 2; i < 24; i++) {
			returnGAS1.add((3 * returnGAS.get(i)) + (2 * returnGAS.get(i - 1))
					+ returnGAS.get(i - 2));
		}
		return returnGAS1;
	}

//	 public static Double giveChartScore(List<Double> a, List<Double> b) {Double returnGCS = new Double(0.0);
//	 	ArrayList<Double> pA = new ArrayList<Double>();
//	 	ArrayList<Double> pB = new ArrayList<Double>();
//	 	Double sumA = new Double(0.0);
//	 	Double sumB = new Double(0.0);
//	 	for (int i = 0; i < 60; i++) {
//	 		pA.add(a.get(i));
//	 		sumA += pA.get(i);
//	 		pB.add(b.get(i));
//	 		sumB += pB.get(i);
//	 	}
//	 // System.out.println(sumA+","+sumB);
//	 	int tempSize =0;
//	 	for (int i = 0; i < 60; i++) {
//	 		pA.set(i, pA.get(i) / sumA);
//	 		pB.set(i, pB.get(i) / sumB);
//	 // returnGCS += Math
//	 // .abs((pA.get(i) * Math.log10(pA.get(i) / pB.get(i))));
//	 		//if((pA.get(i) + pB.get(i)) != 0.0){
//	 		returnGCS += (Math.abs(pA.get(i) - pB.get(i)));
//	 		//tempSize++;
//	 		//}
//	 	}
//	 // System.out.println(returnGCS);
//	 	if (sumA == 0 && sumB== 0){return 0.0;}
//	 	else if (sumA != 0&& sumB == 0){return 1.0;}
//	 	else{return returnGCS/2;}
//	 }
	 public static Double giveChartScore(List<Double> a, List<Double> b) {Double returnGCS = new Double(0.0);
	 	ArrayList<Double> pA = new ArrayList<Double>();
	 	ArrayList<Double> pB = new ArrayList<Double>();
	 	Double sumA = new Double(0.0);
	 	Double sumB = new Double(0.0);
	 	for (int i = 0; i < 60; i++) {
	 		pA.add((1.0 / 60.0) + a.get(i));
	 		sumA += pA.get(i);
	 		pB.add((1.0 / 60.0) + b.get(i));
	 		sumB += pB.get(i);
	 	}
	 // System.out.println(sumA+","+sumB);
	 	for (int i = 0; i < 60; i++) {
	 		pA.set(i, pA.get(i) / sumA);
	 		pB.set(i, pB.get(i) / sumB);
	 // returnGCS += Math
	 // .abs((pA.get(i) * Math.log10(pA.get(i) / pB.get(i))));
	 		returnGCS += Math.abs(pA.get(i) - pB.get(i));
	 	}
	 // System.out.println(returnGCS);
	 	return returnGCS;
	 }

//	public static Double giveChartScore(List<Double> a, List<Double> b) {
//		Double returnGCS = new Double(0.0);
//		ArrayList<Double> pA = new ArrayList<Double>();
//		ArrayList<Double> pB = new ArrayList<Double>();
//		Double sumA = new Double(0.0);
//		Double sumB = new Double(0.0);
//		for (int i = 0; i < 60; i++) {
//			pA.add((1.0 / 60.0) + a.get(i));
//			sumA += pA.get(i);
//			pB.add((1.0 / 60.0) + b.get(i));
//			sumB += pB.get(i);
//		}
//		// System.out.println(sumA+","+sumB);
//		for (int i = 0; i < 60; i++) {
//			pA.set(i, pA.get(i) / sumA);
//			pB.set(i, pB.get(i) / sumB);
//			returnGCS += Math
//					.abs((pA.get(i) * Math.log10(pA.get(i) / pB.get(i))));
//		}
//		// System.out.println(returnGCS);
//		return returnGCS;
//	}

	public static DalyActivityChart mergeChart(DalyActivityChart a,
			DalyActivityChart b) {
		// System.out.println("1111111");
		DalyActivityChart returnMC = new DalyActivityChart();
		for (int k = 0; k < 1440; k++) {
			returnMC.frequency.set(k, a.frequency.get(k) + b.frequency.get(k));
		}
		// SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// for (int l = 0;l<1440;l++){
		// if (returnMC.frequency.get(l)!=0){
		// System.out.println(df.format(returnMC.time.get(l))+","+returnMC.frequency.get(l));
		// }
		// }
		return returnMC;
	}

	public static DalyActivityChart CountChart(DayOrderedRawData oneDay) {
		// System.out.println("here");
		DalyActivityChart returnCC = new DalyActivityChart();
		Double TempDAC = 0.0;
		Calendar calCC = Calendar.getInstance();
		// calCC.setTime(new
		// SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").parse("2000_01_01_00_00_00"));
		calCC.setTime(oneDay.get(0).date);
		calCC.set(2000, 0, 1);
		for (int i = 0, j = 0; i < 1440;) {
			if (returnCC.time.get(i).before(calCC.getTime())) {
				returnCC.frequency.set(i, TempDAC);
				i++;
			} else {
				returnCC.frequency.set(i,
						TempDAC + Double.parseDouble(oneDay.get(j).loca));
				TempDAC = Double.parseDouble(oneDay.get(j).loca);
				if (j != (oneDay.size() - 1)) {
					j++;
				} else {
					TempDAC = 0.0;
					i++;
				}
				calCC.setTime(oneDay.get(j).date);
				calCC.set(2000, 0, 1);
			}
			// System.out.println(df.format(returnCC.time.get(i))+"--"+returnCC.frequency.get(i)+",,"+df.format(oneDay.get(j).date)+"--"+oneDay.get(j).loca);
		}
		// SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// for (int i = 0;i<1440;i++){
		// if (returnCC.frequency.get(i)!=0){
		// System.out.println(df.format(returnCC.time.get(i))+","+returnCC.frequency.get(i));
		// }
		// }
		return returnCC;
	}

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////// location system// /////////////////////////////////////////////////////////////////
	public static void testLocation(
			ArrayList<WeekDayRawData> allUsersWeekRawData, int userIndex,
			int day) throws IOException {
		printOrNot = 0;
		// ////////////////////////////////split train, v, test data.
//		UserLocationRawData training = new UserLocationRawData();
//		UserLocationRawData validation = new UserLocationRawData();
//		UserLocationRawData test = new UserLocationRawData();
//UserLocationRawData testuser = new UserLocationRawData();
//
//Random r = new Random();
//int totleData = allUsersWeekRawData.get(userIndex).week.get(day).size();
//LinkedHashSet index = new LinkedHashSet();
//while (index.size()<2){
//	index.add(r.nextInt(15));
//}
//
//while (index.size()<(int)(Math.log(totleData)+2.5)){
//	index.add(r.nextInt(totleData));
//}
//int indexI[] = new int[(int)(Math.log(totleData)+2.5)];
//int ix = 0;
//for(Iterator ite = index.iterator(); ite.hasNext();){
//	indexI[ix] = (int)ite.next();
////	System.out.println(indexI[ix]);
//	ix++;
//}
//
//for (int i = 0; i < allUsersWeekRawData.get(userIndex).week.get(day)
//		.size(); i++) {
//	if (!index.contains(i)){
//		training.add(allUsersWeekRawData.get(userIndex).week.get(day)
//			.get(i));
//	}
//}
//
//for (int j = 0; j < allUsersWeekRawData.size(); j++) {
//	validation.add(allUsersWeekRawData.get(j).week.get(day).get(indexI[0]));
//	test.add(allUsersWeekRawData.get(j).week.get(day).get(indexI[1]));
//}
//testuser.add(allUsersWeekRawData.get(userIndex).week.get(day).get(indexI[2]));
//testuser.add(allUsersWeekRawData.get(userIndex).week.get(day).get(indexI[3]));
//testuser.add(allUsersWeekRawData.get(userIndex).week.get(day).get(indexI[4]));

////////////////////////////////////////////////////////////////////////////////////
		UserLocationRawData userData = new UserLocationRawData();
		UserLocationRawData otherValData = new UserLocationRawData();
		UserLocationRawData otherTestData = new UserLocationRawData();
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
		double p1 = 0.0;
		double p2 = 0.0;
		double p3 = 0.0;
		double p4 = 0.0;
		double p5 = 0.0;
		double p6 = 0.0;
		double p7 = 0.0;
		double p8 = 0.0;
		double upr = 0.0;
		double unr = 0.0;
		double tcompare = 0.0;
//////////////////////////////////////test HMM //////////////////////////////////////////////////
//		List<List<String>> trainHMM = new ArrayList<List<String>>();
//		List<String> oneSequence;
//		System.out.println(userData.size());
//		for (int i = 0;i<userData.size();i++){
//			oneSequence = new ArrayList<String>();
//			for(int j = 0;j<Math.min(userData.get(i).size(),10);j++){
//				oneSequence.add(userData.get(i).get(j).loca);
//			//	System.out.println(userData.get(i).get(j).loca);
//			}
//			trainHMM.add(oneSequence);
//			
//		}TempJava.buildHmm(trainHMM, 2);
//		
//		System.out.println("end");
//	System.exit(0);	
		
///////////////////// transform raw data to BitSet,and mine all data ////////////////////////////
		MinePattern miner = new MinePattern();
		ArrayList<LocationDecisionModel> userModels = new ArrayList<LocationDecisionModel>();
		ArrayList<LocationDecisionModel> valModels = new ArrayList<LocationDecisionModel>();
		ArrayList<LocationDecisionModel> testModels = new ArrayList<LocationDecisionModel>();
		
		for (DayOrderedRawData tempUser : userData) {
			userModels.add(miner.minePattern(tempUser));
		}
		for (DayOrderedRawData tempVal : otherValData) {
			valModels.add(miner.minePattern(tempVal));
		}
		for (DayOrderedRawData tempTset : otherTestData) {
			testModels.add(miner.minePattern(tempTset));
		}
		
		int totleData = allUsersWeekRawData.get(userIndex).week.get(day).size();
		LinkedHashSet index;
		ArrayList<resultList> allResults = new ArrayList<resultList>();
		ArrayList<Double> bestResult = new ArrayList<Double>();
		for(int i = 0;i<12;i++){
			bestResult.add(1.0);
		}
		for (int i = 0; i<100; i++){
			ArrayList<Double> tempresult = new ArrayList<Double>();
			ArrayList<LocationDecisionModel> trainModels = new ArrayList<LocationDecisionModel>();
			ArrayList<LocationDecisionModel> testUserModels = new ArrayList<LocationDecisionModel>();
			index = new LinkedHashSet();
			while (index.size()<(int)(2*Math.log(totleData))){
				index.add(rn.nextInt(totleData));
			}
			
			for (int j = 0; j <userModels.size(); j++){
				if (index.contains(j)){
					testUserModels.add(userModels.get(j));
				}else{
					trainModels.add(userModels.get(j));
				}
			}
			tempresult = evaluateLocationModel(trainModels,valModels,testModels,testUserModels);
			resultList temprl = new resultList(tempresult);
			allResults.add(temprl);
			if (tempresult.get(1)==bestResult.get(1)){
				if(tempresult.get(0)>bestResult.get(0)){
					bestResult = tempresult;
				}
				if(tempresult.get(0)==bestResult.get(0)&& (int)(tempresult.get(6)+tempresult.get(7))>(int)(bestResult.get(6)+bestResult.get(7))){
					bestResult = tempresult;
				}
			}
			if (tempresult.get(1)<bestResult.get(1)){
				bestResult = tempresult;
			}
			p1+=tempresult.get(8);
			p2+=tempresult.get(9);
			p3+=tempresult.get(10);
			p4+=tempresult.get(11);
			p5+=tempresult.get(12);
			p6+=tempresult.get(13);
			tcompare+=tempresult.get(15);
//			p7+=tempresult.get(14);
//			p8+=tempresult.get(15);
		}
		Collections.sort(allResults);
		resultList avgResult = new resultList();
		Double avgTPR = 0.0;
		Double avgFPR = 0.0;
		Double goodData = 0.0;
		Double allData = 0.0;
		for(int i = 0; i<50; i++){
		//	System.out.println("tp: "+allResults.get(i).results.get(1));
//			System.out.println("fp: "+allResults.get(i).results.get(3));
				//avgResult.results.set(j, (avgResult.results.get(j)+allResults.get(i).results.get(j)));
			avgTPR += allResults.get(i).results.get(0);
			avgFPR += allResults.get(i).results.get(1);
//			System.out.println("good "+(allResults.get(i).results.get(10)+allResults.get(i).results.get(11))+"total: "+allResults.get(i).results.get(10)+allResults.get(i).results.get(11));
			goodData +=(int)(allResults.get(i).results.get(4)+allResults.get(i).results.get(5)+allResults.get(i).results.get(7));
			allData +=(int)(allResults.get(i).results.get(4)+allResults.get(i).results.get(5)+allResults.get(i).results.get(7)+allResults.get(i).results.get(14));
			upr += allResults.get(i).results.get(6)/(allResults.get(i).results.get(2)+allResults.get(i).results.get(3)+allResults.get(i).results.get(6));
			unr += allResults.get(i).results.get(7)/(allResults.get(i).results.get(4)+allResults.get(i).results.get(5)+allResults.get(i).results.get(7));
		}
		avgTPR = avgTPR/50;
		avgFPR = avgFPR/50;
		upr = upr/50;
		unr = unr/50;
//		System.out.println("TPR:	" + bestResult.get(0) + "	,	" + bestResult.get(1)
//				+ "	FPR:	" + bestResult.get(2)+ "	,	" + bestResult.get(3)
//				+ "	Tp: " +bestResult.get(4)+ " Fn: " + bestResult.get(5) + " Fp: " + bestResult.get(6)+ " Tn: " + bestResult.get(7)
//				+ " Tp1: " +bestResult.get(8) + " Fn1: " +bestResult.get(9)+ " Fp1: " +bestResult.get(10) + " Tn1: "+ bestResult.get(11));
		System.out.println("avgTPR:	" + avgTPR + "	bestTPR	" + bestResult.get(0)+"	avgFPR:	"+ avgFPR
				+"	bestFPR:	"+ bestResult.get(1)+"	goodRate:	"+goodData/allData+"	,	"
				+(bestResult.get(4)+bestResult.get(5)+bestResult.get(7))/(bestResult.get(4)+bestResult.get(5)+bestResult.get(7)+bestResult.get(14))+"	OtherRate:	"+ upr+"	UserRate:	"+ unr
				+"	case1:	"+p1/100+"	case2:	"+p2/100+"	case3:	"+p3/100 +"	case1-1:	"+p4/100+"	score1:	"+p5/100+"	score2:	"+p6/100+"	To'/To:	"+ tcompare/100);//+"	score3:	"+p7/100+"	score4:	"+p7/100);
	}

//		ArrayList<LocationDecisionModel> valModels = new ArrayList<LocationDecisionModel>();
//		for (DayOrderedRawData tempVal : validation) {
//			valModels.add(miner.minePattern(tempVal));
//		}
//
//		ArrayList<LocationDecisionModel> testModels = new ArrayList<LocationDecisionModel>();
//		for (DayOrderedRawData tempTest : test) {
//			testModels.add(miner.minePattern(tempTest));
//		}
//		
//ArrayList<LocationDecisionModel> testuserModels = new ArrayList<LocationDecisionModel>();
//	for (DayOrderedRawData tempTestuser : testuser) {
//		testuserModels.add(miner.minePattern(tempTestuser));
//	}
//	
//		ArrayList<LocationDecisionModel> trainModels = new ArrayList<LocationDecisionModel>();
//		LocationDecisionModel trainModel = new LocationDecisionModel();
//		for (DayOrderedRawData eachDay : training) {
//			trainModels.add(miner.minePattern(eachDay));
//			trainModel = MinePattern.mergeDecisionModel(trainModel,
//					trainModels.get(trainModels.size() - 1));
//		}
//
//		trainModels.add(valModels.get(userIndex));
//		trainModel = MinePattern.mergeDecisionModel(trainModel,
//				valModels.get(userIndex));
//		// System.out.println(valModels.size());
//		valModels.remove(userIndex);
//		// System.out.println(valModels.size());
//		// System.out.println("*********************************");
		public static ArrayList<Double> evaluateLocationModel(ArrayList<LocationDecisionModel> trainModels,
				ArrayList<LocationDecisionModel> valModels, ArrayList<LocationDecisionModel> testModels,  ArrayList<LocationDecisionModel> testUserModels) throws IOException {
/////////////////////////////////////evenly set pattern's frequency//////////////////////
		LocationDecisionModel trainModel = new LocationDecisionModel();
		for(LocationDecisionModel tempModel:trainModels){
			trainModel = MinePattern.mergeDecisionModel(trainModel , tempModel);
		}
		
		LocationDecisionModel evenlyTrainModel = new LocationDecisionModel();
		evenlyTrainModel = evenlySetSeq(trainModel, trainModels.size() + 0.0);
        
///////////////////////////////////////// K cross//////////////////////////////////////
		// System.out.println("........................." + trainModels.size());
		ArrayList<ArrayList<PatternScore>> kcScoreList = new ArrayList<ArrayList<PatternScore>>();
		ArrayList<Double> kcScore = new ArrayList<Double>();
		for (int i = 0; i < trainModels.size(); i++) {
			LocationDecisionModel tempKC = new LocationDecisionModel();
			for (int j = 0; j < trainModels.size(); j++) {
				if (i != j) {
					tempKC = MinePattern.mergeDecisionModel(tempKC,
							trainModels.get(j));
				}
			}
			ArrayList<PatternScore> tempList = new ArrayList<PatternScore>();
			tempList = giveScore(
					(evenlySetSeq(tempKC, (trainModels.size() - 1.0))),
					trainModels.get(i));
			for (int t = 0; t < tempList.size(); t++) {
				// System.out.println(t + "t " + " , " + tempList.get(t));
			}
			// System.out.println("///////////////////////////////");
			// kcScoreList.add(giveScore((evenlySetSeq(tempKC,(trainModels.size()
			// -1.0))), trainModels.get(i)));
			kcScoreList.add(tempList);
			// kcScoreList.add(giveScore(tempKC, trainModels.get(i)));
		}
		ArrayList<Double> kcScoreMax = new ArrayList<Double>();
		ArrayList<Double> kcScoreMin = new ArrayList<Double>();
		for (int i = 0; i < 24; i++) {
			Double sum = new Double(0.0);
			Double max = new Double(0.0);
			Double min = new Double(1.0);
			for (int j = 0; j < kcScoreList.size(); j++) {
				sum += kcScoreList.get(j).get(i).toDouble2();
				// System.out.println(i + " , " + " score "+
				// kcScoreList.get(j).get(i));
				if (max < kcScoreList.get(j).get(i).toDouble2()&& kcScoreList.get(j).get(i).toDouble2()<1.0){
					max = kcScoreList.get(j).get(i).toDouble2();
				}
				if (min > kcScoreList.get(j).get(i).toDouble2()){
					min =  kcScoreList.get(j).get(i).toDouble2();
				}
				
			}
			// System.out.println((sum / kcScoreList.size()));
			kcScore.add(sum / kcScoreList.size());
			kcScoreMax.add(max);
			kcScoreMin.add(min);
		}
		// for (int i = 0; i < 24; i++) {
		// Double sum = new Double(0.0);
		// Integer size =new Integer(0);
		// for (int j = 0; j < kcScoreList.size(); j++) {
		// if (kcScoreList.get(j).get(i) !=1){
		// sum += kcScoreList.get(j).get(i);
		// // System.out.println(i + " , " + " score "+
		// kcScoreList.get(j).get(i));
		// size++;
		// }
		//
		// }
		// // System.out.println((sum / kcScoreList.size()));
		// if(size == 0){
		// kcScore.add(1.0);
		// }else{
		// kcScore.add(sum / size);
		// }
		// }
////////////////////////////////////give all score. /////////////////////////////////////////
		// ArrayList<ArrayList<PatternScore>> testscorelist = getScoreList(testModels,
		// trainModel);
		ArrayList<ArrayList<PatternScore>> testscorelist = getScoreList(testModels,
				evenlyTrainModel);
//		printOrNot = 1;
		ArrayList<ArrayList<PatternScore>> testuserscorelist = getScoreList(testUserModels,
			evenlyTrainModel);
		// ArrayList<ArrayList<PatternScore>> valscorelist = getScoreList(valModels,
		// trainModel);
		ArrayList<ArrayList<PatternScore>> valscorelist = getScoreList(valModels,
				evenlyTrainModel);
		
///////////////////////////////////////// Print Score to file///////////////////////////
//		String fileName ="F:\\output-"
//						+ new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + ".txt";
//		File f = new File(fileName);
//	if (!f.exists()) {
//		f.createNewFile();
//	}
//	
//	final PrintStream ps = System.out; // Backup System.out
//	// Set file as System.out
//	System.setOut(new PrintStream(new FileOutputStream(f, true)));
//	for (int t = 0; t<24;t++){
////		for (int i =0;i<kcScoreList.size();i++){
////			System.out.println(kcScoreList.get(i).get(t).c1t+" , "+kcScoreList.get(i).get(t).c2o+" , "+kcScoreList.get(i).get(t).case2+" , "+kcScoreList.get(i).get(t).case3+" User");
////		}
//		for (int i =0;i<testuserscorelist.size();i++){
//		//	if (testuserscorelist.get(i).get(t).toDouble2()<1.0){
//				System.out.println(testuserscorelist.get(i).get(t).c1t+" "+testuserscorelist.get(i).get(t).c2o+" "+testuserscorelist.get(i).get(t).case2+" "+testuserscorelist.get(i).get(t).case3+" User");
//		//	}
//		}
//		for (int i =0;i<testuserscorelist.size();i++){
//			System.out.println(valscorelist.get(i).get(t).c1t+" "+valscorelist.get(i).get(t).c2o+" "+valscorelist.get(i).get(t).case2+" "+valscorelist.get(i).get(t).case3+" Other");
//		}
////		for (int i =0;i<valscorelist.size();i++){
////			System.out.println(valscorelist.get(i).get(t).c1t+" , "+valscorelist.get(i).get(t).c2o+" , "+valscorelist.get(i).get(t).case2+" , "+valscorelist.get(i).get(t).case3+" Other");
////		}
//	}
//
//	System.out.close(); // Close file
//	System.setOut(ps); // Recover System.out
////	System.out.println("Print in console");
//////////////////////////////////////////////////////////////////////////////////////////////
		ArrayList<Rank> tempTest1 = new ArrayList<Rank>();
		double performance1 = 0.0;
		for (int t = 0; t < 24; t++) {
			for(ArrayList<PatternScore> user: testuserscorelist){
				if (user.get(t).toDouble()< 1.0){
					tempTest1.add(new Rank(1 ,user.get(t).case1));
				}
			}
			for(ArrayList<PatternScore> other: testscorelist){
				tempTest1.add(new Rank(0 ,other.get(t).case1));
			}
			performance1 += countBestEntropy(tempTest1);
		}performance1 = performance1/24;
		
		ArrayList<Rank> tempTest2 = new ArrayList<Rank>();
		double performance2 = 0.0;
		for (int t = 0; t < 24; t++) {
			for(ArrayList<PatternScore> user: testuserscorelist){
				if (user.get(t).toDouble()< 1.0){
					tempTest2.add(new Rank(1 ,user.get(t).case2));
				}
			}
			for(ArrayList<PatternScore> other: testscorelist){
				tempTest2.add(new Rank(0 ,other.get(t).case2));
			}
			performance2 += countBestEntropy(tempTest2);
		}performance2 = performance2/24;
		

		ArrayList<Rank> tempTest3 = new ArrayList<Rank>();
		double performance3 = 0.0;
		for (int t = 0; t < 24; t++) {
			for(ArrayList<PatternScore> user: testuserscorelist){
				if (user.get(t).toDouble()< 1.0){
					tempTest3.add(new Rank(1 ,user.get(t).case3));
				}
			}
			for(ArrayList<PatternScore> other: testscorelist){
				tempTest3.add(new Rank(0 ,other.get(t).case3));
			}
			performance3 += countBestEntropy(tempTest3);
		}performance3 = performance3/24;
		
//System.out.println("pass 3");		
		ArrayList<Rank> tempTest4 = new ArrayList<Rank>();
		double performance4 = 0.0;
		for (int t = 0; t < 24; t++) {
			for(ArrayList<PatternScore> user: testuserscorelist){
				if (user.get(t).toDouble()< 1.0){
					tempTest4.add(new Rank(1 ,user.get(t).sumcase1ferq));
				}
			}
			for(ArrayList<PatternScore> other: testscorelist){
				tempTest4.add(new Rank(0 ,other.get(t).sumcase1ferq));
			}
			performance4 += countBestEntropy(tempTest4);
		}performance4 = performance4/24;
//System.out.println("pass 4");
		ArrayList<Rank> tempTest5 = new ArrayList<Rank>();
		double performance5 = 0.0;
		for (int t = 0; t < 24; t++) {
			for(ArrayList<PatternScore> user: testuserscorelist){
				if (user.get(t).toDouble()< 1.0){
					tempTest5.add(new Rank(1 ,user.get(t).toDouble()));
				}
			}
			for(ArrayList<PatternScore> other: testscorelist){
				tempTest5.add(new Rank(0 ,other.get(t).toDouble()));
			}
			performance5 += countBestEntropy(tempTest5);
		}performance5 = performance5/24;
//System.out.println("pass 5");
		ArrayList<Rank> tempTest6 = new ArrayList<Rank>();
		double performance6 = 0.0;
		for (int t = 0; t < 24; t++) {
			for(ArrayList<PatternScore> user: testuserscorelist){
				if (user.get(t).toDouble()< 1.0){
					tempTest6.add(new Rank(1 ,user.get(t).toDouble2()));
				}
			}
			for(ArrayList<PatternScore> other: testscorelist){
				tempTest6.add(new Rank(0 ,other.get(t).toDouble2()));
			}
			performance6 += countBestEntropy(tempTest6);
		}performance6 = performance6/24;
//System.out.println("pass 6");
//		ArrayList<Rank> tempTest7 = new ArrayList<Rank>();
//		double performance7 = 0.0;
//		for (int t = 0; t < 24; t++) {
//			for(ArrayList<PatternScore> user: testuserscorelist){
//				if (user.get(t).toDouble()< 1.0){
//					tempTest7.add(new Rank(1 ,user.get(t).toDouble3()));
//				}
//			}
//			for(ArrayList<PatternScore> other: testscorelist){
//				tempTest7.add(new Rank(0 ,other.get(t).toDouble3()));
//			}
//			performance7 += countBestEntropy(tempTest7);
//		}performance7 = performance7/24;
//		
//		ArrayList<Rank> tempTest8 = new ArrayList<Rank>();
//		double performance8 = 0.0;
//		for (int t = 0; t < 24; t++) {
//			for(ArrayList<PatternScore> user: testuserscorelist){
//				if (user.get(t).toDouble()< 1.0){
//					tempTest8.add(new Rank(1 ,user.get(t).toDouble4()));
//				}
//			}
//			for(ArrayList<PatternScore> other: testscorelist){
//				tempTest8.add(new Rank(0 ,other.get(t).toDouble4()));
//			}
//			performance8 += countBestEntropy(tempTest8);
//		}performance8 = performance8/24;
//		System.out.print("case1:	"+ performance1+"	case2:	"+performance2+"	case3:	"+performance3);
//		System.out.println("	case1:	"+ performance4+"	case2:	"+performance5+"	case3:	"+performance6);
//////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////// set decision threshold ///////////////////////////////////////
//		decisionThreshold = new ArrayList<Double>();
		ArrayList<DecisionRange> decisionRanges = new ArrayList<DecisionRange>();
		Double lowerstScore;
		Double closestLowerScore;
		Double kcClosestHigherstScore;
		for (int t = 0; t < 24; t++) {
			// closestHigherScore = 0.0;
			// for (int i = 0; i < valscorelist.size(); i++) {
			// if (closestHigherScore < valscorelist.get(i).get(t)
			// && kcScore.get(t) > valscorelist.get(i).get(t)) {
			// closestHigherScore = valscorelist.get(i).get(t);
			// }
			// }
		//decisionThreshold.add(kcScoreMax.get(t));
			lowerstScore = 1.0;
			closestLowerScore = 99.0;
			for (int i = 0; i < valscorelist.size(); i++) {
				if (closestLowerScore > valscorelist.get(i).get(t).toDouble2()
						&& kcScoreMax.get(t) < valscorelist.get(i).get(t).toDouble2()) {
					closestLowerScore = valscorelist.get(i).get(t).toDouble2();
				}
				if(lowerstScore > valscorelist.get(i).get(t).toDouble2()){
					lowerstScore = valscorelist.get(i).get(t).toDouble2();
				}
			}
			if (closestLowerScore == 99.0 || closestLowerScore ==2.0) {
				closestLowerScore = 1.0; //kcScoreMax.get(t);
			}
			kcClosestHigherstScore = 0.0;
			if (lowerstScore < closestLowerScore){
				for(int i = 0 ; i< kcScoreList.size();i++){
					if (kcClosestHigherstScore < kcScoreList.get(i).get(t).toDouble2()
							&& kcScoreList.get(i).get(t).toDouble2() < lowerstScore) {
						kcClosestHigherstScore = kcScoreList.get(i).get(t).toDouble2();
					}
				}
			}
			if (kcClosestHigherstScore ==0.0){
		//		System.out.println("lowerst: "+lowerstScore+" KCmin: "+kcScoreMax.get(t));
				kcClosestHigherstScore = closestLowerScore;
			}
		//	System.out.println((kcScoreMax.get(t) + closestLowerScore) / 2+" , "+kcScoreMax.get(t)+" , "+closestLowerScore);
			decisionRanges.add(new DecisionRange(((kcScoreMax.get(t) + closestLowerScore) / 2), ((kcClosestHigherstScore + lowerstScore) / 2)));
			// System.out.println(valscorelist.get(userIndex).get(t));
			// System.out.println(valscorelist.get(userIndex).get(t)+",,,,,,,,,"+closestHigherScore);
		//decisionThreshold.add((kcScoreMax.get(t) + closestLowerScore) / 2);
		//	decisionThreshold.add(kcScoreMax.get(t));
			// System.out.println(t+"   ,   "+decisionThreshold.size()+"   ,   "+decisionThreshold.get(t));
		}
		// System.out.println(decisionThreshold);

		// ////////////////////////////////////////////////////////////////////////////////////////
//		 ArrayList<Double> valSort;
//		 ArrayList<Double> testSort;
//		 for (int t = 6; t < 19; t++) {
//		 valSort = new ArrayList<Double>();
//		 testSort = new ArrayList<Double>();
//		 for (int i = 0; i < valscorelist.size(); i++) {
//		 valSort.add(valscorelist.get(i).get(t));
//		 testSort.add(testscorelist.get(i).get(t));
//		 }testSort.add(testscorelist.get(valscorelist.size()).get(t));
//		
//		 //valSort.sort(null);
//		 //testSort.sort(null);
//		 System.out.println("---------------"+ t +"----------------");
//		 System.out.println("Threshold :	" + decisionThreshold.get(t));
//		 System.out.println("Valation :" + kcScore.get(t));
//		 System.out.println(valSort);
//		 System.out.println("Test :" + testscorelist.get(userIndex).get(t));
//		 System.out.println(testSort);
//		 System.out.println("-------------------------------");
//		 System.out.println("-------------------------------");
//		 }
		// ////////////////////////////////////////////////////////////////////////////////////////
		ArrayList<Double> result = new ArrayList<Double>();
		Double tp = 0.0;
		Double fp = 0.0;
		Double tn = 0.0;
		Double fn = 0.0;
		Double up = 0.0;
		Double un = 0.0;
		Double badData = 0.0;
//		for (int t = 0; t < 24; t++) {
//			for (int i = 0; i < testscorelist.size(); i++) {
//				// System.out.print(testscorelist.get(i));{
//				if (testscorelist.get(i).get(t).toDouble2() > decisionThreshold.get(t)) {// kcScoreMax.get(t)){ //decisionThreshold.get(t)) {
//					tp++;
//				} else {
//					fn++;
//				}
//				// System.out.println(" "+(testscorelist.get(i) >=
//				// decisionThreshold?"T":"F"));
//			}
//			for (int i = 0; i < testuserscorelist.size(); i++) {
//				if (testuserscorelist.get(i).get(t).toDouble2() <= decisionThreshold.get(t)) {//kcScoreMax.get(t)){ //decisionThreshold.get(t)) {
//					tn++;
//				} else {
//					fp++;
//				}
//			}
//		}

//		Double tp1 = 0.0;
//		Double fp1 = 0.0;
//		Double tn1 = 0.0;
//		Double fn1 = 0.0;
/////////////////////////////////////////////////////////////////////
		ArrayList<Double> testmax = new ArrayList<Double>();
		for (int t = 0; t<24; t++){
			testmax.add(decisionRanges.get(t).max);
		}
		for (int i = 0; i < testuserscorelist.size(); i++) {
			for (int t = 0; t < 24; t++) {
				if (testuserscorelist.get(i).get(t).toDouble2()> testmax.get(t) && testuserscorelist.get(i).get(t).toDouble2()< 1.0){
					testmax.set(t, testuserscorelist.get(i).get(t).toDouble2());
				}
			}
		}

//////////////////////////////////////////////////////////////////////
		
//		for (int t = 0; t < 24; t++) {
//	//		System.out.println(t+" , "+decisionRanges.get(t).max+" , "+decisionRanges.get(t).min);
//			for (int i = 0; i < testscorelist.size(); i++) {
//				// System.out.print(testscorelist.get(i));{
//				if (testscorelist.get(i).get(t).toDouble2() >= decisionRanges.get(t).max){//decisionThreshold.get(t)) {// kcScoreMax.get(t)){ //decisionThreshold.get(t)) {
//					tp++;
//				}else if(testscorelist.get(i).get(t).toDouble2() < decisionRanges.get(t).min){
//					fn++;
//				}else{
//					up++;
//				}
//				// System.out.println(" "+(testscorelist.get(i) >=
//				// decisionThreshold?"T":"F"));
//			}
//		}
//		for (int i = 0; i < testuserscorelist.size(); i++) {
//			for (int t = 0; t < 24; t++) {
//				if (testuserscorelist.get(i).get(t).toDouble2() < 1.0){
//					if (testuserscorelist.get(i).get(t).toDouble2() <= decisionRanges.get(t).min){//decisionThreshold.get(t)) {//kcScoreMax.get(t)){ //decisionThreshold.get(t)) {
//						tn++;
////						System.out.println(i+" "+t +" T");
//					} else if (testuserscorelist.get(i).get(t).toDouble2() > decisionRanges.get(t).max){
//						fp++;
////						System.out.println(i+" "+t +" F");
//					}else {
//						un++;
//					}
//				}else{
//					//un++;
//					badData ++;
////				System.out.println(i+" "+t + " "+testuserscorelist.get(i).get(t));
//				}
//			}
//		}
/////////////////////////////////////////////////////////////////////////////////////////
		for (int t = 0; t < 24; t++) {
	//		System.out.println(t+" , "+decisionRanges.get(t).max+" , "+decisionRanges.get(t).min);
			for (int i = 0; i < testscorelist.size(); i++) {
				// System.out.print(testscorelist.get(i));{
				if (testscorelist.get(i).get(t).toDouble2() >= testmax.get(t)){//decisionThreshold.get(t)) {// kcScoreMax.get(t)){ //decisionThreshold.get(t)) {
					tp++;
				}else{
					fn++;
				}
				// System.out.println(" "+(testscorelist.get(i) >=
				// decisionThreshold?"T":"F"));
			}
		}
		for (int i = 0; i < testuserscorelist.size(); i++) {
			for (int t = 0; t < 24; t++) {
				if (testuserscorelist.get(i).get(t).toDouble2() < 1.0){
					if (testuserscorelist.get(i).get(t).toDouble2() <= testmax.get(t)){//decisionThreshold.get(t)) {//kcScoreMax.get(t)){ //decisionThreshold.get(t)) {
						tn++;
//						System.out.println(i+" "+t +" T");
					} else{
						fp++;
//						System.out.println(i+" "+t +" F");
					}
				}else{
					//un++;
					badData ++;
//				System.out.println(i+" "+t + " "+testuserscorelist.get(i).get(t));
				}
			}
		}
		// System.out.println("Accurcy:"+ (tn+tp) / testscorelist.size());
		result.add(tp / (tp + fn + up));
		result.add(fp / (fp + tn + un + badData));
		result.add(tp);
		result.add(fn);
		result.add(fp);
		result.add(tn);
		result.add(up);
		result.add(un);
		result.add(performance1);
		result.add(performance2);
		result.add(performance3);
		result.add(performance4);
		result.add(performance5);
		result.add(performance6);
//		result.add(performance7);
//		result.add(performance8);
		result.add(badData);
		double thresholdCompare = 0.0;
		for (int t = 0; t<24; t++){
			thresholdCompare += (testmax.get(t)/ decisionRanges.get(t).max);
		}
		
		result.add (thresholdCompare /24);
		
//		System.out.println("TPR:	" + tp / (tp + fn) + "	,	" + tp1 / (tp1 + fn1)
//				+ "	FPR:	" + fp / (fp + tn) + "	,	" + fp1 / (fp1 + tn1)
//				+ "	Tp: " + tp + " Fn: " + fn + " Fp: " + fp + " Tn: " + tn
//				+ " Tp1: " + tp1 + " Fn1: " + fn1 + " Fp1: " + fp1 + " Tn1: "
//				+ tn1);
//		System.out.println("TPR:	" + tp / (tp + fn) + "	,	" + tp1 / (tp1 + fn1)+ "	FPR:	" + fp / (fp + tn) + "	,	" + fp1 / (fp1 + tn1));
		return result;
	}

	public static ArrayList<ArrayList<PatternScore>> getScoreList(
			ArrayList<LocationDecisionModel> Models,
			LocationDecisionModel trainModel) {
		ArrayList<ArrayList<PatternScore>> scorelist = new ArrayList<ArrayList<PatternScore>>();

		for (LocationDecisionModel valModel : Models) {
			scorelist.add(giveScore(trainModel, valModel));
		}
		return scorelist;
	}

	/**
	 * @return abc
	 */
	public static ArrayList<PatternScore> giveScore(LocationDecisionModel a,
			LocationDecisionModel b) {
		ArrayList<PatternScore> resultGS = new ArrayList<PatternScore>();
		ArrayList<PatternScore> resultGS1 = new ArrayList<PatternScore>();
		// double retrunS=0.0;
		PatternScore tempPS;
		for (int r = 0; r < 24; r++) {
			tempPS = new PatternScore(MinePattern.mergePatternTable(
					MinePattern.mergePatternTable(a.hourModel.get(r),
							a.hourModelShift.get(r)), a.hourModelShift
							.get(r + 1)), b.hourModel.get(r), a.locs, b.locs);
			resultGS.add(tempPS);
		}
			// PatternTable tempGSS = new PatternTable();
			// tempGSS =
			// MinePattern.mergePatternTable(MinePattern.mergePatternTable(a.hourModel.get(r),
			// a.hourModelShift.get(r)), a.hourModelShift
			// .get(r + 1));
			// resultGS.add(givePatternScore(tempGSS, b.hourModel.get(r)));
			// System.out.println(tempGSS.patternMap.size()+" ++++ "+b.hourModel.get(r).patternMap.size());
			// resultGS.add(givePatternScore(a.oneDayModel.get(r / 6),
			// b.hourModel.get(r))
			// + givePatternScore(MinePattern.mergePatternTable(
			// MinePattern.mergePatternTable(a.hourModel.get(r),
			// a.hourModelShift.get(r)), a.hourModelShift
			// .get(r + 1)), b.hourModel.get(r)));
			// retrunS += resultGS.get(r);
//		for (int r = 2; r < 24; r++) {
//			resultGS1.add((3 * resultGS.get(r)) + (2 * resultGS.get(r - 1))+ resultGS.get(r - 2));
//		}
		return resultGS;
	}
	

	public static double countBestEntropy(ArrayList<Rank> a){
//		System.out.println("////////////////////////////");
//		for(Rank x:a){
//			System.out.println(x.s);
//		}
		Collections.sort(a);
		double ig = 10000;
		for (int i = 1; i<a.size()-1;i++){
			double x = countEP(a.subList(0, i));// * (i+1);
			double y = countEP(a.subList(i+1, a.size()));// * (a.size()-i-1);
			if ((x+y)/a.size() < ig){
				ig = (x+y)/a.size();
			}
		}
		return ig;
	}
	public static double countEP(List<Rank> list){
		double p = 0;
		double n = 0;
		for (int i = 0;i<list.size();i++){
			if(list.get(i).identity == 1){
				p++;
			}else{
				n++;
			}
		}
		p=p/list.size();
		n=n/list.size();
		if (p == 0 || n == 0){
			return 0.0;
		}else{
			return (-p*Math.log(p)/Math.log(2) - n*Math.log(n)/Math.log(2))*list.size();
		}
	}


//	public static double givePatternScore(PatternTable training, PatternTable b, HashSet ths, HashSet hs) {
//		Double score = new Double(0.0);
//		Double sumOfPattern = new Double(0.0);
//		Double case1 = new Double(0.0);
//		Double case2 = new Double(0.0);
//		Double case3 = new Double(0.0);
//
//		for (Pattern x : training.patternMap.values()) {
//			sumOfPattern += x.frequency;
//		}
//		for (Pattern x : b.patternMap.values()) {
//			sumOfPattern += x.frequency;
//		}
//		for (String patternTraining : training.patternMap.keySet()) {
//			if (b.patternMap.containsKey(patternTraining)) {
//				case1+= (Math
//						.abs(training.patternMap.get(patternTraining).frequency
//								- b.patternMap.get(patternTraining).frequency));
//			} else {
//				case2 += training.patternMap.get(patternTraining).frequency;
//			}
//		}
//		for (String patternB : b.patternMap.keySet()) {
//			if (!training.patternMap.containsKey(patternB)) {
//				case3 += b.patternMap.get(patternB).frequency;
//			}
//		}
//		score = case1 + case2 + case3;
//		if (hs.size() == 0){
//			return 2.0;//(score/sumOfPattern)/ (hs.size() /ths.size());
//		}else{
//			return (score/sumOfPattern);
//		}
//	}
//		if (printOrNot == 1){
//			System.out.println(case1+ "	"+ case2+ "	"+case3);
//			//System.out.println(ths.size()+", "+hs.size());
//		}
		// System.out.println("training: "+training.patternMap.size()+"  test: "+
		// b.patternMap.size()+" score: "+score);
		// System.out.println(sumOfPattern+" , "+training.patternMap.size()+" , "+b.patternMap.size()+" , "+score/sumOfPattern);
//		if (case1 == 0 && case3 == 0){
//			return 2.0;
//				//return (score / sumOfPattern)/ (hs.size() /ths.size());
//				//return (score / sumOfPattern) /  ths.size();
//		}else{
//			return (case1+case2 / sumOfPattern);//(case1+case2)/sumOfPattern;
//		}
//		if (case1 == 0 && case3 == 0){
//		if (case1 == 0 && case3 == 0){
//			return 2.0;
//				//return (score / sumOfPattern)/ (hs.size() /ths.size());
//				//return (score / sumOfPattern) /  ths.size();
//		}else{
//			return (score / sumOfPattern);//(case1+case2)/sumOfPattern;
//		}
//		return score / sumOfPattern;
//		System.out.println(score/ hs.size());
//		System.out.println(score);
//		System.out.println(hs.size());
//		if (hs.size()!= 0 && ths.size()!=0){
//			return score / (hs.size() /ths.size());
//		}else{
//			return score;
//		}
//		return score ;



	@SuppressWarnings("unchecked")
	public static LocationDecisionModel evenlySetSeq(
			LocationDecisionModel Model, Double days) {
		LocationDecisionModel evenlyModel = new LocationDecisionModel();
		PatternTable tempPT0;
		for (int q = 0; q < 24; q++) {
			tempPT0 = new PatternTable();
			for (int i = 0; i < Model.hourModel.get(q).patternArrayList.size(); i++) {
				Pattern tempP = new Pattern(
						Model.hourModel.get(q).patternArrayList.get(i).locationList,
						(Model.hourModel.get(q).patternArrayList.get(i).frequency / days));
				tempPT0.add(tempP);
			}
			evenlyModel.hourModel.add(tempPT0);
		}
		PatternTable tempPT1;
		for (int q = 0; q < 25; q++) {
			tempPT1 = new PatternTable();
			for (int i = 0; i < Model.hourModelShift.get(q).patternArrayList
					.size(); i++) {
				Pattern tempP = new Pattern(
						Model.hourModelShift.get(q).patternArrayList.get(i).locationList,
						(Model.hourModelShift.get(q).patternArrayList.get(i).frequency / days));
				tempPT1.add(tempP);
			}
			evenlyModel.hourModelShift.add(tempPT1);
		}
		PatternTable tempPT2;
		for (int q = 0; q < 5; q++) {
			tempPT2 = new PatternTable();
			for (int i = 0; i < Model.oneDayModel.get(q).patternArrayList
					.size(); i++) {
				Pattern tempP = new Pattern(
						Model.oneDayModel.get(q).patternArrayList.get(i).locationList,
						(Model.oneDayModel.get(q).patternArrayList.get(i).frequency / days));
				tempPT2.add(tempP);
			}
			evenlyModel.oneDayModel.add(tempPT2);
		}
		
		evenlyModel.locs.addAll(Model.locs);
//		System.out.println(Model.locs.size()+" , "+evenlyModel.locs.size());
		return evenlyModel;
	}
}
class Rank implements Comparable<Rank>{
	 double s;
	 int identity;
	public Rank(int identity, double score){
		this.s = score;
		this.identity = identity;
	}
	@Override
	public int compareTo(Rank o) {
		return (int) (this.s*10000 - o.s*10000);
	}
}
class DecisionRange {
	 double max;
	 double min;
	public DecisionRange(double max, double min){
		this.max = max;
		this.min = min;
	}
//	@Override
//	public int compareTo(Rank o) {
//		return (int) (this.s*10000 - o.s*10000);
//	}
}
