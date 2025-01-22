package org.xplorg.tool.telco360.DAO.implementation;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.xplorg.tool.telco360.DAO.interfaces.AdminDAO;
import org.xplorg.tool.telco360.config.BaseDAOMongo;
import org.xplorg.tool.telco360.entity.GenericPostBody;
import org.xplorg.tool.telco360.entity.TableHeader;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;

@Repository("adminDAO")
public class AdminDAOImpl extends BaseDAOMongo implements AdminDAO {

Logger log = LogManager.getLogger(AdminDAOImpl.class.getName());

public String adminLogin(String emailId, String password) {
String ret="";
try
{
if(log.isDebugEnabled()) {
log.debug("********************************* Checked into adminLogin  ************************************************");	
}

System.out.println("hello");
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("admin_detail");
ArrayList<Document> resultSet = collection.find(and(eq("email_id",emailId),eq("password",password))).into(new ArrayList<Document>());
JSONArray jsonArray = new JSONArray(JSON.serialize(resultSet));
if(jsonArray.length()>0) {
JSONObject jsonObject1 = jsonArray.getJSONObject(0);
//String status=jsonObject1.optString("status");	
ret=jsonObject1.getString("admin_id")+"~"+jsonObject1.getString("role");
System.out.println("-------------------------------------------> "+ret);


}
else {
ret="-1";
}
/*if(jsonArray.length()>0) {
JSONObject jsonObject1 = jsonArray.getJSONObject(0);
String status=jsonObject1.optString("status");	
String sessionid=jsonObject1.optString("sessionid");
if(status.equals("no") && sessionid.equals("-")) {
ret=jsonObject1.getString("admin_id")+"~"+jsonObject1.getString("role");
}
else {
ret="0";	
}
}
else {
ret="-1";
}
*/
closeConnection(mongo);
}
catch(Exception ex)
{
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
return null;
}

return ret;

}

public int createUser(GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into createUser ****************");	
}
try
{
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("admin_detail");

Document document = new Document("admin_id", ""+genericPostBody.getCreateUserId()).append("email_id",genericPostBody.getCreateUserEmailId()).append("name",genericPostBody.getCreateUserName()).append("password",genericPostBody.getCreateUserPassword()).append("role",genericPostBody.getCreateUserRole());
collection.insertOne(document);	

MongoCollection<Document> collectionUpdate = database.getCollection("hibernate_sequence");
UpdateResult updateResult = collectionUpdate.updateOne(and(eq("next_val",genericPostBody.getCreateUserId())),Updates.set("next_val", ""+(Integer.parseInt(genericPostBody.getCreateUserId())+2)));

MongoCollection<Document> collectionpermissions = database.getCollection("user_permissions");
ArrayList<Document>resultSet= collectionpermissions.find(and(eq("user_id","DEFAULT"))).into(new ArrayList<Document>());

for(Document docs:resultSet) {
Document doc=new Document();	
doc.append("user_id", genericPostBody.getCreateUserId()).append("feature", docs.get("feature").toString()).append("module", docs.get("module").toString()).append("domain", docs.get("domain").toString())
.append("vendor", docs.get("vendor").toString()).append("permission", docs.get("permission").toString());	
collectionpermissions.insertOne(doc);
}

MongoCollection<Document> collectionalarmfilter = database.getCollection("alarm_filter");
ArrayList<Document>resultSetalarmfilter= collectionalarmfilter.find(and(eq("user_id","DEFAULT"))).into(new ArrayList<Document>());

for(Document docs:resultSetalarmfilter) {
Document doc=new Document();

doc.append("user_id", genericPostBody.getCreateUserId()).append("domain", docs.get("domain").toString()).append("vendor", docs.get("vendor").toString()).append("type", docs.get("type").toString())
.append("for", docs.get("for").toString()).append("filter", docs.get("filter").toString()).append("value", docs.get("value").toString());	
collectionalarmfilter.insertOne(doc);
}
//closeConnection(mongo);

MongoDatabase database1=mongo.getDatabase(config.getProperty("database.performance_ericsson_transmission"));
createKpis(database1,genericPostBody.getCreateUserId());
createReports(database1,genericPostBody.getCreateUserId());
createReportsGroups(database1,genericPostBody.getCreateUserId());
createKpisFilter(database1,genericPostBody.getCreateUserId());
//closeConnection();

MongoDatabase database2=mongo.getDatabase(config.getProperty("database.performance_nec_transmission"));
createKpis(database2,genericPostBody.getCreateUserId());
createReports(database2,genericPostBody.getCreateUserId());
createReportsGroups(database2,genericPostBody.getCreateUserId());
createKpisFilter(database2,genericPostBody.getCreateUserId());
//closeConnection();

MongoDatabase database3=mongo.getDatabase(config.getProperty("database.performance_nce_transmission"));
createKpis(database3,genericPostBody.getCreateUserId());
createReports(database3,genericPostBody.getCreateUserId());
createReportsGroups(database3,genericPostBody.getCreateUserId());
createKpisFilter(database3,genericPostBody.getCreateUserId());
//closeConnection();

MongoDatabase database11=mongo.getDatabase(config.getProperty("database.performance_sam_transmission"));
createKpis(database11,genericPostBody.getCreateUserId());
createReports(database11,genericPostBody.getCreateUserId());
createReportsGroups(database11,genericPostBody.getCreateUserId());
createKpisFilter(database11,genericPostBody.getCreateUserId());

MongoDatabase database4=mongo.getDatabase(config.getProperty("database.performance_zambia_ipran"));
createKpis(database4,genericPostBody.getCreateUserId());
createReports(database4,genericPostBody.getCreateUserId());
createKpisFilter(database4,genericPostBody.getCreateUserId());
//closeConnection();

MongoDatabase database5=mongo.getDatabase(config.getProperty("database.performance_zambia_mpbn_zte"));
createKpis(database5,genericPostBody.getCreateUserId());
createReports(database5,genericPostBody.getCreateUserId());
createKpisFilter(database5,genericPostBody.getCreateUserId());
//closeConnection();

MongoDatabase database6=mongo.getDatabase(config.getProperty("database.performance_zambia_mpbn_ericsson"));
createKpis(database6,genericPostBody.getCreateUserId());
createReports(database6,genericPostBody.getCreateUserId());
createKpisFilter(database6,genericPostBody.getCreateUserId());
//closeConnection();

MongoDatabase database7=mongo.getDatabase(config.getProperty("database.performance_zambia_juniper"));
createKpis(database7,genericPostBody.getCreateUserId());
createReports(database7,genericPostBody.getCreateUserId());
createKpisFilter(database7,genericPostBody.getCreateUserId());
//closeConnection();

MongoDatabase database8=mongo.getDatabase(config.getProperty("database.performance_zambia_nokia"));
createKpis(database8,genericPostBody.getCreateUserId());
createReports(database8,genericPostBody.getCreateUserId());
createKpisFilter(database8,genericPostBody.getCreateUserId());
//closeConnection();

MongoDatabase database9=mongo.getDatabase(config.getProperty("database.performance_zambia_ericsson"));
createKpis(database9,genericPostBody.getCreateUserId());
createReports(database9,genericPostBody.getCreateUserId());
createKpisFilter(database9,genericPostBody.getCreateUserId());
//closeConnection();

MongoDatabase database10=mongo.getDatabase(config.getProperty("database.performance_zambia_pc_zte"));
createKpis(database10,genericPostBody.getCreateUserId());
createReports(database10,genericPostBody.getCreateUserId());
createKpisFilter(database10,genericPostBody.getCreateUserId());
//closeConnection();

MongoDatabase database12=mongo.getDatabase(config.getProperty("database.performance_zambia_dptech_firewall"));
createKpis(database12,genericPostBody.getCreateUserId());
createReports(database12,genericPostBody.getCreateUserId());
createKpisFilter(database12,genericPostBody.getCreateUserId());

closeConnection(mongo);

return 1;
}
catch(Exception ex)
{
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 0;
}


public int adminLogout(String emailId) {
int ret=0;
try
{	
if(log.isInfoEnabled()) {
log.debug("********************************* Checked into adminLogout  ************************************************");	
}
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("admin_detail");

UpdateResult updateResult = collection.updateMany(and(eq("email_id",emailId)),Updates.set("status", "no"));
ret=(int) updateResult.getModifiedCount();
closeConnection(mongo);
}
catch(Exception ex)
{
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
return ret;
}
return 0;
}

public void createKpis(MongoDatabase database,String user_id) {
MongoCollection<Document> collection = database.getCollection("kpi_formula");
ArrayList<Document> resultSet = collection.find(and(eq("admin_id","DEFAULT"))).into(new ArrayList<Document>());
for(Document docs:resultSet) {
Document doc=new Document();
doc.append("admin_id", user_id).append("calculation_type", docs.get("calculation_type")).append("element_name", docs.get("element_name")).append("formula", docs.get("formula"))
.append("groups", docs.get("groups")).append("kpi_formula", docs.get("kpi_formula")).append("kpi_name", docs.get("kpi_name")).append("link_to_topology", docs.get("link_to_topology"))
.append("opco", docs.get("opco")).append("rate", docs.get("rate")).append("severity", docs.get("severity")).append("threshold", docs.get("threshold")).append("troubleticket", docs.get("troubleticket"))
.append("unit", docs.get("unit")).append("correlation", docs.get("correlation"));
collection.insertOne(doc);
}
}

public void createReports(MongoDatabase database,String user_id) {
MongoCollection<Document> collection = database.getCollection("kpi_formula_report");
ArrayList<Document> resultSet = collection.find(and(eq("admin_id","DEFAULT"))).into(new ArrayList<Document>());
for(Document docs:resultSet) {
Document doc=new Document();
doc.append("admin_id", user_id).append("calculation_type", docs.get("calculation_type")).append("element_name", docs.get("element_name")).append("formula", docs.get("formula"))
.append("groups", docs.get("groups")).append("kpi_formula", docs.get("kpi_formula")).append("kpi_name", docs.get("kpi_name")).append("link_to_topology", docs.get("link_to_topology"))
.append("opco", docs.get("opco")).append("rate", docs.get("rate")).append("severity", docs.get("severity")).append("threshold", docs.get("threshold")).append("troubleticket", docs.get("troubleticket"))
.append("unit", docs.get("unit"));
collection.insertOne(doc);
}
}

public void createReportsGroups(MongoDatabase database,String user_id) {
MongoCollection<Document> collection = database.getCollection("report_group");
ArrayList<Document> resultSet = collection.find(and(eq("admin_id","DEFAULT"))).into(new ArrayList<Document>());
for(Document docs:resultSet) {
Document doc=new Document();
doc.append("opco", docs.get("opco")).append("admin_id", user_id).append("ReportName", docs.get("ReportName")).append("ElementName", docs.get("ElementName")).append("SelectInterface", docs.get("SelectInterface"));
collection.insertOne(doc);
}
}

public void createKpisFilter(MongoDatabase database,String user_id) {
MongoCollection<Document> collection = database.getCollection("filter_kpi_formula");
ArrayList<Document> resultSet = collection.find(and(eq("admin_id","DEFAULT"))).into(new ArrayList<Document>());
for(Document docs:resultSet) {
Document doc=new Document();
doc.append("admin_id", user_id).append("calculation_type", docs.get("calculation_type")).append("element_name", docs.get("element_name")).append("formula", docs.get("formula"))
.append("groups", docs.get("groups")).append("kpi_formula", docs.get("kpi_formula")).append("kpi_name", docs.get("kpi_name")).append("link_with_dashboard", docs.get("link_with_dashboard"))
.append("opco", docs.get("opco")).append("rate", docs.get("rate")).append("severity", docs.get("severity")).append("threshold", docs.get("threshold")).append("troubleticket", docs.get("troubleticket"))
.append("unit", docs.get("unit")).append("correlation", docs.get("correlation"));
collection.insertOne(doc);
}
}

public String getTableSpecificColsValsConditionGeneric(String tableName,String columns,String conditions) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTableSpecificColsValsConditionGeneric ****************");
}

