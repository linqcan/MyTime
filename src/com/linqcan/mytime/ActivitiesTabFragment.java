package com.linqcan.mytime;

import com.linqcan.mytime.OngoingTabFragment.ManageActivitiesListener;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.SimpleCursorAdapter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class ActivitiesTabFragment extends ListFragment{
	
	private static void putLogMessage(String msg){
		MainActivity.putLogMessage("ActivitiesTabFragment", msg);
	}
	
	private DatabaseProvider mDatabase;
	private Context mContext;
	private ManageActivitiesListener mListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		putLogMessage("onAttach");
		mContext = activity;
		mDatabase = DatabaseProvider.getInstance(activity.getApplicationContext());
		try{
			mListener = (ManageActivitiesListener) activity;
		}
		catch(ClassCastException e){
			throw new ClassCastException("Interface ManageActivitiesListener is not implemented by activity " + activity.toString());
		}
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		putLogMessage("onCreateView");
		setHasOptionsMenu(true);
		setRetainInstance(true); //TODO evaluate the use of this one
		return inflater.inflate(R.layout.tab_activities, container, false);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		putLogMessage("onActivityCreated");
    	getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    	((TextView)getListView().getEmptyView()).setText(getString(R.string.empty_text_activities));
	}
	
	private void populateListView(){
		putLogMessage("populateListView");
		mDatabase.open();
		Cursor cursor = mDatabase.getAllActivitiesCursor();
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				mContext, 
				R.layout.activity_list_item, 
				cursor, 
				new String[] {TimeActivity.COLUMN_ACTIVITY_DATE, TimeActivity.COLUMN_DESCRIPTION, TimeActivity.COLUMN_DURATION}, 
				new int[] {R.id.text1, R.id.text2, R.id.text3}, 
				0);
		adapter.setViewBinder(new ActivityListViewBinder());
		setListAdapter(adapter);
		mDatabase.close();
	}
	
	@Override
	public void onInflate(Activity activity, AttributeSet attrs,
			Bundle savedInstanceState) {
		super.onInflate(activity, attrs, savedInstanceState);
		putLogMessage("onInflate");
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		putLogMessage("onPrepareOptionsMenu");
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		putLogMessage("onCreateOptionsMenu");
		inflater.inflate(R.menu.tab_activities, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_addactivity:
				mListener.startNewActivity();
		}
		return super.onOptionsItemSelected(item);
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		mListener.viewActivity(id);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		putLogMessage("onPause");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		putLogMessage("onResume");
		populateListView();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		putLogMessage("onDestroy");
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		putLogMessage("onDetach");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		putLogMessage("onStart");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		putLogMessage("onStop");
	}
	
}
