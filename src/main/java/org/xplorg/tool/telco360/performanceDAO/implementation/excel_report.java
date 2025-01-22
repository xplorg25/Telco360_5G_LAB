package org.xplorg.tool.telco360.performanceDAO.implementation;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.xplorg.tool.telco360.entity.GenericPerformance;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
public class excel_report extends GenericPerformance {
static Logger log = LogManager.getLogger(excel_report.class.getName());
public void generate_report_excel(String user_report_name, MongoDatabase database, String admin_id, ArrayList < String > report_group, ArrayList < String > unique_dates, String start_time, String end_time, ArrayList < String > columns_for_report, String node, String uname, String table_for_nce, String domain, String vendor, String report_type, String save_report, String url, String mail) throws Exception {
log.debug("*********" + admin_id + "********* checked into insert into generate_report_excel   **" + domain + "===========" + vendor + "************");

DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
Date date = new Date();
String currentdate = dateFormat.format(date);
XSSFWorkbook workbook = new XSSFWorkbook();

CreationHelper createHelper = workbook.getCreationHelper();
XSSFCell cell;
//Hyperlink....
Hyperlink hypr[] = new Hyperlink[1500];
Properties config = getProperties();

//------------------------properties----------------------//

XSSFCellStyle hlinkstyle = workbook.createCellStyle();
XSSFFont hlinkfont = workbook.createFont();
hlinkfont.setUnderline(XSSFFont.U_SINGLE);
hlinkfont.setColor(HSSFColor.BLUE.index);
hlinkstyle.setFont(hlinkfont);
hlinkstyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
hlinkstyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
hlinkstyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
hlinkstyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);

XSSFCellStyle hlinkstyle1 = workbook.createCellStyle();
XSSFFont hlinkfont1 = workbook.createFont();
hlinkfont1.setUnderline(XSSFFont.U_SINGLE);
hlinkfont1.setColor(HSSFColor.BLACK.index);
hlinkstyle1.setFont(hlinkfont1);
hlinkstyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
hlinkstyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
hlinkstyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
hlinkstyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);

XSSFCellStyle row_style = workbook.createCellStyle();
XSSFFont row_font = workbook.createFont();

row_font.setColor(HSSFColor.BLACK.index);
row_style.setFont(row_font);
row_style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
row_style.setBorderTop(HSSFCellStyle.BORDER_THIN);
row_style.setBorderRight(HSSFCellStyle.BORDER_THIN);
row_style.setBorderLeft(HSSFCellStyle.BORDER_THIN);

//----------cover------------------//

CellStyle style3 = workbook.createCellStyle();
XSSFFont font3 = workbook.createFont();
font3.setFontHeightInPoints((short) 13);
font3.setColor(IndexedColors.INDIGO.getIndex());
font3.setBold(true);
style3.setFont(font3);
style3.setBorderBottom(HSSFCellStyle.BORDER_THIN);
style3.setBorderTop(HSSFCellStyle.BORDER_THIN);
style3.setBorderRight(HSSFCellStyle.BORDER_THIN);
style3.setBorderLeft(HSSFCellStyle.BORDER_THIN);

CellStyle style = workbook.createCellStyle();
XSSFFont font = workbook.createFont();
font.setFontHeightInPoints((short) 13);
font.setColor(IndexedColors.BLACK.getIndex());
style.setFont(font);
style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
style.setBorderTop(HSSFCellStyle.BORDER_THIN);
style.setBorderRight(HSSFCellStyle.BORDER_THIN);
style.setBorderLeft(HSSFCellStyle.BORDER_THIN);

XSSFSheet spreadsheet0 = workbook.createSheet("Cover");
XSSFRow rc = spreadsheet0.createRow((short) 2); //count change
spreadsheet0.autoSizeColumn(0);
CellStyle stylec = workbook.createCellStyle();
XSSFFont fontc = workbook.createFont();
fontc.setFontHeightInPoints((short) 30);
fontc.setColor(IndexedColors.INDIGO.getIndex());
fontc.setBold(true);
stylec.setFont(fontc);
cell = rc.createCell(1);
cell.setCellValue("DETAILS");
cell.setCellStyle(stylec);
spreadsheet0.autoSizeColumn(1);

rc = spreadsheet0.createRow((short) 3); //count change
cell = rc.createCell(1);
cell.setCellValue("Domain");
cell.setCellStyle(style3);
spreadsheet0.autoSizeColumn(1);

cell = rc.createCell(2);
cell.setCellValue(domain);
cell.setCellStyle(style);
spreadsheet0.autoSizeColumn(2);

rc = spreadsheet0.createRow((short) 4); //count change
cell = rc.createCell(1);
cell.setCellValue("Vendor");
cell.setCellStyle(style3);
spreadsheet0.autoSizeColumn(1);

cell = rc.createCell(2);
cell.setCellValue(vendor.replace("SAM", "NOKIA"));
cell.setCellStyle(style);
spreadsheet0.autoSizeColumn(2);
/*
rc = spreadsheet0.createRow((short) 5); //count change
cell = rc.createCell(1);
cell.setCellValue("Generated By");
cell.setCellStyle(style3);
spreadsheet0.autoSizeColumn(1);

cell = rc.createCell(2);
cell.setCellValue("Harsh");
cell.setCellStyle(style);
spreadsheet0.autoSizeColumn(2);
*/
rc = spreadsheet0.createRow((short) 5); //count change
cell = rc.createCell(1);
cell.setCellValue("Date And Time");
cell.setCellStyle(style3);
spreadsheet0.autoSizeColumn(1);

cell = rc.createCell(2);
cell.setCellValue(currentdate);
cell.setCellStyle(style);
spreadsheet0.autoSizeColumn(2);

rc = spreadsheet0.createRow((short) 6); //count change
cell = rc.createCell(1);
cell.setCellValue("URL");
cell.setCellStyle(style3);
spreadsheet0.autoSizeColumn(1);

/*
rc=spreadsheet0.createRow((short) 8);//count change
cell=rc.createCell(1);
cell.setCellValue("Report Name");
cell.setCellStyle(style3);
spreadsheet0.autoSizeColumn(1);

cell=rc.createCell(2);
cell.setCellValue(report_name);
cell.setCellStyle(style);
spreadsheet0.autoSizeColumn(2);
*/
XSSFSheet spreadsheet1 = workbook.createSheet("Summary");

XSSFRow r1 = spreadsheet1.createRow((short) 2); //count change

////////////////////////////////////////////////
CellStyle style2 = workbook.createCellStyle();
XSSFFont font2 = workbook.createFont();
font2.setFontHeightInPoints((short) 30);
font2.setColor(IndexedColors.DARK_BLUE.getIndex());
font2.setBold(true);
style2.setFont(font2);
cell = r1.createCell(0);
cell.setCellValue("CONTENT DETAIL");
cell.setCellStyle(style2);
spreadsheet1.autoSizeColumn(0);

//////////////////////////////////////////

XSSFRow r2 = spreadsheet1.createRow((short) 4); //count change

font3.setFontHeightInPoints((short) 13);
font3.setColor(IndexedColors.WHITE.getIndex());

font3.setBold(true);
style3.setVerticalAlignment(style3.ALIGN_CENTER);
style3.setAlignment(style3.ALIGN_CENTER);
style3.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
style3.setBottomBorderColor(IndexedColors.BLACK.getIndex());

style3.setRightBorderColor(IndexedColors.BLUE.getIndex());

style3.setTopBorderColor(IndexedColors.BLACK.getIndex());

style3.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
cell = r2.createCell(0);
cell.setCellValue("Report Name");
cell.setCellStyle(style3);
spreadsheet1.autoSizeColumn(0);

