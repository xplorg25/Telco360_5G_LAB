package org.xplorg.tool.telco360.performanceDAO.implementation;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.descending;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.xplorg.tool.telco360.DAO.interfaces.PerformanceGenericDao;
import org.xplorg.tool.telco360.entity.GenericPerformance;
import org.xplorg.tool.telco360.entity.TableHeader;
import org.xplorg.tool.telco360.entity.check_g_s_parent;
import org.xplorg.tool.telco360.entity.gs_child1;
import org.xplorg.tool.telco360.entity.gs_child2;
import org.xplorg.tool.telco360.entity.gs_child3;
import org.xplorg.tool.telco360.entity.performance_nokia_radio_element_blink_children;
import org.xplorg.tool.telco360.entity.performance_nokia_radio_element_blink_main;
import org.xplorg.tool.telco360.performance.dual_axis.date_format_graph;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_1_date_format;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_1a;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_2;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_3_time_update;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_4;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_5;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_6;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_7_color;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_8;
import org.xplorg.tool.telco360.performance.dual_axis.exporting;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.util.JSON;

@Repository("performanceGenericDao")
public class PerformanceGenericDaoImplementation extends GenericPerformance implements PerformanceGenericDao {
Logger log = LogManager.getLogger(PerformanceGenericDaoImplementation.class.getName());
// TODO For kpi creation Tree
@Override
public List < check_g_s_parent > get_tree_kpi_creation(String opco) {

if (log.isDebugEnabled()) {
log.debug("*************** checked into get_tree_kpi_creation ****************");
}

MongoClient connection = get_mongo_connection();

//MongoDatabase database = null;

//MongoCollection < Document > collection = null;
//String database_name = "";

List < String > vendor_list = new ArrayList < > ();
List < check_g_s_parent > output = new ArrayList < > ();

List < String > domain = new ArrayList < String > ();

domain.add("IPBB");
domain.add("IPRAN");
domain.add("TRANSMISSION");
domain.add("RADIO");
domain.add("CORE");

List < String > g = new ArrayList < String > ();

try {

for (int i = 0; i < domain.size(); i++) {

String domain_name = domain.get(i);

String generation;

List < gs_child1 > coutput = new ArrayList < > ();

if (domain_name.equals("IPRAN")) {
vendor_list.clear();
vendor_list.add("HUAWEI");
} else if (domain_name.equals("IPBB")) {
vendor_list.clear();
vendor_list.add("ERICSSON");
vendor_list.add("JUNIPER");
vendor_list.add("NOKIA");
vendor_list.add("ZTE");
} else if (domain_name.equals("RADIO")) {
vendor_list.clear();
vendor_list.add("ERICSSON");
vendor_list.add("NOKIA");
} else if (domain_name.equals("TRANSMISSION")) {
vendor_list.clear();
vendor_list.add("ERICSSON");
vendor_list.add("NCE");
vendor_list.add("NEC");
vendor_list.add("SAM");
} else if (domain_name.equals("CORE")) {
vendor_list.clear();
vendor_list.add("ZTE");

}

for (String vendor_name: vendor_list) {

List < gs_child2 > coutput1 = new ArrayList < gs_child2 > ();

if (domain_name.equals("IPRAN") && vendor_name.equals("HUAWEI")) {
g.add("Group");
} else if (domain_name.equals("IPBB")) {
g.add("Group");
} else if (domain_name.equals("TRANSMISSION") && vendor_name.equals("NEC")) {
g.add("Tables");
} else if (domain_name.equals("TRANSMISSION") && vendor_name.equals("ERICSSON")) {
g.add("Tables");
} else if (domain_name.equals("TRANSMISSION") && vendor_name.equals("NCE")) {
g.add("Tables");
} else if (domain_name.equals("TRANSMISSION") && vendor_name.equals("SAM")) {
g.add("Tables");
}
for (String value1: g) {
generation = value1;

List < gs_child3 > coutput2 = new ArrayList < gs_child3 > ();

if (domain_name.equals("IPRAN") && vendor_name.equals("HUAWEI")) {

//database_name = config.getProperty("database.performance_zambia_ipran");
if (generation.equals("Group")) {
gs_child3 c1 = new gs_child3("SNMP", "file");
coutput2.add(c1);
}
} else if (domain_name.equals("IPBB")) {

if (generation.equals("Group")) {
ArrayList < String > command_name = ipbb_commands(domain_name, vendor_name);

for (String commands: command_name) {
gs_child3 c1 = new gs_child3(commands, "file");
coutput2.add(c1);
}

}
} else if (domain_name.equals("TRANSMISSION")) {

if (generation.equals("Tables")) {
ArrayList < String > command_name = ipbb_commands(domain_name, vendor_name);

for (String commands: command_name) {
gs_child3 c1 = new gs_child3(commands, "file");
coutput2.add(c1);
}

}
}

gs_child2 aa = new gs_child2(generation, "folder", coutput2);
coutput1.add(aa);

}

gs_child1 c1 = new gs_child1(vendor_name, "folder", coutput1);

coutput.add(c1);
g.clear();

}

check_g_s_parent g1 = new check_g_s_parent(domain_name, "file", coutput);
output.add(g1);

}

close_mongo_connection(connection);
} catch (Exception e) {
log.error("   Performance     Exception occurs:---" + domain + "--------" + e.getMessage(), e);

}
if (log.isDebugEnabled()) {
log.debug("*************** exit from into get_tree_kpi_creation ****************");
}
return output;

}

//// TODO For generic table

@Override

public String getTableSpecificColsValsConditionGeneric(int id, String opco, String domain, String vendor1, String tableName, String columns, String conditions, String tabletype) {
if (log.isDebugEnabled()) {
log.debug("******" + id + "******    " + opco + "*************" + domain + "*     " + vendor1 + "* checked into getTableSpecificColsValsConditionGeneric ****************");
}
//System.out.println(tabletype);
//System.out.println(conditions);
int limit = 0;

MongoClient connection = get_mongo_connection();
BasicDBObject index = null;
index = new BasicDBObject("$hint", "NEName_1");
MongoCollection < Document > collection = null;

String id_table_kpi="";
if (tabletype.equals("kpi_creation")) {

limit = 100;
String table = tableName.toLowerCase();

MongoDatabase database = database(connection, domain, vendor1);
collection = database.getCollection(table);

} else if (tabletype.equals("update_kpis")) {
limit = 500;

String table = tableName.toLowerCase();

if (table.equals("kpi_formula")) {
id_table_kpi="_id.link_to_topology";
}

else {
	id_table_kpi="_id.link_with_dashboard";
}
MongoDatabase database = database(connection, domain, vendor1);

collection = database.getCollection(table);

} else if (tabletype.equals("nce_tables")) {
String start_date = null;
Properties config = getProperties();

//limit = 30;
String a = StringUtils.substringAfter(tableName.toLowerCase(), "_");

SimpleDateFormat formatter = new SimpleDateFormat(config.getProperty("performance.dateformat1"));
try {

Date date = formatter.parse(a);

start_date = new SimpleDateFormat(config.getProperty("performance.dateformat3")).format(date);

} catch (Exception e) {
log.error("   Performance     Exception occurs:---" + domain + "---------" + e.getMessage(), e);
}
String table = StringUtils.substringBefore(tableName, "_").toLowerCase() + "_" + start_date.replace("-", "");

MongoDatabase database = database(connection, domain, vendor1);

collection = database.getCollection(table);

}

else if(tabletype.equals("ping_fail_interface")) {

Properties config = getProperties();

MongoClient database_connection=get_mongo_connection();

MongoDatabase database = database_connection.getDatabase(config.getProperty("database.ip_audit_database"));

String current_date=current_only_date("zambia");
collection = database.getCollection(current_date.replace("-", "_")+"_ping_status");
//System.out.println(collection.getNamespace());
}


try {
ArrayList < Bson > fltr = new ArrayList < Bson > ();
Bson filter = null;
if (conditions.length() > 1) {
String cond = string_replace(conditions.replace("@DOT@", ".").replace("@SEMICOLON@", ";"));
if (cond.contains("and")) {
String cond_spls[] = cond.split("and");
for (String cv: cond_spls) {
if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")) {
String col = cv.substring(0, cv.indexOf("=")).trim();
String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
fltr.add(eq(col, val));
} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")) {
String col = cv.substring(0, cv.indexOf("like")).trim();
String val = cv.substring(cv.indexOf("=") + 1).replace("'", "").trim();
fltr.add(eq(col, java.util.regex.Pattern.compile("^.*" + val + ".*")));
} else if (cv.contains("=") && cv.contains("between")) {
String col = cv.substring(0, cv.indexOf("between")).trim();
String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "").trim();
String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
fltr.add(gte(col, val1));
fltr.add(lte(col, val2));
}
}
//filter=and(fltr);
} else if (cond.contains("AND")) {
String cond_spls[] = cond.split("AND");
for (String cv: cond_spls) {
if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")) {
String col = cv.substring(0, cv.indexOf("=")).trim();
String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
fltr.add(eq(col, val));
} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")) {
String col = cv.substring(0, cv.indexOf("like")).trim();
String val = cv.substring(cv.indexOf("=") + 1).replace("'", "").trim();
fltr.add(eq(col, java.util.regex.Pattern.compile("^.*" + val + ".*")));
} else if (cv.contains("=") && cv.contains("between")) {
String col = cv.substring(0, cv.indexOf("between")).trim();
String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "").trim();
String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
fltr.add(gte(col, val1));
fltr.add(lte(col, val2));
}
}
//filter=and(fltr);
} else if (!(cond.contains("AND") && !cond.contains("and")) && cond.length() > 1) {
if (cond.contains("=") && cond.contains("between")) {
String col = cond.substring(0, cond.indexOf("between")).trim();
String val1 = cond.substring(cond.indexOf("FROM=") + 5, cond.indexOf("TO=")).replace("'", "").trim();
String val2 = cond.substring(cond.indexOf("TO=") + 3).replace("'", "").trim();
fltr.add(gte(col, val1));
fltr.add(lte(col, val2));
} else if (cond.contains("=")) {
String col = cond.substring(0, cond.indexOf("=")).trim();
String val = cond.substring(cond.indexOf("=") + 1).trim().replace("'", "");
//System.out.println(col+"==="+val);
fltr.add(eq(col, val));
}
}
filter = and(fltr);
}
ArrayList < String > cls = new ArrayList < String > ();
ArrayList < Document > resultSet = null;
ArrayList < TableHeader > cols = new ArrayList < TableHeader > ();
JSONArray vals = new JSONArray();

if (!columns.equals("*")) {
Map < String, Object > groupMap = new HashMap < String, Object > ();
String columns_spls[] = columns.split(",");
for (String colm: columns_spls) {
cls.add(colm);
groupMap.put(colm, "$" + colm);
}
//System.out.println(filter);
DBObject groupFields = new BasicDBObject(groupMap);
if (filter != null && tabletype.equals("update_kpis")) {
resultSet = collection.aggregate(Arrays.asList(match(filter), group(groupFields), project(fields(include(cls))), sort(descending(id_table_kpi)), limit(limit))).into(new ArrayList < Document > ());
} else if (filter != null && tabletype.equals("nce_tables")) {
resultSet = collection.aggregate(Arrays.asList(match(filter), group(groupFields), project(fields(include(cls))))).hint(index).into(new ArrayList < Document > ());
} else if (filter != null && tabletype.equals("kpi_creation")) {
resultSet = collection.aggregate(Arrays.asList(match(filter), group(groupFields), project(fields(include(cls))), limit(limit))).into(new ArrayList < Document > ());
} else {
if (tabletype.equals("update_kpis")) { //descending("link_to_topology")
resultSet = collection.aggregate(Arrays.asList(group(groupFields), project(fields(include(cls))), sort(descending(id_table_kpi)), limit(limit))).into(new ArrayList < Document > ());
} else if (tabletype.equals("kpi_creation")) { //descending("link_to_topology")
resultSet = collection.aggregate(Arrays.asList(group(groupFields), project(fields(include(cls))), limit(limit))).into(new ArrayList < Document > ());
}
else if (tabletype.equals("ping_fail_interface")) { //descending("link_to_topology")
//System.out.println("hello");	
resultSet = collection.aggregate(Arrays.asList(group(groupFields), project(fields(include(cls))))).into(new ArrayList < Document > ());
}
else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields), project(fields(include(cls))))).hint(index).into(new ArrayList < Document > ());
}
}

String cols_spls[] = columns.split(",");
for (String col: cols_spls) {
String colm = col;
TableHeader th = new TableHeader(colm, colm);
cols.add(th);
}

for (Document docs: resultSet) {
JSONObject colval = new JSONObject();
String object = docs.get("_id").toString();
String substr = object.substring(object.indexOf("{{") + 2, object.indexOf("}}"));
String spls_cols[] = substr.split(",");
for (String cv: spls_cols) {
String cl = cv.split("=")[0].trim();
//String vl=cv.split("=")[1].trim().replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_()%]","");

String vl = cv.split("=")[1].trim();

colval.put(cl, vl);
}
vals.put(colval);
}
} else {

if (filter != null) {
if (tabletype.equals("kpi_creation") || tabletype.equals("update_kpis")) {
resultSet = collection.find(filter).limit(limit).into(new ArrayList < Document > ());
} else if (tabletype.equals("nce_tables")) {
resultSet = collection.find(filter).limit(limit).hint(index).into(new ArrayList < Document > ());
}
else if (tabletype.equals("ping_fail_interface")) { //descending("link_to_topology")
//System.out.println("hello");	
resultSet = collection.find(filter).into(new ArrayList < Document > ());
}
} else {
if (tabletype.equals("kpi_creation") || tabletype.equals("update_kpis")) {
resultSet = collection.find().limit(limit).into(new ArrayList < Document > ());
} else {
resultSet = collection.find().limit(limit).hint(index).into(new ArrayList < Document > ());
}

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
while (itr.hasNext()) {
String col = itr.next().toString();
if (!col.equals("_id")) {
cls.add(col);
TableHeader th = new TableHeader(col, col);
cols.add(th);
}
}
for (Document docs: resultSet) {
JSONObject colval = new JSONObject();
for (int j = 0; j < cls.size(); j++) {
if (docs.get(cls.get(j)).toString().length() > 0) {
colval.put(cls.get(j), docs.get(cls.get(j)));
} else {
colval.put(cls.get(j), "-");
}
}
vals.put(colval);
}

}

JSONArray jsonArrayFinal = new JSONArray();
JSONObject jsonObjectColVal = new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);

