package com.gongshw.plan.android.activity;

import com.gongshw.plan.service.Unit;

import java.util.Date;

import static com.gongshw.plan.android.support.DateUtils.format;

/**
 * Author       : gongshw
 * Created At   : 15/2/16.
 */
public class DayPlanListActivity extends PlanListActivity {
	@Override
	protected Navi getNavi() {
		return Navi.DAY_PLANS;
	}

	@Override
	protected String getTitle(long index) {
		long indexNow = Unit.DAY.toIndex(new Date());
		int indexDelta = (int) (index - indexNow);
		switch (indexDelta) {
			case -2:
				return "前天";
			case -1:
				return "昨天";
			case 0:
				return "今天";
			case 1:
				return "明天";
			case 2:
				return "后天";
			default:
				return String.valueOf(Math.abs(indexDelta)) + "天" + (indexDelta > 0 ? "后" : "前");
		}
	}

	@Override
	protected String getDescription(long index) {
		Date thatDay = Unit.DAY.endDateTimeOf(index);
		return format(thatDay);
	}
}
