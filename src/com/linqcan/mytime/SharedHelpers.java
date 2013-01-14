package com.linqcan.mytime;

import java.util.Locale;


public class SharedHelpers {
	
	/**
	 * Returns the duration of an activity in milliseconds
	 * 
	 * @param activity
	 * @return
	 */
	public static long getDuration(TimeActivity activity){
		long duration = activity.getEnd_date().getTime() - activity.getStart_date().getTime();
		return duration;
	}
	
	/**
	 * Returns a formated string with the duration in hours and minutes
	 * Example: "8 hours, 40 minutes"
	 * 
	 * @param duration
	 * @return
	 */
	public static String durationToString(long duration){
		
		int seconds = (int) ((duration / 1000) % 60 );
		int minutes = (int) ((duration / (1000*60)) % 60);
		int hours   = (int) ((duration / (1000*60*60)));
		String strHour = "hours";
		String strMin = "minutes";
		// The following is ugly, but needed...
		if(hours == 1){
			strHour = "hour";
		}
		if(minutes == 1){
			strMin = "minute";
		}
		String durationString = String.format(Locale.getDefault(),"%d %s, %d %s", hours, strHour, minutes, strMin);
		return durationString;		
	}
	
	/**
	 * Returns a formated string with the duration of an 
	 * time activity in hours and minutes
	 * Example: "8 hours, 40 minutes"
	 *
	 * @param activity
	 * @return
	 */
	public static String getDurationAsString(TimeActivity activity){
		long duration = activity.getDuration();
		return SharedHelpers.durationToString(duration);
	}

}
