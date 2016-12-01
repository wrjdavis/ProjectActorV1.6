package com.tedrasoft.templategoogle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;




import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


import com.tedrasoft.templategoogle.data.ConfigData;
import com.tedrasoft.templategoogle.data.InitialSizeData;
import com.tedrasoft.templategoogle.data.LevelData;
import com.tedrasoft.templategoogle.data.LevelSpecific;
import com.tedrasoft.templategoogle.json.JsonProcessor;


import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.chartboost.sdk.*;


/**
 * Scene activity for displaying one level. It tracks facebook wall post and ads clicked for awarding coins. 
 * Coins are given for facebook status posted or for ads clicked ( once per day) as there is no way to know 
 * if an application has been downloaded.
 * 
 * @author Dragos
 *   
 */ 
public class SceneActivity extends Activity implements AppConstants {
	private static final String TAG = "SceneActivity";
	//image view
	ImageView imageView;
	//box to enclose images
	LinearLayout box;
	
	
	Button[] bWord;
	// array of buttons for letters pool from which to choose word's characters
	Button[] bPool;
	// configuration data loaded from file
	ConfigData config;
	// level specific data for current level
	LevelSpecific ls;
	// characters currently chosen by user
	String[] attempt;

	int filled = 0;
	// temporary padding info
	int[] padding = new int[4];
	// mapping between selected character pool buttons and word letter buttons
	HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>();
	// counter for views id
	int idCount = 1000;
	// resizer manager - in case images or buttons must be resized to better
	// fill the screen space
	ObjectsResizeManager orm;
	// current level data info
	LevelData ld;
	// hashmap for correct letters revealed using coins
	HashMap<Integer, Integer> revealed = new HashMap<Integer, Integer>();
	// SHared preferences
	SharedPreferences settings;
	
	SharedPreferences shared;
	// various view elements
	TextView moneyText;
	Button level;
	Button back;
	Button displayType;
	boolean finished=false;
	boolean itemPurchased = false;
	// handler for passing to success activity after delay
	Handler mHandler = new Handler();
	// array of positions for deleted letters saved when application is
	// restarted
	ArrayList<Integer> removedLetters;
	// hashmap for correct letters revealed using coins to saved when
	// application is restarted
	HashMap<Integer, Integer> revealedLetters = new HashMap<Integer, Integer>();
	// last level for saved data
	int savedLevel;
	WebDialog feedDialog;
	Button facebookButton;
	LinearLayout coinsLayout;
	LinearLayout sceneTextLayout;
	TextView txtPicture;
	// Parameters of a WebDialog that should be displayed
    private WebDialog dialog = null;
    private String dialogAction = null;
    private Bundle dialogParams = null;
	boolean isClicked = false;
	boolean isAchievement=false;
	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	// chartboost variable
	private Chartboost cb;

