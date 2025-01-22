package org.xplorg.tool.telco360.DAO.implementation;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xplorg.tool.telco360.DAO.interfaces.TopologyDiscoveryDAO;
import org.xplorg.tool.telco360.config.BaseDAOMongo;
import org.xplorg.tool.telco360.entity.GenericPostBody;
import org.xplorg.tool.telco360.entity.TableHeader;
import org.xplorg.tool.telco360.entity.tree_children_g_s;
import org.xplorg.tool.telco360.entity.tree_parents_t_d;
import org.xplorg.tool.telco360.entity.tree_parents_t_d_final;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
@Repository("topologyDiscoveryDAO")
public class TopologyDiscoveryDAOImpl extends BaseDAOMongo implements TopologyDiscoveryDAO{

Logger log = LogManager.getLogger(TopologyDiscoveryDAOImpl.class.getName());


public String getTopologyDetails(String tableName,String columns,String conditions) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getTopologyDetails ****************");
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
resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))))).into(new ArrayList<Document>());
}
else {
resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))))).into(new ArrayList<Document>());
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

}
return null;
}


public List<tree_parents_t_d_final> getTopologyDiscoverySubnets(String vendor,String domain) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into getTopologyDiscoverySubnets ****************");	
}	

List<tree_parents_t_d_final> output=new ArrayList<>();
try {
String tableName="topologydiscoveryscanvrfs";	
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
MongoCollection<Document> collection_details = database.getCollection("topologydiscoveryscandetails");

Map<String, Object> groupMap_details = new HashMap<String, Object>();
groupMap_details.put("ipaddress", "$ipaddress");
groupMap_details.put("locSysServices", "$locSysServices");

DBObject groupFields_details = new BasicDBObject(groupMap_details);

ArrayList<Document> resultSet_details = collection_details.aggregate(Arrays.asList(match(and(eq("vendor",vendor),eq("domain",domain))),group(groupFields_details),project(fields
		(include("ipaddress","locSysServices"))))).into(new ArrayList<Document>());
ArrayList<String>ipaddress_details=new ArrayList<String>();
ArrayList<String>services_details=new ArrayList<String>();

for(Document docs:resultSet_details) {
String object=docs.get("_id").toString();
String object_substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}"));
String object_spls[]=object_substr.split(",");
String ipaddress="",type="";

for(String cv:object_spls) {
if(cv.substring(0,cv.indexOf("=")).trim().equals("ipaddress")) {	
ipaddress=cv.substring(cv.indexOf("=")+1).trim();	
}	
if(cv.substring(0,cv.indexOf("=")).trim().equals("locSysServices")) {	
type=cv.substring(cv.indexOf("=")+1).trim();	
}
}
if(ipaddress_details.indexOf(ipaddress)<0) {
ipaddress_details.add(ipaddress);
services_details.add(type);
}
}

Map<String, Object> groupMap = new HashMap<String, Object>();
groupMap.put("vrfIfNetmask", "$vrfIfNetmask");
groupMap.put("vrfIfIpAddr", "$vrfIfIpAddr");
groupMap.put("ipaddress", "$ipaddress");
//Bson filter=and(eq("loss","0"));
DBObject groupFields = new BasicDBObject(groupMap);

ArrayList<Document> resultSet = collection.aggregate(Arrays.asList(match(and(eq("vendor",vendor),eq("domain",domain))),group(groupFields),project(fields
		(include("vrfIfNetmask","vrfIfIpAddr","ipaddress"))),sort(ascending("vrfIfNetmask")))).into(new ArrayList<Document>());
ArrayList<String> vrf_netmask=new ArrayList<String>();
ArrayList<String> vrf_ipaddress=new ArrayList<String>();
ArrayList<String> vrf_ifipaddress=new ArrayList<String>();

for(Document docs:resultSet) {
String object=docs.get("_id").toString();
String object_substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}"));
String object_spls[]=object_substr.split(",");
String vrf_ipaddr="",vrf_ifipaddr="",vrf_mask="";
for(String cv:object_spls) {
if(cv.substring(0,cv.indexOf("=")).trim().equals("ipaddress")) {	
vrf_ipaddr=cv.substring(cv.indexOf("=")+1).trim();	
}	
if(cv.substring(0,cv.indexOf("=")).trim().equals("vrfIfNetmask")) {	
vrf_mask=cv.substring(cv.indexOf("=")+1).trim();	
}	
if(cv.substring(0,cv.indexOf("=")).trim().equals("vrfIfIpAddr")) {	
vrf_ifipaddr=cv.substring(cv.indexOf("=")+1).trim();	
}
}
vrf_ipaddress.add(vrf_ipaddr);
vrf_netmask.add(vrf_mask);
vrf_ifipaddress.add(vrf_ifipaddr);
}

