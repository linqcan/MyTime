package com.linqcan.mytime;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditActivityFragment extends Fragment implements OnItemSelectedListener {
	
	private void putLogMessage(String msg){
		MainActivity.putLogMessage("EditActivityFragment", msg);
	}
	
	public interface EditActivityListener{
		public void onSave();
		public void onCancel();
	}
	
	private long mActivityId = -1;
	private TimeActivity mCurrentTimeActivity;
	private DatabaseProvider mDatabase;
	
	private EditActivityListener mListener;
	private Activity mActivity;
	private Button mEndDateButton;
	private Button mEndTimeButton;
	private EditText mDescTxt;
	private Spinner mLabelsSpinner;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		putLogMessage("onAttach");
		mActivity = activity;
		mDatabase = new DatabaseProvider(activity);
		mDatabase.open();
		try{
			mListener = (EditActivityListener) activity;
		}
		catch(ClassCastException e){
			throw new ClassCastException("EditActivityListener is not implemented by activity " + e.getMessage());
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if(savedInstanceState != null){
			if(savedInstanceState.containsKey("activity")){
				mCurrentTimeActivity = (TimeActivity) savedInstanceState.getSerializable("activity");
			}
		}
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.edit_activity_fragment, container, false);
		//Cache views
		mEndDateButton = (Button) v.findViewById(R.id.btn_end_date);
		mEndTimeButton = (Button) v.findViewById(R.id.btn_end_time);
		mDescTxt = (EditText) v.findViewById(R.id.txt_activity_desc);
		mLabelsSpinner = (Spinner) v.findViewById(R.id.spinner_labels);
		return v;
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(mActivityId == -1){
			Toast.makeText(mActivity, "No id set, closing", Toast.LENGTH_LONG).show(); //Throw something instead?!
			return;
		}
		mCurrentTimeActivity = mDatabase.getActivityById(mActivityId);
		if(mCurrentTimeActivity == null){
			Toast.makeText(mActivity, "Could not find activity", Toast.LENGTH_LONG).show(); //Throw something instead?!
			return;
		}
		putLogMessage("Found activity with id " + Long.toString(mActivityId));

		setEndButton(mEndDateButton, mCurrentTimeActivity.getEnd_date());
		
		setEndButton(mEndTimeButton, mCurrentTimeActivity.getEnd_date());
				
		populateSpinner();
		
		mDescTxt.setText(mCurrentTimeActivity.getDescription());
		
		mEndDateButton.setOnClickListener(new onClickDateButtonListener());
		mEndTimeButton.setOnClickListener(new onClickTimeButtonListener());
		
		setSpinner();
		
		mLabelsSpinner.setOnItemSelectedListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mDatabase.open();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mDatabase.close();
	}
	
	private void populateSpinner(){
		// Populates the label spinner
		Cursor cursor = mDatabase.getAllLabelsCursor();
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(mActivity, 
				android.R.layout.simple_spinner_item, 
				cursor, 
				new String[] {"name"}, 
				new int[] {android.R.id.text1}, 
				0);
		adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		mLabelsSpinner.setAdapter(adapter);
	}
	
	private void setSpinner(){
		//Set the spinner selection to the correct item
		long lId = mCurrentTimeActivity.getLabel_id();
		long sId = -1;
		for(int i = 0; i < mLabelsSpinner.getCount(); i++){
			sId = mLabelsSpinner.getItemIdAtPosition(i);
			if(sId == lId){
				mLabelsSpinner.setSelection(i);
				break;
			}
		}
	}
	
	private static void setEndButton(Button btn, Date date){
		if(btn.getId() == R.id.btn_end_date){
			String endDate = DateFormat.format("E MMM dd, yyyy", date).toString();
			btn.setText(endDate);
		}
		else if(btn.getId() == R.id.btn_end_time){
			String endTime = DateFormat.format("kk:mm", date).toString();
			btn.setText(endTime);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("activity", mCurrentTimeActivity);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.save_cancel, menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_cancel:
				mListener.onCancel();
				return true;
			case R.id.menu_save:
				updateDatabase();
				mListener.onSave();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void updateDatabase(){
		prepareSave();
		boolean result = mDatabase.updateActivity(mCurrentTimeActivity);
		if(!result){
			Toast.makeText(mActivity, "Database error: Activity could not be updated", Toast.LENGTH_LONG).show();
		}
	}
	
	private void prepareSave(){
		String desc = mDescTxt.getText().toString();
		mCurrentTimeActivity.setDescription(desc);
	}
	
	public void setActivityId(long id){
		mActivityId = id;
	}


	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long id) {
		mCurrentTimeActivity.setLabel_id(id);		
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// Derp
	}
	
	private class onClickDateButtonListener implements OnClickListener{
		
		@Override
		public void onClick(View v) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(mCurrentTimeActivity.getEnd_date());
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);

			DatePickerDialog dp = new DatePickerDialog(getActivity(), new DateListener(), year, month, day);
			dp.setCanceledOnTouchOutside(true);
			dp.show();
		}
	}
	
	private class onClickTimeButtonListener implements OnClickListener{
		
		@Override
		public void onClick(View v) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(mCurrentTimeActivity.getEnd_date());
			int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);

			TimePickerDialog tp = new TimePickerDialog(getActivity(), new TimeListener(), hourOfDay, minute, true);
			tp.setCanceledOnTouchOutside(true);
			tp.show();
		}
	}
	
	private class DateListener implements OnDateSetListener{
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Button btn = (Button) getActivity().findViewById(R.id.btn_end_date);

			Calendar cal = Calendar.getInstance();
			cal.setTime(mCurrentTimeActivity.getEnd_date());
			cal.set(year, monthOfYear, dayOfMonth);
			
			setEndButton(btn, cal.getTime());			
			mCurrentTimeActivity.setEnd_date(cal.getTime());
		}
	}
	
	private class TimeListener implements OnTimeSetListener{
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Button btn = (Button) getActivity().findViewById(R.id.btn_end_time);

			Calendar cal = Calendar.getInstance();
			cal.setTime(mCurrentTimeActivity.getEnd_date());
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE, minute);
			
			setEndButton(btn, cal.getTime());			
			mCurrentTimeActivity.setEnd_date(cal.getTime());			
		}
	}
}
