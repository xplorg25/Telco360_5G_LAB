package org.xplorg.tool.telco360.performanceDAO.implementation;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.xplorg.tool.telco360.DAO.interfaces.Performance_IPRAN_IPBB_DAO;
import org.xplorg.tool.telco360.entity.GenericPerformance;
import org.xplorg.tool.telco360.entity.tree_children_g_s;
import org.xplorg.tool.telco360.entity.tree_parents_g_s;
import org.xplorg.tool.telco360.performance.dual_axis.date_format_graph;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_1_date_format;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_1a;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_2;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_3_time_update;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_4;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_5;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_6;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_7_color;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_8;
import org.xplorg.tool.telco360.performance.dual_axis.exporting;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

@Repository("Performance_IPRAN_IPBB_DAO")

public class Performance_IPRAN_IPBB_Implementation extends GenericPerformance implements Performance_IPRAN_IPBB_DAO {
	Logger log = LogManager.getLogger(Performance_IPRAN_IPBB_Implementation.class.getName());

//TODO  to get (local system) element_name corresponding to IPRAN and IPBB
	@Override
	public ArrayList<String> get_br_local_information(String opco, String admin_id, String domain, String vendor,
			String type, String report_name) {
//		//System.out.println(opco);
//		//System.out.println(admin_id);
//		//System.out.println(domain);
//		//System.out.println(vendor);
//		//System.out.println(report_name);
		if (log.isDebugEnabled()) {
			log.debug("******" + admin_id + "******    zambia*************" + domain + "*     " + vendor
					+ "* enter   get_br_local_information ****************");
		}

		ArrayList<String> to_find = new ArrayList<String>();
		to_find.add("devicename");
		to_find.add("ip");
		ArrayList<String> output = new ArrayList<String>();

		MongoClient connection = get_mongo_connection();
		MongoDatabase database = database(connection, domain, vendor);

		if (type.equals("from_report")) {

			try {
				String elements = (mongo_select1_where2(database, "ElementName", "report_group", "admin_id", admin_id,
						"ReportName", report_name).get(0));

				ArrayList<String> element_name = new ArrayList<String>();

				MongoCollection<Document> document = database.getCollection("element_command_structure");
				FindIterable<Document> docs = document.find();
				ArrayList<String> kpi_data = get_mongodb_distinct_values(docs, to_find);

				if (!elements.equals("ALL")) {

					String split[] = elements.split(",");
					for (String elementss : split) {
						element_name.add(elementss);

						for (String value : kpi_data) {

							String split1[] = value.split("@AND@");

							String devicename = split1[0].trim();

							if (element_name.contains(devicename)) {
								String item = split1[1].trim();

								output.add(devicename + ":" + item);

							}

						}

					}
				} else {

					for (String value : kpi_data) {

						String split1[] = value.split("@AND@");

						String devicename = split1[0].trim();

						String item = split1[1].trim();

						output.add(devicename + ":" + item);
					}

				}

			} catch (Exception e) {
				log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "-----"
						+ e.getMessage(), e);

			}

		} else {
			try {

//database=connection.getDatabase(config.getProperty("database.performance_zambia_mpbn"));

				MongoCollection<Document> document = database.getCollection("element_command_structure");
				FindIterable<Document> docs = document.find();
				ArrayList<String> kpi_data = get_mongodb_distinct_values(docs, to_find);
////System.out.println(kpi_data.size());
				for (String value : kpi_data) {

					String split1[] = value.split("@AND@");

					String devicename = split1[0].trim();

					String item = split1[1].trim();

					output.add(devicename + ":" + item);
				}

			} catch (Exception e) {
				log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
						+ e.getMessage(), e);

			}
		}

		log.debug("******" + admin_id + "******    zambia*************" + domain + "*     " + vendor
				+ "* exit   get_br_local_information ****************");
		close_mongo_connection(connection);

		return output;

	}

//TODO  for interface name
	@Override
	public ArrayList<String> get_br_interface_name(String opco, String admin_id, String domain, String vendor,
			String device_name, String ip) {

		// System.out.println("******" + admin_id + "****** " + opco + "*************" +
		// domain + "* " + vendor
//				+ "* enter   get_br_interface_name ****************");

		if (log.isDebugEnabled()) {
			log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "*     " + vendor
					+ "* enter   get_br_interface_name ****************");
		}
		ArrayList<String> output = new ArrayList<String>();
		Properties config = getProperties();
		MongoClient connection = get_mongo_connection();
		MongoDatabase database = database(connection, domain, vendor);

		if (domain.toUpperCase().equals("IPRAN")) {
			log.debug("*********" + admin_id + "****** checked into get_local_information  IPRAN ****************");

			try {
//if user select sla nailing
				if (opco.equals("for_sla")) {
					ArrayList<String> initial_interface = new ArrayList<String>();

					MongoCollection<Document> collection = database.getCollection("element_command_structure");
					DistinctIterable<String> values = collection.distinct("interface",
							and(eq("devicename", string_replace(device_name)), eq("command", string_replace(ip))),
							String.class);

					for (String value : values) {

						initial_interface.add(value);
					}
					String groups = ip.toUpperCase();
					MongoCollection<Document> collection1 = database.getCollection("sla_alerts");
					DistinctIterable<String> values1 = collection1.distinct("Interface",
							and(eq("Site_ID", string_replace(device_name)), eq("Groups", groups)), String.class);

					for (String value1 : values1) {

						if (initial_interface.contains(value1)) {
							output.add(value1);
						}
					}

				} else {

					MongoCollection<Document> collection = database.getCollection("element_command_structure");
					DistinctIterable<String> values = collection.distinct("interface",
							and(eq("devicename", string_replace(device_name)), eq("command", string_replace(ip))),
							String.class);

					for (String value : values) {

						output.add(value);
					}

				}

			} catch (Exception e) {
				log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
						+ e.getMessage(), e);

			}
		} else if (domain.toUpperCase().equals("IPBB")) {
			log.debug("*********" + admin_id + "****** checked into get_local_information  IPBB ****************");
			try {
				if (opco.equals("for_sla")) {

					ArrayList<String> initial_interface = new ArrayList<String>();

					MongoCollection<Document> collection = null;
					if (string_replace(ip).equals("vlan")) {
						collection = database.getCollection("vlan_element_command_structure");
					}

					else {
						collection = database.getCollection("element_command_structure");
					}
					DistinctIterable<String> values = collection.distinct("interface",
							and(eq("devicename", string_replace(device_name)), eq("command", string_replace(ip))),
							String.class);

					for (String value : values) {

						initial_interface.add(value);
					}

					String groups = "";
//adding keys for vendor 1
					if (vendor.equals("NOKIA")) {
						if (ip.equals("snmp")) {
							groups = ip.toUpperCase();
						} else if (string_replace(ip).equals("show system cpu")) {
							groups = "USAGE";
						} else if (string_replace(ip).equals("show system memory")) {
							groups = "memory";
						} else if (string_replace(ip).equals("show port statistics")) {
							groups = "STATS";
						} else if (string_replace(ip).equals("vlan")) {
							groups = "VLAN";
						}

					} else if (vendor.equals("ERICSSON")) {

						if (ip.equals("snmp")) {
							groups = ip.toUpperCase();
						} else if (string_replace(ip).equals("port counters detail")) {
							groups = "Port_Counter";
						} else if (string_replace(ip).equals("dot1q counters detail")) {
							groups = "Dot1q_counter";
						} else {
							groups = string_replace(ip);
						}

					} else if (vendor.equals("ZTE")) {

						if (ip.equals("snmp")) {
							groups = ip.toUpperCase();
						} else if (ip.equals("processor")) {
							groups = "processor";
						}
					} else if (vendor.equals("JUNIPER")) {

						if (ip.equals("snmp")) {
							groups = ip.toUpperCase();
						} else if (string_replace(ip).equals("interfaces statistics detail")) {
							groups = "Stats";
						}

					}
					if (vendor.equals("DPTECH_FIREWALL")) {
						if (ip.equals("snmp")) {
							groups = ip.toUpperCase();
						} else if (string_replace(ip).equals("session")) {
							groups = "session";
						} else if (string_replace(ip).equals("performance")) {
							groups = "performance";

						}
					}
					MongoCollection<Document> collection1 = database.getCollection("sla_alerts");

					DistinctIterable<String> values1 = collection1.distinct("Interface",
							and(eq("Site_ID", string_replace(device_name)), eq("Groups", groups)), String.class);

					for (String value1 : values1) {

						if (initial_interface.contains(value1)) {
							output.add(value1);
						}
					}

				} else {

					MongoCollection<Document> collection = null;
					if (string_replace(ip).equals("vlan")) {
						collection = database.getCollection("vlan_element_command_structure");
					}

					else {
						collection = database.getCollection("element_command_structure");
					}

					// System.out.println(device_name + "=====" + string_replace(device_name));
					// System.out.println(ip + "=====" + string_replace(ip));

					DistinctIterable<String> values = collection.distinct("interface",
							and(eq("devicename", string_replace(device_name)), eq("command", string_replace(ip))),
							String.class);

					for (String value : values) {

						output.add(value);
					}
				}
			} catch (Exception e) {
				log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
						+ e.getMessage(), e);

			}
		} else if (domain.equals("RADIO")) {
			log.debug("*********" + admin_id + "****** checked into get_local_information  RADIO ****************");

			try {
				database = connection.getDatabase(config.getProperty("database.performance_zambia_ericsson"));
				MongoCollection<Document> collection = database.getCollection("objects");
				DistinctIterable<String> values = collection.distinct("object", and(eq("element_name", ip)),
						String.class);

				for (String value : values) {

					output.add(value);
				}

			} catch (Exception e) {
				log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
						+ e.getMessage(), e);

			}
		}
		log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "*     " + vendor
				+ "* exit   get_br_interface_name ****************");
		close_mongo_connection(connection);
		return output;

	}
//TODO-----to get ipname of perticular local system--ipbb--

	@Override
	public ArrayList<String> get_ipbb_iplist(String opco, String admin_id, String domain, String vendor,
			String element_name, String sla) {
		log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "*     " + vendor
				+ "* enter   get_ipbb_iplist ****************");
		if (log.isDebugEnabled()) {
			log.debug("*********" + admin_id + "****** checked into get_ipbb_iplist ****************");
		}
		ArrayList<String> output = new ArrayList<String>();

		MongoClient connection = get_mongo_connection();
		MongoDatabase database = database(connection, domain, vendor);
		;

		try {

			if (sla.equals("yes")) {
				MongoCollection<Document> collection = database.getCollection("sla_alerts");
				DistinctIterable<String> values = collection.distinct("IPAddress", and(eq("Site_ID", element_name)),
						String.class);

				for (String value : values) {
					output.add(value);
				}
			} else {
				MongoCollection<Document> collection = database.getCollection("topologydiscoveryfinal");
				DistinctIterable<String> values = collection.distinct("IPADDRESS", and(eq("LOCSYSNAME", element_name)),
						String.class);
				for (String value : values) {
					output.add(value);
				}
			}

		} catch (Exception e) {
			log.error(
					"Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(),
					e);

		}
		log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "*     " + vendor
				+ "* exit   get_ipbb_iplist ****************");
		close_mongo_connection(connection);
		return output;

	}

// TODO----get kpi tree for apran and ipbn---
	@Override
	public List<tree_parents_g_s> get_br_kpi_name(String opco, String admin_id, String domain, String vendor,
			String type, String device_name, String element_key) {

		log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "*     " + vendor
				+ "* enter   get_br_kpi_name ****************");

		if (log.isDebugEnabled()) {
			log.debug("*******" + admin_id + "******** checked into get_br_kpi_name ****************");
		}

		MongoClient connection = get_mongo_connection();

		MongoDatabase database = database(connection, domain, vendor);

		List<String> list = new ArrayList<>();
		List<tree_parents_g_s> output = new ArrayList<>();

// for sla nailing
		if (type.equals("for_sla")) {

			if (domain.equals("TRANSMISSION") && vendor.equals("SAM")) {

				ArrayList<String> kpi_with_topology = (mongo_select1_where2(database, "kpi_name", "kpi_formula",
						"admin_id", admin_id, "link_to_topology", "yes"));

				ArrayList<String> groups_with_topology = (mongo_select1_where2(database, "groups", "kpi_formula",
						"admin_id", admin_id, "link_to_topology", "yes"));

				MongoCollection<Document> collection = database.getCollection("sla_alerts");
				DistinctIterable<String> kpi_groups = null;

				try {
					kpi_groups = collection.distinct("Groups", and(eq("OPCO", opco)), String.class);
				} catch (Exception e) {

					log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
							+ e.getMessage(), e);
				}

				for (String value : kpi_groups) {
					if (groups_with_topology.contains(value)) {
						list.add(value);
					}

				}

				for (int i = 0; i < list.size(); i++) {

					String gp = list.get(i);

					DistinctIterable<String> kpi_names = collection.distinct("KPIName",
							and(eq("OPCO", opco), eq("Groups", gp)), String.class);
					List<tree_children_g_s> coutput = new ArrayList<>();
					for (String kpi_name : kpi_names) {
						if (kpi_with_topology.contains(kpi_name)) {
							tree_children_g_s c1 = new tree_children_g_s(kpi_name, "file");
							coutput.add(c1);
						}
					}
					tree_parents_g_s g1 = new tree_parents_g_s(gp, "folder", coutput);
					output.add(g1);

				}
			} else if (domain.equals("TRANSMISSION") && vendor.equals("ERICSSON")) {

				ArrayList<String> kpi_with_topology = (mongo_select1_where2(database, "kpi_name", "kpi_formula",
						"admin_id", admin_id, "link_to_topology", "yes"));

				ArrayList<String> groups_with_topology = (mongo_select1_where2(database, "groups", "kpi_formula",
						"admin_id", admin_id, "link_to_topology", "yes"));

				MongoCollection<Document> collection = database.getCollection("sla_alerts");
				DistinctIterable<String> kpi_groups = null;

				try {
//kpi_groups = collection.distinct("Groups", and(eq("OPCO", opco)), String.class);
				} catch (Exception e) {

					log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
							+ e.getMessage(), e);
				}

//for (String value: kpi_groups) {
				if (groups_with_topology.contains(device_name)) {
					list.add(device_name);
				}

