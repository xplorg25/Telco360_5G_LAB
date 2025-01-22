package org.xplorg.tool.telco360.entity;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xplorg.tool.telco360.config.PerformanceDAO;
import org.xplorg.tool.telco360.performanceDAO.implementation.date_time_relation;
import org.xplorg.tool.telco360.performanceDAO.implementation.ipran_ipbb_graph_values;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

public class GenericPerformance extends PerformanceDAO{

static Logger log = LogManager.getLogger(GenericPerformance.class.getName());


public void create_report(MongoDatabase database,String domain,String vendor,String admin_id,ArrayList<String>unique_dates,  String StartTime,String EndTime,String interval ,ArrayList<String>kpi_name,ArrayList<String>element_name,String report_name ) {

//////System.out.println(report_name+"===="+unique_dates);	
ArrayList <String> values_x = new ArrayList <String> ();// values for x-axis 

values_x=new date_time_relation().date_time_relations_report(unique_dates,StartTime, EndTime, interval);

ArrayList < Double > data1 = new ArrayList < Double > ();

int check=0;


for(String kpi:kpi_name) {
	String table_hint = mongo_generic(database, admin_id, kpi, "kpi_formula", "kpi_formula");

//////////System.out.println("===>"+table_hint);

String final_table_hint=ipbb_table_hint(vendor,table_hint);
	
////////System.out.println("kpi-----"+kpi);	
String calculation_type = mongo_generic(database, admin_id, kpi, "calculation_type", "kpi_formula");	

for(String ne_single_name:element_name) {
	
String ip=mongo_select1_where1(database,"ip","element_command_structure","devicename",ne_single_name).get(0);	

data1=new ipran_ipbb_graph_values().graph_value(domain,database, kpi, ne_single_name,"-","-", values_x,interval,calculation_type,final_table_hint);

//////System.out.println("data1===="+data1);
for(int i=0;i<values_x.size();i++) {
	
String current_date="";
String current_time="-";
ArrayList<String> columns=new ArrayList<String>();
ArrayList<String> values=new ArrayList<String>();
current_date=StringUtils.substringBefore(values_x.get(i), "/");
current_time=StringUtils.substringAfter(values_x.get(i), "/");
columns.add("IPAddress");
columns.add("DeviceName");
columns.add("Date");
columns.add("Time");
columns.add(kpi);

values.add(ip);	
values.add(ne_single_name);	
values.add(current_date);
values.add(current_time);
values.add(""+data1.get(i));
if(check==0) {
	
//////System.out.println(columns);
//////System.out.println(values);
//////System.out.println(report_name);


insert_mongodb(database, columns, values, report_name); //inserting into "_kpis"	
}
else {
String condition="",updates="",cl="",vl="";
if(current_time.length()>0) {
	condition="Time"+"@#@"+current_time+"#@#"+"Date"+"@#@"+current_date+"#@#";	
}
else {
	condition="Time"+"@#@-#@#"+"Date"+"@#@"+current_date+"#@#";
}


//////System.out.println("condition====>"+condition);
updates=updates+kpi+"@#@"+data1.get(i)+"#@#";
//////System.out.println("updates====>"+updates);
String cd=condition.substring(0,condition.length()-3);
String up=updates.substring(0,updates.length()-3);
String data="tablename="+report_name+";condition="+cd+";"+"updates="+up;
updateDataGeneric(database,data);	
		
	}


	}


	}

	check++;
	}
}
	
	






//TODO to get dates corresponding to report type during excel report
public ArrayList<String> unique_dates(String report_type,String start_date,String end_date,MongoDatabase database,Properties config){
	
ArrayList<String>  unique_dates = new ArrayList<String>();

//////////////////System.out.println(kpi_names.size());
if(report_type.equals("MONTHLY")) {
//to get current month
Date date_month = new Date();
String start_month=new SimpleDateFormat("yyyy-MM").format(date_month);
////////////System.out.println("start_month==="+start_month);
unique_dates=get_month_date(database,start_month);	
}

else if(report_type.equals("LAST 7 DAYS")) {

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
Calendar cal = Calendar.getInstance();
Date date=cal.getTime();
String[] days = new String[6];
days[0]=sdf.format(date);

for(int i = 0; i< 6; i++){
cal.add(Calendar.DAY_OF_MONTH,-1);
date=cal.getTime();
days[i]=sdf.format(date);
}

for(int i = (days.length-1); i >= 0; i--){
unique_dates.add(days[i]);

}	
}

else if(report_type.equals("CURRENT DATE")) {
Date date_month = new Date();
String present_date=new SimpleDateFormat("yyyy-MM-dd").format(date_month);	
unique_dates.add(present_date);


}


else {
List<Date> dates = new ArrayList<Date>();



DateFormat formatter ; 

formatter = new SimpleDateFormat(config.getProperty("performance.dateformat1"));
Date startDate;
try {
startDate = (Date)formatter.parse(start_date);
start_date = new SimpleDateFormat(config.getProperty("performance.dateformat3")).format(startDate);
Date  endDate = (Date)formatter.parse(end_date);
end_date = new SimpleDateFormat(config.getProperty("performance.dateformat3")).format(endDate);
long interval = 24*1000 * 60 * 60; // 1 hour in millis
long endTime =endDate.getTime() ; // create your endtime here, possibly using Calendar or Date
long curTime = startDate.getTime();
while (curTime <= endTime) {
dates.add(new Date(curTime));
curTime += interval;
}
for(int i=0;i<dates.size();i++){
Date lDate =(Date)dates.get(i);
String ds = formatter.format(lDate);    
Date date = formatter.parse(ds);

String fdate=new SimpleDateFormat("yyyy-MM-dd").format(date);
unique_dates.add(fdate.toString());

}

} catch (ParseException e) {

	log.error("Exception occurs:----" + e.getMessage(), e);
} 
}




return unique_dates;	
	
}




////TODO for getting replaced value
public String string_replace(String input) {
String output="";


String value=input.replace("FBRACKET", "(").replace("BBRACKET", ")").replace("PERCENTAGE", "%").replace("SPACE", " ").replace("SPACE", " ").replace("SLASH", "/").replace("SPACE", " ").replace("MINUS", "-").replace("COMMA", ",")
.replace("HATCH", "#").replace("STARTSQUAREBRACKET", "[").replace("ENDSQUAREBRACKET", "]");
output=value.trim();
return output;	
	
}



//TODO 
public static String where_condition(ArrayList<String> input) {
	
String output="";
StringBuilder sb=new StringBuilder();
for(String value:input) {
String before=StringUtils.substringBefore(value, "-");
String after=StringUtils.substringAfter(value, "-");
	
sb.append(before+"='"+value+"' and ");
	

	
}


sb.delete(sb.length()-4,sb.length());

return output=sb.toString().trim();
	
}



//TODO 
	
public static ArrayList<String> get_name(Connection con,String start_date,String end_date){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into get_name ****************");	
}

ArrayList<String> output=new ArrayList<String>();

String query="SELECT distinct * FROM data_date WHERE VALUE BETWEEN '"+start_date+"' AND '"+end_date+"' ORDER BY value";
////////////////////System.out.println(query);
ResultSet rs=null;

try {
Statement st=con.createStatement();
rs=st.executeQuery(query);
while(rs.next()) {
output.add(rs.getString(1));
}
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);

}
return output;
}


