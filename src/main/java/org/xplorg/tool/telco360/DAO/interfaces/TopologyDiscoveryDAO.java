package org.xplorg.tool.telco360.DAO.interfaces;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.xplorg.tool.telco360.entity.GenericPostBody;
import org.xplorg.tool.telco360.entity.tree_parents_t_d_final;

public interface TopologyDiscoveryDAO {
		
public String getTopologyDetails(String tableName,String columns,String conditions);	
	
public int uploadTopologyDiscoveryData(MultipartFile file);	
	
public List<tree_parents_t_d_final> getTopologyDiscoverySubnets(String vendor,String domain);

public List<tree_parents_t_d_final> getTopologyMicrowaveTree(String tableName);

public int postTopologyDiscoveryScan(String data);

public int uploadTopologyDiscoveryFile();

public int postTopologyDiscoveryNEUpdate(GenericPostBody genericPostBody);

}
