package loctionMining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

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
			String filename = "F:\\locs_" + i + ".txt";
			UserLocationRawData user = DataReader.read(filename);
			if (!user.isEmpty() && user.size() > 40) {
				System.out.println(filename + " " + user.size());
				allUsersRawData.add(user);
				WeekDayRawData tempWeekData = new WeekDayRawData(user);
				allUsersWeekRawData.add(tempWeekData);
			}
		}
		// System.out.println(allUsersRawData.size());
		// System.out.println(allUsersWeekRawData.size());
		// Calendar cal = Calendar.getInstance();
		for (int userIndex = 0; userIndex < allUsersWeekRawData.size(); userIndex++) {
			testOneUser(allUsersWeekRawData, userIndex, Calendar.MONDAY);
		}
		System.out.println("--the end--");

	}

	public static void testOneUser(
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
		LocationDecisionModel trainModel = new LocationDecisionModel();
		MinePattern miner = new MinePattern();
		for (DayOrderedRawData eachDay : training) {
			trainModel = MinePattern.mergeDecisionModel(trainModel,
					miner.minePattern(eachDay));
		}
		// for (Pattern eachPattern : trainPatternTable.patternMap.values()) {
		// eachPattern.frequency /= training.size();
		// }
		// System.out.println(patternTable.toString());

		ArrayList<LocationDecisionModel> valModels = new ArrayList<LocationDecisionModel>();
		for (DayOrderedRawData tempVal : validation) {
			valModels.add(miner.minePattern(tempVal));
		}

		ArrayList<LocationDecisionModel> testModels = new ArrayList<LocationDecisionModel>();
		for (DayOrderedRawData tempTest : test) {
			testModels.add(miner.minePattern(tempTest));
		}
		// ////////////////set decision threshold
		ArrayList<ArrayList<Double>> valscorelist = getScoreList(valModels,
				trainModel);
		Double closestHigherScore;
		for (int t = 0; t < 24; t++) {
			closestHigherScore = 9999999999999.0;
			for (int i = 0; i < valscorelist.size(); i++) {
				if (closestHigherScore > valscorelist.get(i).get(t)
						&& valscorelist.get(userIndex).get(t) < valscorelist
								.get(i).get(t)) {
					closestHigherScore = valscorelist.get(i).get(t);
				}
			}
	//		System.out.println(valscorelist.get(userIndex).get(t));
			decisionThreshold
					.add((valscorelist.get(userIndex).get(t) + closestHigherScore) / 2);
		}
		// System.out.println(decisionThreshold);
		// ////////////////////give all score.
		ArrayList<ArrayList<Double>> testscorelist = getScoreList(testModels,
				trainModel);
		Double tp = 0.0;
		Double fp = 0.0;
		Double tn = 0.0;
		Double fn = 0.0;
		for (int t = 0; t < 24; t++) {
			for (int i = 0; i < testscorelist.size(); i++) {
				// System.out.print(testscorelist.get(i));
				if (i == userIndex) {
					// System.out.print("this is user it's self");
					if (testscorelist.get(i).get(t) >= decisionThreshold.get(t)) {
						tn++;
					} else {
						fp++;
					}
				} else {
					if (testscorelist.get(i).get(t) < decisionThreshold.get(t)) {
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
				+ "	F1:	" + 2 * tp / (2 * tp + fp + fn));
		// System.out.println("FPR:"+ fp /(fp+tn));
		// System.out.println("F1:"+ 2*tp /(2*tp+fp+fn));
		System.out.println(tp + "	" + fn);
		System.out.println(fp + "	" + tn);
		System.out
				.println("//////////////////////////////////////////////////");
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

	public static ArrayList<Double> giveScore(LocationDecisionModel a,
			LocationDecisionModel b) {
		ArrayList<Double> resultGS = new ArrayList<Double>();
		// double retrunS=0.0;
		for (int r = 0; r < 24; r++) {
			resultGS.add(givePatternScore(a.oneDayModel.get(r / 6),
					b.hourModel.get(r))
					+ givePatternScore(MinePattern.mergePatternTable(
							MinePattern.mergePatternTable(a.hourModel.get(r),
									a.hourModelShift.get(r)), a.hourModelShift
									.get(r + 1)), b.hourModel.get(r)));
			// retrunS += resultGS.get(r);
		}
		return resultGS;
	}

	public static double givePatternScore(PatternTable training, PatternTable b) {
		double score = 0;
		for (String patternB : b.patternMap.keySet()) {
			if (training.patternMap.containsKey(patternB)) {
				score += training.patternMap.get(patternB).frequency;
				// score ++;
			}
		}
		return score; // / b.patternMap.size();
	}

	// public static double giveScore(PatternTable training, PatternTable b) {
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
}
