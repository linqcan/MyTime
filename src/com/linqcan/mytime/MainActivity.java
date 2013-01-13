package com.linqcan.mytime;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity implements TabListener{
	
	private static void putLogMessage(String msg){
		Log.d("Linqcan::MainActivity",msg);
	}

	private enum TabTag{
		RECENT, LABELS, ACTIVITIES
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		Tab tab = actionBar.newTab().setText(R.string.tab_recent).setTabListener(this).setTag(TabTag.RECENT);
		actionBar.addTab(tab);
		
		tab = actionBar.newTab().setText(R.string.tab_labels).setTabListener(this).setTag(TabTag.LABELS);
		actionBar.addTab(tab);
		
		tab = actionBar.newTab().setText(R.string.tab_activities).setTabListener(this).setTag(TabTag.ACTIVITIES);
		actionBar.addTab(tab);
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		TabTag tag = (TabTag) tab.getTag();
		switch(tag){
			case RECENT :
				putLogMessage("Recent");
				break;
			case LABELS:
				putLogMessage("Labels");
				break;
			case ACTIVITIES:
				putLogMessage("Activities");
				break;
			default:
				putLogMessage("Unknown tab tag");
				break;	
		}		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

}
