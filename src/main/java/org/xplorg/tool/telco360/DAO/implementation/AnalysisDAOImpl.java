package org.xplorg.tool.telco360.DAO.implementation;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

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

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xplorg.tool.telco360.DAO.interfaces.AnalysisDAO;
import org.xplorg.tool.telco360.config.AiMlDAO;
import org.xplorg.tool.telco360.entity.CollectionToColNValPOJO;
import org.xplorg.tool.telco360.entity.TableHeader;
import org.xplorg.tool.telco360.entity.analysisLogicEntity;
import org.xplorg.tool.telco360.entity.resultAlarmSolutionTable;
import org.xplorg.tool.telco360.entity.resultEntity;
import org.xplorg.tool.telco360.entity.resultEntityKpi;
import org.xplorg.tool.telco360.entity.resultEntityTable;
import org.xplorg.tool.telco360.entity.resultKpiLive;
import org.xplorg.tool.telco360.entity.solutionKpi;
import org.xplorg.tool.telco360.entity.solutionManuplation;
import org.xplorg.tool.telco360.entity.treeChild;
import org.xplorg.tool.telco360.entity.treeMid;
import org.xplorg.tool.telco360.entity.treeParent;
import org.xplorg.tool.telco360.entity.yangDHCP;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;

//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
@Repository("analysisDAO")
public class AnalysisDAOImpl extends AiMlDAO implements AnalysisDAO {

public static String filePath;
Logger log = LogManager.getLogger(AnalysisDAOImpl.class.getName());


public ArrayList < TableHeader > tbHeader(String alarm1, String alarm2) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into tbHeader ****************");
}
ArrayList < TableHeader > ar = new ArrayList < TableHeader > ();
String tb1 = alarmToDomain(alarm1);
String tb2 = alarmToDomain(alarm2);
       
try {

TableHeader f_h1 = new TableHeader(tb1.toUpperCase() + " ALARM", "leftAlarm");
ar.add(f_h1);
TableHeader f_h2 = new TableHeader(tb1.toUpperCase() + " SOLUTION", "leftSol");
ar.add(f_h2);
TableHeader f_h3 = new TableHeader(tb2.toUpperCase() + " ALARM", "rightAlarm");
ar.add(f_h3);
TableHeader f_h4 = new TableHeader(tb2.toUpperCase() + " SOLUTION", "rightSol");
ar.add(f_h4);
TableHeader f_h5 = new TableHeader("DATE-TIME", "solTime");
ar.add(f_h5);

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);

}

return ar;
}
public ArrayList < resultEntityTable > singleAlarmResult(String alarmX) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into singleAlarmResult ****************");
}
ArrayList < resultEntityTable > finalSol = new ArrayList < resultEntityTable > ();
Properties config = getProperties();
String mongoServer = config.getProperty("database.mongodb.ipaddress");
String mongoDb = config.getProperty("mongo.db.database.topology");
MongoClient mongo = new MongoClient(mongoServer, 27017);
MongoDatabase database = mongo.getDatabase(mongoDb);
MongoCollection < Document > collection = database.getCollection("alarm_alarm_resolution");

ArrayList < Document > iterDo = collection.find(eq("LEFT_ALARM", alarmX)).into(new ArrayList < Document > ());
try {
mongo.close();
JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
String leftAlarm = jsonObject1.optString("LEFT_ALARM");
String leftSol = jsonObject1.optString("LEFT_SOL");
String rightAlarm = jsonObject1.optString("RIGHT_ALARM");
String rightSol = jsonObject1.optString("RIGHT_SOL");
String time = jsonObject1.optString("SOL_TIME");
resultEntityTable ret = new resultEntityTable(leftAlarm, leftSol,
rightAlarm, rightSol, time);
finalSol.add(ret);
}

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);

}

return finalSol;

}
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public int insertUserAlarmSol(solutionManuplation sm) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into insertAlarmSol ****************");
}
int flag = 0;
Properties config = getProperties();
String mongoServer = config.getProperty("database.mongodb.ipaddress");
String mongoDb = config.getProperty("mongo.db.database.topology");
MongoClient mongo = new MongoClient(mongoServer, 27017);
MongoDatabase database = mongo.getDatabase(mongoDb);

MongoCollection < Document > collection = database.getCollection("user_def_rel");
String leftDomain = sm.getDesc1();
String rightDomain = sm.getDesc2();
String leftAlarm = sm.getAlarm1();
String rightAlarm = sm.getAlarm2();
Document document = new Document("leftDomain", leftDomain)
.append("leftAlarm", leftAlarm)
.append("rightDomain", rightDomain)
.append("rightAlarm", rightAlarm);
try {
ArrayList < Document > iterDo = collection.find((and(eq("leftDomain", leftDomain),
eq("rightDomain", rightDomain), eq("leftAlarm", leftAlarm), eq("rightAlarm", rightAlarm)))).into(new ArrayList < Document > ());
if (iterDo.isEmpty()) {
collection.insertOne(document);
flag = 1; //nseted
} else {
flag = 2; //already exist
}
mongo.close();
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);

}
return flag;
}
//-------------------------------file user corel---------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public int userCorelFile(MultipartFile file) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into userCorelFile ****************");
}
int res = 0;
Properties config = getProperties();
ArrayList < solutionManuplation > smAl = new ArrayList < solutionManuplation > ();

Path rootLocation = Paths.get(config.getProperty("server.directory"));
String file_store = file.getOriginalFilename();
File file_check = new File(rootLocation + "/" + file_store);
String extention = FilenameUtils.getExtension(rootLocation + "/" + file_store);

try {
if (file_check.exists()) {
Files.delete(rootLocation.resolve(file_store));
Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
} else {
Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
}

//////System.out.println(extention);
if (extention.equals("xlsx")) {
//////System.out.println(extention);
smAl = excel2Array(ResourceUtils.getFile(rootLocation + "/" + file_store));
} else if (extention.equals("csv")) {
//////System.out.println(extention);
smAl = csv2Array(ResourceUtils.getFile(rootLocation + "/" + file_store));
} else if (extention.equals("xml")) {
smAl = xml2Array(ResourceUtils.getFile(rootLocation + "/" + file_store));
} else {
return res;
}
for (solutionManuplation sm: smAl) {
//////System.out.println(sm.toString());
res = insertUserAlarmSol(sm);
}

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return res;
}
//-------------------------------------------------------------
//------alarm/element based analysis and solution------------------------
//-------------------------------------------------------------

//TODO  for element level corel
public ArrayList < resultAlarmSolutionTable > alarmAnalysisTable(String domainX, String elementName, String alarmName, String domainY, String conRate) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into AlarmAnalysisTable ****************");
}




//System.out.println("F366>>>>>>>>>>>>>>>>>>>." + domainX + ">>" + domainY + "===="+elementName +"======"+ alarmName);

ArrayList < resultAlarmSolutionTable > sol = new ArrayList < resultAlarmSolutionTable > ();
//alarmX to domainX
//String domainX = alarmToDomain(elementName);//NOT APPLICABLE
String alX = elementName + "~~" + alarmName; //NOT APPLICABLE

//confidence