//}

				for (int i = 0; i < list.size(); i++) {

					String gp = list.get(i);

					DistinctIterable<String> kpi_names = collection.distinct("KPI_NAME",
							and(eq("OPCO", opco), eq("SITE_ID", element_key), eq("Groups", gp)), String.class);
					List<tree_children_g_s> coutput = new ArrayList<>();
					for (String kpi_name : kpi_names) {
						if (kpi_with_topology.contains(kpi_name)) {
							tree_children_g_s c1 = new tree_children_g_s(kpi_name, "file");
							coutput.add(c1);
						}
					}
					tree_parents_g_s g1 = new tree_parents_g_s(gp, "folder", coutput);
					output.add(g1);

				}
			} else if (domain.equals("IPBB") || domain.equals("IPRAN")) {

				ArrayList<String> sla_groups = new ArrayList<String>();

				ArrayList<String> sla_kpis = new ArrayList<String>();

				MongoCollection<Document> collection = database.getCollection("sla_alerts");
				DistinctIterable<String> sla_kpi_groups = collection.distinct("Groups", String.class);
				for (String gps : sla_kpi_groups) {
					sla_groups.add(gps);
				}
				DistinctIterable<String> sla_kpi = collection.distinct("KPIName",
						eq("IPAddress", string_replace(element_key)), String.class);
				for (String kpis : sla_kpi) {
					sla_kpis.add(kpis);
				}

				String check = "";

				if (vendor.equals("NOKIA")) {
					if (device_name.equals("SNMP")) {
						check = "ipbb";
					} else if (device_name.equals("show port statistics")) {
						check = "stats";
					} else if (device_name.equals("show system cpu")) {
						check = "usage";

					} else if (device_name.equals("show system memory")) {
						check = "memory";

					} else if (device_name.equals("vlan")) {
						check = "vlan";
					}
				} else if (vendor.equals("ERICSSON")) {

					if (device_name.equals("SNMP")) {
						check = "ipbb";
					} else if (device_name.equals("port counters detail")) {
						check = "port_counter";
					} else if (device_name.equals("dot1q counters detail")) {
						check = "dot1q_counter";
					}

				} else if (vendor.equals("JUNIPER")) {

					if (device_name.equals("SNMP")) {
						check = "ipbb";
					} else if (device_name.equals("interfaces statistics detail")) {
						check = "stats";
					}

				} else if (vendor.equals("HUAWEI")) {

					if (device_name.equals("SNMP")) {
						check = "ipran";
					}
				}

				ArrayList<String> to_find = new ArrayList<String>();
				to_find.add("groups");
				to_find.add("kpi_formula");

				MongoCollection<Document> document = database.getCollection("kpi_formula");
				FindIterable<Document> docs = document.find(eq("admin_id", admin_id));
				ArrayList<String> kpi_data = get_mongodb_distinct_values(docs, to_find);

				for (String value : kpi_data) {

					if (value.contains(check)) {
						String split1[] = value.split("@AND@");
						String groups = split1[0].trim();

//if(sla_groups.contains(groups)) {
						list.add(groups);
//}

					}

				}

				Set<String> set = new LinkedHashSet<>();
				set.addAll(list);
				list.clear();
				list.addAll(set);
				MongoCollection<Document> collection1 = database.getCollection("kpi_formula");

				for (int i = 0; i < list.size(); i++) {
					int check1 = 0; // to stop the entry of usless group
					String gp = list.get(i);

					DistinctIterable<String> kpi_names = collection1.distinct("kpi_name", and(eq("opco", opco),
							eq("admin_id", admin_id), eq("groups", gp), eq("link_to_topology", "yes")), String.class);
					List<tree_children_g_s> coutput = new ArrayList<>();
					for (String kpi_name : kpi_names) {
						if (sla_kpis.contains(kpi_name)) {
							tree_children_g_s c1 = new tree_children_g_s(kpi_name, "file");
							coutput.add(c1);
							check1 = 1;
						}

					}

					if (check1 == 1) {
						tree_parents_g_s g1 = new tree_parents_g_s(gp, "folder", coutput);
						output.add(g1);
					}

				}

			} else {

				String start_date = current_date(opco);
				MongoCollection<Document> collection = database.getCollection("for_sla");
//get kpis which are link with topology

				ArrayList<String> kpi_with_topology = (mongo_select1_where2(database, "kpi_name", "kpi_formula",
						"admin_id", admin_id, "link_to_topology", "yes"));
				String latest_time = get_single_column_value(database, "present_time_calc", "value");
				String current_time = sub_mins(latest_time, -10); // getting previous time for getting single value for
																	// all interfaces

				String gp = "KPI NAME";
				DistinctIterable<String> kpi_names = collection.distinct("KPIName",
						and(eq("OPCO", opco), eq("Site_ID", string_replace(device_name))), String.class);
				List<tree_children_g_s> coutput = new ArrayList<>();
				for (String kpi_name : kpi_names) {

					if (kpi_with_topology.contains(kpi_name)) {
						tree_children_g_s c1 = new tree_children_g_s(kpi_name, "file");
						coutput.add(c1);
					}
				}
				tree_parents_g_s g1 = new tree_parents_g_s(gp, "folder", coutput);
				output.add(g1);
			}

		} else if (type.equals("for_report")) {
//element_key=reportname

			device_name = (mongo_select1_where2(database, "command", "report_group", "admin_id", admin_id, "ReportName",
					element_key).get(0));
			try {

				MongoCollection<Document> collection = database.getCollection("kpi_formula");
				DistinctIterable<String> kpi_groups = null;

				if (domain.equals("IPBB")) {
					String check = "";

					if (vendor.equals("NOKIA")) {
						if (device_name.equals("SNMP") || device_name.equals("snmp")) {
							check = "ipbb";
						} else if (device_name.equals("show port statistics")) {
							check = "stats";
						} else if (device_name.equals("show system cpu")) {
							check = "usage";
						} else if (device_name.equals("show system memory")) {
							check = "memory";
						} else if (device_name.equals("vlan")) {
							check = "vlan";
						}
					} else if (vendor.equals("ERICSSON")) {

						if (device_name.equals("SNMP") || device_name.equals("snmp")) {
							check = "ipbb";
						} else if (device_name.equals("port counters detail")) {
							check = "port_counter";
						} else if (device_name.equals("dot1q counters detail")) {
							check = "dot1q_counter";
						}

					} else if (vendor.equals("JUNIPER")) {

						if (device_name.equals("SNMP") || device_name.equals("snmp")) {
							check = "ipbb";
						} else if (device_name.equals("interfaces statistics detail")) {
							check = "stats";
						}

					}

					ArrayList<String> to_find = new ArrayList<String>();
					to_find.add("groups");
					to_find.add("kpi_formula");

					MongoCollection<Document> document = database.getCollection("kpi_formula");
					FindIterable<Document> docs = document
							.find(and(eq("admin_id", admin_id), eq("groups", element_key)));
					ArrayList<String> kpi_data = get_mongodb_distinct_values(docs, to_find);

					for (String value : kpi_data) {

						if (value.contains(check)) {
							String split1[] = value.split("@AND@");
							String kpi = split1[0].trim();

							list.add(kpi);
						}

					}

					Set<String> set = new LinkedHashSet<>();
					set.addAll(list);
					list.clear();
					list.addAll(set);

				} else if (vendor.equals("SAM") && domain.equals("TRANSMISSION")) {

					try {
						kpi_groups = collection.distinct("groups",
								and(eq("opco", opco), eq("admin_id", admin_id), eq("groups", device_name)),
								String.class);
					} catch (Exception e) {

						log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
								+ e.getMessage(), e);
					}

					for (String value : kpi_groups) {

						list.add(value);
					}
				} else {

					if (vendor.equals("ERICSSON") && domain.equals("RADIO")) {

						kpi_groups = collection.distinct("groups", and(eq("opco", opco), eq("admin_id", admin_id),
								eq("element_name", string_replace(device_name))), String.class);
					} else {

						kpi_groups = collection.distinct("groups",
								and(eq("opco", opco), eq("admin_id", admin_id), eq("groups", element_key)),
								String.class);
					}

					for (String value : kpi_groups) {
						list.add(value);
					}
				}

				for (int i = 0; i < list.size(); i++) {

					String gp = list.get(i);

					DistinctIterable<String> kpi_names = collection.distinct("kpi_name",
							and(eq("opco", opco), eq("admin_id", admin_id), eq("groups", gp)), String.class);
					List<tree_children_g_s> coutput = new ArrayList<>();
					for (String kpi_name : kpi_names) {
						tree_children_g_s c1 = new tree_children_g_s(kpi_name, "file");
						coutput.add(c1);
					}
					tree_parents_g_s g1 = new tree_parents_g_s(gp, "folder", coutput);
					output.add(g1);

				}

			} catch (Exception ex) {
				log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
						+ ex.getMessage(), ex);

			}

		} else {

			try {

				MongoCollection<Document> collection = database.getCollection("kpi_formula");
				DistinctIterable<String> kpi_groups = null;
//adding keys for vendor 2
				if (domain.equals("IPBB")) {
					String check = "";

					if (vendor.equals("NOKIA")) {
						if (device_name.equals("SNMP")) {
							check = "ipbb";
						} else if (device_name.equals("show port statistics")) {
							check = "stats";
						} else if (device_name.equals("show system cpu")) {
							check = "usage";
						} else if (device_name.equals("show system memory")) {
							check = "memory";
						} else if (device_name.equals("vlan")) {
							check = "vlan";
						}
					} else if (vendor.equals("ERICSSON")) {

						if (device_name.equals("SNMP")) {
							check = "ipbb";
						} else if (device_name.equals("port counters detail")) {
							check = "port_counter";
						} else if (device_name.equals("dot1q counters detail")) {
							check = "dot1q_counter";
						}

						else {
							check = device_name;
						}

					} else if (vendor.equals("JUNIPER")) {

						if (device_name.equals("SNMP")) {
							check = "ipbb";
						} else if (device_name.equals("interfaces statistics detail")) {
							check = "stats";
						}

					} else if (vendor.equals("DPTECH_FIREWALL")) {

						if (device_name.equals("SNMP")) {
							check = "ipbb";
						} else if (device_name.equals("session")) {
							check = "session";
						} else if (device_name.equals("performance")) {
							check = "performance";
						}
					}

					else if (vendor.equals("ZTE")) {

						if (device_name.equals("SNMP")) {
							check = "ipbb";
						} else {
							check = device_name;
						}

					}

					ArrayList<String> to_find = new ArrayList<String>();
					to_find.add("groups");
					to_find.add("kpi_formula");

					MongoCollection<Document> document = database.getCollection("kpi_formula");
					FindIterable<Document> docs = document.find(eq("admin_id", admin_id));
					ArrayList<String> kpi_data = get_mongodb_distinct_values(docs, to_find);

					for (String value : kpi_data) {

						if (value.contains(check)) {
							String split1[] = value.split("@AND@");
							String kpi = split1[0].trim();

							list.add(kpi);
						}

					}

					Set<String> set = new LinkedHashSet<>();
					set.addAll(list);
					list.clear();
					list.addAll(set);

				} else if (vendor.equals("SAM") && domain.equals("TRANSMISSION")) {

					try {
						kpi_groups = collection.distinct("groups",
								and(eq("opco", opco), eq("admin_id", admin_id), eq("groups", device_name)),
								String.class);
					} catch (Exception e) {

						log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
								+ e.getMessage(), e);
					}

					for (String value : kpi_groups) {

						list.add(value);
					}
				} else if (vendor.equals("ERICSSON") && domain.equals("TRANSMISSION")) {

					try {
						kpi_groups = collection.distinct("groups", and(eq("opco", opco), eq("admin_id", admin_id),
								eq("groups", device_name.toLowerCase())), String.class);

					} catch (Exception e) {

						log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
								+ e.getMessage(), e);
					}
					if (device_name.toLowerCase().equals("radiolinkg826_24h")) {
						list.add(device_name.toLowerCase());
					} else if (device_name.toLowerCase().equals("adaptivecodingandmodulation_24h")) {
						list.add(device_name.toLowerCase());
					} else {
						for (String value : kpi_groups) {

							list.add(value);
						}
					}
////System.out.println(list);

				} else {

					if (vendor.equals("ERICSSON") && domain.equals("RADIO")) {

						kpi_groups = collection.distinct("groups", and(eq("opco", opco), eq("admin_id", admin_id),
								eq("element_name", string_replace(device_name))), String.class);
					} else {

						kpi_groups = collection.distinct("groups", and(eq("opco", opco), eq("admin_id", admin_id)),
								String.class);
					}

					for (String value : kpi_groups) {
						list.add(value);
					}
				}

				for (int i = 0; i < list.size(); i++) {

					String gp = list.get(i);

					DistinctIterable<String> kpi_names = null;

					if (domain.equals("TRANSMISSION")
							&& (type.equals("from_all_element") || type.equals("from_each_element"))) {
						MongoCollection<Document> collection1 = database.getCollection("filter_kpi_formula");
						kpi_names = collection1.distinct(
								"kpi_name", and(eq("opco", opco), eq("admin_id", admin_id),
										eq("groups", gp.replace("_24h", "")), eq("link_with_dashboard", "yes")),
								String.class);
					} else {
						kpi_names = collection.distinct("kpi_name",
								and(eq("opco", opco), eq("admin_id", admin_id), eq("groups", gp)), String.class);
					}

					List<tree_children_g_s> coutput = new ArrayList<>();
					for (String kpi_name : kpi_names) {
						tree_children_g_s c1 = new tree_children_g_s(kpi_name, "file");
						coutput.add(c1);
					}
					tree_parents_g_s g1 = new tree_parents_g_s(gp, "folder", coutput);
					output.add(g1);

				}

			} catch (Exception e) {
				log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
						+ e.getMessage(), e);

			}

		}
		log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "**************     " + vendor
				+ "****************** exit   get_br_kpi_name ****************");
		close_mongo_connection(connection);
		return output;

	}

//TODO graph
	@Override
	public dual_axis_1_date_format get_ip_graph(String opco, String admin_id, String vendor, String domain,
			ArrayList<String> kpi_name, ArrayList<String> ne_name, ArrayList<String> filter1, ArrayList<String> filter2,
			String duration, String start_date, String end_date, String starttime, String endtime, String apn,
			ArrayList<String> check_axis, String type, String graph_type) {

		ArrayList<String> color = new ArrayList<String>();
		color.add("#483D8B");
		color.add("#228B22");
		color.add("#FFF533");
		color.add("#974858");
		color.add("#489781");
		color.add("#8C9748");
		color.add("#8C9748");
		color.add("#486597");
		color.add("#FFA07A");
		color.add("#FF8C00");
		color.add("#BA55D3");
		color.add("#333DFF");
		color.add("#4682B4");
		color.add("#BC8F8F");
		color.add("#800000");
		color.add("#708090");
		color.add("#D2B48C");
		color.add("#D2B48C");
		color.add("#00FF7F");
		color.add("#FF00FF");
		color.add("#663399");
		color.add("#DB7093");
		color.add("#FFA07A");
		color.add("#FF8C00");
		color.add("#008B8B");
		if (log.isDebugEnabled()) {
			log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "**************     "
					+ vendor + "****************** enter   get_ip_graph ****************");

		}

		String natural_trend = "no";

		StringBuilder title_append = new StringBuilder();

		for (String k : kpi_name) {
			title_append.append(string_replace(k) + " ,");
		}

		if (type.equals("complete_graph")) {
			String kpi = kpi_name.get(0);
			kpi_name.clear();

			String axis = check_axis.get(0);
			check_axis.clear();
			for (String a : ne_name) {
				kpi_name.add(kpi);
			}

			for (String a : ne_name) {
				check_axis.add(axis);
			}
		} else if (type.equals("for_sla")) {
			kpi_name.add("Threshold");
			check_axis.add("1");

			ne_name.add(ne_name.get(0));
			filter1.add(filter1.get(0));
			filter2.add(filter2.get(0));
		}

		MongoClient connection = get_mongo_connection();
		String StartTime = "", EndTime = "";

		MongoDatabase database = null;
		dual_axis_1a title = new dual_axis_1a(); // This is for title of graph.

		dual_axis_1a sub_title = new dual_axis_1a(); // This is for title of graph.

		dual_axis_2 zoomtype = new dual_axis_2(); // for zoomtype

//for zoom type
		zoomtype.setZoomType("xy");
		zoomtype.setWidth(300);
		zoomtype.setHeight(300);

		ArrayList<String> values_x = new ArrayList<String>(); // values for x-axis

		ArrayList<String> unique_dates = new ArrayList<String>(); // unique dates----valid only if the trend is natural

		int check_time;

		Properties config = getProperties();

		if (domain.equals("RADIO")) {
			database = database(connection, domain, vendor);

			if (duration.equals("15 Mins")) {

				natural_trend = "yes";
			}

		}

		if (domain.equals("TRANSMISSION") && vendor.equals("ERICSSON")) {
			database = database(connection, domain, vendor);

			natural_trend = "yes";

		} else if (domain.equals("TRANSMISSION") && vendor.equals("SAM")) {
			database = database(connection, domain, vendor);

			natural_trend = "yes";

		} else if (domain.equals("CORE") && vendor.equals("ZTE")) {
			database = database(connection, domain, vendor);
			if (duration.equals("15 Mins")) {

				natural_trend = "yes";
			}

		} else if (domain.equals("IPRAN")) {
			database = database(connection, domain, vendor);

			if (duration.equals("15 Mins")) {

				natural_trend = "yes";
			}

		} else if (domain.equals("IPBB")) {

			database = database(connection, domain, vendor);
			if (duration.equals("5 Mins")) {
				natural_trend = "yes";

			}

		} else {

		}

