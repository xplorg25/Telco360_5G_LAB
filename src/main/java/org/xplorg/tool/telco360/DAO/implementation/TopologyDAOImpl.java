package org.xplorg.tool.telco360.DAO.implementation;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.descending;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.xplorg.tool.telco360.DAO.interfaces.AnalysisDAO;
import org.xplorg.tool.telco360.DAO.interfaces.TopologyDAO;
import org.xplorg.tool.telco360.config.BaseDAOMongo;
import org.xplorg.tool.telco360.config.WebSocketService;
import org.xplorg.tool.telco360.entity.TableHeader;
import org.xplorg.tool.telco360.entity.topology_gis_getter_setter;
import org.xplorg.tool.telco360.entity.treeMid;
import org.xplorg.tool.telco360.entity.treeParent;
import org.xplorg.tool.telco360.restController.AlarmsWebSocketThreads;
import org.xplorg.tool.telco360.restController.IslWebSocketThreads;
import org.xplorg.tool.telco360.restController.PerformanceWebSocketThreads;
import org.xplorg.tool.telco360.restController.ssh_thread;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

@Repository("topologyDAO")

public class TopologyDAOImpl extends BaseDAOMongo implements TopologyDAO{
@Autowired
private AnalysisDAO analysisDAO;
	
Logger log = LogManager.getLogger(TopologyDAOImpl.class.getName());

public ArrayList<String>getElementData(String element,int element_id,String Column1) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into getElementData ****************");	
}	
//Connection conn=getConnection();
ArrayList<String>list=new ArrayList<String>();
try{
//Statement stmnt=conn.createStatement();
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(element);
ArrayList<Document> resultSet = collection.find(and(eq(element+"_id",""+element_id),eq("Column1",Column1))).into(new ArrayList<Document>());
ArrayList<String>cls=new ArrayList<String>();
if(resultSet.size()>0) {	
for(Document doc:resultSet) {
Iterator<String> itr = doc.keySet().iterator();
while(itr.hasNext()){
String col=itr.next().toString();
if(!col.equals("_id")) {
cls.add(col);
}
}	
for(int j=0;j<cls.size();j++) {
if(doc.get(cls.get(j)).toString().length()>0) {
list.add(""+doc.get(cls.get(j)));	
}
else {
list.add("-");	
}
}
}
}
else {
for(Document doc:resultSet) {
Iterator<String> itr = doc.keySet().iterator();
while(itr.hasNext()){
String col=itr.next().toString();
if(!col.equals("_id")) {
cls.add(col);
}
}
}
for(int i=1;i<=cls.size();i++) {
list.add("0");	
}	
}
closeConnection(mongo);
}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}

return list;

}

@Override
public ArrayList<topology_gis_getter_setter> getGisRegionDetails(String tableName) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into getGisRegionDetails ****************");	
}

ArrayList<topology_gis_getter_setter>list=new ArrayList<topology_gis_getter_setter>();

try{
	
String columns="REGION_NAME,LATITUDE,LONGITUDE";	
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<String>cls=new ArrayList<String>();
Map<String, Object> groupMap = new HashMap<String, Object>();
String columns_spls[]=columns.split(",");
for(String colm:columns_spls) {
cls.add(colm);	
groupMap.put(colm, "$"+colm);
}

DBObject groupFields = new BasicDBObject(groupMap);

ArrayList<Document> resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))))).into(new ArrayList<Document>());
for(Document docs:resultSet) {
String object=docs.get("_id").toString();
String substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}"));
String spls_cols[]=substr.split(",");
String regionName="",latitude="",longitude="";
for(String cv:spls_cols) {
String cl=cv.substring(0,cv.indexOf("=")).trim();
String vl=cv.substring(cv.indexOf("=")+1).trim();
if(cl.equals("REGION_NAME")) {
regionName=vl;	
}
if(cl.equals("LATITUDE")) {
latitude=vl;	
}
if(cl.equals("LONGITUDE")) {
longitude=vl;	
}
list.add(new topology_gis_getter_setter(regionName,latitude,longitude));
//}
}
}	
closeConnection(mongo);

}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
	
return list;
}


@Override
public ArrayList<topology_gis_getter_setter> getGisElementDetails(String elementName) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into getGisElementDetails ****************");	
}

String tableName="";
if(elementName.equals("BSC")) {
tableName="sitedb_2g_zm";	
}
if(elementName.equals("RNC")) {
tableName="sitedb_3g_zm";	
}
if(elementName.equals("ENODEB")) {
tableName="sitedb_lte_zm";	
}
ArrayList<topology_gis_getter_setter>list=new ArrayList<topology_gis_getter_setter>();
try{
	
String columns="SITE_ID,OSS_CELL_ID_NAME,VENDOR,REGION_NAME,LATITUDE,LONGITUDE";	
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<String>cls=new ArrayList<String>();
Map<String, Object> groupMap = new HashMap<String, Object>();
String columns_spls[]=columns.split(",");
for(String colm:columns_spls) {
cls.add(colm);	
groupMap.put(colm, "$"+colm);
}

DBObject groupFields = new BasicDBObject(groupMap);

ArrayList<Document> resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))))).into(new ArrayList<Document>());
for(Document docs:resultSet) {
String object=docs.get("_id").toString();	
String substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}"));
String spls_cols[]=substr.split(",");
String SITE_ID="",OSS_CELL_ID_NAME="",VENDOR="",REGION_NAME="",LATITUDE="",LONGITUDE="";
for(String cv:spls_cols) {
String cl=cv.substring(0,cv.indexOf("=")).trim();
String vl=cv.substring(cv.indexOf("=")+1).trim();
if(cl.equals("SITE_ID")) {
SITE_ID=vl;	
}
if(cl.equals("OSS_CELL_ID_NAME")) {
OSS_CELL_ID_NAME=vl;	
}
if(cl.equals("VENDOR")) {
VENDOR=vl;	
}
if(cl.equals("REGION_NAME")) {
REGION_NAME=vl;	
}
if(cl.equals("LATITUDE")) {
LATITUDE=vl;	
}
if(cl.equals("LONGITUDE")) {
LONGITUDE=vl;	
}
}
for(int j=0;j<cls.size();j++) {
list.add(new topology_gis_getter_setter(SITE_ID,OSS_CELL_ID_NAME,VENDOR,REGION_NAME,LATITUDE,LONGITUDE));
}
}	
closeConnection(mongo);

}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
	
return list;
}

public String getAlarmTableData(String tableName,String conditions) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getAlarmTableData ****************");
}
String columns="";

if(conditions.trim().length()==1 && tableName.equals("ericsson_radio_alarms")) {
columns="ALARM_ID,PERCEIVED_SEVERITY,EVENT_DATE,EVENT_TIME,ELEMENT_NAME,SPECIFIC_PROBLEM,ADDITIONAL_TEXT";	
}

if(conditions.trim().length()>1 && tableName.equals("ericsson_radio_alarms")) {
columns="ALARM_ID,PERCEIVED_SEVERITY,EVENT_DATE,EVENT_TIME,ELEMENT_NAME,SPECIFIC_PROBLEM,ADDITIONAL_TEXT";	
}

if(conditions.trim().length()==1 && tableName.equals("nokia_radio_alarms")) {
columns="ALARM_ID,PERCEIVED_SEVERITY,EVENT_DATE,EVENT_TIME,ELEMENT_NAME,ADDITIONAL_TEXT,PROBABLE_CAUSE";	
}

if(conditions.trim().length()>1 && tableName.equals("nokia_radio_alarms")) {
columns="ALARM_ID,PERCEIVED_SEVERITY,EVENT_DATE,EVENT_TIME,ELEMENT_NAME,ADDITIONAL_TEXT,PROBABLE_CAUSE";	
}

try {
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);

ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(conditions.length()>1) {
String cond=conditions;	
if(cond.contains(" and ")){
String cond_spls[]=cond.split(" and ");
for(String cv:cond_spls) {
if(!cv.contains("=") && cv.contains("like")) {
String col=cv.substring(0,cv.indexOf("like")).trim();
String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*",Pattern.CASE_INSENSITIVE)));
}
if(cv.contains("=") && !cv.contains("like")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).replace("'","").trim();	
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(cond.contains(" AND ")){
String cond_spls[]=cond.split(" AND ");
for(String cv:cond_spls) {
if(!cv.contains("=") && cv.contains("like")) {
String col=cv.substring(0,cv.indexOf("like")).trim();
String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();
fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*",Pattern.CASE_INSENSITIVE)));
}
if(cv.contains("=") && !cv.contains("like")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).replace("'","").trim();	
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(!(cond.contains(" AND ") && cond.contains(" and ")) && cond.length()>1){
if(cond.contains("=")) {
String col=cond.substring(0,cond.indexOf("=")).trim();
String val=cond.substring(cond.indexOf("=")+1).trim().replace("'", "");
fltr.add(eq(col,val));
}
filter=and(fltr);
}
}

ArrayList<String>cls=new ArrayList<String>();
Map<String, Object> groupMap = new HashMap<String, Object>();
String columns_spls[]=columns.split(",");
for(String colm:columns_spls) {
cls.add(colm);	
groupMap.put(colm, "$"+colm);
}

DBObject groupFields = new BasicDBObject(groupMap);
ArrayList<Document> resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))),sort(descending("_id.EVENT_TIME")))).into(new ArrayList<Document>());
ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
JSONArray vals=new JSONArray();

String cols_spls[]=columns.split(",");
for(String col:cols_spls) {
TableHeader th = new TableHeader(col, col);
cols.add(th);
}  

for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
String object=docs.get("_id").toString();
String substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}"));
String spls_cols[]=substr.split(",");
for(String cv:spls_cols) {
String cl=cv.substring(0,cv.indexOf("=")).trim();
String vl=cv.substring(cv.indexOf("=")+1).trim().replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","");
colval.put(cl, vl);	
}
if(!vals.similar(colval)) {
vals.put(colval);	
}	
}

JSONArray jsonArrayFinal=new JSONArray();
JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
	
closeConnection(mongo);
return jsonArrayFinal.toString();	
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}

return null;
}