ArrayList < resultEntity > relationCount = new ArrayList < resultEntity > ();
//-H-count each combination with its count
relationCount = getGroupBy(domainX, domainY, alX);
//System.out.println(relationCount);
ArrayList < ArrayList < String >> min1Result = new ArrayList < ArrayList < String >> ();

int conf = Integer.parseInt(conRate); //////System.out.println(conf+">>>>>>>>CONF");
if (!alarmName.equals("LIKE911")) { // element All corels
min1Result = analysisLogic(domainX, domainY, 0, relationCount, 100);

} else { //elem-alarm 1005 corels
min1Result = analysisLogic(domainX, domainY, 0, relationCount, conf);

}
//System.out.println(min1Result);
ArrayList < String > labelsX = min1Result.get(0);

ArrayList < String > confidenceY = min1Result.get(1);
//////System.out.println(confidenceY);

try {
	for (int i = 0; i < labelsX.size(); i++) {
		//System.out.println(labelsX.get(i));
	}
	
for (int i = 0; i < labelsX.size(); i++) {
////System.out.println(labelsX.get(i));

String confidence;
try {
	confidence = confidenceY.get(i) + "%";
} catch (Exception e) {
	confidence="-";
	
}

String left_side = "";
try {
	left_side = labelsX.get(i).split(" <~>")[0];
} catch (Exception e) {
	left_side="";
}
String right_side;
try {
	right_side = labelsX.get(i).split(" <~>")[1];
} catch (Exception e) {
	right_side="-";
}


String left_element_name;
try {
	left_element_name = left_side.split("~~")[2];
} catch (Exception e) {
	left_element_name="-";
}
String left_ip="";
try {
	left_ip = left_side.split("~~")[1];
} catch (Exception e) {
	
	left_ip="-";
}
String left_alarm;
try {
	left_alarm = left_side.split("~~")[3];
} catch (Exception e) {
	left_alarm="-";
}
String show_left_element_name="";

if(domainX.endsWith("_microwave")) {
show_left_element_name=left_element_name;
}

else {
show_left_element_name=left_element_name+"  ("+left_ip+")";	
}




String right_element_name=right_side.split("~~")[2];
String right_ip=right_side.split("~~")[1];
String right_alarm=right_side.split("~~")[3];

String show_right_element_name="";

if(domainY.endsWith("_microwave")) {
	show_right_element_name=right_element_name;	
}

else {
	show_right_element_name=right_element_name+"  ("+right_ip+")";
}





resultAlarmSolutionTable rast = new resultAlarmSolutionTable(confidence, "DISABLED", show_left_element_name, left_alarm, "DISABLED", "-", show_right_element_name, right_alarm);


sol.add(rast);
}

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
ex.printStackTrace();
return null;

}
return sol;

}

/*-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public ArrayList < TableHeader > alarmAnalysisTableHeader(String domainY, String alarmX) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into AlarmAnalysisTableHeader ****************");
}///////////////////////////////DISBALED NOT USED BUT NOT TESTED
ArrayList < TableHeader > ar = new ArrayList < TableHeader > ();
String tb1 = alarmToDomain(alarmX);
String	tb2 = domainY;
//////System.out.println(tb1 +" >> "+ tb2);        
try {

// The column count starts from 1
TableHeader f_h1 = new TableHeader( "CONFIDENCE", "count");
ar.add(f_h1);
TableHeader f_h2 = new TableHeader(tb1.toUpperCase()+" ALARM", "alarmX");
ar.add(f_h2);
TableHeader f_h3 = new TableHeader(tb1.toUpperCase()+" SOLUTION", "solX");
ar.add(f_h3);
TableHeader f_h4 = new TableHeader(tb2.toUpperCase()+" ALARM", "alarmY");
ar.add(f_h4);
TableHeader f_h5 = new TableHeader(tb2.toUpperCase()+" SOLUTION", "solY");
ar.add(f_h5);
TableHeader f_h6 = new TableHeader("DATE-TIME", "solTime");
ar.add(f_h6);

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}

return ar;
}*/
//-------------------------------------------------------------
//------alarm/element user defined and solution------------------------
//-------------------------------------------------------------
public ArrayList < resultAlarmSolutionTable > alarmAnalysisTableUser(String domainX, String elementName, String alarmName, String domainY) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into AlarmAnalysisTable ****************");
} //////System.out.println("F481>>>>>>>>>>"+domainX+">>>"+  domainY+   elementName+ alarmName);
ArrayList < resultAlarmSolutionTable > sol = new ArrayList < resultAlarmSolutionTable > ();
//alarmX to domainX
//String domainX = alarmToDomain(elementName);
String alX = elementName + "-" + alarmName; //////System.out.println(alX);
//resolution
//ArrayList <resultEntityTable> mid2Result = new ArrayList <resultEntityTable>(); NOT APPLICABLE
//mid2Result = tbSolution(elementName.substring(3), alarmName); NOT APPLICABLE
Properties config = getProperties();
String mongoServer = config.getProperty("database.mongodb.ipaddress");
String mongoDb = config.getProperty("mongo.db.database.topology");
MongoClient mongo = new MongoClient(mongoServer, 27017);
MongoDatabase database = mongo.getDatabase(mongoDb);
MongoCollection < Document > collection = database.getCollection("user_def_rel");
try {
//ArrayList < String > labelsX = new ArrayList < String > ();
//////System.out.println("user_def_rel");
ArrayList < Document > iterDo = collection.find((and(eq("leftDomain", domainX),
eq("rightDomain", domainY), eq("leftAlarm", Pattern.compile("^" + alX.replace("LIKE911", "")))
))
//eq("rightAlarm", Pattern.compile("^"+alX.replace("LIKE911", ""))))))
).into(new ArrayList < Document > ());

mongo.close();
JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
String leftAlarm[] = jsonObject1.optString("leftAlarm").split("-", 2);
String rightAlarm[] = jsonObject1.optString("rightAlarm").split("-", 2);
//labelsX.add(leftAlarm+" <~> "+rightAlarm); //NOT APPLICABLE UNCOMNT WHEN WITH SOLUTION

resultAlarmSolutionTable rast = new resultAlarmSolutionTable("100%", "DISABLED", leftAlarm[0], leftAlarm[1], "DISABLED", "DISABLED", rightAlarm[0], rightAlarm[1]);
sol.add(rast);
}

