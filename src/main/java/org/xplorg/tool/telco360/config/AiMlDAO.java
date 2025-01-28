package org.xplorg.tool.telco360.config;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.ne;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.descending;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xplorg.tool.telco360.entity.CollectionToColNValPOJO;
import org.xplorg.tool.telco360.entity.resultEntity;
import org.xplorg.tool.telco360.entity.resultEntityKpi;
import org.xplorg.tool.telco360.entity.startEntity;
import org.xplorg.tool.telco360.entity.startEntityAB;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

public class AiMlDAO {

Logger log = LogManager.getLogger(AiMlDAO.class.getName());

public Connection getConnection() {

if (log.isDebugEnabled()) {
log.debug("*****************************************  checked into getConnection of AiMlDAO  ************************************************");
}

Connection conn = null;
//connection	
try {
Properties config = getProperties();

Class.forName(config.getProperty("database.driver"));
conn = DriverManager.getConnection(config.getProperty("database.url_analysis"), config.getProperty("database.username"), config.getProperty("database.password"));

} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
//e.printStackTrace();
}

return conn;
}

public ResultSet execute(String query) {
if (log.isDebugEnabled()) {
log.debug("*****************************************  checked into execute of AiMlDAO  ************************************************");
}
Connection conn = null;
ResultSet resultSet = null;
Statement stmnt = null;
try {
conn = getConnection();
stmnt = conn.createStatement();
resultSet = stmnt.executeQuery(query);
} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
//ex.printStackTrace();
closeConnection(conn);
}
return resultSet;
}

public int Update(String query) {
if (log.isDebugEnabled()) {
log.debug("*****************************************  checked into Update of AiMlDAO  ************************************************");
}

int insert = 0;
Statement stmnt = null;
Connection conn = null;
try {
conn = getConnection();
stmnt = conn.createStatement();
insert = stmnt.executeUpdate(query);
} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
closeConnection(conn);
}
return insert;
}

public void closeConnection(Connection conn) {
if (log.isDebugEnabled()) {
log.debug("*****************************************  checked into closeConnection of AiMlDAO  ************************************************");
}

try {
conn.close();
} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);

//ex.printStackTrace();
}
}
public Connection getConnectionZambia() {

if (log.isDebugEnabled()) {
log.debug("*****************************************  checked into getConnectionZ of AiMlDAO  ************************************************");
}

Connection conn = null;
//connection	
try {
Properties config = getProperties();

Class.forName(config.getProperty("database.driver"));
conn = DriverManager.getConnection(config.getProperty("database.url_zambia"), config.getProperty("database.username"), config.getProperty("database.password"));

} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
//e.printStackTrace();
}

return conn;
}

public ResultSet executeZambia(String query) {
if (log.isDebugEnabled()) {
log.debug("*****************************************  checked into executeZ of AiMlDAO  ************************************************");
}
Connection conn = null;
ResultSet resultSet = null;
Statement stmnt = null;
try {
conn = getConnectionZambia();
stmnt = conn.createStatement();
resultSet = stmnt.executeQuery(query);
} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
//ex.printStackTrace();
closeConnection(conn);
}
return resultSet;
}

public int UpdateZambiaa(String query) {
if (log.isDebugEnabled()) {
log.debug("*****************************************  checked into UpdateZ of AiMlDAO  ************************************************");
}

int insert = 0;
Statement stmnt = null;
Connection conn = null;
try {
conn = getConnectionZambia();
stmnt = conn.createStatement();
insert = stmnt.executeUpdate(query);
} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
closeConnection(conn);
}
return insert;
}

//-----------------------------------------------  
//----------DROP TEMP TABLE----------------------
//-----------------------------------------------
public void dropTable(String tableName) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into dropTable of AIMlDAO ****************");
}
String query = "DROP TABLE IF EXISTS " + tableName;
try {
Update(query);
} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
}
}

public Properties getProperties() {
if (log.isDebugEnabled()) {
log.debug("*****************************************  checked into getProperties of AiMlDAO  ************************************************");
}

Properties config = new Properties();
InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
try {
config.load(input);
} catch (Exception ex) {
log.error("Exception occurs:-----" + ex.getMessage(), ex);
//ex.printStackTrace();	
}

return config;
}
public String alarmToDomain(String alarm) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into alarmToDomain of AIMlDAO ****************");
}
String domain = null;
// {"access","applications","cloud","core","ip","radio","transport"}
try {
if (alarm.startsWith("NO")) {
domain = "nokia";
} else if (alarm.startsWith("E")) {
domain = "ericsson";
} else if (alarm.startsWith("H")) {
domain = "huawei_ipran";
} else if (alarm.startsWith("ZC")) {
domain = "zte_cscore";
} else if (alarm.startsWith("ZM")) {
domain = "zte_mpbn";
} else if (alarm.startsWith("ZP")) {
domain = "zte_pscore";
} else if (alarm.startsWith("SM")) {
domain = "sam_microwave";
} else if (alarm.startsWith("NE")) {
domain = "nec_microwave";
}
//////System.out.println(alarm+" x "+domain);

} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
}

return domain;
}
public String vendortodomain(String ven) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into vendortodomain of AIMlDAO ****************");
} //used in TREE label Heads
String domain = null;
try {
if (ven.startsWith("no")) {
domain = " (RADIO)";
} else if (ven.startsWith("e")) {
domain = " (RADIO)";
} else if (ven.startsWith("z")) {
domain = " (CORE)";
} else if (ven.startsWith("h")) {
domain = " (IPRAN)";
} else if (ven.startsWith("s")) {
domain = " (TRANSMISSON)";
} else if (ven.startsWith("ne")) {
domain = " (TRANSMISSON)";
}

} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
}

return domain;
}
public String domainToprefix(String domain) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into domainToprefix of AIMlDAO ****************");
}
String prefix = null;
try {
//////System.out.println(domain);
if (domain.startsWith("no")) {
prefix = "NO";
} else if (domain.startsWith("e")) {
prefix = "ER";
} else if (domain.startsWith("h")) {
prefix = "HU";
} else if (domain.startsWith("zte_c")) {
prefix = "ZC";
} else if (domain.startsWith("zte_m")) {
prefix = "ZM";
} else if (domain.startsWith("zte_p")) {
prefix = "ZP";
} else if (domain.startsWith("s")) {
prefix = "SM";
} else if (domain.startsWith("ne")) {
prefix = "NE";
}
//////System.out.println(prefix);
} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
}

return prefix;
}

//TODO
//X_Y Contains ArrayList of Relation and ArrayList of Confidence
public ArrayList < ArrayList < String >> analysisLogic(String domainX, String domainY, int span, ArrayList < resultEntity > relCount, int confInput) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into analysisLogic of AIMlDAO ****************");
}
//  ////System.out.println("314"+ domainX+ domainY+ span+ confInput);
HashMap < String, Integer > tempCount = new HashMap < String, Integer > ();
// tempCount is countDomain for Count of X(right error)
ArrayList < String > labelsX = new ArrayList < String > ();
ArrayList < String > confidenceY = new ArrayList < String > ();
ArrayList < ArrayList < String >> X_Y = new ArrayList < ArrayList < String >> ();

Properties config = getProperties();
int analysisLimit = Integer.parseInt(config.getProperty("analysis.limit"));
String mongoServer = config.getProperty("database.mongodb.ipaddress");
String mongoDb = config.getProperty("mongo.db.database.topology");
String prefixX = domainToprefix(domainX);
String prefixY = domainToprefix(domainY);

DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
DateTime today = DateTime.now();
today = today.minusDays(1);