public int postUserSubscription(String data,WebSocketService webSocketService) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into postUserSubscription ****************");
}
try {
	System.out.println(userIds);
System.out.println("data----->"+data);	
String userid=data.substring(data.indexOf("userid=")+7,data.indexOf(";"));
String subscription=data.substring(data.indexOf(";subscription=")+14,data.lastIndexOf(";"));

if(subscription.equalsIgnoreCase("FM")) {

int idx=userIds.indexOf(userid);
if(idx>-1) {
if(userSubscription.get(idx).equalsIgnoreCase("-")) {
userSubscription.set(idx, "FM");
AlarmsWebSocketThreads task=new AlarmsWebSocketThreads(webSocketService, userIds.get(idx));
Thread thrd=new Thread(task);
thrd.start();
}
else {
if(!userSubscription.get(idx).contains("FM")) {	
userSubscription.set(idx, userSubscription.get(idx)+"&&FM");	
AlarmsWebSocketThreads task=new AlarmsWebSocketThreads(webSocketService, userIds.get(idx));
Thread thrd=new Thread(task);
thrd.start();
}
}
}
}

if(subscription.equalsIgnoreCase("PM")) {
int idx=userIds.indexOf(userid);
if(idx>-1) {
if(userSubscription.get(idx).equalsIgnoreCase("-")) {	
userSubscription.set(idx, "PM");
PerformanceWebSocketThreads task=new PerformanceWebSocketThreads(webSocketService, userIds.get(idx));
Thread thrd=new Thread(task);
thrd.start();
}
else {
if(!userSubscription.get(idx).contains("PM")) {	
userSubscription.set(idx, userSubscription.get(idx)+"&&PM");	
PerformanceWebSocketThreads task=new PerformanceWebSocketThreads(webSocketService, userIds.get(idx));
Thread thrd=new Thread(task);
thrd.start();
}
}
}
}

/*if(subscription.equalsIgnoreCase("CM")) {
int idx=userIds.indexOf(userid);
if(idx>-1) {
if(userSubscription.get(idx).equalsIgnoreCase("-")) {	
userSubscription.set(idx, "CM");
}
else {
if(!userSubscription.get(idx).contains("CM")) {	
userSubscription.set(idx, userSubscription.get(idx)+"&&CM");	
}
}
}
}*/

if(subscription.equalsIgnoreCase("ISL")) {
int idx=userIds.indexOf(userid);
if(idx>-1) {
if(userSubscription.get(idx).equalsIgnoreCase("-")) {	
userSubscription.set(idx, "ISL");
IslWebSocketThreads task=new IslWebSocketThreads(webSocketService, userIds.get(idx));
Thread thrd=new Thread(task);
thrd.start();
}
else {
if(!userSubscription.get(idx).contains("ISL")) {	
userSubscription.set(idx, userSubscription.get(idx)+"&&ISL");	
IslWebSocketThreads task=new IslWebSocketThreads(webSocketService, userIds.get(idx));
Thread thrd=new Thread(task);
thrd.start();
}
}
}
}


if(subscription.equalsIgnoreCase("SSH")) {
int idx=userIds.indexOf(userid);
if(idx>-1) {
if(userSshSubscription.get(idx).equalsIgnoreCase("-")) {	
	userSshSubscription.set(idx, "SSH");
ssh_thread task=new ssh_thread(webSocketService, userIds.get(idx));
Thread thrd=new Thread(task);
thrd.start();
}

}
}

return 1;
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}	
return 0;
}


public int postUserDeSubscription(String data) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into postUserDeSubscription ****************");
}
try {
String userid=data.substring(data.indexOf("userid=")+7,data.indexOf(";"));
String desubscription=data.substring(data.indexOf(";desubscription=")+16,data.lastIndexOf(";"));
System.out.println(userid+"<------->"+desubscription);

if(desubscription.equalsIgnoreCase("SSH")) {
int idx=userIds.indexOf(userid);
if(idx>-1) {
if(userSshSubscription.get(idx).equalsIgnoreCase("SSH")) {
	userSshSubscription.set(idx, "-");
}

}
}


else if(desubscription.equalsIgnoreCase("FM")) {
int idx=userIds.indexOf(userid);
if(idx>-1) {
if(userSubscription.get(idx).equalsIgnoreCase("FM")) {
userSubscription.set(idx, "-");
}
else {
if(userSubscription.get(idx).contains("&&FM")) {	
userSubscription.set(idx, userSubscription.get(idx).replace("&&FM","-").replace("--","-"));	
}
}
}
}

if(desubscription.equalsIgnoreCase("PM")) {
int idx=userIds.indexOf(userid);
if(idx>-1) {
if(userSubscription.get(idx).equalsIgnoreCase("PM")) {
userSubscription.set(idx, "-");
}
else {
if(userSubscription.get(idx).contains("&&PM")) {	
userSubscription.set(idx, userSubscription.get(idx).replace("&&PM","-").replace("--","-"));	
}
}
}
}

if(desubscription.equalsIgnoreCase("ISL")) {
int idx=userIds.indexOf(userid);
if(idx>-1) {
if(userSubscription.get(idx).equalsIgnoreCase("ISL")) {
userSubscription.set(idx, "-");
}
else {
if(userSubscription.get(idx).contains("&&ISL")) {	
userSubscription.set(idx, userSubscription.get(idx).replace("&&ISL","-").replace("--","-"));	
}
}
}
}

return 1;
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}	
return 0;
}


