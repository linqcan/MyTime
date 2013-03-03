package com.linqcan.mytime;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class OngoingTabFragment extends ListFragment{
	
	private static void putLogMessage(String msg){
		MainActivity.putLogMessage("OngoingTabFragment", msg);
	}
	
	private DatabaseProvider mDatabase;
	private Context mContext;
	private ManageActivitiesListener mListener;
	
	public interface ManageActivitiesListener{
		public void startNewActivity();
		public void viewActivity(long id);
	}
	
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
		View v = inflater.inflate(R.layout.tab_ongoing, container, false);
		setHasOptionsMenu(true);
		setRetainInstance(true); // TODO Evaluate the use of this one
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		putLogMessage("onActivityCreated");
    	getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    	//Hack since setEmptyText does not work.
		((TextView)getListView().getEmptyView()).setText(getString(R.string.empty_text_ongoing));
	}
	
	private void populateListView(){
		putLogMessage("populateListView");
		mDatabase.open();
		Cursor cursor = mDatabase.getAllOngoingActivitiesCursor();
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				mContext, 
				R.layout.label_list_item, 
				cursor, 
				new String[] {TimeActivity.COLUMN_ACTIVITY_DATE}, 
				new int[] {R.id.text1}, 
				0);
		setListAdapter(adapter);
		mDatabase.close();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.tab_ongoing, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_addactivity:
				mListener.startNewActivity();
				return true;		
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		mListener.viewActivity(id);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		populateListView();
	}
}
