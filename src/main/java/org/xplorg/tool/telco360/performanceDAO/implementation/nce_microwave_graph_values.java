package org.xplorg.tool.telco360.performanceDAO.implementation;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
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

public class nce_microwave_graph_values extends GenericPerformance {
Logger log = LogManager.getLogger(nce_microwave_graph_values.class.getName());

//TODO with natural trend	
public ArrayList < String > graph_value_natural_trend(String domain, MongoDatabase database, String kpi_name, String devicename, ArrayList < String > date_time, String key, String reportname, String duration, String calculation_type, String StartTime, String EndTime, int check_time) {

ArrayList < String > output = new ArrayList < String > ();
try {

for (int i = 0; i < date_time.size(); i++) {

ArrayList < Bson > fltr = new ArrayList < Bson > ();
Bson filter = null;
if (reportname.length() > 1) {
String cond = string_replace(reportname);
if (cond.contains("and")) {
String cond_spls[] = cond.split("and");
for (String cv: cond_spls) {
if (cv.contains("=")) {
String col = cv.split("=")[0].trim();
String val = cv.split("=")[1].trim().replace("'", "");;
fltr.add(eq(col, val));
}
}
filter = and(fltr);
} else if (cond.contains("AND")) {
String cond_spls[] = cond.split("AND");
for (String cv: cond_spls) {
if (cv.contains("=")) {
String col = cv.split("=")[0].trim();
String val = cv.split("=")[1].trim().replace("'", "");;
fltr.add(eq(col, val));
}
}
filter = and(fltr);
} else if (!cond.contains("AND") && !cond.contains("and") && cond.contains("=")) {
String col = cond.split("=")[0].trim();
String val = cond.split("=")[1].trim().replace("'", "");;
fltr.add(eq(col, val));
}
fltr.add(eq("EventName", kpi_name));
fltr.add(eq("end_date", date_time.get(i)));

filter = and(fltr);
}

BasicDBObject index = null;
index = new BasicDBObject("$hint", "NEName_1");
MongoCollection < Document > collection = null;

ArrayList < Document > documents = null;

//condition one where first filter value comes---for ipran it will search ifname and for ipbb it will search ipaddress

//if only interface for ipran and ip for ipbb exists

collection = database.getCollection(key.toLowerCase() + "_" + date_time.get(i).replace("-", ""));
if (check_time == 0) {

documents = collection.find(filter).sort(Sorts.ascending("end_time")).hint(index).into(new ArrayList < Document > ());
} else {

fltr.add(gte("end_time", StartTime));
fltr.add(lte("end_time", EndTime));
filter = and(fltr);
documents = collection.find(filter).sort(Sorts.ascending("end_time")).hint(index).into(new ArrayList < Document > ());

}

JSONArray jsonArray = new JSONArray(JSON.serialize(documents));

for (int k = 0; k < jsonArray.length(); k++) {

JSONObject jsonObject1 = jsonArray.getJSONObject(k);

String date = "";
String time = "";

date = jsonObject1.optString("end_date");
time = jsonObject1.optString("end_time");

String value = "";

value = jsonObject1.optString("Value");

String join_date_time = date + "/" + time;

output.add(join_date_time + "@AND@" + value);

}

}
Set < String > set = new LinkedHashSet < > ();
//Add the elements to set
set.addAll(output);

//Clear the list
output.clear();

//add the elements of set
//with no duplicates to the list
output.addAll(set);;

} catch (JSONException e) {
log.error("Exception occurs:----" + e.getMessage(), e);
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