String prev15Dates = dtf.print(today);
//String prev15Dates = "2022-01-25";
//System.out.println("date==="+prev15Dates);
try {

MongoClient mongo = new MongoClient(mongoServer, 27017);
MongoDatabase database = mongo.getDatabase(mongoDb);
MongoCollection < Document > collection;

if(domainX.equals("ericsson_microwave")) {
collection = database.getCollection("ericsson_radio" + "_alarms");
}

else if(domainX.equals("huawei_microwave")) {
collection = database.getCollection("huawei_ipran" + "_alarms");
}
else {
collection = database.getCollection(domainX + "_alarms");	
}


//System.out.println( "=======>"+collection.getNamespace());
//-------Step 2 Domain COUNT Queries------------------------------

BasicDBObject index=null;

if(domainX.equals("huawei_ipran")||domainX.equals("huawei_microwave")) {
	index=new BasicDBObject("$hint", "HWNMNORTHBOUNDEVENTDATE_-1_HWNMNORTHBOUNDDEVICEIP_-1_HWNMNORTHBOUNDNENAME_-1_INSERTIONTIME_-1");
}

else   if(domainX.equals("sam_mpbn")||domainX.equals("sam_microwave")) {
	index=new BasicDBObject("$hint", "FIRSTTIMEDETECTEDDATE_-1_NODEID_-1_NODENAME_-1_INSERTIONTIME_-1");
}

else  if(domainX.equals("zte_mpbn")) {
	index=new BasicDBObject("$hint", "ALARMEVENTDATE_-1_ALARMNEIP_-1_ALARMMOCOBJECTINSTANCE_-1_INSERTIONTIME_-1");
}

else if(domainX.equals("ericsson_microwave")) {
index=new BasicDBObject("$hint", "EVENT_DATE_-1_ELEMENT_NAME_-1_INSERTIONTIME_-1");
}



CollectionToColNValPOJO xPojo = getColNamesofDomain(domainX);
Bson xBson = getFilterofDomain(domainX, prev15Dates);

//System.out.println(xPojo.colIP);
//System.out.println(xPojo.colElement);
//System.out.println(xPojo.colAlarm);
Map < String, Object > groupMap = new HashMap < String, Object > ();
groupMap.put(xPojo.colIP, "$" + xPojo.colIP);
groupMap.put(xPojo.colElement, "$" + xPojo.colElement);
groupMap.put(xPojo.colAlarm, "$" + xPojo.colAlarm);
DBObject groupFields = new BasicDBObject(groupMap);
//log.debug("366"+xBson);

ArrayList < Document > iterDo = collection.aggregate(Arrays.asList(xBson, group(groupFields, sum("count", 1)),
project(fields(include(xPojo.colIP, xPojo.colElement, xPojo.colAlarm, "count"))), sort(descending("count")))).hint(index)
.into(new ArrayList < Document > ());
@SuppressWarnings("deprecation")
JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
String aip = jsonObject_id.optString(xPojo.colIP);

String aoi = jsonObject_id.optString(xPojo.colElement);
////System.out.println(aip);
////System.out.println(aoi);
////System.out.println(ac);
////System.out.println(cnt);

if(domainX.equals("zte_mpbn")) {
	
String name=zte_own_name(aip,aoi, database);
  
aoi = name;
}

else if(domainX.equals("sam_mpbn")) {
	
String ip=nokia_own_ip(aip,aoi, database);
  
aip = ip;
}




else {
aoi = jsonObject_id.optString(xPojo.colElement);
}

String ac = jsonObject_id.optString(xPojo.colAlarm);
Integer cnt = jsonObject1.optInt("count");

////System.out.println("=========================");
tempCount.put(aip + "~~" + aoi + "~~" + ac, cnt);
}

mongo.close();
//System.out.println("tempCount--size"+"==="+tempCount.size());
//System.out.println("relCount--size"+"==="+relCount.size());
//-------Step 3 Placing data to ArrayList by Divide-----------------
for (resultEntity rl: relCount) {
////System.out.println("==>"+rl);
if (analysisLimit != 0) {

Integer countX = tempCount.get(rl.getLeftError());

if (countX != null && !rl.getLeftError().equals(rl.getRightError())) {
//calculation of Confidence 
////System.out.println(countX+"=="+rl.getLeftError()+"==="+countX.intValue());
rl.confidenceCount(countX.intValue());
//Now Value to Getter Setter of child-parent graph
String label = null;
label = prefixX + "~~" + rl.getLeftError() +" <~> " + prefixY + "~~" + rl.getRightError();
int confidence = Math.round(rl.getCount());

if (confidence >= confInput) { //full comfirmity
labelsX.add(label);
confidenceY.add(Integer.toString(confidence));
}

--analysisLimit;
}

} else {

break;
}
}
X_Y.add(labelsX);
X_Y.add(confidenceY);
} catch (Exception e) {
	e.printStackTrace();
log.error("Exception occurs:-----" + e.getMessage(), e);
}
return X_Y;
}


//-----------------------------------------------
//-----------------------------------------------
//----------Distinct from 2 Domains--------------
//-----------------------------------------------
//-----------------------------------------------

//TODO
public ArrayList < resultEntity > getGroupBy(String domainX, String domainY, String alarmX) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getGroupBy of AIMlDAO ****************");
} //System.out.println("485"+domainX +"===== "+domainY+"======" + alarmX);
ArrayList < resultEntity > returnList = new ArrayList < resultEntity > ();

Properties config = getProperties();
String mongoServer = config.getProperty("database.mongodb.ipaddress");
String mongoDb = config.getProperty("mongo.db.database.topology");