//TODO 

public static String generic(Statement st,String node,String formula,String column,String table_name){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into generic ****************");	
}
String output="";
String chk1="";

String query="Select distinct "+column+" from "+table_name+" where kpi_name='"+formula+"'";

////////////////////System.out.println(query);
ResultSet rs=null;
try {
rs=st.executeQuery(query);
while(rs.next()) {
chk1=rs.getString(1);
}
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);

}
output=chk1.trim();
return output;

}



//TODO 
public static String generic_threhold(Statement st,String node,String formula,String column,String table_name){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into generic ****************");	
}
String output="";
String chk1="";

String query="Select distinct "+column+" from "+table_name+" where kpi_name='"+formula+"' and link_to_topology='yes'";

//////////////////System.out.println(query);
ResultSet rs=null;
try {
rs=st.executeQuery(query);
while(rs.next()) {
chk1=rs.getString(1);
}
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);

}
output=chk1.trim();
return output;

}

//TODO 
public ArrayList<String> get_name(Connection con_common,String element_name){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into get_name ****************");	
}
ArrayList<String> output=new ArrayList<>();
Statement statement_get_value;
try {
statement_get_value = con_common.createStatement();

String query;
//query="Select distinct kpi_name from sgsn_kpi_formula where kpi_name='SGs interface CS paging success rate'" ;
query="Select distinct kpi_name from radio_kpi_formula where topology='y' and element_name='"+element_name+"'" ;
ResultSet rs=statement_get_value.executeQuery(query);
while(rs.next()) {
output.add(rs.getString(1));
}
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);

}

return output;
}

//TODO 

public Double getKpiDetails(Connection con,String kpi,String column){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into getKpiDetails ****************");	
}
String chk1="";
try {

Statement 	st = con.createStatement();
String query="Select distinct "+column+" from radio_kpi_formula where kpi_name='"+kpi+"'";
ResultSet rs=st.executeQuery(query);
while(rs.next()) {
chk1=rs.getString(1);
}
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);

}

double op=Double.parseDouble(chk1);

return op;

}


//TODO 
public String getKpiDetails1(Connection con,String kpi,String column){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into getKpiDetails1 ****************");	
}
String chk1="";
try {

Statement 	st = con.createStatement();
String query="Select distinct "+column+" from radio_kpi_formula where kpi_name='"+kpi+"'";
ResultSet rs=st.executeQuery(query);
while(rs.next()) {
chk1=rs.getString(1);
}
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);

}


return chk1;

}


//TODO 
public String get_time_diff(Connection con,String table_name) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into get_time_diff ****************");	
}
String output="0";

try {
Statement st_table=con.createStatement();

ResultSet rs=st_table.executeQuery("select distinct time_difference from "+table_name+" ");

if (rs.next() == false)
{ output="0";
}
else{
do
{
output=rs.getString(1);
}
while (rs.next());
}

} catch (Exception ex) {
output="0";
log.error("Exception occurs:----"+ex.getMessage(),ex);

}

