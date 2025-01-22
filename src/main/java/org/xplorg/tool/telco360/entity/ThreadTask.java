package org.xplorg.tool.telco360.entity;

public class ThreadTask implements Runnable{

int taskTime;	
	
public ThreadTask(int taskTime) {
this.taskTime=taskTime;	
}
		
	
@Override
public void run() {

try {
Thread.sleep(taskTime*1000);	
}catch(Exception ex) {
ex.printStackTrace();	
}
	
	
}

}
