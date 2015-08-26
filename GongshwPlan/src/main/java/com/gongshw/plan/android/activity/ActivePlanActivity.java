package com.gongshw.plan.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.gongshw.plan.android.R;
import com.gongshw.plan.android.fragment.PlanAdderFragment;
import com.gongshw.plan.android.fragment.PlanListFragment;
import com.gongshw.plan.android.support.BackgroundJob;
import com.gongshw.plan.service.PlanMeta;
import com.gongshw.plan.service.PlanRecord;
import com.gongshw.plan.service.ServiceClient;
import com.gongshw.plan.service.Unit;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;


public class ActivePlanActivity extends DrawerActivitySupport
		implements PlanAdderFragment.PlanAdderListener, PlanListFragment.PlanListListener {

	private ServiceClient serviceClient = ServiceClient.getInstance();

	private Map<Unit, List<PlanRecord>> planMap;

	private PlanListFragment planListFragment;

	private PlanAdderFragment planAdderFragment;

	@Override
	protected int getLayout() {
		return R.layout.activity_active_plan;
	}


	protected Navi getNavi() {
		return Navi.ACTIVE_PLANS;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		planListFragment = (PlanListFragment) getFragmentManager().findFragmentById(R.id.fragment_plan_list);
		assert planListFragment.getView() != null;
		ListView listView = (ListView) planListFragment.getView().findViewById(R.id.grouped_plan_list_view);
		super.setCompatibleWith(listView);
		planListFragment.setListListener(this);
		planAdderFragment = (PlanAdderFragment) getFragmentManager().findFragmentById(R.id.fragment_plan_adder);
		planAdderFragment.setAdderListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		executeDelayTask(0, new Runnable() {
			@Override
			public void run() {
				try {
					Map<Unit, List<PlanRecord>> restoredPlans = new HashMap<>();
					Gson gson = new Gson();
					for (Unit unit : Unit.values()) {

						FileInputStream input = openFileInput("active_plans_" + unit + ".json");
						InputStreamReader reader = new InputStreamReader(input, Charset.forName("UTF-8"));
						JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);
						List<PlanRecord> records = new ArrayList<>();
						for (JsonElement json : jsonArray) {
							records.add(gson.fromJson(json, PlanRecord.class));
						}
						restoredPlans.put(unit, records);
						reader.close();
					}
					bindPlans(restoredPlans);
				} catch (IOException e) {
					logger.log(Level.INFO, "no restore file. get from remote.");
					getPlans();
				}
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			if (planMap != null) {
				Gson gson = new Gson();
				for (Unit unit : Unit.values()) {
					FileOutputStream output = openFileOutput("active_plans_" + unit + ".json", MODE_PRIVATE);
					output.write(gson.toJson(planMap.get(unit)).getBytes(Charset.forName("UTF-8")));
					output.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getPlans() {
		executeAsyncTask(new BackgroundJob<Map<Unit, List<PlanRecord>>>() {
			@Override
			public Map<Unit, List<PlanRecord>> run() throws Exception {
				return serviceClient.getActivePlans();
			}

			@Override
			public void handle(Map<Unit, List<PlanRecord>> plans) {
				if (plans != null) {
					bindPlans(plans);
					showToast("刷新成功!");
				}
			}
		});
	}


	private void bindPlans(Map<Unit, List<PlanRecord>> plans) {
		ActivePlanActivity.this.planMap = plans;
		if (plans != null) {
			planListFragment.setPlans(plans.get(Unit.DAY), plans.get(Unit.WEEK), plans.get(Unit.MONTH));
		} else {
			planListFragment.setPlans(
					new ArrayList<PlanRecord>(),
					new ArrayList<PlanRecord>(),
					new ArrayList<PlanRecord>());
		}
	}

	@Override
	public void onRefresh() {
		getPlans();
	}

	@Override
	public void onAddPlan(final PlanMeta planMeta) {
		List<PlanRecord> plans = planMap.get(Unit.fromString(planMeta.getUnit()));
		if (plans != null && plans.size() > 0) {
			planMeta.setSort(plans.get(plans.size() - 1).getSort() + 1);
		}

		executeAsyncTask(new BackgroundJob<Map<Unit, List<PlanRecord>>>() {
			@Override
			public Map<Unit, List<PlanRecord>> run() throws Exception {
				serviceClient.addPlanMeta(planMeta);
				return serviceClient.getActivePlans();
			}

			@Override
			public void handle(Map<Unit, List<PlanRecord>> result) {
				bindPlans(result);
				showToast("添加成功!");
			}
		});
	}

	@Override
	public void onPlanDelete(final PlanRecord planRecord) {
		executeAsyncTask(new BackgroundJob<Map<Unit, List<PlanRecord>>>() {
			@Override
			public Map<Unit, List<PlanRecord>> run() throws Exception {
				serviceClient.deletePlanMeta(planRecord.getId());
				return serviceClient.getActivePlans();
			}

			@Override
			public void handle(Map<Unit, List<PlanRecord>> result) {
				bindPlans(result);
				showToast("删除计划:" + planRecord.getText());
			}
		});
	}

	@Override
	public void onPlanFinish(final PlanRecord planRecord) {
		executeAsyncTask(new BackgroundJob<Void>() {
			@Override
			public Void run() throws Exception {
				serviceClient.markPlanFinish(planRecord.getId(), planRecord.getUnit().toIndex(new Date()));
				return null;
			}

			@Override
			public void handle(Void result) {
				showToast("计划" + planRecord.getText() + "被标记为已完成!");
			}
		});
	}

	@Override
	public void onPlanRemoveFinish(final PlanRecord planRecord) {
		executeAsyncTask(new BackgroundJob<Void>() {
			@Override
			public Void run() throws IOException {
				serviceClient.unmarkPlanFinish(planRecord.getId(), planRecord.getUnit().toIndex(new Date()));
				return null;
			}

			@Override
			public void handle(Void result) {
				showToast("计划" + planRecord.getText() + "被标记为未完成!");
			}
		});
	}

	@Override
	public void onMovePlan(final PlanRecord plan, final double sort) {
		executeAsyncTask(new BackgroundJob<Void>() {
			@Override
			public Void run() throws Exception {
				serviceClient.updatePlanSort(plan.getId(), sort);
				return null;
			}

			@Override
			public void handle(Void result) {
				showToast("更新计划: " + plan.getText());
			}
		});
	}

	@Override
	public void onVirtualKeyboardShow() {
		planAdderFragment.setPlanEditorVisibility(View.VISIBLE);
	}

	@Override
	public void onVirtualKeyboardHide() {
		planAdderFragment.setPlanEditorVisibility(View.GONE);
	}
}
