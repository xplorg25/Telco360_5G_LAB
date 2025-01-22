package org.xplorg.tool.telco360.restController;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xplorg.tool.telco360.DAO.interfaces.AdminDAO;
import org.xplorg.tool.telco360.DAO.interfaces.AlarmManagementDAO;
import org.xplorg.tool.telco360.DAO.interfaces.AnalysisDAO;
import org.xplorg.tool.telco360.DAO.interfaces.IntelligentLogicDAO;
import org.xplorg.tool.telco360.DAO.interfaces.PerformanceGenericDao;
import org.xplorg.tool.telco360.DAO.interfaces.PerformanceMicrowaveDAO;
import org.xplorg.tool.telco360.DAO.interfaces.Performance_IPRAN_IPBB_DAO;
import org.xplorg.tool.telco360.DAO.interfaces.TokenDAO;
import org.xplorg.tool.telco360.DAO.interfaces.TopologyDAO;
import org.xplorg.tool.telco360.DAO.interfaces.TopologyDiscoveryDAO;
import org.xplorg.tool.telco360.DAO.interfaces.TroubleTicketDAO;
import org.xplorg.tool.telco360.config.BaseDAOMongo;
import org.xplorg.tool.telco360.config.WebSocketService;
import org.xplorg.tool.telco360.entity.GenerateToken;
import org.xplorg.tool.telco360.entity.GenericPostBody;
import org.xplorg.tool.telco360.entity.TableHeader;
import org.xplorg.tool.telco360.entity.analysisLogicEntity;
import org.xplorg.tool.telco360.entity.auto_mail_generate_entity_br;
import org.xplorg.tool.telco360.entity.check_g_s_parent;
import org.xplorg.tool.telco360.entity.create_json;
import org.xplorg.tool.telco360.entity.kpi_insert_columns;
import org.xplorg.tool.telco360.entity.multiple_sheets;
import org.xplorg.tool.telco360.entity.performance_nokia_radio_element_blink_main;
import org.xplorg.tool.telco360.entity.resultAlarmSolutionTable;
import org.xplorg.tool.telco360.entity.resultEntityKpi;
import org.xplorg.tool.telco360.entity.resultKpiLive;
import org.xplorg.tool.telco360.entity.topology_gis_getter_setter;
import org.xplorg.tool.telco360.entity.treeParent;
import org.xplorg.tool.telco360.entity.tree_parents_g_s;
import org.xplorg.tool.telco360.entity.tree_parents_t_d_final;
import org.xplorg.tool.telco360.entity.user_specific_kpi_excel;
import org.xplorg.tool.telco360.entity.visibility;
import org.xplorg.tool.telco360.entity.yangDHCP;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_1;
import org.xplorg.tool.telco360.performance.dual_axis.dual_axis_1_date_format;
import org.xplorg.tool.telco360.tree.entity.check_g_s_parent_micro;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", exposedHeaders = "Authorization")
//@CrossOrigin(origins = "https://localhost:4200", allowedHeaders = "*", exposedHeaders = "Authorization")
public class AdminController extends BaseDAOMongo{

@Autowired
private AdminDAO adminDAO;

@Autowired
private TokenDAO tokenDAO;


@Autowired
private TroubleTicketDAO troubleticketDAO;

@Autowired
private PerformanceGenericDao performanceGenericDao;

@Autowired
private Performance_IPRAN_IPBB_DAO performance_IPRAN_IPBB_DAO;

@Autowired
private PerformanceMicrowaveDAO performanceMicrowaveDAO;

@Autowired
private AnalysisDAO analysisDAO;

@Autowired
private TopologyDAO topologyDAO;

@Autowired
private TopologyDiscoveryDAO topologyDiscoveryDAO;


@Autowired
private AlarmManagementDAO alarmManagementDAO;


@Autowired
private IntelligentLogicDAO intelligentLogicDAO;

@Autowired
private WebSocketService webSocketService;

GenerateToken generateToken = new GenerateToken();

//private static Log log = LogFactory.getFactory().getLog(AdminController.class);

Logger log = LogManager.getLogger(AdminController.class.getName());

@PostMapping("/login")
public ResponseEntity<Integer> login(@RequestBody GenericPostBody genericPostBody,HttpServletRequest request)
{
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into Login mapping ****************");	
}	
//long license=getLicense();
//if(license>=0) {
int status=0;
String role="";
HttpHeaders httpHeader = null;

// Authenticate User.
String st=adminDAO.adminLogin(genericPostBody.getEmailId(), genericPostBody.getPassword());

//System.out.println("st===>"+st);

if(st.contains("~")) {
status=Integer.parseInt(st.split("~")[0].trim());	
role=st.split("~")[1].trim();
}
/*
* If User is authenticated then Do Authorization Task.
*/
if (status > 0)
{
	//System.out.println(request.getRemoteAddr());
if(log.isInfoEnabled()) {	
log.info("User:- "+genericPostBody.getEmailId()+" / User Id:- "+status+" / IP Address:- "+request.getRemoteAddr()+":"+request.getRemotePort()+" Successfully Logged in to Telco360.");	
}

if(log.isDebugEnabled()) {	
log.debug("*************** User Authenticated and going for authorization ****************");	
}

String token=tokenDAO.getToken(genericPostBody.getEmailId());
//Create the Header Object
httpHeader = new HttpHeaders();

//Add token to the Header.
httpHeader.set("Authorization", token+"~Role="+role);

return new ResponseEntity<Integer>(status, httpHeader, HttpStatus.OK);
}

// if not authenticated return  status what we get.
else
{
if(log.isDebugEnabled()) {	
log.debug("*************** "+genericPostBody.getEmailId()+" user not Authorized ****************");	
}
if(log.isInfoEnabled()) {	
log.info("Login by User:- "+genericPostBody.getEmailId()+" / IP Address:- "+request.getRemoteAddr()+":"+request.getRemotePort()+" to Telco360 was UnSuccessful.");	
}
return new ResponseEntity<Integer>(status, httpHeader, HttpStatus.OK);
}
//}
/*else{
HttpHeaders httpHeader = new HttpHeaders();	
return new ResponseEntity<Integer>(-2, httpHeader, HttpStatus.OK);//10 Specifies License Expiration
}*/
}

@PostMapping("/postUserCreation")
public int createUser(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into createUser mapping ****************");	
}	

Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("admin_detail");
ArrayList<Document> resultSet = collection.find(and(eq("email_id",genericPostBody.getCreateUserEmailId()))).into(new ArrayList<Document>());
closeConnection(mongo);
if(resultSet.size()==0) {
int adm=adminDAO.createUser(genericPostBody);
int tkn=tokenDAO.createUser(genericPostBody);

if(adm==1 && tkn==1) {
return 1;
}
else {
return 0;	
}
}
else {
return 0;	
}
}

@PostMapping("/logout")
public int logout(@RequestBody GenericPostBody genericPostBody,HttpServletRequest request)
{
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into logout mapping ****************");	
}	

if(log.isInfoEnabled()) {	
log.info("User:- "+genericPostBody.getEmailId()+" / User Id:- "+genericPostBody.getUserId()+" / IP Address:- "+request.getRemoteAddr()+":"+request.getRemotePort()+" was Successfully Logged out from Telco360.");		
}
int ret=adminDAO.adminLogout(genericPostBody.getEmailId());
if(ret>0) {
}
return ret;
}

@GetMapping("/getTableSpecificColsValsConditionGeneric/{adminId}/{tableName}/{columns}/{condition}")
//public String getTableSpecificColsValsConditionGeneric(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition) {

	public String getTableSpecificColsValsConditionGeneric( @PathVariable int adminId, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition) {

		
	/*String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableSpecificColsValsConditionGeneric mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing table specific api using GET Method.");	
}
return adminDAO.getTableSpecificColsValsConditionGeneric(tableName,columns,condition);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableSpecificColsValsConditionGeneric mapping and user is Unauthorization****************");
}
return null;

}

*/
return adminDAO.getTableSpecificColsValsConditionGeneric(tableName,columns,condition);	
}

@PostMapping("/postDataAlarmFilter")
public int  postDataAlarmFilter(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postDataAlarmFilter ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("UserId:- "+genericPostBody.getUserId()+" accessing Alarm Filter using POST Method.");	
}
return alarmManagementDAO.postDataAlarmFilter(genericPostBody);
}


@GetMapping("/getActiveAlarmsOnWindow/{adminId}/{vendorlist}/{domainlist}/{severitylist}/{alarmids}/{alarmnames}/{nename}/{fieldlist}/{filterlist}/{alarmnamelist}")
public String getActiveAlarmsOnWindow(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable ArrayList<String> vendorlist, @PathVariable ArrayList<String> domainlist, @PathVariable ArrayList<String> severitylist
		, @PathVariable String alarmids, @PathVariable String alarmnames, @PathVariable String nename, @PathVariable ArrayList<String> fieldlist, @PathVariable ArrayList<String> filterlist, @PathVariable ArrayList<String> alarmnamelist) {

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getActiveAlarmsOnWindow mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Active Alarms using GET Method.");	
}
return alarmManagementDAO.getActiveAlarmsOnWindow(""+adminId,vendorlist,domainlist,severitylist, alarmids,alarmnames,nename,fieldlist,filterlist,alarmnamelist);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getActiveAlarmsOnWindow mapping and user is Unauthorization****************");
}
return null;

}

}

@PostMapping("/postActiveAlarmsOnWindowStatusChange")
public int  postActiveAlarmsOnWindowStatusChange(@RequestBody String data) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postActiveAlarmsOnWindowStatusChange ****************");	
}	
String userid=data.substring(data.indexOf("USERID:=")+8,data.indexOf(";"));	
if(log.isInfoEnabled()) {	
log.info("UserId:- "+userid+" accessing Alarm Status Change using POST Method.");	
}
return alarmManagementDAO.postActiveAlarmsOnWindowStatusChange(data);
}

@GetMapping("/getHistoryAlarmsOnWindow/{adminId}/{tableName}/{columns}/{condition}/{orderby}")
//public String getHistoryAlarmsOnWindow(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition, @PathVariable String orderby) {
public String getHistoryAlarmsOnWindow( @PathVariable int adminId, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition, @PathVariable String orderby) {

	/*
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getHistoryAlarmsOnWindow mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing history Alarms using GET Method.");	
}
return alarmManagementDAO.getHistoryAlarmsOnWindow(tableName,columns,condition, orderby);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getHistoryAlarmsOnWindow mapping and user is Unauthorization****************");
}
return null;

}
*/
return alarmManagementDAO.getHistoryAlarmsOnWindow(tableName,columns,condition, orderby);
}


@GetMapping("/getUtilizationTable/{databasename}/{adminId}/{tableName}/{columns}/{condition}/{orderby}/{domain}/{vendor}/{type}")
//public String getHistoryAlarmsOnWindow(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition, @PathVariable String orderby) {
public String getUtilizationTable( @PathVariable String databasename,  @PathVariable int adminId, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition, @PathVariable String orderby, @PathVariable String domain, @PathVariable  String vendor, @PathVariable String type) {

	/*
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getHistoryAlarmsOnWindow mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing history Alarms using GET Method.");	
}
return alarmManagementDAO.getHistoryAlarmsOnWindow(tableName,columns,condition, orderby);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getHistoryAlarmsOnWindow mapping and user is Unauthorization****************");
}
return null;

}
*/
return alarmManagementDAO.getUtilizationTable(databasename,tableName,columns,condition, orderby,domain,vendor,type);
}


