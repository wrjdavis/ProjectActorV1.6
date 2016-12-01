package com.tedrasoft.templategoogle;



import java.util.ArrayList;

import com.tedrasoft.templategoogle.data.ConfigData;
import com.tedrasoft.templategoogle.json.JsonProcessor;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity for presenting the hint for one level
 * 
 * @author Dragos
 * 
 */
public class HintActivity extends Activity implements AppConstants {
	SharedPreferences settings;
	TextView hint1;
	TextView hint2;
	Button cancel;
	Button btnHint1;
	Button btnHint2;
	ArrayList<String> hints;
	int level;
	private ConfigData config;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hint);
		hint1 = (TextView) findViewById(R.id.txt_hint_1);
		hint2 = (TextView) findViewById(R.id.txt_hint_2);
		btnHint1 = (Button) findViewById(R.id.btn_hint_1);
		btnHint2 = (Button) findViewById(R.id.btn_hint_2);
		settings = getSharedPreferences(PREFS_NAME, 0);
		// read configuration
		config = JsonProcessor.readConfig(this, "levels");
		//load font for buttons
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
				"Font/game_font.ttf");
		level = getIntent().getExtras().getInt(HINT_LEVEL);
		//hints were sent via Intent.. retrieve them
		Intent i = getIntent();
		hints =((ArrayList<String>) i.getExtras().getSerializable(HINT_INTENT_KEY));
		if (getHintLevel() < level) {
			//reset index
			setHintIndex(0);
			//put new hint level
			setHintLevel(level);
			//first button enabled second disabled
			btnHint1.setEnabled(true);
			btnHint2.setEnabled(false);
			btnHint2.setBackgroundResource(R.drawable.button_shape_disabled);
		
		}else{
			//check what hint index 
			int index= getHintIndex();
			//if hint index=0 enable first
			if(index==0){
				btnHint1.setEnabled(true);
				btnHint1.setBackgroundResource(R.drawable.button_shape);
				btnHint2.setEnabled(false);
				btnHint2.setBackgroundResource(R.drawable.button_shape_disabled);
			}else if(index==1){
			//if hint level =1 enable second
				btnHint1.setEnabled(false);
				btnHint1.setBackgroundResource(R.drawable.button_shape_disabled);
				btnHint2.setEnabled(true);
				btnHint2.setBackgroundResource(R.drawable.button_shape);
				hint1.setText(getResources().getString(R.string.txt_hint) + " "
						+ (hints.get(0) != null ? hints.get(0) : ""));
			}else if(index==2){
			//if hint level =2 no button enabled
				btnHint1.setEnabled(false);
				btnHint1.setBackgroundResource(R.drawable.button_shape_disabled);
				btnHint2.setEnabled(false);
				btnHint2.setBackgroundResource(R.drawable.button_shape_disabled);
				hint1.setText(getResources().getString(R.string.txt_hint) + " "
						+ (hints.get(0) != null ? hints.get(0) : ""));
				hint2.setText(getResources().getString(R.string.txt_hint) + " "
						+ (hints.get(1) != null ? hints.get(1) : ""));
			}
		}
		
				
		cancel = (Button) findViewById(R.id.btn_hint_cancel);
		cancel.setTypeface(tf);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(HINT_RESULT_CANCEL);
				finish();
			}
		});
		
		
		btnHint1.setTypeface(tf);
		btnHint1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!config.isAllowNegative() && getMoney() < MONEY_HINT) {
					Toast.makeText(getContext(), "Not enough coins", Toast.LENGTH_SHORT)
							.show();
					return;
				}else{
				hint1.setText(getResources().getString(R.string.txt_hint) + " "
						+ (hints.get(0) != null ? hints.get(0) : ""));
				//
				btnHint1.setEnabled(false);
				btnHint1.setBackgroundResource(R.drawable.button_shape_disabled);
				btnHint2.setEnabled(true);
				btnHint2.setBackgroundResource(R.drawable.button_shape);
				setHintIndex(1);
				modifyMoney(-MONEY_HINT);
				}
			}
		});
	
		btnHint2.setTypeface(tf);
		btnHint2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//check if it has enough money
				if (!config.isAllowNegative() && getMoney() < MONEY_HINT) {
					Toast.makeText(getContext(), "Not enough coins", Toast.LENGTH_SHORT)
							.show();
					return;
				}else{
				hint2.setText(getResources().getString(R.string.txt_hint) + " "
						+ (hints.get(1) != null ? hints.get(1) : ""));
				btnHint2.setEnabled(false);
				btnHint2.setBackgroundResource(R.drawable.button_shape_disabled);
				setHintIndex(2);
				modifyMoney(-MONEY_HINT);
				}
			}
		});
		

	}

	@Override
	public void onBackPressed() {
		setResult(HINT_RESULT_CANCEL);
		finish();
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
	 * Retrieves hint level
	 * @return
	 */
	private int getHintLevel() {
		int hintLevel = settings.getInt(HINT_LEVEL_PROCESSED, 0);
		return hintLevel;
	}
	/**
	 * Get last index of hint displayed
	 * @return
	 */
	private int getHintIndex() {

		int hintLevel = settings.getInt(HINT_LAST_INDEX, 0);
		return hintLevel;
	}
	
	/**
	 * Get available coins amount
	 * 
	 * @return coins
	 */
	public int getMoney() {

		int money = settings.getInt(MONEY, 0);
		return money;
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
	/**
	 * Returns this activity
	 * @return HintActivity
	 */
	protected HintActivity getContext() {
		return this;
	}
}
