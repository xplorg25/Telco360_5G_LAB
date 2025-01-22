package org.xplorg.tool.telco360.restController;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.xplorg.tool.telco360.config.WebSocketService;
import org.xplorg.tool.telco360.entity.GenericPerformance;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ssh_thread extends GenericPerformance implements Runnable{

Logger log = LogManager.getLogger(ssh_thread.class.getName());

String userId;
WebSocketService webSocketService;
int threadTime=120000;

public ssh_thread(WebSocketService webSocketService,String userId) {
this.webSocketService=webSocketService;
this.userId=userId;
}	


@Override
public void run() {
try {	

if (log.isDebugEnabled()) {
log.debug("*************** checked into run of PerformanceWebSocketThreads ****************");
}



try {
String message_ipbb_zte="SSH:- HELLO SSH WORKING";


ArrayList<String> command_output = new ArrayList<String>();

Properties config = getProperties();

String filepath = config.getProperty("config_file_path");

BufferedReader objReader = null;
try {
	String strCurrentLine;

	
	objReader = new BufferedReader(new FileReader(filepath));

	while ((strCurrentLine = objReader.readLine()) != null) {
		
		Thread.sleep(1000);
		int idx=userIds.indexOf(userId);	
		if(idx>-1 && userSshSubscription.get(idx).contains("SSH")) {	
		webSocketService.sendMessage(userId, "SSH:-  "+strCurrentLine.trim());
		}
		//command_output.add(strCurrentLine + "\n");
	}

} catch (IOException e) {

	e.printStackTrace();

} finally {

	try {
		if (objReader != null)
			objReader.close();
	} catch (IOException ex) {
		ex.printStackTrace();
	}
}







} catch (Exception e) {
e.printStackTrace();
}




}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();	
}
}



//------------------------ericsson mv------------------------------

public String get__mv_ericsson_kpi_alerts(String admin_id) {

////////System.out.println("2");
MongoClient connection = get_mongo_connection();

MongoDatabase database=database(connection,"TRANSMISSION","ERICSSON");	



MongoCollection <Document>collection=database.getCollection("kpi_formula");

DistinctIterable<String> document=collection.distinct("kpi_name",and(eq("admin_id",admin_id),eq("link_to_topology","yes")), String.class);

ArrayList<String>kpi_list=new ArrayList<String>();

for(String docs:document) {
kpi_list.add(docs);
}

////////System.out.println(kpi_list);
MongoCollection <Document>collection1=database.getCollection("sla_alerts");
String alarms="";
for(String kpi:kpi_list) {
////////System.out.println(kpi);
DistinctIterable<String> document1=collection1.distinct("SITE_ID",and(eq("KPI_NAME",kpi)), String.class);



for(String nename:document1) {
////////System.out.println(nename);

alarms=alarms+""+nename+"@@@"+nename+"~"+kpi+"@##@";	


}
}


String message="Domain:-Transmission;Vendor:-Ericsson;Performance:-"+alarms;

return message;
}





//------------------------NEC--------------------------------

public String get_nec_kpi_alerts(String admin_id) {

//////System.out.println("2");
MongoClient connection = get_mongo_connection();

MongoDatabase database=database(connection,"TRANSMISSION","NEC");	



MongoCollection <Document>collection=database.getCollection("kpi_formula");

DistinctIterable<String> document=collection.distinct("kpi_name",and(eq("admin_id",admin_id),eq("link_to_topology","yes")), String.class);

ArrayList<String>kpi_list=new ArrayList<String>();

for(String docs:document) {
kpi_list.add(docs);
}

////////System.out.println(kpi_list);
MongoCollection <Document>collection1=database.getCollection("sla_alerts");
String alarms="";
for(String kpi:kpi_list) {
////////System.out.println(kpi);
DistinctIterable<String> document1=collection1.distinct("NEName",and(eq("KPIName",kpi)), String.class);



for(String nename:document1) {
////////System.out.println(nename);

alarms=alarms+""+nename+"@@@"+nename+"~"+kpi+"@##@";	


}
}


String message="Domain:-Transmission;Vendor:-Nec;Performance:-"+alarms;

return message;
}



//------------------------Huawei--------------------------------

