package org.xplorg.tool.telco360.DAO.implementation;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;

public class SSHTask implements Runnable{

Logger log = LogManager.getLogger(SSHTask.class.getName());	
	
Session session;
ArrayList<String> commands;
public static StringBuilder  result=null;
public SSHTask(Session session,ArrayList<String> commands) {
this.session=session;
this.commands=commands;
}

public void run() {
if (log.isDebugEnabled()) {
log.debug("*************** checked into run of SSHTask ****************");
}	
ArrayList<String> arlst_output=new ArrayList<String>();	

StringBuilder sb=new StringBuilder();
try{
// create the IO streams to send input to remote session.
PipedOutputStream commandIO = new PipedOutputStream();
InputStream sessionInput = new PipedInputStream(commandIO);
// this set's the InputStream the remote server will read from.
Channel channel=session.openChannel("shell");
channel.setInputStream(sessionInput);

// this will have the STDOUT from server.
InputStream sessionOutput = channel.getInputStream();

// this will have the STDERR from server
//InputStream sessionError = channel2.getExtInputStream();

channel.connect();

String command;

//Read input until we get the 'Password:' prompt
byte[] tmp = new byte[1024];

for(int i=0;i<commands.size();i++) {
command =commands.get(i)+"\n";
//System.out.println("command==========="+command);  
Thread.sleep(2000);  
commandIO.write(command.getBytes());
commandIO.flush();

//Thread.sleep(1000);
}

Thread.sleep(2000);
channel.disconnect();

int i=0;

while ((i = sessionOutput.read(tmp, 0, tmp.length)) != -1) {
String str=new String(tmp, 0, i,"UTF-8");
//System.out.println("str================="+str);

arlst_output.add(str.replace("\n", ""));
}


for(int j=0;j<arlst_output.size();j++) {
sb.append(arlst_output.get(j)+"\n");	
}

channel.disconnect();

}catch(Exception ex){
log.error("Exception occurs:----"+ex.getMessage(),ex);
}
result=sb;

}
}