close_mongo_connection(connection);
log.debug("******" + id + "******    " + opco + "*************" + domain + "*     " + vendor1 + "* exit   getTableSpecificColsValsConditionGeneric ****************");
return jsonArrayFinal.toString();
} catch (Exception e) {
log.error("   Performance     Exception occurs:---" + domain + "------" + vendor1 + "---------" + e.getMessage(), e);

}
log.debug("******" + id + "******    " + opco + "*************" + domain + "*     " + vendor1 + "* exit   getTableSpecificColsValsConditionGeneric ****************");
return null;
}

//TODO to insert kpi from create kpi feature

@Override
public int insert_kpi(String opco, String admin_id, String domain, String vendor, String kpiname, String groups, String kpi_formula, String formula, String threshold, String topology, String direction, String element_name, String calc, String trouble_ticket, String severity) {

if (log.isDebugEnabled()) {
log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "*     " + vendor + "* enter   insert_kpi ****************");
}
MongoClient connection = get_mongo_connection();
MongoDatabase database = null;
database = database(connection, domain, vendor);

String rate = StringUtils.substringBefore(direction, "bali");

String unit = StringUtils.substringAfter(severity, "and");

Properties config = getProperties();

ArrayList < String > column_names = new ArrayList < String > ();
ArrayList < String > column_values = new ArrayList < String > ();
column_names.add("opco");
column_names.add("admin_id");
column_names.add("kpi_name");
column_names.add("groups");
column_names.add("kpi_formula");
column_names.add("formula");
column_names.add("threshold");
column_names.add("link_to_topology");
column_names.add("rate");
column_names.add("element_name");
column_names.add("calculation_type");
column_names.add("severity");
column_names.add("troubleticket");
column_names.add("unit");
column_names.add("correlation");

if (domain.equals("IPRAN") || domain.equals("IPBB")) {
log.debug("*********" + admin_id + "********* checked into insert_kpi  HUAWEI****************");
String actual_formula = table_name_with_column_name(formula, database);
//String actual_formula = table_name_with_column_name(formula, database);
column_values.add(opco);
column_values.add(admin_id);
column_values.add(kpiname);
column_values.add(groups);
column_values.add(actual_formula);
column_values.add(formula);
column_values.add(threshold);
column_values.add(topology);
column_values.add(rate);
column_values.add(domain);
column_values.add(calc);
column_values.add(StringUtils.substringBefore(severity, "and")); //severity
column_values.add(trouble_ticket);
column_values.add(StringUtils.substringAfter(severity, "and")); //unit
column_values.add("no"); //unit

insert_mongodb(database, column_names, column_values, "kpi_formula");

}

if (domain.equals("TRANSMISSION")) {
log.debug("*********" + admin_id + "********* checked into insert_kpi  TRANSMISSION****************");
String actual_formula = table_name_with_column_name(formula, database);
//String actual_formula = table_name_with_column_name(formula, database);
column_values.add(opco);
column_values.add(admin_id);
column_values.add(kpiname);
column_values.add(groups);
column_values.add(actual_formula);
column_values.add(formula);
column_values.add(threshold);
column_values.add(topology);
column_values.add(rate);
column_values.add(domain);
column_values.add(calc);
column_values.add(StringUtils.substringBefore(severity, "and")); //severity
column_values.add(trouble_ticket);
column_values.add(StringUtils.substringAfter(severity, "and")); //unit
column_values.add("no"); //unit

insert_mongodb(database, column_names, column_values, "kpi_formula");

}

close_mongo_connection(connection);
log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "*     " + vendor + "* exit   insert_kpi ****************");
return 0;

}

//TODO----get kpi names created by user
@Override
public ArrayList < String > getKpiNameExcel(String opco, String admin_id, String domain, String vendor_name, String element_name) {
log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "*     " + vendor_name + "* enter   getKpiNameExcel ****************");

ArrayList < String > output = new ArrayList < String > ();
List < String > list = new ArrayList < > ();
MongoClient connection = get_mongo_connection();
MongoDatabase database = null;
database = database(connection, domain, vendor_name);

//aading hint for report 3
if (domain.equals("IPBB") || domain.equals("IPRAN")) {

String check = "";

if (vendor_name.equals("NOKIA")) {
if (element_name.equals("snmp")) {
check = "ipbb";
} else if (element_name.equals("show port statistics")) {
check = "stats";
} else if (element_name.equals("show system cpu")) {
check = "usage";
}
else if (element_name.equals("show system memory")) {
check = "memory";
}
else if (element_name.equals("vlan")) {
check = "vlan";
}

} else if (vendor_name.equals("ERICSSON")) {

if (element_name.equals("snmp")) {
check = "ipbb";
} else if (element_name.equals("port counters detail")) {
check = "port_counter";
} else if (element_name.equals("dot1q counters detail")) {
check = "dot1q_counter";
}
else{
check = element_name;
}

} else if (vendor_name.equals("JUNIPER")) {

if (element_name.equals("snmp")) {
check = "ipbb";
} else if (element_name.equals("interfaces statistics detail")) {
check = "stats";
}
else if (element_name.equals("performance")) {
check = "performance";
}
}
else if (vendor_name.equals("DPTECH_FIREWALL")) {

if (element_name.equals("snmp")) {
check = "ipbb";
} else if (element_name.equals("performance")) {
check = "performance";
}
else if (element_name.equals("session")) {
check = "session";
}

} else if (vendor_name.equals("ZTE")) {
if (element_name.equals("snmp")) {
check = "ipbb";
}
else{
check = element_name;
}
} else if (vendor_name.equals("HUAWEI")) {
if (element_name.equals("snmp")) {
check = "ipran";
}
}

ArrayList < String > to_find = new ArrayList < String > ();
to_find.add("kpi_name");
to_find.add("kpi_formula");

MongoCollection < Document > document = database.getCollection("kpi_formula");
FindIterable < Document > docs = document.find(eq("admin_id", admin_id));
ArrayList < String > kpi_data = get_mongodb_distinct_values(docs, to_find);

for (String value: kpi_data) {

if (value.contains(check)) {
String split1[] = value.split("@AND@");
String kpi = split1[0].trim();

list.add(kpi);
}

}

Set < String > set = new LinkedHashSet < > ();
set.addAll(list);
list.clear();
output.addAll(set);

} else {
MongoCollection < Document > collection = database.getCollection("kpi_formula");

DistinctIterable < String > document = collection.distinct("kpi_name", and(eq("opco", opco), eq("admin_id", admin_id), eq("groups", element_name)), String.class);
for (String data: document) {
output.add(data);
}

}
log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "*     " + vendor_name + "* exit   getKpiNameExcel ****************");
close_mongo_connection(connection);
return output;
}

//TODO to insert kpi which is inserted during the creation of excel report
@Override
public int insert_user_specific_kpi(String opco, String admin_id, String event_name, ArrayList < String > kpi_name,
String group, String actual_formula, String formula, String threshold, String topology, String rate,
String domain, String element, String calculation, String vendor, ArrayList < String > element_list, String interface_select) {
if (log.isDebugEnabled()) {
log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "*     " + vendor + "* enter   insert_user_specific_kpi ****************");
}

StringBuilder elements = new StringBuilder();

for (String ele: element_list) {
elements.append(string_replace(ele) + ",");

}
elements.deleteCharAt(elements.length() - 1);


MongoClient connection = get_mongo_connection();
MongoDatabase database = database(connection, domain, vendor);

ArrayList < String > column_names_kpi_table = new ArrayList < String > ();
ArrayList < String > column_names_report_table = new ArrayList < String > ();

column_names_kpi_table.add("opco");
column_names_kpi_table.add("admin_id");
column_names_kpi_table.add("kpi_name");
column_names_kpi_table.add("groups");
column_names_kpi_table.add("kpi_formula");
column_names_kpi_table.add("formula");
column_names_kpi_table.add("threshold");
column_names_kpi_table.add("link_to_topology");
column_names_kpi_table.add("rate");
column_names_kpi_table.add("element_name");
column_names_kpi_table.add("calculation_type");
column_names_kpi_table.add("severity");
column_names_kpi_table.add("troubleticket");
column_names_kpi_table.add("unit");

column_names_report_table.add("opco");
column_names_report_table.add("admin_id");
column_names_report_table.add("ReportName");
column_names_report_table.add("ElementName");
column_names_report_table.add("SelectInterface");

ArrayList < String > column_values_report_table = new ArrayList < String > ();
column_values_report_table.add(opco);
column_values_report_table.add(admin_id);
column_values_report_table.add(string_replace(group));
column_values_report_table.add(elements.toString());
column_values_report_table.add(interface_select);

if (domain.equals("IPBB") || domain.equals("IPRAN")) { //hint as command for select tablename
column_names_report_table.add("command");
column_values_report_table.add(event_name);
column_names_kpi_table.add("correlation");
}

//for report_group
try {
log.debug("*********" + admin_id + "********* checked into insert into report_group   **" + domain + "===========" + vendor + "************");
insert_mongodb(database, column_names_report_table, column_values_report_table, "report_group");
} catch (Exception e) {
log.error("   Performance     Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);

}
//for kpi table 

for (String kpiname: kpi_name) {

String kpi = string_replace(kpiname);
actual_formula = mongo_generic(database, admin_id, kpi, "kpi_formula", "kpi_formula");
formula = mongo_generic(database, admin_id, kpi, "formula", "kpi_formula");
calculation = mongo_generic(database, admin_id, kpi, "calculation_type", "kpi_formula");
String unit = mongo_generic(database, admin_id, kpi, "unit", "kpi_formula");
threshold = mongo_generic(database, admin_id, kpi, "threshold", "kpi_formula");
topology = mongo_generic(database, admin_id, kpi, "link_to_topology", "kpi_formula");
rate = mongo_generic(database, admin_id, kpi, "rate", "kpi_formula");
ArrayList < String > column_values_kpi_table = new ArrayList < String > ();
column_values_kpi_table.add(opco);
column_values_kpi_table.add(admin_id);
column_values_kpi_table.add(string_replace(kpiname));
column_values_kpi_table.add(string_replace(group));
column_values_kpi_table.add(actual_formula);
column_values_kpi_table.add(formula);
column_values_kpi_table.add(threshold);
column_values_kpi_table.add(topology);
column_values_kpi_table.add(rate);

if (domain.equals("TRANSMISSION") && (vendor.equals("NCE") || vendor.equals("NEC") || vendor.equals("SAM") || vendor.equals("ERICSSON"))) {
column_values_kpi_table.add(element);
} else {
column_values_kpi_table.add(domain);
}

column_values_kpi_table.add(calculation);
column_values_kpi_table.add("minor");
column_values_kpi_table.add("no");
column_values_kpi_table.add(unit);

if (domain.equals("TRANSMISSION")) {

insert_mongodb(database, column_names_kpi_table, column_values_kpi_table, "kpi_formula_report");
} else {
column_names_kpi_table.add("correlation");
column_values_kpi_table.add("no");
insert_mongodb(database, column_names_kpi_table, column_values_kpi_table, "kpi_formula");
}
try {

} catch (Exception e) {

log.error("   Performance     Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
}
}

close_mongo_connection(connection);
log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "*     " + vendor + "* exit   insert_user_specific_kpi ****************");
return 0;
}

@Override
public ArrayList < String > kpi_report_group_name(String opco, String admin_id, String element_name, String vendor_name, String domain_name) {

if (log.isDebugEnabled()) {
log.debug("*********" + admin_id + "********* checked into insert into kpi_report_group_name   **" + domain_name + "===========" + vendor_name + "************");
}
ArrayList < String > output = new ArrayList < String > ();

MongoClient connection = get_mongo_connection();
MongoDatabase database = database(connection, domain_name, vendor_name);

MongoCollection < Document > collection = database.getCollection("report_group");

DistinctIterable < String > document = collection.distinct("ReportName", and(eq("opco", opco), eq("admin_id", admin_id)), String.class);

for (String data: document) {
output.add(data);
}
close_mongo_connection(connection);
log.debug("******" + admin_id + "******    " + opco + "*************" + domain_name + "*     " + vendor_name + "* exit   kpi_report_group_name ****************");
return output;
}

//TODO
//----to get kpi related to report name(Existing reports)----

@Override
public ArrayList < String > report_related_kpi(String opco, String admin_id, String report_name, String element_name, String vendor_name, String domain_name) {

log.debug("*********" + admin_id + "********* checked into insert into report_related_kpi   **" + domain_name + "===========" + vendor_name + "************");

ArrayList < String > output = new ArrayList < String > ();

Properties config = getProperties();
MongoClient connection = get_mongo_connection();
MongoDatabase database = database(connection, domain_name, vendor_name);
MongoCollection < Document > collection = null;
if ((domain_name.equals("TRANSMISSION")) && (vendor_name.equals("NCE") || vendor_name.equals("NEC") || vendor_name.equals("SAM") || vendor_name.equals("ERICSSON"))) {
collection = database.getCollection("kpi_formula_report");
} else {
collection = database.getCollection("kpi_formula");
}

DistinctIterable < String > document = collection.distinct("kpi_name", and(eq("opco", opco), eq("admin_id", admin_id), eq("groups", string_replace(report_name.trim()))), String.class);

for (String data: document) {
output.add(data);
}

close_mongo_connection(connection);

log.debug("******" + admin_id + "******    " + opco + "*************" + domain_name + "*     " + vendor_name + "* exit   report_related_kpi ****************");
return output;

}

