package org.xplorg.tool.telco360.DAO.implementation;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.xplorg.tool.telco360.DAO.interfaces.TroubleTicketDAO;
import org.xplorg.tool.telco360.config.BaseDAOMongo;
import org.xplorg.tool.telco360.entity.GenericPostBody;
import org.xplorg.tool.telco360.entity.TableHeader;
import org.xplorg.tool.telco360.entity.alarm_getr_setr;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;

@Repository("troubleticketDAO")
public class TroubleTicketDAOImpl extends BaseDAOMongo implements TroubleTicketDAO{

Logger log = LogManager.getLogger(TroubleTicketDAOImpl.class.getName());
	
public String getTableColsValsTroubleTicketGeneric(String tableName) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableColsValsGeneric ****************");
}
try {
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

}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return null;
}

public String getTableColsValsTroubleTicketConditionGeneric(String tableName,String condition) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableColsValsTroubleTicketConditionGeneric ****************");
}
try {
ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(condition.length()>1) {
String cond=condition;	
if(cond.contains("and")){
String cond_spls[]=cond.split("and");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim();
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(cond.contains("AND")){
String cond_spls[]=cond.split("AND");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim();
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(!(cond.contains("AND") && cond.contains("and")) && cond.length()>1){
if(cond.contains("=")) {
String col=cond.split("=")[0].trim();
String val=cond.split("=")[1].trim();
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
}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return null;
}

public String getTableSpecificColsValsTroubleTicketConditionGeneric(String tableName,String columns,String condition) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableSpecificColsValsTroubleTicketConditionGeneric ****************");
}
try {
ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(condition.length()>1) {
String cond=condition;	
if(cond.contains("and")){
String cond_spls[]=cond.split("and");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim();
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(cond.contains("AND")){
String cond_spls[]=cond.split("AND");
for(String cv:cond_spls) {
if(cv.contains("=")) {
String col=cv.substring(0,cv.indexOf("=")).trim();
String val=cv.substring(cv.indexOf("=")+1).trim();
fltr.add(eq(col,val));
}
}
filter=and(fltr);
}
else if(!(cond.contains("AND") && cond.contains("and")) && cond.length()>1){
if(cond.contains("=")) {
String col=cond.split("=")[0].trim();
String val=cond.split("=")[1].trim();
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
resultSet = collection.aggregate(Arrays.asList(match(and(filter)), group(groupFields),project(fields(include(cls))))).into(new ArrayList<Document>());
}
else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))))).into(new ArrayList<Document>());
}
////eq("NODE_NAME",java.util.regex.Pattern.compile("^S.*")
ArrayList<TableHeader> cols=new ArrayList<TableHeader>();
JSONArray vals=new JSONArray();

//String columns_spls[]=columns.split(",");
for(String colm:cls) {
TableHeader th=new TableHeader(colm, colm);	
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
}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return null;
}


public String get_trouble_ticket_column_data(String tableName,String area,String node,String severity,String status) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into get_trouble_ticket_column_data ****************");	
}

Bson filter=null;
if(area.trim().length()>1 && node.trim().length()>1 && severity.trim().length()>1 && status.trim().length()>1) {
//query = "select * from " + table_name + " where area='"+area+"' and ne_name='"+node+"' and severity='"+severity+"' and status='"+status+"'";
filter=and(eq("AREA",area),eq("NE_NAME",node),eq("SEVERITY",severity),eq("STATUS",status));
}
else if(area.trim().length()>1 && node.trim().length()>1 && severity.trim().length()>1) {
//query = "select * from " + table_name + " where area='"+area+"' and ne_name='"+node+"' and severity='"+severity+"'";
filter=and(eq("AREA",area),eq("NE_NAME",node),eq("SEVERITY",severity));
}
else if(area.trim().length()>1 && severity.trim().length()>1 && status.trim().length()>1) {
//query = "select * from " + table_name + " where area='"+area+"' and status='"+status+"' and severity='"+severity+"'";
filter=and(eq("AREA",area),eq("SEVERITY",severity),eq("STATUS",status));
}
else if(node.trim().length()>1 && severity.trim().length()>1 && status.trim().length()>1) {
//query = "select * from " + table_name + " where ne_name='"+node+"' and severity='"+severity+"' and status='"+status+"'";
filter=and(eq("NE_NAME",node),eq("SEVERITY",severity),eq("STATUS",status));
}
else if(area.trim().length()>1 && node.trim().length()>1) {
//query = "select * from " + table_name + " where area='"+area+"' and ne_name='"+node+"'";
filter=and(eq("AREA",area),eq("NE_NAME",node));
}
else if(area.trim().length()>1 && severity.trim().length()>1) {
//query = "select * from " + table_name + " where area='"+area+"' and severity='"+severity+"'";
filter=and(eq("AREA",area),eq("SEVERITY",severity));
}
else if(area.trim().length()>1 && status.trim().length()>1) {
//query = "select * from " + table_name + " where area='"+area+"' and status='"+status+"'";
filter=and(eq("AREA",area),eq("STATUS",status));
}
else if(node.trim().length()>1 && severity.trim().length()>1) {
//query = "select * from " + table_name + " where ne_name='"+node+"' and severity='"+severity+"'";
filter=and(eq("NE_NAME",node),eq("SEVERITY",severity));
}
else if(node.trim().length()>1 && status.trim().length()>1) {
//query = "select * from " + table_name + " where ne_name='"+node+"' and status='"+status+"'";
filter=and(eq("NE_NAME",node),eq("STATUS",status));
}
else if(severity.trim().length()>1 && status.trim().length()>1) {
//query = "select * from " + table_name + " where severity='"+severity+"' and status='"+status+"'";
filter=and(eq("SEVERITY",severity),eq("STATUS",status));
}
else if(area.trim().length()>1) {
//query = "select * from " + table_name + " where area='"+area+"'";
filter=and(eq("AREA",area));
}
else if(node.trim().length()>1) {
//query = "select * from " + table_name + " where ne_name='"+node+"'";
filter=and(eq("NE_NAME",node));
}
else if(severity.trim().length()>1) {
//query = "select * from " + table_name + " where severity='"+severity+"'";
filter=and(eq("SEVERITY",severity));
}
else if(status.trim().length()>1) {
//query = "select * from " + table_name + " where status='"+status+"'";
filter=and(eq("STATUS",status));
}
/*else {
query = "select * from " + table_name;	
}
*/

