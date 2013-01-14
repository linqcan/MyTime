package com.linqcan.mytime;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class ManageLabelFragment extends Fragment {
	
	public interface ManageLabelListener{
		public void onSave();
		public void onCancel();
	}
	
	private static void putLogMessage(String msg){
		MainActivity.putLogMessage("ManageLabelFragment", msg);
	}
	
	public static enum MODE{
		ADD, EDIT
	}
	
	private DatabaseProvider mDatabase;
	private ManageLabelListener mListener;
	private Label mLabelItem;
	private long mLabelId;
	private static MODE mMode;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		putLogMessage("onAttach");
		try{
			mListener = (ManageLabelListener) activity;
		}
		catch(ClassCastException e){
			putLogMessage("Activity "+ activity.toString() +" does not implement ManageLabelListener");
		}
		mDatabase = new DatabaseProvider(activity);
		mDatabase.open();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		putLogMessage("onCreateView");
		setRetainInstance(true); //TODO Evaluate the use of this one
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.new_label_fragment, container);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		switch(mMode){
			case EDIT:
				if(mLabelId > -1){
					mLabelItem = mDatabase.getLabelById(mLabelId);
				}
				if(mLabelItem != null){
					setFields();
				}
				break;
		default:
			break;
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		putLogMessage("onCreateOptionsMenu");
		inflater.inflate(R.menu.new_label, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_save:
				putLogMessage("Save pressed");
				//Save something
				switch(mMode){
					case ADD:
						return addItem();
					case EDIT:
						return updateItem();
					default:
						return false;
				}
			case R.id.menu_cancel:
				putLogMessage("Cancel pressed");
				mListener.onCancel();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	private void setFields(){
		EditText name = (EditText) getView().findViewById(R.id.new_label_name);
		name.setText(mLabelItem.getName());
	}
	
	private boolean addItem(){
		if(fieldsAreOk()){
			EditText name = (EditText) getView().findViewById(R.id.new_label_name);
			Label label = mDatabase.insertLabel(new Label(name.getText().toString()));
			if(label != null){
				mListener.onSave();
				return true;
			}
			else{
				Toast.makeText(getActivity(), "The name have to be unique!", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		else{
			Toast.makeText(getActivity(), "Empty field!", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	private boolean updateItem(){
		if(fieldsAreOk()){
			EditText name = (EditText) getView().findViewById(R.id.new_label_name);
			Label label = new Label(mLabelItem.getId(),name.getText().toString());
			boolean result = mDatabase.updateLabel(label);
			if(result){
				mListener.onSave();
				return true;
			}
			else{
				Toast.makeText(getActivity(), "Insertion failed!", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		return false;
	}
		
	private boolean fieldsAreOk(){
		EditText name = (EditText) getView().findViewById(R.id.new_label_name);
		return name.getText().length() > 0;
	}
	
	public void setLabelId(long id){
		mLabelId = id;
	}
	
	public void setMode(ManageLabelFragment.MODE mode){
		putLogMessage("setMode called");
		mMode = mode;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mDatabase.close();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mDatabase.open();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		putLogMessage("onDestroy");
	}
}