//user corels NOT APPLICABLE
/*		
String alarmAI = null, elementAI = null, rightAlarmName = null;
for(int i=0; i<labelsX.size(); i++) {
	String alarms[] = labelsX.get(i).split(" <~> ", 2);
	String leftAlarm[] = alarms[0].split("-", 3);
	alarmAI = leftAlarm[2];elementAI = leftAlarm[1];
	rightAlarmName = alarms[1];
	if(mid2Result.size()>0) {
		for(int j=0; j<mid2Result.size(); j++) {
			String ticketIDTS = mid2Result.get(j).getTicketID();
			String elementNameTS = mid2Result.get(j).getNeName();
			String alarmTS = mid2Result.get(j).getAlarmName();
			String solTS = mid2Result.get(j).getSol();
			String solTimeTS = mid2Result.get(j).getSolTime();
			if(elementAI.equals(elementNameTS) && alarmAI.equals(alarmTS)) {
					resultAlarmSolutionTable rast = new resultAlarmSolutionTable("100%",ticketIDTS, elementAI, alarmAI, solTS, solTimeTS, rightAlarmName);
					sol.add(rast);
					}
			else {
				resultAlarmSolutionTable rast = new resultAlarmSolutionTable("100%","DNF", elementAI, alarmAI, "DNF", "DNF",rightAlarmName);
				sol.add(rast);
			}
			}//end of j for
		}//end of if
	else {
	resultAlarmSolutionTable rast = new resultAlarmSolutionTable("100%","DNF", elementAI, alarmAI, "DNF", "DNF",rightAlarmName);
	sol.add(rast);
	}		
}//end of i
*/
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
return null;
//ex.printStackTrace();
} //////System.out.println(sol.toString());
return sol;

}
/*
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public String tbAlarmFileSolution(String alarm, String severity) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into tbAlarmFileSolution ****************");
}
ArrayList < String > colName = new ArrayList < String > ();
ArrayList < String > ar2 = new ArrayList < String > ();
ArrayList < String > ar3 = new ArrayList < String > ();


ResultSet rs = null;
//////System.out.println(alarm+">>>"+severity);
String query = null;
if (alarm.equals("-") && severity.equals("-")) {
query = "SELECT * FROM alarmfile_1";
} else if (severity.equals("-")) {
query = "SELECT * FROM alarmfile_1 where ALARM_ID='" + alarm + "';";
} else if (alarm.equals("-")) {
query = "SELECT * FROM alarmfile_1 where SEVERITY='" + severity + "';";
} else {
query = "SELECT * FROM alarmfile_1 where ALARM_ID='" + alarm + "' and SEVERITY='" + severity + "';";
}

//ALARM_ID,SPECIFIC_PROBLEM,MANAGED_OBJECT,SEVERITY,CLEARED,CLEARING,ACKNOWLEDGED,ACK_USER_ID,ACK_TIME,ALARM_TIME,EVENT_TYPE,APPLICATION,IDENTIF_APPL_ADDL_INFO,APPL_ADDL_INFO
//////System.out.println(query);
try {

rs = execute(query);
int columnCount = rs.getMetaData().getColumnCount();

// The column count starts from 1
for (int i = 1; i <= columnCount; i++) {
String name = rs.getMetaData().getColumnName(i);
colName.add(name);
}

while (rs.next()) {
String s = "";
for (int a = 0; a < colName.size(); a++) {
s += "\"" + colName.get(a).trim() + "\"" + ":" + "\"" + rs.getString(a + 1).trim() + "\"" + ",";
}
String ss = s.substring(0, s.length() - 1);
ar2.add(ss.trim());
}

for (int i = 0; i < ar2.size(); i++) {
ar3.add("{" + ar2.get(i).toString().trim() + "}");
}

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return ar3.toString();

}
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public ArrayList < TableHeader > tbAlarmFileHeader(String tableName) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into tbAlarmFileHeader ****************");
}
//-------------------------------common table header
//use in AlarmFile, trouble_ticket, kpi-resolution
//-------------------------------
ArrayList < TableHeader > ar = new ArrayList < TableHeader > ();
ResultSet rs = null;
String query = "SELECT * FROM "+tableName+" limit 1";
//////System.out.println("head"+query);
try {


rs = execute(query);
int columnCount = rs.getMetaData().getColumnCount();

// The column count starts from 1
for (int i = 1; i <= columnCount; i++) {
String name = rs.getMetaData().getColumnName(i);
TableHeader f_h = new TableHeader(name, name);
//////System.out.println(name);//point1
ar.add(f_h);
}
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}

return ar;
}
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public String tableDataTicketAi(String alarm) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into tableDataTicketAi ****************");
}

ArrayList < String > colName = new ArrayList < String > ();
ArrayList < String > ar2 = new ArrayList < String > ();
ArrayList < String > ar3 = new ArrayList < String > ();

ResultSet rs = null;
String query ="SELECT * FROM trouble_ticket"
+ " WHERE ticket_id IN "
+ "(SELECT ticket FROM ticket_ai"
+ " WHERE alarms='"+alarm+"')";
try {
rs = execute(query);
int columnCount = rs.getMetaData().getColumnCount();

// The column count starts from 1
for (int i = 1; i <= columnCount; i++) {
String name = rs.getMetaData().getColumnName(i);
colName.add(name.replace("COUNT(*)", "COUNT"));
}

while (rs.next()) {
String s = "";
for (int a = 0; a < colName.size(); a++) {
s += "\"" + colName.get(a).trim() + "\"" + ":" + "\"" + rs.getString(a + 1).trim() + "\"" + ",";
}
String ss = s.substring(0, s.length() - 1);
ar2.add(ss.trim());
}

for (int i = 0; i < ar2.size(); i++) {
ar3.add("{" + ar2.get(i).toString().trim() + "}");
}

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return ar3.toString();

}
//-------------------------------------------------------------
//--------------------common table solution--------------------
//-------------------------------------------------------------
public String tableDataKpiResolution(String tableName) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into tableDataTicketAi ****************");
}
//USE in KPI_RESOLUTION
ArrayList < String > colName = new ArrayList < String > ();
ArrayList < String > ar2 = new ArrayList < String > ();
ArrayList < String > ar3 = new ArrayList < String > ();

ResultSet rs = null;
String query ="SELECT * FROM "+tableName;
//////System.out.println("SOLUTION "+query);
try {
rs = execute(query);
int columnCount = rs.getMetaData().getColumnCount();

// The column count starts from 1
for (int i = 1; i <= columnCount; i++) {
String name = rs.getMetaData().getColumnName(i);
colName.add(name);
}

while (rs.next()) {
String s = "";
for (int a = 0; a < colName.size(); a++) {
s += "\"" + colName.get(a).trim() + "\"" + ":" + "\"" + rs.getString(a + 1).trim() + "\"" + ",";
}
String ss = s.substring(0, s.length() - 1);
ar2.add(ss.trim());
}

for (int i = 0; i < ar2.size(); i++) {
ar3.add("{" + ar2.get(i).toString().trim() + "}");
}
//////System.out.println(ar3.toString());
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return ar3.toString();

}*/
public int insertAlarmSol(solutionManuplation sm) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into insertAlarmSol ****************");
}
int flag = 0;
Properties config = getProperties();
String mongoServer = config.getProperty("database.mongodb.ipaddress");
String mongoDb = config.getProperty("mongo.db.database.topology");
MongoClient mongo = new MongoClient(mongoServer, 27017);
MongoDatabase database = mongo.getDatabase(mongoDb);
MongoCollection < Document > collection = database.getCollection("alarm_alarm_resolution");
LocalDateTime localDateTime = new LocalDateTime();
DateTimeFormatter dateForm = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss");
String currTime = dateForm.print(localDateTime);
//String alarmLeft = sm.getAlarm1().substring(3);
//String alarmRight = sm.getAlarm2().substring(3);

String alarmLeft1 = sm.getAlarm1();
String alarmRight1 = sm.getAlarm2();