@GetMapping("/getActiveAlarmsOnElementsWhiteList/{domain}/{tableName}/{columns}/{condition}/{orderby}/{alarm_type}")
//public String getHistoryAlarmsOnWindow(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition, @PathVariable String orderby) {
public String getActiveAlarmsOnElementsWhiteList( @PathVariable String domain, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition, @PathVariable String orderby,@PathVariable String alarm_type) {

	/*
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getHistoryAlarmsOnWindow mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing history Alarms using GET Method.");	
}
return alarmManagementDAO.getHistoryAlarmsOnWindow(tableName,columns,condition, orderby);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getHistoryAlarmsOnWindow mapping and user is Unauthorization****************");
}
return null;

}
*/
return alarmManagementDAO.getActiveAlarmsOnElementsWhiteList( domain, tableName, columns, condition, orderby, alarm_type);
}

@GetMapping("/getActiveAlarmsOnElementsFiltered/{adminId}/{domain}/{vendor}/{neipaddress}/{nename}")
public String getActiveAlarmsOnElementsFiltered(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String domain, @PathVariable String vendor, @PathVariable String neipaddress, @PathVariable String nename) {

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getActiveAlarmsOnElementsFiltered mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Filtered Active Alarms on Element using GET Method.");	
}


System.out.println(alarmManagementDAO.getActiveAlarmsOnElementsFiltered(""+adminId, domain, vendor, neipaddress, nename));
return alarmManagementDAO.getActiveAlarmsOnElementsFiltered(""+adminId, domain, vendor, neipaddress, nename);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getActiveAlarmsOnElementsFiltered mapping and user is Unauthorization****************");
}
return null;

}

}

@GetMapping("/getActiveAlarmsOnElements/{adminId}/{tableName}/{columns}/{condition}/{orderby}")
public String getActiveAlarmsOnElements(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition, @PathVariable String orderby) {

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getActiveAlarmsOnElements mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Active Alarms on Element using GET Method.");	
}
return alarmManagementDAO.getActiveAlarmsOnElements(tableName,columns,condition, orderby);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getActiveAlarmsOnElements mapping and user is Unauthorization****************");
}
return null;

}

}

@GetMapping("/getAlarmsDescription/{adminId}/{tableName}/{columns}/{condition}")
public String getAlarmsDescription(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition) {

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getAlarmsDescription mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Alarms Description using GET Method.");	
}
return alarmManagementDAO.getAlarmsDescription(tableName,columns,condition);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getAlarmsDescription mapping and user is Unauthorization****************");
}
return null;
}

}


@GetMapping("/getTopologyDetails/{adminId}/{tableName}/{columns}/{condition}")
//public String getTopologyDetails(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition) {
	public String getTopologyDetails( @PathVariable int adminId, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition) {

	return topologyDiscoveryDAO.getTopologyDetails(tableName,columns,condition);	
	
	/*
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTopologyDetails mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Topology Details using GET Method.");	
}
return topologyDiscoveryDAO.getTopologyDetails(tableName,columns,condition);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTopologyDetails mapping and user is Unauthorization****************");
}
return null;

}
*/
}

@PostMapping("/postUserSubscription")
public int  postUserSubscription(@RequestBody String data) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postUserSubscription ****************");	
}	
String adminId=data.substring(data.indexOf("userid=")+7,data.indexOf(";"));
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing user Subscription using POST Method.");	
}
return topologyDAO.postUserSubscription(data,webSocketService);
}

@PostMapping("/postUserDeSubscription")
public int  postUserDeSubscription(@RequestBody String data) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postUserDeSubscription ****************");	
}	
String adminId=data.substring(data.indexOf("userid=")+7,data.indexOf(";"));
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing user DeSubscription using POST Method.");	
}
return topologyDAO.postUserDeSubscription(data);
}

@PostMapping("/postDataGeneric")
public int  postDataGeneric(@RequestBody String data) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postDataGeneric ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Data Generic using POST Method.");	
}
return topologyDAO.postDataGeneric(data);
}

@PostMapping("/updateDataGeneric")
public int updateDataGeneric(@RequestBody String data) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into updateDataGeneric ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing update Data Generic using POST Method.");	
}
return topologyDAO.updateDataGeneric(data);
}

@PostMapping("/deleteDataGeneric")
public int deleteDataGeneric(@RequestBody String data) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into deleteDataGeneric ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Delete Data Generic using POST Method.");	
}
return topologyDAO.deleteDataGeneric(data);
}



@PostMapping("/uploadExcelDataGeneric")
public int uploadExcelDataGeneric(@RequestParam("file") MultipartFile file,@RequestParam("data") String data) throws Exception {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into uploadExcelDataGeneric mapping ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing upload Excel Data Generic using POST Method.");	
}
return topologyDAO.uploadExcelDataGeneric(file,data);
}

@PostMapping("/uploadCsvDataMismatchedElements")
public int uploadCsvDataMismatchedElements(@RequestParam("file") MultipartFile file) throws Exception {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into uploadCsvDataMismatchedElements mapping ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing upload CSV Data Mismatched Elements using POST Method.");	
}
return topologyDAO.uploadCsvDataMismatchedElements(file);
}

@PostMapping("/postUserDetailsDelete")
public int  postUserDetailsDelete(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postUserDetailsDelete ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("UserId:- "+genericPostBody.getCreateUserId()+" accessing user details delete using POST Method.");	
}
return adminDAO.postUserDetailsDelete(genericPostBody);
}

@PostMapping("/postUserDetailsUpdate")
public int  postUserDetailsUpdate(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postUserDetailsUpdate ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("UserId:- "+genericPostBody.getCreateUserId()+" accessing user details update using POST Method.");	
}
return adminDAO.postUserDetailsUpdate(genericPostBody);
}

@PostMapping("/postUserPermissionsUpdate")
public int  postUserPermissionsUpdate(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postUserPermissionsUpdate ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("UserId:- "+genericPostBody.getUserPermissionUserId()+" accessing user permissions update using POST Method.");	
}
return adminDAO.postUserPermissionsUpdate(genericPostBody);
}

@PostMapping("/postChangePasswordUpdate")
public int  postChangePasswordUpdate(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postChangePasswordUpdate ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("UserId:- "+genericPostBody.getChangePasswordUserId()+" accessing password update using POST Method.");	
}
return adminDAO.postChangePasswordUpdate(genericPostBody);
}

//--------------------------------------------Topology Management---------------------------------------------------------------------------------


@GetMapping("/getActiveAlarmsOnWindowclear/{adminId}/{vendorlist}/{domainlist}/{severitylist}/{alarmids}/{alarmnames}/{nename}/{fieldlist}/{alarmnamelist}")
public String getActiveAlarmsOnWindowclear(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable ArrayList<String> vendorlist, @PathVariable ArrayList<String> domainlist, @PathVariable ArrayList<String> severitylist
		, @PathVariable String alarmids, @PathVariable String alarmnames, @PathVariable String nename, @PathVariable ArrayList<String> fieldlist, @PathVariable ArrayList<String> alarmnamelist) {

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getActiveAlarmsOnWindow mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Active Alarms using GET Method.");	
}
return alarmManagementDAO.getActiveAlarmsOnWindow_clear(""+adminId,vendorlist,domainlist,severitylist, alarmids,alarmnames,nename,fieldlist,alarmnamelist);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getActiveAlarmsOnWindow mapping and user is Unauthorization****************");
}
return null;

}

}


@GetMapping("/getTableColsValsGeneric/{adminId}/{tableName}")
public String getTableColsValsGeneric(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName) {

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataOnElementGenericRadio mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Table Columns Values using GET Method.");	
}
return topologyDAO.getTableColsValsGeneric(tableName);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataOnElementGenericRadio mapping and user is Unauthorization****************");
}
return null;

}

}

//---------------------------------------------------------------GISMAP----------------------------------------------------------------------
@GetMapping("/getGisRegionDetails/{adminId}/{table_name}")
public ArrayList<topology_gis_getter_setter> getGisRegionDetails(@RequestHeader("Authorization") String authorizationToken,@PathVariable int adminId,@PathVariable String table_name)
{
//return topologyDAO.getGisElementDetails(region,element_name);
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into getGisRegionDetails getting GIS region details****************");	
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing GIS Region Details using GET Method.");	
}
return topologyDAO.getGisRegionDetails(table_name);
}
else {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into getGisRegionDetails getting GIS region details****************");	
}	
return null;
}
}

@GetMapping("/getGisElementDetails/{adminId}/{element_name}")
public ArrayList<topology_gis_getter_setter> getGisElementDetails(@RequestHeader("Authorization") String authorizationToken,@PathVariable int adminId,@PathVariable String element_name)
{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into getGisElementDetails getting GIS element details****************");	
}	
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing GIS Element Details using GET Method.");	
}
return topologyDAO.getGisElementDetails(element_name);
}
else {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into getGisElementDetails getting GIS element details****************");	
}	
return null;
}
}

@GetMapping("/getGisGenericInfo/{adminId}/{tableName}/{conditions}")
public String getGisGenericInfo(@RequestHeader("Authorization") String authorizationToken,@PathVariable int adminId,@PathVariable String tableName,@PathVariable String conditions)
{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into getGisGenericInfo getting GIS element details****************");	
}	
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing GIS Generic Info using GET Method.");	
}
return topologyDAO.getGisGenericInfo(tableName,conditions);
}
else {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into getGisGenericInfo getting GIS element details****************");	
}	
return null;
}
}

//---------------------------------------Topology Discovery----------------------------------------------------

@PostMapping("/postTopologyDiscoveryScan")
public int postTopologyDiscoveryScan(@RequestBody String data) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postTopologyDiscoveryScan mapping ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Topology Discovery Scan using POST Method.");	
}
return topologyDiscoveryDAO.postTopologyDiscoveryScan(data);

}

@GetMapping("/getTopologyDiscoverySubnets/{adminId}/{vendor}/{domain}")
public List<tree_parents_t_d_final> getTopologyDiscoverySubnets(@RequestHeader("Authorization") String authorizationToken,@PathVariable int adminId,@PathVariable String vendor,@PathVariable String domain)
{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into getTopologyDiscoverySubnets getting Subnets and their corresponding IPAddress  ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Topology Discovery Subnets using GET Method.");	
}
return topologyDiscoveryDAO.getTopologyDiscoverySubnets(vendor,domain);
}
else {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into getTopologyDiscoverySubnets getting Subnets and their corresponding IPAddress****************");	
}	
return null;
}
}

@PostMapping("/postTopologyDiscoveryFile")
public int uploadTopologyDiscoveryFile() {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into uploadTopologyDiscoveryFile mapping ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Uploading Topology Discovery File using POST Method.");	
}
return topologyDiscoveryDAO.uploadTopologyDiscoveryFile();

}

@PostMapping("/postTopologyDiscoveryNEUpdate")
public int postTopologyDiscoveryNEUpdate(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postTopologyDiscoveryNEUpdate mapping ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Topology Discovery NE Update using POST Method.");	
}
return topologyDiscoveryDAO.postTopologyDiscoveryNEUpdate(genericPostBody);
}


@GetMapping("/getTopologyMicrowaveTree/{adminId}/{tableName}")
public List<tree_parents_t_d_final> getTopologyMicrowaveTree(@RequestHeader("Authorization") String authorizationToken,@PathVariable int adminId, @PathVariable String tableName)
{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into getTopologyMicrowaveTree getting Subnets and their corresponding IPAddress  ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Topology Microwave Tree using GET Method.");	
}
return topologyDiscoveryDAO.getTopologyMicrowaveTree(tableName);
}
else {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into getTopologyMicrowaveTree getting Subnets and their corresponding IPAddress****************");	
}	
return null;
}
}


