package org.xplorg.tool.telco360.visibility_excel_report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.xplorg.tool.telco360.entity.GenericPerformance;

import com.mongodb.client.MongoDatabase;

public class complete_excel_visibility_report extends GenericPerformance {
Logger log = LogManager.getLogger(complete_excel_visibility_report.class.getName());	
public void generate_report(MongoDatabase database,String number_of_weeks,String id,String current_week) throws IOException {	

	
	


try {
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Date date = new Date();
	String currentdate = dateFormat.format(date);
	XSSFWorkbook workbook = new XSSFWorkbook();
	
	CreationHelper createHelper = workbook.getCreationHelper();
	XSSFCell cell;
	//Hyperlink....
	Hyperlink hypr[] = new Hyperlink[1500];
	
	
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
	cell.setCellValue("TRANSMISSION");
	cell.setCellStyle(style);
	spreadsheet0.autoSizeColumn(2);
	
	rc = spreadsheet0.createRow((short) 4); //count change
	cell = rc.createCell(1);
	cell.setCellValue("Vendor");
	cell.setCellStyle(style3);
	spreadsheet0.autoSizeColumn(1);
	
	cell = rc.createCell(2);
	cell.setCellValue("ALL");
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
	cell.setCellValue("Week");
	cell.setCellStyle(style3);
	spreadsheet0.autoSizeColumn(1);
	
	cell = rc.createCell(2);
	cell.setCellValue(current_week);
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
	
	*/
	
	ArrayList<String> countries=new ArrayList<String>();   //sheet name
	countries.add("Zambia");
	countries.add("Graphical View");
	countries.add("Madagascar");
	countries.add("Malawi");
	countries.add("Uganda");
	countries.add("Tanzania");
	countries.add("Kenya");
	countries.add("Chad");
	countries.add("Gabon Seychelles Niger Cango-B");
	
	for(String sheet_country:countries) {
		
	if(sheet_country.equals("Zambia")) {	
	try {
		new excel_weekly_table().weekly_table(database,workbook,current_week);
	} catch (Exception e) {
		log.error("Performance  complete_excel_visibility_report Exception occurs:--------" + id + "---" + e.getMessage(), e);
		e.printStackTrace();
	}	
	}
	else if(sheet_country.equals("Graphical View")) {
	try {
		new excel_insert_image().insert_image_sheet(database,workbook,number_of_weeks,id);
	} catch (Exception e) {
		log.error("Performance  complete_excel_visibility_report Exception occurs:--------" + id + "---" + e.getMessage(), e);
		e.printStackTrace();
	}	
	}
	
	else {
		XSSFSheet spreadsheet= workbook.createSheet(sheet_country);
	}
		
	}
	
	
	Properties config = getProperties();
	
	String report_directiory = config.getProperty("performance_mail_visibility_report");
	String report_directiory_path=report_directiory+"\\Transmission_Visibility_Status_"+id+".xlsx";
	
	
	
	
	
	FileOutputStream out = new FileOutputStream(new File(report_directiory_path));
	workbook.write(out);
	out.close();
} catch (FileNotFoundException e) {
	log.error("Performance  complete_excel_visibility_report Exception occurs:--------" + id + "---" + e.getMessage(), e);
	e.printStackTrace();
} catch (IOException e) {
	log.error("Performance  complete_excel_visibility_report Exception occurs:--------" + id + "---" + e.getMessage(), e);
	e.printStackTrace();
}


	
	
}
	

}