public String getAlarmHistoryTableData(String tableName,String conditions,String orderby) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getAlarmHistoryTableData ****************");
}
try {
ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(conditions.length()>1) {
String cond=conditions;	
if(cond.contains(" and ")){
String cond_spls[]=cond.split(" and ");
for(String cv:cond_spls) {
if(!cv.contains("=") && cv.contains("like")) {
String col=cv.substring(0,cv.indexOf("like")).trim();
String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*",Pattern.CASE_INSENSITIVE)));
}
if(cv.contains("=") && !cv.contains("like")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).replace("'","").trim();	
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(cond.contains(" AND ")){
String cond_spls[]=cond.split(" AND ");
for(String cv:cond_spls) {
if(!cv.contains("=") && cv.contains("like")) {
String col=cv.substring(0,cv.indexOf("like")).trim();
String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();
fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*",Pattern.CASE_INSENSITIVE)));
}
if(cv.contains("=") && !cv.contains("like")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).replace("'","").trim();	
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(!(cond.contains(" AND ") && cond.contains(" and ")) && cond.length()>1){
if(cond.contains("=")) {
String col=cond.substring(0,cond.indexOf("=")).trim();
String val=cond.substring(cond.indexOf("=")+1).trim().replace("'", "");
fltr.add(eq(col,val));
}
filter=and(fltr);
}
}
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<Document> resultSet =null;
if(filter!=null) {
resultSet = collection.find(filter).sort(descending("_id."+orderby)).limit(200).into(new ArrayList<Document>());
}
else {
resultSet = collection.find().into(new ArrayList<Document>());
}

int size=0;
int max=0;
Document doc=null;
for(int i=0;i<resultSet.size();i++) {
Document document=resultSet.get(i);
size=document.keySet().size();
if(size>max) {
max=size;
doc=document;
}
}
ArrayList<String>cls=new ArrayList<String>();
ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
JSONArray vals=new JSONArray();
Iterator<String> itr = doc.keySet().iterator();
while(itr.hasNext()){
String col=itr.next().toString();
if(!col.equals("_id")) {
cls.add(col);
TableHeader th=new TableHeader(col, col);
cols.add(th);
}
}
for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
for(int j=0;j<cls.size();j++) {
if(docs.containsKey(cls.get(j))) {	
if(docs.get(cls.get(j)).toString().length()>0 && docs.get(cls.get(j)).toString()!=null) {
colval.put(cls.get(j), docs.get(cls.get(j)));	
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

JSONArray jsonArrayFinal=new JSONArray();
JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
closeConnection(mongo);
return jsonArrayFinal.toString();

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return null;

}


public String getAlarmListAndSiteName(String tableName) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getAlarmListAndSiteName ****************");
}

String columns="";

if(tableName.toUpperCase().equals("NOKIA_RADIO_ALARMS")) {
columns="ALARM_ID,PERCEIVED_SEVERITY,EVENT_DATE,EVENT_TIME,ELEMENT_NAME,ADDITIONAL_TEXT";	
}
if(tableName.toUpperCase().equals("ERICSSON_RADIO_ALARMS")) {
columns="ALARM_ID,PERCEIVED_SEVERITY,EVENT_TIME,ELEMENT_NAME,SPECIFIC_PROBLEM,MANAGED_OBJECT_INSTANCE";	
}

try {
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<String>cls=new ArrayList<String>();
Map<String, Object> groupMap = new HashMap<String, Object>();
String columns_spls[]=columns.split(",");
for(String colm:columns_spls) {
cls.add(colm);	
groupMap.put(colm, "$"+colm);
}

DBObject groupFields = new BasicDBObject(groupMap);

ArrayList<Document> resultSet = collection.aggregate(Arrays.asList(match(and(eq("PERCEIVED_SEVERITY","CRITICAL"))),group(groupFields),project(fields(include(cls))),sort(descending("_id.EVENT_TIME")))).into(new ArrayList<Document>());
ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
JSONArray vals=new JSONArray();

String cols_spls[]=columns.split(",");
for(String col:cols_spls) {
TableHeader th = new TableHeader(col, col);
cols.add(th);
}  

for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
for(int j=0;j<cls.size();j++) {
if(docs.containsKey(cls.get(j))) {	
if(docs.get(cls.get(j)).toString().length()>0 && docs.get(cls.get(j)).toString()!=null) {
colval.put(cls.get(j), docs.get(cls.get(j)));	
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
JSONArray jsonArrayFinal=new JSONArray();
JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
	
closeConnection(mongo);

return jsonArrayFinal.toString();	
	
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}

return null;

}

public String getAlarmTableDataZte(String tableName,String columns,String conditions,String orderby) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getAlarmTableDataZte ****************");
}

try {	
ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(conditions.length()>1) {
String cond=conditions;	
if(cond.contains(" and ")){
String cond_spls[]=cond.split(" and ");
for(String cv:cond_spls) {	
if(cv.contains("=") && !cv.contains("like") && !cv.contains("between")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'","");
fltr.add(eq(col,val));
}
else if(!cv.contains("=") && cv.contains("like") && !cv.contains("between")) {
String col=cv.substring(0,cv.indexOf("like")).trim();
String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*",Pattern.CASE_INSENSITIVE)));
}
else if(cv.contains("=") && cv.contains("between")) {
String col=cv.substring(0,cv.indexOf("between")).trim();
String val1=cv.substring(cv.indexOf("FROM=")+5,cv.indexOf("TO=")).replace("'","").trim();
String val2=cv.substring(cv.indexOf("TO=")+3).replace("'","").trim();	
fltr.add(gte(col,val1));
fltr.add(lte(col,val2));
}
}
//filter=and(fltr);
}
else if(cond.contains(" AND ")){
String cond_spls[]=cond.split(" AND ");
for(String cv:cond_spls) {
if(cv.contains("=") && !cv.contains("like") && !cv.contains("between")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'", "");
fltr.add(eq(col,val));
}
else if(!cv.contains("=") && cv.contains("like") && !cv.contains("between")) {
String col=cv.substring(0,cv.indexOf("like")).trim();
String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*",Pattern.CASE_INSENSITIVE)));
}
else if(cv.contains("=") && cv.contains("between")) {
String col=cv.substring(0,cv.indexOf("between")).trim();
String val1=cv.substring(cv.indexOf("FROM=")+5,cv.indexOf("TO=")).replace("'","").trim();
String val2=cv.substring(cv.indexOf("TO=")+3).replace("'","").trim();	
fltr.add(gte(col,val1));
fltr.add(lte(col,val2));
}
}
//filter=and(fltr);
}
else if(!(cond.contains(" AND ") && !cond.contains(" and ")) && cond.length()>1){
if(cond.contains("=") && cond.contains("between")) {
String col=cond.substring(0,cond.indexOf("between")).trim();
String val1=cond.substring(cond.indexOf("FROM=")+5,cond.indexOf("TO=")).replace("'","").trim();
String val2=cond.substring(cond.indexOf("TO=")+3).replace("'","").trim();	
fltr.add(gte(col,val1));
fltr.add(lte(col,val2));
}
else if(cond.contains("=")) {
String col=cond.substring(0,cond.indexOf("=")).trim();
String val=cond.substring(cond.indexOf("=")+1).trim().replace("'", "");
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
JSONArray vals=new JSONArray();
Map<String, Object> groupMap = new HashMap<String, Object>();
ArrayList<String>cls=new ArrayList<String>();

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

ArrayList<Document> resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))),sort(descending("_id."+orderby)),limit(100000))).into(new ArrayList<Document>());
String cols_spls[]=columns.split(",");
for(String col:cols_spls) {
String colm=col.replace("AID","ALARM_ID").replace("ALARMACK","ACK_STATE")
.replace("ALARMADDITIONALTEXT","ADDITIONAL_TEXT").replace("ALARMEVENTDATE","EVENT_DATE").replace("ALARMEVENTTIME","EVENT_TIME")
.replace("ALARMEVENTTYPE","EVENT_TYPE").replace("ALARMMANAGEDOBJECTINSTANCE","MANAGED_ELEMENT")
.replace("ALARMMANAGEDOBJECTINSTANCENAME","MANAGED_OBJECT_CLASS").replace("ALARMMOCOBJECTINSTANCE","ELEMENT_NAME")
.replace("ALARMNETYPE","ELEMENT_TYPE").replace("ALARMPERCEIVEDSEVERITY","PERCEIVED_SEVERITY").replace("ALARMPROBABLECAUSE","PROBABLE_CAUSE")
.replace("ALARMSPECIFICPROBLEM","SPECIFIC_PROBLEM").replace("SNMPTRAPOID","NOTIFICATION_TYPE")
.replace("HWNMNORTHBOUNDADDITIONALINFO","ADDITIONAL_TEXT").replace("HWNMNORTHBOUNDEVENTDATE","EVENT_DATE")
.replace("HWNMNORTHBOUNDEVENTDETAIL","SPCIFIC_PROBLEM").replace("HWNMNORTHBOUNDEVENTNAME","EVENT_NAME")
.replace("HWNMNORTHBOUNDEVENTTIME","EVENT_TIME").replace("HWNMNORTHBOUNDEVENTTYPE","EVENT_TYPE").replace("HWNMNORTHBOUNDFAULTID","ALARM_ID")
.replace("HWNMNORTHBOUNDMAINTAINSTATUS","ACK_STATE").replace("HWNMNORTHBOUNDNENAME","ELEMENT_NAME").replace("HWNMNORTHBOUNDNETYPE","ELEMENT_TYPE")
.replace("HWNMNORTHBOUNDOBJECTINSTANCE","MANAGED_OBJECT_INSTANCE").replace("HWNMNORTHBOUNDPROBABLECAUSE","PROBABLE_CAUSE")
.replace("HWNMNORTHBOUNDSEVERITY","PERCEIVED_SEVERITY").replace("SNMPTRAPOID","NOTIFICATION_TYPE")
.replace("EVENTNAME","EVENT_TYPE").replace("FIRSTTIMEDETECTEDDATE","EVENT_DATE").replace("FIRSTTIMEDETECTEDTIME","EVENT_TIME").replace("ISACKNOWLEDGED","ACK_STATE")
.replace("MANAGEDOBJECTID","MANAGED_OBJECT").replace("NODENAME","ELEMENT_NAME").replace("PROBABLECAUSE","PROBABLE_CAUSE").replace("SEVERITY","PERCEIVED_SEVERITY").replace("PERCEIVED_PERCEIVED_SEVERITY","PERCEIVED_SEVERITY")
.replace("SPECIFICPROBLEM","SPECIFIC_PROBLEM");
TableHeader th = new TableHeader(colm, colm);
cols.add(th);
}  

for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
String object=docs.get("_id").toString();
String substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}"));
String spls_cols[]=substr.split(",");
for(String cv:spls_cols) {
String cl=cv.substring(0,cv.indexOf("=")).trim().replace("AID","ALARM_ID").replace("ALARMACK","ACK_STATE")
.replace("ALARMADDITIONALTEXT","ADDITIONAL_TEXT").replace("ALARMEVENTDATE","EVENT_DATE").replace("ALARMEVENTTIME","EVENT_TIME")
.replace("ALARMEVENTTYPE","EVENT_TYPE").replace("ALARMMANAGEDOBJECTINSTANCE","MANAGED_ELEMENT")
.replace("ALARMMANAGEDOBJECTINSTANCENAME","MANAGED_OBJECT_CLASS").replace("ALARMMOCOBJECTINSTANCE","ELEMENT_NAME")
.replace("ALARMNETYPE","ELEMENT_TYPE").replace("ALARMPERCEIVEDSEVERITY","PERCEIVED_SEVERITY").replace("ALARMPROBABLECAUSE","PROBABLE_CAUSE")
.replace("ALARMSPECIFICPROBLEM","SPECIFIC_PROBLEM").replace("SNMPTRAPOID","NOTIFICATION_TYPE")
.replace("HWNMNORTHBOUNDADDITIONALINFO","ADDITIONAL_TEXT").replace("HWNMNORTHBOUNDEVENTDATE","EVENT_DATE")
.replace("HWNMNORTHBOUNDEVENTDETAIL","SPCIFIC_PROBLEM").replace("HWNMNORTHBOUNDEVENTNAME","EVENT_NAME")
.replace("HWNMNORTHBOUNDEVENTTIME","EVENT_TIME").replace("HWNMNORTHBOUNDEVENTTYPE","EVENT_TYPE").replace("HWNMNORTHBOUNDFAULTID","ALARM_ID")
.replace("HWNMNORTHBOUNDMAINTAINSTATUS","ACK_STATE").replace("HWNMNORTHBOUNDNENAME","ELEMENT_NAME").replace("HWNMNORTHBOUNDNETYPE","ELEMENT_TYPE")
.replace("HWNMNORTHBOUNDOBJECTINSTANCE","MANAGED_OBJECT_INSTANCE").replace("HWNMNORTHBOUNDPROBABLECAUSE","PROBABLE_CAUSE")
.replace("HWNMNORTHBOUNDSEVERITY","PERCEIVED_SEVERITY").replace("SNMPTRAPOID","NOTIFICATION_TYPE")
.replace("EVENTNAME","EVENT_TYPE").replace("FIRSTTIMEDETECTEDDATE","EVENT_DATE").replace("FIRSTTIMEDETECTEDTIME","EVENT_TIME").replace("ISACKNOWLEDGED","ACK_STATE")
.replace("MANAGEDOBJECTID","MANAGED_OBJECT").replace("NODENAME","ELEMENT_NAME").replace("PROBABLECAUSE","PROBABLE_CAUSE").replace("SEVERITY","PERCEIVED_SEVERITY").replace("PERCEIVED_PERCEIVED_SEVERITY","PERCEIVED_SEVERITY")
.replace("SPECIFICPROBLEM","SPECIFIC_PROBLEM");
String vl=cv.substring(cv.indexOf("=")+1).trim().replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","");
colval.put(cl, vl);	
}
if(!vals.similar(colval)) {
vals.put(colval);	
}		
}

}
else {
ArrayList<Document> resultSet = collection.find(filter).limit(100000).into(new ArrayList<Document>());
ArrayList<String>colms=new ArrayList<String>();

int size=0;
int max=0;
Document doc=null;
for(int i=0;i<resultSet.size();i++) {
Document document=resultSet.get(i);
size=document.keySet().size();
if(size>max) {
max=size;
doc=document;
}
}

Iterator<String> itr = doc.keySet().iterator();
while(itr.hasNext()){
String col=itr.next().toString();
if(!col.equals("_id")) {
colms.add(col);	
String cl=col.replace("AID","ALARM_ID").replace("ALARMACK","ACK_STATE")
.replace("ALARMADDITIONALTEXT","ADDITIONAL_TEXT").replace("ALARMEVENTDATE","EVENT_DATE").replace("ALARMEVENTTIME","EVENT_TIME")
.replace("ALARMEVENTTYPE","EVENT_TYPE").replace("ALARMMANAGEDOBJECTINSTANCE","MANAGED_ELEMENT")
.replace("ALARMMANAGEDOBJECTINSTANCENAME","MANAGED_OBJECT_CLASS").replace("ALARMMOCOBJECTINSTANCE","ELEMENT_NAME")
.replace("ALARMNETYPE","ELEMENT_TYPE").replace("ALARMPERCEIVEDSEVERITY","PERCEIVED_SEVERITY").replace("ALARMPROBABLECAUSE","PROBABLE_CAUSE")
.replace("ALARMSPECIFICPROBLEM","SPECIFIC_PROBLEM").replace("SNMPTRAPOID","NOTIFICATION_TYPE")
.replace("HWNMNORTHBOUNDADDITIONALINFO","ADDITIONAL_TEXT").replace("HWNMNORTHBOUNDEVENTDATE","EVENT_DATE")
.replace("HWNMNORTHBOUNDEVENTDETAIL","SPCIFIC_PROBLEM").replace("HWNMNORTHBOUNDEVENTNAME","EVENT_NAME")
.replace("HWNMNORTHBOUNDEVENTTIME","EVENT_TIME").replace("HWNMNORTHBOUNDEVENTTYPE","EVENT_TYPE").replace("HWNMNORTHBOUNDFAULTID","ALARM_ID")
.replace("HWNMNORTHBOUNDMAINTAINSTATUS","ACK_STATE").replace("HWNMNORTHBOUNDNENAME","ELEMENT_NAME").replace("HWNMNORTHBOUNDNETYPE","ELEMENT_TYPE")
.replace("HWNMNORTHBOUNDOBJECTINSTANCE","MANAGED_OBJECT_INSTANCE").replace("HWNMNORTHBOUNDPROBABLECAUSE","PROBABLE_CAUSE")
.replace("HWNMNORTHBOUNDSEVERITY","PERCEIVED_SEVERITY").replace("SNMPTRAPOID","NOTIFICATION_TYPE")
.replace("EVENTNAME","EVENT_TYPE").replace("FIRSTTIMEDETECTEDDATE","EVENT_DATE").replace("FIRSTTIMEDETECTEDTIME","EVENT_TIME").replace("ISACKNOWLEDGED","ACK_STATE")
.replace("MANAGEDOBJECTID","MANAGED_OBJECT").replace("NODENAME","ELEMENT_NAME").replace("PROBABLECAUSE","PROBABLE_CAUSE").replace("SEVERITY","PERCEIVED_SEVERITY")
.replace("SPECIFICPROBLEM","SPECIFIC_PROBLEM");	
cls.add(cl);
TableHeader th=new TableHeader(cl, cl);
cols.add(th);
}
}
for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
for(int j=0;j<colms.size();j++) {
String cl=colms.get(j).replace("AID","ALARM_ID").replace("ALARMACK","ACK_STATE")
.replace("ALARMADDITIONALTEXT","ADDITIONAL_TEXT").replace("ALARMEVENTDATE","EVENT_DATE").replace("ALARMEVENTTIME","EVENT_TIME")
.replace("ALARMEVENTTYPE","EVENT_TYPE").replace("ALARMMANAGEDOBJECTINSTANCE","MANAGED_ELEMENT")
.replace("ALARMMANAGEDOBJECTINSTANCENAME","MANAGED_OBJECT_CLASS").replace("ALARMMOCOBJECTINSTANCE","ELEMENT_NAME")
.replace("ALARMNETYPE","ELEMENT_TYPE").replace("ALARMPERCEIVEDSEVERITY","PERCEIVED_SEVERITY").replace("ALARMPROBABLECAUSE","PROBABLE_CAUSE")
.replace("ALARMSPECIFICPROBLEM","SPECIFIC_PROBLEM").replace("SNMPTRAPOID","NOTIFICATION_TYPE")
.replace("HWNMNORTHBOUNDADDITIONALINFO","ADDITIONAL_TEXT").replace("HWNMNORTHBOUNDEVENTDATE","EVENT_DATE")
.replace("HWNMNORTHBOUNDEVENTDETAIL","SPCIFIC_PROBLEM").replace("HWNMNORTHBOUNDEVENTNAME","EVENT_NAME")
.replace("HWNMNORTHBOUNDEVENTTIME","EVENT_TIME").replace("HWNMNORTHBOUNDEVENTTYPE","EVENT_TYPE").replace("HWNMNORTHBOUNDFAULTID","ALARM_ID")
.replace("HWNMNORTHBOUNDMAINTAINSTATUS","ACK_STATE").replace("HWNMNORTHBOUNDNENAME","ELEMENT_NAME").replace("HWNMNORTHBOUNDNETYPE","ELEMENT_TYPE")
.replace("HWNMNORTHBOUNDOBJECTINSTANCE","MANAGED_OBJECT_INSTANCE").replace("HWNMNORTHBOUNDPROBABLECAUSE","PROBABLE_CAUSE")
.replace("HWNMNORTHBOUNDSEVERITY","PERCEIVED_SEVERITY").replace("SNMPTRAPOID","NOTIFICATION_TYPE")
.replace("EVENTNAME","EVENT_TYPE").replace("FIRSTTIMEDETECTEDDATE","EVENT_DATE").replace("FIRSTTIMEDETECTEDTIME","EVENT_TIME").replace("ISACKNOWLEDGED","ACK_STATE")
.replace("MANAGEDOBJECTID","MANAGED_OBJECT").replace("NODENAME","ELEMENT_NAME").replace("PROBABLECAUSE","PROBABLE_CAUSE").replace("SEVERITY","PERCEIVED_SEVERITY")
.replace("SPECIFICPROBLEM","SPECIFIC_PROBLEM");	
if(docs.get(colms.get(j)).toString().length()>0) {
colval.put(cl, docs.get(colms.get(j)));	
}
else {
colval.put(cl,"-");	
}
}
if(!vals.similar(colval)) {
vals.put(colval);	
}
}	
}
JSONArray jsonArrayFinal=new JSONArray();
JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
	
closeConnection(mongo);
return jsonArrayFinal.toString();	
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return null;
}

public String getTableDataGeneric(String tableName,String columns,String conditions) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableDataGeneric ****************");
}
JSONArray jsonArrayFinal=new JSONArray();
try {
ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(conditions.length()>1) {
String cond=conditions.replace("@DOT@", ".");	
if(cond.contains(" and ")){
String cond_spls[]=cond.split(" and ");
for(String cv:cond_spls) {
if(cv.contains("=") && !cv.contains("like")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'", "");;
fltr.add(eq(col,val));
}
if(!cv.contains("=") && cv.contains("like")) {
String col=cv.substring(0,cv.indexOf("like")).trim();
String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*",Pattern.CASE_INSENSITIVE)));
}
}
filter=and(fltr);
}
else if(cond.contains(" AND ")){
String cond_spls[]=cond.split(" AND ");
for(String cv:cond_spls) {
if(cv.contains("=") && !cv.contains("like")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'", "");;
fltr.add(eq(col,val));
}
if(!cv.contains("=") && cv.contains("like")) {
String col=cv.substring(0,cv.indexOf("like")).trim();
String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*",Pattern.CASE_INSENSITIVE)));
}
}
filter=and(fltr);
}
else if(!(cond.contains(" AND ") && cond.contains(" and ")) && cond.length()>1){
if(cond.contains("=")) {
String col=cond.substring(0,cond.indexOf("=")).trim();
String val=cond.substring(cond.indexOf("=")+1).trim().replace("'", "");
fltr.add(eq(col,val));
}
filter=and(fltr);
}
}
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<String>cls=new ArrayList<String>();
ArrayList<Document> resultSet = null;
ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
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
resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))),limit(500))).into(new ArrayList<Document>());
}
else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))),limit(500))).into(new ArrayList<Document>());
}
String cols_spls[]=columns.split(",");
for(String col:cols_spls) {
String colm=col;
TableHeader th = new TableHeader(colm, colm);
cols.add(th);
}  

