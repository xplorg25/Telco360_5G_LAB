package org.xplorg.tool.telco360.DAO.implementation;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.ne;
import static com.mongodb.client.model.Filters.not;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.ascending;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

public class KMLFileUpdateVendors implements Runnable{

Logger log = LogManager.getLogger(KMLFileUpdateVendors.class.getName());
	
MongoDatabase database;	
public KMLFileUpdateVendors(MongoDatabase database) {
this.database=database;
}
	
public void run() {
try {
if (log.isDebugEnabled()) {
log.debug("*************** checked into run of KMLFileUpdateVendors ****************");
}	
String timezone="GMT+2";	
SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
sdf.setTimeZone(TimeZone.getTimeZone(timezone));
String date=sdf.format(new Date());
Date datCurrent = sdf.parse(date);
Calendar calDateCurrent = Calendar.getInstance();
calDateCurrent.setTime(datCurrent);
calDateCurrent.add(Calendar.DAY_OF_YEAR, -30);
String date_from = sdf.format(calDateCurrent.getTime());
calDateCurrent.add(Calendar.DAY_OF_YEAR, 31);
String date_to = sdf.format(calDateCurrent.getTime());

ArrayList<String>siteid=new ArrayList<String>();
ArrayList<String>sitename=new ArrayList<String>();
ArrayList<String>vendor=new ArrayList<String>();
{
String tableName="huawei_ipran_alarms";
String columns="HWNMNORTHBOUNDNENAME";
String conditions="INSERTIONTIME between FROM="+date_from+" TO="+date_to+" and HWNMNORTHBOUNDFAULTFLAG=FAULT or HWNMNORTHBOUNDFAULTFLAG=RECOVERY and HWNMNORTHBOUNDNETYPE not like(ATN) or HWNMNORTHBOUNDNETYPE not like(CX) or HWNMNORTHBOUNDNETYPE not like(OSS) or HWNMNORTHBOUNDNETYPE not like(NE)";	
String orderby="HWNMNORTHBOUNDNENAME";

JSONArray data=getTableData(tableName, columns, conditions, orderby);

for(int i=0;i<data.length();i++) {
JSONObject object=(JSONObject) data.get(i);
String name=object.get("HWNMNORTHBOUNDNENAME").toString();
if(name.toUpperCase().contains("ZM")) {
String nam=name.replace("ZM-ZM","ZM").replace("ZM_ZM", "ZM").replace("ZM_", "ZM").replace("ZM ","ZM").replace("ZM-","ZM").trim();	
String sid=nam.substring(nam.indexOf("ZM"),nam.indexOf("ZM")+6).trim();
String nameFinal=sid;
String vndr="Huawei";
sitename.add(name);
siteid.add(nameFinal);
vendor.add(vndr);
}
else {
sitename.add(name);
siteid.add(name);
vendor.add("Huawei");
}
}
}

{
String tableName="ericsson_radio_alarms";
String columns="ELEMENT_NAME";
String conditions="INSERTIONTIME between FROM="+date_from+" TO="+date_to+" and PERCEIVED_SEVERITY not like(CLEARED) or PERCEIVED_SEVERITY not like(CLEARED)";	
String orderby="ELEMENT_NAME";

JSONArray data=getTableData(tableName, columns, conditions, orderby);

for(int i=0;i<data.length();i++) {
JSONObject object=(JSONObject) data.get(i);
String name=object.get("ELEMENT_NAME").toString();
if(name.toUpperCase().contains("ZM")) {
String nam=name.replace("ZM-ZM","ZM").replace("ZM_ZM", "ZM").replace("ZM_", "ZM").replace("ZM ","ZM").replace("ZM-","ZM").trim();	
String sid=nam.substring(nam.indexOf("ZM"),nam.indexOf("ZM")+6).trim();
String nameFinal=sid;
String vndr="Ericsson";
sitename.add(name);
siteid.add(nameFinal);
vendor.add(vndr);
}
else {
sitename.add(name);
siteid.add(name);
vendor.add("Ericsson");
}
}
}

{
String tableName="sam_microwave_alarms";
String columns="NODENAME";
String conditions="INSERTIONTIME between FROM="+date_from+" TO="+date_to+" and SEVERITY not like(CLEARED) or SEVERITY not like(CLEARED)";	
String orderby="NODENAME";

JSONArray data=getTableData(tableName, columns, conditions, orderby);

for(int i=0;i<data.length();i++) {
JSONObject object=(JSONObject) data.get(i);
String name=object.get("NODENAME").toString();
if(name.toUpperCase().contains("ZM")) {
String nam=name.replace("ZM-ZM","ZM").replace("ZM_ZM", "ZM").replace("ZM_", "ZM").replace("ZM ","ZM").replace("ZM-","ZM").trim();	
String sid=nam.substring(nam.indexOf("ZM"),nam.indexOf("ZM")+6).trim();
String nameFinal=sid;
String vndr="Nokia";
sitename.add(name);
siteid.add(nameFinal);
vendor.add(vndr);
}
else {
sitename.add(name);
siteid.add(name);
vendor.add("Nokia");
}
}
}

{
String tableName="nec_microwave_alarms";
String columns="NENAME";
String conditions="INSERTIONTIME between FROM="+date_from+" TO="+date_to+" and SEVERITY not like(CLEARED) or SEVERITY not like(CLEARED)";	
String orderby="NENAME";

JSONArray data=getTableData(tableName, columns, conditions, orderby);

for(int i=0;i<data.length();i++) {
JSONObject object=(JSONObject) data.get(i);
String name=object.get("NENAME").toString();
if(name.toUpperCase().contains("ZM")) {
String nam=name.replace("ZM-ZM","ZM").replace("ZM_ZM", "ZM").replace("ZM_", "ZM").replace("ZM ","ZM").replace("ZM-","ZM").trim();	
String sid=nam.substring(nam.indexOf("ZM"),nam.indexOf("ZM")+6).trim();
String nameFinal=sid;
String vndr="Nec";
sitename.add(name);
siteid.add(nameFinal);
vendor.add(vndr);
}
else {
sitename.add(name);
siteid.add(name);
vendor.add("Nec");
}
}
}


ArrayList<String>finalSiteId=new ArrayList<String>();
ArrayList<String>finalVendor=new ArrayList<String>();

for(int i=0;i<siteid.size();i++) {
if(finalSiteId.indexOf(siteid.get(i))>-1) {
int idx=finalSiteId.indexOf(siteid.get(i));
if(!finalVendor.get(idx).contains(vendor.get(i))){
finalVendor.set(idx,finalVendor.get(idx)+"&"+vendor.get(i));
}
}
else {
finalSiteId.add(siteid.get(i));
finalVendor.add(vendor.get(i));
}
}

MongoCollection<Document> collection1 = database.getCollection("microwave_optical_transmission");

for(int i=0;i<finalSiteId.size();i++) {
collection1.updateMany(and(eq("siteid",finalSiteId.get(i))),Updates.set("vendor", finalVendor.get(i)));	
}

}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();	
}
}
	

