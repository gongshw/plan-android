package com.gongshw.plan.android.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.daimajia.swipe.SwipeLayout;
import com.gongshw.plan.android.BuildConfig;
import com.gongshw.plan.android.R;
import com.gongshw.plan.service.PlanRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Author       : gongshw
 * Created At   : 15/2/10.
 */
public class PlanListFragment extends Fragment {


	private PlanListListener listListener;

	private View view;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_active_plans, container);
		return view;
	}

	public void setPlans(List<PlanRecord> dayPlans, List<PlanRecord> weekPlans, List<PlanRecord> monthPlans) {
		List<ListItem> listItems = new ArrayList<>();
		addPlans(listItems, dayPlans, "每日计划", "当前没有每日计划");
		addPlans(listItems, weekPlans, "每周计划", "当前没有每周计划");
		addPlans(listItems, monthPlans, "每月计划", "当前没有每月计划");
		ListView planListView = (ListView) view.findViewById(R.id.grouped_plan_list_view);
		if (planListView.getAdapter() != null && (planListView.getAdapter() instanceof PlansAdapter)) {
			PlansAdapter adapter = (PlansAdapter) planListView.getAdapter();
			adapter.setNotifyOnChange(false);
			adapter.clear();
			adapter.addAll(listItems);
			adapter.notifyDataSetChanged();
		} else {
			planListView.setAdapter(new PlansAdapter(listItems));
		}
	}

	private void addPlans(List<ListItem> listItems, List<PlanRecord> plans, String header, String emptyHeader) {
		if (plans == null || plans.size() == 0) {
			listItems.add(new ListItem(emptyHeader));
		} else {
			listItems.add(new ListItem(header));
			for (PlanRecord record : plans) {
				listItems.add(new ListItem(record));
			}
		}
	}

	public void setListListener(PlanListListener listListener) {
		this.listListener = listListener;
	}


	static class ViewHolder {
		CheckBox checkBox;
		SwipeLayout swipeLayout;
		Button deleteBtn;
		Button upBtn;
		Button downBtn;
		ImageView tag;
	}

	class PlansAdapter extends ArrayAdapter<ListItem> {

		LayoutInflater mInflater;

		List<ListItem> items;

		int movedIndex = -1;

		public PlansAdapter(List<ListItem> objects) {
			super(getActivity(), 0, objects);
			items = objects;
			mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ListItem item = getItem(position);
			View view;
			ViewHolder holder;
			if (item.record != null) {
				final PlanRecord record = item.record;
				view = mInflater.inflate(R.layout.plan_item_in_list, parent, false);
				holder = new ViewHolder();
				holder.swipeLayout = (SwipeLayout) view.findViewById(R.id.plan_swipe_layout);
				holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
				holder.swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
				holder.swipeLayout.setDragDistance(96);
				if (position == movedIndex) {
					openSwipeLayoutDelay(holder.swipeLayout);
					movedIndex = -1;
				}
				holder.deleteBtn = (Button) view.findViewById(R.id.delete_plan_btn);
				holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						listListener.onPlanDelete(record);
					}
				});
				holder.upBtn = (Button) view.findViewById(R.id.move_up_plan_btn);
				holder.downBtn = (Button) view.findViewById(R.id.move_down_plan_btn);
				holder.checkBox = (CheckBox) view.findViewById(R.id.plan_finish_checkbox);
				setMoveBtn(position, holder, record);
				setCheckBox(holder, record);
				holder.tag = (ImageView) view.findViewById(R.id.color_tag);
				GradientDrawable tagDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.color_tag);
				try {
					tagDrawable.setColor(Color.parseColor(record.getColor()));
				} catch (IllegalArgumentException e) {
					tagDrawable.setColor(getResources().getColor(R.color.plan_null));
				}
				holder.tag.setImageDrawable(tagDrawable);
				return view;
			} else if (item.header != null) {
				view = mInflater.inflate(R.layout.plan_list_header, parent, false);
				TextView header = (TextView) view.findViewById(R.id.plan_list_header);
				header.setText(item.header);
				return view;
			}
			if (BuildConfig.DEBUG) {
				throw new AssertionError();
			}
			return null;
		}

		private void openSwipeLayoutDelay(final SwipeLayout swipeLayout) {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					return null;
				}

				@Override
				protected void onPostExecute(Void aVoid) {
					swipeLayout.open(false, false);
				}
			}.execute();
		}

		private void setCheckBox(ViewHolder holder, final PlanRecord record) {
			holder.checkBox.setText(record.getText());
			holder.checkBox.setChecked(record.getFinished());
			holder.checkBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final CheckBox checkBox = (CheckBox) v;
					if (checkBox.isChecked()) {
						record.setFinished(true);
						listListener.onPlanFinish(record);
					} else {
						checkBox.toggle();
						AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
						builder
								.setTitle("确定要将计划" + record.getText() + "标记为未完成?")
								.setPositiveButton("标记为未完成", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										checkBox.toggle();
										record.setFinished(false);
										listListener.onPlanRemoveFinish(record);
									}
								})
								.setNegativeButton("放弃标记", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
									}
								})
								.create().show();
					}
				}
			});
		}

		private void setMoveBtn(final int position, ViewHolder holder, final PlanRecord record) {
			final double sort_up;
			final double sort_down;
			if (position == 0 || getItem(position - 1).header != null) {
				holder.upBtn.setVisibility(View.GONE);
			} else {
				final double sort_up_1 = getItem(position - 1).record.getSort();
				if (position == 1 || getItem(position - 2).header != null) {
					sort_up = sort_up_1 - 1;
				} else {
					double sort_up_2 = getItem(position - 2).record.getSort();
					sort_up = (sort_up_1 + sort_up_2) / 2;
				}
				holder.upBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						invokeMovedListener(position, position - 1, record, sort_up);
					}
				});
			}
			if (position == getCount() - 1 || getItem(position + 1).header != null) {
				holder.downBtn.setVisibility(View.GONE);
			} else {
				double sort_down_1 = getItem(position + 1).record.getSort();
				if (position == getCount() - 2 || getItem(position + 2).header != null) {
					sort_down = sort_down_1 + 1;
				} else {
					double sort_down_2 = getItem(position + 2).record.getSort();
					sort_down = (sort_down_1 + sort_down_2) / 2;
				}
				holder.downBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						invokeMovedListener(position, position + 1, record, sort_down);
					}
				});
			}
		}

		private void invokeMovedListener(int oldPosition, int newPosition, PlanRecord record, double sort) {
			swapListItem(oldPosition, newPosition);
			movedIndex = newPosition;
			notifyDataSetChanged();
			listListener.onMovePlan(record, sort);
		}

		@Override
		public boolean isEnabled(int position) {
			return getItem(position).record != null && super.isEnabled(position);
		}


		private void swapListItem(int index1, int index2) {
			ListItem temp = items.get(index1);
			items.set(index1, items.get(index2));
			items.set(index2, temp);
		}
	}


	class ListItem {
		ListItem(String header) {
			this.header = header;
		}

		ListItem(PlanRecord record) {
			this.record = record;
		}

		String header;
		PlanRecord record;
	}

	public interface PlanListListener {
		void onPlanDelete(PlanRecord planRecord);

		void onPlanFinish(PlanRecord planRecord);

		void onPlanRemoveFinish(PlanRecord planRecord);

		void onMovePlan(PlanRecord plan, double sort);
	}

}
