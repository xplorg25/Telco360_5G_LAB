package org.xplorg.tool.telco360.performanceDAO.implementation;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.xplorg.tool.telco360.DAO.interfaces.PerformanceMicrowaveDAO;
import org.xplorg.tool.telco360.entity.GenericPerformance;
import org.xplorg.tool.telco360.entity.TableHeader;
import org.xplorg.tool.telco360.entity.gs_child3;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_1;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_1a;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_2;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_3;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_4;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_5;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_6;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_7;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_8;
import org.xplorg.tool.telco360.tree.entity.check_g_s_parent_micro;
import org.xplorg.tool.telco360.tree.entity.gs_child1_micro;
import org.xplorg.tool.telco360.tree.entity.gs_child2_micro;
import org.xplorg.tool.telco360.tree.entity.gs_child3a;
import org.xplorg.tool.telco360.tree.entity.gs_child4_micro;
import org.xplorg.tool.telco360.tree.entity.gs_child5_micro;
import org.xplorg.tool.telco360.tree.entity.gs_child6_micro;
import org.xplorg.tool.telco360.tree.entity.gs_child7_micro;
import org.xplorg.tool.telco360.visibility_excel_report.complete_excel_visibility_report;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;

@Repository("performanceMicrowaveDAO")
public class PerformanceMicrowaveDao_implementation extends GenericPerformance implements PerformanceMicrowaveDAO {
Logger log = LogManager.getLogger(PerformanceMicrowaveDao_implementation.class.getName());
@Override
public ArrayList < String > get_parameter_value(String opco, String admin_id, String domain, String vendor, String elementname, String table, String slot, ArrayList < String > notcontains1) {

if (log.isDebugEnabled()) {
log.debug("*************** checked into get_parameter_value **********" + domain + "***" + vendor + "****" + opco + "****" + admin_id + "******");
}
BasicDBObject index = new BasicDBObject("$hint", "index");
ArrayList < String > notcontains = new ArrayList < String > ();

for (String value: notcontains1) {
notcontains.add(string_replace(value));
}

ArrayList < String > output = new ArrayList < String > ();

MongoClient connection = get_mongo_connection();

MongoDatabase database = database(connection, domain, vendor);
String final_table = "";

if (table.equals("mtr")) {
final_table = table;
} else {
final_table = table + "_1day";
}
MongoCollection < Document > collection = database.getCollection(final_table);
FindIterable < Document > documents = null;

String check = "";
if (table.equals("pmon") || table.equals("rmon") || table.equals("vlan")) {
check = "Port";
} else {
check = "Slot No";
}
if (string_replace(slot).length() > 2) {

documents = collection.find(and(eq("NE Name", string_replace(elementname)), eq(check, string_replace(slot)))).hint(index).limit(1);

} else {
documents = collection.find(eq("NE Name", string_replace(elementname))).hint(index).limit(1);
}

for (Document d: documents) {
for (Entry < String, Object > entry: d.entrySet()) {

if (!entry.getKey().toString().contains("_id")) {

if (!notcontains.contains(entry.getKey().toString())) {

String column = entry.getKey().toString();
String value = entry.getValue().toString();

String join = column + "==" + value;

output.add(join);
}

}
}

}
log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "**************     " + vendor + "****************** exit   get_parameter_value ****************");

close_mongo_connection(connection);

return output;
}

//TODO
@Override
public dual_axis_1 get_microwave_graph(String opco, String admin_id, String vendor, String domain, ArrayList < String > kpi_name, ArrayList < String > ne_name, ArrayList < String > key, String reportname, String duration, String start_date, String end_date, String starttime, String endtime, ArrayList < String > check_axis, String type) {

if (log.isDebugEnabled()) {

log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "**************     " + vendor + "****************** enter   get_microwave_graph ****************");

}

String natural_trend = "yes";

if (type.equals("complete_graph")) {
String kpi = kpi_name.get(0);

kpi_name.clear();

String axis = check_axis.get(0);
check_axis.clear();
for (String a: key) {

kpi_name.add(kpi);
}

for (String a: key) {
check_axis.add(axis);
}

} else if (type.equals("for_sla")) {

String kpi = kpi_name.get(0);

kpi_name.clear();

String axis = check_axis.get(0);
check_axis.clear();
for (String a: key) {

kpi_name.add(kpi);
}

for (String a: key) {
check_axis.add(axis);
}

kpi_name.add("Threshold");
check_axis.add("1");

key.add(key.get(0));

}

MongoClient connection = get_mongo_connection();
String StartTime = "", EndTime = "";

MongoDatabase database = database(connection, domain, vendor);
dual_axis_1a title = new dual_axis_1a(); // This is for title of graph.

dual_axis_2 zoomtype = new dual_axis_2(); // for zoomtype

//for zoom type
zoomtype.setZoomType("xy");

ArrayList < String > values_x = new ArrayList < String > (); // values for x-axis 

ArrayList < String > unique_dates = new ArrayList < String > (); // unique dates----valid only if the trend is natural

int check_time;

Properties config = getProperties();

;

SimpleDateFormat formatter = new SimpleDateFormat(config.getProperty("performance.dateformat1"));
try {

Date date = formatter.parse(start_date);

start_date = new SimpleDateFormat(config.getProperty("performance.dateformat3")).format(date);

} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
}