boolean bolnCheck = false;

String alarmLeft = "";
String alarmRight = "";
try {
if (alarmLeft1.charAt(2) == '-') {
alarmLeft = alarmLeft1.substring(3);
} else {
alarmLeft = alarmLeft1;
bolnCheck = true;
}

if (alarmRight1.charAt(2) == '-') {
alarmRight = alarmRight1.substring(3);
} else {
alarmRight = alarmRight1;
bolnCheck = true;
}
String query = "";
if (bolnCheck == false) {
Document document = new Document("LEFT_ALARM", alarmLeft)
.append("LEFT_SOL", sm.getDesc1()).append("RIGHT_ALARM", alarmRight)
.append("RIGHT_SOL", sm.getDesc2()).append("SOL_TIME", currTime);
collection.insertOne(document);
flag = 1;
} else {
query = "Update `alarm_alarm_resolution` set LEFT_SOL='" + sm.getDesc1() + "',RIGHT_SOL='" + sm.getDesc2() + "' where LEFT_ALARM='" + alarmLeft + "' and RIGHT_ALARM='" + alarmRight + "'";
UpdateResult updateResult = collection.updateMany(and(eq("LEFT_ALARM", alarmLeft), eq("RIGHT_ALARM", alarmRight)),
combine(set("LEFT_SOL", sm.getDesc1()), set("RIGHT_SOL", sm.getDesc2())));
flag = 1; //Integer.parseInt(updateResult.getModifiedCount());
}

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return flag;
}
//-------------------------------------------------------------
//-------------------------------------------------------------
public int insertKpiSol(solutionKpi ks) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into insertKpiSol ****************");
}
int flag = 0;
String query = "INSERT INTO `kpi_resolution` " +
"VALUES ('" + ks.getElement() + "', '" +
ks.getKpiName() + "', '" + ks.getAlarms() + "', '" + ks.getSolution() + "');";
try {
flag = Update(query);
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return flag;
}
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public ArrayList < treeParent > treeAlarm(String domainX, String domainY, String comfirmity_100) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into alarmTree ****************");
} // ////System.out.println(domainX+"TREE"+domainY+">"+comfirmity_100);
ArrayList < treeParent > result = new ArrayList < treeParent > ();
try {
String header = domainX.toUpperCase() + vendortodomain(domainX) + " " + domainY.toUpperCase() + vendortodomain(domainY);
ArrayList < resultEntity > relationCount = new ArrayList < resultEntity > ();
//////System.out.println(header);
relationCount = getGroupBy(domainX, domainY, "NA");
//rcNull=false;
//////System.out.println(i+j+" true");
//////System.out.println(relationCount);
int conf = Integer.parseInt(comfirmity_100); //greater than
ArrayList < treeMid > tml = new ArrayList < treeMid > ();
ArrayList < treeChild > alChild = new ArrayList < treeChild > ();
ArrayList < ArrayList < String >> labelValues = new ArrayList < ArrayList < String >> ();
labelValues = analysisLogic(domainX, domainY, 0, relationCount, conf);
ArrayList < String > labelsX = labelValues.get(0);
ArrayList < String > confidenceY = labelValues.get(1);
String label = null;
for (int z = 0; z < labelsX.size(); z++) { //////System.out.println("saya"+ labelsX.get(z));
String LeftError0 = labelsX.get(z).split(" <~> ", 2)[0];
if (z == 0) { //0
treeChild tc1 = new treeChild(labelsX.get(z), confidenceY.get(z));
alChild.add(tc1);
label = labelsX.get(z).split(" <~> ", 2)[0];
} else if (LeftError0.equals(labelsX.get(z - 1).split(" <~> ", 2)[0])) { //prev!
treeChild tc2 = new treeChild(labelsX.get(z), confidenceY.get(z));
alChild.add(tc2);
label = labelsX.get(z - 1).split(" <~> ", 2)[0];
} else {
treeMid tm = new treeMid(label, alChild);
tml.add(tm);
alChild = new ArrayList < treeChild > (); //empty
treeChild tc = new treeChild(labelsX.get(z), confidenceY.get(z));
alChild.add(tc);
label = labelsX.get(z).split(" <~> ", 2)[0];
}
}
treeMid tm = new treeMid(label, alChild);
tml.add(tm);
treeParent tp = new treeParent(header + " (System)", tml);
result.add(tp);

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
//////System.out.println(result.toString());
return result;
}

public ArrayList < treeParent > treeAlarmUser(String domainX, String domainY) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into alarmTreeUser ****************");
} //////System.out.println(domainY+"TREE"+domainX);
ArrayList < treeParent > result = new ArrayList < treeParent > ();
String header = domainX.toUpperCase() + vendortodomain(domainX) + " " + domainY.toUpperCase() + vendortodomain(domainY);
try {
ArrayList < treeMid > tmlUs = new ArrayList < treeMid > ();
ArrayList < String > usAL = new ArrayList < String > ();
Properties config = getProperties();
String mongoServer = config.getProperty("database.mongodb.ipaddress");
String mongoDb = config.getProperty("mongo.db.database.topology");
MongoClient mongo = new MongoClient(mongoServer, 27017);
MongoDatabase database = mongo.getDatabase(mongoDb);
MongoCollection < Document > collection = database.getCollection("user_def_rel");

ArrayList < Document > iterDo = collection.find(and(eq("leftDomain", domainX.replace("_alarms", "")),
eq("rightDomain", domainY.replace("_alarms", "")))).sort(ascending("leftAlarm"))
.into(new ArrayList < Document > ());

mongo.close();
JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
String leftAlarm = jsonObject1.optString("leftAlarm");
String leftSol = jsonObject1.optString("rightAlarm");
usAL.add(leftAlarm + " <~> " + leftSol);
//////System.out.println("878 "+leftAlarm);
}
ArrayList < treeChild > alChildUs = new ArrayList < treeChild > ();
String label = null;
for (int z = 0; z < usAL.size(); z++) {
String LeftError0 = usAL.get(z).split(" <~> ", 2)[0];
if (z == 0) { //0
treeChild tcUs1 = new treeChild(usAL.get(z), "100");
alChildUs.add(tcUs1);
label = usAL.get(z).split(" <~> ", 2)[0];
} else if (LeftError0.equals(usAL.get(z - 1).split(" <~> ", 2)[0])) { //prev!
treeChild tcUs2 = new treeChild(usAL.get(z), "100");
alChildUs.add(tcUs2);
label = usAL.get(z - 1).split(" <~> ", 2)[0];
} else {
treeMid tmUs = new treeMid(label, alChildUs);
tmlUs.add(tmUs);
alChildUs = new ArrayList < treeChild > (); //empty
treeChild tcUs = new treeChild(usAL.get(z), "100");
alChildUs.add(tcUs);
label = usAL.get(z).split(" <~> ", 2)[0];
}
}
treeMid tmUs = new treeMid(label, alChildUs);
tmlUs.add(tmUs);
treeParent tpUs = new treeParent(header + " (User)", tmlUs);
result.add(tpUs);
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
//////System.out.println(result.toString());
return result;
}
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public String boundElement(String alarm1, String alarm2) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into boundElement ****************");
}
String resolution = null;
try {
resolution = elementName(alarm1, alarm2);
//////System.out.println("bounded "+ resolution);
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return resolution;
}
/*  public ArrayList<String> getKpiNameExcel(){
//HARSH kpi Name 
ArrayList<String> kpiNames = new ArrayList<String>();

String query = "SELECT kpi_name from kpi_resolution";
try {
ResultSet rs = execute(query);
while(rs.next()) 
 {
	kpiNames.add(rs.getString(1));
}
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return kpiNames;

}*/
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public int updateKpiTable(String kpiName, String groupName, String f1) {
int flag = 0;
String query = "UPDATE sgsn_kpi_formula SET kpi_formula = '" + f1 +
"' WHERE kpi_name = '" + kpiName + "' and groups = '" + groupName + "';";
try {
flag = Update(query);
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
}
return flag;
}
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public ArrayList < resultEntityKpi > kpiAlarmAnalysis(String kpiX, String domainY, String kpiName) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into kpiAlamAnalysis ****************");
} ///////System.out.println(kpiX+" > "+ domainY+" > " +kpiName);
ArrayList < resultEntityKpi > labelValues = new ArrayList < resultEntityKpi > ();
labelValues = kpiAnalysisLogic(kpiX, domainY, kpiName);
return labelValues;
}
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public ArrayList < resultKpiLive > kpiAlarmLive(String tableName, String dttm, int inTime) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into kpiAlarmLiveR ****************");
}
////System.out.println("1008Live>>>>>>>>>>>>>>>>>" + tableName + "inTime" + inTime);
ArrayList < resultKpiLive > result = new ArrayList < resultKpiLive > ();
String query = null;
DateTimeFormatter strToDate = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
DateTime s_dt = strToDate.parseDateTime(dttm);
DateTime e_dt = s_dt.plusMinutes(inTime);
//Timestamp to Date & Time
DateTimeFormatter dateForm = DateTimeFormat.forPattern("yyyy-MM-dd");
String startDate = dateForm.print(s_dt);
String endDate = dateForm.print(e_dt);
DateTimeFormatter timeForm = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
String startTime = timeForm.print(s_dt);
String endTime = timeForm.print(e_dt);

