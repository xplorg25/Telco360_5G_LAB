package org.xplorg.tool.telco360.DAO.implementation;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.descending;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;
import org.xplorg.tool.telco360.DAO.interfaces.AlarmManagementDAO;
import org.xplorg.tool.telco360.config.BaseDAOMongo;
import org.xplorg.tool.telco360.entity.GenericPostBody;
import org.xplorg.tool.telco360.entity.StringInt;
import org.xplorg.tool.telco360.entity.TableHeader;
import org.xplorg.tool.telco360.entity.TableHeaderAlarms;
import org.xplorg.tool.telco360.entity.su_task;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

@Repository("alarmManagementDAO")
public class AlarmManagementDAOImpl extends BaseDAOMongo implements AlarmManagementDAO {
	int limitActiveAlarms = 50000;
	int limitHistoryAlarms = 100000;
	int limitActiveAlarmsElement = 5000;

	Logger log = LogManager.getLogger(AlarmManagementDAOImpl.class.getName());

	public String getActiveAlarmsOnWindow_clear(String userId,ArrayList<String> vendorlist,ArrayList<String> domainlist,ArrayList<String> severitylist,String alarmids,String alarmnames,String nename,ArrayList<String> columnsFinal,ArrayList<String>alarmnamelist) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into getActiveAlarmsOnWindow ****************");
			}
			try {	
			/*
			ArrayList<String>columnsFinal=new ArrayList<String>();	
			columnsFinal.add("VENDOR");
			columnsFinal.add("DOMAIN");
			columnsFinal.add("MANAGED_OBJECT");
			columnsFinal.add("ALARM_ID");
			columnsFinal.add("SEVERITY");
			columnsFinal.add("TIME");
			columnsFinal.add("NENAME");
			columnsFinal.add("NEIP");
			columnsFinal.add("ALARMNAME");
			columnsFinal.add("ALARM_INFO");
			columnsFinal.add("INSERTIONTIME");*/
			//columnsFinal.add("OCCURENCE");
			ArrayList<String>Alarmnames=new ArrayList<String>();
			System.out.println(alarmnamelist);
			ArrayList<TableHeaderAlarms>cols=new ArrayList<TableHeaderAlarms>();
			JSONArray vals=new JSONArray();
			JSONArray valsfinal=new JSONArray();

			for(String col:columnsFinal) {
			boolean visible=false;
			if(col.equals("STATUS")) {
			visible=false;	
			}
			else {
			visible=true;	
			}
			TableHeaderAlarms th = new TableHeaderAlarms(col, col,visible);
			cols.add(th);
			}
			cols.add(new TableHeaderAlarms("CLR","CLR",false));
			for(int d=0;d<domainlist.size();d++) {
			for(int j=0;j<vendorlist.size();j++) {
			if(domainlist.get(d).length()>1 && vendorlist.get(j).length()>1) {	
			ArrayList<String>filterType=new ArrayList<String>();	
			ArrayList<String>filterValue=new ArrayList<String>();	

			String almids="",alarmidsfinal="";
			if(alarmids.length()<2) {
			for(int i=0;i<filterType.size();i++){
			if(filterType.get(i).equalsIgnoreCase("ALARM NUMBERS") || filterType.get(i).equalsIgnoreCase("ALARM RANGE")){
			if(filterValue.get(i).contains(",")) {
			String spls[]=filterValue.get(i).split(",");
			for(String split:spls) {
			almids=almids+"ALARM_ID="+split+" or ";	
			}
			alarmidsfinal=almids.substring(0,almids.length()-4);
			}
			else {
			alarmidsfinal="ALARM_ID="+filterValue.get(i);		
			}
			}
			}
			}
			else {
			if(alarmids.contains(",")) {
			String spls[]=alarmids.split(",");
			for(String split:spls) {
			almids=almids+"ALARM_ID="+split+" or ";	
			}
			alarmidsfinal=almids.substring(0,almids.length()-4);
			}
			else {
				alarmidsfinal="ALARM_ID="+alarmids.replace("Shubham", "/");		
			}	
			}
			System.out.println(alarmnames);
			String almnames="",alarmnamesfinal="";
			if(alarmnames.length()<2) {
			for(int i=0;i<filterType.size();i++){
			if(filterType.get(i).equalsIgnoreCase("ALARM NAME")){
			if(filterValue.get(i).contains(",")) {
			String spls[]=filterValue.get(i).split(",");
			for(String split:spls) {
			almnames=almnames+"ALARMNAME like("+split+") or ";	
			}
			alarmnamesfinal=almnames.substring(0,almnames.length()-4);
			}
			else {
			alarmnamesfinal="ALARMNAME="+filterValue.get(i);		
			}
			}
			}
			}
			else {
			if(alarmnames.contains(",")) {
			String spls[]=alarmnames.split(",");
			for(String split:spls) {
//				Alarmnames.add(split);
			almnames=almnames+"ALARMNAME="+split+" or ";	

			}
			alarmnamesfinal=almnames.substring(0,almnames.length()-4);
			System.out.println("alarmnamesfinal===="+alarmnamesfinal);

			//alarmidsfinal=almids.substring(0,almids.length()-4);
			}
			//else if(alarmnamelist.size()==1){
			//	
			//	
//				if(alarmnamelist.get(0).equals("Equals"))
//				{
//					alarmnamesfinal="ALARMNAME="+alarmnames;		
			//
//					
//				}
//				Alarmnames.add(alarmnames);
			//
			//	
			//	
			//
			//}

			else
			{

				if(!alarmnames.equals("-")&&alarmnames.trim().length()>0)
				{
				if(alarmnamelist.contains("Equals"))
				{
				alarmnamesfinal="ALARMNAME="+alarmnames;		
				}
				

				Alarmnames.add(alarmnames);
				}
			}







			}
			//System.out.println("=====>>"+alarmnamesfinal);
			String tablenamedefault="allalarms";	
			String tablename="activealarms_kafka";	
			String tablename1="activealarms_kafka_bak";	
			String tablename2="nbi_tt_clearance";	
			String tablename3="nbi_tt_clearance_other";	

			String colms="",columns="";	
			for(int k=0;k<columnsFinal.size();k++) {
			if(columnsFinal.get(k).trim().length()>0) {	
			colms+=columnsFinal.get(k)+",";	
			}
			}
			columns=colms.substring(0,colms.length()-1);
			String cond_severity="",cond_sever="";
			if(severitylist.size()>0) {
			for(int i=0;i<severitylist.size();i++){
			if(severitylist.get(i).length()>1){
			cond_severity=cond_severity+"SEVERITY ="+severitylist.get(i)+" or ";
			}
			}
			if(cond_severity.length()>4) {
			cond_sever=cond_severity.substring(0,cond_severity.length()-4);
			}
			}
		/*
			String cond_filter="",cond_fltr="";
			if(filterlist.size()>0) {
			for(int i=0;i<filterlist.size();i++){
			if(filterlist.get(i).length()>1){
			cond_filter=cond_filter+"STATUS ="+filterlist.get(i)+" or ";
			}
			}
			if(cond_filter.length()>4) {
			cond_fltr=cond_filter.substring(0,cond_filter.length()-4);
			}
			}
	*/
			String nenamefinal="";
			if(nename.length()>1) {
			nenamefinal="NENAME like("+nename+")";		
			}
			String conditions="VENDOR="+vendorlist.get(j)+" and DOMAIN="+domainlist.get(d);	
			if(cond_sever.length()>0) {
			conditions=conditions+" and "+cond_sever;	
			}
			if(alarmidsfinal.length()>0) {
			conditions=conditions+" and "+alarmidsfinal;
			}
			if(alarmnamesfinal.length()>0) {
			conditions=conditions+" and "+alarmnamesfinal;
			}
			if(nenamefinal.length()>0) {
			conditions=conditions+" and "+nenamefinal;
			}
		/*
			if(cond_fltr.length()>0) {
			conditions=conditions+" and "+cond_fltr;
			}
			*/
			String cond="";
			//System.out.println(conditions);
			if(alarmnamelist.size()==1)
			{
				
				cond=alarmnamelist.get(0);
			}

			if(alarmnames.trim().equals("-")&&alarmnames.trim().length()>0)
			{
				
				cond="";
			}

			

			if(!alarmids.equals("-")||!alarmnames.equals("-")||!nename.equals("-")||!(alarmnamelist.size()==1&&alarmnamelist.get(0).equals("Empty"))||(severitylist.size()==1&&severitylist.get(0).equals("CLEAR")))
			{

				//System.out.println("is condition ch");
				JSONArray val=getActiveAlarmsOnWindowall(vendorlist.get(j).toUpperCase(),domainlist.get(d).toUpperCase(),columnsFinal,tablename, columns, conditions,"INSERTIONTIME",cond,Alarmnames,500);
				for(int i=0;i<val.length();i++) {
				vals.put(val.get(i));	
				}
				//System.out.println(vals);
				
				
			JSONArray val1=getActiveAlarmsOnWindowall(vendorlist.get(j).toUpperCase(),domainlist.get(d).toUpperCase(),columnsFinal,tablename1, columns, conditions,"INSERTIONTIME",cond,Alarmnames,10);
			for(int i=0;i<val1.length();i++) {
			vals.put(val1.get(i));	
			}
			//System.out.println(vals);

			JSONArray val2=getActiveAlarmsOnWindowall(vendorlist.get(j).toUpperCase(),domainlist.get(d).toUpperCase(),columnsFinal,tablename2, columns, conditions,"INSERTIONTIME",cond,Alarmnames,200);
			for(int i=0;i<val2.length();i++) {
			vals.put(val2.get(i));	
			}
			//System.out.println(vals);

			JSONArray val3=getActiveAlarmsOnWindowall(vendorlist.get(j).toUpperCase(),domainlist.get(d).toUpperCase(),columnsFinal,tablename3, columns, conditions,"INSERTIONTIME",cond,Alarmnames,10);
			for(int i=0;i<val3.length();i++) {
			vals.put(val3.get(i));	
			}
			//System.out.println(vals);








	}
			else {
				JSONArray val11=getActiveAlarmsOnWindowall(vendorlist.get(j).toUpperCase(),domainlist.get(d).toUpperCase(),columnsFinal,tablenamedefault, columns, conditions,"INSERTIONTIME",cond,Alarmnames,10000);
				for(int i=0;i<val11.length();i++) {
					valsfinal.put(val11.get(i));	
				}
			}
			
			
			//JSONArray val1=getActiveAlarmsOnWindow_clear(vendorlist.get(j).toUpperCase(),domainlist.get(d).toUpperCase(),columnsFinal,tablename1, columns, conditions,"INSERTIONTIME",cond,Alarmnames);
			//for(int i=0;i<val1.length();i++) {
			//vals.put(val1.get(i));	
			//}

			}
			}

			}

			
			
			if(!alarmids.equals("-")){
				ArrayList<Date>clr=new ArrayList<Date>();
				ArrayList<Date>act=new ArrayList<Date>();
				int k=0;
				String clrtime="";
				for (int i = 0; i < vals.length(); i++) {
				    JSONObject docs = vals.getJSONObject(i);
					String syncrec=docs.getString("SynchReceiver");

					String sev=docs.getString("SEVERITY");
					String insertime=docs.getString("INSERTIONTIME");
					
//					System.out.println(insertime);
					
				    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    
				    
				    if(sev.equals("CLEAR"))
				    {
				    Date date = formatter.parse(insertime);
				   System.out.println(formatter.format(date));
		
				    clr.add(date);    
				 
				}
				    else
				    {
				        Date date = formatter.parse(insertime);
				       // System.out.println(date);

				    	act.add(date);
				    }
				  //  System.out.println(act);
				    
				    
				    
				    
				    
				   
				    
				    
				    
				}
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				if(act.size()>0)
				{
				Date earliestDate = Collections.min(act);

				for (int i = 0; i < vals.length(); i++) {
				    JSONObject docs = vals.getJSONObject(i);
					String syncrec=docs.getString("SynchReceiver");

					String sev=docs.getString("SEVERITY");
					String insertime=docs.getString("INSERTIONTIME");
				    String time=formatter.format(earliestDate);

					if(!sev.equals("CLEAR")&&insertime.equals(time))
					{
						//System.out.println("----->"+vals.get(i));
						valsfinal.put(vals.get(i));		
						
					}
					
				}
				}
			
					if(clr.size()>0)
					{
				    Date clrDate = Collections.min(clr);

					for (int i = 0; i < vals.length(); i++) {
					    JSONObject docs = vals.getJSONObject(i);
						String syncrec=docs.getString("SynchReceiver");

						String sev=docs.getString("SEVERITY");
						String insertime=docs.getString("INSERTIONTIME");
					    String time=formatter.format(clrDate);

						if(sev.equals("CLEAR")&&insertime.equals(time))
						{
							
							valsfinal.put(vals.get(i));		
							
						}
						
					}
					}
				}

				
			
			JSONArray jsonArrayFinal=new JSONArray();
			JSONObject jsonObjectColVal=new JSONObject();
			jsonObjectColVal.put("cols", cols);
			jsonObjectColVal.put("vals", valsfinal);
			jsonArrayFinal.put(jsonObjectColVal);
			System.out.println(jsonArrayFinal.toString());
			return jsonArrayFinal.toString();

			}catch(Exception ex) {
			log.error("Exception occurs:----"+ex.getMessage(),ex);
			//ex.printStackTrace();
			}
			return null;
	}

	
	public JSONArray getActiveAlarmsOnWindowall(String vendor,String domain,ArrayList<String>columnsFinal,String tableName,String columns,String conditions,String orderby,String condi,ArrayList<String> Alarmname,int limits) {
		if (log.isDebugEnabled()) {
		log.debug("*************** checked into getActiveAlarmsOnWindow ****************");
		}
		try {	
			Properties conf=getProperties();
			MongoClient mongo=getConnection();
			MongoDatabase database = mongo.getDatabase(conf.getProperty("mongo.db.database.topology"));
		 	MongoCollection<Document> collection = database.getCollection(tableName);
			MongoCollection<Document> collection_blacklist = database.getCollection("alarms_blacklist");
		ArrayList<Bson> fltr=new ArrayList<Bson>();
		Bson filter=null;
		//System.out.println(condi);
		//System.out.println(Alarmname);
		if(conditions.length()>1) {
			
			

			if(condi.trim().length()>0&&Alarmname.size()>0)
			{
				

			//	if(Alarmname.size()==1)
				//{
			//	if(Alarmname.get(0).trim().length()>0&&Alarmname.get(0).trim().equals("-"))
				//{
				 if(condi.equals("Contains"))
				{
//					 System.out.println("oo aagyaaaaaaaaaaaaaaaaa");
					 fltr.add(eq("ALARMNAME",java.util.regex.Pattern.compile("^.*"+Alarmname.get(0)+".*")));

				}
				
				 else if(condi.equals("Not Contains"))
					{
						
						fltr.add(Filters.ne("ALARMNAME",java.util.regex.Pattern.compile("^" + Alarmname.get(0) + ".*")));

						
					}
					
				else if(condi.equals("Starts With"))
				{
					
					fltr.add(eq("ALARMNAME",java.util.regex.Pattern.compile("^" + Alarmname.get(0) + ".*")));

					
				}
				
				else if(condi.equals("Ends With"))
				{
					
					fltr.add(eq("ALARMNAME",java.util.regex.Pattern.compile(".*" + Alarmname.get(0) + "$")));

					
				}
					

			//	}
				
			}
			
			//System.out.println(fltr);

			//////*******************************************************************************///////

			ArrayList<Document> resultSet = collection_blacklist.find().into(new ArrayList<Document>());

			for(Document docs:resultSet) {

				String alamnamee=docs.getString("alarmname");
				String domainn=docs.getString("domain");
				String vindor=docs.getString("vendor");

				if(domainn.equalsIgnoreCase(domain)&&vindor.equalsIgnoreCase(vendor))
				{
				fltr.add(Filters.ne("ALARMNAME", alamnamee));
				}

			}

			
		String cond=conditions.replace("@DOT@", ".").replace("@SEMICOLON@", ";");	
		if(cond.contains(" and ")){
		String cond_spls[]=cond.split(" and ");
		for(String cv:cond_spls) {	
		if(cv.contains("=") && !cv.contains("like") && !cv.contains("between") && !cv.contains(" or ")) {
		String col=cv.substring(0,cv.indexOf("=")).trim();
		String val=cv.substring(cv.indexOf("=")+1).trim().replace("'","");
		fltr.add(eq(col,val));
		}
		else if(!cv.contains("=") && cv.contains("like") && !cv.contains("between") && !cv.contains(" or ")) {
		String col=cv.substring(0,cv.indexOf("like")).trim();
		String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
		if(!val.toUpperCase().contains("MANAGEDELEMENT")) {
		fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*",Pattern.CASE_INSENSITIVE)));
		}
		else {
		fltr.add(eq(col,java.util.regex.Pattern.compile("^"+val+".*",Pattern.CASE_INSENSITIVE)));	
		}
		}
		else if(!cv.contains("=") && cv.contains("like") && !cv.contains("between") && cv.contains(" or ")) {
		String or[]=cv.split(" or ");
		ArrayList<Bson> fltr_or=new ArrayList<Bson>();
		for(String splt_or:or) {
		String col=splt_or.substring(0,splt_or.indexOf("like")).trim();
		String val=splt_or.substring(splt_or.indexOf("(")+1,splt_or.lastIndexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
		if(!val.toUpperCase().contains("MANAGEDELEMENT")) {
		fltr_or.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*",Pattern.CASE_INSENSITIVE)));
		}
		else {
		fltr_or.add(eq(col,java.util.regex.Pattern.compile("^"+val+".*",Pattern.CASE_INSENSITIVE)));	
		}
		}
		fltr.add(or(fltr_or));
		}
		else if(cv.contains("=") && cv.contains("between")) {
		String col=cv.substring(0,cv.indexOf("between")).trim();
		String val1=cv.substring(cv.indexOf("FROM=")+5,cv.indexOf("TO=")).replace("'","").trim();
		String val2=cv.substring(cv.indexOf("TO=")+3).replace("'","").trim();	
		fltr.add(gte(col,val1));
		fltr.add(lte(col,val2));
		}
		else if(cv.contains("=") && cv.contains(" or ") && !cv.contains("like") && !cv.contains("between")) {
		String or[]=cv.split(" or ");
		ArrayList<Bson> fltr_or=new ArrayList<Bson>();
		for(String splt_or:or) {
		String col=splt_or.substring(0,splt_or.indexOf("=")).trim();
		String val=splt_or.substring(splt_or.indexOf("=")+1).trim().replace("'","");
		fltr_or.add(eq(col,val));
		}
		fltr.add(or(fltr_or));
		}
		}
		}
		else if(cond.contains(" AND ")){
		String cond_spls[]=cond.split(" AND ");
		for(String cv:cond_spls) {
		if(cv.contains("=") && !cv.contains("like") && !cv.contains("between")  && !cv.contains(" OR ")) {
		String col=cv.substring(0,cv.indexOf("=")).trim();
		String val=cv.substring(cv.indexOf("=")+1).trim().replace("'", "");
		fltr.add(eq(col,val));
		}
		else if(!cv.contains("=") && cv.contains("like") && !cv.contains("between")  && !cv.contains(" OR ")) {
		String col=cv.substring(0,cv.indexOf("like")).trim();
		String val=cv.substring(cv.indexOf("(")+1,cv.indexOf(")")).replace("'","").replace("@","").replace("%", "").trim();	
		if(!val.toUpperCase().contains("MANAGEDELEMENT")) {
		fltr.add(eq(col,java.util.regex.Pattern.compile("^.*"+val+".*",Pattern.CASE_INSENSITIVE)));
		}
		else {
		fltr.add(eq(col,java.util.regex.Pattern.compile("^"+val+".*",Pattern.CASE_INSENSITIVE)));	
		}
		}
		else if(cv.contains("=") && cv.contains("between")) {
		String col=cv.substring(0,cv.indexOf("between")).trim();
		String val1=cv.substring(cv.indexOf("FROM=")+5,cv.indexOf("TO=")).replace("'","").trim();
		String val2=cv.substring(cv.indexOf("TO=")+3).replace("'","").trim();	
		fltr.add(gte(col,val1));
		fltr.add(lte(col,val2));
		}
		else if(cv.contains("=") && cv.contains(" OR ") && !cv.contains("like") && !cv.contains("between")) {
		String or[]=cv.split(" or ");
		ArrayList<Bson> fltr_or=new ArrayList<Bson>();
		for(String splt_or:or) {
		String col=splt_or.substring(0,splt_or.indexOf("=")).trim();
		String val=splt_or.substring(splt_or.indexOf("=")+1).trim().replace("'","");
		fltr_or.add(eq(col,val));
		}
		fltr.add(or(fltr_or));
		}

		}
		}
		else if(!(cond.contains(" AND ") && !cond.contains(" and ")) && cond.length()>1){
		if(cond.contains("=") && cond.contains("between") && !cond.contains(" or ") && !cond.contains(" OR ")) {
		String col=cond.substring(0,cond.indexOf("between")).trim();
		String val1=cond.substring(cond.indexOf("FROM=")+5,cond.indexOf("TO=")).replace("'","").trim();
		String val2=cond.substring(cond.indexOf("TO=")+3).replace("'","").trim();	
		fltr.add(gte(col,val1));
		fltr.add(lte(col,val2));
		}
		else if(cond.contains(" or ") && !cond.contains("like") && !cond.contains("between")) {
		String or[]=cond.split(" or ");
		ArrayList<Bson> fltr_or=new ArrayList<Bson>();
		for(String splt_or:or) {
		String col=splt_or.substring(0,splt_or.indexOf("=")).trim();
		String val=splt_or.substring(splt_or.indexOf("=")+1).trim().replace("'","");
		fltr_or.add(eq(col,val));
		}
		fltr.add(or(fltr_or));
		}
		else if(cond.contains(" OR ") && !cond.contains("like") && !cond.contains("between")) {
		String or[]=cond.split(" OR ");
		ArrayList<Bson> fltr_or=new ArrayList<Bson>();
		for(String splt_or:or) {
		String col=splt_or.substring(0,splt_or.indexOf("=")).trim();
		String val=splt_or.substring(splt_or.indexOf("=")+1).trim().replace("'","");
		fltr_or.add(eq(col,val));
		}
		fltr.add(or(fltr_or));
		}
		else if(cond.contains("=")) {
		String col=cond.substring(0,cond.indexOf("=")).trim();
		String val=cond.substring(cond.indexOf("=")+1).trim().replace("'", "");
		fltr.add(eq(col,val));
		}
		}




		filter=and(fltr);
		}

		//MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
		//MongoCollection<Document> collection = database.getCollection(tableName);
		ArrayList<String>cls=new ArrayList<String>();
		ArrayList<Document> resultSet = null;
		JSONArray vals=new JSONArray();

		Map<String, Object> groupMap = new HashMap<String, Object>();

		if(!columns.equals("*")) {
		if(columns.contains(",")) {
		String columns_spls[]=columns.split(",");
		for(String colm:columns_spls) {
		cls.add(colm);	
		groupMap.put(colm, "$"+colm);
		}
		}
		else {
		cls.add(columns);	
		groupMap.put(columns, "$"+columns);	
		}
		System.out.println(tableName);
		System.out.println(filter);

		DBObject groupFields = new BasicDBObject(groupMap);
		BasicDBObject index = new BasicDBObject("$hint", "ALARM_ID_1");



		if(filter!=null) {
		resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))),sort(descending("_id."+orderby)),limit(limits))).hint(index).batchSize(1000).allowDiskUse(true).into(new ArrayList<Document>());
		}
		else {
		resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))),sort(descending("_id."+orderby)),limit(limits))).hint(index).batchSize(1000).allowDiskUse(true).into(new ArrayList<Document>());
		}

		for(Document documents:resultSet) {
		JSONObject colval=new JSONObject();
		Document docs=(Document) documents.get("_id");
		for(int j=0;j<cls.size();j++) {	
		if(docs.containsKey(cls.get(j))) {	
		if(docs.get(cls.get(j)).toString().length()>0 && docs.get(cls.get(j)).toString()!=null) {
		if(cls.get(j).equals("SEVERITY")) {
		colval.put(cls.get(j), docs.get(cls.get(j)).toString().replace("\n"," "));
		if(docs.getString("STATUS").equalsIgnoreCase("UNREAD")) {
		if(docs.getString(cls.get(j)).equalsIgnoreCase("CRITICAL")) {
		colval.put("CLR",conf.getProperty("alarm_critical"));	
		}
		else if(docs.getString(cls.get(j)).equalsIgnoreCase("MAJOR")) {
		colval.put("CLR",conf.getProperty("alarm_major"));	
		}
		else if(docs.getString(cls.get(j)).equalsIgnoreCase("MINOR")) {
		colval.put("CLR",conf.getProperty("alarm_minor"));	
		}
		else if(docs.getString(cls.get(j)).equalsIgnoreCase("WARNING")) {
		colval.put("CLR",conf.getProperty("alarm_warning"));	
		}
		else if(docs.getString(cls.get(j)).equalsIgnoreCase("INDETERMINATE")) {
		colval.put("CLR",conf.getProperty("alarm_indeterminate"));	
		}
		}
		else {
		colval.put("CLR",conf.getProperty("alarm_read"));		
		}
		}
		else {
		colval.put(cls.get(j), docs.get(cls.get(j)).toString().replace("\n"," "));		
		}
		}
		else {
		colval.put(cls.get(j),"-");	
		}
		}
		else {
		colval.put(cls.get(j),"-");	
		}
		}
		if(!vals.similar(colval)) {
		vals.put(colval);	
		}	
		}
		}
		closeConnection(mongo);
		return vals;	
		}catch(Exception ex) {
		log.error("Exception occurs:----"+ex.getMessage(),ex);
		ex.printStackTrace();
		}
		return null;
		}
	
	public String getActiveAlarmsOnWindow(String userId, ArrayList<String> vendorlist, ArrayList<String> domainlist,
			ArrayList<String> severitylist, String alarmids, String alarmnames, String nename,
			ArrayList<String> columnsFinal, ArrayList<String> filterlist, ArrayList<String> alarmnamelist) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into getActiveAlarmsOnWindow ****************");
		}
		try {

			System.out.println("chllll");
			/*
			 * ArrayList<String>columnsFinal=new ArrayList<String>();
			 * columnsFinal.add("VENDOR"); columnsFinal.add("DOMAIN");
			 * columnsFinal.add("MANAGED_OBJECT"); columnsFinal.add("ALARM_ID");
			 * columnsFinal.add("SEVERITY"); columnsFinal.add("TIME");sdf
			 * columnsFinal.add("NENAME"); columnsFinal.add("NEIP");
			 * columnsFinal.add("ALARMNAME"); columnsFinal.add("ALARM_INFO");
			 * columnsFinal.add("INSERTIONTIME");
			 */
//columnsFinal.add("OCCURENCE");
			ArrayList<String> Alarmnames = new ArrayList<String>();
			ArrayList<TableHeaderAlarms> cols = new ArrayList<TableHeaderAlarms>();
			JSONArray vals = new JSONArray();
			for (String col : columnsFinal) {
				boolean visible = false;
				if (col.equals("STATUS")) {
					visible = false;
				} else {
					visible = true;
				}
				TableHeaderAlarms th = new TableHeaderAlarms(col, col, visible);
				cols.add(th);
			}
			cols.add(new TableHeaderAlarms("CLR", "CLR", false));
			for (int d = 0; d < domainlist.size(); d++) {
				for (int j = 0; j < vendorlist.size(); j++) {
					if (domainlist.get(d).length() > 1 && vendorlist.get(j).length() > 1) {
						ArrayList<String> filterType = new ArrayList<String>();
						ArrayList<String> filterValue = new ArrayList<String>();

						String almids = "", alarmidsfinal = "";
						if (alarmids.length() < 2) {
							for (int i = 0; i < filterType.size(); i++) {
								if (filterType.get(i).equalsIgnoreCase("ALARM NUMBERS")
										|| filterType.get(i).equalsIgnoreCase("ALARM RANGE")) {
									if (filterValue.get(i).contains(",")) {
										String spls[] = filterValue.get(i).split(",");
										for (String split : spls) {
											almids = almids + "ALARM_ID=" + split + " or ";
										}
										alarmidsfinal = almids.substring(0, almids.length() - 4);
									} else {
										alarmidsfinal = "ALARM_ID=" + filterValue.get(i);
									}
								}
							}
						} else {
							if (alarmids.contains(",")) {
								String spls[] = alarmids.split(",");
								for (String split : spls) {
									almids = almids + "ALARM_ID=" + split + " or ";
								}
								alarmidsfinal = almids.substring(0, almids.length() - 4);
							} else {
								alarmidsfinal = "ALARM_ID=" + alarmids;
							}
						}
						String almnames = "", alarmnamesfinal = "";
						if (alarmnames.length() < 2) {
							for (int i = 0; i < filterType.size(); i++) {
								if (filterType.get(i).equalsIgnoreCase("ALARM NAME")) {
									if (filterValue.get(i).contains(",")) {
										String spls[] = filterValue.get(i).split(",");
										for (String split : spls) {
											almnames = almnames + "ALARMNAME like(" + split + ") or ";
										}
										alarmnamesfinal = almnames.substring(0, almnames.length() - 4);
									} else {
										alarmnamesfinal = "ALARMNAME=" + filterValue.get(i);
									}
								}
							}
						} else {
							if (alarmnames.contains(",")) {
								String spls[] = alarmnames.split(",");
								for (String split : spls) {
									almnames = almnames + "ALARMNAME=" + split + " or ";
								}
								alarmnamesfinal = almnames.substring(0, almnames.length() - 4);
							} else {
								if (!alarmnames.equals("-") && alarmnames.trim().length() > 0) {
									if (alarmnamelist.contains("Equals")) {
										alarmnamesfinal = "ALARMNAME=" + alarmnames;
									}

									Alarmnames.add(alarmnames);
								}
							}
						}
						String tablename = "activealarms";
						String tablenameevents = "events";
						String tablename1 = "nbi_tt_clearance";
						String tablename2 = "nbi_tt_clearance_others";

						String colms = "", columns = "";
						for (int k = 0; k < columnsFinal.size(); k++) {
							if (columnsFinal.get(k).trim().length() > 0) {
								colms += columnsFinal.get(k) + ",";
							}
						}
						columns = colms.substring(0, colms.length() - 1);
						String cond_severity = "", cond_sever = "";
						if (severitylist.size() > 0) {
							for (int i = 0; i < severitylist.size(); i++) {
								if (severitylist.get(i).length() > 1) {
									cond_severity = cond_severity + "SEVERITY ="
											+ severitylist.get(i).replace("Cleared", "CLEAR") + " or ";
								}
							}
							if (cond_severity.length() > 4) {
								cond_sever = cond_severity.substring(0, cond_severity.length() - 4);
							}
						}
						String cond_filter = "", cond_fltr = "";
						if (filterlist.size() > 0) {
							for (int i = 0; i < filterlist.size(); i++) {
								if (filterlist.get(i).length() > 1) {
									cond_filter = cond_filter + "STATUS =" + filterlist.get(i) + " or ";
								}
							}
							if (cond_filter.length() > 4) {
								cond_fltr = cond_filter.substring(0, cond_filter.length() - 4);
							}
						}

						String nenamefinal = "";
						if (nename.length() > 1) {
							nenamefinal = "NENAME like(" + nename + ")";
						}
						String conditions = "VENDOR=" + vendorlist.get(j) + " and DOMAIN="
								+ domainlist.get(d).replace("Events", "Mpbn");
						if (cond_sever.length() > 0) {
							conditions = conditions + " and " + cond_sever;
						}
						if (alarmidsfinal.length() > 0) {
							conditions = conditions + " and " + alarmidsfinal;
						}
						if (alarmnamesfinal.length() > 0) {
							conditions = conditions + " and " + alarmnamesfinal;
						}
						if (nenamefinal.length() > 0) {
							conditions = conditions + " and " + nenamefinal;
						}
						if (cond_fltr.length() > 0) {
							conditions = conditions + " and " + cond_fltr;
						}

						String cond = "";
						if (alarmnamelist.size() == 1) {

							cond = alarmnamelist.get(0);
						}

						if (alarmnames.trim().equals("-") && alarmnames.trim().length() > 0) {

							cond = "";
						}

						System.out.println(domainlist);

						if (domainlist.contains("Events")) {
							System.out.println("AA gyaaaaa");
							JSONArray val = getActiveAlarmsOnWindow(vendorlist.get(j).toUpperCase(), "Mpbn",
									columnsFinal, tablenameevents, columns, conditions, "INSERTIONTIME", cond,
									Alarmnames);
							for (int i = 0; i < val.length(); i++) {
								vals.put(val.get(i));
							}
						} else {
							System.out.println("aaaaa");
							JSONArray val = getActiveAlarmsOnWindow(vendorlist.get(j).toUpperCase(),
									domainlist.get(d).toUpperCase(), columnsFinal, tablename, columns, conditions,
									"INSERTIONTIME", cond, Alarmnames);
							for (int i = 0; i < val.length(); i++) {
								vals.put(val.get(i));
							}

						}

//
//JSONArray val1=getActiveAlarmsOnWindow_nbi_clear(vendorlist.get(j).toUpperCase(),domainlist.get(d).toUpperCase(),columnsFinal,tablename1, columns, conditions,"INSERTIONTIME",cond,Alarmnames);
//for(int i=0;i<val1.length();i++) {
//vals.put(val1.get(i));
//}
//
//
//JSONArray val2=getActiveAlarmsOnWindow_nbi_clear_others(vendorlist.get(j).toUpperCase(),domainlist.get(d).toUpperCase(),columnsFinal,tablename2, columns, conditions,"INSERTIONTIME",cond,Alarmnames);
//for(int i=0;i<val2.length();i++) {
//vals.put(val2.get(i));
//}
//

					}
				}
			}

			JSONArray jsonArrayFinal = new JSONArray();
			JSONObject jsonObjectColVal = new JSONObject();
			jsonObjectColVal.put("cols", cols);
			jsonObjectColVal.put("vals", vals);
			jsonArrayFinal.put(jsonObjectColVal);
			return jsonArrayFinal.toString();

		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
		}
		return null;
	}

	public JSONArray getActiveAlarmsOnWindow(String vendor, String domain, ArrayList<String> columnsFinal,
			String tableName, String columns, String conditions, String orderby, String condi,
			ArrayList<String> Alarmname) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into getActiveAlarmsOnWindow ****************");
		}
		try {

			Properties config = getProperties();
			MongoClient mongo = getConnection();
			MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
			MongoCollection<Document> collection = database.getCollection(tableName);
			MongoCollection<Document> collection_blacklist = database.getCollection("alarms_blacklist");

			ArrayList<Bson> fltr = new ArrayList<Bson>();
			Bson filter = null;
			if (conditions.length() > 1) {

				if (condi.trim().length() > 0 && Alarmname.size() > 0) {

					// if(Alarmname.size()==1)
					// {
					// if(Alarmname.get(0).trim().length()>0&&Alarmname.get(0).trim().equals("-"))
					// {
					if (condi.equals("Contains")) {
						fltr.add(eq("ALARMNAME", java.util.regex.Pattern.compile("^.*" + Alarmname.get(0) + ".*")));

					}

					else if (condi.equals("Not Contains")) {

						fltr.add(Filters.ne("ALARMNAME", Alarmname.get(0)));
					}

					else if (condi.equals("Starts With")) {

						fltr.add(eq("ALARMNAME", java.util.regex.Pattern.compile("^" + Alarmname.get(0) + ".*")));

					}

					else if (condi.equals("Ends With")) {

						fltr.add(eq("ALARMNAME", java.util.regex.Pattern.compile(".*" + Alarmname.get(0) + "$")));

					}

					// }

				}

				ArrayList<Document> resultSet = collection_blacklist.find().into(new ArrayList<Document>());

				for (Document docs : resultSet) {

					String alamnamee = docs.getString("alarmname");
					String domainn = docs.getString("domain");
					String vindor = docs.getString("vendor");

					if (domainn.equalsIgnoreCase(domain) && vindor.equalsIgnoreCase(vendor)) {
						fltr.add(Filters.ne("ALARMNAME", alamnamee));
					}

				}

				String cond = conditions.replace("@DOT@", ".").replace("@SEMICOLON@", ";");
				if (cond.contains(" and ")) {
					String cond_spls[] = cond.split(" and ");
					for (String cv : cond_spls) {
						if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" or ")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							fltr.add(eq(col, val));
						} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" or ")) {
							String col = cv.substring(0, cv.indexOf("like")).trim();
							String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "")
									.replace("@", "").replace("%", "").trim();
							if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^.*" + val + ".*", Pattern.CASE_INSENSITIVE)));
							} else {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
							}
						} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& cv.contains(" or ")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("like")).trim();
								String val = splt_or.substring(splt_or.indexOf("(") + 1, splt_or.lastIndexOf(")"))
										.replace("'", "").replace("@", "").replace("%", "").trim();
								if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^.*" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								} else {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								}
							}
							fltr.add(or(fltr_or));
						} else if (cv.contains("=") && cv.contains("between")) {
							String col = cv.substring(0, cv.indexOf("between")).trim();
							String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "")
									.trim();
							String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
							fltr.add(gte(col, val1));
							fltr.add(lte(col, val2));
						} else if (cv.contains("=") && cv.contains(" or ") && !cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
								String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
								fltr_or.add(eq(col, val));
							}
							fltr.add(or(fltr_or));
						}
					}
				} else if (cond.contains(" AND ")) {
					String cond_spls[] = cond.split(" AND ");
					for (String cv : cond_spls) {
						if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" OR ")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							fltr.add(eq(col, val));
						} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" OR ")) {
							String col = cv.substring(0, cv.indexOf("like")).trim();
							String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "")
									.replace("@", "").replace("%", "").trim();
							if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^.*" + val + ".*", Pattern.CASE_INSENSITIVE)));
							} else {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
							}
						} else if (cv.contains("=") && cv.contains("between")) {
							String col = cv.substring(0, cv.indexOf("between")).trim();
							String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "")
									.trim();
							String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
							fltr.add(gte(col, val1));
							fltr.add(lte(col, val2));
						} else if (cv.contains("=") && cv.contains(" OR ") && !cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
								String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
								fltr_or.add(eq(col, val));
							}
							fltr.add(or(fltr_or));
						}

					}
				} else if (!(cond.contains(" AND ") && !cond.contains(" and ")) && cond.length() > 1) {
					if (cond.contains("=") && cond.contains("between") && !cond.contains(" or ")
							&& !cond.contains(" OR ")) {
						String col = cond.substring(0, cond.indexOf("between")).trim();
						String val1 = cond.substring(cond.indexOf("FROM=") + 5, cond.indexOf("TO=")).replace("'", "")
								.trim();
						String val2 = cond.substring(cond.indexOf("TO=") + 3).replace("'", "").trim();
						fltr.add(gte(col, val1));
						fltr.add(lte(col, val2));
					} else if (cond.contains(" or ") && !cond.contains("like") && !cond.contains("between")) {
						String or[] = cond.split(" or ");
						ArrayList<Bson> fltr_or = new ArrayList<Bson>();
						for (String splt_or : or) {
							String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
							String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
							fltr_or.add(eq(col, val));
						}
						fltr.add(or(fltr_or));
					} else if (cond.contains(" OR ") && !cond.contains("like") && !cond.contains("between")) {
						String or[] = cond.split(" OR ");
						ArrayList<Bson> fltr_or = new ArrayList<Bson>();
						for (String splt_or : or) {
							String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
							String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
							fltr_or.add(eq(col, val));
						}
						fltr.add(or(fltr_or));
					} else if (cond.contains("=")) {
						String col = cond.substring(0, cond.indexOf("=")).trim();
						String val = cond.substring(cond.indexOf("=") + 1).trim().replace("'", "");
						fltr.add(eq(col, val));
					}
				}
				filter = and(fltr);
			}

			System.out.println(filter);

			ArrayList<String> cls = new ArrayList<String>();
			ArrayList<Document> resultSet = null;
			JSONArray vals = new JSONArray();

			Map<String, Object> groupMap = new HashMap<String, Object>();

			if (!columns.equals("*")) {
				if (columns.contains(",")) {
					String columns_spls[] = columns.split(",");
					for (String colm : columns_spls) {
						cls.add(colm);
						groupMap.put(colm, "$" + colm);
					}
				} else {
					cls.add(columns);
					groupMap.put(columns, "$" + columns);
				}
				DBObject groupFields = new BasicDBObject(groupMap);
				if (filter != null) {
					resultSet = collection
							.aggregate(Arrays.asList(match(filter), group(groupFields), project(fields(include(cls))),
									sort(descending("_id." + orderby)), limit(limitActiveAlarms)))
							.allowDiskUse(true).into(new ArrayList<Document>());
				} else {
					resultSet = collection
							.aggregate(Arrays.asList(group(groupFields), project(fields(include(cls))),
									sort(descending("_id." + orderby)), limit(limitActiveAlarms)))
							.allowDiskUse(true).into(new ArrayList<Document>());
				}

				for (Document documents : resultSet) {
					JSONObject colval = new JSONObject();
					Document docs = (Document) documents.get("_id");
					for (int j = 0; j < cls.size(); j++) {
						if (docs.containsKey(cls.get(j))) {
							if (docs.get(cls.get(j)).toString().length() > 0
									&& docs.get(cls.get(j)).toString() != null) {
								if (cls.get(j).equals("SEVERITY")) {
									colval.put(cls.get(j), docs.get(cls.get(j)).toString().replace("\n", " "));
									if (docs.getString("STATUS").equalsIgnoreCase("UNREAD")) {
										if (docs.getString(cls.get(j)).equalsIgnoreCase("CRITICAL")) {
											colval.put("CLR", config.getProperty("alarm_critical"));
										} else if (docs.getString(cls.get(j)).equalsIgnoreCase("MAJOR")) {
											colval.put("CLR", config.getProperty("alarm_major"));
										} else if (docs.getString(cls.get(j)).equalsIgnoreCase("MINOR")) {
											colval.put("CLR", config.getProperty("alarm_minor"));
										} else if (docs.getString(cls.get(j)).equalsIgnoreCase("WARNING")) {
											colval.put("CLR", config.getProperty("alarm_warning"));
										} else if (docs.getString(cls.get(j)).equalsIgnoreCase("INDETERMINATE")) {
											colval.put("CLR", config.getProperty("alarm_indeterminate"));
										} else if (docs.getString(cls.get(j)).equalsIgnoreCase("CLEAR")) {
											colval.put("CLR", config.getProperty("alarm_clear"));
										}

									} else {
										colval.put("CLR", config.getProperty("alarm_read"));
									}
								} else {
									colval.put(cls.get(j), docs.get(cls.get(j)).toString().replace("\n", " "));
								}
							} else {
								colval.put(cls.get(j), "-");
							}
						} else {
							colval.put(cls.get(j), "-");
						}
					}
					if (!vals.similar(colval)) {
						vals.put(colval);
					}
				}
			}
			closeConnection(mongo);
			return vals;
		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
			ex.printStackTrace();
		}
		return null;
	}

	public void getAlarmFilters(String userId, String domain, String vendor, ArrayList<String> filterType,
			ArrayList<String> filterValue) {
		try {
			Properties config = getProperties();
			MongoClient mongo = getConnection();
			MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
			MongoCollection<Document> collection = database.getCollection("alarm_filter");
			ArrayList<Document> resultSet = collection
					.find(and(eq("user_id", userId), eq("for", "Topology"), eq("domain", domain), eq("vendor", vendor)))
					.into(new ArrayList<Document>());
			for (Document docs : resultSet) {
				if (docs.get("filter").toString().equalsIgnoreCase("Alarm Numbers")) {
					filterType.add(docs.get("filter").toString());
					filterValue.add(docs.get("value").toString());
					/*
					 * if(docs.get("value").toString().contains(",")) { String
					 * spls[]=docs.get("value").toString().split(","); for(String splt:spls) {
					 * if(splt.trim().length()>0) { filterType.add(docs.get("filter").toString());
					 * filterValue.add(splt); } } } else {
					 * filterType.add(docs.get("filter").toString());
					 * filterValue.add(docs.get("value").toString().toUpperCase()); }
					 */
				}

				else if (docs.get("filter").toString().equalsIgnoreCase("Alarm Name")) {
					filterType.add(docs.get("filter").toString());
					filterValue.add(docs.get("value").toString());
					/*
					 * if(docs.get("value").toString().contains(",")) { String
					 * spls[]=docs.get("value").toString().split(","); for(String splt:spls) {
					 * if(splt.trim().length()>0) { filterType.add(docs.get("filter").toString());
					 * filterValue.add(splt); } } } else {
					 * filterType.add(docs.get("filter").toString());
					 * filterValue.add(docs.get("value").toString().toUpperCase()); }
					 */
				}

				else if (docs.get("filter").toString().equalsIgnoreCase("Alarm Severity")) {
					filterType.add(docs.get("filter").toString());
					filterValue.add(docs.get("value").toString());
					/*
					 * if(docs.get("value").toString().contains(",")) { String
					 * spls[]=docs.get("value").toString().split(","); for(String splt:spls) {
					 * if(splt.trim().length()>0) { filterType.add(docs.get("filter").toString());
					 * filterValue.add(splt); } } } else {
					 * filterType.add(docs.get("filter").toString());
					 * filterValue.add(docs.get("value").toString().toUpperCase()); }
					 */
				}

				else if (docs.get("filter").toString().equalsIgnoreCase("Alarm range")) {
					if (docs.get("value").toString().contains("-")) {
						String val = docs.get("value").toString();
						String rangeFrom = val.split("-")[0].trim();
						String rangeTo = val.split("-")[1].trim();

						String rangeStart = "", rangeEnd = "";
						int start = 0, end = 0;
						for (int j = 0; j < rangeFrom.length(); j++) {
							for (int k = 0; k < rangeTo.length(); k++) {
								if (rangeTo.charAt(k) == rangeFrom.charAt(j)) {
									rangeStart = rangeStart + rangeFrom.charAt(j);
								} else {
									rangeEnd = rangeTo.substring(k);
									break;
								}
							}
						}
						start = Integer.parseInt(rangeFrom.substring(rangeStart.length()).trim());
						end = Integer.parseInt(rangeEnd.trim());
						for (int j = start; j <= end; j++) {
							filterType.add(docs.get("filter").toString());
							filterValue.add(rangeStart + "" + start);
						}
					}
				}
			}
			closeConnection(mongo);
		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
		}
	}

	public String getActiveAlarmsOnElementsFiltered(String userId, String domain, String vendor, String neipaddress,
			String nename) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into getActiveAlarmsOnElementsFiltered ****************");
		}
		try {

//			 System.out.println(neipaddress+nename+ vendor+"(-----------------");
			// System.out.println(nename);
			// System.out.println(vendor);
			ArrayList<String> columnsFinal = new ArrayList<String>();
			columnsFinal.add("SEVERITY");
			columnsFinal.add("DOMAIN");
			columnsFinal.add("VENDOR");
			columnsFinal.add("MANAGED_OBJECT");
			columnsFinal.add("ALARM_ID");
			columnsFinal.add("TIME");
			columnsFinal.add("NENAME");
			columnsFinal.add("NEIP");
			columnsFinal.add("ALARMNAME");
			columnsFinal.add("ALARM_INFO");
			columnsFinal.add("INSERTIONTIME");
			columnsFinal.add("STATUS");
			// columnsFinal.add("OCCURENCE");

			String colms = "", columns = "";

			ArrayList<TableHeaderAlarms> cols = new ArrayList<TableHeaderAlarms>();
			JSONArray vals = new JSONArray();
			for (String col : columnsFinal) {
				String colm = col;
				boolean visible = false;
				if (colm.equals("STATUS")) {
					visible = false;
				} else {
					visible = true;
				}
				TableHeaderAlarms th = new TableHeaderAlarms(colm, colm, visible);
				cols.add(th);

				if (col.trim().length() > 0) {
					colms += col + ",";
				}
			}
			cols.add(new TableHeaderAlarms("CLR", "CLR", false));

			columns = colms.substring(0, colms.length() - 1);

			ArrayList<String> filterType = new ArrayList<String>();
			ArrayList<String> filterValue = new ArrayList<String>();

			Properties config = getProperties();
			MongoClient mongo = getConnection();
			MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
			MongoCollection<Document> collection = database.getCollection("alarm_filter");
			ArrayList<Document> resultSet = collection
					.find(and(eq("user_id", userId), eq("for", "Topology"), eq("domain", domain), eq("vendor", vendor)))
					.into(new ArrayList<Document>());
			for (Document docs : resultSet) {
				if (docs.get("filter").toString().equalsIgnoreCase("Alarm Numbers")) {
					filterType.add(docs.get("filter").toString());
					filterValue.add(docs.get("value").toString());
					/*
					 * if(docs.get("value").toString().contains(",")) { String
					 * spls[]=docs.get("value").toString().split(","); for(String splt:spls) {
					 * if(splt.trim().length()>0) { filterType.add(docs.get("filter").toString());
					 * filterValue.add(splt); } } } else {
					 * filterType.add(docs.get("filter").toString());
					 * filterValue.add(docs.get("value").toString().toUpperCase()); }
					 */
				}

				else if (docs.get("filter").toString().equalsIgnoreCase("Alarm Name")) {
					filterType.add(docs.get("filter").toString());
					// System.out.println(docs.get("value").toString());
					filterValue.add(docs.get("value").toString());

					/*
					 * if(docs.get("value").toString().contains(",")) { String
					 * spls[]=docs.get("value").toString().split(","); for(String splt:spls) {
					 * if(splt.trim().length()>0) { filterType.add(docs.get("filter").toString());
					 * filterValue.add(splt); } } } else {
					 * filterType.add(docs.get("filter").toString());
					 * filterValue.add(docs.get("value").toString().toUpperCase()); }
					 */
				}

				else if (docs.get("filter").toString().equalsIgnoreCase("Alarm Severity")) {
					filterType.add(docs.get("filter").toString());
					filterValue.add(docs.get("value").toString());

					/*
					 * if(docs.get("value").toString().contains(",")) { String
					 * spls[]=docs.get("value").toString().split(","); for(String splt:spls) {
					 * if(splt.trim().length()>0) { filterType.add(docs.get("filter").toString());
					 * filterValue.add(splt); } } } else {
					 * filterType.add(docs.get("filter").toString());
					 * filterValue.add(docs.get("value").toString().toUpperCase()); }
					 */
				}

				else if (docs.get("filter").toString().equalsIgnoreCase("Alarm range")) {
					if (docs.get("value").toString().contains("-")) {
						String val = docs.get("value").toString();
						String rangeFrom = val.split("-")[0].trim();
						String rangeTo = val.split("-")[1].trim();

						String rangeStart = "", rangeEnd = "";
						int start = 0, end = 0;
						for (int j = 0; j < rangeFrom.length(); j++) {
							for (int k = 0; k < rangeTo.length(); k++) {
								if (rangeTo.charAt(k) == rangeFrom.charAt(j)) {
									rangeStart = rangeStart + rangeFrom.charAt(j);
								} else {
									rangeEnd = rangeTo.substring(k);
									break;
								}
							}
						}
						start = Integer.parseInt(rangeFrom.substring(rangeStart.length()).trim());
						end = Integer.parseInt(rangeEnd.trim());
						for (int j = start; j <= end; j++) {
							filterType.add(docs.get("filter").toString());
							filterValue.add(rangeStart + "" + start);
						}
					}
				}
			}

			String cond_severity = "", cond_sever = "";
			for (int i = 0; i < filterType.size(); i++) {
				if (filterType.get(i).equalsIgnoreCase("ALARM SEVERITY")) {
					cond_severity = cond_severity + "SEVERITY =" + filterValue.get(i) + " or ";
				}
			}
			if (cond_severity.length() > 0) {
				cond_sever = cond_severity.substring(0, cond_severity.length() - 4);
			}
			String almids = "", alarmidsfinal = "";
			for (int i = 0; i < filterType.size(); i++) {
				if (filterType.get(i).equalsIgnoreCase("ALARM NUMBERS")
						|| filterType.get(i).equalsIgnoreCase("ALARM RANGE")) {
					if (filterValue.get(i).contains(",")) {
						String spls[] = filterValue.get(i).split(",");
						for (String split : spls) {
							almids = almids + "ALARM_ID=" + split.trim() + " or ";
						}
						alarmidsfinal = almids.substring(0, almids.length() - 4).trim();
					} else {
						alarmidsfinal = "ALARM_ID=" + filterValue.get(i).trim();
					}
				}
			}

			String almnames = "", alarmnamesfinal = "";
			for (int i = 0; i < filterType.size(); i++) {
				if (filterType.get(i).equalsIgnoreCase("ALARM NAME")) {
					if (filterValue.get(i).contains(",")) {
						String spls[] = filterValue.get(i).split(",");
						for (String split : spls) {
							almnames = almnames + "ALARMNAME=" + split + " or ";
						}
						alarmnamesfinal = almnames.substring(0, almnames.length() - 4);
					} else {
						alarmnamesfinal = "ALARMNAME=" + filterValue.get(i);
					}
				}
			}
			String nenamefinal = "";
			if (nename.length() > 1) {
				if (nename.contains("~~")) {
					String nenameSpls[] = nename.split("~~");
					nenamefinal = "NEIP=" + neipaddress + " or NENAME="
							+ nenameSpls[0].replaceAll("@FORWARDSLASH@", "/") + " or NENAME="
							+ nenameSpls[1].replaceAll("@FORWARDSLASH@", "/");
				} else {
					nenamefinal = "NEIP=" + neipaddress + " or NENAME=" + nename.replaceAll("@FORWARDSLASH@", "/");
				}
			}
			String conditions = "VENDOR=" + vendor + " and DOMAIN=" + domain;
			if (cond_sever.length() > 0) {
				conditions = conditions + " and " + cond_sever;
			}
			if (alarmidsfinal.length() > 0) {
				conditions = conditions + " and " + alarmidsfinal;
			}
			if (alarmnamesfinal.length() > 0) {
				conditions = conditions + " and " + alarmnamesfinal;
			}
			if (nenamefinal.length() > 0) {
				conditions = conditions + " and " + nenamefinal;
			}

			String tablename = "activealarms";
			// System.out.println(conditions);
			JSONArray val = getActiveAlarmsOnWindowElement(vendor, domain, columnsFinal, tablename, columns, conditions,
					"INSERTIONTIME");

			// System.out.println(val.length());
			for (int i = 0; i < val.length(); i++) {

				vals.put(val.get(i));

			}

			JSONArray jsonArrayFinal = new JSONArray();
			JSONObject jsonObjectColVal = new JSONObject();
			jsonObjectColVal.put("cols", cols);
			jsonObjectColVal.put("vals", vals);
			jsonArrayFinal.put(jsonObjectColVal);
			// System.out.println(jsonArrayFinal.toString());
			return jsonArrayFinal.toString();

		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
			ex.printStackTrace();
		}
		return null;
	}

	public JSONArray getActiveAlarmsOnWindowElement(String vendor, String domain, ArrayList<String> columnsFinal,
			String tableName, String columns, String conditions, String orderby) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into getActiveAlarmsOnWindow ****************");
		}
		try {
			ArrayList<Bson> fltr = new ArrayList<Bson>();
			Bson filter = null;

			// System.out.println("inside condition==>"+conditions);

			if (conditions.length() > 1) {
				String cond = conditions.replace("@DOT@", ".").replace("@SEMICOLON@", ";");
				if (cond.contains(" and ")) {

					// System.out.println("condition1");
					String cond_spls[] = cond.split(" and ");
					for (String cv : cond_spls) {

						// System.out.println("==>"+cv);
						if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" or ")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							fltr.add(eq(col, val));
							// System.out.println("1==>"+fltr);
						}

						else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" or ")) {
							String col = cv.substring(0, cv.indexOf("like")).trim();
							String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "")
									.replace("@", "").replace("%", "").trim();
							if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^.*" + val + ".*", Pattern.CASE_INSENSITIVE)));
							} else {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
							}
						} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& cv.contains(" or ")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("like")).trim();
								String val = splt_or.substring(splt_or.indexOf("(") + 1, splt_or.lastIndexOf(")"))
										.replace("'", "").replace("@", "").replace("%", "").trim();
								String val1 = "";
								if (val.contains("(")) {
									val1 = val.substring(0, val.indexOf("("));
								} else {
									val1 = val;
								}
								if (!val1.toUpperCase().contains("MANAGEDELEMENT")) {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^.*" + val1 + ".*",
											Pattern.CASE_INSENSITIVE)));
								} else {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^" + val1 + ".*",
											Pattern.CASE_INSENSITIVE)));
								}
							}
							fltr.add(or(fltr_or));
						} else if (cv.contains("=") && cv.contains("between")) {
							String col = cv.substring(0, cv.indexOf("between")).trim();
							String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "")
									.trim();
							String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
							fltr.add(gte(col, val1));
							fltr.add(lte(col, val2));
						} else if (cv.contains("=") && cv.contains(" or ") && !cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
								String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
								String val1 = "";
								if (val.contains("(")) {
									val1 = val.substring(0, val.indexOf("("));
								} else {
									val1 = val;
								}
								if (!val1.toUpperCase().contains("MANAGEDELEMENT")) {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^.*" + val1 + ".*",
											Pattern.CASE_INSENSITIVE)));
								} else {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^" + val1 + ".*",
											Pattern.CASE_INSENSITIVE)));
								}
							}
							fltr.add(or(fltr_or));
							// System.out.println("2==>"+fltr);
						}
					}
				} else if (cond.contains(" AND ")) {

					// System.out.println("condition2");
					String cond_spls[] = cond.split(" AND ");
					for (String cv : cond_spls) {
						if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" OR ")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							fltr.add(eq(col, val));
						} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" OR ")) {
							String col = cv.substring(0, cv.indexOf("like")).trim();
							String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "")
									.replace("@", "").replace("%", "").trim();
							if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^.*" + val + ".*", Pattern.CASE_INSENSITIVE)));
							} else {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
							}
						} else if (cv.contains("=") && cv.contains("between")) {
							String col = cv.substring(0, cv.indexOf("between")).trim();
							String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "")
									.trim();
							String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
							fltr.add(gte(col, val1));
							fltr.add(lte(col, val2));
						} else if (cv.contains("=") && cv.contains(" OR ") && !cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
								String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
								fltr_or.add(eq(col, val));
							}
							fltr.add(or(fltr_or));
						}

					}
				} else if (!(cond.contains(" AND ") && !cond.contains(" and ")) && cond.length() > 1) {

					// System.out.println("condition3");
					if (cond.contains("=") && cond.contains("between") && !cond.contains(" or ")
							&& !cond.contains(" OR ")) {
						String col = cond.substring(0, cond.indexOf("between")).trim();
						String val1 = cond.substring(cond.indexOf("FROM=") + 5, cond.indexOf("TO=")).replace("'", "")
								.trim();
						String val2 = cond.substring(cond.indexOf("TO=") + 3).replace("'", "").trim();
						fltr.add(gte(col, val1));
						fltr.add(lte(col, val2));
					} else if (cond.contains(" or ") && !cond.contains("like") && !cond.contains("between")) {
						String or[] = cond.split(" or ");
						ArrayList<Bson> fltr_or = new ArrayList<Bson>();
						for (String splt_or : or) {
							String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
							String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
							fltr_or.add(eq(col, val));
						}
						fltr.add(or(fltr_or));
					} else if (cond.contains(" OR ") && !cond.contains("like") && !cond.contains("between")) {
						String or[] = cond.split(" OR ");
						ArrayList<Bson> fltr_or = new ArrayList<Bson>();
						for (String splt_or : or) {
							String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
							String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
							fltr_or.add(eq(col, val));
						}
						fltr.add(or(fltr_or));
					} else if (cond.contains("=")) {
						String col = cond.substring(0, cond.indexOf("=")).trim();
						String val = cond.substring(cond.indexOf("=") + 1).trim().replace("'", "");
						fltr.add(eq(col, val));
					}
				}
				filter = and(fltr);
			}

			// System.out.println(filter);
			Properties config = getProperties();
			MongoClient mongo = getConnection();
			MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
			MongoCollection<Document> collection = database.getCollection(tableName);
			ArrayList<String> cls = new ArrayList<String>();
			ArrayList<Document> resultSet = null;
			JSONArray vals = new JSONArray();

			Map<String, Object> groupMap = new HashMap<String, Object>();

			if (!columns.equals("*")) {
				if (columns.contains(",")) {
					String columns_spls[] = columns.split(",");
					for (String colm : columns_spls) {
						cls.add(colm);
						groupMap.put(colm, "$" + colm);
					}
				} else {
					cls.add(columns);
					groupMap.put(columns, "$" + columns);
				}
				DBObject groupFields = new BasicDBObject(groupMap);
				if (filter != null) {
					resultSet = collection
							.aggregate(Arrays.asList(match(filter), group(groupFields), project(fields(include(cls))),
									sort(descending("_id." + orderby)), limit(limitActiveAlarms)))
							.allowDiskUse(true).into(new ArrayList<Document>());
				} else {
					resultSet = collection
							.aggregate(Arrays.asList(group(groupFields), project(fields(include(cls))),
									sort(descending("_id." + orderby)), limit(limitActiveAlarms)))
							.allowDiskUse(true).into(new ArrayList<Document>());
				}

				for (Document documents : resultSet) {
					JSONObject colval = new JSONObject();
					Document docs = (Document) documents.get("_id");
					for (int j = 0; j < cls.size(); j++) {
						if (docs.containsKey(cls.get(j))) {
							if (docs.get(cls.get(j)).toString().length() > 0
									&& docs.get(cls.get(j)).toString() != null) {
								if (cls.get(j).equals("SEVERITY")) {
									colval.put(cls.get(j), docs.get(cls.get(j)).toString().replace("\n", " "));
									if (docs.getString("STATUS").equalsIgnoreCase("UNREAD")) {
										if (docs.getString(cls.get(j)).equalsIgnoreCase("CRITICAL")) {
											colval.put("CLR", config.getProperty("alarm_critical"));
										} else if (docs.getString(cls.get(j)).equalsIgnoreCase("MAJOR")) {
											colval.put("CLR", config.getProperty("alarm_major"));
										} else if (docs.getString(cls.get(j)).equalsIgnoreCase("MINOR")) {
											colval.put("CLR", config.getProperty("alarm_minor"));
										} else if (docs.getString(cls.get(j)).equalsIgnoreCase("WARNING")) {
											colval.put("CLR", config.getProperty("alarm_warning"));
										} else if (docs.getString(cls.get(j)).equalsIgnoreCase("INDETERMINATE")) {
											colval.put("CLR", config.getProperty("alarm_indeterminate"));
										}
									} else {
										colval.put("CLR", config.getProperty("alarm_read"));
									}
								} else {
									colval.put(cls.get(j), docs.get(cls.get(j)).toString().replace("\n", " "));
								}
							} else {
								colval.put(cls.get(j), "-");
							}
						} else {
							colval.put(cls.get(j), "-");
						}
					}
					if (!vals.similar(colval)) {
						vals.put(colval);
					}
				}
			}
			closeConnection(mongo);
			return vals;
		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
			ex.printStackTrace();
		}
		return null;
	}

	public String getHistoryAlarmsOnWindow(String tableName, String columns, String conditions, String orderby) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into getHistoryAlarmsOnWindow ****************");
		}

		try {
			ArrayList<Bson> fltr = new ArrayList<Bson>();
			Bson filter = null;
			if (conditions.length() > 1) {
				String cond = conditions.replace("@DOT@", ".").replace("@SEMICOLON@", ";");
				if (cond.contains(" and ")) {
					String cond_spls[] = cond.split(" and ");
					for (String cv : cond_spls) {
						if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" or ")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							fltr.add(eq(col, val));
						} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" or ")) {
							String col = cv.substring(0, cv.indexOf("like")).trim();
							String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "")
									.replace("@", "").replace("%", "").trim();
							if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^.*" + val + ".*", Pattern.CASE_INSENSITIVE)));
							} else {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
							}
						} else if (cv.contains("=") && cv.contains("between")) {
							String col = cv.substring(0, cv.indexOf("between")).trim();
							String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "")
									.trim();
							String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
							fltr.add(gte(col, val1));
							fltr.add(lte(col, val2));
						} else if (cv.contains("=") && cv.contains(" or ") && !cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
								String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
								fltr_or.add(eq(col, val));
							}
							fltr.add(or(fltr_or));
						} else if (!cv.contains("=") && cv.contains(" or ") && cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("like")).trim();
								String val = splt_or.substring(splt_or.indexOf("(") + 1, splt_or.lastIndexOf(")"))
										.replace("'", "").replace("@", "").replace("%", "").trim();
								if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^.*" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								} else {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								}
							}
							fltr.add(or(fltr_or));
						}
					}
				} else if (cond.contains(" AND ")) {
					String cond_spls[] = cond.split(" AND ");
					for (String cv : cond_spls) {
						if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" OR ")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							fltr.add(eq(col, val));
						} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" OR ")) {
							String col = cv.substring(0, cv.indexOf("like")).trim();
							String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "")
									.replace("@", "").replace("%", "").trim();
							if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^.*" + val + ".*", Pattern.CASE_INSENSITIVE)));
							} else {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
							}
						} else if (!cv.contains("=") && cv.contains(" OR ") && cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" OR ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("like")).trim();
								String val = splt_or.substring(splt_or.indexOf("(") + 1, splt_or.lastIndexOf(")"))
										.replace("'", "").replace("@", "").replace("%", "").trim();
								if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^.*" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								} else {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								}
							}
							fltr.add(or(fltr_or));
						} else if (cv.contains("=") && cv.contains("between")) {
							String col = cv.substring(0, cv.indexOf("between")).trim();
							String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "")
									.trim();
							String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
							fltr.add(gte(col, val1));
							fltr.add(lte(col, val2));
						} else if (cv.contains("=") && cv.contains(" OR ") && !cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
								String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
								fltr_or.add(eq(col, val));
							}
							fltr.add(or(fltr_or));
						}

					}
				} else if (!(cond.contains(" AND ") && !cond.contains(" and ")) && cond.length() > 1) {
					if (cond.contains("=") && cond.contains("between") && !cond.contains(" or ")
							&& !cond.contains(" OR ")) {
						String col = cond.substring(0, cond.indexOf("between")).trim();
						String val1 = cond.substring(cond.indexOf("FROM=") + 5, cond.indexOf("TO=")).replace("'", "")
								.trim();
						String val2 = cond.substring(cond.indexOf("TO=") + 3).replace("'", "").trim();
						fltr.add(gte(col, val1));
						fltr.add(lte(col, val2));
					} else if (cond.contains(" or ") && !cond.contains("like") && !cond.contains("between")) {
						String or[] = cond.split(" or ");
						ArrayList<Bson> fltr_or = new ArrayList<Bson>();
						for (String splt_or : or) {
							String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
							String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
							fltr_or.add(eq(col, val));
						}
						fltr.add(or(fltr_or));
					} else if (cond.contains(" OR ") && !cond.contains("like") && !cond.contains("between")) {
						String or[] = cond.split(" OR ");
						ArrayList<Bson> fltr_or = new ArrayList<Bson>();
						for (String splt_or : or) {
							String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
							String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
							fltr_or.add(eq(col, val));
						}
						fltr.add(or(fltr_or));
					} else if (cond.contains("=")) {
						String col = cond.substring(0, cond.indexOf("=")).trim();
						String val = cond.substring(cond.indexOf("=") + 1).trim().replace("'", "");
						fltr.add(eq(col, val));
					}
				}
				filter = and(fltr);
			}
			Properties config = getProperties();
			MongoClient mongo = getConnection();
			MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
			MongoCollection<Document> collection = database.getCollection(tableName);
			ArrayList<String> cls = new ArrayList<String>();
			ArrayList<Document> resultSet = null;
			ArrayList<TableHeader> cols = new ArrayList<TableHeader>();
			JSONArray vals = new JSONArray();

			Map<String, Object> groupMap = new HashMap<String, Object>();

			if (!columns.equals("*")) {
				if (columns.contains(",")) {
					String columns_spls[] = columns.split(",");
					for (String colm : columns_spls) {
						cls.add(colm);
						groupMap.put(colm, "$" + colm);
					}
				} else {
					cls.add(columns);
					groupMap.put(columns, "$" + columns);
				}

				DBObject groupFields = new BasicDBObject(groupMap);
				if (filter != null) {
					resultSet = collection
							.aggregate(Arrays.asList(match(filter), group(groupFields), project(fields(include(cls))),
									sort(descending("_id." + orderby)), limit(limitHistoryAlarms)))
							.allowDiskUse(true).into(new ArrayList<Document>());
				} else {
					resultSet = collection
							.aggregate(Arrays.asList(group(groupFields), project(fields(include(cls))),
									sort(descending("_id." + orderby)), limit(limitHistoryAlarms)))
							.allowDiskUse(true).into(new ArrayList<Document>());
				}
				String cols_spls[] = columns.split(",");
				for (String col : cols_spls) {
					String colm = col;
					TableHeader th = new TableHeader(colm, colm);
					cols.add(th);
				}

				for (Document docs : resultSet) {
					JSONObject colval = new JSONObject();
					String object = docs.get("_id").toString();
					String substr = object.substring(object.indexOf("{{") + 2, object.indexOf("}}"));
					String spls_cols[] = substr.split(",");
					for (String cv : spls_cols) {
						String cl = cv.substring(0, cv.indexOf("=")).trim();
						String vl = cv.substring(cv.indexOf("=") + 1).trim().replace(",", "@COMMA@")
								.replace("/", "@FORWARDSLASH@").replace("\\", "@BACKWARDSLASH@").replace("\"", "")
								.replace("\n", " ");// .replace("@COMMA@",
													// "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","")
						colval.put(cl, vl);
					}
					if (!vals.similar(colval)) {
						vals.put(colval);
					}
				}
			} else {
				if (filter != null) {
					resultSet = collection.find(filter).limit(limitHistoryAlarms).into(new ArrayList<Document>());
				} else {
					resultSet = collection.find().limit(limitHistoryAlarms).into(new ArrayList<Document>());
				}
				int size = 0;
				int max = 0;
				Document doc = null;
				for (int i = 0; i < resultSet.size(); i++) {
					Document document = resultSet.get(i);
					size = document.keySet().size();
					if (size > max) {
						max = size;
						doc = document;
						break;
					}
				}
				Iterator<String> itr = doc.keySet().iterator();
				while (itr.hasNext()) {
					String col = itr.next().toString();
					if (!col.equals("_id")) {
						cls.add(col);
						TableHeader th = new TableHeader(col, col);
						cols.add(th);
					}
				}
				for (Document docs : resultSet) {
					JSONObject colval = new JSONObject();
					for (int j = 0; j < cls.size(); j++) {
						if (docs.containsKey(cls.get(j))) {
							if (docs.get(cls.get(j)).toString().length() > 0
									&& docs.get(cls.get(j)).toString() != null) {
								colval.put(cls.get(j),
										docs.get(cls.get(j)).toString().replace(",", "@COMMA@")
												.replace("/", "@FORWARDSLASH@").replace("\\", "@BACKWARDSLASH@")
												.replace("\"", "").replace("\n", " "));
							} else {
								colval.put(cls.get(j), "-");
							}
						} else {
							colval.put(cls.get(j), "-");
						}
					}
					if (!vals.similar(colval)) {
						vals.put(colval);
					}
				}

			}

			JSONArray jsonArrayFinal = new JSONArray();
			JSONObject jsonObjectColVal = new JSONObject();
			jsonObjectColVal.put("cols", cols);
			jsonObjectColVal.put("vals", vals);
			jsonArrayFinal.put(jsonObjectColVal);
			closeConnection(mongo);
			String output = jsonArrayFinal.toString();
			return output;
		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
		}
		return null;
	}

	public String getActiveAlarmsOnElements(String tableName, String columns, String conditions, String orderby) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into getActiveAlarmsOnElements ****************");
		}
		try {
			ArrayList<Bson> fltr = new ArrayList<Bson>();
			Bson filter = null;
			if (conditions.length() > 1) {
				String cond = conditions.replace("@DOT@", ".").replace("@SEMICOLON@", ";").replace("@FORWARDSLASH@",
						"/");
				if (cond.contains(" and ")) {
					String cond_spls[] = cond.split(" and ");
					for (String cv : cond_spls) {
						if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" or ")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							fltr.add(eq(col, val));
						} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" or ")) {
							String col = cv.substring(0, cv.indexOf("like")).trim();
							String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "")
									.replace("@", "").replace("%", "").trim();
							if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^.*" + val + ".*", Pattern.CASE_INSENSITIVE)));
							} else {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
							}
						} else if (!cv.contains("=") && cv.contains(" or ") && cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("like")).trim();
								String val = splt_or.substring(splt_or.indexOf("(") + 1, splt_or.lastIndexOf(")"))
										.replace("'", "").replace("@", "").replace("%", "").trim();
								if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^.*" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								} else {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								}
							}
							fltr.add(or(fltr_or));
						} else if (cv.contains("=") && cv.contains("between")) {
							String col = cv.substring(0, cv.indexOf("between")).trim();
							String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "")
									.trim();
							String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
							fltr.add(gte(col, val1));
							fltr.add(lte(col, val2));
						} else if (cv.contains("=") && cv.contains(" or ") && !cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
								String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
								fltr_or.add(eq(col, val));
							}
							fltr.add(or(fltr_or));
						}
					}
				} else if (cond.contains(" AND ")) {
					String cond_spls[] = cond.split(" AND ");
					for (String cv : cond_spls) {
						if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" OR ")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							fltr.add(eq(col, val));
						} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" OR ")) {
							String col = cv.substring(0, cv.indexOf("like")).trim();
							String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "")
									.replace("@", "").replace("%", "").trim();
							if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^.*" + val + ".*", Pattern.CASE_INSENSITIVE)));
							} else {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
							}
						} else if (!cv.contains("=") && cv.contains(" OR ") && cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" OR ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("like")).trim();
								String val = splt_or.substring(splt_or.indexOf("(") + 1, splt_or.lastIndexOf(")"))
										.replace("'", "").replace("@", "").replace("%", "").trim();
								if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^.*" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								} else {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								}
							}
							fltr.add(or(fltr_or));
						} else if (cv.contains("=") && cv.contains("between")) {
							String col = cv.substring(0, cv.indexOf("between")).trim();
							String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "")
									.trim();
							String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
							fltr.add(gte(col, val1));
							fltr.add(lte(col, val2));
						} else if (cv.contains("=") && cv.contains(" OR ") && !cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
								String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
								fltr_or.add(eq(col, val));
							}
							fltr.add(or(fltr_or));
						}

					}
				} else if (!(cond.contains(" AND ") && !cond.contains(" and ")) && cond.length() > 1) {
					if (cond.contains("=") && cond.contains("between") && !cond.contains(" or ")
							&& !cond.contains(" OR ")) {
						String col = cond.substring(0, cond.indexOf("between")).trim();
						String val1 = cond.substring(cond.indexOf("FROM=") + 5, cond.indexOf("TO=")).replace("'", "")
								.trim();
						String val2 = cond.substring(cond.indexOf("TO=") + 3).replace("'", "").trim();
						fltr.add(gte(col, val1));
						fltr.add(lte(col, val2));
					} else if (cond.contains(" or ") && !cond.contains("like") && !cond.contains("between")) {
						String or[] = cond.split(" or ");
						ArrayList<Bson> fltr_or = new ArrayList<Bson>();
						for (String splt_or : or) {
							String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
							String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
							fltr_or.add(eq(col, val));
						}
						fltr.add(or(fltr_or));
					} else if (cond.contains(" OR ") && !cond.contains("like") && !cond.contains("between")) {
						String or[] = cond.split(" OR ");
						ArrayList<Bson> fltr_or = new ArrayList<Bson>();
						for (String splt_or : or) {
							String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
							String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
							fltr_or.add(eq(col, val));
						}
						fltr.add(or(fltr_or));
					} else if (cond.contains("=")) {
						String col = cond.substring(0, cond.indexOf("=")).trim();
						String val = cond.substring(cond.indexOf("=") + 1).trim().replace("'", "");
						fltr.add(eq(col, val));
					}
				}
				filter = and(fltr);
			}

			Properties config = getProperties();
			MongoClient mongo = getConnection();
			MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
			MongoCollection<Document> collection = database.getCollection(tableName);
			ArrayList<String> cls = new ArrayList<String>();
			ArrayList<Document> resultSet = null;
			ArrayList<TableHeader> cols = new ArrayList<TableHeader>();
			JSONArray vals = new JSONArray();

			Map<String, Object> groupMap = new HashMap<String, Object>();

			if (!columns.equals("*")) {
				if (columns.contains(",")) {
					String columns_spls[] = columns.split(",");
					for (String colm : columns_spls) {
						cls.add(colm);
						groupMap.put(colm, "$" + colm);
					}
				} else {
					cls.add(columns);
					groupMap.put(columns, "$" + columns);
				}

				DBObject groupFields = new BasicDBObject(groupMap);
				if (filter != null) {
					resultSet = collection
							.aggregate(Arrays.asList(match(filter), group(groupFields), project(fields(include(cls))),
									sort(descending("_id." + orderby)), limit(1000)))
							.allowDiskUse(true).into(new ArrayList<Document>());
				} else {
					resultSet = collection
							.aggregate(Arrays.asList(group(groupFields), project(fields(include(cls))),
									sort(descending("_id." + orderby)), limit(1000)))
							.allowDiskUse(true).into(new ArrayList<Document>());
				}
				String cols_spls[] = columns.split(",");
				for (String col : cols_spls) {
					String colm = col;
					TableHeader th = new TableHeader(colm, colm);
					cols.add(th);
				}

				for (Document docs : resultSet) {
					JSONObject colval = new JSONObject();
					Document doc = (Document) docs.get("_id");
					for (int j = 0; j < cls.size(); j++) {
						if (doc.containsKey(cls.get(j))) {
							if (doc.get(cls.get(j)).toString().length() > 0 && doc.get(cls.get(j)).toString() != null) {
								colval.put(cls.get(j), doc.get(cls.get(j)));
							} else {
								colval.put(cls.get(j), "-");
							}
						} else {
							colval.put(cls.get(j), "-");
						}
					}

					if (!vals.similar(colval)) {
						vals.put(colval);
					}
				}
			} else {
				if (filter != null) {
					resultSet = collection.find(filter).into(new ArrayList<Document>());
				} else {
					resultSet = collection.find().into(new ArrayList<Document>());
				}
				int size = 0;
				int max = 0;
				Document doc = null;
				if (resultSet.size() > 0) {
					for (int i = 0; i < resultSet.size(); i++) {
						Document document = resultSet.get(i);
						size = document.keySet().size();
						if (size > max) {
							max = size;
							doc = document;
						}
					}
					Iterator<String> itr = doc.keySet().iterator();
					while (itr.hasNext()) {
						String col = itr.next().toString();
						if (!col.equals("_id")) {
							cls.add(col);
							TableHeader th = new TableHeader(col, col);
							cols.add(th);
						}
					}
					for (Document docs : resultSet) {
						JSONObject colval = new JSONObject();
						for (int j = 0; j < cls.size(); j++) {
							if (docs.containsKey(cls.get(j))) {
								if (docs.get(cls.get(j)).toString().length() > 0
										&& docs.get(cls.get(j)).toString() != null) {
									colval.put(cls.get(j), docs.get(cls.get(j)));
								} else {
									colval.put(cls.get(j), "-");
								}
							} else {
								colval.put(cls.get(j), "-");
							}
						}
						if (!vals.similar(colval)) {
							vals.put(colval);
						}
					}
				}

			}

			JSONArray jsonArrayFinal = new JSONArray();
			JSONObject jsonObjectColVal = new JSONObject();
			jsonObjectColVal.put("cols", cols);
			jsonObjectColVal.put("vals", vals);
			jsonArrayFinal.put(jsonObjectColVal);
			closeConnection(mongo);
			String output = jsonArrayFinal.toString();
			return output;
		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
		}
		return null;
	}

	public String getAlarmsDescription(String tableName, String columns, String conditions) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into getAlarmsDescription ****************");
		}

		try {
			ArrayList<Bson> fltr = new ArrayList<Bson>();
			Bson filter = null;
			if (conditions.length() > 1) {
				String cond = conditions.replaceAll("@FORWARDSLASH@", "/").replace("@DOT@", ".").replace("@SEMICOLON@",
						";");
				if (cond.contains(" and ")) {
					String cond_spls[] = cond.split(" and ");
					for (String cv : cond_spls) {
						if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" or ")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							fltr.add(eq(col, val));
						} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" or ")) {
							String col = cv.substring(0, cv.indexOf("like")).trim();
							String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "")
									.replace("@", "").replace("%", "").trim();
							if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^.*" + val + ".*", Pattern.CASE_INSENSITIVE)));
							} else {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
							}
						} else if (cv.contains("=") && cv.contains("between")) {
							String col = cv.substring(0, cv.indexOf("between")).trim();
							String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "")
									.trim();
							String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
							fltr.add(gte(col, val1));
							fltr.add(lte(col, val2));
						} else if (cv.contains("=") && cv.contains(" or ") && !cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
								String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
								fltr_or.add(eq(col, val));
							}
							fltr.add(or(fltr_or));
						}
					}
				} else if (cond.contains(" AND ")) {
					String cond_spls[] = cond.split(" AND ");
					for (String cv : cond_spls) {
						if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" OR ")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							fltr.add(eq(col, val));
						} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" OR ")) {
							String col = cv.substring(0, cv.indexOf("like")).trim();
							String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "")
									.replace("@", "").replace("%", "").trim();
							if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^.*" + val + ".*", Pattern.CASE_INSENSITIVE)));
							} else {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
							}
						} else if (cv.contains("=") && cv.contains("between")) {
							String col = cv.substring(0, cv.indexOf("between")).trim();
							String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "")
									.trim();
							String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
							fltr.add(gte(col, val1));
							fltr.add(lte(col, val2));
						} else if (cv.contains("=") && cv.contains(" OR ") && !cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
								String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
								fltr_or.add(eq(col, val));
							}
							fltr.add(or(fltr_or));
						}

					}
				} else if (!(cond.contains(" AND ") && !cond.contains(" and ")) && cond.length() > 1) {
					if (cond.contains("=") && cond.contains("between") && !cond.contains(" or ")
							&& !cond.contains(" OR ")) {
						String col = cond.substring(0, cond.indexOf("between")).trim();
						String val1 = cond.substring(cond.indexOf("FROM=") + 5, cond.indexOf("TO=")).replace("'", "")
								.trim();
						String val2 = cond.substring(cond.indexOf("TO=") + 3).replace("'", "").trim();
						fltr.add(gte(col, val1));
						fltr.add(lte(col, val2));
					} else if (cond.contains(" or ") && !cond.contains("like") && !cond.contains("between")) {
						String or[] = cond.split(" or ");
						ArrayList<Bson> fltr_or = new ArrayList<Bson>();
						for (String splt_or : or) {
							String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
							String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
							fltr_or.add(eq(col, val));
						}
						fltr.add(or(fltr_or));
					} else if (cond.contains(" OR ") && !cond.contains("like") && !cond.contains("between")) {
						String or[] = cond.split(" OR ");
						ArrayList<Bson> fltr_or = new ArrayList<Bson>();
						for (String splt_or : or) {
							String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
							String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
							fltr_or.add(eq(col, val));
						}
						fltr.add(or(fltr_or));
					} else if (cond.contains("=")) {
						String col = cond.substring(0, cond.indexOf("=")).trim();
						String val = cond.substring(cond.indexOf("=") + 1).trim().replace("'", "");
						fltr.add(eq(col, val));
					}
				}
				filter = and(fltr);
			}
			Properties config = getProperties();
			MongoClient mongo = getConnection();
			MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
			MongoCollection<Document> collection = database.getCollection(tableName);
			ArrayList<String> cls = new ArrayList<String>();
			ArrayList<Document> resultSet = null;
			ArrayList<TableHeader> cols = new ArrayList<TableHeader>();
			JSONArray vals = new JSONArray();

			Map<String, Object> groupMap = new HashMap<String, Object>();

			if (!columns.equals("*")) {
				if (columns.contains(",")) {
					String columns_spls[] = columns.split(",");
					for (String colm : columns_spls) {
						cls.add(colm);
						groupMap.put(colm, "$" + colm);
					}
				} else {
					cls.add(columns);
					groupMap.put(columns, "$" + columns);
				}

				DBObject groupFields = new BasicDBObject(groupMap);
				if (filter != null) {
					resultSet = collection.aggregate(
							Arrays.asList(match(filter), group(groupFields), project(fields(include(cls))), limit(1)))
							.allowDiskUse(true).into(new ArrayList<Document>());
				} else {
					resultSet = collection
							.aggregate(Arrays.asList(group(groupFields), project(fields(include(cls))), limit(1)))
							.allowDiskUse(true).into(new ArrayList<Document>());
				}
				String cols_spls[] = columns.split(",");
				for (String col : cols_spls) {
					String colm = col;
					TableHeader th = new TableHeader(colm, colm);
					cols.add(th);
				}

				for (Document docs : resultSet) {
					JSONObject colval = new JSONObject();
					Document doc = (Document) docs.get("_id");
					for (int j = 0; j < cls.size(); j++) {
						if (doc.containsKey(cls.get(j))) {
							if (doc.get(cls.get(j)).toString().length() > 0 && doc.get(cls.get(j)).toString() != null) {
								colval.put(cls.get(j), doc.get(cls.get(j)));
							} else {
								colval.put(cls.get(j), "-");
							}
						} else {
							colval.put(cls.get(j), "-");
						}
					}
//JSONObject obj=new JSONObject(object);
					/*
					 * String substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}")).
					 * replace(", ", " "); String spls_cols[]=substr.split(","); for(String
					 * cv:spls_cols) { String cl=cv.substring(0,cv.indexOf("=")).trim(); String
					 * vl=cv.substring(cv.indexOf("=")+1).trim().replace(",",
					 * "@COMMA@").replace("/","@FORWARDSLASH@").replace("\\","@BACKWARDSLASH@
					 * ").replace("\"","");//.replace("@COMMA@",
					 * "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").
					 * replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","") colval.put(cl, vl); }
					 */
					if (!vals.similar(colval)) {
						vals.put(colval);
					}
				}
			} else {
				if (filter != null) {
					resultSet = collection.find(filter).limit(1).into(new ArrayList<Document>());
				} else {
					resultSet = collection.find().limit(1).into(new ArrayList<Document>());
				}

				int size = 0;
				int max = 0;
				Document doc = null;
				for (int i = 0; i < resultSet.size(); i++) {
					Document document = resultSet.get(i);
					size = document.keySet().size();
					if (size > max) {
						max = size;
						doc = document;
					}
				}
				Iterator<String> itr = doc.keySet().iterator();
				while (itr.hasNext()) {
					String col = itr.next().toString();
					if (!col.equals("_id")) {
						cls.add(col);
						TableHeader th = new TableHeader(col, col);
						cols.add(th);
					}
				}
				for (Document docs : resultSet) {
					JSONObject colval = new JSONObject();
					for (int j = 0; j < cls.size(); j++) {
						if (docs.containsKey(cls.get(j))) {
							if (docs.get(cls.get(j)).toString().length() > 0
									&& docs.get(cls.get(j)).toString() != null) {
								colval.put(cls.get(j), docs.get(cls.get(j)));
							} else {
								colval.put(cls.get(j), "-");
							}
						} else {
							colval.put(cls.get(j), "-");
						}
					}
					if (!vals.similar(colval)) {
						vals.put(colval);
					}
				}

			}

			JSONArray jsonArrayFinal = new JSONArray();
			JSONObject jsonObjectColVal = new JSONObject();
			jsonObjectColVal.put("cols", cols);
			jsonObjectColVal.put("vals", vals);
			jsonArrayFinal.put(jsonObjectColVal);
			closeConnection(mongo);
			String output = jsonArrayFinal.toString();
			return output;
		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
		}
		return null;
	}

	public HashMap<String, ArrayList<StringInt>> add(HashMap<String, ArrayList<StringInt>> getData, String c,
			StringInt subAL) {
		if (getData.containsKey(c)) {
			ArrayList<StringInt> xOld = getData.get(c);
			xOld.add(subAL);
			getData.put(c, xOld);
		} else {
			ArrayList<StringInt> xNew = new ArrayList<StringInt>();
			xNew.add(subAL);
			getData.put(c, xNew);
		}
		return getData;
	}

	public int postActiveAlarmsOnWindowStatusChange(String data) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into postActiveAlarmsOnWindowStatusChange ****************");
		}
		try {
			Properties config = getProperties();
			MongoClient mongo = getConnection();
			MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
			MongoCollection<Document> collection = database.getCollection("activealarms");

			String spls[] = data.split(";");
			Bson filter = null;
			String status = "";
			ArrayList<Bson> fltr = new ArrayList<Bson>();
			for (String splt : spls) {
				if (!splt.contains("USERID") && !splt.contains("STATUS")) {
					fltr.add(eq(splt.split(":=")[0].trim(), splt.split(":=")[1].trim()));
				} else if (splt.contains("STATUS")) {
					status = splt.split(":=")[1].trim();
				}
			}

			filter = and(fltr);
			collection.updateOne(filter, Updates.set("STATUS", status));
			closeConnection(mongo);
			return 1;
		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
		}
		return 0;
	}

	public int postDataAlarmFilter(GenericPostBody genericPostBody) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into postDataAlarmFilter ****************");
		}
		try {

			Properties config = getProperties();
			MongoClient mongo = getConnection();
			MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
			MongoCollection<Document> collection = database.getCollection("alarm_filter");

			String user_id = genericPostBody.getUserId();
			ArrayList<String> domains = new ArrayList<String>();
			ArrayList<String> vendors = new ArrayList<String>();
			ArrayList<String> types = new ArrayList<String>();
			ArrayList<String> fors = new ArrayList<String>();
			ArrayList<String> severities = new ArrayList<String>();
			String filterlist = "", filtervalue = "";

			domains = genericPostBody.getDomainlist();
			vendors = genericPostBody.getVendorlist();
			types = genericPostBody.getTypelist();
			fors = genericPostBody.getForlist();
			severities = genericPostBody.getSeveritylist();
			filterlist = genericPostBody.getFilterlist();
			filtervalue = genericPostBody.getFiltervalue();

			for (int i = 0; i < domains.size(); i++) {
				if (domains.get(i).equalsIgnoreCase("IPRAN") && vendors.contains("Huawei")) {
					for (int j = 0; j < types.size(); j++) {
						if (types.get(j) != null && types.get(j).length() > 0) {
							for (int k = 0; k < fors.size(); k++) {
								if (fors.get(k) != null && fors.get(k).length() > 0) {
									if (severities.size() > 0) {
										for (int l = 0; l < severities.size(); l++) {
											if (severities.get(l) != null && severities.get(l).length() > 0) {
												Document document = new Document();
												document.append("user_id", user_id).append("domain", domains.get(i))
														.append("vendor", "Huawei").append("type", types.get(j))
														.append("for", fors.get(k)).append("filter", filterlist)
														.append("value", severities.get(l));
												collection.insertOne(document);
											}
										}
									} else if (filtervalue.length() > 0) {
										Document document = new Document();
										document.append("user_id", user_id).append("domain", domains.get(i))
												.append("vendor", "Huawei").append("type", types.get(j))
												.append("for", fors.get(k)).append("filter", filterlist)
												.append("value", filtervalue);
										collection.insertOne(document);
									}
								}
							}
						}
					}
				}
				if (domains.get(i).equalsIgnoreCase("MPBN") && vendors.contains("Nokia")) {
					for (int j = 0; j < types.size(); j++) {
						if (types.get(j) != null && types.get(j).length() > 0) {
							for (int k = 0; k < fors.size(); k++) {
								if (fors.get(k) != null && fors.get(k).length() > 0) {
									if (severities.size() > 0) {
										for (int l = 0; l < severities.size(); l++) {
											if (severities.get(l) != null && severities.get(l).length() > 0) {
												Document document = new Document();
												document.append("user_id", user_id).append("domain", domains.get(i))
														.append("vendor", "Nokia").append("type", types.get(j))
														.append("for", fors.get(k)).append("filter", filterlist)
														.append("value", severities.get(l));
												collection.insertOne(document);
											}
										}
									} else if (filtervalue.length() > 0) {
										Document document = new Document();
										document.append("user_id", user_id).append("domain", domains.get(i))
												.append("vendor", "Nokia").append("type", types.get(j))
												.append("for", fors.get(k)).append("filter", filterlist)
												.append("value", filtervalue);
										collection.insertOne(document);
									}
								}
							}
						}
					}
				}
				if (domains.get(i).equalsIgnoreCase("MPBN") && vendors.contains("Zte")) {
					for (int j = 0; j < types.size(); j++) {
						if (types.get(j) != null && types.get(j).length() > 0) {
							for (int k = 0; k < fors.size(); k++) {
								if (fors.get(k) != null && fors.get(k).length() > 0) {
									if (severities.size() > 0) {
										for (int l = 0; l < severities.size(); l++) {
											if (severities.get(l) != null && severities.get(l).length() > 0) {
												Document document = new Document();
												document.append("user_id", user_id).append("domain", domains.get(i))
														.append("vendor", "Zte").append("type", types.get(j))
														.append("for", fors.get(k)).append("filter", filterlist)
														.append("value", severities.get(l));
												collection.insertOne(document);
											}
										}
									} else if (filtervalue.length() > 0) {
										Document document = new Document();
										document.append("user_id", user_id).append("domain", domains.get(i))
												.append("vendor", "Zte").append("type", types.get(j))
												.append("for", fors.get(k)).append("filter", filterlist)
												.append("value", filtervalue);
										collection.insertOne(document);
									}
								}
							}
						}
					}
				}
				if (domains.get(i).equalsIgnoreCase("MPBN") && vendors.contains("Ericsson")) {
					for (int j = 0; j < types.size(); j++) {
						if (types.get(j) != null && types.get(j).length() > 0) {
							for (int k = 0; k < fors.size(); k++) {
								if (fors.get(k) != null && fors.get(k).length() > 0) {
									if (severities.size() > 0) {
										for (int l = 0; l < severities.size(); l++) {
											if (severities.get(l) != null && severities.get(l).length() > 0) {
												Document document = new Document();
												document.append("user_id", user_id).append("domain", domains.get(i))
														.append("vendor", "Ericsson").append("type", types.get(j))
														.append("for", fors.get(k)).append("filter", filterlist)
														.append("value", severities.get(l));
												collection.insertOne(document);
											}
										}
									} else if (filtervalue.length() > 0) {
										Document document = new Document();
										document.append("user_id", user_id).append("domain", domains.get(i))
												.append("vendor", "Ericsson").append("type", types.get(j))
												.append("for", fors.get(k)).append("filter", filterlist)
												.append("value", filtervalue);
										collection.insertOne(document);
									}
								}
							}
						}
					}
				}
				if (domains.get(i).equalsIgnoreCase("MPBN") && vendors.contains("Juniper")) {
					for (int j = 0; j < types.size(); j++) {
						if (types.get(j) != null && types.get(j).length() > 0) {
							for (int k = 0; k < fors.size(); k++) {
								if (fors.get(k) != null && fors.get(k).length() > 0) {
									if (severities.size() > 0) {
										for (int l = 0; l < severities.size(); l++) {
											if (severities.get(l) != null && severities.get(l).length() > 0) {
												Document document = new Document();
												document.append("user_id", user_id).append("domain", domains.get(i))
														.append("vendor", "Juniper").append("type", types.get(j))
														.append("for", fors.get(k)).append("filter", filterlist)
														.append("value", severities.get(l));
												collection.insertOne(document);
											}
										}
									} else if (filtervalue.length() > 0) {
										Document document = new Document();
										document.append("user_id", user_id).append("domain", domains.get(i))
												.append("vendor", "Juniper").append("type", types.get(j))
												.append("for", fors.get(k)).append("filter", filterlist)
												.append("value", filtervalue);
										collection.insertOne(document);
									}
								}
							}
						}
					}
				}
				if (domains.get(i).equalsIgnoreCase("TRANSMISSION") && vendors.contains("Nokia")) {
					for (int j = 0; j < types.size(); j++) {
						if (types.get(j) != null && types.get(j).length() > 0) {
							for (int k = 0; k < fors.size(); k++) {
								if (fors.get(k) != null && fors.get(k).length() > 0) {
									if (severities.size() > 0) {
										for (int l = 0; l < severities.size(); l++) {
											if (severities.get(l) != null && severities.get(l).length() > 0) {
												Document document = new Document();
												document.append("user_id", user_id).append("domain", domains.get(i))
														.append("vendor", "Nokia").append("type", types.get(j))
														.append("for", fors.get(k)).append("filter", filterlist)
														.append("value", severities.get(l));
												collection.insertOne(document);
											}
										}
									} else if (filtervalue.length() > 0) {
										Document document = new Document();
										document.append("user_id", user_id).append("domain", domains.get(i))
												.append("vendor", "Nokia").append("type", types.get(j))
												.append("for", fors.get(k)).append("filter", filterlist)
												.append("value", filtervalue);
										collection.insertOne(document);
									}
								}
							}
						}
					}
				}
				if (domains.get(i).equalsIgnoreCase("TRANSMISSION") && vendors.contains("Ericsson")) {
					for (int j = 0; j < types.size(); j++) {
						if (types.get(j) != null && types.get(j).length() > 0) {
							for (int k = 0; k < fors.size(); k++) {
								if (fors.get(k) != null && fors.get(k).length() > 0) {
									if (severities.size() > 0) {
										for (int l = 0; l < severities.size(); l++) {
											if (severities.get(l) != null && severities.get(l).length() > 0) {
												Document document = new Document();
												document.append("user_id", user_id).append("domain", domains.get(i))
														.append("vendor", "Ericsson").append("type", types.get(j))
														.append("for", fors.get(k)).append("filter", filterlist)
														.append("value", severities.get(l));
												collection.insertOne(document);
											}
										}
									} else if (filtervalue.length() > 0) {
										Document document = new Document();
										document.append("user_id", user_id).append("domain", domains.get(i))
												.append("vendor", "Ericsson").append("type", types.get(j))
												.append("for", fors.get(k)).append("filter", filterlist)
												.append("value", filtervalue);
										collection.insertOne(document);
									}
								}
							}
						}
					}
				}
				if (domains.get(i).equalsIgnoreCase("TRANSMISSION") && vendors.contains("Huawei")) {
					for (int j = 0; j < types.size(); j++) {
						if (types.get(j) != null && types.get(j).length() > 0) {
							for (int k = 0; k < fors.size(); k++) {
								if (fors.get(k) != null && fors.get(k).length() > 0) {
									if (severities.size() > 0) {
										for (int l = 0; l < severities.size(); l++) {
											if (severities.get(l) != null && severities.get(l).length() > 0) {
												Document document = new Document();
												document.append("user_id", user_id).append("domain", domains.get(i))
														.append("vendor", "Huawei").append("type", types.get(j))
														.append("for", fors.get(k)).append("filter", filterlist)
														.append("value", severities.get(l));
												collection.insertOne(document);
											}
										}
									} else if (filtervalue.length() > 0) {
										Document document = new Document();
										document.append("user_id", user_id).append("domain", domains.get(i))
												.append("vendor", "Huawei").append("type", types.get(j))
												.append("for", fors.get(k)).append("filter", filterlist)
												.append("value", filtervalue);
										collection.insertOne(document);
									}
								}
							}
						}
					}
				}
				if (domains.get(i).equalsIgnoreCase("TRANSMISSION") && vendors.contains("Nec")) {
					for (int j = 0; j < types.size(); j++) {
						if (types.get(j) != null && types.get(j).length() > 0) {
							for (int k = 0; k < fors.size(); k++) {
								if (fors.get(k) != null && fors.get(k).length() > 0) {
									if (severities.size() > 0) {
										for (int l = 0; l < severities.size(); l++) {
											if (severities.get(l) != null && severities.get(l).length() > 0) {
												Document document = new Document();
												document.append("user_id", user_id).append("domain", domains.get(i))
														.append("vendor", "Nec").append("type", types.get(j))
														.append("for", fors.get(k)).append("filter", filterlist)
														.append("value", severities.get(l));
												collection.insertOne(document);
											}
										}
									} else if (filtervalue.length() > 0) {
										Document document = new Document();
										document.append("user_id", user_id).append("domain", domains.get(i))
												.append("vendor", "Nec").append("type", types.get(j))
												.append("for", fors.get(k)).append("filter", filterlist)
												.append("value", filtervalue);
										collection.insertOne(document);
									}
								}
							}
						}
					}
				}

			}

			closeConnection(mongo);
			return 1;
		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
		}
		return 0;
	}

	public int postAcknowledgeAlarms(GenericPostBody genericPostBody) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into AcknowledgeAlarms ****************");
		}
		int result = 0;
		Properties config = getProperties();
		try {
			if (genericPostBody.getAlarmProtocol().equalsIgnoreCase("CORBA")
					&& genericPostBody.getAlarmDomain().equalsIgnoreCase("RADIO")) {

				Path rootLocation = Paths.get(config.getProperty("server.directory"));
				String file_store = genericPostBody.getFileName();
				String nameServiceIOR = null;
				File file_check = new File(rootLocation + "\\" + file_store);
				if (file_check.exists()) {

					File file = ResourceUtils.getFile(rootLocation + "\\" + file_store);

					BufferedReader br;

					try {
						br = new BufferedReader(new FileReader(file));
						nameServiceIOR = br.readLine();
						br.close();
					} catch (Exception ex) {
						log.error("Exception occurs:----" + ex.getMessage(), ex);
					}
				}
				String args[] = null;

				if (log.isDebugEnabled()) {
					log.debug("*************** NameServiceIOR=" + nameServiceIOR + " ****************");
				}

				ORB orb = ORB.init(args, null);

				org.omg.CORBA.Object ref = orb.string_to_object(nameServiceIOR);

				NamingContextExt nameContext = NamingContextExtHelper.narrow(ref);

				org.omg.CORBA.Object alarmIRPIOR = nameContext
						.resolve(nameContext.to_name(genericPostBody.getResolveString()));
//System.out.println("ref = " + ref);

				if (log.isDebugEnabled()) {
					log.debug("*************** AlarmIRPIOR=" + alarmIRPIOR + " ****************");
				}
				/*
				 * AlarmIRP alarmIRP=AlarmIRPHelper.narrow(alarmIRPIOR);
				 *
				 * BadAcknowledgeAlarmInfoSeqHolder badackalarmholder = new
				 * BadAcknowledgeAlarmInfoSeqHolder(); StringTypeOpt opt = new StringTypeOpt();
				 * opt.value(genericPostBody.getSystemId()); AlarmInformationIdAndSev alarm =
				 * new AlarmInformationIdAndSev(); ShortTypeOpt op = new ShortTypeOpt();
				 * op.value(Short.parseShort(genericPostBody.getSeverity()));
				 * alarm.alarm_information_reference = genericPostBody.getaId();
				 * alarm.perceived_severity = op; String userId=genericPostBody.getUserId();
				 *
				 * alarmIRP.acknowledge_alarms(new AlarmInformationIdAndSev[] { alarm}, userId ,
				 * opt, badackalarmholder); BadAcknowledgeAlarmInfo[] info =
				 * badackalarmholder.value;
				 */
				result = 1;
			}

			if (genericPostBody.getAlarmProtocol().equalsIgnoreCase("SNMP")
					&& genericPostBody.getAlarmVendor().equalsIgnoreCase("ZTE")) {
				String hostname = "", username = "", password = "";
				hostname = config.getProperty("server.hostname");
				username = config.getProperty("server.username");
				password = config.getProperty("server.password");

				String tableName1 = genericPostBody.getTableName();
				String columns1 = "ALARMINDEX,ALARMCODE,ALARMCODENAME";
				String conditions1 = "ALARMCODE=" + genericPostBody.getAlarmCode() + " AND ALARMCODENAME="
						+ genericPostBody.getAlarmName() + " AND ALARMMOCOBJECTINSTANCE="
						+ genericPostBody.getAlarmElementName() + "" + " AND ALARMEVENTDATE="
						+ genericPostBody.getAlarmDate() + " AND ALARMEVENTTIME=" + genericPostBody.getAlarmTime() + "";

				JSONArray jsonArray1 = getTableSpecificColsValsConditionGeneric(tableName1, columns1, conditions1);
				String alarmIndex = "";
				for (int i = 0; i < jsonArray1.length(); i++) {
					JSONObject jsonObject = jsonArray1.getJSONObject(i);
					alarmIndex = jsonObject.getString("ALARMINDEX");
				}

				String tableName2 = "connectivitydetails";
				String columns2 = "hostname(fm),trapsrcport(fm),securityname(fm),authprotocol(fm),privprotocol(fm),authpassphrase(fm),privpassphrase(fm),";
				String conditions2 = "protocol(fm)=Snmp AND domain=" + genericPostBody.getAlarmDomain()
						+ " AND vendor=Zte AND type=Ems AND ne name=netnumen AND version(fm)=V3";

				JSONArray jsonArray2 = getTableSpecificColsValsConditionGeneric(tableName2, columns2, conditions2);

				String hostnamefm = "", trapsrcportfm = "", securitynamefm = "", authprotocolfm = "",
						privprotocolfm = "", authpassphrasefm = "", privpassphrasefm = "";

				for (int i = 0; i < jsonArray2.length(); i++) {
					JSONObject jsonObject = jsonArray2.getJSONObject(i);
					hostnamefm = jsonObject.getString("hostname(fm)");
					trapsrcportfm = jsonObject.getString("trapsrcport(fm)");
					securitynamefm = jsonObject.getString("securityname(fm)");
					authprotocolfm = jsonObject.getString("authprotocol(fm)");
					String privprotocol = jsonObject.getString("privprotocol(fm)");
					if (privprotocol.contains("AES")) {
						privprotocolfm = "AES";
					}
					if (privprotocol.contains("DES")) {
						privprotocolfm = "DES";
					}
					authpassphrasefm = jsonObject.getString("authpassphrase(fm)");
					privpassphrasefm = jsonObject.getString("privpassphrase(fm)");
				}

				Session session = getSession(hostname, username, password);
				String cmd1 = "snmpset -v3 -l authPriv -u " + securitynamefm + " -a " + authprotocolfm + " -A "
						+ authpassphrasefm + " -x " + privprotocolfm + " -X " + privpassphrasefm + " " + hostnamefm
						+ ":" + trapsrcportfm + " currentAlarmTable.0 i " + alarmIndex;
				String cmd2 = "quit";
				if (session.isConnected()) {
					su_task task = new su_task(session, cmd1, cmd2);
					Thread thrd = new Thread(task);
					thrd.start();

					while (true) {
						try {
							Thread.sleep(1000);
						} catch (Exception ex) {
//ex.printStackTrace();
						}
						if (!thrd.isAlive()) {

							String res = genericPostBody.getCommandTaskOutput();
							if (res.endsWith(alarmIndex)) {
								result = 1;
							} else {
								result = 0;
							}
							break;
						}
					}
					session.disconnect();
				}
			}

		} catch (Exception ex) {
//ex.printStackTrace();
			log.error("Exception occurs:----" + ex.getMessage(), ex);
		}

		return result;
	}

	public int postClearAlarms(GenericPostBody genericPostBody) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into postClearAlarms ****************");
		}

		Properties config = getProperties();
		int result = 0;
		try {
			if (genericPostBody.getAlarmProtocol().toUpperCase().equals("CORBA")
					&& genericPostBody.getAlarmDomain().equalsIgnoreCase("RADIO")) {

				Path rootLocation = Paths.get(config.getProperty("server.directory"));
				String file_store = genericPostBody.getFileName();
				String nameServiceIOR = null;
				File file_check = new File(rootLocation + "\\" + file_store);
				if (file_check.exists()) {

//System.out.println("file=========="+rootLocation + "\\" + file_store);

					File file = ResourceUtils.getFile(rootLocation + "\\" + file_store);

					BufferedReader br;

					try {
						br = new BufferedReader(new FileReader(file));
						nameServiceIOR = br.readLine();
						br.close();
					} catch (Exception ex) {
						log.error("Exception occurs:----" + ex.getMessage(), ex);
					}
				}
				String args[] = null;

				if (log.isDebugEnabled()) {
					log.debug("*************** NameServiceIOR=" + nameServiceIOR + " ****************");
				}

//System.out.println("NameService========"+nameServiceIOR);

				ORB orb = ORB.init(args, null);

				org.omg.CORBA.Object ref = orb.string_to_object(nameServiceIOR);

				NamingContextExt nameContext = NamingContextExtHelper.narrow(ref);

				org.omg.CORBA.Object alarmIRPIOR = nameContext
						.resolve(nameContext.to_name(genericPostBody.getResolveString()));
//System.out.println("ref = " + ref);

				if (log.isDebugEnabled()) {
					log.debug("*************** AlarmIRPIOR=" + alarmIRPIOR + " ****************");
				}

				/*
				 * AlarmIRP alarmIRP=AlarmIRPHelper.narrow(alarmIRPIOR);
				 *
				 * BadAlarmInformationIdSeqHolder badclearalarmholder = new
				 * BadAlarmInformationIdSeqHolder(); StringTypeOpt opt = new StringTypeOpt();
				 * opt.value(genericPostBody.getSystemId());
				 *
				 * String alarmIds[]=new String[1]; alarmIds[0]=genericPostBody.getaId();
				 * alarmIRP.clear_alarms(alarmIds,genericPostBody.getUserId(),
				 * opt,badclearalarmholder); BadAlarmInformationId[] info =
				 * badclearalarmholder.value;
				 */
				result = 1;
			}

			if (genericPostBody.getAlarmProtocol().equalsIgnoreCase("SNMP")
					&& genericPostBody.getAlarmVendor().equalsIgnoreCase("ZTE")) {
				String hostname = "", username = "", password = "";
				hostname = config.getProperty("server.hostname");
				username = config.getProperty("server.username");
				password = config.getProperty("server.password");

				String tableName1 = genericPostBody.getTableName();
				String columns1 = "ALARMINDEX,ALARMCODE,ALARMCODENAME";
				String conditions1 = "ALARMCODE=" + genericPostBody.getAlarmCode() + " AND ALARMCODENAME="
						+ genericPostBody.getAlarmName() + " AND ALARMMOCOBJECTINSTANCE="
						+ genericPostBody.getAlarmElementName() + "" + " AND ALARMEVENTDATE="
						+ genericPostBody.getAlarmDate() + " AND ALARMEVENTTIME=" + genericPostBody.getAlarmTime() + "";

				JSONArray jsonArray1 = getTableSpecificColsValsConditionGeneric(tableName1, columns1, conditions1);
				String alarmIndex = "";
				for (int i = 0; i < jsonArray1.length(); i++) {
					JSONObject jsonObject = jsonArray1.getJSONObject(i);
					alarmIndex = jsonObject.getString("ALARMINDEX");
				}

				String tableName2 = "connectivitydetails";
				String columns2 = "hostname(fm),trapsrcport(fm),securityname(fm),authprotocol(fm),privprotocol(fm),authpassphrase(fm),privpassphrase(fm),";
				String conditions2 = "protocol(fm)=Snmp AND domain=" + genericPostBody.getAlarmDomain()
						+ " AND vendor=Zte AND type=Ems AND ne name=netnumen AND version(fm)=V3";

				JSONArray jsonArray2 = getTableSpecificColsValsConditionGeneric(tableName2, columns2, conditions2);

				String hostnamefm = "", trapsrcportfm = "", securitynamefm = "", authprotocolfm = "",
						privprotocolfm = "", authpassphrasefm = "", privpassphrasefm = "";

				for (int i = 0; i < jsonArray2.length(); i++) {
					JSONObject jsonObject = jsonArray2.getJSONObject(i);
					hostnamefm = jsonObject.getString("hostname(fm)");
					trapsrcportfm = jsonObject.getString("trapsrcport(fm)");
					securitynamefm = jsonObject.getString("securityname(fm)");
					authprotocolfm = jsonObject.getString("authprotocol(fm)");
					String privprotocol = jsonObject.getString("privprotocol(fm)");
					if (privprotocol.contains("AES")) {
						privprotocolfm = "AES";
					}
					if (privprotocol.contains("DES")) {
						privprotocolfm = "DES";
					}
					authpassphrasefm = jsonObject.getString("authpassphrase(fm)");
					privpassphrasefm = jsonObject.getString("privpassphrase(fm)");
				}

				Session session = getSession(hostname, username, password);
				String cmd1 = "snmpset -v3 -l authPriv -u " + securitynamefm + " -a " + authprotocolfm + " -A "
						+ authpassphrasefm + " -x " + privprotocolfm + " -X " + privpassphrasefm + " " + hostnamefm
						+ ":" + trapsrcportfm + " currentAlarmTable.currentAlarmEntry.alarmAck i " + alarmIndex;
				String cmd2 = "quit";
				if (session.isConnected()) {
					su_task task = new su_task(session, cmd1, cmd2);
					Thread thrd = new Thread(task);
					thrd.start();

					while (true) {
						try {
							Thread.sleep(1000);
						} catch (Exception ex) {
//ex.printStackTrace();
						}
						if (!thrd.isAlive()) {

							String res = genericPostBody.getCommandTaskOutput();
							if (res.endsWith(alarmIndex)) {
								result = 1;
							} else {
								result = 0;
							}
							break;
						}
					}
					session.disconnect();
				}
			}
		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
		}

		return result;
	}

	public int postCommentAlarms(GenericPostBody genericPostBody) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into postCommentAlarms ****************");
		}

		Properties config = getProperties();

		try {
			if (genericPostBody.getAlarmProtocol().equalsIgnoreCase("CORBA")
					&& genericPostBody.getAlarmDomain().equalsIgnoreCase("RADIO")) {

				Path rootLocation = Paths.get(config.getProperty("server.directory"));
				String file_store = genericPostBody.getFileName();
				String nameServiceIOR = null;
				File file_check = new File(rootLocation + "\\" + file_store);
				if (file_check.exists()) {

					File file = ResourceUtils.getFile(rootLocation + "\\" + file_store);

					BufferedReader br;

					try {
						br = new BufferedReader(new FileReader(file));
						nameServiceIOR = br.readLine();
						br.close();
					} catch (Exception ex) {
						log.error("Exception occurs:----" + ex.getMessage(), ex);
					}
				}
				String args[] = null;

				if (log.isDebugEnabled()) {
					log.debug("*************** NameServiceIOR=" + nameServiceIOR + " ****************");
				}

				ORB orb = ORB.init(args, null);

				org.omg.CORBA.Object ref = orb.string_to_object(nameServiceIOR);

				NamingContextExt nameContext = NamingContextExtHelper.narrow(ref);

				org.omg.CORBA.Object alarmIRPIOR = nameContext
						.resolve(nameContext.to_name(genericPostBody.getResolveString()));
//System.out.println("ref = " + ref);

				if (log.isDebugEnabled()) {
					log.debug("*************** AlarmIRPIOR=" + alarmIRPIOR + " ****************");
				}

				/*
				 * AlarmIRP alarmIRP=AlarmIRPHelper.narrow(alarmIRPIOR);
				 *
				 * BadAlarmInformationIdSeqHolder badcommentalarmholder = new
				 * BadAlarmInformationIdSeqHolder(); StringTypeOpt opt = new StringTypeOpt();
				 * opt.value(genericPostBody.getSystemId()); String alarmIds[]=new String[1];
				 * alarmIds[0]=genericPostBody.getaId();
				 *
				 * alarmIRP.comment_alarms(alarmIds, genericPostBody.getUserId(),
				 * opt,"alarm comment", badcommentalarmholder); BadAlarmInformationId[] info =
				 * badcommentalarmholder.value;
				 */
			}
		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
		}

		return 1;
	}

	public int postSetCommandAlarms(GenericPostBody genericPostBody) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into postSetCommandAlarms ****************");
		}
		int result = 0;
		Properties config = getProperties();
		try {
			if (genericPostBody.getAlarmProtocol().equalsIgnoreCase("SNMP")
					&& genericPostBody.getAlarmVendor().equalsIgnoreCase("ZTE")) {
				String hostname = "", username = "", password = "";
				hostname = config.getProperty("server.hostname");
				username = config.getProperty("server.username");
				password = config.getProperty("server.password");

				String setCommand = "";

				if (genericPostBody.getCommandSetType().equalsIgnoreCase("heartbeat")) {
					setCommand = "csIRPHeartbeatPeriod.0 i " + genericPostBody.getCommandSetValue();
				} else if (genericPostBody.getCommandSetType().equalsIgnoreCase("alarmfilter")) {
					setCommand = "alarmFilterEnable.0 i " + genericPostBody.getCommandSetValue();
				} else if (genericPostBody.getCommandSetType().equalsIgnoreCase("alarmTypefilter")) {
					setCommand = "alarmTypeFilter.0 i " + genericPostBody.getCommandSetValue();
				}

				String tableName = "connectivitydetails";
				String columns = "hostname(fm),trapsrcport(fm),securityname(fm),authprotocol(fm),privprotocol(fm),authpassphrase(fm),privpassphrase(fm),";
				String conditions = "protocol(fm)=Snmp AND domain=" + genericPostBody.getAlarmDomain()
						+ " AND vendor=Zte AND type=Ems AND ne name=netnumen AND version(fm)=V3";

				JSONArray jsonArray2 = getTableSpecificColsValsConditionGeneric(tableName, columns, conditions);
				String hostnamefm = "", trapsrcportfm = "", securitynamefm = "", authprotocolfm = "",
						privprotocolfm = "", authpassphrasefm = "", privpassphrasefm = "";

				for (int i = 0; i < jsonArray2.length(); i++) {
					JSONObject jsonObject = jsonArray2.getJSONObject(i);
					hostnamefm = jsonObject.getString("hostname(fm)");
					trapsrcportfm = jsonObject.getString("trapsrcport(fm)");
					securitynamefm = jsonObject.getString("securityname(fm)");
					authprotocolfm = jsonObject.getString("authprotocol(fm)");
					String privprotocol = jsonObject.getString("privprotocol(fm)");
					if (privprotocol.contains("AES")) {
						privprotocolfm = "AES";
					}
					if (privprotocol.contains("DES")) {
						privprotocolfm = "DES";
					}
					authpassphrasefm = jsonObject.getString("authpassphrase(fm)");
					privpassphrasefm = jsonObject.getString("privpassphrase(fm)");
				}

				Session session = getSession(hostname, username, password);
				String cmd1 = "snmpset -v3 -l authPriv -u " + securitynamefm + " -a " + authprotocolfm + " -A "
						+ authpassphrasefm + " -x " + privprotocolfm + " -X " + privpassphrasefm + " " + hostnamefm
						+ ":" + trapsrcportfm + " " + setCommand;
				String cmd2 = "quit";
				if (session.isConnected()) {
					su_task task = new su_task(session, cmd1, cmd2);
					Thread thrd = new Thread(task);
					thrd.start();

					while (true) {
						try {
							Thread.sleep(1000);
						} catch (Exception ex) {
//ex.printStackTrace();
						}
						if (!thrd.isAlive()) {
							String res = genericPostBody.getCommandTaskOutput();
							if (res.length() > 0 && res.endsWith(genericPostBody.getCommandSetValue())) {
								result = 1;
							} else {
								result = 0;
							}
							break;
						}

					}
					session.disconnect();
				}
			}
		} catch (Exception ex) {
//ex.printStackTrace();
			log.error("Exception occurs:----" + ex.getMessage(), ex);
		}
		return result;
	}

	public String getAlarmsCount(String vendor, String domain, String protocol) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into postSetCommandAlarms ****************");
		}
		Properties config = getProperties();
		JSONArray jsonArray = new JSONArray();

		try {
			if (protocol.equalsIgnoreCase("SNMP") && vendor.equalsIgnoreCase("ZTE")) {
				String hostname = "", username = "", password = "";
				hostname = config.getProperty("server.hostname");
				username = config.getProperty("server.username");
				password = config.getProperty("server.password");

				String setCommand = "1.3.6.1.4.1.3902.4101.1.2";

				String tableName = "connectivitydetails";
				String columns = "hostname(fm),trapsrcport(fm),securityname(fm),authprotocol(fm),privprotocol(fm),authpassphrase(fm),privpassphrase(fm),";
				String conditions = "protocol(fm)=Snmp AND domain=" + domain
						+ " AND vendor=Zte AND type=Ems AND ne name=netnumen AND version(fm)=V3";

				JSONArray jsonArray2 = getTableSpecificColsValsConditionGeneric(tableName, columns, conditions);
				String hostnamefm = "", trapsrcportfm = "", securitynamefm = "", authprotocolfm = "",
						privprotocolfm = "", authpassphrasefm = "", privpassphrasefm = "";

				for (int i = 0; i < jsonArray2.length(); i++) {
					JSONObject jsonObject = jsonArray2.getJSONObject(i);
					hostnamefm = jsonObject.getString("hostname(fm)");
					trapsrcportfm = jsonObject.getString("trapsrcport(fm)");
					securitynamefm = jsonObject.getString("securityname(fm)");
					authprotocolfm = jsonObject.getString("authprotocol(fm)");
					String privprotocol = jsonObject.getString("privprotocol(fm)");
					if (privprotocol.contains("AES")) {
						privprotocolfm = "AES";
					}
					if (privprotocol.contains("DES")) {
						privprotocolfm = "DES";
					}
					authpassphrasefm = jsonObject.getString("authpassphrase(fm)");
					privpassphrasefm = jsonObject.getString("privpassphrase(fm)");
				}

				String intermediate = "", critical = "", major = "", minor = "", warning = "", total = "";

				Session session = getSession(hostname, username, password);
				String cmd1 = "snmpbulkwalk -v3 -l authPriv -u " + securitynamefm + " -a " + authprotocolfm + " -A "
						+ authpassphrasefm + " -x " + privprotocolfm + " -X " + privpassphrasefm + " " + hostnamefm
						+ ":" + trapsrcportfm + " " + setCommand;
				String cmd2 = "quit";
				if (session.isConnected()) {
					su_task task = new su_task(session, cmd1, cmd2);
					Thread thrd = new Thread(task);
					thrd.start();

					while (true) {
						try {
							Thread.sleep(1000);
						} catch (Exception ex) {
//ex.printStackTrace();
						}
						if (!thrd.isAlive()) {
							ArrayList<String> cls = new ArrayList<String>();
							cls.add("indeterminate");
							cls.add("critical");
							cls.add("major");
							cls.add("minor");
							cls.add("warning");
							cls.add("total");

							GenericPostBody genericPostBody = new GenericPostBody();
							String spls[] = genericPostBody.getCommandTaskOutput().split("\n");
							for (String splt : spls) {
								if (splt.contains("alarmIndeterminateNumber")) {
									intermediate = splt.substring(splt.lastIndexOf(":") + 1);
								}
								if (splt.contains("alarmCriticalNumber")) {
									critical = splt.substring(splt.lastIndexOf(":") + 1);
								}
								if (splt.contains("alarmMajorNumber")) {
									major = splt.substring(splt.lastIndexOf(":") + 1);
								}
								if (splt.contains("alarmMinorNumber")) {
									minor = splt.substring(splt.lastIndexOf(":") + 1);
								}
								if (splt.contains("alarmWarningNumber")) {
									warning = splt.substring(splt.lastIndexOf(":") + 1);
								}
								if (splt.contains("alarmNumber")) {
									total = splt.substring(splt.lastIndexOf(":") + 1);
								}
							}
							break;
						}

					}

					JSONObject jsonObject = new JSONObject();
					jsonObject.put("indeterminate", intermediate);
					jsonObject.put("critical", critical);
					jsonObject.put("major", major);
					jsonObject.put("minor", minor);
					jsonObject.put("warning", warning);
					jsonObject.put("total", total);
					jsonArray.put(jsonObject);

					session.disconnect();
				}
			}
		} catch (Exception ex) {
//ex.printStackTrace();
			log.error("Exception occurs:----" + ex.getMessage(), ex);
		}
		return jsonArray.toString();
	}

	public JSONArray getTableSpecificColsValsConditionGeneric(String tableName, String columns, String conditions) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into getTableSpecificColsValsConditionGeneric ****************");
		}
		try {
			ArrayList<Bson> fltr = new ArrayList<Bson>();
			Bson filter = null;
			if (conditions.length() > 1) {
				String cond = conditions.replace("@DOT@", ".");
				if (cond.contains("and")) {
					String cond_spls[] = cond.split("and");
					for (String cv : cond_spls) {
						if (cv.contains("=")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							;
							fltr.add(eq(col, val));
						}
					}
					filter = and(fltr);
				} else if (cond.contains("AND")) {
					String cond_spls[] = cond.split("AND");
					for (String cv : cond_spls) {
						if (cv.contains("=")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							;
							fltr.add(eq(col, val));
						}
					}
					filter = and(fltr);
				} else if (!cond.contains("AND") && !cond.contains("and") && cond.contains("=")) {
					String col = cond.split("=")[0].trim();
					String val = cond.split("=")[1].trim().replace("'", "");
					;
					fltr.add(eq(col, val));
					filter = and(fltr);
				}
			}

			Properties config = getProperties();
			MongoClient mongo = getConnection();
			MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
			MongoCollection<Document> collection = database.getCollection(tableName);
			ArrayList<String> cls = new ArrayList<String>();
			ArrayList<Document> resultSet = null;
			ArrayList<TableHeader> cols = new ArrayList<TableHeader>();
			JSONArray vals = new JSONArray();

			Map<String, Object> groupMap = new HashMap<String, Object>();

			if (!columns.equals("*")) {
				if (columns.contains(",")) {
					String columns_spls[] = columns.split(",");
					for (String colm : columns_spls) {
						cls.add(colm);
						groupMap.put(colm, "$" + colm);
					}
				} else {
					cls.add(columns);
					groupMap.put(columns, "$" + columns);
				}

				DBObject groupFields = new BasicDBObject(groupMap);

				if (filter != null) {
					resultSet = collection.aggregate(
							Arrays.asList(match(filter), group(groupFields), project(fields(include(cls))), limit(500)))
							.allowDiskUse(true).into(new ArrayList<Document>());
				} else {
					resultSet = collection
							.aggregate(Arrays.asList(group(groupFields), project(fields(include(cls))), limit(500)))
							.allowDiskUse(true).into(new ArrayList<Document>());
				}
				String cols_spls[] = columns.split(",");
				for (String col : cols_spls) {
					String colm = col;
					TableHeader th = new TableHeader(colm, colm);
					cols.add(th);
				}

				for (Document docs : resultSet) {
					JSONObject colval = new JSONObject();
					String object = docs.get("_id").toString();
					String substr = object.substring(object.indexOf("{{") + 2, object.indexOf("}}"));
					String spls_cols[] = substr.split(",");
					for (String cv : spls_cols) {
						String cl = cv.substring(0, cv.indexOf("=")).trim();
						String vl = cv.substring(cv.indexOf("=") + 1).trim();// .replace("@COMMA@",
																				// "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","")
						colval.put(cl, vl);
					}
					if (!vals.similar(colval)) {
						vals.put(colval);
					}
				}
			} else {
				if (filter != null) {
					resultSet = collection.find(filter).limit(500).into(new ArrayList<Document>());
				} else {
					resultSet = collection.find().limit(500).into(new ArrayList<Document>());
				}
				int size = 0;
				int max = 0;
				Document doc = null;
				for (int i = 0; i < resultSet.size(); i++) {
					Document document = resultSet.get(i);
					size = document.keySet().size();
					if (size > max) {
						max = size;
						doc = document;
					}
				}
				Iterator<String> itr = doc.keySet().iterator();
				while (itr.hasNext()) {
					String col = itr.next().toString();
					if (!col.equals("_id")) {
						cls.add(col);
						TableHeader th = new TableHeader(col, col);
						cols.add(th);
					}
				}
				for (Document docs : resultSet) {
					JSONObject colval = new JSONObject();
					for (int j = 0; j < cls.size(); j++) {
						if (docs.containsKey(cls.get(j))) {
							if (docs.get(cls.get(j)).toString().length() > 0
									&& docs.get(cls.get(j)).toString() != null) {
								colval.put(cls.get(j), docs.get(cls.get(j)));
							} else {
								colval.put(cls.get(j), "-");
							}
						} else {
							colval.put(cls.get(j), "-");
						}
					}
					if (!vals.similar(colval)) {
						vals.put(colval);
					}
				}

			}
			/*
			 * JSONArray jsonArrayFinal=new JSONArray(); JSONObject jsonObjectColVal=new
			 * JSONObject(); jsonObjectColVal.put("cols", cols);
			 * jsonObjectColVal.put("vals", vals); jsonArrayFinal.put(jsonObjectColVal);
			 */
			closeConnection(mongo);
			return vals;
		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
		}
		return null;
	}

	public Session getSession(String hostname, String username, String password) throws Exception {
		JSch jsch = new JSch();
		Session session = jsch.getSession(username, hostname, 22);
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		;
		session.setPassword(password);
		session.connect();

		return session;
	}

	@Override
	public String getUtilizationTable(String database_name, String tableName, String columns, String conditions,
			String orderby, String domain, String vendor, String type) {

		if (log.isDebugEnabled()) {
			log.debug("*************** checked into getHistoryAlarmsOnWindow ****************");
		}

		Properties config = getProperties();

		if (type.equals("userlogs") || type.equals("isl")) {

			database_name = config.getProperty("mongo.db.database.topology");

		}

		MongoClient mongo = getConnection();
		MongoDatabase database = mongo.getDatabase(database_name);

		BasicDBObject element_index;
		if (type.equals("utilization")) {

			tableName = current_date() + "_kpis";

			MongoDatabase database1 = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));

			String minutes = "";
			String current_time = "";

			MongoCollection<Document> collection_filter = database1.getCollection("nokia_snmp_credentials");
			ArrayList<Document> resultSet = collection_filter
					.find(and(eq("vendor", vendor), eq("domain", domain), eq("type", "interface")))
					.into(new ArrayList<Document>());

			for (Document docs : resultSet) {
				minutes = docs.get("wait_minutes").toString();
			}

			MongoCollection<Document> collection = database.getCollection("present_time_calc");

			ArrayList<Document> resultSet1 = collection.find().into(new ArrayList<Document>());

			for (Document docs : resultSet1) {
				current_time = sub_mins(docs.get("value").toString(), Integer.parseInt("-" + minutes));
			}
			System.out.println("------------------------->" + resultSet1);

			conditions = conditions + " and start_time=" + current_time;
		}

		if (tableName.contains("_logs")) {
			limitHistoryAlarms = 1500;
			if (vendor.equalsIgnoreCase("Huawei") || vendor.equalsIgnoreCase("Zte")
					|| vendor.equalsIgnoreCase("nokia")) {
				element_index = new BasicDBObject("$hint", "nename");
			}

			else if (vendor.contains("tech")) {
				element_index = new BasicDBObject("$hint", "nename");
			}

			else {
				element_index = new BasicDBObject("$hint", "_id_");
			}
		}

		else if (tableName.contains("isl")) {
			limitHistoryAlarms = 1500;
			element_index = new BasicDBObject("$hint", "nename");
		}

		else if (tableName.contains("details")) {
			limitHistoryAlarms = 1500;
			element_index = new BasicDBObject("$hint", "devicename_1");
		}

		else if (tableName.contains("kpis")) {

			element_index = new BasicDBObject("$hint", "devicename_1");
		} else {
			element_index = new BasicDBObject("$hint", "_id_");
		}

		try {
			ArrayList<Bson> fltr = new ArrayList<Bson>();
			Bson filter = null;
			if (conditions.length() > 1) {
				String cond = conditions.replace("@DOT@", ".").replace("@SEMICOLON@", ";");
				if (cond.contains(" and ")) {
					String cond_spls[] = cond.split(" and ");
					for (String cv : cond_spls) {
						if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" or ")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							fltr.add(eq(col, val));
						} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" or ")) {
							String col = cv.substring(0, cv.indexOf("like")).trim();
							String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "")
									.replace("@", "").replace("%", "").trim();
							if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^.*" + val + ".*", Pattern.CASE_INSENSITIVE)));
							} else {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
							}
						} else if (cv.contains("=") && cv.contains("between")) {
							String col = cv.substring(0, cv.indexOf("between")).trim();
							String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "")
									.trim();
							String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
							fltr.add(gte(col, val1));
							fltr.add(lte(col, val2));
						} else if (cv.contains("=") && cv.contains(" or ") && !cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
								String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
								fltr_or.add(eq(col, val));
							}
							fltr.add(or(fltr_or));
						} else if (!cv.contains("=") && cv.contains(" or ") && cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("like")).trim();
								String val = splt_or.substring(splt_or.indexOf("(") + 1, splt_or.lastIndexOf(")"))
										.replace("'", "").replace("@", "").replace("%", "").trim();
								if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^.*" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								} else {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								}
							}
							fltr.add(or(fltr_or));
						}
					}
				} else if (cond.contains(" AND ")) {
					String cond_spls[] = cond.split(" AND ");
					for (String cv : cond_spls) {
						if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" OR ")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							fltr.add(eq(col, val));
						} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" OR ")) {
							String col = cv.substring(0, cv.indexOf("like")).trim();
							String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "")
									.replace("@", "").replace("%", "").trim();
							if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^.*" + val + ".*", Pattern.CASE_INSENSITIVE)));
							} else {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
							}
						} else if (!cv.contains("=") && cv.contains(" OR ") && cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" OR ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("like")).trim();
								String val = splt_or.substring(splt_or.indexOf("(") + 1, splt_or.lastIndexOf(")"))
										.replace("'", "").replace("@", "").replace("%", "").trim();
								if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^.*" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								} else {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								}
							}
							fltr.add(or(fltr_or));
						} else if (cv.contains("=") && cv.contains("between")) {
							String col = cv.substring(0, cv.indexOf("between")).trim();
							String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "")
									.trim();
							String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
							fltr.add(gte(col, val1));
							fltr.add(lte(col, val2));
						} else if (cv.contains("=") && cv.contains(" OR ") && !cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
								String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
								fltr_or.add(eq(col, val));
							}
							fltr.add(or(fltr_or));
						}

					}
				} else if (!(cond.contains(" AND ") && !cond.contains(" and ")) && cond.length() > 1) {
					if (cond.contains("=") && cond.contains("between") && !cond.contains(" or ")
							&& !cond.contains(" OR ")) {
						String col = cond.substring(0, cond.indexOf("between")).trim();
						String val1 = cond.substring(cond.indexOf("FROM=") + 5, cond.indexOf("TO=")).replace("'", "")
								.trim();
						String val2 = cond.substring(cond.indexOf("TO=") + 3).replace("'", "").trim();
						fltr.add(gte(col, val1));
						fltr.add(lte(col, val2));
					} else if (cond.contains(" or ") && !cond.contains("like") && !cond.contains("between")) {
						String or[] = cond.split(" or ");
						ArrayList<Bson> fltr_or = new ArrayList<Bson>();
						for (String splt_or : or) {
							String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
							String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
							fltr_or.add(eq(col, val));
						}
						fltr.add(or(fltr_or));
					} else if (cond.contains(" OR ") && !cond.contains("like") && !cond.contains("between")) {
						String or[] = cond.split(" OR ");
						ArrayList<Bson> fltr_or = new ArrayList<Bson>();
						for (String splt_or : or) {
							String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
							String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
							fltr_or.add(eq(col, val));
						}
						fltr.add(or(fltr_or));
					} else if (cond.contains("=")) {
						String col = cond.substring(0, cond.indexOf("=")).trim();
						String val = cond.substring(cond.indexOf("=") + 1).trim().replace("'", "");
						fltr.add(eq(col, val));
					}
				}

				if (type.equals("userlogs")) {

					String username = "";

					String ip = StringUtils.substringAfter(conditions, "hostname=");

					MongoCollection<Document> collection_filter = database.getCollection("connectivitydetails");
					ArrayList<Document> resultSet = collection_filter.find(and(eq("hostname(fm)", ip)))
							.into(new ArrayList<Document>());

					for (Document docs : resultSet) {
						username = docs.get("username(fm)").toString();
					}

					System.out.println("username==" + username);
					fltr.add(Filters.ne("user", username));
				}

				filter = and(fltr);
			}

			MongoCollection<Document> collection = database.getCollection(tableName);
			ArrayList<String> cls = new ArrayList<String>();
			ArrayList<Document> resultSet = null;
			ArrayList<TableHeader> cols = new ArrayList<TableHeader>();
			JSONArray vals = new JSONArray();

			Map<String, Object> groupMap = new HashMap<String, Object>();

			if (!columns.equals("*")) {
				if (columns.contains(",")) {
					String columns_spls[] = columns.split(",");
					for (String colm : columns_spls) {
						cls.add(colm);
						groupMap.put(colm, "$" + colm);
					}
				} else {
					cls.add(columns);
					groupMap.put(columns, "$" + columns);
				}

				DBObject groupFields = new BasicDBObject(groupMap);
				if (filter != null) {
					resultSet = collection
							.aggregate(Arrays.asList(match(filter), group(groupFields), project(fields(include(cls))),
									sort(descending("_id." + orderby)), limit(limitHistoryAlarms)))
							.allowDiskUse(true).hint(element_index).into(new ArrayList<Document>());
				} else {
					resultSet = collection
							.aggregate(Arrays.asList(group(groupFields), project(fields(include(cls))),
									sort(descending("_id." + orderby)), limit(limitHistoryAlarms)))
							.allowDiskUse(true).hint(element_index).into(new ArrayList<Document>());
				}
				String cols_spls[] = columns.split(",");
				for (String col : cols_spls) {
					String colm = col;
					TableHeader th = new TableHeader(colm, colm);
					cols.add(th);
				}

				for (Document docs : resultSet) {
					JSONObject colval = new JSONObject();
					String object = docs.get("_id").toString();
					String substr = object.substring(object.indexOf("{{") + 2, object.indexOf("}}"));
					String spls_cols[] = substr.split(",");
					for (String cv : spls_cols) {
						String cl = cv.substring(0, cv.indexOf("=")).trim();
						String vl = cv.substring(cv.indexOf("=") + 1).trim().replace(",", "@COMMA@")
								.replace("/", "@FORWARDSLASH@").replace("\\", "@BACKWARDSLASH@").replace("\"", "")
								.replace("\n", " ");// .replace("@COMMA@",
													// "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","")
						colval.put(cl, vl);
					}
					if (!vals.similar(colval)) {
						vals.put(colval);
					}
				}
			} else {
				if (filter != null) {
					resultSet = collection.find(filter).limit(limitHistoryAlarms).into(new ArrayList<Document>());
				} else {
					resultSet = collection.find().limit(limitHistoryAlarms).into(new ArrayList<Document>());
				}
				int size = 0;
				int max = 0;
				Document doc = null;
				for (int i = 0; i < resultSet.size(); i++) {
					Document document = resultSet.get(i);
					size = document.keySet().size();
					if (size > max) {
						max = size;
						doc = document;
						break;
					}
				}
				Iterator<String> itr = doc.keySet().iterator();
				while (itr.hasNext()) {
					String col = itr.next().toString();
					if (!col.equals("_id")) {
						cls.add(col);
						TableHeader th = new TableHeader(col, col);
						cols.add(th);
					}
				}
				for (Document docs : resultSet) {
					JSONObject colval = new JSONObject();
					for (int j = 0; j < cls.size(); j++) {
						if (docs.containsKey(cls.get(j))) {
							if (docs.get(cls.get(j)).toString().length() > 0
									&& docs.get(cls.get(j)).toString() != null) {
								/******************************************/

								if (cls.get(j).equalsIgnoreCase("DOMAIN")) {
									colval.put(cls.get(j), domain);
								} else {
									colval.put(cls.get(j),
											docs.get(cls.get(j)).toString().replace(",", "@COMMA@")
													.replace("/", "@FORWARDSLASH@").replace("\\", "@BACKWARDSLASH@")
													.replace("\"", "").replace("\n", " "));
								}
							} else {
								colval.put(cls.get(j), "-");
							}
						} else {
							colval.put(cls.get(j), "-");
						}
					}
					if (!vals.similar(colval)) {
						vals.put(colval);
					}
				}

			}

			JSONArray jsonArrayFinal = new JSONArray();
			JSONObject jsonObjectColVal = new JSONObject();
			jsonObjectColVal.put("cols", cols);
			jsonObjectColVal.put("vals", vals);
			jsonArrayFinal.put(jsonObjectColVal);
			closeConnection(mongo);
			String output = jsonArrayFinal.toString();
			return output;
		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
		}
		return null;

	}

	@Override
	public String getActiveAlarmsOnElementsWhiteList(String domain, String tableName, String columns, String conditions,
			String orderby, String alarm_type) {

		if (log.isDebugEnabled()) {
			log.debug("*************** checked into getHistoryAlarmsOnWindow ****************");
		}

		try {

			Properties config = getProperties();
			MongoClient mongo = getConnection();
			MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
			MongoCollection<Document> collection = database.getCollection(tableName);

			ArrayList<String> filterValue = new ArrayList<String>();
			if (alarm_type.equals("WhiteList")) {

				MongoCollection<Document> collection_filter = database.getCollection("alarm_filter");
				ArrayList<Document> resultSet = collection_filter
						.find(and(eq("user_id", "DEFAULT"), eq("domain", domain), eq("filter", "Alarm Name")))
						.into(new ArrayList<Document>());

				for (Document docs : resultSet) {
					if (!docs.get("value").toString().contains("AND")) {
						filterValue.add(docs.get("value").toString());
					}

				}
				// filterValue.add("LINK DOWN");

				String almnames = "", alarmnamesfinal = "";
				for (int i = 0; i < filterValue.size(); i++) {

					if (filterValue.get(i).contains(",")) {
						String spls[] = filterValue.get(i).split(",");
						for (String split : spls) {
							almnames = almnames + "ALARMNAME=" + split + " OR ";
						}
						alarmnamesfinal = almnames.substring(0, almnames.length() - 4);
					} else {
						alarmnamesfinal = "ALARMNAME=" + filterValue.get(i);
					}

				}

				conditions = conditions + " AND " + alarmnamesfinal;
				System.out.println(conditions);

			}

			ArrayList<Bson> fltr = new ArrayList<Bson>();
			Bson filter = null;
			if (conditions.length() > 1) {
				String cond = conditions.replace("@DOT@", ".").replace("@SEMICOLON@", ";");
				if (cond.contains(" and ")) {
					String cond_spls[] = cond.split(" and ");
					for (String cv : cond_spls) {
						if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" or ")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							fltr.add(eq(col, val));
						} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" or ")) {
							String col = cv.substring(0, cv.indexOf("like")).trim();
							String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "")
									.replace("@", "").replace("%", "").trim();
							if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^.*" + val + ".*", Pattern.CASE_INSENSITIVE)));
							} else {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
							}
						} else if (cv.contains("=") && cv.contains("between")) {
							String col = cv.substring(0, cv.indexOf("between")).trim();
							String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "")
									.trim();
							String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
							fltr.add(gte(col, val1));
							fltr.add(lte(col, val2));
						} else if (cv.contains("=") && cv.contains(" or ") && !cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
								String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
								fltr_or.add(eq(col, val));
							}
							fltr.add(or(fltr_or));
						} else if (!cv.contains("=") && cv.contains(" or ") && cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" or ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("like")).trim();
								String val = splt_or.substring(splt_or.indexOf("(") + 1, splt_or.lastIndexOf(")"))
										.replace("'", "").replace("@", "").replace("%", "").trim();
								if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^.*" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								} else {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								}
							}

							fltr.add(or(fltr_or));
						}
					}
				} else if (cond.contains(" AND ")) {
					String cond_spls[] = cond.split(" AND ");
					for (String cv : cond_spls) {
						if (cv.contains("=") && !cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" OR ")) {
							String col = cv.substring(0, cv.indexOf("=")).trim();
							String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
							fltr.add(eq(col,
									java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
						} else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between")
								&& !cv.contains(" OR ")) {
							String col = cv.substring(0, cv.indexOf("like")).trim();
							String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "")
									.replace("@", "").replace("%", "").trim();
							if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^.*" + val + ".*", Pattern.CASE_INSENSITIVE)));
							} else {
								fltr.add(eq(col,
										java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
							}
						} else if (!cv.contains("=") && cv.contains(" OR ") && cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" OR ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								String col = splt_or.substring(0, splt_or.indexOf("like")).trim();
								String val = splt_or.substring(splt_or.indexOf("(") + 1, splt_or.lastIndexOf(")"))
										.replace("'", "").replace("@", "").replace("%", "").trim();
								if (!val.toUpperCase().contains("MANAGEDELEMENT")) {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^.*" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								} else {
									fltr_or.add(eq(col, java.util.regex.Pattern.compile("^" + val + ".*",
											Pattern.CASE_INSENSITIVE)));
								}
							}
							fltr.add(or(fltr_or));
						} else if (cv.contains("=") && cv.contains("between")) {
							String col = cv.substring(0, cv.indexOf("between")).trim();
							String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "")
									.trim();
							String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
							fltr.add(gte(col, val1));
							fltr.add(lte(col, val2));
						} else if (cv.contains("=") && cv.contains(" OR ") && !cv.contains("like")
								&& !cv.contains("between")) {
							String or[] = cv.split(" OR ");
							ArrayList<Bson> fltr_or = new ArrayList<Bson>();
							for (String splt_or : or) {
								System.out.println("==>" + splt_or);
								String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
								String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");

								fltr_or.add(eq(col,
										java.util.regex.Pattern.compile("^" + val + ".*", Pattern.CASE_INSENSITIVE)));
							}
							fltr.add(or(fltr_or));
						}

					}
				} else if (!(cond.contains(" AND ") && !cond.contains(" and ")) && cond.length() > 1) {
					if (cond.contains("=") && cond.contains("between") && !cond.contains(" or ")
							&& !cond.contains(" OR ")) {
						String col = cond.substring(0, cond.indexOf("between")).trim();
						String val1 = cond.substring(cond.indexOf("FROM=") + 5, cond.indexOf("TO=")).replace("'", "")
								.trim();
						String val2 = cond.substring(cond.indexOf("TO=") + 3).replace("'", "").trim();
						fltr.add(gte(col, val1));
						fltr.add(lte(col, val2));
					} else if (cond.contains(" or ") && !cond.contains("like") && !cond.contains("between")) {
						String or[] = cond.split(" or ");
						ArrayList<Bson> fltr_or = new ArrayList<Bson>();
						for (String splt_or : or) {
							String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
							String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
							fltr_or.add(eq(col, val));
						}
						fltr.add(or(fltr_or));
					} else if (cond.contains(" OR ") && !cond.contains("like") && !cond.contains("between")) {
						String or[] = cond.split(" OR ");
						ArrayList<Bson> fltr_or = new ArrayList<Bson>();
						for (String splt_or : or) {
							String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
							String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
							fltr_or.add(eq(col, val));
						}
						fltr.add(or(fltr_or));
					} else if (cond.contains("=")) {
						String col = cond.substring(0, cond.indexOf("=")).trim();
						String val = cond.substring(cond.indexOf("=") + 1).trim().replace("'", "");
						fltr.add(eq(col, val));
					}
				}
				filter = and(fltr);
			}

			ArrayList<String> cls = new ArrayList<String>();
			ArrayList<Document> resultSet = null;
			ArrayList<TableHeader> cols = new ArrayList<TableHeader>();
			JSONArray vals = new JSONArray();

			Map<String, Object> groupMap = new HashMap<String, Object>();

			if (!columns.equals("*")) {
				if (columns.contains(",")) {
					String columns_spls[] = columns.split(",");
					for (String colm : columns_spls) {
						cls.add(colm);
						groupMap.put(colm, "$" + colm);
					}
				} else {
					cls.add(columns);
					groupMap.put(columns, "$" + columns);
				}

				DBObject groupFields = new BasicDBObject(groupMap);
				if (filter != null) {
					resultSet = collection
							.aggregate(Arrays.asList(match(filter), group(groupFields), project(fields(include(cls))),
									sort(descending("_id." + orderby)), limit(limitHistoryAlarms)))
							.allowDiskUse(true).into(new ArrayList<Document>());
				} else {
					resultSet = collection
							.aggregate(Arrays.asList(group(groupFields), project(fields(include(cls))),
									sort(descending("_id." + orderby)), limit(limitHistoryAlarms)))
							.allowDiskUse(true).into(new ArrayList<Document>());
				}
				String cols_spls[] = columns.split(",");
				for (String col : cols_spls) {
					String colm = col;
					TableHeader th = new TableHeader(colm, colm);
					cols.add(th);
				}

				for (Document docs : resultSet) {
					JSONObject colval = new JSONObject();
					String object = docs.get("_id").toString();
					String substr = object.substring(object.indexOf("{{") + 2, object.indexOf("}}"));
					String spls_cols[] = substr.split(",");
					for (String cv : spls_cols) {
						String cl = "";
						try {
							cl = cv.substring(0, cv.indexOf("=")).trim();
						} catch (Exception e) {

							// e.printStackTrace();
						}

						String vl = cv.substring(cv.indexOf("=") + 1).trim().replace(",", "@COMMA@")
								.replace("/", "@FORWARDSLASH@").replace("\\", "@BACKWARDSLASH@").replace("\"", "")
								.replace("\n", " ");// .replace("@COMMA@",
													// "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","")

						colval.put(cl, vl);
					}
					if (!vals.similar(colval)) {
						vals.put(colval);
					}
				}
			} else {
				if (filter != null) {
					resultSet = collection.find(filter).limit(limitHistoryAlarms).into(new ArrayList<Document>());
				} else {
					resultSet = collection.find().limit(limitHistoryAlarms).into(new ArrayList<Document>());
				}
				int size = 0;
				int max = 0;
				Document doc = null;
				for (int i = 0; i < resultSet.size(); i++) {
					Document document = resultSet.get(i);
					size = document.keySet().size();
					if (size > max) {
						max = size;
						doc = document;
						break;
					}
				}
				Iterator<String> itr = doc.keySet().iterator();
				while (itr.hasNext()) {
					String col = itr.next().toString();
					if (!col.equals("_id")) {
						cls.add(col);
						TableHeader th = new TableHeader(col, col);
						cols.add(th);
					}
				}
				for (Document docs : resultSet) {
					JSONObject colval = new JSONObject();
					for (int j = 0; j < cls.size(); j++) {
						if (docs.containsKey(cls.get(j))) {
							if (docs.get(cls.get(j)).toString().length() > 0
									&& docs.get(cls.get(j)).toString() != null) {
								colval.put(cls.get(j),
										docs.get(cls.get(j)).toString().replace(",", "@COMMA@")
												.replace("/", "@FORWARDSLASH@").replace("\\", "@BACKWARDSLASH@")
												.replace("\"", "").replace("\n", " "));
							} else {
								colval.put(cls.get(j), "-");
							}
						} else {
							colval.put(cls.get(j), "-");
						}
					}
					if (!vals.similar(colval)) {
						vals.put(colval);
					}
				}

			}

			JSONArray jsonArrayFinal = new JSONArray();
			JSONObject jsonObjectColVal = new JSONObject();
			jsonObjectColVal.put("cols", cols);
			jsonObjectColVal.put("vals", vals);
			jsonArrayFinal.put(jsonObjectColVal);
			closeConnection(mongo);
			String output = jsonArrayFinal.toString();
			return output;
		} catch (Exception ex) {
			log.error("Exception occurs:----" + ex.getMessage(), ex);
			ex.printStackTrace();
		}
		return null;

	}

//TODO to subtract interval after giving interval
	public String sub_mins(String start_time, int interval) {
		String output = "";

		try {
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
			Date d = df.parse(start_time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.add(Calendar.MINUTE, interval);
			output = df.format(cal.getTime());

		} catch (Exception e) {

			log.error("Exception occurs:----" + e.getMessage(), e);

		}
		return output;

	}

	public String current_date() {

		String output = "";
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd");
		Properties config = getProperties();

		df.setTimeZone(TimeZone.getTimeZone(config.getProperty("timezone")));

		String dateTime = df.format(date);

		output = dateTime;
		return output;

	}
}