//TODO //---- excel report----
@Override
public int excel_report(String admin_id, String report_name, String report_type, String report_interval, String start_date, String end_date, String start_time, String end_time, String domain, String vendor, String element, String save_report, String single_multiple, String mail) {
int returns = 1;
log.debug("******" + admin_id + "******    zambia*************" + domain + "*     " + vendor + "* enter   report_related_kpi ****************");

String natural_trend = "no";
Properties config = getProperties();
MongoClient connection = get_mongo_connection();
MongoDatabase database = null;
ArrayList < String > columns_for_report = new ArrayList < String > ();

if (domain.equals("IPRAN") && vendor.equals("HUAWEI")) {

log.debug("*********" + admin_id + "********* checked into excel_report   **" + domain + "===========" + vendor + "************");
database = connection.getDatabase(config.getProperty("database.performance_zambia_ipran"));
if (report_type.equals("15 Mins")) {
columns_for_report.add("ipaddress");
columns_for_report.add("devicename");
columns_for_report.add("start_date");
columns_for_report.add("start_time");
natural_trend = "yes";
}

} else if (domain.equals("IPBB")) {
log.debug("*********" + admin_id + "********* checked into excel_report   **" + domain + "===========" + vendor + "************");
database = database(connection, domain, vendor);
if (report_type.equals("5 Mins")) {
columns_for_report.add("ipaddress");
columns_for_report.add("devicename");
columns_for_report.add("start_date");
columns_for_report.add("start_time");
natural_trend = "yes";
}

} else if (domain.equals("TRANSMISSION") && vendor.equals("NCE")) {

log.debug("*********" + admin_id + "********* checked into excel_report   **" + domain + "===========" + vendor + "************");
database = connection.getDatabase(config.getProperty("database.performance_nce_transmission"));

natural_trend = "yes";

} else if (domain.equals("TRANSMISSION") && vendor.equals("NEC")) {

log.debug("*********" + admin_id + "********* checked into excel_report   **" + domain + "===========" + vendor + "************");
database = connection.getDatabase(config.getProperty("database.performance_nec_transmission"));

natural_trend = "yes";

} else if (domain.equals("TRANSMISSION") && vendor.equals("SAM")) {

log.debug("*********" + admin_id + "********* checked into excel_report   **" + domain + "===========" + vendor + "************");
database = connection.getDatabase(config.getProperty("database.performance_sam_transmission"));

natural_trend = "yes";

} else if (domain.equals("TRANSMISSION") && vendor.equals("ERICSSON")) {

log.debug("*********" + admin_id + "********* checked into excel_report   **" + domain + "===========" + vendor + "************");
database = connection.getDatabase(config.getProperty("database.performance_ericsson_transmission"));

natural_trend = "yes";

}

String StartTime = "", EndTime = "";
int check_time;
//checking time ie whether time is for full day or some limited value
String interval = "";
if (start_time.contains(":") && start_time.contains(":")) {

StartTime = start_time;
EndTime = start_time;

if (natural_trend.equals("no")) {

if (report_type.equals("15 Mins")) {

interval = "15";
} else if (report_type.equals("HOURLY")) {

interval = "60";
} else if (report_type.equals("DAY")) {

interval = "Day";
}

}
check_time = 1;

} else {

if (natural_trend.equals("no")) {

if (report_type.equals("15 Mins")) {
StartTime = "00:00:00";
EndTime = "23:45:00";
interval = "15";
} else if (report_type.equals("HOURLY")) {
StartTime = "00:00:00";
EndTime = "00:00:00";
interval = "60";
} else if (report_type.equals("DAY")) {
StartTime = "00:00:00";
EndTime = "00:00:00";
interval = "Day";
}
}
check_time = 0;
}
ArrayList < String > report_groups = new ArrayList < String > ();

try {
if (single_multiple.equals("multiple")) {
MongoCollection < Document > collection = database.getCollection("multiple_sheet_report");
DistinctIterable < String > document = collection.distinct("sheet_name", eq("report_name", report_name), String.class);
for (String docs: document) {
report_groups.add(docs);
}
} else {

report_groups.add(report_name);

}
} catch (Exception e) {

log.error("   Performance     Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
}

//unique dates as per report
ArrayList < String > unique_dates = unique_dates(report_interval, start_date, end_date, database, config);

if (unique_dates.size() > 0) {

ArrayList < String > kpis = new ArrayList < > (); //kpi/counters as per report
//kpis corrsponding to report
String table_for_nce_nec = "";
String url = "";

String url_address = config.getProperty("performance_mail_url_ip");
if (start_date.equals("-")) {
url = "" + url_address + "/performance/kpireport/" + domain + "/" + vendor + "/" + admin_id + "/" + report_name + "/" + current_date_for_report() + "/" + current_date_for_report() + "/" + start_time + "/" + end_time;

} else {
url = "" + url_address + "/performance/kpireport/" + domain + "/" + vendor + "/" + admin_id + "/" + report_name + "/" + start_date + "/" + end_date + "/" + start_time + "/" + end_time;

}


try {
if (natural_trend.equals("yes")) {



if (domain.equals("TRANSMISSION") && vendor.equals("ERICSSON")) {
try {
new excel_report().generate_report_excel(report_name, database, admin_id, report_groups, unique_dates, start_time, end_time, columns_for_report, domain, "Xplorg", table_for_nce_nec, domain, vendor, report_type, save_report, url, mail);

} catch (Exception e) {

log.error("   Performance     Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);

}

} else {


try {
new excel_report().generate_report_excel(report_name, database, admin_id, report_groups, unique_dates, start_time, end_time, columns_for_report, domain, "Xplorg", table_for_nce_nec, domain, vendor, report_type, save_report, url, mail);
} catch (Exception e) {
log.error("   Performance     Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
}

}

} else {

kpis = mongo_select1_where2(database, "kpi_name", "kpi_formula", "admin_id", admin_id, "groups", report_groups.get(0));

String elements = (mongo_select1_where2(database, "ElementName", "report_group", "admin_id", admin_id, "ReportName", report_groups.get(0)).get(0));

//String interfacee = (mongo_select1_where2(database, "SelectInterface", "report_group", "admin_id", admin_id, "ReportName", report_groups.get(0)).get(0));
ArrayList < String > element_name = new ArrayList < String > ();
if (elements.equals("ALL")) {
MongoCollection < Document > collection = database.getCollection("ip_for_snmp");
DistinctIterable < String > document = collection.distinct("DEVICENAME", String.class);

for (String doc: document) {
element_name.add(doc);
}

} else {

String split[] = elements.split(",");
for (String elementss: split) {
element_name.add(elementss);
}
}

try {
create_report(database, domain, vendor, admin_id, unique_dates, StartTime, EndTime, interval, kpis, element_name, report_name);
} catch (Exception e) {
log.error("   Performance     Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
}

new excel_report().generate_report_excel_without_natural_trend(database, admin_id, report_name, domain, "Xplorg", save_report, vendor, domain, url);

}

} catch (Exception e) {

log.error("   Performance     Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
}

} else {
returns = 0;
}
log.debug("******" + admin_id + "******    zambia*************" + domain + "*     " + vendor + "* exit   report_related_kpi ****************");

close_mongo_connection(connection);

return returns;

}

//TODO for element_blink
@Override
public ArrayList < performance_nokia_radio_element_blink_main > element_blink(String opco, String admin_id, String domain, String vendor) {

log.debug("*********" + admin_id + "********* checked into insert into element_blink   **" + domain + "===========" + vendor + "************");

ArrayList < performance_nokia_radio_element_blink_main > output = new ArrayList < performance_nokia_radio_element_blink_main > ();
performance_nokia_radio_element_blink_main parent = null;

MongoClient connection = get_mongo_connection();
MongoDatabase database = null;

//for current date
String start_date = current_date(opco);

database = database(connection, domain, vendor);

//get kpis which are link with topology

ArrayList < String > kpi_with_topology = (mongo_select1_where2(database, "kpi_name", "kpi_formula", "admin_id", admin_id, "link_to_topology", "yes"));


ArrayList < String > get_column_name = new ArrayList < String > ();

ArrayList < String > ar_IPaddress = new ArrayList < String > ();
ArrayList < String > ar_DeviceName = new ArrayList < String > ();
ArrayList < String > ar_DeviceName_check = new ArrayList < String > ();
ArrayList < String > ar_KPIName = new ArrayList < String > ();
ArrayList < String > ar_Time = new ArrayList < String > ();
ArrayList < String > ar_Threshold = new ArrayList < String > ();

int cases = 0;

//cases=1 with site id

//case=2 with device name and ip
String latest_time = get_single_column_value(database, "present_time_calc", "value");
String current_time = "";
String table_name = "";
MongoCollection < Document > collection = null;
if ((domain.equals("CORE") && vendor.equals("ZTE")) || (domain.equals("RADIO") && vendor.equals("ERICSSON"))) {

get_column_name.add("Site_ID");
get_column_name.add("KPIName");
get_column_name.add("Time");
get_column_name.add("Threshold");

cases = 1;
current_time = latest_time; //getting previous time for getting single value for all interfaces
collection = database.getCollection(start_date.replace("-", "_") + "_kpi_alerts");
} else {
get_column_name.add("IPAddress");
get_column_name.add("Site_ID");
get_column_name.add("KPIName");
get_column_name.add("Time");
get_column_name.add("Threshold");
cases = 2;
current_time = sub_mins(latest_time, -10); //getting previous time for getting single value for all interfaces
collection = database.getCollection("for_sla");
}

FindIterable < Document > documents = collection.find();
ArrayList < String > distinct_values = get_mongodb_distinct_values(documents, get_column_name);

for (String unique_value: distinct_values) {
String split1[] = unique_value.split("@AND@");

if (cases == 2) {
ar_IPaddress.add(split1[0].trim());
ar_DeviceName.add(split1[1].trim());
ar_DeviceName_check.add(split1[1].trim());
ar_KPIName.add(split1[2].trim());
ar_Time.add(split1[3].trim());
ar_Threshold.add(split1[4].trim());
}

if (cases == 1) {

ar_DeviceName.add(split1[0].trim());
ar_DeviceName_check.add(split1[0].trim());
ar_KPIName.add(split1[1].trim());
ar_Time.add(split1[2].trim());
ar_Threshold.add(split1[3].trim());
}

}

Set < String > distinct = new LinkedHashSet < String > ();
distinct.addAll(ar_DeviceName_check);
ar_DeviceName_check.clear();
ar_DeviceName_check.addAll(distinct);

for (String device: ar_DeviceName_check) {
ArrayList < String > ar_threshold = new ArrayList < String > ();

ArrayList < String > ar_kpi_name = new ArrayList < String > ();

ArrayList < performance_nokia_radio_element_blink_children > ar_children = new ArrayList < performance_nokia_radio_element_blink_children > ();
for (int i = 0; i < ar_DeviceName.size(); i++) {

if (device.equals(ar_DeviceName.get(i))) {

if (kpi_with_topology.contains(ar_KPIName.get(i))) {
ar_threshold.add(ar_Threshold.get(i));
String trouble_ticket = (mongo_select1_where2(database, "troubleticket", "kpi_formula", "admin_id", admin_id, "kpi_name", ar_KPIName.get(i)).get(0));
ar_kpi_name.add(ar_KPIName.get(i) + "#ticket#" + trouble_ticket + "@time" + ar_Time.get(i));
}
}

}

if (ar_kpi_name.size() > 0) {

performance_nokia_radio_element_blink_children child = new performance_nokia_radio_element_blink_children(ar_threshold, ar_kpi_name);

ar_children.add(child);
String device_name = "";
try {
if (cases == 2) {
device_name = device + "(" + ar_IPaddress.get(ar_DeviceName.indexOf(device)) + ")";
} else {
device_name = device;
}

parent = new performance_nokia_radio_element_blink_main(device_name, ar_children);

output.add(parent);
} catch (Exception e) {
log.error("   Performance     Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);

}

}

}
close_mongo_connection(connection);
return output;
}

@Override
public int updateKpiTable(String opco, String admin_id, String domain, String vendor, String kpiName, String groupName, String formula, String rate, String threshold, String topology, String severity, String troubleticket,String tablename) {
log.debug("*********" + admin_id + "********* checked into insert into updateKpiTable   **" + domain + "===========" + vendor + "************");

MongoClient connection = get_mongo_connection();
MongoDatabase database = null;

/*
if(domain.equals("IPRAN")&&vendor.equals("HUAWEI")) {

log.debug("*********"+admin_id+"********* checked into report_related_kpi   **"+domain+"==========="+vendor+"************");
database=connection.getDatabase(config.getProperty("database.performance_zambia_ipran"));

}

else if(domain.equals("IPBB")&&vendor.equals("ZTE")) {

log.debug("*********"+admin_id+"********* checked into report_related_kpi   **"+domain+"==========="+vendor+"************");
database=connection.getDatabase(config.getProperty("database.performance_zambia_mpbn"));



}
*/

database = database(connection, domain, vendor);
String actual_formula = "";

try {
if (domain.equals("IPRAN") || domain.equals("IPBB")) {
actual_formula = table_name_with_column_name(string_replace(formula), database);
} else {
actual_formula = mongo_generic(database, admin_id, string_replace(kpiName), "kpi_formula",tablename);
}
} catch (Exception e1) {
log.error("   Performance     Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e1.getMessage(), e1);

}

MongoCollection < Document > collection = database.getCollection(tablename);

try {
	
if(tablename.equals("kpi_formula")) {
collection.updateMany(
		//Filters.and(eq("kpi_name", string_replace(kpiName)), eq("admin_id", admin_id), eq("groups", groupName)),
Filters.and(eq("kpi_name", string_replace(kpiName))),
Updates.combine(
Updates.set("kpi_formula", actual_formula),
Updates.set("formula", string_replace(formula)),
Updates.set("threshold", threshold),
Updates.set("link_to_topology", topology),
Updates.set("rate", rate),
Updates.set("severity", severity),
Updates.set("troubleticket", troubleticket),
Updates.set("correlation", "no")
));
}

else {

collection.updateMany(
		//Filters.and(eq("kpi_name", string_replace(kpiName)), eq("admin_id", admin_id), eq("groups", groupName)),
Filters.and(eq("kpi_name", string_replace(kpiName))),
Updates.combine(
Updates.set("kpi_formula", actual_formula),
Updates.set("formula", string_replace(formula)),
Updates.set("threshold", threshold),
Updates.set("link_with_dashboard", topology),
Updates.set("rate", rate),
Updates.set("severity", severity),
Updates.set("troubleticket", troubleticket),
Updates.set("correlation", "no")
));

}
	




} catch (Exception e) {
log.error("   Performance     Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
}
log.debug("******" + admin_id + "******    zambia*************" + domain + "*     " + vendor + "* exit   updateKpiTable ****************");
close_mongo_connection(connection);

return 1;
}

//TODO get any list
@Override
public ArrayList < String > get_any_list(String opco, String admin_id, String domain, String vendor, String element, String type1) {
log.debug("*********" + admin_id + "********* checked into get_any_list  **" + domain + "===========" + vendor + "************");

ArrayList < String > output = new ArrayList < String > ();
MongoClient connection = get_mongo_connection();

MongoDatabase database = database(connection, domain, vendor);

DistinctIterable < String > document = null;
FindIterable < Document > documents = null;

String type = string_replace(type1);

String search_type = "values";

if (type.equals("ericsson_element_structure")) {

MongoCollection < Document > collection = database.getCollection("objects");
document = collection.distinct("element_name", String.class);
} else if (type.equals("select_multiple_sheet_report")) {

MongoCollection < Document > collection = database.getCollection("multiple_sheet_report");
document = collection.distinct("report_name", eq("admin_id", admin_id), String.class);
} 

else if (type.equals("kpi_list")) {

MongoCollection < Document > collection = database.getCollection("filter_kpi_formula");
document = collection.distinct("formula", and(eq("admin_id", admin_id), eq("groups", string_replace(element)), eq("link_with_dashboard", "yes")), String.class);
}


else if (type.equals("nce_sla_kpi_list")) {

MongoCollection < Document > collection = database.getCollection("sla_alerts");
document = collection.distinct("KPIName", and( eq("NEName", string_replace(element))), String.class);
}

else if (type.equals("filter_groups")) {

MongoCollection < Document > collection = database.getCollection("filter_kpi_formula");
document = collection.distinct("groups", and( eq("link_with_dashboard", "yes")), String.class);
}

else if (type.equals("nec_sla_groups")) {

MongoCollection < Document > collection = database.getCollection("sla_alerts");
document = collection.distinct("groups", and( eq("NEName", java.util.regex.Pattern.compile("^.*" + element + ".*"))), String.class);
}


else if (type.equals("transmission_nce_elements")) {

MongoCollection < Document > collection = database.getCollection(element.toLowerCase() + "_element");
document = collection.distinct("NEName", String.class);
} 

else if (type.equals("microwave_ericsson_kpi_groups")) {

MongoCollection < Document > collection = database.getCollection("kpi_formula");
document = collection.distinct("groups", eq("admin_id", admin_id), String.class);
} else if (type.equals("element_list")) {

if (element.length() > 1) {
MongoCollection < Document > collection = database.getCollection("objects");
document = collection.distinct("object", and(eq("element_name", string_replace(element))), String.class);

} else {

MongoCollection < Document > collection = database.getCollection("objects");
document = collection.distinct("element_name", String.class);
}
} else if (type.equals("ericsson_element_list")) {

MongoCollection < Document > collection = database.getCollection(element.toLowerCase());
//System.out.println(element.toLowerCase());
document = collection.distinct("SITE_ID", String.class);

} else if (type.equals("ericsson_element_object")) {

String actual_string = string_replace(element);

String table = StringUtils.substringBefore(actual_string, "join");
String site_id = StringUtils.substringAfter(actual_string, "join");

MongoCollection < Document > collection = database.getCollection(table.toLowerCase());
document = collection.distinct("MEASOBJLDN", eq("SITE_ID", string_replace(site_id)), String.class);

} else if (type.equals("sla_ericsson_element_object")) {

String actual_string = string_replace(element);

String group = StringUtils.substringBefore(actual_string, "join");
String site_id = StringUtils.substringAfter(actual_string, "join");

MongoCollection < Document > collection = database.getCollection("sla_alerts");
document = collection.distinct("MEASOBJLDN", eq("SITE_ID", string_replace(site_id)), String.class);

} else if (type.equals("report_column_name")) {

search_type = "columns";

if (element.length() > 1) {
if (vendor.equals("NEC")) {
if (element.toLowerCase().equals("metering")) {
MongoCollection < Document > collection = database.getCollection("mtr" + "_" + previous_day().replace("-", ""));

documents = collection.find().limit(20);
} else if (element.toLowerCase().equals("inventory")||element.toLowerCase().equals("visibility")) {
BasicDBObject index = new BasicDBObject("$hint", "NE_NAME_1");
MongoCollection < Document > collection = database.getCollection(element.toLowerCase() + "_" + previous_day().replace("-", ""));
documents = collection.find().limit(20).hint(index);
} else {

//MongoCollection < Document > collection = database.getCollection(element.toLowerCase() + "_1day_" + previous_day().replace("-", ""));
//documents = collection.find().limit(1);
MongoCollection < Document > collection = database.getCollection(element.toLowerCase() + "_1day");
documents = collection.find().limit(20);
}

} else if (vendor.equals("SAM")) {
BasicDBObject index = null;
if (element.toLowerCase().contains("stats")) {

index = new BasicDBObject("$hint", "monitoredObjectSiteName_1");
} else {

index = new BasicDBObject("$hint", "siteName_1");
}

MongoCollection < Document > collection = database.getCollection(element.toLowerCase() + "_" + previous_day().replace("-", ""));;
documents = collection.find().limit(600).hint(index);
} else if (vendor.equals("NCE")) {

if (element.toLowerCase().equals("sdh") || element.toLowerCase().equals("wdm")) {
MongoCollection < Document > collection = database.getCollection(element.toLowerCase());
documents = collection.find().limit(20);
} else {
BasicDBObject index = new BasicDBObject("$hint", "NE_Name_1");
MongoCollection < Document > collection = database.getCollection(element.toLowerCase() + "_" + previous_day().replace("-", ""));
documents = collection.find().limit(20).hint(index);
}

} else {

if (element.toLowerCase().contains("report")) {
MongoCollection < Document > collection = database.getCollection(element.toLowerCase() + "_" + previous_day().replace("-", ""));
documents = collection.find().limit(10);
} else {
BasicDBObject index = new BasicDBObject("$hint", "ELEMENT_NAME_1");
MongoCollection < Document > collection = database.getCollection(element.toLowerCase());
documents = collection.find().limit(50).hint(index);
}

}

}

} else if (type.equals("sam_objects")) {

if (element.length() > 1) {

String table = StringUtils.substringBefore(element, "join");
String id = StringUtils.substringAfter(string_replace(element), "join");
MongoCollection < Document > collection = database.getCollection(table.toLowerCase());
document = collection.distinct("displayedName", eq("monitoredObjectSiteName", id), String.class);
}

} else if (type.equals("raw_reports")) {

if (element.length() > 1) {

MongoCollection < Document > collection = database.getCollection("raw_reports");
document = collection.distinct("report_name", eq("report_type", element), String.class);
}

} else if (type.equals("inventory_reports")) {

if (element.length() > 0) {

MongoCollection < Document > collection = database.getCollection("raw_reports");
document = collection.distinct("report_name", String.class);
}

} else if (type.equals("transmission_element_name")) {

if (element.length() > 1) {
if (vendor.equals("NCE")) {
String column_name = "NE";
if (element.equals("NE_Report") || element.equals("Port_Report")) {

column_name = "NE_Name";
MongoCollection < Document > collection = database.getCollection(element.toLowerCase() + "_" + previous_day().replace("-", ""));
document = collection.distinct(column_name, String.class);

} else if (element.equals("Optical_Report")) {

column_name = "NE_Name";
MongoCollection < Document > collection = database.getCollection(element.toLowerCase() + "_" + previous_day().replace("-", ""));
document = collection.distinct(column_name, String.class);

} else if (element.equals("Board_Report") || element.equals("Subcard_Report") || element.equals("Subrack_Report")) {

column_name = "NE_Name";
MongoCollection < Document > collection = database.getCollection(element.toLowerCase() + "_" + previous_day().replace("-", ""));
document = collection.distinct(column_name, String.class);

} else {
column_name = "NEName";

//MongoCollection < Document > collection = database.getCollection(element.toLowerCase());
MongoCollection < Document > collection = database.getCollection(element.toLowerCase() + "_element");
document = collection.distinct(column_name, String.class);

}

} else if (vendor.equals("ERICSSON")) {
String column_name = "NodeId";
MongoCollection < Document > collection = database.getCollection(element.toLowerCase() + "_" + previous_day().replace("-", ""));
document = collection.distinct(column_name, String.class);

} else if (vendor.equals("SAM")) {

String column_name = "siteName";
MongoCollection < Document > collection = database.getCollection(element.toLowerCase() + "_" + previous_day().replace("-", ""));
document = collection.distinct(column_name, String.class);

} else {

String column_name = "NE Name";
if (element.equals("Metering")) {
MongoCollection < Document > collection = database.getCollection("mtr" + "_" + previous_day().replace("-", ""));
document = collection.distinct(column_name, String.class);
} else if (element.equals("Inventory")) {
MongoCollection < Document > collection = database.getCollection("inventory" + "_" + previous_day().replace("-", ""));
document = collection.distinct("NE_NAME", String.class);
}
else if (element.equals("Visibility")) {
MongoCollection < Document > collection = database.getCollection("visibility" + "_" + previous_day().replace("-", ""));
document = collection.distinct("NE_NAME", String.class);
}


else {

//MongoCollection < Document > collection = database.getCollection(element.toLowerCase() + "_1day_" + previous_day().replace("-", "")); 
MongoCollection < Document > collection = database.getCollection(element.toLowerCase() + "_1day");
document = collection.distinct(column_name, String.class);

}

}
}

} else if (type.equals("sam_element_details")) {
search_type = "sam";
ArrayList < String > to_find = new ArrayList < String > ();
to_find.add("monitoredObjectSiteId");
to_find.add("monitoredObjectSiteName");
BasicDBObject index = new BasicDBObject("$hint", "monitoredObjectSiteId_1");

//System.out.println(element.toLowerCase());
MongoCollection < Document > collection = database.getCollection(element.toLowerCase());
FindIterable < Document > docs = collection.find().hint(index);
ArrayList < String > kpi_data = get_mongodb_distinct_values(docs, to_find);

for (String value: kpi_data) {

String split1[] = value.split("@AND@");
String a = split1[0];
String b = split1[1];

output.add(a + "##" + b);

}

} else if (type.equals("sam_sla_element_groups")) {
MongoCollection < Document > collection = database.getCollection("sla_alerts");
document = collection.distinct("Groups", and(eq("monitoredObjectSiteName", java.util.regex.Pattern.compile("^.*" + element + ".*"))), String.class);

} else if (type.equals("sam_for_each_element_groups")) {
MongoCollection < Document > collection = database.getCollection("filter_kpi_formula");
document = collection.distinct("groups", and(eq("admin_id", admin_id),eq("link_with_dashboard", "yes")), String.class);

} else if (type.equals("ericsson_sla_element_groups")) {
MongoCollection < Document > collection = database.getCollection("sla_alerts");
document = collection.distinct("Groups", and(eq("SITE_ID", java.util.regex.Pattern.compile("^.*" + element + ".*"))), String.class);

} else if (type.equals("nce_sla_element_groups")) {
MongoCollection < Document > collection = database.getCollection("sla_alerts");


document = collection.distinct("Groups", and(eq("NEName", java.util.regex.Pattern.compile("^.*" + element + ".*"))), String.class);

} else if (type.equals("nec_sla_slot")) {


String group = StringUtils.substringBefore(element, "join");
String element_name = string_replace(StringUtils.substringAfter(element, "join"));;

MongoCollection < Document > collection = database.getCollection("sla_alerts");
document = collection.distinct("Port", and(eq("groups", group), eq("NEName", element_name)), String.class);
} else if (type.equals("sam_sla_element_details")) {
search_type = "sam";
ArrayList < String > to_find = new ArrayList < String > ();
to_find.add("monitoredObjectSiteId");
to_find.add("monitoredObjectSiteName");

MongoCollection < Document > collection = database.getCollection("sla_alerts");
FindIterable < Document > docs = collection.find();
ArrayList < String > kpi_data = get_mongodb_distinct_values(docs, to_find);

for (String value: kpi_data) {
if (value.contains(element)) {
String split1[] = value.split("@AND@");
String a = split1[0];
String b = split1[1];

output.add(a + "##" + b);
}
}

} else {

if (type.contains("&&")) {
String type_change = string_replace(type);

MongoCollection < Document > collection = database.getCollection(element);
document = collection.distinct(StringUtils.substringBefore(type, "&&"), eq("device_name", StringUtils.substringAfter(string_replace(type_change), "&&")), String.class);

;
} else {
MongoCollection < Document > collection = database.getCollection(element);
document = collection.distinct(type, String.class);
}

if (type.equals("nec_sla_kpi_list")) {
search_type = "nec_sla";
MongoCollection < Document > collection = database.getCollection("kpi_formula");
DistinctIterable < String > document1 = collection.distinct("kpi_name", and(eq("admin_id", admin_id), eq("groups", element), eq("link_to_topology", "yes")), String.class);
ArrayList < String > old = new ArrayList < String > ();
for (String k1: document1) {
old.add(k1);
}
MongoCollection < Document > collection2 = database.getCollection("sla_alerts");
DistinctIterable < String > document2 = collection2.distinct("KPIName", and(eq("groups", element)), String.class);

for (String k2: document2) {

if (old.contains(k2)) {
output.add(k2);
}

}

}

}

if (search_type.equals("values")) {
if (type.equals("inventory_reports")) {

for (String docs: document) {
if (!docs.endsWith("stats")) {
output.add(docs);
}

}
} else {
for (String docs: document) {

if(type.equals("filter_groups")&&domain.equals("TRANSMISSION")&&vendor.equals("ERICSSON")) {	// to add _24

if(docs.equals("radiolinkg826")) {
output.add(docs+"_24h")	;
}
else if(docs.equals("adaptivecodingandmodulation")) {
output.add(docs+"_24h")	;
}
}

output.add(docs);
}
}

} else if (search_type.equals("sam")) {

} else if (search_type.equals("nec_sla")) {

} else {

for (Document d: documents) {
for (Entry < String, Object > entry: d.entrySet()) {

if (!entry.getKey().toString().contains("_id")) {

String column = entry.getKey().toString();
//String value=entry.getValue().toString();

//String join=column+"=="+value;

output.add(column.replace("\"", ""));

Set < String > set = new LinkedHashSet < > ();
set.addAll(output);
output.clear();
output.addAll(set);

}
}

}

}
log.debug("******" + admin_id + "******    zambia*************" + domain + "*     " + vendor + "* exit   get_any_list ****************");
close_mongo_connection(connection);
return output;
}

@Override
public int create_multiple_sheet(String opco, String admin_id, String domain, String vendor, String report_name, ArrayList < String > sheets) {

log.debug("******" + admin_id + "******    zambia*************" + domain + "*     " + vendor + "* enter   create_multiple_sheet ****************");
MongoClient connection = get_mongo_connection();

MongoDatabase database = database(connection, domain, vendor);

ArrayList < String > columns = new ArrayList < String > ();

columns.add("admin_id");
columns.add("sheet_name");
columns.add("report_name");

for (String sheet: sheets) {
ArrayList < String > values = new ArrayList < String > ();
values.add(admin_id);
values.add(sheet);
values.add(report_name);

try {

insert_mongodb(database, columns, values, "multiple_sheet_report");

} catch (Exception e) {
log.error("   Performance     Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(), e);
}

}
log.debug("******" + admin_id + "******    zambia*************" + domain + "*     " + vendor + "* exit   create_multiple_sheet ****************");
close_mongo_connection(connection);

return 1;
}

//TODO to insert schduling report

@Override
public int insert_schdule_report(String data) {

int api_status = 0;
try {
String status;


String tablename = "";
String opco = "";
String admin_id = "";
String domain = "";
String vendor = "";
String report_type = "";
String report_name = "";
String report_schdule_date = "";
String report_schdule_time = "";
String report_start_date = "";
String report_end_date = "";
String report_start_time = "";
String report_end_time = "";
String report_interval = "";
String report_sheets = ""; //for inventory or performance

String data_split[] = data.split(";");

ArrayList < String > columns = new ArrayList < String > ();
ArrayList < String > values = new ArrayList < String > ();

tablename = replace_value(data_split[0]);
opco = replace_value(data_split[1]);
admin_id = replace_value(data_split[2]);
domain = replace_value(data_split[3]);
vendor = replace_value(data_split[4]);
String rt = replace_value(data_split[5]); //report type
if (rt.equals("Select Date")) {
report_type = "Manually";
} else {
report_type = rt;
}
report_name = replace_value(data_split[6]);

String rscd = replace_value(data_split[7]);


report_schdule_date = date_as_per_day(rscd); //7

report_schdule_time = replace_value(data_split[8]);

String rsd = replace_value(data_split[9]); //report start date

//changing date as per report type
report_sheets = replace_value(data_split[15]);

if (!report_type.equals("Manually")) {

SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd");
Date datCurrent = sdfd.parse(report_schdule_date);
Calendar calDateCurrent = Calendar.getInstance();
calDateCurrent.setTime(datCurrent);

if (report_type.equalsIgnoreCase("Daily")) {

if (report_sheets.equals("Inventory")) {
calDateCurrent.add(Calendar.DAY_OF_MONTH, 0);
report_start_date = sdfd.format(calDateCurrent.getTime());
} else {
calDateCurrent.add(Calendar.DAY_OF_MONTH, -1);
report_start_date = sdfd.format(calDateCurrent.getTime());
}

} else if (report_type.equalsIgnoreCase("Weekly")) {

calDateCurrent.add(Calendar.WEEK_OF_MONTH, -1);
report_start_date = sdfd.format(calDateCurrent.getTime());
} else if (report_type.equalsIgnoreCase("Monthly")) {

calDateCurrent.add(Calendar.MONTH, -1);
report_start_date = sdfd.format(calDateCurrent.getTime());
}

} else {
if (rsd.equals("-")) {
report_start_date = current_date();
} else {
DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
Date startDate = (Date) formatter.parse(rsd);
report_start_date = new SimpleDateFormat("yyyy-MM-dd").format(startDate);;
}
}

String red = replace_value(data_split[10]); //report end date

if (!report_type.equals("Manually")) {
report_end_date = report_schdule_date;

} else {

if (red.equals("-")) {
report_end_date = current_date();
} else {

DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
Date startDate = (Date) formatter.parse(red);
report_end_date = new SimpleDateFormat("yyyy-MM-dd").format(startDate);;

}

}

report_start_time = replace_value(data_split[11]);
report_end_time = replace_value(data_split[12]);
report_interval = replace_value(data_split[13]);

status = "Running";
columns.add("opco");
values.add(opco);

columns.add("user_id");
values.add(admin_id);

columns.add("domain");
values.add(domain);

columns.add("vendor");
values.add(vendor);

columns.add("report");
values.add(report_sheets);

columns.add("report_type");
values.add(report_type);

columns.add("report_name");
values.add(report_name);

columns.add("report_schedule_date");
values.add(report_schdule_date);

columns.add("report_schedule_time");
values.add(report_schdule_time);

columns.add("report_start_date");
values.add(report_start_date);

columns.add("report_end_date");
values.add(report_end_date);

columns.add("report_start_time");
values.add(report_start_time);

columns.add("report_end_time");
values.add(report_end_time);

columns.add("report_interval");
values.add(report_interval);

columns.add("status");
values.add(status);

log.debug("******" + admin_id + "******    zambia*************" + domain + "*     " + vendor + "* enter   create_multiple_sheet ****************");
MongoClient connection = get_mongo_connection();

MongoDatabase database = database(connection, "all", "topology");
insert_mongodb(database, columns, values, tablename);
log.debug("******" + admin_id + "******    zambia*************" + domain + "*     " + vendor + "* exit   create_multiple_sheet ****************");
try {
close_mongo_connection(connection);
api_status = 1;
} catch (Exception e) {
api_status = 0;
}

} catch (ParseException e) {

log.error("   Performance     Exception occurs:----------" + e.getMessage(), e);
}

return api_status;
}

public String getInterfaceTraffic(String domain, String vendor, String tableName, String columns, String conditions) {
if (log.isDebugEnabled()) {
log.debug("************    zambia*************" + domain + "*     " + vendor + "* enter   getInterfaceTraffic ****************");
}
try {
ArrayList < Bson > fltr = new ArrayList < Bson > ();
Bson filter = null;
if (conditions.length() > 1) {
String cond = conditions.replace("@DOT@", ".").replace("@SEMICOLON@", ";").replaceAll("@FORWARDSLASH@","/").replaceAll("@BACKWARDSLASH@","\\");
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
filter = and(fltr);
} else if (!cond.contains(" AND ") && !cond.contains(" and ") && cond.contains("=")) {
String col = cond.split("=")[0].trim();
String val = cond.split("=")[1].trim().replace("'", "");;
fltr.add(eq(col, val));
filter = and(fltr);
}
}
MongoClient connection = get_mongo_connection();
MongoDatabase database = database(connection, domain, vendor);
MongoCollection < Document > collection = database.getCollection(tableName);
ArrayList < String > cls = new ArrayList < String > ();
ArrayList < Document > resultSet = null;
ArrayList < TableHeader > cols = new ArrayList < TableHeader > ();
JSONArray vals = new JSONArray();

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
resultSet = collection.aggregate(Arrays.asList(match(filter), group(groupFields), project(fields(include(cls))))).into(new ArrayList < Document > ()); //,limit(500)
} else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields), project(fields(include(cls))))).into(new ArrayList < Document > ()); //,limit(500)
}
String cols_spls[] = columns.split(",");
for (String col: cols_spls) {
String colm = col;
TableHeader th = new TableHeader(colm, colm);
cols.add(th);
}

for (Document docs: resultSet) {
JSONObject colval = new JSONObject();
String object = docs.get("_id").toString();
String substr = object.substring(object.indexOf("{{") + 2, object.indexOf("}}"));
String spls_cols[] = substr.split(",");
for (String cv: spls_cols) {
String cl = cv.substring(0, cv.indexOf("=")).trim();
String vl = cv.substring(cv.indexOf("=") + 1).trim().replace(",", "@COMMA@").replace("/", "@FORWARDSLASH@").replace("\\", "@BACKWARDSLASH@").replace("\"", ""); //.replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","")
colval.put(cl, vl);
}
if (!vals.similar(colval)) {
vals.put(colval);
}
}
} else {
if (filter != null) {
resultSet = collection.find(filter).into(new ArrayList < Document > ());
} else {
resultSet = collection.find().into(new ArrayList < Document > ());
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
while (itr.hasNext()) {
String col = itr.next().toString();
if (!col.equals("_id")) {
cls.add(col);
TableHeader th = new TableHeader(col, col);
cols.add(th);
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
if (!vals.similar(colval)) {
vals.put(colval);
}
}

}

JSONArray jsonArrayFinal = new JSONArray();
JSONObject jsonObjectColVal = new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
close_mongo_connection(connection);

log.debug("************    zambia*************" + domain + "*     " + vendor + "* exit   getInterfaceTraffic ****************");
return jsonArrayFinal.toString();
} catch (Exception e) {
log.error("   Performance     Exception occurs:---" + domain + "------" + vendor + "---------" + e.getMessage(), e);
//ex.printStackTrace();
}
return null;
}

@Override
public int import_export_kpi(MultipartFile file, String data) {
try {
String opco = "";
String domain = "";
String vendor = "";
String admin_id = "";

String split[] = data.split(";");

opco = StringUtils.substringAfter(split[0], "=");

domain = StringUtils.substringAfter(split[1], "=");
vendor = StringUtils.substringAfter(split[2], "=");
admin_id = StringUtils.substringAfter(split[3], "=");
Properties config = getProperties();
Path rootLocation = Paths.get(config.getProperty("server.directory"));
String file_store = file.getOriginalFilename();
File file_check = new File(rootLocation + "/" + file_store);

if (file_check.exists()) {
Files.delete(rootLocation.resolve(file_store));
Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
MongoClient connection = get_mongo_connection();
MongoDatabase database = null;

database = database(connection, domain, vendor);

new kpi_import_export().start_process(opco, vendor, domain, admin_id, database, file_check.getAbsolutePath());

connection.close();
} else {
Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
MongoClient connection = get_mongo_connection();
MongoDatabase database = null;

database = database(connection, domain, vendor);

new kpi_import_export().start_process(opco, vendor, domain, admin_id, database, file_check.getAbsolutePath());

connection.close();
}

} catch (Exception e) {
log.error("   Performance     Exception occurs:-----------------" + e.getMessage(), e);
//ex.printStackTrace();
}
return 1;
}


//TODO
@Override
public ArrayList<String> get_distinct_list(String opco, String admin_id, String domain, String vendor, String type,String where1,String where2, String where3, String where4) {
	
	
	//////System.out.printlnout.printlnout.println(string_replace(where1));
//////System.out.printlnout.printlnout.println(string_replace(where2));
ArrayList<String> output=new ArrayList<String>();
MongoClient connection = get_mongo_connection();

MongoDatabase database = database(connection, domain, vendor);

if(type.equals("transmission_nce_elements")) {
	
String table=string_replace(where1)+"_dummy";	
String tofind="NEName";
output=distinct_1_where_with_hint(database, table.toLowerCase(), "ONEName_1", tofind, "EventName", string_replace(where2));
//
	
}

if(type.equals("nce_kpi_list")) {
	ArrayList<String> kpi=new ArrayList<String>();
String table=string_replace(where1)+"_dummy";	
String tofind="EventName";
kpi=distinct_1_where_with_hint_regex(database, table.toLowerCase(), "ONEName_1", tofind, "NEName", string_replace(where2));


MongoCollection < Document > collection = database.getCollection("filter_kpi_formula");
DistinctIterable<String>document = collection.distinct("formula", and(eq("admin_id", admin_id), eq("groups", string_replace(where1)), eq("link_with_dashboard", "yes")), String.class);

for(String val:document) {
	
if(kpi.contains(val)) {
	
	output.add(val);
}
	
	
}

//////System.out.printlnout.printlnout.println(output);
}





return output;
}

@Override
public int insert_create_multiple_graphs(String admin_id,String graph_name, String graph_count, String domain, String vendor,
		String elementname, String ip, String interfacee, ArrayList<String> kpi_list,String graph_type,String sla_threshold) {
	
	
	System.out.println("graph_type===="+graph_type);
	System.out.println("sla===="+sla_threshold);
	
MongoClient connection = get_mongo_connection();

String db_domain="all";
String db_vendor="topology";



MongoDatabase database = database(connection, db_domain, db_vendor);
	
	

System.out.println("graph_name====>"+graph_name);
System.out.println("graph_count====>"+graph_count);
System.out.println("domain====>"+domain);
System.out.println("vendor====>"+vendor);
System.out.println("elementname====>"+elementname);
System.out.println("ip====>"+ip);
System.out.println("interfacee====>"+interfacee);
System.out.println("kpi_list====>"+kpi_list);



ArrayList<String>columns=new ArrayList<String>();


columns.add("admin_id");
columns.add("graph_name");
columns.add("graph_count");
columns.add("domain");
columns.add("vendor");
columns.add("elementname");
columns.add("ip");
columns.add("interfacee");
columns.add("kpis");
columns.add("graph_type");
columns.add("sla");



for(String kpis:kpi_list) {
	
ArrayList<String>values=new ArrayList<String>();


values.add(admin_id);
	
values.add(graph_name);
values.add(graph_count);
values.add(domain);
values.add(vendor);
values.add(elementname);
values.add(ip);
values.add(interfacee);
values.add(kpis);
values.add(graph_type);
values.add(sla_threshold);

insert_mongodb(database, columns, values, "user_multiple_kpis");

}





connection.close();
	

	return 5;
}

@Override
public String get_multiple_graphs(String opco, String admin_id ,String group) {

	
	JSONArray array=new JSONArray();
	
    ArrayList < String > columns_toget= new ArrayList < String > ();
    columns_toget.add("domain");
    columns_toget.add("vendor");
    columns_toget.add("elementname");
    columns_toget.add("ip");
    columns_toget.add("interfacee");
    columns_toget.add("kpis");
    columns_toget.add("sla");
    columns_toget.add("graph_type");
    
    Map < String, Object > groupMap_interface = new HashMap < String, Object > ();

    for (String column: columns_toget) {
      groupMap_interface.put(column, "$" + column);
    }
    DBObject groupFields_interface = new BasicDBObject(groupMap_interface);
    
    


	MongoClient connection = get_mongo_connection();

	String db_domain="all";
	String db_vendor="topology";



MongoDatabase database = database(connection, db_domain, db_vendor);

MongoCollection <Document>collection=database.getCollection("user_multiple_kpis");

ArrayList<String> graphname=new ArrayList<String>();




/*
 * //getting distinct graphs DistinctIterable<String>
 * docs=collection.distinct("graph_name", String.class);
 * 
 * for(String name:docs) { graphname.add(name); }
 */

//getting distinct graphs

graphname.add(group);




//getting graph number
for(String name:graphname) {
	
	


	
	
	
	
	
	
DistinctIterable<String> docs_graph_number=collection.distinct("graph_count",and(eq("graph_name",name),eq("admin_id",admin_id)), String.class);





for(String count: docs_graph_number) {
	
	JSONObject object=new JSONObject();
	ArrayList<String> kpis=new ArrayList<String>();
	object.put("graph_name", name);
	
	
	
	object.put("graph_count", count);

	
	

ArrayList < Document > iterDo_interface = collection.aggregate(Arrays.asList(match(and(eq("graph_count",count),eq("graph_name",name),eq("admin_id",admin_id))), group(groupFields_interface))).into(new ArrayList < Document > ());

	
	
JSONArray jsonArray_vendor_domain = new JSONArray(JSON.serialize(iterDo_interface));
for (int i = 0; i < jsonArray_vendor_domain.length(); i++) {

  JSONObject jsonObject1 = jsonArray_vendor_domain.getJSONObject(i);
  JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
  
  object.put("domain", jsonObject_id.getString("domain"));
	object.put("vendor", jsonObject_id.getString("vendor"));
	object.put("elementname", jsonObject_id.getString("elementname"));
	object.put("ip", jsonObject_id.getString("ip"));
	object.put("interfacee", jsonObject_id.getString("interfacee"));
	object.put("sla", jsonObject_id.getString("sla"));
	object.put("graph_type", jsonObject_id.getString("graph_type"));
	
	
	kpis.add(jsonObject_id.getString("kpis"));

}
object.put("kpis", kpis);


array.put(object);
	
}




}





	
	return array.toString();
}

@Override
public ArrayList<String> get_any_list(String opco, String admin_id, String domain, String vendor, String type,
		String check1, String check2, String check3, String check4) {
	ArrayList<String>output=new ArrayList<String>();
	
	
	MongoClient connection = get_mongo_connection();
	
	
	if(type.equals("user_graphs")) {

	String db_domain="all";
	String db_vendor="topology";



	MongoDatabase database = database(connection, db_domain, db_vendor);
	
	
	MongoCollection <Document>collection =database.getCollection("user_multiple_kpis");
	
	
	DistinctIterable<String> distinct=collection.distinct("graph_name",eq("admin_id",admin_id), String.class);
	
	
	for(String value:distinct) {
		
		output.add(value);
		
	}
	
	
}
	connection.close();
	

	return output;
}

@Override
public dual_axis_1_date_format get_ip_graph(String opco, String admin_id, String vendor, String domain,
		ArrayList<String> kpi_name, ArrayList<String> ne_name, ArrayList<String> filter1, ArrayList<String> filter2,
		String duration, String start_date, String end_date, String starttime, String endtime, String apn,
		ArrayList<String> check_axis, String type, String graph_type,String sla) {
	
	
	System.out.println(filter1);
	System.out.println(filter2);


	ArrayList<String> color = new ArrayList<String>();
	color.add("#483D8B");
	color.add("#FF000D");
	color.add("#FFF533");
	color.add("#974858");
	color.add("#489781");
	color.add("#8C9748");
	color.add("#8C9748");
	color.add("#486597");
	color.add("#FFA07A");
	color.add("#FF8C00");
	color.add("#BA55D3");
	color.add("#333DFF");
	color.add("#4682B4");
	color.add("#BC8F8F");
	color.add("#800000");
	color.add("#708090");
	color.add("#D2B48C");
	color.add("#D2B48C");
	color.add("#00FF7F");
	color.add("#FF00FF");
	color.add("#663399");
	color.add("#DB7093");
	color.add("#FFA07A");
	color.add("#FF8C00");
	color.add("#008B8B");
	if (log.isDebugEnabled()) {
		log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "**************     "
				+ vendor + "****************** enter   get_ip_graph ****************");

	}

	String natural_trend = "no";

	StringBuilder title_append = new StringBuilder();

	for (String k : kpi_name) {
		title_append.append(string_replace(k) + " ,");
	}

	if (type.equals("complete_graph")) {
		String kpi = kpi_name.get(0);
		kpi_name.clear();

		String axis = check_axis.get(0);
		check_axis.clear();
		for (String a : ne_name) {
			kpi_name.add(kpi);
		}

		for (String a : ne_name) {
			check_axis.add(axis);
		}
	} else if (type.equals("for_sla")) {
		kpi_name.add("Threshold");
		check_axis.add("1");

		ne_name.add(ne_name.get(0));
		filter1.add(filter1.get(0));
		filter2.add(filter2.get(0));
	}

	MongoClient connection = get_mongo_connection();
	String StartTime = "", EndTime = "";

	MongoDatabase database = null;
	dual_axis_1a title = new dual_axis_1a(); // This is for title of graph.

	dual_axis_1a sub_title = new dual_axis_1a(); // This is for title of graph.

	dual_axis_2 zoomtype = new dual_axis_2(); // for zoomtype

//for zoom type
	zoomtype.setZoomType("xy");
	zoomtype.setWidth(300);
	zoomtype.setHeight(300);


	ArrayList<String> values_x = new ArrayList<String>(); // values for x-axis

	ArrayList<String> unique_dates = new ArrayList<String>(); // unique dates----valid only if the trend is natural

	int check_time;

	Properties config = getProperties();

	if (domain.equals("RADIO")) {
		database = database(connection, domain, vendor);

		if (duration.equals("15 Mins")) {

			natural_trend = "yes";
		}

	}

	if (domain.equals("TRANSMISSION") && vendor.equals("ERICSSON")) {
		database = database(connection, domain, vendor);

		natural_trend = "yes";

	} else if (domain.equals("TRANSMISSION") && vendor.equals("SAM")) {
		database = database(connection, domain, vendor);

		natural_trend = "yes";

	} else if (domain.equals("CORE") && vendor.equals("ZTE")) {
		database = database(connection, domain, vendor);
		if (duration.equals("15 Mins")) {

			natural_trend = "yes";
		}

	} else if (domain.equals("IPRAN")) {
		database = database(connection, domain, vendor);

		if (duration.equals("15 Mins")) {

			natural_trend = "yes";
		}

	} else if (domain.equals("IPBB")) {

		database = database(connection, domain, vendor);
		if (duration.equals("5 Mins")) {
			natural_trend = "yes";

		}

	} else {

	}

//for sla======
	if (domain.equals("IPBB") || domain.equals("IPRAN")) {

//for all interfaces
		if (string_replace(string_replace(filter2.get(0))).equals("All Interface")) {
			String kpi = string_replace(kpi_name.get(0));

			kpi_name.clear();

			String table_hint = mongo_generic(database, admin_id, kpi, "kpi_formula", "kpi_formula");

			String hint = to_get_table_name(table_hint);

//try {
//hint=StringUtils.substringBetween(table_hint, "(",":").toLowerCase();
//} catch (Exception e) {
//
//hint=StringUtils.substringBetween(table_hint, "(",":").toLowerCase();
//} 
//

//link option with command name  
			String command_name = "";

			if (vendor.equals("ERICSSON")) {
				if (hint.contains("ipbb")) {
					command_name = "snmp";
				} else {
					command_name = hint.replace("port_counter", "port counters detail").replace("dot1q_counter",
							"dot1q counters detail");
				}
			} else if (vendor.equals("ZTE")) {
				if (hint.contains("ipbb")) {
					command_name = "snmp";
				}

			} else if (vendor.equals("NOKIA")) {
				if (hint.contains("ipbb")) {
					command_name = "snmp";
				} else {
					command_name = hint.replace("stats", "show port statistics").replace("usage", "show system cpu")
							.replace("memory", "show system memory");
				}

			} else if (vendor.equals("HUAWEI")) {
				if (hint.contains("ipran")) {
					command_name = "snmp";
				}

			} else if (vendor.equals("JUNIPER")) {
				if (hint.contains("ipbb")) {
					command_name = "snmp";
				} else {
					command_name = hint.replace("stats", "interfaces statistics detail");
				}

			}

			String axis = check_axis.get(0);
			//check_axis.clear();

			String ip = filter1.get(0);
		//	filter1.clear();

			String elementname = string_replace(ne_name.get(0));
		//	ne_name.clear();

			String interface_name = string_replace(filter2.get(0));
		//	filter2.clear();

			if (type.equals("for_sla")) {

				ArrayList<String> initial_interface = new ArrayList<String>();

				MongoCollection<Document> collection = null;
				if (command_name.equals("vlan")) {
					collection = database.getCollection("vlan_element_command_structure");
				}

				else {
					collection = database.getCollection("element_command_structure");
				}

				DistinctIterable<String> values = collection.distinct("interface",
						and(eq("devicename", string_replace(elementname)), eq("command", command_name)),
						String.class);

				for (String value : values) {
					initial_interface.add(value);
				}

				/*
				 * MongoCollection<Document> collection1 = database.getCollection("sla_alerts");
				 * DistinctIterable<String> values1 = collection1.distinct("Interface",
				 * and(eq("Site_ID", string_replace(elementname)), eq("KPIName",
				 * string_replace(kpi))), String.class); for (String value1 : values1) {
				 * 
				 * if (initial_interface.contains(value1)) { kpi_name.add(kpi);
				 * check_axis.add(axis); filter1.add(ip); filter2.add(value1);
				 * ne_name.add(elementname); }
				 * 
				 * }
				 * 
				 * for (String value1 : values1) {
				 * 
				 * if (initial_interface.contains(value1)) { kpi_name.add(kpi);
				 * check_axis.add(axis); filter1.add(ip); filter2.add(value1);
				 * ne_name.add(elementname); }
				 * 
				 * }
				 */
				
				kpi_name.add("Threshold");
				check_axis.add("1");
				filter1.add(ip);
				filter2.add(filter2.get(0));
				ne_name.add(elementname);

			} else {

				MongoCollection<Document> collection = null;
				if (command_name.equals("vlan")) {
					collection = database.getCollection("vlan_element_command_structure");
				}

				else {
					collection = database.getCollection("element_command_structure");
				}
				DistinctIterable<String> document = collection.distinct("interface",
						and(eq("devicename", string_replace(elementname)), eq("command", command_name)),
						String.class);

				for (String docs : document) {

					kpi_name.add(kpi);
					check_axis.add(axis);
					filter1.add(ip);
					filter2.add(docs);
					ne_name.add(elementname);
				}

			}

		}

	} else if (domain.equals("TRANSMISSION") && vendor.equals("ERICSSON")) {
//for all interfaces
		if (string_replace(string_replace(filter1.get(0))).equals("All")) {
			String kpi = "";

			ArrayList<String> initial_kpis = new ArrayList<String>();
			ArrayList<String> initial_axis = new ArrayList<String>();

			for (String kp : kpi_name) {
				initial_kpis.add(kp);
			}

			for (String ax : check_axis) {
				initial_axis.add(ax);
			}

			kpi = string_replace(kpi_name.get(0));

			kpi_name.clear();

			String axis = check_axis.get(0);
			check_axis.clear();

			String elementname = string_replace(ne_name.get(0));
			ne_name.clear();

			String interface_name = string_replace(filter1.get(0));
			filter1.clear();
			filter2.clear();
			if (type.equals("for_sla")) {

//MongoCollection < Document > collection = database.getCollection("objects");

//DistinctIterable < String > values = collection.distinct("object", and(eq("element_name", string_replace(elementname))), String.class);

//for (String value: values) {
//initial_interface.add(value);
//}   

				MongoCollection<Document> collection1 = database.getCollection("sla_alerts");

				DistinctIterable<String> values1 = collection1.distinct("MEASOBJLDN",
						and(eq("SITE_ID", string_replace(elementname)), eq("KPI_NAME", string_replace(kpi))),
						String.class);

				for (String value1 : values1) {

					kpi_name.add(kpi);
					check_axis.add(axis);

					filter1.add(value1);
					ne_name.add(elementname);

					filter2.add("-");

				}
				kpi_name.add("Threshold");
				check_axis.add("1");

				filter1.add(filter1.get(0));
				filter2.add(filter2.get(0));
				ne_name.add(elementname);

			} else {
				MongoCollection<Document> collection = database.getCollection(duration.toLowerCase());
				DistinctIterable<String> document = collection.distinct("MEASOBJLDN",
						and(eq("ELEMENT_NAME", string_replace(elementname))), String.class);

				int c = 0;
				for (String kpii : initial_kpis) {
					for (String docs : document) {
						kpi_name.add(kpii);
						check_axis.add(initial_axis.get(c));
						ne_name.add(elementname);
						filter1.add(docs);
						filter2.add("-");
//ne_name.add(elementname);
					}
					c++;
				}
			}

		}

	} else if (domain.equals("TRANSMISSION") && vendor.equals("SAM")) {
		if (string_replace(string_replace(filter2.get(0))).equals("All monitoredObjectPointer")) {

			if (type.equals("for_sla")) {

				String kpi = string_replace(kpi_name.get(0));

				kpi_name.clear();

				String table_hint = mongo_generic(database, admin_id, kpi, "kpi_formula", "kpi_formula");

				String axis = check_axis.get(0);

				check_axis.clear();

				String ip = filter1.get(0);
				filter1.clear();

				String elementname = string_replace(ne_name.get(0));

				ne_name.clear();

				String interface_name = string_replace(filter2.get(0));

				filter2.clear();

				String hint = "";

				try {
					hint = StringUtils.substringBetween(table_hint, "(", ":").toLowerCase();
				} catch (Exception e) {
					log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
							+ e.getMessage(), e);
				}

				MongoCollection<Document> collection = database.getCollection("sla_alerts");
				DistinctIterable<String> document = collection.distinct("monitoredObjectPointer",
						and(eq("monitoredObjectSiteId", string_replace(elementname))), String.class);

				for (String docs : document) {

					kpi_name.add(kpi);
					check_axis.add(axis);
					filter1.add(ip);
					filter2.add(docs);
					ne_name.add(elementname);
				}
				kpi_name.add("Threshold");
				filter2.add(filter2.get(0));
				check_axis.add(check_axis.get(0));
				filter1.add(filter1.get(0));
				ne_name.add(ne_name.get(0));

			} else {

				ArrayList<String> initial_kpis = new ArrayList<String>();
				ArrayList<String> initial_axis = new ArrayList<String>();

				for (String kp : kpi_name) {
					initial_kpis.add(kp);
				}

				for (String ax : check_axis) {
					initial_axis.add(ax);
				}

				String kpi = string_replace(kpi_name.get(0));

				kpi_name.clear();
				String table_hint = mongo_generic(database, admin_id, kpi, "kpi_formula", "kpi_formula");

				String axis = check_axis.get(0);
				check_axis.clear();

				String ip = filter1.get(0);
				filter1.clear();

				String elementname = string_replace(ne_name.get(0));
				ne_name.clear();

				String interface_name = string_replace(filter2.get(0));
				filter2.clear();

				String hint = "";

				try {
					hint = StringUtils.substringBetween(table_hint, "(", ":").toLowerCase();
				} catch (Exception e) {

					log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
							+ e.getMessage(), e);
				}

				MongoCollection<Document> collection = database.getCollection(hint);
				DistinctIterable<String> document = collection.distinct("displayedName",
						and(eq("monitoredObjectSiteId", string_replace(elementname))), String.class);

				int c = 0;
				for (String kpii : initial_kpis) {
					for (String docs : document) {
						kpi_name.add(kpii);
						check_axis.add(initial_axis.get(c));
						ne_name.add(elementname);
						filter1.add(ip);
						filter2.add(docs);
//ne_name.add(elementname);
					}
					c++;
				}

			}

		}
	}

	SimpleDateFormat formatter = new SimpleDateFormat(config.getProperty("performance.dateformat1"));
	try {

		Date date = formatter.parse(start_date);

		start_date = new SimpleDateFormat(config.getProperty("performance.dateformat3")).format(date);

	} catch (Exception e) {
		log.error(
				"Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(),
				e);
	}

	try {
		Date date = formatter.parse(end_date);

		end_date = new SimpleDateFormat(config.getProperty("performance.dateformat3")).format(date);

	} catch (Exception e) {
		log.error(
				"Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(),
				e);

	}

//checking time ie whether time is for full day or some limited value
	String interval = "";
	if (starttime.contains(":") && endtime.contains(":")) {

		StartTime = starttime;
		EndTime = endtime;

		if (natural_trend.equals("no")) {

			if (duration.equals("15 Mins")) {

				interval = "15";
			} else if (duration.equals("Hourly")) {

				interval = "60";
			} else if (duration.equals("Day")) {

				interval = "Day";
			}

		}
		check_time = 1;

	} else {

		if (natural_trend.equals("no")) {

			if (duration.equals("15 Mins")) {
				StartTime = "00:00:00";
				EndTime = "23:45:00";
				interval = "15";
			} else if (duration.equals("Hourly")) {
				StartTime = "00:00:00";
				EndTime = "00:00:00";
				interval = "60";
			} else if (duration.equals("Day")) {
				StartTime = "00:00:00";
				EndTime = "00:00:00";
				interval = "Day";
			}
		}
		check_time = 0;
	}

	ArrayList<dual_axis_7_color> series = new ArrayList<dual_axis_7_color>();
//----check whether kpi contains apn or not---------

//ArrayList<String> time = new ArrayList<>();

	dual_axis_3_time_update xaxis = null;
//naming for yaxis
	ArrayList<dual_axis_5> yaxis = null;

	if (check_time == 0) {

	} else if (check_time == 1) {

	}

	try {

		if (natural_trend.equals("no")) {
			values_x = new date_time_relation().date_time_relations(database, start_date, end_date, StartTime,
					EndTime, interval);

			int tick_interval = values_x.size() / 12;
			date_format_graph format = new date_format_graph("{value:%Y-%b-%e %H:%M:%S}");
//dateTimeLabelFormats format1 =new dateTimeLabelFormats("%H:%M:%S.%L", "%H:%M:%S", "%H:%M", "%H:%M:%S", "24:00", "%e. %b", "%b \'%y", "%Y");
			xaxis = new dual_axis_3_time_update(values_x, true, "category", tick_interval, format, 3);
		} else {
			unique_dates = mongo_get_name(database, start_date, end_date);

		}

	} catch (Exception e) {
		log.error(
				"Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(),
				e);

	}

	if (!type.equals("for_sla") && domain.equals("IPBB") && kpi_name.size() == 1) {

		String ne_single_name = string_replace(ne_name.get(0));

		String filter1_name = string_replace(filter1.get(0));

		String filter2_name = string_replace(filter2.get(0));

		MongoDatabase database1 = database(connection, "report", "report");
		MongoCollection<Document> collection = database1.getCollection("interface_details");
		String output = "";
		DistinctIterable<String> values = collection.distinct("ifDescr",
				and(eq("ipaddress", filter1_name), eq("ifName", string_replace(filter2_name))), String.class);

		for (String inter : values) {
			output = inter;
		}

		if (output.trim().length() > 1) {
			sub_title.setText(output);
		} else {
			sub_title.setText("");
		}
//System.out.println(output);
	} else {
		sub_title.setText("");
	}

	for (int i = 0; i < kpi_name.size(); i++) {
		String kpi = string_replace(kpi_name.get(i));
		if (kpi.equals("Threshold")) {
			String threshold = sla;
			kpi = string_replace(kpi_name.get(0));
			String titlee = string_replace(kpi_name.get(0)) + "  Vs  Threshold (" + threshold + ")";
			title.setText(titlee);
			String ne_single_name = "";
			if (domain.equals("TRANSMISSION") && vendor.equals("ERICSSON")) {
				ne_single_name = string_replace(ne_name.get(i));
// final_table_hint=duration.toLowerCase();
			} else {
				ne_single_name = string_replace(ne_name.get(i));
			}

			String filter1_name = string_replace(filter1.get(i));

			String filter2_name = string_replace(filter2.get(i));

			String label = "";

//if only 1st filter contains value
			if (filter1_name.length() > 1 && filter2_name.length() <= 1) {
				label = "Threshold";
			} else if (filter1_name.length() > 1 && filter2_name.length() > 1) {
				label = "Threshold";
			} else {
				label = "Threshold";
			}

			ArrayList<Double> dummy = new ArrayList<Double>();

			ArrayList<Double> data1 = new ArrayList<Double>();
			try {

				String calculation_type = mongo_generic(database, admin_id, kpi, "calculation_type", "kpi_formula");

				if (natural_trend.equals("no")) {
					dummy = new ipran_ipbb_graph_values().graph_value(domain, database, kpi, ne_single_name,
							filter1_name, filter2_name, values_x, interval, calculation_type, "");

					for (double d : dummy) {

						data1.add(Double.parseDouble(threshold));
					}
				} else {

					ArrayList<String> values_natural_trend = null;

					if (vendor.equals("ZTE") && domain.equals("CORE")) {
//values_natural_trend = new ericsson_radio_graph_values().graph_value_natural_trend(domain, database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval, calculation_type, StartTime, EndTime, check_time);

					} else if (vendor.equals("ERICSSON") && domain.equals("TRANSMISSION")) {

						String hint = duration;
						values_natural_trend = new ericsson_radio_graph_values().graph_value_natural_trend(domain,
								database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval,
								calculation_type, StartTime, EndTime, check_time, hint);

					} else if (vendor.equals("SAM") && domain.equals("TRANSMISSION")) {
						String table_hint = mongo_generic(database, admin_id, kpi, "kpi_formula", "kpi_formula");
						values_natural_trend = new sam_microwave_graph_values().graph_value_natural_trend(domain,
								database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval,
								calculation_type, StartTime, EndTime, check_time, table_hint);

					} else {
						String table_hint = mongo_generic(database, admin_id, kpi, "kpi_formula", "kpi_formula");

						String final_table_hint = ipbb_table_hint(vendor, table_hint);

						values_natural_trend = new ipran_ipbb_graph_values().graph_value_natural_trend(domain,
								database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval,
								calculation_type, StartTime, EndTime, check_time, final_table_hint);

					}

					for (String values : values_natural_trend) {
						String split1[] = values.split("@AND@");
						values_x.add(split1[0]);
						data1.add(Double.parseDouble(threshold));
					}
					int tick_interval = values_natural_trend.size() / 12;
					date_format_graph format = new date_format_graph("{value:%Y-%b-%e %H:%M:%S}");
//dateTimeLabelFormats format1 =new dateTimeLabelFormats("%H:%M:%S.%L", "%H:%M:%S", "%H:%M", "%H:%M:%S", "24:00", "%e. %b", "%b \'%y", "%Y");
					xaxis = new dual_axis_3_time_update(values_x, true, "category", tick_interval, format, 3);
				}

				dual_axis_8 valuesuffix1 = new dual_axis_8("");

				dual_axis_7_color series_item1 = new dual_axis_7_color(label, "line",
						Integer.parseInt(check_axis.get(i)), data1, valuesuffix1, color.get(i)); // for series value
																									// to represent
																									// line
				series.add(series_item1);

			} catch (Exception e) {
				log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
						+ e.getMessage(), e);

			}

		} else {

			title.setText(StringUtils.chop(title_append.toString()));
			ArrayList<String> gitter_kpis = new ArrayList<String>();
			gitter_kpis.add("Packet Loss(%)");
			gitter_kpis.add("Minimum Delay(ms)");
			gitter_kpis.add("Average Delay(ms)");
			gitter_kpis.add("Maximum Delay(ms)");
			gitter_kpis.add("Jitter(mdev) ms");
			String table_hint = "";
			String final_table_hint = "";
			if (gitter_kpis.contains(kpi)) {
				table_hint = "_ping";
				final_table_hint = table_hint;
			}

			else {
				table_hint = mongo_generic(database, admin_id, kpi, "kpi_formula", "kpi_formula");
				final_table_hint = ipbb_table_hint(vendor, table_hint);
			}

//System.out.println(final_table_hint);
			String ne_single_name = "";
			if (domain.equals("TRANSMISSION") && vendor.equals("ERICSSON")) {
				ne_single_name = string_replace(ne_name.get(i));
				final_table_hint = duration.toLowerCase();

			} else {

				ne_single_name = string_replace(ne_name.get(i));
			}

			String filter1_name = string_replace(filter1.get(i));

			String filter2_name = string_replace(filter2.get(i));

			String label = "";

//if only 1st filter contains value
			if (filter1_name.length() > 1 && filter2_name.length() <= 1) {

				if (domain.equals("TRANSMISSION")) {
					MongoDatabase database_connectivity = database(connection, "all", "topology");
					if (vendor.equals("ERICSSON")) {
						String el_name = StringUtils.substringAfter(ne_single_name, "_");
						String lan_wan = filter1_name.replace("LAN ", "LAN-").replace("WAN ", "WAN-").replace("IF=",
								"");

						String facing_side = connectivity_where_with_hint_regex(database_connectivity, "index_1",
								"name", "vendor", "Ericsson", "locSysName", el_name, "locSysPort", lan_wan);

//////////System.out.printlnout.println("facing_side==="+facing_side);

						label = kpi + "       " + ne_single_name + "       " + filter1_name + "<=====>"
								+ facing_side;
					}

					else {
						label = kpi + "    " + ne_single_name + "       " + filter1_name;
					}

				}

				else {
					label = kpi + "    " + ne_single_name + "       " + filter1_name;
				}
			} else if (filter1_name.length() > 1 && filter2_name.length() > 1) {

				if (domain.equals("TRANSMISSION")) {

					MongoDatabase database_connectivity = database(connection, "all", "topology");
					if (vendor.equals("SAM")) {
						String facing_side = connectivity_where_with_hint_regex(database_connectivity, "index_1",
								"userLabel", "vendor", "Nokia", "siteId", ne_single_name, "terminatedObjectName",
								filter2_name);

//////////System.out.printlnout.println("facing_side==="+facing_side);

						label = kpi + "       " + filter1_name + "       " + filter2_name + "<=====>" + facing_side;
					}

					else {

						label = kpi + "    " + ne_single_name + "       " + filter1_name + "       " + filter2_name;
					}

				}

				else {
					label = kpi + "    " + ne_single_name + "       " + filter1_name + "       " + filter2_name;
				}

			} else {

				label = kpi + "    " + ne_single_name;

			}

			ArrayList<Double> data1 = new ArrayList<Double>();

			try {

				String calculation_type = mongo_generic(database, admin_id, kpi, "calculation_type", "kpi_formula");

				if (natural_trend.equals("no")) {

					if (domain.equals("CORE") && vendor.equals("ZTE")) {
						data1 = new ericsson_radio_graph_values().graph_value(domain, database, kpi, ne_single_name,
								filter1_name, filter2_name, values_x, interval, calculation_type);
					} else if (domain.equals("RADIO")) {
						data1 = new ericsson_radio_graph_values().graph_value(domain, database, kpi, ne_single_name,
								filter1_name, filter2_name, values_x, interval, calculation_type);
					} else {

						data1 = new ipran_ipbb_graph_values().graph_value(domain, database, kpi, ne_single_name,
								filter1_name, filter2_name, values_x, interval, calculation_type, final_table_hint);
					}
				} else {
					ArrayList<String> values_natural_trend = null;

					if (domain.equals("CORE") && vendor.equals("ZTE")) {
//values_natural_trend = new ericsson_radio_graph_values().graph_value_natural_trend(domain, database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval, calculation_type, StartTime, EndTime, check_time);
					} else if (vendor.equals("SAM") && domain.equals("TRANSMISSION")) {
						values_natural_trend = new sam_microwave_graph_values().graph_value_natural_trend(domain,
								database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval,
								calculation_type, StartTime, EndTime, check_time, table_hint);

					} else if (domain.equals("RADIO") || domain.equals("TRANSMISSION")) {

						values_natural_trend = new ericsson_radio_graph_values().graph_value_natural_trend(domain,
								database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval,
								calculation_type, StartTime, EndTime, check_time, final_table_hint);
					} else {

						values_natural_trend = new ipran_ipbb_graph_values().graph_value_natural_trend(domain,
								database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval,
								calculation_type, StartTime, EndTime, check_time, final_table_hint);

					}
					ArrayList<String> x_values = new ArrayList<>();

					for (String values : values_natural_trend) {
						String split1[] = values.split("@AND@");
						x_values.add(split1[0].substring(0, split1[0].length() - 3));

						data1.add(Double.parseDouble(split1[1]));

					}
					String maximum_value = "";
					BigDecimal result_maximum = new BigDecimal("" + peak_value(data1));
					maximum_value = "" + result_maximum.longValue();

					String minimum_value = "";
					BigDecimal result_minimum = new BigDecimal("" + minimum_value(data1));
					minimum_value = "" + result_minimum.longValue();

					String average_value = "";
					DecimalFormat f = new DecimalFormat("##.00");
					average_value = f.format(calculateAverage(data1));

					String show_values = "( Min:" + minimum_value + " , Max:" + maximum_value + " , Avg:"
							+ average_value + ")";
//////System.out.println(show_values);
					if (domain.equals("IPBB") | domain.equals("IPRAN")) {
						label = label + " " + show_values;
					}
					int tick_interval = values_natural_trend.size() / 12;
					date_format_graph format = new date_format_graph("{value:%Y-%b-%e %H:%M:%S}");
//dateTimeLabelFormats format1 =new dateTimeLabelFormats("%H:%M:%S.%L", "%H:%M:%S", "%H:%M", "%H:%M:%S", "24:00", "%e. %b", "%b \'%y", "%Y");
					xaxis = new dual_axis_3_time_update(x_values, true, "category", tick_interval, format, 3);

				}

				dual_axis_8 valuesuffix1 = new dual_axis_8("");
				String final_graph_type = graph_type.toLowerCase().replace("horizontal bar", "bar")
						.replace("vertical bar", "column").toLowerCase();
				dual_axis_7_color series_item1 = new dual_axis_7_color(label, final_graph_type.toLowerCase(),
						Integer.parseInt(check_axis.get(i)), data1, valuesuffix1, color.get(i)); // for series value
																									// to represent
																									// line
				series.add(series_item1);

			} catch (Exception e) {
				log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
						+ e.getMessage(), e);

			}

		}

	}

	dual_axis_6 tooltip = new dual_axis_6(true); // for tooltip

