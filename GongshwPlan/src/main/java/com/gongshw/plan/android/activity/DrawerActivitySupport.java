package com.gongshw.plan.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.gongshw.plan.android.R;
import com.gongshw.plan.android.fragment.DrawerFragment;
import com.gongshw.plan.android.support.BackgroundJob;
import com.gongshw.plan.android.widget.VirtualKeyboardMeasureLayout;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author       : gongshw
 * Created At   : 15/2/10.
 */
public abstract class DrawerActivitySupport extends Activity
		implements DrawerFragment.NaviSelectListener, VirtualKeyboardMeasureLayout.VirtualKeyboardListener, SwipeRefreshLayout.OnRefreshListener {

	Logger logger = Logger.getLogger("ServiceClient");

	private Executor executor = Executors.newFixedThreadPool(5);

	private ActionBarDrawerToggle drawerToggle;

	private DrawerFragment drawerFragment;

	private DrawerLayout drawerLayout;

	private SwipeRefreshLayout container;

	private TextView toastView;

	protected abstract int getLayout();

	protected abstract Navi getNavi();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getNavi().getTitle());
		setContentView(R.layout.main_drawer_layout);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setActionBar(toolbar);

		drawerLayout = (DrawerLayout) findViewById(R.id.left_drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(
				this,
				drawerLayout,
				R.string.app_name,
				R.string.app_name);
		drawerLayout.setDrawerListener(drawerToggle);

		drawerFragment = (DrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
		drawerFragment.setSelectListener(this);
		drawerFragment.setPosition(getNavi().ordinal());

		container = (SwipeRefreshLayout) findViewById(R.id.frame_container);
		View v = View.inflate(this, getLayout(), null);
		v.setClickable(true);
		container.addView(v);
		container.setOnRefreshListener(this);
		toastView = (TextView) findViewById(R.id.toast_text_view);

		VirtualKeyboardMeasureLayout measureLayout =
				(VirtualKeyboardMeasureLayout) findViewById(R.id.virtual_keyboard_measure_layout);
		measureLayout.setVirtualKeyboardListener(this);
	}


	@Override
	public void onBackPressed() {
		View v = drawerFragment.getView();
		assert v != null;
		if (drawerLayout.isDrawerOpen(v)) {
			drawerLayout.closeDrawers();
		} else {
			this.finish();
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}

	protected enum Navi {
		ACTIVE_PLANS(R.string.active_plans),
		DAY_PLANS(R.string.day_plans),
		WEEK_PLANS(R.string.week_plans),
		MONTH_PLANS(R.string.month_plans);
		private int title;

		Navi(int title) {
			this.title = title;
		}

		public int getTitle() {
			return title;
		}
	}

	@Override
	public void onNaviSelect(int position) {
		if (position != getNavi().ordinal()) {
			Intent i = null;
			switch (position) {
				case 0:
					i = new Intent(this, ActivePlanActivity.class);
					break;
				case 1:
					i = new Intent(this, DayPlanListActivity.class);
					break;
				case 2:
					i = new Intent(this, WeekPlanListActivity.class);
					break;
				case 3:
					i = new Intent(this, MonthListActivity.class);
			}
			this.startActivity(i);
			this.finish();
		}
	}

	protected <T> void executeAsyncTask(final BackgroundJob<T> background) {
		showLoad();
		new AsyncTask<Void, Void, T>() {
			@Override
			protected T doInBackground(Void... params) {
				try {
					return background.run();
				} catch (Exception e) {
					onException(e);
					return null;
				}
			}

			@Override
			protected void onPostExecute(T result) {
				hideLoad();
				background.handle(result);
			}
		}.executeOnExecutor(executor);
	}

	protected void executeDelayTask(final long delay, final Runnable task) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(delay);
				} catch (Exception e) {
					onException(e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				task.run();
			}
		}.executeOnExecutor(executor);
	}

	private void showLoad() {

		executeDelayTask(0, new Runnable() {
			@Override
			public void run() {
				if (!container.isRefreshing()) {
					container.setRefreshing(true);
				}
			}
		});
	}

	private void hideLoad() {
		container.setRefreshing(false);
	}


	private int toastCount = 0;

	protected synchronized void showToast(String text) {
		toastView.setText(text);
		toastView.setVisibility(View.VISIBLE);
		final int toastMark = ++toastCount;
		executeDelayTask(3000, new Runnable() {
			@Override
			public void run() {
				if (toastCount == toastMark) {
					toastView.setVisibility(View.GONE);
				}
			}
		});
	}

	protected void setCompatibleWith(final ListView listView) {
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int topRowVerticalPosition =
						(listView == null || listView.getChildCount() == 0) ?
								0 : listView.getChildAt(0).getTop();
				setRefreshable(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
			}
		});
	}

	public void onException(final Exception e) {
		e.printStackTrace();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showToast("出现错误!: " + e.getMessage());
			}
		});
	}

	@Override
	public void onVirtualKeyboardShow() {
		logger.log(Level.INFO, "onVirtualKeyboardShow");
	}

	@Override
	public void onVirtualKeyboardHide() {
		logger.log(Level.INFO, "onVirtualKeyboardHide");
	}

	@Override
	public void onRefresh() {
		container.setRefreshing(false);
	}

	public void setRefreshable(boolean enable) {
		container.setEnabled(enable);
	}

}