for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
String object=docs.get("_id").toString();
String substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}"));
String spls_cols[]=substr.split(",");
for(String cv:spls_cols) {
String cl=cv.substring(0,cv.indexOf("=")).trim();
String vl=cv.substring(cv.indexOf("=")+1).trim().replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","");
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
int size=0;
int max=0;
Document doc=null;
for(int i=0;i<resultSet.size();i++) {
Document document=resultSet.get(i);
size=document.keySet().size();
if(size>max) {
max=size;
doc=document;
}
}
Iterator<String> itr = doc.keySet().iterator();
while(itr.hasNext()){
String col=itr.next().toString();
if(!col.equals("_id")) {
cls.add(col);
TableHeader th=new TableHeader(col, col);
cols.add(th);
}
}
for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
for(int j=0;j<cls.size();j++) {
if(docs.containsKey(cls.get(j))) {	
if(docs.get(cls.get(j)).toString().length()>0 && docs.get(cls.get(j)).toString()!=null) {
colval.put(cls.get(j), docs.get(cls.get(j)));	
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

JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
	
closeConnection(mongo);
//return jsonArrayFinal.toString();	
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return jsonArrayFinal.toString();
}

/*
public String getTableDataGenericWOC(String tableName,String columns,String conditions) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableDataGenericWOC ****************");
}
try {
ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(conditions.length()>1) {
String cond=conditions.replace("@DOT@", ".");	
if(cond.contains(" and ")){
String cond_spls[]=cond.split(" and ");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim();
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(cond.contains(" AND ")){
String cond_spls[]=cond.split(" AND ");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim();
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
}

Properties config=getProperties();
BaseDAOMongo baseDAOMongo=new BaseDAOMongo();
MongoDatabase database = baseDAOMongo.getConnection(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<String>cls=new ArrayList<String>();
Map<String, Object> groupMap = new HashMap<String, Object>();
String columns_spls[]=columns.split(",");
for(String colm:columns_spls) {
cls.add(colm);	
groupMap.put(colm, "$"+colm);
}

DBObject groupFields = new BasicDBObject(groupMap);

ArrayList<Document> resultSet = null;
if(filter!=null) {
resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))),limit(500))).into(new ArrayList<Document>());
}
else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))),limit(500))).into(new ArrayList<Document>());
}

ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
JSONArray vals=new JSONArray();

String cols_spls[]=columns.split(",");
for(String col:cols_spls) {
String colm=col;
TableHeader th = new TableHeader(colm, colm);
cols.add(th);
}  

for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
for(int j=0;j<cls.size();j++) {
String colm=cls.get(j);
	
if(docs.get(cls.get(j)).toString().length()>0) {
String val=docs.get(cls.get(j)).toString().replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","");
colval.put(colm, val);	
}
else {
colval.put(colm,"-");	
}
}
vals.put(colval);	
}
JSONArray jsonArrayFinal=new JSONArray();
JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
	
baseDAOMongo.closeConnection();

return jsonArrayFinal.toString();	
} catch (Exception ex) {
  log.error("Exception occurs:----" + ex.getMessage(), ex);
  //ex.printStackTrace();
}
return null;
}
*/

public String getTableDataOnElementGeneric(String tableName,String columns,String conditions,String order_by) {


if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableDataOnElementGeneric ****************");
}
try {
ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(conditions.length()>1) {
String cond=conditions.replace("@DOT@", ".").replace("@SLASH@", "/");	
if(cond.contains(" and ")){
String cond_spls[]=cond.split(" and ");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'","");
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(cond.contains(" AND ")){
String cond_spls[]=cond.split(" AND ");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'","");
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(!(cond.contains(" AND ") && cond.contains(" and ")) && cond.length()>1){
if(cond.contains("=")) {
String col=cond.substring(0,cond.indexOf("=")).trim();
String val=cond.substring(cond.indexOf("=")+1).trim();
fltr.add(eq(col,val));
}
filter=and(fltr);
}
}
//-----------------ANALYSIS
ArrayList < treeParent >  list = new ArrayList < treeParent >();
list.addAll(analysisDAO.treeAlarmUser(tableName, "ericsson"));

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<String>cls=new ArrayList<String>();
ArrayList<Document> resultSet = null;
ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
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
resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))),sort(descending("_id."+order_by)),limit(500))).into(new ArrayList<Document>());
}
else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))),sort(descending(order_by)),limit(500))).into(new ArrayList<Document>());
}
String cols_spls[]=columns.split(",");
for(String col:cols_spls) {
String colm=col;
TableHeader th = new TableHeader(colm, colm);
cols.add(th);
}  

