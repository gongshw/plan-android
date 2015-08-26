package com.gongshw.plan.android.activity;

import com.gongshw.plan.service.Unit;

import java.util.Date;

import static com.gongshw.plan.android.support.DateUtils.format;

/**
 * Author       : gongshw
 * Created At   : 15/2/16.
 */
public class WeekPlanListActivity extends PlanListActivity {
	@Override
	protected Navi getNavi() {
		return Navi.WEEK_PLANS;
	}

	@Override
	protected String getTitle(long index) {
		long indexToday = Unit.WEEK.toIndex(new Date());
		int indexDelta = (int) (index - indexToday);
		switch (indexDelta) {
			case -1:
				return "上周";
			case 0:
				return "本周";
			case 1:
				return "下周";
			default:
				return String.valueOf(Math.abs(indexDelta)) + "周" + (indexDelta > 0 ? "后" : "前");
		}
	}

	@Override
	protected String getDescription(long index) {
		return format(Unit.WEEK.startDateTimeOf(index)) + " 至 " + format(Unit.WEEK.endDateTimeOf(index));
	}
}