try {

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<Document> resultSet = collection.find(filter).into(new ArrayList<Document>());
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
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
// System.out.println(query);
return null;
}
/*
public ArrayList < tableHeader > getTableColumnsGeneric() {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableColumnsGeneric ****************");
}
//-------------------------------
//use in AlarmFile, trouble_ticket, kpi-resolution
//-------------------------------
Connection conn = getConnection();
ArrayList < tableHeader > ar = new ArrayList < tableHeader > ();
ResultSet rs = null;
String query = "SELECT Distinct t2.LEVEL1,t2.LEVEL2,t2.LEVEL3,t2.LEVEL4,t1.* FROM trouble_ticket t1,trouble_ticket_level t2 limit 1";

try {
  
Statement stmnt=conn.createStatement();

  rs = stmnt.executeQuery(query);
  int columnCount = rs.getMetaData().getColumnCount();

  // The column count starts from 1
  for (int i = 1; i <= columnCount; i++) {
    String name = rs.getMetaData().getColumnName(i);
    tableHeader f_h = new tableHeader(name, name);

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

public String getTableDataGeneric(String conditions) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableDataGeneric ****************");
}

Connection conn = getConnection();
ArrayList < String > colName = new ArrayList < String > ();
ArrayList < String > ar2 = new ArrayList < String > ();
ArrayList < String > ar3 = new ArrayList < String > ();


ResultSet rs = null;
//System.out.println(alarm+">>>"+severity);
String query = "";

//if(conditions.trim().length()<2) {
//query="SELECT Distinct t2.LEVEL1,t2.LEVEL2,t2.LEVEL3,t2.LEVEL4,t1.* FROM trouble_ticket t1,trouble_ticket_level t2 limit 500";	
//}

//if(conditions.trim().length()>1) {
query="SELECT Distinct t2.LEVEL1,t2.LEVEL2,t2.LEVEL3,t2.LEVEL4,t1.* FROM trouble_ticket t1,trouble_ticket_level t2 where t1.TICKET_ID=t2.TICKET_ID limit 200";	
//}
//System.out.println("query============"+query);
try {
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
  s += "\"" + colName.get(a).trim() + "\"" + ":" + "\"" + rs.getString(a + 1).replace("\"","").replace("\"","").replace("/","@@").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","").trim() + "\"" + ",";
    }
    String ss = s.substring(0, s.length() - 1);
    ar2.add(ss.trim());
  }

  for (int i = 0; i < ar2.size(); i++) {
    ar3.add("{" + ar2.get(i).toString().trim() + "}");
  }
  stmnt.close();
  closeConnection(conn);
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return ar3.toString();
}

*/