Properties config = getProperties();
String mongoServer = config.getProperty("database.mongodb.ipaddress");
String mongoDb = config.getProperty("mongo.db.database.topology");
MongoClient mongo = new MongoClient(mongoServer, 27017);
MongoDatabase database = mongo.getDatabase(mongoDb);
MongoCollection < Document > collection = database.getCollection(tableName + "_alarms");
CollectionToColNValPOJO xPojo = getColNamesofDomain(tableName);

Bson date = null;
if (startDate.equals(endDate)) {
date = eq(xPojo.colDate, startDate);
} else {
date = in (xPojo.colDate, Arrays.asList(startDate, endDate));
}
try {
ArrayList < Document > iterDo = collection.aggregate(Arrays.asList(project(fields(include(xPojo.colDate, xPojo.colTime,
xPojo.colElement, xPojo.colAlarm))),
match(and(date, gte(xPojo.colTime, startTime), lte(xPojo.colTime, endTime)))
)).into(new ArrayList < Document > ());

mongo.close();
JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
for (int i = 0; i < jsonArray.length(); i++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(i);
String a_date = jsonObject1.optString(xPojo.colDate);
String a_time = jsonObject1.optString(xPojo.colTime);
String element = jsonObject1.optString(xPojo.colElement);
String alarm = jsonObject1.optString(xPojo.colAlarm);
resultKpiLive rkl = new resultKpiLive(a_date, a_time, alarm, element, "DISABLED", "DISABLED");
result.add(rkl);
}
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
return null;
}
return result;
}
public ArrayList < solutionManuplation > excel2Array(File file) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into read_excel ****************");
}

ArrayList < solutionManuplation > ar1 = new ArrayList < solutionManuplation > ();
try {
InputStream ExcelFileToRead = new FileInputStream(file.getAbsolutePath());
XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);
XSSFRow row;
XSSFCell cell0, cell1, cell2, cell3;

XSSFSheet sheet0 = wb.getSheetAt(0);

Iterator rows = sheet0.rowIterator();

while (rows.hasNext()) {
row = (XSSFRow) rows.next();
cell0 = row.getCell(0, row.CREATE_NULL_AS_BLANK);
String domainLeft = cell0.toString();
cell1 = row.getCell(1, row.CREATE_NULL_AS_BLANK);
String alarmLeft = cell1.toString();
cell2 = row.getCell(2, row.CREATE_NULL_AS_BLANK);
String domainRight = cell2.toString();
cell3 = row.getCell(3, row.CREATE_NULL_AS_BLANK);
String alarmRight = cell3.toString();
solutionManuplation sm = new solutionManuplation(domainLeft, alarmLeft, domainRight, alarmRight);
ar1.add(sm);
}

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return ar1;
}
public ArrayList < solutionManuplation > csv2Array(File file) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into csv_excel ****************");
}

ArrayList < solutionManuplation > ar1 = new ArrayList < solutionManuplation > ();
try {
FileReader fr = new FileReader(file.getAbsolutePath());

BufferedReader br = new BufferedReader(fr);
String line = "";
String[] tempArr;
while ((line = br.readLine()) != null) {
tempArr = line.split(",", 4);
String domainLeft = tempArr[0];
String alarmLeft = tempArr[1];
String domainRight = tempArr[2];
String alarmRight = tempArr[3];
solutionManuplation sm = new solutionManuplation(domainLeft, alarmLeft, domainRight, alarmRight);
ar1.add(sm);
}
br.close();

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return ar1;
}
public ArrayList < solutionManuplation > xml2Array(File file) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into xml_excel ****************");
}

