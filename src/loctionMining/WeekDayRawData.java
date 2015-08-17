package loctionMining;

import java.util.ArrayList;
import java.util.Calendar;
public class WeekDayRawData {

	ArrayList<UserLocationRawData> week = new ArrayList<UserLocationRawData>();
	public WeekDayRawData(UserLocationRawData all) {
		super();
		for(int i=0;i<8;i++){
			week.add(new UserLocationRawData());
		}
		Calendar cal = Calendar.getInstance();
		for (DayOrderedRawData day : all) {
			cal.setTime(day.get(0).date);
			int currentDay = cal.get(Calendar.DAY_OF_WEEK);
			week.get(currentDay).add(day);
		}
	}

}

