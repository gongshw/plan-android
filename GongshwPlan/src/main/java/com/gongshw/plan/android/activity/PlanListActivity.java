package com.gongshw.plan.android.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.gongshw.plan.android.R;
import com.gongshw.plan.android.support.BackgroundJob;
import com.gongshw.plan.service.PlanRecord;
import com.gongshw.plan.service.ServiceClient;
import com.gongshw.plan.service.Unit;

import java.util.Date;
import java.util.List;

/**
 * Author       : gongshw
 * Created At   : 15/2/16.
 */
public abstract class PlanListActivity extends DrawerActivitySupport {

	private int viewCacheCount = 2;

	private ServiceClient serviceClient = ServiceClient.getInstance();

	private long index;

	private ViewPager pager;

	private PagedPlanAdapter pagedPlanAdapter;

	abstract protected String getTitle(long index);

	abstract protected String getDescription(long index);

	@Override
	protected int getLayout() {
		return R.layout.paged_swipe_view;
	}

	private static Unit[] UNITS = new Unit[]{null, Unit.DAY, Unit.WEEK, Unit.MONTH};

	private Unit getUnit() {
		return UNITS[getNavi().ordinal()];
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pager = (ViewPager) findViewById(R.id.pager_view);
		pagedPlanAdapter = new PagedPlanAdapter();
		pager.setAdapter(pagedPlanAdapter);
		Date now = new Date();
		pager.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						break;
				}
				return false;
			}
		});
		pager.setCurrentItem(viewCacheCount);
		index = getUnit().toIndex(now);
	}

	private void getPlans(final ListView listView, final long index) {
		executeAsyncTask(new BackgroundJob<List<PlanRecord>>() {
			@Override
			public List<PlanRecord> run() throws Exception {
				return serviceClient.getPlans(index, getUnit());
			}

			@Override
			public void handle(List<PlanRecord> plans) {
				listView.setAdapter(new ReadOnlyPlansAdapter(plans));
			}
		});
	}

	class ReadOnlyPlansAdapter extends ArrayAdapter<PlanRecord> {

		private LayoutInflater mInflater;

		public ReadOnlyPlansAdapter(List<PlanRecord> objects) {
			super(PlanListActivity.this, 0, objects);
			mInflater = (LayoutInflater) PlanListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		class ViewHolder {
			private ImageView color;
			private TextView text;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PlanRecord record = getItem(position);
			View view = null;
			if (record != null) {
				ViewHolder holder = new ViewHolder();
				view = mInflater.inflate(R.layout.read_only_plan_in_list, parent, false);
				holder.color = (ImageView) view.findViewById(R.id.color_tag);
				holder.text = (TextView) view.findViewById(R.id.plan_text_view);
				GradientDrawable tagDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.color_tag);
				tagDrawable.setColor(Color.parseColor(record.getColor()));
				holder.color.setImageDrawable(tagDrawable);
				holder.text.setText(record.getText());
				if (record.getFinished()) {
					holder.text.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
				}
			}
			return view;
		}
	}

	@Override
	public void onRefresh() {
		pagedPlanAdapter.updateView(pager.getCurrentItem());
	}

	class PagedPlanAdapter extends PagerAdapter {

		private void updateView(int page) {
			View view = views[page];
			long indexOfPage = index + page - viewCacheCount;
			final ListView listView = (ListView) view.findViewById(R.id.plan_list_view);
			PlanListActivity.super.setCompatibleWith(listView);
			getPlans(listView, indexOfPage);
			TextView description = (TextView) view.findViewById(R.id.time_description_text);
			description.setText(getDescription(indexOfPage));
			getPlans((ListView) views[page].findViewById(R.id.plan_list_view), indexOfPage);
		}

		private View views[] = new View[viewCacheCount * 2 + 1];

		@Override
		public int getCount() {
			return views.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getTitle(index + position - viewCacheCount);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			if (views[position] == null) {
				views[position] = getLayoutInflater().inflate(R.layout.activity_plan_list, null);
			}
			container.addView(views[position]);
			updateView(position);
			return views[position];
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views[position]);
		}
	}
}
