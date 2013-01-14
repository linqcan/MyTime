package com.linqcan.mytime;

import java.io.Serializable;

public class Label implements Serializable {
	
	private static final long serialVersionUID = 5555257641688413558L;
	private Long id;
	private String name;
	/*
	 * TODO
	 * Add color
	 */
	
	public final static String TABLE_NAME = "labels";
	public final static String COLUMN_NAME = "name";
	public final static String DEFAULT_NAME = "Default";
	public final static String DATABASE_TABLE_CREATE = 
			"CREATE TABLE "+ TABLE_NAME +
			"(_id integer primary key autoincrement, "+ COLUMN_NAME +" text not null unique);";
	public final static String DATABASE_DEFAULT_CREATE = 
			"INSERT INTO "+ TABLE_NAME +"("+COLUMN_NAME+") VALUES ('"+DEFAULT_NAME+"');";
	
	public Label(String name){
		this.name = name;
	}
	
	public Label(Long id, String name){
		this.name = name;
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
