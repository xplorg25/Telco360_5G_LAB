package org.xplorg.tool.telco360.performanceDAO.implementation;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.xplorg.tool.telco360.entity.GenericPerformance;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class microwave_ericsson_excel_for_each_object extends GenericPerformance  {

Logger log = LogManager.getLogger(microwave_ericsson_excel_for_each_object.class.getName());
	
public void generate_report(MongoDatabase database,String report,String admin_id,String start_time,String end_time,ArrayList<String> unique_dates) {	
//////System.out.println("report===>"+report);
String group = mongo_generic_for_kpi_report(database, admin_id, report, "element_name", "kpi_formula_report");	
//////System.out.println("group==="+group);
MongoCollection<Document> collection=database.getCollection("kpi_formula_report");	

DistinctIterable<String> document=collection.distinct("kpi_name",eq("groups",report) ,String.class);
ArrayList<String> kpis=new ArrayList<String>();
for(String kpi:document) {
kpis.add(kpi);
}//////System.out.println(kpis);

ArrayList<String> columns_for_first=new ArrayList<String>();

columns_for_first.add("SITE_ID");
columns_for_first.add("MEASOBJLDN");
columns_for_first.add("DATE");
columns_for_first.add("TIME");

for(String report_date:unique_dates) {

MongoCollection < Document > document1 = database.getCollection(report_date.replace("-", "_")+"_kpis");
FindIterable < Document > docs=null;
if (start_time.contains(":")) {
 docs = document1.find(and(eq("Groups",group),gte("TIME", start_time), lte("TIME", end_time)));

} else {
 docs = document1.find(and(eq("Groups",group)));

}





ArrayList < String > kpi_data = get_mongodb_distinct_values(docs, columns_for_first);
////////System.out.println(kpi_data);

for (String value: kpi_data) {
ArrayList<String> values_for_first=new ArrayList<String>();

String split1[] = value.split("@AND@");
values_for_first.add(split1[0].trim());
values_for_first.add(split1[1].trim());
values_for_first.add(split1[2].trim());
values_for_first.add(split1[3].trim());

insert_mongodb(database, columns_for_first, values_for_first, report); //inserting into "_kpis"


}
MongoCollection<Document> collection2=database.getCollection(report);
for(String kpi:kpis) {
//	//////System.out.println("===>"+kpi);
for (String value: kpi_data) {
	
String split1[] = value.split("@AND@");	
String site_id=split1[0].trim();
String object=split1[1].trim();
String date=split1[2].trim();
String time=split1[3].trim();


DistinctIterable<String> kpis_val=document1.distinct("VALUE",and(eq("SITE_ID",site_id),eq("MEASOBJLDN",object),eq("DATE",date),eq("TIME",time),eq("Groups",group),eq("KPI_NAME",kpi)), String.class);
	
String values="";
for(String values1:kpis_val) {

values=values1;

break;
}


try {

	collection2.updateOne(
Filters.and(eq("SITE_ID",site_id),eq("MEASOBJLDN",object),eq("DATE",date),eq("TIME",time)),
Updates.combine(
Updates.set(kpi, values)

));
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//e.printStackTrace();
}

}

	
}
}
	
}
	

}
