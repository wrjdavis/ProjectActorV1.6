package com.tedrasoft.templategoogle;



import java.util.Date;

import com.tedrasoft.templategoogle.data.ConfigData;
import com.tedrasoft.templategoogle.json.JsonProcessor;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
/**
 * Options Activity for buying correct character or deleting up to 3 incorrect characters or showing a hint
 * @author Dutzu
 *
 */
public class OptionsActivity extends Activity implements AppConstants{
	Button delete;
	Button reveal;
	Button skip;
	Button cancel;
	Button hint;
	SharedPreferences settings;
	// configuration data loaded from file
	ConfigData config;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = getSharedPreferences(PREFS_NAME, 0);
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "Font/game_font.ttf");
		setContentView(R.layout.activity_options);
		delete=(Button) findViewById(R.id.btn_options_delete);
		
		delete.setTypeface(tf);
		delete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				setResult(OPTIONS_RESULT_DELETE);
				finish();
			}});
		reveal=(Button) findViewById(R.id.btn_options_reveal);
		reveal.setTypeface(tf);
		reveal.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				setResult(OPTIONS_RESULT_REVEAL);
				finish();
			}});
		skip=(Button) findViewById(R.id.btn_options_skip);
		skip.setTypeface(tf);
		skip.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				if(getSkipDate()<((new Date()).getTime()-24*60*60*1000)){
					setSkipDate();
					setResult(OPTIONS_RESULT_SKIP);
					finish();
				}else{
					Toast.makeText(getContext(), "Already skipped one level in last 24 hours", Toast.LENGTH_SHORT).show();
				}
			}});
		
		config = JsonProcessor.readConfig(this, "levels");
		
		hint=(Button) findViewById(R.id.btn_options_hint);
		if(config.isShowHints()){
		hint.setTypeface(tf);
		hint.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				setResult(OPTIONS_RESULT_HINT);
				finish();
			}});
		}else{
			hint.setVisibility(View.INVISIBLE);
			LinearLayout ll=(LinearLayout)findViewById(R.id.layout_hint);
                        ll.getLayoutParams().height=0;
			ll.setVisibility(View.INVISIBLE);
		}
		cancel=(Button) findViewById(R.id.btn_options_cancel);
		cancel.setTypeface(tf);
		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				setResult(OPTIONS_RESULT_CANCEL);
				finish();
			}});
	}

	private OptionsActivity getContext() {
		
		return this;
	}

	/**
	 * Sets skip date 
	 * @param level
	 */
	public void setSkipDate() {
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(SKIP_DATE, (new Date()).getTime());
		editor.commit();
	}
	
	/**
	 * Get skip date 
	 * @param level
	 */
	public long getSkipDate() {
		return settings.getLong(SKIP_DATE, 0);
	}
	
}
