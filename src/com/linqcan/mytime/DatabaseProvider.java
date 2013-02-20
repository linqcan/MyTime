package com.linqcan.mytime;

import java.util.Date;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseProvider {
	
	private SQLiteDatabase mDatabase;
	private DatabaseOpenHelper mDatabaseHelper;
	private static DatabaseProvider mInstance = null;
	
	private DatabaseProvider(Context context){
		mDatabaseHelper = new DatabaseOpenHelper(context);
	}
	
	public static DatabaseProvider getInstance(Context context){
		if(mInstance == null){
			mInstance = new DatabaseProvider(context.getApplicationContext());
		}
		return mInstance;
	}
	
	public void open(){
		mDatabase = mDatabaseHelper.getWritableDatabase();
	}
	
	public void close(){
		mDatabaseHelper.close();
	}
	
	public Label insertLabel(Label label){
		ContentValues values = new ContentValues();
		values.put(Label.COLUMN_NAME, label.getName());
		long row = -1;
		try{
			row = mDatabase.insertOrThrow(Label.TABLE_NAME, null, values);
		}
		catch(SQLiteConstraintException e){
			return null;
		}
		
		Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+Label.TABLE_NAME+" WHERE _id = ?",
											new String[] { Long.toString(row) });
		cursor.moveToFirst();
		
		Label newlabel = cursorToLabel(cursor);
		cursor.close();
		return newlabel;
	}
	
	public boolean updateLabel(Label label){
		ContentValues values = new ContentValues();
		values.put(Label.COLUMN_NAME, label.getName());
		int result = mDatabase.update(Label.TABLE_NAME, values, "_id = ?",new String[] { Long.toString(label.getId())});
		return result > 0;
	}
	
	public boolean deleteLabel(Long id){
		removeLabelFromActivities(id); //Verify this?
		int result = mDatabase.delete(Label.TABLE_NAME, "_id = ?", new String[] {Long.toString(id)});
		return result > 0;
	}
	
	public int removeLabelFromActivities(long id){
		if(id < 0){
			return -1;
		}
		ContentValues values = new ContentValues();
		values.put(TimeActivity.COLUMN_LABEL, getDefaultLabelId());
		int rows = mDatabase.update(TimeActivity.TABLE_NAME, values, TimeActivity.COLUMN_LABEL+"=?", new String[] {Long.toString(id)});
		return rows;
	}
	
	private Label cursorToLabel(Cursor cursor){
		Label label = new Label(cursor.getLong(0), cursor.getString(1));
		return label;
	}
	
	public Cursor getAllLabelsCursor(){
		Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+ Label.TABLE_NAME+" ORDER BY name ASC", null);
		return cursor;
	}
	
	public Label getLabelById(long id){
		Cursor cursor = mDatabase.rawQuery("SELECT * FROM "+Label.TABLE_NAME+" WHERE _id = ?", new String[] {Long.toString(id)});
		if(!cursor.moveToFirst()){
			return null;
		}
		Label label = cursorToLabel(cursor);
		cursor.close();
		return label;
	}
	
	public TimeActivity insertActivity(TimeActivity activity){
		ContentValues values = timeActivityToContentValues(activity);
		
		long row = mDatabase.insert(TimeActivity.TABLE_NAME, null, values);
		
		if(row == -1){
			return null;
		}
		
		TimeActivity newta = getActivityById(row);
		return newta;
	}
	
	public boolean updateActivity(TimeActivity activity){
		ContentValues values = timeActivityToContentValues(activity);
		
		int result = mDatabase.update(TimeActivity.TABLE_NAME, values, "_id = ?", new String[] {Long.toString(activity.getId())});
		return result > 0;
	}
	
	private ContentValues timeActivityToContentValues(TimeActivity activity){
		ContentValues values = new ContentValues();
		values.put(TimeActivity.COLUMN_ACTIVITY_DATE, activity.getActivity_date().getTime() / 1000);
		values.put(TimeActivity.COLUMN_START_DATE, activity.getStart_date().getTime() / 1000);
		if(!activity.getOngoing()){
			values.put(TimeActivity.COLUMN_END_DATE, activity.getEnd_date().getTime() / 1000);
		}
		else{
			values.put(TimeActivity.COLUMN_END_DATE,0);
		}
		values.put(TimeActivity.COLUMN_DURATION, activity.getDuration());
		values.put(TimeActivity.COLUMN_DESCRIPTION, activity.getDescription());
		values.put(TimeActivity.COLUMN_LABEL, activity.getLabel_id());
		values.put(TimeActivity.COLUMN_ONGOING, (activity.getOngoing() ? 1 : 0));
		
		return values;
	}
	
	public boolean deleteActivity(long id){
		int result = mDatabase.delete(TimeActivity.TABLE_NAME, "_id = ?", new String[] {Long.toString(id)});
		return result > 0;
	}
	
	public TimeActivity getActivityById(long id){
		
		Cursor cursor = mDatabase.rawQuery("SELECT *, " +
				"("+TimeActivity.COLUMN_CREATED_ON+" * 1000) AS "+TimeActivity.COLUMN_CREATED_ON+", " +
				"("+TimeActivity.COLUMN_ACTIVITY_DATE+" * 1000) AS "+TimeActivity.COLUMN_ACTIVITY_DATE+", " +
				"("+TimeActivity.COLUMN_START_DATE+" * 1000) AS "+TimeActivity.COLUMN_START_DATE+", " +
				"("+TimeActivity.COLUMN_END_DATE+" * 1000) AS "+TimeActivity.COLUMN_END_DATE+", " +
				"("+TimeActivity.COLUMN_DURATION+" * 1000) AS "+TimeActivity.COLUMN_DURATION+" " +
				"FROM "+TimeActivity.TABLE_NAME+" WHERE _id = ?", 
				new String[] {Long.toString(id)});
		if(!cursor.moveToFirst()){
			return null;
		}
		TimeActivity ac = cursorToTimeActivity(cursor);
		cursor.close();
		return ac;
				
	}
	
	public Cursor getAllActivitiesCursor(){
		Cursor cursor = mDatabase.rawQuery("SELECT *,datetime("+TimeActivity.COLUMN_ACTIVITY_DATE+",'unixepoch','localtime') AS "+TimeActivity.COLUMN_ACTIVITY_DATE+"" +
				" FROM "+TimeActivity.TABLE_NAME+" ORDER BY "+TimeActivity.COLUMN_ACTIVITY_DATE+" DESC", null);
		
		return cursor;
	}
	
	public Cursor getAllActivitiesByLabelId(long id){
		if(id < 0){
			return null;
		}
		Cursor cursor = mDatabase.rawQuery("SELECT *,datetime("+TimeActivity.COLUMN_ACTIVITY_DATE+",'unixepoch','localtime') AS "+TimeActivity.COLUMN_ACTIVITY_DATE+"" +
				" FROM "+TimeActivity.TABLE_NAME+" WHERE "+TimeActivity.COLUMN_LABEL+"=? ORDER BY "+TimeActivity.COLUMN_ACTIVITY_DATE+" DESC", new String[] {Long.toString(id)});
		return cursor;
	}
	
	public long getDefaultLabelId(){
		Cursor cursor = mDatabase.rawQuery("SELECT _id FROM "+Label.TABLE_NAME+" WHERE "+Label.COLUMN_NAME+" = ?", new String[] {Label.DEFAULT_NAME});
		cursor.moveToNext();
		return cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
	}
	
	public Cursor getAllOngoingActivitiesCursor(){
		Cursor cursor = mDatabase.rawQuery("SELECT *,datetime("+TimeActivity.COLUMN_ACTIVITY_DATE+",'unixepoch','localtime') AS "+TimeActivity.COLUMN_ACTIVITY_DATE+"" +
				" FROM "+TimeActivity.TABLE_NAME+" WHERE "+TimeActivity.COLUMN_ONGOING+"=1 ORDER BY "+TimeActivity.COLUMN_ACTIVITY_DATE+" ASC", null);
		
		return cursor;		
	}
	
	public long getTotalDurationForLabel(long id){
		String sql = "SELECT SUM("+TimeActivity.COLUMN_DURATION+") AS duration " +
					 "FROM "+TimeActivity.TABLE_NAME+" " +
					 "WHERE "+TimeActivity.COLUMN_LABEL+" = ?";
		Cursor cursor = mDatabase.rawQuery(sql, new String[] {Long.toString(id)});
		if(!cursor.moveToNext()){
			return -1;
		}
		return cursor.getLong(cursor.getColumnIndexOrThrow("duration"));
	}
	
	private TimeActivity cursorToTimeActivity(Cursor cursor){
		TimeActivity ta = new TimeActivity();
			ta.setId(cursor.getLong(cursor.getColumnIndexOrThrow("_id"))); 
			ta.setCreated_on(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(TimeActivity.COLUMN_CREATED_ON))));
			ta.setActivity_date(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(TimeActivity.COLUMN_ACTIVITY_DATE))));
			ta.setStart_date(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(TimeActivity.COLUMN_START_DATE))));
			ta.setEnd_date(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(TimeActivity.COLUMN_END_DATE))));
			ta.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(TimeActivity.COLUMN_DESCRIPTION)));
			ta.setLabel_id(cursor.getLong(cursor.getColumnIndexOrThrow(TimeActivity.COLUMN_LABEL)));
			ta.setOngoing(cursor.getLong(cursor.getColumnIndexOrThrow(TimeActivity.COLUMN_ONGOING)) == 1);
			//ta.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(TimeActivity.COLUMN_DURATION)));
		return ta;
	}

}
