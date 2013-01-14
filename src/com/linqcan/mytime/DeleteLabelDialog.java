package com.linqcan.mytime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class DeleteLabelDialog extends DialogFragment {
	
	private static void putLogMessage(String msg){
		MainActivity.putLogMessage("DeleteLabelDialog", msg);
	}
	
	public interface DeleteLabelDialogListener{
		public void onDeleteConfirmed();
		public void onCancelDialog();
	}
	
	private DeleteLabelDialogListener mListener;
	private String mFragTag;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		putLogMessage("onCreate called");
		try{
			mListener = (DeleteLabelDialogListener) getFragmentManager().findFragmentByTag(mFragTag);
		}
		catch(ClassCastException e){
			throw new ClassCastException("Fragment does not implement the DeleleLabelDialogListener");
		}
		if(mListener == null){
			putLogMessage("mListener == null, Fragment has not specified a tag name?");
		}
	}
	
	public void show(FragmentManager manager, String tag, String fragtag) {
		mFragTag = fragtag;
		show(manager, tag);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle(R.string.delete_label_title);
		builder.setMessage(R.string.delete_label_message);
		
		builder.setPositiveButton(R.string.delete_dialog_positive, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onDeleteConfirmed();
			}
		});
		
		builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onCancelDialog();
			}
		});
		
		return builder.create();
		
	}

}
