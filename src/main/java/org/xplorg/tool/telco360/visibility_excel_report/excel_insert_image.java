package org.xplorg.tool.telco360.visibility_excel_report;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.xplorg.tool.telco360.entity.GenericPerformance;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
public class excel_insert_image extends GenericPerformance {

Logger log = LogManager.getLogger(excel_insert_image.class.getName());		
public void insert_image_sheet(MongoDatabase database,XSSFWorkbook workbook,String number_of_weeks,String id) {	

ArrayList < String > column_names = new ArrayList < String > ();
column_names.add("Country");

Sheet sheet = workbook.createSheet("Graphical View");
int week = 0;
try {
String input = current_date_yyyyMMdd();
String format = "yyyyMMdd";

SimpleDateFormat df = new SimpleDateFormat(format);
Date date = df.parse(input);

Calendar cal = Calendar.getInstance();
cal.setTime(date);
week = cal.get(Calendar.WEEK_OF_YEAR);

} catch (ParseException e) {
log.error("Performance  insert_image_sheet Exception occurs:--------" + id + "---" + e.getMessage(), e);
}

if(number_of_weeks.equals("0")) {
	column_names.add("WEEK " + week);	
}

else {
	int lastweeks=Integer.parseInt(number_of_weeks);
	for (int i = week - 1; i > (week - (lastweeks + 1)); i--) {

	column_names.add("WEEK " + i);
	}	
}


try {

Row row = sheet.createRow((short) 0);

Cell cell;

CreationHelper createHelper = workbook.getCreationHelper();
Hyperlink link1 = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
link1.setAddress("'Summary'!A1");

CellStyle hlinkstyle = workbook.createCellStyle();
Font hlinkfont = workbook.createFont();
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
Font font = workbook.createFont();
font.setFontHeightInPoints((short) 11);
font.setColor(IndexedColors.BLACK.getIndex());

style.setFont(font);

CellStyle def = workbook.createCellStyle();
//  style2.setFillPattern(CellStyle.SOLID_FOREGROUND);
Font fontdef = workbook.createFont();
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
Font font3 = workbook.createFont();
font3.setFontHeightInPoints((short) 11);
font3.setColor(IndexedColors.BLACK.getIndex());

style3.setFont(font3);

row = sheet.createRow((short) 2);
cell = row.createCell(0);
cell.setCellValue("Consolidate Visibility Report");
cell.setCellStyle(style);

row = sheet.createRow(3);

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

MongoCollection < Document > collection1 = database.getCollection("country_weekly");

ArrayList < Document > documents1 = null;

documents1 = collection1.find().into(new ArrayList < Document > ());


for (int j = 0; j < documents1.size(); j++) {

row = sheet.createRow(i);
String ss1 = "-";
Document d = documents1.get(j);
for (int k = 0; k < column_names.size(); k++) {
if (d.get(column_names.get(k)) != null) {
if (d.get(column_names.get(k)).toString().length() > 0) {
ss1 = d.get(column_names.get(k)).toString();
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

for (int c = 1; c < column_names.size() + 1; c++) {
sheet.autoSizeColumn(c - 1);
}

} catch (Exception e) {
	log.error("Performance  insert_image_sheet Exception occurs:--------" + id + "---" + e.getMessage(), e);
}

try {
	
 Properties config = getProperties();
  
 String report_directiory = config.getProperty("performance_mail_visibility_image");
 String image_name=report_directiory+"\\visibility_"+id+".png";	
	

//FileInputStream obtains input bytes from the image file
InputStream inputStream = new FileInputStream(image_name);
//Get the contents of an InputStream as a byte[].
byte[] bytes = IOUtils.toByteArray(inputStream);
//Adds a picture to the workbook
int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
//close the input stream
inputStream.close();
//Returns an object that handles instantiating concrete classes
CreationHelper helper = workbook.getCreationHelper();
//Creates the top-level drawing patriarch.
Drawing drawing = sheet.createDrawingPatriarch();

//Create an anchor that is attached to the worksheet
ClientAnchor anchor = helper.createClientAnchor();

//create an anchor with upper left cell _and_ bottom right cell
anchor.setCol1(6); //Column B
anchor.setRow1(2); //Row 3
anchor.setCol2(19); //Column C
anchor.setRow2(23); //Row 4

//Creates a picture
Picture pict = drawing.createPicture(anchor, pictureIdx);

//Reset the image to the original size
//pict.resize(); //don't do that. Let the anchor resize the image!

//Create the Cell B3
Cell cell = sheet.createRow(2).createCell(1);

//set width to n character widths = count characters * 256
//int widthUnits = 20*256;
//sheet.setColumnWidth(1, widthUnits);

//set height to n points in twips = n * 20
//short heightUnits = 60*20;
//cell.getRow().setHeight(heightUnits);

//Write the Excel file

} catch (IOException ioex) {
	
	log.error("Performance  insert_image_sheet Exception occurs:--------" + id + "---" + ioex.getMessage(), ioex);
	
}

	
}
	

}
