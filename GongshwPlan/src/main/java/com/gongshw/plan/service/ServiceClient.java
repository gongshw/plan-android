package com.gongshw.plan.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author       : gongshw
 * Created At   : 15/2/9.
 */
public class ServiceClient {
	Logger logger = Logger.getLogger("ServiceClient");

	private static final Gson gson = new Gson();

	private static final ServiceClient SINGLETON = new ServiceClient();

	private final static String targetEndpoint = "http://plan.gongshw.com/";

	public static ServiceClient getInstance() {
		return SINGLETON;
	}


	private ServiceClient() {

	}

	public void addPlanMeta(PlanMeta meta) throws IOException {
		request(HttpMethod.PUT, "/plan/" + meta.getId(), meta);
	}


	public void updatePlanSort(String id, double sort) throws IOException {
		PlanMeta meta = new PlanMeta();
		meta.setId(id);
		meta.setSort(sort);
		request(HttpMethod.POST, "/plan/" + id, meta);
	}

	public void deletePlanMeta(String planId) throws IOException {
		request(HttpMethod.DELETE, "/plan/" + planId);
	}

	public List<PlanRecord> getPlans(long index, Unit unit) throws IOException {
		String result = request(HttpMethod.GET, "/plan/" + unit.toName() + "/" + index);
		JsonArray rawList = (JsonArray) gson.fromJson(result, JsonObject.class).get("result");
		return convertJsonArrayToPlans(rawList, unit);
	}

	public Map<Unit, List<PlanRecord>> getActivePlans() throws IOException {
		String result = request(HttpMethod.GET, "/plan/active");
		JsonObject obj = gson.fromJson(result, JsonObject.class).get("result").getAsJsonObject();
		Map<Unit, List<PlanRecord>> map = new HashMap<>();
		for (Unit unit : Unit.values()) {
			map.put(unit, convertJsonArrayToPlans(obj.getAsJsonArray(unit.toName()), unit));
		}
		return map;
	}

	private List<PlanRecord> convertJsonArrayToPlans(JsonArray rawList, Unit unit) {
		List<PlanRecord> records = new ArrayList<>();
		for (JsonElement obj : rawList) {
			PlanRecord record = gson.fromJson(obj, PlanRecord.class);
			record.setUnit(unit);
			records.add(record);
		}
		return records;
	}

	public void markPlanFinish(String planId, long index) throws IOException {
		request(HttpMethod.PUT, "/plan/" + planId + "/" + index + "/_done");
	}

	public void unmarkPlanFinish(String planId, long index) throws IOException {
		request(HttpMethod.DELETE, "/plan/" + planId + "/" + index + "/_done");
	}

	private String request(HttpMethod method, String path) throws IOException {
		return this.request(method, path, null);
	}

	private String request(HttpMethod method, String path, Object data) throws IOException {
		logger.log(Level.INFO, path);
		URL url;
		if (targetEndpoint.endsWith("/") && path.startsWith("/")) {
			url = new URL(targetEndpoint + path.substring(1));
		} else if (!targetEndpoint.endsWith("/") && !path.startsWith("/")) {
			url = new URL(targetEndpoint + "/" + path);
		} else {
			url = new URL(targetEndpoint + path);
		}

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod(method.name());
		if (data != null && (method == HttpMethod.PUT || method == HttpMethod.POST)) {
			connection.setDoOutput(true);
			OutputStream os = connection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));
			writer.write(gson.toJson(data));
			writer.flush();
			writer.close();
		}
		connection.connect();
		String result = IOUtils.toString(connection.getInputStream());
		result = result.replaceAll("\\n| ", "");
		logger.log(Level.INFO, result);
		return result;
	}

	enum HttpMethod {
		GET, POST, PUT, DELETE
	}
}
