package org.xplorg.tool.telco360.DAO.interfaces;

import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;
import org.xplorg.tool.telco360.entity.TableHeader;
import org.xplorg.tool.telco360.entity.analysisLogicEntity;
import org.xplorg.tool.telco360.entity.resultAlarmSolutionTable;
import org.xplorg.tool.telco360.entity.resultEntityKpi;
import org.xplorg.tool.telco360.entity.resultKpiLive;
import org.xplorg.tool.telco360.entity.treeParent;
import org.xplorg.tool.telco360.entity.yangDHCP;

public interface AnalysisDAO {
//	public int insertAlarmSol(solutionManuplation sm);
//  public int insertKpiSol(solutionKpi sk);
//  public int insertUserAlarmSol(solutionManuplation sm);
  //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
//  public graph_parent_getter_setter graphCount(String tab1, String tab2, int span,String elemSpecific, String mailBoolean);
  public ArrayList < treeParent > treeAlarm(String domainX, String domainY, String comfirmity_100);
  public ArrayList < treeParent > treeAlarmUser(String domainX, String domainY);
  public String boundElement(String alarm1, String alarm2);
  public ArrayList<resultEntityKpi> kpiAlarmAnalysis(String kpiX, String domainY, String kpiName);
  public ArrayList < resultKpiLive > kpiAlarmLive(String tableName, String dttm, int inTime);
  public ArrayList < TableHeader > tbHeader(String alarm1, String alarm2);
  public ArrayList <resultAlarmSolutionTable> alarmAnalysisTable(String domainX, String elementName, String alarmName, String domainY, String confRate);
  public ArrayList <resultAlarmSolutionTable> alarmAnalysisTableUser(String domainX, String elementName, String alarmName, String domainY);
//  public ArrayList < resultEntityTable > singleAlarmResult(String alarmX);
  public int userCorelFile(MultipartFile file);
  public int netconfRPC(MultipartFile file);
  public String netconfGetRPCreply();
  public int netconfUIedit(yangDHCP dhcp);
  public String netconfUIget(String yangName);
  //+++++++++++++++++++harsh+++++++++++++++++++++
public int updateKpiTable(String tfKpiName,String tfGroupName,String f1);
public String kpiAlarmAnalysis(String alarmCollection, String time5);
public String kpikpiAnalysis(String adminID, String kpi2Domain, String time5);
public ArrayList<analysisLogicEntity> kpiAlarmAnalysisConf(String kpiVendor,String kpiDomain,String kpiElement,String  kpiName,String  alarmCollection,String  confirmity);
public ArrayList<analysisLogicEntity> kpikpiAnalysisConf(String adminID,String kpiVendor,String kpiDomain,String  kpiElement,String  kpiName,String  kpi2Collection,String  confirmity); 
public ArrayList<analysisLogicEntity> alarmKpiAnalysisConf(String alarmDomain,String alarmVendor,String  alarmElement,String  alarmName,String  kpiB,String  confirmity); 
public String logCheck(String logOf, String date,String time);
}