try {
Date date = formatter.parse(end_date);

end_date = new SimpleDateFormat(config.getProperty("performance.dateformat3")).format(date);

} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);

}

//checking time ie whether time is for full day or some limited value
String interval = "";
if (starttime.contains(":") && endtime.contains(":")) {

StartTime = starttime;
EndTime = endtime;

check_time = 1;

} else {

check_time = 0;
}

ArrayList < dual_axis_7 > series = new ArrayList < dual_axis_7 > ();
//----check whether kpi contains apn or not---------

//ArrayList<String> time = new ArrayList<>();

dual_axis_3 xaxis = null;
//naming for yaxis
ArrayList < dual_axis_5 > yaxis = null;

if (check_time == 0) {

} else if (check_time == 1) {

}

try {

if (natural_trend.equals("no")) {
values_x = new date_time_relation().date_time_relations(database, start_date, end_date, StartTime, EndTime, interval);

xaxis = new dual_axis_3(values_x, true); // it contains categories & crosshair present under xAxis.

} else {
unique_dates = mongo_get_name(database, start_date, end_date);

}

} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);

}

//naming for yaxis
yaxis = new ArrayList < dual_axis_5 > ();

//name for left axis under y axis.
String s_axis_name = "";
String p_axis_name = "";
if (check_axis.contains("0")) {
s_axis_name = "Secondary Axis";
} else {
s_axis_name = "";
}

if (check_axis.contains("1")) {
p_axis_name = "Primary Axis";
} else {
p_axis_name = "";
}

dual_axis_4 textright = new dual_axis_4(s_axis_name);

dual_axis_5 yaxis_items2 = new dual_axis_5(textright, true);

yaxis.add(yaxis_items2);

dual_axis_4 text_left = new dual_axis_4(p_axis_name);

dual_axis_5 yaxis_items1 = new dual_axis_5(text_left, false);

yaxis.add(yaxis_items1);

for (int i = 0; i < kpi_name.size(); i++) {

String label = "";

String kpi = string_replace(kpi_name.get(i));
if (kpi.equals("Threshold")) {

if (type.equals("for_sla")) {
kpi = mongo_generic(database, admin_id, string_replace(kpi_name.get(0)), "formula", "kpi_formula");

} else {
kpi = string_replace(kpi_name.get(0));
}

String threshold = "";
threshold = mongo_select1_where2(database, "threshold", "kpi_formula", "admin_id", admin_id, "formula", kpi).get(0);

String slot_check = string_replace(key.get(i));;
title.setText(kpi + "         Vs      Threshold");

String ne_single_name = string_replace(ne_name.get(0));

label = " Threshold";

ArrayList < Double > dummy = new ArrayList < Double > ();

ArrayList < Double > data1 = new ArrayList < Double > ();
try {

String calculation_type = mongo_generic(database, admin_id, kpi, "calculation_type", "kpi_formula");

if (natural_trend.equals("no")) {

for (double d: dummy) {

data1.add(Double.parseDouble(threshold));
}
} else {

ArrayList < String > values_natural_trend = null;
values_natural_trend = new microwave_graph_values().graph_value_natural_trend(domain, database, kpi, ne_single_name, unique_dates, slot_check, reportname, duration, calculation_type, StartTime, EndTime, check_time);

for (String values: values_natural_trend) {
String split1[] = values.split("@AND@");
values_x.add(split1[0]);
data1.add(Double.parseDouble(threshold));
}
xaxis = new dual_axis_3(values_x, true);
}

dual_axis_8 valuesuffix1 = new dual_axis_8("");

dual_axis_7 series_item1 = new dual_axis_7(label, "line", Integer.parseInt(check_axis.get(i)), data1, valuesuffix1); // for series value to represent line
series.add(series_item1);

} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);

}

} else {

title.setText(kpi);

if (type.equals("for_sla")) {

kpi = mongo_generic(database, admin_id, kpi, "formula", "kpi_formula");

} else {
kpi = string_replace(kpi_name.get(i));
}

String ne_single_name = string_replace(ne_name.get(0));

String slot_check = string_replace(key.get(i));;






String table_type = "";
if (duration.equals("15 min")) {
table_type = "_15min";
} else {
table_type = "_1day";
}
String where = "";

if (reportname.equals("mtr")) {
table_type = "";
where = "Slot No";
} else if (reportname.equals("pmon") || reportname.equals("rmon") || reportname.equals("vlan")) {
where = "Port";
} else {
where = "";
}
String tablename=reportname + table_type +"_"+end_date.replace("-", "");
String facing_side=connectivity_where_with_hint_regex_nce(database,tablename, "NE NAME_1", "Opposite NE Name", "NE Name", ne_single_name, where, slot_check,where, slot_check);


if(facing_side.equals("-")||facing_side.trim().length()<1) {
String final_table = "";

if (reportname.equals("mtr")) {
final_table = reportname;
} else {
final_table = reportname + "_1day";
}	
	

String face=connectivity_where_with_hint_regex_nce(database,final_table, "index", "Opposite NE Name", "NE Name", ne_single_name, where, slot_check,where, slot_check);

if(face.equals("0")) {
	face.replace("0", "");
}
label = slot_check + "<======>"+face;
////System.out.println(ne_single_name+"=1="+final_table+"==="+end_date+"===="+face);
}

else {
	////System.out.println(ne_single_name+"=3="+tablename+"==="+end_date+"===="+facing_side);
	label = slot_check + "<======>"+facing_side;
}


ArrayList < Double > data1 = new ArrayList < Double > ();

try {

String calculation_type = mongo_generic(database, admin_id, kpi, "calculation_type", "kpi_formula");

if (natural_trend.equals("no")) {

data1 = new microwave_graph_values().graph_value(domain, database, kpi, ne_single_name, values_x, interval, calculation_type);

} else {
ArrayList < String > values_natural_trend = null;

values_natural_trend = new microwave_graph_values().graph_value_natural_trend(domain, database, kpi, ne_single_name, unique_dates, slot_check, reportname, duration, calculation_type, StartTime, EndTime, check_time);

for (String values: values_natural_trend) {
String split1[] = values.split("@AND@");
values_x.add(split1[0]);
data1.add(Double.parseDouble(split1[1]));
}
xaxis = new dual_axis_3(values_x, true);

}

dual_axis_8 valuesuffix1 = new dual_axis_8("");

dual_axis_7 series_item1 = new dual_axis_7(label, "line", Integer.parseInt(check_axis.get(i)), data1, valuesuffix1); // for series value to represent line
series.add(series_item1);

} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);

}

}

}