@PostMapping("/uploadTopologyDiscoveryData")
public int uploadTopologyDiscoveryData(@RequestParam("file") MultipartFile file) throws Exception {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into uploadTopologyDiscoveryData mapping and user is authorization****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Uploading Topology Discovery Data using POST Method.");	
}
return topologyDiscoveryDAO.uploadTopologyDiscoveryData(file);
}

//-------------------User Logs-----------------------------------//

@GetMapping("/getUserlogDetails/{adminId}/{tableName}/{columns}/{condition}/{orderby}")
public String getUserlogDetails(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition, @PathVariable String orderby) {

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getUserlogDetails mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing User Log Details using GET Method.");	
}
return topologyDAO.getUserlogDetails(tableName,columns,condition, orderby);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getUserlogDetails mapping and user is Unauthorization****************");
}
return null;

}

}

//-------------------TROUBLE TICKET------------------------------//

@GetMapping("/get_trouble_ticket_column_data/{adminId}/{tablename}/{area}/{node}/{severity}/{status}")
public String get_trouble_ticket_column_data(@RequestHeader("Authorization") String authorizationToken,@PathVariable int adminId,@PathVariable String tablename,@PathVariable String area,@PathVariable String node,@PathVariable String severity,@PathVariable String status)
{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into get_trouble_ticket_column_name mapping post user Authorization****************");	
}	
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Trouble Ticket Columns Values using GET Method.");	
}
return troubleticketDAO.get_trouble_ticket_column_data(tablename,area,node,severity,status);
}
else {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into get_trouble_ticket_column_name mapping and user is Unauthorization****************");	
}	
}
return null;
}

@PostMapping("/postSmeDetails")
public int  postSmeDetails(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postSmeDetails ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("UserId:- "+genericPostBody.getSmeUserId()+" inserting data using POST Method.");	
}
return troubleticketDAO.postSmeDetails(genericPostBody);
}

@PostMapping("/postSmeDetailsDelete")
public int  postSmeDetailsDelete(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postSmeDetailsDelete ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("UserId:- "+genericPostBody.getSmeUserId()+" deleting SME details using POST Method.");	
}
return troubleticketDAO.postSmeDetailsDelete(genericPostBody);
}


@PostMapping("/postCredentialDetails")
public int  postCredentialDetails(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postCredentialDetails ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Inserting Credential Details using POST Method.");	
}
return troubleticketDAO.postCredentialDetails(genericPostBody);
}

@PostMapping("/postCredentialsDelete")
public int  postCredentialsDelete(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postCredentialsDelete ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Deleting Credential Details using POST Method.");	
}
return troubleticketDAO.postCredentialsDelete(genericPostBody);
}


@PostMapping("/postPerformanceSchedulerUpdate")
public int  postPerformanceSchedulerDetails(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postPerformanceSchedulerUpdate ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Updating Performance Scheduler using POST Method.");	
}
return troubleticketDAO.postPerformanceSchedulerUpdate(genericPostBody);
}

@PostMapping("/postPerformanceSchedulerDelete")
public int  postPerformanceSchedulerDelete(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postPerformanceSchedulerDelete ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Deleting Performance Scheduler using POST Method.");	
}
return troubleticketDAO.postPerformanceSchedulerDelete(genericPostBody);
}

//----------------------------------------------------Trouble Ticket and Communications to Stack Holder-----------------------------------------------

@PostMapping("/Email")
public int  Email(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into email ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Email using POST Method.");	
}
return troubleticketDAO.Email(genericPostBody);
}

@PostMapping("/EmailReport")
public int  EmailReport(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into EmailReport ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Email Report using POST Method.");	
}
return troubleticketDAO.EmailReport(genericPostBody);
}

@PostMapping("/TroubleTicket")
public int  TroubleTicket(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into TroubleTicket ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Trouble Ticket using POST Method.");	
}
return troubleticketDAO.TroubleTicket(genericPostBody);
}

@PostMapping("/ManualTroubleTicket")
public int  ManualTroubleTicket(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into ManualTroubleTicket ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Manual Trouble Ticket using POST Method.");	
}
return troubleticketDAO.ManualTroubleTicket(genericPostBody);
}
@PostMapping("/TroubleTicketGeneration")
public int  TroubleTicketGeneration(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into TroubleTicketGeneration ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Trouble Ticket Generation using POST Method.");	
}
return troubleticketDAO.TroubleTicketGeneration(genericPostBody);
}

@PostMapping("/TroubleTicketGenerationKpi")
public int  TroubleTicketGenerationkpi(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into TroubleTicketGenerationKpi ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Trouble Ticket Generation Kpi using POST Method.");	
}
return troubleticketDAO.TroubleTicketGenerationKpi(genericPostBody);
}
//----Trouble Ticket End User-------------------------------------

@GetMapping("/troubleTicketEndUser/{adminId}")
public List troubleTicketEndUser(@RequestHeader("Authorization") String authorizationToken,@PathVariable int adminId)
{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into troubleTicketEndUser mapping post user Authorization****************");	
}	
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Trouble Ticket End User using GET Method.");	
}
return troubleticketDAO.troubleTicketEndUser();
}
else {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into troubleTicketEndUser mapping and user is Unauthorization****************");	
}	
return null;
}
}

@PostMapping("/postTroubleTicketEndUser")
public int  postTroubleTicketEndUser(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into email ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Inserting Trouble Ticket End User using POST Method.");	
}
return troubleticketDAO.postTroubleTicketEndUser(genericPostBody);
}

@PostMapping("/postTroubleTicketLevels")
public int  postTroubleTicketLevels(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postTroubleTicketLevels ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Trouble Ticket Levels using POST Method.");	
}

return troubleticketDAO.postTroubleTicketLevels(genericPostBody);
}


//========Harsh=======Performance APIs=====


//tree for all the performance data	during kpi_creation
@GetMapping("/get_tree_kpi_creation/{adminId}/{opco}")
public List<check_g_s_parent> get_tree_kpi_creation(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId,@PathVariable String opco)
{

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if(log.isDebugEnabled()) {
log.info("*************** Enter into get_tree_kpi_creation mapping post user Authorization****************");
} 
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Tree KPI Creation using GET Method.");	
}

return performanceGenericDao.get_tree_kpi_creation(opco);
} 
else {
if(log.isDebugEnabled()) { 
log.info("*************** Enter into get_tree_kpi_creation mapping and user is Unauthorization****************");
}
return null;
}
}



@GetMapping("/getTableSpecificColsValsConditionGeneric/{adminId}/{opco}/{domain}/{vendor}/{tableName}/{columns}/{condition}/{tabletype}")

public String getTableSpecificColsValsConditionGeneric( @PathVariable int adminId, @PathVariable String opco,@PathVariable String tableName, @PathVariable String domain,@PathVariable String vendor,@PathVariable String columns, @PathVariable String condition, @PathVariable String tabletype) {
/*
public String getTableSpecificColsValsConditionGeneric(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String opco,@PathVariable String tableName, @PathVariable String domain,@PathVariable String vendor,@PathVariable String columns, @PathVariable String condition, @PathVariable String tabletype) {

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableColsValsGeneric mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Table Specifc Cols Vals using GET Method.");	
}
return performanceGenericDao.getTableSpecificColsValsConditionGeneric(adminId,opco,domain,vendor,tableName,columns,condition,tabletype);       
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableColsValsGeneric mapping and user is Unauthorization****************");
}
return null;

}
*/
	return performanceGenericDao.getTableSpecificColsValsConditionGeneric(adminId,opco,domain,vendor,tableName,columns,condition,tabletype);    
}



//to insert kpi from create kpi feature  pending
@PostMapping("/postkpi_insert")
public int insert_kpi(@RequestBody kpi_insert_columns kic) {
if(log.isDebugEnabled()) {	
log.debug("***************  Enter into insert_kpi mapping  ****************");	
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+kic.getAdmin_id()+" inserting KPI using POST Method.");	
}
return performanceGenericDao.insert_kpi(kic.getOpco(),kic.getAdmin_id(),kic.getDomain(),kic.getVendor(),kic.getKpi_name(),kic.getGroup(), kic.getActual_formula(), kic.getFormula(),kic.getThreshold(), kic.getTopology(),kic.getRate(),kic.getElement(),kic.getCalculation(),kic.getTrouble_ticket(),kic.getSeverity());
}


//performance_IPRAN_IPBB_DAO

//----to get local system name corresponding to perticular ip
@GetMapping("performance/ipran_ipbb/elementname/{opco}/{admin_id}/{domain_name}/{vendor_name}/{type}/{report_name}")
public ArrayList <String > get_br_local_information( @PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain_name,@PathVariable String vendor_name,@PathVariable String type,@PathVariable String report_name) { 
	/*
public ArrayList <String > get_br_local_information(@RequestHeader("Authorization") String authorizationToken, @PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain_name,@PathVariable String vendor_name,@PathVariable String type,@PathVariable String report_name) { 

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_br_local_information mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance IPRAN IPBB using GET Method.");	
}
return performance_IPRAN_IPBB_DAO.get_br_local_information(opco,admin_id,domain_name,vendor_name,type,report_name);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_br_local_information mapping and user is Unauthorization****************");
}
return null;

}
*/
	return performance_IPRAN_IPBB_DAO.get_br_local_information(opco,admin_id,domain_name,vendor_name,type,report_name);
}


//---to get interface name for corresponding device name----

@GetMapping("performance/br/interface_name/{opco}/{admin_id}/{domain_name}/{vendor_name}/{device_name}/{ip}")
//public ArrayList <String > get_br_interface_name(@RequestHeader("Authorization") String authorizationToken,@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain_name,@PathVariable String vendor_name,@PathVariable String device_name,@PathVariable String ip) { //----------HARSH-----------
public ArrayList <String > get_br_interface_name(@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain_name,@PathVariable String vendor_name,@PathVariable String device_name,@PathVariable String ip) { //----------HARSH-----------


return performance_IPRAN_IPBB_DAO.get_br_interface_name(opco,admin_id,domain_name,vendor_name,device_name,ip);
	
	/*
	String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_br_interface_name mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance Interface Name using GET Method.");	
}
return performance_IPRAN_IPBB_DAO.get_br_interface_name(opco,admin_id,domain_name,vendor_name,device_name,ip);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_br_interface_name mapping and user is Unauthorization****************");
}
return null;



}
*/
}




//-----to get ipname of perticular local system----ipbb

@GetMapping("performance/ipbb/ipaddress/{opco}/{admin_id}/{domain_name}/{vendor}/{device_name}/{sla}")
public ArrayList <String > get_ipbb_iplist(@RequestHeader("Authorization") String authorizationToken,@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain_name,@PathVariable String vendor,@PathVariable String device_name,@PathVariable String sla) { //----------HARSH-----------
return performance_IPRAN_IPBB_DAO.get_ipbb_iplist(opco,admin_id,domain_name,vendor,device_name,sla);
	
	/*
	
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_ipbb_iplist mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance IPBB IP List using GET Method.");	
}
return performance_IPRAN_IPBB_DAO.get_ipbb_iplist(opco,admin_id,domain_name,vendor,device_name,sla);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_ipbb_iplist mapping and user is Unauthorization****************");
}
return null;

}

*/
}





//-----get kpi tree for apran and ipbn---



@GetMapping("performance/br/get_kpi_name/{opco}/{admin_id}/{domain_name}/{vendor}/{type}/{device_name}/{element_key}")

public List<tree_parents_g_s> get_br_kpi_name(@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain_name,@PathVariable String vendor,@PathVariable String type,@PathVariable String device_name,@PathVariable String element_key)

