package org.xplorg.tool.telco360.DAO.interfaces;

import org.xplorg.tool.telco360.entity.GenericPostBody;

public interface TokenDAO {
	
	public int createUser(GenericPostBody genericPostBody);
	
	public boolean updateToken(String email , String authenticationToken , String secretKey);
	
	public int getTokenDetail(String email );

	public String getToken(String email );
	
	public int tokenAuthentication(String token , int emailId);

}