dual_axis_6 tooltip = new dual_axis_6(true); // for tooltip 
dual_axis_1 main_json = new dual_axis_1(zoomtype, title, xaxis, yaxis, tooltip, series); // This is the main class where all the classes are join together to create a single json.

try {

} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
//ex.printStackTrace();
}

log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "**************     " + vendor + "****************** exit   get_microwave_graph ****************");

close_mongo_connection(connection);
return main_json;

}

//TODO  tree microwave

@Override
public List < check_g_s_parent_micro > get_tree_microwave(String opco, String admin_id, String domain, String vendor, String report, String element_name,String type,String kpi_name) {

if (log.isDebugEnabled()) {
log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "**************     " + vendor + "****************** enter   get_tree_microwave ****************");

}


element_name = string_replace(element_name);
Properties config = getProperties();

MongoClient connection = get_mongo_connection();

MongoDatabase database = null;

String database_name = config.getProperty("database.performance_nce_transmission");
database = connection.getDatabase(database_name);

MongoCollection < Document > collection=null;

if(type.equals("sla")) {
	collection = database.getCollection("sla_alerts");
}

else {
collection = database.getCollection(report.toLowerCase() + "_dummy");
}



List < String > ne_type = new ArrayList < > ();

DistinctIterable < String > db_NEType = collection.distinct("NEType", eq("NEName", element_name), String.class);
for (String doc: db_NEType) {
ne_type.add(doc);
}
List < check_g_s_parent_micro > output = new ArrayList < check_g_s_parent_micro > ();

