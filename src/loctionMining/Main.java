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
		
//		System.out.println("HMM");
//		LocationHMM.locatinoHMM(allUsersWeekRawData, 4, Calendar.MONDAY);
		
		for (int userIndex = 0; userIndex < allUsersWeekRawData.size();
				 userIndex++) {
			LocationHMM.locatinoHMM(allUsersWeekRawData, userIndex, Calendar.MONDAY);
		}
		
//		for (int userIndex = 0; userIndex < allUsersWeekRawData.size(); userIndex++) {
//			testActivityData(allUsersWeekRawData, userIndex, Calendar.MONDAY);
//		}

		// System.out.println(allUsersRawData.size());
		// System.out.println(allUsersWeekRawData.size());
		// Calendar cal = Calendar.getInstance();

		
//		 System.out.println("MONDAY");
//		 for (int userIndex = 0; userIndex < allUsersWeekRawData.size();
//		 userIndex++) {
//			 testLocation(allUsersWeekRawData, userIndex, Calendar.MONDAY);
//		 }
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
//		ArrayList<resultList> allResults = new ArrayList<resultList>();
//		ArrayList<Double> bestResult = new ArrayList<Double>();
//		for(int i = 0;i<12;i++){
//			bestResult.add(1.0);
//		}
		ArrayList<Double> results = new ArrayList<Double>();
		for(int i = 0; i<17 ;i++){
			results.add(0.0);
		}
		ArrayList<Roc> scase1 = new ArrayList<Roc>();
		ArrayList<Roc> score1 = new ArrayList<Roc>();
		ArrayList<Roc> score2 = new ArrayList<Roc>();
		for (int j = 0 ; j<10 ; j++){
			scase1.add(new Roc(0.0 , 0.0));
			score1.add(new Roc(0.0 , 0.0));
			score2.add(new Roc(0.0 , 0.0));
		}
		
		ArrayList<ResultsTandF> strategy1 = new ArrayList<ResultsTandF>();
		ArrayList<ResultsTandF> strategy2 = new ArrayList<ResultsTandF>();
		ArrayList<ResultsTandF> strategy3 = new ArrayList<ResultsTandF>();
		ArrayList<ResultsTandF> strategy4 = new ArrayList<ResultsTandF>();
		for (int i = 0; i<100; i++){
			ArrayList<ArrayList<Double>> tempresult = new ArrayList<ArrayList<Double>>();
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
			strategy1.add(new ResultsTandF (tempresult.get(0).get(0),tempresult.get(0).get(1)));
//			results.set(0, results.get(0)+tempresult.get(0).get(0));
//			results.set(1, results.get(1)+tempresult.get(0).get(1));
			results.set(2, results.get(2)+tempresult.get(0).get(2));
			
			strategy2.add(new ResultsTandF (tempresult.get(1).get(0),tempresult.get(1).get(1)));
//			results.set(3, results.get(3)+tempresult.get(1).get(0));
//			results.set(4, results.get(4)+tempresult.get(1).get(1));
			results.set(5, results.get(5)+tempresult.get(1).get(2));
			
			strategy3.add(new ResultsTandF (tempresult.get(2).get(0),tempresult.get(2).get(1)));
//			results.set(6, results.get(6)+tempresult.get(2).get(0));
//			results.set(7, results.get(7)+tempresult.get(2).get(1));
			results.set(8, results.get(8)+tempresult.get(2).get(2));
			results.set(9, results.get(9)+tempresult.get(2).get(3));
			
			strategy4.add(new ResultsTandF (tempresult.get(3).get(0),tempresult.get(3).get(1)));
//			results.set(10, results.get(10)+tempresult.get(3).get(0));
//			results.set(11, results.get(11)+tempresult.get(3).get(1));
			results.set(12, results.get(12)+tempresult.get(3).get(2));
			results.set(13, results.get(13)+tempresult.get(3).get(3));
			
			results.set(14, results.get(14)+tempresult.get(3).get(4));
			results.set(15, results.get(15)+tempresult.get(3).get(5));
			results.set(16, results.get(16)+tempresult.get(3).get(6));
			
			for (int j = 0 ; j<10 ; j++){
//				System.out.println(tempresult.get(4).get(j));
				if (tempresult.get(4).get(j) != 0){
					scase1.set(j, new Roc((scase1.get(j).counter + 1), (scase1.get(j).scores+tempresult.get(4).get(j))));
				}
				if (tempresult.get(5).get(j) != 0){
					score1.set(j, new Roc((score1.get(j).counter + 1), (score1.get(j).scores+tempresult.get(5).get(j))));
				}
				if (tempresult.get(6).get(j) != 0){
					score2.set(j, new Roc((score2.get(j).counter + 1), (score2.get(j).scores+tempresult.get(6).get(j))));
				}
			}
		}
		ArrayList<Double> showTandF = new ArrayList<Double>();
		for(int i = 0; i<8; i++){
			showTandF.add(0.0);
		}
		Collections.sort(strategy1);Collections.sort(strategy2);Collections.sort(strategy3);Collections.sort(strategy4);
		for(int i = 0; i<50; i++){
			showTandF.set(0 , showTandF.get(0)+strategy1.get(i).tpr);
			showTandF.set(1 , showTandF.get(1)+strategy1.get(i).fpr);
			showTandF.set(2 , showTandF.get(2)+strategy2.get(i).tpr);
			showTandF.set(3 , showTandF.get(3)+strategy2.get(i).fpr);
			showTandF.set(4 , showTandF.get(4)+strategy3.get(i).tpr);
			showTandF.set(5 , showTandF.get(5)+strategy3.get(i).fpr);
			showTandF.set(6 , showTandF.get(6)+strategy4.get(i).tpr);
			showTandF.set(7 , showTandF.get(7)+strategy4.get(i).fpr);
		}
