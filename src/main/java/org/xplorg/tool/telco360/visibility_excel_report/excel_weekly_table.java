package org.xplorg.tool.telco360.visibility_excel_report;

import java.util.ArrayList;

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

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class excel_weekly_table extends GenericPerformance {
Logger log = LogManager.getLogger(excel_weekly_table.class.getName());		
public void weekly_table(MongoDatabase database,XSSFWorkbook workbook,String week_count) {

	



try {


XSSFSheet spreadsheet = workbook.createSheet("Zambia");
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
def.setVerticalAlignment(def.ALIGN_CENTER);
def.setAlignment(def.ALIGN_CENTER);
def.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
def.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
def.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
def.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
def.setBottomBorderColor(IndexedColors.BLACK.getIndex());
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

style3.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
XSSFFont font3 = workbook.createFont();
font3.setFontHeightInPoints((short) 11);
font3.setColor(IndexedColors.BLACK.getIndex());
font3.setBold(true);
style3.setFont(font3);

row = spreadsheet.createRow((short) 2);
cell = row.createCell(0);
cell.setCellValue("Consolidate Visibility Report");
cell.setCellStyle(style);

row = spreadsheet.createRow(3);

ArrayList<String> column_names=new ArrayList<String>();

column_names.add("Vendor Name");
column_names.add("Total Hops /NE Available as on GIS");
column_names.add("Total Hops visible on NMS");
column_names.add("Need To Recover on NMS");
column_names.add("Visibility%");
ArrayList < String > cols = new ArrayList < String > ();

int cc = 1;
for (String col: column_names) {

cols.add(col);

row.getRowStyle();
cell = row.createCell(cc - 1);
cell.setCellValue(col);
cell.setCellStyle(style3);

cc++;

}
int i = 4;



String previous_day=previous_day_yyyyMMdd();

MongoCollection < Document > collection1 = database.getCollection("visibility_"+week_count);



ArrayList < Document > documents1 = null;

documents1 = collection1.find().into(new ArrayList < Document > ());


for (int j = 0; j < documents1.size(); j++) {

row = spreadsheet.createRow(i);
String ss1 = "-";
Document d = documents1.get(j);
for (int k = 0; k < column_names.size(); k++) {
if (d.get(column_names.get(k)) != null) {
if (d.get(column_names.get(k)).toString().length() > 0) {
ss1 = d.get(column_names.get(k)).toString();
cell = row.createCell(k);
cell.setCellValue(ss1);
if(j==documents1.size()-1) {
cell.setCellStyle(style3);	
}
else {
cell.setCellStyle(def);	
}

} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
if(j==documents1.size()-1) {
cell.setCellStyle(style3);	
}
else {
cell.setCellStyle(def);	
}
}
} else {
ss1 = "";
cell = row.createCell(k);
cell.setCellValue(ss1);
if(j==documents1.size()-1) {
cell.setCellStyle(style3);	
}
else {
cell.setCellStyle(def);	
}
}
}

i++;
}



for (int c = 1; c < column_names.size() + 1; c++) {
spreadsheet.autoSizeColumn(c - 1);
}

} catch (Exception e) {
	log.error("Performance  complete_excel_visibility_report Exception occurs:---------" + e.getMessage(), e);
}

	
	
	

}

}
