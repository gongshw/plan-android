package com.gongshw.plan.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ToggleButton;
import com.gongshw.plan.android.R;

/**
 * Author       : gongshw
 * Created At   : 15/2/19.
 */
public class ColorPickerItem extends ToggleButton {

	private int color;

	private GradientDrawable checkedDrawable;

	private GradientDrawable uncheckedDrawable;

	public ColorPickerItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerItem);
		color = a.getColor(R.styleable.ColorPickerItem_color_filter, R.color.plan_blue);
		checkedDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.color_checked);
		checkedDrawable.setColor(color);
		uncheckedDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.color_unchecked);
		uncheckedDrawable.setStroke((int) (5 * context.getResources().getDisplayMetrics().density), color);
		//uncheckedDrawable.setColor(color);
		setBackground(isChecked() ? checkedDrawable : uncheckedDrawable);
		a.recycle();
	}

	@Override
	public void setChecked(boolean checked) {
		setBackground(checked ? checkedDrawable : uncheckedDrawable);
		super.setChecked(checked);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText("", type);
	}

	@Override
	public void toggle() {
		if (!isChecked()) {
			super.toggle();
		}
	}

	public String getColor() {
		return String.format("#%06X", (0xFFFFFF & color));
	}
}
