package com.gongshw.plan.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.gongshw.plan.android.R;
import com.gongshw.plan.android.widget.ColorPickerItem;
import com.gongshw.plan.service.PlanMeta;
import com.gongshw.plan.service.Unit;

import java.util.Date;

/**
 * Author       : gongshw
 * Created At   : 15/2/10.
 */
public class PlanAdderFragment extends Fragment {

	private PlanAdderListener adderListener;
	private LinearLayout planArea;
	private RadioGroup radioGroup;
	private View view;

	private ColorPickerItem[] colorPickerItems = new ColorPickerItem[8];

	private String selectColor;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_plan_adder, container);
		setPlanAdder();
		int[] ids = new int[]{
				R.id.plan_null, R.id.plan_red, R.id.plan_orange, R.id.plan_yellow,
				R.id.plan_grey, R.id.plan_blue, R.id.plan_cyan, R.id.plan_green};
		View.OnClickListener clickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ColorPickerItem pickerItem = (ColorPickerItem) v;
				selectColor = pickerItem.getColor();
				for (ColorPickerItem picker : colorPickerItems) {
					if (picker != null && picker != pickerItem) {
						picker.setChecked(false);
					}
				}
			}
		};
		for (int i = 0; i < ids.length; i++) {
			ColorPickerItem pickerItem = (ColorPickerItem) view.findViewById(ids[i]);
			colorPickerItems[i] = pickerItem;
			pickerItem.setOnClickListener(clickListener);
			if (pickerItem.isChecked()) {
				selectColor = pickerItem.getColor();
			}
		}
		return view;
	}


	private void setPlanAdder() {
		final EditText editText = (EditText) view.findViewById(R.id.adding_plan_edit_text);
		planArea = (LinearLayout) view.findViewById(R.id.adding_plan_time_info_area);
		radioGroup = (RadioGroup) view.findViewById(R.id.unit_radios);
		editText.clearFocus();
		editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					String text = editText.getText().toString();
					if (text.length() > 0) {
						Unit unit = Unit.values()[getCheckedIndex()];
						PlanMeta planMeta = new PlanMeta();
						planMeta.setText(text);
						planMeta.setIndex((int) unit.toIndex(new Date()));
						planMeta.setRepeat(true);
						planMeta.setUnit(unit.toName());
						planMeta.setColor(selectColor);
						adderListener.onAddPlan(planMeta);
						editText.setText("");
					}
				}
				return false;
			}
		});
	}

	private int getCheckedIndex() {
		RadioButton checkedRadio = (RadioButton) view.findViewById(radioGroup.getCheckedRadioButtonId());
		return radioGroup.indexOfChild(checkedRadio);
	}

	public void setPlanEditorVisibility(int visibility) {
		planArea.setVisibility(visibility);
	}

	public void setAdderListener(PlanAdderListener adderListener) {
		this.adderListener = adderListener;
	}

	public interface PlanAdderListener {
		void onAddPlan(PlanMeta planMeta);
	}

}
