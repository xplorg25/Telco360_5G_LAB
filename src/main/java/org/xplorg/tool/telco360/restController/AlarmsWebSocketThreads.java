package org.xplorg.tool.telco360.restController;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xplorg.tool.telco360.config.BaseDAOMongo;
import org.xplorg.tool.telco360.config.WebSocketService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class AlarmsWebSocketThreads extends BaseDAOMongo implements Runnable{

	Logger log = LogManager.getLogger(AlarmsWebSocketThreads.class.getName());

	int limitActiveAlarms=5000;
	String userId;
	WebSocketService webSocketService;
	int threadTime=30000;
	public AlarmsWebSocketThreads(WebSocketService webSocketService,String userId) {
		this.webSocketService=webSocketService;
		this.userId=userId;
	}


	@Override
	public void run() {
		try {

			while(true) {

				int idx=userIds.indexOf(userId);
				if(idx>-1 && userSubscription.get(idx).contains("FM")) {
					ArrayList<String> vendorlistAll=new ArrayList<String>();
					ArrayList<String> domainlistAll=new ArrayList<String>();

					ArrayList<String> vendorlistMpbn=new ArrayList<String>();
					ArrayList<String> domainlistMpbn=new ArrayList<String>();
					ArrayList<String> vendorlistIpran=new ArrayList<String>();
					ArrayList<String> domainlistIpran=new ArrayList<String>();
					ArrayList<String> vendorlistTransmission=new ArrayList<String>();
					ArrayList<String> domainlistTransmission=new ArrayList<String>();
					ArrayList<String> severitylist=new ArrayList<String>();

					Properties config=getProperties();
					MongoClient mongo=getConnection();
					MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
					MongoCollection<Document>collection=database.getCollection("alarm_filter");
					ArrayList<Document>resultSet=collection.find(and(eq("user_id","DEFAULT"))).into(new ArrayList<Document>());

					for(Document docs:resultSet) {
						domainlistAll.add(docs.getString("domain"));
						vendorlistAll.add(docs.getString("vendor"));
					}
					domainlistMpbn.add("Mpbn");
					domainlistIpran.add("Ipran");
					domainlistTransmission.add("Transmission");

					for(int i=0;i<domainlistAll.size();i++) {
						if(domainlistAll.get(i).equalsIgnoreCase("Mpbn")) {
							if(vendorlistMpbn.indexOf(vendorlistAll.get(i))==-1) {
								vendorlistMpbn.add(vendorlistAll.get(i));
							}
						}
						if(domainlistAll.get(i).equalsIgnoreCase("Ipran")) {
							if(vendorlistIpran.indexOf(vendorlistAll.get(i))==-1) {
								vendorlistIpran.add(vendorlistAll.get(i));
							}
						}
						if(domainlistAll.get(i).equalsIgnoreCase("Transmission")) {
							if(vendorlistTransmission.indexOf(vendorlistAll.get(i))==-1) {
								vendorlistTransmission.add(vendorlistAll.get(i));
							}
						}
					}

					severitylist.add("Critical");
					severitylist.add("Major");
					severitylist.add("Minor");
					severitylist.add("Warning");
					severitylist.add("Cleared");
					severitylist.add("Indeterminate");

					ArrayList<String>columnsFinal=new ArrayList<String>();
					columnsFinal.add("NENAME");
					columnsFinal.add("NEIP");

					if(vendorlistIpran.size()>0) {
						getActiveAlarmsOnElementBlink(userId,"Ipran",columnsFinal,vendorlistIpran,domainlistIpran,severitylist);
						Thread.sleep(2000);
					}
					if(vendorlistMpbn.size()>0) {
						getActiveAlarmsOnElementBlink(userId,"Mpbn",columnsFinal,vendorlistMpbn,domainlistMpbn,severitylist);
						Thread.sleep(2000);
					}
					if(vendorlistTransmission.size()>0) {
						getActiveAlarmsOnElementBlink(userId,"Transmission",columnsFinal,vendorlistTransmission,domainlistTransmission,severitylist);
						Thread.sleep(threadTime);
					}
					closeConnection(mongo);
				}
				else{
					break;
				}

				Thread.sleep(threadTime);
			}
		}catch(Exception ex) {
			log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
		}
	}

	public String getActiveAlarmsOnElementBlink(String userId,String domain,ArrayList<String>columnsFinal,ArrayList<String> vendorlist,ArrayList<String> domainlist,ArrayList<String> severitylist) {
		try {
			String message="";
			for(int d=0;d<domainlist.size();d++) {
				if(domainlist.get(d).equalsIgnoreCase(domain)) {
					for(int j=0;j<vendorlist.size();j++) {
						ArrayList<String>filterType=new ArrayList<String>();
						ArrayList<String>filterValue=new ArrayList<String>();

						getAlarmFilters(userId, domain, vendorlist.get(j), filterType, filterValue);

/*for(int i=0;i<filterType.size();i++){
if(filterType.get(i).equalsIgnoreCase("ALARM SEVERITY")){
if(severitylist.get(i).indexOf(filterValue.get(i))<0) {
severitylist.add(filterValue.get(i));
}
}
}*/
						String almids="",alarmidsfinal="";
						String almnames="",alarmnamesfinal="";
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

						String colms="",columns="";
						String tablename="activealarms";
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

						JSONArray val=getActiveAlarmsOnElementBlink(vendorlist.get(j),domainlist.get(d),columnsFinal,tablename, columns, conditions,"INSERTIONTIME");

						String alarms="";
						for(int i=0;i<val.length();i++) {
							JSONObject object=(JSONObject) val.get(i);
							alarms=alarms+""+object.get("NENAME")+"@@@"+object.get("NEIP")+"~*@##@";//
						}
						message=message+"Domain:-"+domain+"; Vendor:-"+vendorlist.get(j)+";Alarms:-"+alarms+"#@@#";
					}
				}
			}
			if(message.length()>4) {
				String msg=message.substring(0,message.length()-4);

				webSocketService.sendMessage(userId, msg);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			log.error("Exception occurs:----"+ex.getMessage(),ex);
		}
		return null;
	}

	public JSONArray getActiveAlarmsOnElementBlink(String vendor,String domain,ArrayList<String>columnsFinal,String tableName,String columns,String conditions,String orderby) {
		if (log.isDebugEnabled()) {
			log.debug("*************** checked into getActiveAlarmsOnWindow ****************");
		}
		try {
			ArrayList<Bson> fltr=new ArrayList<Bson>();
			Bson filter=null;
			if(conditions.length()>1) {
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
								String val1="";
								if(val.contains("(")) {
									val1=val.substring(0,val.indexOf("("));
								}
								else {
									val1=val;
								}
								if(!val1.toUpperCase().contains("MANAGEDELEMENT")) {
									fltr_or.add(eq(col,java.util.regex.Pattern.compile("^.*"+val1+".*",Pattern.CASE_INSENSITIVE)));
								}
								else {
									fltr_or.add(eq(col,java.util.regex.Pattern.compile("^"+val1+".*",Pattern.CASE_INSENSITIVE)));
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


			Properties config=getProperties();
			MongoClient mongo=getConnection();
			MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
			MongoCollection<Document> collection = database.getCollection(tableName);
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
				DBObject groupFields = new BasicDBObject(groupMap);
				if(filter!=null) {
					resultSet = collection.aggregate(Arrays.asList(match(filter),group(groupFields),project(fields(include(cls))),sort(descending("_id."+orderby)),limit(limitActiveAlarms))).allowDiskUse(true).into(new ArrayList<Document>());
				}
				else {
					resultSet = collection.aggregate(Arrays.asList(group(groupFields),project(fields(include(cls))),sort(descending("_id."+orderby)),limit(limitActiveAlarms))).allowDiskUse(true).into(new ArrayList<Document>());
				}

				for(Document docs:resultSet) {
					JSONObject colval=new JSONObject();
					Document doc=(Document) docs.get("_id");
					for(int j=0;j<cls.size();j++) {
						if(doc.containsKey(cls.get(j))) {
							if(doc.get(cls.get(j)).toString().length()>0 && doc.get(cls.get(j)).toString()!=null) {
								colval.put(cls.get(j), doc.get(cls.get(j)).toString().replace("\n"," "));
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

	public void getAlarmFilters(String userId,String domain,String vendor,ArrayList<String>filterType,ArrayList<String>filterValue) {
		try {
			Properties config=getProperties();
			MongoClient mongo=getConnection();
			MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
			MongoCollection<Document> collection = database.getCollection("alarm_filter");
			ArrayList<Document> resultSet = collection.find(and(eq("user_id",userId),eq("for","Topology"),eq("domain",domain),eq("vendor",vendor))).into(new ArrayList<Document>());
			for(Document docs:resultSet) {
				if(docs.get("filter").toString().equalsIgnoreCase("Alarm Numbers")) {
					filterType.add(docs.get("filter").toString());
					filterValue.add(docs.get("value").toString());
/*
if(docs.get("value").toString().contains(",")) {
String spls[]=docs.get("value").toString().split(",");
for(String splt:spls) {
if(splt.trim().length()>0) {
filterType.add(docs.get("filter").toString());
filterValue.add(splt);
}
}
}
else {
filterType.add(docs.get("filter").toString());
filterValue.add(docs.get("value").toString());
}*/
				}

				else if(docs.get("filter").toString().equalsIgnoreCase("Alarm Name")) {
					filterType.add(docs.get("filter").toString());
					filterValue.add(docs.get("value").toString());
/*if(docs.get("value").toString().contains(",")) {
String spls[]=docs.get("value").toString().split(",");
for(String splt:spls) {
if(splt.trim().length()>0) {
filterType.add(docs.get("filter").toString());
filterValue.add(splt);
}
}
}
else {
filterType.add(docs.get("filter").toString());
filterValue.add(docs.get("value").toString());
}*/
				}

				else if(docs.get("filter").toString().equalsIgnoreCase("Alarm Severity")) {
					filterType.add(docs.get("filter").toString());
					filterValue.add(docs.get("value").toString());
/*if(docs.get("value").toString().contains(",")) {
String spls[]=docs.get("value").toString().split(",");
for(String splt:spls) {
if(splt.trim().length()>0) {
filterType.add(docs.get("filter").toString());
filterValue.add(splt);
}
}
}
else {
filterType.add(docs.get("filter").toString());
filterValue.add(docs.get("value").toString());
}*/
				}

				else if(docs.get("filter").toString().equalsIgnoreCase("Alarm range")) {
					if(docs.get("value").toString().contains("-")) {
						String val=docs.get("value").toString();
						String rangeFrom=val.split("-")[0].trim();
						String rangeTo=val.split("-")[1].trim();

						String rangeStart="",rangeEnd="";
						int start=0,end=0;
						for(int j=0;j<rangeFrom.length();j++) {
							for(int k=0;k<rangeTo.length();k++) {
								if(rangeTo.charAt(k)==rangeFrom.charAt(j)){
									rangeStart=rangeStart+rangeFrom.charAt(j);
								}
								else {
									rangeEnd=rangeTo.substring(k);
									break;
								}
							}
						}
						start=Integer.parseInt(rangeFrom.substring(rangeStart.length()).trim());
						end=Integer.parseInt(rangeEnd.trim());
						for(int j=start;j<=end;j++) {
							filterType.add(docs.get("filter").toString());
							filterValue.add(rangeStart+""+start);
						}
					}
				}
			}
			closeConnection(mongo);
		}catch(Exception ex) {
			log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
		}
	}

}
