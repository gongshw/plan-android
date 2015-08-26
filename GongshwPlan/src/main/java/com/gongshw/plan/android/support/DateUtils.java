package com.gongshw.plan.android.support;

import android.text.format.DateFormat;

import java.util.Date;

/**
 * Author       : gongshw
 * Created At   : 15/2/18.
 */
public class DateUtils {
	private DateUtils() {
	}

	public static String format(Date date) {
		return DateFormat.format("y年MM月dd日", date).toString();
	}
}
