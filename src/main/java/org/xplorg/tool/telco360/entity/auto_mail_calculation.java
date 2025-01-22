package org.xplorg.tool.telco360.entity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class auto_mail_calculation {

	
Logger log = LogManager.getLogger(auto_mail_calculation.class.getName());
	
	
public ArrayList output(Connection	db_connect,String formula,String actual_formula,String node1,String element_name,String start_date,String end_date,int check_time,String starttime,String endtime,String kpiname,ArrayList time
,String calculation_type,ArrayList <String>time_size){
	
//System.out.println(time_size);
ArrayList <String>result=new ArrayList<String>();
if(log.isDebugEnabled()) {	
log.debug("*************** checked into output ****************");	
}

try {	
Statement st=db_connect.createStatement();


ArrayList <Long>counter_arraylist[] = new ArrayList[100];


int increment_with_each_counter = 0;


// sb_formula  gets the formula in alphabets like(a+b)



StringBuilder sb_formula=new StringBuilder();



//jj counts number of items in formula--like number and alphabets
int jj=0;


//---converting formula in alpha form like(a+b)

//---changing special characters into ","
String formula_spliter=formula.replace("(",",(,").replace(")",",),").replace("+",",+,").replace("*", ",*,").replace("-",",-,").replace("/",",/,");

String splt_formula[]=formula_spliter.toString().split(",");

for(String spls:splt_formula){
String each_charcter=spls;
//checking  whether the character is in number of alpha
if(spls.length()>1||spls.matches(".*\\d.*")){
jj++;

String char_to_alpha = "";
char_to_alpha=counterToAlphabet(jj, char_to_alpha);
each_charcter=each_charcter.replace(spls, char_to_alpha);
}

sb_formula.append(each_charcter);

}
//System.out.println(sb_formula);
//---here we get the formula in (a+b) form



// check is used for getting only alpha and number from formula

ArrayList<String> check=counter_list(actual_formula);

//tablename is used to get table name(before :)

String tablename = "";

// now breaking each counter to calculate formula one by one
String split_counter=formula.trim().replace(".","_").replace("(","").replace(")", "").replace("+", ",").replace("*",",").replace("-",",").replace("/",",");

String splt[]=split_counter.toString().split(",");


// to know the time difference to calculate interval
int time_difference=0;

for(String breaking_counter:splt){
	
// this is used to join time with value to get the missing value if time is missing
ArrayList<String> join_val_time=new ArrayList<String>();

	
//getting each name of counter including numbers
String each_counter_name=get_cloumn_name(check, breaking_counter);



//coulmn_name=counter name
String column = "";
if(each_counter_name.contains(":")&&!breaking_counter.matches(".*\\d.*")) {
	
// getting counters containg only alphabets
column=StringUtils.substringAfter(each_counter_name, ":");
tablename=StringUtils.substringBefore(each_counter_name, ":");
}

else {
column=breaking_counter;
//dummy_table is used to get column name for number counter
tablename=dummy_table(check);
}

//as increment_with_each_counter increases new arraylist is generating 


if(column.length()>0){
increment_with_each_counter++;
counter_arraylist[increment_with_each_counter]=new ArrayList<Long>();
}

// initially adding 0 because to avoid error if column name is absent
if(increment_with_each_counter>1){
for(int u=0;u<counter_arraylist[1].size();u++) {
long a=0;
counter_arraylist[increment_with_each_counter].add(a);
}
}


// get_time_diff is used to get the measurement time of files 

String time_diff=get_time_diff(db_connect, tablename);

//// get the time difference to sum files
//if(!time_diff.equals("0")) {
//int time1=Integer.parseInt(time_diff);
//time_difference=60/time1;
//}
//
//// query for getting values from each counter
//
//String query="";
//
//// condition if the user need to get data of whole day

ArrayList<String>calculated_values;
if(check_time==0) {
starttime="00:00:00";
endtime="23:00:00";

calculated_values=hourly_time(db_connect,start_date,starttime,endtime,"60",column,tablename,element_name,calculation_type);
}


//condition if the user need to get data of perticular period
else {
	
calculated_values=hourly_time(db_connect,start_date,starttime,endtime,"60",column,tablename,element_name,calculation_type);
}




ArrayList<Long> sum_array=new ArrayList<Long>();

try {
	
//System.out.println("calculated_values==="+calculated_values);

// clearing the counter_arraylist to insert new values
counter_arraylist[increment_with_each_counter].clear();



for(String filter_values:calculated_values) {
	
double d=Double.parseDouble((String)filter_values.replace(".000000",""));


DecimalFormat df = new DecimalFormat("##.##");

String ss=df.format(d);

long a=Long.parseLong(ss);

//value from table

try {

sum_array.add(a);


////System.out.println(a);
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
ex.printStackTrace();
	}

}

} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
ex.printStackTrace();
}


if(!time_diff.equals("0")) {
	
	// getting values after calculations on the bases of time difference

counter_arraylist[increment_with_each_counter]=sum_array;
//System.out.println(counter_arraylist[increment_with_each_counter]);

if(counter_arraylist[increment_with_each_counter].size()!=time_size.size()) {
	counter_arraylist[increment_with_each_counter].add((long) 0);
}
counter_arraylist[increment_with_each_counter].removeAll(Arrays.asList(null,""));
}

/*else {
for(int i=0;i<counter_arraylist[1].size();i++) {

counter_arraylist[increment_with_each_counter].removeAll(Arrays.asList(null,""));

}
}*/

}

Expression exp=null;
try {
	 exp = new ExpressionBuilder(sb_formula.toString()).variables("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
"l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z").build();
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
ex.printStackTrace();
}
//System.out.println(time_size.size());
for (int i = 0; i < time_size.size(); i++) {

if (increment_with_each_counter == 1) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
}
else if (increment_with_each_counter == 2) {

exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
}

else if (increment_with_each_counter == 3) {

exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
}

else if (increment_with_each_counter == 4) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
} else if (increment_with_each_counter == 5) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
} else if (increment_with_each_counter == 6) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));

} else if (increment_with_each_counter == 7) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));

} else if (increment_with_each_counter == 8) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));

} else if (increment_with_each_counter == 9) {

//////////////////System..out.println(counter_arraylist[1].get(i)+"--"+counter_arraylist[2].get(i)+"--"+counter_arraylist[3].get(i)+"--"+counter_arraylist[4].get(i)+"--"+counter_arraylist[5].get(i)+"--"+counter_arraylist[6].get(i)+"--"+counter_arraylist[7].get(i)+"--"+counter_arraylist[8].get(i)+"--"+counter_arraylist[9].get(i)+"");

exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));

} else if (increment_with_each_counter == 10) {
//////////////////System..out.println(counter_arraylist[1].get(i)+"--"+counter_arraylist[2].get(i)+"--"+counter_arraylist[3].get(i)+"--"+counter_arraylist[4].get(i)+"--"+counter_arraylist[5].get(i)+"--"+counter_arraylist[6].get(i)+"--"+counter_arraylist[7].get(i)+"--"+counter_arraylist[8].get(i)+"--"+counter_arraylist[9].get(i)+"--"+counter_arraylist[10].get(i)+"");

exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));

} else if (increment_with_each_counter == 11) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));

} else if (increment_with_each_counter == 12) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));
exp.setVariable("l", (long) counter_arraylist[12].get(i));

} else if (increment_with_each_counter == 13) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));
exp.setVariable("l", (long) counter_arraylist[12].get(i));
exp.setVariable("m", (long) counter_arraylist[13].get(i));

} else if (increment_with_each_counter == 14) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));
exp.setVariable("l", (long) counter_arraylist[12].get(i));
exp.setVariable("m", (long) counter_arraylist[13].get(i));
exp.setVariable("n", (long) counter_arraylist[14].get(i));

} else if (increment_with_each_counter == 15) {
////////////////////System..out.println(counter_arraylist[1]+"--"+counter_arraylist[2]+"--"+counter_arraylist[3]+"--"+counter_arraylist[4]+"--"+counter_arraylist[5]+"--"+counter_arraylist[6]+"--"+counter_arraylist[7]+"--"+counter_arraylist[8]+"--"+counter_arraylist[9]+"--"+counter_arraylist[10]+"--"+counter_arraylist[11]+"--"+counter_arraylist[12]+"--"+counter_arraylist[13]+"--"+counter_arraylist[14]+"--"+counter_arraylist[15]);

exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));
exp.setVariable("l", (long) counter_arraylist[12].get(i));
exp.setVariable("m", (long) counter_arraylist[13].get(i));
exp.setVariable("n", (long) counter_arraylist[14].get(i));
exp.setVariable("o", (long) counter_arraylist[15].get(i));

} else if (increment_with_each_counter == 16) {


//////////////////////System..out.println(counter_arraylist[1].get(i)+"--"+counter_arraylist[2].get(i)+"--"+counter_arraylist[3].get(i)+"--"+counter_arraylist[4].get(i)+"--"+counter_arraylist[5].get(i)+"--"+counter_arraylist[6].get(i)+"--"+counter_arraylist[7].get(i)+"--"+counter_arraylist[8].get(i)+"--"+counter_arraylist[9].get(i)+"--"+counter_arraylist[10].get(i)+"--"+counter_arraylist[11].get(i)+"--"+counter_arraylist[12].get(i)+"--"+counter_arraylist[13].get(i)+"--"+counter_arraylist[14].get(i)+"--"+counter_arraylist[15].get(i)+"--"+counter_arraylist[16].get(i));
////////////////////System..out.println(counter_arraylist[1].get(i));
exp.setVariable("a", (long) counter_arraylist[1].get(i));
////////////////////System..out.println(counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
////////////////////System..out.println(counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
////////////////////System..out.println(counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
////////////////////System..out.println(counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
////////////////////System..out.println(counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
////////////////////System..out.println(counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
////////////////////System..out.println(counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
////////////////////System..out.println(counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
////////////////////System..out.println(counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
////////////////////System..out.println(counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));
////////////////////System..out.println(counter_arraylist[11].get(i));
exp.setVariable("l", (long) counter_arraylist[12].get(i));
////////////////////System..out.println(counter_arraylist[12].get(i));
exp.setVariable("m", (long) counter_arraylist[13].get(i));
////////////////////System..out.println(counter_arraylist[13].get(i));
exp.setVariable("n", (long) counter_arraylist[14].get(i));
////////////////////System..out.println(counter_arraylist[14].get(i));
exp.setVariable("o", (long) counter_arraylist[15].get(i));
////////////////////System..out.println(counter_arraylist[15].get(i));
exp.setVariable("p", (long) counter_arraylist[16].get(i));
////////////////////System..out.println(counter_arraylist[16].get(i));

} else if (increment_with_each_counter == 17) {

////////////////////System..out.println(counter_arraylist[1]+"--"+counter_arraylist[2]+"--"+counter_arraylist[3]+"--"+counter_arraylist[4]+"--"+counter_arraylist[5]+"--"+counter_arraylist[6]+"--"+counter_arraylist[7]+"--"+counter_arraylist[8]+"--"+counter_arraylist[9]+"--"+counter_arraylist[10]+"--"+counter_arraylist[11]+"--"+counter_arraylist[12]+"--"+counter_arraylist[13]+"--"+counter_arraylist[14]+"--"+counter_arraylist[15]+"--"+counter_arraylist[16]+"--"+counter_arraylist[17]);

exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));
exp.setVariable("l", (long) counter_arraylist[12].get(i));
exp.setVariable("m", (long) counter_arraylist[13].get(i));
exp.setVariable("n", (long) counter_arraylist[14].get(i));
exp.setVariable("o", (long) counter_arraylist[15].get(i));
exp.setVariable("p", (long) counter_arraylist[16].get(i));
exp.setVariable("q", (long) counter_arraylist[17].get(i));

} else if (increment_with_each_counter == 18) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));
exp.setVariable("l", (long) counter_arraylist[12].get(i));
exp.setVariable("m", (long) counter_arraylist[13].get(i));
exp.setVariable("n", (long) counter_arraylist[14].get(i));
exp.setVariable("o", (long) counter_arraylist[15].get(i));
exp.setVariable("p", (long) counter_arraylist[16].get(i));
exp.setVariable("q", (long) counter_arraylist[17].get(i));
exp.setVariable("r", (long) counter_arraylist[18].get(i));

} else if (increment_with_each_counter == 19) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));
exp.setVariable("l", (long) counter_arraylist[12].get(i));
exp.setVariable("m", (long) counter_arraylist[13].get(i));
exp.setVariable("n", (long) counter_arraylist[14].get(i));
exp.setVariable("o", (long) counter_arraylist[15].get(i));
exp.setVariable("p", (long) counter_arraylist[16].get(i));
exp.setVariable("q", (long) counter_arraylist[17].get(i));
exp.setVariable("r", (long) counter_arraylist[18].get(i));
exp.setVariable("s", (long) counter_arraylist[19].get(i));

} else if (increment_with_each_counter == 20) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));
exp.setVariable("l", (long) counter_arraylist[12].get(i));
exp.setVariable("m", (long) counter_arraylist[13].get(i));
exp.setVariable("n", (long) counter_arraylist[14].get(i));
exp.setVariable("o", (long) counter_arraylist[15].get(i));
exp.setVariable("p", (long) counter_arraylist[16].get(i));
exp.setVariable("q", (long) counter_arraylist[17].get(i));
exp.setVariable("r", (long) counter_arraylist[18].get(i));
exp.setVariable("s", (long) counter_arraylist[19].get(i));
exp.setVariable("t", (long) counter_arraylist[20].get(i));

} else if (increment_with_each_counter == 21) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));
exp.setVariable("l", (long) counter_arraylist[12].get(i));
exp.setVariable("m", (long) counter_arraylist[13].get(i));
exp.setVariable("n", (long) counter_arraylist[14].get(i));
exp.setVariable("o", (long) counter_arraylist[15].get(i));
exp.setVariable("p", (long) counter_arraylist[16].get(i));
exp.setVariable("q", (long) counter_arraylist[17].get(i));
exp.setVariable("r", (long) counter_arraylist[18].get(i));
exp.setVariable("s", (long) counter_arraylist[19].get(i));
exp.setVariable("t", (long) counter_arraylist[20].get(i));
exp.setVariable("u", (long) counter_arraylist[21].get(i));

} else if (increment_with_each_counter == 22) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));
exp.setVariable("l", (long) counter_arraylist[12].get(i));
exp.setVariable("m", (long) counter_arraylist[13].get(i));
exp.setVariable("n", (long) counter_arraylist[14].get(i));
exp.setVariable("o", (long) counter_arraylist[15].get(i));
exp.setVariable("p", (long) counter_arraylist[16].get(i));
exp.setVariable("q", (long) counter_arraylist[17].get(i));
exp.setVariable("r", (long) counter_arraylist[18].get(i));
exp.setVariable("s", (long) counter_arraylist[19].get(i));
exp.setVariable("t", (long) counter_arraylist[20].get(i));
exp.setVariable("u", (long) counter_arraylist[21].get(i));
exp.setVariable("v", (long) counter_arraylist[22].get(i));

}