//naming for yaxis
	yaxis = new ArrayList<dual_axis_5>();

//name for left axis under y axis.
	String s_axis_name = "";
	String p_axis_name = "";
	if (check_axis.contains("0")) {

		String kpi = string_replace(kpi_name.get(check_axis.indexOf("0")));

		if (domain.equals("TRANSMISSION")) {
			s_axis_name = mongo_generic(database, admin_id, kpi, "unit", "kpi_formula");
		} else {
			s_axis_name = "Secondary Axis";

		}

	} else {
		s_axis_name = "";
	}

	if (check_axis.contains("1")) {

		String kpi = string_replace(kpi_name.get(check_axis.indexOf("1")));

		if (domain.equals("TRANSMISSION")) {
			p_axis_name = mongo_generic(database, admin_id, kpi, "unit", "kpi_formula");
		} else {
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

	exporting exporting = new exporting(true, false, "line-chart");

	dual_axis_1_date_format main_json = new dual_axis_1_date_format(zoomtype, title, sub_title, xaxis, yaxis,
			tooltip, series, exporting); // This is the main class where all the classes are join together to create
											// a single json.

	try {

	} catch (Exception e) {
		log.error(
				"Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(),
				e);

	}

	log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "**************     " + vendor
			+ "****************** exit   get_ip_graph ****************");

	close_mongo_connection(connection);
	return main_json;


}