public int Email(GenericPostBody genericPostBody) {
String area=genericPostBody.getEmailArea();	
String subject=genericPostBody.getEmailSubject();	
String content=genericPostBody.getEmailText();	

//new mailmongo().mail(area, subject, content);	
	
return 1;
}
	
	
public int TroubleTicket(GenericPostBody genericPostBody) {
int result=0;	
try {
	
Properties config=getProperties();	
String link=config.getProperty("link.address.trouble_ticket");

String tableName="";

Date date = new Date();
SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
Random random = new Random();
String troubleTicketId=formatter1.format(date) + String.valueOf(random.nextInt(999));

String troubleTicketUserId=genericPostBody.getTroubleTicketUserId();
//String troubleTicketId=genericPostBody.getTroubleTicketId();
String troubleTicketArea=genericPostBody.getTroubleTicketArea();	
String troubleTicketDomain=genericPostBody.getTroubleTicketDomain();	
String troubleTicketPriority=genericPostBody.getTroubleTicketPriority();	
String troubleTicketComments=genericPostBody.getTroubleTicketComments();	
String troubleTicketOpeningDateTime=genericPostBody.getTroubleTicketOpeningDateTime();	
String troubleTicketStatus=genericPostBody.getTroubleTicketStatus();	
String troubleTicketVendor=genericPostBody.getTroubleTicketVendor();	

String troubleTicketRegion="",troubleTicketElementType="",troubleTicketElementName="";

String columns="";
String where="";
if(troubleTicketVendor.toUpperCase().equals("NOKIA")) {
tableName="nokia_alarms";
columns="ELEMENT_TYPE,ELEMENT_NAME";
where="ALARM_ID";
}
if(troubleTicketVendor.toUpperCase().equals("ERICSSON")){
tableName="ericsson_alarms";	
columns="ELEMENT_TYPE,ELEMENT_NAME";
where="ALARM_ID";
}
if(troubleTicketVendor.toUpperCase().equals("ZTE") && (troubleTicketDomain.toUpperCase().equals("CS CORE"))){
tableName="zte_cscore_alarms";	
columns="ALARMMOCOBJECTINSTANCE";
where="AID";
}
if(troubleTicketVendor.toUpperCase().equals("ZTE") && (troubleTicketDomain.toUpperCase().equals("PS CORE"))){
tableName="zte_pscore_alarms";	
columns="ALARMMOCOBJECTINSTANCE";
where="AID";
}
if(troubleTicketVendor.toUpperCase().equals("ZTE") && (troubleTicketDomain.toUpperCase().equals("MPBN"))){
tableName="zte_mpbn_alarms";	
columns="ALARMMOCOBJECTINSTANCE";
where="AID";
}
if(troubleTicketVendor.toUpperCase().equals("HUAWEI") && (troubleTicketDomain.toUpperCase().equals("IPRAN"))){
tableName="huawei_ipran_alarms";	
columns="HWNMNORTHBOUNDNENAME,HWNMNORTHBOUNDDEVICETYPE";
where="HWNMNORTHBOUNDFAULTID";
}


String alarmDetails[]=troubleTicketComments.toUpperCase().split(",");

String alarmId=alarmDetails[0].substring(alarmDetails[0].indexOf("NEW ALARM:")+10);
//String alarmDate=alarmDetails[1].substring(alarmDetails[1].indexOf("ALARM DATE:")+11);
//String alarmTime=alarmDetails[2].substring(alarmDetails[2].indexOf("ALARM TIME:")+11);
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("trouble_ticket");
ArrayList<Document> resultSet = collection.find(and(eq("DESCRIPTION",troubleTicketComments))).into(new ArrayList<Document>());
JSONArray jsonArray = new JSONArray(JSON.serialize(resultSet));

if(jsonArray.length()==0) {
MongoCollection<Document> collectionSpecific = database.getCollection(tableName);

ArrayList<String>colms=new ArrayList<String>();
Map<String, Object> groupMap = new HashMap<String, Object>();
String columns_spls[]=columns.split(",");
for(String colm:columns_spls) {
colms.add(colm);	
groupMap.put(colm, "$"+colm);
}

DBObject groupFields = new BasicDBObject(groupMap);

ArrayList<Document> groupDistinct = collectionSpecific.aggregate(Arrays.asList(match(and(eq(where,alarmId))), group(groupFields),project(fields
		(include(colms))))).into(new ArrayList<Document>());
JSONArray jsonArraySpecific = new JSONArray(JSON.serialize(groupDistinct));
for(int i=0;i<jsonArraySpecific.length();i++) {
JSONObject jsonObject = jsonArraySpecific.getJSONObject(i);
JSONObject jsonObject_id = jsonObject.getJSONObject("_id");

for(int j=0;j<colms.size();j++) {
if(troubleTicketVendor.toUpperCase().equals("ZTE")){
troubleTicketElementType=jsonObject_id.getString("ALARMMOCOBJECTINSTANCE");
troubleTicketElementName=jsonObject_id.getString("ALARMMOCOBJECTINSTANCE");
}
else if(troubleTicketVendor.toUpperCase().equals("NOKIA") || troubleTicketVendor.toUpperCase().equals("ERICSSON")){
troubleTicketElementType=jsonObject_id.getString("ELEMENT_TYPE");
troubleTicketElementName=jsonObject_id.getString("ELEMENT_NAME");	
}
else if(troubleTicketVendor.toUpperCase().equals("HUAWEI")){
troubleTicketElementType=jsonObject_id.getString("HWNMNORTHBOUNDDEVICETYPE");
troubleTicketElementName=jsonObject_id.getString("HWNMNORTHBOUNDNENAME");	
}
	
}
}

MongoCollection<Document> collectionSpecificRegion = database.getCollection("sitedb_2g_zm");

ArrayList<String>colmsRegion=new ArrayList<String>();
Map<String, Object> groupMapRegion = new HashMap<String, Object>();
colmsRegion.add("REGION_NAME");	
groupMapRegion.put("REGION_NAME", "$REGION_NAME");

DBObject groupFieldsRegion = new BasicDBObject(groupMapRegion);

ArrayList<Document> groupDistinctRegion = collectionSpecificRegion.aggregate(Arrays.asList(match(and(eq("SITE_ID",troubleTicketElementName))), group(groupFieldsRegion),project(fields
		(include(colmsRegion))))).into(new ArrayList<Document>());
JSONArray jsonArraySpecificRegion = new JSONArray(JSON.serialize(groupDistinctRegion));
for(int i=0;i<jsonArraySpecificRegion.length();i++) {
JSONObject jsonObject = jsonArraySpecificRegion.getJSONObject(i);
JSONObject jsonObject_id = jsonObject.getJSONObject("_id");

for(int j=0;j<colmsRegion.size();j++) {
troubleTicketRegion=jsonObject_id.getString("REGION_NAME");	
}
}

String opdt[]=troubleTicketOpeningDateTime.split("\\s+");

Document document = new Document("USER_ID", troubleTicketUserId).append("TICKET_ID",troubleTicketId).append("AREA", troubleTicketArea).append("REGION", troubleTicketRegion)
.append("DOMAIN",troubleTicketDomain).append("NE_TYPE",troubleTicketElementType).append("NE_NAME",troubleTicketElementName).append("SEVERITY",troubleTicketPriority).append("DESCRIPTION",troubleTicketComments)
.append("OPEN_DATE",opdt[0]).append("OPEN_TIME",opdt[1]).append("CLOSE_DATE","-").append("CLOSE_TIME","-")
.append("STATUS",troubleTicketStatus).append("RESOLUTION","-").append("RESOLUTION_TIME","-");
collection.insertOne(document);
result=1;
closeConnection(mongo);


StringBuilder sb_mail=new StringBuilder();

sb_mail.append("Trouble Ticket Generated with Ticket No. "+troubleTicketId+"\n"+"\n");
//sb_mail.append("L2 Support:--sahil@xplor-g.com"+"\n");
//sb_mail.append("L3 Support:--subhash@xplor-g.com"+"\n");
sb_mail.append("Country:-ZAMBIA"+"\n");
//sb_mail.append("Severity:-"+troubleTicketPriority+"\n");
sb_mail.append("Domain:-"+troubleTicketDomain+"\n");
sb_mail.append("Problem Statement:-"+troubleTicketComments+"\n");
sb_mail.append("****************************************************************************"+"\n");
sb_mail.append("Go to the following Link to check the status:-"+"\n");
//sb_mail.append("http://localhost:4200/TroubleTicketPortal/"+troubleTicketUserId.trim()+"\n\n");
sb_mail.append("http://"+link+"/TroubleTicketPortal/"+troubleTicketUserId.trim()+"\n\n");


StringBuilder sb_whatsapp=new StringBuilder();

sb_whatsapp.append("Trouble Ticket Generated with Ticket No. "+troubleTicketId+"\n"+"\n");
//sb_whatsapp.append("L2 Support:--sahil@xplor-g.com"+"\n");
//sb_whatsapp.append("L3 Support:--subhash@xplor-g.com"+"\n");

sb_whatsapp.append("Country:-ZAMBIA"+"\n");
//sb_mail.append("Severity:-"+troubleTicketPriority+"\n");
sb_whatsapp.append("Domain:-"+troubleTicketDomain+"\n");
sb_whatsapp.append("Problem Statement:-"+troubleTicketComments+"\n");
sb_whatsapp.append("****************************************************************************"+"\n");

sb_whatsapp.append("Go to the following Link to check the status:-"+"\n");
//sb_whatsapp.append("http:\\localhost:4200\\TroubleTicketPortal\\"+troubleTicketUserId.trim()+"\n\n");
sb_whatsapp.append("http:\\"+link+"\\TroubleTicketPortal\\"+troubleTicketUserId.trim()+"\n\n");

//new mailmongo().mail("FM", "Trouble Ticket Generated with Ticket No. "+genericPostBody.getTroubleTicketId(), sb_mail.toString());

//new whatsapp().send_whatsapp("91","9888652531", sb_whatsapp.toString());
//new whatsapp().send_whatsapp("254","732291441", sb_whatsapp.toString());


result= 1;
}

else{
result=0;	
}

}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
ex.printStackTrace();
}
return result;
}