for (int i = 0; i < report_group.size(); i++) {

columns_for_report.clear();
if (domain.equals("IPBB") || domain.equals("IPRAN")) {
columns_for_report.add("ipaddress");
columns_for_report.add("devicename");
columns_for_report.add("start_date");
columns_for_report.add("start_time");
}
int k = i + 5;

hypr[i] = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
hypr[i].setAddress(report_group.get(i).replace("_ref", "") + "!A1");

XSSFRow row1 = spreadsheet1.createRow((short) k); //count change
cell = row1.createCell(0);
//cell.setCellValue(columns.get(i));
cell.setCellValue(report_group.get(i).replace("_ref", ""));
cell.setCellStyle(hlinkstyle1);
cell.setHyperlink(hypr[i]);
cell.setCellStyle(hlinkstyle);
spreadsheet1.autoSizeColumn(0);
spreadsheet1.autoSizeColumn(1);

String table_for_nce_nec = "";
ArrayList < String > kpis = null;
if (((domain.equals("TRANSMISSION") && (vendor.equals("NCE") || vendor.equals("NEC") || vendor.equals("SAM") || vendor.equals("ERICSSON"))))) {

log.debug("*********" + admin_id + "********* checking vendor and domain   **" + domain + "===========" + vendor + "************");

kpis = mongo_select1_where2(database, "kpi_name", "kpi_formula_report", "admin_id", admin_id, "groups", report_group.get(i));
try {
table_for_nce_nec = mongo_select1_where2(database, "element_name", "kpi_formula_report", "admin_id", admin_id, "groups", report_group.get(i)).get(0);

table_for_nce = table_for_nce_nec;

} catch (Exception e) {
	
log.error("Performance   Exception occurs:---"+domain+"------"+vendor+"------"+admin_id+"---" + e.getMessage(), e);

}
} else if (domain.equals("IPBB")) {
log.debug("*********" + admin_id + "********* checking vendor and domain   **" + domain + "===========" + vendor + "************");
//elements corresponding to reports
try {
table_for_nce_nec = (mongo_select1_where2(database, "command", "report_group", "admin_id", admin_id, "ReportName", report_group.get(i)).get(0));
kpis = mongo_select1_where2(database, "kpi_name", "kpi_formula", "admin_id", admin_id, "groups", report_group.get(i));
table_for_nce = table_for_nce_nec;

//System.out.printlnout.println("table_for_nce================"+table_for_nce);
} catch (Exception e) {
	log.error("Performance   Exception occurs:---"+domain+"------"+vendor+"------"+admin_id+"---" + e.getMessage(), e);
}
} else {
log.debug("*********" + admin_id + "********* checking vendor and domain   **" + domain + "===========" + vendor + "************");
table_for_nce_nec = (mongo_select1_where2(database, "command", "report_group", "admin_id", admin_id, "ReportName", report_group.get(i)).get(0));
kpis = mongo_select1_where2(database, "kpi_name", "kpi_formula", "admin_id", admin_id, "groups", report_group.get(i));
table_for_nce = table_for_nce_nec;
}

for (String kpi: kpis) {
columns_for_report.add(kpi);
}
//TODO

//TODO

//TODO

String url_address = config.getProperty("performance_mail_url_ip");
//elements corresponding to reports
String elements = (mongo_select1_where2(database, "ElementName", "report_group", "admin_id", admin_id, "ReportName", report_group.get(i)).get(0));

//interface for report
String interfacee = (mongo_select1_where2(database, "SelectInterface", "report_group", "admin_id", admin_id, "ReportName", report_group.get(i)).get(0));

if (domain.equals("TRANSMISSION") && vendor.equals("NCE")) {
log.debug("*********" + admin_id + "********* checking vendor 2 and domain   **" + domain + "===========" + vendor + "************");
try {

if (table_for_nce.contains("_Report")) {
url = "-----";
} else {
//url = url_address + "/performance/ncereport/" + domain + "/" + vendor + "/" + admin_id + "/nce";
}

cell = rc.createCell(2);
cell.setCellValue(url);
cell.setCellStyle(style);
spreadsheet0.autoSizeColumn(2);
sht_excel_for_nce_transmission(workbook, database, unique_dates, start_time, end_time, columns_for_report, report_group.get(i), node, elements, interfacee, table_for_nce);
} catch (Exception e) {

log.error("Performance   Exception occurs:---"+domain+"------"+vendor+"------"+admin_id+"---" + e.getMessage(), e);
}
} else if (domain.equals("TRANSMISSION") && vendor.equals("SAM")) {
log.debug("*********" + admin_id + "********* checking 2 vendor and domain   **" + domain + "===========" + vendor + "************");

try {

sht_excel_for_sam_transmission(workbook, database, unique_dates, start_time, end_time, columns_for_report, report_group.get(i), node, elements, interfacee, table_for_nce);
} catch (Exception e) {

log.error("Performance   Exception occurs:---"+domain+"------"+vendor+"------"+admin_id+"---" + e.getMessage(), e);
}
} else if (domain.equals("TRANSMISSION") && vendor.equals("ERICSSON")) {
try {

if (table_for_nce.contains("IP_report") || table_for_nce.contains("Sync_report")) {
url = "-----";
}

//	else {
//		url = url_address+"/Telco360/performance/ncereport/" + domain + "/" + vendor + "/" +admin_id+"/nce" ;
//}

log.debug("*********" + admin_id + "********* checking vendor and domain   **" + domain + "===========" + vendor + "************");
sht_excel_for_ericsson_transmission(workbook, database, unique_dates, start_time, end_time, columns_for_report, report_group.get(i), node, elements, interfacee, table_for_nce);
} catch (Exception e) {

	log.error("Performance   Exception occurs:---"+domain+"------"+vendor+"------"+admin_id+"---" + e.getMessage(), e);
}
} else if (domain.equals("TRANSMISSION") && vendor.equals("NEC")) {
log.debug("*********" + admin_id + "********* checking vendor and domain   **" + domain + "===========" + vendor + "************");

if (table_for_nce.contains("Inventory")||table_for_nce.contains("Visibility")) {
url = "-----";
} else {
	
url = "-----";	
//url = url_address + "/Telco360/performance/ncereport/" + domain + "/" + vendor + "/" + admin_id + "/nce";
}

//url = "http://localhost:4200/#/Telco360/performance/necreport/" + domain + "/" + vendor + "/" +admin_id+"/nec" ;

cell = rc.createCell(2);
cell.setCellValue(url);
cell.setCellStyle(style);
spreadsheet0.autoSizeColumn(2);
try {
sht_excel_for_nec_transmission(workbook, database, unique_dates, start_time, end_time, columns_for_report, report_group.get(i), node, elements, interfacee, table_for_nce, report_type);
} catch (Exception e) {

log.error("Performance   Exception occurs:---"+domain+"------"+vendor+"------"+admin_id+"---" + e.getMessage(), e);
}
} else {
log.debug("*********" + admin_id + "********* checking 2 vendor and domain   **" + domain + "===========" + vendor + "************");
cell = rc.createCell(2);
url = url + "/" + table_for_nce;
cell.setCellValue(url);
cell.setCellStyle(style);
spreadsheet0.autoSizeColumn(2);
try {
sht_excel(workbook, database, unique_dates, start_time, end_time, columns_for_report, report_group.get(i), node, elements, interfacee, table_for_nce, vendor, domain);
} catch (Exception e) {

log.error("Performance   Exception occurs:---"+domain+"------"+vendor+"------"+admin_id+"---" + e.getMessage(), e);
}
}

}

String report_directiory = config.getProperty("performance_mail_report_path");

FileOutputStream out = new FileOutputStream(new File(report_directiory + save_report));
workbook.write(out);
out.close();

String txt = "Report  " + user_report_name + "  is Created on date " + currentdate + " ." + "\n\n" + "";