try {

for (int i = 0; i < ne_type.size(); i++) { //for ne type

String element = ne_type.get(i);;

List < gs_child1_micro > output_ne_name = new ArrayList < > ();

DistinctIterable < String > db_NEName = collection.distinct("NEName", eq("NEType", element), String.class);
for (String value_NEName: db_NEName) { //for ne name
String nename = value_NEName;
if (nename.equals(element_name)) {
List < gs_child2_micro > output_shelf_id = new ArrayList < gs_child2_micro > ();
DistinctIterable < String > db_shelfid = collection.distinct("ShelfID", and(eq("NEType", element), eq("NEName", nename), eq("EventName", kpi_name)), String.class);

for (String value_shelfid: db_shelfid) { //for ShelfID
String shelfid = value_shelfid;
List < gs_child3a > coutput2 = new ArrayList < gs_child3a > ();
DistinctIterable < String > db_board_id = collection.distinct("BrdID", and(eq("NEType", element), eq("NEName", nename), eq("ShelfID", shelfid), eq("EventName", kpi_name)), String.class);

for (String value_db_board_id: db_board_id) { // for BrdID

List < gs_child4_micro > gs_child4 = new ArrayList < gs_child4_micro > ();

DistinctIterable < String > db_board_type = collection.distinct("BrdType", and(eq("NEType", element), eq("NEName", nename), eq("ShelfID", shelfid), eq("BrdID", value_db_board_id), eq("EventName", kpi_name)), String.class);

for (String value_board_type: db_board_type) {

List < gs_child5_micro > gs_child5 = new ArrayList < gs_child5_micro > ();

DistinctIterable < String > db_PortID = collection.distinct("PortID", and(eq("NEType", element), eq("NEName", nename), eq("ShelfID", shelfid), eq("BrdID", value_db_board_id), eq("BrdType", value_board_type), eq("EventName", kpi_name)), String.class);

for (String value_db_PortID: db_PortID) {

List < gs_child6_micro > gs_child6 = new ArrayList < gs_child6_micro > ();

DistinctIterable < String > db_PortNO = collection.distinct("PortNO", and(eq("NEType", element), eq("NEName", nename), eq("ShelfID", shelfid), eq("BrdID", value_db_board_id), eq("BrdType", value_board_type), eq("PortID", value_db_PortID), eq("EventName", kpi_name)), String.class);

for (String value_db_PortNO: db_PortNO) {

List < gs_child7_micro > gs_child7 = new ArrayList < gs_child7_micro > ();

DistinctIterable < String > db_PortName = collection.distinct("PortName", and(eq("PortNO", value_db_PortNO), eq("NEType", element), eq("NEName", nename), eq("ShelfID", shelfid), eq("BrdID", value_db_board_id), eq("BrdType", value_board_type), eq("PortID", value_db_PortID), eq("EventName", kpi_name)), String.class);

for (String value_db_PortName: db_PortName) {

List < gs_child3 > gs_child3 = new ArrayList < gs_child3 > ();

DistinctIterable < String > db_MOType = collection.distinct("FBName", and(eq("PortName", value_db_PortName), eq("PortNO", value_db_PortNO), eq("NEType", element), eq("NEName", nename), eq("ShelfID", shelfid), eq("BrdID", value_db_board_id), eq("BrdType", value_board_type), eq("PortID", value_db_PortID), eq("EventName", kpi_name)), String.class);

for (String value_db_MOType: db_MOType) {

gs_child3 gc3 = new gs_child3("FBName=" + value_db_MOType, "file");
gs_child3.add(gc3);
}

gs_child7_micro c11 = new gs_child7_micro("PortName=" + value_db_PortName, "folder", gs_child3);

gs_child7.add(c11);

}

gs_child6_micro c11 = new gs_child6_micro("PortNO=" + value_db_PortNO, "folder", gs_child7);

gs_child6.add(c11);

}

gs_child5_micro c11 = new gs_child5_micro("PortID=" + value_db_PortID, "folder", gs_child6);

gs_child5.add(c11);

}

gs_child4_micro c2 = new gs_child4_micro("BrdType=" + value_board_type, "folder", gs_child5);

gs_child4.add(c2);

}

gs_child3a c1 = new gs_child3a("BrdID=" + value_db_board_id, "folder", gs_child4);
coutput2.add(c1);

}

gs_child2_micro parent_shelf_id = new gs_child2_micro("ShelfID=" + shelfid, "folder", coutput2);
output_shelf_id.add(parent_shelf_id);

}

gs_child1_micro parent_nename = new gs_child1_micro(nename, "folder", output_shelf_id);

output_ne_name.add(parent_nename);

}

}
check_g_s_parent_micro final_parent = new check_g_s_parent_micro(ne_type.get(i), "file", output_ne_name);
output.add(final_parent);

}

//close_mongo_connection(connection);
} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
}
log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "**************     " + vendor + "****************** exit   get_tree_microwave ****************");

close_mongo_connection(connection);
return output;

}
//TODO
@Override
public dual_axis_1 get_nce_microwave_graph(String opco, String admin_id, String vendor, String domain,
ArrayList < String > kpi_name, ArrayList < String > ne_name, ArrayList < String > key, ArrayList < String > conditions,
String duration, String start_date, String end_date, String starttime, String endtime,
ArrayList < String > check_axis, String type) {

if (log.isDebugEnabled()) {
log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "**************     " + vendor + "****************** enter   get_nce_microwave_graph ****************");

}
String natural_trend = "yes";

if (type.equals("complete_graph")) {
String kpi = kpi_name.get(0);

kpi_name.clear();

String axis = check_axis.get(0);
check_axis.clear();
for (String a: key) {

kpi_name.add(kpi);
}

for (String a: key) {
check_axis.add(axis);
}
} 

else if (type.equals("for_sla")) {
	
////System.out.println("hello");	
kpi_name.add("Threshold");
check_axis.add("1");

ne_name.add(ne_name.get(0));

}

MongoClient connection = get_mongo_connection();
String StartTime = "", EndTime = "";

MongoDatabase database = database(connection, domain, vendor);

dual_axis_1a title = new dual_axis_1a(); // This is for title of graph.

dual_axis_2 zoomtype = new dual_axis_2(); // for zoomtype

//for zoom type
zoomtype.setZoomType("xy");

ArrayList < String > values_x = new ArrayList < String > (); // values for x-axis 

ArrayList < String > unique_dates = new ArrayList < String > (); // unique dates----valid only if the trend is natural

int check_time;

Properties config = getProperties();

if (domain.equals("TRANSMISSION") && vendor.equals("ERICSSON")) {
database = connection.getDatabase(config.getProperty("database.performance_ericsson_transmission"));
if (duration.equals("15 Mins")) {

natural_trend = "yes";
}

} else {
natural_trend = "yes";
}

String threshold = mongo_select1_where2(database,"threshold","kpi_formula","admin_id",admin_id,"kpi_name",string_replace(kpi_name.get(0))).get(0);

;
////System.out.println(threshold);
SimpleDateFormat formatter = new SimpleDateFormat(config.getProperty("performance.dateformat1"));
try {

Date date = formatter.parse(start_date);

start_date = new SimpleDateFormat(config.getProperty("performance.dateformat3")).format(date);

} catch (Exception e) {

}

try {
Date date = formatter.parse(end_date);

end_date = new SimpleDateFormat(config.getProperty("performance.dateformat3")).format(date);

} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);

}

//checking time ie whether time is for full day or some limited value
String interval = "";
if (starttime.contains(":") && endtime.contains(":")) {

StartTime = starttime;
EndTime = endtime;

check_time = 1;

} else {

check_time = 0;
}

