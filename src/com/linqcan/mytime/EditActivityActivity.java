package com.linqcan.mytime;

import com.linqcan.mytime.EditActivityFragment.EditActivityListener;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class EditActivityActivity extends FragmentActivity implements EditActivityListener{
	
	private void putLogMessage(String msg){
		MainActivity.putLogMessage("EditActivityActivity", msg);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		//savedInstance, handle somehow later
		
		Bundle extras = getIntent().getExtras();
		if(!extras.containsKey("id")){
			return;
		}
		
		setContentView(R.layout.edit_activity_activity);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayUseLogoEnabled(false);
		
		long id = extras.getLong("id");
		putLogMessage("Received id " + Long.toString(id));
		EditActivityFragment fragment = (EditActivityFragment) getSupportFragmentManager().findFragmentById(R.id.edit_activity_fragment);
		if(fragment != null){
			fragment.setActivityId(id);
		}
	}


	@Override
	public void onSave() {
		setResult(0);
		finish();		
	}


	@Override
	public void onCancel() {
		setResult(1);
		finish();		
	}
}
