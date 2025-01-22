package org.xplorg.tool.telco360.entity;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;

public class su_task_il extends GenericPostBody implements Runnable{
Logger log = LogManager.getLogger(su_task_il.class.getName());

Session session;	
String ipaddress,username,password,cmd1,cmd2;

public su_task_il(Session session,String ipaddress,String username,String password,String cmd1,String cmd2){
this.session=session;	
this.ipaddress=ipaddress;
this.username=username;
this.password=password;
this.cmd1=cmd1;
this.cmd2=cmd2;
}
	
	
@Override
public void run() {
if (log.isDebugEnabled()) {
log.debug("*************** checked into run of su_task_il ****************");
}
ArrayList<String> ar=new ArrayList<String>();
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
InputStream sessionError = channel.getExtInputStream();

channel.connect();

String command;
command = "ssh "+username+"@"+ipaddress+"\n";
commandIO.write(command.getBytes());
commandIO.flush();

// Read input until we get the 'Password:' prompt
byte[] tmp = new byte[1024];
String stdOut = "";
//String stdErr = "";

int i;

while (true) {
if (sessionError.available() > 0) {
i = sessionError.read(tmp, 0, tmp.length);
if (i < 0) {
System.err.println("input stream closed earlierthan expected");
System.exit(1);
}
// stdErr += new String(tmp, 0, i);
}

if (sessionOutput.available() > 0) {
i = sessionOutput.read(tmp, 0, tmp.length);
if (i < 0) {
System.err.println("input stream closed earlier   than expected");
System.exit(1);
}
stdOut += new String(tmp, 0, i);
}

if (stdOut.contains("assword")) {
break;
}

Thread.sleep(500);
}
command = password + "\n";
commandIO.write(command.getBytes());
commandIO.flush();

//command ="screen-length 0 temporary" + "\n";
command =cmd1 + "\n";
Thread.sleep(500);  
commandIO.write(command.getBytes());
commandIO.flush();
  
//command ="dis isis last-peer-change"+ "\n";      
command =cmd2+ "\n";      
Thread.sleep(500); 
commandIO.write(command.getBytes());
commandIO.flush();
  
command ="exit"+ "\n";
  
System.out.println(command);       
Thread.sleep(2000); 
commandIO.write(command.getBytes());
commandIO.flush();
 
// read and print output.
while ((i = sessionOutput.read(tmp, 0, tmp.length)) != -1) {
String r=(new String(tmp, 0, i));	
ar.add((new String(tmp, 0, i)));
if(r.trim().contains("exit")) {
break;    		
}
}

StringBuilder sb=new StringBuilder();

for(int j=0;j<ar.size();j++) {
sb.append(ar.get(j));	
}

setCommandTaskOutput(sb.toString());

}catch(Exception ex){
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();	
}
}
}



