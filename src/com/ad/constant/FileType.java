package com.ad.constant;

public enum FileType {

	PNG("image/png"), JPG("image/jpeg"), BMP("image/bmp"), GIF("image/gif"), 
	
	HTML("text/html"), CSS("text/css"), CSV("text/csv"), JS("text/javascript"),
	
	DOC("application/msword"), DOT("application/msword"), TXT("text/plain"),
	
	ZIP("aplication/zip");
	
	String desc;
	
	FileType(String desc){
		
		this.desc = desc;
	}
	
	public String desc(){
		
		return desc;
	}
	
}
