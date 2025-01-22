package org.xplorg.tool.telco360.performanceDAO.implementation;
import static com.mongodb.client.model.Filters.eq;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xplorg.tool.telco360.entity.GenericPerformance;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.util.JSON;
public class excel_visibility_graph_printing extends GenericPerformance{
Logger log = LogManager.getLogger(excel_visibility_graph_printing.class.getName());


public void print_visibility_graph(MongoDatabase database,String number_of_weeks,String admin_id) {
	



int last_weeks=Integer.parseInt(number_of_weeks);	  

int week=0;
try {
String input = current_date_yyyyMMdd();
String format = "yyyyMMdd";

SimpleDateFormat df = new SimpleDateFormat(format);
Date date = df.parse(input);

Calendar cal = Calendar.getInstance();
cal.setTime(date);
week = cal.get(Calendar.WEEK_OF_YEAR);
	

} catch (ParseException e) {
log.error("Performance   Exception occurs:------" + admin_id + "---" + e.getMessage(), e);
}	
ArrayList<String>week_groups=new ArrayList<String>();
if(number_of_weeks.equals("0")) {
week_groups.add(""+week);	
}

else {
for(int i=week-1;i>(week-(last_weeks+1));i--) {

	week_groups.add(""+i);
}
	
}




ArrayList<String> column_names_week=new ArrayList<String>();

column_names_week.add("week_count");

column_names_week.add("total_elements");
column_names_week.add("visibility_ok");
column_names_week.add("visibility_not_ok");
column_names_week.add("visibility_%");
ArrayList<String> vendors=new ArrayList<String>();

vendors.add("atn");
vendors.add("cx");
vendors.add("ericsson");
vendors.add("mpr");
vendors.add("nec");
vendors.add("u2000");


ArrayList<String> column_names=new ArrayList<String>();
column_names.add("Vendor Name");
column_names.add("Total Hops /NE Available as on GIS");
column_names.add("Total Hops visible on NMS");
column_names.add("Need To Recover on NMS");
column_names.add("Visibility%");	
		
		
		
for(String week_count:week_groups) {

MongoCollection < Document > collection = database.getCollection("visibility_"+week_count);
collection.drop();
ArrayList<String> column_values=new ArrayList<String>();	
ArrayList<String> column_values_week=new ArrayList<String>();	
for(String vendor:vendors) {
	
	//getting max date where value is maximum for each week
String max_date=peak_date(database, vendor, week_count);





insert_as_per_week(database, vendor, week_count, max_date);	
	
}
column_values_week.add(week_count);
int total=Integer.parseInt(total(database, "Total Hops /NE Available as on GIS",week_count));
column_values_week.add(""+total);
int present=Integer.parseInt(total(database, "Total Hops visible on NMS",week_count));
column_values_week.add(""+present);
int percentage=percentage(present,total);
column_values.add("Grand Total");
column_values.add(total(database, "Total Hops /NE Available as on GIS",week_count));
column_values.add(total(database, "Total Hops visible on NMS",week_count));
column_values.add(total(database, "Need To Recover on NMS",week_count));
column_values_week.add(""+total(database, "Need To Recover on NMS",week_count));
column_values.add(""+percentage);
column_values_week.add(""+percentage);

insert_mongodb(database,column_names, column_values, "visibility_"+week_count );
insert_mongodb(database,column_names_week, column_values_week, "weekly_report");

		
	}



MongoCollection < Document > collection = database.getCollection("weekly_report");

ArrayList<String> countries=new ArrayList<String>();   
countries.add("Zambia");
countries.add("Madagascar");
countries.add("Malawi");
countries.add("Uganda");

countries.add("Tanzania");
countries.add("Kenya");
countries.add("Chad");
countries.add("Gabon Seychelles Niger Cango-B");
final DefaultCategoryDataset dataset = new DefaultCategoryDataset();  
for(String country:countries) {
	
for(String weeks:week_groups) {
double average_value=	weekly_average(weeks, collection);

if(country.equals("Zambia")) {
dataset.addValue( average_value , "WEEK "+weeks , country);  
country_updates_week(database,"WEEK "+weeks,""+average_value,country);
}
else {
	dataset.addValue( 0.0 , "WEEK "+weeks , country);  
	country_updates_week(database,"WEEK "+weeks,"-",country);
}

}

}
JFreeChart  barChart=null;
if(number_of_weeks.equals("0")) {
barChart = ChartFactory.createBarChart3D( "Average Visibility Trend of Current Week ("+week+")","Weeks","%",dataset,PlotOrientation.VERTICAL,true,true,false);
}

else if(number_of_weeks.equals("1")) {
barChart = ChartFactory.createBarChart3D( "Average Visibility Trend of "+number_of_weeks+" Week","Weeks","%",dataset,PlotOrientation.VERTICAL,true,true,false);
}

else {
barChart = ChartFactory.createBarChart3D( "Average Visibility Trend of "+number_of_weeks+" Weeks","Weeks","%",dataset,PlotOrientation.VERTICAL,true,true,false);	
}


barChart.setBackgroundPaint(Color.white);
barChart.getPlot().setBackgroundPaint(Color.white);
CategoryItemRenderer renderer = ((CategoryPlot)barChart.getPlot()).getRenderer();

renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
renderer.setBaseItemLabelsVisible(true);
ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, 
        TextAnchor.TOP_CENTER);
renderer.setBasePositiveItemLabelPosition(position);
ChartPanel chartPanel = new ChartPanel( barChart );
chartPanel.setPreferredSize( new java.awt.Dimension( 1400 , 600 ) );
Properties config = getProperties();
try {
 String report_directiory = config.getProperty("performance_mail_visibility_image");
 String image_name=report_directiory+"\\visibility_"+admin_id+".png";
    OutputStream out = new FileOutputStream(image_name);
    ChartUtilities.writeChartAsPNG(out,
    		barChart,
    		1400,
    		600);

} catch (IOException ex) {
log.error("Performance   Exception occurs:------" + admin_id + "---" + ex.getMessage(), ex);
}



	
		
}

public static Double weekly_average(String week,MongoCollection  < Document > collection) {
	 
ArrayList < Document > docs_current = collection.find(eq("week_count",week)).into(new ArrayList<>());	 
double output=0.0;
ArrayList<Double> for_average=new ArrayList<Double>();
JSONArray jsonArray = new JSONArray(JSON.serialize(docs_current));

for (int k = 0; k < jsonArray.length(); k++) {
JSONObject jsonObject1 = jsonArray.getJSONObject(k);
//jsonObject1.optString("neState");;
String elements=jsonObject1.optString("visibility_%");

for_average.add(Double.parseDouble(elements));

}

output=Math.round(calculateAverage(for_average));
return output;
}



 
 public static double calculateAverage(ArrayList <Double> input) {
  double sum = 0;
  if(!input.isEmpty()) {
for (Double mark : input) {
sum += mark;
}
return sum / input.size();
  }
  return sum;
}
 
 
 public static void country_updates_week(MongoDatabase  db_sam,String week,String value,String country) {
MongoCollection < Document > collection = db_sam.getCollection("country_weekly");
collection.updateMany(
Filters.and(eq("Country", country)),
Updates.combine(
Updates.set(week, value)
));	 
}
}
