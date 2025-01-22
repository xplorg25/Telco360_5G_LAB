package org.xplorg.tool.telco360.config;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;
public class MongoDBFunctions extends BaseDAOMongo{
static Logger log = LogManager.getLogger(MongoDBFunctions.class.getName());		

//TODO to get data where select1_where1
public static ArrayList<String>mongo_select1_where2(MongoDatabase db,String select,String from,String where1,String whereequals1,String where2,String whereequals2){
if(log.isDebugEnabled()) {
	log.debug("*****************************************  checked into mongo_select1_where2 of MongoDBFunctions  ************************************************");	
}
ArrayList<String> output=new ArrayList<String>();

MongoCollection<Document> collection=db.getCollection(from);

DistinctIterable<String> document=collection.distinct(select,and(eq(where1,whereequals1),eq(where2,whereequals2)), String.class);

for(String data:document) {
output.add(data);	
}

return output;	


}

//TODO to get data where select1_where1
public static ArrayList<String>mongo_select1_where1(MongoDatabase db,String select,String from,String where1,String whereequals1){
	if(log.isDebugEnabled()) {
		log.debug("*****************************************  checked into mongo_select1_where1 of MongoDBFunctions  ************************************************");	
		}
ArrayList<String> output=new ArrayList<String>();

MongoCollection<Document> collection=db.getCollection(from);

DistinctIterable<String> document=collection.distinct(select,and(eq(where1,whereequals1)), String.class);

for(String data:document) {
output.add(data);	
}

return output;	


}





//TODO insert into mongodb
public static void insert_mongodb(MongoDatabase db,ArrayList < String > columns, ArrayList < String > values, String table_name) {
	
if(log.isDebugEnabled()) {
	log.debug("*****************************************  checked into insert_mongodb of MongoDBFunctions  ************************************************");	
	}

  MongoCollection < Document > col = db.getCollection(table_name);
  Document x = new Document();
	  //////System.out.println(table_name + columns.size());
  for (int i = 0; i < columns.size(); i++) {
    try {
      x.append(columns.get(i), values.get(i).trim().replace("\"", "").toString());
} catch (Exception e) {

 // e.printStackTrace();
    }
  }
  col.insertOne(x);

}	


//TODO to get data where select1_where0
public static ArrayList<String>mongo_select1_where0(MongoDatabase db,String select,String from){
	if(log.isDebugEnabled()) {
		log.debug("*****************************************  checked into mongo_select1_where1 of MongoDBFunctions  ************************************************");	
		}
ArrayList<String> output=new ArrayList<String>();

MongoCollection<Document> collection=db.getCollection(from);

DistinctIterable<String> document=collection.distinct(select, String.class);

for(String data:document) {
output.add(data);	
}

return output;	


}


//TODO to get column name corresponding to perticular table
public static String table_name_with_column_name(String formula,MongoDatabase db)  {
	if(log.isDebugEnabled()) {	
		log.debug("*************** checked into table_name_with_column_name of ****************");	
		}
	  MongoCollection < Document > col = db.getCollection("table_column_relation");
	
	String output="";
	
	StringBuilder sb=new StringBuilder();
	String formula_spliter=formula.replace("(",",(,").replace(")",",),").replace("+",",+,").replace("*", ",*,").replace("-",",-,").replace("/",",/,");

	String splt_formula[]=formula_spliter.toString().split(",");
	for(String spls:splt_formula){
		

	String sng=spls;
	if(spls.length()>1||spls.matches(".*\\d.*")){
	
		String s_type="";
		
		if(spls.length()<8&&spls.matches(".*\\d.*")) {
			s_type=spls;
			
		}
		else {
		
			DistinctIterable <String> val=col.distinct("table_name",and(eq("column_name",spls)),String.class );
			String tb_name="";
			for(String value:val) {
				 tb_name=value;	
			}

			s_type=tb_name+":"+spls;
		
		}
	
	sng=s_type;
	
	
	}

	sb.append(sng);

	}	
	output=sb.toString();
	return output;
	
	
	
	
}

//TODO
public static String mongo_generic(MongoDatabase db,String admin_id,String formula,String column,String table_name){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into mongo_generic ****************");	
}
String output="";
String chk1="";

MongoCollection <Document> collection = db.getCollection(table_name);
DistinctIterable<String> documents ;

//String query="Select distinct "+column+" from "+table_name+" where kpi_name='"+formula+"'";
documents = collection.distinct(column, and(eq("kpi_name", formula),eq("admin_id",admin_id)),String.class );
//////////////System.out.println(query);

try {
for (String document : documents) {
	chk1=document;
}	


} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
ex.printStackTrace();
}
output=chk1.trim();
return output;



}


