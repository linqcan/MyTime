package com.linqcan.mytime;

import com.linqcan.mytime.ManageLabelFragment.ManageLabelListener;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class NewLabelActivity extends FragmentActivity implements ManageLabelListener{
	
	private static void putLogMessage(String msg){
		MainActivity.putLogMessage("NewLabelActivity", msg);
	}	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		setContentView(R.layout.new_label_activity);
		ManageLabelFragment fragment = (ManageLabelFragment) getSupportFragmentManager().findFragmentById(R.id.new_label_fragment);
		fragment.setMode(ManageLabelFragment.MODE.ADD);
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
