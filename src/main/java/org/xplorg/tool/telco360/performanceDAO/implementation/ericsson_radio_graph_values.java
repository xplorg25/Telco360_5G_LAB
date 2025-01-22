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

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;

public class ericsson_radio_graph_values extends GenericPerformance {

//TODO with natural trend	
public ArrayList < String > graph_value_natural_trend(String domain, MongoDatabase database, String kpi_name, String devicename, String filter1, String filter2, ArrayList < String > date_time, String interval, String calculation_type, String StartTime, String EndTime, int check_time, String table_hint) {
ArrayList < String > output = new ArrayList < String > ();

for (int i = 0; i < date_time.size(); i++) {
String tablename = date_time.get(i);;

MongoCollection < Document > collection = null;

ArrayList < Document > documents = null;

//condition one where first filter value comes---for ipran it will search ifname and for ipbb it will search ipaddress

//if only interface for ipran and ip for ipbb exists

String condition = "";
String table_group = "";

if (filter1.length() > 1 && filter2.length() <= 1) {
condition = "condition1";

collection = database.getCollection(tablename.replace("-", "_") + "_" + table_hint);
if (check_time == 0) {
documents = collection.find(and(eq("SITE_ID", devicename), eq("MEASOBJLDN", filter1))).sort(Sorts.ascending("START_TIME")).into(new ArrayList < Document > ());
} else {
documents = collection.find(and(eq("SITE_ID", devicename), eq("MEASOBJLDN", filter1), gte("START_TIME", StartTime), lte("START_TIME", EndTime))).sort(Sorts.ascending("START_TIME")).into(new ArrayList < Document > ());
}
//if(calculation_type.equals("sum")) {

//}
//
//} else if (filter1.length() > 1 && filter2.length() > 1) {
//	
//
//condition="condition2";
//
//
//collection = database.getCollection(tablename.replace("-", "_") + "_kpis");
//if (check_time == 0) {
//documents = collection.find(and(eq("SITE_ID", devicename), eq("MEASOBJLDN", filter1))).sort(Sorts.ascending("START_TIME")).into(new ArrayList < Document > ());
//}
//
//else{
//documents = collection.find(and(eq("SITE_ID", devicename), eq("MEASOBJLDN", filter1), gte("START_TIME", StartTime), lte("START_TIME", EndTime))).sort(Sorts.ascending("START_TIME")).into(new ArrayList < Document > ());
//}
//}
//
//
//
//else {
//
//collection = database.getCollection(tablename.replace("-", "_") + "_kpis");
//
//if (check_time == 0) {
//documents = collection.find(and(eq("devicename", devicename))).sort(Sorts.ascending("start_time")).into(new ArrayList < Document > ());
//} else {
//documents = collection.find(and(eq("devicename", devicename), gte("start_time", StartTime), lte("start_time", EndTime))).sort(Sorts.ascending("start_time")).into(new ArrayList < Document > ());
//
//}
//
//}
}
JSONArray jsonArray = new JSONArray(JSON.serialize(documents));
for (int k = 0; k < jsonArray.length(); k++) {

JSONObject jsonObject1 = jsonArray.getJSONObject(k);

String date = "";
String time = "";

date = jsonObject1.optString("START_DATE");
time = jsonObject1.optString("START_TIME");

String value = "";
if (condition.equals("condition1")) {
value = jsonObject1.optString(kpi_name);
} else if (condition.equals("condition2")) {
value = jsonObject1.optString("VALUE");
}

String join_date_time = date + "/" + time;
output.add(join_date_time + "@AND@" + value);

}

}

return output;

}

//TODO without natural trend	
public ArrayList < Double > graph_value(String domain, MongoDatabase database, String kpi_name, String devicename, String filter1, String filter2, ArrayList < String > date_time, String interval, String calculation_type) {

ArrayList < Double > output = new ArrayList < Double > ();

if (interval.equals("Day")) {
for (int i = 0; i < date_time.size(); i++) {
output.add(0.0);

String tablename = date_time.get(i);

MongoCollection < Document > collection = null;
AggregateIterable < Document > documents = null;

//if only interface for ipran and ip for ipbb exists

if (filter1.length() > 1 && filter2.length() <= 1) {

String filter1_column = "SITE_ID";
String for_element = "SITE_ID";
String for_time = "TIME";

String table_group = "";

if (domain.equals("CORE")) {

MongoCollection < Document > collection1 = database.getCollection("group_kpi");
DistinctIterable < String > document = collection1.distinct("groups", eq("kpi_formula", kpi_name), String.class);
for (String doc: document) {
table_group = doc;
}
collection = database.getCollection(tablename.replace("-", "_") + "_" + table_group);
filter1_column = "SITE_ID";
for_element = "SITE_ID";
for_time = "start_time";
} else {

collection = database.getCollection(tablename.replace("-", "_") + "_hour_kpis");
}

if (calculation_type.equals("sum")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq(for_element, filter1), eq(for_element, filter1))),
Aggregates.group(null, Accumulators.sum(kpi_name, eq("$toDouble", "$" + kpi_name)))));
} else if (calculation_type.equals("average")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq(for_element, filter1), eq(for_element, filter1))),
Aggregates.group(null, Accumulators.avg(kpi_name, eq("$toDouble", "$" + kpi_name)))));
} else if (calculation_type.equals("peak")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq(for_element, filter1), eq(for_element, filter1))),
Aggregates.group(null, Accumulators.max(kpi_name, eq("$toDouble", "$" + kpi_name)))));
}

}