//String relDate = "2022-01-25";
DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
DateTime today = DateTime.now();
today = today.minusDays(1);
String relDate = dtf.print(today);
try {
MongoClient mongo = new MongoClient(mongoServer, 27017);
MongoDatabase database = mongo.getDatabase(mongoDb);

//System.out.println("rel_" + domainX + "_" + domainY);
MongoCollection < Document > collection = database.getCollection("rel_" + domainX + "_" + domainY);

if (alarmX.equals("NA")) {


Map < String, Object > groupMap = new HashMap < String, Object > ();
groupMap.put(domainX + "_AlarmL", "$" + domainX + "_AlarmL");
groupMap.put(domainX + "_AlarmR", "$" + domainX + "_AlarmR");
DBObject groupFields = new BasicDBObject(groupMap);
ArrayList < Document > iterDo = collection.aggregate(Arrays.asList(match(eq("relDate", relDate)), group(
groupFields, sum("count", 1)), project(fields(include(domainX + "_AlarmL", domainY + "_AlarmR", "count"))), sort(descending("count"))))
.into(new ArrayList < Document > ());

JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
String aoi = jsonObject_id.optString(domainX + "_AlarmL");
String ac = jsonObject_id.optString(domainX + "_AlarmR");
Integer cnt = jsonObject1.optInt("count");
//log.debug("gp1: "+aoi+ac+cnt);	
resultEntity en = new resultEntity(aoi, ac, cnt);
returnList.add(en);
}
mongo.close();

} else if (alarmX.endsWith("LIKE911")) { //for Analysis Element Specific
String sX = alarmX.replace("LIKE911", "");
//System.out.println();
String key_name=domainX + "_AlarmL_1";

//System.out.println("key==="+key_name);
BasicDBObject index=index=new BasicDBObject("$hint", key_name);

Map < String, Object > groupMap = new HashMap < String, Object > ();
groupMap.put(domainX + "_AlarmL", "$" + domainX + "_AlarmL");
groupMap.put(domainX + "_AlarmR", "$" + domainX + "_AlarmR");
DBObject groupFields = new BasicDBObject(groupMap);
ArrayList < Document > iterDo;
if(domainX.equals("zte_mpbn")) {
	iterDo = collection.aggregate(Arrays.asList(match(and(eq("relDate", relDate), eq(domainX + "_AlarmL", java.util.regex.Pattern.compile(sX)))), group(
			groupFields, sum("count", 1)), project(fields(include(domainX + "_AlarmL", domainX + "_AlarmR", "count"))), sort(descending("count")))).hint(index)
			.into(new ArrayList < Document > ());
}

else {
	iterDo = collection.aggregate(Arrays.asList(match(and(eq("relDate", relDate), eq(domainX + "_AlarmL", java.util.regex.Pattern.compile(sX)))), group(
			groupFields, sum("count", 1)), project(fields(include(domainX + "_AlarmL", domainX + "_AlarmR", "count"))), sort(descending("count")))).hint(index)
			.into(new ArrayList < Document > ());
}


JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
String aoi = jsonObject_id.optString(domainX + "_AlarmL");
String ac = jsonObject_id.optString(domainX + "_AlarmR");
Integer cnt = jsonObject1.optInt("count");



resultEntity en = new resultEntity(aoi, ac, cnt);
returnList.add(en);
}

} else { 
	String key_name=domainX + "_AlarmL_1";

	//System.out.println("key==="+key_name);
	BasicDBObject index=index=new BasicDBObject("$hint", key_name);
Map < String, Object > groupMap = new HashMap < String, Object > ();
groupMap.put(domainX + "_AlarmL", "$" + domainX + "_AlarmL");
groupMap.put(domainX + "_AlarmR", "$" + domainX + "_AlarmR");
DBObject groupFields = new BasicDBObject(groupMap);

ArrayList < Document > iterDo ;
if(domainX.equals("zte_mpbn")) {
	iterDo = collection.aggregate(Arrays.asList(match(and(eq("relDate", relDate), eq(domainX + "_AlarmL", java.util.regex.Pattern.compile(alarmX)))), group(
			groupFields, sum("count", 1)), project(fields(include(domainX + "_AlarmL", domainX + "_AlarmR", "count"))), sort(descending("count")))).hint(index)
			.into(new ArrayList < Document > ());
}
iterDo = collection.aggregate(Arrays.asList(match(and(eq("relDate", relDate), eq(domainX + "_AlarmL", java.util.regex.Pattern.compile(alarmX)))), group(
groupFields, sum("count", 1)), project(fields(include(domainX + "_AlarmL", domainX + "_AlarmR", "count"))), sort(descending("count")))).hint(index)
.into(new ArrayList < Document > ());

JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
String aoi = jsonObject_id.optString(domainX + "_AlarmL");
String ac = jsonObject_id.optString(domainX + "_AlarmR");
Integer cnt = jsonObject1.optInt("count");

resultEntity en = new resultEntity(aoi, ac, cnt);
returnList.add(en);
}
}
mongo.close();

} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
}
return returnList;
}

public String elementName(String alarm1, String alarm2) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into elementName in AiMlDAO ****************");
}
String r1 = null, r2 = null, resolution = null;
String query = "SELECT RESOLUTION_I, RESOLUTION_II FROM enz_resolution WHERE ALARM_I = '" + alarm1 + "' and ALARM_II  = '" + alarm2 + "' LIMIT 1";
//SELECT ELEMENT FROM transport_ref WHERE transport_ALARM = 'T6666' LIMIT 1;
try {
ResultSet rs = execute(query);
while (rs.next()) {
r1 = rs.getString(1);
r2 = rs.getString(2);
}
resolution = r1 + "," + r2;
} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
}
return resolution;
}
//-----------------------------------------------
//--------------MAIL for AI----------------------
//-----------------------------------------------
//-----------------------------------------------
@SuppressWarnings("static-access")
public void mail(String area, String msgBody) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into AI/ML mail ****************");
}

try {
Properties config = new Properties();
InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");

config.load(input);

Properties props = new Properties();
String mongoServer = config.getProperty("mongo.server");
String mongoDb = config.getProperty("mongo.db");
props.put("mail.smtp.host", config.getProperty("mail.smtp.host"));
props.put("mail.stmp.user", config.getProperty("mail.stmp.user"));
//If you want you use TLS
props.put("mail.smtp.auth", config.getProperty("mail.smtp.auth"));

props.put("mail.smtp.starttls.enable", config.getProperty("mail.smtp.starttls.enable"));
props.put("mail.smtp.password", config.getProperty("mail.smtp.password"));
// If you want to use SSL
props.put("mail.smtp.socketFactory.port", config.getProperty("mail.smtp.socketFactory.port"));
props.put("mail.smtp.socketFactory.class", config.getProperty("mail.smtp.socketFactory.class"));
props.put("mail.smtp.auth", config.getProperty("mail.smtp.auth"));
props.put("mail.smtp.port", config.getProperty("mail.smtp.port"));
Session session = Session.getDefaultInstance(props, new Authenticator() {
@Override
protected PasswordAuthentication getPasswordAuthentication() {
String username = config.getProperty("mail.username");
String password = config.getProperty("mail.password");
return new PasswordAuthentication(username, password);
}
});
MongoClient mongo = new MongoClient(mongoServer, 27017);
MongoDatabase database = mongo.getDatabase(mongoDb);
MongoCollection < Document > collection = database.getCollection("trouble_ticket_details");
ArrayList < String > to = new ArrayList < String > ();
ArrayList < Document > iterDo = collection.aggregate(Arrays.asList(match(eq("area", area)),
project(fields(include("email_id"))))).into(new ArrayList < Document > ());
JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
to.add(jsonObject1.optString("email_id"));
}
mongo.close();
/*

String qry="select email_id from trouble_ticket_details where area='"+area+"'";
ResultSet rs=execute(qry);

while(rs.next()){
to.add(rs.getString("email_id"));
}
*/
String from = config.getProperty("mail.from");
String subject = "Trouble Ticket - Telco360 AI-ML";
MimeMessage msg = new MimeMessage(session);
try {
msg.setFrom(new InternetAddress(from));
InternetAddress[] addressTo = new InternetAddress[to.size()];
for (int i = 0; i < to.size(); i++) {
addressTo[i] = new InternetAddress(to.get(i));
}
msg.setRecipients(RecipientType.TO, addressTo);
msg.setSubject(subject);

// Create the message part
BodyPart messageBodyPart = new MimeBodyPart();

// Fill the message
messageBodyPart.setText(msgBody);

// Create a multipar message
Multipart multipart = new MimeMultipart();

// Set text message part
multipart.addBodyPart(messageBodyPart);

// Part two is attachment
//multipart.addBodyPart(messageBodyPart);

// Send the complete message parts
msg.setContent(multipart);

Transport transport = session.getTransport(config.getProperty("mail.transport"));
transport.send(msg);

//////System.out.println("E-mail sent !");
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
}
//X_Y Contains ArrayList of Relation and ArrayList of Confidence
//--------------------------------------------------------------
//--------------------------------------------------------------
//--------------------------------------------------------------
//--------------------------------------------------------------
public ArrayList < resultEntityKpi > kpiAnalysisLogic(String kpiX, String domainY, String kpiNameSpec) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into kpiAnalysisLogic of AIMlDAO ****************");
}
ArrayList < resultEntityKpi > relationCount = new ArrayList < resultEntityKpi > ();
// returnList relationDomain for Relation Count of X & Y
HashMap < String, Integer > tempCount = new HashMap < String, Integer > ();
// tempCount is countDomain for Count of X(right error)
//ArrayList < String > labelsX = new ArrayList < String > ();
//ArrayList < String > confidenceY = new ArrayList < String > ();
//ArrayList<ArrayList<String>> X_Y = new ArrayList<ArrayList<String>> ();
Properties config = getProperties();
String mongoServer = config.getProperty("mongo.server");
String mongoDb = config.getProperty("mongo.db");
try {
//-------Step 1 Relation COUNT Queries--------------------------
relationCount = kpiDistinctRelation(kpiX, domainY, kpiNameSpec);
//-------Step 2 Domain COUNT Queries------------------------------
/**String query = null;sql
query = "SELECT COUNT(*), kpi_name, kpi_element_type," + 
	" domain, element" + 
	" FROM "+kpiX+
	" where kpi_name ='"+kpiNameSpec+
	"' GROUP BY kpi_name, kpi_element_type, domain, element;";

//////System.out.println("533"+query);
ResultSet rs = executeZambia(query);
while (rs.next()) {
//domainCount - Key(AlarmName) Value(count) in tempRelationCount
tempCount.put(rs.getString(2)+rs.getString(5), rs.getInt(1));
}//////System.out.println("2> "+tempCount.toString());*/

MongoClient mongo = new MongoClient(mongoServer, 27017);
MongoDatabase database = mongo.getDatabase(mongoDb);
MongoCollection < Document > collection = database.getCollection(kpiX);

Map < String, Object > groupMap = new HashMap < String, Object > ();
groupMap.put("kpi_name", "$kpi_name");
groupMap.put("kpi_element_type", "$kpi_element_type");
groupMap.put("domain", "$domain");
groupMap.put("element", "$element");
DBObject groupFields = new BasicDBObject(groupMap);
ArrayList < Document > iterDo = collection.aggregate(Arrays.asList(match(eq("kpi_name", kpiNameSpec)), group(
groupFields, sum("count", 1)), project(fields(include("kpi_name", "kpi_element_type", "domain", "element", "count")))))
.into(new ArrayList < Document > ());

JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
String kn = jsonObject_id.optString("kpi_name");
//String kpn = jsonObject_id.optString("kpi_element_type");
//String domain = jsonObject_id.optString("domain");
String elem = jsonObject_id.optString("element");
Integer cnt = jsonObject1.optInt("count");
//////System.out.println("gp: "+aoi+ac+cnt);	
tempCount.put(kn + elem, cnt);
}
//-------Step 3 Placing data to ArrayList by Divide-----------------
for (resultEntityKpi rl: relationCount) {
//////System.out.println(rl.toString());
Integer countX = tempCount.get(rl.getKpi_name() + rl.getElement());
//calculation of Confidence 
//////System.out.println(domainX+" >> "+domainY+" >> "+countX+" >> "+rl.getLeftError());
if (countX != null) {
rl.confidenceCount(countX);
}
mongo.close();
}
} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
}
//////System.out.println(relationCount.toString());
return relationCount;
}
//-----------------------------------------------
//-----------------------------------------------
//------KPI Distinct from 2 Domains--------------
//-----------------------------------------------
//-----------------------------------------------
public ArrayList < resultEntityKpi > kpiDistinctRelation(String kpiX, String domainY, String kpiName) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into kpiDistinctRelation of AIMlDAO ****************");
}
ArrayList < resultEntityKpi > returnList = new ArrayList < resultEntityKpi > ();
Properties config = getProperties();
String mongoServer = config.getProperty("mongo.server");
String mongoDb = config.getProperty("mongo.db");
/* sql
groupQry = "SELECT COUNT(*), kpi_name, kpi_element_type, domain,"
+ " element, domain_Alarm, domain_element_name, domain_severity, domain_alarm_desc"
+" FROM rel_"+kpiX+"_"+domainY
+" WHERE kpi_name = '"+kpiName
+"' GROUP BY kpi_name, kpi_element_type, domain,"
+ " element, domain_Alarm, domain_element_name;";
//+ " ,domain_severity, domain_alarm_desc;";
*/

