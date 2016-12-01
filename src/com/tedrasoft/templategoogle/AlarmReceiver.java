package com.tedrasoft.templategoogle;



import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver implements AppConstants {
	private static final String TAG = "AlarmReceiver";
	SharedPreferences settings;
    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.d(TAG, "alarm intercepted ");
    	boolean process=false;
//    	if(intent!=null){
//    		Log.d(TAG, "Intent "+intent.toString());
//    		try{
//    			process=intent.getBooleanExtra("QUIZ", false);
//    		}catch(Exception e){
//    			Log.e(TAG, e.getMessage());
//    		}
//    	}
//    	if(process){
    	settings = context.getSharedPreferences(PREFS_NAME,  0);
    	//get current level
    	int savedInfoLevel=settings.getInt(INFO_LEVEL, 0);
    	int money = settings.getInt(MONEY, 0);
    	
    	if(money<GIFT_LIMIT){
    
    	NotificationCompat.Builder mBuilder =
    		    new NotificationCompat.Builder(context)
    		    .setSmallIcon(R.drawable.icon)
    		    .setContentTitle(context.getResources().getString(R.string.txt_coins_awarded))
    		    .setContentText(context.getResources().getString(R.string.text1_notification)+savedInfoLevel+context.getResources().getString(R.string.text2_notification)+COINS_AWARDED+context.getResources().getString(R.string.text3_notification)).setAutoCancel(true);
    	//update money
    	int printMoney = modifyMoney(COINS_AWARDED);
    
    	Log.d(TAG, "Giving "+COINS_AWARDED+" coins to total "+printMoney);
    	
    	Intent resultIntent = new Intent(context, MainActivity.class);
    	//resultIntent.putExtra(EXTRA_COINS, COINS_AWARDED);
    	// Because clicking the notification opens a new ("special") activity, there's
    	// no need to create an artificial back stack.
    	PendingIntent resultPendingIntent =
    	    PendingIntent.getActivity(
    	    context,
    	    0,
    	    resultIntent,
    	    PendingIntent.FLAG_UPDATE_CURRENT
    	);
    	
    	mBuilder.setContentIntent(resultPendingIntent);
    	// Sets an ID for the notification
    	int mNotificationId = 001;
    	// Gets an instance of the NotificationManager service
try{
    	NotificationManager mNotifyMgr = 
    	        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	// Builds the notification and issues it.
    	mNotifyMgr.notify(mNotificationId, mBuilder.build());

    	}catch(Exception e){
    		Log.e(TAG, ""+e.getMessage(),e);
    	}
    	}
//    	}
    }
	
    
    /**
	 * Updates available coins
	 * 
	 * @param value
	 * @return coins
	 */
	public int modifyMoney(int value) {

		int money = settings.getInt(MONEY, 0);
		int number = settings.getInt(INFO_GIFTS_NUMBER, 0);
		money = money + value;
		number++;
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(MONEY, money);
		editor.putLong(AWARD_DATE, System.currentTimeMillis());
		editor.putInt(INFO_GIFTS_NUMBER, number);
		editor.commit();

		return money;
	}
	
  
}