//TODO
public static String mongo_generic_for_kpi_report(MongoDatabase db,String admin_id,String group,String column,String table_name){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into mongo_generic ****************");	
}
String output="";
String chk1="";

MongoCollection <Document> collection = db.getCollection(table_name);
DistinctIterable<String> documents ;

//String query="Select distinct "+column+" from "+table_name+" where kpi_name='"+formula+"'";
documents = collection.distinct(column, and(eq("groups", group),eq("admin_id",admin_id)),String.class );
//////////////System.out.println(query);

try {
for (String document : documents) {
	chk1=document;
}	


} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
ex.printStackTrace();
}
output=chk1.trim();
return output;



}
//TODO
//adding keys for vendor 3
public String ipbb_table_hint(String vendor,String formula) {
	
String output = null;
	
if(vendor.equals("NOKIA")) {
	
if(formula.contains("ipbb")) {
output="";
}


else if(formula.contains("stats")) {
output="_stats";
}

else if(formula.contains("usage")) {
output="_usage";
}
else if(formula.contains("memory")) {
output="_memory";
}
else if(formula.contains("vlan")) {
output="_vlan";
}
	
}

else if(vendor.equals("ERICSSON")) {
	
if(formula.contains("ipbb")) {
output="";
}


else if(formula.contains("port_counter")) {
output="_port_counter";
}

else if(formula.contains("dot1q_counter")) {
output="_dot1q_counter";
}
else if(formula.contains("cpu_usage")) {
output="_cpu_usage";
}
else if(formula.contains("memory_usage")) {
output="_memory_usage";
}
	
}

else if(vendor.equals("JUNIPER")) {
	
if(formula.contains("ipbb")) {
output="";
}


else if(formula.contains("stats")) {
output="_stats";
}


}

else if(vendor.equals("ZTE")) {
	
if(formula.contains("ipbb")) {
output="";
}


else if(formula.contains("processor")) {
output="_processor";
}


}

else if(vendor.equals("ZTE")) {
	
if(formula.contains("ipbb")) {
output="";
}
	
}
	
else if(vendor.equals("HUAWEI")) {
	
if(formula.contains("ipran")) {
output="";
}
}
else if(vendor.equals("DPTECH_FIREWALL")) {
	
if(formula.contains("ipbb")) {
output="";
}


else if(formula.contains("performance")) {
output="_performance";
}
else if(formula.contains("session")) {
output="_session";
}

}
	
	return output;
	
}


//table hint for all interfaces
public String ipbb_interface_table_hint(String vendor,String formula) {
	
	String output = null;
	
if(vendor.equals("NOKIA")) {
	
if(formula.contains("ipbb")) {
output="snmp";
}


else if(formula.contains("stats")) {
output="show port statistics";
}

else if(formula.contains("usage")) {
output="show system cpu";
}
	
}

else if(vendor.equals("ERICSSON")) {
	
if(formula.contains("ipbb")) {
output="";
}


else if(formula.contains("port_counter")) {
output="_port_counter";
}

else if(formula.contains("dot1q_counter")) {
output="_dot1q_counter";
}
	
}

else if(vendor.equals("JUNIPER")) {
	
if(formula.contains("ipbb")) {
output="";
}


else if(formula.contains("stats")) {
output="_stats";
}


}

else if(vendor.equals("ZTE")) {
	
if(formula.contains("ipbb")) {
output="";
}
	
}
	
else if(vendor.equals("HUAWEI")) {
	
if(formula.contains("ipran")) {
output="snmp";
}
}
	
	
	return output;
	
}

//TODO to get unique date as per database
public static ArrayList < String > mongo_get_name(MongoDatabase db, String start_date, String end_date) {

if (log.isDebugEnabled()) {
log.debug("*************** checked into mongo_get_name ****************");
}

ArrayList < String > output = new ArrayList < String > ();
MongoCollection collection = db.getCollection("data_date");

DistinctIterable < String > documents = collection.distinct("value", and(gte("value", start_date), lte("value", end_date)), String.class);

try {
for (String document: documents) {
output.add(document);
}
////System.out.println("++++++++++"+output+"+++++++++++++++++++++++"+start_date+"++++++++++++++++++++++++++"+end_date+"+++++++++++++++++++++++++++");
} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
ex.printStackTrace();
}
return output;

}

// TODO to get date corresponding to current month
public static ArrayList<String> get_month_date(MongoDatabase db,String current_month){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into get_month_date ****************");	
}