for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
String object=docs.get("_id").toString();
String substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}"));
String spls_cols[]=substr.split(",");
for(String cv:spls_cols) {
String cl=cv.substring(0,cv.indexOf("=")).trim();
String vl=cv.substring(cv.indexOf("=")+1).trim().replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","");
for(treeMid l : list.get(0).getChildren()) {
if(l.getName().contains(vl) ||  l.getName().equals("145960")){
colval.put(cl, vl+" (-Learning Found-)");
}
else{
colval.put(cl, vl);
}	
}

}
if(!vals.similar(colval)) {
vals.put(colval);	
}	
}
}
else {	
if(filter!=null) {	
resultSet = collection.find(filter).sort(descending("_id."+order_by)).into(new ArrayList<Document>());
}
else {
resultSet = collection.find().sort(descending("_id."+order_by)).into(new ArrayList<Document>());
}

int size=0;
int max=0;
Document doc=null;
for(int i=0;i<resultSet.size();i++) {
Document document=resultSet.get(i);
size=document.keySet().size();
if(size>max) {
max=size;
doc=document;
}
}
Iterator<String> itr = doc.keySet().iterator();
while(itr.hasNext()){
String col=itr.next().toString();
if(!col.equals("_id")) {
cls.add(col);
TableHeader th=new TableHeader(col, col);
cols.add(th);
}
}
for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
for(int j=0;j<cls.size();j++) {
String vl = docs.get(cls.get(j)).toString();
if(vl.toString().length()>0) {
for(treeMid l : list.get(0).getChildren()) {
if(l.getName().contains(vl) ||  l.getName().equals("145960")){
colval.put(cls.get(j), vl+" (-Learning Found-)");
}
else{
colval.put(cls.get(j), vl);
}	
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
JSONArray jsonArrayFinal=new JSONArray();
JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
	
closeConnection(mongo);

return jsonArrayFinal.toString();	


/*

Connection conn = getConnection();
ArrayList < String > colName = new ArrayList < String > ();
ArrayList < String > ar2 = new ArrayList < String > ();
ArrayList < String > ar3 = new ArrayList < String > ();


ResultSet rs = null;
//System.out.println(alarm+">>>"+severity);
String query = "";

if(conditions.trim().length()<2) {
query="SELECT Distinct "+columns+" FROM "+tableName+" ORDER BY "+order_by+" desc LIMIT 50";	
}

if(conditions.trim().length()>1) {
query="SELECT Distinct "+columns+" FROM "+tableName+" where "+conditions.replace("@SLASH@", "/")+" ORDER BY "+order_by+" desc LIMIT 50";	
}
System.out.println("query============="+query);
try {
	ArrayList < treeParent >  list = new ArrayList < treeParent >();
		//list =	analysisDAO.treeAlarm(tableName, "ericsson", "NA");
list.addAll(analysisDAO.treeAlarmUser(tableName, "ericsson"));
  Statement stmnt=conn.createStatement();
  rs = stmnt.executeQuery(query);
  int columnCount = rs.getMetaData().getColumnCount();

  // The column count starts from 1
  for (int i = 1; i <= columnCount; i++) {
    String name = rs.getMetaData().getColumnName(i);
    colName.add(name);
  }

  while (rs.next()) {
String s = "";
for (int a = 0; a < colName.size(); a++) {
	String strVal = rs.getString(a + 1).replace("\"","").replace("\"","").replaceAll("[^a-zA-Z0-9\\s+-+-*.#:-_]","").trim();
if(a==0) {
	for(treeMid l : list.get(0).getChildren()) {
		if(l.getName().contains(strVal)) {//l.getName() || strVal.equals("145960")
s += "\"" + colName.get(a).trim() + "\"" + ":" + "\"" + strVal + " (-Learning Found-)\"" + ",";
	break;
}
else {
	s += "\"" + colName.get(a).trim() + "\"" + ":" + "\"" + strVal + "\"" + ",";
			}
		}
		}
	else {
  s += "\"" + colName.get(a).trim() + "\"" + ":" + "\"" + strVal + "\"" + ",";
	}
	}
    String ss = s.substring(0, s.length() - 1);
    ar2.add(ss.trim());
  }

  for (int i = 0; i < ar2.size(); i++) {
    ar3.add("{" + ar2.get(i).toString().trim() + "}");
  }
  stmnt.close();
  closeConnection(conn);
*/
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return null;
}
/*
public String getTableDataOnElementGenericWOO(String tableName,String columns,String conditions) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableDataOnElementGenericWOO ****************");
}

try {

ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(conditions.length()>1) {
String cond=conditions.replace("@SLASH@", "/");	
if(cond.contains(" and ")){
String cond_spls[]=cond.split(" and ");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim();
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(cond.contains(" AND ")){
String cond_spls[]=cond.split(" AND ");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim();
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
}

Properties config=getProperties();
BaseDAOMongo baseDAOMongo=new BaseDAOMongo();
MongoDatabase database = baseDAOMongo.getConnection(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<String>cls=new ArrayList<String>();
Map<String, Object> groupMap = new HashMap<String, Object>();
String columns_spls[]=columns.split(",");
for(String colm:columns_spls) {
cls.add(colm);	
groupMap.put(colm, "$"+colm);
}

DBObject groupFields = new BasicDBObject(groupMap);

ArrayList<Document> resultSet = null;
if(filter!=null) {
resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))),limit(500))).into(new ArrayList<Document>());
}
else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))),limit(500))).into(new ArrayList<Document>());
}

ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
JSONArray vals=new JSONArray();

String cols_spls[]=columns.split(",");
for(String col:cols_spls) {
String colm=col;
TableHeader th = new TableHeader(colm, colm);
cols.add(th);
}  

for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
for(int j=0;j<cls.size();j++) {
String colm=cls.get(j);
	
if(docs.get(cls.get(j)).toString().length()>0) {
String val=docs.get(cls.get(j)).toString().replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","");
colval.put(colm, val);	
}
else {
colval.put(colm,"-");	
}
}
vals.put(colval);	
}
JSONArray jsonArrayFinal=new JSONArray();
JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
	
baseDAOMongo.closeConnection();

return jsonArrayFinal.toString();	

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}

return null;
}
*/

/*
public ArrayList < TableHeader > getTableColumnsOnElementGenericIpran(String tableName,String columns) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableColumnsOnElementGenericIpran ****************");
}
//-------------------------------
//use in AlarmFile, trouble_ticket, kpi-resolution
//-------------------------------
Connection conn = getConnection();
ArrayList < TableHeader > ar = new ArrayList < TableHeader > ();
ResultSet rs = null;
String query = "SELECT Distinct "+columns+" FROM "+tableName+" limit 1";
//System.out.println("query====="+query);
try {
  
Statement stmnt=conn.createStatement();

  rs = stmnt.executeQuery(query);
  int columnCount = rs.getMetaData().getColumnCount();

  // The column count starts from 1
  for (int i = 1; i <= columnCount; i++) {
    String name = rs.getMetaData().getColumnName(i);
    TableHeader f_h = new TableHeader(name, name);

    ar.add(f_h);
  }
  stmnt.close();
  closeConnection(conn);
} catch (Exception ex) {
  log.error("Exception occurs:----" + ex.getMessage(), ex);
  //ex.printStackTrace();
    }

    return ar;
  }
*/
public String getTableDataOnElementGenericSpecificDomain(String tableName,String columns,String conditions,String order_by,String specificColumn,String specificColName) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableDataOnElementGeneric ****************");
}

try {

ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(conditions.length()>1) {
String cond=conditions.replace("@DOT@", ".").replace("@SLASH@", "/");	
if(cond.contains(" and ")){
String cond_spls[]=cond.split(" and ");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'", "");
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(cond.contains(" AND ")){
String cond_spls[]=cond.split(" AND ");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'", "");;
fltr.add(eq(col,val));
}
}
}
else if(!(cond.contains(" AND ") && cond.contains(" and ")) && cond.length()>1){
if(cond.contains("=")) {
String col=cond.substring(0,cond.indexOf("=")).trim();
String val=cond.substring(cond.indexOf("=")+1).trim().replace("'", "");
fltr.add(eq(col,val));
}
}
fltr.add(eq(specificColumn,java.util.regex.Pattern.compile("^.*"+specificColName+".*")));
filter=and(fltr);
}
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<String>cls=new ArrayList<String>();
ArrayList<Document> resultSet = null;
ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
JSONArray vals=new JSONArray();

Map<String, Object> groupMap = new HashMap<String, Object>();

if(!columns.equals("*")) {
if(columns.contains(",")) {	
String columns_spls[]=columns.split(",");
for(String colm:columns_spls) {
cls.add(colm.trim());	
groupMap.put(colm, "$"+colm.trim());
}
}
else {
cls.add(columns);	
groupMap.put(columns, "$"+columns);	
}

DBObject groupFields = new BasicDBObject(groupMap);

if(filter!=null) {

resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))),sort(descending("_id."+order_by)),limit(200))).into(new ArrayList<Document>());
}
else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))),sort(descending("_id."+order_by)),limit(200))).into(new ArrayList<Document>());
}

String cols_spls[]=columns.split(",");
for(String col:cols_spls) {
String colm=col;
TableHeader th = new TableHeader(colm, colm);
cols.add(th);
}  