public int ManualTroubleTicket(GenericPostBody genericPostBody) {
int result=0;	
try {
	
Properties config=getProperties();	
String link=config.getProperty("link.address.trouble_ticket");

Date date = new Date();
SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
Random random = new Random();
String troubleTicketId=formatter1.format(date) + String.valueOf(random.nextInt(999));

String troubleTicketUserId=genericPostBody.getTroubleTicketUserId();
//String troubleTicketId=genericPostBody.getTroubleTicketId();
String troubleTicketArea=genericPostBody.getTroubleTicketArea();	
String troubleTicketDomain=genericPostBody.getTroubleTicketDomain();	
String troubleTicketPriority=genericPostBody.getTroubleTicketPriority();	
String troubleTicketComments=genericPostBody.getTroubleTicketComments();	
String troubleTicketOpeningDateTime=genericPostBody.getTroubleTicketOpeningDateTime();	
String troubleTicketStatus=genericPostBody.getTroubleTicketStatus();	
String troubleTicketVendor=genericPostBody.getTroubleTicketVendor();	
String troubleTicketRegion=genericPostBody.getTroubleTicketRegion();
String troubleTicketElementType=genericPostBody.getTroubleTicketNode();
String troubleTicketElementName=genericPostBody.getTroubleTicketNodeName();

String opdt[]=troubleTicketOpeningDateTime.split("\\s+");

MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("trouble_ticket");
Document document = new Document("USER_ID", troubleTicketUserId).append("TICKET_ID",troubleTicketId).append("AREA", troubleTicketArea).append("REGION", troubleTicketRegion)
.append("DOMAIN",troubleTicketDomain).append("NE_TYPE",troubleTicketElementType).append("NE_NAME",troubleTicketElementName).append("SEVERITY",troubleTicketPriority).append("DESCRIPTION",troubleTicketComments)
.append("OPEN_DATE",opdt[0]).append("OPEN_TIME",opdt[1]).append("CLOSE_DATE","-").append("CLOSE_TIME","-")
.append("STATUS",troubleTicketStatus).append("RESOLUTION","-").append("RESOLUTION_TIME","-");
collection.insertOne(document);
result=1;
closeConnection(mongo);

StringBuilder sb_mail=new StringBuilder();

sb_mail.append("Trouble Ticket Generated with Ticket No. "+troubleTicketId+"\n"+"\n");
//sb_mail.append("L2 Support:--sahil@xplor-g.com"+"\n");
//sb_mail.append("L3 Support:--subhash@xplor-g.com"+"\n");

sb_mail.append("Country:-ZAMBIA"+"\n");
sb_mail.append("Domain:-"+troubleTicketDomain+"\n");

//sb_mail.append("Severity:-"+troubleTicketPriority+"\n");
sb_mail.append("Problem Statement:-"+troubleTicketComments+"\n");

sb_mail.append("****************************************************************************"+"\n");

sb_mail.append("Go to the following Link to check the status:-"+"\n");
//sb_mail.append("http://localhost:4200/TroubleTicketPortal/"+troubleTicketUserId.trim()+"\n\n");
sb_mail.append("http://"+link+"/TroubleTicketPortal/"+troubleTicketUserId.trim()+"\n\n");


StringBuilder sb_whatsapp=new StringBuilder();

sb_whatsapp.append("Trouble Ticket Generated with Ticket No. "+troubleTicketId+"\n"+"\n");
//sb_whatsapp.append("L2 Support:--sahil@xplor-g.com"+"\n");
//sb_whatsapp.append("L3 Support:--subhash@xplor-g.com"+"\n");

sb_whatsapp.append("Country:-ZAMBIA"+"\n");
sb_whatsapp.append("Domain:-"+troubleTicketDomain+"\n");
//sb_mail.append("Severity:-"+troubleTicketPriority+"\n");
sb_whatsapp.append("Problem Statement:-"+troubleTicketComments+"\n");

sb_whatsapp.append("****************************************************************************"+"\n");
sb_whatsapp.append("Go to the following Link to check the status:-"+"\n");
//sb_whatsapp.append("http:\\localhost:4200\\TroubleTicketPortal\\"+troubleTicketUserId.trim()+"\n\n");
sb_whatsapp.append("http:\\"+link+"\\TroubleTicketPortal\\"+troubleTicketUserId.trim()+"\n\n");



//new mailmongo().mail("FM", "Trouble Ticket Generated with Ticket No. "+genericPostBody.getTroubleTicketId(), sb_mail.toString());

//new whatsapp().send_whatsapp("91","9888652531", sb_whatsapp.toString());
//new whatsapp().send_whatsapp("254","732291441", sb_whatsapp.toString());

return result;

}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 0;
}