//public List<tree_parents_g_s> get_br_kpi_name(@RequestHeader("Authorization") String authorizationToken,@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain_name,@PathVariable String vendor,@PathVariable String type,@PathVariable String device_name,@PathVariable String element_key)
{
	
	return performance_IPRAN_IPBB_DAO.get_br_kpi_name(opco,admin_id,domain_name,vendor,type,device_name,element_key);
	/*
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_br_kpi_name mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance KPI Name using GET Method.");	
}
return performance_IPRAN_IPBB_DAO.get_br_kpi_name(opco,admin_id,domain_name,vendor,type,device_name,element_key);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_br_kpi_name mapping and user is Unauthorization****************");
}
return null;



}
*/
}



@GetMapping("service_stitching/{vlan}")
public String new_topology(@PathVariable String vlan)
{
	
	////System.out.println(ip_auditDAO.ring_topology(opco));
	
return performance_IPRAN_IPBB_DAO.service_stitching(vlan);
	
}


@GetMapping("new_topology_vis/{domain}/{elementname}")
public String new_topology_vis(@PathVariable String domain,@PathVariable String elementname)
{
	
	////System.out.println(ip_auditDAO.ring_topology(opco));
	
return performance_IPRAN_IPBB_DAO.new_topology_vis(domain,elementname);
	
}



@GetMapping("new_topology/{opco}/{domain}/{vendor}/{topology_type}/{key}/{click_type}")
public String new_topology(@PathVariable String opco,@PathVariable String domain,@PathVariable String vendor,@PathVariable String topology_type,@PathVariable String key,@PathVariable String click_type)
{
	
	////System.out.println(ip_auditDAO.ring_topology(opco));
	
return performance_IPRAN_IPBB_DAO.new_topology(opco,domain,vendor,topology_type,key,click_type);
	
}

//@GetMapping("test_topology/{selectedDomain}/")
//public String test_topology(@PathVariable String selectedDomain )
//{
//	return performance_IPRAN_IPBB_DAO.test_topology(selectedDomain);
//}



//BY UTKARSH
@GetMapping("topology/{admin_id}/{selectedOpco}/{selectedDomain}/{selectedVendor}/{selected_topology_type}/{key}/{click_type}")
public String topology(@RequestHeader("Authorization") String authorizationToken,@PathVariable String admin_id,@PathVariable String selectedOpco,@PathVariable String selectedDomain,@PathVariable String selectedVendor,@PathVariable String selected_topology_type,@PathVariable String key, @PathVariable
		String click_type )	{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into topology mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance KPI Report Group Name using GET Method.");	
}
return performance_IPRAN_IPBB_DAO.topology(selectedOpco,selectedDomain,selectedVendor,selected_topology_type,key,click_type);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into topology mapping and user is Unauthorization****************");
}
return null;

}
}


/*
@GetMapping("topology/{selectedOpco}/{selectedDomain}/{selectedVendor}/{selected_topology_type}/{key}/{click_type}")
public String topology(@PathVariable String selectedOpco,@PathVariable String selectedDomain,@PathVariable String selectedVendor,@PathVariable String selected_topology_type,@PathVariable String key, @PathVariable
		String click_type )
{
	return performance_IPRAN_IPBB_DAO.topology(selectedOpco,selectedDomain,selectedVendor,selected_topology_type,key,click_type);
}
*/

@GetMapping("cpu_memory_gauge/{opco}/{domain}/{vendor}/{type}/{ip}/{element_name}")
public String cpu_memory_gauge(@PathVariable String opco,@PathVariable String domain,@PathVariable String vendor,@PathVariable String type,@PathVariable String ip,@PathVariable String element_name)
{
	
	////System.out.println(ip_auditDAO.ring_topology(opco));
	
return performance_IPRAN_IPBB_DAO.cpu_memory_gauge(opco,domain,vendor,type,ip,element_name);
	
}

//for zte pc kpi dual_axis------


@GetMapping("performance/ip/kpi/get_ip_graph/{opco}/{admin_id}/{vendor}/{domain}/{kpiname}/{ne_name}/{filter1}/{filter2}/{duration}/{start_date}/{end_date}/{starttime}/{endtime}/{apn}/{check_axis}/{type}/{graph_type}")
public dual_axis_1_date_format get_ip_graph(@PathVariable String opco,@PathVariable String admin_id,@PathVariable String vendor,@PathVariable String domain,@PathVariable ArrayList<String> kpiname, @PathVariable ArrayList<String> ne_name, @PathVariable ArrayList<String> filter1, @PathVariable ArrayList<String> filter2,@PathVariable String duration,@PathVariable String start_date, @PathVariable String end_date, @PathVariable String starttime, @PathVariable String endtime,@PathVariable String apn,@PathVariable ArrayList<String> check_axis,@PathVariable String type,@PathVariable String graph_type)
{
	
	return performance_IPRAN_IPBB_DAO.get_ip_graph(opco,admin_id,vendor,domain,kpiname,ne_name,filter1,filter2,duration,start_date,end_date,starttime,endtime,apn,check_axis,type,graph_type);

	
	/*
	
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_ip_graph mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance IP Graph using GET Method.");	
}
return performance_IPRAN_IPBB_DAO.get_ip_graph(opco,admin_id,vendor,domain,kpiname,ne_name,filter1,filter2,duration,start_date,end_date,starttime,endtime,apn,check_axis,type,graph_type);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_ip_graph mapping and user is Unauthorization****************");
}
return null;

}
*/
}

//----get kpi names created by user

@GetMapping("performance/getKpiNameExcel/{opco}/{admin_id}/{domain}/{vendor}/{element_name}")
//public ArrayList <String > getKpiNameExcel(@RequestHeader("Authorization") String authorizationToken,@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain,@PathVariable String vendor,@PathVariable String element_name) { //----------HARSH-----------
public ArrayList <String > getKpiNameExcel(@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain,@PathVariable String vendor,@PathVariable String element_name) { //----------HARSH-----------

	return performanceGenericDao.getKpiNameExcel(opco,admin_id,domain,vendor,element_name);
	/*
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getKpiNameExcel mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance KPI Name Excel using GET Method.");	
}
return performanceGenericDao.getKpiNameExcel(opco,admin_id,domain,vendor,element_name);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getKpiNameExcel mapping and user is Unauthorization****************");
}
return null;

}

*/
}



@PostMapping("/performance_management/postkpi_insert")
public int insert_user_specific_kpi(@RequestBody user_specific_kpi_excel kic) {
if(log.isDebugEnabled()) {	
log.debug("***************  Enter into insert_user_specific_kpi  ****************");	
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+kic.getUser_id()+" inserting Performance User Specific KPI's using POST Method.");	
}
return performanceGenericDao.insert_user_specific_kpi(kic.getOpco(),kic.getUser_id(),kic.getEvent_name(),kic.getKpi_name(),kic.getGroup(), kic.getActual_formula(), kic.getFormula(),kic.getThreshold(), kic.getTopology(),kic.getRate(),kic.getDomain(),kic.getElement(),kic.getCalculation(),kic.getVendor(),kic.getElement_list()
,kic.getInterface_select());
}




//------to get report names for kpis


@GetMapping("performance/existing_report_name/{opco}/{admin_id}/{element_name}/{vendor_name}/{domain_name}")
public ArrayList <String > kpi_report_group_name(@RequestHeader("Authorization") String authorizationToken,@PathVariable String opco,@PathVariable String admin_id,@PathVariable String element_name,@PathVariable String vendor_name,@PathVariable String domain_name) { //----------HARSH-----------
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into kpi_report_group_name mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance KPI Report Group Name using GET Method.");	
}
return performanceGenericDao.kpi_report_group_name(opco,admin_id,element_name,vendor_name,domain_name);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into kpi_report_group_name mapping and user is Unauthorization****************");
}
return null;

}
}


//----to get kpi related to report name----


@GetMapping("performance/existing_report_related_kpi/{opco}/{admin_id}/{report_name}/{element_name}/{vendor_name}/{domain_name}")
public ArrayList <String > report_related_kpi(@RequestHeader("Authorization") String authorizationToken,@PathVariable String opco,@PathVariable String admin_id,@PathVariable String report_name,@PathVariable String element_name,@PathVariable String vendor_name,@PathVariable String domain_name) { //----------HARSH-----------
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into report_related_kpi mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance Report Related KPIs using GET Method.");	
}
return performanceGenericDao.report_related_kpi(opco,admin_id,report_name,element_name,vendor_name,domain_name);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into report_related_kpi mapping and user is Unauthorization****************");
}
return null;

}
}




//------------------- excel report---------------------

@PostMapping("/performance_management/excel_report")
public int excel_report(@RequestBody auto_mail_generate_entity_br kic) {
if(log.isDebugEnabled()) {	
log.debug("***************  Enter into insert_kpi mapping  ****************");	
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+kic.getAdmin_id()+" accessing Performance Excel Report using POST Method.");	
}
return performanceGenericDao.excel_report(kic.getAdmin_id(),kic.getReport_name(),kic.getReport_duration(),kic.getReport_interval(),kic.getStart_date(),kic.getEnd_date(),kic.getStart_time(),kic.getEnd_time(),kic.getDomain(),kic.getVendor(),kic.getElement(),kic.getSave_report(),kic.getSingle_multiple(),kic.getMail());
}


//br  radio alarm blink------

@GetMapping("performance/element_blink/{opco}/{admin_id}/{domain}/{vendor}")
public ArrayList<performance_nokia_radio_element_blink_main> element_blink(@RequestHeader("Authorization") String authorizationToken,@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain,@PathVariable String vendor)
{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into performance_nokia_radio_element_blink_main mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance Element Blink using GET Method.");	
}
return performanceGenericDao.element_blink(opco,admin_id,domain,vendor);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into performance_nokia_radio_element_blink_main mapping and user is Unauthorization****************");
}
return null;

}

}



@GetMapping("/updateTable/{opco}/{admin_id}/{domain}/{vendor}/{kpiName}/{groupName}/{formula}/{rate}/{threshold}/{topology}/{severity}/{troubleticket}/{tablename}")//------------harsh
public int updateKpiTable(@RequestHeader("Authorization") String authorizationToken,@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain,@PathVariable String vendor,@PathVariable String kpiName,@PathVariable String groupName, @PathVariable String formula, @PathVariable String rate, @PathVariable String threshold, @PathVariable String topology, @PathVariable String severity, @PathVariable String troubleticket, @PathVariable String tablename) {
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into updateKpiTable mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance Kpi Table using GET Method.");	
}
return performanceGenericDao.updateKpiTable(opco, admin_id, domain,  vendor, kpiName,  groupName,  formula,  rate, threshold,  topology,  severity,  troubleticket,tablename);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into updateKpiTable mapping and user is Unauthorization****************");
}
return 0;

}
}



//---- get any list---------

@GetMapping("performance/get_any_list/{opco}/{admin_id}/{domain}/{vendor}/{element}/{type}")
public ArrayList<String> get_any_list(@RequestHeader("Authorization") String authorizationToken,@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain,@PathVariable String vendor,@PathVariable String element,@PathVariable String type)
{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_any_list mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance Any List using GET Method.");	
}
return performanceGenericDao.get_any_list(opco,admin_id,domain,vendor,element,type);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_any_list mapping and user is Unauthorization****************");
}
return null;

}

}

//---- get get_perameter_value---------

@GetMapping("performance/get_perameter_value/{opco}/{admin_id}/{domain}/{vendor}/{elementname}/{table}/{slot}/{notcontain}")
public ArrayList<String> get_perameter_value(@RequestHeader("Authorization") String authorizationToken,@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain,@PathVariable String vendor,@PathVariable String elementname,@PathVariable String table,@PathVariable String slot,@PathVariable ArrayList<String> notcontain)
{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_perameter_value mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance Parameter Value using GET Method.");	
}
return performanceMicrowaveDAO.get_parameter_value(opco,admin_id,domain,vendor,elementname,table,slot,notcontain);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_perameter_value mapping and user is Unauthorization****************");
}
return null;

}


}