for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
String object=docs.get("_id").toString();
String substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}"));
String spls_cols[]=substr.split(",");
for(String cv:spls_cols) {
String cl=cv.substring(0,cv.indexOf("=")).trim();
String vl=cv.substring(cv.indexOf("=")+1).trim().replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","");
colval.put(cl, vl);	
}
if(!vals.similar(colval)) {
vals.put(colval);	
}
}
}
else {
if(filter!=null) {	
resultSet = collection.find(filter).sort(descending("_id."+order_by)).limit(200).into(new ArrayList<Document>());
}
else {
resultSet = collection.find().sort(descending("_id."+order_by)).limit(200).into(new ArrayList<Document>());
}
int size=0;
int max=0;
Document doc=null;
for(int i=0;i<resultSet.size();i++) {
Document document=resultSet.get(i);
size=document.keySet().size();
if(size>max) {
max=size;
doc=document;
}
}
Iterator<String> itr = doc.keySet().iterator();
while(itr.hasNext()){
String col=itr.next().toString();
if(!col.equals("_id")) {
cls.add(col);
TableHeader th=new TableHeader(col, col);
cols.add(th);
}
}
for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
for(int j=0;j<cls.size();j++) {
if(docs.containsKey(cls.get(j))) {	
if(docs.get(cls.get(j)).toString().length()>0 && docs.get(cls.get(j)).toString()!=null) {
colval.put(cls.get(j), docs.get(cls.get(j)));	
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

JSONArray jsonArrayFinal=new JSONArray();
JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
	
closeConnection(mongo);
return jsonArrayFinal.toString();	
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return null;

}

public String getTableDataOnElementGenericSpecificDomainWOO(String tableName,String columns,String conditions,String specificColumn,String specificColName) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableDataOnElementGenericSpecificDomainWOO ****************");
}

try {

ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(conditions.length()>1) {
String cond=conditions.replace("@DOT@", ".").replace("@SLASH@", "/");	
if(cond.contains(" and ")){
String cond_spls[]=cond.split(" and ");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'", "");;
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(cond.contains(" AND ")){
String cond_spls[]=cond.split(" AND ");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'", "");;
fltr.add(eq(col,val));
}
}
fltr.add(eq(specificColumn,java.util.regex.Pattern.compile("^.*"+specificColName+".*")));
filter=and(fltr);
}
else if(!(cond.contains(" AND ") && cond.contains(" and ")) && cond.length()>1){
if(cond.contains("=")) {
String col=cond.substring(0,cond.indexOf("=")).trim();
String val=cond.substring(cond.indexOf("=")+1).trim().replace("'", "");
fltr.add(eq(col,val));
}
filter=and(fltr);
}
}

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<String>cls=new ArrayList<String>();
ArrayList<Document> resultSet = null;
ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
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
resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))),limit(50))).into(new ArrayList<Document>());
}
else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))),limit(50))).into(new ArrayList<Document>());
}
String cols_spls[]=columns.split(",");
for(String col:cols_spls) {
String colm=col;
TableHeader th = new TableHeader(colm, colm);
cols.add(th);
}  

for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
String object=docs.get("_id").toString();
String substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}"));
String spls_cols[]=substr.split(",");
for(String cv:spls_cols) {
String cl=cv.substring(0,cv.indexOf("=")).trim();
String vl=cv.substring(cv.indexOf("=")+1).trim().replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","");
colval.put(cl, vl);	
}
if(!vals.similar(colval)) {
vals.put(colval);	
}	
}
}
else {
if(filter!=null) {	
resultSet = collection.find(filter).limit(50).into(new ArrayList<Document>());
}
else {
resultSet = collection.find().limit(50).into(new ArrayList<Document>());
}
int size=0;
int max=0;
Document doc=null;
for(int i=0;i<resultSet.size();i++) {
Document document=resultSet.get(i);
size=document.keySet().size();
if(size>max) {
max=size;
doc=document;
}
}
Iterator<String> itr = doc.keySet().iterator();
while(itr.hasNext()){
String col=itr.next().toString();
if(!col.equals("_id")) {
cls.add(col);
TableHeader th=new TableHeader(col, col);
cols.add(th);
}
}
for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
for(int j=0;j<cls.size();j++) {
if(docs.containsKey(cls.get(j))) {	
if(docs.get(cls.get(j)).toString().length()>0 && docs.get(cls.get(j)).toString()!=null) {
colval.put(cls.get(j), docs.get(cls.get(j)));	
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

JSONArray jsonArrayFinal=new JSONArray();
JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
	
closeConnection(mongo);
return jsonArrayFinal.toString();	
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return null;

}


public String getTableDataOnElementGenericWOO(String tableName,String columns,String conditions) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableDataOnElementGenericWOO ****************");
}
try {

ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(conditions.length()>1) {
String cond=conditions.replace("@DOT@", ".").replace("@SLASH@", "/");	
if(cond.contains(" and ")){
String cond_spls[]=cond.split(" and ");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'", "");;
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(cond.contains(" AND ")){
String cond_spls[]=cond.split(" AND ");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'", "");;
fltr.add(eq(col,val));
}
}
//fltr.add(eq(specificColumn,java.util.regex.Pattern.compile("^.*"+specificColName+".*")));
filter=and(fltr);
}
else if(!(cond.contains(" AND ") && cond.contains(" and ")) && cond.length()>1){
if(cond.contains("=")) {
String col=cond.substring(0,cond.indexOf("=")).trim();
String val=cond.substring(cond.indexOf("=")+1).trim().replace("'", "");
fltr.add(eq(col,val));
}
filter=and(fltr);
}
}

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<String>cls=new ArrayList<String>();
ArrayList<Document> resultSet = null;
ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
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
resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))),limit(50))).into(new ArrayList<Document>());
}
else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))),limit(50))).into(new ArrayList<Document>());
}
String cols_spls[]=columns.split(",");
for(String col:cols_spls) {
String colm=col;
TableHeader th = new TableHeader(colm, colm);
cols.add(th);
}  

for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
String object=docs.get("_id").toString();
String substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}"));
String spls_cols[]=substr.split(",");
for(String cv:spls_cols) {
String cl=cv.substring(0,cv.indexOf("=")).trim();
String vl=cv.substring(cv.indexOf("=")+1).trim().replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","");
colval.put(cl, vl);	
}
if(!vals.similar(colval)) {
vals.put(colval);	
}	
}
}
else {
if(filter!=null) {	
resultSet = collection.find(filter).limit(50).into(new ArrayList<Document>());
}
else {
resultSet = collection.find().limit(50).into(new ArrayList<Document>());
}
int size=0;
int max=0;
Document doc=null;
for(int i=0;i<resultSet.size();i++) {
Document document=resultSet.get(i);
size=document.keySet().size();
if(size>max) {
max=size;
doc=document;
}
}
Iterator<String> itr = doc.keySet().iterator();
while(itr.hasNext()){
String col=itr.next().toString();
if(!col.equals("_id")) {
cls.add(col);
TableHeader th=new TableHeader(col, col);
cols.add(th);
}
}
for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
for(int j=0;j<cls.size();j++) {
if(docs.containsKey(cls.get(j))) {	
if(docs.get(cls.get(j)).toString().length()>0 && docs.get(cls.get(j)).toString()!=null) {
colval.put(cls.get(j), docs.get(cls.get(j)));	
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

JSONArray jsonArrayFinal=new JSONArray();
JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
	
closeConnection(mongo);
return jsonArrayFinal.toString();	
	
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return null;
}


public String getTableDataGenericResolution(String tableName,String columns,String conditions,String AlarmId) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableDataGeneric ****************");
}

try {
ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(conditions.length()>1) {
String cond=conditions.replace("@SLASH@", "/");	
if(cond.contains(" and ")){
String cond_spls[]=cond.split(" and ");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'", "");;
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(cond.contains(" AND ")){
String cond_spls[]=cond.split(" AND ");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'", "");;
fltr.add(eq(col,val));
}
}
fltr.add(eq("DESCRIPTION",java.util.regex.Pattern.compile("^.*"+AlarmId+".*")));
filter=and(fltr);
}
else if(!(cond.contains(" AND ") && cond.contains(" and ")) && cond.length()>1){
if(cond.contains("=")) {
String col=cond.substring(0,cond.indexOf("=")).trim();
String val=cond.substring(cond.indexOf("=")+1).trim().replace("'", "");
fltr.add(eq(col,val));
}
filter=and(fltr);
}
}

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<String>cls=new ArrayList<String>();
Map<String, Object> groupMap = new HashMap<String, Object>();
String columns_spls[]=columns.split(",");
for(String colm:columns_spls) {
cls.add(colm);	
groupMap.put(colm, "$"+colm);
}

DBObject groupFields = new BasicDBObject(groupMap);

ArrayList<Document> resultSet = null;
if(filter!=null) {
resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))),limit(50))).into(new ArrayList<Document>());
}
else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))),limit(50))).into(new ArrayList<Document>());
}

ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
JSONArray vals=new JSONArray();

String cols_spls[]=columns.split(",");
for(String col:cols_spls) {
String colm=col;
TableHeader th = new TableHeader(colm, colm);
cols.add(th);
}  

for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
String object=docs.get("_id").toString();
String substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}"));
String spls_cols[]=substr.split(",");
for(String cv:spls_cols) {
String cl=cv.substring(0,cv.indexOf("=")).trim();
String vl=cv.substring(cv.indexOf("=")+1).trim().replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","");
colval.put(cl, vl);	
}
if(!vals.similar(colval)) {
vals.put(colval);	
}	
}
JSONArray jsonArrayFinal=new JSONArray();
JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
	
closeConnection(mongo);

return jsonArrayFinal.toString();	

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return null;
}



public String getGisGenericInfo(String tableName,String columns) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getGisGenericInfo ****************");
}
JSONArray vals=new JSONArray();

try {
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<String>cls=new ArrayList<String>();
Map<String, Object> groupMap = new HashMap<String, Object>();
String columns_spls[]=columns.split(",");
for(String colm:columns_spls) {
cls.add(colm);	
groupMap.put(colm, "$"+colm);
}

DBObject groupFields = new BasicDBObject(groupMap);

ArrayList<Document> resultSet = null;
resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))))).into(new ArrayList<Document>());

ArrayList<TableHeader>cols=new ArrayList<TableHeader>();

String cols_spls[]=columns.split(",");
for(String col:cols_spls) {
String colm=col;
TableHeader th = new TableHeader(colm, colm);
cols.add(th);
}  
for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
String object=docs.get("_id").toString();
String substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}"));
String spls_cols[]=substr.split(",");
for(String cv:spls_cols) {
String cl=cv.substring(0,cv.indexOf("=")).trim();
String vl=cv.substring(cv.indexOf("=")+1).trim();
colval.put(cl, vl);
}
if(!vals.similar(colval)) {
vals.put(colval);	
}	
}	
closeConnection(mongo);

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return vals.toString();	

}