ArrayList < solutionManuplation > ar1 = new ArrayList < solutionManuplation > ();
try {
FileReader fr = new FileReader(file);
BufferedReader br = new BufferedReader(fr);
String line = null;
String domainLeft = null, alarmLeft = null, domainRight = null, alarmRight = null;
while ((line = br.readLine()) != null) {
if (line.contains("domain_alfa")) {
domainLeft = (line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
} else if (line.contains("alarm_alfa")) {
alarmLeft = (line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
} else if (line.contains("domain_beta")) {
domainRight = (line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
} else if (line.contains("alarm_beta")) {
alarmRight = (line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
} else if (line.contains("</corel")) {
solutionManuplation sm = new solutionManuplation(domainLeft, alarmLeft, domainRight, alarmRight);
ar1.add(sm);
domainLeft = null;
alarmLeft = null;
domainRight = null;
alarmRight = null;
} else if (line.contains("</user")) {
break;
}
}
br.close();

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return ar1;
}

//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
/////////////////////////////////////////////ssh session
public Session getSession(String ipaddress, String username, String password, int port) throws Exception {
JSch jsch = new JSch();
Session session = jsch.getSession(username, ipaddress, port);
Properties config = new Properties();
config.put("StrictHostKeyChecking", "no");
session.setConfig(config);;
session.setPassword(password);
session.connect();

return session;
}
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
///////////////////////////////////////NETCONF UI============------------
public int netconfUIedit(yangDHCP dhcp) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into netconfUIedit ****************");
} //////System.out.println(dhcp.toString());
int finalResult = 0;
try {
String mlt = dhcp.getMlt();
String dlt = dhcp.getDlt();
//String subnet=dhcp.getSubnet().getNet();
//String subnetMask=dhcp.getSubnet().getMask();
String subnetDynamicboolp = dhcp.getSubnet().getDbp();
String subnetRangeLow = dhcp.getSubnet().getRangeLow();
String subnetRangeHigh = dhcp.getSubnet().getRangeHigh();
String rpc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<hello xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">" +
"  <capabilities>" +
"    <capability>urn:ietf:params:netconf:base:1.0</capability>" +
"  </capabilities>" +
"</hello>" +
"]]>]]>" +
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\"  message-id=\"1\">" +
" <edit-config>" +
"    <target>" +
"      <running/>" +
"    </target>" +
"    <config>" +
"      <dhcp xmlns=\"http://tail-f.com/ns/example/dhcpd\"" +
"            xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">" +
"		<max-lease-time nc:operation=\"merge\">" +
"          " + mlt +
"        </max-lease-time>" +
"		<default-lease-time nc:operation=\"merge\">" +
"          " + dlt +
"        </default-lease-time>" +
"        <subnets>" +
"          <subnet nc:operation=\"merge\">" +
"            <net>192.168.100.0</net>" +
"            <mask>255.255.255.0</mask>" +
"            <range>" +
"              <dynamic-bootp>" + subnetDynamicboolp + "</dynamic-bootp>" +
"              <low-addr>" + subnetRangeLow + "</low-addr>" +
"              <high-addr>" + subnetRangeHigh + "</high-addr>" +
"            </range>" +
"          </subnet>" +
"        </subnets>" +
"      </dhcp>" +
"    </config>" +
"  </edit-config>" +
"</rpc>" +
"]]>]]>" +
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"2\">" +
"  <close-session/>" +
"</rpc>";
//////System.out.println(mlt);

ArrayList < String > commands = new ArrayList < String > ();
commands.add("ssh admin@192.168.100.101 -p 2022 -s netconf");
commands.add("admin");
commands.add(rpc); // RPC config-edit

Session session = getSession("192.168.100.101", "root", "admin", 22);
String fr = null;
if (session.isConnected()) {
//////System.out.println("connected");
SSHTask task = new SSHTask(session, commands);
task.run();
session.disconnect();
//////System.out.println("result==========="+task.result.toString());
//<rpc-reply xmlns="urn:ietf:params:xml:ns:netconf:base:1.0" message-id="1"><ok/></rpc-reply>
fr = task.result.substring(task.result.indexOf("message-id=\"1\"><"), task.result.indexOf("</rpc-reply>"));
}
String check = fr.substring(fr.indexOf("<"));
//////System.out.println(fr +"++++ok++++"+check);
if (check.equals("<ok/>")) {
return 1;
} else return 0;

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return finalResult;
}
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
///////////////////////////////-----------------File
public int netconfRPC(MultipartFile file) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into netconfRPC ****************");
}
try {
Properties config = getProperties();
Path rootLocation = Paths.get(config.getProperty("server.directory"));
String file_store = file.getOriginalFilename();
File file_check = new File(rootLocation + "/" + file_store);
filePath = rootLocation + "/" + file_store;
if (file_check.exists()) {
Files.delete(rootLocation.resolve(file_store));
Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
} else {
Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
}
//getMapping netconfGetRPCreply
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return 1;
}

//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public String netconfGetRPCreply() {
String finalReply = null;
try {
BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
String line = "";
String rpc = "";
StringBuilder sbb = new StringBuilder();
while ((line = br.readLine()) != null) {
sbb.append(line + "\n");
}
br.close();

rpc = sbb.toString();
//////System.out.println(rpc);
ArrayList < String > commands = new ArrayList < String > ();
commands.add("ssh admin@192.168.100.101 -p 2022 -s netconf");
commands.add("admin");
commands.add(rpc);
Session session = getSession("192.168.100.101", "root", "admin", 22);

if (session.isConnected()) {
//////System.out.println("connected");
SSHTask task = new SSHTask(session, commands);
task.run();
session.disconnect();
String mid = task.result.toString();
////System.out.println(mid);
String reply = mid.substring(mid.indexOf("<rpc-reply"), mid.lastIndexOf("]]>]]>"));
//////System.out.println(reply);
JSONObject xmlJSONObj = XML.toJSONObject(reply);
finalReply = xmlJSONObj.toString();
}
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return finalReply;
}

//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
///////////////////////////////////////NETCONF GET============------------
public String netconfUIget(String yangName) {
if (log.isDebugEnabled()) {
log.debug("*************** checked into netconfUIget ****************");
}
String finalResult = null;
try {
String rpc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<hello xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">" +
"  <capabilities>" +
"    <capability>urn:ietf:params:netconf:base:1.0</capability>" +
"  </capabilities>" +
"</hello>" +
"]]>]]>" +
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"1\">" +
"  <get>" +
"    <filter>" +
"      <dhcp xmlns=\"http://tail-f.com/ns/example/dhcpd\"/>" +
"    </filter>" +
"  </get>" +
"</rpc>" +
"]]>]]>" +
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"2\">" +
"  <close-session/>" +
"</rpc>";
///////System.out.println(rpc);

ArrayList < String > commands = new ArrayList < String > ();
commands.add("ssh admin@192.168.100.101 -p 2022 -s netconf");
commands.add("admin");
/*
commands.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
"<hello xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\r\n" + 
"<capabilities>\r\n" + 
"<capability>urn:ietf:params:netconf:base:1.0</capability>\r\n" + 
"</capabilities>\r\n" + 
"</hello>\r\n" + 
"]]>]]>\r\n" + 
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
"<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"1\">\r\n" + 
"<get/>\r\n" + 
"</rpc>\r\n" + 
"]]>]]>\r\n" + 
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
"<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" message-id=\"2\">\r\n" + 
"<close-session/>\r\n" + 
"</rpc>");*/
commands.add(rpc);

Session session = getSession("192.168.100.101", "root", "admin", 22);
String fr = null;
if (session.isConnected()) {
//////System.out.println("connected");
SSHTask task = new SSHTask(session, commands);
task.run();
session.disconnect();
//////System.out.println("result==========="+task.result.toString());
//<rpc-reply xmlns="urn:ietf:params:xml:ns:netconf:base:1.0" message-id="1"><ok/></rpc-reply>
fr = task.result.substring(task.result.indexOf("<data><dhcp"), task.result.indexOf("</data>"));
}
//////System.out.println("1>>"+fr.replaceAll("\n", "").substring(6));
//------XML To JSON-----------------------
JSONObject xmlJSONObj = XML.toJSONObject(fr.replaceAll("\n", "").substring(6));
finalResult = xmlJSONObj.toString();
//////System.out.println("2>>"+finalResult);

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
}
return finalResult;
}

////------------------------////////////
////------------------------////////////
////--------kpi-alarm-live--////////////
////------------------------////////////
////------------------------////////////		
public String kpiAlarmAnalysis(String alarmCollection, String time5) {

if (log.isDebugEnabled()) {
log.debug("*************** checked into kpiAnalysis ****************");
}
String finalReply = null;
JSONArray jsonArrayValues = new JSONArray();

Properties config = getProperties();
String mongoServer = config.getProperty("database.mongodb.ipaddress");
MongoClient mongo = new MongoClient(mongoServer, 27017);
String mongoDB = config.getProperty("mongo.db.database.topology"); //alarms
MongoDatabase database = mongo.getDatabase(mongoDB);
DateTimeFormatter dtf0 = DateTimeFormat.forPattern("yyyy-MM-dd");
DateTime now = DateTime.now();
String collectionDate = dtf0.print(now);

DateTime lowerCircut = null, upperCircut = null;
DateTimeFormatter strToTime = DateTimeFormat.forPattern("HH:mm:ss");
lowerCircut = strToTime.parseDateTime(time5).minusMinutes(5); //String to DATE then minus 5(both 5min)
upperCircut = strToTime.parseDateTime(time5).plusMinutes(5);
String lowerTime = strToTime.print(lowerCircut);
String upperTime = strToTime.print(upperCircut);
try {

///////////////////////////////////////////////////////////
///goto AlarmCollection for alarm while up/down 5 mins/////
///////////////////////////////////////////////////////////
MongoCollection < Document > collectionAlarm = database.getCollection(alarmCollection);
CollectionToColNValPOJO xPojo = getColNamesofDomain(alarmCollection.substring(0, 1));
Map < String, Object > groupMap = new HashMap < String, Object > ();
groupMap.put(xPojo.colElement, "$" + xPojo.colElement);
groupMap.put(xPojo.colAlarm, "$" + xPojo.colAlarm);
//groupMap.put(xPojo.colDate, "$"+xPojo.colDate);
groupMap.put(xPojo.colTime, "$" + xPojo.colTime);
DBObject groupFields = new BasicDBObject(groupMap);
ArrayList < Document > timeAlarms = collectionAlarm.aggregate(Arrays.asList(
match(and(eq(xPojo.colDate, collectionDate),
gte(xPojo.colTime, lowerTime), lte(xPojo.colTime, upperTime))),
group(groupFields)
)).into(new ArrayList < Document > ());

JSONArray timeAlarmsJson = new JSONArray(JSON.serialize(timeAlarms));
for (int i = 0; i < timeAlarmsJson.length(); i++) {
JSONObject jsonObjectList = timeAlarmsJson.getJSONObject(i);
JSONObject jsonObject_id = jsonObjectList.getJSONObject("_id");
JSONObject values = new JSONObject();
//values.put("Date", jsonObject_id.optString(xPojo.colDate));
values.put("Time", jsonObject_id.optString(xPojo.colTime));
values.put("Element", jsonObject_id.optString(xPojo.colElement));
values.put("Alarm", jsonObject_id.optString(xPojo.colAlarm));
jsonArrayValues.put(values);
}
//jsonObjectCOLVAL.put("vals", jsonArrayValues);

//jsonArrayCOLVAL.put(jsonObjectCOLVAL);
finalReply = jsonArrayValues.toString();
mongo.close();
return finalReply;
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
return null;
}
}
//------------------------------------------------------------
//kpikpiLIVE--------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public String kpikpiAnalysis(String adminID, String kpi2Domain, String time5) {

if (log.isDebugEnabled()) {
log.debug("*************** checked into kpikpiAnalysis ****************");
}
JSONArray jsonArrayFinal = new JSONArray();
ArrayList < TableHeader > cols = new ArrayList < TableHeader > ();
JSONArray vals = new JSONArray();

Properties config = getProperties();
String mongoServer = config.getProperty("database.mongodb.ipaddress");
MongoClient mongo = new MongoClient(mongoServer, 27017);
String kpiDB2 = getCollectiondb(kpi2Domain);
MongoDatabase database2kpi2 = mongo.getDatabase(kpiDB2);
String DeviceName = "DeviceName";
if (kpi2Domain.startsWith("e") || kpi2Domain.startsWith("z")) {
DeviceName = "Site_ID";
}
DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy_MM_dd");
DateTime now = DateTime.now();
String collectionDate = dtf.print(now) + "_kpi_alerts";
//////System.out.println("collectionDate==="+collectionDate);

DateTime lowerCircut = null, upperCircut = null;
DateTimeFormatter strToTime = DateTimeFormat.forPattern("HH:mm:ss");
lowerCircut = strToTime.parseDateTime(time5).minusMinutes(10); //String to DATE (both 10min)
upperCircut = strToTime.parseDateTime(time5).plusMinutes(10);
String lowerTime = strToTime.print(lowerCircut);
String upperTime = strToTime.print(upperCircut);
try {
//////////////////////////////////////////////////////
///goto kpiCollection for kpi while down KPI/////
//////////////////////////////////////////////////////
MongoCollection < Document > collection = database2kpi2.getCollection(collectionDate);
///starts With to columns in database
ArrayList < Document > timeAlarms = collection.aggregate(Arrays.asList(
match(and(gte("Time", lowerTime), lte("Time", upperTime), eq("admin_id", adminID))),
project(fields(excludeId(), include("Date", "Time", "IPAddress", DeviceName, "Interface", "KPIName"))),
limit(50) ////////////////////////////////////Interface column only in ipbb and ipran
)).into(new ArrayList < Document > ());
//////System.out.println(date+"><>1<><"+Arrays.asList(time));
int size = 0;
int max = 0;
//////////////
//table head//
//////////////
Document doc = null;
for (int i = 0; i < timeAlarms.size(); i++) {
Document document = timeAlarms.get(i);
size = document.keySet().size();
if (size > max) {
max = size;
doc = document;
}
}
////////////////////////
//single doc for head//
//////////////////////
Iterator < String > itr = doc.keySet().iterator();
while (itr.hasNext()) {

String col = itr.next().toString();
//if (!col.equals("_id")) {
TableHeader th = new TableHeader(col, col);
cols.add(th);
// }
}
//////////////
//table data//
//////////////
for (Document docs: timeAlarms) {
JSONObject colval = new JSONObject();
for (int j = 0; j < cols.size(); j++) {
if (docs.get(cols.get(j).getHeader()).toString().length() > 0) {
colval.put(cols.get(j).getHeader(), docs.get(cols.get(j).getHeader()));
//////System.out.println( docs.get(cols.get(j).getHeader()));
} else {
colval.put(cols.get(j).getHeader(), "-");
}
}
vals.put(colval);
}
JSONObject jsonObjectColVal = new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
mongo.close();
return jsonArrayFinal.toString();
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
return null;
}
}
//-------------------------------------------------------------
//KPITO alarm WithFormulaInterversion-CONFIDENCE---------------
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public ArrayList < analysisLogicEntity > kpiAlarmAnalysisConf(String kpiVendor, String kpiDomain, String kpiElement, String kpiName, String alarmCollection, String confirmity_100) {

if (log.isDebugEnabled()) {
log.debug("*************** checked into kpiAlarmAnalysisConf ****************");
}
ArrayList < analysisLogicEntity > result = new ArrayList < analysisLogicEntity > ();
ArrayList < ArrayList < String >> min1Result = new ArrayList < ArrayList < String >> ();
try {
min1Result = analysisLogicLiveK2A(kpiVendor, kpiDomain, kpiElement, kpiName, alarmCollection, confirmity_100);
ArrayList < String > labelsX = min1Result.get(0);
ArrayList < String > confidenceY = min1Result.get(1);
for (int i = 0; i < labelsX.size(); i++) {
String[] labels = labelsX.get(i).split("<~>", 2);
String[] elementAlarmA = labels[0].split("~~", 3);
String[] elementAlarmB = labels[1].split("~~", 3);
analysisLogicEntity ale = new analysisLogicEntity(elementAlarmA[0] + "~~" + elementAlarmA[1], elementAlarmA[2], elementAlarmB[0] + "~~" + elementAlarmB[1], elementAlarmB[2], confidenceY.get(i));
result.add(ale);
}
return result;
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
return null;
}
}
//------------------------------------------------------------
//kpi-kpiFormulaInterversion-Confidence-----------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public ArrayList < analysisLogicEntity > kpikpiAnalysisConf(String adminID, String kpiVendor, String kpiDomain, String kpiElement, String kpiName, String kpi2Collection, String confirmity_100) {

if (log.isDebugEnabled()) {
log.debug("*************** checked into kpikpiAnalysisConf ****************");
}
ArrayList < analysisLogicEntity > result = new ArrayList < analysisLogicEntity > ();
ArrayList < ArrayList < String >> min1Result = new ArrayList < ArrayList < String >> ();
try {
min1Result = analysisLogicLiveK2K(adminID, kpiVendor, kpiDomain, kpiElement, kpiName, kpi2Collection, confirmity_100);
ArrayList < String > labelsX = min1Result.get(0);
ArrayList < String > confidenceY = min1Result.get(1);
for (int i = 0; i < labelsX.size(); i++) {
String[] labels = labelsX.get(i).split("<~>", 2);
String[] elementAlarmA = labels[0].split("~~", 3);
String[] elementAlarmB = labels[1].split("~~", 3);
analysisLogicEntity ale = new analysisLogicEntity(elementAlarmA[0] + "-" + elementAlarmA[1], elementAlarmA[2], elementAlarmB[0] + "-" + elementAlarmB[1], elementAlarmB[2], confidenceY.get(i));
result.add(ale);
}
return result;
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
return null;
}
}
//------------------------------------------------------------
//alarm-kpiFormulaInterversion-Confidence-----------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public ArrayList < analysisLogicEntity > alarmKpiAnalysisConf(String alarmDomain, String alarmVendor, String alarmElement, String alarmName, String kpiB, String confirmity_100) {