//for microwave kpi dual_axis------


@GetMapping("microwave_performance/graph/{opco}/{admin_id}/{vendor}/{domain}/{kpiname}/{ne_name}/{key}/{reportname}/{duration}/{start_date}/{end_date}/{starttime}/{endtime}/{check_axis}/{type}")
public dual_axis_1 get_microwave_graph(@RequestHeader("Authorization") String authorizationToken,@PathVariable String opco,@PathVariable String admin_id,@PathVariable String vendor,@PathVariable String domain,@PathVariable ArrayList<String> kpiname, @PathVariable ArrayList<String> ne_name, @PathVariable ArrayList<String> key,@PathVariable String reportname,@PathVariable String duration,@PathVariable String start_date, @PathVariable String end_date, @PathVariable String starttime, @PathVariable String endtime,@PathVariable ArrayList<String> check_axis,@PathVariable String type)
{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_microwave_graph mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance Microwave Graph using GET Method.");	
}
return performanceMicrowaveDAO.get_microwave_graph(opco,admin_id,vendor,domain,kpiname,ne_name,key,reportname,duration,start_date,end_date,starttime,endtime,check_axis,type);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_microwave_graph mapping and user is Unauthorization****************");
}
return null;

}

}



@GetMapping("microwave_performance/nce_graph/{opco}/{admin_id}/{vendor}/{domain}/{kpiname}/{ne_name}/{key}/{reportname}/{duration}/{start_date}/{end_date}/{starttime}/{endtime}/{check_axis}/{type}")
public dual_axis_1 get_nce_microwave_graph(@RequestHeader("Authorization") String authorizationToken,@PathVariable String opco,@PathVariable String admin_id,@PathVariable String vendor,@PathVariable String domain,@PathVariable ArrayList<String> kpiname, @PathVariable ArrayList<String> ne_name, @PathVariable ArrayList<String> key,@PathVariable  ArrayList<String> reportname,@PathVariable String duration,@PathVariable String start_date, @PathVariable String end_date, @PathVariable String starttime, @PathVariable String endtime,@PathVariable ArrayList<String> check_axis,@PathVariable String type)
{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_nce_microwave_graph mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance NCE Microwave Graph using GET Method.");	
}
return performanceMicrowaveDAO.get_nce_microwave_graph(opco,admin_id,vendor,domain,kpiname,ne_name,key,reportname,duration,start_date,end_date,starttime,endtime,check_axis,type);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_nce_microwave_graph mapping and user is Unauthorization****************");
}
return null;

}
}


//tree for microwave

//tree for all the performance data	during kpi_creation
@GetMapping("/get_tree_microwave/{opco}/{admin_id}/{domain}/{vendor}/{report}/{elementname}/{type}/{kpi_name}")
public List<check_g_s_parent_micro> get_tree_microwave(@RequestHeader("Authorization") String authorizationToken,@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain,@PathVariable String vendor,@PathVariable String report,@PathVariable String elementname,@PathVariable String type,@PathVariable String kpi_name)
{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_tree_microwave mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance Tree Microwave using GET Method.");	
}
return performanceMicrowaveDAO.get_tree_microwave(opco,admin_id,domain,vendor,report,elementname,type,kpi_name);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_tree_microwave mapping and user is Unauthorization****************");
}
return null;

}
}

@GetMapping("/getTableSpecificColsValsConditionGenericInventory/{opco}/{adminId}/{domain}/{vendor}/{start_date}/{end_date}/{report_name}/{columns}/{condition}")
public String getTableSpecificColsValsConditionGenericInventory(@RequestHeader("Authorization") String authorizationToken,@PathVariable String opco, @PathVariable String adminId,@PathVariable String domain,@PathVariable String vendor,@PathVariable String start_date,@PathVariable String end_date,@PathVariable String report_name,@PathVariable ArrayList<String>  columns, @PathVariable ArrayList<String> condition)
{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(adminId));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_tree_microwave mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Performance Table Columns Generic Inventory using GET Method.");	
}
return performanceMicrowaveDAO.getTableSpecificColsValsConditionGenericInventory(opco,adminId,domain,vendor,start_date,end_date,report_name,columns,condition);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_tree_microwave mapping and user is Unauthorization****************");
}
return null;

}

}


//to insert multiple_sheets
@PostMapping("/post_create_multiple_sheet")
public int create_multiple_sheet(@RequestBody multiple_sheets sheet) {
if(log.isDebugEnabled()) {	
log.debug("***************  Enter into insert_kpi mapping  ****************");	
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+sheet.getAdmin_id()+" accessing Performance Multiple Sheet using POST Method.");	
}
return performanceGenericDao.create_multiple_sheet(sheet.getOpco(),sheet.getAdmin_id(),sheet.getDomain(),sheet.getVendor(),sheet.getReport_name(),sheet.getSheets());
}




//to insert multiple_sheets
@PostMapping("/post_insert_schduling")
public int post_insert_schduling(@RequestBody String data) {
if(log.isDebugEnabled()) {	
log.debug("***************  Enter into insert_kpi mapping  ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Inserting Performance scheduling using POST Method.");	
}
return performanceGenericDao.insert_schdule_report(data);
}




//------------------- visibility consolidate report---------------------

@PostMapping("/performance_management/complete_visibility_report")
public int complete_visibility_report(@RequestBody visibility visi) {
if(log.isDebugEnabled()) {	
log.debug("***************  Enter into complete_visibility_report  ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("UserId:- "+visi.getAdminId()+" accessing Performance Visibility Report using POST Method.");	
}
return performanceMicrowaveDAO.excel_visibility_report(visi.getOpco(),visi.getAdminId(),visi.getDomain(),visi.getVendor(),visi.getWeek());
}

/*
//------For Alarm Report------------------------------
@GetMapping("/getAlarmExcelReport/{adminId}")
public ResponseEntity<InputStreamResource> getAlarmExcelReport(@RequestHeader("Authorization") String authorizationToken,@PathVariable int adminId,HttpServletResponse response) throws Exception{
String fileName=""; 
InputStreamResource file=null;
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {   
if(log.isDebugEnabled()) {    
log.debug("*************** Enter into getAlarmExcelReport mapping post user Authorization****************"); 
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Alarm Elexcel Report using GET Method.");	
}
Properties config=getProperties();
String timezone=config.getProperty("timezone");
SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
sdf.setTimeZone(TimeZone.getTimeZone(timezone));
String date=sdf.format(new Date());
String filename="TransmissionEricssonAlarmsReport_"+date+".xlsx";

int ret=alarmManagementDAO.getAlarmReportEricsson(filename);
if(ret>0) {
fileName=config.getProperty("performance_mail_report_path")+filename;
File downloadFile= new File(fileName);

byte[] isr = Files.readAllBytes(downloadFile.toPath());
file = new InputStreamResource(new ByteArrayInputStream(isr));
return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
             .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
        .body(file);
}
return null;
}

else {
if(log.isDebugEnabled()) {    
log.debug("*************** Enter into getAlarmExcelReport mapping and user is Unauthorization****************");      
}      
return null;
}
}

*/



//---- get_distinct_list---------

@GetMapping("performance/get_distinct_list/{opco}/{admin_id}/{domain}/{vendor}/{type}/{where1}/{where2}/{where3}/{where4}")
public ArrayList<String> get_distinct_list(@RequestHeader("Authorization") String authorizationToken,@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain,@PathVariable String vendor,@PathVariable String type,@PathVariable String where1,@PathVariable String where2,@PathVariable String where3,@PathVariable String where4)
{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_distinct_list mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance Distinct List using GET Method.");	
}
return performanceGenericDao.get_distinct_list(opco,admin_id,domain,vendor,type,where1,where2,where3,where4);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_distinct_list mapping and user is Unauthorization****************");
}
return null;

}


}




//IP PING AUDIT=======


@GetMapping("ipaudit/ping/struture/{opco}/{vendor}/{topology_type}/{ring_id}/{click_type}")
public String ip_audit_structure(@PathVariable String opco,@PathVariable String vendor,@PathVariable String topology_type,@PathVariable String ring_id,@PathVariable String click_type)
{
	
	////System.out.println(ip_auditDAO.ring_topology(opco));
	
return performance_IPRAN_IPBB_DAO.ip_audit_structure(opco,vendor,topology_type,ring_id,click_type);
	
}

//----to get ping failed elements
@GetMapping("ipaudit/ping/elementname/{opco}/{admin_id}/{domain_name}/{vendor_name}")
public ArrayList <String > get_failed_ping_elements( @PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain_name,@PathVariable String vendor_name) { 
	/*
public ArrayList <String > get_failed_ping_elements(@RequestHeader("Authorization") String authorizationToken, @PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain_name,@PathVariable String vendor_name,@PathVariable String type,@PathVariable String report_name) { 

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_br_local_information mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance IPRAN IPBB using GET Method.");	
}
return performance_IPRAN_IPBB_DAO.get_br_local_information(opco,admin_id,domain_name,vendor_name,type,report_name);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_br_local_information mapping and user is Unauthorization****************");
}
return null;

}
*/
	return performance_IPRAN_IPBB_DAO.get_failed_ping_elements(opco,admin_id,domain_name,vendor_name);
}

@GetMapping("ipaudit/ssh_output/{opco}/{domain}/{vendor}/{element_address}/{command}")
public ArrayList<String> ipaudit_ssh_output(@PathVariable String opco,@PathVariable String domain,@PathVariable String vendor,@PathVariable String element_address,@PathVariable String command)
{
	
	////System.out.println(ip_auditDAO.ring_topology(opco));
	
return performance_IPRAN_IPBB_DAO.ipaudit_ssh_output(opco,domain,vendor,element_address,command);
	
}



@GetMapping("configuration_output")
public ArrayList<String> configuration_output()
{
	
	////System.out.println(ip_auditDAO.ring_topology(opco));
	
return performance_IPRAN_IPBB_DAO.configuration_output();
	
}




//TODO to download visibility excel report

@GetMapping("/getVisibilityExcelReport/{adminId}/{filename}")
public ResponseEntity<InputStreamResource> getExcelVisibilityReport(@RequestHeader("Authorization") String authorizationToken,@PathVariable int adminId,@PathVariable String filename,HttpServletResponse response) throws Exception{
String fileName=""; 
InputStreamResource file=null;
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {   
if(log.isDebugEnabled()) {    
log.debug("*************** Enter into getExcelReport mapping post user Authorization****************"); 
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Performance Visibility Excel Report using GET Method.");	
}
Properties config=getProperties();


String report_directiory = config.getProperty("performance_mail_visibility_report");
String report_directiory_path=report_directiory+"\\Transmission_Visibility_Status_"+adminId+".xlsx";

File downloadFile= new File(report_directiory_path);

byte[] isr = Files.readAllBytes(downloadFile.toPath());
file = new InputStreamResource(new ByteArrayInputStream(isr));
return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
           .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
      .body(file);

}

else {
if(log.isDebugEnabled()) {    
log.debug("*************** Enter into getAuditData mapping and user is Unauthorization****************");      
}      
return null;
}
}

//TODO to download get_ipbb_summary_report