ArrayList < dual_axis_7 > series = new ArrayList < dual_axis_7 > ();
//----check whether kpi contains apn or not---------

//ArrayList<String> time = new ArrayList<>();

dual_axis_3 xaxis = null;
//naming for yaxis
ArrayList < dual_axis_5 > yaxis = null;

try {

unique_dates = mongo_get_name(database, start_date, end_date);

} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);

}

//naming for yaxis
yaxis = new ArrayList < dual_axis_5 > ();

//name for left axis under y axis.
String s_axis_name = "";
String p_axis_name = "";
if (check_axis.contains("0")) {
	
String kpi=string_replace(kpi_name.get(check_axis.indexOf("0")));

if(domain.equals("TRANSMISSION")) {
	s_axis_name = mongo_generic(database, admin_id, kpi, "unit", "kpi_formula");
}
else {
s_axis_name = "Secondary Axis";
	
}



} else {
s_axis_name = "";
}

if (check_axis.contains("1")) {
	
	String kpi=string_replace(kpi_name.get(check_axis.indexOf("1")));
	
	if(domain.equals("TRANSMISSION")) {
		p_axis_name = mongo_generic(database, admin_id, kpi, "unit", "kpi_formula");
	}
	else {
		p_axis_name = "Primary Axis";
	}

		



	
	

} else {
p_axis_name = "";
}

dual_axis_4 textright = new dual_axis_4(s_axis_name);

dual_axis_5 yaxis_items2 = new dual_axis_5(textright, true);

yaxis.add(yaxis_items2);

dual_axis_4 text_left = new dual_axis_4(p_axis_name);

dual_axis_5 yaxis_items1 = new dual_axis_5(text_left, false);

yaxis.add(yaxis_items1);

////System.out.println(kpi_name);

for (int i = 0; i < kpi_name.size(); i++) {

String kpi = string_replace(kpi_name.get(i));

if (kpi.equals("Threshold")) {

kpi = string_replace(kpi_name.get(0));
title.setText(kpi+" Vs Threshold(" +threshold+")");

String ne_single_name = string_replace(ne_name.get(i));

String slot_check = string_replace(key.get(0));;

String label = "Threshold";
String condition = string_replace(conditions.get(0));

ArrayList < Double > dummy = new ArrayList < Double > ();

ArrayList < Double > data1 = new ArrayList < Double > ();
try {

String calculation_type = mongo_generic(database, admin_id, kpi, "calculation_type", "kpi_formula");

if (natural_trend.equals("no")) {

for (double d: dummy) {

data1.add(Double.parseDouble(threshold));
}
} else {

ArrayList < String > values_natural_trend = null;

values_natural_trend = new nce_microwave_graph_values().graph_value_natural_trend(domain, database, kpi, ne_single_name, unique_dates, slot_check, condition, duration, calculation_type, StartTime, EndTime, check_time);

for (String values: values_natural_trend) {
String split1[] = values.split("@AND@");
values_x.add(split1[0]);
data1.add(Double.parseDouble(threshold));
}
xaxis = new dual_axis_3(values_x, true);
}

dual_axis_8 valuesuffix1 = new dual_axis_8("");

dual_axis_7 series_item1 = new dual_axis_7(label, "line", Integer.parseInt(check_axis.get(i)), data1, valuesuffix1); // for series value to represent line
series.add(series_item1);

} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);

}

} else {

title.setText(kpi);

String ne_single_name = string_replace(ne_name.get(i));

String slot_check = string_replace(key.get(i));;

String label = "";
String condition = string_replace(conditions.get(i));
label = kpi + " " + slot_check + " " + "   (" + ne_single_name + ")";

ArrayList < Double > data1 = new ArrayList < Double > ();

try {

String calculation_type = mongo_generic(database, admin_id, kpi, "calculation_type", "kpi_formula");

if (natural_trend.equals("no")) {

data1 = new microwave_graph_values().graph_value(domain, database, kpi, ne_single_name, values_x, interval, calculation_type);

} else {
ArrayList < String > values_natural_trend = null;

values_natural_trend = new nce_microwave_graph_values().graph_value_natural_trend(domain, database, kpi, ne_single_name, unique_dates, slot_check, condition, duration, calculation_type, StartTime, EndTime, check_time);

for (String values: values_natural_trend) {

String split1[] = values.split("@AND@");
values_x.add(split1[0]);
data1.add(Double.parseDouble(split1[1]));

}
xaxis = new dual_axis_3(values_x, true);

}

dual_axis_8 valuesuffix1 = new dual_axis_8("");

dual_axis_7 series_item1 = new dual_axis_7(label, "line", Integer.parseInt(check_axis.get(i)), data1, valuesuffix1); // for series value to represent line
series.add(series_item1);

} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);

}

}

}

dual_axis_6 tooltip = new dual_axis_6(true); // for tooltip 
dual_axis_1 main_json = new dual_axis_1(zoomtype, title, xaxis, yaxis, tooltip, series); // This is the main class where all the classes are join together to create a single json.

try {

} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
//ex.printStackTrace();
}
log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "**************     " + vendor + "****************** exit   get_nce_microwave_graph ****************");

