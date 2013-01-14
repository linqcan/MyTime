package com.linqcan.mytime;

import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;


public class ActivityListViewBinder implements SimpleCursorAdapter.ViewBinder{

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		if(view.getId() == R.id.text3){
			TextView tv = (TextView) view;
			Long duration = cursor.getLong(columnIndex);
			tv.setText(SharedHelpers.durationToString(duration));
			return true;
		}
		else if (view.getId() == R.id.text2){
			TextView tv = (TextView) view;
			String description = cursor.getString(columnIndex);
			if(description == null){
				return false;
			}
			int end = 20;
			if(description.length() > 20){
				description = description.substring(0, end);
				description += "...";
			}
			tv.setText(description);
			return true;
		}
		return false;
	}
	
}