return output;

}
//TODO 
public static ArrayList<String> start_time(String start_time,String end_time,int check_time,Connection conn) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into start_time ****************");	
}
ArrayList<String> output=new ArrayList<String>();

try {
Statement st=conn.createStatement();
String query="";
if(check_time==0) {

query=	"Select value from data_time  ORDER BY value";
}
else {
query= "Select value from data_time where value between '"+start_time+"' and '"+end_time+"' ORDER BY value" ;
}

////////////////////System.out.println(query);

ResultSet rs=st.executeQuery(query)	;
while(rs.next()) {
output.add(rs.getString(1));
}
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);

}

return output;

}

//TODO 
public static ArrayList<String> start_time_br(String start_time,String end_time,int check_time,Connection conn,String table_name) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into start_time ****************");	
}
ArrayList<String> output=new ArrayList<String>();

try {
Statement st=conn.createStatement();
String query="";
if(check_time==0) {

query=	"Select value from "+table_name+"  ORDER BY value";
}
else {
query= "Select value from "+table_name+" where value between '"+start_time+"' and '"+end_time+"' ORDER BY value" ;
}

////////////////System.out.println(query);

ResultSet rs=st.executeQuery(query)	;
while(rs.next()) {
output.add(rs.getString(1));
}
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);

}

return output;

}

//TODO 
public String check_failure_success_rate(ArrayList<String> ar,double d,String kpi_name) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into check_failure_success_rate ****************");	
}
String output = "";

ArrayList <String>a=new ArrayList<String>();

for(int i=0;i<ar.size();i++) {
double da=Double.parseDouble(ar.get(i).toString());
if(da<d) {
a.add("yes");
}
else {
a.add("no");
}

}
if (a.contains("yes")) {
output="yes";
}
else {
output="no";
}
return output;
}


//TODO 

public String check_failure_failure_rate(ArrayList <String>ar,double d,String kpi_name) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into check_failure_failure_rate ****************");	
}
String output = "";

ArrayList<String> a=new ArrayList<String>();

for(int i=0;i<ar.size();i++) {
double da=Double.parseDouble(ar.get(i).toString());
if(da>d) {
a.add("yes");
}
else {
a.add("no");
}
}
if (a.contains("yes")) {
output="yes";
}
else {
output="no";
}

return output;
}

//TODO 
public ArrayList<Long> sum_array(ArrayList<Long> input,int count,String table_type,String calculation_type){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into sum_array ****************");	
}

ArrayList<Long> output=new ArrayList<>();
ArrayList<Double> ar1=new ArrayList<Double>();;
int in=0;

for(int i=0;i<input.size();i++) {

double d=input.get(i);
ar1.add(d)	;
in++;

if(in==count) {
double sum_array = 0;

in=0;
if ( !Character.isDigit(table_type.charAt(0)) ) {
if(calculation_type.equals("sum")) {
sum_array= sum_array_individual(ar1) ;
}

if(calculation_type.equals("average")) {
sum_array= calculateAverage(ar1) ;
}

if(calculation_type.equals("peak_value")) {
sum_array= peak_value(ar1) ;
}

DecimalFormat df = new DecimalFormat("##.##");
String ss=df.format(sum_array);
Double dd = Double.parseDouble(ss.trim());
Long l = dd.longValue();
output.add(l);
}

else {
long a=Long.parseLong(table_type);
output.add(a);
}

ar1=new ArrayList<Double>();

}
}

return output;

}


//TODO 
public static  double sum_array_individual(ArrayList<Double> input) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into sum_array_individual ****************");	
}
double sum = 0;
for(int i = 0; i < input.size(); i++)
sum += input.get(i);
return sum;

}


//TODO 
public static double peak_value(ArrayList<Double> input) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into peak_value ****************");	
}
double sum = 0;
sum= Collections.max(input);
return sum;

}
//TODO 
public static double minimum_value(ArrayList<Double> input) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into minimum_value ****************");	
}
double sum = 0;
sum= Collections.min(input);
return sum;

}
//TODO 
public static double calculateAverage(ArrayList <Double> input) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into calculateAverage ****************");	
}
double sum = 0;
if(!input.isEmpty()) {
for (double mark : input) {
sum += mark;
}
return sum / input.size();
}
return sum;
}