else if (increment_with_each_counter == 23) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));
exp.setVariable("l", (long) counter_arraylist[12].get(i));
exp.setVariable("m", (long) counter_arraylist[13].get(i));
exp.setVariable("n", (long) counter_arraylist[14].get(i));
exp.setVariable("o", (long) counter_arraylist[15].get(i));
exp.setVariable("p", (long) counter_arraylist[16].get(i));
exp.setVariable("q", (long) counter_arraylist[17].get(i));
exp.setVariable("r", (long) counter_arraylist[18].get(i));
exp.setVariable("s", (long) counter_arraylist[19].get(i));
exp.setVariable("t", (long) counter_arraylist[20].get(i));
exp.setVariable("u", (long) counter_arraylist[21].get(i));
exp.setVariable("v", (long) counter_arraylist[22].get(i));
exp.setVariable("w", (long) counter_arraylist[23].get(i));

} else if (increment_with_each_counter == 24) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));
exp.setVariable("l", (long) counter_arraylist[12].get(i));
exp.setVariable("m", (long) counter_arraylist[13].get(i));
exp.setVariable("n", (long) counter_arraylist[14].get(i));
exp.setVariable("o", (long) counter_arraylist[15].get(i));
exp.setVariable("p", (long) counter_arraylist[16].get(i));
exp.setVariable("q", (long) counter_arraylist[17].get(i));
exp.setVariable("r", (long) counter_arraylist[18].get(i));
exp.setVariable("s", (long) counter_arraylist[19].get(i));
exp.setVariable("t", (long) counter_arraylist[20].get(i));
exp.setVariable("u", (long) counter_arraylist[21].get(i));
exp.setVariable("v", (long) counter_arraylist[22].get(i));
exp.setVariable("w", (long) counter_arraylist[23].get(i));
exp.setVariable("x", (long) counter_arraylist[24].get(i));

} else if (increment_with_each_counter == 25) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));
exp.setVariable("l", (long) counter_arraylist[12].get(i));
exp.setVariable("m", (long) counter_arraylist[13].get(i));
exp.setVariable("n", (long) counter_arraylist[14].get(i));
exp.setVariable("o", (long) counter_arraylist[15].get(i));
exp.setVariable("p", (long) counter_arraylist[16].get(i));
exp.setVariable("q", (long) counter_arraylist[17].get(i));
exp.setVariable("r", (long) counter_arraylist[18].get(i));
exp.setVariable("s", (long) counter_arraylist[19].get(i));
exp.setVariable("t", (long) counter_arraylist[20].get(i));
exp.setVariable("u", (long) counter_arraylist[21].get(i));
exp.setVariable("v", (long) counter_arraylist[22].get(i));
exp.setVariable("w", (long) counter_arraylist[23].get(i));
exp.setVariable("x", (long) counter_arraylist[24].get(i));
exp.setVariable("x", (long) counter_arraylist[25].get(i));

} else if (increment_with_each_counter == 26) {
exp.setVariable("a", (long) counter_arraylist[1].get(i));
exp.setVariable("b", (long) counter_arraylist[2].get(i));
exp.setVariable("c", (long) counter_arraylist[3].get(i));
exp.setVariable("d", (long) counter_arraylist[4].get(i));
exp.setVariable("e", (long) counter_arraylist[5].get(i));
exp.setVariable("f", (long) counter_arraylist[6].get(i));
exp.setVariable("g", (long) counter_arraylist[7].get(i));
exp.setVariable("h", (long) counter_arraylist[8].get(i));
exp.setVariable("i", (long) counter_arraylist[9].get(i));
exp.setVariable("j", (long) counter_arraylist[10].get(i));
exp.setVariable("k", (long) counter_arraylist[11].get(i));
exp.setVariable("l", (long) counter_arraylist[12].get(i));
exp.setVariable("m", (long) counter_arraylist[13].get(i));
exp.setVariable("n", (long) counter_arraylist[14].get(i));
exp.setVariable("o", (long) counter_arraylist[15].get(i));
exp.setVariable("p", (long) counter_arraylist[16].get(i));
exp.setVariable("q", (long) counter_arraylist[17].get(i));
exp.setVariable("r", (long) counter_arraylist[18].get(i));
exp.setVariable("s", (long) counter_arraylist[19].get(i));
exp.setVariable("t", (long) counter_arraylist[20].get(i));
exp.setVariable("u", (long) counter_arraylist[21].get(i));
exp.setVariable("v", (long) counter_arraylist[22].get(i));
exp.setVariable("w", (long) counter_arraylist[23].get(i));
exp.setVariable("x", (long) counter_arraylist[24].get(i));
exp.setVariable("y", (long) counter_arraylist[25].get(i));
exp.setVariable("z", (long) counter_arraylist[26].get(i));

}