try {
ArrayList<Bson> fltr=new ArrayList<Bson>();
Bson filter=null;
if(conditions.length()>1) {
String cond=conditions.replace("@DOT@", ".").replace("@SEMICOLON@", ";").replace("@FORWARDSLASH@", "/").replace("@HASH@", "#");	
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
resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))))).into(new ArrayList<Document>());//,limit(500)
}
else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))))).into(new ArrayList<Document>());//,limit(500)
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
String vl=cv.substring(cv.indexOf("=")+1).trim().replace(",", "@COMMA@").replace("/","@FORWARDSLASH@").replace("\\","@BACKWARDSLASH@").replace("\"","");//.replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","")
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

public int postUserDetailsDelete(GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into postUserDetailsDelete ****************");	
}	
try {
String tableName1="admin_detail";
String tableName2="token";
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection1 = database.getCollection(tableName1);
MongoCollection<Document> collection2 = database.getCollection(tableName2);
Bson filter1=and(eq("admin_id",genericPostBody.getCreateUserId().toString()),eq("email_id",genericPostBody.getCreateUserEmailId().toString()),eq("name",genericPostBody.getCreateUserName().toString()),eq("password",genericPostBody.getCreateUserPassword().toString()),eq("role",genericPostBody.getCreateUserRole().toString()));
DeleteResult result1 = collection1.deleteOne(filter1);
int admDelete=(int)result1.getDeletedCount();
Bson filter2=and(eq("token_id",""+(Integer.parseInt(genericPostBody.getCreateUserId())+1)),eq("email_id",genericPostBody.getCreateUserEmailId().toString()));
DeleteResult result2 = collection2.deleteOne(filter2);
int tknDelete=(int)result2.getDeletedCount();

closeConnection(mongo);
if(admDelete>0 && tknDelete>0) {
return 1;	
}
else {
return 0;	
}

}catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 0;
}