ArrayList<String>unique_mask=new ArrayList<String>();
for(int i=0;i<vrf_netmask.size();i++) {
if(unique_mask.indexOf(vrf_netmask.get(i))<0 && vrf_netmask.get(i).length()>5) {
unique_mask.add(vrf_netmask.get(i));	
}	
}

Collections.sort(unique_mask);
for(int i=0;i<unique_mask.size();i++){
List<tree_parents_t_d> poutput=new ArrayList<>();	
ArrayList<String>check=new ArrayList<String>();	
for(int j=0;j<vrf_netmask.size();j++){
if(vrf_ifipaddress.get(j).contains(".")) {	
String substrSubnet=vrf_ifipaddress.get(j).substring(0,vrf_ifipaddress.get(j).lastIndexOf("."));
if(unique_mask.get(i).equals(vrf_netmask.get(j)) && check.indexOf(substrSubnet+".")<0){
check.add(substrSubnet+".");	
List<tree_children_g_s> coutput=new ArrayList<>();
ArrayList<String>check_ipaddress_sort=new ArrayList<String>();	
ArrayList<String>check_ipaddress=new ArrayList<String>();	
ArrayList<String>check_servicetype=new ArrayList<String>();	
for(int k=0;k<vrf_netmask.size();k++) {
if(unique_mask.get(i).equals(vrf_netmask.get(k)) && vrf_ifipaddress.get(k).startsWith(substrSubnet+".")) {
if(check_ipaddress.indexOf(vrf_ifipaddress.get(k))<0) {	
int idx=ipaddress_details.indexOf(vrf_ipaddress.get(k));
if(idx>-1) {
String ipaddr=vrf_ifipaddress.get(k).trim();	
String type=services_details.get(idx).trim();	
check_ipaddress.add(ipaddr);
check_ipaddress_sort.add(ipaddr);
check_servicetype.add(type);
}
}
}
}

Collections.sort(check_ipaddress_sort);

for(int k=0;k<check_ipaddress_sort.size();k++) {
int idx=check_ipaddress.indexOf(check_ipaddress_sort.get(k));	
tree_children_g_s tcgs=new tree_children_g_s(check_ipaddress.get(idx), check_servicetype.get(idx));
coutput.add(tcgs);	
}

tree_parents_t_d c1=new tree_parents_t_d(substrSubnet+".*","SUBNET",coutput);
poutput.add(c1);
}
}
}	

tree_parents_t_d_final g1=new tree_parents_t_d_final("Subnet("+unique_mask.get(i)+")", "NETWORK", poutput);
output.add(g1);
}

closeConnection(mongo);
} 
catch (Exception ex) 
{
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();

}  	
return output;	
}

@Override
public int postTopologyDiscoveryScan(String data) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into postTopologyDiscoveryScan ****************");	
}
try{
String tableName="topologydiscoveryscan";	
String colsvals[]=data.split(";");
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
////database.createCollection(tableName);
MongoCollection<Document> collection = database.getCollection(tableName);
Document document=new Document();
String scanfrequency="",scanfrequencyvalue="";
for(String cv:colsvals) {
String cl=cv.substring(0,cv.indexOf("="));
String vl=cv.substring(cv.indexOf("=")+1);
if(cl.equalsIgnoreCase("scanfrequency")) {
scanfrequency=vl;	
}
else if(cl.equalsIgnoreCase("scanfrequencyvalue")) {
scanfrequencyvalue=vl;	
}
document.append(cl,vl);
}

if(scanfrequency.equalsIgnoreCase("One Time")) {
Date date=new Date();
SimpleDateFormat sdfd=new SimpleDateFormat("yyyy-MM-dd");
//SimpleDateFormat sdft=new SimpleDateFormat("HH:mm:ss");
sdfd.setTimeZone(TimeZone.getTimeZone(config.getProperty("timezone")));
String dat=sdfd.format(date);
//String timo=sdft.format(date);
//Date timf = sdft.parse(timo);
//Calendar cal = Calendar.getInstance();
//cal.setTime(timf);
//cal.add(Calendar.MINUTE, 5);
//String tim = sdft.format(cal.getTime());

document.append("scandate", dat).append("scantime", scanfrequencyvalue);
}

if(scanfrequency.equalsIgnoreCase("Daily") || scanfrequency.equalsIgnoreCase("Weekly") || scanfrequency.equalsIgnoreCase("Monthly")) {
Date date=new Date();
SimpleDateFormat sdfd=new SimpleDateFormat("yyyy-MM-dd");
//SimpleDateFormat sdft=new SimpleDateFormat("HH:mm:ss");
String dat=sdfd.format(date);
String tim=scanfrequencyvalue;
document.append("scandate", dat).append("scantime", tim);
}


collection.insertOne(document);

closeConnection(mongo);
}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 1;
}


