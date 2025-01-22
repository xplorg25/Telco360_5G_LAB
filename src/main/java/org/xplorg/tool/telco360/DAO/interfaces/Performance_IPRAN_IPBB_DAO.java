package org.xplorg.tool.telco360.DAO.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.xplorg.tool.telco360.entity.tree_parents_g_s;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_1_date_format;

public interface Performance_IPRAN_IPBB_DAO {
	
	
// to get (local system) element_name corresponding to IPRAN and IPBB
public ArrayList<String> get_br_local_information(String opco,String admin_id,String domain,String vendor,String type,String report_name);


//---to get interface name for corresponding (local system) element_name
public ArrayList<String> get_br_interface_name(String opco,String admin_id,String domain,String vendor,String device_name,String ip);


//-----to get ipname of perticular local system--ipbb--
public ArrayList<String> get_ipbb_iplist(String opco,String admin_id,String domain,String vendor,String element_name,String sla);


//-----get kpi tree for apran and ipbn---

public List<tree_parents_g_s> get_br_kpi_name(String opco,String admin_id,String domain,String vendor,String type,String device_name,String element_key);


//-----get graph of ipran and ipbb---------------

//show the graph for zte pc kpis


public dual_axis_1_date_format get_ip_graph(String opco,String admin_id,String vendor,String domain,ArrayList<String> kpi_name, ArrayList<String> ne_name,ArrayList<String> filter1,ArrayList<String> filter2,String duration,String start_date,String end_date,String starttime,String endtime,String apn,ArrayList<String> check_axis,String type,String graph_type);





//FOR IP PING AUDIT==============

public String ip_audit_structure(String opco,String vendor,String topology_type,String ring_id,String click_type);

//to get (local system) element_name corresponding to IPRAN and IPBB
public ArrayList<String> get_failed_ping_elements(String opco,String admin_id,String domain,String vendor);

//to gte ssh command output=======

public ArrayList<String> ipaudit_ssh_output(String opco,String domain,String vendor,String element_address,String command);

public ArrayList<String> configuration_output();	

//to get cpu & memory utilization into gauge
public String cpu_memory_gauge(String opco,String domain,String vendor,String type,String ip,String element_name);


//FOR MPBN TOPOLOGY==============

public String new_topology(String opco,String domain,String vendor,String topology_type,String key,String click_type);


//BY UTKARSH
public String topology(String selectedOpco,String selectedDomain, String selectedVendor, String selected_topology_type, String key,
		String click_type);

public String new_topology_vis(String domain,String elementname);

public String service_stitching(String vlan);


}
