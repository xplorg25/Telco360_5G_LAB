package org.xplorg.tool.telco360.performanceDAO.implementation;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xplorg.tool.telco360.entity.GenericPerformance;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;

public class microwave_graph_values extends GenericPerformance {

//TODO with natural trend	
public ArrayList < String > graph_value_natural_trend(String domain, MongoDatabase database, String kpi_name, String devicename, ArrayList < String > date_time, String key, String reportname, String duration, String calculation_type, String StartTime, String EndTime, int check_time) {

BasicDBObject index = new BasicDBObject("$hint", "NE NAME_1");

ArrayList < String > output = new ArrayList < String > ();
String where = "";
String table_type = "";
if (duration.equals("15 min")) {
table_type = "_15min";
} else {
table_type = "_1day";
}

if (reportname.equals("mtr")) {
table_type = "";
where = "Slot No";
} else if (reportname.equals("pmon") || reportname.equals("rmon") || reportname.equals("vlan")) {
where = "Port";
} else {
where = "";
}

for (int i = 0; i < date_time.size(); i++) {

//String tablename = date_time.get(i);;
String tablename = reportname;;
MongoCollection < Document > collection = null;

ArrayList < Document > documents = null;

//condition one where first filter value comes---for ipran it will search ifname and for ipbb it will search ipaddress

//if only interface for ipran and ip for ipbb exists

String condition = "";
String table_group = "";




try {
	collection = database.getCollection(tablename + table_type + "_" + date_time.get(i).replace("-", ""));
	
//	System.out.println(tablename + table_type + "_" + date_time.get(i).replace("-", ""));
	//System.out.println(devicename);
	//System.out.println(where+"==="+key);
	if (check_time == 0) {
	documents = collection.find(and(eq("NE Name", devicename), eq(where, key))).sort(Sorts.ascending("start_time")).hint(index).into(new ArrayList < Document > ());
	} else {
	documents = collection.find(and(eq("NE Name", devicename), eq(where, key), gte("start_time", StartTime), lte("start_time", EndTime))).sort(Sorts.ascending("start_time")).hint(index).into(new ArrayList < Document > ());
	
	}
} catch (Exception e) {

	e.printStackTrace();
}

JSONArray jsonArray = new JSONArray(JSON.serialize(documents));

for (int k = 0; k < jsonArray.length(); k++) {

JSONObject jsonObject1 = jsonArray.getJSONObject(k);

String date = "";
String time = "";

date = date_time.get(i);
time = jsonObject1.optString("start_time");

String value = "";
value = jsonObject1.optString(kpi_name);

String join_date_time = date + "/" + time;

output.add(join_date_time + "@AND@" + value);

}

}

return output;

}

//TODO without natural trend	
public ArrayList < Double > graph_value(String domain, MongoDatabase database, String kpi_name, String devicename, ArrayList < String > date_time, String interval, String calculation_type) {

ArrayList < Double > output = new ArrayList < Double > ();

if (interval.equals("Day")) {
for (int i = 0; i < date_time.size(); i++) {
output.add(0.0);

String tablename = date_time.get(i);

MongoCollection < Document > collection = null;
AggregateIterable < Document > documents = null;

if (calculation_type.equals("sum")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename))),
Aggregates.group(null, Accumulators.sum(kpi_name, eq("$toDouble", "$" + kpi_name)))));
} else if (calculation_type.equals("average")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename))),
Aggregates.group(null, Accumulators.avg(kpi_name, eq("$toDouble", "$" + kpi_name)))));
} else if (calculation_type.equals("peak")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename))),
Aggregates.group(null, Accumulators.max(kpi_name, eq("$toDouble", "$" + kpi_name)))));
}

JSONArray jsonArray = new JSONArray(JSON.serialize(documents));
for (int k = 0; k < jsonArray.length(); k++) {

JSONObject jsonObject1 = jsonArray.getJSONObject(k);

output.set(i, Double.parseDouble(jsonObject1.optString(kpi_name)));

}

}
} else {
for (int i = 0; i < date_time.size(); i++) {
output.add(0.0);

String value1 = date_time.get(i);

String tablename = StringUtils.substringBefore(value1, "/");

String from = StringUtils.substringAfter(value1, "/");

String to = add_mins(from, Integer.parseInt(interval));

if (from.equals("23:00:00") && to.equals("00:00:00")) {
to = "23:55:00";
}

AggregateIterable < Document > documents = null;

//condition one where first filter value comes---for ipran it will search ifname and for ipbb it will search ipaddress

//if only interface for ipran and ip for ipbb exists
MongoCollection < Document > collection = null;
String filter1_column = "SITE_ID";
String filter2_column = "MEASOBJLDN";
String time = "TIME";
String to_get = "";

if (calculation_type.equals("sum")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), gte("start_time", from), lte("start_time", to))),
Aggregates.group(null, Accumulators.sum(kpi_name, eq("$toDouble", "$" + kpi_name)))));
} else if (calculation_type.equals("peak")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), gte("start_time", from), lte("start_time", to))),
Aggregates.group(null, Accumulators.max(kpi_name, eq("$toDouble", "$" + kpi_name)))));
} else if (calculation_type.equals("average")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), gte("start_time", from), lte("start_time", to))),
Aggregates.group(null, Accumulators.avg(kpi_name, eq("$toDouble", "$" + kpi_name)))));
}

JSONArray jsonArray = new JSONArray(JSON.serialize(documents));
for (int k = 0; k < jsonArray.length(); k++) {

JSONObject jsonObject1 = jsonArray.getJSONObject(k);
output.set(i, Double.parseDouble(jsonObject1.optString(to_get)));

}

}
}

return output;

}

}