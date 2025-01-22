package org.xplorg.tool.telco360.DAO.interfaces;

import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;
import org.xplorg.tool.telco360.config.WebSocketService;
import org.xplorg.tool.telco360.entity.topology_gis_getter_setter;

public interface TopologyDAO {
	
public String getUserlogDetails(String tableName,String columns,String conditions,String orderby);	
	
public int postUserSubscription(String data,WebSocketService webSocketService);	

public int postUserDeSubscription(String data);	
			
public ArrayList<topology_gis_getter_setter> getGisRegionDetails(String tableName);	
	
public ArrayList<topology_gis_getter_setter> getGisElementDetails(String elementName);	

public String getAlarmTableData(String tableName,String severity);

public String getAlarmHistoryTableData(String tableName,String severity,String orderby);

public String getAlarmListAndSiteName(String tableName);

public String getGisGenericInfo(String tableName,String columnName);

public String getAlarmTableDataZte(String tableName,String columns,String conditions,String orderby);

public String getTableDataGeneric(String tableName,String columns,String conditions);

public String getTableDataOnElementGeneric(String tableName,String columns,String conditions,String order_by);

public String getTableDataOnElementGenericSpecificDomain(String tableName,String columns,String conditions,String order_by,String specificColumn,String specificColNamename);

public String getTableDataOnElementGenericSpecificDomainWOO(String tableName,String columns,String conditions,String specificColumn,String specificColNamename);

public String getTableDataOnElementGenericWOO(String tableName,String columns,String conditions);

public String getTableDataGenericResolution(String tableName,String columns,String conditions,String AlarmId);

public String getTableColumnsGeneric(String tableName,String columns,String conditions);

public String getTableColsValsGeneric(String tableName);

public int postDataGeneric(String data);

public int updateDataGeneric(String data);

public int deleteDataGeneric(String data);

public int uploadExcelDataGeneric(MultipartFile file,String tableName);

public int uploadCsvDataMismatchedElements(MultipartFile file);
}
