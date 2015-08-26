package com.gongshw.plan.android.activity;

import com.gongshw.plan.service.Unit;

import java.util.Date;

import static com.gongshw.plan.android.support.DateUtils.format;

/**
 * Author       : gongshw
 * Created At   : 15/2/16.
 */
public class MonthListActivity extends PlanListActivity {
	@Override
	protected Navi getNavi() {
		return Navi.MONTH_PLANS;
	}

	@Override
	protected String getTitle(long index) {
		long indexToday = Unit.MONTH.toIndex(new Date());
		int indexDelta = (int) (index - indexToday);
		switch (indexDelta) {
			case -1:
				return "上个月";
			case 0:
				return "本月";
			case 1:
				return "下个月";
			default:
				return String.valueOf(Math.abs(indexDelta)) + "个月" + (indexDelta > 0 ? "后" : "前");
		}
	}

	@Override
	protected String getDescription(long index) {
		return format(Unit.MONTH.startDateTimeOf(index)) + " 至 " + format(Unit.MONTH.endDateTimeOf(index));
	}
}
