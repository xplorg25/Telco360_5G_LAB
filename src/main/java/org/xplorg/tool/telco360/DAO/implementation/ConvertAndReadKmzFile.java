package org.xplorg.tool.telco360.DAO.implementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ConvertAndReadKmzFile implements Runnable{
Logger log = LogManager.getLogger(ConvertAndReadKmzFile.class.getName());
MongoDatabase database;	
File file;	
String path;
public ConvertAndReadKmzFile(MongoDatabase database,File file,String path) {
this.database=database;
this.file=file;
this.path=path;
}
	
public void run() {
try {	
if (log.isDebugEnabled()) {
log.debug("*************** checked into run of ConvertAndReadKmzFile ****************");
}	
ZipFile zipFile = new ZipFile(file);
File fileOut = new File(path+"/TransmissionMapInfo.kml");
ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
InputStream inputStream = null;
ZipEntry entry = null;
while ((entry = zipInputStream.getNextEntry()) != null) {
	
String zipEntryName = entry.getName();
//Get the node of the required file
if (zipEntryName.equals("doc.kml")) {
inputStream = zipFile.getInputStream(entry);
FileOutputStream outputStream = new FileOutputStream(fileOut);
IOUtils.copy(inputStream, outputStream);
inputStream.close();
outputStream.close();
}
}

zipFile.close();
zipInputStream.close();

MongoCollection<Document> collection1 = database.getCollection("microwave_optical_transmission");
MongoCollection<Document> collection2 = database.getCollection("microwave_optical_transmission_links");

collection1.drop();
collection2.drop();

File file = new File(path+"/TransmissionMapInfo.kml");
StringBuilder sbb=new StringBuilder();
BufferedReader br=new BufferedReader(new FileReader(file));
String line="";
while((line=br.readLine())!=null) {
if(line.trim().length()>0){	
		
if(line.contains("<Folder")){
String substr1=line.substring(0,line.indexOf("<Folder"));
int len1=substr1.length()*4;
if(len1==4){	
sbb.append(line.replace("\"", "").replace("<Folder","#@1@#<Folder")+"\n");	
}
else if(len1==8){	
sbb.append(line.replace("\"", "").replace("<Folder","#@2@#<Folder")+"\n");	
}
else if(len1==12){	
sbb.append(line.replace("\"", "").replace("<Folder","#@3@#<Folder")+"\n");	
}
else if(len1==16){	
sbb.append(line.replace("\"", "").replace("<Folder","#@4@#<Folder")+"\n");	
}
else if(len1==20){	
sbb.append(line.replace("\"", "").replace("<Folder","#@5@#<Folder")+"\n");	
}
else{
sbb.append(line.replace("\"", "")+"\n");		
}
}

else if(line.contains("</Folder")){
String substr1=line.substring(0,line.indexOf("</Folder"));
int len1=substr1.length()*4;
if(len1==4){	
sbb.append(line.replace("\"", "").replace("</Folder","@#1#@</Folder")+"\n");	
}
else if(len1==8){	
sbb.append(line.replace("\"", "").replace("</Folder","@#2#@</Folder")+"\n");	
}
else if(len1==12){	
sbb.append(line.replace("\"", "").replace("</Folder","@#3#@</Folder")+"\n");	
}
else if(len1==16){	
sbb.append(line.replace("\"", "").replace("</Folder","@#4#@</Folder")+"\n");	
}
else if(len1==20){	
sbb.append(line.replace("\"", "").replace("</Folder","@#5#@</Folder")+"\n");	
}
else{
sbb.append(line.replace("\"", "")+"\n");		
}
}
else{
sbb.append(line.replace("\"", "")+"\n");	
}
}
}
br.close();	

String substr1=sbb.toString();
String siteType="",location="";
String substr=substr1.substring(substr1.indexOf("#@1@#<Folder"));
String strng1="",strng2="",strng3="";
String spls1[]=substr.split("#@2@#<Folder");
for(String splt1:spls1) {
if(splt1.contains("#@3@#")) {
strng1=splt1.substring(0,splt1.indexOf("#@3@#<Folder"));	
String spls2[]=splt1.split("#@3@#<Folder");
for(String splt2:spls2) {
String domain=strng1.substring(strng1.indexOf("<name>")+6,strng1.indexOf("</name>"));

if((domain.contains("Fiber") || domain.contains("OFC")) && splt2.contains("#@4@#")) {
domain="Fiber";	
strng2=splt2.substring(0,splt2.indexOf("#@4@#<Folder"));	
if(strng2.contains("<name")) {
siteType=strng2.substring(strng2.indexOf("<name>")+6,strng2.indexOf("</name>"));	
}
if(!siteType.equalsIgnoreCase("Routes")) {
String spls3[]=splt2.split("#@4@#<Folder");
for(String splt3:spls3) {
if(splt3.contains("<Placemark")) {			
strng3=splt3.substring(0,splt3.indexOf("<Placemark"));	
if(strng3.contains("<name")) {
location=strng3.substring(strng3.indexOf("<name>")+6,strng3.indexOf("</name>"));	
}
String spls4[]=splt3.split("<Placemark>");	
for(String splt4:spls4) {
if(splt4.contains("<ExtendedData")){	
String siteid="-",name="-",locator="-",siteName="-",siteid2="-",city="-",longitude="-",latitude="-",siteStatus="-",coordinates="";
name=splt4.substring(splt4.indexOf("<name>")+6,splt4.indexOf("</name>"));
String spls5[]=splt4.split("\n");
for(String splt5:spls5) {
if((splt5.contains("name=ZM_ID") || splt5.contains("name=ZMID")) && splt5.contains(">") && splt5.contains("</")) {
siteid=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if((splt5.contains("name=GGE_Name") || splt5.contains("name=Site_Name_ID")) && splt5.contains(">") && splt5.contains("</")) {
name=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if((splt5.contains("name=Oracle_Locator") || splt5.contains("name=Area_District")) && splt5.contains(">") && splt5.contains("</")) {
locator=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if((splt5.contains("name=Site_Name") || splt5.contains("name=Site_ID")) && splt5.contains(">") && splt5.contains("</")) {
siteName=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if(splt5.contains("name=ZM_ID_2") && splt5.contains(">") && splt5.contains("</")) {
siteid2=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if(splt5.contains("name=City_Town") && splt5.contains(">") && splt5.contains("</")) {
city=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if((splt5.contains("name=Longitude") || splt5.contains("name=Long")) && splt5.contains(">") && splt5.contains("</")) {
longitude=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if((splt5.contains("name=Latitude") || splt5.contains("name=Lat")) && splt5.contains(">") && splt5.contains("</")) {
latitude=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if((splt5.contains("name=Site_Status") || splt5.contains("name=Status")) && splt5.contains(">") && splt5.contains("</")) {
siteStatus=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if( splt5.contains("<coordinates>") && splt5.contains("</")) {
coordinates=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).replace(",", "comma").trim();	
}
}
if(name.length()>1 && latitude.length()>1 && longitude.length()>1 && coordinates.length()>1) {
Document document=new Document("domain",domain).append("vendor", "-").append("sitetype", siteType).append("location", location).append("siteid", siteid).append("name", name)
.append("locator", locator).append("sitename", siteName).append("siteid2", siteid2).append("city", city).append("longitude", longitude).append("latitude", latitude)
.append("sitestatus", siteStatus).append("coordinates", coordinates);
collection1.insertOne(document);
}

}
else {
if(splt4.contains("name") && splt4.contains("lat") && splt4.contains("long") && splt4.contains("coordinates")){	
String spls5[]=splt4.split("\n");
String siteid="-",name="-",locator="-",siteName="-",siteid2="-",city="-",longitude="-",latitude="-",siteStatus="-",coordinates="";
for(String splt5:spls5) {
if(splt5.contains("name")) {
name=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</"));
if(name.contains("ZM")) {
String sid=name;
String sid1=sid.replaceAll("ZM_ZM", "ZM").replaceAll("ZM ZM", "ZM").replaceAll("ZM-ZM", "ZM").replaceAll("ZM-", "ZM");
siteid=sid1.substring(sid1.indexOf("ZM"),sid1.indexOf("ZM")+6);
}
else {
siteid=name;	
}
}
if(splt5.contains("<lat")) {
latitude=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</"));	
}
if(splt5.contains("<long")) {
longitude=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</"));	
}
if(splt5.contains("<coordinates")) {
coordinates=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).replace(",", "comma").trim();	;	
}

}

if(name.length()>1 && latitude.length()>1 && longitude.length()>1 && coordinates.length()>1) {
Document document=new Document("domain",domain).append("vendor", "-").append("sitetype", siteType).append("location", location).append("siteid", siteid).append("name", name)
.append("locator", locator).append("sitename", siteName).append("siteid2", siteid2).append("city", city).append("longitude", longitude).append("latitude", latitude)
.append("sitestatus", siteStatus).append("coordinates", coordinates);
collection1.insertOne(document);
}
}
}
}
}
}
}

if(siteType.contains("Routes")) {//siteType.equalsIgnoreCase("InterCity Routes") || siteType.equalsIgnoreCase("Metro Routes")
if(splt2.contains("#@4@#")) {	
String spls3[]=splt2.split("#@4@#<Folder");
for(String splt3:spls3) {
if(splt3.contains("#@5@#<Folder")){			
strng3=splt3.substring(0,splt3.indexOf("#@5@#<Folder"));	
if(strng3.contains("<name")){
location=strng3.substring(strng3.indexOf("<name>")+6,strng3.indexOf("</name>"));	
}
String spls4[]=splt3.split("#@5@#<Folder");
for(String splt4:spls4){
if(splt4.contains("<Placemark")) {
String strng4=splt4.substring(0,splt4.indexOf("<Placemark"));	
String routename=strng4.substring(strng4.indexOf("<name>")+6,strng4.indexOf("</name>"));

String strng5=splt4.substring(splt4.indexOf("<Placemark"));	
String spls5[]=strng5.split("<Placemark");

for(String splt5:spls5) {
if(splt5.trim().length()>0) {	
String routeto="-";
if(splt5.contains("<name>")) {
routeto=splt5.substring(splt5.indexOf("<name>")+6,splt5.indexOf("</name>")).trim();
}
String coordinates=splt5.substring(splt5.indexOf("<coordinates>")+13,splt5.indexOf("</coordinates>")).replace(",", "comma").trim();
Document document=new Document("domain","Fiber").append("vendor", "-").append("routename", routename).append("routeto", routeto).append("coordinates", coordinates);
collection2.insertOne(document);
}
}
}
}
}
if(!splt3.contains("#@5@#<Folder")) {
if(splt3.contains("<Placemark")) {
strng3=splt3.substring(0,splt3.indexOf("<Placemark"));	
if(strng3.contains("<name")){
location=strng3.substring(strng3.indexOf("<name>")+6,strng3.indexOf("</name>")).replace(",", "comma");	
}

String strng4=splt3.substring(splt3.indexOf("<Placemark"));
String spls4[]=strng4.split("<Placemark");

for(String splt4:spls4) {
if(splt4.trim().length()>0) {
String routeto=splt4.substring(splt4.indexOf("<name>")+6,splt4.indexOf("</name>")).trim().replace(",", "comma");
String coordinates=splt4.substring(splt4.indexOf("<coordinates>")+13,splt4.indexOf("</coordinates>")).replace(",", "comma").trim();
Document document=new Document("domain","Fiber").append("vendor", "-").append("routename", location).append("routeto", routeto).append("coordinates", coordinates);
collection2.insertOne(document);
}	
}
}
}
}
}
}
}
else if(domain.equalsIgnoreCase("Microwave") || domain.equalsIgnoreCase("Microwave Links")) {
if(splt2.contains("<Placemark")) {

strng3=splt2.substring(0,splt2.indexOf("<Placemark"));
if(strng3.contains("<name")) {
siteType=strng3.substring(strng3.indexOf("<name>")+6,strng3.indexOf("</name>"));	
}
location="-";
if(siteType.equalsIgnoreCase("Sites")) {

String spls3[]=splt2.split("<Placemark");	
for(String splt3:spls3) {
if(splt3.contains("<ExtendedData")){	
String siteid="-",name="-",locator="-",siteName="-",siteid2="-",city="-",longitude="-",latitude="-",siteStatus="-",coordinates="-";

String spls4[]=splt3.split("\n");

for(String splt4:spls4) {
if((splt4.contains("name=ZM_ID") || splt4.contains("name=ZMID")) && splt4.contains(">") && splt4.contains("</")) {
siteid=splt4.substring(splt4.indexOf(">")+1,splt4.indexOf("</")).trim();	
}
if((splt4.contains("name=GGE_Name") || splt4.contains("name=Site_Name_ID")) && splt4.contains(">") && splt4.contains("</")) {
name=splt4.substring(splt4.indexOf(">")+1,splt4.indexOf("</")).trim();	
}
if((splt4.contains("name=Oracle_Locator") || splt4.contains("name=Area_District")) && splt4.contains(">") && splt4.contains("</")) {
locator=splt4.substring(splt4.indexOf(">")+1,splt4.indexOf("</")).trim();	
}
if((splt4.contains("name=Site_Name") || splt4.contains("name=Site_ID")) && splt4.contains(">") && splt4.contains("</")) {
siteName=splt4.substring(splt4.indexOf(">")+1,splt4.indexOf("</")).trim();	
}
if(splt4.contains("name=ZM_ID_2") && splt4.contains(">") && splt4.contains("</")) {
siteid2=splt4.substring(splt4.indexOf(">")+1,splt4.indexOf("</")).trim();	
}
if(splt4.contains("name=City_Town") && splt4.contains(">") && splt4.contains("</")) {
city=splt4.substring(splt4.indexOf(">")+1,splt4.indexOf("</")).trim();	
}
if((splt4.contains("name=Longitude") || splt4.contains("name=Long")) && splt4.contains(">") && splt4.contains("</")) {
longitude=splt4.substring(splt4.indexOf(">")+1,splt4.indexOf("</")).trim();	
}
if((splt4.contains("name=Latitude") || splt4.contains("name=Latitude")) && splt4.contains(">") && splt4.contains("</")) {
latitude=splt4.substring(splt4.indexOf(">")+1,splt4.indexOf("</")).trim();	
}
if((splt4.contains("name=Site_Status") || splt4.contains("name=Status")) && splt4.contains(">") && splt4.contains("</")) {
siteStatus=splt4.substring(splt4.indexOf(">")+1,splt4.indexOf("</")).trim();	
}
if( splt4.contains("<coordinates>") && splt4.contains("</")) {
coordinates=splt4.substring(splt4.indexOf(">")+1,splt4.indexOf("</")).replace(",", "comma").trim();	
}
}

if(siteid.length()>1 && name.length()>1 && siteName.length()>1 && city.length()>1 && latitude.length()>1 && longitude.length()>1) {
Document document=new Document("domain",domain).append("vendor", "-").append("sitetype", siteType).append("location", location).append("siteid", siteid).append("name", name)
.append("locator", locator).append("sitename", siteName).append("siteid2", siteid2).append("city", city).append("longitude", longitude).append("latitude", latitude)
.append("sitestatus", siteStatus).append("coordinates", coordinates);
collection1.insertOne(document);
}

}
}
}
if(siteType.contains("Links") || siteType.contains("MW")) {
String strng4=splt2.substring(splt2.indexOf("<Placemark"));	
String spls4[]=strng4.split("<Placemark");

for(String splt4:spls4) {
if(splt4.trim().length()>0) {	
if(splt4.contains("<name") && splt4.contains("<coordinates")){	
String routeto=splt4.substring(splt4.indexOf("<name>")+6,splt4.indexOf("</name>")).trim().replace(",", "comma");
String coordinates=splt4.substring(splt4.indexOf("<coordinates>")+13,splt4.indexOf("</coordinates>")).trim().replace(",", "comma");
Document document=new Document("domain","Microwave").append("vendor", "-").append("routename", "link").append("routeto", routeto).append("coordinates", coordinates);
collection2.insertOne(document);
}
}
}
}
}
}

}
}
if(!splt1.contains("#@3@#")) {
if(splt1.contains("<Placemark>")) {
String domain="Microwave";	
strng3=splt1.substring(0,splt1.indexOf("<Placemark"));	
if(strng3.contains("<name")) {
siteType=location=strng3.substring(strng3.indexOf("<name>")+6,strng3.indexOf("</name>"));	
}
String spls4[]=splt1.split("<Placemark>");	
for(String splt4:spls4) {
if(splt4.contains("<ExtendedData")){	
String siteid="-",name="-",locator="-",siteName="-",siteid2="-",city="-",longitude="-",latitude="-",siteStatus="-",coordinates="";
name=splt4.substring(splt4.indexOf("<name>")+6,splt4.indexOf("</name>"));
String spls5[]=splt4.split("\n");
for(String splt5:spls5) {
if((splt5.contains("name=ZM_ID") || splt5.contains("name=ZMID")) && splt5.contains(">") && splt5.contains("</")) {
siteid=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if((splt5.contains("name=GGE_Name") || splt5.contains("name=Site_Name_ID")) && splt5.contains(">") && splt5.contains("</")) {
name=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if((splt5.contains("name=Oracle_Locator") || splt5.contains("name=Area_District")) && splt5.contains(">") && splt5.contains("</")) {
locator=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if((splt5.contains("name=Site_Name") || splt5.contains("name=Site_ID")) && splt5.contains(">") && splt5.contains("</")) {
siteName=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if(splt5.contains("name=ZM_ID_2") && splt5.contains(">") && splt5.contains("</")) {
siteid2=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if(splt5.contains("name=City_Town") && splt5.contains(">") && splt5.contains("</")) {
city=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if((splt5.contains("name=Longitude") || splt5.contains("name=Long")) && splt5.contains(">") && splt5.contains("</")) {
longitude=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if((splt5.contains("name=Latitude") || splt5.contains("name=Lat")) && splt5.contains(">") && splt5.contains("</")) {
latitude=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if((splt5.contains("name=Site_Status") || splt5.contains("name=Status")) && splt5.contains(">") && splt5.contains("</")) {
siteStatus=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).trim();	
}
if( splt5.contains("<coordinates>") && splt5.contains("</")) {
coordinates=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).replace(",", "comma").trim();	
}
}
if(name.length()>1 && latitude.length()>1 && longitude.length()>1 && coordinates.length()>1) {
if(siteid.equals("-")) {
if(name.contains("_")) {	
siteid=name.substring(0,name.indexOf("_"));
}
else {
siteid=name;	
}
}
Document document=new Document("domain",domain).append("vendor", "-").append("sitetype", siteType).append("location", location).append("siteid", siteid).append("name", name)
.append("locator", locator).append("sitename", siteName).append("siteid2", siteid2).append("city", city).append("longitude", longitude).append("latitude", latitude)
.append("sitestatus", siteStatus).append("coordinates", coordinates);
collection1.insertOne(document);
}

}
else {
if(splt4.contains("name") && splt4.contains("lat") && splt4.contains("long") && splt4.contains("coordinates")){	
String spls5[]=splt4.split("\n");
String siteid="-",name="-",locator="-",siteName="-",siteid2="-",city="-",longitude="-",latitude="-",siteStatus="-",coordinates="";
for(String splt5:spls5) {
if(splt5.contains("name")) {
name=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</"));
if(name.contains("ZM")) {
String sid=name;
String sid1=sid.replaceAll("ZM_ZM", "ZM").replaceAll("ZM ZM", "ZM").replaceAll("ZM-ZM", "ZM").replaceAll("ZM-", "ZM");
siteid=sid1.substring(sid1.indexOf("ZM"),sid1.indexOf("ZM")+6);
}
else {
siteid=name;	
}
}
if(splt5.contains("<lat")) {
latitude=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</"));	
}
if(splt5.contains("<long")) {
longitude=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</"));	
}
if(splt5.contains("<coordinates")) {
coordinates=splt5.substring(splt5.indexOf(">")+1,splt5.indexOf("</")).replace(",", "comma").trim();	;	
}

}

if(name.length()>1 && latitude.length()>1 && longitude.length()>1 && coordinates.length()>1) {
Document document=new Document("domain",domain).append("vendor", "-").append("sitetype", siteType).append("location", location).append("siteid", siteid).append("name", name)
.append("locator", locator).append("sitename", siteName).append("siteid2", siteid2).append("city", city).append("longitude", longitude).append("latitude", latitude)
.append("sitestatus", siteStatus).append("coordinates", coordinates);
collection1.insertOne(document);
}
}
else {
String siteid="-",name="-",locator="-",siteName="-",siteid2="-",city="-",longitude="-",latitude="-",siteStatus="-",coordinates="";
if(splt4.contains("name") && splt4.contains("description") && splt4.contains("coordinates")) {
name=splt4.substring(splt4.indexOf("<name>")+6,splt4.indexOf("</name>"));
coordinates=splt4.substring(splt4.indexOf("<coordinates>")+13,splt4.indexOf("</coordinates>")).replace(",", "comma").trim();	;	
if(name.contains("ZM")) {
String sid=name;
String sid1=sid.replaceAll("ZM_ZM", "ZM").replaceAll("ZM ZM", "ZM").replaceAll("ZM-ZM", "ZM").replaceAll("ZM-", "ZM");
siteid=sid1.substring(sid1.indexOf("ZM"),sid1.indexOf("ZM")+6);
}
else {
siteid=name;	
}
String description1=splt4.substring(splt4.indexOf("<description>")+13,splt4.indexOf("</description>"));
String spls5[]=description1.split("\n");
for(String splt5:spls5) {
if(splt5.contains("Lat")) {
latitude=splt5.substring(splt5.indexOf("Lat:")+4).trim();
}
if(splt5.contains("Lng")) {
longitude=splt5.substring(splt5.indexOf("Lng:")+4).trim();
}
}
if(name.length()>1 && latitude.length()>1 && longitude.length()>1 && coordinates.length()>1) {
Document document=new Document("domain",domain).append("vendor", "-").append("sitetype", siteType).append("location", location).append("siteid", siteid).append("name", name)
.append("locator", locator).append("sitename", siteName).append("siteid2", siteid2).append("city", city).append("longitude", longitude).append("latitude", latitude)
.append("sitestatus", siteStatus).append("coordinates", coordinates);
collection1.insertOne(document);
}
}
}
}
}
}
}
}

Thread thrd=new Thread(new KMLFileUpdateVendors(database));
thrd.start();

//mongo.close();
}catch(Exception ex) {
log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();	
}
}
	
	
}