close_mongo_connection(connection);
return main_json;

}
//TODO
@Override
public String getTableSpecificColsValsConditionGenericInventory(String opco, String admin_id, String domain, String vendor, String start_date, String end_date, String report_name, ArrayList < String > ar_columns, ArrayList < String > ar_conditions) {

try {
log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "**************     " + vendor + "****************** enter   getTableSpecificColsValsConditionGenericInventory ****************");

BasicDBObject index = null;
String ne_hint = ne_names_hint(vendor, report_name.toLowerCase());

if (vendor.equals("NEC")) {
index = new BasicDBObject("$hint", "NE_NAME_1");
} else if (vendor.equals("NCE")) {
index = new BasicDBObject("$hint", "NE_Name_1");
} else if (vendor.equals("SAM")) {
index = new BasicDBObject("$hint", "siteName_1");
}

//for columns==============================
StringBuilder sb_column = new StringBuilder();
String columns = "";

if (ar_columns.contains("ALL")) {
columns = "*";
} else {
for (String val: ar_columns) {
sb_column.append(string_replace(val) + ",");
}
sb_column.setLength(sb_column.length() - 1);
columns = sb_column.toString();
}

//for conditions============================

String conditions = "";
StringBuilder sb_condition = new StringBuilder();

if (ar_conditions.contains("ALL")) {
conditions = "-";
} else {
for (String val: ar_conditions) {
sb_condition.append("" + ne_hint + "=" + string_replace(val) + " AND ");
}
sb_condition.setLength(sb_condition.toString().trim().length() - 3);

conditions = sb_condition.toString().trim();

}

Properties config = getProperties();
//date format change
SimpleDateFormat formatter = new SimpleDateFormat(config.getProperty("performance.dateformat1"));
try {

Date date = formatter.parse(start_date);

start_date = new SimpleDateFormat(config.getProperty("performance.dateformat3")).format(date);

} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
}

try {
Date date = formatter.parse(end_date);

end_date = new SimpleDateFormat(config.getProperty("performance.dateformat3")).format(date);

} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
//ex.printStackTrace();
}

MongoClient connection = get_mongo_connection();
MongoDatabase database = database(connection, domain, vendor);

ArrayList < String > unique_dates = mongo_get_name(database, start_date, end_date);

ArrayList < String > tableNames = new ArrayList < > ();

if (vendor.equals("NCE") || vendor.equals("NEC") || vendor.equals("ERICSSON") || vendor.equals("SAM")) {
for (String date: unique_dates) {

String report = report_name.toLowerCase() + "_" + date.replace("-", "");
tableNames.add(report);
}
}

ArrayList < Bson > fltr = new ArrayList < Bson > ();
Bson filter = null;
if (conditions.length() > 1) {
String cond = conditions.replace("@DOT@", ".").replace("@SEMICOLON@", ";");
if (cond.contains(" and ")) {
String cond_spls[] = cond.split(" and ");
for (String cv: cond_spls) {
if (cv.contains("=")) {
String col = cv.substring(0, cv.indexOf("=")).trim();
String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");;
fltr.add(eq(col, val));
}
}
filter = and(fltr);
} else if (cond.contains(" AND ")) {
String cond_spls[] = cond.split(" AND ");
for (String cv: cond_spls) {
if (cv.contains("=")) {
String col = cv.substring(0, cv.indexOf("=")).trim();
String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");;
fltr.add(eq(col, val));
}
}
filter = or(fltr);
} else if (!cond.contains(" AND ") && !cond.contains(" and ") && cond.contains("=")) {
String col = cond.split("=")[0].trim();
String val = cond.split("=")[1].trim().replace("'", "");;
fltr.add(eq(col, val));
filter = and(fltr);
}
}

String StartTime = "", EndTime = "";

ArrayList < TableHeader > cols = new ArrayList < TableHeader > ();
JSONArray vals = new JSONArray();
ArrayList < String > cls = new ArrayList < String > ();

for (int tn = 0; tn < tableNames.size(); tn++) {
MongoCollection < Document > collection = database.getCollection(tableNames.get(tn));
ArrayList < Document > resultSet = null;

Map < String, Object > groupMap = new HashMap < String, Object > ();

if (!columns.equals("*")) {
if (columns.contains(",")) {
String columns_spls[] = columns.split(",");
for (String colm: columns_spls) {
cls.add(colm);
groupMap.put(colm, "$" + colm);
}
} else {
cls.add(columns);
groupMap.put(columns, "$" + columns);
}

DBObject groupFields = new BasicDBObject(groupMap);
if (filter != null) {
resultSet = collection.aggregate(Arrays.asList(match(filter), group(groupFields), project(fields(include(cls))))).hint(index).into(new ArrayList < Document > ()); //,limit(500)

} else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields), project(fields(include(cls))))).hint(index).into(new ArrayList < Document > ()); //,limit(500)
}
if (tn == 0) {
String cols_spls[] = columns.split(",");
for (String col: cols_spls) {
String colm = col;
TableHeader th = new TableHeader(colm, colm);
cols.add(th);
}
}