//
//String txt="Report  "+user_report_name+"  is Created on date "+currentdate+" ."+"\n\n"+""
//		+ "Check Link :-  "+url+"";
String file_name = report_directiory + save_report;

try {
if (mail.equals("yes")) {
mail(user_report_name, txt, file_name, admin_id);
}

} catch (Exception e) {

log.error("Performance   Exception occurs:---"+domain+"------"+vendor+"------"+admin_id+"---" + e.getMessage(), e);
}

}

public static void sht_excel(XSSFWorkbook workbook, MongoDatabase database, ArrayList < String > unique_dates, String start_time, String end_time, ArrayList < String > columns_for_report, String name, String node, String elements, String interfacee, String table_for_nce, String vendor, String domain) {

try {
String hint = "";

//adding key w.r.t vendor for report
//System.out.printlnout.println(table_for_nce);
if (table_for_nce.contains("show port statistics")) {
hint = "_stats";
} else if (table_for_nce.contains("show system cpu")) {
hint = "_usage";}
else if (table_for_nce.contains("show system memory")) {
hint = "_memory";
} else if (table_for_nce.contains("port counters detail")) {
hint = "_port_counter";
} else if (table_for_nce.contains("dot1q counters detail")) {
hint = "_dot1q_counter";
} else if (table_for_nce.contains("interfaces statistics detail")) {
hint = "_stats";

}

else if (table_for_nce.contains("vlan")) {
hint = "_vlan";

}
else if (table_for_nce.contains("processor")) {
hint = "_processor";

}
else if (table_for_nce.contains("cpu_usage")) {
hint = "_cpu_usage";

}

else if (table_for_nce.contains("memory_usage")) {
hint = "_memory_usage";

}
else if (table_for_nce.contains("session")) {
hint = "_session";

}
else if (table_for_nce.contains("performance")) {
hint = "_performance";

}


String database_type = "";
if (interfacee.equals("yes")) {
database_type = hint + "_kpis";
//aading hint for report 1
if(hint.contains("usage")||hint.contains("processor")||hint.contains("cpu")||hint.contains("memory")||hint.contains("session")||hint.contains("performance")) {
columns_for_report.add("ifName");	
}

else {
columns_for_report.add("ifIndex");
columns_for_report.add("ifName");
}


} else {
database_type = hint + "_hour_kpis";
}

XSSFSheet spreadsheet = workbook.createSheet(name);
XSSFRow row = spreadsheet.createRow((short) 0);

XSSFCell cell;

CreationHelper createHelper = workbook.getCreationHelper();
Hyperlink link1 = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
link1.setAddress("'Summary'!A1");

XSSFCellStyle hlinkstyle = workbook.createCellStyle();
XSSFFont hlinkfont = workbook.createFont();
hlinkfont.setUnderline(XSSFFont.U_SINGLE);
hlinkfont.setColor(HSSFColor.BLUE.index);
hlinkstyle.setFont(hlinkfont);
row.createCell(0).setHyperlink(link1);
cell = row.createCell(0);
cell.setCellValue("<-Summary");
cell.setHyperlink(link1);
cell.setCellStyle(hlinkstyle);

//Hyperlink....

CellStyle style = workbook.createCellStyle();
style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
style.setFillPattern(CellStyle.SOLID_FOREGROUND);
style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
XSSFFont font = workbook.createFont();
font.setFontHeightInPoints((short) 11);
font.setColor(IndexedColors.BLACK.getIndex());
font.setBold(true);
style.setFont(font);

CellStyle def = workbook.createCellStyle();
//  style2.setFillPattern(CellStyle.SOLID_FOREGROUND);
XSSFFont fontdef = workbook.createFont();
fontdef.setFontHeightInPoints((short) 10);
fontdef.setColor(IndexedColors.BLACK.getIndex());
//fontdef.setBold(true);
def.setFont(fontdef);

CellStyle style3 = workbook.createCellStyle();
style3.setVerticalAlignment(style3.ALIGN_CENTER);
style3.setAlignment(style3.ALIGN_CENTER);
style3.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
style3.setBottomBorderColor(IndexedColors.BLACK.getIndex());

style3.setRightBorderColor(IndexedColors.BLUE.getIndex());

style3.setTopBorderColor(IndexedColors.BLACK.getIndex());

style3.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
XSSFFont font3 = workbook.createFont();
font3.setFontHeightInPoints((short) 11);
font3.setColor(IndexedColors.WHITE.getIndex());
font3.setBold(true);
style3.setFont(font3);

row = spreadsheet.createRow((short) 2);
cell = row.createCell(0);
cell.setCellValue(name.toUpperCase());
cell.setCellStyle(style);

row = spreadsheet.createRow(3);
ArrayList < String > cols = new ArrayList < String > ();
int cc = 1;
for (String col: columns_for_report) {

cols.add(col);

row.getRowStyle();
cell = row.createCell(cc - 1);
//aading hint for report 2
if(hint.contains("usage")||hint.contains("processor")||hint.contains("cpu")||hint.contains("memory")||hint.contains("session")||hint.contains("performance")) {
cell.setCellValue(col.replace("ifName", "ifName/SLOT"));
}

else {
cell.setCellValue(col);	
}

cell.setCellStyle(style3);

cc++;

}

int i = 4;

for (String date: unique_dates) {

BasicDBObject index = new BasicDBObject("$hint", "devicename_1");

MongoCollection < Document > collection1 = database.getCollection(date.replace("-", "_") + database_type);

if (elements.equals("ALL")) {

ArrayList < Document > documents1 = null;
if (start_time.contains(":")) {
documents1 = collection1.find(and(gte("start_time", start_time), lte("start_time", end_time))).hint(index).into(new ArrayList < Document > ());
} else {
documents1 = collection1.find().hint(index).into(new ArrayList < Document > ());
}

for (int j = 0; j < documents1.size(); j++) {

row = spreadsheet.createRow(i);
String ss1 = "-";
Document d = documents1.get(j);
for (int k = 0; k < columns_for_report.size(); k++) {
if (d.get(columns_for_report.get(k)) != null) {
if (d.get(columns_for_report.get(k)).toString().length() > 0) {
ss1 = d.get(columns_for_report.get(k)).toString();
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
}

i++;
}

} else {

String split[] = elements.split(",");

for (String element: split) {

ArrayList < Document > documents1 = null;
if (start_time.contains(":")) {
documents1 = collection1.find(and(eq("devicename", element), gte("start_time", start_time), lte("start_time", end_time))).hint(index).into(new ArrayList < Document > ());;
} else {
documents1 = collection1.find(eq("devicename", element)).hint(index).into(new ArrayList < Document > ());
}

try {
for (int j = 0; j < documents1.size(); j++) {

row = spreadsheet.createRow(i);
String ss1 = "-";
Document d = documents1.get(j);
for (int k = 0; k < columns_for_report.size(); k++) {
if (d.get(columns_for_report.get(k)) != null) {
if (d.get(columns_for_report.get(k)).toString().length() > 0) {
ss1 = d.get(columns_for_report.get(k)).toString();
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
}

i++;
}
} catch (Exception e) {

log.error("Performance   Exception occurs:---"+domain+"------"+vendor+"--------" + e.getMessage(), e);
}

}

}

}

for (int c = 1; c < columns_for_report.size() + 1; c++) {
spreadsheet.autoSizeColumn(c - 1);
}

} catch (Exception e) {
log.error("Performance   Exception occurs:---" + domain + "------" + vendor + "--------" + e.getMessage(), e);
}

}

//TODO

public void generate_report_excel_without_natural_trend(MongoDatabase database, String admin_id, String report_name, String node, String uname, String save_report, String vendor, String domain, String url) throws Exception {

DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
Date date = new Date();
String currentdate = dateFormat.format(date);
XSSFWorkbook workbook = new XSSFWorkbook();

CreationHelper createHelper = workbook.getCreationHelper();
XSSFCell cell;
//Hyperlink....
Hyperlink hypr[] = new Hyperlink[1500];

ArrayList < String > table = new ArrayList < String > ();

table.add(report_name);
String table_for_nce_nec = "";
if (domain.equals("IPBB") || domain.equals("IPRAN")) {
//elements corresponding to reports
table_for_nce_nec = (mongo_select1_where2(database, "command", "report_group", "admin_id", admin_id, "ReportName", report_name).get(0));

}

//------------------------properties----------------------//

XSSFCellStyle hlinkstyle = workbook.createCellStyle();
XSSFFont hlinkfont = workbook.createFont();
hlinkfont.setUnderline(XSSFFont.U_SINGLE);
hlinkfont.setColor(HSSFColor.BLUE.index);
hlinkstyle.setFont(hlinkfont);
hlinkstyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
hlinkstyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
hlinkstyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
hlinkstyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);

XSSFCellStyle hlinkstyle1 = workbook.createCellStyle();
XSSFFont hlinkfont1 = workbook.createFont();
hlinkfont1.setUnderline(XSSFFont.U_SINGLE);
hlinkfont1.setColor(HSSFColor.BLACK.index);
hlinkstyle1.setFont(hlinkfont1);
hlinkstyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
hlinkstyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
hlinkstyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
hlinkstyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);

XSSFCellStyle row_style = workbook.createCellStyle();
XSSFFont row_font = workbook.createFont();

row_font.setColor(HSSFColor.BLACK.index);
row_style.setFont(row_font);
row_style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
row_style.setBorderTop(HSSFCellStyle.BORDER_THIN);
row_style.setBorderRight(HSSFCellStyle.BORDER_THIN);
row_style.setBorderLeft(HSSFCellStyle.BORDER_THIN);

//----------cover------------------//

CellStyle style3 = workbook.createCellStyle();
XSSFFont font3 = workbook.createFont();
font3.setFontHeightInPoints((short) 13);
font3.setColor(IndexedColors.INDIGO.getIndex());
font3.setBold(true);
style3.setFont(font3);
style3.setBorderBottom(HSSFCellStyle.BORDER_THIN);
style3.setBorderTop(HSSFCellStyle.BORDER_THIN);
style3.setBorderRight(HSSFCellStyle.BORDER_THIN);
style3.setBorderLeft(HSSFCellStyle.BORDER_THIN);

CellStyle style = workbook.createCellStyle();
XSSFFont font = workbook.createFont();
font.setFontHeightInPoints((short) 13);
font.setColor(IndexedColors.BLACK.getIndex());
style.setFont(font);
style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
style.setBorderTop(HSSFCellStyle.BORDER_THIN);
style.setBorderRight(HSSFCellStyle.BORDER_THIN);
style.setBorderLeft(HSSFCellStyle.BORDER_THIN);

XSSFSheet spreadsheet0 = workbook.createSheet("Cover");
XSSFRow rc = spreadsheet0.createRow((short) 2); //count change
spreadsheet0.autoSizeColumn(0);
CellStyle stylec = workbook.createCellStyle();
XSSFFont fontc = workbook.createFont();
fontc.setFontHeightInPoints((short) 30);
fontc.setColor(IndexedColors.INDIGO.getIndex());
fontc.setBold(true);
stylec.setFont(fontc);
cell = rc.createCell(1);
cell.setCellValue("DETAILS");
cell.setCellStyle(stylec);
spreadsheet0.autoSizeColumn(1);

rc = spreadsheet0.createRow((short) 3); //count change
cell = rc.createCell(1);
cell.setCellValue("Domain");
cell.setCellStyle(style3);
spreadsheet0.autoSizeColumn(1);

cell = rc.createCell(2);
cell.setCellValue(domain);
cell.setCellStyle(style);
spreadsheet0.autoSizeColumn(2);

rc = spreadsheet0.createRow((short) 4); //count change
cell = rc.createCell(1);
cell.setCellValue("Vendor");
cell.setCellStyle(style3);
spreadsheet0.autoSizeColumn(1);

cell = rc.createCell(2);
cell.setCellValue(vendor);
cell.setCellStyle(style);
spreadsheet0.autoSizeColumn(2);
/*
rc = spreadsheet0.createRow((short) 5); //count change
cell = rc.createCell(1);
cell.setCellValue("Generated By");
cell.setCellStyle(style3);
spreadsheet0.autoSizeColumn(1);

cell = rc.createCell(2);
cell.setCellValue("Harsh");
cell.setCellStyle(style);
spreadsheet0.autoSizeColumn(2);
*/
rc = spreadsheet0.createRow((short) 5); //count change
cell = rc.createCell(1);
cell.setCellValue("Date And Time");
cell.setCellStyle(style3);
spreadsheet0.autoSizeColumn(1);

cell = rc.createCell(2);
cell.setCellValue(currentdate);
cell.setCellStyle(style);
spreadsheet0.autoSizeColumn(2);

rc = spreadsheet0.createRow((short) 6); //count change
cell = rc.createCell(1);
cell.setCellValue("URL");
cell.setCellStyle(style3);
spreadsheet0.autoSizeColumn(1);

cell = rc.createCell(2);
cell.setCellValue(url + "/" + table_for_nce_nec);
cell.setCellStyle(style);
spreadsheet0.autoSizeColumn(2);

/*
rc=spreadsheet0.createRow((short) 8);//count change
cell=rc.createCell(1);
cell.setCellValue("Report Name");
cell.setCellStyle(style3);
spreadsheet0.autoSizeColumn(1);

cell=rc.createCell(2);
cell.setCellValue(report_name);
cell.setCellStyle(style);
spreadsheet0.autoSizeColumn(2);
*/
XSSFSheet spreadsheet1 = workbook.createSheet("Summary");

XSSFRow r1 = spreadsheet1.createRow((short) 2); //count change

////////////////////////////////////////////////
CellStyle style2 = workbook.createCellStyle();
XSSFFont font2 = workbook.createFont();
font2.setFontHeightInPoints((short) 30);
font2.setColor(IndexedColors.DARK_BLUE.getIndex());
font2.setBold(true);
style2.setFont(font2);
cell = r1.createCell(0);
cell.setCellValue("CONTENT DETAIL");
cell.setCellStyle(style2);
spreadsheet1.autoSizeColumn(0);

//////////////////////////////////////////

XSSFRow r2 = spreadsheet1.createRow((short) 4); //count change

font3.setFontHeightInPoints((short) 13);
font3.setColor(IndexedColors.WHITE.getIndex());

font3.setBold(true);
style3.setVerticalAlignment(style3.ALIGN_CENTER);
style3.setAlignment(style3.ALIGN_CENTER);
style3.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
style3.setBottomBorderColor(IndexedColors.BLACK.getIndex());

style3.setRightBorderColor(IndexedColors.BLUE.getIndex());

style3.setTopBorderColor(IndexedColors.BLACK.getIndex());

style3.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
cell = r2.createCell(0);
cell.setCellValue("Report Name");
cell.setCellStyle(style3);
spreadsheet1.autoSizeColumn(0);

for (int i = 0; i < table.size(); i++) {
int k = i + 5;

hypr[i] = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
hypr[i].setAddress(table.get(i).replace("_ref", "") + "!A1");

XSSFRow row1 = spreadsheet1.createRow((short) k); //count change
cell = row1.createCell(0);
//cell.setCellValue(columns.get(i));
cell.setCellValue(table.get(i).replace("_ref", ""));
cell.setCellStyle(hlinkstyle1);
cell.setHyperlink(hypr[i]);
cell.setCellStyle(hlinkstyle);
spreadsheet1.autoSizeColumn(0);
//row1.createCell(0).setCellValue(table.get(i));//sheet name

spreadsheet1.autoSizeColumn(1);

sht_excel_without_natural_trend(workbook, database, report_name);

}

Properties config = getProperties();
//database = connection.getDatabase(config.getProperty("database.performance_zambia_ipran"));
String report_directiory = config.getProperty("performance_mail_report_path");
FileOutputStream out = new FileOutputStream(new File(report_directiory + save_report));
workbook.write(out);
out.close();

//initially delete time table 
MongoCollection < Document > col3 = database.getCollection(save_report);
col3.drop();

}

public static void sht_excel_without_natural_trend(XSSFWorkbook workbook, MongoDatabase database, String report_name) {
try {

String database_type = "";

XSSFSheet spreadsheet = workbook.createSheet(report_name);
XSSFRow row = spreadsheet.createRow((short) 0);

XSSFCell cell;

CreationHelper createHelper = workbook.getCreationHelper();
Hyperlink link1 = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
link1.setAddress("'Summary'!A1");

XSSFCellStyle hlinkstyle = workbook.createCellStyle();
XSSFFont hlinkfont = workbook.createFont();
hlinkfont.setUnderline(XSSFFont.U_SINGLE);
hlinkfont.setColor(HSSFColor.BLUE.index);
hlinkstyle.setFont(hlinkfont);
row.createCell(0).setHyperlink(link1);
cell = row.createCell(0);
cell.setCellValue("<-Summary");
cell.setHyperlink(link1);
cell.setCellStyle(hlinkstyle);

//Hyperlink....

CellStyle style = workbook.createCellStyle();
style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
style.setFillPattern(CellStyle.SOLID_FOREGROUND);
style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
XSSFFont font = workbook.createFont();
font.setFontHeightInPoints((short) 11);
font.setColor(IndexedColors.BLACK.getIndex());
font.setBold(true);
style.setFont(font);

CellStyle def = workbook.createCellStyle();
//  style2.setFillPattern(CellStyle.SOLID_FOREGROUND);
XSSFFont fontdef = workbook.createFont();
fontdef.setFontHeightInPoints((short) 10);
fontdef.setColor(IndexedColors.BLACK.getIndex());
//fontdef.setBold(true);
def.setFont(fontdef);

CellStyle style3 = workbook.createCellStyle();
style3.setVerticalAlignment(style3.ALIGN_CENTER);
style3.setAlignment(style3.ALIGN_CENTER);
style3.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
style3.setBottomBorderColor(IndexedColors.BLACK.getIndex());

style3.setRightBorderColor(IndexedColors.BLUE.getIndex());

style3.setTopBorderColor(IndexedColors.BLACK.getIndex());

style3.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
XSSFFont font3 = workbook.createFont();
font3.setFontHeightInPoints((short) 11);
font3.setColor(IndexedColors.WHITE.getIndex());
font3.setBold(true);
style3.setFont(font3);

row = spreadsheet.createRow((short) 2);
cell = row.createCell(0);
cell.setCellValue(report_name.toUpperCase());
cell.setCellStyle(style);

MongoCollection < Document > collection = database.getCollection(report_name);

FindIterable < Document > documents = collection.find().limit(1);
int column_count = 0;
for (Document d: documents) {
for (Entry < String, Object > entry: d.entrySet()) {
if (!entry.getKey().toString().contains("_id")) {

column_count++;

}
}

}
row = spreadsheet.createRow(3);
int vcount = column_count;
for (Document d: documents) {
int c = 1;
for (Entry < String, Object > entry: d.entrySet()) {

if (!entry.getKey().toString().contains("_id")) {
String str = entry.getKey().trim();;
row.getRowStyle();
cell = row.createCell(c - 1);
cell.setCellValue(str.toUpperCase());
cell.setCellStyle(style3);

c++;
}
}

}

int i = 4;

MongoCollection < Document > collection1 = database.getCollection(report_name);
FindIterable < Document > documents1 = null;

documents1 = collection1.find();

for (Document d: documents1) {
int v = 1;
row = spreadsheet.createRow(i);
for (Entry < String, Object > entry: d.entrySet()) {
if (!entry.getKey().toString().contains("_id")) {
String ss1 = entry.getValue().toString().trim();
cell = row.createCell(v - 1);
cell.setCellValue(ss1);
cell.setCellStyle(def);

v++;

}
}
if (v <= vcount) {
break;
}
i++;
}

for (int c = 1; c < vcount + 1; c++) {
spreadsheet.autoSizeColumn(c - 1);
}

} catch (Exception e) {
log.error("Performance   Exception occurs:---------------------" + e.getMessage(), e);
}

}

//TODO excel for nce transmission

public static void sht_excel_for_nce_transmission(XSSFWorkbook workbook, MongoDatabase database, ArrayList < String > unique_dates, String start_time, String end_time, ArrayList < String > columns_for_report, String name, String node, String elements, String interfacee, String table_for_nce) {
try {

XSSFSheet spreadsheet = workbook.createSheet(name);
XSSFRow row = spreadsheet.createRow((short) 0);

XSSFCell cell;

CreationHelper createHelper = workbook.getCreationHelper();
Hyperlink link1 = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
link1.setAddress("'Summary'!A1");

XSSFCellStyle hlinkstyle = workbook.createCellStyle();
XSSFFont hlinkfont = workbook.createFont();
hlinkfont.setUnderline(XSSFFont.U_SINGLE);
hlinkfont.setColor(HSSFColor.BLUE.index);
hlinkstyle.setFont(hlinkfont);
row.createCell(0).setHyperlink(link1);
cell = row.createCell(0);
cell.setCellValue("<-Summary");
cell.setHyperlink(link1);
cell.setCellStyle(hlinkstyle);

//Hyperlink....

CellStyle style = workbook.createCellStyle();
style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
style.setFillPattern(CellStyle.SOLID_FOREGROUND);
style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
XSSFFont font = workbook.createFont();
font.setFontHeightInPoints((short) 11);
font.setColor(IndexedColors.BLACK.getIndex());
font.setBold(true);
style.setFont(font);

CellStyle def = workbook.createCellStyle();
//  style2.setFillPattern(CellStyle.SOLID_FOREGROUND);
XSSFFont fontdef = workbook.createFont();
fontdef.setFontHeightInPoints((short) 10);
fontdef.setColor(IndexedColors.BLACK.getIndex());
//fontdef.setBold(true);
def.setFont(fontdef);

CellStyle style3 = workbook.createCellStyle();
style3.setVerticalAlignment(style3.ALIGN_CENTER);
style3.setAlignment(style3.ALIGN_CENTER);
style3.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
style3.setBottomBorderColor(IndexedColors.BLACK.getIndex());

style3.setRightBorderColor(IndexedColors.BLUE.getIndex());

style3.setTopBorderColor(IndexedColors.BLACK.getIndex());

style3.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
XSSFFont font3 = workbook.createFont();
font3.setFontHeightInPoints((short) 11);
font3.setColor(IndexedColors.WHITE.getIndex());
font3.setBold(true);
style3.setFont(font3);

row = spreadsheet.createRow((short) 2);
cell = row.createCell(0);
cell.setCellValue(name.toUpperCase());
cell.setCellStyle(style);
String table_for_nce_database = table_for_nce.toLowerCase() + "_" + unique_dates.get(0).replace("-", "");

row = spreadsheet.createRow(3);
ArrayList < String > cols = new ArrayList < String > ();
int cc = 1;
for (String col: columns_for_report) {

cols.add(col);

row.getRowStyle();
cell = row.createCell(cc - 1);
cell.setCellValue(col);
cell.setCellStyle(style3);

cc++;

}

int i = 4;

for (String date: unique_dates) {

table_for_nce_database = table_for_nce.toLowerCase() + "_" + date.replace("-", "");

MongoCollection < Document > collection1 = database.getCollection(table_for_nce_database);

if (elements.equals("ALL")) {
BasicDBObject index = null;

if (table_for_nce.equals("sdh") || table_for_nce.equals("wdm")) {
index = new BasicDBObject("$hint", "NEName_1");

} else {
index = new BasicDBObject("$hint", "NE_Name_1");
}

ArrayList < Document > documents1 = null;
if (start_time.contains(":")) {
documents1 = collection1.find(and(gte("start_time", start_time), lte("start_time", end_time))).hint(index).into(new ArrayList < Document > ());;
} else {
documents1 = collection1.find().hint(index).into(new ArrayList < Document > ());;
}

for (int j = 0; j < documents1.size(); j++) {

row = spreadsheet.createRow(i);
String ss1 = "-";
Document d = documents1.get(j);
for (int k = 0; k < cols.size(); k++) {
if (d.get(cols.get(k)) != null) {
if (d.get(cols.get(k)).toString().length() > 0) {
ss1 = d.get(cols.get(k)).toString();
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
}

i++;
}

} else {

String split[] = elements.split(",");
for (String element: split) {

String column_name = "";
BasicDBObject index = null;
if (table_for_nce.equals("sdh") || table_for_nce.equals("wdm")) {
column_name = "NEName";
index = new BasicDBObject("$hint", "NEName_1");
} else {
column_name = "NE_Name";
index = new BasicDBObject("$hint", "NEName_1");
}

ArrayList < Document > documents1 = null;
if (start_time.contains(":")) {
documents1 = collection1.find(and(eq(column_name, element), gte("start_time", start_time), lte("start_time", end_time))).hint(index).into(new ArrayList < Document > ());;
} else {
documents1 = collection1.find(eq(column_name, element)).hint(index).into(new ArrayList < Document > ());;
}

try {
for (int j = 0; j < documents1.size(); j++) {

row = spreadsheet.createRow(i);
String ss1 = "-";
Document d = documents1.get(j);
for (int k = 0; k < cols.size(); k++) {
if (d.get(cols.get(k)) != null) {
if (d.get(cols.get(k)).toString().length() > 0) {
ss1 = d.get(cols.get(k)).toString();
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
}

i++;
}
} catch (Exception e) {

log.error("Exception occurs:----" + e.getMessage(), e);
}

}

}

}

for (int c = 1; c < cols.size() + 1; c++) {
spreadsheet.autoSizeColumn(c - 1);
}

} catch (Exception e) {
log.error("Performance   Exception occurs:----------" + e.getMessage(), e);
}

}

//TODO excel for nec transmission

public static void sht_excel_for_nec_transmission(XSSFWorkbook workbook, MongoDatabase database, ArrayList < String > unique_dates, String start_time, String end_time, ArrayList < String > columns_for_report, String name, String node, String elements, String interfacee, String table_for_nce, String report_type) {
try {

String report_duration = "";

if (report_type.equals("DAY")) {

report_duration = "_1day_";

} else {
report_duration = "_15min_";
}

String table_for_nce_database = "";

BasicDBObject index = null;

if (table_for_nce.toLowerCase().equals("metering")) {
table_for_nce_database = "mtr_" + unique_dates.get(0).replace("-", "");
index = new BasicDBObject("$hint", "NE NAME_1");
} else if (table_for_nce.toLowerCase().equals("inventory")||table_for_nce.toLowerCase().equals("visibility")) {

table_for_nce_database = table_for_nce.toLowerCase() + "_" + unique_dates.get(0).replace("-", "");
index = new BasicDBObject("$hint", "NE_NAME_1");
} else {
table_for_nce_database = table_for_nce.toLowerCase() + report_duration + unique_dates.get(0).replace("-", "");
index = new BasicDBObject("$hint", "NE NAME_1");
}

XSSFSheet spreadsheet = workbook.createSheet(name);
XSSFRow row = spreadsheet.createRow((short) 0);

XSSFCell cell;

CreationHelper createHelper = workbook.getCreationHelper();
Hyperlink link1 = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
link1.setAddress("'Summary'!A1");

XSSFCellStyle hlinkstyle = workbook.createCellStyle();
XSSFFont hlinkfont = workbook.createFont();
hlinkfont.setUnderline(XSSFFont.U_SINGLE);
hlinkfont.setColor(HSSFColor.BLUE.index);
hlinkstyle.setFont(hlinkfont);
row.createCell(0).setHyperlink(link1);
cell = row.createCell(0);
cell.setCellValue("<-Summary");
cell.setHyperlink(link1);
cell.setCellStyle(hlinkstyle);

//Hyperlink....

CellStyle style = workbook.createCellStyle();
style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
style.setFillPattern(CellStyle.SOLID_FOREGROUND);
style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
XSSFFont font = workbook.createFont();
font.setFontHeightInPoints((short) 11);
font.setColor(IndexedColors.BLACK.getIndex());
font.setBold(true);
style.setFont(font);

CellStyle def = workbook.createCellStyle();
//  style2.setFillPattern(CellStyle.SOLID_FOREGROUND);
XSSFFont fontdef = workbook.createFont();
fontdef.setFontHeightInPoints((short) 10);
fontdef.setColor(IndexedColors.BLACK.getIndex());
//fontdef.setBold(true);
def.setFont(fontdef);

CellStyle style3 = workbook.createCellStyle();
style3.setVerticalAlignment(style3.ALIGN_CENTER);
style3.setAlignment(style3.ALIGN_CENTER);
style3.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
style3.setBottomBorderColor(IndexedColors.BLACK.getIndex());

style3.setRightBorderColor(IndexedColors.BLUE.getIndex());

style3.setTopBorderColor(IndexedColors.BLACK.getIndex());

style3.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
XSSFFont font3 = workbook.createFont();
font3.setFontHeightInPoints((short) 11);
font3.setColor(IndexedColors.WHITE.getIndex());
font3.setBold(true);
style3.setFont(font3);

row = spreadsheet.createRow((short) 2);
cell = row.createCell(0);
cell.setCellValue(name.toUpperCase());
cell.setCellStyle(style);

ArrayList < String > cols = new ArrayList < String > ();

try {

row = spreadsheet.createRow(3);

int cc = 1;
for (String col: columns_for_report) {

cols.add(col);

row.getRowStyle();
cell = row.createCell(cc - 1);
cell.setCellValue(col);
cell.setCellStyle(style3);

cc++;

}

} catch (Exception e) {

log.error("Performance   Exception occurs:-------------" + e.getMessage(), e);
}

int i = 4;

for (String date: unique_dates) {

if (table_for_nce.toLowerCase().equals("metering")) {
table_for_nce_database = "mtr_" + date.replace("-", "");
} else if (table_for_nce.toLowerCase().equals("inventory")||table_for_nce.toLowerCase().equals("visibility")) {
table_for_nce_database = table_for_nce.toLowerCase() + "_" + date.replace("-", "");
} else {
table_for_nce_database = table_for_nce.toLowerCase() + report_duration + date.replace("-", "");
}

MongoCollection < Document > collection1 = database.getCollection(table_for_nce_database);
if (elements.equals("ALL")) {
ArrayList < Document > documents1 = null;
if (start_time.contains(":")) {
documents1 = collection1.find(and(gte("start_time", start_time), lte("start_time", end_time))).hint(index).into(new ArrayList < Document > ());;
} else {
documents1 = collection1.find().hint(index).into(new ArrayList < Document > ());;
}

for (int j = 0; j < documents1.size(); j++) {

row = spreadsheet.createRow(i);
String ss1 = "-";
Document d = documents1.get(j);
for (int k = 0; k < cols.size(); k++) {
if (d.get(cols.get(k)) != null) {
if (d.get(cols.get(k)).toString().length() > 0) {
ss1 = d.get(cols.get(k)).toString();
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
}

i++;
}

} else {

String split[] = elements.split(",");
for (String element: split) {
String column_name = "NE Name";
if (table_for_nce.toLowerCase().equals("inventory")||table_for_nce.toLowerCase().equals("visibility")) {
column_name = "NE_NAME";
}

ArrayList < Document > documents1 = null;
if (start_time.contains(":")) {
documents1 = collection1.find(and(eq(column_name, element), gte("start_time", start_time), lte("start_time", end_time))).hint(index).into(new ArrayList < Document > ());;
} else {
documents1 = collection1.find(eq(column_name, element)).hint(index).into(new ArrayList < Document > ());;
}

try {

for (int j = 0; j < documents1.size(); j++) {

row = spreadsheet.createRow(i);
String ss1 = "-";
Document d = documents1.get(j);
for (int k = 0; k < cols.size(); k++) {
if (d.get(cols.get(k)) != null) {
if (d.get(cols.get(k)).toString().length() > 0) {
ss1 = d.get(cols.get(k)).toString();
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
}

i++;
}
} catch (Exception e) {

log.error("Exception occurs:----" + e.getMessage(), e);
}

}

}

}

for (int c = 1; c < cols.size(); c++) {
spreadsheet.autoSizeColumn(c - 1);
}

} catch (Exception e) {
log.error("Performance   Exception occurs:----------------" + e.getMessage(), e);
}

}

//TODO SAM
public static void sht_excel_for_sam_transmission(XSSFWorkbook workbook, MongoDatabase database, ArrayList < String > unique_dates, String start_time, String end_time, ArrayList < String > columns_for_report, String name, String node, String elements, String interfacee, String table_for_nce) {
try {

XSSFSheet spreadsheet = workbook.createSheet(name);
XSSFRow row = spreadsheet.createRow((short) 0);

XSSFCell cell;

CreationHelper createHelper = workbook.getCreationHelper();
Hyperlink link1 = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
link1.setAddress("'Summary'!A1");

XSSFCellStyle hlinkstyle = workbook.createCellStyle();
XSSFFont hlinkfont = workbook.createFont();
hlinkfont.setUnderline(XSSFFont.U_SINGLE);
hlinkfont.setColor(HSSFColor.BLUE.index);
hlinkstyle.setFont(hlinkfont);
row.createCell(0).setHyperlink(link1);
cell = row.createCell(0);
cell.setCellValue("<-Summary");
cell.setHyperlink(link1);
cell.setCellStyle(hlinkstyle);

//Hyperlink....

CellStyle style = workbook.createCellStyle();
style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
style.setFillPattern(CellStyle.SOLID_FOREGROUND);
style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
XSSFFont font = workbook.createFont();
font.setFontHeightInPoints((short) 11);
font.setColor(IndexedColors.BLACK.getIndex());
font.setBold(true);
style.setFont(font);

CellStyle def = workbook.createCellStyle();
//  style2.setFillPattern(CellStyle.SOLID_FOREGROUND);
XSSFFont fontdef = workbook.createFont();
fontdef.setFontHeightInPoints((short) 10);
fontdef.setColor(IndexedColors.BLACK.getIndex());
//fontdef.setBold(true);
def.setFont(fontdef);

CellStyle style3 = workbook.createCellStyle();
style3.setVerticalAlignment(style3.ALIGN_CENTER);
style3.setAlignment(style3.ALIGN_CENTER);
style3.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
style3.setBottomBorderColor(IndexedColors.BLACK.getIndex());

style3.setRightBorderColor(IndexedColors.BLUE.getIndex());

style3.setTopBorderColor(IndexedColors.BLACK.getIndex());

style3.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
XSSFFont font3 = workbook.createFont();
font3.setFontHeightInPoints((short) 11);
font3.setColor(IndexedColors.WHITE.getIndex());
font3.setBold(true);
style3.setFont(font3);

row = spreadsheet.createRow((short) 2);
cell = row.createCell(0);
cell.setCellValue(name.toUpperCase());
cell.setCellStyle(style);

//MongoCollection < Document > collection = database.getCollection(table_for_nce_database);
row = spreadsheet.createRow(3);
ArrayList < String > cols = new ArrayList < String > ();
int cc = 1;
for (String col: columns_for_report) {

cols.add(col);

row.getRowStyle();
cell = row.createCell(cc - 1);
cell.setCellValue(col);
cell.setCellStyle(style3);

cc++;

}

/*
* 
* 
* 
* ArrayList < Document > documents = collection.find().limit(1).into(new ArrayList < Document > ());
try {

for (Document d: documents) {
int c = 1;
for (Entry < String, Object > entry: d.entrySet()) {

if (columns_for_report.contains(entry.getKey().toString())) {
String str = entry.getKey().trim();
cols.add(str);
row.getRowStyle();
cell = row.createCell(c - 1);
cell.setCellValue(str);
cell.setCellStyle(style3);

c++;
}
}

}
} catch (Exception e1) {

e1.printStackTrace();
}
*/

int i = 4;

for (String date: unique_dates) {
MongoCollection < Document > collection1 = database.getCollection(table_for_nce.toLowerCase() + "_" + date.replace("-", ""));

if (elements.equals("ALL")) {
BasicDBObject index = null;
if (table_for_nce.toLowerCase().contains("stats")) {

index = new BasicDBObject("$hint", "monitoredObjectSiteName_1");
} else {

index = new BasicDBObject("$hint", "siteName_1");
}
ArrayList < Document > documents1 = null;
if (start_time.contains(":")) {
	
	
if(table_for_nce.toLowerCase().contains("zm_mpr_visibility")) {
documents1 = collection1.find(and(eq("administrativeState","inService"),ne("locale","undefined"),gte("start_time", start_time), lte("start_time", end_time))).hint(index).into(new ArrayList < Document > ());
}
else {
documents1 = collection1.find(and(gte("start_time", start_time), lte("start_time", end_time))).hint(index).into(new ArrayList < Document > ());	
}

} else {
	
if(table_for_nce.toLowerCase().contains("zm_mpr_visibility")) {
documents1 = collection1.find(and(eq("administrativeState","inService"),ne("locale","undefined"))).hint(index).into(new ArrayList < Document > ());
}	
else {
documents1 = collection1.find().hint(index).into(new ArrayList < Document > ());
}
}

for (int j = 0; j < documents1.size(); j++) {

row = spreadsheet.createRow(i);
String ss1 = "-";
Document d = documents1.get(j);
for (int k = 0; k < cols.size(); k++) {
if (d.get(cols.get(k)) != null) {
if (d.get(cols.get(k)).toString().length() > 0) {
ss1 = d.get(cols.get(k)).toString();
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
}

i++;
}

} else {

String split[] = elements.split(",");
BasicDBObject index = null;
for (String element: split) {
String column_name = "siteName";
if (table_for_nce.toLowerCase().contains("stats")) {
column_name = "monitoredObjectSiteName";
index = new BasicDBObject("$hint", "monitoredObjectSiteName_1");
} else {
column_name = "siteName";
index = new BasicDBObject("$hint", "siteName_1");
}

ArrayList < Document > documents1 = null;
if (start_time.contains(":")) {
if(table_for_nce.toLowerCase().contains("zm_mpr_visibility")) {

documents1 = collection1.find(and(eq("administrativeState","inService"),eq(column_name, element),ne("locale","undefined"), gte("start_time", start_time), lte("start_time", end_time))).hint(index).into(new ArrayList < Document > ());
}	
else {
documents1 = collection1.find(and(eq(column_name, element), gte("start_time", start_time), lte("start_time", end_time))).hint(index).into(new ArrayList < Document > ());
}

} else {
if(table_for_nce.toLowerCase().contains("zm_mpr_visibility")) {

documents1 = collection1.find(and(eq("administrativeState","inService"),ne("locale","undefined"),eq(column_name, element))).hint(index).into(new ArrayList < Document > ());
		}	
	else {
	
documents1 = collection1.find(eq(column_name, element)).hint(index).into(new ArrayList < Document > ());
	}
}

try {
for (int j = 0; j < documents1.size(); j++) {

row = spreadsheet.createRow(i);
String ss1 = "-";
Document d = documents1.get(j);
for (int k = 0; k < cols.size(); k++) {
if (d.get(cols.get(k)) != null) {
if (d.get(cols.get(k)).toString().length() > 0) {
ss1 = d.get(cols.get(k)).toString();
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
}

i++;
}
} catch (Exception e) {

log.error("Exception occurs:----" + e.getMessage(), e);
}

}

}

}

for (int c = 1; c < cols.size(); c++) {
spreadsheet.autoSizeColumn(c - 1);
}

} catch (Exception e) {
log.error("Performance   Exception occurs:---------------" + e.getMessage(), e);
}

}

//TODO ERICSSON
public static void sht_excel_for_ericsson_transmission(XSSFWorkbook workbook, MongoDatabase database, ArrayList < String > unique_dates, String start_time, String end_time, ArrayList < String > columns_for_report, String name, String node, String elements, String interfacee, String table_for_nce) {
try {

String hint = "";

String table_for_nce_database = "";

if (table_for_nce.toLowerCase().contains("report")) {
table_for_nce_database = table_for_nce.toLowerCase() + "_" + unique_dates.get(0).replace("-", "");
} else {
table_for_nce_database = unique_dates.get(0).replace("-", "_") + "_" + table_for_nce;
}

XSSFSheet spreadsheet = workbook.createSheet(name);
XSSFRow row = spreadsheet.createRow((short) 0);

XSSFCell cell;

CreationHelper createHelper = workbook.getCreationHelper();
Hyperlink link1 = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
link1.setAddress("'Summary'!A1");

XSSFCellStyle hlinkstyle = workbook.createCellStyle();
XSSFFont hlinkfont = workbook.createFont();
hlinkfont.setUnderline(XSSFFont.U_SINGLE);
hlinkfont.setColor(HSSFColor.BLUE.index);
hlinkstyle.setFont(hlinkfont);
row.createCell(0).setHyperlink(link1);
cell = row.createCell(0);
cell.setCellValue("<-Summary");
cell.setHyperlink(link1);
cell.setCellStyle(hlinkstyle);

//Hyperlink....

CellStyle style = workbook.createCellStyle();
style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
style.setFillPattern(CellStyle.SOLID_FOREGROUND);
style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
XSSFFont font = workbook.createFont();
font.setFontHeightInPoints((short) 11);
font.setColor(IndexedColors.BLACK.getIndex());
font.setBold(true);
style.setFont(font);

CellStyle def = workbook.createCellStyle();
//style2.setFillPattern(CellStyle.SOLID_FOREGROUND);
XSSFFont fontdef = workbook.createFont();
fontdef.setFontHeightInPoints((short) 10);
fontdef.setColor(IndexedColors.BLACK.getIndex());
//fontdef.setBold(true);
def.setFont(fontdef);

CellStyle style3 = workbook.createCellStyle();
style3.setVerticalAlignment(style3.ALIGN_CENTER);
style3.setAlignment(style3.ALIGN_CENTER);
style3.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
style3.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
style3.setBottomBorderColor(IndexedColors.BLACK.getIndex());

style3.setRightBorderColor(IndexedColors.BLUE.getIndex());

style3.setTopBorderColor(IndexedColors.BLACK.getIndex());

style3.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
XSSFFont font3 = workbook.createFont();
font3.setFontHeightInPoints((short) 11);
font3.setColor(IndexedColors.WHITE.getIndex());
font3.setBold(true);
style3.setFont(font3);

row = spreadsheet.createRow((short) 2);
cell = row.createCell(0);
cell.setCellValue(name.toUpperCase());
cell.setCellStyle(style);

row = spreadsheet.createRow(3);
ArrayList < String > cols = new ArrayList < String > ();

try {

int cc = 1;
for (String col: columns_for_report) {

cols.add(col);

row.getRowStyle();
cell = row.createCell(cc - 1);
cell.setCellValue(col);
cell.setCellStyle(style3);

cc++;

}

} catch (Exception e) {

log.error("Performance   Exception occurs:---------------" + e.getMessage(), e);
}

int i = 4;

for (String date: unique_dates) {

String column_name = "NodeId";

if (table_for_nce.toLowerCase().contains("report")) {
table_for_nce_database = table_for_nce.toLowerCase() + "_" + date.replace("-", "");
column_name = "NodeId";
} else {

table_for_nce_database = unique_dates.get(0).replace("-", "_") + "_" + table_for_nce;

column_name = "SITE_ID";
}

MongoCollection < Document > collection1 = database.getCollection(table_for_nce_database);
if (elements.equals("ALL")) {

ArrayList < Document > documents1 = null;

if (table_for_nce.toLowerCase().contains("report")) {
if (start_time.contains(":")) {

documents1 = collection1.find(and(gte("START_DATE", start_time), lte("START_DATE", end_time))).into(new ArrayList < Document > ());
} else {
documents1 = collection1.find().into(new ArrayList < Document > ());
}
} else {
BasicDBObject index = new BasicDBObject("$hint", "ELEMENT_NAME_1");

if (start_time.contains(":")) {

documents1 = collection1.find(and(gte("START_DATE", start_time), lte("START_DATE", end_time))).hint(index).into(new ArrayList < Document > ());
} else {
documents1 = collection1.find().hint(index).into(new ArrayList < Document > ());
}

}

for (int j = 0; j < documents1.size(); j++) {

row = spreadsheet.createRow(i);
String ss1 = "-";
Document d = documents1.get(j);
for (int k = 0; k < cols.size(); k++) {
if (d.get(cols.get(k)) != null) {
if (d.get(cols.get(k)).toString().length() > 0) {
ss1 = d.get(cols.get(k)).toString();
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
}

i++;
}

//==============

} else {

String split[] = elements.split(",");
for (String element: split) {

ArrayList < Document > documents1 = null;

if (table_for_nce.toLowerCase().contains("report")) {
if (start_time.contains(":")) {

documents1 = collection1.find(and(eq(column_name, element), gte("START_DATE", start_time), lte("START_DATE", end_time))).into(new ArrayList < Document > ());
} else {
documents1 = collection1.find(eq(column_name, element)).into(new ArrayList < Document > ());
}
} else {
BasicDBObject index = new BasicDBObject("$hint", "ELEMENT_NAME_1");

if (start_time.contains(":")) {

documents1 = collection1.find(and(eq(column_name, element), gte("START_DATE", start_time), lte("START_DATE", end_time))).hint(index).into(new ArrayList < Document > ());
} else {
documents1 = collection1.find(eq(column_name, element)).hint(index).into(new ArrayList < Document > ());
}

}

try {
for (int j = 0; j < documents1.size(); j++) {

row = spreadsheet.createRow(i);
String ss1 = "-";
Document d = documents1.get(j);
for (int k = 0; k < cols.size(); k++) {
if (d.get(cols.get(k)) != null) {
if (d.get(cols.get(k)).toString().length() > 0) {
ss1 = d.get(cols.get(k)).toString();
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
cell.setCellStyle(def);
}
}

i++;
}
} catch (Exception e) {

log.error("Exception occurs:----" + e.getMessage(), e);
}

}

}

}

for (int c = 1; c < cols.size() + 1; c++) {
spreadsheet.autoSizeColumn(c - 1);
}

} catch (Exception e) {
log.error("Performance   Exception occurs:---------------" + e.getMessage(), e);
}

}
}