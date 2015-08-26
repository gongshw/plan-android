package com.gongshw.plan.android.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

/**
 * Author       : gongshw
 * Created At   : 15/2/16.
 */
public class NaviItemView extends CheckedTextView {
	public NaviItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public NaviItemView(Context context) {
		super(context);
	}

	public NaviItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NaviItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void setChecked(boolean checked) {
		super.setChecked(checked);
		if (checked) {
			setBackgroundColor(Color.parseColor("#C3C3C3"));
		} else {
			setBackgroundColor(Color.WHITE);
		}
	}
}