//////System.out.println("577"+groupQry);
try {
MongoClient mongo = new MongoClient(mongoServer, 27017);
MongoDatabase database = mongo.getDatabase(mongoDb);
MongoCollection < Document > collection = database.getCollection("relnew_" + kpiX + "_" + domainY);

Map < String, Object > groupMap = new HashMap < String, Object > ();
groupMap.put("kpi_name", "$kpi_name");
groupMap.put("kpi_element_type", "$kpi_element_type");
groupMap.put("domain", "$domain");
groupMap.put("element", "$element");
groupMap.put("domain_Alarm", "$domain_Alarm");
groupMap.put("domain_alarm_desc", "$domain_alarm_desc");
DBObject groupFields = new BasicDBObject(groupMap);
ArrayList < Document > iterDo = collection.aggregate(Arrays.asList(match(eq("kpi_name", kpiName)), group(
groupFields, sum("count", 1)), project(fields(include("kpi_name", "kpi_element_type",
"domain", "element", "domain_Alarm", "domain_element_name", "domain_severity", "domain_alarm_desc", "count")))))
.into(new ArrayList < Document > ());

/* sql
groupQry = "SELECT COUNT(*), kpi_name, kpi_element_type, domain,"
+ " element, domain_Alarm, domain_element_name, 
domain_severity, domain_alarm_desc"
+" FROM rel_"+kpiX+"_"+domainY
+" WHERE kpi_name = '"+kpiName
+"' GROUP BY kpi_name, kpi_element_type, domain,"
+ " element, domain_Alarm, domain_element_name;";
//+ " ,domain_severity, domain_alarm_desc;";
*/
JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
String kn = jsonObject_id.optString("kpi_name");
String ket = jsonObject_id.optString("kpi_element_type");
String domain = jsonObject_id.optString("domain");
String elem = jsonObject_id.optString("element");
String domainAlarm = jsonObject_id.optString("domain_Alarm");
String domainAlarmDesc = jsonObject_id.optString("domain_alarm_desc");
String domainEleName = jsonObject1.optString("domain_element_name");
String domainSeverity = jsonObject1.optString("domain_severity");
Integer cnt = jsonObject1.optInt("count");
resultEntityKpi en = new resultEntityKpi(cnt, kn,
ket, domain, elem, domainAlarm,
domainEleName, domainSeverity, domainAlarmDesc);
returnList.add(en);
}
mongo.close();
/*
ResultSet rs = executeZambia(groupQry);
while (rs.next()) {
resultEntityKpi en = new resultEntityKpi(rs.getInt(1), rs.getString(2),
rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6),
rs.getString(7), rs.getString(8), rs.getString(9));
returnList.add(en);
//////System.out.println("1> "+en.toString());
}*/
} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
}
return returnList;
}
public String getCollectiondb(String domain) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getCollectiondb of AIMlDAO ****************");
} //kpiPerformance
String db = null;
Properties config = getProperties();
if (domain.startsWith("huawei_ipran")) {
db = config.getProperty("database.performance_zambia_huawei_ipran");
} else if (domain.startsWith("zte_ipbb")) {
db = config.getProperty("database.performance_zambia_zte_ipbb");
} else if (domain.startsWith("ericsson_ipbb")) {
db = config.getProperty("database.performance_zambia_ericsson_ipbb");
} else if (domain.startsWith("juniper_ipbb")) {
db = config.getProperty("database.performance_zambia_juniper_ipbb");
} else if (domain.startsWith("nokia_radio")) {
db = config.getProperty("database.performance_zambia_nokia_radio");
} else if (domain.startsWith("ericsson_radio")) {
db = config.getProperty("database.performance_zambia_ericsson_radio");
} else if (domain.startsWith("ericsson_microwave")) {
db = config.getProperty("database.performance_zambia_ericsson_microwave");
} else if (domain.startsWith("sam_microwave")) {
db = config.getProperty("database.performance_zambia_sam_microwave");
} else if (domain.startsWith("huawei_microwave")) {
db = config.getProperty("database.performance_zambia_huawei_microwave");
} else if (domain.startsWith("zte_cscore")) {
db = config.getProperty("database.performance_zambia_zte_cscore");
} else if (domain.startsWith("zte_pscore")) {
db = config.getProperty("database.performance_zambia_zte_pscore");
}

return db;
}