//for sla======
		if (domain.equals("IPBB") || domain.equals("IPRAN")) {

//for all interfaces
			if (string_replace(string_replace(filter2.get(0))).equals("All Interface")) {
				String kpi = string_replace(kpi_name.get(0));

				kpi_name.clear();

				String table_hint = mongo_generic(database, admin_id, kpi, "kpi_formula", "kpi_formula");

				String hint = to_get_table_name(table_hint);

//try {
//  hint=StringUtils.substringBetween(table_hint, "(",":").toLowerCase();
//} catch (Exception e) {
//
//  hint=StringUtils.substringBetween(table_hint, "(",":").toLowerCase();
//} 
//  

//link option with command name  
				String command_name = "";

				if (vendor.equals("ERICSSON")) {
					if (hint.contains("ipbb")) {
						command_name = "snmp";
					} else {
						command_name = hint.replace("port_counter", "port counters detail").replace("dot1q_counter",
								"dot1q counters detail");
					}
				} else if (vendor.equals("ZTE")) {
					if (hint.contains("ipbb")) {
						command_name = "snmp";
					}

				} else if (vendor.equals("NOKIA")) {
					if (hint.contains("ipbb")) {
						command_name = "snmp";
					} else {
						command_name = hint.replace("stats", "show port statistics").replace("usage", "show system cpu")
								.replace("memory", "show system memory");
					}

				} else if (vendor.equals("HUAWEI")) {
					if (hint.contains("ipran")) {
						command_name = "snmp";
					}

				} else if (vendor.equals("JUNIPER")) {
					if (hint.contains("ipbb")) {
						command_name = "snmp";
					} else {
						command_name = hint.replace("stats", "interfaces statistics detail");
					}

				}

				String axis = check_axis.get(0);
				check_axis.clear();

				String ip = filter1.get(0);
				filter1.clear();

				String elementname = string_replace(ne_name.get(0));
				ne_name.clear();

				String interface_name = string_replace(filter2.get(0));
				filter2.clear();

				if (type.equals("for_sla")) {

					ArrayList<String> initial_interface = new ArrayList<String>();

					MongoCollection<Document> collection = null;
					if (command_name.equals("vlan")) {
						collection = database.getCollection("vlan_element_command_structure");
					}

					else {
						collection = database.getCollection("element_command_structure");
					}

					DistinctIterable<String> values = collection.distinct("interface",
							and(eq("devicename", string_replace(elementname)), eq("command", command_name)),
							String.class);

					for (String value : values) {
						initial_interface.add(value);
					}

					MongoCollection<Document> collection1 = database.getCollection("sla_alerts");
					DistinctIterable<String> values1 = collection1.distinct("Interface",
							and(eq("Site_ID", string_replace(elementname)), eq("KPIName", string_replace(kpi))),
							String.class);
					for (String value1 : values1) {

						if (initial_interface.contains(value1)) {
							kpi_name.add(kpi);
							check_axis.add(axis);
							filter1.add(ip);
							filter2.add(value1);
							ne_name.add(elementname);
						}

					}
					kpi_name.add("Threshold");
					check_axis.add("1");
					filter1.add(ip);
					filter2.add(filter2.get(0));
					ne_name.add(elementname);

				} else {

					MongoCollection<Document> collection = null;
					if (command_name.equals("vlan")) {
						collection = database.getCollection("vlan_element_command_structure");
					}

					else {
						collection = database.getCollection("element_command_structure");
					}
					DistinctIterable<String> document = collection.distinct("interface",
							and(eq("devicename", string_replace(elementname)), eq("command", command_name)),
							String.class);

					for (String docs : document) {

						kpi_name.add(kpi);
						check_axis.add(axis);
						filter1.add(ip);
						filter2.add(docs);
						ne_name.add(elementname);
					}

				}

			}

		} else if (domain.equals("TRANSMISSION") && vendor.equals("ERICSSON")) {
//for all interfaces
			if (string_replace(string_replace(filter1.get(0))).equals("All")) {
				String kpi = "";

				ArrayList<String> initial_kpis = new ArrayList<String>();
				ArrayList<String> initial_axis = new ArrayList<String>();

				for (String kp : kpi_name) {
					initial_kpis.add(kp);
				}

				for (String ax : check_axis) {
					initial_axis.add(ax);
				}

				kpi = string_replace(kpi_name.get(0));

				kpi_name.clear();

				String axis = check_axis.get(0);
				check_axis.clear();

				String elementname = string_replace(ne_name.get(0));
				ne_name.clear();

				String interface_name = string_replace(filter1.get(0));
				filter1.clear();
				filter2.clear();
				if (type.equals("for_sla")) {

//MongoCollection < Document > collection = database.getCollection("objects");

//DistinctIterable < String > values = collection.distinct("object", and(eq("element_name", string_replace(elementname))), String.class);

//for (String value: values) {
//initial_interface.add(value);
//}   

					MongoCollection<Document> collection1 = database.getCollection("sla_alerts");

					DistinctIterable<String> values1 = collection1.distinct("MEASOBJLDN",
							and(eq("SITE_ID", string_replace(elementname)), eq("KPI_NAME", string_replace(kpi))),
							String.class);

					for (String value1 : values1) {

						kpi_name.add(kpi);
						check_axis.add(axis);

						filter1.add(value1);
						ne_name.add(elementname);

						filter2.add("-");

					}
					kpi_name.add("Threshold");
					check_axis.add("1");

					filter1.add(filter1.get(0));
					filter2.add(filter2.get(0));
					ne_name.add(elementname);

				} else {
					MongoCollection<Document> collection = database.getCollection(duration.toLowerCase());
					DistinctIterable<String> document = collection.distinct("MEASOBJLDN",
							and(eq("ELEMENT_NAME", string_replace(elementname))), String.class);

					int c = 0;
					for (String kpii : initial_kpis) {
						for (String docs : document) {
							kpi_name.add(kpii);
							check_axis.add(initial_axis.get(c));
							ne_name.add(elementname);
							filter1.add(docs);
							filter2.add("-");
//ne_name.add(elementname);
						}
						c++;
					}
				}

			}

		} else if (domain.equals("TRANSMISSION") && vendor.equals("SAM")) {
			if (string_replace(string_replace(filter2.get(0))).equals("All monitoredObjectPointer")) {

				if (type.equals("for_sla")) {

					String kpi = string_replace(kpi_name.get(0));

					kpi_name.clear();

					String table_hint = mongo_generic(database, admin_id, kpi, "kpi_formula", "kpi_formula");

					String axis = check_axis.get(0);

					check_axis.clear();

					String ip = filter1.get(0);
					filter1.clear();

					String elementname = string_replace(ne_name.get(0));

					ne_name.clear();

					String interface_name = string_replace(filter2.get(0));

					filter2.clear();

					String hint = "";

					try {
						hint = StringUtils.substringBetween(table_hint, "(", ":").toLowerCase();
					} catch (Exception e) {
						log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
								+ e.getMessage(), e);
					}

					MongoCollection<Document> collection = database.getCollection("sla_alerts");
					DistinctIterable<String> document = collection.distinct("monitoredObjectPointer",
							and(eq("monitoredObjectSiteId", string_replace(elementname))), String.class);

					for (String docs : document) {

						kpi_name.add(kpi);
						check_axis.add(axis);
						filter1.add(ip);
						filter2.add(docs);
						ne_name.add(elementname);
					}
					kpi_name.add("Threshold");
					filter2.add(filter2.get(0));
					check_axis.add(check_axis.get(0));
					filter1.add(filter1.get(0));
					ne_name.add(ne_name.get(0));

				} else {

					ArrayList<String> initial_kpis = new ArrayList<String>();
					ArrayList<String> initial_axis = new ArrayList<String>();

					for (String kp : kpi_name) {
						initial_kpis.add(kp);
					}

					for (String ax : check_axis) {
						initial_axis.add(ax);
					}

					String kpi = string_replace(kpi_name.get(0));

					kpi_name.clear();
					String table_hint = mongo_generic(database, admin_id, kpi, "kpi_formula", "kpi_formula");

					String axis = check_axis.get(0);
					check_axis.clear();

					String ip = filter1.get(0);
					filter1.clear();

					String elementname = string_replace(ne_name.get(0));
					ne_name.clear();

					String interface_name = string_replace(filter2.get(0));
					filter2.clear();

					String hint = "";

					try {
						hint = StringUtils.substringBetween(table_hint, "(", ":").toLowerCase();
					} catch (Exception e) {

						log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
								+ e.getMessage(), e);
					}

					MongoCollection<Document> collection = database.getCollection(hint);
					DistinctIterable<String> document = collection.distinct("displayedName",
							and(eq("monitoredObjectSiteId", string_replace(elementname))), String.class);

					int c = 0;
					for (String kpii : initial_kpis) {
						for (String docs : document) {
							kpi_name.add(kpii);
							check_axis.add(initial_axis.get(c));
							ne_name.add(elementname);
							filter1.add(ip);
							filter2.add(docs);
//ne_name.add(elementname);
						}
						c++;
					}

				}

			}
		}

		SimpleDateFormat formatter = new SimpleDateFormat(config.getProperty("performance.dateformat1"));
		try {

			Date date = formatter.parse(start_date);

			start_date = new SimpleDateFormat(config.getProperty("performance.dateformat3")).format(date);

		} catch (Exception e) {
			log.error(
					"Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(),
					e);
		}

		try {
			Date date = formatter.parse(end_date);

			end_date = new SimpleDateFormat(config.getProperty("performance.dateformat3")).format(date);

		} catch (Exception e) {
			log.error(
					"Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(),
					e);

		}

//checking time ie whether time is for full day or some limited value
		String interval = "";
		if (starttime.contains(":") && endtime.contains(":")) {

			StartTime = starttime;
			EndTime = endtime;

			if (natural_trend.equals("no")) {

				if (duration.equals("15 Mins")) {

					interval = "15";
				} else if (duration.equals("Hourly")) {

					interval = "60";
				} else if (duration.equals("Day")) {

					interval = "Day";
				}

			}
			check_time = 1;

		} else {

			if (natural_trend.equals("no")) {

				if (duration.equals("15 Mins")) {
					StartTime = "00:00:00";
					EndTime = "23:45:00";
					interval = "15";
				} else if (duration.equals("Hourly")) {
					StartTime = "00:00:00";
					EndTime = "00:00:00";
					interval = "60";
				} else if (duration.equals("Day")) {
					StartTime = "00:00:00";
					EndTime = "00:00:00";
					interval = "Day";
				}
			}
			check_time = 0;
		}

		ArrayList<dual_axis_7_color> series = new ArrayList<dual_axis_7_color>();
//----check whether kpi contains apn or not---------

//ArrayList<String> time = new ArrayList<>();

		dual_axis_3_time_update xaxis = null;
