package org.xplorg.tool.telco360.restController;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
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

public class PerformanceWebSocketThreads extends GenericPerformance implements Runnable {

	Logger log = LogManager.getLogger(PerformanceWebSocketThreads.class.getName());

	String userId;
	WebSocketService webSocketService;
	int threadTime = 120000;

	public PerformanceWebSocketThreads(WebSocketService webSocketService, String userId) {
		this.webSocketService = webSocketService;
		this.userId = userId;
	}

	@Override
	public void run() {
		try {

			if (log.isDebugEnabled()) {
				log.debug("*************** checked into run of PerformanceWebSocketThreads ****************");
			}
			while (true) {
				int idx = userIds.indexOf(userId);
				if (idx > -1 && userSubscription.get(idx).contains("PM")) {
					
					//Step4
					//BY UTKARSH
					try {
						String message_ipbb_zte = get_ipbb_alerts(userId, "IPBB", "Mpbn", "Nokia");
						String message_ipbb_nokia = get_ipbb_alerts(userId, "IPBB", "Mpbn", "Zte");
						String message_ipbb_ericsson = get_ipbb_alerts(userId, "IPBB", "Mpbn", "Ericsson");
//						String message_ipbb_extreme = get_ipbb_alerts(userId, "IPBB", "Mpbn", "Extreme");
//						String message_ipbb_dell = get_ipbb_alerts(userId, "IPBB", "Mpbn", "Dell");
						convertToSingleMessage(message_ipbb_zte, message_ipbb_nokia, message_ipbb_ericsson);
						System.out.println("------HERE 1 ");

					} catch (Exception e) {
						e.printStackTrace();
					}

					Thread.sleep(1000);

					try {
//	String message_ipbb_juniper=get_ipbb_alerts(userId,"IPBB","Mpbn","Juniper");
//webSocketService.sendMessage(userId, message_ipbb_juniper);
					} catch (Exception ex) {
						log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
					}

					Thread.sleep(1000);
					try {
						String message_ipran = get_ipran_alerts(userId);
						webSocketService.sendMessage(userId, message_ipran);  
					} catch (Exception ex) {
						log.error("Exception occurs:----" + ex.getMessage(), ex);
//e.printStackTrace();
					}

					Thread.sleep(1000);

					String message_mv_ericsson = get__mv_ericsson_kpi_alerts(userId);

//webSocketService.sendMessage(userId, message_mv_ericsson);

					Thread.sleep(1000);
					String message_nce = get_nce_kpi_alerts(userId);

//webSocketService.sendMessage(userId, message_nce);

					String message_sam = get_sam_kpi_alerts(userId);
//webSocketService.sendMessage(userId, message_sam);

					Thread.sleep(1000);

					String message_nec = get_nec_kpi_alerts(userId);
					String message = message_mv_ericsson + "#@@#" + message_nce + "#@@#" + message_sam + "#@@#"
							+ message_nec;

					webSocketService.sendMessage(userId, message);
					Thread.sleep(threadTime);
				} else {
					break;
				}
			}
		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();	
		}
	}

//------------------------ericsson mv------------------------------

	public String get__mv_ericsson_kpi_alerts(String admin_id) {

////////System.out.println("2");
		MongoClient connection = get_mongo_connection();

		MongoDatabase database = database(connection, "TRANSMISSION", "ERICSSON");

		MongoCollection<Document> collection = database.getCollection("kpi_formula");

		DistinctIterable<String> document = collection.distinct("kpi_name",
				and(eq("admin_id", admin_id), eq("link_to_topology", "yes")), String.class);

		ArrayList<String> kpi_list = new ArrayList<String>();

		for (String docs : document) {
			kpi_list.add(docs);
		}

////////System.out.println(kpi_list);
		MongoCollection<Document> collection1 = database.getCollection("sla_alerts");
		String alarms = "";
		for (String kpi : kpi_list) {
////////System.out.println(kpi);
			DistinctIterable<String> document1 = collection1.distinct("SITE_ID", and(eq("KPI_NAME", kpi)),
					String.class);

			for (String nename : document1) {
////////System.out.println(nename);

				alarms = alarms + "" + nename + "@@@" + nename + "~" + kpi + "@##@";

			}
		}
		connection.close();

		String message = "Domain:-Transmission;Vendor:-Ericsson;Performance:-" + alarms;

		return message;
	}

//------------------------NEC--------------------------------

	public String get_nec_kpi_alerts(String admin_id) {

//////System.out.println("2");
		MongoClient connection = get_mongo_connection();

		MongoDatabase database = database(connection, "TRANSMISSION", "NEC");

		MongoCollection<Document> collection = database.getCollection("kpi_formula");

		DistinctIterable<String> document = collection.distinct("kpi_name",
				and(eq("admin_id", admin_id), eq("link_to_topology", "yes")), String.class);

		ArrayList<String> kpi_list = new ArrayList<String>();

		for (String docs : document) {
			kpi_list.add(docs);
		}

////////System.out.println(kpi_list);
		MongoCollection<Document> collection1 = database.getCollection("sla_alerts");
		String alarms = "";
		for (String kpi : kpi_list) {
////////System.out.println(kpi);
			DistinctIterable<String> document1 = collection1.distinct("NEName", and(eq("KPIName", kpi)), String.class);

			for (String nename : document1) {
////////System.out.println(nename);

				alarms = alarms + "" + nename + "@@@" + nename + "~" + kpi + "@##@";

			}
		}

		String message = "Domain:-Transmission;Vendor:-Nec;Performance:-" + alarms;

		return message;
	}

//------------------------Huawei--------------------------------

