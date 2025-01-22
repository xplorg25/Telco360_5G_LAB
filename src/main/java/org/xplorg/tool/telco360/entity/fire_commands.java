package org.xplorg.tool.telco360.entity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

public class fire_commands {

Logger log = LogManager.getLogger(fire_commands.class.getName());

public ArrayList<String> fire_command(Session session,String command) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into fire_command ****************");	
}

ArrayList<String> ar_ip=new ArrayList<String>();
try{

ChannelExec channelExec = (ChannelExec)session.openChannel("exec");

InputStream in = channelExec.getInputStream();

channelExec.setCommand(command);

channelExec.connect();

BufferedReader reader = new BufferedReader(new InputStreamReader(in));
String line;
StringBuilder sb=new StringBuilder();
while ((line = reader.readLine()) != null)
{
sb.append(line+"\n");

}

ar_ip=new neighbour_details().ip_details(sb.toString());

channelExec.disconnect();
session.disconnect();

}catch(Exception ex){
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return ar_ip;
}

}