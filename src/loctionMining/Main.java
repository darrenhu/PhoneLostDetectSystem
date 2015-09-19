package loctionMining;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Main {

	public static int gapSize = 5;
	public static int patternSize = 3;
	public static int patternThreshold = 5;
	public static ArrayList<Double> decisionThreshold = new ArrayList<Double>();

	public static void main(String[] arges) {
		// /////////////////////////////////Reading
		ArrayList<UserLocationRawData> allUsersRawData = new ArrayList<UserLocationRawData>();
		ArrayList<WeekDayRawData> allUsersWeekRawData = new ArrayList<WeekDayRawData>();
		for (int i = 2; i <= 106; i++) {
			String filename = "F:\\locs\\locs_" + i + ".txt";
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
		 for (int userIndex = 0; userIndex < allUsersWeekRawData.size();
		 userIndex++) {
		 testLocation(allUsersWeekRawData, userIndex, Calendar.MONDAY);
		 }
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

	// //////////////////////////////////////////// location system
	// /////////////////////////////////////////////////////////////////
	public static void testLocation(
			ArrayList<WeekDayRawData> allUsersWeekRawData, int userIndex,
			int day) {
		// ////////////////////////////////split train, v, test data.
		UserLocationRawData training = new UserLocationRawData();
		UserLocationRawData validation = new UserLocationRawData();
		UserLocationRawData test = new UserLocationRawData();
		for (int i = 0; i < (allUsersWeekRawData.get(userIndex).week.get(day)
				.size() - 3); i++) {
			training.add(allUsersWeekRawData.get(userIndex).week.get(day)
					.get(i));
		}
		for (int j = 0; j < allUsersWeekRawData.size(); j++) {
			validation.add(allUsersWeekRawData.get(j).week.get(day).get(
					allUsersWeekRawData.get(j).week.get(day).size() - 2));
			test.add(allUsersWeekRawData.get(j).week.get(day).get(
					allUsersWeekRawData.get(j).week.get(day).size() - 1));
		}
		// ///////////////// transform raw data to BitSet,and mine all data
		MinePattern miner = new MinePattern();

		ArrayList<LocationDecisionModel> valModels = new ArrayList<LocationDecisionModel>();
		for (DayOrderedRawData tempVal : validation) {
			valModels.add(miner.minePattern(tempVal));
		}

		ArrayList<LocationDecisionModel> testModels = new ArrayList<LocationDecisionModel>();
		for (DayOrderedRawData tempTest : test) {
			testModels.add(miner.minePattern(tempTest));
		}

		ArrayList<LocationDecisionModel> trainModels = new ArrayList<LocationDecisionModel>();
		LocationDecisionModel trainModel = new LocationDecisionModel();
		for (DayOrderedRawData eachDay : training) {
			trainModels.add(miner.minePattern(eachDay));
			trainModel = MinePattern.mergeDecisionModel(trainModel,
					trainModels.get(trainModels.size() - 1));
		}
		// ///////////////////////////////////////////////////////////////evenly
		// set pattern's frequency//////////////////////
		trainModels.add(valModels.get(userIndex));
		trainModel = MinePattern.mergeDecisionModel(trainModel,
				valModels.get(userIndex));
		// System.out.println(valModels.size());
		valModels.remove(userIndex);
		// System.out.println(valModels.size());
		// System.out.println("*********************************");

		LocationDecisionModel evenlyTrainModel = new LocationDecisionModel();
		evenlyTrainModel = evenlySetSeq(trainModel, trainModels.size() + 0.0);
		//
		// /////////////////////////////////////// K
		// crossover///////////////////////////////
		// System.out.println("........................." + trainModels.size());
		ArrayList<ArrayList<Double>> kcScoreList = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> kcScore = new ArrayList<Double>();
		for (int i = 0; i < trainModels.size(); i++) {
			LocationDecisionModel tempKC = new LocationDecisionModel();
			for (int j = 0; j < trainModels.size(); j++) {
				if (i != j) {
					tempKC = MinePattern.mergeDecisionModel(tempKC,
							trainModels.get(j));
				}
			}
			ArrayList<Double> tempList = new ArrayList<Double>();
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
		for (int i = 0; i < 22; i++) {
			Double sum = new Double(0.0);
			for (int j = 0; j < kcScoreList.size(); j++) {
				sum += kcScoreList.get(j).get(i);
				// System.out.println(i + " , " + " score "+
				// kcScoreList.get(j).get(i));
			}
			// System.out.println((sum / kcScoreList.size()));
			kcScore.add(sum / kcScoreList.size());
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

		// //////////////////// set decision threshold
		// /////////////////////////////////////
		decisionThreshold = new ArrayList<Double>();
		// ArrayList<ArrayList<Double>> valscorelist = getScoreList(valModels,
		// trainModel);
		ArrayList<ArrayList<Double>> valscorelist = getScoreList(valModels,
				evenlyTrainModel);

		Double closestHigherScore;
		Double closestLowerScore;
		for (int t = 0; t < 22; t++) {
			// closestHigherScore = 0.0;
			// for (int i = 0; i < valscorelist.size(); i++) {
			// if (closestHigherScore < valscorelist.get(i).get(t)
			// && kcScore.get(t) > valscorelist.get(i).get(t)) {
			// closestHigherScore = valscorelist.get(i).get(t);
			// }
			// }

			closestLowerScore = 9999999999.0;
			for (int i = 0; i < valscorelist.size(); i++) {
				if (closestLowerScore > valscorelist.get(i).get(t)
						&& kcScore.get(t) < valscorelist.get(i).get(t)) {
					closestLowerScore = valscorelist.get(i).get(t);
				}
			}
			if (closestLowerScore == 9999999999.0) {
				closestLowerScore = kcScore.get(t);
			}
			// System.out.println(valscorelist.get(userIndex).get(t));
			// System.out.println(valscorelist.get(userIndex).get(t)+",,,,,,,,,"+closestHigherScore);
			decisionThreshold.add((kcScore.get(t) + closestLowerScore) / 2);
			// System.out.println(t+"   ,   "+decisionThreshold.size()+"   ,   "+decisionThreshold.get(t));
		}

		// System.out.println(decisionThreshold);
		// ////////////////////give all score.
		// ArrayList<ArrayList<Double>> testscorelist = getScoreList(testModels,
		// trainModel);
		ArrayList<ArrayList<Double>> testscorelist = getScoreList(testModels,
				evenlyTrainModel);
		// /////////////////////////////////////////////////////////////////////
		// ArrayList<Double> valSort;
		// ArrayList<Double> testSort;
		// for (int t = 6; t < 19; t++) {
		// valSort = new ArrayList<Double>();
		// testSort = new ArrayList<Double>();
		// for (int i = 0; i < valscorelist.size(); i++) {
		// valSort.add(valscorelist.get(i).get(t));
		// testSort.add(testscorelist.get(i).get(t));
		// }testSort.add(testscorelist.get(valscorelist.size()).get(t));
		//
		// //valSort.sort(null);
		// //testSort.sort(null);
		// System.out.println("---------------"+ t +"----------------");
		// System.out.println("Threshold :	" + decisionThreshold.get(t));
		// System.out.println("Valation :" + kcScore.get(t));
		// System.out.println(valSort);
		// System.out.println("Test :" + testscorelist.get(userIndex).get(t));
		// System.out.println(testSort);
		// System.out.println("-------------------------------");
		// System.out.println("-------------------------------");
		// }
		// ////////////////////////////////////////////////////////////////////

		Double tp = 0.0;
		Double fp = 0.0;
		Double tn = 0.0;
		Double fn = 0.0;
		for (int t = 0; t < 22; t++) {
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
		// System.out.println("TPR:	" + tp / (tp + fn) + "	FPR:	" + fp / (fp +
		// tn)
		// + "	F1:	" + 2 * tp / (2 * tp + fp + fn) + " Tp: " + tp
		// + " Fn: " + fn + " Fp: " + fp + " Tn: " + tn);
		// System.out.println("FPR:"+ fp /(fp+tn));
		// System.out.println("F1:"+ 2*tp /(2*tp+fp+fn));
		// System.out.println(tp + "	" + fn);
		// System.out.println(fp + "	" + tn);
		Double tp1 = 0.0;
		Double fp1 = 0.0;
		Double tn1 = 0.0;
		Double fn1 = 0.0;
		for (int t = 0; t < 22; t++) {
			if (testscorelist.get(userIndex).get(t) != 6.0) {
				for (int i = 0; i < testscorelist.size(); i++) {
					// System.out.print(testscorelist.get(i));
					if (i == userIndex) {
						// System.out.print("this is user it's self");
						if (testscorelist.get(i).get(t) <= decisionThreshold
								.get(t)) {
							tn1++;
						} else {
							fp1++;
						}
					} else {
						if (testscorelist.get(i).get(t) > decisionThreshold
								.get(t)) {
							tp1++;
						} else {
							fn1++;
						}
					}
					// System.out.println(" "+(testscorelist.get(i) >=
					// decisionThreshold?"T":"F"));
				}
			}
		}
		// System.out.println("Accurcy:"+ (tn+tp) / testscorelist.size());
		System.out.println("TPR:	" + tp / (tp + fn) + "	,	" + tp1 / (tp1 + fn1)
				+ "	FPR:	" + fp / (fp + tn) + "	,	" + fp1 / (fp1 + tn1)
				+ "	Tp: " + tp + " Fn: " + fn + " Fp: " + fp + " Tn: " + tn
				+ " Tp1: " + tp1 + " Fn1: " + fn1 + " Fp1: " + fp1 + " Tn1: "
				+ tn1);
		// System.out.println("//////////////////////////////////////////////////");
	}

	public static ArrayList<ArrayList<Double>> getScoreList(
			ArrayList<LocationDecisionModel> Models,
			LocationDecisionModel trainModel) {
		ArrayList<ArrayList<Double>> scorelist = new ArrayList<ArrayList<Double>>();

		for (LocationDecisionModel valModel : Models) {
			scorelist.add(giveScore(trainModel, valModel));
		}
		return scorelist;
	}

	/**
	 * @return abc
	 */
	public static ArrayList<Double> giveScore(LocationDecisionModel a,
			LocationDecisionModel b) {
		ArrayList<Double> resultGS = new ArrayList<Double>();
		ArrayList<Double> resultGS1 = new ArrayList<Double>();
		// double retrunS=0.0;
		for (int r = 0; r < 24; r++) {
			// PatternTable tempGSS = new PatternTable();
			// tempGSS =
			// MinePattern.mergePatternTable(MinePattern.mergePatternTable(a.hourModel.get(r),
			// a.hourModelShift.get(r)), a.hourModelShift
			// .get(r + 1));
			// resultGS.add(givePatternScore(tempGSS, b.hourModel.get(r)));
			// System.out.println(tempGSS.patternMap.size()+" ++++ "+b.hourModel.get(r).patternMap.size());
			resultGS.add(givePatternScore(MinePattern.mergePatternTable(
					MinePattern.mergePatternTable(a.hourModel.get(r),
							a.hourModelShift.get(r)), a.hourModelShift
							.get(r + 1)), b.hourModel.get(r)));
			// resultGS.add(givePatternScore(a.oneDayModel.get(r / 6),
			// b.hourModel.get(r))
			// + givePatternScore(MinePattern.mergePatternTable(
			// MinePattern.mergePatternTable(a.hourModel.get(r),
			// a.hourModelShift.get(r)), a.hourModelShift
			// .get(r + 1)), b.hourModel.get(r)));
			// retrunS += resultGS.get(r);
		}
		
		for (int r = 2; r < 24; r++) {
			resultGS1.add((3 * resultGS.get(r)) + (2 * resultGS.get(r - 1))+ resultGS.get(r - 2));
		}
		return resultGS1;
	}

	// public static double givePatternScore(PatternTable training, PatternTable
	// b) {
	// double score = 0;
	// for (String patternB : b.patternMap.keySet()) {
	// if (training.patternMap.containsKey(patternB)) {
	// score += training.patternMap.get(patternB).frequency;
	// // score ++;
	// }
	// }
	// return score; // / b.patternMap.size();
	// }

	// public static double givePatternScore(PatternTable training, PatternTable
	// b) {
	// double score = 0.0;
	// for (String patternTraining : training.patternMap.keySet()) {
	// if (b.patternMap.containsKey(patternTraining)) {
	// score += (training.patternMap.get(patternTraining).frequency / (Math
	// .abs(training.patternMap.get(patternTraining).frequency
	// - b.patternMap.get(patternTraining).frequency) + 1));
	// }
	// // } else {
	// // score +=
	// //
	// (training.patternMap.get(patternTraining).frequency/(training.patternMap.get(patternTraining).frequency
	// // +1));
	// // }
	// }
	// return score; // / b.patternMap.size();
	// }
	// public static double givePatternScore(PatternTable training, PatternTable
	// b) {
	// Double score = new Double(0.0);
	// Double sumOfTraining = new Double(0.0);
	// Double sumOfB = new Double(0.0);
	// for (Pattern x : training.patternMap.values()) {
	// sumOfTraining += x.frequency;
	// }
	// for (Pattern x : b.patternMap.values()) {
	// sumOfB += x.frequency;
	// }
	// for (String patternTraining : training.patternMap.keySet()) {
	// if (b.patternMap.containsKey(patternTraining)) {
	// score +=
	// (Math.abs((training.patternMap.get(patternTraining).frequency/sumOfTraining)
	// - (b.patternMap.get(patternTraining).frequency/sumOfB)));
	// } else {
	// score +=
	// (training.patternMap.get(patternTraining).frequency/sumOfTraining);
	// }
	// }
	// for (String patternB : b.patternMap.keySet()) {
	// if (!training.patternMap.containsKey(patternB)) {
	// score += (b.patternMap.get(patternB).frequency/sumOfB);
	// }
	// }
	// // System.out.println("training: "+training.patternMap.size()+" , "+
	// sumOfTraining+" TEST: "+b.patternMap.size()+" , "+sumOfB+" Score: "+score/2);
	// if (b.patternMap.size() == 0){return 1;}
	// else{return score/2;}
	// }
	public static double givePatternScore(PatternTable training, PatternTable b) {
		Double score = new Double(0.0);
		Double sumOfPattern = new Double(0.0);
		for (Pattern x : training.patternMap.values()) {
			sumOfPattern += x.frequency;
		}
		for (Pattern x : b.patternMap.values()) {
			sumOfPattern += x.frequency;
		}
		for (String patternTraining : training.patternMap.keySet()) {
			if (b.patternMap.containsKey(patternTraining)) {
				score += (Math
						.abs(training.patternMap.get(patternTraining).frequency
								- b.patternMap.get(patternTraining).frequency));
			} else {
				score += training.patternMap.get(patternTraining).frequency;
			}
		}
		for (String patternB : b.patternMap.keySet()) {
			if (!training.patternMap.containsKey(patternB)) {
				score += b.patternMap.get(patternB).frequency;
			}
		}
		// System.out.println("training: "+training.patternMap.size()+"  test: "+
		// b.patternMap.size()+" score: "+score);
		// System.out.println(sumOfPattern+" , "+training.patternMap.size()+" , "+b.patternMap.size()+" , "+score/sumOfPattern);
		return score / sumOfPattern;
	}

	// public static double givePatternScore(PatternTable training, PatternTable
	// b) {
	// double score = 0.0;
	// for (String patternTraining : training.patternMap.keySet()) {
	// if (b.patternMap.containsKey(patternTraining)){
	// score += (Math.abs(training.patternMap.get(patternTraining).frequency -
	// b.patternMap.get(patternTraining).frequency));
	// }else {
	// score += training.patternMap.get(patternTraining).frequency;
	// }
	// }
	// for (String patternB : b.patternMap.keySet()) {
	// if (!training.patternMap.containsKey(patternB)){
	// score += b.patternMap.get(patternB).frequency;
	// }
	// }
	// // System.out.println("training: "+training.patternMap.size()+"  test: "+
	// b.patternMap.size()+" score: "+score);
	// return score;
	// }
	// public static double givePatternScore(PatternTable training, PatternTable
	// b) {
	// double score = 0;
	// for (String patternTraining : training.patternMap.keySet()) {
	// if (b.patternMap.containsKey(patternTraining)) {
	// score += Math.abs((training.patternMap.get(patternTraining).frequency -
	// b.patternMap
	// .get(patternTraining).frequency));
	// } else {
	// score += training.patternMap.get(patternTraining).frequency;
	// }
	// } //
	// b.patternMap.size();
	// return score; // / b.patternMap.size();
	// }
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
		return evenlyModel;
	}
}