//-----------------------------------------------
//-----------------------------------------------
//----------LIVE FORMATION-----------------------
//---------------alarm to alarm------------------
//-----------------------------------------------
public ArrayList < ArrayList < String >> analysisLogicLive(String tableA, String tableB, String comfirmity_100) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into analysisLogic of AIMlDAO ****************");
}
Properties config = getProperties();
int analysisLimit = Integer.parseInt(config.getProperty("analysis.limit"));
String mongoServer = config.getProperty("mongo.server");
String mongoDb = config.getProperty("mongo.db");

ArrayList < String > labelsX = new ArrayList < String > ();
ArrayList < String > confidenceY = new ArrayList < String > ();
ArrayList < ArrayList < String >> X_Y = new ArrayList < ArrayList < String >> ();

DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy_MM_dd");
DateTime now = DateTime.now();
String liveDate = dtf.print(now);
boolean sameDomain = false;
MongoClient mongo = new MongoClient(mongoServer, 27017);
MongoDatabase database = mongo.getDatabase(mongoDb);
MongoCollection < Document > collection = database.getCollection(tableA + "_alarms");
ArrayList < startEntity > cassette = new ArrayList < startEntity > ();
HashMap < String, Integer > setACount = new HashMap < String, Integer > ();
if (tableA.equals(tableB)) {
sameDomain = true;
}
CollectionToColNValPOJO xPojo = getColNamesofDomain(tableA);
try {
ArrayList < Document > iterDo = collection.aggregate(Arrays.asList(project(fields(include(xPojo.colTime,
xPojo.colElement, xPojo.colAlarm))),
match(eq(xPojo.colDate, liveDate))
))
.into(new ArrayList < Document > ());

JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
String start_date = jsonObject1.optString(xPojo.colTime);
String alarm = jsonObject1.optString(xPojo.colElement) + "-" + jsonObject1.optString(xPojo.colAlarm);
DateTimeFormatter strToDate = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss");
//String sec1 = null, sec30 = null, mint5 = null, mint10 = null, mint15 = null;
DateTime secn1 = null, min5 = null, min10 = null, min15 = null;
if (sameDomain == true) { //within domain check +1s
secn1 = strToDate.parseDateTime(start_date).plusSeconds(1);
min5 = secn1.plusSeconds(300);
min10 = secn1.plusSeconds(600);
min15 = secn1.plusSeconds(900);
} else {
secn1 = strToDate.parseDateTime(start_date);
min5 = secn1.plusSeconds(300);
min10 = secn1.plusSeconds(600);
min15 = secn1.plusSeconds(900);
}

String[] datesfromA = new String[33];
for (int h = 0; h < 30; h++) {
datesfromA[h] = strToDate.print(secn1);
secn1 = secn1.plusSeconds(1);
}
datesfromA[30] = strToDate.print(min5);
datesfromA[31] = strToDate.print(min10);
datesfromA[32] = strToDate.print(min15);
////System.out.print(alarm+" >>> "+ sec1+" >>> "+ sec30+" >>> "+ mint5+" >>> "+ mint10+" >>> "+ mint15);
startEntity se = new startEntity(alarm, datesfromA);
cassette.add(se);
setACount = chkNaddHash(setACount, alarm);
}

ArrayList < startEntityAB > setABCount = getGroupByLive(database, cassette, tableB);

for (startEntityAB setAB: setABCount) {
if (analysisLimit != 0) {
//////System.out.println(domainA.toString());
Integer countA = setACount.get(setAB.getLeftError());
setAB.confidenceCount(countA.intValue());
//////System.out.println(domainA.toString());
//			    	String label = null, confidence = null;
//		        	label = domainA.getLeftError()+"<~>"+ domainA.getRightError()
String label = null, confidence = null;
confidence = new DecimalFormat("##").format(setAB.getCount());

label = domainToprefix(tableA) + "~~" + setAB.getLeftError() +
" <~> " + domainToprefix(tableB) + "~~" + setAB.getRightError();
if (comfirmity_100.startsWith("Full") && confidence.startsWith("100")) { //full comfirmity
labelsX.add(label);
confidenceY.add(confidence);
} else if (comfirmity_100.startsWith("NA")) { // all corels
labelsX.add(label);
confidenceY.add(confidence);
}
--analysisLimit;
} else {
//////System.out.println("break");
break;
}
}
mongo.close();
X_Y.add(labelsX);
X_Y.add(confidenceY);

} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
}
return X_Y;
}

//-----------------------------------------------
//-----------------------------------------------
//----------Distinct from 2 Domains--------------
//-----------------------------------------------
//-----------------------------------------------
public ArrayList < startEntityAB > getGroupByLive(MongoDatabase database, ArrayList < startEntity > cassette, String tableB) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getGroupByLIVE of AIMlDAO ****************");
}
ArrayList < startEntityAB > relationWCount = new ArrayList < startEntityAB > ();
MongoCollection < Document > collection = database.getCollection(tableB + "_alarms");
CollectionToColNValPOJO xPojo = getColNamesofDomain(tableB);
for (startEntity sE: cassette) {
//String[] h = {"03-03-202114:01:52","03-03-202115:05:06"};
ArrayList < Document > iterDo = collection.aggregate(Arrays.asList(project(fields(
include(xPojo.colElement, xPojo.colAlarm, xPojo.colTime))), match(and( in (xPojo.colTime, sE.getDatesfromA()), ne(xPojo.colElement, ""), ne(xPojo.colAlarm, ""))))).into(new ArrayList < Document > ());

JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
String alarmB = jsonObject1.optString(xPojo.colElement) + "-" + jsonObject1.optString(xPojo.colAlarm);
startEntityAB rE = new startEntityAB(sE.getAlarm(), alarmB);
relationWCount = chkNadd(relationWCount, rE);
}
}
return relationWCount;
}
//-----------------------------------------------
//-----------------------------------------------
//----------LIVE FORMATION-----------------------
//---------------kpi to alarm------------------
//-----------------------------------------------
public ArrayList < ArrayList < String >> analysisLogicLiveK2A(String kpiVendor, String kpiDomain, String kpiElement, String kpiName, String alarmCollection, String confirmity_100) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into analysisLogicLiveK2A of AIMlDAO ****************");
}
Properties config = getProperties();
int analysisLimit = Integer.parseInt(config.getProperty("analysis.limit"));
String mongoServer = config.getProperty("mongo.server");
String mongoDb = config.getProperty("mongo.db");

ArrayList < String > labelsX = new ArrayList < String > ();
ArrayList < String > confidenceY = new ArrayList < String > ();
ArrayList < ArrayList < String >> X_Y = new ArrayList < ArrayList < String >> ();

DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy_MM_dd");
DateTime now = DateTime.now();
String liveDate = dtf.print(now);

