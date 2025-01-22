package org.xplorg.tool.telco360.DAO.interfaces;

import java.util.List;

import org.xplorg.tool.telco360.entity.GenericPostBody;
import org.xplorg.tool.telco360.entity.alarm_getr_setr;

public interface TroubleTicketDAO {

//------------Generic-----------------------------------------------------------------------------
public String getTableColsValsTroubleTicketGeneric(String tableName);
	
public String getTableColsValsTroubleTicketConditionGeneric(String tableName,String condition);

public String getTableSpecificColsValsTroubleTicketConditionGeneric(String tableName,String columns,String condition);

//------------------------------------trouble ticket----------------------------------------------//
public String get_trouble_ticket_column_data(String table_name,String area,String node,String severity,String status) ;

public int Email(GenericPostBody genericPostBody);	

public int ManualTroubleTicket(GenericPostBody genericPostBody);

public int TroubleTicket(GenericPostBody genericPostBody);	

public int TroubleTicketGeneration(GenericPostBody genericPostBody);	

public int TroubleTicketGenerationKpi(GenericPostBody genericPostBody);	

public List<alarm_getr_setr> troubleTicketEndUser();

public int postTroubleTicketEndUser(GenericPostBody genericPostBody);	

public int postSmeDetails(GenericPostBody genericPostBody);

public int postSmeDetailsDelete(GenericPostBody genericPostBody);

public int postTroubleTicketLevels(GenericPostBody genericPostBody);

public int postCredentialDetails(GenericPostBody genericPostBody);

public int postCredentialsDelete(GenericPostBody genericPostBody);

public int postPerformanceSchedulerUpdate(GenericPostBody genericPostBody);

public int postPerformanceSchedulerDelete(GenericPostBody genericPostBody);

public int EmailReport(GenericPostBody genericPostBody);
}