	public String get_nce_kpi_alerts(String admin_id) {

		MongoClient connection = get_mongo_connection();

		MongoDatabase database = database(connection, "TRANSMISSION", "NCE");

		MongoCollection<Document> collection = database.getCollection("kpi_formula");

		DistinctIterable<String> document = collection.distinct("kpi_name",
				and(eq("admin_id", admin_id), eq("link_to_topology", "yes")), String.class);

		ArrayList<String> kpi_list = new ArrayList<String>();

		for (String docs : document) {
			kpi_list.add(docs);
		}
		MongoCollection<Document> collection1 = database.getCollection("sla_alerts");
		String alarms = "";
		for (String kpi : kpi_list) {

			DistinctIterable<String> document1 = collection1.distinct("NEName", and(eq("KPIName", kpi)), String.class);

			for (String nename : document1) {
				alarms = alarms + "" + nename + "@@@" + nename + "~" + kpi + "@##@";

			}
		}

		connection.close();
		String message = "Domain:-Transmission;Vendor:-Huawei;Performance:-" + alarms;

		return message;
	}

//-------------------(Sam)------------------------------------------

	public String get_sam_kpi_alerts(String admin_id) {
//////System.out.println("3");

		MongoClient connection = get_mongo_connection();

		MongoDatabase database = database(connection, "TRANSMISSION", "SAM");

		MongoCollection<Document> collection = database.getCollection("kpi_formula");

		DistinctIterable<String> document = collection.distinct("kpi_name",
				and(eq("admin_id", admin_id), eq("link_to_topology", "yes")), String.class);

		ArrayList<String> kpi_list = new ArrayList<String>();

		for (String docs : document) {
			kpi_list.add(docs);
		}
		MongoCollection<Document> collection1 = database.getCollection("sla_alerts");
		String alarms = "";
		for (String kpi : kpi_list) {

			DistinctIterable<String> document1 = collection1.distinct("monitoredObjectSiteName",
					and(eq("KPIName", kpi)), String.class);

			for (String nename : document1) {
				alarms = alarms + "" + nename + "@@@" + nename + "~" + kpi + "@##@";

//////////System.out.println(alarms);
			}
		}

		connection.close();
		String message = "Domain:-Transmission;Vendor:-Nokia;Performance:-" + alarms;
		return message;
	}

//---------for ipran---------

	public String get_ipran_alerts(String admin_id) {

		MongoClient connection = get_mongo_connection();

		MongoDatabase database = database(connection, "IPRAN", "HUAWEI");

		MongoCollection<Document> collection = database.getCollection("kpi_formula");

		DistinctIterable<String> document = collection.distinct("kpi_name",
				and(eq("admin_id", admin_id), eq("link_to_topology", "yes")), String.class);

		ArrayList<String> kpi_list = new ArrayList<String>();

		for (String docs : document) {
			kpi_list.add(docs);
		}

		String alarms = "";
		for (String kpi : kpi_list) {

			ArrayList<String> to_find = new ArrayList<String>();
			to_find.add("Site_ID");
			to_find.add("IPAddress");
			MongoCollection<Document> document_ip = database.getCollection("sla_alerts");
			FindIterable<Document> documents_ip = document_ip.find(eq("KPIName", kpi));
			ArrayList<String> pinggedIpAddress_data = get_mongodb_distinct_values(documents_ip, to_find);

			for (String unique_value : pinggedIpAddress_data) {
				String split1[] = unique_value.split("@AND@");
				alarms = alarms + "" + split1[0].trim() + "@@@" + split1[1].trim() + "~" + kpi + "@##@";

			}

		}

		connection.close();
		String message = "Domain:-Ipran;Vendor:-Huawei;Performance:-" + alarms;
		return message;

	}

//---------for ipbb---------

	public String get_ipbb_alerts(String admin_id, String db_domain, String domain, String vendor) {

		MongoClient connection = get_mongo_connection();

		MongoDatabase database = database(connection, db_domain, vendor.toUpperCase());

		MongoCollection<Document> collection = database.getCollection("kpi_formula");

		DistinctIterable<String> document = collection.distinct("kpi_name",
				and(eq("admin_id", admin_id), eq("link_to_topology", "yes")), String.class);

		ArrayList<String> kpi_list = new ArrayList<String>();

		BasicDBObject index = new BasicDBObject("$hint", "Site_ID_1");

		for (String docs : document) {
			kpi_list.add(docs);
		}

		String alarms = "";
		for (String kpi : kpi_list) {

			ArrayList<String> to_find = new ArrayList<String>();
			to_find.add("Site_ID");
			to_find.add("IPAddress");
			MongoCollection<Document> document_ip = database.getCollection("sla_alerts");
			FindIterable<Document> documents_ip = document_ip.find(eq("KPIName", kpi)).hint(index);
			ArrayList<String> pinggedIpAddress_data = get_mongodb_distinct_values(documents_ip, to_find);

			for (String unique_value : pinggedIpAddress_data) {
				String split1[] = unique_value.split("@AND@");
				alarms = alarms + "" + split1[0].trim() + "@@@" + split1[1].trim() + "~" + kpi + "@##@";

			}

		}

		String message = "Domain:-" + domain + ";Vendor:-" + vendor + ";Performance:-" + alarms;
System.out.println("--------->>>>>ALRMS_IPBB"
		+ "  "+message);

		connection.close();
		return message;

	}

	/* FUNCTION_FOR_MESSAGE */
	public String convertToSingleMessage(String result1, String result2, String result3) {
		String singleMessage = result1 + "#@@#" + result2 + "#@@#" + result3;
		webSocketService.sendMessage(userId, singleMessage);
	System.out.println("-------SINGLE_MESSAGE------->"+singleMessage);
		return singleMessage;
	}

}
