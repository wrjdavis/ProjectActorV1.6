package com.tedrasoft.templategoogle.data;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonAutoDetect(fieldVisibility=Visibility.NONE,getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonFilter("fieldFilter")
public class LevelData {
	
	public static final Set<String> currentProperties;
	public static final Set<String> templateProperties;
	

	static{
		currentProperties=new HashSet<String>(10);
		currentProperties.add("id");
		currentProperties.add("label");
		currentProperties.add("specific");
		
		
		templateProperties=new HashSet<String>(10);
		templateProperties.add("id");
		templateProperties.add("label");
		templateProperties.add("specific");
	
	}
	
	
	
	@JsonProperty("id")
	int id;
	@JsonProperty("label")
	String levelText;
	
	@JsonProperty("specific")
	LevelSpecific levelSpecific;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLevelText() {
		return levelText.trim();
	}
	public void setLevelText(String levelText) {
		this.levelText = levelText;
	}
	
	public LevelSpecific getLevelSpecific() {
		return levelSpecific;
	}
	public void setLevelSpecific(LevelSpecific levelSpecific) {
		this.levelSpecific = levelSpecific;
	}
	
	
}
