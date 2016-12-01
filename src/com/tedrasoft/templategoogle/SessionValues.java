package com.tedrasoft.templategoogle;

public class SessionValues {
	private static SessionValues instance = null;
	private int interstitialsShown=0;
	   protected SessionValues() {
	      // Exists only to defeat instantiation.
	   }
	   public static SessionValues getInstance() {
	      if(instance == null) {
	         instance = new SessionValues();
	      }
	      return instance;
	   }
	public int getInterstitialsShown() {
		return interstitialsShown;
	}
	public void setInterstitialsShown(int interstitialsShown) {
		this.interstitialsShown = interstitialsShown;
	}
	public void incrementInterstitialsShown() {
		// TODO Auto-generated method stub
		interstitialsShown++;
	}
	   
	   
}