if (log.isDebugEnabled()) {
log.debug("*************** checked into alarmKpiAnalysisConf ****************");
}
ArrayList < analysisLogicEntity > result = new ArrayList < analysisLogicEntity > ();
ArrayList < ArrayList < String >> min1Result = new ArrayList < ArrayList < String >> ();
try {
min1Result = analysisLogicLiveA2K(alarmDomain, alarmVendor, alarmElement, alarmName, kpiB, confirmity_100);
ArrayList < String > labelsX = min1Result.get(0);
ArrayList < String > confidenceY = min1Result.get(1);
for (int i = 0; i < labelsX.size(); i++) {
String[] labels = labelsX.get(i).split("<~>", 2);
String[] elementAlarmA = labels[0].split("~~", 3);
String[] elementAlarmB = labels[1].split("~~", 3);
analysisLogicEntity ale = new analysisLogicEntity(elementAlarmA[0] + "-" + elementAlarmA[1], elementAlarmA[2], elementAlarmB[0] + "-" + elementAlarmB[1], elementAlarmB[2], confidenceY.get(i));
result.add(ale);
}
return result;
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
return null;
}
}
//------------------------------------------------------------
//kpikpiLIVE--------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
//-------------------------------------------------------------
public String logCheck(String logOf, String date, String time) {

if (log.isDebugEnabled()) {
log.debug("*************** checked into kpikpiAnalysis ****************");
}
JSONArray jsonArrayFinal = new JSONArray();
ArrayList < TableHeader > cols = new ArrayList < TableHeader > ();
JSONArray vals = new JSONArray();

Properties config = getProperties();
String mongoServer = config.getProperty("database.mongodb.ipaddress");
MongoClient mongo = new MongoClient(mongoServer, 27017);
MongoDatabase databaseOfLogs = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));