public int uploadMapInfoTransmission(MultipartFile file){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into uploadMapInfoTransmission ****************");	
}	

Properties config=getProperties();
Path rootLocation = Paths.get(config.getProperty("server.directory"));
String file_store = file.getOriginalFilename();
File file_check = new File(rootLocation + "/"  + file_store);
try {
MongoClient mongo=getConnection();
if (file_check.exists()) {
Files.delete(rootLocation.resolve(file_store));
Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
String path=""+rootLocation;
ConvertAndReadKmzFile task=new ConvertAndReadKmzFile(database, file_check, path);
Thread thrd=new Thread(task);
thrd.start();
} 
else {
Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
String path=""+rootLocation;
ConvertAndReadKmzFile task=new ConvertAndReadKmzFile(database, file_check, path);
Thread thrd=new Thread(task);
thrd.start();
}

closeConnection(mongo);
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 1;	
}


public String getTableColsValsGeneric(String tableName) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableColsValsGeneric ****************");
}

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<Document> resultSet = collection.find().into(new ArrayList<Document>());
int size=0;
int max=0;
Document doc=null;
for(int i=0;i<resultSet.size();i++) {
Document document=resultSet.get(i);
size=document.keySet().size();
if(size>max) {
max=size;
doc=document;
}
}
ArrayList<String>cls=new ArrayList<String>();
ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
JSONArray vals=new JSONArray();
Iterator<String> itr = doc.keySet().iterator();
while(itr.hasNext()){
String col=itr.next().toString();
if(!col.equals("_id")) {
cls.add(col);
TableHeader th=new TableHeader(col, col);
cols.add(th);
}
}
for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
for(int j=0;j<cls.size();j++) {
if(docs.containsKey(cls.get(j))) {	
if(docs.get(cls.get(j)).toString().length()>0 && docs.get(cls.get(j)).toString()!=null) {
colval.put(cls.get(j), docs.get(cls.get(j)));	
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

JSONArray jsonArrayFinal=new JSONArray();
JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
closeConnection(mongo);

return jsonArrayFinal.toString();
}


public String getTableColsGeneric(String tableName) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableColsGeneric ****************");
}

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<Document> resultSet = collection.find().into(new ArrayList<Document>());
int size=0;
int max=0;
Document doc=null;
for(int i=0;i<resultSet.size();i++) {
Document document=resultSet.get(i);
size=document.keySet().size();
if(size>max) {
max=size;
doc=document;
}
}
ArrayList<String>cls=new ArrayList<String>();
ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
Iterator<String> itr = doc.keySet().iterator();
while(itr.hasNext()){
String col=itr.next().toString();
if(!col.equals("_id")) {
cls.add(col);
TableHeader th=new TableHeader(col, col);
cols.add(th);
}
}
closeConnection(mongo);
return cols.toString();
}

public String getTableValsGeneric(String tableName) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableValsGeneric ****************");
}

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<Document> resultSet = collection.find().into(new ArrayList<Document>());
int size=0;
int max=0;
Document doc=null;
for(int i=0;i<resultSet.size();i++) {
Document document=resultSet.get(i);
size=document.keySet().size();
if(size>max) {
max=size;
doc=document;
}
}
ArrayList<String>cls=new ArrayList<String>();
JSONArray vals=new JSONArray();
Iterator<String> itr = doc.keySet().iterator();
while(itr.hasNext()){
String col=itr.next().toString();
if(!col.equals("_id")) {
cls.add(col);
}
}
for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
for(int j=0;j<cls.size();j++) {
if(docs.containsKey(cls.get(j))) {	
if(docs.get(cls.get(j)).toString().length()>0 && docs.get(cls.get(j)).toString()!=null) {
colval.put(cls.get(j), docs.get(cls.get(j)));	
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

closeConnection(mongo);
return vals.toString();
}

public String getTableColumnsGeneric(String tableName,String columns,String conditions) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableColumnsGeneric ****************");
}

try {
ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(conditions.length()>1) {
String cond=conditions.replace("@DOT@", ".").replace("@SEMICOLON@", ";");	
if(cond.contains(" and ")){
String cond_spls[]=cond.split(" and ");
for(String cv:cond_spls) {	
if(cv.contains("=") && !cv.contains("like") && !cv.contains("between")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'","");
fltr.add(eq(col,val));
}
else if(!cv.contains("=") && cv.contains("like") && !cv.contains("between")) {
String col=cv.substring(0,cv.indexOf("like")).trim();
String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*",Pattern.CASE_INSENSITIVE)));
}
else if(cv.contains("=") && cv.contains("between")) {
String col=cv.substring(0,cv.indexOf("between")).trim();
String val1=cv.substring(cv.indexOf("FROM=")+5,cv.indexOf("TO=")).replace("'","").trim();
String val2=cv.substring(cv.indexOf("TO=")+3).replace("'","").trim();	
fltr.add(gte(col,val1));
fltr.add(lte(col,val2));
}
}
//filter=and(fltr);
}
else if(cond.contains(" AND ")){
String cond_spls[]=cond.split(" AND ");
for(String cv:cond_spls) {
if(cv.contains("=") && !cv.contains("like") && !cv.contains("between")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'", "");
fltr.add(eq(col,val));
}
else if(!cv.contains("=") && cv.contains("like") && !cv.contains("between")) {
String col=cv.substring(0,cv.indexOf("like")).trim();
String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*",Pattern.CASE_INSENSITIVE)));
}
else if(cv.contains("=") && cv.contains("between")) {
String col=cv.substring(0,cv.indexOf("between")).trim();
String val1=cv.substring(cv.indexOf("FROM=")+5,cv.indexOf("TO=")).replace("'","").trim();
String val2=cv.substring(cv.indexOf("TO=")+3).replace("'","").trim();	
fltr.add(gte(col,val1));
fltr.add(lte(col,val2));
}
}
//filter=and(fltr);
}
else if(!(cond.contains(" AND ") && !cond.contains(" and ")) && cond.length()>1){
if(cond.contains("=") && cond.contains("between")) {
String col=cond.substring(0,cond.indexOf("between")).trim();
String val1=cond.substring(cond.indexOf("FROM=")+5,cond.indexOf("TO=")).replace("'","").trim();
String val2=cond.substring(cond.indexOf("TO=")+3).replace("'","").trim();	
fltr.add(gte(col,val1));
fltr.add(lte(col,val2));
}
else if(cond.contains("=")) {
String col=cond.substring(0,cond.indexOf("=")).trim();
String val=cond.substring(cond.indexOf("=")+1).trim().replace("'", "");
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<String>cls=new ArrayList<String>();
ArrayList<Document> resultSet = null;
ArrayList<TableHeader>cols=new ArrayList<TableHeader>();

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
resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))),limit(5))).into(new ArrayList<Document>());//,limit(500)
}
else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))),limit(5))).into(new ArrayList<Document>());//,limit(500)
}
String cols_spls[]=columns.split(",");
for(String col:cols_spls) {
String colm=col;
TableHeader th = new TableHeader(colm, colm);
cols.add(th);
}  
}
else {
if(filter!=null) {	
resultSet = collection.find(filter).limit(5).into(new ArrayList<Document>());
}
else {
resultSet = collection.find().limit(5).into(new ArrayList<Document>());
}

int size=0;
int max=0;
Document doc=null;
for(int i=0;i<resultSet.size();i++) {
Document document=resultSet.get(i);
size=document.keySet().size();
if(size>max) {
max=size;
doc=document;
}
}
Iterator<String> itr = doc.keySet().iterator();
while(itr.hasNext()){
String col=itr.next().toString();
if(!col.equals("_id")) {
cls.add(col);
TableHeader th=new TableHeader(col, col);
cols.add(th);
}
}

}

JSONArray jsonArrayFinal=new JSONArray();
JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonArrayFinal.put(jsonObjectColVal);
closeConnection(mongo);
String output=jsonArrayFinal.toString();
return output;	
}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return null;
}

public String getUserlogDetails(String tableName,String columns,String conditions,String orderby) {
	
	
if (log.isDebugEnabled()) {
log.debug("*************** checked into getUserlogDetails ****************");
}
try {
ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(conditions.length()>1) {
String cond=conditions.replace("@DOT@", ".").replace("@SEMICOLON@", ";").replace("@FORWARDSLASH@", "/");	
if(cond.contains(" and ")){
String cond_spls[]=cond.split(" and ");
for(String cv:cond_spls) {	
if(cv.contains("=") && !cv.contains("like") && !cv.contains("between") && !cv.contains(" or ")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim().replace("'","");
fltr.add(eq(col,val));
}
else if(!cv.contains("=") && cv.contains("like") && !cv.contains("between") && !cv.contains(" or ")) {
String col=cv.substring(0,cv.indexOf("like")).trim();
String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
if(!val.toUpperCase().contains("MANAGEDELEMENT")) {
fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*",Pattern.CASE_INSENSITIVE)));
}
else {
fltr.add(eq(col,java.util.regex.Pattern.compile("^"+val+".*",Pattern.CASE_INSENSITIVE)));	
}
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
else if(!cv.contains("=") && cv.contains("like") && !cv.contains("between")  && !cv.contains(" OR ")) {
String col=cv.substring(0,cv.indexOf("like")).trim();
String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
if(!val.toUpperCase().contains("MANAGEDELEMENT")) {
fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*",Pattern.CASE_INSENSITIVE)));
}
else {
fltr.add(eq(col,java.util.regex.Pattern.compile("^"+val+".*",Pattern.CASE_INSENSITIVE)));	
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

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<String>cls=new ArrayList<String>();
ArrayList<Document> resultSet = null;
ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
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
resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))),sort(descending("_id."+orderby)),limit(5000))).into(new ArrayList<Document>());
}
else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))),sort(descending("_id."+orderby)),limit(5000))).into(new ArrayList<Document>());
}
String cols_spls[]=columns.split(",");
for(String col:cols_spls) {
String colm=col;
TableHeader th = new TableHeader(colm, colm);
cols.add(th);
}  

