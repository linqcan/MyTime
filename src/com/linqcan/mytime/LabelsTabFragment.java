package com.linqcan.mytime;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class LabelsTabFragment extends android.support.v4.app.ListFragment{
	
	private static void putLogMessage(String msg){
		MainActivity.putLogMessage("LabelsTabFragment", msg);
	}
		
	private DatabaseProvider mDatabase;
	private Context mContext;
	private ManageLabelsListener mListener;
	
	public interface ManageLabelsListener{
		public void startNewLabel();
		public void viewLabel(long id);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		mDatabase = new DatabaseProvider(mContext);
		try{
			mListener = (ManageLabelsListener) activity;
		}
		catch(ClassCastException e){
			throw new ClassCastException("Interface ManageLabelsListener is not implemented by activity "+activity.toString());
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		setRetainInstance(true); //Solved the issue with multiple menus
		putLogMessage("onCreateView called");
		return inflater.inflate(R.layout.tab_labels, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		putLogMessage("onActivityCreated called");
    	getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    	((TextView)getListView().getEmptyView()).setText(getString(R.string.empty_text_labels));
	}
	
	private void populateListView(){
		putLogMessage("populateListView");
		mDatabase.open();
		SimpleCursorAdapter adapter = mDatabase.getAllLabelsAdapter();
		setListAdapter(adapter);
		mDatabase.close();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.tab_labels, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_addlabel:
				mListener.startNewLabel();
				return true;
			
			default:
				return super.onOptionsItemSelected(item);				
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		mListener.viewLabel(id);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		populateListView();
	}
}
