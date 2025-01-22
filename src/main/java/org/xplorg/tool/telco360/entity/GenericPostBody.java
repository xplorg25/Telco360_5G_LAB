package org.xplorg.tool.telco360.entity;

import java.util.ArrayList;

public class GenericPostBody {
	
int adminID;
public String emailId, name,password,role;	
	
String tableName,fileName,resolveString,aId,severity,userId,systemId,nameServiceIOR,resolveStringNotificationIRP,
resolveStringCSIRP,resolveStringAlarmIRP,severityTopology,severityTroubleTicket,alarmNumbersTopology,
alarmNumbersTroubleTicket,alarmRangeTopology,alarmRangeTroubleTicket,ackStateTopology,ackStateTroubleTicket,alarmExistenceCritical,alarmExistenceMajor,alarmExistenceMinor,alarmExistenceWarning,
resolveStringFTIRP,resolveStringEPIRP,resolveStringKernelIRP,filter,timeTick,vendor,urlAddress;

String emailArea,emailSubject,emailText,filePath;

String troubleTicketUserId,troubleTicketId,troubleTicketArea,troubleTicketRegion,troubleTicketDomain,troubleTicketNode,troubleTicketNodeName,troubleTicketPriority,troubleTicketComments,
troubleTicketOpeningDateTime,troubleTicketClosingDateTime,troubleTicketStatus,troubleTicketRCA,troubleTicketResolutionTime,troubleTicketVendor;

String snmpAgentHostname,snmpAgentPort,snmpAgentCommunity,snmpAgentVersion,snmpManagerHostname,snmpManagerPort,snmpManagerCommunity,snmpManagerVersion;

String smeUserId,smeDomain,smeArea,smeName,smeEmailId,smeContactDetails;

String level1,level2,level3,level4;

String ne,netype,nenameincore,nenameinradio,nehandler,cellids,regionname,latitude,longitude;

String ilalarms,ilalarms_count,ilcluster,ilconfiguration_command,ilconfiguration_pattern,ildomain,ilelement,ilmonitoring_time,iloperation1,iloperation2
,ilperformance,ilprotocol,ilvendor;

String credentialsDomain,credentialsNename,credentialsHostname,credentialsUsername,credentialsPassword;

String createUserId,createUserEmailId,createUserName,createUserPassword,createUserRole;

String userPermissionUserId,userPermissionFeature,userPermissionModule,userPermissionDomain,userPermissionVendor,userPermissionPermission;

String report_name,reporting;

String nename;

String changePasswordUserId,changePasswordEmailId,changePasswordName,changePasswordOldPassword,changePasswordNewPassword;

String domain;

String ipRangeStart,ipRangeEnd;

String alarmsTopology,alarmsTroubleTicket;

String alarmDomain,alarmProtocol,alarmCode,alarmName,alarmElementName,alarmVendor,alarmDate,alarmTime;

String commandSetType,commandSetValue;

ArrayList<String>vendorlist=new ArrayList<String>();
ArrayList<String>domainlist=new ArrayList<String>();
ArrayList<String>severitylist=new ArrayList<String>();
ArrayList<String>typelist=new ArrayList<String>();
ArrayList<String>forlist=new ArrayList<String>();
String filterlist,filtervalue;

public static String commandTaskOutput;


public String getFiltervalue() {
	return filtervalue;
}

public void setFiltervalue(String filtervalue) {
	this.filtervalue = filtervalue;
}

public ArrayList<String> getVendorlist() {
	return vendorlist;
}

public void setVendorlist(ArrayList<String> vendorlist) {
	this.vendorlist = vendorlist;
}

public ArrayList<String> getDomainlist() {
	return domainlist;
}

public void setDomainlist(ArrayList<String> domainlist) {
	this.domainlist = domainlist;
}

public ArrayList<String> getSeveritylist() {
	return severitylist;
}

public void setSeveritylist(ArrayList<String> severitylist) {
	this.severitylist = severitylist;
}

public ArrayList<String> getTypelist() {
	return typelist;
}

public void setTypelist(ArrayList<String> typelist) {
	this.typelist = typelist;
}

public ArrayList<String> getForlist() {
	return forlist;
}

public void setForlist(ArrayList<String> forlist) {
	this.forlist = forlist;
}

public String getFilterlist() {
	return filterlist;
}

public void setFilterlist(String filterlist) {
	this.filterlist = filterlist;
}

public String getSmeUserId() {
	return smeUserId;
}

public void setSmeUserId(String smeUserId) {
	this.smeUserId = smeUserId;
}

public String getCommandSetValue() {
	return commandSetValue;
}

public void setCommandSetValue(String commandSetValue) {
	this.commandSetValue = commandSetValue;
}

public String getCommandSetType() {
	return commandSetType;
}

public void setCommandSetType(String commandSetType) {
	this.commandSetType = commandSetType;
}

public String getAlarmDate() {
	return alarmDate;
}

public void setAlarmDate(String alarmDate) {
	this.alarmDate = alarmDate;
}

public String getAlarmTime() {
	return alarmTime;
}

public void setAlarmTime(String alarmTime) {
	this.alarmTime = alarmTime;
}

public String getAlarmDomain() {
	return alarmDomain;
}

public void setAlarmDomain(String alarmDomain) {
	this.alarmDomain = alarmDomain;
}

public String getAlarmProtocol() {
	return alarmProtocol;
}

public void setAlarmProtocol(String alarmProtocol) {
	this.alarmProtocol = alarmProtocol;
}

public String getAlarmCode() {
	return alarmCode;
}

public void setAlarmCode(String alarmCode) {
	this.alarmCode = alarmCode;
}

public String getAlarmName() {
	return alarmName;
}

public void setAlarmName(String alarmName) {
	this.alarmName = alarmName;
}

public String getAlarmElementName() {
	return alarmElementName;
}

public void setAlarmElementName(String alarmElementName) {
	this.alarmElementName = alarmElementName;
}

public String getAlarmVendor() {
	return alarmVendor;
}

public void setAlarmVendor(String alarmVendor) {
	this.alarmVendor = alarmVendor;
}

public int getAdminID() {
	return adminID;
}

public void setAdminID(int adminID) {
	this.adminID = adminID;
}

public String getEmailId() {
	return emailId;
}

public void setEmailId(String emailId) {
	this.emailId = emailId;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getPassword() {
	return password;
}

public void setPassword(String password) {
	this.password = password;
}

public String getRole() {
	return role;
}

public void setRole(String role) {
	this.role = role;
}

public String getChangePasswordUserId() {
	return changePasswordUserId;
}

public void setChangePasswordUserId(String changePasswordUserId) {
	this.changePasswordUserId = changePasswordUserId;
}

public String getChangePasswordEmailId() {
	return changePasswordEmailId;
}

public void setChangePasswordEmailId(String changePasswordEmailId) {
	this.changePasswordEmailId = changePasswordEmailId;
}

public String getChangePasswordName() {
	return changePasswordName;
}

public void setChangePasswordName(String changePasswordName) {
	this.changePasswordName = changePasswordName;
}

public String getChangePasswordOldPassword() {
	return changePasswordOldPassword;
}

public void setChangePasswordOldPassword(String changePasswordOldPassword) {
	this.changePasswordOldPassword = changePasswordOldPassword;
}

public String getChangePasswordNewPassword() {
	return changePasswordNewPassword;
}

public void setChangePasswordNewPassword(String changePasswordNewPassword) {
	this.changePasswordNewPassword = changePasswordNewPassword;
}

public String getUserPermissionUserId() {
	return userPermissionUserId;
}

public void setUserPermissionUserId(String userPermissionUserId) {
	this.userPermissionUserId = userPermissionUserId;
}

public String getUserPermissionFeature() {
	return userPermissionFeature;
}

public void setUserPermissionFeature(String userPermissionFeature) {
	this.userPermissionFeature = userPermissionFeature;
}

public String getUserPermissionModule() {
	return userPermissionModule;
}

public void setUserPermissionModule(String userPermissionModule) {
	this.userPermissionModule = userPermissionModule;
}

public String getUserPermissionDomain() {
	return userPermissionDomain;
}

public void setUserPermissionDomain(String userPermissionDomain) {
	this.userPermissionDomain = userPermissionDomain;
}

public String getUserPermissionVendor() {
	return userPermissionVendor;
}

public void setUserPermissionVendor(String userPermissionVendor) {
	this.userPermissionVendor = userPermissionVendor;
}

public String getUserPermissionPermission() {
	return userPermissionPermission;
}

public void setUserPermissionPermission(String userPermissionPermission) {
	this.userPermissionPermission = userPermissionPermission;
}

public String getCreateUserId() {
	return createUserId;
}

public void setCreateUserId(String createUserId) {
	this.createUserId = createUserId;
}

public String getCreateUserEmailId() {
	return createUserEmailId;
}

public void setCreateUserEmailId(String createUserEmailId) {
	this.createUserEmailId = createUserEmailId;
}

public String getCreateUserName() {
	return createUserName;
}

public void setCreateUserName(String createUserName) {
	this.createUserName = createUserName;
}

public String getCreateUserPassword() {
	return createUserPassword;
}

public void setCreateUserPassword(String createUserPassword) {
	this.createUserPassword = createUserPassword;
}

public String getCreateUserRole() {
	return createUserRole;
}

public void setCreateUserRole(String createUserRole) {
	this.createUserRole = createUserRole;
}

public String getFilePath() {
	return filePath;
}

public void setFilePath(String filePath) {
	this.filePath = filePath;
}

public String getNename() {
	return nename;
}

public void setNename(String nename) {
	this.nename = nename;
}

public String getReporting() {
	return reporting;
}

public void setReporting(String reporting) {
	this.reporting = reporting;
}

public String getReport_name() {
	return report_name;
}

public void setReport_name(String report_name) {
	this.report_name = report_name;
}

public String getCredentialsDomain() {
	return credentialsDomain;
}

public void setCredentialsDomain(String credentialsDomain) {
	this.credentialsDomain = credentialsDomain;
}

public String getCredentialsNename() {
	return credentialsNename;
}

public void setCredentialsNename(String credentialsNename) {
	this.credentialsNename = credentialsNename;
}

public String getCredentialsHostname() {
	return credentialsHostname;
}

public void setCredentialsHostname(String credentialsHostname) {
	this.credentialsHostname = credentialsHostname;
}

public String getCredentialsUsername() {
	return credentialsUsername;
}

public void setCredentialsUsername(String credentialsUsername) {
	this.credentialsUsername = credentialsUsername;
}

public String getCredentialsPassword() {
	return credentialsPassword;
}

public void setCredentialsPassword(String credentialsPassword) {
	this.credentialsPassword = credentialsPassword;
}

public String getIlalarms() {
	return ilalarms;
}

public void setIlalarms(String ilalarms) {
	this.ilalarms = ilalarms;
}

public String getIlalarms_count() {
	return ilalarms_count;
}

public void setIlalarms_count(String ilalarms_count) {
	this.ilalarms_count = ilalarms_count;
}

public String getIlcluster() {
	return ilcluster;
}

public void setIlcluster(String ilcluster) {
	this.ilcluster = ilcluster;
}

public String getIlconfiguration_command() {
	return ilconfiguration_command;
}

public void setIlconfiguration_command(String ilconfiguration_command) {
	this.ilconfiguration_command = ilconfiguration_command;
}

public String getIlconfiguration_pattern() {
	return ilconfiguration_pattern;
}

public void setIlconfiguration_pattern(String ilconfiguration_pattern) {
	this.ilconfiguration_pattern = ilconfiguration_pattern;
}

public String getIldomain() {
	return ildomain;
}

public void setIldomain(String ildomain) {
	this.ildomain = ildomain;
}

public String getIlelement() {
	return ilelement;
}

public void setIlelement(String ilelement) {
	this.ilelement = ilelement;
}

public String getIlmonitoring_time() {
	return ilmonitoring_time;
}

public void setIlmonitoring_time(String ilmonitoring_time) {
	this.ilmonitoring_time = ilmonitoring_time;
}

public String getIloperation1() {
	return iloperation1;
}

public void setIloperation1(String iloperation1) {
	this.iloperation1 = iloperation1;
}

public String getIloperation2() {
	return iloperation2;
}

public void setIloperation2(String iloperation2) {
	this.iloperation2 = iloperation2;
}

public String getIlperformance() {
	return ilperformance;
}

public void setIlperformance(String ilperformance) {
	this.ilperformance = ilperformance;
}

public String getIlprotocol() {
	return ilprotocol;
}

public void setIlprotocol(String ilprotocol) {
	this.ilprotocol = ilprotocol;
}

public String getIlvendor() {
	return ilvendor;
}

public void setIlvendor(String ilvendor) {
	this.ilvendor = ilvendor;
}

public String getNehandler() {
	return nehandler;
}

public void setNehandler(String nehandler) {
	this.nehandler = nehandler;
}

public String getNe() {
	return ne;
}

public void setNe(String ne) {
	this.ne = ne;
}

public String getNetype() {
	return netype;
}

public void setNetype(String netype) {
	this.netype = netype;
}

public String getNenameincore() {
	return nenameincore;
}

public void setNenameincore(String nenameincore) {
	this.nenameincore = nenameincore;
}

public String getNenameinradio() {
	return nenameinradio;
}

public void setNenameinradio(String nenameinradio) {
	this.nenameinradio = nenameinradio;
}

public String getCellids() {
	return cellids;
}

public void setCellids(String cellids) {
	this.cellids = cellids;
}

public String getRegionname() {
	return regionname;
}

public void setRegionname(String regionname) {
	this.regionname = regionname;
}

public String getLatitude() {
	return latitude;
}

public void setLatitude(String latitude) {
	this.latitude = latitude;
}

public String getLongitude() {
	return longitude;
}

public void setLongitude(String longitude) {
	this.longitude = longitude;
}

public String getSmeDomain() {
	return smeDomain;
}

public void setSmeDomain(String smeDomain) {
	this.smeDomain = smeDomain;
}

public String getAlarmsTopology() {
	return alarmsTopology;
}

public void setAlarmsTopology(String alarmsTopology) {
	this.alarmsTopology = alarmsTopology;
}

public String getAlarmsTroubleTicket() {
	return alarmsTroubleTicket;
}

public void setAlarmsTroubleTicket(String alarmsTroubleTicket) {
	this.alarmsTroubleTicket = alarmsTroubleTicket;
}

public String getDomain() {
	return domain;
}

public void setDomain(String domain) {
	this.domain = domain;
}

public String getFileName() {
	return fileName;
}

public void setFileName(String fileName) {
	this.fileName = fileName;
}

public String getResolveString() {
	return resolveString;
}

public void setResolveString(String resolveString) {
	this.resolveString = resolveString;
}

public String getaId() {
	return aId;
}

public void setaId(String aId) {
	this.aId = aId;
}

public String getSeverity() {
	return severity;
}

public void setSeverity(String severity) {
	this.severity = severity;
}

public String getUserId() {
	return userId;
}

public void setUserId(String userId) {
	this.userId = userId;
}

public String getSystemId() {
	return systemId;
}

public void setSystemId(String systemId) {
	this.systemId = systemId;
}

public String getResolveStringNotificationIRP() {
	return resolveStringNotificationIRP;
}

public void setResolveStringNotificationIRP(String resolveStringNotificationIRP) {
	this.resolveStringNotificationIRP = resolveStringNotificationIRP;
}

public String getResolveStringCSIRP() {
	return resolveStringCSIRP;
}

public void setResolveStringCSIRP(String resolveStringCSIRP) {
	this.resolveStringCSIRP = resolveStringCSIRP;
}

public String getResolveStringAlarmIRP() {
	return resolveStringAlarmIRP;
}

public void setResolveStringAlarmIRP(String resolveStringAlarmIRP) {
	this.resolveStringAlarmIRP = resolveStringAlarmIRP;
}

public String getTableName() {
	return tableName;
}

public void setTableName(String tableName) {
	this.tableName = tableName;
}

public String getSeverityTopology() {
	return severityTopology;
}

public void setSeverityTopology(String severityTopology) {
	this.severityTopology = severityTopology;
}

public String getSeverityTroubleTicket() {
	return severityTroubleTicket;
}

public void setSeverityTroubleTicket(String severityTroubleTicket) {
	this.severityTroubleTicket = severityTroubleTicket;
}

public String getAlarmNumbersTopology() {
	return alarmNumbersTopology;
}

public void setAlarmNumbersTopology(String alarmNumbersTopology) {
	this.alarmNumbersTopology = alarmNumbersTopology;
}

public String getAlarmNumbersTroubleTicket() {
	return alarmNumbersTroubleTicket;
}

public void setAlarmNumbersTroubleTicket(String alarmNumbersTroubleTicket) {
	this.alarmNumbersTroubleTicket = alarmNumbersTroubleTicket;
}

public String getAlarmRangeTroubleTicket() {
	return alarmRangeTroubleTicket;
}

public void setAlarmRangeTroubleTicket(String alarmRangeTroubleTicket) {
	this.alarmRangeTroubleTicket = alarmRangeTroubleTicket;
}

public String getNameServiceIOR() {
	return nameServiceIOR;
}

public void setNameServiceIOR(String nameServiceIOR) {
	this.nameServiceIOR = nameServiceIOR;
}

public String getResolveStringFTIRP() {
	return resolveStringFTIRP;
}

public void setResolveStringFTIRP(String resolveStringFTIRP) {
	this.resolveStringFTIRP = resolveStringFTIRP;
}

public String getResolveStringEPIRP() {
	return resolveStringEPIRP;
}

public void setResolveStringEPIRP(String resolveStringEPIRP) {
	this.resolveStringEPIRP = resolveStringEPIRP;
}

public String getResolveStringKernelIRP() {
	return resolveStringKernelIRP;
}

public void setResolveStringKernelIRP(String resolveStringKernelIRP) {
	this.resolveStringKernelIRP = resolveStringKernelIRP;
}

public String getFilter() {
	return filter;
}

public void setFilter(String filter) {
	this.filter = filter;
}

public String getTimeTick() {
	return timeTick;
}

public void setTimeTick(String timeTick) {
	this.timeTick = timeTick;
}

public String getVendor() {
	return vendor;
}

public void setVendor(String vendor) {
	this.vendor = vendor;
}

public String getUrlAddress() {
	return urlAddress;
}

public void setUrlAddress(String urlAddress) {
	this.urlAddress = urlAddress;
}

public String getAckStateTroubleTicket() {
	return ackStateTroubleTicket;
}

public void setAckStateTroubleTicket(String ackStateTroubleTicket) {
	this.ackStateTroubleTicket = ackStateTroubleTicket;
}

public String getAlarmRangeTopology() {
	return alarmRangeTopology;
}

public void setAlarmRangeTopology(String alarmRangeTopology) {
	this.alarmRangeTopology = alarmRangeTopology;
}

public String getAckStateTopology() {
	return ackStateTopology;
}

public void setAckStateTopology(String ackStateTopology) {
	this.ackStateTopology = ackStateTopology;
}

public String getEmailArea() {
	return emailArea;
}

public void setEmailArea(String emailArea) {
	this.emailArea = emailArea;
}

public String getEmailSubject() {
	return emailSubject;
}

public void setEmailSubject(String emailSubject) {
	this.emailSubject = emailSubject;
}

public String getEmailText() {
	return emailText;
}

public void setEmailText(String emailText) {
	this.emailText = emailText;
}

public String getTroubleTicketUserId() {
	return troubleTicketUserId;
}

public void setTroubleTicketUserId(String troubleTicketUserId) {
	this.troubleTicketUserId = troubleTicketUserId;
}

public String getTroubleTicketId() {
	return troubleTicketId;
}

public void setTroubleTicketId(String troubleTicketId) {
	this.troubleTicketId = troubleTicketId;
}

public String getTroubleTicketArea() {
	return troubleTicketArea;
}

public void setTroubleTicketArea(String troubleTicketArea) {
	this.troubleTicketArea = troubleTicketArea;
}

public String getTroubleTicketRegion() {
	return troubleTicketRegion;
}

public void setTroubleTicketRegion(String troubleTicketRegion) {
	this.troubleTicketRegion = troubleTicketRegion;
}

public String getTroubleTicketDomain() {
	return troubleTicketDomain;
}

public void setTroubleTicketDomain(String troubleTicketDomain) {
	this.troubleTicketDomain = troubleTicketDomain;
}

public String getTroubleTicketNode() {
	return troubleTicketNode;
}

public void setTroubleTicketNode(String troubleTicketNode) {
	this.troubleTicketNode = troubleTicketNode;
}

public String getTroubleTicketNodeName() {
	return troubleTicketNodeName;
}

public void setTroubleTicketNodeName(String troubleTicketNodeName) {
	this.troubleTicketNodeName = troubleTicketNodeName;
}

public String getTroubleTicketPriority() {
	return troubleTicketPriority;
}

public void setTroubleTicketPriority(String troubleTicketPriority) {
	this.troubleTicketPriority = troubleTicketPriority;
}

public String getTroubleTicketComments() {
	return troubleTicketComments;
}

public void setTroubleTicketComments(String troubleTicketComments) {
	this.troubleTicketComments = troubleTicketComments;
}

public String getTroubleTicketOpeningDateTime() {
	return troubleTicketOpeningDateTime;
}

public void setTroubleTicketOpeningDateTime(String troubleTicketOpeningDateTime) {
	this.troubleTicketOpeningDateTime = troubleTicketOpeningDateTime;
}

public String getTroubleTicketClosingDateTime() {
	return troubleTicketClosingDateTime;
}

public void setTroubleTicketClosingDateTime(String troubleTicketClosingDateTime) {
	this.troubleTicketClosingDateTime = troubleTicketClosingDateTime;
}

public String getTroubleTicketStatus() {
	return troubleTicketStatus;
}

public void setTroubleTicketStatus(String troubleTicketStatus) {
	this.troubleTicketStatus = troubleTicketStatus;
}

public String getTroubleTicketRCA() {
	return troubleTicketRCA;
}

public void setTroubleTicketRCA(String troubleTicketRCA) {
	this.troubleTicketRCA = troubleTicketRCA;
}

public String getTroubleTicketResolutionTime() {
	return troubleTicketResolutionTime;
}

public void setTroubleTicketResolutionTime(String troubleTicketResolutionTime) {
	this.troubleTicketResolutionTime = troubleTicketResolutionTime;
}

public String getTroubleTicketVendor() {
	return troubleTicketVendor;
}

public void setTroubleTicketVendor(String troubleTicketVendor) {
	this.troubleTicketVendor = troubleTicketVendor;
}

public String getSnmpAgentHostname() {
	return snmpAgentHostname;
}

public void setSnmpAgentHostname(String snmpAgentHostname) {
	this.snmpAgentHostname = snmpAgentHostname;
}

public String getSnmpAgentPort() {
	return snmpAgentPort;
}

public void setSnmpAgentPort(String snmpAgentPort) {
	this.snmpAgentPort = snmpAgentPort;
}

public String getSnmpAgentCommunity() {
	return snmpAgentCommunity;
}

public void setSnmpAgentCommunity(String snmpAgentCommunity) {
	this.snmpAgentCommunity = snmpAgentCommunity;
}

public String getSnmpAgentVersion() {
	return snmpAgentVersion;
}

public void setSnmpAgentVersion(String snmpAgentVersion) {
	this.snmpAgentVersion = snmpAgentVersion;
}

public String getSnmpManagerHostname() {
	return snmpManagerHostname;
}

public void setSnmpManagerHostname(String snmpManagerHostname) {
	this.snmpManagerHostname = snmpManagerHostname;
}

public String getSnmpManagerPort() {
	return snmpManagerPort;
}

public void setSnmpManagerPort(String snmpManagerPort) {
	this.snmpManagerPort = snmpManagerPort;
}

public String getSnmpManagerCommunity() {
	return snmpManagerCommunity;
}

public void setSnmpManagerCommunity(String snmpManagerCommunity) {
	this.snmpManagerCommunity = snmpManagerCommunity;
}

public String getSnmpManagerVersion() {
	return snmpManagerVersion;
}

public void setSnmpManagerVersion(String snmpManagerVersion) {
	this.snmpManagerVersion = snmpManagerVersion;
}

public String getSmeArea() {
	return smeArea;
}

public void setSmeArea(String smeArea) {
	this.smeArea = smeArea;
}

public String getSmeName() {
	return smeName;
}

public void setSmeName(String smeName) {
	this.smeName = smeName;
}

public String getSmeEmailId() {
	return smeEmailId;
}

public void setSmeEmailId(String smeEmailId) {
	this.smeEmailId = smeEmailId;
}

public String getSmeContactDetails() {
	return smeContactDetails;
}

public void setSmeContactDetails(String smeContactDetails) {
	this.smeContactDetails = smeContactDetails;
}

public static String getCommandTaskOutput() {
	return commandTaskOutput;
}

public static void setCommandTaskOutput(String commandTaskOutput) {
	GenericPostBody.commandTaskOutput = commandTaskOutput;
}

public String getAlarmExistenceCritical() {
	return alarmExistenceCritical;
}

public void setAlarmExistenceCritical(String alarmExistenceCritical) {
	this.alarmExistenceCritical = alarmExistenceCritical;
}

public String getAlarmExistenceMajor() {
	return alarmExistenceMajor;
}

public void setAlarmExistenceMajor(String alarmExistenceMajor) {
	this.alarmExistenceMajor = alarmExistenceMajor;
}

public String getAlarmExistenceMinor() {
	return alarmExistenceMinor;
}

public void setAlarmExistenceMinor(String alarmExistenceMinor) {
	this.alarmExistenceMinor = alarmExistenceMinor;
}

public String getAlarmExistenceWarning() {
	return alarmExistenceWarning;
}

public void setAlarmExistenceWarning(String alarmExistenceWarning) {
	this.alarmExistenceWarning = alarmExistenceWarning;
}

public String getIpRangeStart() {
	return ipRangeStart;
}

public void setIpRangeStart(String ipRangeStart) {
	this.ipRangeStart = ipRangeStart;
}

public String getIpRangeEnd() {
	return ipRangeEnd;
}

public void setIpRangeEnd(String ipRangeEnd) {
	this.ipRangeEnd = ipRangeEnd;
}

public String getLevel1() {
	return level1;
}

public void setLevel1(String level1) {
	this.level1 = level1;
}

public String getLevel2() {
	return level2;
}

public void setLevel2(String level2) {
	this.level2 = level2;
}

public String getLevel3() {
	return level3;
}

public void setLevel3(String level3) {
	this.level3 = level3;
}

public String getLevel4() {
	return level4;
}

public void setLevel4(String level4) {
	this.level4 = level4;
}




}