@GetMapping("/get_ipbb_summary_report/{adminId}/{date}")
public ResponseEntity<InputStreamResource> get_ipbb_summary_report(@RequestHeader("Authorization") String authorizationToken,@PathVariable int adminId,@PathVariable String date,HttpServletResponse response) throws Exception{

String fileName=""; 
InputStreamResource file=null;
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {   
if(log.isDebugEnabled()) {    
log.debug("*************** Enter into getExcelReport mapping post user Authorization****************"); 
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Performance IPBB Summary Report using GET Method.");	
}
Properties config=getProperties();
DateFormat formatter ; 

formatter = new SimpleDateFormat(config.getProperty("performance.dateformat1"));
Date startDate;

startDate = (Date)formatter.parse(date);
String start_date = new SimpleDateFormat(config.getProperty("performance.dateformat3")).format(startDate);

String report_directiory = config.getProperty("performance_mail_mpbn_report");
String report_directiory_path=report_directiory+"Summary Report Zambia "+start_date+".xlsx";

File downloadFile= new File(report_directiory_path);

byte[] isr = Files.readAllBytes(downloadFile.toPath());
file = new InputStreamResource(new ByteArrayInputStream(isr));
return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
         .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
    .body(file);

}

else {
if(log.isDebugEnabled()) {    
log.debug("*************** Enter into getAuditData mapping and user is Unauthorization****************");      
}      
return null;
}
}

//TODO to download excel report

@GetMapping("/getExcelReport/{adminId}/{filename}")
public ResponseEntity<InputStreamResource> getExcelReport(@RequestHeader("Authorization") String authorizationToken,@PathVariable int adminId,@PathVariable String filename,HttpServletResponse response) throws Exception{
String fileName=""; 
InputStreamResource file=null;
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {   
if(log.isDebugEnabled()) {    
log.debug("*************** Enter into getExcelReport mapping post user Authorization****************"); 
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Performance Excel Report using GET Method.");	
}

Properties config=getProperties();
fileName=config.getProperty("performance_mail_report_path")+filename;

File downloadFile= new File(fileName);

byte[] isr = Files.readAllBytes(downloadFile.toPath());
file = new InputStreamResource(new ByteArrayInputStream(isr));
return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
             .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
        .body(file);

}

else {
if(log.isDebugEnabled()) {    
log.debug("*************** Enter into getAuditData mapping and user is Unauthorization****************");      
}      
return null;
}
}



//TODO to download report

@GetMapping("/getCSVReport/{adminId}/{filename}/{duration}/{date}")
public ResponseEntity<InputStreamResource> getCSVReport(@RequestHeader("Authorization") String authorizationToken,@PathVariable int adminId,@PathVariable String filename,@PathVariable String duration,@PathVariable String date,HttpServletResponse response) throws Exception{
String fileName=""; 
InputStreamResource file=null;
////System.out.println("test1");
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {   
if(log.isDebugEnabled()) {    
log.debug("*************** Enter into getExcelReport mapping post user Authorization****************"); 
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Performance CSV Report using GET Method.");	
}
Properties config=getProperties();
DateFormat formatter ; 

formatter = new SimpleDateFormat(config.getProperty("performance.dateformat1"));
Date startDate;

startDate = (Date)formatter.parse(date);
String start_date = new SimpleDateFormat(config.getProperty("performance.dateformat3")).format(startDate);
String file_name=filename+"_"+duration+"_"+start_date.replace("-","")+".zip";
////System.out.println(file_name);

fileName=config.getProperty("performance_mail_nec_csv_path")+filename+"/"+start_date.replace("-","")+"/"+file_name;

File downloadFile= new File(fileName);

byte[] isr = Files.readAllBytes(downloadFile.toPath());
file = new InputStreamResource(new ByteArrayInputStream(isr));
return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
           .contentType(MediaType.parseMediaType("application/zip"))
      .body(file);

}

else {
if(log.isDebugEnabled()) {    
log.debug("*************** Enter into getAuditData mapping and user is Unauthorization****************");      
}      
return null;
}
}

@GetMapping("/getInterfaceTraffic/{adminId}/{domain}/{vendor}/{tableName}/{columns}/{condition}")
public String getInterfaceTraffic(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId,@PathVariable String domain, @PathVariable String vendor, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition) {

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getInterfaceTraffic mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Performance Interface Traffic using GET Method.");	
}
return performanceGenericDao.getInterfaceTraffic(domain, vendor, tableName, columns,condition);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getInterfaceTraffic mapping and user is Unauthorization****************");
}
return null;

}

}

//--------------------------------------------Alarm Events--------------------------------------------------------------------------------

@PostMapping("/postAcknowledgeAlarms")
public int  postAcknowledgeAlarms(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postAcknowledgeAlarms ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Acknowledge Alarms using POST Method.");	
}
return alarmManagementDAO.postAcknowledgeAlarms(genericPostBody);
}

@PostMapping("/postClearAlarms")
public int  postClearAlarms(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postClearAlarms ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Clear Alarms using POST Method.");	
}
return alarmManagementDAO.postClearAlarms(genericPostBody);
}

@PostMapping("/postCommentAlarms")
public int  postCommentAlarms(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postCommentAlarms ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Comment Alarms using POST Method.");	
}

return alarmManagementDAO.postCommentAlarms(genericPostBody);
}

@PostMapping("/postSetCommandAlarms")
public int  postSetCommandAlarms(@RequestBody GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into postSetCommandAlarms ****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Changing Alarms using POST Method.");	
}

return alarmManagementDAO.postSetCommandAlarms(genericPostBody);
}

@GetMapping("/getAlarmsCount/{adminId}/{vendor}/{domain}/{protocol}")
public String getAlarmsCount(@RequestHeader("Authorization") String authorizationToken,@PathVariable int adminId,@PathVariable String vendor,@PathVariable String domain,@PathVariable String protocol)
{
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into getAlarmsCount mapping post user Authorization****************");	
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Alarms Count using GET Method.");	
}

return alarmManagementDAO.getAlarmsCount(vendor, domain, protocol);
}
else {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into getAlarmsCount mapping and user is Unauthorization****************");	
}	
return null;
}

}



//------------------------------------------------------------------------
//-----------------------------Analysis-------------------------------------------
//------------------------------------------------------------------------
/*@GetMapping("/analysis/graphCount/{adminId}/{tab1}+{tab2}-{span}/{elemSpecific}/{mailBoolean}")
public graph_parent_getter_setter graphCount(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tab1, @PathVariable String tab2,@PathVariable int span,@PathVariable String elemSpecific, @PathVariable String mailBoolean) {
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into graphCount mapping post user Authorization****************");
  }
  return analysisDAO.graphCount(tab1, tab2, span,elemSpecific, mailBoolean);
} else {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into graphCount mapping and user is Unauthorization****************");
  }
  return null;
}

}*/


//-----------kpi alarm analysis----
@GetMapping("/analysis/kaa/{kpiTabNameX}/{tableNameY}/{kpiName}")
public ArrayList<resultEntityKpi> kpiAlarmAnalysis(@PathVariable String kpiTabNameX, @PathVariable String tableNameY, @PathVariable String kpiName) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into kpiAlarmAnalysis mapping ****************");
}
return analysisDAO.kpiAlarmAnalysis(kpiTabNameX,tableNameY,kpiName); 
}
//----------live-----------------
@GetMapping("/analysis/kal/{tableName}/{dttm}/{inTime}")
public ArrayList<resultKpiLive> kpiAlarmLive(@PathVariable String tableName,@PathVariable String dttm, @PathVariable String inTime) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into kpiAlarmLive mapping ****************");
}
////System.out.println("1008Live>>>>>>>>>>>>>>>>>"+tableName+"inTime"+ inTime);
return analysisDAO.kpiAlarmLive(tableName, dttm, Integer.parseInt(inTime)); 
}


/*
//-----------kpi alarm analysis----
@GetMapping("/analysis/kaa/{kpiTabNameX}/{tableNameY}/{kpiName}")
public ArrayList<resultEntityKpi> kpiAlarmAnalysis(@PathVariable String kpiTabNameX, @PathVariable String tableNameY, @PathVariable String kpiName) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into kpiAlarmAnalysis mapping and user is Unauthorized****************");
}
return analysisDAO.kpiAlarmAnalysis(kpiTabNameX,tableNameY,kpiName); 
}
//----------live-----------------
@GetMapping("/analysis/kal/{tableName}/{dttm}")
public ArrayList<resultKpiLive> kpiAlarmLive(@PathVariable String tableName,@PathVariable String dttm) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into kpiAlarmLive mapping and user is Unauthorized****************");
}
return analysisDAO.kpiAlarmLive(tableName, dttm); 
}
*/

@GetMapping("/analysis/tbheader/{adminId}/{alarm1}+{alarm2}")
public ArrayList < TableHeader > tb_header(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String alarm1, @PathVariable String alarm2) { //----------sol table-----------
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into tb_header mapping post user Authorization****************");
  }
  if(log.isInfoEnabled()) {	
	  log.info("UserId:- "+adminId+" accessing AI&ML Header using GET Method.");	
	  }
  return analysisDAO.tbHeader(alarm1, alarm2);
} else {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into tb_header mapping and user is Unauthorization****************");
  }
  return null;
}

}

@GetMapping("/analysis/alarmtree/{adminId}/{domainX}/{domainY}/{comfirmity_100}")
public ArrayList < treeParent > alarmTree(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId,
		@PathVariable String domainX, @PathVariable String domainY,  @PathVariable String comfirmity_100) {
//-----------for alarm tree----
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into alarmTree mapping post user Authorization****************");
  }
  if(log.isInfoEnabled()) {	
	  log.info("UserId:- "+adminId+" accessing AI&ML Alarm Tree using GET Method.");	
	  }
  return analysisDAO.treeAlarm(domainX, domainY, comfirmity_100);
} else {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into alarmTree mapping and user is Unauthorization****************");
  }
  return null;
}

}

@GetMapping("/analysis/alarmtreeUser/{domainX}/{domainY}")
public ArrayList < treeParent > alarmTreeUser(
		@PathVariable String domainX, @PathVariable String domainY) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into alarmTreeUser mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("Accessing AI&ML Alarm Tree User using GET Method.");	
}
return analysisDAO.treeAlarmUser(domainX, domainY);
}