ArrayList<String> output=new ArrayList<String>();
try {
MongoCollection<Document> collection=db.getCollection("data_date");

DistinctIterable<String> document=collection.distinct("value", String.class);
for(String data:document) {
if(data.trim().contains(current_month+"-")) {
	output.add(data.trim());
}	
	
}



} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
ex.printStackTrace();
}
return output;
}


//get single value from single table

public String get_single_column_value(MongoDatabase db,String table_name,String column_name) {
if(log.isDebugEnabled()) {	
	log.debug("*************** checked into get_single_column_value ****************");	
}

String output="";	

try {
MongoCollection<Document> collection=db.getCollection(table_name);

DistinctIterable<String> document=collection.distinct(column_name, String.class);
for(String data:document) {

	output=data;

	
}

} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
ex.printStackTrace();
}
	
return output;
}

//TODO to get distinct values corresponding to various columns

public static ArrayList < String > get_mongodb_distinct_values(FindIterable < Document > documents, ArrayList < String > column_names) {
StringBuffer sb = new StringBuffer();

ArrayList < String > output = new ArrayList < String > ();

JSONArray jsonArray = new JSONArray(JSON.serialize(documents));

for (int k = 0; k < jsonArray.length(); k++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(k);
for (String column: column_names) {

sb.append(jsonObject1.optString(column) + "@AND@");

}

String value = sb.toString().substring(0, sb.length() - 5);

output.add(value);
sb.delete(0, sb.length());
// 

}

Set < String > set = new LinkedHashSet < > ();
// Add the elements to set
set.addAll(output);

// Clear the list
output.clear();

// add the elements of set
// with no duplicates to the list
output.addAll(set);;

return output;

}


//TODO for update table

//update==