for (Document docs: resultSet) {
JSONObject colval = new JSONObject();
String object = docs.get("_id").toString();
String substr = object.substring(object.indexOf("{{") + 2, object.indexOf("}}"));
String spls_cols[] = substr.split(",");
for (String cv: spls_cols) {
if (cv.contains("=")) {
String cl = cv.substring(0, cv.indexOf("=")).trim();
String vl = cv.substring(cv.indexOf("=") + 1).trim(); //.replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","")
colval.put(cl, vl);
}
}
//if(!vals.similar(colval)) {
vals.put(colval);
//}      
}
} else {

if (filter != null) {
resultSet = collection.find(filter).hint(index).into(new ArrayList < Document > ());
} else {
resultSet = collection.find().hint(index).into(new ArrayList < Document > ());
}

int size = 0;
int max = 0;
Document doc = null;
for (int i = 0; i < resultSet.size(); i++) {
Document document = resultSet.get(i);
size = document.keySet().size();
if (size > max) {
max = size;
doc = document;
}
}
Iterator < String > itr = doc.keySet().iterator();
if (tn == 0) {
while (itr.hasNext()) {
String col = itr.next().toString();
if (!col.equals("_id")) {
cls.add(col);
TableHeader th = new TableHeader(col, col);
cols.add(th);
}
}
}

for (Document docs: resultSet) {
JSONObject colval = new JSONObject();
for (int j = 0; j < cls.size(); j++) {
if (docs.get(cls.get(j)).toString().length() > 0 && docs.get(cls.get(j)).toString() != null) {
colval.put(cls.get(j), docs.get(cls.get(j)));
} else {
colval.put(cls.get(j), "-");
}
}
//if(!vals.similar(colval)) {
vals.put(colval);
//}      
}

}

}

JSONArray jsonArrayFinal = new JSONArray();
JSONObject jsonObjectColVal = new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);

log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "**************     " + vendor + "****************** exit   getTableSpecificColsValsConditionGenericInventory ****************");

close_mongo_connection(connection);
return jsonArrayFinal.toString();
} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
}

return null;

}
//TODO
@Override
public int excel_visibility_report(String opco, String admin_id, String domain, String vendor, String number_of_weeks) {
	
int returns = 1;	
log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "**************     " + vendor + "****************** enter   excel_visibility_report ****************");


MongoClient connection = get_mongo_connection();
MongoDatabase database = database(connection, domain, vendor);

try {
	new excel_visibility_graph_printing().print_visibility_graph(database, number_of_weeks,admin_id);
} catch (Exception e1) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e1.getMessage(), e1);
}

int weeks=0;
try {
String input = current_date_yyyyMMdd();
String format = "yyyyMMdd";

SimpleDateFormat df = new SimpleDateFormat(format);
Date date = df.parse(input);

Calendar cal = Calendar.getInstance();
cal.setTime(date);
weeks = cal.get(Calendar.WEEK_OF_YEAR);
	

} catch (ParseException e) {

log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
}

if(number_of_weeks.equals("0")) {
try {
	new complete_excel_visibility_report().generate_report(database, number_of_weeks,admin_id,""+(weeks));
} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
}	
}

else {
try {
	new complete_excel_visibility_report().generate_report(database, number_of_weeks,admin_id,""+(weeks-1));
} catch (Exception e) {
	log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
}
}






return returns;
}

@Override
public String vendor_nodes_details(String opco, String admin_id, String domain) {

	
	
	
	
	MongoClient connection = get_mongo_connection();
	MongoDatabase database = database(connection, "all", "topology");
	MongoCollection <Document>collection=database.getCollection("connectivitydetails");
	
	JSONArray final_output=new JSONArray();
	DistinctIterable<String> vendors=collection.distinct("vendor",eq("domain",domain), String.class);
	ArrayList<String>columns_to_insert=new ArrayList<String>();
	columns_to_insert.add("nename");
	columns_to_insert.add("vendor");
	columns_to_insert.add("nat_ip");
	columns_to_insert.add("physical_ip");
	columns_to_insert.add("region");
    //columns.add("locSysDescr");
	columns_to_insert.add("locSysName");
	columns_to_insert.add("locSysLocation");
	columns_to_insert.add("snmp");
	columns_to_insert.add("ssh");
	columns_to_insert.add("ping");
	
	
	ArrayList<String>columns=new ArrayList<String>();
	columns.add("nename");
	columns.add("vendor");
	columns.add("hostname(fm)");
	columns.add("hostname_physical(fm)");
	columns.add("location");
	//columns.add("locSysDescr");
	columns.add("nename");
	columns.add("location");
	columns.add("permissions_snmp");
	columns.add("permissions_ssh");
	columns.add("status");
	
    Map < String, Object > groupMap_discovery = new HashMap < String, Object > ();

    for (String column: columns) {
    	groupMap_discovery.put(column, "$" + column);
    }
    DBObject groupFields_discovery = new BasicDBObject(groupMap_discovery);
    
	
	for(String vendor:vendors) {
		
		
		JSONObject parent=new JSONObject();

		JSONArray children_array=new JSONArray();
		
		
		ArrayList < Document > iterDo_interface = collection.aggregate(Arrays.asList(match(and(eq("vendor",vendor),eq("domain",domain))), group(groupFields_discovery))).into(new ArrayList < Document > ());
		
		JSONArray jsonArray_vendor_domain = new JSONArray(JSON.serialize(iterDo_interface));
	
		for (int i = 0; i < jsonArray_vendor_domain.length(); i++) {
			StringBuilder sb=new StringBuilder();
			
			  JSONObject jsonObject1 = jsonArray_vendor_domain.getJSONObject(i);
			  JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
			  String nename=jsonObject_id.getString("nename");
			  
			  int ii=0;
			  for(String column:columns) {
				  
				  if(column.equals("status")) {
					  String result="";
					  int val=jsonObject_id.getInt(column);
					  if(val==100) {
						  result="no";
					  }
					  else {
						  result="yes";
					  }
					  sb.append(columns_to_insert.get(ii).toUpperCase()+"   :   "+result +"            ");
					  sb.append("                                         ");
					  sb.append("\n");
				  }
				  
				  else {
					  sb.append(columns_to_insert.get(ii).toUpperCase()+"   :   "+jsonObject_id.getString(column) +"            ");
					  sb.append("                                         ");
					  sb.append("\n");
				  }
				  
				
				  ii++;
			  }
			 
			
			JSONObject child=new JSONObject();
			
			child.put("name", nename);
			child.put("other", sb.toString());
			child.put("type", "file");
			
			if(vendor.equalsIgnoreCase("nokia")) {
				child.put("alarms", "yes");
			}
			
			else {
				child.put("alarms", "no");
			}
			children_array.put(child);
			
			
		}

		
	
		if(vendor.equalsIgnoreCase("nokia")) {
			parent.put("alarms", "yes");
		}
	
		else {
			parent.put("alarms", "no");
		}
		
		
		
		parent.put("name", vendor);
		parent.put("type", "folder");
		
		parent.put("children", children_array);
		
		
		
		
		final_output.put(parent);
		
		
	}
	
	
	
	

	

	
	String output=final_output.toString();
	
	
	
	
	
	
	
	connection.close();
	System.gc();
	
	return output;

}

