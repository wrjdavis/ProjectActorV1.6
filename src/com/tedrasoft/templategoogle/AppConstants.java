package com.tedrasoft.templategoogle;
/**
 * All constants used in the application
 * @author Dragos
 *
 */
public interface AppConstants {
	//request codes for startActivityForResults
	public static final int LEVELS_ACTIVITY = 1;
	public static final int SCENE_ACTIVITY = 2;
	public static final int POP_UP_ACTIVITY = 3;
	public static final int OPTIONS_ACTIVITY = 4;
	public static final int INAPP_ACTIVITY = 5;
	public static final int HINT_ACTIVITY = 6;
	public static final int ACHIEVEMENT_ACTIVITY = 7;
	
	//result codes for options activity
	public static final int OPTIONS_RESULT_REVEAL = 1;
	public static final int OPTIONS_RESULT_DELETE = 2;
	public static final int OPTIONS_RESULT_CANCEL = 3;
	public static final int OPTIONS_RESULT_HINT = 6;
	public static final int OPTIONS_RESULT_SKIP= 7;
	
	//result code for INAPP Activity
	public static final int INAPP_RESULT_FINISH = 4;
	//result codes for hint activity
	public static final int HINT_RESULT_CANCEL = 5;
	
	//result codes for achievement activity
		public static final int ACHIEVEMENT_CANCEL = 7;
		public static final int ACHIEVEMENT_OK = 8;
	//intent value for hint
	public static final String HINT_INTENT_KEY = "HintIntentKey";
	public static final String PREFS_NAME="WWE";
	public static final String PREFS_NAME_STATUSES="STATUSESQUIZ";
	public static final String LANGUAGE="Language";
	public static final String FIRSTTIME="Firsttime";
	public static final String VERSION="Version";
	public static final String JSON_NUM="Json_num";
	public static final String DEFAULT_LOCALE="Default_locale";
	//keys for storing values in settings
	public static final String MONEY="Money";
	public static final String LEVEL="Level";
	public static final String NO_LEVELS="NoLevels";
	public static final String NEGATIVE="Negative";
	public static final String REVEALED_LETTERS="RevealedLetters";
	public static final String REMOVED_LETTERS="RemovedLetters";
	public static final String SAVED_LEVEL="SavedLevel";
	public static final String SAVED_URIS="SavedUris";
	public static final String COINS_INITIALIZED="CoinsInitialized";
	public static final String ITEM_PURCHASED="ItemPurchased";
	public static final String HINT_LEVEL_PROCESSED="HintLevelProcessed";
	public static final String HINT_LAST_INDEX="HintLastIndex";
	public static final String LAST_DAY_DOWNLOAD="LastDayDownload";
	public static final String APP_IS_RATED="AppIsRated";
	public static final String APP_IS_ON_FACEBOOK="AppIsOnFacebook";
public static final String APP_COMPLETED="Completed";
	public static final String SKIP_DATE="skip_date";
	public static final String ADDITIONAL_UNLOCKED="additional_unlocked";
	public static final String LEVELS_UPDATED="levels_updated";
	
	//coins paid for correct letter revealing
	public static final int MONEY_CORRECT_LETTER=10;
	//coins received for a completed level 
	public static final int MONEY_EARNED_LEVEL=4;
	//coins paid for a hint 
	public static final int MONEY_HINT=10;
	//coins paid for a hint 
	public static final int MONEY_SKIP=5;
	//coins paid for removing up to three incorrect letters
	public static final int MONEY_REMOVE_LETTERS=15;
	//initial coins amount
	public static final int MONEY_INITIAL_SUM=200;
	//coins received for sharing status on facebook
	public static final int MONEY_FACEBOOK=30;
	//coins received for rating the app
	public static final int MONEY_RATE=100;
	//coins received for downloading a game
	public static final int MONEY_DOWNLOAD_GAME=30;
	//coins received for buying first item 
	public static final int COINS_INAPP_ITEM_1=100;
		
	//coins received for buying second item 
	public static final int COINS_INAPP_ITEM_2=300;
	
	public static final int COINS_CLAIMED=150;
	//extra for HintActivity
	public static final String HINT_LEVEL="hint_level";
	
	//extra for SuccessActivity intent
	public static final String SUCCESS_WORD="success_word";
	public static final String SUCCESS_LEVEL="success_level";
	public static final String SHOW_MONEY="succes_show_money";
	//faceBook data - application id from facebook
	//public static final String FACEBOOK_APPID = "127872954080655";
	public static final String FACEBOOK_PERMISSION = "publish_stream";
	//App Rate constants
	public static final String PREF_APP_HAS_CRASHED = "pref_app_has_crashed";
	public static final String PREF_DATE_FIRST_LAUNCH = "date_firstlaunch";
	public static final String PREF_LAUNCH_COUNT = "launch_count";
	public static final String PREF_DONT_SHOW_AGAIN = "dont_show_again";
	
	
	
	
	public static final String SOUND_SETTING="sound_setting";
	public static final String SOUND_OFF="sound_off";
	public static final String SOUND_ON="sound_on";
	
	//IAP
	
    // (arbitrary) request code for the purchase flow
    public static final int RC_REQUEST = 10022;
    public static final boolean USE_IAP =true;
    
    
    public static final String INFO_LEVEL = "INFO_LEVEL";
    public static final String INFO_TIME = "INFO_TIME";
    public static final String INFO_GIFTS_NUMBER = "INFO_GIFTS_NUMBER";
    public static final String EXTRA_COINS = "EXTRA_COINS";
    
    public static final int GIFT_LIMIT = 40;
    public static final int COINS_AWARDED = 30;
    public static final String AWARD_DATE = "AWARD_DATE";
    
    public static final int ALARM_ID = 22345;
   
    public static final String ACHIEVEMENT_SAVED ="ACHIEVEMENT_SAVED";
    
    public static final int WORD_MAX_SIZE=16;
    
    public static final int ROW_POOL_SIZE = 9;
  //value of level status
  	public static final int AVAILABLE= 0;
  	public static final int COMPLETED= 1;
  	public static final int NOT_COMPLETED= 2;
    
}
