package org.xplorg.tool.telco360.performanceDAO.implementation;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

public class ipran_ipbb_graph_values extends GenericPerformance {
Logger log = LogManager.getLogger(ipran_ipbb_graph_values.class.getName());

BasicDBObject index = new BasicDBObject("$hint", "devicename_1");

//TODO without natural trend	
public ArrayList < Double > graph_value(String domain, MongoDatabase database, String kpi_name, String devicename, String filter1, String filter2, ArrayList < String > date_time, String interval, String calculation_type, String hint) {

ArrayList < Double > output = new ArrayList < Double > ();

if (interval.equals("Day")) {
for (int i = 0; i < date_time.size(); i++) {
output.add(0.0);

String tablename = date_time.get(i);

MongoCollection < Document > collection = database.getCollection(tablename.replace("-", "_") + hint + "_kpis");
AggregateIterable < Document > documents = null;

//if only interface for ipran and ip for ipbb exists

if (filter1.length() > 1 && filter2.length() <= 1) {
String filter1_column = "";
if (domain.equals("IPRAN")) {
filter1_column = "ipaddress";
} else {
filter1_column = "ipaddress";
}

if (calculation_type.equals("sum")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), eq(filter1_column, filter1))),
Aggregates.group(null, Accumulators.sum(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);
} else if (calculation_type.equals("average")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), eq(filter1_column, filter1))),
Aggregates.group(null, Accumulators.avg(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);
} else if (calculation_type.equals("peak")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), eq(filter1_column, filter1))),
Aggregates.group(null, Accumulators.max(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);
}

}

//this condition is exists only for ipbb 
else if (filter1.length() > 1 && filter2.length() > 1) {

if (calculation_type.equals("sum")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), eq("ipaddress", filter1), eq("ifName", filter2))),
Aggregates.group(null, Accumulators.sum(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);
} else if (calculation_type.equals("average")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), eq("ipaddress", filter1), eq("ifName", filter2))),
Aggregates.group(null, Accumulators.avg(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);
} else if (calculation_type.equals("peak")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), eq("ipaddress", filter1), eq("ifName", filter2))),
Aggregates.group(null, Accumulators.max(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);
}

}

// this is the basic condition for both ipran and ipbb
else {

if (calculation_type.equals("sum")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename))),
Aggregates.group(null, Accumulators.sum(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);
} else if (calculation_type.equals("average")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename))),
Aggregates.group(null, Accumulators.avg(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);
} else if (calculation_type.equals("peak")) {
documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename))),
Aggregates.group(null, Accumulators.max(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);
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

MongoCollection < Document > collection = database.getCollection(tablename.replace("-", "_") + hint + "_kpis");

AggregateIterable < Document > documents = null;

//condition one where first filter value comes---for ipran it will search ifname and for ipbb it will search ipaddress

//if only interface for ipran and ip for ipbb exists

if (filter1.length() > 1 && filter2.length() <= 1) {

String filter1_column = "";
if (domain.equals("IPRAN")) {
filter1_column = "ipaddress";
} else {
filter1_column = "ipaddress";
}

if (calculation_type.equals("sum")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), eq(filter1_column, filter1), gte("start_time", from), lte("start_time", to))),
Aggregates.group(null, Accumulators.sum(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);

} else if (calculation_type.equals("peak")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), eq(filter1_column, filter1), gte("start_time", from), lte("start_time", to))),
Aggregates.group(null, Accumulators.max(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);

} else if (calculation_type.equals("average")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), eq(filter1_column, filter1), gte("start_time", from), lte("start_time", to))),
Aggregates.group(null, Accumulators.avg(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);

}

} else if (filter1.length() > 1 && filter2.length() > 1) {

if (calculation_type.equals("sum")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), eq("ipaddress", filter1), eq("ifName", filter2), gte("start_time", from), lte("start_time", to))),
Aggregates.group(null, Accumulators.sum(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);

} else if (calculation_type.equals("peak")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), eq("ipaddress", filter1), eq("ifName", filter2), gte("start_time", from), lte("start_time", to))),
Aggregates.group(null, Accumulators.max(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);

} else if (calculation_type.equals("average")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), eq("ipaddress", filter1), eq("ifName", filter2), gte("start_time", from), lte("start_time", to))),
Aggregates.group(null, Accumulators.avg(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);

}

} else {
if (calculation_type.equals("sum")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), gte("start_time", from), lte("start_time", to))),
Aggregates.group(null, Accumulators.sum(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);
} else if (calculation_type.equals("peak")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), gte("start_time", from), lte("start_time", to))),
Aggregates.group(null, Accumulators.max(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);
} else if (calculation_type.equals("average")) {

documents = collection.aggregate(Arrays.asList(Aggregates.match(Filters.and(eq("devicename", devicename), gte("start_time", from), lte("start_time", to))),
Aggregates.group(null, Accumulators.avg(kpi_name, eq("$toDouble", "$" + kpi_name))))).hint(index);
}

}
JSONArray jsonArray = new JSONArray(JSON.serialize(documents));
for (int k = 0; k < jsonArray.length(); k++) {

JSONObject jsonObject1 = jsonArray.getJSONObject(k);

output.set(i, Double.parseDouble(jsonObject1.optString(kpi_name)));

}

}
}
return output;

}

