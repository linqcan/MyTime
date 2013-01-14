package com.linqcan.mytime;

import com.linqcan.mytime.ManageLabelFragment.ManageLabelListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class EditLabelActivity extends FragmentActivity implements ManageLabelListener {
	
	private static void putLogMessage(String msg){
		MainActivity.putLogMessage("EditLabelActivity", msg);
	}
	
	private long mLabelId;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.new_label_activity);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		
		Intent intent = getIntent();
		if(intent != null){
			long id = intent.getLongExtra("id", -1);
			//mLabelItem = (Label) intent.getSerializableExtra("label");
			if(mLabelId > -1){
				ManageLabelFragment fragment = (ManageLabelFragment) getSupportFragmentManager().findFragmentById(R.id.new_label_fragment);
				fragment.setMode(ManageLabelFragment.MODE.EDIT);
				fragment.setLabelId(id);
				return;
			}
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
