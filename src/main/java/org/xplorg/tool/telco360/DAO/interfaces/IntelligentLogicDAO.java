package org.xplorg.tool.telco360.DAO.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface IntelligentLogicDAO {

public String getIslDetails(String tableName,String columns,String conditions,String orderby);	
	
public String getIntelligentLogicUseCase(String protocol,String ucase,String command,String ring,String pattern,String value,String outputType);

public int uploadTextDataGeneric(MultipartFile file,String data);
}
