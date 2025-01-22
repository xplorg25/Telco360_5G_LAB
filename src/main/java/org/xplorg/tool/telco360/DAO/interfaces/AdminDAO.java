package org.xplorg.tool.telco360.DAO.interfaces;

import org.xplorg.tool.telco360.entity.GenericPostBody;

public interface AdminDAO{

public String adminLogin(String emailId , String password);

public int createUser(GenericPostBody genericPostBody);

public int adminLogout(String emailId);

public String getTableSpecificColsValsConditionGeneric(String tableName,String columns,String condition);
	
public int postUserDetailsDelete(GenericPostBody genericPostBody);

public int postUserDetailsUpdate(GenericPostBody genericPostBody);

public int postUserPermissionsUpdate(GenericPostBody genericPostBody);

public int postChangePasswordUpdate(GenericPostBody genericPostBody);
}