@Override
public String gis_details(String opco, String admin_id, String domain) {

	
	
	
	
	MongoClient connection = get_mongo_connection();
	MongoDatabase database = database(connection, "all", "topology");
	MongoCollection <Document>collection=database.getCollection("microwave_optical_transmission");
	
	MongoCollection <Document>collection_links=database.getCollection("microwave_optical_transmission_links");
	
	JSONArray final_output=new JSONArray();
	DistinctIterable<String> vendors=collection.distinct("FolderName", String.class);
	DistinctIterable<String> links=collection_links.distinct("FolderName", String.class);
	
	ArrayList<String>ar_links=new ArrayList<String>();
	for(String link:links) {
		ar_links.add(link);
	}
	
	ArrayList<String>columns=new ArrayList<String>();
	columns.add("name");
	columns.add("siteid");
	columns.add("city");

	
	
    Map < String, Object > groupMap_discovery = new HashMap < String, Object > ();

    for (String column: columns) {
    	groupMap_discovery.put(column, "$" + column);
    }
    DBObject groupFields_discovery = new BasicDBObject(groupMap_discovery);
    
	
	for(String vendor:vendors) {
		if(ar_links.contains(vendor)) {
		
		JSONObject parent=new JSONObject();

		JSONArray children_array=new JSONArray();
		
		
		ArrayList < Document > iterDo_interface = collection.aggregate(Arrays.asList(match(and(eq("FolderName",vendor))), group(groupFields_discovery))).into(new ArrayList < Document > ());
		
		JSONArray jsonArray_vendor_domain = new JSONArray(JSON.serialize(iterDo_interface));
	
		for (int i = 0; i < jsonArray_vendor_domain.length(); i++) {
			StringBuilder sb=new StringBuilder();
			
			  JSONObject jsonObject1 = jsonArray_vendor_domain.getJSONObject(i);
			  JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
			  String nename=jsonObject_id.getString("name");
			  
			  
			  for(String column:columns) {
				  
				  sb.append(column.toUpperCase()+"   :   "+jsonObject_id.getString(column) +"            ");
				  sb.append("                                         ");
				  sb.append("\n");
				  
			  }
			 
			
			JSONObject child=new JSONObject();
			
			child.put("name", nename);
			child.put("other", sb.toString());
			child.put("type", "file");
			children_array.put(child);
			
			
		}

		
	

	
		
		
		
		parent.put("name", vendor);
		parent.put("type", "folder");
		
		parent.put("children", children_array);
		
		
		
		
		final_output.put(parent);
		
	}
	}
	
	
	
	

	

	
	String output=final_output.toString();
	
	
	
	
	
	
	
	connection.close();
	System.gc();
	
	return output;

}

@Override
public ArrayList<String> get_command(String opco, String admin_id, String domain, String vendor) {

	ArrayList<String> output=new ArrayList<String>();;
	
	MongoClient connection = get_mongo_connection();
	MongoDatabase database = database(connection, "all", "topology");
	MongoCollection <Document>collection=database.getCollection("ipran_mpbn_commands");

	
	
	Bson Filter=Filters.and(Filters.eq("domain",domain),Filters.eq("vendor",vendor));
    FindIterable<Document> resultset = collection.find(Filter);

    // Iterate over the documents and access a particular field value
    for (Document doc : resultset) {
        // Access a particular field value, e.g., "field_name"
        output.add(doc.getString("command"));
        
        // Print the field value
        System.out.println("Field value: " + output);
    }





    HashSet<String> set = new HashSet<>(output);

    // Convert back to ArrayList
    output.clear();
    output.addAll(set);





	
	
	
	return output;
}

}