@Override
public String cause_graph(String protocol) {
	
	

	
	
		MongoClient connection = get_mongo_connection();
		
		ArrayList<String>causes=new ArrayList<String>();
		ArrayList<Integer>values=new ArrayList<Integer>();
		JSONArray connectivity_type = new JSONArray();
		try {
		if(!protocol.equals("others")) {
			

		
		MongoDatabase database = null;
		
		database = database_5g(connection, protocol);
		
		
		System.out.println(database.getName());
		
		
		
System.out.println(protocol+"_all_cause");
		
		

		
		MongoCollection<Document> collection_cause_all = database.getCollection(protocol+"_all_cause");
		
		
		
		  String fieldName = "Cause";
  
		
		DistinctIterable<String> distinct_causes=collection_cause_all.distinct(fieldName, String.class);
		
		for(String cause:distinct_causes) {
  
			System.out.println(cause);
			 


		    long count = getCount(collection_cause_all, fieldName, cause);
		    System.out.println(cause +"     " +count);
			causes.add(cause);
			values.add(Integer.parseInt(""+count));

		   
		}
		
  
		}
		
		else {
 
		
		
		
		
		causes.add("Protocol error, unspecified");
		causes.add("Onboarding services terminated");
		causes.add("DNN not supported or not subscribed in the slice");
		causes.add("UAS services not allowed");
		causes.add("Invalid PTI value");
		causes.add("PDU session type IPv6 only allowed");
		causes.add("Network failure");
		causes.add("Operator determined barring");
		causes.add("Insufficient resources");
		causes.add("Network failure");
		causes.add("Not authorized for this CSG");
		causes.add("UE security capabilities mismatch");
		causes.add("MAC failure");
		causes.add("CS domain not available");
		causes.add("PLMN not allowed");
		

		values.add(890);
		values.add(800);
		values.add(700);
		values.add(673);
		values.add(621);
		values.add(537);
		values.add(510);
		values.add(420);
		values.add(400);
		values.add(397);
		values.add(320);
		values.add(280);
		values.add(210);
		values.add(100);
		values.add(90);
		
		
		}
		for(int i=0;i<causes.size();i++) {
			JSONObject object=new JSONObject();
			
			object.put("name", causes.get(i));
			object.put("y", values.get(i));
			
			object.put("x", i*2);
			
			connectivity_type.put(object);
		}
		
		
		
		
		

		
	
		
		
		connection.close();
		
	} catch (NumberFormatException e) {

		e.printStackTrace();
	} catch (JSONException e) {
	
		e.printStackTrace();
	}
	String output=connectivity_type.toString();
	return output;
}