//////////////////////////////LIVE
@GetMapping("/analysis/kpiAlarmAnalysis/{alarmCollection}/{time5}")
public String kpiAlarmAnalysis(@PathVariable String alarmCollection, @PathVariable String time5) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into kpiAlarmAnalysis mapping ****************");
}
if(log.isInfoEnabled()) {	
log.info("Accessing AI&ML KPI Alarm Analysis using GET Method.");	
}
return analysisDAO.kpiAlarmAnalysis(alarmCollection, time5); 
}
@GetMapping("/analysis/kpikpiAnalysis/{adminID}/{kpi2Collection}/{time5}")
public String kpikpiAnalysis(@PathVariable String adminID,@PathVariable String kpi2Collection, @PathVariable String time5) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into kpikpiAnalysis mapping ****************");
}
if(log.isInfoEnabled()) {	
log.info("Accessing AI&ML KPI KPI Analysis using GET Method.");	
}
return analysisDAO.kpikpiAnalysis(adminID, kpi2Collection, time5); 
}
//////////////////////////////Confidence
@GetMapping("/analysis/kpiAlarmAnalysisConf/{kpiDomain}/{kpiVendor}/{kpiElement}/{kpiName}/{alarmCollection}/{confirmity}")
public ArrayList<analysisLogicEntity> kpiAlarmAnalysisConf( @PathVariable String kpiDomain,@PathVariable String kpiVendor,@PathVariable String kpiElement,@PathVariable String kpiName, @PathVariable String alarmCollection, @PathVariable String confirmity) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into kpiAlarmAnalysisConf mapping ****************");
}
if(log.isInfoEnabled()) {	
log.info("Accessing AI&ML KPI Alarm Analysis Conf using GET Method.");	
}
return analysisDAO.kpiAlarmAnalysisConf(kpiVendor,kpiDomain,kpiElement, kpiName, alarmCollection, confirmity); 
}
@GetMapping("/analysis/kpikpiAnalysisConf/{adminID}/{kpiDomain}/{kpiVendor}/{kpiElement}/{kpiName}/{alarmCollection}/{confirmity}")
public ArrayList<analysisLogicEntity> kpikpiAnalysisConf(@PathVariable String adminID, @PathVariable String kpiDomain,@PathVariable String kpiVendor,@PathVariable String kpiElement,@PathVariable String kpiName, @PathVariable String kpi2Collection, @PathVariable String confirmity) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into kpikpiAnalysisConf mapping ****************");
}
if(log.isInfoEnabled()) {	
log.info("Accessing AI&ML KPI KPI Analysis Conf using GET Method.");	
}
return analysisDAO.kpikpiAnalysisConf(adminID,kpiVendor,kpiDomain, kpiElement, kpiName, kpi2Collection, confirmity); 
}
@GetMapping("/analysis/alarmKpiAnalysisConf/{alarmDomain}/{alarmVendor}/{alarmElement}/{alarmName}/{kpiB}/{confirmity}")
public ArrayList<analysisLogicEntity> alarmKpiAnalysisConf(@PathVariable String alarmDomain,@PathVariable String alarmVendor,@PathVariable String alarmElement,@PathVariable String alarmName,@PathVariable String kpiB, @PathVariable String confirmity) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into alarmKpiAnalysisConf mapping ****************");
}
if(log.isInfoEnabled()) {	
log.info("Accessing AI&ML Alarm KPI Analysis Conf using GET Method.");	
}
return analysisDAO.alarmKpiAnalysisConf(alarmDomain,alarmVendor, alarmElement, alarmName, kpiB, confirmity); 
}
@GetMapping("/analysis/logCheck/{logOf}/{date}/{time}")
public String logCheck(@PathVariable String logOf,@PathVariable String date,@PathVariable String time) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into alarmKpiAnalysisConf mapping and user is Unauthorized****************");
}
if(log.isInfoEnabled()) {	
log.info("Accessing AI&ML Log Check using GET Method.");	
}
return analysisDAO.logCheck(logOf,date, time); 
}
@GetMapping("/analysis/AlarmAnalysisTable/{domainX}/{elementName}/{alarmName}/{domainY}/{confRate}")
public ArrayList <resultAlarmSolutionTable> AlarmAnalysisTable(@PathVariable String domainX,
		@PathVariable String elementName, @PathVariable String alarmName, @PathVariable String domainY, @PathVariable String confRate) { 
	log.debug("*************** Enter into AlarmAnalysisTable mapping ****************");
	if(log.isInfoEnabled()) {	
		log.info("Accessing AI&ML Alarm Analysis using GET Method.");	
		}
  return analysisDAO.alarmAnalysisTable(domainX, elementName,alarmName, domainY, confRate);

}
@GetMapping("/analysis/AlarmAnalysisTableUser/{domainX}/{elementName}/{alarmName}/{domainY}")
public ArrayList <resultAlarmSolutionTable> AlarmAnalysisTableUser(@PathVariable String domainX,
		@PathVariable String elementName, @PathVariable String alarmName, @PathVariable String domainY) { 
	log.debug("*************** Enter into AlarmAnalysisTableUser mapping ****************");
	if(log.isInfoEnabled()) {	
	log.info("Accessing AI&ML Alarm Analysis User using GET Method.");	
	}
  return analysisDAO.alarmAnalysisTableUser(domainX, elementName,alarmName, domainY);
}

/*
@GetMapping("/getAlarmTableColumns/{adminId}/{tableName}")
public ArrayList<TableHeader> getAlarmTableColumns(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName) { //----------file table-----------
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into getAlarmTableColumns mapping post user Authorization****************");
  }
  return topologyDAO.getAlarmTableColumns(tableName);
} else {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into getAlarmTableColumns mapping and user is Unauthorization****************");
  }
  return null;
}

}
*/

@GetMapping("/getAlarmTableData/{adminId}/{tableName}/{severity}")
public String getAlarmTableData(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName, @PathVariable String severity) { //----------file table-----------
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into getAlarmTableData mapping post user Authorization****************");
  }
  if(log.isInfoEnabled()) {	
	  log.info("UserId:- "+adminId+" accessing Alarm Table Data using GET Method.");	
	  }
  return topologyDAO.getAlarmTableData(tableName,severity);
} else {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into getAlarmTableData mapping and user is Unauthorization****************");
  }
  return null;
}

}

@GetMapping("/getAlarmHistoryTableData/{adminId}/{tableName}/{severity}/{orderby}")
public String getAlarmHistoryTableData(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName, @PathVariable String severity, @PathVariable String orderby) { //----------file table-----------
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into getAlarmHistoryTableData mapping post user Authorization****************");
  }
  if(log.isInfoEnabled()) {	
	  log.info("UserId:- "+adminId+" accessing Alarm History Data using GET Method.");	
	  }
  return topologyDAO.getAlarmHistoryTableData(tableName,severity,orderby);
} else {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into getAlarmHistoryTableData mapping and user is Unauthorization****************");
  }
  return null;
}

}

@GetMapping("/getAlarmTableDataZte/{adminId}/{tableName}/{columns}/{severity}/{orderby}")
public String getAlarmTableDataZte(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName, @PathVariable String columns, @PathVariable String severity, @PathVariable String orderby) {
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getAlarmTableDataZte mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Alarm Table Data Zte using GET Method.");	
}
return topologyDAO.getAlarmTableDataZte(tableName,columns,severity,orderby);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getAlarmTableDataZte mapping and user is Unauthorization****************");
}
return null;
}

}

@GetMapping("/getTableColumnsGeneric/{adminId}/{tableName}/{columns}/{condition}")
public String getTableColumnsGeneric(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition) {

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableColumnsGeneric mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Alarm Table Columns Generic using GET Method.");	
}
return topologyDAO.getTableColumnsGeneric(tableName,columns,condition);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableColumnsGeneric mapping and user is Unauthorization****************");
}
return null;

}

}

@GetMapping("/getTableDataGeneric/{adminId}/{tableName}/{columns}/{conditions}")
public String getTableDataGeneric(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName, @PathVariable String columns, @PathVariable String conditions) {
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataGeneric mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Alarm Table Data Generic using GET Method.");	
}
return topologyDAO.getTableDataGeneric(tableName,columns,conditions);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataGeneric mapping and user is Unauthorization****************");
}
return null;
}

}

@GetMapping("/getTableDataGenericWOC/{adminId}/{tableName}/{columns}/{conditions}")
public String getTableDataGenericWOC(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName, @PathVariable String columns, @PathVariable String conditions) {
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataGenericWOC mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Alarm Table Data Without Condition using GET Method.");	
}
return topologyDAO.getTableDataGeneric(tableName,columns,conditions);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataGenericWOC mapping and user is Unauthorization****************");
}
return null;
}

}


@GetMapping("/getTroubleTicketLevelColumnsGeneric/{adminId}")
public ArrayList<TableHeader> getTroubleTicketLevelColumnsGeneric(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId) {
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTroubleTicketLevelColumnsGeneric mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Trouble Ticket Levels using GET Method.");	
}
//return troubleticketDAO.getTableColumnsGeneric();
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTroubleTicketLevelColumnsGeneric mapping and user is Unauthorization****************");
}
}
return null;
}

@GetMapping("/getTroubleTicketLevelDataGeneric/{adminId}/{conditions}")
public String getTroubleTicketLevelDataGeneric(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String conditions) {
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTroubleTicketLevelDataGeneric mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Alarm Trouble Ticket Levels using GET Method.");	
}
//return troubleticketDAO.getTableDataGeneric(conditions);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTroubleTicketLevelDataGeneric mapping and user is Unauthorization****************");
}
}
return null;
}

@GetMapping("/getTableColsValsTroubleTicketGeneric/{adminId}/{tableName}")
public String getTableColsValsTroubleTicketGeneric(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName) {

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableColsValsTroubleTicketGeneric mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Trouble Ticket Cols Vals Generic using GET Method.");	
}
return troubleticketDAO.getTableColsValsTroubleTicketGeneric(tableName);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableColsValsTroubleTicketGeneric mapping and user is Unauthorization****************");
}
return null;

}

}

@GetMapping("/getTableColsValsTroubleTicketConditionGeneric/{adminId}/{tableName}/{condition}")
public String getTableColsValsTroubleTicketConditionGeneric(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName, @PathVariable String condition) {

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableColsValsTroubleTicketGeneric mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Trouble Ticket Cols Vals Condition Generic using GET Method.");	
}
return troubleticketDAO.getTableColsValsTroubleTicketConditionGeneric(tableName,condition);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableColsValsTroubleTicketGeneric mapping and user is Unauthorization****************");
}
return null;

}

}

@GetMapping("/getTableSpecificColsValsTroubleTicketConditionGeneric/{adminId}/{tableName}/{columns}/{condition}")
public String getTableSpecificColsValsTroubleTicketConditionGeneric(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition) {

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableColsValsTroubleTicketGeneric mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Trouble Ticket Cols Vals Condition Generic using GET Method.");	
}
return troubleticketDAO.getTableSpecificColsValsTroubleTicketConditionGeneric(tableName,columns,condition);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableColsValsTroubleTicketGeneric mapping and user is Unauthorization****************");
}
return null;

}

}

@GetMapping("/getTableDataOnElementGeneric/{adminId}/{tableName}/{columns}/{conditions}/{order_by}")
public String getTableDataOnElementGeneric(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName, @PathVariable String columns, @PathVariable String conditions, @PathVariable String order_by) {
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {	
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataOnElementGeneric mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Table Data on Element Generic using GET Method.");	
}
return topologyDAO.getTableDataOnElementGeneric(tableName,columns,conditions,order_by);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataOnElementGeneric mapping and user is Unauthorization****************");
}
return null;
}

}


@GetMapping("/getTableDataOnElementGenericWOO/{adminId}/{tableName}/{columns}/{conditions}")
public String getTableDataOnElementGenericWOO(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName, @PathVariable String columns, @PathVariable String conditions) {
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {	
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataOnElementGenericWOO mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Table Data on Element Generic WOO using GET Method.");	
}
return topologyDAO.getTableDataGeneric(tableName,columns,conditions);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataOnElementGenericWOO mapping and user is Unauthorization****************");
}
return null;
}

}

@GetMapping("/getTableDataOnElementGenericSpecificDomain/{adminId}/{tableName}/{columns}/{conditions}/{order_by}/{specificColumn}/{nameLike}")
public String getTableDataOnElementGenericSpecificDomain(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName, @PathVariable String columns, @PathVariable String conditions, @PathVariable String order_by, @PathVariable String specificColumn, @PathVariable String nameLike) {
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataOnElementGenericSpecificDomain mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Table Data on Element Generic Specific Domain using GET Method.");	
}
return topologyDAO.getTableDataOnElementGenericSpecificDomain(tableName,columns,conditions,order_by,specificColumn,nameLike);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataOnElementGenericSpecificDomain mapping and user is Unauthorization****************");
}
return null;
}

}

@GetMapping("/getTableDataOnElementGenericSpecificDomainWOO/{adminId}/{tableName}/{columns}/{conditions}/{specificColumn}/{nameLike}")
public String getTableDataOnElementGenericSpecificDomainWOO(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName, @PathVariable String columns, @PathVariable String conditions, @PathVariable String specificColumn, @PathVariable String nameLike) {
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataOnElementGenericSpecificDomainWOO mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Table Data on Element Generic Specific Domain WOO using GET Method.");	
}
return topologyDAO.getTableDataOnElementGenericSpecificDomainWOO(tableName,columns,conditions,specificColumn,nameLike);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataOnElementGenericSpecificDomainWOO mapping and user is Unauthorization****************");
}
return null;
}

}

