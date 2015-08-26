package com.gongshw.plan.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.gongshw.plan.android.R;

import java.util.Arrays;

/**
 * Author       : gongshw
 * Created At   : 15/2/11.
 */
public class DrawerFragment extends Fragment implements AdapterView.OnItemClickListener {

	private ListView listView;

	private NaviSelectListener selectListener;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_drawer, container);
		listView = (ListView) v.findViewById(R.id.left_drawer_list);
		listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.navi_item,
				Arrays.asList(getString(R.string.active_plans), getString(R.string.day_plans),
						getString(R.string.week_plans), getString(R.string.month_plans))));
		listView.setOnItemClickListener(this);
		return v;
	}

	public void setPosition(int position) {
		listView.setItemChecked(position, true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (selectListener != null) {
			selectListener.onNaviSelect(position);
		}
	}

	public void setSelectListener(NaviSelectListener selectListener) {
		this.selectListener = selectListener;
	}

	public interface NaviSelectListener {
		void onNaviSelect(int position);
	}
}
