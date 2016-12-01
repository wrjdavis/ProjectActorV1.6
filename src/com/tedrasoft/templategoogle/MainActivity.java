package com.tedrasoft.templategoogle;



import java.io.InputStream;
import java.util.Locale;

import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tedrasoft.templategoogle.data.ConfigData;
import com.tedrasoft.templategoogle.json.JsonProcessor;




import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * First Activity. Display current level and play button
 * 
 * @author Dragos
 * 
 */
@SuppressLint("NewApi")
public class MainActivity extends Activity implements AppConstants {
	private static final String TAG = "MainActivity";
	Button start;
	Button more;
	Button language;
	TextView mainLevel;
	TextView mainTitle;
	TextView mainVersion;
	Button levelIcon;
	LinearLayout levelLayout;
	
	SharedPreferences shared;
	SharedPreferences settings;
	Button toggle;
	ConfigData config;
	// chart boost
	private Chartboost cb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		settings = getSharedPreferences(PREFS_NAME, 0);
		shared = getSharedPreferences(PREFS_NAME_STATUSES, 0);
		SharedPreferences.Editor editor = settings.edit();
		config = JsonProcessor.readConfig(this, "levels");
			
			String appId = getResources().getString(R.string.appId_Chartboost);
			String appSignature = getResources().getString(R.string.appSignature_Chartboost);
			Chartboost.startWithAppId(getContext(), appId, appSignature);
			Chartboost.onStart(this);
			Chartboost.onCreate(getContext());
			// Notify the beginning of a user session
			
			Chartboost.cacheMoreApps(CBLocation.LOCATION_DEFAULT);
			
			
		
		
		
			
			
		
		onStart();
		setContentView(R.layout.activity_main);
		



		//check if all levels completed are marked 
		checkLevels();		
//check intent for extra coins 
		
		/*
		
		try{
			Log.d(TAG, "extra checking");
			int extra=intent.getIntExtra(EXTRA_COINS, 0);
			if(extra>0){
				Log.d(TAG, "extra found "+extra);
				//Log.d(TAG, "money " + modifyMoney(extra));
				
				 putAwardTime();
				increaseGiftsNumber();
				Log.d(TAG, "extra finish ");
			}
		}catch(Exception ex){
			Log.d(TAG, "Extra coins not found");
		}
		*/
		//Intent intent=getIntent();

		Log.d(TAG, "chartBoost for more buttons");
				//Configure Chartboost
				
				// *************************************************************/
		start = (Button) findViewById(R.id.main_play);
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
				"Font/game_font.ttf");
		start.setTypeface(tf);
		
		
		
		
		
		
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), SceneActivity.class);
				

				startActivity(intent);
				finish();
			}
		};
		start.setOnClickListener(listener);

		more = (Button) findViewById(R.id.main_more);

		more.setTypeface(tf);
		OnClickListener moreListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Then show the more apps page in the same way
				
					if(Chartboost.hasMoreApps(CBLocation.LOCATION_DEFAULT)){
						Log.d(TAG, "shows more apps");
						Chartboost.showMoreApps(CBLocation.LOCATION_DEFAULT);
					}else
						Log.d(TAG, "No more apps to show");
					
				
//				Intent main = new Intent(getContext(), InAppActivity.class);
//				startActivityForResult(main, INAPP_ACTIVITY);
			}
		};
		more.setOnClickListener(moreListener);

		mainLevel = (TextView) findViewById(R.id.txt_main_level);
		mainLevel.setTypeface(tf);
		mainTitle = (TextView) findViewById(R.id.txt_main_title);
		mainTitle.setTypeface(tf);
		levelIcon = (Button) findViewById(R.id.main_level_icon);
		levelIcon.setText("" + getLevel());
		