//		System.out.print("GoodRate:	"+results.get(2)/100+"	T*/Tu:	" +results.get(5)/100
//				+"	strategy1:	" +results.get(0)/100 +"	,	"+results.get(1)/100
//				+"	strategy2:	" +results.get(3)/100 +"	,	"+results.get(4)/100
//				+"	strategy3:	" +results.get(6)/100 +"	,	"+results.get(7)/100+"	unKnownO	"+results.get(8)/100+"	unKnownU	"+results.get(9)/100
//				+"	strategy4:	" +results.get(10)/100 +"	,	"+results.get(11)/100+"	unKnownO	"+results.get(12)/100+"	unKnownU	"+results.get(13)/100);
		System.out.print("GoodRate:	"+results.get(2)/100+"	T*/Tu:	" +results.get(5)/100+"	T*/Tu':	" +results.get(16)/100
				+"	strategy1:	" +showTandF.get(0)/50 +"	,	"+showTandF.get(1)/50
				+"	strategy2:	" +showTandF.get(2)/50 +"	,	"+showTandF.get(3)/50+"	T*:TPR	"+ results.get(15)/100
				+"	strategy3:	" +showTandF.get(4)/50 +"	,	"+showTandF.get(5)/50+"	unKnownO	"+results.get(8)/100+"	unKnownU	"+results.get(9)/100
				+"	strategy4:	" +showTandF.get(6)/50 +"	,	"+showTandF.get(7)/50+"	unKnownO	"+results.get(12)/100+"	unKnownU	"+results.get(13)/100+"	T*:TPR	"+ results.get(14)/100);
		System.out.print("	Scoreing1:	");
		for (int j = 0 ; j<10 ; j++){
			System.out.print(scase1.get(j).scores / scase1.get(j).counter+"	,	");
		}
		System.out.print("	Scoreing2:	");
		for (int j = 0 ; j<10 ; j++){
			System.out.print(score1.get(j).scores / score1.get(j).counter+"	,	");
		}
		System.out.print("	Scoreing3:	");
		for (int j = 0 ; j<10 ; j++){
			System.out.print(score2.get(j).scores / score2.get(j).counter+"	,	");
		}
		System.out.println(" ");
