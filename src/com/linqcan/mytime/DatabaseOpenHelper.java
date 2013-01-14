package com.linqcan.mytime;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "mytime";
	private static final int DATABASE_VERSION = 1;
		
	public DatabaseOpenHelper(Context context){
		this(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public DatabaseOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("Linqcan::DatabaseOpenHelper","onCreate");
		db.execSQL(Label.DATABASE_TABLE_CREATE);
		db.execSQL(TimeActivity.DATABASE_TABLE_CREATE);
		db.execSQL(Label.DATABASE_DEFAULT_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		MainActivity.putLogMessage("DatabaseOpenHelper", "onUpgrade called but not implemented");
	}

}