public int TroubleTicketGeneration(GenericPostBody genericPostBody) {
int result=0;	
try {
String tableName="";

Properties config=getProperties();	
String link=config.getProperty("link.address.trouble_ticket");

Date date = new Date();
SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

Random random = new Random();

String troubleTicketId=formatter1.format(date) + String.valueOf(random.nextInt(999));

String troubleTicketUserId=genericPostBody.getTroubleTicketUserId();
//String troubleTicketId=formatter1.format(date);
String troubleTicketArea=genericPostBody.getTroubleTicketArea();	
String troubleTicketDomain=genericPostBody.getTroubleTicketDomain();	
String troubleTicketPriority=genericPostBody.getTroubleTicketPriority();	
String troubleTicketComments=genericPostBody.getTroubleTicketComments();	
String troubleTicketOpeningDateTime=formatter2.format(date);	
String troubleTicketStatus="OPEN";	
String troubleTicketVendor=genericPostBody.getTroubleTicketVendor();	

String troubleTicketRegion="",troubleTicketElementType="",troubleTicketElementName="";

String where="",where_date="",where_code="";
String aid="",dat="",tim="",type="",name="",severity="",cause="";

if(troubleTicketVendor.toUpperCase().equals("NOKIA")) {
tableName="nokia_alarms";
where="ALARM_ID";
where_date="EVENT_DATE";
where_code="SPECIFIC_PROBLEM";
aid="ALARM_ID";
dat="EVENT_DATE";
tim="EVENT_TIME";
type="ELEMENT_TYPE";
name="ELEMENT_NAME";
severity="PERCEIVED_SEVERITY";
cause="PROBABLE_CAUSE";
troubleTicketRegion="-";
}
if(troubleTicketVendor.toUpperCase().equals("ERICSSON")){
tableName="ericsson_alarms";	
where="ALARM_ID";
where_date="EVENT_DATE";
where_code="SPECIFIC_PROBLEM";
aid="ALARM_ID";
dat="EVENT_DATE";
tim="EVENT_TIME";
type="ELEMENT_TYPE";
name="ELEMENT_NAME";
severity="PERCEIVED_SEVERITY";
cause="PROBABLE_CAUSE";
troubleTicketRegion="-";
}
if(troubleTicketVendor.toUpperCase().equals("ZTE") && (troubleTicketDomain.toUpperCase().equals("CS CORE"))){
tableName="zte_cscore_alarms";	
where="AID";
where_date="ALARMEVENTDATE";
where_code="ALARMCODE";
troubleTicketRegion="LUSAKA";
aid="AID";
dat="ALARMEVENTDATE";
tim="ALARMEVENTTIME";
type="ALARMMOCOBJECTINSTANCE";
name="ALARMMOCOBJECTINSTANCE";
severity="ALARMPERCEIVEDSEVERITY";
cause="ALARMPROBABLECAUSE";
}
if(troubleTicketVendor.toUpperCase().equals("ZTE") && (troubleTicketDomain.toUpperCase().equals("PS CORE"))){
tableName="zte_pscore_alarms";	
where="AID";
where_date="ALARMEVENTDATE";
where_code="ALARMCODE";
troubleTicketRegion="LUSAKA";
aid="AID";
dat="ALARMEVENTDATE";
tim="ALARMEVENTTIME";
type="ALARMMOCOBJECTINSTANCE";
name="ALARMMOCOBJECTINSTANCE";
severity="ALARMPERCEIVEDSEVERITY";
cause="ALARMPROBABLECAUSE";
}
if(troubleTicketVendor.toUpperCase().equals("ZTE") && (troubleTicketDomain.toUpperCase().equals("MPBN"))){
tableName="zte_mpbn_alarms";	
where="AID";
where_date="ALARMEVENTDATE";
where_code="ALARMCODE";
troubleTicketRegion="LUSAKA";
aid="AID";
dat="ALARMEVENTDATE";
tim="ALARMEVENTTIME";
type="ALARMMOCOBJECTINSTANCE";
name="ALARMMOCOBJECTINSTANCE";
severity="ALARMPERCEIVEDSEVERITY";
cause="ALARMPROBABLECAUSE";
}
if(troubleTicketVendor.toUpperCase().equals("HUAWEI") && (troubleTicketDomain.toUpperCase().equals("IPRAN"))){
tableName="huawei_ipran_alarms";	
where="HWNMNORTHBOUNDFAULTID";
where_date="HWNMNORTHBOUNDEVENTDATE";
where_code="HWNMNORTHBOUNDFAULTID";
troubleTicketRegion="-";
aid="HWNMNORTHBOUNDFAULTID";
dat="HWNMNORTHBOUNDEVENTDATE";
tim="HWNMNORTHBOUNDEVENTTIME";
type="HWNMNORTHBOUNDDEVICETYPE";
name="HWNMNORTHBOUNDNENAME";
severity="HWNMNORTHBOUNDSEVERITY";
cause="HWNMNORTHBOUNDEVENTDETAIL";
}

String almid="";
String alarmDetails[]=troubleTicketComments.toUpperCase().split(",");

String alarmCode=alarmDetails[0].substring(alarmDetails[0].indexOf("New Code:")+10);
String alarmDate=alarmDetails[1].substring(alarmDetails[1].indexOf("Alarm Date:")+12);

MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<Document> resultSet = collection.find(and(eq(where_code,alarmCode),eq(where_date,alarmDate))).sort(descending(tim)).limit(1).into(new ArrayList<Document>());
JSONArray jsonArray = new JSONArray(JSON.serialize(resultSet));

if(jsonArray.length()>0) {
for(int i=0;i<jsonArray.length();i++) {	
JSONObject jsonObject=jsonArray.getJSONObject(i);
almid=jsonObject.getString(aid);	
troubleTicketElementType=jsonObject.getString(type);
troubleTicketElementName=jsonObject.getString(name);
troubleTicketPriority=jsonObject.getString(severity);
troubleTicketComments="New Alarm:"+jsonObject.getString(aid).trim()+",Alarm Date:"+jsonObject.getString(dat)+",Alarm Time:"+jsonObject.getString(tim)+",Alarm Severity:"+jsonObject.getString(severity)+",Alarm Details:"+jsonObject.getString(cause);
}	
}

String opdt[]=troubleTicketOpeningDateTime.split("\\s+");

ArrayList<Document> resultSetSpecific = collection.find(and(eq("DESCRIPTION",java.util.regex.Pattern.compile("^%"+almid+".*")),eq("OPEN_DATE",opdt[0]))).sort(descending(tim)).limit(1).into(new ArrayList<Document>());
JSONArray jsonArraySpecific = new JSONArray(JSON.serialize(resultSetSpecific));

if(jsonArraySpecific.length()==0) {

//MongoCollection<Document> collection = database.getCollection("trouble_ticket");
Document document = new Document("USER_ID", troubleTicketUserId).append("TICKET_ID",troubleTicketId).append("AREA", troubleTicketArea).append("REGION", troubleTicketRegion)
.append("DOMAIN",troubleTicketDomain).append("NE_TYPE",troubleTicketElementType).append("NE_NAME",troubleTicketElementName).append("SEVERITY",troubleTicketPriority).append("DESCRIPTION",troubleTicketComments)
.append("OPEN_DATE",opdt[0]).append("OPEN_TIME",opdt[1]).append("CLOSE_DATE","-").append("CLOSE_TIME","-")
.append("STATUS",troubleTicketStatus).append("RESOLUTION","-").append("RESOLUTION_TIME","-");
collection.insertOne(document);
result=1;
closeConnection(mongo);

StringBuilder sb_mail=new StringBuilder();

sb_mail.append("Trouble Ticket Generated with Ticket No. "+troubleTicketId+"\n"+"\n");
//sb_mail.append("L2 Support:--sahil@xplor-g.com"+"\n");
//sb_mail.append("L3 Support:--subhash@xplor-g.com"+"\n");

StringBuilder sb_whatsapp=new StringBuilder();

sb_whatsapp.append("Trouble Ticket Generated with Ticket No. "+troubleTicketId+"\n"+"\n");
//sb_whatsapp.append("L2 Support:--sahil@xplor-g.com"+"\n");
//sb_whatsapp.append("L3 Support:--subhash@xplor-g.com"+"\n");

sb_mail.append("Country:-ZAMBIA"+"\n");
sb_mail.append("Domain:-"+troubleTicketDomain+"\n");

//sb_mail.append("Severity:-"+troubleTicketPriority+"\n");
sb_mail.append("Problem Statement:-"+troubleTicketComments+"\n");

sb_mail.append("****************************************************************************"+"\n");

sb_mail.append("Go to the following Link to check the status:-"+"\n");
//sb_mail.append("http://localhost:4200/TroubleTicketPortal/"+troubleTicketUserId.trim()+"\n\n");
sb_mail.append("http://"+link+"/TroubleTicketPortal/"+troubleTicketUserId.trim()+"\n\n");

sb_whatsapp.append("Country:-ZAMBIA"+"\n");
sb_whatsapp.append("Domain:-"+troubleTicketDomain+"\n");

//sb_mail.append("Severity:-"+troubleTicketPriority+"\n");
sb_whatsapp.append("Problem Statement:-"+troubleTicketComments+"\n");

sb_whatsapp.append("****************************************************************************"+"\n");

sb_whatsapp.append("Go to the following Link to check the status:-"+"\n");
//sb_whatsapp.append("http:\\localhost:4200\\TroubleTicketPortal\\"+troubleTicketUserId.trim()+"\n\n");
sb_whatsapp.append("http:\\"+link+"\\TroubleTicketPortal\\"+troubleTicketUserId.trim()+"\n\n");

//new mailmongo().mail("FM", "Trouble Ticket Generated with Ticket No. "+troubleTicketId, sb_mail.toString());

//new whatsapp().send_whatsapp("91","9888652531", sb_whatsapp.toString());
//new whatsapp().send_whatsapp("254","732291441", sb_whatsapp.toString());

}

}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return result;
}


