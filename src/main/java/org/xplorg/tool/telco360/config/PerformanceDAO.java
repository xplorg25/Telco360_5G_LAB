package org.xplorg.tool.telco360.config;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.MongoClient;

public class PerformanceDAO extends MongoDBFunctions {
	
Logger log = LogManager.getLogger(PerformanceDAO.class.getName());	
	
public Properties getProperties() {
if(log.isDebugEnabled()) {
log.debug("*****************************************  checked into getProperties of PerformanceDAO  ************************************************");	
}
Properties config=new Properties();
InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
try {
config.load(input);	
}catch(Exception ex) {
log.error("Exception occurs:-----"+ex.getMessage(),ex);	
//ex.printStackTrace();
}

return config;
}


//=====for Mongo DB=====Performance========

public MongoClient  get_mongo_connection()
{
if(log.isDebugEnabled()) {
log.debug("*****************************************  checked into get_mongo_connection of PerformanceDAO  ************************************************");	
}

MongoClient mongo_connection = null;
		
try{
Properties config=getProperties();

String ip=config.getProperty("database.mongodb_ip");
int port=Integer.parseInt(config.getProperty("database.mongodb_port"));
mongo_connection  = new MongoClient( ip , port );
java.util.logging.Logger mongoLogger = java.util.logging.Logger.getLogger( "org.mongodb.driver"); mongoLogger.setLevel(Level.SEVERE);

}catch(Exception ex)
{
log.error("Exception occurs:-----"+ex.getMessage(),ex);	
ex.printStackTrace();
}
//System.out.println("---MONGOCONNECTION-->"+mongo_connection);
return mongo_connection; 
}



public void  close_mongo_connection(MongoClient mc)
{
if(log.isDebugEnabled()) {
log.debug("*****************************************  checked into close_mongo_connection of PerformanceDAO  ************************************************");	
}


		
try{
	mc.close();
}catch(Exception ex)
{
log.error("Exception occurs:-----"+ex.getMessage(),ex);	
ex.printStackTrace();
}


}

    
}