NumberFormat format = new DecimalFormat("##.#####");
try{

String		ggs = format.format(exp.evaluate());

double dd=Double.parseDouble(ggs);

DecimalFormat decimalFormat = new DecimalFormat("#");

decimalFormat.setMaximumFractionDigits(8);



String sss=String.format("%.5f",dd);

result.add(sss);

}catch(Exception ex){

if(ex.getMessage() == "Division by zero!"){

result.add("0");
}

if(ex.getMessage() == "Mismatched parentheses detected."){
}
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();

}

}
}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
	
}

////System.out.println(result);
return result;

}



public String get_cloumn_name(ArrayList<String> input,String contains) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into get_cloumn_name ****************");	
}

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
if(s_input.trim().contains(contains.trim())) {

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

public ArrayList<String> counter_list(String input) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into counter_list ****************");	
}
	
ArrayList<String> output=new ArrayList<String>();

String split_counter=input.trim().replace(".","_").replace("(","").replace(")", "").replace("+", ",").replace("*",",").replace("-",",").replace("/",",");

String splt[]=split_counter.toString().split(",");

for(String spl:splt){
output.add(spl);
}
return output;
}

public String get_time_diff(Connection con,String table_name) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into get_time_diff ****************");	
}
	
String output="0";

try {
Statement st_table=con.createStatement();
	
ResultSet rs=st_table.executeQuery("select distinct measurementinterval from "+table_name+" ");

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
//ex.printStackTrace();
}	
output="15";
return output;
	
}