public int postUserDetailsUpdate(GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into postUserDetailsUpdate ****************");	
}	
try {
String tableName1="admin_detail";
String tableName2="token";

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection1 = database.getCollection(tableName1);
MongoCollection<Document> collection2 = database.getCollection(tableName2);
UpdateResult updateResult1 = collection1.updateOne(and(eq("admin_id",genericPostBody.getCreateUserId())),Updates.combine(Updates.set("email_id", genericPostBody.getCreateUserEmailId())
		,Updates.set("name", genericPostBody.getCreateUserName()),Updates.set("password", genericPostBody.getCreateUserPassword()),Updates.set("role", genericPostBody.getCreateUserRole())));
//int admUpdate = (int) updateResult1.getModifiedCount();
UpdateResult updateResult2 = collection2.updateOne(and(eq("user_id",""+genericPostBody.getCreateUserId())),Updates.combine(Updates.set("email_id", genericPostBody.getCreateUserEmailId())));
//int tknUpdate = (int) updateResult2.getModifiedCount();
closeConnection(mongo);
return 1;	
}catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 0;
}

public int postUserPermissionsUpdate(GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into postUserPermissionsUpdate ****************");	
}	
try {
String tableName="user_permissions";

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
UpdateResult updateResult = collection.updateOne(and(eq("user_id",genericPostBody.getUserPermissionUserId()),eq("feature",genericPostBody.getUserPermissionFeature()),eq("module",genericPostBody.getUserPermissionModule()),eq("domain",genericPostBody.getUserPermissionDomain())
		,eq("vendor",genericPostBody.getUserPermissionVendor())),Updates.combine(Updates.set("permission", genericPostBody.getUserPermissionPermission())));
closeConnection(mongo);
return 1;	
}catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 0;
}

public int postChangePasswordUpdate(GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into postChangePasswordUpdate ****************");	
}	
try {
String tableName="admin_detail";

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
UpdateResult updateResult = collection.updateOne(and(eq("admin_id",genericPostBody.getChangePasswordUserId()),eq("email_id",genericPostBody.getChangePasswordEmailId()),eq("name",genericPostBody.getChangePasswordName())
		,eq("password",genericPostBody.getChangePasswordOldPassword())),Updates.combine(Updates.set("password", genericPostBody.getChangePasswordNewPassword())));
closeConnection(mongo);
return (int)updateResult.getModifiedCount();	
}catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 0;
}


}