//naming for yaxis
		ArrayList<dual_axis_5> yaxis = null;

		if (check_time == 0) {

		} else if (check_time == 1) {

		}

		try {

			if (natural_trend.equals("no")) {
				values_x = new date_time_relation().date_time_relations(database, start_date, end_date, StartTime,
						EndTime, interval);

				int tick_interval = values_x.size() / 12;
				date_format_graph format = new date_format_graph("{value:%Y-%b-%e %H:%M:%S}");
//dateTimeLabelFormats format1 =new dateTimeLabelFormats("%H:%M:%S.%L", "%H:%M:%S", "%H:%M", "%H:%M:%S", "24:00", "%e. %b", "%b \'%y", "%Y");
				xaxis = new dual_axis_3_time_update(values_x, true, "category", tick_interval, format, 3);
			} else {
				unique_dates = mongo_get_name(database, start_date, end_date);

			}

		} catch (Exception e) {
			log.error(
					"Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(),
					e);

		}

		if (!type.equals("for_sla") && domain.equals("IPBB") && kpi_name.size() == 1) {

			String ne_single_name = string_replace(ne_name.get(0));

			String filter1_name = string_replace(filter1.get(0));

			String filter2_name = string_replace(filter2.get(0));

			MongoDatabase database1 = database(connection, "report", "report");
			MongoCollection<Document> collection = database1.getCollection("interface_details");
			String output = "";
			DistinctIterable<String> values = collection.distinct("ifDescr",
					and(eq("ipaddress", filter1_name), eq("ifName", string_replace(filter2_name))), String.class);

			for (String inter : values) {
				output = inter;
			}

			if (output.trim().length() > 1) {
				sub_title.setText(output);
			} else {
				sub_title.setText("");
			}
////System.out.println(output);
		} else {
			sub_title.setText("");
		}

		for (int i = 0; i < kpi_name.size(); i++) {
			String kpi = string_replace(kpi_name.get(i));
			if (kpi.equals("Threshold")) {
				String threshold = mongo_select1_where2(database, "threshold", "kpi_formula", "admin_id", admin_id,
						"kpi_name", string_replace(kpi_name.get(0))).get(0);
				kpi = string_replace(kpi_name.get(0));
				String titlee = string_replace(kpi_name.get(0)) + "  Vs  Threshold (" + threshold + ")";
				title.setText(titlee);
				String ne_single_name = "";
				if (domain.equals("TRANSMISSION") && vendor.equals("ERICSSON")) {
					ne_single_name = string_replace(ne_name.get(i));
//     final_table_hint=duration.toLowerCase();
				} else {
					ne_single_name = string_replace(ne_name.get(i));
				}

				String filter1_name = string_replace(filter1.get(i));

				String filter2_name = string_replace(filter2.get(i));

				String label = "";

//if only 1st filter contains value
				if (filter1_name.length() > 1 && filter2_name.length() <= 1) {
					label = "Threshold";
				} else if (filter1_name.length() > 1 && filter2_name.length() > 1) {
					label = "Threshold";
				} else {
					label = "Threshold";
				}

				ArrayList<Double> dummy = new ArrayList<Double>();

				ArrayList<Double> data1 = new ArrayList<Double>();
				try {

					String calculation_type = mongo_generic(database, admin_id, kpi, "calculation_type", "kpi_formula");

					if (natural_trend.equals("no")) {
						dummy = new ipran_ipbb_graph_values().graph_value(domain, database, kpi, ne_single_name,
								filter1_name, filter2_name, values_x, interval, calculation_type, "");

						for (double d : dummy) {

							data1.add(Double.parseDouble(threshold));
						}
					} else {

						ArrayList<String> values_natural_trend = null;

						if (vendor.equals("ZTE") && domain.equals("CORE")) {
//values_natural_trend = new ericsson_radio_graph_values().graph_value_natural_trend(domain, database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval, calculation_type, StartTime, EndTime, check_time);

						} else if (vendor.equals("ERICSSON") && domain.equals("TRANSMISSION")) {

							String hint = duration;
							values_natural_trend = new ericsson_radio_graph_values().graph_value_natural_trend(domain,
									database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval,
									calculation_type, StartTime, EndTime, check_time, hint);

						} else if (vendor.equals("SAM") && domain.equals("TRANSMISSION")) {
							String table_hint = mongo_generic(database, admin_id, kpi, "kpi_formula", "kpi_formula");
							values_natural_trend = new sam_microwave_graph_values().graph_value_natural_trend(domain,
									database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval,
									calculation_type, StartTime, EndTime, check_time, table_hint);

						} else {
							String table_hint = mongo_generic(database, admin_id, kpi, "kpi_formula", "kpi_formula");

							String final_table_hint = ipbb_table_hint(vendor, table_hint);

							values_natural_trend = new ipran_ipbb_graph_values().graph_value_natural_trend(domain,
									database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval,
									calculation_type, StartTime, EndTime, check_time, final_table_hint);

						}

						for (String values : values_natural_trend) {
							String split1[] = values.split("@AND@");
							values_x.add(split1[0]);
							data1.add(Double.parseDouble(threshold));
						}
						int tick_interval = values_natural_trend.size() / 12;
						date_format_graph format = new date_format_graph("{value:%Y-%b-%e %H:%M:%S}");
//dateTimeLabelFormats format1 =new dateTimeLabelFormats("%H:%M:%S.%L", "%H:%M:%S", "%H:%M", "%H:%M:%S", "24:00", "%e. %b", "%b \'%y", "%Y");
						xaxis = new dual_axis_3_time_update(values_x, true, "category", tick_interval, format, 3);
					}

					dual_axis_8 valuesuffix1 = new dual_axis_8("");

					dual_axis_7_color series_item1 = new dual_axis_7_color(label, "line",
							Integer.parseInt(check_axis.get(i)), data1, valuesuffix1, color.get(i)); // for series value
																										// to represent
																										// line
					series.add(series_item1);

				} catch (Exception e) {
					log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
							+ e.getMessage(), e);

				}

			} else {

				title.setText(StringUtils.chop(title_append.toString()));
				ArrayList<String> gitter_kpis = new ArrayList<String>();
				gitter_kpis.add("Packet Loss(%)");
				gitter_kpis.add("Minimum Delay(ms)");
				gitter_kpis.add("Average Delay(ms)");
				gitter_kpis.add("Maximum Delay(ms)");
				gitter_kpis.add("Jitter(mdev) ms");
				String table_hint = "";
				String final_table_hint = "";
				if (gitter_kpis.contains(kpi)) {
					table_hint = "_ping";
					final_table_hint = table_hint;
				}

				else {
					table_hint = mongo_generic(database, admin_id, kpi, "kpi_formula", "kpi_formula");
					final_table_hint = ipbb_table_hint(vendor, table_hint);
				}

////System.out.println(final_table_hint);
				String ne_single_name = "";
				if (domain.equals("TRANSMISSION") && vendor.equals("ERICSSON")) {
					ne_single_name = string_replace(ne_name.get(i));
					final_table_hint = duration.toLowerCase();

				} else {

					ne_single_name = string_replace(ne_name.get(i));
				}

				String filter1_name = string_replace(filter1.get(i));

				String filter2_name = string_replace(filter2.get(i));

				String label = "";

//if only 1st filter contains value
				if (filter1_name.length() > 1 && filter2_name.length() <= 1) {

					if (domain.equals("TRANSMISSION")) {
						MongoDatabase database_connectivity = database(connection, "all", "topology");
						if (vendor.equals("ERICSSON")) {
							String el_name = StringUtils.substringAfter(ne_single_name, "_");
							String lan_wan = filter1_name.replace("LAN ", "LAN-").replace("WAN ", "WAN-").replace("IF=",
									"");

							String facing_side = connectivity_where_with_hint_regex(database_connectivity, "index_1",
									"name", "vendor", "Ericsson", "locSysName", el_name, "locSysPort", lan_wan);

//	//////////System.out.printlnout.println("facing_side==="+facing_side);

							label = kpi + "       " + ne_single_name + "       " + filter1_name + "<=====>"
									+ facing_side;
						}

						else {
							label = kpi + "    " + ne_single_name + "       " + filter1_name;
						}

					}

					else {
						label = kpi + "    " + ne_single_name + "       " + filter1_name;
					}
				} else if (filter1_name.length() > 1 && filter2_name.length() > 1) {

					if (domain.equals("TRANSMISSION")) {

						MongoDatabase database_connectivity = database(connection, "all", "topology");
						if (vendor.equals("SAM")) {
							String facing_side = connectivity_where_with_hint_regex(database_connectivity, "index_1",
									"userLabel", "vendor", "Nokia", "siteId", ne_single_name, "terminatedObjectName",
									filter2_name);

//	//////////System.out.printlnout.println("facing_side==="+facing_side);

							label = kpi + "       " + filter1_name + "       " + filter2_name + "<=====>" + facing_side;
						}

						else {

							label = kpi + "    " + ne_single_name + "       " + filter1_name + "       " + filter2_name;
						}

					}

					else {
						label = kpi + "    " + ne_single_name + "       " + filter1_name + "       " + filter2_name;
					}

				} else {

					label = kpi + "    " + ne_single_name;

				}

				ArrayList<Double> data1 = new ArrayList<Double>();

				try {

					String calculation_type = mongo_generic(database, admin_id, kpi, "calculation_type", "kpi_formula");

					if (natural_trend.equals("no")) {

						if (domain.equals("CORE") && vendor.equals("ZTE")) {
							data1 = new ericsson_radio_graph_values().graph_value(domain, database, kpi, ne_single_name,
									filter1_name, filter2_name, values_x, interval, calculation_type);
						} else if (domain.equals("RADIO")) {
							data1 = new ericsson_radio_graph_values().graph_value(domain, database, kpi, ne_single_name,
									filter1_name, filter2_name, values_x, interval, calculation_type);
						} else {

							data1 = new ipran_ipbb_graph_values().graph_value(domain, database, kpi, ne_single_name,
									filter1_name, filter2_name, values_x, interval, calculation_type, final_table_hint);
						}
					} else {
						ArrayList<String> values_natural_trend = null;

						if (domain.equals("CORE") && vendor.equals("ZTE")) {
//values_natural_trend = new ericsson_radio_graph_values().graph_value_natural_trend(domain, database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval, calculation_type, StartTime, EndTime, check_time);
						} else if (vendor.equals("SAM") && domain.equals("TRANSMISSION")) {
							values_natural_trend = new sam_microwave_graph_values().graph_value_natural_trend(domain,
									database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval,
									calculation_type, StartTime, EndTime, check_time, table_hint);

						} else if (domain.equals("RADIO") || domain.equals("TRANSMISSION")) {

							values_natural_trend = new ericsson_radio_graph_values().graph_value_natural_trend(domain,
									database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval,
									calculation_type, StartTime, EndTime, check_time, final_table_hint);
						} else {

							values_natural_trend = new ipran_ipbb_graph_values().graph_value_natural_trend(domain,
									database, kpi, ne_single_name, filter1_name, filter2_name, unique_dates, interval,
									calculation_type, StartTime, EndTime, check_time, final_table_hint);

						}
						ArrayList<String> x_values = new ArrayList<>();

						for (String values : values_natural_trend) {
							String split1[] = values.split("@AND@");
							x_values.add(split1[0].substring(0, split1[0].length() - 3));

							data1.add(Double.parseDouble(split1[1]));

						}
						String maximum_value = "";
						BigDecimal result_maximum = new BigDecimal("" + peak_value(data1));
						maximum_value = "" + result_maximum.longValue();

						String minimum_value = "";
						BigDecimal result_minimum = new BigDecimal("" + minimum_value(data1));
						minimum_value = "" + result_minimum.longValue();

						String average_value = "";
						DecimalFormat f = new DecimalFormat("##.00");
						average_value = f.format(calculateAverage(data1));

						String show_values = "( Min:" + minimum_value + " , Max:" + maximum_value + " , Avg:"
								+ average_value + ")";
////////System.out.println(show_values);
						if (domain.equals("IPBB") | domain.equals("IPRAN")) {
							label = label + " " + show_values;
						}
						int tick_interval = values_natural_trend.size() / 12;
						date_format_graph format = new date_format_graph("{value:%Y-%b-%e %H:%M:%S}");
//dateTimeLabelFormats format1 =new dateTimeLabelFormats("%H:%M:%S.%L", "%H:%M:%S", "%H:%M", "%H:%M:%S", "24:00", "%e. %b", "%b \'%y", "%Y");
						xaxis = new dual_axis_3_time_update(x_values, true, "category", tick_interval, format, 3);

					}

					dual_axis_8 valuesuffix1 = new dual_axis_8("");
					String final_graph_type = graph_type.toLowerCase().replace("horizontal bar", "bar")
							.replace("vertical bar", "column").toLowerCase();
					dual_axis_7_color series_item1 = new dual_axis_7_color(label, final_graph_type.toLowerCase(),
							Integer.parseInt(check_axis.get(i)), data1, valuesuffix1, color.get(i)); // for series value
																										// to represent
																										// line
					series.add(series_item1);

				} catch (Exception e) {
					log.error("Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---"
							+ e.getMessage(), e);

				}

			}

		}

		dual_axis_6 tooltip = new dual_axis_6(true); // for tooltip

//naming for yaxis
		yaxis = new ArrayList<dual_axis_5>();

//name for left axis under y axis.
		String s_axis_name = "";
		String p_axis_name = "";
		if (check_axis.contains("0")) {

			String kpi = string_replace(kpi_name.get(check_axis.indexOf("0")));

			if (domain.equals("TRANSMISSION")) {
				s_axis_name = mongo_generic(database, admin_id, kpi, "unit", "kpi_formula");
			} else {
				s_axis_name = "Secondary Axis";

			}

		} else {
			s_axis_name = "";
		}

		if (check_axis.contains("1")) {

			String kpi = string_replace(kpi_name.get(check_axis.indexOf("1")));

			if (domain.equals("TRANSMISSION")) {
				p_axis_name = mongo_generic(database, admin_id, kpi, "unit", "kpi_formula");
			} else {
				p_axis_name = "Primary Axis";
			}

		} else {
			p_axis_name = "";
		}

		dual_axis_4 textright = new dual_axis_4(s_axis_name);

		dual_axis_5 yaxis_items2 = new dual_axis_5(textright, true);

		yaxis.add(yaxis_items2);

		dual_axis_4 text_left = new dual_axis_4(p_axis_name);

		dual_axis_5 yaxis_items1 = new dual_axis_5(text_left, false);

		yaxis.add(yaxis_items1);

		exporting exporting = new exporting(true, false, "line-chart");

		dual_axis_1_date_format main_json = new dual_axis_1_date_format(zoomtype, title, sub_title, xaxis, yaxis,
				tooltip, series, exporting); // This is the main class where all the classes are join together to create
												// a single json.

		try {

		} catch (Exception e) {
			log.error(
					"Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(),
					e);

		}

		log.debug("******" + admin_id + "******    " + opco + "*************" + domain + "**************     " + vendor
				+ "****************** exit   get_ip_graph ****************");

		close_mongo_connection(connection);
		return main_json;

	}

