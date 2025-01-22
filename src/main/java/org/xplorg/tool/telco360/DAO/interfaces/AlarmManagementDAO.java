package org.xplorg.tool.telco360.DAO.interfaces;

import java.util.ArrayList;

import org.xplorg.tool.telco360.entity.GenericPostBody;

public interface AlarmManagementDAO {

public String getAlarmsDescription(String tableName,String columns,String conditions);	
	
public int postDataAlarmFilter(GenericPostBody genericPostBody);	

public int postActiveAlarmsOnWindowStatusChange(String genericPostBody);

public int postAcknowledgeAlarms(GenericPostBody genericPostBody);

public String getActiveAlarmsOnWindow_clear(String userId,ArrayList<String> vendorlist,ArrayList<String> domainlist,ArrayList<String> severitylist,String alarmids,String alarmnames,String nename,ArrayList<String> fieldlist,ArrayList<String>alarmnamelist );

public int postClearAlarms(GenericPostBody genericPostBody);

public int postCommentAlarms(GenericPostBody genericPostBody);

public int postSetCommandAlarms(GenericPostBody genericPostBody);

public String getAlarmsCount(String vendor,String domain,String protocol);

public String getActiveAlarmsOnWindow(String userId,ArrayList<String> vendorlist,ArrayList<String> domainlist,ArrayList<String> severitylist,String alarmids,String alarmnames,String nename,ArrayList<String> fieldlist,ArrayList<String> filterlist, ArrayList<String>alarmnamelist);

public String getHistoryAlarmsOnWindow(String tableName,String columns,String conditions,String orderby);

public String getActiveAlarmsOnElementsFiltered(String userId,String domain,String vendor,String neipaddress,String nename);

public String getActiveAlarmsOnElements(String tableName,String columns,String conditions,String orderby);

public String getUtilizationTable(String database_name,String tableName,String columns,String conditions,String orderby,String domain,String vendor,String type);

public String getActiveAlarmsOnElementsWhiteList(String domain,String tableName,String columns,String conditions,String orderby,String alarm_type);

//public int getAlarmReportEricsson(String save_report);
}