@GetMapping("/getTableDataGenericResolution/{adminId}/{tableName}/{columns}/{conditions}/{alarmId}")
public String getTableDataGenericResolution(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName, @PathVariable String columns, @PathVariable String conditions, @PathVariable String alarmId) {
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataGeneric mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing Table Data Resolution using GET Method.");	
}
return topologyDAO.getTableDataGenericResolution(tableName,columns,conditions,alarmId);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getTableDataGeneric mapping and user is Unauthorization****************");
}
return null;
}

}

@GetMapping("/getAlarmListAndSiteName/{adminId}/{tableName}")
public String getAlarmListAndSiteName(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName) {
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into getAlarmListAndSiteName mapping post user Authorization****************");
  }
  if(log.isInfoEnabled()) {	
	  log.info("UserId:- "+adminId+" accessing Alarm List and SiteName using GET Method.");	
	  }
  return topologyDAO.getAlarmListAndSiteName(tableName);
} else {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into getAlarmListAndSiteName mapping and user is Unauthorization****************");
  }
  return null;
}

}
/*
@PostMapping("/analysis/insertAlarmSol")
public int insertSol(@RequestBody solutionManuplation sm) {
if (log.isDebugEnabled()) {
  log.debug("*************** Enter into insertAlarmSol mapping and User is Unauthorized****************");
}
//-----------insert solution----
return analysisDAO.insertAlarmSol(sm);
}
@PostMapping("/analysis/insertKpiSol")
public int insetKpiSol(@RequestBody solutionKpi ks) {
if (log.isDebugEnabled()) {
  log.debug("*************** Enter into insertKpiSol mapping and user is Unauthorized****************");
}
//-----------insert solution----
return analysisDAO.insertKpiSol(ks);
}

*/
//-----------------------------------Intelligent Logic--------------------------------------------------------------------------

@GetMapping("/getIntelligentLogicUseCase/{adminId}/{protocol}/{usecase}/{command}/{ring}/{pattern}/{value}/{outputType}")
public String getIntelligentLogicUseCaseOthers(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String protocol, @PathVariable String usecase, @PathVariable String command, @PathVariable String ring, @PathVariable String pattern, @PathVariable String value,@PathVariable String outputType) {

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);

if (result > 0) {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into getIntelligentLogicUseCase mapping post user Authorization****************");
  }
  if(log.isInfoEnabled()) {	
	  log.info("UserId:- "+adminId+" accessing Intelligent Logic Use Case using GET Method.");	
	  }
  return intelligentLogicDAO.getIntelligentLogicUseCase(protocol,usecase,command,ring,pattern,value,outputType);
} else {
  if (log.isDebugEnabled()) {
    log.debug("*************** Enter into getIntelligentLogicUseCase mapping and user is Unauthorization****************");
  }
  return null;
}

}

@PostMapping("/uploadTextDataGeneric")
public int uploadTextDataGeneric(@RequestParam("file") MultipartFile file,@RequestParam("data") String data) throws Exception {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into uploadTextDataGeneric mapping and user is Authorized****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Accessing Text Data Generic using POST Method.");	
}
return intelligentLogicDAO.uploadTextDataGeneric(file,data);
}

@GetMapping("/getIslDetails/{adminId}/{tableName}/{columns}/{condition}/{orderby}")
public String getIslDetails(@RequestHeader("Authorization") String authorizationToken, @PathVariable int adminId, @PathVariable String tableName,@PathVariable String columns, @PathVariable String condition, @PathVariable String orderby) {

String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], adminId);
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getIslDetails mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+adminId+" accessing ISL Details using GET Method.");	
}
return intelligentLogicDAO.getIslDetails(tableName,columns,condition, orderby);	
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into getIslDetails mapping and user is Unauthorization****************");
}
return null;

}

}

//-------------------------------------------------------AI/ML--------------------------------------------------------------------------------------------------

/*
@PostMapping("/analysis/insertUserResolution")
public int insertUserResolution(@RequestBody solutionManuplation sm) {
if (log.isDebugEnabled()) {
  log.debug("*************** Enter into insertAlarmSol mapping and User is Unauthorized****************");
}
//-----------insert solution---- 
return analysisDAO.insertUserAlarmSol(sm);
}*/
@PostMapping("/analysis/userCorelFile")
public int userCorelFile(@RequestParam("file") MultipartFile file) throws Exception {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into userCorelFile mapping and user is Unauthorization****************");	
}	
if(log.isInfoEnabled()) {	
log.info("Upload User Corel using POST Method.");	
}
return analysisDAO.userCorelFile(file);
}/*
@GetMapping("/analysis/saResult/{alarmX}")
public ArrayList<resultEntityTable> singleAlarmResult(@PathVariable String alarmX) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into singleAlarmResult mapping and user is Unauthorized****************");
}
return analysisDAO.singleAlarmResult(alarmX); 
}*/
///////////////////////YANG
@PostMapping("/yangUpload")//pending
public int yangUpload(@RequestParam("file") MultipartFile file) throws Exception {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into yangRPC mapping and user is Unauthorization****************");	
}	
return analysisDAO.netconfRPC(file);
//
}
@PostMapping("/netconfRPC")
public int netconfRPC(@RequestParam("file") MultipartFile file) throws Exception {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into netconfRPC mapping and user is Unauthorization****************");	
}	
return analysisDAO.netconfRPC(file);
//
}
@GetMapping("/netconfGetRPCreply")
public String netconfGetRPCreply() {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into netconfGetRPCreply mapping and user is Unauthorized****************");
}
return analysisDAO.netconfGetRPCreply(); 
}
@PostMapping("/netconfUIedit")
public int netconfUI(@RequestBody yangDHCP yDhcp) throws Exception {
if(log.isDebugEnabled()) {	
log.debug("*************** Enter into netconfUI mapping and user is Unauthorization****************");	
}	
return analysisDAO.netconfUIedit(yDhcp);
//
}
@GetMapping("/netconfUIget/{yangName}")
public String netconfUIget(@PathVariable String yangName) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into netconfUIget mapping and user is Unauthorized****************");
}
return analysisDAO.netconfUIget(yangName); 
}



//FOR INSERT MULTIPLE KPIs GRAPH

//to insert kpi from create kpi feature  pending
@PostMapping("/post_multiple_graph_kpi_insert")
public int insert_kpi(@RequestBody create_json create_json) {
if(log.isDebugEnabled()) {	
log.debug("***************  Enter into insert_kpi mapping  ****************");	
}



return performanceGenericDao.insert_create_multiple_graphs(create_json.getAdmin_id(),create_json.getGraph_name(),create_json.getGraph_count(),create_json.getDomain(),create_json.getVendor(),create_json.getElementname(),create_json.getIp(),create_json.getInterfacee(),create_json.getKpi_list(),create_json.getGraph_type(),create_json.getSla_threshold());
}




//get multipleKPIs Input

@GetMapping("/getmultipleinputs/{opco}/{adminId}/{group}")

public String getMultipleInputs( @PathVariable String opco,@PathVariable String adminId,@PathVariable String group) {

	return performanceGenericDao.get_multiple_graphs(opco,adminId,group);    
}




//get anylist


//get multipleKPIs Input

@GetMapping("/get_anylist/{opco}/{admin_id}/{domain}/{vendor}/{type}/{check1}/{check2}/{check3}/{check4}")

public ArrayList<String> get_any_list( @PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain,@PathVariable String vendor,@PathVariable String type,@PathVariable String check1,@PathVariable String check2,@PathVariable String check3,@PathVariable String check4) {

	return performanceGenericDao.get_any_list( opco, admin_id, domain, vendor, type, check1, check2, check3, check4);    
}




@GetMapping("performance/ip/kpi/get_ip_multiple_graph/{opco}/{admin_id}/{vendor}/{domain}/{kpiname}/{ne_name}/{filter1}/{filter2}/{duration}/{start_date}/{end_date}/{starttime}/{endtime}/{apn}/{check_axis}/{type}/{graph_type}/{sla}")
public dual_axis_1_date_format get_ip_multiple_graph(@PathVariable String opco,@PathVariable String admin_id,@PathVariable String vendor,@PathVariable String domain,@PathVariable ArrayList<String> kpiname, @PathVariable ArrayList<String> ne_name, @PathVariable ArrayList<String> filter1, @PathVariable ArrayList<String> filter2,@PathVariable String duration,@PathVariable String start_date, @PathVariable String end_date, @PathVariable String starttime, @PathVariable String endtime,@PathVariable String apn,@PathVariable ArrayList<String> check_axis,@PathVariable String type,@PathVariable String graph_type
		,@PathVariable String sla)
{
	
	return performanceGenericDao.get_ip_graph(opco,admin_id,vendor,domain,kpiname,ne_name,filter1,filter2,duration,start_date,end_date,starttime,endtime,apn,check_axis,type,graph_type,sla);

	
	/*
	
String token[] = authorizationToken.split(" ");
int result = tokenDAO.tokenAuthentication(token[1], Integer.parseInt(admin_id));
if (result > 0) {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_ip_graph mapping post user Authorization****************");
}
if(log.isInfoEnabled()) {	
log.info("UserId:- "+admin_id+" accessing Performance IP Graph using GET Method.");	
}
return performance_IPRAN_IPBB_DAO.get_ip_graph(opco,admin_id,vendor,domain,kpiname,ne_name,filter1,filter2,duration,start_date,end_date,starttime,endtime,apn,check_axis,type,graph_type);
} 
else {
if (log.isDebugEnabled()) {
log.debug("*************** Enter into get_ip_graph mapping and user is Unauthorization****************");
}
return null;

}
*/
}



//=======================5G=================================

@GetMapping("/5g/cause/{protocol}")
public String cause_graph(@PathVariable String protocol)
{
	
	System.out.println(protocol);
	
return performanceGenericDao.cause_graph(protocol);
	
}

//=======================5G=================================

@GetMapping("/5g/cause_name")
public ArrayList<String> cause_graph()
{
	
	
	
return performanceGenericDao.causes();
	
}


//=======================5G=================================

@GetMapping("/5g/right_click/{protocol}")
public String rightclick(@PathVariable String protocol)
{
	
	
	
return performanceGenericDao.cause_right_click(protocol);
	
}


//=======================5G=================================

@GetMapping("/5g/barclick/{protocol}/{barname}/{item_clicked}")
public String click_on_graph(@PathVariable String protocol,@PathVariable String barname,@PathVariable String item_clicked)
{


	
return performanceGenericDao.click_on_graph(protocol,barname,item_clicked);
	
}



//=======================5G=================================

@GetMapping("/ipbb/{opco}/{admin_id}/{domain}")
public String ipbb_ipran(@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain)
{


	
return performanceMicrowaveDAO.vendor_nodes_details(opco,admin_id,domain);
	
}

//=======================5G=================================

@GetMapping("/gisview/{opco}/{admin_id}/{domain}")
public String gis_details(@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain)
{


	
return performanceMicrowaveDAO.gis_details(opco,admin_id,domain);
	
}






@GetMapping("/getcommand/{opco}/{admin_id}/{domain}/{vendor}")
public ArrayList<String>get_command(@PathVariable String opco,@PathVariable String admin_id,@PathVariable String domain,@PathVariable String vendor)
{


	
return performanceMicrowaveDAO.get_command(opco,admin_id,domain,vendor);
	
}


}