private static long getCount(MongoCollection<Document> collection, String fieldName, String searchValue) {
    Document matchStage = new Document("$match", new Document(fieldName, searchValue));
    Document groupStage = new Document("$group", new Document("_id", null).append("count", new Document("$sum", 1)));

    // Create the aggregation pipeline
    collection.aggregate(Arrays.asList(matchStage, groupStage));

    // Retrieve the result of the aggregation
    Document result = collection.aggregate(Arrays.asList(matchStage, groupStage)).first();

    // Get the count value from the result
    return result != null ? result.getInteger("count", 0) : 0;
}

@Override
public ArrayList<String> causes() {
	ArrayList<String>causes=new ArrayList<String>();
	causes.add("s1ap");
	causes.add("ngap");
	causes.add("htt2");
	causes.add("others");

	
	return causes;
}

@Override
public String cause_right_click(String protocol) {
	
	
protocol="ngap";	
	
	
	MongoClient connection = get_mongo_connection();
	MongoDatabase database = database_5g(connection, protocol);
	
	BasicDBObject index = null;
	index = new BasicDBObject("$hint", "_id_");
	
	Map<String, Object> groupMap_node = new HashMap<String, Object>();
	groupMap_node.put("Main", "$Main");
	groupMap_node.put("SubTree", "$SubTree");
	groupMap_node.put("Collection_availability", "$Collection_availability");

	DBObject groupFields_node = new BasicDBObject(groupMap_node);
	
	MongoCollection<Document> collection_cause_all = database.getCollection(protocol.toUpperCase()+"_filters_parent_child");

	ArrayList<Document> iterDo_node = collection_cause_all
			.aggregate(Arrays.asList( group(groupFields_node))).hint(index)
			.into(new ArrayList<Document>());
	JSONArray jsonArray_node = new JSONArray(JSON.serialize(iterDo_node));
	
	ArrayList<String>parent=new ArrayList<String>();
	ArrayList<String>child=new ArrayList<String>();
	ArrayList<String>type=new ArrayList<String>();


	for (int i = 0; i < jsonArray_node.length(); i++) {
	
		JSONObject jsonObject1 = jsonArray_node.getJSONObject(i);
		JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");

		String parent_element = jsonObject_id.optString("Main");
		String child_element = jsonObject_id.optString("SubTree");
		String type_element = jsonObject_id.optString("Collection_availability");
		
		parent.add(parent_element);
		child.add(child_element);
		
		if(type_element.equals("Y")) {
			type.add("filed");
		}
		
		else {
			type.add("nofiled");
		}
		
		
	}
	
	


	
	
	
	JSONArray right_click = new JSONArray();
	
	/*

	parent.add("chand");
	parent.add("chand");
	parent.add("chand");
	parent.add("harsh");
	parent.add("harsh");
	parent.add("harsh");
	parent.add("kunal");
	parent.add("kunal");
	
	
	child.add("veer1");
	child.add("veer2");
	child.add("veer3");
	child.add("bali1");
	child.add("bali2");
	child.add("bali3");
	child.add("sharma1");
	child.add("sharma2");
	

	

	type.add("field");
	type.add("nofield");
	type.add("field");
	type.add("nofield");
	type.add("field");
	type.add("nofield");
	type.add("nofield");
	type.add("field");

	*/
	
	
	for(int i=0;i<parent.size();i++) {
		JSONObject object=new JSONObject();
		
		object.put("parent", parent.get(i));
		object.put("child", child.get(i));
		object.put("type", type.get(i));

		
		right_click.put(object);
	}
	
	
	
	
	

	
	String output=right_click.toString();
	
	
	connection.close();
	return output;


	

}