//this condition is exists only for ipbb 
else if (filter1.length() > 1 && filter2.length() > 1) {

collection = database.getCollection(tablename.replace("-", "_") + "_kpis");

String filter1_column = "SITE_ID";
String filter2_column = "MEASOBJLDN";
String to_get = "VALUE";

if (calculation_type.equals("sum")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("KPI_NAME", kpi_name), eq(filter1_column, filter1), eq(filter2_column, filter2))),
Aggregates.group(null, Accumulators.sum(to_get, eq("$toDouble", "$" + to_get)))));
} else if (calculation_type.equals("average")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("KPI_NAME", kpi_name), eq(filter1_column, filter1), eq(filter2_column, filter2))),
Aggregates.group(null, Accumulators.avg(to_get, eq("$toDouble", "$" + to_get)))));
} else if (calculation_type.equals("peak")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("KPI_NAME", kpi_name), eq(filter1_column, filter1), eq(filter2_column, filter2))),
Aggregates.group(null, Accumulators.max(to_get, eq("$toDouble", "$" + to_get)))));
}

}

//this is the basic condition for both ipran and ipbb
else {

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

if (filter1.length() > 1 && filter2.length() <= 1) {

String table_group = "";

if (domain.equals("CORE")) {

MongoCollection < Document > collection1 = database.getCollection("group_kpi");
DistinctIterable < String > document = collection1.distinct("groups", eq("kpi_formula", kpi_name), String.class);
for (String doc: document) {
table_group = doc;
}
collection = database.getCollection(tablename.replace("-", "_") + "_" + table_group);
filter1_column = "SITE_ID";

time = "start_time";
} else {

collection = database.getCollection(tablename.replace("-", "_") + "_hour_kpis");
}

to_get = kpi_name;
if (calculation_type.equals("sum")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq(filter1_column, filter1), gte(time, from), lte(time, to))),
Aggregates.group(null, Accumulators.sum(to_get, eq("$toDouble", "$" + to_get)))));

} else if (calculation_type.equals("peak")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq(filter1_column, filter1), gte(time, from), lte(time, to))),
Aggregates.group(null, Accumulators.max(to_get, eq("$toDouble", "$" + to_get)))));

} else if (calculation_type.equals("average")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq(filter1_column, filter1), gte(time, from), lte(time, to))),
Aggregates.group(null, Accumulators.avg(to_get, eq("$toDouble", "$" + to_get)))));

}

} else if (filter1.length() > 1 && filter2.length() > 1) {

to_get = "VALUE";

collection = database.getCollection(tablename.replace("-", "_") + "_kpis");
if (calculation_type.equals("sum")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("KPI_NAME", kpi_name), eq(filter1_column, filter1), eq(filter2_column, filter2), gte("TIME", from), lte("TIME", to))),
Aggregates.group(null, Accumulators.sum(to_get, eq("$toDouble", "$" + to_get)))));

} else if (calculation_type.equals("peak")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("KPI_NAME", kpi_name), eq(filter1_column, filter1), eq(filter2_column, filter2), gte("TIME", from), lte("TIME", to))),
Aggregates.group(null, Accumulators.max(to_get, eq("$toDouble", "$" + to_get)))));

} else if (calculation_type.equals("average")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("KPI_NAME", kpi_name), eq(filter1_column, filter1), eq(filter2_column, filter2), gte("TIME", from), lte("TIME", to))),
Aggregates.group(null, Accumulators.avg(to_get, eq("$toDouble", "$" + to_get)))));

}

} else {

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