//TODO
//======================IP PING AUDIT=======================================

	@Override
	public String ip_audit_structure(String opco, String vendor, String topology_type, String ring_id,
			String click_type) {
////System.out.println("ring_id==="+ring_id);
		String output = "";
		Properties config = getProperties();

		MongoClient database_connection = get_mongo_connection();

		MongoDatabase database = database_connection.getDatabase(config.getProperty("database.ip_audit_database"));

		JSONObject main_chart_json = new JSONObject();
		JSONObject test_series = new JSONObject();
		try {

			JSONObject for_series = new JSONObject();

			JSONObject dataLabels = new JSONObject();
			JSONObject link_properties = new JSONObject();

			dataLabels.put("enabled", true);
			// dataLabels.put("linkFormat", "{point.fromNode.name} \u2192
			// {point.toNode.name}");
			dataLabels.put("linkFormat", "{point.key}");

			dataLabels.put("id", "{point.key}");
			for_series.put("dataLabels", dataLabels);
			for_series.put("id", "check");

			link_properties.put("width", 2);
			link_properties.put("length", 100);
			link_properties.put("color", "#828282");
			link_properties.put("dashStyle", "dash");

			link_properties.put("id", "{point.key}");
			for_series.put("link", link_properties);

			if (topology_type.equals("ring_topology")) {

				MongoCollection<Document> collection = database.getCollection("ring_topology");
				Map<String, Object> groupMap = new HashMap<String, Object>();
				groupMap.put("source", "$source");
				groupMap.put("target", "$target");
				DBObject groupFields = new BasicDBObject(groupMap);
				ArrayList<Document> iterDo = collection.aggregate(Arrays.asList(group(groupFields)))
						.into(new ArrayList<Document>());
				JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
				JSONArray for_data = new JSONArray();
				for (int i = 0; i < jsonArray.length(); i++) {
					ArrayList<String> data = new ArrayList<String>();
					JSONObject jsonObject1 = jsonArray.getJSONObject(i);
					JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
					String source = jsonObject_id.optString("source");
					String target = jsonObject_id.optString("target");

					data.add(source);
					data.add(target);

					String blink1 = blink_ring(database, source);
					String blink2 = blink_ring(database, target);

					String blink = "";
					if (blink1.equals("yes") && blink2.equals("yes")) {
						blink = "yes";
					} else {
						blink = "no";
					}
					// //System.out.println(source+"==="+target+"==="+blink);
					data.add(blink);
					for_data.put(data);
					for_series.put("data", for_data);
				}

			}

			else if (topology_type.equals("atn_cx_connections")) {

				MongoCollection<Document> collection = database.getCollection("ping_status");
				Map<String, Object> groupMap = new HashMap<String, Object>();

				groupMap.put("element_name", "$element_name");
				groupMap.put("remote_element_name", "$remote_element_name");
				groupMap.put("interface", "$interface");
				groupMap.put("remote_interface_ipaddress", "$remote_interface_ipaddress");
				groupMap.put("vrf", "$vrf");
				groupMap.put("actual_ip_address", "$actual_ip_address");
				groupMap.put("ping_status", "$ping_status");
				DBObject groupFields = new BasicDBObject(groupMap);

				ArrayList<Document> iterDo = null;

				if (click_type.equals("initial")) {

					if (ring_id.contains(".")) {
						iterDo = collection
								.aggregate(
										Arrays.asList(match(and(eq("actual_ip_address", ring_id))), group(groupFields)))
								.into(new ArrayList<Document>());

					} else {
						iterDo = collection.aggregate(Arrays
								.asList(match(and(eq("ring", ring_id), ne("ping_status", "yes"))), group(groupFields)))
								.into(new ArrayList<Document>());
					}
				}

				else if (click_type.equals("full_topology")) {
					iterDo = collection
							.aggregate(Arrays.asList(match(and(eq("actual_ip_address", ring_id))), group(groupFields)))
							.into(new ArrayList<Document>());
				}

				else if (click_type.equals("vrf_interface")) {
					iterDo = collection.aggregate(Arrays
							.asList(match(and(eq("actual_ip_address", ring_id), ne("vrf", "-"))), group(groupFields)))
							.into(new ArrayList<Document>());
				} else if (click_type.equals("nonvrf_interface")) {
					iterDo = collection.aggregate(Arrays
							.asList(match(and(eq("actual_ip_address", ring_id), eq("vrf", "-"))), group(groupFields)))
							.into(new ArrayList<Document>());
				} else if (click_type.equals("ping_fail")) {
					iterDo = collection.aggregate(Arrays.asList(
							match(and(eq("actual_ip_address", ring_id), ne("ping_status", "yes"))), group(groupFields)))
							.into(new ArrayList<Document>());
				}

//ArrayList<Document> iterDo = collection.aggregate(Arrays.asList( match(and(eq("ring", ring_id ),eq("ping_status","no"))),group (groupFields))).into(new ArrayList<Document>());
//ArrayList<Document> iterDo = collection.aggregate(Arrays.asList( match(and(eq("actual_ip_address", "10.20.1.181" ))),group (groupFields))).into(new ArrayList<Document>());

				JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
				JSONArray for_data = new JSONArray();
				for (int i = 0; i < jsonArray.length(); i++) {
					ArrayList<String> data = new ArrayList<String>();
					JSONObject jsonObject1 = jsonArray.getJSONObject(i);
					JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
					String source = jsonObject_id.optString("element_name");
					String target = jsonObject_id.optString("remote_element_name");
					String interfaces = jsonObject_id.optString("interface");
					String remote_ip = jsonObject_id.optString("remote_interface_ipaddress");
					String vrf = jsonObject_id.optString("vrf");
					String actual_ip_address = jsonObject_id.optString("actual_ip_address");
					String no_blink = jsonObject_id.optString("ping_status");
					data.add(source);
					data.add(target);
					data.add(interfaces.replace("GigabitEthernet", "GE "));
					data.add(remote_ip);
					data.add(vrf);
					data.add(actual_ip_address);
					data.add(no_blink);
					for_data.put(data);
					test_series.put("data", for_data);
					for_series.put("data", for_data);

					if (i == 100) {
						// +988957868*+++++++++++++break;
					}
				}

			}

			JSONArray for_series_array = new JSONArray();
			for_series_array.put(for_series);
			main_chart_json.put("series", for_series_array);

//      ================================
			JSONObject networkgraph = new JSONObject();
			JSONObject networkgraph_item = new JSONObject();
			JSONArray keys = new JSONArray();

			keys.put("from");
			keys.put("to");
			keys.put("blink");
			networkgraph_item.put("keys", keys);

			JSONObject layoutAlgorithm = new JSONObject(); // subtitle

			layoutAlgorithm.put("enableSimulation", true);
			layoutAlgorithm.put("friction", Double.parseDouble("-0.950"));
			layoutAlgorithm.put("integration", "euler");
			layoutAlgorithm.put("maxIterations", 10000);
			layoutAlgorithm.put("maxSpeed", 100);
			layoutAlgorithm.put("linkLength", 30);
			networkgraph_item.put("layoutAlgorithm", layoutAlgorithm);
			networkgraph.put("networkgraph", networkgraph_item);
			main_chart_json.put("plotOptions", networkgraph);

//      ====================================================
			JSONObject sub_title = new JSONObject(); // subtitle

			sub_title.put("text", "");// add if you want subheading
			main_chart_json.put("subtitle", sub_title);

			JSONObject title_item = new JSONObject(); // title

			title_item.put("text", ring_id);
			main_chart_json.put("title", title_item);

			JSONObject chart_item = new JSONObject(); // chart

			chart_item.put("type", "networkgraph");
			main_chart_json.put("chart", chart_item);// chart type

			/*
			 * JSONObject chart_tooltip = new JSONObject(); // chart
			 * chart_tooltip.put("enabled", true); chart_tooltip.put("headerFormat", "");
			 * chart_tooltip.put("pointFormat", "{point.key}");
			 * 
			 * main_chart_json.put("tooltip", chart_tooltip);// Tool Tip
			 */

			// output=main_chart_json.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (topology_type.equals("atn_cx_connections")) {
			output = test_series.toString();
			// //System.out.println(output);
			// //System.out.println(main_chart_json.toString());
		} else {
			output = main_chart_json.toString();
		}
		return output;

	}

	@Override
	public ArrayList<String> get_failed_ping_elements(String opco, String admin_id, String domain, String vendor) {

		if (log.isDebugEnabled()) {
			log.debug("******" + admin_id + "******    zambia*************" + domain + "*     " + vendor
					+ "* enter   get_failed_ping_elements ****************");
		}

		ArrayList<String> output = new ArrayList<String>();

		Properties config = getProperties();

		MongoClient database_connection = get_mongo_connection();

		MongoDatabase database = database_connection.getDatabase(config.getProperty("database.ip_audit_database"));

		try {

			MongoCollection<Document> collection = database.getCollection("ping_status");

			Map<String, Object> groupMap = new HashMap<String, Object>();
			groupMap.put("element_name", "$element_name");
			groupMap.put("actual_ip_address", "$actual_ip_address");
			DBObject groupFields = new BasicDBObject(groupMap);
			ArrayList<Document> iterDo = collection
					.aggregate(Arrays.asList(match(eq("ping_status", "no")), group(groupFields)))
					.into(new ArrayList<Document>());
			JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject1 = jsonArray.getJSONObject(i);
				JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
				String element_name = jsonObject_id.optString("element_name");
				String actual_ip_address = jsonObject_id.optString("actual_ip_address");
////System.out.println(element_name + ":" + actual_ip_address);
				output.add(element_name + ":" + actual_ip_address);
			}

		} catch (Exception e) {
			log.error(
					"Exception occurs:---" + domain + "------" + vendor + "------" + admin_id + "---" + e.getMessage(),
					e);

		}

		log.debug("******" + admin_id + "******    zambia*************" + domain + "*     " + vendor
				+ "* exit   get_br_local_information ****************");
		close_mongo_connection(database_connection);

		return output;

	}

	@Override
	public ArrayList<String> ipaudit_ssh_output(String opco, String domain, String vendor, String element_address,
			String command) {
		// System.out.println("================command=====================");
		String a = "HELLO";

		String b = "HELLO 2";
		/*
		 * ArrayList<String>commands=new ArrayList<String>();
		 * 
		 * ////System.out.println("command===="+string_replace(command));
		 * ////System.out.println("element_address===="+element_address); String
		 * output=""; commands.add("ping 8.8.8.8 -c10");
		 * 
		 * 
		 * 
		 * if(command.startsWith("ping 8.8.8.8 -c10")) {
		 * commands.add(string_replace(command)); } else {
		 * commands.add("screen-length 0 temporary");
		 * commands.add(string_replace(command)); }
		 * 
		 * 
		 * // commands.add("quit"); Session session = null; MongoClient connection =
		 * get_mongo_connection();
		 * 
		 * MongoDatabase database = database(connection, "all", "topology"); String
		 * ip="192.168.100.100"; // String username=mongo_select1_where1(database,
		 * "username(fm)", "connectivitydetails", "hostname(fm)", ip.trim()).get(0); //
		 * String password=mongo_select1_where1(database, "password(fm)",
		 * "connectivitydetails", "hostname(fm)", ip.trim()).get(0);
		 * 
		 * try { session = getSession(ip, "root", "admin");
		 * 
		 * 
		 * } catch (JSchException e1) {
		 * 
		 * e1.printStackTrace(); }
		 * ////System.out.println("session==="+session.isConnected());
		 * 
		 * ArrayList<String> command_output = new ArrayList<String>(); new
		 * command_task_version3(session, commands, command_output).run();
		 * 
		 * // output=command_output.toString(); ////System.out.println(command_output);
		 * if(session.isConnected()) { session.disconnect(); }
		 */

		ArrayList<String> output = new ArrayList<String>();
		output.add(a);

		output.add(b);
		return output;
	}

	@Override
	public ArrayList<String> configuration_output() {

		ArrayList<String> command_output = new ArrayList<String>();

		Properties config = getProperties();

		String filepath = config.getProperty("config_file_path");

		BufferedReader objReader = null;
		try {
			String strCurrentLine;

			objReader = new BufferedReader(new FileReader(filepath));

			while ((strCurrentLine = objReader.readLine()) != null) {

				command_output.add(strCurrentLine + "\n");
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

		return command_output;
	}

	@Override
	public String cpu_memory_gauge(String opco, String domain, String vendor, String type, String ip,
			String element_name) {
		String output = null;
		try {
			String domainn = domain;
			if (domainn == "MPBN") {
//				this.domainn="IPBB";
			}

			JSONObject output_json = new JSONObject();
			MongoClient connection = get_mongo_connection();

			// System.out.println("---domain"+domainn);
			// System.out.println(connection);

			MongoDatabase database = database(connection, "IPBB", vendor);

//			String present_time = mongo_select1_where0(database, "value", "present_time_calc").get(0);
			String present_time = "00:00:00";

			// System.out.println("===>"+present_time);
			// present_time=sub_mins(present_time,-5);
			Properties config = getProperties();
			// String present_time = "18:00:00";

			// current date
			String timezone = config.getProperty("timezone");
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd");
			df.setTimeZone(TimeZone.getTimeZone(timezone));
			String dateTime = df.format(date);
			output = dateTime.trim();

			output = "2023_07_21";
			String firstLetter = vendor.substring(0, 1).toUpperCase();
			String restLetters = vendor.substring(1).toLowerCase();
			String v = firstLetter + restLetters.replace("_firewall", "");

			MongoDatabase database_hint = connection.getDatabase(config.getProperty("database.topology_database"));

			String column = "";
			String tablename = "";
			if (type.equals("cpu")) {

				String tablehint = mongo_select1_where2(database_hint, "tablehint", "mpbn_report_commands", "vendor", v,
						"sheetname", type.trim()).get(0);
				tablename = output + "_" + tablehint;
				column = mongo_select1_where2(database_hint, "kpiname", "mpbn_report_commands", "vendor", v,
						"sheetname", type.trim()).get(0);

				output_json.put("type", element_name + "=> CPU Utilization(%)");

			}

			else if (type.equals("memory")) {
				String tablehint = mongo_select1_where2(database_hint, "tablehint", "mpbn_report_commands", "vendor", v,
						"sheetname", type.trim()).get(0);
				tablename = output + "_" + tablehint;
				column = mongo_select1_where2(database_hint, "kpiname", "mpbn_report_commands", "vendor", v,
						"sheetname", type.trim()).get(0);

				output_json.put("type", element_name + "=> Memory Utilization(%)");

			}

			ArrayList<String> all_value = mongo_select1_where2(database, column, tablename, "ipaddress", ip,
					"start_time", present_time.trim());
			// System.out.println("AT_LINE_2413"+all_value+"---"+database+"----"+column+"----"+tablename+"---"+ip);

			ArrayList<Double> all_number_values = new ArrayList<Double>();

			for (String val : all_value) {
				all_number_values.add(Double.parseDouble(val));
			}
			double average = calculateAverage_gauge(all_number_values);
			String value = "" + average;

			if (type.equals("cpu")) {

				output_json.put("text",
						"CPU Utilization of " + element_name + "  at  " + present_time + " = " + value + " %");
			}

			else if (type.equals("memory")) {
				output_json.put("text",
						"Memory Utilization of " + element_name + "  at  " + present_time + " = " + value + "%");
			}

			output_json.put("value", Double.parseDouble(value));

			output = output_json.toString();

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("-----PERFORMANCE_IPRAN_IPBB_LINE_2444------->"+output);
		return output;
	}

	@Override
	public String new_topology(String opco, String domain, String vendor, String topology_type, String key,
			String click_type) {

		log.debug("**********    " + opco + "*************" + domain + "*     " + vendor
				+ "* enter   new_topology ****************");
		if (log.isDebugEnabled()) {
			log.debug("*********** checked into new_topology ****************");
		}

//change the table name 

//String tablename_connectivitydetails="topologydiscoveryscanconnectivity"; // table name which contains all topology details for ipran and mpbn
		String tablename_connectivitydetails = "topologydiscoveryscanconnectivity_zambia";
		String tablename_element_details = "topologydiscoveryscandetails";
		BasicDBObject index_details = new BasicDBObject("$hint", "_id_");
		BasicDBObject element_index = new BasicDBObject("$hint", "_id_");

		ArrayList<String> active_alarms_elements = new ArrayList<String>();

		String output = "";
		Properties config = getProperties();
//picking the asset path from config file
		String asset_path = config.getProperty("assets_path");
		MongoClient database_connection = get_mongo_connection();

		MongoDatabase database = database_connection.getDatabase(config.getProperty("database.topology_database"));

		JSONObject for_series = new JSONObject();

		ArrayList<Bson> fltr = new ArrayList<Bson>();
		ArrayList<Bson> fltr_or1 = new ArrayList<Bson>();

		Bson filter = null;
//applying the filters
		if (topology_type.equals("full_topology")) {
			fltr.add(eq("domain", domain));
			fltr.add(eq("remSysDomain", domain));
			fltr.add(ne("remSysIfName", "NA"));
			if (domain.equals("Ipran")) {
				fltr.add(eq("remSysIfType", "l2vlan (135)"));
				// fltr_or1.add(eq("remSysIfType","l2vlan (135)"));
				// fltr_or1.add(eq("remSysIfType","ethernet-csmacd(6)"));
				// fltr.add(or(fltr_or1));
			} else {
				// fltr_or1.add(eq("remSysIfType","Mpls"));
				// fltr_or1.add(eq("remSysIfType","ethernet-csmacd(6)"));
				// fltr.add(or(fltr_or1));
				// fltr.add(eq("remSysIfType","ethernet-csmacd(6)"));
			}

			filter = and(fltr);
		}

		else if (topology_type.equals("connectivity_type")) {

			fltr.add(eq("domain", domain));
			fltr.add(eq("remSysDomain", domain));
			fltr.add(ne("remSysIfName", "NA"));
			fltr.add(eq("remSysIfType", key));
			filter = and(fltr);
		}

		else if (topology_type.equals("by_location")) {

			fltr.add(eq("domain", domain));
			fltr.add(eq("remSysDomain", domain));

			/*
			 * fltr.add(eq("remSysLocation",key)); fltr.add(eq("location",key));
			 */
			fltr_or1.add(eq("remSysLocation", key));
			fltr_or1.add(eq("location", key));
			fltr.add(or(fltr_or1));
			fltr.add(ne("remSysIfName", "NA"));
			filter = and(fltr);
		}

		else if (topology_type.equals("by_element")) {
			/*
			 * fltr.add(eq("domain",domain)); fltr.add(eq("remSysDomain",domain));
			 * fltr.add(eq("remSysName",key)); fltr.add(eq("locSysName",key));
			 * fltr.add(ne("remSysIfName","NA")); filter=and(fltr);
			 */

			fltr.add(eq("domain", domain));
			fltr.add(eq("remSysDomain", domain));
//fltr.add(eq("remSysName",key));
//fltr.add(eq("locSysName",key));
			fltr.add(ne("remSysIfName", "NA"));

			fltr_or1.add(eq("remSysName", key));
			fltr_or1.add(eq("locSysName", key));
			fltr.add(or(fltr_or1));

			filter = and(fltr);
		} else if (topology_type.equals("by_single_element")) {

			fltr.add(eq("domain", domain));
			fltr.add(eq("remSysDomain", domain));
//fltr.add(eq("remSysName",key));
//fltr.add(eq("locSysName",key));
			fltr.add(ne("remSysIfName", "NA"));
			fltr.add(eq("remSysIfType", click_type));

			fltr_or1.add(eq("remSysName", key));
			fltr_or1.add(eq("locSysName", key));
			fltr.add(or(fltr_or1));

			filter = and(fltr);
		}

		else if (topology_type.equals("by_physical_ip")) {

			fltr.add(eq("domain", domain));
			fltr.add(eq("remSysDomain", domain));
//fltr.add(eq("remSysName",key));
//fltr.add(eq("ipaddress",key));
			fltr_or1.add(eq("ipaddress", key));
			fltr_or1.add(eq("remSysIpaddress", key));
			fltr.add(or(fltr_or1));
			fltr.add(ne("remSysIfName", "NA"));
			filter = and(fltr);
		}

		else if (topology_type.equals("by_nat_ip")) {

			fltr.add(eq("domain", domain));
			fltr.add(eq("remSysDomain", domain));
//fltr.add(eq("remSysName",key));
//fltr.add(eq("ipaddressNatted",key));

			fltr_or1.add(eq("ipaddressNatted", key));
			fltr_or1.add(eq("remSysNattedIpaddress", key));
			fltr.add(or(fltr_or1));

			fltr.add(ne("remSysIfName", "NA"));
			filter = and(fltr);
		} else if (topology_type.equals("by_ring")) {

			fltr.add(eq("domain", domain));
			fltr.add(eq("remSysDomain", domain));
			fltr.add(eq("remSysRingId", key));
			fltr.add(eq("locSysRingId", key));
			fltr.add(ne("remSysIfName", "NA"));
			filter = and(fltr);
		}

		else if (topology_type.equals("by_region")) {

			fltr.add(eq("domain", domain));
			fltr.add(eq("remSysDomain", domain));
			fltr.add(eq("location", key));
			fltr.add(eq("remSysLocation", key));
			fltr.add(ne("remSysIfName", "NA"));
			filter = and(fltr);
		} else if (topology_type.equals("vendor")) {

			fltr.add(eq("domain", domain));
			fltr.add(eq("remSysDomain", domain));
//fltr.add(eq("remSysVendor",key));
			fltr.add(eq("vendor", key));
			fltr.add(ne("remSysIfName", "NA"));
			filter = and(fltr);
		}

		else if (topology_type.equals("active_alarms")) {
//creating the filters for active alarms
			BasicDBObject index_alarm = null;
			index_alarm = new BasicDBObject("$hint",
					"VENDOR_-1_DOMAIN_-1_ALARM_ID_-1_MANAGED_OBJECT_-1_TIME_-1_NENAME_-1_NEIP_-1_ALARMNAME_-1_INSERTIONTIME_-1");
			MongoCollection<Document> collection = database.getCollection("activealarms");
			Map<String, Object> groupMap = new HashMap<String, Object>();
			groupMap.put("NENAME", "$NENAME");

			DBObject groupFields = new BasicDBObject(groupMap);
			ArrayList<Document> iterDo = collection
					.aggregate(Arrays.asList(match(eq("DOMAIN", domain)), group(groupFields))).hint(index_alarm)
					.into(new ArrayList<Document>());
			JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));

			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject jsonObject1 = jsonArray.getJSONObject(i);
				JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
				String active_alarms_element = jsonObject_id.optString("NENAME");

				active_alarms_elements.add(active_alarms_element);
			}

			ArrayList<Bson> fltr_or = new ArrayList<Bson>();
			for (int i = 0; i < active_alarms_elements.size(); i++) {
				fltr_or.add(eq("locSysName", active_alarms_elements.get(i)));
				fltr_or.add(eq("remSysName", active_alarms_elements.get(i)));
			}
			fltr.add(or(fltr_or));

			fltr.add(eq("domain", domain));
			fltr.add(eq("remSysDomain", domain));
			fltr.add(ne("remSysIfName", "NA"));
			filter = and(fltr);
		}

//first pick the nodes name from-->to
		try {
			MongoCollection<Document> collection = database.getCollection(tablename_connectivitydetails);
			MongoCollection<Document> collection_element_details = database.getCollection(tablename_element_details);
			Map<String, Object> groupMap = new HashMap<String, Object>();
			groupMap.put("locSysName", "$locSysName");
			groupMap.put("remSysName", "$remSysName");
			// groupMap.put("remSysIfName", "$remSysIfName");

			DBObject groupFields = new BasicDBObject(groupMap);
			ArrayList<Document> iterDo = collection.aggregate(Arrays.asList(match(filter), group(groupFields)))
					.hint(index_details).into(new ArrayList<Document>());
			JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
			JSONArray for_data = new JSONArray();
			JSONArray for_data_interface = new JSONArray();

			ArrayList<String> nodes = new ArrayList<String>();
			JSONArray nodes_insert = new JSONArray();
			JSONArray element_info_insert = new JSONArray();
			for (int i = 0; i < jsonArray.length(); i++) {
				ArrayList<String> data = new ArrayList<String>();
				JSONObject jsonObject1 = jsonArray.getJSONObject(i);
				JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
				String source = jsonObject_id.optString("locSysName");
				String target = jsonObject_id.optString("remSysName");
				// String interfaces=jsonObject_id.optString("remSysIfName");

				// if(!interfaces.equals("NA")) {
				if (!source.equals("End Point") && !target.equals("End Point")) { // ignoring End Points

					if (!target.contains("ATN") && !target.contains("CX600") && domain.equals("Mpbn")) { // Picking only
																											// the MPBN
																											// Nodes
						if (!source.equals(target)) {
							if (source.length() > 0 && target.length() > 0) {
								data.add(source);
								data.add(target);

								data.add("");
								nodes.add(source);
								nodes.add(target);

								for_data.put(data);
								for_series.put("data", for_data);

							}
						}
					}
					// Picking only the IPRAN Nodes
					else {
						if (!source.equals(target)) {
							if (source.length() > 0 && target.length() > 0) {
								data.add(source);
								data.add(target);

								data.add("");
								nodes.add(source);
								nodes.add(target);

								for_data.put(data);
								for_series.put("data", for_data);
							}
						}
					}
				}
				// }
			}

			// Picking the node names including the interface name and interface type(for
			// filter purpose) which put separatelt
			Map<String, Object> groupMap_interface = new HashMap<String, Object>();
			groupMap_interface.put("locSysName", "$locSysName");
			groupMap_interface.put("remSysName", "$remSysName");
			groupMap_interface.put("remSysIfName", "$remSysIfName");
			groupMap_interface.put("remSysIfType", "$remSysIfType");

			DBObject groupFields_interface = new BasicDBObject(groupMap_interface);
			ArrayList<Document> iterDo_interface = collection
					.aggregate(Arrays.asList(match(filter), group(groupFields_interface))).hint(index_details)
					.into(new ArrayList<Document>());
			JSONArray jsonArray_interface = new JSONArray(JSON.serialize(iterDo_interface));

			for (int i = 0; i < jsonArray_interface.length(); i++) {
				ArrayList<String> data_interface = new ArrayList<String>();
				JSONObject jsonObject1 = jsonArray_interface.getJSONObject(i);
				JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
				String source = jsonObject_id.optString("locSysName");
				String target = jsonObject_id.optString("remSysName");
				String interfaces = jsonObject_id.optString("remSysIfName");
				String interface_type = jsonObject_id.optString("remSysIfType");

				if (!interfaces.equals("NA")) {
					if (!source.equals("End Point") && !target.equals("End Point")) {
						if (!target.contains("ATN") && !target.contains("CX600") && domain.equals("Mpbn")) {
							if (!source.equals(target)) {
								if (source.length() > 0 && target.length() > 0) {
									data_interface.add(source);
									data_interface.add(target);
									data_interface.add(interfaces);
									data_interface.add(interface_type);
									for_data_interface.put(data_interface);
									for_series.put("interface_data", for_data_interface);

								}
							}
						}

						else {

							if (!source.equals(target)) {
								if (source.length() > 0 && target.length() > 0) {
									data_interface.add(source);
									data_interface.add(target);

									data_interface.add(interfaces);
									data_interface.add(interface_type);

									for_data_interface.put(data_interface);
									for_series.put("interface_data", for_data_interface);
								}
							}

						}
					}
				}
			}

			Set<String> set = new HashSet<String>(nodes);
			nodes.clear();
			nodes.addAll(set);

			Map<String, Object> groupMap_node = new HashMap<String, Object>();
			groupMap_node.put("locSysName", "$locSysName");
			groupMap_node.put("ipaddress", "$ipaddress");
			groupMap_node.put("ipaddressNatted", "$ipaddressNatted");

			groupMap_node.put("vendor", "$vendor");
			if (domain.equals("Ipran")) {
				groupMap_node.put("location", "$location");
				groupMap_node.put("locSysRingId", "$locSysRingId");
			}

			else {
				groupMap_node.put("location", "$location");
			}

			DBObject groupFields_node = new BasicDBObject(groupMap_node);

			ArrayList<Document> iterDo_node = collection_element_details
					.aggregate(Arrays.asList(match(eq("domain", domain)), group(groupFields_node))).hint(element_index)
					.into(new ArrayList<Document>());
			JSONArray jsonArray_node = new JSONArray(JSON.serialize(iterDo_node));
			String vendor_check = "";
			String node_name = "";

			for (int i = 0; i < jsonArray_node.length(); i++) {
				JSONObject for_nodes = new JSONObject();
				JSONObject info_element = new JSONObject();
				JSONObject marker = new JSONObject();

				JSONObject jsonObject1 = jsonArray_node.getJSONObject(i);
				JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");

				node_name = jsonObject_id.optString("locSysName");

				vendor_check = jsonObject_id.optString("vendor");
				String phy_ipaddress = jsonObject_id.optString("ipaddress");
				String nat_ipaddress = jsonObject_id.optString("ipaddressNatted");
				String location = "";
				String ring = "";
				if (domain.equals("Ipran")) {
					location = jsonObject_id.optString("location");
					ring = jsonObject_id.optString("locSysRingId");
				} else {
					location = jsonObject_id.optString("location");
				}

				if (nodes.contains(node_name)) {
					for_nodes.put("id", node_name);
					for_nodes.put("vendor", vendor_check);
					for_nodes.put("ph_address", phy_ipaddress);
					for_nodes.put("nat_address", nat_ipaddress);
					for_nodes.put("location", location);
					if (domain.equals("Ipran")) {
						for_nodes.put("ring", ring);
					}
					try {
						if (jsonArray_node.length() != 0) {
							for_nodes.put("className", node_name);
						}

						if (domain.equals("Mpbn")) {
							marker.put("symbol",
									"url(" + asset_path + "/images/MPBN_" + vendor_check.toUpperCase() + ".png)");

						}

						else {
							// //System.out.println("===>"+vendor_check);
							if (vendor_check.toUpperCase().equals("NOKIA")) {
								if (node_name.contains("7705")) {
									marker.put("symbol", "url(" + asset_path + "/images/IPRAN_"
											+ vendor_check.toUpperCase() + "_ATN.png)");
								}

								else if (node_name.contains("7750")) {
									marker.put("symbol", "url(" + asset_path + "/images/IPRAN_"
											+ vendor_check.toUpperCase() + "_CX.png)");
								}
							} else if (vendor_check.toUpperCase().equals("HUAWEI")) {
								if (node_name.contains("CX")) {
									marker.put("symbol", "url(" + asset_path + "/images/IPRAN_"
											+ vendor_check.toUpperCase() + "_CX.png)");

								}

								else {
									marker.put("symbol", "url(" + asset_path + "/images/IPRAN_"
											+ vendor_check.toUpperCase() + "_ATN.png)");
								}
							}
						}

						nodes_insert.put(for_nodes);

					} catch (Exception e) {
						log.error("Exception occurs:---" + domain + "------" + vendor + "--------" + e.getMessage(), e);
						e.printStackTrace();
					}

					if (jsonArray_node.length() != 0) {
						for_nodes.put("marker", marker);
					}
				}

				info_element.put("id", node_name);
				info_element.put("vendor", vendor_check);
				info_element.put("ph_address", phy_ipaddress);
				info_element.put("nat_address", nat_ipaddress);
				info_element.put("location", location);
				if (domain.equals("Ipran")) {
					info_element.put("ring", ring);
				}
				element_info_insert.put(info_element);

			}

			Map<String, Object> groupMap_conn_type = new HashMap<String, Object>();
			groupMap_conn_type.put("remSysIfType", "$remSysIfType");
			groupMap_conn_type.put("locSysName", "$locSysName");
			groupMap_conn_type.put("remSysName", "$remSysName");

			DBObject groupFields_conn_type = new BasicDBObject(groupMap_conn_type);
			ArrayList<Document> iterDo_conn_type = collection
					.aggregate(Arrays.asList(match(eq("domain", domain)), group(groupFields_conn_type)))
					.hint(index_details).into(new ArrayList<Document>());
			JSONArray jsonArray_conn_type = new JSONArray(JSON.serialize(iterDo_conn_type));

			JSONArray connectivity_type = new JSONArray();

			for (int i = 0; i < jsonArray_conn_type.length(); i++) {
				JSONObject conn_type = new JSONObject();
				JSONObject jsonObject1 = jsonArray_conn_type.getJSONObject(i);
				JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");

				String type = jsonObject_id.optString("remSysIfType");

				conn_type.put("con_type", type);
				connectivity_type.put(conn_type);
			}

			//// //System.out.println(missing_for_vendor);

			//// //System.out.println(missing_for_vendor);
			for_series.put("connectivity_type", connectivity_type);
			for_series.put("element_information", element_info_insert);

			for_series.put("nodes", nodes_insert);
		} catch (JSONException e) {
			log.error("Exception occurs:---" + domain + "------" + vendor + "--------" + e.getMessage(), e);
			e.printStackTrace();
		}

		output = for_series.toString();
////System.out.println("--------------->done"+for_series);

		return output;

	}

	// TODO

