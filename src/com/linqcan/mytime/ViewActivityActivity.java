package com.linqcan.mytime;

import com.linqcan.mytime.ViewActivityFragment.MODE;
import com.linqcan.mytime.ViewActivityFragment.ViewActivityListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ViewActivityActivity extends FragmentActivity implements ViewActivityListener{
	
	private static void putLogMessage(String msg){
		MainActivity.putLogMessage("NewActivityActivity", msg);
	}
	
	private static final int REQ_EDIT_ACTIVITY = 0;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.view_activity_activity);
		
		Intent intent = getIntent();
		if(intent == null){
			return;
		}
		
		long id = intent.getLongExtra("id", -1);
		if(id > -1){
			ViewActivityFragment fragment = (ViewActivityFragment)getSupportFragmentManager().findFragmentById(R.id.new_activity_fragment);
			fragment.setMode(MODE.VIEW);
			fragment.setTaId(id);
		}
		
		setTitle(R.string.title_activity_view_activity);
	}

	@Override
	public void onDelete() {
		finish();
	}
	
	
	private void onUpdate() {
		Intent intent = getIntent();
		finish();
		startActivity(intent); //Restarts activity
	}

	@Override
	public void editActivity(long id) {
		Intent intent = new Intent(getApplicationContext(), EditActivityActivity.class);
		intent.putExtra("id", id);
		startActivityForResult(intent,REQ_EDIT_ACTIVITY);		
	}
	
	@Override
	public void onFinsihedActivity(long id) {
		Intent intent = new Intent(getApplicationContext(), ViewActivityActivity.class);
		intent.putExtra("id", id);
		finish();
		startActivity(intent);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
			case REQ_EDIT_ACTIVITY:
				switch(resultCode){
					case 0:
						onUpdate();
						break;
					case 2:
						onDelete();
						break;				
				}
		}
	}

}