//--------------------------Microwave Topology Tree--------------------------------------

public List<tree_parents_t_d_final> getTopologyMicrowaveTree(String tableName) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into getTopologyMicrowaveTree ****************");	
}	

List<tree_parents_t_d_final> output=new ArrayList<>();
try {
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
Map<String, Object> groupMap = new HashMap<String, Object>();
groupMap.put("vendor", "$vendor");
groupMap.put("domain", "$domain");
DBObject groupFields = new BasicDBObject(groupMap);

ArrayList<Document> resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields
		(include("vendor","domain"))),sort(descending("vendor")))).into(new ArrayList<Document>());
ArrayList<String> listVendor=new ArrayList<String>();
ArrayList<String> listDomain=new ArrayList<String>();

for(Document docs:resultSet) {
String object=docs.get("_id").toString();
String object_substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}"));
String object_spls[]=object_substr.split(",");
for(String cv:object_spls) {
if(cv.substring(0,cv.indexOf("=")).trim().equals("vendor")) {	
listVendor.add(cv.substring(cv.indexOf("=")+1).trim());	
}
if(cv.substring(0,cv.indexOf("=")).trim().equals("domain")) {	
listDomain.add(cv.substring(cv.indexOf("=")+1).trim());	
}
}
}

for(int i=0;i<listVendor.size();i++) {
String vendor=listVendor.get(i);
String domain=listDomain.get(i);
List<tree_parents_t_d> poutput=new ArrayList<>();	
Map<String, Object> groupMap2 = new HashMap<String, Object>();
groupMap2.put("routename", "$routename");
groupMap2.put("domain", "$domain");
Bson filter2=and(eq("vendor",vendor),eq("domain",domain));
DBObject groupFields2 = new BasicDBObject(groupMap2);

ArrayList<Document> resultSet2 = collection.aggregate(Arrays.asList(match(and(filter2)),group(groupFields2),project(fields
		(include("routename","domain"))),sort(ascending("routename")))).into(new ArrayList<Document>());
String routename="";
for(Document docs2:resultSet2) {
List<tree_children_g_s> coutput=new ArrayList<>();
routename="";	
String object2=docs2.get("_id").toString();
String object_substr2=object2.substring(object2.indexOf("{{")+2,object2.indexOf("}}"));
String object_spls[]=object_substr2.split(",");

for(String cv:object_spls) {
if(cv.substring(0,cv.indexOf("=")).trim().equals("routename")) {	
routename=cv.substring(cv.indexOf("=")+1).trim();	
}
}

Map<String, Object> groupMap3 = new HashMap<String, Object>();
groupMap3.put("routeto", "$routeto");
Bson filter3=and(eq("vendor",vendor),eq("domain",domain),eq("routename",routename));
DBObject groupFields3 = new BasicDBObject(groupMap3);
ArrayList<Document> resultSet3 = collection.aggregate(Arrays.asList(match(and(filter3)),group(groupFields3),project(fields
		(include("routeto"))),sort(ascending("routeto")))).into(new ArrayList<Document>());
for(Document docs3:resultSet3) {	
String object3=docs3.get("_id").toString();
String object_substr3=object3.substring(object3.indexOf("{{")+2,object3.indexOf("}}"));
String object_spls3[]=object_substr3.split(",");
String routeto="";
for(String cv:object_spls3) {
if(cv.substring(0,cv.indexOf("=")).trim().equals("routeto")) {	
routeto=cv.substring(cv.indexOf("=")+1).trim();	
}
}
tree_children_g_s tcgs=new tree_children_g_s(routeto, routename);
coutput.add(tcgs);
}

tree_parents_t_d c1=new tree_parents_t_d(routename,"Routenames",coutput);
poutput.add(c1);

}
tree_parents_t_d_final g1=new tree_parents_t_d_final(vendor+"("+domain+")", "Vendor", poutput);
output.add(g1);

}

closeConnection(mongo);
} 
catch (Exception ex) 
{
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();

}  	

return output;	
}