MongoClient mongo = new MongoClient(mongoServer, 27017);
MongoDatabase databaseAlarm = mongo.getDatabase(mongoDb); //alarm
String kpiDB = getCollectiondb(kpiDomain);
MongoDatabase databaseKpi = mongo.getDatabase(kpiDB); //kpi	
MongoCollection < Document > collection = databaseKpi.getCollection(liveDate + "_kpi_alerts");
ArrayList < startEntity > cassette = new ArrayList < startEntity > ();
HashMap < String, Integer > setACount = new HashMap < String, Integer > ();
String DeviceName = "DeviceName";
if (kpiDomain.startsWith("e") || kpiDomain.startsWith("z")) {
DeviceName = "Site_ID";
}
try {
Map < String, Object > groupMap = new HashMap < String, Object > ();
groupMap.put("IPAddress", "$IPAddress");
groupMap.put(DeviceName, "$" + DeviceName);
groupMap.put("KPIName", "$KPIName");
groupMap.put("Date", "$Date");
groupMap.put("Time", "$Time");
if (kpiVendor.equals("ipbb") || kpiVendor.equals("ipran")) {
groupMap.put("Interface", "$Interface");
}
DBObject groupFields = new BasicDBObject(groupMap);
//--------------x---------------------------
ArrayList < Document > list = collection.aggregate(Arrays.asList(
//match(and(eq("admin_id", adminID))),
group(groupFields)
)).into(new ArrayList < Document > ());

JSONArray jsonArray = new JSONArray(JSON.serialize(list));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject = jsonArray.getJSONObject(i);
JSONObject jsonObject_id = jsonObject.getJSONObject("_id");
String start_date = jsonObject_id.optString("Date") + jsonObject_id.optString("Time");
String alarm = null;
alarm = jsonObject_id.optString("IPaddress") + "-" + jsonObject_id.optString(DeviceName) + "-" + jsonObject_id.optString("KPIName");
if (kpiVendor.equals("ipbb") || kpiVendor.equals("ipran")) {
alarm = jsonObject_id.optString("IPaddress") + "-" + jsonObject_id.optString(DeviceName) +
"-" + jsonObject_id.optString("Interface") + "-" + jsonObject_id.optString("KPIName");
}
DateTimeFormatter strToDate = DateTimeFormat.forPattern("yyyy-MM-ddHH:mm:ss");
DateTime secn1 = null;

secn1 = strToDate.parseDateTime(start_date);

String[] datesfromA = new String[300];
for (int h = 0; h < 300; h++) { ///5mins
datesfromA[h] = strToDate.print(secn1);
secn1 = secn1.plusSeconds(1);
}
startEntity se = new startEntity(alarm, datesfromA);
cassette.add(se);
setACount = chkNaddHash(setACount, alarm);
}
//--------------xY---------------------------
ArrayList < startEntityAB > setABCount = getGroupByLive(databaseAlarm, cassette, alarmCollection);

for (startEntityAB setAB: setABCount) {
if (analysisLimit != 0) {
//////System.out.println(domainA.toString());
Integer countA = setACount.get(setAB.getLeftError());
setAB.confidenceCount(countA.intValue());
//////System.out.println(domainA.toString());
//			    	String label = null, confidence = null;
//		        	label = domainA.getLeftError()+"<~>"+ domainA.getRightError()
String label = null, confidence = null;
confidence = new DecimalFormat("##").format(setAB.getCount());

label = domainToprefix(kpiDomain) + "-" + setAB.getLeftError() +
" <~> " + domainToprefix(alarmCollection) + "-" + setAB.getRightError();
if (confirmity_100.startsWith("Full") && confidence.startsWith("100")) { //full confirmity
labelsX.add(label);
confidenceY.add(confidence);
} else if (confirmity_100.startsWith("NA")) { // all corels
labelsX.add(label);
confidenceY.add(confidence);
}
--analysisLimit;
} else {
//////System.out.println("break");
break;
}
}
mongo.close();
X_Y.add(labelsX);
X_Y.add(confidenceY);
} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
}
return X_Y;
}

/////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////
////////////////counters///////////////////////////////////////////
////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////
public HashMap < String, Integer > chkNaddHash(HashMap < String, Integer > er, String c) {
if (log.isDebugEnabled()) { //setACounter
log.debug("*************** checked into chkNaddHash of AIMlDAO ****************");
}
boolean flag = false;
if (er.containsKey(c)) {
flag = true;
er.computeIfPresent(c, (k, v) -> v + 1);
} else {
flag = false;
}

if (!flag) {
er.put(c, 1);
}
return er;

}
public ArrayList < startEntityAB > chkNadd(ArrayList < startEntityAB > x1, startEntityAB c) {
if (log.isDebugEnabled()) { //setAB counter
log.debug("*************** checked into chkNadd of AIMlDAO ****************");
}
boolean flag = false;
for (startEntityAB er: x1) {
if (er.getLeftError().equals(c.getLeftError()) && er.getRightError().equals(c.getRightError())) {
flag = true;
er.setCount(er.getCount() + 1);
break;
} else {
flag = false;
}
}
if (!flag) {
x1.add(c);
}
return x1;
}
public CollectionToColNValPOJO getColNamesofDomain(String domain) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getColNamesofDomain of AIMlDAO ****************");
}

HashMap < String, CollectionToColNValPOJO > collectionCols = new HashMap < String, CollectionToColNValPOJO > ();
///////////SET of Columns name in Collection
CollectionToColNValPOJO xPojo = new CollectionToColNValPOJO("ELEMENT_NAME", "SPECIFIC_PROBLEM", "ELEMENT_NAME", "EVENT_DATE", "INSERTIONTIME");
collectionCols.put("er", xPojo);
xPojo = new CollectionToColNValPOJO("ELEMENT_NAME", "ADDITIONAL_TEXT", "ELEMENT_NAME", "EVENT_DATE", "INSERTIONTIME");
collectionCols.put("no", xPojo);
xPojo = new CollectionToColNValPOJO("HWNMNORTHBOUNDNENAME", "HWNMNORTHBOUNDEVENTNAME", "HWNMNORTHBOUNDDEVICEIP", "HWNMNORTHBOUNDEVENTDATE", "INSERTIONTIME");
collectionCols.put("hu", xPojo);
xPojo = new CollectionToColNValPOJO("ALARMMOCOBJECTINSTANCE", "ALARMCODENAME", "ALARMNEIP", "ALARMEVENTDATE", "INSERTIONTIME");
collectionCols.put("zt", xPojo);
xPojo = new CollectionToColNValPOJO("NODENAME", "ALARMNAME", "NEID", "FIRSTTIMEDETECTEDDATE", "INSERTIONTIME");
collectionCols.put("sa", xPojo);
xPojo = new CollectionToColNValPOJO("NENAME", "ALARMNAME", "NENAME", "ALARMDATE", "INSERTIONTIME");
collectionCols.put("ne", xPojo);

xPojo = new CollectionToColNValPOJO("HWNMNORTHBOUNDNENAME", "HWNMNORTHBOUNDEVENTNAME", "HWNMNORTHBOUNDNENAME", "HWNMNORTHBOUNDEVENTDATE", "INSERTIONTIME");
collectionCols.put("huawei_microwave", xPojo);

xPojo = new CollectionToColNValPOJO("NODENAME", "ALARMNAME", "NODENAME", "FIRSTTIMEDETECTEDDATE", "INSERTIONTIME");
collectionCols.put("sam_microwave", xPojo);

xPojo = new CollectionToColNValPOJO("ELEMENT_NAME", "SPECIFIC_PROBLEM", "ELEMENT_NAME", "EVENT_DATE", "INSERTIONTIME");
collectionCols.put("ericsson_microwave", xPojo);


if(domain.equals("sam_microwave")) {
return collectionCols.get("sam_microwave");
}
else if(domain.equals("huawei_microwave")) {
return collectionCols.get("huawei_microwave");
}
else if(domain.equals("ericsson_microwave")) {
return collectionCols.get("ericsson_microwave");
}
else {
return collectionCols.get(domain.substring(0, 2));
}
}
public Bson getFilterofDomain(String domain, String prev15Dates) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getFilterofDomain of AIMlDAO ****************");

}
//System.out.println("--inside=="+prev15Dates);
HashMap < String, Bson > collectionCols = new HashMap < String, Bson > ();
///////////SET of Filter Rules
Bson xPojo = match(and(eq("NOTIFICATION_TYPE", "NOTIFY_FM_NEW_ALARM"), ne("ELEMENT_NAME", ""), ne("SPECIFIC_PROBLEM", ""), eq("EVENT_DATE", prev15Dates)));
collectionCols.put("er", xPojo);