public int TroubleTicketGenerationKpi(GenericPostBody genericPostBody) {
int result=0;	
try {
Properties config=getProperties();	
String link=config.getProperty("link.address.trouble_ticket");

Date date = new Date();
SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Random random = new Random();

String troubleTicketId=formatter1.format(date) + String.valueOf(random.nextInt(999));
//System.out.println("troubleTicketId==================="+troubleTicketId);
String troubleTicketUserId=genericPostBody.getTroubleTicketUserId();
String troubleTicketArea=genericPostBody.getTroubleTicketArea();	
String troubleTicketDomain=genericPostBody.getTroubleTicketDomain();	
String troubleTicketPriority=genericPostBody.getTroubleTicketPriority();	
String troubleTicketComments=genericPostBody.getTroubleTicketComments();	
String troubleTicketOpeningDateTime=formatter2.format(date);	
String troubleTicketStatus="OPEN";	
String troubleTicketVendor=genericPostBody.getTroubleTicketVendor();	
String troubleTicketRegion=genericPostBody.getTroubleTicketRegion();
String troubleTicketElementType=genericPostBody.getTroubleTicketNode();
String troubleTicketElementName=genericPostBody.getTroubleTicketNodeName();

//troubleTicketElementType=res1.getString(type);
//troubleTicketElementName=res1.getString(name);
//troubleTicketPriority=res1.getString(severity);
//troubleTicketComments="New Alarm:"+res1.getString(aid).trim()+",Alarm Date:"+res1.getString(dat)+",Alarm Time:"+res1.getString(tim)+",Alarm Severity:"+res1.getString(severity)+",Alarm Details:"+res1.getString(cause);
String opdt[]=troubleTicketOpeningDateTime.split("\\s+");

MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("trouble_ticket");
Document document = new Document("USER_ID", troubleTicketUserId).append("TICKET_ID",troubleTicketId).append("AREA", troubleTicketArea).append("REGION", troubleTicketRegion)
.append("DOMAIN",troubleTicketDomain).append("NE_TYPE",troubleTicketElementType).append("NE_NAME",troubleTicketElementName).append("SEVERITY",troubleTicketPriority).append("DESCRIPTION",troubleTicketComments)
.append("OPEN_DATE",opdt[0]).append("OPEN_TIME",opdt[1]).append("CLOSE_DATE","-").append("CLOSE_TIME","-")
.append("STATUS",troubleTicketStatus).append("RESOLUTION","-").append("RESOLUTION_TIME","-");
collection.insertOne(document);
result=1;
closeConnection(mongo);

StringBuilder sb_mail=new StringBuilder();

sb_mail.append("Trouble Ticket Generated with Ticket No. "+troubleTicketId+"\n"+"\n");
//sb_mail.append("L2 Support:--sahil@xplor-g.com"+"\n");
//sb_mail.append("L3 Support:--subhash@xplor-g.com"+"\n");

sb_mail.append("Country:-ZAMBIA"+"\n");
sb_mail.append("Domain:-"+troubleTicketDomain+"\n");
sb_mail.append("NE Name:-"+troubleTicketElementName+"\n");

//sb_mail.append("Severity:-"+troubleTicketPriority+"\n");
//sb_mail.append("Problem Statement:-"+troubleTicketComments.replaceAll("New Alarm","SLA Problem").replace("Alarm","SLA").replaceAll(",","\n")+"\n");
//sb_mail.append("SLA Status:-Out Of SLA"+"\n");

String split1=troubleTicketComments.replaceAll("New Alarm","SLA Problem").replace("Alarm","SLA");
//System.out.println("split1============"+split1);
String split[]=split1.split(",");

for(String spls:split) {
	
sb_mail.append(spls+"\n");	
}

sb_mail.append("****************************************************************************"+"\n");

sb_mail.append("Go to the following Link to check the status:-"+"\n");
//sb_mail.append("http://localhost:4200/TroubleTicketPortal/"+troubleTicketUserId.trim()+"\n\n");
sb_mail.append("http://"+link+"/TroubleTicketPortal/"+troubleTicketUserId.trim()+"\n\n");


StringBuilder sb_whatsapp=new StringBuilder();

sb_whatsapp.append("Trouble Ticket Generated with Ticket No. "+troubleTicketId+"\n"+"\n");
//sb_whatsapp.append("L2 Support:--sahil@xplor-g.com"+"\n");
//sb_whatsapp.append("L3 Support:--subhash@xplor-g.com"+"\n");

sb_whatsapp.append("Country:-ZAMBIA"+"\n");
sb_whatsapp.append("Domain:-"+troubleTicketDomain+"\n");
sb_whatsapp.append("NE Name:-"+troubleTicketElementName+"\n");
//sb_mail.append("Severity:-"+troubleTicketPriority+"\n");
sb_whatsapp.append("Problem Statement:-"+troubleTicketComments.replaceAll("New Alarm","SLA Problem").replace("Alarm","SLA").replaceAll(",","\n")+"\n");

sb_whatsapp.append("****************************************************************************"+"\n");

sb_whatsapp.append("Go to the following Link to check the status:-"+"\n");
//sb_whatsapp.append("http:\\localhost:4200\\TroubleTicketPortal\\"+troubleTicketUserId.trim()+"\n\n");
sb_whatsapp.append("http:\\"+link+"\\TroubleTicketPortal\\"+troubleTicketUserId.trim()+"\n\n");


//System.out.println("sb_mail_before================="+sb_mail.toString());

//new mailmongo().mail("PM", "Trouble Ticket Generated with Ticket No. "+troubleTicketId, sb_mail.toString());

//new whatsapp().send_whatsapp("91","9888652531", sb_whatsapp.toString());
//new whatsapp().send_whatsapp("254","732291441", sb_whatsapp.toString());

return result;
}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 0;
}



