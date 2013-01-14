package com.linqcan.mytime;

import com.linqcan.mytime.LabelsTabFragment.ManageLabelsListener;
import com.linqcan.mytime.OngoingTabFragment.ManageActivitiesListener;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends FragmentActivity implements TabListener,
																ManageActivitiesListener,
																ManageLabelsListener{
	
	private static void putLogMessage(String msg){
		putLogMessage("MainActivity", msg);
	}
	
	public static void putLogMessage(String tag, String msg){
		Log.d("Linqcan::"+tag,msg);
	}

	private enum TabTag{
		RECENT, LABELS, ACTIVITIES
	}
	
	public static final int REQ_NEW_ACTIVITY = 0;
	public static final int REQ_VIEW_ACTIVITY = 1;
	public static final int REQ_NEW_LABEL = 10;
	public static final int REQ_VIEW_LABEL = 11;
	
	private ViewPager mViewPager;
	private PagerAdapter mPageAdapter;
	private static int mPosition;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mPageAdapter = new MyPageAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mPageAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				getActionBar().setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {				
			}
		});
		
		Tab tab = actionBar.newTab().setText(R.string.tab_recent).setTabListener(this).setTag(TabTag.RECENT);
		actionBar.addTab(tab);
		
		tab = actionBar.newTab().setText(R.string.tab_labels).setTabListener(this).setTag(TabTag.LABELS);
		actionBar.addTab(tab);
		
		tab = actionBar.newTab().setText(R.string.tab_activities).setTabListener(this).setTag(TabTag.ACTIVITIES);
		actionBar.addTab(tab);
		
		if(savedInstanceState != null){
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab"));
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
	
	public static class MyPageAdapter extends FragmentStatePagerAdapter{
		
		public MyPageAdapter(android.support.v4.app.FragmentManager fm){
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			switch(position){
				case 0:
					return new OngoingTabFragment();
				case 1:
					return new LabelsTabFragment();
				case 2:
					return new ActivitiesTabFragment();
				default:
					return null;
			}
		}
		
		@Override
		public int getCount() {
			return 3;
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		putLogMessage("onPause");
		mPosition = getActionBar().getSelectedNavigationIndex();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		putLogMessage("onResume");
		getActionBar().setSelectedNavigationItem(mPosition);
	}

	@Override
	public void startNewActivity() {
		Intent intent = new Intent(getApplicationContext(), ViewActivityActivity.class);
		startActivityForResult(intent, REQ_NEW_ACTIVITY);	
	}

	@Override
	public void viewActivity(long id) {
		Intent intent = new Intent(getApplicationContext(), ViewActivityActivity.class);
		intent.putExtra("id", id);
		startActivityForResult(intent, REQ_VIEW_ACTIVITY);		
	}

	@Override
	public void startNewLabel() {
		Intent intent = new Intent(getApplicationContext(), NewLabelActivity.class);
		startActivityForResult(intent, REQ_NEW_LABEL);
	}

	@Override
	public void viewLabel(long id) {
		Intent intent = new Intent(getApplicationContext(), ViewLabelActivity.class);
		intent.putExtra("id", id);
		startActivityForResult(intent, REQ_VIEW_LABEL);		
	}
}
