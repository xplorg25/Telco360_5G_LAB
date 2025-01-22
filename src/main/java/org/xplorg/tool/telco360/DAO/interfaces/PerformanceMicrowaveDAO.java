package org.xplorg.tool.telco360.DAO.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_1;
import org.xplorg.tool.telco360.tree.entity.check_g_s_parent_micro;

public interface PerformanceMicrowaveDAO {
// to get parameter and value
public ArrayList<String> get_parameter_value(String opco,String admin_id,String domain,String vendor,String elementname,String table,String slot,ArrayList<String>notcontains);




//show the graph for zte pc kpis
public dual_axis_1 get_microwave_graph(String opco,String admin_id,String vendor,String domain,ArrayList<String> kpi_name, ArrayList<String> ne_name, ArrayList<String> key,String reportname,String duration,String start_date,String end_date,String starttime,String endtime,ArrayList<String> check_axis,String type);





//show the graph for zte pc kpis
public dual_axis_1 get_nce_microwave_graph(String opco,String admin_id,String vendor,String domain,ArrayList<String> kpi_name, ArrayList<String> ne_name, ArrayList<String> key,ArrayList<String> conditions,String duration,String start_date,String end_date,String starttime,String endtime,ArrayList<String> check_axis,String type);



////tree for all the performance data	
public List<check_g_s_parent_micro> get_tree_microwave(String opco,String admin_id,String domain,String vendor,String report,String element_name ,String type,String kpi_name);	



//to get table and column corresponding to each vendor for inventory
public String getTableSpecificColsValsConditionGenericInventory(String opco,String admin_id,String domain ,String vendor,String start_date,String end_date,String report_name,ArrayList<String> columns,ArrayList<String> conditions);



//to get consolidate excel visibility report
public int excel_visibility_report(String opco,String admin_id,String domain ,String vendor,String week);


//to get table and column corresponding to each vendor for inventory
public String vendor_nodes_details(String opco,String admin_id,String domain );


//to get table and column corresponding to each vendor for inventory
public String gis_details(String opco,String admin_id,String domain );

//to get command according to vendor and domain
public ArrayList<String> get_command(String opco,String admin_id,String domain,String vendor );


}
