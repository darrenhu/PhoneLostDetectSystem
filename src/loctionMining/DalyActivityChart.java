package loctionMining;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DalyActivityChart {
	ArrayList<Date> time;
	ArrayList<Double> frequency;
	
	public DalyActivityChart() {
		super();
		this.time = new ArrayList<Date>();
		this.frequency = new ArrayList<Double>();
		Calendar calDYC = Calendar.getInstance();
		try {
			calDYC.setTime(new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").parse("2000_01_01_00_00_00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0;i<1440;i++){
			calDYC.add(Calendar.MINUTE, 1);
			time.add(calDYC.getTime());
			frequency.add(0.0);
		}
	}
	@Override
	public String toString() {
		return time+" "+frequency;
	}
}
