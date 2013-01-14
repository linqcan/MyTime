package com.linqcan.mytime;

import java.io.ObjectOutputStream.PutField;

import com.linqcan.mytime.DeleteLabelDialog.DeleteLabelDialogListener;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.TextView;

public class ViewLabelFragment extends ListFragment implements DeleteLabelDialogListener {
	
	private void putLogMessage(String msg){
		MainActivity.putLogMessage("ViewLabelFragment", msg);
	}
	
	private Label mLabelItem;
	private long mLabelId;
	private ViewLabelListener mListener;
	private DatabaseProvider mDatabase;
	private Activity mActivity;
	private boolean mIsDefaultLabel = false;
	
	//View cache
	private TextView mLabelInfo;
	
	public interface ViewLabelListener{
		public void editLabel(long id);
		public void onDelete();
		public void viewActivity(long id);
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			mListener = (ViewLabelListener) activity;
		}
		catch(ClassCastException e){
			putLogMessage("Activity "+ activity.toString() +" does not implement ViewLabelListener");
		}
		mActivity = activity;
		mDatabase = new DatabaseProvider(activity);
		mDatabase.open();		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.view_label_fragment, container, false);
		//Cache view
		mLabelInfo = (TextView) v.findViewById(R.id.label_info);
		setHasOptionsMenu(true);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		((TextView)getListView().getEmptyView()).setText(getString(R.string.empty_text_view_label));
		
		mLabelItem = mDatabase.getLabelById(mLabelId);

		mActivity.setTitle(mLabelItem.getName());
		
		if(mLabelItem.getId() == 1){
			//This is the "None" label, ensure no menu is created
			mIsDefaultLabel = true;
			mActivity.invalidateOptionsMenu();		
		}
		
		populateList();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mDatabase.open();
		updateLabelInfo();
		populateList();
	}	
	
	@Override
	public void onPause() {
		super.onPause();
		mDatabase.close();
	}
	
	private void populateList(){
		Cursor cursor = mDatabase.getAllActivitiesByLabelId(mLabelItem.getId());		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(mActivity,
				R.layout.activity_list_item, 
				cursor, 
				new String[] {TimeActivity.COLUMN_ACTIVITY_DATE,TimeActivity.COLUMN_DESCRIPTION,TimeActivity.COLUMN_DURATION},
				new int[] {R.id.text1,R.id.text2, R.id.text3}, 0);
		adapter.setViewBinder(new ActivityListViewBinder());
		setListAdapter(adapter);
	}
	
	private void updateLabelInfo(){
		long duration = mDatabase.getTotalDurationForLabel(mLabelId);
		mLabelInfo.setText(SharedHelpers.durationToString(duration));
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if(!mIsDefaultLabel){
			//Create menu for all labels other than "None"
			inflater.inflate(R.menu.view_label, menu);
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mListener.viewActivity(id);
	}
	
	public void setLabelId(long id){
		mLabelId = id;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_edit:
				mListener.editLabel(mLabelItem.getId());
				return true;
			case R.id.menu_delete:
				DeleteLabelDialog dialog = new DeleteLabelDialog();
				dialog.show(getFragmentManager(), null, getTag());
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onDeleteConfirmed() {
		boolean result = false;
		//Not only should we delete the label
		//we also have to remove all relations to activities
		int rows = mDatabase.removeLabelFromActivities(mLabelItem.getId());
		putLogMessage("Label cleared from "+Integer.toString(rows)+" activities");
		//Remove the label
		result = mDatabase.deleteLabel(mLabelItem.getId());
		if(result){
			mListener.onDelete();
		}
		else{
			Toast.makeText(mActivity, "Database error,  label could not be deleted", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onCancelDialog() {
		// Do nothing	
	}

}