xPojo = match(and(eq("NOTIFICATION_TYPE", "NOTIFY_FM_NEW_ALARM"), ne("ELEMENT_NAME", ""), ne("ADDITIONAL_TEXT", ""), eq("EVENT_DATE", prev15Dates)));
collectionCols.put("no", xPojo);
xPojo = match(and(or(eq("HWNMNORTHBOUNDNETYPE", java.util.regex.Pattern.compile("^.*CX.*")),eq("HWNMNORTHBOUNDNETYPE", java.util.regex.Pattern.compile("^.*ATN.*"))),ne("HWNMNORTHBOUNDFAULTFLAG", "RECOVERY"),ne("HWNMNORTHBOUNDSEVERITY","MINOR"),ne("HWNMNORTHBOUNDSEVERITY","WARNING"), ne("HWNMNORTHBOUNDNENAME", ""), ne("HWNMNORTHBOUNDEVENTNAME", ""), eq("HWNMNORTHBOUNDEVENTDATE", prev15Dates)));
collectionCols.put("hu", xPojo);
xPojo = match(and(eq("SNMPTRAPOID", "ALARMNEW"), ne("ALARMMOCOBJECTINSTANCE", ""), ne("ALARMCODE", ""), eq("ALARMEVENTDATE", prev15Dates)));
collectionCols.put("zt", xPojo);
xPojo = match(and(eq("LASTTIMECLEARED", "0"), ne("NODENAME", ""), ne("ALARMNAME", ""), eq("FIRSTTIMEDETECTEDDATE", prev15Dates)));
collectionCols.put("sa", xPojo);
xPojo = match(and(ne("ALARMSEVERITY", "CLEARED"), ne("NENAME", ""), ne("ALARMNAME", ""), eq("ALARMDATE", prev15Dates)));
collectionCols.put("ne", xPojo);

xPojo =  match(and(or(eq("HWNMNORTHBOUNDNETYPE", java.util.regex.Pattern.compile("^.*OPTIX.*")),eq("HWNMNORTHBOUNDNETYPE", java.util.regex.Pattern.compile("^.*WDM.*")),eq("HWNMNORTHBOUNDNETYPE", java.util.regex.Pattern.compile("^.*ADM.*"))),ne("HWNMNORTHBOUNDFAULTFLAG","RECOVERY"), eq("HWNMNORTHBOUNDEVENTDATE", prev15Dates)));

collectionCols.put("huawei_microwave", xPojo);

if(domain.equals("huawei_microwave")) {
return collectionCols.get("huawei_microwave");
}

else {
return collectionCols.get(domain.substring(0, 2));
}
}
//-----------------------------------------------
//-----------------------------------------------
//----------LIVE FORMATION-----------------------
//---------------kpi to kpi------------------
//-----------------------------------------------
public ArrayList < ArrayList < String >> analysisLogicLiveK2K(String adminID, String kpiVendor, String kpiDomain, String kpiElement, String kpiName, String kpi2Collection, String confirmity_100) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into analysisLogicLiveK2K of AIMlDAO ****************");
}
Properties config = getProperties();
int analysisLimit = Integer.parseInt(config.getProperty("analysis.limit"));
String mongoServer = config.getProperty("mongo.server");

ArrayList < String > labelsX = new ArrayList < String > ();
ArrayList < String > confidenceY = new ArrayList < String > ();
ArrayList < ArrayList < String >> X_Y = new ArrayList < ArrayList < String >> ();

DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy_MM_dd");
DateTime now = DateTime.now();
String liveDate = dtf.print(now);

MongoClient mongo = new MongoClient(mongoServer, 27017);
String AkpiDB = getCollectiondb(kpiDomain); //kpiA
MongoDatabase AdatabaseKpi = mongo.getDatabase(AkpiDB);
MongoCollection < Document > collection = AdatabaseKpi.getCollection(liveDate + "_kpi_alerts");
ArrayList < startEntity > cassette = new ArrayList < startEntity > ();
HashMap < String, Integer > setACount = new HashMap < String, Integer > ();
String DeviceName = "DeviceName";
if (kpiDomain.startsWith("e") || kpiDomain.startsWith("z")) {
DeviceName = "Site_ID";
}
try {
Map < String, Object > groupMap = new HashMap < String, Object > ();
groupMap.put("IPaddress", "$IPaddress");
groupMap.put(DeviceName, "$" + DeviceName);
groupMap.put("KPIName", "$KPIName");
groupMap.put("Date", "$Date");
groupMap.put("Time", "$Time");
if (kpiVendor.equals("ipbb") || kpiVendor.equals("ipran")) {
groupMap.put("Interface", "$Interface");
}
DBObject groupFields = new BasicDBObject(groupMap);
//--------------x---------------------------
ArrayList < Document > list = collection.aggregate(Arrays.asList(
match(and(eq("admin_id", adminID),
eq(DeviceName, kpiElement),
eq("KPIName", kpiName))),
group(groupFields)
)).into(new ArrayList < Document > ());

JSONArray jsonArray = new JSONArray(JSON.serialize(list));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject = jsonArray.getJSONObject(i);
JSONObject jsonObject_id = jsonObject.getJSONObject("_id");
String start_date = jsonObject_id.optString("Date") + jsonObject_id.optString("Time");
String alarm = jsonObject_id.optString("IPaddress") + "-" + jsonObject_id.optString(DeviceName) + "-" + jsonObject_id.optString("KPIName");
if (kpiVendor.equals("ipbb") || kpiVendor.equals("ipran")) {
alarm = jsonObject_id.optString("IPaddress") + "-" + jsonObject_id.optString(DeviceName) +
"-" + jsonObject_id.optString("Interface") + "-" + jsonObject_id.optString("KPIName");
}
DateTimeFormatter strToDate = DateTimeFormat.forPattern("yyyy-MM-ddHH:mm:ss");
//String sec1 = null, sec30 = null, mint5 = null, mint10 = null, mint15 = null;
DateTime secn1 = null;

secn1 = strToDate.parseDateTime(start_date);

String[] datesfromA = new String[300]; // 5mins
for (int h = 0; h < 300; h++) {
datesfromA[h] = strToDate.print(secn1);
secn1 = secn1.plusSeconds(1);
}
////System.out.print(alarm+" >>> "+ sec1+" >>> "+ sec30+" >>> "+ mint5+" >>> "+ mint10+" >>> "+ mint15);
startEntity se = new startEntity(alarm, datesfromA);
cassette.add(se);
setACount = chkNaddHash(setACount, alarm);
}
//--------------xY---------------------------
String BkpiDB = getCollectiondb(kpi2Collection); //kpiB
MongoDatabase BdatabaseKpi = mongo.getDatabase(BkpiDB);
ArrayList < startEntityAB > setABCount = getGroupByLiveKPI(BdatabaseKpi, cassette, kpi2Collection);