public int uploadTopologyDiscoveryFile(){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into uploadTopologyDiscoveryFile ****************");	
}	
Properties config=getProperties();
int result=0;
Path rootLocation = Paths.get(config.getProperty("server.directory"));
try {
ArrayList<String>ne=new ArrayList<String>();
ArrayList<String>ne_type=new ArrayList<String>();
ArrayList<String>ne_name_in_core=new ArrayList<String>();
ArrayList<String>ne_name_in_radio=new ArrayList<String>();
ArrayList<String>ne_handler=new ArrayList<String>();
ArrayList<String>cellids=new ArrayList<String>();
ArrayList<String>vendor=new ArrayList<String>();
ArrayList<String>region_name=new ArrayList<String>();
ArrayList<String>latitude=new ArrayList<String>();
ArrayList<String>longitude=new ArrayList<String>();

FileReader fr = new FileReader(ResourceUtils.getFile(rootLocation + "\\" + "NewSites.csv"));

BufferedReader br=new BufferedReader(fr);
String line="";
while ((line=br.readLine())!=null)
{
if(!line.contains("NE_Name_In_Core")) {
	
String spls[]=line.split(",");	
	
ne.add(spls[0]);
ne_type.add(spls[1]);
ne_name_in_core.add(spls[2]);
ne_name_in_radio.add(spls[3]);
ne_handler.add(spls[4]);
cellids.add(spls[5]);
vendor.add(spls[6]);
region_name.add(spls[7]);
latitude.add(spls[8]);
longitude.add(spls[9]);
}
}
br.close();	
for(int i=0;i<ne.size();i++) {
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("TopologyDiscoveryGeneric");
ArrayList<Document> resultSet = collection.find(and(eq("NE_Name_In_Core",ne_name_in_core.get(i)))).into(new ArrayList<Document>());
JSONArray jsonArray = new JSONArray(resultSet);

if(jsonArray.length()==0) {
Document document = new Document("NE", ne.get(i)).append("NE_Type",ne_type.get(i)).append("NE_Name_In_Core", ne_name_in_core.get(i)).append("NE_Name_In_Radio", ne_name_in_radio.get(i))
.append("NE_Handler",ne_handler.get(i)).append("Cell_ids",cellids.get(i)).append("Vendor",vendor.get(i)).append("Region_Name",region_name.get(i)).append("Latitude",latitude.get(i))
.append("Longitude",longitude.get(i)).append("Acknowledged","NO");
collection.insertOne(document);
result=1;
closeConnection(mongo);

}

}


} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return result;	
}



public int postTopologyDiscoveryNEUpdate(GenericPostBody genericPostBody) {
int ret=0;	
try {
String tableName=genericPostBody.getTableName();
	
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);

UpdateResult updateResult = collection.updateMany(and(eq("NE_Type",genericPostBody.getNetype()),eq("NE",genericPostBody.getNe()),eq("NE_Name_In_Core",genericPostBody.getNenameincore())),Updates.combine(Updates.set("NE_Name_In_Radio",genericPostBody.getNenameinradio())
		,Updates.set("NE_Handler",genericPostBody.getNehandler()),Updates.set("Cell_ids",genericPostBody.getCellids()),Updates.set("Vendor",genericPostBody.getVendor()),Updates.set("Region_Name",genericPostBody.getRegionname()),Updates.set("Latitude",genericPostBody.getLatitude())
		,Updates.set("Longitude",genericPostBody.getLongitude()),Updates.set("Acknowledged","YES")));

tableName=genericPostBody.getTableName();

MongoCollection<Document> collectionInsert = database.getCollection("sitedb_2g_zm");

Document document = new Document("OSS_CELL_ID_NAME", genericPostBody.getCellids()).append("SITE_ID",genericPostBody.getNenameinradio()).append("BSC", genericPostBody.getNehandler()).append("REGION_NAME", genericPostBody.getRegionname())
.append("LATITUDE",genericPostBody.getLatitude()).append("LONGITUDE",genericPostBody.getLongitude()).append("VENDOR",genericPostBody.getVendor());
collectionInsert.insertOne(document);

ret= 1;
closeConnection(mongo);
}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return ret;
}

public int uploadTopologyDiscoveryData(MultipartFile file){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into uploadTopologyDiscoveryData ****************");	
}	

Properties config=getProperties();

Path rootLocation = Paths.get(config.getProperty("server.directory"));
String file_store = file.getOriginalFilename();
File file_check = new File(rootLocation + "/"  + file_store);
try {
if (file_check.exists()) {
Files.delete(rootLocation.resolve(file_store));
Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
//new drop_tables("jio_sbc_gct").drop();
//read_excel(ResourceUtils.getFile(rootLocation + "/"  + file_store), getConnection());
} 
else {
Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
//read_excel(ResourceUtils.getFile(rootLocation + "/"  + file_store), getConnection());
}
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 1;	
}


public Session getSession(String ipaddress,String username,String password) throws Exception{
JSch jsch = new JSch();
Session session = jsch.getSession(username, ipaddress, 22);
Properties config = new Properties();
config.put("StrictHostKeyChecking", "no");
session.setConfig(config);;
session.setPassword(password);
session.connect();

return session;
}

}
