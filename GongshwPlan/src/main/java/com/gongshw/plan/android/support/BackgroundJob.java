package com.gongshw.plan.android.support;

/**
 * Author       : gongshw
 * Created At   : 15/2/18.
 */
public interface BackgroundJob<T> {
	T run() throws Exception;

	void handle(T result);
}