public JSONArray getTableData(String tableName,String columns,String conditions,String orderby) {
try {
ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(conditions.length()>1) {
String cond=conditions.replace("@DOT@", ".").replace("@SEMICOLON@", ";");	
if(cond.contains(" and ")){
String cond_spls[]=cond.split(" and ");
for(String cv:cond_spls) {	
if(cv.contains("=") && !cv.contains("like") && !cv.contains("between") && !cv.contains(" or ")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'","");
fltr.add(eq(col,val));
}
else if(!cv.contains("=") && cv.contains("like") && !cv.contains("not like") && !cv.contains("between") && !cv.contains(" or ")) {
String col=cv.substring(0,cv.indexOf("like")).trim();
String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
if(!val.toUpperCase().contains("MANAGEDELEMENT")) {
fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*")));
}
else {
fltr.add(eq(col,java.util.regex.Pattern.compile("^"+val+".*")));	
}
}
else if(!cv.contains("=") && cv.contains("not like") && !cv.contains("between") && cv.contains(" or ")) {
String or[]=cv.split(" or ");
ArrayList<Bson> fltr_or=new ArrayList<Bson>();
for(String splt_or:or) {
String col=splt_or.substring(0,splt_or.indexOf("not like")).trim();
String val=splt_or.substring(splt_or.indexOf("(")+1,splt_or.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
if(!val.toUpperCase().contains("MANAGEDELEMENT")) {
fltr_or.add(not(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*"))));
}
else {
fltr_or.add(not(eq(col,java.util.regex.Pattern.compile("^"+val+".*"))));	
}
}
fltr.add(or(fltr_or));
}
else if(!cv.contains("=") && cv.contains("like") && !cv.contains("not like") && !cv.contains("between") && cv.contains(" or ")) {
String or[]=cv.split(" or ");
ArrayList<Bson> fltr_or=new ArrayList<Bson>();
for(String splt_or:or) {
String col=splt_or.substring(0,splt_or.indexOf("like")).trim();
String val=splt_or.substring(splt_or.indexOf("(")+1,splt_or.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
if(!val.toUpperCase().contains("MANAGEDELEMENT")) {
fltr_or.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*")));
}
else {
fltr_or.add(eq(col,java.util.regex.Pattern.compile("^"+val+".*")));	
}
}
fltr.add(or(fltr_or));
}
else if(cv.contains("=") && cv.contains("between")) {
String col=cv.substring(0,cv.indexOf("between")).trim();
String val1=cv.substring(cv.indexOf("FROM=")+5,cv.indexOf("TO=")).replace("'","").trim();
String val2=cv.substring(cv.indexOf("TO=")+3).replace("'","").trim();	
fltr.add(gte(col,val1));
fltr.add(lte(col,val2));
}
else if(cv.contains("=") && cv.contains(" or ") && !cv.contains("like") && !cv.contains("between")) {
String or[]=cv.split(" or ");
ArrayList<Bson> fltr_or=new ArrayList<Bson>();
for(String splt_or:or) {
String col=splt_or.substring(0,splt_or.indexOf("=")).trim();
String val=splt_or.substring(splt_or.indexOf("=")+1).trim().replace("'","");
fltr_or.add(eq(col,val));
}
fltr.add(or(fltr_or));
}
}
}
else if(cond.contains(" AND ")){
String cond_spls[]=cond.split(" AND ");
for(String cv:cond_spls) {
if(cv.contains("=") && !cv.contains("like") && !cv.contains("between")  && !cv.contains(" OR ")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'", "");
fltr.add(eq(col,val));
}
else if(!cv.contains("=") && cv.contains("like") && !cv.contains("not like") && !cv.contains("between")  && !cv.contains(" OR ")) {
String col=cv.substring(0,cv.indexOf("like")).trim();
String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
if(!val.toUpperCase().contains("MANAGEDELEMENT")) {
fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*")));
}
else {
fltr.add(eq(col,java.util.regex.Pattern.compile("^"+val+".*")));	
}
}
else if(!cv.contains("=") && cv.contains("not like") && !cv.contains("between")  && !cv.contains(" OR ")) {
String col=cv.substring(0,cv.indexOf("not like")).trim();
String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
if(!val.toUpperCase().contains("MANAGEDELEMENT")) {
fltr.add(ne(col,java.util.regex.Pattern.compile("^.*"+val+".*")));
}
else {
fltr.add(ne(col,java.util.regex.Pattern.compile("^"+val+".*")));	
}
}
else if(cv.contains("=") && cv.contains("between")) {
String col=cv.substring(0,cv.indexOf("between")).trim();
String val1=cv.substring(cv.indexOf("FROM=")+5,cv.indexOf("TO=")).replace("'","").trim();
String val2=cv.substring(cv.indexOf("TO=")+3).replace("'","").trim();	
fltr.add(gte(col,val1));
fltr.add(lte(col,val2));
}
else if(cv.contains("=") && cv.contains(" OR ") && !cv.contains("like") && !cv.contains("between")) {
String or[]=cv.split(" OR ");
ArrayList<Bson> fltr_or=new ArrayList<Bson>();
for(String splt_or:or) {
String col=splt_or.substring(0,splt_or.indexOf("=")).trim();
String val=splt_or.substring(splt_or.indexOf("=")+1).trim().replace("'","");
fltr_or.add(eq(col,val));
}
fltr.add(or(fltr_or));
}
}
}
else if(!(cond.contains(" AND ") && !cond.contains(" and ")) && cond.length()>1){
if(cond.contains("=") && cond.contains("between") && !cond.contains(" or ") && !cond.contains(" OR ")) {
String col=cond.substring(0,cond.indexOf("between")).trim();
String val1=cond.substring(cond.indexOf("FROM=")+5,cond.indexOf("TO=")).replace("'","").trim();
String val2=cond.substring(cond.indexOf("TO=")+3).replace("'","").trim();	
fltr.add(gte(col,val1));
fltr.add(lte(col,val2));
}
else if(cond.contains(" or ") && !cond.contains("like") && !cond.contains("between")) {
String or[]=cond.split(" or ");
ArrayList<Bson> fltr_or=new ArrayList<Bson>();
for(String splt_or:or) {
String col=splt_or.substring(0,splt_or.indexOf("=")).trim();
String val=splt_or.substring(splt_or.indexOf("=")+1).trim().replace("'","");
fltr_or.add(eq(col,val));
}
fltr.add(or(fltr_or));
}
else if(cond.contains(" OR ") && !cond.contains("like") && !cond.contains("between")) {
String or[]=cond.split(" OR ");
ArrayList<Bson> fltr_or=new ArrayList<Bson>();
for(String splt_or:or) {
String col=splt_or.substring(0,splt_or.indexOf("=")).trim();
String val=splt_or.substring(splt_or.indexOf("=")+1).trim().replace("'","");
fltr_or.add(eq(col,val));
}
fltr.add(or(fltr_or));
}
else if(cond.contains("=")) {
String col=cond.substring(0,cond.indexOf("=")).trim();
String val=cond.substring(cond.indexOf("=")+1).trim().replace("'", "");
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
System.out.println("fltr===="+filter);
MongoClient mongo=new MongoClient("localhost",27017);
MongoDatabase database = mongo.getDatabase("nokia_project_telco360_topology");
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<String>cls=new ArrayList<String>();
ArrayList<Document> resultSet = null;
JSONArray vals=new JSONArray();

Map<String, Object> groupMap = new HashMap<String, Object>();

if(!columns.equals("*")) {
if(columns.contains(",")) {
String columns_spls[]=columns.split(",");
for(String colm:columns_spls) {
cls.add(colm);	
groupMap.put(colm, "$"+colm);
}
}
else {
cls.add(columns);	
groupMap.put(columns, "$"+columns);	
}

DBObject groupFields = new BasicDBObject(groupMap);
if(filter!=null) {
resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))),sort(ascending("_id."+orderby)))).allowDiskUse(true).into(new ArrayList<Document>());//,limit(limitHistoryAlarms)
}
else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))),sort(ascending("_id."+orderby)))).allowDiskUse(true).into(new ArrayList<Document>());//,limit(limitHistoryAlarms)
}  

for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
String object=docs.get("_id").toString();
String substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}"));
String spls_cols[]=substr.split(",");
for(String cv:spls_cols) {
String cl=cv.substring(0,cv.indexOf("=")).trim();
String vl=cv.substring(cv.indexOf("=")+1).trim().replace("\n"," ");//.replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","")
colval.put(cl, vl);	
}
if(!vals.similar(colval)) {
vals.put(colval);	
}	
}
}
else {
if(filter!=null) {
resultSet = collection.find(filter).into(new ArrayList<Document>());
}
else {
resultSet = collection.find().into(new ArrayList<Document>());
}
if(resultSet.size()>0) {
for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
for(int j=0;j<cls.size();j++) {
if(docs.containsKey(cls.get(j))) {	
if(docs.get(cls.get(j)).toString().length()>0 && docs.get(cls.get(j)).toString()!=null) {
colval.put(cls.get(j), docs.get(cls.get(j)).toString().replace("\n"," "));	
}
else {
colval.put(cls.get(j),"-");	
}
}
else {
colval.put(cls.get(j),"-");	
}
}
if(!vals.similar(colval)) {
vals.put(colval);	
}	
}
}
}

mongo.close();

return vals;	
}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return null;
}
	
}
