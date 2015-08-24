package loctionMining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataReader {
	public static Date convertDate(String date) {
	Date result = null;
	SimpleDateFormat formatter = new SimpleDateFormat(
			"dd-MMM-yyyy hh:mm:ss");
	try {
		result = formatter.parse(date);

	} catch (ParseException e) {
	}
	return result;
}
	public static UserLocationRawData read(String filename) {
		// System.out.println(filename);
		UserLocationRawData currentuser = new UserLocationRawData();
		DayOrderedRawData tempDayData = new DayOrderedRawData();
		BufferedReader br = null;
		try {
			File file = new File(filename);
			if (!file.exists())
				return currentuser;
			// System.out.println("printing file" + filename);
			br = new BufferedReader(new FileReader(file));
			String line;
			int lastDateData = -1;
			Calendar cal = Calendar.getInstance();
			while ((line = br.readLine()) != null) {
				String[] str = line.split("\t");
				if (!str[1].equals("00")) {
					Date tempD = convertDate(str[0]);
					if (tempD != null) {
						LocationRawData data = new LocationRawData(tempD,
								str[1]);
						cal.setTime(data.date);
						int currentDay = cal.get(Calendar.DAY_OF_YEAR);
						if (currentDay == lastDateData) {
							tempDayData.add(data);
						} else {
							tempDayData = new DayOrderedRawData();
							tempDayData.add(data);
							currentuser.add(tempDayData);
						}
						lastDateData = currentDay;
					}
				}
				// System.out.print(str[0]);
				// System.out.println(str[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
			}
		}
		return currentuser;
	}
}
