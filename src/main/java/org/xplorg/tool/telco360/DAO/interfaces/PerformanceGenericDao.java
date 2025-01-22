package org.xplorg.tool.telco360.DAO.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.xplorg.tool.telco360.entity.check_g_s_parent;
import org.xplorg.tool.telco360.entity.performance_nokia_radio_element_blink_main;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_1_date_format;

public interface PerformanceGenericDao {
	
	
	
//tree for all the performance data	
public List<check_g_s_parent> get_tree_kpi_creation(String opco);	


//to get table and column corresponding to each vendor
public String getTableSpecificColsValsConditionGeneric(int id,String opco,String domain ,String vendor,String tableName,String columns,String condition,String tabletype);


//to insert kpi from create kpi feature
public int insert_kpi(String opco,String admin_id,String domain,String vendor,String kpiname,String groups,String kpi_formula,String formula,String threshold,String topology,String direction,String element_name,String calc,String trouble_ticket,String severity);


	
//----get kpi names created by user
public ArrayList<String> getKpiNameExcel(String opco,String admin_id,String domain,String vendor_name,String element_name);




// to insert kpi which is inserted during the creation of excel report
public int insert_user_specific_kpi(String opco,String admin_id, String event_name, ArrayList<String> kpi_name, String group,
String actual_formula, String formula, String threshold, String topology, String rate, String domain,
String element, String calculation,String vendor,ArrayList<String> element_list,String interface_select);



//-----to get (Existing reports) report names for kpis
public ArrayList<String> kpi_report_group_name(String opco,String admin_id,String element_name,String vendor_name,String domain_name);


//----to get kpi related to report name(Existing reports)----
public ArrayList<String> report_related_kpi(String opco,String admin_id,String report_name,String element_name,String vendor_name,String domain_name);



//---- excel report----
public int excel_report(String admin_id,String report_name,String report_type,String report_interval,String start_date,String end_date,String start_time,String end_time,String domain,String vendor,String element,String save_report,String single_multiple,String mail) ;




//element blink---

public ArrayList<performance_nokia_radio_element_blink_main>element_blink(String opco,String admin_id,String domain,String vendor);


//update formula table

public int updateKpiTable(String opco,String admin_id,String domain, String vendor,String kpiName, String groupName, String formula, String rate,String threshold, String topology, String severity, String troubleticket,String tablename) ;



//---- get any list----
public ArrayList<String>get_any_list(String opco,String admin_id,String domain,String vendor,String element,String type) ;

//create multiple sheet

public int create_multiple_sheet(String opco,String admin_id,String domain, String vendor,String report_name, ArrayList<String>sheets) ;



//insert schdule report
public int insert_schdule_report(String data);

//-----get Interface Traffic------------------------

public String getInterfaceTraffic(String domain,String vendor,String tableName,String columns,String conditions);


//to upload and export kpis

public int import_export_kpi(MultipartFile file,String data);



public ArrayList<String> get_distinct_list(String opco,String admin_id,String domain,String vendor,String type,String where1,String where2,String where3,String where4);


//to insert kpi from create kpi feature
public int insert_create_multiple_graphs(String admin_id,String graph_name,String graph_count,String domain,String vendor,String elementname,String ip,String interfacee,ArrayList<String>kpi_list,String graph_type,String sla_threshold);


	
//to insert kpi from create kpi feature
public String get_multiple_graphs(String opco,String admin_id,String group);




public ArrayList<String> get_any_list(String opco,String admin_id,String domain,String vendor,String type,String check1,String check2,String check3,String check4);



public dual_axis_1_date_format get_ip_graph(String opco,String admin_id,String vendor,String domain,ArrayList<String> kpi_name, ArrayList<String> ne_name,ArrayList<String> filter1,ArrayList<String> filter2,String duration,String start_date,String end_date,String starttime,String endtime,String apn,ArrayList<String> check_axis,String type,String graph_type,String sla);



public String cause_graph(String protocol);


public String click_on_graph(String protocol,String barname,String item_clicked);


public String cause_right_click(String protocol);

public  ArrayList<String>  causes();

public String test(String check1);


	

}