for (startEntityAB setAB: setABCount) {
if (analysisLimit != 0) {
//////System.out.println(domainA.toString());
Integer countA = setACount.get(setAB.getLeftError());
setAB.confidenceCount(countA.intValue());
//////System.out.println(domainA.toString());
//				    	String label = null, confidence = null;
//			        	label = domainA.getLeftError()+"<~>"+ domainA.getRightError()
String label = null, confidence = null;
confidence = new DecimalFormat("##").format(setAB.getCount());

label = domainToprefix(kpiDomain) + "-" + setAB.getLeftError() +
" <~> " + domainToprefix(kpi2Collection) + "-" + setAB.getRightError();
if (confirmity_100.startsWith("Full") && confidence.startsWith("100")) { //full comfirmity
labelsX.add(label);
confidenceY.add(confidence);
} else if (confirmity_100.startsWith("NA")) { // all corels
labelsX.add(label);
confidenceY.add(confidence);
}
--analysisLimit;
} else {
//////System.out.println("break");
break;
}
}
mongo.close();
X_Y.add(labelsX);
X_Y.add(confidenceY);
} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
}
return X_Y;
}
//-----------------------------------------------
//-----------------------------------------------
//----------Distinct from II KPI-----------------
//-----------------------------------------------
//-----------------------------------------------
public ArrayList < startEntityAB > getGroupByLiveKPI(MongoDatabase database, ArrayList < startEntity > cassette, String kpiB) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into getGroupByLIVEKPI of AIMlDAO ****************");

}
String DeviceName = "DeviceName";
if (kpiB.startsWith("e") || kpiB.startsWith("z")) {
DeviceName = "Site_ID";
}
Bson incl = include("IPaddress", DeviceName, "KPIName");
if (kpiB.contains("ipbb") || kpiB.contains("ipran")) {
incl = include("IPaddress", DeviceName, "Interface", "KPIName");
}
ArrayList < startEntityAB > relationWCount = new ArrayList < startEntityAB > ();
DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy_MM_dd");
DateTime now = DateTime.now();
String collectionDate = dtf.print(now) + "_kpi_alerts";
MongoCollection < Document > collection = database.getCollection(collectionDate);
for (startEntity sE: cassette) {
//String[] h = {"03-03-202114:01:52","03-03-202115:05:06"};
ArrayList < Document > iterDo = collection.aggregate(Arrays.asList(project(fields(
incl,
new Document("datecon", new Document("$concat", Arrays.asList("$Date", "$Time"))))), match(and( in ("datecon", sE.getDatesfromA()))))).into(new ArrayList < Document > ());

JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
String alarmB = jsonObject1.optString("IPaddress") + "-" + jsonObject1.optString(DeviceName) + "-" + jsonObject1.optString("KPIName");
if (kpiB.contains("ipbb") || kpiB.contains("ipran")) {
alarmB = jsonObject1.optString("IPaddress") + "-" + jsonObject1.optString(DeviceName) +
"-" + jsonObject1.optString("Interface") + "-" + jsonObject1.optString("KPIName");
}
startEntityAB rE = new startEntityAB(sE.getAlarm(), alarmB);
relationWCount = chkNadd(relationWCount, rE);
}
}
return relationWCount;
}
//-----------------------------------------------
//-----------------------------------------------
//----------LIVE FORMATION-----------------------
//---------------alarm to kpi--------------------
//---------------test ok-------------------------
public ArrayList < ArrayList < String >> analysisLogicLiveA2K(String alarmDomain, String alarmVendor, String alarmElement, String alarmName, String kpiB, String confirmity_100) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into analysisLogicLiveA2K of AIMlDAO ****************");
}
Properties config = getProperties();
int analysisLimit = Integer.parseInt(config.getProperty("analysis.limit"));
String mongoServer = config.getProperty("mongo.server");
String mongoDb = config.getProperty("mongo.db");

ArrayList < String > labelsX = new ArrayList < String > ();
ArrayList < String > confidenceY = new ArrayList < String > ();
ArrayList < ArrayList < String >> X_Y = new ArrayList < ArrayList < String >> ();

DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy_MM_dd");
DateTime now = DateTime.now();
String liveDate = dtf.print(now);
MongoClient mongo = new MongoClient(mongoServer, 27017);
MongoDatabase database = mongo.getDatabase(mongoDb);
MongoCollection < Document > collection = database.getCollection(alarmVendor + "_alarms");
ArrayList < startEntity > cassette = new ArrayList < startEntity > ();
HashMap < String, Integer > setACount = new HashMap < String, Integer > ();
CollectionToColNValPOJO xPojo = getColNamesofDomain(alarmVendor);
try {
ArrayList < Document > iterDo = collection.aggregate(Arrays.asList(project(fields(include(xPojo.colTime,
xPojo.colElement, xPojo.colAlarm))),
match(and(eq(xPojo.colDate, liveDate),
eq(xPojo.colElement, alarmElement),
eq(xPojo.colAlarm, alarmName)))
))
.into(new ArrayList < Document > ());

JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
String start_date = jsonObject1.optString(xPojo.colTime);
String alarm = jsonObject1.optString(xPojo.colElement) + "-" + jsonObject1.optString(xPojo.colAlarm);
DateTimeFormatter strToDate = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
//String sec1 = null, sec30 = null, mint5 = null, mint10 = null, mint15 = null;
DateTime secn1 = null, min5 = null, min10 = null, min15 = null;

secn1 = strToDate.parseDateTime(start_date);
min5 = secn1.plusSeconds(300);
min10 = secn1.plusSeconds(600);
min15 = secn1.plusSeconds(900);

String[] datesfromA = new String[33];
for (int h = 0; h < 30; h++) {
datesfromA[h] = strToDate.print(secn1);
secn1 = secn1.plusSeconds(1);
}
datesfromA[30] = strToDate.print(min5);
datesfromA[31] = strToDate.print(min10);
datesfromA[32] = strToDate.print(min15);
////System.out.print(alarm+" >>> "+ sec1+" >>> "+ sec30+" >>> "+ mint5+" >>> "+ mint10+" >>> "+ mint15);
startEntity se = new startEntity(alarm, datesfromA);
cassette.add(se);
setACount = chkNaddHash(setACount, alarm);
}
String kpiDB2 = getCollectiondb(kpiB);
database = mongo.getDatabase(kpiDB2);
ArrayList < startEntityAB > setABCount = getGroupByLiveKPI(database, cassette, kpiB);

for (startEntityAB setAB: setABCount) {
if (analysisLimit != 0) {
//////System.out.println(domainA.toString());
Integer countA = setACount.get(setAB.getLeftError());
setAB.confidenceCount(countA.intValue());
//////System.out.println(domainA.toString());
//				    	String label = null, confidence = null;
//			        	label = domainA.getLeftError()+"<~>"+ domainA.getRightError()
String label = null, confidence = null;
confidence = new DecimalFormat("##").format(setAB.getCount());

label = domainToprefix(alarmVendor) + "-" + setAB.getLeftError() +
" <~> " + domainToprefix(kpiB) + "-" + setAB.getRightError();
if (confirmity_100.startsWith("Full") && confidence.startsWith("100")) { //full comfirmity
labelsX.add(label);
confidenceY.add(confidence);
} else if (confirmity_100.startsWith("NA")) { // all corels
labelsX.add(label);
confidenceY.add(confidence);
}
--analysisLimit;
} else {
//////System.out.println("break");
break;
}
}
mongo.close();
X_Y.add(labelsX);
X_Y.add(confidenceY);

} catch (Exception e) {
log.error("Exception occurs:-----" + e.getMessage(), e);
}
return X_Y;
}


//from harsh

public static String zte_own_name(String ip,String name,MongoDatabase database) {
	
	MongoCollection<Document> collection=database.getCollection("topologydiscoveryscandetails");	
	String output="";
	
	String dummy_ip="";
	DistinctIterable<String> distinct=collection.distinct("locSysName",eq("ipaddress",ip), String.class);	

	for(String val:distinct) {
		dummy_ip=val;
		break;
	}
	
	
	if(dummy_ip.length()>0) {
		output=dummy_ip;
	}
	
	else {
		output=name;
	}

	return output;
		
	}


public static String nokia_own_ip(String ip,String name,MongoDatabase database) {
	
	MongoCollection<Document> collection=database.getCollection("topologydiscoveryscandetails");	
	String output="";
	
	String dummy_ip="";
	DistinctIterable<String> distinct=collection.distinct("ipaddress",eq("locSysName",name), String.class);	

	for(String val:distinct) {
		dummy_ip=val;
		break;
	}
	
	
	if(dummy_ip.length()>0) {
		output=dummy_ip;
	}
	
	else {
		output=name;
	}

	return output;
		
	}


}