//TODO 
public static String table_name_with_column_name(String formula,String db_name,Connection connection) throws SQLException {
	if(log.isDebugEnabled()) {	
		log.debug("*************** checked into table_name_with_column_name ****************");	
		}
	//////////////////System.out.println(formula);
	
	String output="";
	
	StringBuilder sb=new StringBuilder();
	String formula_spliter=formula.replace("(",",(,").replace(")",",),").replace("+",",+,").replace("*", ",*,").replace("-",",-,").replace("/",",/,");

	String splt_formula[]=formula_spliter.toString().split(",");
	for(String spls:splt_formula){
		
	//////////////////System.out.println(spls);
	String sng=spls;
	if(spls.length()>1||spls.matches(".*\\d.*")){
	
		String s_type="";
		
		if(spls.length()<8&&spls.matches(".*\\d.*")) {
			s_type=spls;
			//////////////////////System.out.println("1==="+spls);
		}
		else {
			//////////////////////System.out.println("2==="+spls);
			String tb_name=table_name(connection,spls,db_name);
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
public static String table_name (Connection connection,String input,String db_name) throws SQLException {
	if(log.isDebugEnabled()) {	
		log.debug("*************** checked into table_name ****************");	
		}
	String output="";
	
	
	Statement st=connection.createStatement();
	
	
	String query="SELECT DISTINCT TABLE_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE COLUMN_NAME IN ('"+input+"') AND TABLE_SCHEMA='"+db_name+"';";
	////////////System.out.println(query);
	ResultSet rs=st.executeQuery(query);
	
	if (rs.next() == false)
	{ output="0";
	}
	else{
	do
	{
	output=rs.getString(1);
	}
	while (rs.next());
	}
	
	return output;
	
}


//TODO 
public static String date_timezone(String timezone,String format) {
	if(log.isDebugEnabled()) {	
		log.debug("*************** checked into date_timezone ****************");	
		}
Date date = new Date();  
SimpleDateFormat df = new SimpleDateFormat(format);
df.setTimeZone(TimeZone.getTimeZone(timezone));
String date_out = df.format(date);
return date_out;  
	
}

//TODO 
public static String sub_date(String input) {
	if(log.isDebugEnabled()) {	
		log.debug("*************** checked into sub_date ****************");	
		}
	
String untildate=input;//can take any date in current format    
SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");   
Calendar cal = Calendar.getInstance();    
try {
	cal.setTime( dateFormat.parse(untildate));
} catch (ParseException e) {
	
	
}    
cal.add( Calendar.MINUTE, -15 );    
String convertedDate=dateFormat.format(cal.getTime());    

return convertedDate;
}

//TODO 
public static String sub_time_30(String input) {
	if(log.isDebugEnabled()) {	
		log.debug("*************** checked into sub_time_30 ****************");	
		}	
	
String untildate=input;//can take any date in current format    
SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");   
Calendar cal = Calendar.getInstance();    
try {
	cal.setTime( dateFormat.parse(untildate));
} catch (ParseException e) {

}    
cal.add( Calendar.MINUTE, -30 );    
String convertedDate=dateFormat.format(cal.getTime());    

return convertedDate;
}

//TODO 
public static String sub_time_60(String input) {
	
	if(log.isDebugEnabled()) {	
		log.debug("*************** checked into sub_time_60 ****************");	
		}
String untildate=input;//can take any date in current format    
SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");   
Calendar cal = Calendar.getInstance();    
try {
	cal.setTime( dateFormat.parse(untildate));
} catch (ParseException e) {
	

}    
cal.add( Calendar.MINUTE, -60 );    
String convertedDate=dateFormat.format(cal.getTime());    

return convertedDate;
}


//TODO 
public void truncate_affected_kpi(Connection connection) {
	
	if(log.isDebugEnabled()) {	
		log.debug("*************** checked into truncate_affected_kpi ****************");	
		}
	try {
		
		Statement st=connection.createStatement();
		
		
		String query="truncate table affected_kpi_details";	
		
	 st.executeUpdate(query);
	} catch (SQLException e) {
	

	}
	
	
}


//TODO 
public static String get_element_name (Connection connection,String ip,String table_name,String column) throws SQLException {
	if(log.isDebugEnabled()) {	
		log.debug("*************** checked into get_element_name ****************");	
		}
	String output="";
	
	
	Statement st=connection.createStatement();
	
	
	String query="SELECT DISTINCT devicename FROM "+table_name+" where "+column+"= '"+ip+"' ;";
	//////////////////System.out.println(query);
	ResultSet rs=st.executeQuery(query);
	
	if (rs.next() == false)
	{ output="0";
	}
	else{
	do
	{
	output=rs.getString(1);
	}
	while (rs.next());
	}
	
	return output;
	
}


//TODO 
public static String get_kpi_table_name_br (String kpi) {
	if(log.isDebugEnabled()) {	
		log.debug("*************** checked into get_element_name ****************");	
		}
	String output="";
	ArrayList <String>ar=new ArrayList<String>();
	ar.add("ifhcoutucastpkts(pps)");
	ar.add("ipifstatsindiscards");
	ar.add("throughtput_mbps(ifhcinoctets)");
	ar.add("total throughtput_mbps");
	ar.add("throughtput_mbps(ifhcoutoctets)");
	ar.add("interface utilization(rx)");
	ar.add("cpu_usage");
	ar.add("memory_usage");
	ar.add("ipifstatsoutdiscards");
	ar.add("ifhcinucastpkts(pps)");
	ar.add("interface utilization(tx)");
	
	
	
	
	if(ar.contains(kpi.toLowerCase())) {
		
		output="kpi_formula_total";	
		
	}
	
	
	else {
		output="kpi_formula";	
	}
	
	
	
	
	
	return output;
	
}

//TODO 
public static ArrayList<String> get_mongodb_distinct_values(FindIterable<Document> documents,ArrayList<String>column_names){	
	StringBuffer sb=new StringBuffer(); 	
	
ArrayList<String> output=new ArrayList<String>();






JSONArray jsonArray = new JSONArray(JSON.serialize(documents));

for(int k=0;k<jsonArray.length();k++) {
	  JSONObject jsonObject1 = jsonArray.getJSONObject(k); 
	  for(String column:column_names) {
		  
		  sb.append(jsonObject1.optString(column)+"@AND@");
	
	  }
	
String value=	 sb.toString().substring(0,sb.length()-5);
	 

	 
	 output.add(value);
	 sb.delete(0, sb.length());
	// 



} 


Set<String> set = new LinkedHashSet<>();
// Add the elements to set
set.addAll(output);

// Clear the list
output.clear();

// add the elements of set
// with no duplicates to the list
output.addAll(set);
;

	


return output;	
	
}


//TODO to subtract interval after giving interval	
public static String sub_mins(String start_time, int interval) {
String output = "";

try {
SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
Date d = df.parse(start_time);
Calendar cal = Calendar.getInstance();
cal.setTime(d);
cal.add(Calendar.MINUTE, interval);
output = df.format(cal.getTime());

} catch (ParseException e) {

	log.error("Exception occurs:----" + e.getMessage(), e);

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


public String current_only_date(String opco) {
	
String output="";	
Date date = new Date();
SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

if(opco.equals("zambia")) {
df.setTimeZone(TimeZone.getTimeZone("Africa/Lusaka"));	
}

String dateTime = df.format(date);
String split[] = dateTime.split("\\s+");

 output = split[0];
return output;	
	
}



//TODO
//to get database name as per api 

public MongoDatabase database(MongoClient connection,String domain,String vendor) {
	
	
if(log.isDebugEnabled()) {	
	log.debug("*************** checked into database **********"+domain+"***"+vendor+"********");	
	}	
MongoDatabase database=null;


Properties config=getProperties();
if(domain.equals("report")&&vendor.equals("report")) {
database=connection.getDatabase(config.getProperty("database.consolidate_reports"));
}


if(domain.equals("TRANSMISSION")&&vendor.equals("ALL")) {
database=connection.getDatabase(config.getProperty("database.consolidate_reports"));
}

if(domain.equals("all")&&vendor.equals("topology")) {
	

database=connection.getDatabase(config.getProperty("mongo.db.database.topology"));
}


if(domain.equals("TRANSMISSION")&&vendor.equals("ERICSSON")) {
	

database=connection.getDatabase(config.getProperty("database.performance_ericsson_transmission"));
}

else if (vendor.equals("NEC")&&domain.equals("TRANSMISSION")) {
database = connection.getDatabase(config.getProperty("database.performance_nec_transmission"));	

}
else if (vendor.equals("NCE")&&domain.equals("TRANSMISSION")) {
database = connection.getDatabase(config.getProperty("database.performance_nce_transmission"));	

}

else if (vendor.equals("SAM")&&domain.equals("TRANSMISSION")) {
database = connection.getDatabase(config.getProperty("database.performance_sam_transmission"));	

}

if(domain.equals("IPRAN")&&vendor.equals("HUAWEI")) {
	

database=connection.getDatabase(config.getProperty("database.performance_zambia_ipran"));
}

else if(domain.equals("IPBB")&&vendor.equals("ZTE")) {
	
//log.debug("*********"+admin_id+"********* checked into getKpiNameExcel   **"+domain+"==========="+vendor_name+"************");
database=connection.getDatabase(config.getProperty("database.performance_zambia_mpbn_zte"));
}

else if(domain.equals("IPBB")&&vendor.equals("DPTECH_FIREWALL")) {
	
	//log.debug("*********"+admin_id+"********* checked into getKpiNameExcel   **"+domain+"==========="+vendor_name+"************");
	database=connection.getDatabase(config.getProperty("database.performance_zambia_dptech_firewall"));
	}



else if(domain.equals("IPBB")&&vendor.equals("ERICSSON")) {

//log.debug("*********"+admin_id+"********* checked into getKpiNameExcel   **"+domain+"==========="+vendor_name+"************");
database=connection.getDatabase(config.getProperty("database.performance_zambia_mpbn_ericsson"));
}

else if(domain.equals("IPBB")&&vendor.equals("JUNIPER")) {

//log.debug("*********"+admin_id+"********* checked into getKpiNameExcel   **"+domain+"==========="+vendor_name+"************");
database=connection.getDatabase(config.getProperty("database.performance_zambia_juniper"));
}

else if(domain.equals("IPBB")&&vendor.equals("NOKIA")) {
	
//log.debug("*********"+admin_id+"********* checked into getKpiNameExcel   **"+domain+"==========="+vendor_name+"************");
database=connection.getDatabase(config.getProperty("database.performance_zambia_nokia"));
}


else if (vendor.equals("ERICSSON")&&domain.equals("RADIO")) {
database = connection.getDatabase(config.getProperty("database.performance_zambia_ericsson"));	

}


else if (domain.equals("CORE")&&vendor.equals("ZTE")) {
database = connection.getDatabase(config.getProperty("database.performance_zambia_pc_zte"));	

}


return database;	
	
}




public MongoDatabase database_5g(MongoClient connection,String protocol) {


	
	
if(log.isDebugEnabled()) {	
	log.debug("*************** checked into database **********"+protocol+"*******");	
	}	
MongoDatabase database=null;


Properties config=getProperties();
if(protocol.equals("ngap")) {
database=connection.getDatabase(config.getProperty("database.protocol.ngap"));
}

else if(protocol.equals("s1ap")) {
database=connection.getDatabase(config.getProperty("database.protocol.s1ap"));
}
else if(protocol.equals("http2")) {
database=connection.getDatabase(config.getProperty("database.protocol.http2"));
}






return database;	
	


}

//TODO to add interval after giving interval	
public static String add_mins(String start_time, int interval) {
String output = "";

try {
SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
Date d = df.parse(start_time);
Calendar cal = Calendar.getInstance();
cal.setTime(d);
cal.add(Calendar.MINUTE, interval);
output = df.format(cal.getTime());
//////////////////System..out.println(output);
} catch (ParseException e) {
	log.error("Exception occurs:----" + e.getMessage(), e);

}
return output;

}

//TODO pattern previous date
public static String previous_day() {	
String output="";
DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
dateFormat.setTimeZone(TimeZone.getTimeZone("Africa/Lusaka"));
Calendar cal = Calendar.getInstance();
cal = Calendar.getInstance();
cal.add(Calendar.DATE, -1);
//////////System.out.println(dateFormat.format(cal.getTime()));
output=dateFormat.format(cal.getTime()).trim();
return output;	
}


//TODO Current date as per zambia

public static String current_date() {	
String output="";
Date date = new Date();
SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
df.setTimeZone(TimeZone.getTimeZone("Africa/Lusaka"));
String dateTime = df.format(date);
output=dateTime.trim();
return output;	

}

public ArrayList<String> ipbb_commands(String domain,String vendor){
	
ArrayList<String> output=new ArrayList<String>();	

if(domain.equals("IPRAN")||domain.equals("IPBB")) {
if(vendor.equals("NOKIA")) {	
output.add("SNMP");		
//output.add("system_cpu_statistics");	
//output.add("port_statistics");	
}

else if(vendor.equals("ERICSSON")) {	
output.add("SNMP");		
//output.add("dot1q counters detail");	
//output.add("port counters detail");	
}

else if(vendor.equals("JUNIPER")) {	
output.add("SNMP");	
//output.add("interfaces statistics");
}

else if(vendor.equals("ZTE")) {	
output.add("SNMP");	

}

}

if(domain.equals("TRANSMISSION")) {
if(vendor.equals("NEC")) {	
output.add("PMON");	
output.add("RMON");	

}
else if(vendor.equals("ERICSSON")) {	
output.add("pdh");	
//output.add("ip_rt");	
output.add("radiolinkpower_rt");	
output.add("radiolinkg826");	
output.add("adaptivecodingandmodulation");	

}

else if(vendor.equals("NCE")) {
output.add("sdh");	
output.add("wdm");		
}
else if(vendor.equals("SAM")) {
output.add("peakandaveragecurrentdatastats");	
output.add("rslhopcurrentdatastats");		
output.add("rsllinkcurrentdatastats");		
output.add("systemcpustats");		
output.add("systemmemorystats");	
}
}

return output;	
	
	
	
}

//TODO get table name
public String to_get_table_name(String input) {

	
ArrayList<String> abc=counter_list(input);	
	



String split_counter=input.trim().replace(".","_").replace("(","").replace(")", "").replace("+", ",").replace("*",",").replace("-",",").replace("/",",");

String splt[]=split_counter.toString().split(",");
String tablename = "";
for(String breaking_counter:splt){
String column;
//getting each name of counter including numbers
String each_counter_name=get_cloumn_name(abc, breaking_counter);	



if(each_counter_name.contains(":")&&!Character.isDigit(breaking_counter.charAt(0))) {

//getting counters containg only alphabets
column=StringUtils.substringAfter(each_counter_name, ":");
tablename=StringUtils.substringBefore(each_counter_name, ":");
}

else {
column=breaking_counter;
//dummy_table is used to get column name for number counter
tablename=dummy_table(abc);
}


}


return tablename;

	
	
	
	
}
public ArrayList<String> counter_list(String input) {
ArrayList<String> output=new ArrayList<String>();

String split_counter=input.trim().replace(".","_").replace("(","").replace(")", "").replace("+", ",").replace("*",",").replace("-",",").replace("/",",");

String splt[]=split_counter.toString().split(",");

for(String spl:splt){
output.add(spl);
}
return output;
}



//TODO  dummy column name if the counter is number
String dummy_table(ArrayList<String> input) {
	
	
//this is used to get any counter name(column name) for getting data

String output="";
	
for(String tb:input) {
if(tb.contains(":")) {
output=StringUtils.substringBefore(tb, ":");
				
break;
}
			
}
			
return output;
		}





//TODO //getting each name of counter including numbers

public String get_cloumn_name(ArrayList<String> input,String contains) {

String output = "";
String other_value="";
String dm="";
for(String ss_input:input) {

if(ss_input.contains(":")) {
other_value=ss_input;
}
}
int t=0;
int k=0;
for(String s_input:input) {

t++;
String verify=StringUtils.substringAfter(s_input, ":");	
if(verify.equals(contains.trim())) {
	

	//////////System.out.println("s_input====="+s_input+"==="+contains);
k=t;
dm=s_input;
output=s_input;
}
}

if(k!=0 && k<input.size()) {
output=dm;
}

else {
output=other_value;
}
return output;

}

//TODO 

public String replace_value(String input) {
String output="";
output=StringUtils.substringAfter(input, "=");
return output;
	
	
	
}

protected String current_date_for_report() {

Date date_month = new Date();
String present_date=new SimpleDateFormat("MM-dd-yyy").format(date_month);
return present_date;	


}
//TODO
//distinct values from 1 where 1
public static ArrayList < String > distinct_1_where_with_hint(MongoDatabase db,String table_name,String index_name,String to_find,String from1,String where1) {
	
MongoCollection < Document > table = db.getCollection(table_name);
BasicDBObject index = null;
index = new BasicDBObject("$hint", index_name);
	
FindIterable < Document > find = table.find(eq(from1,where1)).hint(index);	
	
ArrayList < String > output = new ArrayList < String > ();

JSONArray jsonArray = null;
try {
jsonArray = new JSONArray(JSON.serialize(find));
} catch (JSONException e) {

e.printStackTrace();
}

for (int k = 0; k < jsonArray.length(); k++) {

JSONObject jsonObject1 = jsonArray.getJSONObject(k);

output.add(jsonObject1.optString(to_find));

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

//TODO
//distinct values from 1 where 1
public static ArrayList < String > distinct_1_where_with_hint_regex(MongoDatabase db,String table_name,String index_name,String to_find,String from1,String where1) {
	
MongoCollection < Document > table = db.getCollection(table_name);
BasicDBObject index = null;
index = new BasicDBObject("$hint", index_name);
	
FindIterable < Document > find = table.find(eq(from1, java.util.regex.Pattern.compile("^.*" + where1 + ".*"))).hint(index);	
	
ArrayList < String > output = new ArrayList < String > ();

JSONArray jsonArray = null;
try {
jsonArray = new JSONArray(JSON.serialize(find));
} catch (JSONException e) {

e.printStackTrace();
}

for (int k = 0; k < jsonArray.length(); k++) {

JSONObject jsonObject1 = jsonArray.getJSONObject(k);

output.add(jsonObject1.optString(to_find));

}

Set < String > set = new LinkedHashSet < > ();
//Add the elements to set
set.addAll(output);

//Clear the list
output.clear();

//add the elements of set
//with no duplicates to the list
output.addAll(set);;

return output;

}


//TODO
//distinct values from 1 where 1
public static String connectivity_where_with_hint_regex(MongoDatabase db,String index_name,String to_find,String from1,String where1,String from2,String where2,String from3,String where3) {
	String table_name="microwave_connectivity_performance";
MongoCollection < Document > table = db.getCollection(table_name);
BasicDBObject index = null;
index = new BasicDBObject("$hint", index_name);
/*
System.out.println("from1====="+from1);
System.out.println("where1====="+where1);
System.out.println("from2====="+from2);
System.out.println("where2====="+where2);
System.out.println("from3====="+from3);
System.out.println("where3====="+where3);
	*/
FindIterable < Document > find = table.find(and(eq(from1,where1),eq(from2,where2),eq(from3, java.util.regex.Pattern.compile("^.*" + where3 + ".*")))).hint(index);	
	
ArrayList < String > output = new ArrayList < String > ();

JSONArray jsonArray = null;
try {
jsonArray = new JSONArray(JSON.serialize(find));
} catch (JSONException e) {

e.printStackTrace();
}

for (int k = 0; k < jsonArray.length(); k++) {

JSONObject jsonObject1 = jsonArray.getJSONObject(k);

output.add(jsonObject1.optString(to_find));

}

Set < String > set = new LinkedHashSet < > ();
//Add the elements to set
set.addAll(output);

//Clear the list
output.clear();

//add the elements of set
//with no duplicates to the list
output.addAll(set);;

String out="";
if(output.size()>0) {
	out=output.get(0);
}

else
{
	out="-";
}

return out;

}

//TODO
//distinct values from 1 where 1
public static String connectivity_where_with_hint_regex_nce(MongoDatabase db,String table_name,String index_name,String to_find,String from1,String where1,String from2,String where2,String from3,String where3) {
	
MongoCollection < Document > table = db.getCollection(table_name);
BasicDBObject index = null;
index = new BasicDBObject("$hint", index_name);

/*
 * System.out.println("from1====="+from1);
 * System.out.println("where1====="+where1);
 * System.out.println("from2====="+from2);
 * System.out.println("where2====="+where2);
 * System.out.println("from3====="+from3);
 * System.out.println("where3====="+where3);
 */
	
FindIterable < Document > find = table.find(and(eq(from1,where1),eq(from2,where2),eq(from3,where3))).hint(index);	
	
ArrayList < String > output = new ArrayList < String > ();

JSONArray jsonArray = null;
try {
jsonArray = new JSONArray(JSON.serialize(find));
} catch (JSONException e) {

e.printStackTrace();
}

for (int k = 0; k < jsonArray.length(); k++) {

JSONObject jsonObject1 = jsonArray.getJSONObject(k);

output.add(jsonObject1.optString(to_find));

}

Set < String > set = new LinkedHashSet < > ();
//Add the elements to set
set.addAll(output);

//Clear the list
output.clear();

//add the elements of set
//with no duplicates to the list
output.addAll(set);;

String out="";
if(output.size()>0) {
	out=output.get(0);
}

else
{
	out="-";
}

return out;

}


//TODO Current date as per zambia

public static String current_date_yyyyMMdd() {	
String output="";
Date date = new Date();
SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
df.setTimeZone(TimeZone.getTimeZone("Africa/Lusaka"));
String dateTime = df.format(date);
output=dateTime.trim();
return output;	

}

//TODO pattern previous date
public static String previous_day_yyyyMMdd() {	
String output="";
DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
dateFormat.setTimeZone(TimeZone.getTimeZone("Africa/Lusaka"));
Calendar cal = Calendar.getInstance();
cal = Calendar.getInstance();
cal.add(Calendar.DATE, -1);
//////////System.out.println(dateFormat.format(cal.getTime()));
output=dateFormat.format(cal.getTime()).trim();
return output;	
}

// TODO peak_date for visibility report
public static String peak_date(MongoDatabase database,String table_name,String week) {
String output="";
table_name=table_name+"_visibility";
MongoCollection < Document > collection = database.getCollection(table_name);
ArrayList < Document > docs_current = collection.find(eq("week_number",week)).into(new ArrayList<>());
JSONArray jsonArray = new JSONArray(JSON.serialize(docs_current));

ArrayList<Integer>inputs=new ArrayList<Integer>();

for (int k = 0; k < jsonArray.length(); k++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(k);
//jsonObject1.optString("neState");;
String elements=jsonObject1.optString("Visibility%");
inputs.add(Integer.parseInt(elements));

}
Integer max = Collections.max(inputs);

output=""+max;



DistinctIterable<String> dist=collection.distinct("date",and(eq("week_number",week),eq("Visibility%",output)), String.class);

for(String val:dist) {
        output=val;
}
return output;
}

//TODO insert as per week

public static void insert_as_per_week(MongoDatabase database,String vendor,String week,String date ) {
	
ArrayList<String> column_names=new ArrayList<String>();
column_names.add("Vendor Name");
column_names.add("Total Hops /NE Available as on GIS");
column_names.add("Total Hops visible on NMS");
column_names.add("Need To Recover on NMS");
column_names.add("Visibility%");	
MongoCollection < Document > collection = database.getCollection(vendor+"_visibility");
ArrayList < Document > docs_current = collection.find(and(eq("date",date),eq("week_number",week))).into(new ArrayList<>());;
JSONArray jsonArray = new JSONArray(JSON.serialize(docs_current));

for (int k = 0; k < jsonArray.length(); k++) {
ArrayList<String> column_values=new ArrayList<String>();	
	
JSONObject jsonObject1 = jsonArray.getJSONObject(k);

for(String column_name:column_names) {
column_values.add(jsonObject1.optString(column_name));	
}


insert_mongodb(database,column_names, column_values, "visibility_"+week);

}

	
}

public static  String total(MongoDatabase  database ,String to_find,String date) {
String output="";

MongoCollection < Document > collection = database.getCollection("visibility_"+date);
ArrayList < Document > docs_current = collection.find().into(new ArrayList<>());

JSONArray jsonArray = new JSONArray(JSON.serialize(docs_current));

ArrayList<Integer>inputs=new ArrayList<Integer>();

for (int k = 0; k < jsonArray.length(); k++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(k);
//jsonObject1.optString("neState");;
String elements=jsonObject1.optString(to_find);
inputs.add(Integer.parseInt(elements));

}

output=""+sum(inputs);
	
	return output;
}


public static Integer sum(ArrayList<Integer>m) {
	int sum = 0;
	for(int d : m)
	    sum += d;
	return sum;
}

protected static int percentage(int n,int v) {
float percent = (n * 100.0f) / v;
return Math.round(percent);
}

//TO pick blink rings
public  String blink_ring(MongoDatabase database,String ring) {
MongoCollection <Document>collection=database.getCollection("ping_status");  

DistinctIterable<String> distinct_values=collection.distinct("ping_status",eq("ring",ring), String.class);
String output="no";
for(String distinct:distinct_values) {
	if(distinct.equals("no")||distinct.equals("between")) {
		output="yes";
		break;
	}
	else {
		output="no";
	}
}

	
	
	return output;
	
}

public  static Session getSession(String ipaddress, String username, String password) throws JSchException {
    JSch       jsch    = new JSch();
    Session    session = jsch.getSession(username, ipaddress, 22);
    Properties config  = new Properties();

    config.put("StrictHostKeyChecking", "no");
    session.setConfig(config);;
    session.setPassword(password);

    try {
       session.connect();
    	
    } catch (Exception e) {

  
   //	 logger.log(Level.SEVERE, "login failed ======" + ipaddress+"==="+username+"===="+password);

    
        
    }

    return session;
}

public static double calculateAverage_gauge(ArrayList <Double> marks) {
	
	Double output=0.0;
	  Double sum = 0.0;
  if(!marks.isEmpty()) {
    for (Double mark : marks) {
        sum += mark;
    }
    Double uo=sum.doubleValue() / marks.size();
	String	ss=String.format("%.2f", uo);
	
	output=Double.parseDouble(ss);
    return output;
    
    
  }
  
	String	ss=String.format("%.2f", sum);
	
	output=Double.parseDouble(ss);
  return output;
	}
}
