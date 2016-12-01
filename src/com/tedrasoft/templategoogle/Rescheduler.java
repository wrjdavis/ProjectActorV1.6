package com.tedrasoft.templategoogle;

import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class Rescheduler extends BroadcastReceiver implements AppConstants {
	private static final String TAG = "Rescheduler";
	SharedPreferences settings;
    @Override
    public void onReceive(Context context, Intent intent) {
    	settings = context.getSharedPreferences(PREFS_NAME, 0);
		long timeToRun = settings.getLong(INFO_TIME, 0);
		long awardDate = settings.getLong(AWARD_DATE, 0);
		Log.d(TAG, "timeToRun "+timeToRun);
		if(timeToRun>0&&timeToRun>awardDate){
		//reset Alarm 
			int number = settings.getInt(INFO_GIFTS_NUMBER, 0);
			if(number>2){
				//number;
			}else{
				number=1;
			}
			Log.d(TAG, "Setting alarm for level "+INFO_LEVEL+" multiple for gifts already awarded: "+number+" to go off at:" +new Date(timeToRun));
			Intent it = new Intent(context, AlarmReceiver.class);
			//intent.setAction("com.tedrasoft.quiz.ALARMACTION");
			intent.putExtra("QUIZ", true);
			PendingIntent pi = PendingIntent.getBroadcast(context,ALARM_ID, it, 0);
		
			AlarmManager am = (AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
			am.set(AlarmManager.RTC_WAKEUP, timeToRun,pi);//1000*60*60*24*number,pi);
			Log.d(TAG, "Alarm set!");
			
		}
    }
}