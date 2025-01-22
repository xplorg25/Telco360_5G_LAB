package org.xplorg.tool.telco360.entity;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class neighbour_details {

Logger log = LogManager.getLogger(neighbour_details.class.getName());

public ArrayList ip_details(String input) {
if(log.isInfoEnabled() || log.isDebugEnabled()) {	
log.info("*************** checked into ip_details ****************");	
}	
ArrayList sb=new ArrayList();
ArrayList ar_ip=new ArrayList();
ArrayList ar_name=new ArrayList();
String ip="";
String name="";
String cut1[]=input.split("\n");
for(String split1:cut1) {



if(split1.contains("MgmtIP:")&&!split1.contains("::")) {

ip=StringUtils.substringAfter(split1, ":").trim();
ar_ip.add(ip);
}

if(split1.contains("SysName:")) {
name=StringUtils.substringAfter(split1, ":").trim();
ar_name.add(name);
}



}


for(int i=0;i<ar_ip.size();i++) {
String output=ar_ip.get(i)+"--"+ar_name.get(i);
sb.add(output);

}

return sb;

}

}