String collectionName = logOf + "_logs";

DateTime lowerCircut = null, upperCircut = null;
DateTimeFormatter strToTime = DateTimeFormat.forPattern("HH:mm:ss");
lowerCircut = strToTime.parseDateTime(time);
upperCircut = strToTime.parseDateTime(time).plusMinutes(10);
String lowerTime = strToTime.print(lowerCircut);
String upperTime = strToTime.print(upperCircut);
try {
strToTime = DateTimeFormat.forPattern("MM-dd-yyyy");
DateTime date0 = strToTime.parseDateTime(date);
strToTime = DateTimeFormat.forPattern("yyyy-MM-dd");
date = strToTime.print(date0);
//////////////////////////////////////////////////////
///goto kpiCollection for kpi while down KPI/////
//////////////////////////////////////////////////////
MongoCollection < Document > collection = databaseOfLogs.getCollection(collectionName);
///starts With to columns in database
ArrayList < Document > timeAlarms = collection.aggregate(Arrays.asList(
match(and(gte("Time", lowerTime), lte("Time", upperTime), eq("Date", date))),
project(fields(excludeId())),
limit(50)
)).into(new ArrayList < Document > ());
//////System.out.println(date+"><>1<><"+Arrays.asList(time));
int size = 0;
int max = 0;
//////////////
//table head//
//////////////
Document doc = null;
for (int i = 0; i < timeAlarms.size(); i++) {
Document document = timeAlarms.get(i);

////System.out.println(">>>" + document);
size = document.keySet().size();
if (size > max) {
max = size;
doc = document;
}
}
////////////////////////
//single doc for head//
//////////////////////
Iterator < String > itr = doc.keySet().iterator();
while (itr.hasNext()) {

String col = itr.next().toString();
//if (!col.equals("_id")) {
TableHeader th = new TableHeader(col, col);
cols.add(th);
// }
}
//////////////
//table data//
//////////////
for (Document docs: timeAlarms) {
JSONObject colval = new JSONObject();
for (int j = 0; j < cols.size(); j++) {
if (docs.get(cols.get(j).getHeader()).toString().length() > 0) {
colval.put(cols.get(j).getHeader(), docs.get(cols.get(j).getHeader()));
//////System.out.println( docs.get(cols.get(j).getHeader()));
} else {
colval.put(cols.get(j).getHeader(), "-");
}
}
vals.put(colval);
}
JSONObject jsonObjectColVal = new JSONObject();
jsonObjectColVal.put("cols", cols);
jsonObjectColVal.put("vals", vals);
jsonArrayFinal.put(jsonObjectColVal);
mongo.close();
return jsonArrayFinal.toString();
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
return null;
}
}
}