for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
Document doc=(Document)docs.get("_id");
for(int j=0;j<cls.size();j++) {
if(doc.containsKey(cls.get(j))) {	
if(doc.get(cls.get(j)).toString().length()>0 && doc.get(cls.get(j)).toString()!=null) {
colval.put(cls.get(j), doc.get(cls.get(j)));	
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
else {	
if(filter!=null) {	
resultSet = collection.find(filter).limit(5000).into(new ArrayList<Document>());
}
else {
resultSet = collection.find().limit(5000).into(new ArrayList<Document>());
}
int size=0;
int max=0;
Document doc=null;
if(resultSet.size()>0){
for(int i=0;i<resultSet.size();i++) {
Document document=resultSet.get(i);
size=document.keySet().size();
if(size>max) {
max=size;
doc=document;
}
}
Iterator<String> itr = doc.keySet().iterator();
while(itr.hasNext()){
String col=itr.next().toString();
if(!col.equals("_id")) {
cls.add(col);
TableHeader th=new TableHeader(col, col);
cols.add(th);
}
}
for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
for(int j=0;j<cls.size();j++) {
if(docs.containsKey(cls.get(j))) {	
if(docs.get(cls.get(j)).toString().length()>0 && docs.get(cls.get(j)).toString()!=null) {
colval.put(cls.get(j), docs.get(cls.get(j)));	
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

JSONArray jsonArrayFinal=new JSONArray();
JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
closeConnection(mongo);
String output=jsonArrayFinal.toString();
return output;	
}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return null;
}



/*
public String getTableSpecificColsValsGeneric(String tableName,String columns) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableSpecificValsGeneric ****************");
}
Properties config=getProperties();
MongoDatabase database = getConnection(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);

ArrayList<String>colms=new ArrayList<String>();
Map<String, Object> groupMap = new HashMap<String, Object>();
String columns_spls[]=columns.split(",");
for(String colm:columns_spls) {
colms.add(colm);	
groupMap.put(colm, "$"+colm);
}

DBObject groupFields = new BasicDBObject(groupMap);

ArrayList<Document> resultSet = collection.aggregate(Arrays.asList( group(groupFields),project(fields
		(include(colms))))).into(new ArrayList<Document>());

int size=0;
int max=0;
Document doc=null;
for(int i=0;i<resultSet.size();i++) {
Document document=resultSet.get(i);
size=document.keySet().size();
if(size>max) {
max=size;
doc=document;
}
}
ArrayList<String>cls=new ArrayList<String>();
ArrayList<TableHeader>cols=new ArrayList<TableHeader>();
JSONArray vals=new JSONArray();
Iterator<String> itr = doc.keySet().iterator();
while(itr.hasNext()){
String col=itr.next().toString();
if(!col.equals("_id")) {
cls.add(col);
TableHeader th=new TableHeader(col, col);
cols.add(th);
}
}
for(Document docs:resultSet) {
JSONObject colval=new JSONObject();
for(int j=0;j<cls.size();j++) {
if(docs.get(cls.get(j)).toString().length()>0) {
colval.put(cls.get(j), docs.get(cls.get(j)));	
}
else {
colval.put(cls.get(j),"-");	
}
}
if(!vals.similar(colval)) {
vals.put(colval);	
}	
}

JSONArray jsonArrayFinal=new JSONArray();
JSONObject jsonObjectColVal=new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
closeConnection();

return jsonArrayFinal.toString();
}
*/

public int postDataGeneric(String data) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into postDataGeneric ****************");	
}	

System.out.println(data);
try {
String spls[]=data.split(";");	
String tn=spls[0].trim();
String tableName=tn.substring(tn.indexOf("=")+1).trim();
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
Document document=new Document();
for(String cv:spls) {
if(!cv.toUpperCase().contains("TABLENAME")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim();
document.append(col, val);	
}
}
collection.insertOne(document);
closeConnection(mongo);
return 1;	
}catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 0;	
}

public int updateDataGeneric(String data) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into updateDataGeneric ****************");	
}	
try {
String tableName="";
ArrayList<Bson> fltr=new ArrayList<Bson>();
ArrayList<Bson> updt=new ArrayList<Bson>();
Bson condition=null;
Bson updates=null;

String spls[]=data.split(";");	
for(String splt:spls) {
if(splt.contains("tablename")){
tableName=splt.substring(splt.indexOf("=")+1).trim();	
}
if(splt.contains("condition")){	
String conds=splt.substring(splt.indexOf("=")+1);	
String cond[]=conds.split("#@#");
for(String con:cond) {
String cl=con.split("@#@")[0].trim();
String vl=con.split("@#@")[1].trim();
fltr.add(eq(cl,vl));	
}
}
if(splt.contains("updates")){	
String updts=splt.substring(splt.indexOf("=")+1);	
String upd[]=updts.split("#@#");
for(String up:upd) {
String cl=up.split("@#@")[0].trim();
String vl=up.split("@#@")[1].trim();
updt.add(Updates.set(cl,vl));	
}
}

}
	
condition=and(fltr);
updates=and(updt);

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
UpdateResult updateResult = collection.updateOne(condition,Updates.combine(updates));
int result=(int)updateResult.getModifiedCount();
closeConnection(mongo);
return result;	
}catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 0;
}


public int deleteDataGeneric(String data) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into updateDataGeneric ****************");	
}	
try {
String tableName="";
ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson condition=null;

String spls[]=data.split(";");	
for(String splt:spls) {
if(splt.contains("tablename")){
tableName=splt.substring(splt.indexOf("=")+1).trim();	
}
if(splt.contains("condition")){	
String conds=splt.substring(splt.indexOf("=")+1);	
String cond[]=conds.split("#@#");
for(String con:cond) {
String cl=con.split("@#@")[0].trim();
String vl=con.split("@#@")[1].trim();
fltr.add(eq(cl,vl));	
}
}

}
	
condition=and(fltr);
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
DeleteResult updateResult = collection.deleteOne(condition);
int result=(int)updateResult.getDeletedCount();
closeConnection(mongo);
return result;	
}catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 0;
}


public int uploadExcelDataGeneric(MultipartFile file,String tableName){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into uploadExcelDataGeneric ****************");	
}	

Properties config=getProperties();
String tblName1=tableName;
String tblName=tblName1.substring(tableName.indexOf("=")+1);

Path rootLocation = Paths.get(config.getProperty("server.directory"));
String file_store = file.getOriginalFilename();
File file_check = new File(rootLocation + "/"  + file_store);
try {
if (file_check.exists()) {
Files.delete(rootLocation.resolve(file_store));
Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
read_excel(file_check,tblName);
} 
else {
Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
read_excel(file_check,tblName);
}
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 1;	
}


public int uploadCsvDataMismatchedElements(MultipartFile file){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into uploadCsvDataMismatchedElements ****************");	
}	

Properties config=getProperties();
Path rootLocation = Paths.get(config.getProperty("server.directory"));
String file_store = file.getOriginalFilename();
File file_check = new File(rootLocation + "/"  + file_store);
try {
if (file_check.exists()) {
Files.delete(rootLocation.resolve(file_store));
Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
read_csv(file_check);
} 
else {
Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
read_csv(file_check);
}
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 1;	
}


public void read_csv(File file) {
try {
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection=database.getCollection("topologymappingmicrowave");
collection.deleteMany(and(eq("siteid","#")));
ArrayList<String>cols=new ArrayList<String>();
ArrayList<String>vals=new ArrayList<String>();
BufferedReader br=new BufferedReader(new FileReader(file));
String line="";
while((line=br.readLine())!=null) {
if(line.contains("nename")) {
String spls[]=line.split(",");	
for(String splt:spls) {
if(splt.trim().length()>0) {
cols.add(splt);
}
}
}
else {
String spls[]=line.split(",");	
for(String splt:spls) {
if(splt.trim().length()>0) {
vals.add(splt);
}
}

Document document=new Document();

for(int i=0;i<vals.size();i++) {
document.append(cols.get(i), vals.get(i));
}
collection.insertOne(document);
vals.clear();
}
}
br.close();

closeConnection(mongo);
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
}


public void read_excel(File file,String tableName) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into read_excel ****************");	
}	
try {	
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));

//Reading excel worksheet

InputStream excelFileToRead = new FileInputStream(file);
XSSFWorkbook wb = new XSSFWorkbook(excelFileToRead);	 
for(int i=0;i<wb.getNumberOfSheets();i++){
read_excel(database,wb, i,tableName);
}

excelFileToRead.close();
closeConnection(mongo);	

} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}

}	

public void read_excel(MongoDatabase database,XSSFWorkbook wb,int sheet,String tableName) {
//Read excel sheets with name and create , insert data to database

MongoCollection<Document> collection = database.getCollection(tableName);
	
StringBuilder sbb=new StringBuilder();

ArrayList<String> ar1=new ArrayList<String>();
ArrayList<String> ar2=new ArrayList<String>();
ArrayList<String>col=new ArrayList<String>();
ArrayList<String>val=new ArrayList<String>();
XSSFRow row;
XSSFCell cell = null;
try{
if(log.isDebugEnabled()) {	
log.debug("*************** checked into read_excel ****************");	
}	

XSSFSheet sheet0 = wb.getSheetAt(sheet);
Iterator rows = sheet0.rowIterator();
while (rows.hasNext()) {
row = (XSSFRow) rows.next();
for(int i=0; i<sheet0.getRow(row.getRowNum()).getLastCellNum(); i++)
{
cell = row.getCell(i, row.CREATE_NULL_AS_BLANK);
cell.setCellType(cell.CELL_TYPE_STRING);
cell.removeCellComment();

ar2.add(cell.toString().replace("\n", " ").replace(","," "));

}
ar1.add(ar2.toString());

ar2.clear();
}

for(int i=0;i<ar1.size();i++){
sbb.append(ar1.get(i)+"\n");	
}

String str=sbb.toString();

String spls[]=str.split("\n");

boolean bol =true;

for(String line_read2:spls) {
int len=line_read2.replaceAll("[^,]", "").length();	
if(len>0){
if(line_read2.replace("[","").replace("]","").trim().length()>0){

//-----------------------------Create table---------------------------------------//
if(bol==true) {
bol = false;
String split1[]=line_read2.replace("[", "").replace("]", "").trim().split(",");
for(String strng:split1){
strng = strng.trim();
col.add(strng.replace(" ","_").trim());	
} 
} 
//-----------------------------insert table data---------------------------------------//
else if(bol==false) {

line_read2=line_read2.replace("[", "").replace("]", ""); 
String split21[]=line_read2.replace("[", "").replace("]", "").split(",");
for(int k=0;k<col.size();k++) {
if(split21[k].trim().length()>0) { 
val.add(split21[k].replace(",","&&").trim());  	
}
else {
val.add("");	
}

}

Document document=new Document();

for(int i=0;i<col.size();i++) {
document.append(col.get(i), val.get(i));
}

collection.insertOne(document);

val.clear();
}

}
}	
}

} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
ex.printStackTrace();
}		
}


}
