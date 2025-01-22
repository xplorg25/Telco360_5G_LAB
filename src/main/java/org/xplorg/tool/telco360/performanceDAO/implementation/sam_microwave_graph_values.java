package org.xplorg.tool.telco360.performanceDAO.implementation;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xplorg.tool.telco360.entity.GenericPerformance;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;

public class sam_microwave_graph_values extends GenericPerformance {

Logger log = LogManager.getLogger(sam_microwave_graph_values.class.getName());

//TODO with natural trend	
public ArrayList < String > graph_value_natural_trend(String domain, MongoDatabase database, String kpi_name, String devicename, String filter1, String filter2, ArrayList < String > date_time, String interval, String calculation_type, String StartTime, String EndTime, int check_time, String hint) {

ArrayList < String > output = new ArrayList < String > ();
BasicDBObject index = null;
index = new BasicDBObject("$hint", "monitoredObjectSiteName_1");
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

documents = collection.find(and(eq(for_element, devicename), eq(filter1_column, filter1))).sort(Sorts.ascending(for_time)).into(new ArrayList < Document > ());
} else {
documents = collection.find(and(eq(for_element, filter1), eq(filter1_column, filter1))).sort(Sorts.ascending(for_time)).into(new ArrayList < Document > ());

}

} else {

if (domain.equals("IPBB") || domain.equals("IPRAN")) {
documents = collection.find(and(eq(for_element, devicename), eq(filter1_column, filter1), gte(for_time, StartTime), lte(for_time, EndTime))).sort(Sorts.ascending(for_time)).into(new ArrayList < Document > ());
} else {

}
}
//if(calculation_type.equals("sum")) {

//}

} else if (filter1.length() > 1 && filter2.length() > 1) {
	

String hint1 = "";
try {
hint1 = StringUtils.substringBetween(hint, "(", ":").toLowerCase();
} catch (Exception e) {
// TODO Auto-generated catch block
log.error("Exception occurs:----" + e.getMessage(), e);
}

collection = database.getCollection(hint1 + "_" + tablename.replace("-", ""));

if (check_time == 0) {

documents = collection.find(and(eq("monitoredObjectSiteId", devicename), eq("monitoredObjectSiteId", devicename), eq("displayedName", filter2))).sort(Sorts.ascending("start_time")).hint(index).into(new ArrayList < Document > ());

} else {

documents = collection.find(and(eq("monitoredObjectSiteId", devicename), eq("monitoredObjectSiteId", devicename), eq("displayedName", filter2), gte("start_time", StartTime), lte("start_time", EndTime))).sort(Sorts.ascending("start_time")).hint(index).into(new ArrayList < Document > ());
}

}

JSONArray jsonArray = new JSONArray(JSON.serialize(documents));
for (int k = 0; k < jsonArray.length(); k++) {

JSONObject jsonObject1 = jsonArray.getJSONObject(k);

String date = jsonObject1.optString("start_date");
String time = jsonObject1.optString("start_time");
String value = jsonObject1.optString(kpi_name);

String join_date_time = date + "/" + time;

output.add(join_date_time + "@AND@" + value);

}

}

return output;

}

}