//			resultList temprl = new resultList(tempresult);
//			allResults.add(temprl);
//			if (tempresult.get(1)==bestResult.get(1)){
//				if(tempresult.get(0)>bestResult.get(0)){
//					bestResult = tempresult;
//				}
//				if(tempresult.get(0)==bestResult.get(0)&& (int)(tempresult.get(6)+tempresult.get(7))>(int)(bestResult.get(6)+bestResult.get(7))){
//					bestResult = tempresult;
//				}
//			}
//			if (tempresult.get(1)<bestResult.get(1)){
//				bestResult = tempresult;
//			}
//			p1+=tempresult.get(8);
//			p2+=tempresult.get(9);
//			p3+=tempresult.get(10);
//			p4+=tempresult.get(11);
//			p5+=tempresult.get(12);
//			p6+=tempresult.get(13);
//			tcompare+=tempresult.get(15);
////			p7+=tempresult.get(14);
////			p8+=tempresult.get(15);
//		}
//		Collections.sort(allResults);
//		resultList avgResult = new resultList();
//		Double avgTPR = 0.0;
//		Double avgFPR = 0.0;
//		Double goodData = 0.0;
//		Double allData = 0.0;
//		for(int i = 0; i<50; i++){
//		//	System.out.println("tp: "+allResults.get(i).results.get(1));
////			System.out.println("fp: "+allResults.get(i).results.get(3));
//				//avgResult.results.set(j, (avgResult.results.get(j)+allResults.get(i).results.get(j)));
//			avgTPR += allResults.get(i).results.get(0);
//			avgFPR += allResults.get(i).results.get(1);
////			System.out.println("good "+(allResults.get(i).results.get(10)+allResults.get(i).results.get(11))+"total: "+allResults.get(i).results.get(10)+allResults.get(i).results.get(11));
//			goodData +=(int)(allResults.get(i).results.get(4)+allResults.get(i).results.get(5)+allResults.get(i).results.get(7));
//			allData +=(int)(allResults.get(i).results.get(4)+allResults.get(i).results.get(5)+allResults.get(i).results.get(7)+allResults.get(i).results.get(14));
//			upr += allResults.get(i).results.get(6)/(allResults.get(i).results.get(2)+allResults.get(i).results.get(3)+allResults.get(i).results.get(6));
//			unr += allResults.get(i).results.get(7)/(allResults.get(i).results.get(4)+allResults.get(i).results.get(5)+allResults.get(i).results.get(7));
//		}
//		avgTPR = avgTPR/50;
//		avgFPR = avgFPR/50;
//		upr = upr/50;
//		unr = unr/50;
////		System.out.println("TPR:	" + bestResult.get(0) + "	,	" + bestResult.get(1)
////				+ "	FPR:	" + bestResult.get(2)+ "	,	" + bestResult.get(3)
////				+ "	Tp: " +bestResult.get(4)+ " Fn: " + bestResult.get(5) + " Fp: " + bestResult.get(6)+ " Tn: " + bestResult.get(7)
////				+ " Tp1: " +bestResult.get(8) + " Fn1: " +bestResult.get(9)+ " Fp1: " +bestResult.get(10) + " Tn1: "+ bestResult.get(11));
//		System.out.println("avgTPR:	" + avgTPR + "	bestTPR	" + bestResult.get(0)+"	avgFPR:	"+ avgFPR
//				+"	bestFPR:	"+ bestResult.get(1)+"	goodRate:	"+goodData/allData+"	,	"
//				+(bestResult.get(4)+bestResult.get(5)+bestResult.get(7))/(bestResult.get(4)+bestResult.get(5)+bestResult.get(7)+bestResult.get(14))+"	OtherRate:	"+ upr+"	UserRate:	"+ unr
//				+"	case1:	"+p1/100+"	case2:	"+p2/100+"	case3:	"+p3/100 +"	case1-1:	"+p4/100+"	score1:	"+p5/100+"	score2:	"+p6/100+"	To'/To:	"+ tcompare/100);//+"	score3:	"+p7/100+"	score4:	"+p7/100);
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
		public static ArrayList<ArrayList<Double>> evaluateLocationModel(ArrayList<LocationDecisionModel> trainModels,
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
			//for (int t = 0; t < tempList.size(); t++) {
				// System.out.println(t + "t " + " , " + tempList.get(t));
			//}
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
//	System.out.println("Print in console");
//////////////////////////////////////////////////////////////////////////////////////////////
//		ArrayList<Rank> tempTest1 = new ArrayList<Rank>();
//		double performance1 = 0.0;
//		for (int t = 0; t < 24; t++) {
//			for(ArrayList<PatternScore> user: testuserscorelist){
//				if (user.get(t).toDouble()< 1.0){
//					tempTest1.add(new Rank(1 ,user.get(t).case1));
//				}
//			}
//			for(ArrayList<PatternScore> other: testscorelist){
//				tempTest1.add(new Rank(0 ,other.get(t).case1));
//			}
//			performance1 += countBestEntropy(tempTest1);
//		}performance1 = performance1/24;
//		
//		ArrayList<Rank> tempTest2 = new ArrayList<Rank>();
//		double performance2 = 0.0;
//		for (int t = 0; t < 24; t++) {
//			for(ArrayList<PatternScore> user: testuserscorelist){
//				if (user.get(t).toDouble()< 1.0){
//					tempTest2.add(new Rank(1 ,user.get(t).case2));
//				}
//			}
//			for(ArrayList<PatternScore> other: testscorelist){
//				tempTest2.add(new Rank(0 ,other.get(t).case2));
//			}
//			performance2 += countBestEntropy(tempTest2);
//		}performance2 = performance2/24;
//		
//
//		ArrayList<Rank> tempTest3 = new ArrayList<Rank>();
//		double performance3 = 0.0;
//		for (int t = 0; t < 24; t++) {
//			for(ArrayList<PatternScore> user: testuserscorelist){
//				if (user.get(t).toDouble()< 1.0){
//					tempTest3.add(new Rank(1 ,user.get(t).case3));
//				}
//			}
//			for(ArrayList<PatternScore> other: testscorelist){
//				tempTest3.add(new Rank(0 ,other.get(t).case3));
//			}
//			performance3 += countBestEntropy(tempTest3);
//		}performance3 = performance3/24;
		
//System.out.println("pass 3");		
//		ArrayList<Rank> tempTest4 = new ArrayList<Rank>();
//		double performance4 = 0.0;
//		for (int t = 0; t < 24; t++) {
//			for(ArrayList<PatternScore> user: testuserscorelist){
//				if (user.get(t).toDouble()< 1.0){
//					tempTest4.add(new Rank(1 ,user.get(t).sumcase1ferq));
//				}
//			}
//			for(ArrayList<PatternScore> other: testscorelist){
//				tempTest4.add(new Rank(0 ,other.get(t).sumcase1ferq));
//			}
//			performance4 += countBestEntropy(tempTest4);
//		}performance4 = performance4/24;
//////System.out.println("pass 4");
//		ArrayList<Rank> tempTest5 = new ArrayList<Rank>();
//		double performance5 = 0.0;
//		for (int t = 0; t < 24; t++) {
//			for(ArrayList<PatternScore> user: testuserscorelist){
//				if (user.get(t).toDouble()< 1.0){
//					tempTest5.add(new Rank(1 ,user.get(t).toDouble()));
//				}
//			}
//			for(ArrayList<PatternScore> other: testscorelist){
//				tempTest5.add(new Rank(0 ,other.get(t).toDouble()));
//			}
//			performance5 += countBestEntropy(tempTest5);
//		}performance5 = performance5/24;
//////System.out.println("pass 5");
//		ArrayList<Rank> tempTest6 = new ArrayList<Rank>();
//		double performance6 = 0.0;
//		for (int t = 0; t < 24; t++) {
//			for(ArrayList<PatternScore> user: testuserscorelist){
//				if (user.get(t).toDouble()< 1.0){
//					tempTest6.add(new Rank(1 ,user.get(t).toDouble2()));
//				}
//			}
//			for(ArrayList<PatternScore> other: testscorelist){
//				tempTest6.add(new Rank(0 ,other.get(t).toDouble2()));
//			}
//			performance6 += countBestEntropy(tempTest6);
//		}performance6 = performance6/24;
		
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
//////////////////////////////////////////////////////////////////////////////////////////////
		ArrayList<Rank> tempTest4 = new ArrayList<Rank>();
		ArrayList<Roc> roc1 = new ArrayList<Roc>();
		for (int i = 0; i<10;i++){
			roc1.add(new Roc(0.0, 0.0));
		}
		ArrayList<Roc> tempRoc1 = new ArrayList<Roc>();
		for (int t = 0; t < 24; t++) {
			tempTest4 = new ArrayList<Rank>();
			tempRoc1 = new ArrayList<Roc>();
			for(ArrayList<PatternScore> user: testuserscorelist){
				if (user.get(t).toDouble()< 1.0){
					tempTest4.add(new Rank(1 ,100000 - user.get(t).sumcase1ferq));
				}
			}
			for(ArrayList<PatternScore> other: testscorelist){
				tempTest4.add(new Rank(0 ,100000 - other.get(t).sumcase1ferq));
			}
			tempRoc1.addAll(GiveTPRandFPRforROC(tempTest4, (tempTest4.size()- testscorelist.size()), testscorelist.size()));
//for (int i = 0; i<10;i++){
//System.out.println("outside: "+tempRoc1.get(i).scores+" , "+ tempRoc1.get(i).counter);
//}
			for (int i = 0; i<10;i++){
				roc1.set(i,  new Roc((tempRoc1.get(i).counter + roc1.get(i).counter) ,(tempRoc1.get(i).scores + roc1.get(i).scores)));
			}
		}
		ArrayList<Double> rocList1 = new ArrayList<Double>();
		for (int i = 0; i<10;i++){
//System.out.println( roc1.get(i).scores+" , "+ roc1.get(i).counter);
			if (roc1.get(i).counter == 0){
				rocList1.add(0.0);
			}else{
				rocList1.add(roc1.get(i).scores/roc1.get(i).counter);
			}
		}

////System.out.println("pass 4");
		ArrayList<Rank> tempTest5 = new ArrayList<Rank>();
		ArrayList<Roc> roc2 = new ArrayList<Roc>();
		for (int i = 0; i<10;i++){
			roc2.add(new Roc(0.0, 0.0));
		}
		ArrayList<Roc> tempRoc2 = new ArrayList<Roc>();
		for (int t = 7; t < 20; t++) {
			tempTest5 = new ArrayList<Rank>();
			tempRoc2 = new ArrayList<Roc>();
			for(ArrayList<PatternScore> user: testuserscorelist){
					if (user.get(t).toDouble()< 1.0){
						tempTest5.add(new Rank(1 ,user.get(t).toDouble()));
					}
			}
			for(ArrayList<PatternScore> user: kcScoreList){
				if (user.get(t).toDouble()< 1.0){
					tempTest5.add(new Rank(1 ,user.get(t).toDouble()));
				}
			}
			if (tempTest5.size()>20){
				for(ArrayList<PatternScore> other: testscorelist){
					tempTest5.add(new Rank(0 ,other.get(t).toDouble()));
				}
				tempRoc2.addAll(GiveTPRandFPRforROC(tempTest5, (tempTest5.size()- testscorelist.size()), testscorelist.size()));
				for (int i = 0; i<10;i++){
					roc2.set(i,  new Roc((tempRoc2.get(i).counter + roc2.get(i).counter) ,(tempRoc2.get(i).scores + roc2.get(i).scores)));
				}
			}
		}
		ArrayList<Double> rocList2 = new ArrayList<Double>();
		for (int i = 0; i<10;i++){
			if (roc2.get(i).counter == 0){
				rocList2.add(0.0);
			}else{
				rocList2.add(roc2.get(i).scores/roc2.get(i).counter);
//if(roc2.get(i).scores/roc2.get(i).counter > 1.0){
//System.out.println("11111111111111111111111111111\r\n"+ roc2.get(i).scores+" , "+roc2.get(i).counter +"\r\n111111111111111111111111");
//}
			}
		}
////System.out.println("pass 5");
		ArrayList<Rank> tempTest6 = new ArrayList<Rank>();
		ArrayList<Roc> roc3 = new ArrayList<Roc>();
		for (int i = 0; i<10;i++){
			roc3.add(new Roc(0.0, 0.0));
		}
		ArrayList<Roc> tempRoc3 = new ArrayList<Roc>();
		for (int t = 7; t < 20; t++) {
			tempTest6 = new ArrayList<Rank>();
			tempRoc3 = new ArrayList<Roc>();
			for(ArrayList<PatternScore> user: testuserscorelist){
				if (user.get(t).toDouble()< 1.0){
					tempTest6.add(new Rank(1 ,user.get(t).toDouble2()));
				}
			}
			for(ArrayList<PatternScore> user: kcScoreList){
				if (user.get(t).toDouble()< 1.0){
					tempTest6.add(new Rank(1 ,user.get(t).toDouble()));
				}
			}
			if (tempTest6.size()>20){
				for(ArrayList<PatternScore> other: testscorelist){
					tempTest6.add(new Rank(0 ,other.get(t).toDouble2()));
				}
				tempRoc3.addAll(GiveTPRandFPRforROC(tempTest6, (tempTest6.size()- testscorelist.size()), testscorelist.size()));
				for (int i = 0; i<10;i++){
					roc3.set(i,  new Roc((tempRoc3.get(i).counter + roc3.get(i).counter),(tempRoc3.get(i).scores + roc3.get(i).scores)));
				}
			}
		}
		ArrayList<Double> rocList3 = new ArrayList<Double>();
		for (int i = 0; i<10;i++){
			if (roc3.get(i).counter == 0){
				rocList3.add(0.0);
			}else{
				rocList3.add(roc3.get(i).scores/roc3.get(i).counter);
			}
		}
////////////////////////////////////////////////////////////////////////////////////////// use KC max
		ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> result1 = new ArrayList<Double>();
		Double tp = 0.0;
		Double fp = 0.0;
		Double tn = 0.0;
		Double fn = 0.0;
		Double up = 0.0;
		Double un = 0.0;
		Double badData = 0.0;
		for (int t = 0; t < 24; t++) {

			for (int i = 0; i < testscorelist.size(); i++) {
				if (testscorelist.get(i).get(t).toDouble2() >=  kcScoreMax.get(t)){ //decisionThreshold.get(t)) {
					tp++;
				}else{
					fn++;
				}

			}
		}
		for (int i = 0; i < testuserscorelist.size(); i++) {
			for (int t = 0; t < 24; t++) {
				if (testuserscorelist.get(i).get(t).toDouble2() < 1.0){
					if (testuserscorelist.get(i).get(t).toDouble2() <= kcScoreMax.get(t)){ //decisionThreshold.get(t)) {
						tn++;
					} else{
						fp++;
					}
				}else{
					badData ++;
				}
			}
		}
		result1.add(tp / (tp + fn));
		result1.add(fp / (fp + tn));
//		result1.add(tp);
//		result1.add(fn);
//		result1.add(fp);
//		result1.add(tn);
		result1.add((tn+fp)/(tn+fp+badData));
		result.add(result1);
////////////////////////////////////////////////////////////////////////rising threshold by fp
		ArrayList<Double> testmax = new ArrayList<Double>();
		for (int t = 0; t<24; t++){
			testmax.add(kcScoreMax.get(t));
		}
		
		ArrayList<Double> result2 = new ArrayList<Double>();
		Double tp2 = 0.0;
		Double fp2 = 0.0;
		Double tn2 = 0.0;
		Double fn2 = 0.0;
		Double badData2 = 0.0;
		
		Double tp22 = 0.0;
		Double fn22 = 0.0;
		for (int t = 0; t < 24; t++) {
			for (int i = 0; i < testscorelist.size(); i++) {
				if (testscorelist.get(i).get(t).toDouble2() >= testmax.get(t)){
					tp2++;
				}else{
					fn2++;
				}
			}
		}
		for (int i = 0; i < testuserscorelist.size(); i++) {
			for (int t = 0; t < 24; t++) {
				if (testuserscorelist.get(i).get(t).toDouble2() < 1.0){
					if (testuserscorelist.get(i).get(t).toDouble2() <= testmax.get(t)){
						tn2++;
					} else{
						testmax.set(t, testuserscorelist.get(i).get(t).toDouble2());
						fp2++;
					}
				}else{
					badData2 ++;
				}
			}
		}
		for (int t = 0; t < 24; t++) {
			for (int i = 0; i < testscorelist.size(); i++) {
				if (testscorelist.get(i).get(t).toDouble2() >= testmax.get(t)){
					tp22++;
				}else{
					fn22++;
				}
			}
		}
		tp2 = (tp2+tp22) / 2;
//		fn2 = (fn2+fn22) / 2;
		result2.add(tp2 / (tp2 + fn2));
		result2.add(fp2 / (fp2 + tn2));
//		result2.add(tp2);
//		result2.add(fn2);
//		result2.add(fp2);
//		result2.add(tn2);
		double thresholdCompare = 0.0;
		double counterT = 0.0;
		for (int t = 0; t<24; t++){
			if (kcScoreMax.get(t)>0.001){
				thresholdCompare += (testmax.get(t)/ kcScoreMax.get(t));
				counterT ++;
			}
//			System.out.println(testmax.get(t)/ kcScoreMax.get(t)+" , "+testmax.get(t)/decisionRanges.get(t).max);
		}
//		System.out.println(testmax.get(t)/ kcScoreMax.get(t));
		
		result2.add (thresholdCompare /counterT);
		result.add(result2);
/////////////////////////////////////////////////////////////////////

//		for (int i = 0; i < testuserscorelist.size(); i++) {
//			for (int t = 0; t < 24; t++) {
//				if (testuserscorelist.get(i).get(t).toDouble2()> testmax.get(t) && testuserscorelist.get(i).get(t).toDouble2()< 1.0){
//					testmax.set(t, testuserscorelist.get(i).get(t).toDouble2());
//				}
//			}
//		}

////////////////////////////////////////////////////////////////////// with two threshold
		ArrayList<Double> result3 = new ArrayList<Double>();
		Double tp3 = 0.0;
		Double fp3 = 0.0;
		Double tn3 = 0.0;
		Double fn3 = 0.0;
		Double up3 = 0.0;
		Double un3 = 0.0;
		Double badData3 = 0.0;
		
		for (int t = 0; t < 24; t++) {
			for (int i = 0; i < testscorelist.size(); i++) {
				if (testscorelist.get(i).get(t).toDouble2() >= decisionRanges.get(t).max){//decisionThreshold.get(t)) {// kcScoreMax.get(t)){ //decisionThreshold.get(t)) {
					tp3++;
				}else if(testscorelist.get(i).get(t).toDouble2() < decisionRanges.get(t).min){
					fn3++;
				}else{
					up3++;
				}
			}
		}
		for (int i = 0; i < testuserscorelist.size(); i++) {
			for (int t = 0; t < 24; t++) {
				if (testuserscorelist.get(i).get(t).toDouble2() < 1.0){
					if (testuserscorelist.get(i).get(t).toDouble2() <= decisionRanges.get(t).min){//decisionThreshold.get(t)) {//kcScoreMax.get(t)){ //decisionThreshold.get(t)) {
						tn3++;
					} else if (testuserscorelist.get(i).get(t).toDouble2() > decisionRanges.get(t).max){
						fp3++;
					}else {
						un3++;
					}
				}else{
					badData3 ++;
				}
			}
		}
		result3.add(tp3 / (tp3 + fn3 + up3));
		result3.add(fp3 / (fp3 + tn3 + un3 + badData3));
//		result3.add(tp3);
//		result3.add(fn3);
//		result3.add(fp3);
//		result3.add(tn3);
		result3.add(up3 / (tp3 + fn3 + up3));
		
		result3.add((un3 + badData3) / (fp3 + tn3 + un3 + badData3));
		result.add(result3);
/////////////////////////////////////////////////////////////////////////////////////////
		ArrayList<Double> testmax2 = new ArrayList<Double>();
		for (int t = 0; t<24; t++){
			testmax2.add(decisionRanges.get(t).max);
		}
		ArrayList<Double> result4 = new ArrayList<Double>();
		Double tp4 = 0.0;
		Double fp4 = 0.0;
		Double tn4 = 0.0;
		Double fn4 = 0.0;
		Double up4 = 0.0;
		Double un4 = 0.0;
		Double badData4 = 0.0;
		
		Double tp42 = 0.0;
		Double fn42 = 0.0;
		Double up42 = 0.0;
		for (int t = 0; t < 24; t++) {
			for (int i = 0; i < testscorelist.size(); i++) {
				if (testscorelist.get(i).get(t).toDouble2() >= testmax2.get(t)){
					tp4++;
				}else if(testscorelist.get(i).get(t).toDouble2() < decisionRanges.get(t).min){
					fn4++;
				}else{
					up4++;
				}
			}
		}
		for (int i = 0; i < testuserscorelist.size(); i++) {
			for (int t = 0; t < 24; t++) {
				if (testuserscorelist.get(i).get(t).toDouble2() < 1.0){
					if (testuserscorelist.get(i).get(t).toDouble2() <=   decisionRanges.get(t).min){
						tn4++;
					} else if (testuserscorelist.get(i).get(t).toDouble2() > testmax2.get(t)){
						testmax2.set(t, testuserscorelist.get(i).get(t).toDouble2());
						fp4++;
					}else {
						un4++;
					}
				}else{
					badData4 ++;
				}
			}
		}
		for (int t = 0; t < 24; t++) {
			for (int i = 0; i < testscorelist.size(); i++) {
				if (testscorelist.get(i).get(t).toDouble2() >= testmax2.get(t)){
					tp42++;
				}else if(testscorelist.get(i).get(t).toDouble2() < decisionRanges.get(t).min){
					fn42++;
				}else{
					up42++;
				}
			}
		}
		tp4 = (tp4+tp42)/2;
//		fn4 = (fn4+fn42)/2;
//		up4 = (up4+up42)/2;
		result4.add(tp4 / (tp4 + fn4 + up4));
		result4.add(fp4 / (fp4 + tn4 + un4 + badData4));
//		result4.add(tp4);
//		result4.add(fn4);
//		result4.add(fp4);
//		result4.add(tn4);
		result4.add(up4 / (tp4 + fn4 + up4));
		result4.add((un4 + badData4) / (fp4 + tn4 + un4 + badData4));	
		
		result4.add(tp42 / (tp4 + fn4 + up4));
		result4.add(tp22 / (tp2 + fn2));
			
		
		double thresholdCompare4 = 0.0;
		double counterT4 = 0.0;
		for (int t = 0; t<24; t++){
			if (decisionRanges.get(t).max >0.001){
				thresholdCompare4 += (testmax2.get(t)/decisionRanges.get(t).max);
				counterT4 ++;
			}
//			System.out.println(testmax.get(t)/ kcScoreMax.get(t)+" , "+testmax.get(t)/decisionRanges.get(t).max);
			
		}
//		System.out.println(testmax.get(t)/ kcScoreMax.get(t));
		
		result4.add (thresholdCompare4 /counterT4);
		result.add(result4);
/////////////////////////////////////////////////////////////////////////////////////////
		result.add(rocList1);
		result.add(rocList2);
		result.add(rocList3);
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
//		ArrayList<PatternScore> resultGS1 = new ArrayList<PatternScore>();
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
	

	public static ArrayList<Roc> GiveTPRandFPRforROC(ArrayList<Rank> a, int u, int o){
		Collections.sort(a);
		ArrayList<Roc> tprAndfpr = new ArrayList<Roc>();
		int[] counter = new int[10];
		double[] scores = new double[10];
		for (int i = 0; i<10; i++){
			counter[i]=0;
			scores[i]=0.0;
			
		}
		
		double tpr;
		double fpr;
		for (int i =1; i < a.size();i++){
			tpr = new Double(0.0);
			fpr = new Double(0.0);
			if (a.get(i-1).identity != a.get(i).identity){
				for (int k = 0; k< i; k++){
					if (a.get(k).identity == 0){
						tpr ++;
					}else{
						fpr++;
					}
				}
				tpr = tpr/o;
				fpr = fpr/u;
//				for (int j = i; j<a.size();j++){
//					if (a.get(j).identity == 1){
//						fpr ++;
//					}
//				}
//				System.out.println("tpr:	"+tpr/o+"	fpr:	"+fpr/u+ "	other	"+o+"	tp	"+tpr+ "	user	"+u+"	fp	"+fpr);
//				tprAndfpr.add(tpr/o);
//				tprAndfpr.add(fpr/u);
//				System.out.println("befor "+tpr+" , "+fpr);
				if (fpr<=0.1) {scores[0] += tpr; counter[0]++;}
				else if (fpr<=0.2) {scores[1] += tpr; counter[1]++;}
				else if (fpr<=0.3) {scores[2] += tpr; counter[2]++;}
				else if (fpr<=0.4) {scores[3] += tpr; counter[3]++;}
				else if (fpr<=0.5) {scores[4] += tpr; counter[4]++;}
				else if (fpr<=0.6) {scores[5] += tpr; counter[5]++;}
				else if (fpr<=0.7) {scores[6] += tpr; counter[6]++;}
				else if (fpr<=0.8) {scores[7] += tpr; counter[7]++;}
				else if (fpr<=0.9) {scores[8] += tpr; counter[8]++;}
				else if (fpr<=1.0) {scores[9] += tpr; counter[9]++;}
//				if(((int)(fpr*10)) < 10){
//					scores[(int)(fpr*10)] += tpr;
//					counter[(int)(fpr*10)]++;
//				}else{
//					scores[9] += tpr;
//					counter[9]++;
//				}
			}
		}
//		System.out.println(" after ");
		for (int i = 0; i<10; i++){
//			System.out.println("inside: "+counter[i]+" , "+scores[i]);
			tprAndfpr.add(new Roc(counter[i] , (Double)scores[i]));			
		}
		
		return tprAndfpr;
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
class ResultsTandF implements Comparable<ResultsTandF>{
	 double tpr;
	 double fpr;
	public ResultsTandF(double t, double f){
		this.tpr = t;
		this.fpr = f;
	}
	@Override
	public int compareTo(ResultsTandF o) {
		return (int) (this.fpr*10000 - o.fpr*10000);
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
class Roc
{
	double counter;
	double scores;
	public Roc(){
		this.counter = 0.0;
		this.scores = 0.0;
	}
	public Roc(double c, double s){
		this.counter = c;
		this.scores = s;
	}
}