@Override
public String click_on_graph(String protocol, String barname, String item_clicked) {

	
	

	
	
	MongoClient connection = get_mongo_connection();
	
	ArrayList<String>causes=new ArrayList<String>();
	ArrayList<Integer>values=new ArrayList<Integer>();
	JSONArray connectivity_type = new JSONArray();
	try {
	if(!protocol.equals("others")) {
		

	
	MongoDatabase database = null;
	
	database = database=connection.getDatabase(protocol+"_db");;
	
	
	System.out.println(database.getName());
	
	
	

	
	
	System.out.println("Extract_"+item_clicked+"_cause");
	
	MongoCollection<Document> collection_cause_all = database.getCollection("Extract_"+item_clicked+"_cause");
	
	
	
	  String fieldName = item_clicked;

	
	    DistinctIterable<String> distinct_causes=collection_cause_all.distinct(fieldName,eq("Cause",barname), String.class);
	
	for(String cause:distinct_causes) {

		System.out.println(cause);
		 


	    long count = getCount_with_cause(collection_cause_all, fieldName, cause,barname);
	    System.out.println(cause +"     " +count);
		causes.add(cause);
		values.add(Integer.parseInt(""+count));

	   
	}
	

	}
	
	else {

	
	
	
	
	causes.add("Protocol error, unspecified");
	causes.add("Onboarding services terminated");
	causes.add("DNN not supported or not subscribed in the slice");
	causes.add("UAS services not allowed");
	causes.add("Invalid PTI value");
	causes.add("PDU session type IPv6 only allowed");
	causes.add("Network failure");
	causes.add("Operator determined barring");
	causes.add("Insufficient resources");
	causes.add("Network failure");
	causes.add("Not authorized for this CSG");
	causes.add("UE security capabilities mismatch");
	causes.add("MAC failure");
	causes.add("CS domain not available");
	causes.add("PLMN not allowed");
	

	values.add(890);
	values.add(800);
	values.add(700);
	values.add(673);
	values.add(621);
	values.add(537);
	values.add(510);
	values.add(420);
	values.add(400);
	values.add(397);
	values.add(320);
	values.add(280);
	values.add(210);
	values.add(100);
	values.add(90);
	
	
	}
	for(int i=0;i<causes.size();i++) {
		JSONObject object=new JSONObject();
		
		object.put("name", causes.get(i));
		object.put("y", values.get(i));
		
		object.put("x", i*2);
		
		connectivity_type.put(object);
	}
	
	
	
	
	

	

	
	
	connection.close();
	
} catch (NumberFormatException e) {

	e.printStackTrace();
} catch (JSONException e) {

	e.printStackTrace();
}
String output=connectivity_type.toString();
return output;

}


private static long getCount_with_cause(MongoCollection<Document> collection, String fieldName, String searchValue,String cause) {
	 
	 Document doc=new Document();
	 doc.append(fieldName, searchValue);
	 doc.append("Cause", cause);
	 
       Document matchStage = new Document("$match", doc);
       Document groupStage = new Document("$group", new Document("_id", null).append("count", new Document("$sum", 1)));

       // Create the aggregation pipeline
       collection.aggregate(Arrays.asList(matchStage, groupStage));

       // Retrieve the result of the aggregation
       Document result = collection.aggregate(Arrays.asList(matchStage, groupStage)).first();

       // Get the count value from the result
       return result != null ? result.getInteger("count", 0) : 0;
   }

@Override
public String test(String check1) {
	
	
	String a="check";
	
	return a;
}

}