	private MediaPlayer mp;
	private InterstitialAd mInterstitial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scene);
		
		// *************facebook connector
		// initialization************************
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback,
						savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this)
						.setCallback(statusCallback));
			}
		}
		// **************** just for facebook KeyHash checking in development
		// phase
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getApplicationContext().getPackageName(),
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}

		} catch (NameNotFoundException ex) {
		} catch (NoSuchAlgorithmException ex2) {
		}
		// *********************************************************
		// coins layout 
		coinsLayout = (LinearLayout) findViewById(R.id.layout_money);
		coinsLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent main = new Intent(getContext(), InAppActivity.class);
				startActivityForResult(main, INAPP_ACTIVITY);

			}
		});

		// *********************************************************
		//************************* text for picture (question)****
		sceneTextLayout = (LinearLayout) findViewById(R.id.scene_layout_text);
		txtPicture = (TextView) findViewById(R.id.scene_txt_picture);
		
		//**********************************************************
		settings = getSharedPreferences(PREFS_NAME,  0);
		shared = getSharedPreferences(PREFS_NAME_STATUSES, 0);
		
		Log.d(TAG,"money="+getMoney());
		// read configuration
		config = JsonProcessor.readConfig(this, "levels");
		// levels number 
		int lvlNumber = setLevelsNumbers(config.getNoLevels()); 
		int currentLevel = getLevel();
                //if level is not completed  then put it as available
		if(!getStatus(currentLevel).equalsIgnoreCase(getResources().getString(R.string.txt_completed))){
				setStatus(getLevel(), getResources().getString(R.string.txt_available));
		}
		//*******************info for giving coins if user is blocked on a level**************************/
		int savedInfoLevel=settings.getInt(INFO_LEVEL, 0);
		Log.d(TAG, "savedInfoLevel "+savedInfoLevel+"" +
				"money="+getMoney()); 
		if(savedInfoLevel!=currentLevel&&currentLevel>1){
			savedInfoLevel=currentLevel;
			long dateAlarm=System.currentTimeMillis();
			SharedPreferences.Editor editor = settings.edit();
			//reset Alarm 
			int number = settings.getInt(INFO_GIFTS_NUMBER, 0);
			if(number>2){ 
				number=2; 
			}else{
				number=1;
			}
			//set new level
			editor.putInt(INFO_LEVEL, savedInfoLevel);
			//set new time 
			editor.putLong(INFO_TIME, dateAlarm+1000*60*60*23*number);
			editor.commit();
			
			
			Log.d(TAG, "Setting alarm for level "+INFO_LEVEL+" multiple for gifts already awarded: "+number+" to go off at:" +new Date(dateAlarm+1000*60*60*23*number));//dateAlarm+1000*60*60*23*number));
			Intent intent = new Intent(this, AlarmReceiver.class);
			//intent.setAction("com.tedrasoft.quiz.ALARMACTION");
			intent.putExtra(getResources().getString(R.string.app_name), true);
			PendingIntent pi = PendingIntent.getBroadcast(this,ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager am = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
			am.set(AlarmManager.RTC_WAKEUP, dateAlarm+1000*60*60*23*number,pi);
			Calendar cal=Calendar.getInstance();
			cal.setTimeInMillis(dateAlarm+1000*60*60*23*number);
			
			Log.d(TAG, "Alarm set for !"+cal.toString());
			
			
		}
		
		//******************end info for level duration***************************************************/
		
		Log.d(TAG, "current level:" + currentLevel+" nr levels:"+lvlNumber);
		if (currentLevel > lvlNumber) {
			// reset level
			currentLevel = lvlNumber;
		}
		ld = config.getLevels().get(currentLevel - 1);
 
		// every 3 levels display an ad - when %3=0 and %6!=0 revmob , when %3=0
		// and %6=0 chartBoost
		Log.d(TAG, "interstitials shown so far "+SessionValues.getInstance().getInterstitialsShown());
		
		
    	String appId = getResources().getString(R.string.appId_Chartboost);
    	String appSignature = getResources().getString(R.string.appSignature_Chartboost);
    	Chartboost.startWithAppId(this, appId, appSignature);
    	Chartboost.onStart(this);
    	Chartboost.onCreate(getContext());
		if (!(itemPurchased = getItemPurchased())&&SessionValues.getInstance().getInterstitialsShown()<4) {
        	

			if (currentLevel % 3 == 0 ) {
				// Configure Chartboost
            	
				 mInterstitial = new InterstitialAd(this);
			        mInterstitial.setAdUnitId(getResources().getString(R.string.admob_unit_id));
			     // Set the AdListener.
			        mInterstitial.setAdListener(new AdListener() {
			          @Override
			          public void onAdLoaded() {
			            Log.d(TAG, "onAdmob AdLoaded");
			            if (mInterstitial.isLoaded()) {
			            	try{
			            	mInterstitial.show();
			            	SessionValues.getInstance().incrementInterstitialsShown();
			        		Log.d(TAG,"interstitials shown :"+SessionValues.getInstance().getInterstitialsShown());
			            	}catch(Exception admobException){
			            		Log.d(TAG, ""+admobException.getMessage());
			            	}
			              } else {
			                Log.d(TAG, "Interstitial ad was not ready to be shown.");
			                //chartboost
			             // Starting Chartboost session for fullscreen anyway
			                try{
			                	
			        			

			        				if (!Chartboost.hasInterstitial(CBLocation.LOCATION_DEFAULT)) {
			        					Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);

			        				}
			        				// Show an interstitial
			        				Chartboost.showInterstitial(CBLocation.LOCATION_DEFAULT);
			                }catch(Exception chartboostException){
			                	Log.d(TAG, ""+chartboostException.getMessage());
			                }
					   
			              }
			          }
			          

			          @Override
			          public void onAdFailedToLoad(int errorCode) {
			        	     //chartboost
				             // Starting Chartboost session for fullscreen anyway
			        	  try{
		        			

			        		  if (!Chartboost.hasInterstitial(CBLocation.LOCATION_DEFAULT)) {
		        					Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);

		        				}
		        				// Show an interstitial
		        				Chartboost.showInterstitial(CBLocation.LOCATION_DEFAULT);
		                }catch(Exception chartboostException){
		                	Log.d(TAG, ""+chartboostException.getMessage());
		                }
			          }
			          
			        });

			        AdRequest adRequest = new AdRequest.Builder().build();
			        // Load the interstitial ad.
			        mInterstitial.loadAd(adRequest);
							
			
			}
			
			

		}

		// set back button listener
		back = (Button) findViewById(R.id.scene_back);
		if (back != null) {
			back.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent main = new Intent(getContext(), MainActivity.class);
					main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(main);
					finish();

				}
			});
		}
		// set level on button
		level = (Button) findViewById(R.id.scene_level);
		level.setText("" + currentLevel);

		if (!isMoneyInitialized()) {
			initializeMoney(MONEY_INITIAL_SUM);
		}
		// set money text view
		moneyText = (TextView) findViewById(R.id.scene_money);
		moneyText.setText("" + getMoney());
		
		
		// load image text view
		imageView = (ImageView) findViewById(R.id.image_view);
		box = (LinearLayout) findViewById(R.id.box);
		
		// create resizer manager
		createObjectResizerManager();

		
		
		// set in resizer initial data for boxes and action bar sizes
		orm.setBoxInitialSize(box.getLayoutParams().height);
		orm.setBoxSize(box.getLayoutParams().height);
			
		//hidden display type tells what kind of display we have
		displayType = (Button) findViewById(R.id.display_type);

	
		Log.d(TAG, orm.toString());
		
		// load preferred initial sizes depending on screen type
		InitialSizeData isd;
		if (displayType.getText().toString().equalsIgnoreCase("xlarge")) {
			// extra large
			isd = JsonProcessor.readSize(this, "xlarge");
		} else if (displayType.getText().toString().equalsIgnoreCase("small")) {
			// screen small
			isd = JsonProcessor.readSize(this, "small");
		} else if (displayType.getText().toString().equalsIgnoreCase("large")) {
			// large
			isd = JsonProcessor.readSize(this, "large");
		} else {
			Log.d(TAG, "normal size");
			// normal screen
			isd = JsonProcessor.readSize(this, "normal");
		}
		Log.d(TAG, isd.toString());
		ls = ld.getLevelSpecific();
		
		if(ls.getTextPicture()==null||ls.getTextPicture().trim().length()==0){
			//do not show layout for text
			LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0, 0);
			sceneTextLayout.setLayoutParams(params);
		}else{
			//put question in text view
			txtPicture.setText(ls.getTextPicture());
		}
		//orm.MAXIMUM_WORD=ls.getWord().length();
		orm.calculateNewSizes(isd);
		Log.d(TAG, " Box size final :"+orm.getBoxSize());
		
		attempt = new String[ls.getWord().length()];
		bWord = new Button[ls.getWord().length()];
		bPool = new Button[(ls.getCharPool(config.getAlphabet())).length];
		 
		String image = ld.getLevelSpecific().getImage();
		try {
		
				Bitmap bm;
				bm = getBitmapFromAsset("images" + File.separator + image.trim());
				imageView.setImageBitmap(bm);
				
				LayoutParams params = box.getLayoutParams();
				// Changes the height and width to the specified *pixels*
				params.height = orm.getBoxSize();
				params.width = orm.getBoxSize();
				box.setLayoutParams(params);
				TextView path = (TextView) findViewById(R.id.txt_scene_adress);
				path.setText(ls.getPath());
				if(ls.getPath().trim().length()==0){
					path.setVisibility(View.INVISIBLE);
				}
		
		} catch (IOException e) {
			Log.e(TAG,"Exception", e);
		}
		
		// draw word buttons

		RelativeLayout wordLayout = (RelativeLayout) findViewById(R.id.wordlayout);
		String[] word = ld.getLevelSpecific().getProcessedWord();
		for (int i = 0; i < word.length; i++) {
			Button temp = new Button(this);
			temp.setText("");
			temp.setTypeface(null, Typeface.BOLD);
			temp.setTextSize(TypedValue.COMPLEX_UNIT_SP, isd.getWordTextSize());
			temp.setIncludeFontPadding(false);
			temp.setId(idCount * 2 + i); 
			temp.setTag(Integer.valueOf(i));
			temp.setBackgroundResource(R.drawable.button_shape_letter);
			RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
					(int) (isd.getWordButtonSize() * orm.getScaleRatio()-2),
(int) (isd.getWordButtonSize() * orm.getScaleRatio()+6*orm.getScaleRatio()));


			if (i > 0) {
				p.addRule(RelativeLayout.RIGHT_OF, idCount * 2 + i - 1);
			} else {
				p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			}

			p.setMargins(isd.getExtraspaceWord()+1, isd.getExtraspaceWord(),
					isd.getExtraspaceWord()+1, isd.getExtraspaceWord());
			temp.setLayoutParams(p);
			bWord[i] = temp;
			temp.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					onWordPressed((Integer) v.getTag());

				}
			});
			wordLayout.addView(temp);
		}
		
		// wordLayout.addView(facebookButton);

		// draw letter pool buttons
		RelativeLayout poolLayout = (RelativeLayout) findViewById(R.id.poollayout);
		idCount = poolLayout.getId() + 1000;
		String[] pool = ld.getLevelSpecific().getCharPool(config.getAlphabet());
		for (int i = 0; i < pool.length; i++) {
			Button temp = new Button(this);

			temp.setText("" + pool[i]);
			temp.setTypeface(null, Typeface.BOLD);
			temp.setTextSize(TypedValue.COMPLEX_UNIT_SP, isd.getPoolTextSize());
			temp.setIncludeFontPadding(false);
			temp.setBackgroundResource(R.drawable.button_shape_letter);
			temp.setId(idCount + i);
			temp.setTag(Integer.valueOf(i));
			Log.d(TAG,
					" Pool size " + isd.getPoolButtonSize() + "*"
							+ orm.getScaleRatio() + "="
							+ isd.getPoolButtonSize() * orm.getScaleRatio());
			RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
					(int) (isd.getPoolButtonSize() * orm.getScaleRatio()-2),
					(int) (isd.getPoolButtonSize() * orm.getScaleRatio()+6*orm.getScaleRatio()));
			if (i > (AppConstants.ROW_POOL_SIZE-2)) {
				p.addRule(RelativeLayout.BELOW, idCount + i % (AppConstants.ROW_POOL_SIZE-1));
				if (i % (AppConstants.ROW_POOL_SIZE-1) > 0) {
					p.addRule(RelativeLayout.RIGHT_OF, idCount + i - 1);
				}
			} else {
				if (i > 0) {
					p.addRule(RelativeLayout.RIGHT_OF, idCount + i - 1);
				} else {
					p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				}
			}

			p.setMargins(isd.getExtraspace(), isd.getExtraspace(),
					isd.getExtraspace(), isd.getExtraspace());
			temp.setLayoutParams(p);
			temp.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					onPoolPressed((Integer) v.getTag());

				}
			});
			bPool[i] = temp;

			poolLayout.addView(temp);
		}
		//
		// add facebook button
				facebookButton = new Button(this);
				facebookButton.setId(idCount +pool.length+10);
				facebookButton.setTypeface(null, Typeface.BOLD);
				facebookButton.setTextColor(getResources().getColor(
						R.color.txt_color_white));
				facebookButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, isd.getPoolTextSize()+2);
				facebookButton.setIncludeFontPadding(false);
				facebookButton.setText("f");
				facebookButton.setTag(Integer.valueOf(word.length));
				facebookButton.setBackgroundResource(R.drawable.button_shape_facebook);
				RelativeLayout.LayoutParams pfacebook = new RelativeLayout.LayoutParams(
						(int) (isd.getPoolButtonSize() * orm.getScaleRatio()-2),
						(int) (isd.getPoolButtonSize() * orm.getScaleRatio()+6*orm.getScaleRatio()));

				pfacebook.addRule(RelativeLayout.RIGHT_OF, idCount + (AppConstants.ROW_POOL_SIZE-2));

				pfacebook.setMargins(isd.getExtraspace(),

						isd.getExtraspace(), isd.getExtraspace(),
						isd.getExtraspace());
				facebookButton.setLayoutParams(pfacebook);

				facebookButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						onClickLogin();
						isClicked = true;

					}
				});
				if(getResources().getString(R.string.useFacebook).equalsIgnoreCase("false")){
					pfacebook = new RelativeLayout.LayoutParams(0,0);
					facebookButton.setLayoutParams(pfacebook);
				}

				poolLayout.addView(facebookButton);
		// add extra button for coins options
		ImageButton options = new ImageButton(this);
		options.setBackgroundResource(R.drawable.button_shape_letter);
		options.setImageResource(R.drawable.bulbshine);
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
				(int) (isd.getPoolButtonSize() * orm.getScaleRatio()-2),
				(int) (isd.getPoolButtonSize() * orm.getScaleRatio()+6*orm.getScaleRatio()));
		
	if(getResources().getString(R.string.useFacebook).equalsIgnoreCase("false")){
			
			p.addRule(RelativeLayout.RIGHT_OF, idCount +(AppConstants.ROW_POOL_SIZE-2));
		}else{
		p.addRule(RelativeLayout.BELOW, idCount +pool.length+10);
		p.addRule(RelativeLayout.RIGHT_OF, idCount + (2*(AppConstants.ROW_POOL_SIZE-1)-1));
}
		p.setMargins(isd.getExtraspace(), isd.getExtraspace(),
				isd.getExtraspace(), isd.getExtraspace());
		options.setLayoutParams(p);
		options.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getContext(), OptionsActivity.class);
				startActivityForResult(intent, OPTIONS_ACTIVITY);

			}
		});

		poolLayout.addView(options);

		// if application was restarted and revealed and removed letters are
		// null or empty
		// check if there are any saved data for this level and make adjustments
		if (attempt != null) {
			boolean check = true;
			for (int i = 0; i < attempt.length; i++) {
				if (attempt[i] != null) {
					check = false;
					break;
				}
			}
			if (check) {
				getSavedValues();
				
				if (savedLevel == currentLevel) {
					// iterate removed
				//	if (removedLetters != null) {
				//		for (int i = 0; i < removedLetters.size(); i++) {
				//			deleteCharacter(removedLetters.get(i));
				//		}
				//	}
					
					HashMap<Integer, Integer> newRevealedLetters =new HashMap<Integer, Integer>();
					// iterate revealed
					if (revealedLetters != null) {
						Collection<Integer> values = revealedLetters.values();

						Iterator<Integer> it = values.iterator();
						Integer temp;
						
						while (it.hasNext()) {
							temp = it.next();
if(temp<word.length){
							//find first pool position with the same value and add it to newRevealedLetters
							String value= word[temp];
							int i=0;
							while(i<pool.length){
								if(value.equalsIgnoreCase(pool[i])&&!newRevealedLetters.containsKey(i)){
									newRevealedLetters.put(i, temp);
									deleteCharacter(i);
									break;
								}
								i++;
							}
						}
}
						
						revealedLetters = newRevealedLetters;
						it = revealedLetters.keySet().iterator();
						
						while (it.hasNext()) {
							temp = it.next();
							selectCharacter(temp, revealedLetters.get(temp),
									false);
						}
					}
				} else {
					// clear revealed and removed
					removedLetters = null;
					revealedLetters = null;
				}
			}
		}
		
		
		///************* publish achievement on facebook**************//
		
	if((getResources().getString(R.string.useFacebook).equalsIgnoreCase("true"))&&(currentLevel==20||currentLevel==50||currentLevel==100)&&!getAchievementSaved()){
			// 1. Instantiate an AlertDialog.Builder with its constructor
			//AlertDialog.Builder builder = new AlertDialog.Builder(this);
			//LayoutInflater inflater = getLayoutInflater();

		    // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
		 //  builder.setView(inflater.inflate(R.layout.achievement, null));
			// 2. Chain together various setter methods to set the dialog characteristics
			//builder.setMessage("You have reached level "+currentLevel+". Publish this achievement on facebook and get 20 coins!")
			  //     .setTitle("Publish Achievement");
			// Add the buttons
			//builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			//           public void onClick(DialogInterface dialog, int id) {
			        	   
			//				isAchievement = true;
			//				onClickLogin();
							
							
			  //         }
			  //     });
			//builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			  //         public void onClick(DialogInterface dialog, int id) {
			               // User cancelled the dialog
			 //          }
			 //      });
			//// 3. Get the AlertDialog from create()
			//AlertDialog dialog = builder.create();
			//dialog.show();
			//setAchievementSaved(true);
			
			Intent intentAchievement = new Intent(getContext(), AchievementActivity.class);
			startActivityForResult(intentAchievement, ACHIEVEMENT_ACTIVITY);
		}

	}
	/**
	 * Retrieves Bitmap from Asset
	 * @param strName
	 * @return
	 * @throws IOException
	 */
		private Bitmap getBitmapFromAsset(String strName) throws IOException {
			AssetManager assetManager = getAssets();
			InputStream istr = assetManager.open(strName);
			Bitmap bitmap = BitmapFactory.decodeStream(istr);
			istr.close();
			return bitmap;
		}
	/**
	 * Creates Resizer Manager in order to better use screen dimensions
	 */

	private void createObjectResizerManager() {
		Log.d(TAG, "create ObjectsResizerManager");
		int screenHeight;
		int screenWidth;
		int dpi;

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		dpi = metrics.densityDpi;

		if (android.os.Build.VERSION.SDK_INT >= 13) {
			//new api
			Log.d(TAG, "heightpixels:" + metrics.heightPixels + "width Pixels:"
					+ metrics.widthPixels);
			screenHeight = metrics.heightPixels;
			screenWidth = metrics.widthPixels;
		} else {
			//old api
			Display display = getWindowManager().getDefaultDisplay();
			Log.d(TAG, "screenHeight:" + display.getHeight() + "screenWidth:"
					+ display.getWidth());

			screenWidth = display.getWidth();
			screenHeight = display.getHeight();
		}

		orm = new ObjectsResizeManager(screenWidth, screenHeight, dpi);
		orm.calculateActionBarHeight();

	}

	/**
	 * On Pool button pressed complete attempt characters , check if attempt =
	 * level word and get to next activity if needed
	 * 
	 * @param bNumber
	 */
	protected void onPoolPressed(Integer bNumber) {
		String chosen = bPool[bNumber.intValue()].getText().toString();
	

		boolean processed = false;

		for (int i = 0; i < bWord.length; i++) {
			// check each button if has text
			if (bWord[i].getText().length() == 0) {
				if (processed) {
					// buttons already processed but empty button found .. not
					// done yet
					return;
				}
				// first empty button
				bWord[i].setText("" + chosen);
				bWord[i].setBackgroundResource(R.drawable.button_shape_attempt);
				attempt[i] = chosen;
				// save mapping
				mapping.put((Integer) bWord[i].getTag(), bNumber);
				processed = true;
				bPool[bNumber.intValue()].setVisibility(View.INVISIBLE);

			}
		}

		if (processed) {
			if (checkSuccess()) {
				if(!getStatus(getLevel()).equalsIgnoreCase(getResources().getString(R.string.txt_completed))){
				// add money
				modifyMoney(MONEY_EARNED_LEVEL);
				}
                                finished=true;
				mHandler.postDelayed(mLaunchTask, 500);

			} else {
				// shake buttons
				Animation shake = AnimationUtils.loadAnimation(this,
						R.anim.shake);
				for (int i = 0; i < bWord.length; i++) {
					bWord[i].startAnimation(shake);
				}
			}
		}

	}

	/**
	 * On word button pressed reveal mapped pool button and clear corresponding
	 * attempt letter
	 * 
	 * @param bNumber
	 */
	protected void onWordPressed(Integer bNumber) {
		Log.d(TAG, " on WordPressed+" + bNumber);
		if (revealed.containsKey(bNumber))
			return;
		// empty button text
		bWord[bNumber.intValue()].setText("");
		bWord[bNumber.intValue()].setBackgroundResource(R.drawable.button_shape_letter);
		// set corresponding pool button visible
		Integer poolButton = mapping.get(bNumber);
		mapping.remove(bNumber);
		if (poolButton != null && poolButton.intValue() >= 0) {
			Log.d(TAG, "poolButton value:" + poolButton);
			bPool[poolButton].setVisibility(View.VISIBLE);
		}
		// empty attempt[bNumber]
		attempt[bNumber] = "";

	}

	/**
	 * Check if attempt is the same with level word
	 * 
	 * @return
	 */

	private boolean checkSuccess() {
		
		Log.d(TAG, "check success for "+attempt);
		
		printAttempt(ls.getProcessedWord());
		for (int i = 0; i < ls.getProcessedWord().length; i++) {
			if (!attempt[i].equalsIgnoreCase(ls.getProcessedWord()[i])) {
				if(settings.getBoolean(SOUND_SETTING, false)){
					//play success sound
					mp = MediaPlayer.create(this, R.raw.fail);
					mp.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							mp.release();
						}

					});   
					mp.start();
				}
				return false; 
			}
		}
		//play success sound
		if(settings.getBoolean(SOUND_SETTING, false)){
			mp = MediaPlayer.create(this, R.raw.success);
			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.release();
				}

			});   
			mp.start();
		}
		return true;
	}

	
	/**
	 * 
	 * @return SceneActivity
	 */
	protected SceneActivity getContext() {
		return this;
	}

	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case OPTIONS_ACTIVITY:
			resolveOptions(resultCode);
			break;
			
		case ACHIEVEMENT_ACTIVITY:
			resolveAchievement(resultCode);
			break;
		case INAPP_ACTIVITY:
			// reload money
			moneyText.setText("" + getMoney());
			break;
		case HINT_ACTIVITY:
			// reload money
			moneyText.setText("" + getMoney());
			break;
		default:
			Session.getActiveSession().onActivityResult(this, requestCode,
					resultCode, data);
			break;
		}
	}
	
	/**
	 * Solves Achievement selections
	 * @param resultCode
	 */
	private void resolveAchievement(int resultCode) {
		Log.d(TAG, "resolve achievement selection:" + resultCode);
		
		if (resultCode == ACHIEVEMENT_OK) {
			isAchievement = true;
			onClickLogin();
			if(!isNetworkAvailable()){
				Toast.makeText(this, "No internet connection available!", Toast.LENGTH_LONG).show();
			}else{
				setAchievementSaved(true);
			}
		}else {
			setAchievementSaved(true);
		}
		
	}

	/**
	 * Solves selected options - reveal correct letter or delete incorrect
	 * characters
	 * 
	 * @param resultCode
	 */
	private void resolveOptions(int resultCode) {
		Log.d(TAG, "resolve options selection:" + resultCode);
		ArrayList<Integer> toBeRemoved = new ArrayList<Integer>();

		String[] word = ls.getProcessedWord().clone();

		if (resultCode == OPTIONS_RESULT_DELETE) {
			// check if enough money
			if (!config.isAllowNegative() && getMoney() < MONEY_REMOVE_LETTERS) {
				Toast.makeText(this,  R.string.not_enough_coins, Toast.LENGTH_SHORT)
						.show();
				return;
			}

			// empty word buttons before processing
			// delete up to three unused letters
			// iterate through bpool and see what characters are visible 
			for (int i = 0; i < bPool.length; i++) {
				if (bPool[i].getVisibility() == View.VISIBLE) {
					// check if they are used in the word
					String temp = bPool[i].getText().toString();
					boolean found = false;
					for (int j = 0; j < word.length; j++) {
						if (word[j] != null) {
							if (word[j].equalsIgnoreCase(temp)) {
								// is used; character cannot be removed
								word[j] = null;
								found = true;
							}
						}
						if (found)
							break;
					}

					if (!found) {
						// can be removed
						toBeRemoved.add(i);
					}
				}// end if visible

			}
			if (toBeRemoved.size() > 0) {
				// remove coins for deleted letters
				moneyText.setText("" + modifyMoney(-MONEY_REMOVE_LETTERS));
			}
			// if toBeRemoved size>=3 then removed 3 random positions
			if (toBeRemoved.size() >= 3) {
				Random r = new Random();
				for (int k = 0; k < 3; k++) {
					int position = r.nextInt(toBeRemoved.size());
					deleteCharacter(toBeRemoved.get(position));
					if (removedLetters == null) {
						removedLetters = new ArrayList<Integer>();
					}
					removedLetters.add(toBeRemoved.get(position));
					toBeRemoved.remove(position);
				}

			} else {
				// remove all
				for (int k = 0; k < toBeRemoved.size(); k++) {

					deleteCharacter(toBeRemoved.get(k));
					if (removedLetters == null) {
						removedLetters = new ArrayList<Integer>();
					}
					removedLetters.add(toBeRemoved.get(k));
				}
			}
			// save current values for revealed and removed
			setSavedValues();
		} else if (resultCode == OPTIONS_RESULT_REVEAL) {
			// check if enough money
			if (!config.isAllowNegative() && getMoney() < MONEY_CORRECT_LETTER) {
				Toast.makeText(this,  R.string.not_enough_coins, Toast.LENGTH_SHORT)
						.show();
				return;
			}

			HashMap<Integer, Integer> candidates = new HashMap<Integer, Integer>();
			// clear all selected word character except from revealing actions
			Log.d(TAG, "Revealed character size" + revealed.size());
			if (revealed.size() > 0) {
				// remove from word revealed characters
				Integer[] keys = revealed.keySet().toArray(new Integer[0]);
				for (int i = 0; i < keys.length; i++) {
					Log.d(TAG, "remove revealed " + keys[i] + " value "
							+ word[keys[i]]);
					word[keys[i]] = null;
				}
			}
			//check attempt and see if it has empty positions
			for(int i=0;i<attempt.length;i++){
				if(attempt[i]==null||attempt[i].equalsIgnoreCase("")){
					//find  visible bPoolButton that has the same value as word[i] and add to candidates.
					if(word[i]!=null){
						for (int j = 0; j < bPool.length; j++) {
							if (bPool[j].getVisibility() == View.VISIBLE) {
								String temp = bPool[j].getText().toString();
								if(temp.equalsIgnoreCase(word[i])){
									candidates.put(j, i);
								}
							}
						}
					}
				}
			}
			if(!candidates.isEmpty()){
				// get a random character
				if (candidates.size() > 0) {
					Integer[] keys = candidates.keySet().toArray(new Integer[1]);
					Random r = new Random();

					int position = r.nextInt(keys.length);
					selectCharacter(keys[position], candidates.get(keys[position]),
							true);
					if (revealedLetters == null) {
						revealedLetters = new HashMap<Integer, Integer>();
					}
					revealedLetters.put(keys[position],
							candidates.get(keys[position]));
					candidates.remove(keys[position]);

				}
			}else{
				
				HashMap<Integer,Integer> wrongPositions =new HashMap<Integer, Integer>();
				// if there are not empty positions then check incorrect ones 

				//check attempt and see if it has empty positions
				for(int i=0;i<attempt.length;i++){
					if(attempt[i]==null||attempt[i].equalsIgnoreCase("")){
						//find  visible bPoolButton that has the same value as word[i] and add to candidates.
						if(word[i]!=null){
							for (int j = 0; j < bPool.length; j++) {
								if (bPool[j].getVisibility() == View.INVISIBLE) {
									String temp = bPool[j].getText().toString();
									if (temp.equalsIgnoreCase(word[i])) {
										int position=getAttemptPositionFromMaping(j);
										if(position>0 && word[position]!=null && !word[position].equalsIgnoreCase(attempt[position])){
											candidates.put(j, i);
											wrongPositions.put(j, position);
										}
									}
								}
							}
						}
					}
				}
				if(candidates.isEmpty()){
					for (int i = 0; i < attempt.length; i++) {
						if (attempt[i] != null && word[i]!=null&&!attempt[i].equalsIgnoreCase(word[i])) {
							// find all bPoolButton that has the same
							// value as word[i] and corresponding attempt position for pool[j] is not a correct one 
							// add to candidates.

							for (int j = 0; j < bPool.length; j++) {
								if (bPool[j].getVisibility() == View.INVISIBLE) {
									String temp = bPool[j].getText().toString();
									if (temp.equalsIgnoreCase(word[i])) {
										int position=getAttemptPositionFromMaping(j);
										if(position>0 && word[position]!=null && !word[position].equalsIgnoreCase(attempt[position])){
											candidates.put(j, i);
											wrongPositions.put(j, position);
										}
									}
								}else{
									
											String temp = bPool[j].getText().toString();
											if(temp.equalsIgnoreCase(word[i])){
												candidates.put(j, i);
											}
										
								}
							}

						}
					}
				}
				if(!candidates.isEmpty()){
					// get a random character
					if (candidates.size() > 0) {
						Integer[] keys = candidates.keySet().toArray(new Integer[1]);
						Random r = new Random();

						int position = r.nextInt(keys.length);
						onWordPressed(candidates.get(keys[position]));
						//also wrong position
						if(wrongPositions.get(keys[position])!=null){
							onWordPressed(wrongPositions.get(keys[position]));
						}
						selectCharacter(keys[position], candidates.get(keys[position]),
								true);
						if (revealedLetters == null) {
							revealedLetters = new HashMap<Integer, Integer>();
						}
						revealedLetters.put(keys[position],
								candidates.get(keys[position]));
						candidates.remove(keys[position]);

					}
				}
			}
			/*
			// remove all word buttons
			for (int i = 0; i < bWord.length; i++) {
				if (!bWord[i].getText().toString().equalsIgnoreCase("")
						&& !revealed.containsKey(i)) {
					Log.d(TAG,
							"clear Buttons that are not revealed but contain character");
					onWordPressed(i);
				}
			}

			// iterate through character pool
			for (int i = 0; i < bPool.length; i++) {
				if (bPool[i].getVisibility() == View.VISIBLE) {
					// check if they are used in the word
					String temp = bPool[i].getText().toString();
					boolean found = false;
					for (int j = 0; j < word.length; j++) {
						if (word[j] != null) {
							if (word[j].equalsIgnoreCase(temp)) {
								// is used; character cannot be removed
								word[j] = null;
								found = true;
								// can be selected
								candidates.put(i, j);
							}
						}
						if (found)
							break;
					}

				}// end if visible

			}

			// get a random character
			if (candidates.size() > 0) {
				Integer[] keys = candidates.keySet().toArray(new Integer[1]);
				Random r = new Random();

				int position = r.nextInt(keys.length);
				selectCharacter(keys[position], candidates.get(keys[position]),
						true);
				if (revealedLetters == null) {
					revealedLetters = new HashMap<Integer, Integer>();
				}
				revealedLetters.put(keys[position],
						candidates.get(keys[position]));
				candidates.remove(keys[position]);

			}
*/
			// save current values for revealed and removed
			setSavedValues();
		} else if (resultCode == OPTIONS_RESULT_HINT) {
			
			// start hint activity
			Intent main = new Intent(getContext(), HintActivity.class);
			main.putExtra(HINT_INTENT_KEY, ld.getLevelSpecific().getHints());
			main.putExtra(HINT_LEVEL, getLevel());
			startActivityForResult(main, HINT_ACTIVITY);
		}
		 else if (resultCode == OPTIONS_RESULT_SKIP) {
				
				// increase level and start scene activity
			 	modifyMoney(-MONEY_SKIP);
				Intent scene = new Intent(getContext(), SceneActivity.class);
				setLevel(getLevel()+1);
				startActivity(scene);
			}
	}

	private int getAttemptPositionFromMaping(int value) {
		Set<Integer> colection=mapping.keySet();
		Iterator<Integer> it=colection.iterator();
		while(it.hasNext()){
			Integer temp=it.next();
			if(mapping.get(temp)==value) return temp.intValue();
		}
		return -1;
	}
	/**
	 * Reveals one correct character from pool corresponding to a character
	 * position in word
	 * 
	 * @param bPoolPosition
	 * @param bWordPosition
	 */
	private void selectCharacter(Integer bPoolPosition, Integer bWordPosition,
			boolean takeCoins) {
		Log.d(TAG, "select pool character " + bPoolPosition + " wordPosition "
				+ bWordPosition);
		String chosen = bPool[bPoolPosition].getText().toString();
		Log.d(TAG, "select pool character " + chosen);
		bWord[bWordPosition].setText("" + chosen);
		attempt[bWordPosition] = chosen;
		printAttempt(attempt);
		// save mapping
		mapping.put((Integer) bWord[bWordPosition].getTag(), bPoolPosition);
		Log.d(TAG, "putMapping " + (Integer) bWord[bWordPosition].getTag()
				+ " - " + bPoolPosition);
		bPool[bPoolPosition.intValue()].setVisibility(View.INVISIBLE);

		revealed.put(bWordPosition, bWordPosition);
		Log.d(TAG, "revealed hasmap add :" + bWordPosition);
		bWord[bWordPosition]
				.setBackgroundResource(R.drawable.button_shape_guessed);
		Log.d(TAG, "change background to revealed :" + bWordPosition);
		boolean completed = true;
		for (int i = 0; i < bWord.length; i++) {
			if (bWord[i].getText().length() == 0) {
				completed = false;
				break;
			}

		}

		if (completed) {
if (checkSuccess()&&!finished) {
				if(!getStatus(getLevel()).equalsIgnoreCase(getResources().getString(R.string.txt_completed))){
				// add money
				modifyMoney(MONEY_EARNED_LEVEL);
				}
finished=true;
				mHandler.postDelayed(mLaunchTask, 500);

			} else {
				// shake buttons
				Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake);
				for (int i = 0; i < bWord.length; i++) {
					bWord[i].startAnimation(shake);
				}
			}
		}
		if (takeCoins) {
			// get coins
			moneyText.setText("" + modifyMoney(-MONEY_CORRECT_LETTER));
		}
	}

	private void printAttempt(String[] attempt2) {
		for (int i = 0; i < attempt2.length; i++) {
			Log.d(TAG, "attempt[" + i + "]" + attempt2[i]);
		}

	}

	/**
	 * Hides a character button from pool
	 * 
	 * @param position
	 */
	private void deleteCharacter(Integer position) {
		// make BPool button invisible
		bPool[position].setVisibility(View.INVISIBLE);

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
		try{
			if(value<0&& money<GIFT_LIMIT){
				
					long dateAlarm=System.currentTimeMillis();
					
					//reset Alarm 
					int number = settings.getInt(INFO_GIFTS_NUMBER, 0);
					if(number>2){ 
						number=2; 
					}else{
						number=1;
					}
					
					//set new time 
					editor.putLong(INFO_TIME, dateAlarm+1000*60*60*23*number);
					editor.commit();
				
					
					Log.d(TAG, "Setting alarm for level "+INFO_LEVEL+" multiple for gifts already awarded: "+number+" to go off at:" +new Date(dateAlarm+1000*60*60*23*number));//dateAlarm+1000*60*60*23*number));
					Intent intent = new Intent(this, AlarmReceiver.class);
					//intent.setAction("com.tedrasoft.quiz.ALARMACTION");
					intent.putExtra(getResources().getString(R.string.app_name), true);
					PendingIntent pi = PendingIntent.getBroadcast(this,ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					AlarmManager am = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
					am.set(AlarmManager.RTC_WAKEUP, dateAlarm+1000*60*60*23*number,pi);
					Calendar cal=Calendar.getInstance();
					cal.setTimeInMillis(dateAlarm+1000*60*60*23*number);
					
					Log.d(TAG, "Alarm set for !"+cal.toString());
			}
		}catch(Exception e){
			
		}
		return money;
	}
	//Verifies if coins are initialized
	private boolean isMoneyInitialized() {
		boolean initialized = settings.getBoolean(COINS_INITIALIZED, false);
		return initialized;
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
	 * Initializes available money with the given amount
	 * 
	 * @param sum
	 */
	public void initializeMoney(int sum) {

		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(MONEY, sum);
		editor.putBoolean(COINS_INITIALIZED, true);
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
	
	/**
	 * Get isAchievement saved in shared settings
	 * 
	 * @return isAchievement saved
	 */
	public boolean getAchievementSaved() {
		boolean isSaved = settings.getBoolean(ACHIEVEMENT_SAVED+"_"+getLevel(), false);
		return isSaved;
	}
	/**
	 * Sets achievement in shared settings
	 * 
	 * @param achievement
	 */
	public void setAchievementSaved(boolean isSaved) {
		// SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(ACHIEVEMENT_SAVED+"_"+getLevel(), isSaved);
		editor.commit();
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

	/**
	 * Sets next level in shared settings
	 * 
	 * @param level
	 */
	public void setLevel(int level) {
		// SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(LEVEL, level);
		editor.commit();
	}

	@Override
	public void onBackPressed() {
		

		Intent main = new Intent(this, MainActivity.class);
		main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(main);
		finish();
		 // If an interstitial is on screen, close it. Otherwise continue as normal.
		if (this.cb.onBackPressed())
	        return;
	            else
	        super.onBackPressed();


	}

	// launch the success activity
	private Runnable mLaunchTask = new Runnable() {
		public void run() {
			// start activity pop - up for receiving coins and getting to next
			// level
			Intent intent = new Intent(getContext(), SuccessActivity.class);
			intent.putExtra(SUCCESS_WORD, ls.getWord());
			intent.putExtra(SUCCESS_LEVEL, getLevel());
			String status = getStatus(getLevel());
			
			if(!status.equalsIgnoreCase(getResources().getString(R.string.txt_completed))){
				Log.d(TAG,"status level"+getLevel()+"="+ status);
				setStatus(getLevel(), getResources().getString(R.string.txt_completed));
				intent.putExtra(SHOW_MONEY, true);
				
			}else{
				intent.putExtra(SHOW_MONEY, false);
			}
			setLevel(getLevel() + 1);
			startActivity(intent);

		}
	};

	/**
	 * Verifies number of levels and put value in settings if needed
	 * 
	 * @param noLevels
	 */
	private int setLevelsNumbers(int noLevels) {
		int levels = settings.getInt(NO_LEVELS, -1);
		if (levels < 0||levels<noLevels) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(NO_LEVELS, noLevels);
			editor.commit();
			return noLevels;
		}
		return levels;
	}

	/**
	 * Saves removed letters and revealed letters
	 * 
	 */
	private void setSavedValues() {
		SharedPreferences.Editor editor = settings.edit();
		String temp = constructString(removedLetters);
		editor.putString(REMOVED_LETTERS, temp);
		temp = constructString(revealedLetters);
		editor.putString(REVEALED_LETTERS, temp);
		int currentLevel = getLevel();
		editor.putInt(SAVED_LEVEL, currentLevel);
		editor.commit();
	}

	/**
	 * Gets saved removed letters and revealed letters
	 */
	private void getSavedValues() {
		String retrieved = settings.getString(REMOVED_LETTERS, null);
		Log.d(TAG, "retrieved removed "+retrieved);
		removedLetters = constructArrayList(retrieved);
		retrieved = settings.getString(REVEALED_LETTERS, null);
		revealedLetters = constructHashMap(retrieved);
		Log.d(TAG, "retrieved revealed "+retrieved);
		savedLevel = settings.getInt(SAVED_LEVEL, -1);
	}

	/**
	 * Constructs a string from deleted letter positions from pool array for
	 * saving when application is closed
	 * 
	 * @param arraylist
	 * @return String
	 */
	private String constructString(ArrayList<Integer> arraylist) {
		if (arraylist == null)
			return null;
		String converted = new String();
		for (int i = 0; i < arraylist.size(); i++) {
			if (converted.length() > 0) {
				converted = converted + "," + arraylist.get(i);
			} else {
				converted = converted + arraylist.get(i);
			}
		}
		Log.d(TAG, "Converted string:"+converted);
		return converted;
	}

	/**
	 * Constructs a string from positions of revealed word letters for saving
	 * when application is closed
	 * 
	 * @param hashMap
	 * @return String
	 */
	private String constructString(HashMap<Integer, Integer> hashMap) {
		if (hashMap == null)
			return null;
		String converted = new String();
		Set<Integer> keys = hashMap.keySet();
		Iterator<Integer> it = keys.iterator();
		Integer temp;
		while (it.hasNext()) {
			temp = it.next();
			if (converted.length() > 0) {
				converted = converted + "," + temp + "=" + hashMap.get(temp);
			} else {
				converted = converted + temp + "=" + hashMap.get(temp);
			}
		}
		Log.d(TAG, "Converted string revealed:"+converted);
		return converted;
	}

	/**
	 * Constructs an array of integers from given string
	 * 
	 * @param value
	 * @return
	 */
	private ArrayList<Integer> constructArrayList(String value) {
		if (value == null)
			return null;
		ArrayList<Integer> converted = new ArrayList<Integer>();
		if (value.length() > 0) {
			String[] splits = value.split(",");
			for (int i = 0; i < splits.length; i++) {
				converted.add(Integer.parseInt(splits[i]));
			}
		}
		return converted;
	}

	/**
	 * Constructs a HashMap of pool and word letter positions from a String
	 * 
	 * @param value
	 * @return
	 */
	private HashMap<Integer, Integer> constructHashMap(String value) {
		if (value == null)
			return null;
		HashMap<Integer, Integer> converted = new HashMap<Integer, Integer>();
		if (value.length() > 0) {
			String[] splits = value.split(",");
			for (int i = 0; i < splits.length; i++) {
				String[] pair = splits[i].split("=");
				converted.put(Integer.parseInt(pair[0]),
						Integer.parseInt(pair[1]));
			}
		}
		return converted;
	}

	@Override
	protected void onRestart() {
		moneyText.setText("" + getMoney());
		super.onRestart();
		Log.d(TAG, "onRestart");
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		moneyText.setText("" + getMoney());
		if(attempt!=null){
			try{
if (checkSuccess()&&!finished) {
		
			if(!getStatus(getLevel()).equalsIgnoreCase(getResources().getString(R.string.txt_completed))){
			// add money
			modifyMoney(MONEY_EARNED_LEVEL);
			}
finished=true;
			mHandler.postDelayed(mLaunchTask, 500);
			}
			}catch(Exception e){
				Log.e(TAG, "Error "+e.getMessage());
			}
		} 
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
		

        // Notify the beginning of a user session. Must not be dependent on user actions or any prior network requests.
        

		
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		this.cb.onDestroy(this);

		
	}

	@Override
	public void onStop() {
		Log.d(TAG, "onStop");
		super.onStop();
this.cb.onStop(this);
		
		Session.getActiveSession().removeCallback(statusCallback);
	}

	/**
	 * If facebook Session is open and facebook button has been clicked then
	 * open post to wall interface
	 * 
	 * @param session
	 * @param state
	 * @param exception
	 */
	protected void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		session = Session.getActiveSession();
		if (session.isOpened()) {
			try {
				publishFeedDialog();
				publishAchievement();
			} catch (IOException e) {
				Log.e(TAG, "Error publish feed");
			}
		} else {
			// nothing .. maybe message
		}

	}

	/**
	 * Method called when facebook button is pressed. Ensures login is achieved.
	 */
	private void onClickLogin() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this)
					.setCallback(statusCallback));
		} else {
			Session.openActiveSession(this, true, statusCallback);
		}
	}

	/**
	 * Facebook Post to wall interface
	 * 
	 * @throws IOException
	 */
	private void publishFeedDialog() throws IOException {

		if (!isClicked)
			return;
		String description = buildDescription();
		Bundle params = new Bundle();
		params.putString("name",
				getResources().getString(R.string.msg_facebook_name));
		params.putString("caption",
				getResources().getString(R.string.msg_facebook_caption));
		params.putString("description", description);
		params.putString("link",
				getResources().getString(R.string.msg_facebook_link));
	
		params.putString("picture",  getResources().getString(R.string.msg_facebook_image_url)+"/"+ld.getLevelSpecific().getImage().trim());


		feedDialog = (new WebDialog.FeedDialogBuilder(this,
				Session.getActiveSession(), params))
				.setDescription(description)
				.setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							// When the story is posted give money
							final String postId = values.getString("post_id");
							if (postId != null) {
								Toast.makeText(
										getContext(),
										getResources().getString(
												R.string.msg_post_success),
										Toast.LENGTH_SHORT).show();
								
								if (!getIsOnFacebook()) {
									// give coins once for facebook 
									modifyMoney(MONEY_FACEBOOK);

									// put last Download date
									setIsOnFacebook();
								}
								
								
								moneyText.setText("" + getMoney());
							} else {
								// User clicked the Cancel button
								// Toast.makeText(getContext().getApplicationContext(),
								// "Publish cancelled",
								// Toast.LENGTH_SHORT).show();

							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							// Toast.makeText(getContext().getApplicationContext(),
							// "Publish cancelled",
							//
							// Toast.LENGTH_SHORT).show();
Log.e(TAG, "Error publishing to facebook :"+error.getMessage(),error);

						} else {
							// Generic, ex: network error
							// Toast.makeText(getContext().getApplicationContext(),
							// "Error posting story",
							// Toast.LENGTH_SHORT).show();
							Log.e(TAG, "Error publishing to facebook :"+error.getMessage(),error);
						}
					}

					

				}).build();
		isClicked = false;
		feedDialog.show();
	}

	protected void setIsOnFacebook() {
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(APP_IS_ON_FACEBOOK, true);
		editor.commit();
		
	}
	protected boolean getIsOnFacebook() {
		boolean isOnFacebook = settings.getBoolean(APP_IS_ON_FACEBOOK, false);
		return isOnFacebook;
	}

	/**
	 * Build Facebook description
	 * 
	 * @return String
	 */
	private String buildDescription() {
		StringBuffer sb = new StringBuffer();
		if (attempt != null) {
			for (int i = 0; i < attempt.length; i++) {
				if (attempt[i] != null && (!attempt[i].equalsIgnoreCase("")) && revealed != null
						& revealed.containsValue(i)) {
					sb.append(attempt[i]).append(' ');
				} else {
					sb.append("_ ");
				}
			}
		} else {
			for (int i = 0; i < ld.getLevelSpecific().getWord().length(); i++) {
				sb.append("_ ");
			}
			sb.append("(" + ld.getLevelSpecific().getWord().length()
					+ getResources().getString(R.string.msg_facebook_letters)+". ");
		}
	//	sb.append("\"" + ld.getLevelSpecific().getRiddle() + "\".\n");
		if (getHintLevel() == getLevel()) {
			int last=getHintIndex();
			for(int i=0;i<last;i++){
				sb.append("Hint"+(i+1)+" : " + ld.getLevelSpecific().getHints().get(i) + ".\t\n");
			}
		}
		sb.append(getResources().getString(R.string.msg_facebook_possible));

		for (int i = 0; i < bPool.length; i++) {
			sb.append(bPool[i].getText()).append(' ');
		}
		return sb.toString();
	}
	/**
	 * Get level of last hint displayed
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
	 * 
	 * @return last ad clicked day
	 */
	protected String getLastDownloadDate() {

		return settings.getString(LAST_DAY_DOWNLOAD, "");

	}
	
	/**
	 * Sets last ad clicked day
	 */
	protected void setLastDownloadDate(String today) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(LAST_DAY_DOWNLOAD, today);
		editor.commit();

	}

	
	// Show a dialog (feed or request) without a notification bar (i.e. full screen)
	private void publishAchievement() {
		// Create the dialog
		if (!isAchievement)
			return;
		Bundle params = new Bundle();
    	params.putString("link", getResources().getString(R.string.msg_facebook_link));
    	params.putString("name", getResources().getString(R.string.msg_facebook_achievement_title_1)+" "+getLevel()+" "+getResources().getString(R.string.msg_facebook_achievement_title_2));
    	params.putString("caption",getResources().getString(R.string.msg_facebook_caption));
    	params.putString("description", getResources().getString(R.string.msg_facebook_achievement_text_1)+" "+getLevel()+" "+getResources().getString(R.string.msg_facebook_achievement_text_2));
    	params.putString("picture", getResources().getString(R.string.msg_facebook_image_url)+"/icon.png");
    	
    
		dialog = new WebDialog.Builder(this, Session.getActiveSession(), "feed", params).setOnCompleteListener(
				new WebDialog.OnCompleteListener() {
			
			@Override
			public void onComplete(Bundle values, FacebookException error) {
				if (error != null && !(error instanceof FacebookOperationCanceledException)) {
					
					Toast.makeText(getContext(), "Error publishing achievement", Toast.LENGTH_SHORT).show();
				}else if(error==null){
					modifyMoney(20);
					moneyText.setText("" + getMoney());
				}
				dialog = null;
				dialogAction = null;
				dialogParams = null;
			}
		}).build();
		
		// Hide the notification bar and resize to full screen
		Window dialog_window = dialog.getWindow();
    	dialog_window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    	// Store the dialog information in attributes
    	dialogAction = "feed";
    	dialogParams = params;
    	
    	isAchievement=false;
    	// Show the dialog
    	dialog.show();
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
}