public String get_nce_kpi_alerts(String admin_id) {


MongoClient connection = get_mongo_connection();

MongoDatabase database=database(connection,"TRANSMISSION","NCE");	



MongoCollection <Document>collection=database.getCollection("kpi_formula");

DistinctIterable<String> document=collection.distinct("kpi_name",and(eq("admin_id",admin_id),eq("link_to_topology","yes")), String.class);

ArrayList<String>kpi_list=new ArrayList<String>();

for(String docs:document) {
kpi_list.add(docs);
}
MongoCollection <Document>collection1=database.getCollection("sla_alerts");
String alarms="";
for(String kpi:kpi_list) {

DistinctIterable<String> document1=collection1.distinct("NEName",and(eq("KPIName",kpi)), String.class);



for(String nename:document1) {
alarms=alarms+""+nename+"@@@"+nename+"~"+kpi+"@##@";	


}
}
String message="Domain:-Transmission;Vendor:-Huawei;Performance:-"+alarms;

return message;
}



//-------------------(Sam)------------------------------------------

public String get_sam_kpi_alerts(String admin_id) {
//////System.out.println("3");

MongoClient connection = get_mongo_connection();

MongoDatabase database=database(connection,"TRANSMISSION","SAM");	



MongoCollection <Document>collection=database.getCollection("kpi_formula");

DistinctIterable<String> document=collection.distinct("kpi_name",and(eq("admin_id",admin_id),eq("link_to_topology","yes")), String.class);

ArrayList<String>kpi_list=new ArrayList<String>();

for(String docs:document) {
kpi_list.add(docs);
}
MongoCollection <Document>collection1=database.getCollection("sla_alerts");
String alarms="";
for(String kpi:kpi_list) {

DistinctIterable<String> document1=collection1.distinct("monitoredObjectSiteName",and(eq("KPIName",kpi)), String.class);



for(String nename:document1) {
alarms=alarms+""+nename+"@@@"+nename+"~"+kpi+"@##@";	

//////////System.out.println(alarms);
}
}


String message="Domain:-Transmission;Vendor:-Nokia;Performance:-"+alarms;
return message;
}

//---------for ipran---------



public String get_ipran_alerts(String admin_id) {




MongoClient connection = get_mongo_connection();

MongoDatabase database=database(connection,"IPRAN","HUAWEI");	



MongoCollection <Document>collection=database.getCollection("kpi_formula");

DistinctIterable<String> document=collection.distinct("kpi_name",and(eq("admin_id",admin_id),eq("link_to_topology","yes")), String.class);

ArrayList<String>kpi_list=new ArrayList<String>();

for(String docs:document) {
kpi_list.add(docs);
}

String alarms="";
for(String kpi:kpi_list) {

ArrayList<String>to_find=new ArrayList<String>();	
to_find.add("Site_ID");
to_find.add("IPAddress");
MongoCollection < Document > document_ip = database.getCollection("sla_alerts");
FindIterable < Document > documents_ip = document_ip.find(eq("KPIName",kpi));
ArrayList < String > pinggedIpAddress_data = get_mongodb_distinct_values(documents_ip, to_find);

for (String unique_value: pinggedIpAddress_data) {
String split1[] = unique_value.split("@AND@");
alarms=alarms+""+split1[0].trim()+"@@@"+split1[1].trim()+"~"+kpi+"@##@";

}


}


String message="Domain:-Ipran;Vendor:-Huawei;Performance:-"+alarms;
return message;


}



//---------for ipbb---------



public String get_ipbb_alerts(String admin_id,String db_domain,String domain,String vendor) {



MongoClient connection = get_mongo_connection();

MongoDatabase database=database(connection,db_domain,vendor.toUpperCase());	



MongoCollection <Document>collection=database.getCollection("kpi_formula");

DistinctIterable<String> document=collection.distinct("kpi_name",and(eq("admin_id",admin_id),eq("link_to_topology","yes")), String.class);

ArrayList<String>kpi_list=new ArrayList<String>();

BasicDBObject index = new BasicDBObject("$hint", "Site_ID_1");	

for(String docs:document) {
kpi_list.add(docs);
}

String alarms="";
for(String kpi:kpi_list) {

ArrayList<String>to_find=new ArrayList<String>();	
to_find.add("Site_ID");
to_find.add("IPAddress");
MongoCollection < Document > document_ip = database.getCollection("sla_alerts");
FindIterable < Document > documents_ip = document_ip.find(eq("KPIName",kpi)).hint(index);
ArrayList < String > pinggedIpAddress_data = get_mongodb_distinct_values(documents_ip, to_find);

for (String unique_value: pinggedIpAddress_data) {
String split1[] = unique_value.split("@AND@");
alarms=alarms+""+split1[0].trim()+"@@@"+split1[1].trim()+"~"+kpi+"@##@";

}


}


String message="Domain:-"+domain+";Vendor:-"+vendor+";Performance:-"+alarms;
////////System.out.println(message);
return message;


}

}