public ArrayList<Long> sum_array(ArrayList<Long> input,int intervl_values,String table_type,String calculation_type){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into sum_array ****************");	
}
////System..out.println("calculation hoi");
ArrayList<Long> output=new ArrayList<>();
ArrayList<Double> ar1=new ArrayList<Double>();;
int matching_with_interval=0;

for(int i=0;i<input.size();i++) {

double d=input.get(i);
ar1.add(d)	;
matching_with_interval++;
//getting values after interval
if(matching_with_interval==intervl_values) {
//change to zero after each check
matching_with_interval=0;
	
	
//output after applying calculation_type
double sum_array = 0;


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
long a=Long.parseLong(table_type.trim());
output.add(a);
}

ar1=new ArrayList<Double>();

}
}

return output;	

}

public double sum_array_individual(ArrayList<Double> input) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into sum_array_individual ****************");	
}
//	//System..out.println(input);
double sum = 0;
for(int i = 0; i < input.size(); i++)
sum += input.get(i);
return sum;

}

public double peak_value(ArrayList<Double> input) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into peak_value ****************");	
}
double sum = 0;
sum= Collections.max(input);
return sum;

}
public double calculateAverage(ArrayList <Double> input) {
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

public String counterToAlphabet(int a, String b)
{
if(log.isDebugEnabled()) {	
log.debug("*************** checked into counterToAlphabet ****************");	
}
	
if( a==1)
{
b="a";
}
else if(a==2)
{
b="b";
}
else if(a==3)
{
b="c";
}

else if(a==4)
{
b="d";
}
else if(a==5)
{
b="e";
}
else if(a==6)
{
b="f";
}
else if(a==7)
{
b="g";
}
else if(a==8)
{
b="h";
}
else if(a==9)
{
b="i";
}
else if(a==10)
{
b="j";
}

else if(a==11)
{
b="k";
}
else if(a==12)
{
b="l";
}
else if(a==13)
{
b="m";
}
else if(a==14)
{
b="n";
}
else if(a==15)
{
b="o";
}
else if(a==16)
{
b="p";
}

else if(a==17)
{
b="q";
}
else if(a==18)
{
b="r";
}
else if(a==19)
{
b="s";
}
else if(a==20)
{
b="t";
}
else if(a==21)
{
b="u";
}
else if(a==22)
{
b="v";
}

else if(a==23)
{
b="w";
}
else if(a==24)
{
b="x";
}
else if(a==25)
{
b="y";
}
else if(a==26)
{
b="z";
}

return b;
}


public static ArrayList<String> get_time_seq(String start,String end,String interval){
	
	
	ArrayList<String> output=new ArrayList<String>();
	output.add(start);
	
	int ii=Integer.parseInt(interval);
	
	for(int i=0;i<100;i++) {
		
		
		String out=add_mins(start,ii);
		
		start=out;
		output.add(start.trim());
		
		out="";
		
		if(start.equals(end)) {
			break;
		}

		
	}
	
	return output;
	
}
	
	
	
	public static String add_mins(String start_time,int interval ) {
		String output = "";

 try {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
 Date d = df.parse(start_time); 
 Calendar cal = Calendar.getInstance();
 cal.setTime(d);
 cal.add(Calendar.MINUTE, interval);
 output = df.format(cal.getTime());
////System..out.println(output);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
	return output;
	
}


public static ArrayList <String> get_filter_value(ArrayList <String>com_value,ArrayList<String> seq_vlue ) {
	
ArrayList<String> output=new ArrayList<String>();

String value="";

for(String sq_time:seq_vlue) {
	
	for(String com:com_value) {
		
		String check1=StringUtils.substringAfter(com, "@")	;
//	//System..out.println("check1==="+check1+"==="+sq_time);
	if(sq_time.trim().equals(check1.trim())) {
	
value=StringUtils.substringBefore(com, "@")	;
			break;
			
		
		}
		
		else {
			value=value;
		}
		
	
		
	}
	String a;

	if(value.length()<1) {
		output.add("0")	;	
	}
	
	else {
		output.add(value)	;	
	}
	
	
}


	
	return output;
	
}	


String dummy_table(ArrayList<String> input) {
	
	
// this is used to get any counter name(column name) for getting data
	
	String output="";
	
for(String tb:input) {
	
	if(tb.contains(":")) {
output=StringUtils.substringBefore(tb, ":");
			
			break;
		}
		
	}
		
		
		return output;
		
		
		
		
	}



public static ArrayList<String> hourly_time(Connection connection,String date,String start_time,String end_time,String interval,String counter
		,String table_name,String where_condition,String calculation_type) {
	ArrayList<String>output=new ArrayList<String>();
	


ArrayList<String> get_time_seq=get_time_seq(start_time, end_time, interval);


String calc="";

if(calculation_type.equals("sum")) {
	calc="sum";
}

else if(calculation_type.equals("average")) {
	calc="avg";
}

else if(calculation_type.equals("peak")) {
calc="max";
}




for(String time:get_time_seq) {
	String endtime=add_mins( time,Integer.parseInt(interval)) ;
	
	if(counter.matches("[+-]?\\d*(\\.\\d+)?")) {
		calc="";
	}
	else {
		calc=calc;
	}
	
String query="SELECT  distinct "+calc+"("+counter+")  from "+table_name+" where site_id = '"+where_condition+"' and start_date BETWEEN '"+date+"' AND '"+date+"' AND   start_time BETWEEN '"+time+"' and '"+endtime+"'";	
//System.out.println(query);	

try {
	Statement st=connection.createStatement();
ResultSet rs=st.executeQuery(query);	

if (rs.next() == false) {
	output.add("0");
   // //////System.out.println("=="+0);
  } else {

    do {
      String data = rs.getString(1);
      if(data==null) {
    	  output.add("0");
    	 // //////System.out.println("12---"+0);
      }
      else {
    	  output.add(data);
    	//  //////System.out.println("11---"+data);
      }
     
    } while (rs.next());
  }

} catch (SQLException e) {

	//e.printStackTrace();
}



}
return output;
	
	
	
	

}



}