public List<alarm_getr_setr> troubleTicketEndUser(){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into troubleTicketEndUser ****************");	
}	

try {
String tableName="trouble_ticket";
ArrayList<alarm_getr_setr> output =new ArrayList<alarm_getr_setr>();

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<Document> resultSet = collection.find().sort(ascending("OPEN_DATE")).into(new ArrayList<Document>());
JSONArray jsonArray = new JSONArray(JSON.serialize(resultSet));
for(int i=0;i<jsonArray.length();i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
String details=jsonObject1.optString("DESCRIPTION");
String alarmId=details.split(",")[0].substring(details.split(",")[0].indexOf("New Alarm:")+10).trim();
String alarmDate=details.split(",")[1].substring(details.split(",")[1].indexOf("Alarm Date:")+11).trim();
String alarmTime=details.split(",")[2].substring(details.split(",")[2].indexOf("Alarm Time:")+11).trim();
String alarmSeverity=details.split(",")[3].substring(details.split(",")[3].indexOf("Alarm Severity:")+15).trim();
String alarmDetails=details.split(",")[4].substring(details.split(",")[4].indexOf("Alarm Details:")+14).trim();
String resolutionTime=jsonObject1.optString("RESOLUTION_TIME");
String resolution=jsonObject1.optString("RESOLUTION");
String status=jsonObject1.optString("STATUS");
String ticketno=jsonObject1.optString("TICKET_ID");
alarm_getr_setr data=new alarm_getr_setr(ticketno,alarmId,alarmDate,alarmTime,alarmSeverity,alarmDetails,resolutionTime,resolution,status);
output.add(data);
}
		
//StringBuilder sb_mail=new StringBuilder();

//sb_mail.append("Trouble Ticket Generated with Ticket No. "+ticketno+"\n"+"\n");
//sb_mail.append("L2 Support:--sahil@xplor-g.com"+"\n");
//sb_mail.append("L3 Support:--subhash@xplor-g.com"+"\n");
//sb_mail.append("Go to the following Link to check the status:-"+"\n");
//sb_mail.append("http://localhost:4200/trouble_ticket/0/"+ticketno+"\n\n");
/*if(check.equals("1")) {
try {
new mail().mail(conn_specific, "AM", sb_mail.toString());
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
}*/

closeConnection(mongo);
return output;
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return null;
}


