package org.xplorg.tool.telco360.entity;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;

public class su_task extends GenericPostBody implements Runnable{

Logger log = LogManager.getLogger(su_task.class.getName());

Session session;
String cmd1,cmd2;

public su_task(Session session,String cmd1,String cmd2) {
this.session=session;
this.cmd1=cmd1;
this.cmd2=cmd2;
}


@Override
public void run() {
if (log.isDebugEnabled()) {
log.debug("*************** checked into run of su_task ****************");
}
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

command =cmd1+"\n";
  
Thread.sleep(500);  
commandIO.write(command.getBytes());
commandIO.flush();
  
command =cmd2+"\n";
        
Thread.sleep(500); 
commandIO.write(command.getBytes());
commandIO.flush();

Thread.sleep(1000);
channel.disconnect();

int i=0;

while ((i = sessionOutput.read(tmp, 0, tmp.length)) != -1) {
String str=new String(tmp, 0, i,"UTF-8");
//System.out.println("str=========="+str);
sb.append(str+"\n");
if(str.trim().contains("quit")) {
channel.disconnect();
break;    		
}
}


}catch(Exception ex){
log.error("Exception occurs:----"+ex.getMessage(),ex);
}

setCommandTaskOutput(sb.toString());


}
}



