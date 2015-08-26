package com.gongshw.plan.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Author       : gongshw
 * Created At   : 15/2/9.
 */
public enum Unit {
	DAY {
		@Override
		public long toIndex(long timestamp) {
			timestamp += TIME_ZONE_FIX_SECONDS;
			return timestamp / DAY_SECONDS;
		}

		@Override
		public long startTimeOf(long index) {
			return DAY_SECONDS * index - TIME_ZONE_FIX_SECONDS;
		}

		@Override
		public long endTimeOf(long index) {
			return DAY_SECONDS * (index + 1) - 1 - TIME_ZONE_FIX_SECONDS;
		}
	}, WEEK {
		@Override
		public long toIndex(long timestamp) {
			timestamp += TIME_ZONE_FIX_SECONDS;
			return (timestamp + DAY_SECONDS * 3) / (DAY_SECONDS * 7);
		}

		@Override
		public long startTimeOf(long index) {
			return DAY_SECONDS * (7 * index - 3) - TIME_ZONE_FIX_SECONDS;
		}

		@Override
		public long endTimeOf(long index) {
			return DAY_SECONDS * (7 * index + 4) - 1 - TIME_ZONE_FIX_SECONDS;
		}
	}, MONTH {
		@Override
		public long toIndex(long timestamp) {
			timestamp += TIME_ZONE_FIX_SECONDS;
			GregorianCalendar c = new GregorianCalendar();
			c.setTimeZone(TimeZone.getTimeZone("UTC"));
			c.setTimeInMillis(timestamp * 1000);
			return (c.get(Calendar.YEAR) - 1970) * 12 + c.get(Calendar.MONTH);
		}

		@Override
		public long startTimeOf(long index) {
			int year = (int) (1970 + index / 12);
			int month = (int) (index % 12);
			Calendar c = new GregorianCalendar();
			c.setTimeZone(TimeZone.getTimeZone("UTC"));
			c.set(year, month, 1, 0, 0, 0);
			return c.getTimeInMillis() / 1000 - TIME_ZONE_FIX_SECONDS;
		}

		@Override
		public long endTimeOf(long index) {
			int year = (int) (1970 + index / 12);
			int month = (int) (index % 12) + 1;
			if (month == 12) {
				year += 1;
				month = 0;
			}
			Calendar c = new GregorianCalendar();
			c.setTimeZone(TimeZone.getTimeZone("UTC"));
			c.set(year, month, 1, 0, 0, 0);
			return c.getTimeInMillis() / 1000 - 1 - TIME_ZONE_FIX_SECONDS;
		}
	};

	private static final long TIME_ZONE_FIX_SECONDS = +60 * 60 * 8;

	private static final long DAY_SECONDS = 60 * 60 * 24;

	public static Unit fromString(String unit) {
		return Unit.valueOf(unit.toUpperCase());
	}

	public abstract long toIndex(long timestamp);

	public abstract long startTimeOf(long index);

	public abstract long endTimeOf(long index);

	public long toIndex(Date date) {
		return toIndex(date.getTime() / 1000);
	}

	public Date startDateTimeOf(long index) {
		Date d = new Date();
		d.setTime(startTimeOf(index) * 1000);
		return d;
	}

	public Date endDateTimeOf(long index) {
		Date d = new Date();
		d.setTime(endTimeOf(index) * 1000);
		return d;
	}

	public String toName() {
		return name().toLowerCase();
	}
}
