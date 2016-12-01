package com.tedrasoft.templategoogle.data;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
/**
 * Config data loaded from json in assets
 * @author Dragos
 *
 */
@JsonAutoDetect(fieldVisibility=Visibility.NONE,getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class ConfigData {

	
	@JsonProperty("name")
	private String name;
	@JsonProperty("path")
	private String path;
	@JsonProperty("alphabet")
	private String alphabet; 
	@JsonProperty("no_levels")
	private int noLevels;//number of levels
	@JsonProperty("allow_negative")
	private boolean allowNegative; //allow negative coins amount
	@JsonProperty("show_hints")
	private boolean showHints;
	@JsonProperty("additional_levels_from")
	private int additionalLevels;
	
	public boolean isShowHints() {
		return showHints;
	}
	public void setShowHint(boolean showHints) {
		this.showHints = showHints;
	}
	public boolean isAllowNegative() {
		return allowNegative;
	}
	public void setAllowNegative(boolean allowNegative) {
		this.allowNegative = allowNegative;
	}
	public int getNoLevels() {
		return noLevels;
	}
	public void setNoLevels(int noLevels) {
		this.noLevels = noLevels;
	}
	@JsonProperty("levels")
	private ArrayList<LevelData> levels;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public ArrayList<LevelData> getLevels() {
		return levels;
	}
	public void setLevels(ArrayList<LevelData> levels) {
		this.levels = levels;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getAlphabet() {
		return alphabet;
	}
	public void setAlphabet(String alphabet) {
		this.alphabet = alphabet;
	}
	public int getAdditionalLevels() {
		return additionalLevels;
}
	public void setAdditionalLevels(int additionalLevels) {
		this.additionalLevels = additionalLevels;
	}
	
	
}
