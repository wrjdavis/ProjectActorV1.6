package com.tedrasoft.templategoogle;



import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tedrasoft.templategoogle.data.ConfigData;
import com.tedrasoft.templategoogle.json.JsonProcessor;


import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


/**
 * Activity for choosing level. 
 * @author Dragos
 *
 */
public class LevelChooserActivity extends Activity implements AppConstants {
	private static final String TAG = "LevelChooserActivity";
	// configuration data loaded from file
	ConfigData config;
	// SHared preferences
	SharedPreferences settings;
	SharedPreferences shared;
	ScrollView scroll;
	TextView title;
	int activity=0;
	AdView adView ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level_chooser);
		
		settings = getSharedPreferences(PREFS_NAME, 0);
		shared = getSharedPreferences(PREFS_NAME_STATUSES, 0);
		try{
			activity=getIntent().getIntExtra("Activity", 0);
		}catch(Exception e){
			Log.e(TAG, " "+e.getMessage());
		}
		// read configuration
		config = JsonProcessor.readConfig(this, "levels");
		scroll=(ScrollView) findViewById(R.id.scrolllevels);
		LinearLayout layout=new LinearLayout(this);
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(params);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER);
		title=(TextView)findViewById(R.id.title_choose_level);	
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
				"Font/game_font.ttf");
		title.setTypeface(tf);
		LinearLayout layoutLevel;
		
		int additional=config.getAdditionalLevels();
		boolean isAdditionalUnlocked =getAdditionalUnlocked();
		int visibleLevels=isAdditionalUnlocked?config.getLevels().size():additional;
		for (int i=0;i<visibleLevels;i++){
			LinearLayout.LayoutParams paramsLevel=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			paramsLevel.setMargins(20, 5, 20, 5);
			layoutLevel=new LinearLayout(this);
			layoutLevel.setLayoutParams(paramsLevel);
			layoutLevel.setGravity(Gravity.CENTER);
			layoutLevel.setBackgroundResource(R.drawable.choose_level_shape);
			boolean isLocked=false;
			int levelDepended=0;
			if(config.getLevels().get(i).getLevelSpecific().getDependsOn()==-1&&config.getLevels().get(i).getId()>1){
				levelDepended=config.getLevels().get(i).getId()-1;
			}else if(config.getLevels().get(i).getLevelSpecific().getDependsOn()>0) {
				levelDepended=config.getLevels().get(i).getLevelSpecific().getDependsOn();
			}else{
				levelDepended=0;
			}
			
			if(levelDepended>0&&!getStatus(levelDepended).equals(getResources().getString(R.string.txt_completed))){
				//set txt locked
				isLocked=true;
			} 
			
			Button levelNumber=(Button)getLayoutInflater().inflate(R.layout.button_icon_level, null);
			levelNumber.setBackgroundResource(R.drawable.levelmain);
			levelNumber.setText(config.getLevels().get(i).getLevelText());
			LinearLayout.LayoutParams buttonParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			
			levelNumber.setLayoutParams(buttonParams);

			//status button
			Button levelStatus=(Button)getLayoutInflater().inflate(R.layout.button_level, null);
			//LinearLayout.LayoutParams statusParams=new LinearLayout.LayoutParams(250,LinearLayout.LayoutParams.WRAP_CONTENT);
			//levelStatus.setLayoutParams(statusParams);
			String status = getStatus(config.getLevels().get(i).getId());
			if(status.equalsIgnoreCase(getResources().getString(R.string.txt_not_completed))&&isLocked){
				levelStatus.setBackgroundResource(R.drawable.selector_button_locked);
				status= getResources().getString(R.string.txt_locked);
			}else{
				levelStatus.setBackgroundResource(R.drawable.selector_button);

			}

			levelStatus.setText((status.equalsIgnoreCase(getResources().getString(R.string.txt_not_completed))?getResources().getString(R.string.txt_available):status));

			levelStatus.setTag(i+1);
			if(!status.equalsIgnoreCase(getResources().getString(R.string.txt_locked))){
			levelStatus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setLevel((Integer)v.getTag());
					Intent intent = new Intent(getContext(), SceneActivity.class);
					startActivity(intent);
					
					finish();
				}
			});
			}
			layoutLevel.addView(levelNumber);
			layoutLevel.addView(levelStatus);
			layout.addView(layoutLevel);
		}
		scroll.addView(layout);
if (!(getItemPurchased())) {
try{
		adView = (AdView)this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
		  
		    .build();
		adView.loadAd(adRequest);
}catch(Exception e){
}}
	}

	private String getStatus(int id) {
		try{
			int status=shared.getInt(LEVEL+id, NOT_COMPLETED);
			String name="";
			if(status==AVAILABLE){
				name=getResources().getString(R.string.txt_available);
					
			}
			else if(status==COMPLETED){
				name= getResources().getString(R.string.txt_completed);
				
			}
			else if(status==NOT_COMPLETED){
				name= getResources().getString(R.string.txt_not_completed);
				
			}
			return name;
		}catch(Exception e){
			return getResources().getString(R.string.txt_not_completed);
		}

	}

	/**
	 * Sets next level in shared settings
	 * 
	 * @param level
	 */
	public void setLevel(int level) {
		// SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		Log.d(TAG, "setLevel "+level);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(LEVEL, level);
		editor.commit();
	}

	/**
	 * Get current level saved in shared settings
	 * 
	 * @return current level
	 */
	public int getLevel() {
		int level = settings.getInt(LEVEL, 1);
		return level;
	}
	private LevelChooserActivity getContext() {
		return this;
	}

	@Override
	protected void onStart() {

		super.onStart();
	}
	/**
	 * Are additional levels unlocked
	 * 
	 * @return current level
	 */
	public boolean getAdditionalUnlocked() {
		boolean unlocked = settings.getBoolean(ADDITIONAL_UNLOCKED, false);
		return unlocked;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		if(activity==SCENE_ACTIVITY){
			Intent intent = new Intent(getContext(),
					SceneActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		else{
			super.onBackPressed();
		}
//		Intent intent;
//		intent = new Intent(getContext(), MainActivity.class);
//		startActivity(intent);
//		finish();
	}
	
@Override
	public void onPause() {
		try{
			adView.pause();
		}catch(Exception e){
			Log.e(TAG," "+ e.getMessage());
		}
	  super.onPause();
	}

	@Override
	public void onResume() {
	  super.onResume();
	  try{
			adView.resume();
		}catch(Exception e){
			Log.e(TAG, " " +e.getMessage());
		}
	}
	@Override
	protected void onDestroy() {
		try{
			adView.destroy();
		}catch(Exception e){
			Log.e(TAG," "+ e.getMessage());
		}
		super.onDestroy();
		
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
    
}