//NEW_TOPOLOGY_UTKARSH
	public String topology(String selectedOpco, String selectedDomain, String selectedVendor,
			String selected_topology_type, String key, String click_type) {

		log.debug("**********    " + selectedOpco + "*************" + selectedDomain + "*     " + selectedVendor
				+ "* enter topology ****************");
		if (log.isDebugEnabled()) {
			log.debug("*********** checked into test_topology ****************");
		}

		String tablename_connectivitydetails = "topologydiscoveryscanconnectivity_zambia";
//	    String tablename_activealarms="activealarms";
		String output = "";
		Properties config = getProperties();
		String asset_path = config.getProperty("assets_path");
		ArrayList<String> active_alarms_elements = new ArrayList<String>();
		MongoClient database_connection = get_mongo_connection();
		MongoDatabase database = database_connection.getDatabase(config.getProperty("database.topology_database"));
		BasicDBObject index_details = new BasicDBObject("$hint", "_id_");

//	    JSONObject alarmNodes = new JSONObject(); 
		Map<String, JSONObject> alarmNodes = new HashMap<>();
		Map<String, JSONObject> nodes = new HashMap<>();
		Set<Map<String, String>> edges = new HashSet<>();

		// Create filters based on topology type
		ArrayList<Bson> fltr = new ArrayList<>();
		ArrayList<Bson> fltr_or1 = new ArrayList<>();
		Bson filter = null;

		// applying the filters
		 //applying the filters
		if (selected_topology_type.equals("full_topology")) {
			fltr.add(eq("domain", selectedDomain));
			fltr.add(eq("remSysDomain", selectedDomain));
		//	fltr.add(eq("remSysName", "LV_M6K-CS-01"));
			fltr.add(ne("remSysIfName", "NA"));
			fltr.add(ne("remSysName", "End Point"));
			if (selectedDomain.equals("Ipran")) {
				//fltr.add(eq("remSysIfType", "ethernet-csmacd(6)"));
				//fltr.add(eq("remSysIfType", "ethernetCsmacd(6)"));
				// fltr_or1.add(eq("remSysIfType","l2vlan (135)"));
				 fltr_or1.add(eq("remSysIfType","ethernetCsmacd(6)"));
				fltr_or1.add(eq("remSysIfType","ethernet-csmacd(6)"));

				 fltr.add(or(fltr_or1));
			} else {
				// fltr_or1.add(eq("remSysIfType","Mpls"));
				// fltr_or1.add(eq("remSysIfType","ethernet-csmacd(6)"));
				// fltr.add(or(fltr_or1));
				// fltr.add(eq("remSysIfType","ethernet-csmacd(6)"));
				fltr.add(ne("remSysIfName", "NA"));
    			fltr.add(ne("remSysName", "End Point"));
			}

			filter = and(fltr);
		}

		else if (selected_topology_type.equals("connectivity_type")) {
			
			fltr.add(ne("remSysName", "End Point"));
			fltr.add(eq("domain", selectedDomain));
			fltr.add(eq("remSysDomain", selectedDomain));
			fltr.add(ne("remSysIfName", "NA"));
			fltr.add(eq("remSysIfType", click_type));
			filter = and(fltr);
		}

		else if (selected_topology_type.equals("by_location")) {

			fltr.add(eq("domain", selectedDomain));
			fltr.add(eq("remSysDomain", selectedDomain));
			fltr.add(ne("remSysName", "End Point"));
			/*
			 * fltr.add(eq("remSysLocation",key)); fltr.add(eq("location",key));
			 */
			fltr_or1.add(eq("remSysLocation", click_type));
			fltr_or1.add(eq("location", click_type));
			fltr.add(or(fltr_or1));
			fltr.add(ne("remSysIfName", "NA"));
			filter = and(fltr);
		}
		else if (selected_topology_type.equals("by_location_with_vendor")) {

			fltr.add(eq("domain", selectedDomain));
			fltr.add(eq("remSysDomain", selectedDomain));
			fltr.add(ne("remSysName", "End Point"));
			/*
			 * fltr.add(eq("remSysLocation",key)); fltr.add(eq("location",key));
			 */
			fltr_or1.add(eq("remSysVendor",click_type ));
			fltr_or1.add(eq("vendor", click_type));
			fltr.add(eq("remSysLocation", key));
			fltr.add(eq("location", key));

//			System.out.println("---------------------------------------------MORNI-------->>>>>>>>>>>>>>>>>>..."+click_type+"--"+key);
//			fltr.add(eq);
//			fltr.add(eq);
			fltr.add(or(fltr_or1));
			fltr.add(ne("remSysIfName", "NA"));
			filter = and(fltr);
		}

		else if (selected_topology_type.equals("by_element")) {
			/*
			 * fltr.add(eq("domain",domain)); fltr.add(eq("remSysDomain",domain));
			 * fltr.add(eq("remSysName",key)); fltr.add(eq("locSysName",key));
			 * fltr.add(ne("remSysIfName","NA")); filter=and(fltr);
			 */

			//fltr.add(eq("domain", selectedDomain));
			//fltr.add(eq("remSysDomain", selectedDomain));
//fltr.add(eq("remSysName",key));
//fltr.add(eq("locSysName",key));
			fltr.add(ne("remSysIfName", "NA"));
			fltr.add(ne("remSysName", "End Point"));
			fltr_or1.add(eq("remSysName", click_type));
			fltr_or1.add(eq("locSysName", click_type));
			fltr.add(or(fltr_or1));

			filter = and(fltr);
		} else if (selected_topology_type.equals("by_single_element")) {
			fltr.add(ne("remSysName", "End Point"));
			fltr.add(eq("domain", selectedDomain));
			fltr.add(eq("remSysDomain", selectedDomain));
//fltr.add(eq("remSysName",key));
//fltr.add(eq("locSysName",key));
			fltr.add(ne("remSysIfName", "NA"));
			fltr.add(eq("remSysIfType", key));

//			fltr_or1.add(eq("remSysName", click_type));
//			fltr_or1.add(eq("locSysName", click_type));
			fltr.add(or(fltr_or1));

			filter = and(fltr);
		}

		else if (selected_topology_type.equals("by_physical_ip")) {
			fltr.add(ne("remSysName", "End Point"));
			fltr.add(eq("domain", selectedDomain));
			fltr.add(eq("remSysDomain", selectedDomain));
//fltr.add(eq("remSysName",key));
//fltr.add(eq("ipaddress",key));
			fltr_or1.add(eq("ipaddress", click_type));
			fltr_or1.add(eq("remSysIpaddress", click_type));
			fltr.add(or(fltr_or1));
			fltr.add(ne("remSysIfName", "NA"));
			filter = and(fltr);
		}

		else if (selected_topology_type.equals("by_nat_ip")) {
			fltr.add(ne("remSysName", "End Point"));
			fltr.add(eq("domain", selectedDomain));
			fltr.add(eq("remSysDomain", selectedDomain));
//fltr.add(eq("remSysName",key));
//fltr.add(eq("ipaddressNatted",key));

			fltr_or1.add(eq("ipaddressNatted", click_type));
			fltr_or1.add(eq("remSysNattedIpaddress", click_type));
			fltr.add(or(fltr_or1));

			fltr.add(ne("remSysIfName", "NA"));
			filter = and(fltr);
		} else if (selected_topology_type.equals("by_ring")) {
			fltr.add(ne("remSysName", "End Point"));
			fltr.add(eq("domain", selectedDomain));
			fltr.add(eq("remSysDomain", selectedDomain));
			fltr.add(eq("remSysRingId", click_type));
			fltr.add(eq("locSysRingId", click_type));
			fltr.add(ne("remSysIfName", "NA"));
			filter = and(fltr);
		}

		else if (selected_topology_type.equals("by_region")) {
			fltr.add(ne("remSysName", "End Point"));
			fltr.add(eq("domain", selectedDomain));
			fltr.add(eq("remSysDomain", selectedDomain));
			fltr.add(eq("location", click_type));
//			fltr.add(eq("remSysLocation", click_type));
			fltr.add(ne("remSysIfName", "NA"));
			filter = and(fltr);
		} else if (selected_topology_type.equals("vendor")) {
			fltr.add(ne("remSysName", "End Point"));
			fltr.add(eq("domain", selectedDomain));
			fltr.add(eq("remSysDomain", selectedDomain));
//fltr.add(eq("remSysVendor",key));
			fltr.add(eq("vendor", click_type));
			fltr.add(ne("remSysIfName", "NA"));
			filter = and(fltr);
		}

		else if (selected_topology_type.equals("active_alarms")) {
//creating the filters for active alarms
			BasicDBObject index_alarm = null;
			index_alarm = new BasicDBObject("$hint",
					"ALARM_ID_1");
			MongoCollection<Document> collection = database.getCollection("activealarms");
			Map<String, Object> groupMap = new HashMap<String, Object>();
			groupMap.put("NENAME", "$NENAME");

			DBObject groupFields = new BasicDBObject(groupMap);
			ArrayList<Document> iterDo = collection
					.aggregate(Arrays.asList(match(eq("DOMAIN", selectedDomain)), group(groupFields))).hint(index_alarm)
					.into(new ArrayList<Document>());
			JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));

			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject jsonObject1 = jsonArray.getJSONObject(i);
				JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
				String active_alarms_element = jsonObject_id.optString("NENAME");

				active_alarms_elements.add(active_alarms_element);
			}