public int postTroubleTicketEndUser(GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into postTroubleTicketEndUser ****************");	
}	
int result=0;
try {
String cldt[]=genericPostBody.getTroubleTicketClosingDateTime().split("\\s+");
	
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("trouble_ticket");
UpdateResult updateResult = collection.updateOne(and(eq("TICKET_ID",genericPostBody.getTroubleTicketId()),eq("DESCRIPTION",genericPostBody.getTroubleTicketComments())),Updates.combine(Updates.set("CLOSE_DATE", cldt[0]),Updates.set("CLOSE_TIME",cldt[1])
		,Updates.set("RESOLUTION_TIME",genericPostBody.getTroubleTicketResolutionTime()),Updates.set("RESOLUTION",genericPostBody.getTroubleTicketRCA()),Updates.set("STATUS",genericPostBody.getTroubleTicketStatus())));
result=(int) updateResult.getModifiedCount();
closeConnection(mongo);	
StringBuilder sb_mail=new StringBuilder();
String link=config.getProperty("link.address.trouble_ticket");
sb_mail.append("Trouble Ticket Resolved with Ticket No. "+genericPostBody.getTroubleTicketId()+"\n"+"\n");
//sb_mail.append("L2 Support:--sahil@xplor-g.com"+"\n");
//sb_mail.append("L3 Support:--subhash@xplor-g.com"+"\n");

sb_mail.append("****************************************************************************"+"\n");

sb_mail.append("Go to the following Link to check the status:-"+"\n");
//sb_mail.append("http://localhost:4200/TroubleTicketPortal/"+genericPostBody.getTroubleTicketUserId().trim()+"\n\n");
sb_mail.append("http://"+link+"/TroubleTicketPortal/"+genericPostBody.getTroubleTicketUserId().trim()+"\n\n");

//new mailmongo().mail("FM", "Trouble Ticket Resolved with Ticket No. "+genericPostBody.getTroubleTicketId(), sb_mail.toString());

StringBuilder sb_whatsapp=new StringBuilder();

sb_whatsapp.append("Trouble Ticket Resolved with Ticket No. "+genericPostBody.getTroubleTicketId()+"\n"+"\n");
//sb_whatsapp.append("L2 Support:--sahil@xplor-g.com"+"\n");
//sb_whatsapp.append("L3 Support:--subhash@xplor-g.com"+"\n");

sb_whatsapp.append("Country:-ZAMBIA"+"\n");
//sb_whatsapp.append("Severity:-"+troubleTicketPriority+"\n");
//sb_whatsapp.append("Problem Statement:-"+troubleTicketComments.replaceAll("New Alarm","SLA Problem")+"\n");

sb_whatsapp.append("****************************************************************************"+"\n");

sb_whatsapp.append("Go to the following Link to check the status:-"+"\n");
//sb_whatsapp.append("http:\\localhost:4200\\TroubleTicketPortal\\"+genericPostBody.getTroubleTicketUserId().trim()+"\n\n");
sb_whatsapp.append("http:\\"+link+"\\TroubleTicketPortal\\"+genericPostBody.getTroubleTicketUserId().trim()+"\n\n");

//new whatsapp().send_whatsapp("91","9888652531", sb_whatsapp.toString());
//new whatsapp().send_whatsapp("254","732291441", sb_whatsapp.toString());

//stmnt.close();
//closeConnection(conn);
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}

return result;
}


public int postSmeDetails(GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into postSmeDetails ****************");	
}	
int result=0;
try {
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(genericPostBody.getTableName());
Document document = new Document("user_id", genericPostBody.getSmeUserId()).append("domain", genericPostBody.getSmeDomain()).append("area",genericPostBody.getSmeArea()).append("name", genericPostBody.getSmeName())
.append("email_id",genericPostBody.getSmeEmailId()).append("contact_no",genericPostBody.getSmeContactDetails());
collection.insertOne(document);
result=1;
closeConnection(mongo);
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}

return result;
}


public int postSmeDetailsDelete(GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into postSmeDetailsDelete ****************");	
}	
try {
String tableName=genericPostBody.getTableName();
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
Bson filter=and(eq("user_id",genericPostBody.getSmeUserId()),eq("area",genericPostBody.getSmeArea()),eq("name",genericPostBody.getSmeName()),eq("email_id",genericPostBody.getSmeEmailId()),eq("contact_no",genericPostBody.getSmeContactDetails()));
DeleteResult result = collection .deleteOne(filter);
closeConnection(mongo);
return (int)result.getDeletedCount();
}catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 0;
}

public int postTroubleTicketLevels(GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into postTroubleTicketLevels ****************");	
}	
int result=0;
try {
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("trouble_ticket_level");
Document document = new Document("LEVEL1", genericPostBody.getLevel1()).append("LEVEL2",genericPostBody.getLevel2()).append("LEVEL3", genericPostBody.getLevel3())
.append("LEVEL4",genericPostBody.getLevel4()).append("TICKET_ID",genericPostBody.getTroubleTicketId());
collection.insertOne(document);
result=1;
closeConnection(mongo);

} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}

return result;
}

public int postCredentialDetails(GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into postCredentialDetails ****************");	
}	
int result=0;
try{
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(genericPostBody.getTableName());
Document document = new Document("domain", genericPostBody.getCredentialsDomain()).append("elementname",genericPostBody.getCredentialsNename()).append("hostname", genericPostBody.getCredentialsHostname())
.append("username",genericPostBody.getCredentialsUsername()).append("password",genericPostBody.getCredentialsPassword());
collection.insertOne(document);
result=1;
closeConnection(mongo);

} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}

return result;
}

public int postCredentialsDelete(GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into postCredentialsDelete ****************");	
}	
try {
String tableName=genericPostBody.getTableName();
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
Bson filter=and(eq("elementname",genericPostBody.getCredentialsNename()),eq("hostname",genericPostBody.getCredentialsHostname()),eq("username",genericPostBody.getCredentialsUsername()),eq("password",genericPostBody.getCredentialsPassword()));
DeleteResult result = collection .deleteOne(filter);
closeConnection(mongo);
return (int)result.getDeletedCount();

} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}

return 0;
}


public int postPerformanceSchedulerDelete(GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into postPerformanceSchedulerDelete ****************");	
}	

try {
String tableName=genericPostBody.getTableName();
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
Bson filter=and(eq("domain",genericPostBody.getDomain()),eq("vendor",genericPostBody.getVendor()),eq("report_name",genericPostBody.getReport_name()));
DeleteResult result = collection .deleteOne(filter);
closeConnection(mongo);
return (int)result.getDeletedCount();

} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}

return 0;
}

public int postPerformanceSchedulerUpdate(GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into postPerformanceSchedulerUpdate ****************");	
}	
int result=0;
try {
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(genericPostBody.getTableName());
UpdateResult updateResult = collection.updateOne(and(eq("domain",genericPostBody.getDomain()),eq("vendor",genericPostBody.getVendor()),eq("report_name",genericPostBody.getReport_name())),Updates.combine(Updates.set("reporting", genericPostBody.getReporting())));
result=(int) updateResult.getModifiedCount();
closeConnection(mongo);

} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}

return result;
}

public int EmailReport(GenericPostBody genericPostBody){
String file_name=genericPostBody.getFilePath();
String area=genericPostBody.getEmailArea();	
String subject=genericPostBody.getEmailSubject();	
String content=genericPostBody.getEmailText();	

//new mailmongo().mail(area, subject, content,file_name);	
return 1;
}

}
