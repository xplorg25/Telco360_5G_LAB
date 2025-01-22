package org.xplorg.tool.telco360.performanceDAO.implementation;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class date_time_relation {

static Logger log = LogManager.getLogger(date_time_relation.class.getName());

//TODO get date time relation for time axis as well to get y axis values
public ArrayList < String > date_time_relations(MongoDatabase db, String start_date, String end_date, String start_time, String end_time, String interval) {

ArrayList < String > output = new ArrayList < String > ();
ArrayList < String > unique_dates = new ArrayList < String > ();
ArrayList < String > date_time = new ArrayList < String > ();
unique_dates = mongo_get_name(db, start_date, end_date);
if (interval.equals("Day")) {
for (String date: unique_dates) {
output.add(date);
}

} else {
for (String date: unique_dates) {

ArrayList < String > time = get_time_seq(start_time, end_time, interval);

for (String value_time: time) {

date_time.add(date + "/" + value_time);

}
}
//date_time.add("00");
for (int i = 0; i < date_time.size(); i++) {

if ((i + 1) == date_time.size()) {
break;
}
String value1 = date_time.get(i);

String value2 = date_time.get(i + 1);

String tablename = StringUtils.substringBefore(value1, "/");

String from = StringUtils.substringAfter(value1, "/");

String to = StringUtils.substringAfter(value2, "/");

if ((from.equals(to))) {

} else if (from.equals("23:45:00") && to.equals("00:00:00")) {

} else if (from.equals("23:55:00") && to.equals("00:00:00")) {

} else {
//		////System.out.println("===>  " + from + "====" + to);
output.add(tablename + "/" + from);
}

}
}
return output;

}

//TODO get date time relation for time axis as well to get y axis values
public ArrayList < String > date_time_relations_report(ArrayList < String > unique_dates, String start_time, String end_time, String interval) {

ArrayList < String > output = new ArrayList < String > ();

ArrayList < String > date_time = new ArrayList < String > ();

if (interval.equals("Day")) {
for (String date: unique_dates) {
output.add(date);
}

} else {
for (String date: unique_dates) {

ArrayList < String > time = get_time_seq(start_time, end_time, interval);

for (String value_time: time) {

date_time.add(date + "/" + value_time);

}
}
//date_time.add("00");
for (int i = 0; i < date_time.size(); i++) {

if ((i + 1) == date_time.size()) {
break;
}
String value1 = date_time.get(i);

String value2 = date_time.get(i + 1);

String tablename = StringUtils.substringBefore(value1, "/");

String from = StringUtils.substringAfter(value1, "/");

String to = StringUtils.substringAfter(value2, "/");

if ((from.equals(to))) {

} else if (from.equals("23:45:00") && to.equals("00:00:00")) {

} else if (from.equals("23:55:00") && to.equals("00:00:00")) {

} else {
//		////System.out.println("===>  " + from + "====" + to);
output.add(tablename + "/" + from);
}

}
}
return output;

}

//TODO to get time sequence as per interval
public static ArrayList < String > get_time_seq(String start, String end, String interval) {

ArrayList < String > output = new ArrayList < String > ();
output.add(start);

int ii = Integer.parseInt(interval);

for (int i = 0; i < 20000; i++) {

String out = add_mins(start, ii);

start = out;
output.add(start.trim());

out = "";

if (start.equals(end)) {
break;
}

}

return output;

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
////////////System..out.println(output);
} catch (ParseException e) {
e.printStackTrace();
log.error("Performance   Exception occurs:------------" + e.getMessage(), e);
}
return output;

}

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

} catch (Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);

}
return output;

}

}