//TODO with natural trend	
public ArrayList < String > graph_value_natural_trend(String domain, MongoDatabase database, String kpi_name, String devicename, String filter1, String filter2, ArrayList < String > date_time, String interval, String calculation_type, String StartTime, String EndTime, int check_time, String hint) {

ArrayList < String > output = new ArrayList < String > ();

for (int i = 0; i < date_time.size(); i++) {

String tablename = date_time.get(i);;

MongoCollection < Document > collection = null;

ArrayList < Document > documents = null;

//condition one where first filter value comes---for ipran it will search ifname and for ipbb it will search ipaddress

//if only interface for ipran and ip for ipbb exists

if (filter1.length() > 1 && filter2.length() <= 1) {

String filter1_column = "";
String for_element = "devicename";
String for_time = "start_time";

if (domain.equals("IPRAN")) {
filter1_column = "ipaddress";
collection = database.getCollection(tablename.replace("-", "_") + hint + "_hour_kpis");
} else if (domain.equals("IPBB")) {
filter1_column = "ipaddress";
collection = database.getCollection(tablename.replace("-", "_") + hint + "_hour_kpis");
}

if (check_time == 0) {

if (domain.equals("IPBB") || domain.equals("IPRAN")) {

documents = collection.find(and(eq(for_element, devicename), eq(filter1_column, filter1))).sort(Sorts.ascending(for_time)).hint(index).into(new ArrayList < Document > ());
} else {
documents = collection.find(and(eq(for_element, filter1), eq(filter1_column, filter1))).sort(Sorts.ascending(for_time)).hint(index).into(new ArrayList < Document > ());

}

} else {

if (domain.equals("IPBB") || domain.equals("IPRAN")) {
documents = collection.find(and(eq(for_element, devicename), eq(filter1_column, filter1), gte(for_time, StartTime), lte(for_time, EndTime))).sort(Sorts.ascending(for_time)).hint(index).into(new ArrayList < Document > ());
} else {

}
}
//if(calculation_type.equals("sum")) {

//}

} else if (filter1.length() > 1 && filter2.length() > 1) {

collection = database.getCollection(tablename.replace("-", "_") + hint + "_kpis");
//System.out.println(tablename.replace("-", "_") + hint + "_kpis");
//System.out.println("devicename==="+devicename);
//System.out.println("ipaddress==="+filter1);
//System.out.println("ifName==="+filter2);
if (check_time == 0) {

documents = collection.find(and(eq("devicename", devicename), eq("ipaddress", filter1), eq("ifName", filter2))).sort(Sorts.ascending("start_time")).hint(index).into(new ArrayList < Document > ());

} else {

documents = collection.find(and(eq("devicename", devicename), eq("ipaddress", filter1), eq("ifName", filter2), gte("start_time", StartTime), lte("start_time", EndTime))).sort(Sorts.ascending("start_time")).hint(index).into(new ArrayList < Document > ());

}

} else {

collection = database.getCollection(tablename.replace("-", "_") + hint + "_hour_kpis");

if (check_time == 0) {
documents = collection.find(and(eq("devicename", devicename))).sort(Sorts.ascending("start_time")).hint(index).into(new ArrayList < Document > ());
} else {
documents = collection.find(and(eq("devicename", devicename), gte("start_time", StartTime), lte("start_time", EndTime))).sort(Sorts.ascending("start_time")).hint(index).into(new ArrayList < Document > ());

}

}

JSONArray jsonArray = new JSONArray(JSON.serialize(documents));

for (int k = 0; k < jsonArray.length(); k++) {

JSONObject jsonObject1 = jsonArray.getJSONObject(k);

if (domain.equals("IPBB") || domain.equals("IPRAN")) {
String date = jsonObject1.optString("start_date");
String time = jsonObject1.optString("start_time");
String value = jsonObject1.optString(kpi_name);
String join_date_time = date + "/" + time;
if (value.length() > 0) {

output.add(join_date_time + "@AND@" + value);
}

} else {

String date = jsonObject1.optString("DATE");
String time = jsonObject1.optString("TIME");
String value = jsonObject1.optString(kpi_name);

String join_date_time = date + "/" + time;

output.add(join_date_time + "@AND@" + value);

}

}

}

return output;

}

}