//System.out.println(active_alarms_elements);
			ArrayList<Bson> fltr_or = new ArrayList<Bson>();
			for (int i = 0; i < active_alarms_elements.size(); i++) {
				fltr_or.add(eq("locSysName", active_alarms_elements.get(i)));
				fltr_or.add(eq("remSysName", active_alarms_elements.get(i)));
			}
			fltr.add(or(fltr_or));
			fltr.add(ne("remSysName", "End Point"));
			fltr.add(eq("domain", selectedDomain));
			fltr.add(eq("remSysDomain", selectedDomain));
			fltr.add(ne("remSysIfName", "NA"));
			filter = and(fltr);
			
	
		}		try {
			MongoCollection<Document> collection = database.getCollection(tablename_connectivitydetails);
			// System.out.println("filter===="+filter);
//	        FindIterable<Document> documents = collection.find();
			FindIterable<Document> documents = collection.find(filter).hint(index_details);

			for (Document doc : documents) {

//	        	System.out.println(doc);
				String locSysName = trimValue(doc.getString("locSysName"));
				String remSysName = trimValue(doc.getString("remSysName"));
				String vendor = trimValue(doc.getString("vendor"));
				String domain = trimValue(doc.getString("domain"));
				String ipaddress = trimValue(doc.getString("ipaddress"));
				String ipaddressNatted = trimValue(doc.getString("ipaddressNatted"));
				String remSysIfIndex = trimValue(doc.getString("remSysIfIndex"));
				String remSysIpaddress = trimValue(doc.getString("remSysIpaddress"));
				String locRemMediaType = trimValue(doc.getString("locRemMediaType"));
				String locRemPhysicalAddress = trimValue(doc.getString("locRemPhysicalAddress"));
				String location = trimValue(doc.getString("location"));
				String remSysDomain = trimValue(doc.getString("remSysDomain"));
				String remSysIfName = trimValue(doc.getString("remSysIfName"));
				String remSysIfType = trimValue(doc.getString("remSysIfType"));
				String remSysLocation = trimValue(doc.getString("remSysLocation"));
				String remSysNattedIpaddress = trimValue(doc.getString("remSysNattedIpaddress"));
				String remSysRingId = trimValue(doc.getString("remSysRingId"));
				String remSysVendor = trimValue(doc.getString("remSysVendor"));
				String locSysRingId = trimValue(doc.getString("locSysRingId"));
				// Add nodes
				if (selectedDomain.equals("Ipran")) {
					if (!locSysName.isEmpty()) {
						JSONObject node = new JSONObject();
						node.put("id", locSysName);
//	            	    node.put("label", vendor);
						node.put("vendor", vendor);
						node.put("domain", domain);
						node.put("ipaddress", ipaddress);
						node.put("ipaddressNatted", ipaddressNatted);
//	            	    node.put("remSysIfIndex", remSysIfIndex);
//	            	    node.put("remSysIpaddress", remSysIpaddress);
//	            	    node.put("locRemMediaType", locRemMediaType);
//	            	    node.put("locRemPhysicalAddress", locRemPhysicalAddress);
//	            	    node.put("remSysDomain", remSysDomain);
						node.put("location", location);
						node.put("remSysIfName", remSysIfName);
	            	    node.put("remSysIfType", remSysIfType);
						node.put("remSysLocation", remSysLocation);
//	            	    node.put("remSysNattedIpaddress", remSysNattedIpaddress);
						node.put("remSysRingId", remSysRingId);
//	            	    node.put("remSysVendor", remSysVendor);
//	            	    node.put("locSysRingId", locSysRingId);
//	            	    System.out.println(remSysName+"====="+locSysName);
						// Add edges
						// Create a Set to track existing edges for quick lookup (source-target pair)
						Set<String> edgeSet = new HashSet<>();
						for (Map<String, String> edge : edges) {
							String edgeKey = edge.get("source") + "->" + edge.get("target");
							edgeSet.add(edgeKey);
						}

						if (!locSysName.isEmpty() && !remSysName.isEmpty() && !locSysName.equals(remSysName)) {
							// Create the edge for the local system name to remote system name
							String edgeKey = locSysName + "->" + remSysName;

							if (!edgeSet.contains(edgeKey)) {
								Map<String, String> firstEdge = new HashMap<>();
								firstEdge.put("source", locSysName);
								firstEdge.put("target", remSysName);
								edges.add(firstEdge);
								edgeSet.add(edgeKey); // Add the edge to the set for future lookups

								// Add local system to nodes if it does not already exist
								nodes.putIfAbsent(locSysName, node);
							}

							// Check if there's already an edge from remote system to local system
							String reverseEdgeKey = remSysName + "->" + locSysName;
							if (!edgeSet.contains(reverseEdgeKey)) {
								JSONObject remNode = new JSONObject();
								remNode.put("id", remSysName);
								remNode.put("vendor", remSysVendor);
								remNode.put("domain", remSysDomain);
								remNode.put("ipaddress", remSysIpaddress);
								remNode.put("ipaddressNatted", remSysNattedIpaddress);
								remNode.put("location", remSysLocation);
								remNode.put("remSysLocation", location);
								remNode.put("remSysIfName", remSysIfName);
								remNode.put("remSysIfType", remSysIfType);
								remNode.put("remSysRingId", remSysRingId);

								// Add reverse edge and remote system to nodes if not present
								Map<String, String> reverseEdge = new HashMap<>();
								reverseEdge.put("source", remSysName);
								reverseEdge.put("target", locSysName);
								edges.add(reverseEdge);
								edgeSet.add(reverseEdgeKey); // Add reverse edge to the set

								nodes.putIfAbsent(remSysName, remNode);
							}

//						    System.out.println("---nodes-->>> "+nodes+"---edges-->>> "+edges+"---edgeSet-->>> "+edgeSet);
						}

						/*
						 * if (!locSysName.isEmpty() && !remSysName.isEmpty() &&
						 * !locSysName.equals(remSysName)) { Map<String, String> firstEdge = new
						 * HashMap<>(); firstEdge.put("source", locSysName); firstEdge.put("target",
						 * remSysName); edges.add(firstEdge); if (!nodes.containsKey(locSysName)) {
						 * nodes.put(locSysName, node);
						 * 
						 * }
						 * 
						 * boolean tar_exists = false; // System.out.println("1-edges-->"+edges); for
						 * (Map<String, String> edge : edges)
						 * 
						 * {
						 * 
						 * if (edge.get("source").equals(remSysName)) { tar_exists = true; break; } }
						 * 
						 * if (!tar_exists) {
						 * 
						 * JSONObject remNode = new JSONObject(); remNode.put("id", remSysName);
						 * remNode.put("vendor", remSysVendor); remNode.put("domain", remSysDomain);
						 * remNode.put("ipaddress", remSysIpaddress); remNode.put("ipaddressNatted",
						 * remSysNattedIpaddress); remNode.put("location", remSysLocation);
						 * remNode.put("remSysLocation", location); remNode.put("remSysIfName",
						 * remSysIfName); node.put("remSysRingId", remSysRingId); Map<String, String>
						 * edge = new HashMap<>();
						 * 
						 * edge.put("source", remSysName); edge.put("target", locSysName);
						 * edges.add(edge);
						 * 
						 * if (!nodes.containsKey(remSysName)) { nodes.put(remSysName, remNode);
						 * 
						 * }
						 * 
						 * } else {
						 * 
						 * Map<String, String> edge = new HashMap<>();
						 * 
						 * edge.put("source", locSysName); edge.put("target", remSysName);
						 * 
						 * edges.add(edge);
						 * 
						 * if (!nodes.containsKey(locSysName)) { nodes.put(locSysName, node); } }
						 * 
						 * }
						 */

						/*
						 * if (!locSysName.isEmpty() && !remSysName.isEmpty() &&
						 * !locSysName.equals(remSysName)) { // boolean tar_exists = edges.stream() //
						 * .anyMatch(edge -> edge.get("source").equals(remSysName)); boolean tar_exists
						 * = false; System.out.println(edges); for (Map<String, String> edge : edges)
						 * 
						 * {
						 * 
						 * if (edge.get("source").equals(remSysName)) { tar_exists = true; break; }
						 * System.out.println("1--->"+tar_exists); }
						 * 
						 * if (!tar_exists) { Map<String, String> remnode = new HashMap<>();
						 * node.put("id", remSysName); node.put("vendor", remSysVendor);
						 * node.put("domain", remSysDomain); node.put("ipaddress", remSysIpaddress);
						 * node.put("ipaddressNatted", remSysNattedIpaddress); node.put("location",
						 * remSysLocation); node.put("remSysLocation", location);
						 * node.put("remSysIfName", remSysIfName);
						 * 
						 * // Convert Map<String, String> to JSONObject JSONObject jsonNode = new
						 * JSONObject(remnode);
						 * 
						 * // Add to nodes if not already present if (!nodes.containsKey(remSysName)) {
						 * nodes.put(remSysName, jsonNode); }
						 * 
						 * Map<String, String> edge = new HashMap<>(); edge.put("source", remSysName);
						 * edge.put("target", locSysName);
						 * 
						 * if (!edges.stream().anyMatch(e -> e.get("source").equals(edge.get("source"))
						 * && e.get("target").equals(edge.get("target")))) { edges.add(edge); } } else {
						 * Map<String, String> edge = new HashMap<>(); edge.put("source", locSysName);
						 * edge.put("target", remSysName);
						 * 
						 * if (!edges.stream().anyMatch(e -> e.get("source").equals(edge.get("source"))
						 * && e.get("target").equals(edge.get("target")))) { edges.add(edge); }
						 * 
						 * Map<String, String> locnode = new HashMap<>(); node.put("id", locSysName);
						 * 
						 * // Convert Map<String, String> to JSONObject JSONObject jsonNode = new
						 * JSONObject(locnode);
						 * 
						 * // Add to nodes if not already present if (!nodes.containsKey(locSysName)) {
						 * nodes.put(locSysName, jsonNode); } } }
						 */

					}

//		            }
				} else if (selectedDomain.equals("Mpbn")) {

					if (!locSysName.isEmpty()) {
						JSONObject node = new JSONObject();
						node.put("id", locSysName);
//	            	    node.put("label", vendor);
						node.put("vendor", vendor);
						node.put("domain", domain);
						node.put("ipaddress", ipaddress);
						node.put("ipaddressNatted", ipaddressNatted);
//	            	    node.put("remSysIfIndex", remSysIfIndex);
//	            	    node.put("remSysIpaddress", remSysIpaddress);
//	            	    node.put("locRemMediaType", locRemMediaType);
//	            	    node.put("locRemPhysicalAddress", locRemPhysicalAddress);
//	            	    node.put("remSysDomain", remSysDomain);
						node.put("location", location);
						node.put("remSysIfName", remSysIfName);
	            	    node.put("remSysIfType", remSysIfType);
						node.put("remSysLocation", remSysLocation);
//	            	    node.put("remSysNattedIpaddress", remSysNattedIpaddress);
//	            	    node.put("remSysRingId", remSysRingId);
//	            	    node.put("remSysVendor", remSysVendor);
//	            	    node.put("locSysRingId", locSysRingId);
//	            	    System.out.println(remSysName+"====="+locSysName);
						// Add edges
						// Create a Set to track existing edges for quick lookup (source-target pair)
						Set<String> edgeSet = new HashSet<>();
						for (Map<String, String> edge : edges) {
							String edgeKey = edge.get("source") + "->" + edge.get("target");
							edgeSet.add(edgeKey);
						}

						if (!locSysName.isEmpty() && !remSysName.isEmpty() && !locSysName.equals(remSysName)) {
							// Create the edge for the local system name to remote system name
							String edgeKey = locSysName + "->" + remSysName;

							if (!edgeSet.contains(edgeKey)) {
								Map<String, String> firstEdge = new HashMap<>();
								firstEdge.put("source", locSysName);
								firstEdge.put("target", remSysName);
								edges.add(firstEdge);
								edgeSet.add(edgeKey); // Add the edge to the set for future lookups

								// Add local system to nodes if it does not already exist
								nodes.putIfAbsent(locSysName, node);
							}

							// Check if there's already an edge from remote system to local system
							String reverseEdgeKey = remSysName + "->" + locSysName;
							if (!edgeSet.contains(reverseEdgeKey)) {
								JSONObject remNode = new JSONObject();
								remNode.put("id", remSysName);
								remNode.put("vendor", remSysVendor);
								remNode.put("domain", remSysDomain);
								remNode.put("ipaddress", remSysIpaddress);
								remNode.put("ipaddressNatted", remSysNattedIpaddress);
								remNode.put("location", remSysLocation);
								remNode.put("remSysLocation", location);
								remNode.put("remSysIfName", remSysIfName);
								remNode.put("remSysIfType", remSysIfType);
								remNode.put("remSysRingId", remSysRingId);

								// Add reverse edge and remote system to nodes if not present
								Map<String, String> reverseEdge = new HashMap<>();
								reverseEdge.put("source", remSysName);
								reverseEdge.put("target", locSysName);
								edges.add(reverseEdge);
								edgeSet.add(reverseEdgeKey); // Add reverse edge to the set

								nodes.putIfAbsent(remSysName, remNode);
							}

//						    System.out.println("---nodes-->>> "+nodes+"---edges-->>> "+edges+"---edgeSet-->>> "+edgeSet);
						}

					}

//		            }

				}

			}
//	        }

			// Convert nodes and edges to JSON format
			JSONArray nodeElements = new JSONArray(nodes.values());
			JSONArray alarmNodeElements = new JSONArray(alarmNodes.values());
			JSONArray validEdges = new JSONArray();

			for (Map<String, String> edge : edges) {
				if (nodes.containsKey(edge.get("source")) && nodes.containsKey(edge.get("target"))) {
					validEdges.put(edge);
				}

			}

			JSONObject result = new JSONObject();
			result.put("nodes", nodeElements);
			result.put("edges", validEdges);
//	        result.put("activeAlarms", alarmNodeElements);

			output = result.toString();
		} catch (Exception e) {
			log.error("Error fetching data from MongoDB", e);
			// System.out.println(e);
		} finally {
			database_connection.close();
		}
		System.out.println("#####################################" + output);
		return output;

	}

	private String trimValue(String value) {
		return value != null ? value.trim() : "";
	}

	// Extract alarm data from Document
	private JSONObject extractAlarmData(Document doc) {
		String alarmId = trimValue(doc.getString("ALARM_ID"));
		if (alarmId.isEmpty())
			return null;

		JSONObject alarm = new JSONObject();
		alarm.put("VENDOR", trimValue(doc.getString("VENDOR")));
		alarm.put("DOMAIN", trimValue(doc.getString("DOMAIN")));
		alarm.put("MANAGED_OBJECT", trimValue(doc.getString("MANAGED_OBJECT")));
		alarm.put("ALARM_ID", alarmId);
		alarm.put("SEVERITY", trimValue(doc.getString("SEVERITY")));
		alarm.put("TIME", trimValue(doc.getString("TIME")));
		alarm.put("NENAME", trimValue(doc.getString("NENAME")));
		alarm.put("NEIP", trimValue(doc.getString("NEIP")));
		alarm.put("ALARMNAME", trimValue(doc.getString("ALARMNAME")));
		alarm.put("ALARM_INFO", trimValue(doc.getString("ALARM_INFO")));
		alarm.put("INSERTIONTIME", trimValue(doc.getString("INSERTIONTIME")));
		return alarm;
	}

	// Method to process active alarms
	private JSONArray getActiveAlarms(List<JSONObject> alarmNodes) {
		List<JSONObject> uniqueAlarms = new ArrayList<>();
		Map<String, JSONObject> alarmMap = new HashMap<>();

		for (JSONObject alarm : alarmNodes) {
			String alarmId = alarm.getString("ALARM_ID");
			if (!alarmId.isEmpty() && !alarmMap.containsKey(alarmId)) {
				alarmMap.put(alarmId, alarm);
			}
		}

		uniqueAlarms.addAll(alarmMap.values());
		return new JSONArray(uniqueAlarms);
	}

	// Service Stiching
	@Override
	public String service_stitching(String vlan) {

		if (log.isDebugEnabled()) {
			log.debug("*********** checked into new_topology ****************");
		}
		vlan = "786";
//change the table name 

//String tablename_connectivitydetails="topologydiscoveryscanconnectivity"; // table name which contains all topology details for ipran and mpbn
		String tablename_connectivitydetails = "new_service_stitch_ipran_nec";
		String tablename_element_details = "new_service_stitch_ipran_nec";
		BasicDBObject index_details = new BasicDBObject("$hint", "_id_");
		BasicDBObject element_index = new BasicDBObject("$hint", "_id_");

		ArrayList<String> active_alarms_elements = new ArrayList<String>();

		String output = "";
		Properties config = getProperties();
//picking the asset path from config file
		String asset_path = config.getProperty("assets_path");
		MongoClient database_connection = get_mongo_connection();

		MongoDatabase database = database_connection.getDatabase(config.getProperty("database.topology_database"));

		JSONObject for_series = new JSONObject();

		ArrayList<Bson> fltr = new ArrayList<Bson>();
		ArrayList<Bson> fltr_or1 = new ArrayList<Bson>();

		Bson filter = null;
//applying the filters

		fltr.add(eq("vlan", vlan));

		filter = and(fltr);

//first pick the nodes name from-->to
		try {
			MongoCollection<Document> collection = database.getCollection(tablename_connectivitydetails);
			MongoCollection<Document> collection_element_details = database.getCollection(tablename_element_details);
			Map<String, Object> groupMap = new HashMap<String, Object>();
			groupMap.put("from_sitename", "$from_sitename");
			groupMap.put("to_sitename", "$to_sitename");
			// groupMap.put("remSysIfName", "$remSysIfName");

			DBObject groupFields = new BasicDBObject(groupMap);
			ArrayList<Document> iterDo = collection.aggregate(Arrays.asList(match(filter), group(groupFields)))
					.hint(index_details).into(new ArrayList<Document>());
			JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
			JSONArray for_data = new JSONArray();
			JSONArray for_data_interface = new JSONArray();

			ArrayList<String> nodes = new ArrayList<String>();
			JSONArray nodes_insert = new JSONArray();
			JSONArray element_info_insert = new JSONArray();
			for (int i = 0; i < jsonArray.length(); i++) {
				ArrayList<String> data = new ArrayList<String>();
				JSONObject jsonObject1 = jsonArray.getJSONObject(i);
				JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
				String source = jsonObject_id.optString("from_sitename");
				String target = jsonObject_id.optString("to_sitename");
				// String interfaces=jsonObject_id.optString("remSysIfName");

				if (!source.equals(target)) {
					if (source.length() > 0 && target.length() > 0) {
						data.add(source);
						data.add(target);

						data.add("");
						nodes.add(source);
						nodes.add(target);

						// System.out.println(data);

						for_data.put(data);
						for_series.put("data", for_data);

					}
				}

			}

			// Picking the node names including the interface name and interface type(for
			// filter purpose) which put separatelt
			Map<String, Object> groupMap_interface = new HashMap<String, Object>();
			groupMap_interface.put("from_sitename", "$from_sitename");
			groupMap_interface.put("to_sitename", "$to_sitename");
			// groupMap_interface.put("remSysIfName", "$remSysIfName");
//	groupMap_interface.put("remSysIfType", "$remSysIfType");

			DBObject groupFields_interface = new BasicDBObject(groupMap_interface);
			ArrayList<Document> iterDo_interface = collection
					.aggregate(Arrays.asList(match(filter), group(groupFields_interface))).hint(index_details)
					.into(new ArrayList<Document>());
			JSONArray jsonArray_interface = new JSONArray(JSON.serialize(iterDo_interface));

			for (int i = 0; i < jsonArray_interface.length(); i++) {
				ArrayList<String> data_interface = new ArrayList<String>();
				JSONObject jsonObject1 = jsonArray_interface.getJSONObject(i);
				JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
				String source = jsonObject_id.optString("from_sitename");
				String target = jsonObject_id.optString("to_sitename");
				// String interfaces=jsonObject_id.optString("remSysIfName");
				// String interface_type=jsonObject_id.optString("remSysIfType");

				if (!source.equals(target)) {
					if (source.length() > 0 && target.length() > 0) {
						data_interface.add(source);
						data_interface.add(target);
						data_interface.add("");
						data_interface.add("");
						for_data_interface.put(data_interface);
						for_series.put("interface_data", for_data_interface);

					}
				}

				// else {}

			}

			Set<String> set = new HashSet<String>(nodes);
			nodes.clear();
			nodes.addAll(set);

			Map<String, Object> groupMap_node = new HashMap<String, Object>();
			groupMap_node.put("from_sitename", "$from_sitename");
			groupMap_node.put("from_port", "$from_port");
			groupMap_node.put("from_vendor", "$from_vendor");

			DBObject groupFields_node = new BasicDBObject(groupMap_node);

			ArrayList<Document> iterDo_node = collection_element_details
					.aggregate(Arrays.asList(match(eq("vlan", vlan)), group(groupFields_node))).hint(element_index)
					.into(new ArrayList<Document>());
			JSONArray jsonArray_node = new JSONArray(JSON.serialize(iterDo_node));
			String vendor_check = "";
			String node_name = "";

			for (int i = 0; i < jsonArray_node.length(); i++) {
				JSONObject for_nodes = new JSONObject();
				JSONObject info_element = new JSONObject();
				JSONObject marker = new JSONObject();

				JSONObject jsonObject1 = jsonArray_node.getJSONObject(i);
				JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");

				node_name = jsonObject_id.optString("from_sitename");

				vendor_check = jsonObject_id.optString("from_vendor");
				String phy_ipaddress = jsonObject_id.optString("from_port");
				String nat_ipaddress = "---natip----";
				String location = "";
				String ring = "";

				location = "location";
				ring = "ring";

				if (nodes.contains(node_name)) {
					for_nodes.put("id", node_name);
					for_nodes.put("vendor", vendor_check);
					for_nodes.put("ph_address", phy_ipaddress);
					for_nodes.put("nat_address", nat_ipaddress);
					for_nodes.put("location", location);

					try {
						if (jsonArray_node.length() != 0) {
							for_nodes.put("className", node_name);
						}

						// if(domain.equals("Mpbn")) {
						marker.put("symbol", "square");

//	}
						/*
						 * else {
						 * 
						 * ////System.out.println("===>"+vendor_check);
						 * if(vendor_check.toUpperCase().equals("NOKIA")) {} else
						 * if(vendor_check.toUpperCase().equals("HUAWEI")) {
						 * if(node_name.contains("CX")) {
						 * marker.put("symbol","url("+asset_path+"/images/IPRAN_"+vendor_check.
						 * toUpperCase()+"_CX.png)");
						 * 
						 * }
						 * 
						 * else { marker.put("symbol","url("+asset_path+"/images/IPRAN_"+vendor_check.
						 * toUpperCase()+"_ATN.png)"); } } }
						 */
						nodes_insert.put(for_nodes);

					} catch (Exception e) {
						log.error("Exception occurs:---" + e.getMessage(), e);
						e.printStackTrace();
					}

					if (jsonArray_node.length() != 0) {
						// for_nodes.put("marker", marker);
					}
				}

				/*
				 * info_element.put("id",node_name); info_element.put("vendor", vendor_check);
				 * info_element.put("ph_address", phy_ipaddress);
				 * info_element.put("nat_address", nat_ipaddress); info_element.put("location",
				 * location);
				 * 
				 * element_info_insert.put(info_element);
				 */
			}
			/*
			 * Map<String, Object> groupMap_conn_type = new HashMap<String, Object>();
			 * groupMap_conn_type.put("remSysIfType", "$remSysIfType");
			 * groupMap_conn_type.put("locSysName", "$locSysName");
			 * groupMap_conn_type.put("remSysName", "$remSysName");
			 * 
			 * DBObject groupFields_conn_type = new BasicDBObject(groupMap_conn_type);
			 * ArrayList<Document> iterDo_conn_type =
			 * collection.aggregate(Arrays.asList(match(eq("domain",vlan)),group
			 * (groupFields_conn_type))).hint(index_details).into(new
			 * ArrayList<Document>()); JSONArray jsonArray_conn_type = new
			 * JSONArray(JSON.serialize(iterDo_conn_type));
			 * 
			 * 
			 * JSONArray connectivity_type = new JSONArray();
			 * 
			 * 
			 * for(int i=0;i<jsonArray_conn_type.length();i++){ JSONObject conn_type = new
			 * JSONObject(); JSONObject jsonObject1 = jsonArray_conn_type.getJSONObject(i);
			 * JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
			 * 
			 * 
			 * String type=jsonObject_id.optString("remSysIfType");
			 * 
			 * conn_type.put("con_type",type); connectivity_type.put(conn_type); }
			 */

			//// //System.out.println(missing_for_vendor);

			//// //System.out.println(missing_for_vendor);
			// for_series.put("connectivity_type", connectivity_type);
			// for_series.put("element_information", element_info_insert);

			for_series.put("nodes", nodes_insert);
		} catch (JSONException e) {
			log.error("Exception occurs:---" + e.getMessage(), e);
			e.printStackTrace();
		}

		output = for_series.toString();
		// System.out.println("done");
		return output;

	}

	@Override
	public String new_topology_vis(String domain, String elementname) {
		JSONObject to_send = new JSONObject();

		String element = elementname;
		try {
			String tablename_connectivitydetails = "topologydiscoveryscanconnectivity";
			String tablename_element_details = "topologydiscoveryscandetails";
			BasicDBObject index_details = new BasicDBObject("$hint", "_id_");
			BasicDBObject element_index = new BasicDBObject("$hint", "_id_");

			String output = "";
			Properties config = getProperties();

			MongoClient database_connection = get_mongo_connection();

			MongoDatabase database = database_connection.getDatabase(config.getProperty("database.topology_database"));

			ArrayList<Bson> fltr = new ArrayList<Bson>();

			ArrayList<Bson> fltr_ele = new ArrayList<Bson>();

			MongoCollection<Document> collection = database.getCollection(tablename_connectivitydetails);
			/*
			 * DistinctIterable<String> neighbours=collection.distinct("remSysName",
			 * and(eq("locSysName", element),ne("remSysName","End Point")),String.class);
			 * 
			 * 
			 * try { for(String a:neighbours) { //System.out.println(a);
			 * fltr_ele.add(eq("locSysName", a));
			 * 
			 * 
			 * } } catch (Exception e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */
			// fltr.add(or(fltr_ele));
			fltr.add(or(eq("locSysName", element)));
			fltr.add(eq("domain", domain));

			// fltr.add(eq("locSysName", "NTC_M6K-8S-P_CR02"));

			// System.out.println(fltr);

			Bson filter = null;
			filter = and(fltr);

			MongoCollection<Document> collection_element_details = database.getCollection(tablename_element_details);

			ArrayList<String> nodes = new ArrayList<String>();
			Map<String, Object> groupMap = new HashMap<String, Object>();
			groupMap.put("locSysName", "$locSysName");
			groupMap.put("remSysName", "$remSysName");
			groupMap.put("vendor", "$vendor");
			groupMap.put("remSysVendor", "$remSysVendor");
			JSONArray edges_insert = new JSONArray();
			DBObject groupFields = new BasicDBObject(groupMap);
			ArrayList<Document> iterDo = collection.aggregate(Arrays.asList(match(filter), group(groupFields)))
					.hint(index_details).into(new ArrayList<Document>());
			JSONArray jsonArray = new JSONArray(JSON.serialize(iterDo));
			ArrayList<String> filter_nodes = new ArrayList<String>();
			for (int i = 0; i < jsonArray.length(); i++) {
				ArrayList<String> data = new ArrayList<String>();
				JSONObject jsonObject1 = jsonArray.getJSONObject(i);
				JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");
				String source = jsonObject_id.optString("locSysName").trim();
				String target = jsonObject_id.optString("remSysName".trim());
				String vendor = jsonObject_id.optString("vendor").trim();
				String target_vendor = jsonObject_id.optString("remSysVendor").trim();
//System.out.println(source+"=="+target+"==="+vendor);
				// if(!interfaces.equals("NA")) {
				if (!source.equals("End Point") && !target.equals("End Point")) { // ignoring End Points

					if (!target.contains("ATN") && !target.contains("CX600") && domain.equals("Mpbn")) { // Picking only
																											// the
																											// MPBN
																											// Nodes
						if (!source.equals(target)) {
							if (source.length() > 0 && target.length() > 0) {

								JSONObject for_edges = new JSONObject();

								if (!filter_nodes.contains(target + "===" + source)) {
									// System.out.println(source + "===" + target);
									filter_nodes.add(source + "===" + target);
									for_edges.put("from", source);
									for_edges.put("to", target);
									for_edges.put("title", source + "=====" + target);

									edges_insert.put(for_edges);
								}

								if (!nodes.contains(source + "==" + vendor)) {
									nodes.add(source + "==" + vendor);
								}

								if (!nodes.contains(target + "==" + target_vendor)) {
									nodes.add(target + "==" + target_vendor);
								}

								// System.out.println(edges_insert.length());

							}
						}
					}
					// Picking only the IPRAN Nodes
					else {
						if (!source.equals(target)) {
							if (source.length() > 0 && target.length() > 0) {

								JSONObject for_edges = new JSONObject();

								if (!filter_nodes.contains(target + "===" + source)) {
									// System.out.println(source + "===" + target);
									filter_nodes.add(source + "===" + target);
									for_edges.put("from", source);
									for_edges.put("to", target);
									for_edges.put("title", source + "=====" + target);

									edges_insert.put(for_edges);
								}

								// System.out.println(edges_insert.length());

							}
						}
					}
				}
				// }
			}

			Set<String> set = new HashSet<String>(nodes);
			nodes.clear();
			nodes.addAll(set);

			JSONArray nodes_insert = new JSONArray();
			for (String node : nodes) {

				// System.out.println("===>"+node);
				JSONObject for_nodes = new JSONObject();
				for_nodes.put("id", node.split("==")[0]);
				for_nodes.put("label", node.split("==")[0]);
				for_nodes.put("title", node.split("==")[0]);

				if (node.split("==")[0].equalsIgnoreCase(element)) {
					for_nodes.put("color", "green");
				}

				else {
					for_nodes.put("color", "black");
				}

				nodes_insert.put(for_nodes);
			}
//	//System.out.println(nodes_insert);
			// //System.out.println(edges_insert);

			to_send.put("nodes", nodes_insert.toString());
			to_send.put("edges", edges_insert.toString());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "" + to_send;
	}

}