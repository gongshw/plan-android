package com.gongshw.plan.android.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Author       : gongshw
 * Created At   : 15/2/17.
 */
public class VirtualKeyboardMeasureLayout extends LinearLayout {

	private VirtualKeyboardListener virtualKeyboardListener;

	public VirtualKeyboardMeasureLayout(Context context) {
		super(context);
	}

	public VirtualKeyboardMeasureLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VirtualKeyboardMeasureLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public VirtualKeyboardMeasureLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}


	private boolean isVirtualKeyboardShow = false;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if (virtualKeyboardListener == null) {
			return;
		}

		final int proposedHeight = MeasureSpec.getSize(heightMeasureSpec);
		final int actualHeight = getHeight();

		if (actualHeight > proposedHeight && !isVirtualKeyboardShow) {
			produceVirtualKeyboardEvent(true);
		} else if (actualHeight < proposedHeight && isVirtualKeyboardShow) {
			produceVirtualKeyboardEvent(false);
		}
	}

	public void setVirtualKeyboardListener(VirtualKeyboardListener virtualKeyboardListener) {
		this.virtualKeyboardListener = virtualKeyboardListener;
	}

	public interface VirtualKeyboardListener {
		void onVirtualKeyboardShow();

		void onVirtualKeyboardHide();
	}

	private void produceVirtualKeyboardEvent(boolean show) {
		new OnMeasureTask(show).execute();
	}


	class OnMeasureTask extends AsyncTask<Void, Void, Void> {

		private boolean show;

		OnMeasureTask(boolean show) {
			this.show = show;
		}

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			isVirtualKeyboardShow = show;
			if (show) {
				virtualKeyboardListener.onVirtualKeyboardShow();
			} else {
				virtualKeyboardListener.onVirtualKeyboardHide();
			}
		}
	}

}
