package com.linqcan.mytime;

import com.linqcan.mytime.ViewLabelFragment.ViewLabelListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;

public class ViewLabelActivity extends FragmentActivity implements ViewLabelListener {
	
	private void putLogMessage(String msg){
		MainActivity.putLogMessage("ViewLabelActivity", msg);
	}
	
	private static final int REQ_EDIT_LABEL = 0;
	//private static final int REQ_VIEW_ACTIVTY = 1;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.view_label_activity);
		Intent intent = getIntent();
		if(intent != null){
			long id = intent.getLongExtra("id", -1);
			putLogMessage("LabelId "+Long.toString(id));
			if(id > -1){
				ViewLabelFragment fragment = (ViewLabelFragment) getSupportFragmentManager().findFragmentById(R.id.view_label_fragment);
				fragment.setLabelId(id);
			}
			else{
				putLogMessage("Label id <= -1! Finishing activity!"); //TODO Throw something?
				finish();
			}
		}
		else{
			putLogMessage("No arguments!");
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			case REQ_EDIT_LABEL:
				switch(resultCode){
					case 0:
						// Label has been updated, restart this activity
						Intent intent = getIntent();
						finish();
						startActivity(intent);
						break;
					case 2:
						//Delete of label has been done. Close this activity
						onDelete();
				}
				break;
		}
	}
	
	@Override
	public void editLabel(long id) {
		//Start the EditLabelActivity using intent.
		Intent intent = new Intent(getApplicationContext(), EditLabelActivity.class);
		intent.putExtra("id", id);
		startActivityForResult(intent,REQ_EDIT_LABEL);
	}
	
	@Override
	public void onDelete() {
		setResult(2);
		finish();
	}

	@Override
	public void viewActivity(long id) {
		//Start the ViewActivityActivity using intent.
		Intent intent = new Intent(getApplicationContext(), ViewActivityActivity.class);
		intent.putExtra("id", id);
		startActivity(intent);		
	}
}