public static int updateDataGeneric(MongoDatabase database,String data) {
  //System.out.println(data);
try {
String tableName="";
ArrayList<Bson> fltr=new ArrayList<Bson>();
ArrayList<Bson> updt=new ArrayList<Bson>();
Bson condition=null;
Bson updates=null;

String spls[]=data.split(";");   
for(String splt:spls) {
if(splt.contains("tablename")){
tableName=splt.split("=")[1].trim();    
}
if(splt.contains("condition")){  
String conds=splt.split("=")[1]; 
String cond[]=conds.split("#@#");
for(String con:cond) {
String cl=con.split("@#@")[0].trim();
String vl=con.split("@#@")[1].trim();
fltr.add(eq(cl,vl));       
}
}
if(splt.contains("updates")){    
String updts=splt.split("=")[1]; 
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


MongoCollection<Document> collection = database.getCollection(tableName);
UpdateResult updateResult = collection.updateOne(condition,Updates.combine(updates));
int result=(int)updateResult.getModifiedCount();

return result;      
}catch (Exception ex) {

ex.printStackTrace();
}
return 0;
}


public String ne_names_hint(String vendor,String input) {
String output="";
if(vendor.equals("NCE")) {
if(input.equals("board_report")||input.equals("subcard_report")||input.equals("subrack_report")) {	
output="NE_Name";	
}
else if(input.equals("optical_report")) {
output="NE_Name";	
}
else {
output="NE_Name";	
}
	
}

else if(vendor.equals("NEC")) {
output="NE_NAME";	
}

else if(vendor.equals("ERICSSON")) {
output="NodeId";	
}

else if(vendor.equals("SAM")) {
output="siteName";	
}
	
	
	
	

return output;	



}

public String date_as_per_day(String input) {
	String output="";
	DateTime today = DateTime.now();

	DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
	DateTime next_week = today.plusWeeks(1);
	DateTime zambia_time = next_week.withZone(DateTimeZone.forID("Africa/Lusaka"));
	DateTime convert = null;
	if(input.equals("Monday")) {
	convert = zambia_time.withDayOfWeek(DateTimeConstants.MONDAY);
	output=	format.print(convert);
	}
	else if(input.equals("Tuesday")) {
		convert = zambia_time.withDayOfWeek(DateTimeConstants.TUESDAY);
		output=	format.print(convert);	
	}

	else if(input.equals("Wednesday")) {
		convert = zambia_time.withDayOfWeek(DateTimeConstants.WEDNESDAY);
		output=	format.print(convert);
		}

	
	
	else if(input.equals("Thursday")) {
		convert = zambia_time.withDayOfWeek(DateTimeConstants.THURSDAY);
		output=	format.print(convert);
		}

	
	else if(input.equals("Friday")) {
		convert = zambia_time.withDayOfWeek(DateTimeConstants.FRIDAY);
		output=	format.print(convert);
		}

	
	else if(input.equals("Saturday")) {
		convert = zambia_time.withDayOfWeek(DateTimeConstants.SATURDAY);
		output=	format.print(convert);
		}

	
	else if(input.equals("Sunday")) {
		convert = zambia_time.withDayOfWeek(DateTimeConstants.SUNDAY);
		output=	format.print(convert);
		}
	
	else {
		output=current_date("zambia");
	}

	
	
	
	
		
	
	
	
	return output;
	
}
//TODO for current date

public String current_date(String opco) {
	
String output="";	
Date date = new Date();
SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

if(opco.equals("zambia")) {
df.setTimeZone(TimeZone.getTimeZone("Africa/Lusaka"));	
}

String dateTime = df.format(date);
String split[] = dateTime.split("\\s+");

output = split[0];
return output;	
	
}

public void mail(String subject,String txt,String file_attach,String admin_id){
if(log.isDebugEnabled()) {  
log.debug("*************** checked into mail ****************");        
}

try {
               

               
Properties config=new Properties();
InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");

config.load(input);           
               
Properties props = new Properties();
props.put("mail.smtp.host", config.getProperty("mail.smtp.host"));
props.put("mail.stmp.user",  config.getProperty("mail.stmp.user"));
//If you want you use TLS
props.put("mail.smtp.auth",  config.getProperty("mail.smtp.auth"));

props.put("mail.smtp.starttls.enable",  config.getProperty("mail.smtp.starttls.enable"));
props.put("mail.smtp.password",  config.getProperty("mail.smtp.password"));
// If you want to use SSL
props.put("mail.smtp.socketFactory.port",  config.getProperty("mail.smtp.socketFactory.port"));
props.put("mail.smtp.socketFactory.class", config.getProperty("mail.smtp.socketFactory.class"));
props.put("mail.smtp.auth",  config.getProperty("mail.smtp.auth"));
props.put("mail.smtp.port",  config.getProperty("mail.smtp.port"));
Session session = Session.getDefaultInstance(props, new Authenticator() {
@Override
protected PasswordAuthentication getPasswordAuthentication() {
String username =  config.getProperty("mail.username");
String password =  config.getProperty("mail.password");
return new PasswordAuthentication(username,password);
}
});

ArrayList<String>to=new ArrayList<String>();

to=user_email("PM", admin_id);

if(log.isDebugEnabled()) {  
log.debug("******                   "+    admin_id       +"***           ****** checked into mail ****        "+to+"                ************");               
}
String from =  config.getProperty("mail.from");
//String sub = "Trouble Ticket with reference ticket number(202003200401)";
MimeMessage msg = new MimeMessage(session);
try {
msg.setFrom(new InternetAddress(from));
InternetAddress[] addressTo = new InternetAddress[to.size()];
for (int i = 0; i < to.size(); i++)
{
addressTo[i] = new InternetAddress(to.get(i));
}
msg.setRecipients(RecipientType.TO, addressTo);
// msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
msg.setSubject(subject);
// msg.setText("JAVA is the BEST");

// Create the message part
BodyPart messageBodyPart = new MimeBodyPart();

// Fill the message
messageBodyPart.setText(txt);

// Create a multipar message
Multipart multipart = new MimeMultipart();

// Set text message part
multipart.addBodyPart(messageBodyPart);


//new whatsapp().send_whatsapp("+919888652531",txt);



if(log.isDebugEnabled()) {  
log.debug("*************** "+file_attach+" ****************");             
}
if(file_attach.length()>0) {
               
messageBodyPart = new MimeBodyPart();

File file_name=new File(file_attach);

String filename = file_attach;
DataSource source = new FileDataSource(filename);
messageBodyPart.setDataHandler(new DataHandler(source));
messageBodyPart.setFileName(file_name.getName());
multipart.addBodyPart(messageBodyPart);
               
               
               
}


// Part two is attachment
//multipart.addBodyPart(messageBodyPart);

// Send the complete message parts
msg.setContent(multipart );

Transport transport = session.getTransport(config.getProperty("mail.transport"));
transport.send(msg);
if(log.isDebugEnabled()) {  
log.debug("Email Successfully Sent    ******   "+admin_id+"        "+to+"********* "+file_attach+" ****************");      
}
}
catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
}
}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);

}
}


public ArrayList<String> user_email(String area,String id) {
ArrayList<String> output=new ArrayList<String>();

Properties config = getProperties();
////System.out.println("==>"+config.getProperty("database.topology_database"));
MongoClient mongo=getConnection();
MongoDatabase database=mongo.getDatabase(config.getProperty("database.topology_database"));

output=mongo_select1_where2(database, "email_id", "trouble_ticket_details", "user_id", id, "area", area);

closeConnection(mongo);

return output;
               
               
}

}