levelLayout=(LinearLayout)findViewById(R.id.layout_main_group_level);

		
		levelLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), LevelChooserActivity.class);
				startActivity(intent);
			
			}
		});
		levelIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), LevelChooserActivity.class);
				startActivity(intent);
			
			}
		});
		toggle=(Button)findViewById(R.id.togglebutton);
		
		if(settings.getBoolean(SOUND_SETTING, false))
		{
			toggle.setBackgroundResource(R.drawable.sound_on);
			
		}
		else{
			toggle.setBackgroundResource(R.drawable.sound_off);
			
		}
		
		toggle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(settings.getBoolean(SOUND_SETTING, false)){
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean(SOUND_SETTING, false);
					toggle.setBackgroundResource(R.drawable.sound_off);
					editor.commit();
				}else{
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean(SOUND_SETTING, true);
					toggle.setBackgroundResource(R.drawable.sound_on);
					editor.commit();
				}
			
			}
		});
		
	
		try{
			String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			mainVersion = (TextView) findViewById(R.id.txt_version);
			mainVersion.setText("Version "+versionName);
		}catch(Exception e1){
			Log.d(TAG, "No version Name");
		}
	}



	private void checkLevels() {
		//if levels are not updated
		if(!getLevelsUpdated()){
			if(getLevel()>1){
				//set all levels available
				for(int i=1;i<getLevel();i++){
					setStatus(i,  getResources().getString(R.string.txt_completed));
				}
			}
			setLevelsUpdated(true);
				
		}
		
	}
	
	/**
	 * Are levels completed updated
	 * 
	 * @return levelsUpdated
	 */
	public boolean getLevelsUpdated() {
		boolean levelsUpdated = settings.getBoolean(LEVELS_UPDATED, false);
		return levelsUpdated;
	}
	
	/**
	 * Sets levels are updated in settings
	 * 
	 * @param updated
	 */
	public void setLevelsUpdated(boolean updated) {
		// SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(LEVELS_UPDATED, updated);
		editor.commit();
	}
	private MainActivity getContext() {
		return this;
	}
	/**
	 * Get level from shared settings
	 * @return current level
	 */
	public int getLevel() {
		int level = settings.getInt(LEVEL, 1);
		int maxLevel = settings.getInt(NO_LEVELS, -1);
		if (maxLevel < 0) {
			return level;
		} else {
			if (level > maxLevel) {
				level = 1;
			}
		}
		return level;
	}

	@Override
	protected void onStart() {
		super.onStart();
		Chartboost.onStart(getContext());
		
		//this.cb.onStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		this.cb.onStop(this);
	}

	  /**
		 * Updates available coins
		 * 
		 * @param value
		 * @return coins
		 */
		public int modifyMoney(int value) {

			int money = settings.getInt(MONEY, 0);
			money = money + value;
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(MONEY, money);
			editor.commit();

			return money;
		}


	@Override
	public void onBackPressed() {
		// If an interstitial is on screen, close it. Otherwise continue as normal.
	//	if (this.cb.onBackPressed())
	//		return;
	//	else 
			finish();
	}
@Override
	public void onPause() {

	  super.onPause();
	}

	@Override
	public void onResume() {
	  super.onResume();
	  
	}
	@Override
	protected void onDestroy() {

		super.onDestroy();
		this.cb.onDestroy(this);
		unbindDrawables(findViewById(R.id.rootView));
		System.gc();
	}
	/**
	 * Not used for now but useful if any bitmap based drawable will be added
	 * @param view
	 */
	private void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	public void onToggleClicked(View view) {
		// Is the toggle on?
		boolean on = ((ToggleButton) view).isChecked();

		if (on) {
			// Enable sound
			SharedPreferences.Editor editor=settings.edit();
			editor.putBoolean(SOUND_SETTING, true);
			editor.commit();
			toggle.setBackgroundResource(R.drawable.sound_on);
		
			
			
		} else {
			// Disable sound
			SharedPreferences.Editor editor=settings.edit();
			editor.putBoolean(SOUND_SETTING, false);
			editor.commit();
			toggle.setBackgroundResource(R.drawable.sound_off);
		}
	}
	
	/**
	 * Has user bought any items? If yes then you can remove adds.
	 * 
	 * @return current level
	 */
	public boolean getItemPurchased() {
		// always false if you don't implement inapp purchases and notify by
		// putting true in settings
		boolean purchased = settings.getBoolean(ITEM_PURCHASED, false);
		return purchased;
		// return false;// if you still want to display ads even if the user has
		// bought in-app items
	}
	//Config level status after update
	private void updateStatus(){
		int i;
		Log.d(TAG, "update status");
		SharedPreferences.Editor editor = shared.edit();
		// read configuration
				
		
		for(i=1;i<config.getNoLevels();i++){
			String status=shared.getString(LEVEL+i, getResources().getString(R.string.txt_not_completed));
			if(status.equals("Completed")){
				editor.putInt(LEVEL+i, COMPLETED);
			}else if(status.equals("Not completed")){
				editor.putInt(LEVEL+i, NOT_COMPLETED);	
		} else if(status.equals("Available")){
			editor.putInt(LEVEL+i, AVAILABLE);
		
	}editor.commit();
	}
	}
	/**
	 * Set status for level - true or false = completed or not
	 * @param id
	 */
	private void setStatus(int id,String status) {
		try {
			SharedPreferences.Editor editor = shared.edit();
			if(status.equals(getResources().getString(R.string.txt_available))){
				editor.putInt(LEVEL + id, AVAILABLE);	
			}
			else if(status.equals(getResources().getString(R.string.txt_not_completed))){
				editor.putInt(LEVEL + id, NOT_COMPLETED);	
			}
			else if(status.equals(getResources().getString(R.string.txt_completed))){
				editor.putInt(LEVEL + id, COMPLETED);	
			}
			

			editor.commit();
		} catch (Exception e) {
			Log.e(TAG,""+ e.getMessage(), e);
		}
	}
	private Integer getStatus(int id) {
		
			int status=shared.getInt(LEVEL+id, NOT_COMPLETED);
			String name="";
			if(status==AVAILABLE){
				return status;
					
			}
			else if(status==COMPLETED){
				return status;
				
			}
			else if(status==NOT_COMPLETED){
				return status;
				
			}
			return 1;
		
		

	}
}
