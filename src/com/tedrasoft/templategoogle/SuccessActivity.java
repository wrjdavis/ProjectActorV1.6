package com.tedrasoft.templategoogle;



 
import com.tedrasoft.templategoogle.data.ConfigData;
import com.tedrasoft.templategoogle.json.JsonProcessor;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * Success activity. Displays guessed word and coins gained
 * 
 * @author Dragos
 * 
 */
public class SuccessActivity extends Activity implements AppConstants {
	private static final String TAG = "SuccessActivity";
	Button choose;
	Button next;
	SharedPreferences settings;
	int level;
	boolean showMoney=true;
	private ConfigData config;
	private AppRate appRate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_success);
		settings = getSharedPreferences(PREFS_NAME, 0);
		config = JsonProcessor.readConfig(this, "levels");
		String word = getIntent().getExtras().getString(SUCCESS_WORD);
		level = getIntent().getExtras().getInt(SUCCESS_LEVEL);
		try{
if(level%7==0&&level%3!=0){
			if(isNetworkAvailable()){
				if(!getIsRated()){
					//starts rating dialog immediate (0 to min days, 0 to min launches)
					appRate=new AppRate(getContext());
					appRate.setMinDaysUntilPrompt(0)
						.setMinLaunchesUntilPrompt(0)
						.init();
					
				}
			}else{
				Log.d(TAG, "No network available");
			}
		}
		}catch (Exception e){
			
		}
		try{
			showMoney =getIntent().getExtras().getBoolean(SHOW_MONEY);
		}catch(Exception e){
			showMoney=true;
		}
		Log.d(TAG, "Show money "+showMoney);
		TextView tv = (TextView) findViewById(R.id.success_word);
		TextView tvfound = (TextView) findViewById(R.id.success_found);
		TextView tvcoins = (TextView) findViewById(R.id.success_coins);
		next = (Button) findViewById(R.id.btn_success_next);
		choose = (Button) findViewById(R.id.btn_success_choose);


		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
				"Font/game_font.ttf");

		next.setTypeface(tf);
		choose.setTypeface(tf);
		choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(appRate!=null){
					try{
						appRate.dismissDialog();
					}catch(Exception e){
						Log.e(TAG, ""+e.getMessage());
					}
				}
				Intent intent = new Intent(getContext(), LevelChooserActivity.class);
				intent.putExtra("Activity", SCENE_ACTIVITY);
				startActivity(intent);
				finish();
			
			}
		});
		if (level >= getLevelsNumber()||(!getAdditionalUnlocked()&&level>=config.getAdditionalLevels())) {
			//TODO additional levels 
			
			// done.. reset counter
			tv.setVisibility(View.INVISIBLE);
			tvfound.setText(R.string.txt_success_message1);
			tvcoins.setText(getResources().getString(
					R.string.txt_success_message2)
					+ " " + getMoney());
			next.setText("Restart");
			next.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(appRate!=null){
						try{
							appRate.dismissDialog();
						}catch(Exception e){
Log.e(TAG, ""+e.getMessage());
						}
					}
					// start main activity 
					setLevel(1);
					//reset index
					setHintIndex(0);
					//reset hint level
					setHintLevel(0);
					//resetSavedValues
					resetSavedValues();
					Intent intent = new Intent(getContext(), MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				}
			});

		} else {

			tv.setText(word);
			if(!showMoney){
				tvcoins.setVisibility(View.INVISIBLE);
				RelativeLayout.LayoutParams layoutParams=(LayoutParams) tvcoins.getLayoutParams();
				layoutParams.height=1;
				layoutParams.width=2;
				tvcoins.setLayoutParams(layoutParams);
			}
			next.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// start scene activity for next level
					Intent intent = new Intent(getContext(),
							SceneActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
finish();
				}
			});
		}
	}

	public SuccessActivity getContext() {

		return this;
	}
  /**
   * Get total number of levels
   * @return levels number
   */
	private int getLevelsNumber() {
		int levels = settings.getInt(NO_LEVELS, -1);
		return levels;

	}
	/**
	 * Set next level
	 * @param level
	 */
	public void setLevel(int level) {

		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(LEVEL, level);
		editor.commit();
		
	}
	/**
	 * Retrieves current coins amount
	 * @return coins number
	 */
	public int getMoney() {

		int money = settings.getInt(MONEY, 0);
		return money;
	}

	@Override
	public void onBackPressed() {
		if(appRate!=null){
			try{
				appRate.dismissDialog();
			}catch(Exception e){
Log.e(TAG, ""+e.getMessage());
			}
		}
		if (level >= getLevelsNumber()||(!getAdditionalUnlocked()&&level>=config.getAdditionalLevels())) {

			setLevel(1);
			//reset index
			setHintIndex(0);
			//reset hint level
			setHintLevel(0);
			//resetSavedValues
			resetSavedValues();
			Intent intent = new Intent(getContext(), MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
finish();

		} else {
			
				// start activity levels
				Intent intent = new Intent(getContext(),
						SceneActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
finish();
			
		}

	}
	
	/**
	 * Sets hint level to remember for which one hint was already paid for
	 * @param level
	 */
	public void setHintLevel(int level) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(HINT_LEVEL_PROCESSED, level);
		editor.commit();
	}
	
	/**
	 * Sets hint index 
	 * @param level
	 */
	public void setHintIndex(int index) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(HINT_LAST_INDEX, index);
		editor.commit();
	}
	
	/**
	 * Saves removed letters and revealed letters
	 * 
	 */
	private void resetSavedValues() {
		SharedPreferences.Editor editor = settings.edit();
	
		editor.putString(REMOVED_LETTERS, "");
		editor.putString(REVEALED_LETTERS, "");
		editor.putInt(SAVED_LEVEL, -1);
		editor.commit();
	}
	
	public boolean getAdditionalUnlocked() {
		boolean unlocked = settings.getBoolean(ADDITIONAL_UNLOCKED, false);
		return unlocked;
	}
	/**
	 * Page has been rated, save this status in order to avoid showing the dialog again, update coins amount and disable button
	 */
	public void setIsRated() {
		
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(APP_IS_RATED, true);
		editor.commit();
	
	}
	
	/**
	 * Check if Rate button has been clicked already
	 * @return
	 */
	protected boolean getIsRated() {
		return settings.getBoolean(APP_IS_RATED, false);
		
	}
	
	/**
	 * Checks if network is available
	 * @return boolean
	 */
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	@Override
	protected void onDestroy() {
		if(appRate!=null){
			try{
				appRate.dismissDialog();
			}catch(Exception e){
Log.e(TAG, ""+e.getMessage());
			}
		}
		super.onDestroy();
	}
	
	
}
