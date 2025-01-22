package org.xplorg.tool.telco360.entity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class il_task_generic extends Thread{

String protocol;
String local_hostname;
String local_username;
String local_password;
int a;
int b;
ArrayList<String> elementname;
ArrayList<String> hostname;
ArrayList<String> username;
ArrayList<String> password;
ArrayList<String> ring;
ArrayList<String> commands;
ArrayList<String> patterns;
ArrayList<String> values;
ArrayList<String> conditions;
String pattern;
String value;
String outputType;
StringBuilder sbb_response;

Logger log = LogManager.getLogger(il_task_generic.class.getName());

public il_task_generic() {
}

public il_task_generic(StringBuilder sbb_response,String protocol,String local_hostname,String local_username,String local_password,int a, int b, ArrayList<String> elementname, ArrayList<String> hostname, ArrayList<String> username,ArrayList<String> password,
		ArrayList<String> ring,ArrayList<String> commands,ArrayList<String> patterns,ArrayList<String> values,ArrayList<String> conditions,String pattern,String value,String outputType){
this.sbb_response=sbb_response;
this.protocol=protocol;
this.local_hostname=local_hostname;
this.local_username=local_username;
this.local_password=local_password;
this.a = a;
this.b = b;
this.elementname=elementname;
this.hostname=hostname;
this.username=username;
this.password=password;
this.ring=ring;
this.commands=commands;
this.patterns=patterns;
this.values=values;
this.conditions=conditions;
this.pattern=pattern;
this.value=value;
this.outputType=outputType;
}


public void run(){
try {
if(protocol.toUpperCase().equals("SSH")){
for(int i=a;i<b;i++){				
Session session=getSession(hostname.get(i), username.get(i), password.get(i));
if(session.isConnected()){	
task(sbb_response,protocol,session,commands,patterns,values,conditions,value,outputType,i);
session.disconnect();
}
}
}
if(protocol.toUpperCase().equals("SNMP")){
Session session=getSession(local_hostname,local_username,local_password);
if(session.isConnected()){	
for(int i=a;i<b;i++){		
task(sbb_response,protocol,session,commands,patterns,values,conditions,value,outputType,i);
}
session.disconnect();
}
}
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}	
}

public void task(StringBuilder sbb_response,String protocol,Session session,ArrayList<String>commands,ArrayList<String>patterns,ArrayList<String>values,ArrayList<String>conditions,String value,String outputType,int i) {
try {		
if(protocol.toUpperCase().equals("SSH")) {	
ArrayList<String>output=new ArrayList<String>();	

// create the IO streams to send input to remote session.
PipedOutputStream commandIO = new PipedOutputStream();
InputStream sessionInput = new PipedInputStream(commandIO);
// this set's the InputStream the remote server will read from.
Channel channel=session.openChannel("shell");
channel.setInputStream(sessionInput);

// this will have the STDOUT from server.
InputStream sessionOutput = channel.getInputStream();

// this will have the STDERR from server
//InputStream sessionError = channel.getExtInputStream();

channel.connect();

//Read input until we get the 'Password:' prompt
byte[] tmp = new byte[1024];
String stdOut = "";

int k = sessionOutput.read(tmp, 0, tmp.length);

String command;
stdOut += new String(tmp, 0, k);

if(stdOut.toUpperCase().contains("YES/NO")) {
command = "yes"+"\n";
commandIO.write(command.getBytes());
commandIO.flush();
Thread.sleep(1000);
}

for(String cmd:commands) {
command =cmd + "\n";
commandIO.write(command.getBytes());
commandIO.flush();
Thread.sleep(1000);  
}
  
command ="exit"+ "\n";  
Thread.sleep(3000); 
commandIO.write(command.getBytes());
commandIO.flush();
  
command ="quit"+ "\n";  
Thread.sleep(1000); 
commandIO.write(command.getBytes());
commandIO.flush();
 
// read and print output.
while ((k = sessionOutput.read(tmp, 0, tmp.length)) != -1) {
String r=(new String(tmp, 0, k));	
output.add(r);
if(r.trim().contains("quit")) {
channel.disconnect();
break;    		
}
}

StringBuilder sb=new StringBuilder();

for(int j=0;j<output.size();j++) {
sb.append(output.get(j));	
}

String result=sb.toString();

StringBuilder sbb=new StringBuilder();
BufferedReader br=new BufferedReader(new StringReader(result));
String line="";
while((line=br.readLine())!=null) {	
sbb.append(line+"\n");
}

br.close();	

if(sbb.toString().trim().length()>0 && outputType.equalsIgnoreCase("Normal")){
String finalresult="";
String resultFinal="";

for(String strng:patterns) {
String ptrn=strng;
Pattern patt=Pattern.compile(ptrn);
Matcher mat=patt.matcher(sbb.toString());
while(mat.find()) {
finalresult=finalresult+mat.group()+"\n"+"#@@#";
}
}
for(String vals:values) {
String checkParam="";
String checkValue="";
String checkCondition="";
if(vals.contains("=") && !vals.contains("<=") && !vals.contains(">=") && !vals.contains("!=")) {
checkParam=vals.substring(0,vals.indexOf("=")).trim();
checkValue=vals.substring(vals.indexOf("=")+1).trim();
checkCondition="=";
}
else if(vals.contains(">") && !vals.contains("<=") && !vals.contains("=") && !vals.contains("!=")) {
checkParam=vals.substring(0,vals.indexOf(">")).trim();
checkValue=vals.substring(vals.indexOf(">")+1).trim();
checkCondition=">";
}
else if(vals.contains(">=")) {
checkParam=vals.substring(0,vals.indexOf(">=")).trim();
checkValue=vals.substring(vals.indexOf(">=")+2).trim();
checkCondition=">=";
}
else if(vals.contains("<") && !vals.contains("<=") && !vals.contains("=") && !vals.contains("!=")) {
checkParam=vals.substring(0,vals.indexOf("<")).trim();
checkValue=vals.substring(vals.indexOf("<")+1).trim();
checkCondition="<";
}
else if(vals.contains("<=")) {
checkParam=vals.substring(0,vals.indexOf("<=")).trim();
checkValue=vals.substring(vals.indexOf("<=")+2).trim();
checkCondition="<=";
}
else if(vals.contains("!=")) {
checkParam=vals.substring(0,vals.indexOf("!=")).trim();
checkValue=vals.substring(vals.indexOf("!=")+2).trim();
checkCondition="!=";
}

if(finalresult.trim().length()>0){
String spls[]=finalresult.split("#@@#");	
for(String splt:spls) {
String spls1[]=splt.split("\n");

for(String splt1:spls1){
if(splt1.contains(checkParam)){
if(checkCondition.equals("=")) {
String spltPrm=splt1.replaceAll("\\s+","");	
String splt1Value=spltPrm.substring(spltPrm.indexOf(checkParam)+checkParam.length()+1).trim();
if(splt1Value.toUpperCase().equals(checkValue.toUpperCase())) {
resultFinal=resultFinal+splt+"\n";	
}
}
else if(checkCondition.equals(">")) {
String spltPrm=splt1.replaceAll("\\s+","");	
String splt1Value=spltPrm.substring(spltPrm.indexOf(checkParam)+checkParam.length()+1).trim();
if(Double.parseDouble(splt1Value)>Double.parseDouble((checkValue))) {
resultFinal=resultFinal+splt+"\n";	
}
}
else if(checkCondition.equals(">=")) {
String spltPrm=splt1.replaceAll("\\s+","");	
String splt1Value=spltPrm.substring(spltPrm.indexOf(checkParam)+checkParam.length()+1).trim();
if(Double.parseDouble(splt1Value)>=Double.parseDouble((checkValue))) {
resultFinal=resultFinal+splt+"\n";	
}
}
else if(checkCondition.equals("<")) {
String spltPrm=splt1.replaceAll("\\s+","");	
String splt1Value=spltPrm.substring(spltPrm.indexOf(checkParam)+checkParam.length()+1).trim();
if(Double.parseDouble(splt1Value)<Double.parseDouble((checkValue))) {
resultFinal=resultFinal+splt+"\n";	
}
}
else if(checkCondition.equals("<=")) {
String spltPrm=splt1.replaceAll("\\s+","");	
String splt1Value=spltPrm.substring(spltPrm.indexOf(checkParam)+checkParam.length()+1).trim();
if(Double.parseDouble(splt1Value)<=Double.parseDouble((checkValue))) {
resultFinal=resultFinal+splt+"\n";	
}
}
else if(checkCondition.equals("!=")) {
String spltPrm=splt1.replaceAll("\\s+","");	
String splt1Value=spltPrm.substring(spltPrm.indexOf(checkParam)+checkParam.length()+1).trim();
if(!splt1Value.equalsIgnoreCase((checkValue))) {
resultFinal=resultFinal+splt+"\n";	
}
}
}
}

}
}
}

if(resultFinal.length()>0) {
String response="Hostname:"+hostname.get(i)+"; NE Name:"+elementname.get(i)+";"+resultFinal.replaceAll("\n","@NEXTLINE@");	
sbb_response.append(response+";;");
}
else {
String response="Hostname:"+hostname.get(i)+"; NE Name:"+elementname.get(i)+";"+"NA";	
sbb_response.append(response+";;");
}
}

if(sbb.toString().trim().length()>0 && outputType.equalsIgnoreCase("Tabular")){
String finalresult="";
String resultFinal="";

for(String strng:patterns) {
String ptrn=strng;
String substr1=ptrn.substring(ptrn.indexOf("(")+1,ptrn.indexOf(")")).replace(".*", "");
String substr=sbb.toString();
finalresult=substr.substring(substr.indexOf(substr1));
}

for(String vals:values) {
String checkParam="";
String checkValue="";
String checkCondition="";
if(vals.contains("=") && !vals.contains("<=") && !vals.contains(">=") && !vals.contains("!=")) {
checkParam=vals.substring(0,vals.indexOf("=")).trim();
checkValue=vals.substring(vals.indexOf("=")+1).trim();
checkCondition="=";
}
else if(vals.contains(">") && !vals.contains("<=") && !vals.contains("=") && !vals.contains("!=")) {
checkParam=vals.substring(0,vals.indexOf(">")).trim();
checkValue=vals.substring(vals.indexOf(">")+1).trim();
checkCondition=">";
}
else if(vals.contains(">=")) {
checkParam=vals.substring(0,vals.indexOf(">=")).trim();
checkValue=vals.substring(vals.indexOf(">=")+2).trim();
checkCondition=">=";
}
else if(vals.contains("<") && !vals.contains("<=") && !vals.contains("=") && !vals.contains("!=")) {
checkParam=vals.substring(0,vals.indexOf("<")).trim();
checkValue=vals.substring(vals.indexOf("<")+1).trim();
checkCondition="<";
}
else if(vals.contains("<=")) {
checkParam=vals.substring(0,vals.indexOf("<=")).trim();
checkValue=vals.substring(vals.indexOf("<=")+2).trim();
checkCondition="<=";
}
else if(vals.contains("!=")) {
checkParam=vals.substring(0,vals.indexOf("!=")).trim();
checkValue=vals.substring(vals.indexOf("!=")+2).trim();
checkCondition="!=";
}

if(finalresult.trim().length()>0){
String matcher_cols="";
String matcher_vals="";
BufferedReader brRead=new BufferedReader(new StringReader(finalresult));
String lineRead="";
boolean boln=false;
while((lineRead=brRead.readLine())!=null) {
if(!lineRead.contains("--------")){
if(boln==false) {
matcher_cols=lineRead.trim().replace(" Id", "_Id").replace(" id", "_id").replaceAll("\\s+", "@#@");
boln=true;
}
else {
matcher_vals=matcher_vals+lineRead.trim().replaceAll("\\s+", "@#@")+"\n";
}
}
}

brRead.close();	

String indexColSpls[]=matcher_cols.split("@#@");
int idxcol=0,idxcolfinal=0;
for(String clsp:indexColSpls) {	
if(clsp.equals(checkParam)) {
idxcolfinal=idxcol;	
break;
}
idxcol++;
}

String spls1[]=matcher_vals.split("\n");
for(String splt1:spls1){
if(splt1.contains("@#@")) {	
if(checkCondition.equals("=")) {
String spltValue=splt1.split("@#@")[idxcolfinal].trim();
if(spltValue.replaceAll("[^0-9.]", "").trim().length()<0){
if(spltValue.length()>0) {
if(Double.parseDouble(spltValue)==Double.parseDouble((checkValue))) {
resultFinal=resultFinal+splt1+"\n";	
}
}
}
else if(spltValue.replaceAll("[0-9.]", "").trim().length()>0) {
if(spltValue.equalsIgnoreCase((checkValue))){
resultFinal=resultFinal+splt1+"\n";	
}
}
}
else if(checkCondition.equals(">")) {
String spltValue1=splt1.split("@#@")[idxcolfinal];
String spltValue=spltValue1.replaceAll("[^0-9.]", "").trim();
if(spltValue.length()>0) {
if(Double.parseDouble(spltValue)>Double.parseDouble((checkValue))) {
resultFinal=resultFinal+splt1+"\n";	
}
}
}
else if(checkCondition.equals(">=")) {
String spltValue=splt1.split("@#@")[idxcolfinal].replaceAll("[^0-9.]", "").trim();
if(spltValue.length()>0) {
if(Double.parseDouble(spltValue)>=Double.parseDouble((checkValue))) {
resultFinal=resultFinal+splt1+"\n";	
}
}
}
else if(checkCondition.equals("<")) {
String spltValue=splt1.split("@#@")[idxcolfinal].replaceAll("[^0-9.]", "").trim();
if(spltValue.length()>0) {
if(Double.parseDouble(spltValue)<Double.parseDouble((checkValue))) {
resultFinal=resultFinal+splt1+"\n";	
}
}
}
else if(checkCondition.equals("<=")) {
String spltValue=splt1.split("@#@")[idxcolfinal].replaceAll("[^0-9.]", "").trim();
if(spltValue.length()>0) {
if(Double.parseDouble(spltValue)<=Double.parseDouble((checkValue))) {
resultFinal=resultFinal+splt1+"\n";	
}
}
}
else if(checkCondition.equals("!=")) {
String spltValue=splt1.split("@#@")[idxcolfinal].replace("%", "").trim();
if(!spltValue.equalsIgnoreCase((checkValue))) {
resultFinal=resultFinal+splt1+"\n";	
}
}
}
}
}
}

if(resultFinal.length()>0) {
String response="Hostname:"+hostname.get(i)+"; NE Name:"+elementname.get(i)+";"+resultFinal.replaceAll("@#@", " ").replaceAll("\n","@NEXTLINE@");	
sbb_response.append(response+";;");
}
else {
String response="Hostname:"+hostname.get(i)+"; NE Name:"+elementname.get(i)+";"+"NA";	
sbb_response.append(response+";;");
}
}


if(!channel.isConnected()){	
channel.disconnect();
}

}

if(protocol.toUpperCase().equals("SNMP")){		
ArrayList<String>output=new ArrayList<String>();	

// create the IO streams to send input to remote session.
PipedOutputStream commandIO = new PipedOutputStream();
InputStream sessionInput = new PipedInputStream(commandIO);
// this set's the InputStream the remote server will read from.
Channel channel=session.openChannel("shell");
channel.setInputStream(sessionInput);
// this will have the STDOUT from server.
InputStream sessionOutput = channel.getInputStream();
// this will have the STDERR from server
//InputStream sessionError = channel.getExtInputStream();
channel.connect();

//Read input until we get the 'Password:' prompt
byte[] tmp = new byte[1024];
//String stdOut = "";
//String stdErr = "";
int k;

String command="";

for(String cmd:commands) {
if(cmd.contains("*")) {
String cmd1=cmd.substring(0,cmd.indexOf("*")).trim();
String cmd2=cmd.substring(cmd.lastIndexOf("*")+1).trim();
command =cmd1+" "+hostname.get(i)+" "+cmd2 + "\n";
commandIO.write(command.getBytes());
commandIO.flush();
Thread.sleep(5000);  
}
else {
command =cmd + "\n";
commandIO.write(command.getBytes());
commandIO.flush();
Thread.sleep(5000);  	
}
}
  
command ="exit"+ "\n";
        
Thread.sleep(1000); 
commandIO.write(command.getBytes());
commandIO.flush();
 
// read and print output.
while ((k = sessionOutput.read(tmp, 0, tmp.length)) != -1) {
String r=(new String(tmp, 0, k));	
output.add(r);
if(r.trim().contains("exit")) {
channel.disconnect();
break;    		
}
}

StringBuilder sb=new StringBuilder();

for(int j=0;j<output.size();j++) {
sb.append(output.get(j));	
}

String result=sb.toString();
StringBuilder sbb=new StringBuilder();
BufferedReader br=new BufferedReader(new StringReader(result));
String line="";
while((line=br.readLine())!=null) {	
sbb.append(line+"\n");
}


if(sbb.toString().trim().length()>0){
String finalresult="";
String resultFinal="";

for(String strng:patterns) {
String ptrn=strng;
Pattern patt=Pattern.compile(ptrn);
Matcher mat=patt.matcher(sbb.toString());
while(mat.find()) {
if(!mat.group().contains("snmpbulkwalk") && !mat.group().contains("snmpwalk") && !mat.group().contains("snmpbulkget") && !mat.group().contains("snmpget")  && !mat.group().contains("snmptable")) {	
finalresult=finalresult+mat.group()+"\n"+"#@@#";
}
}
}
for(String vals:values) {
String checkParam="";
String checkValue="";
String checkCondition="";
if(vals.contains("=") && !vals.contains("<=") && !vals.contains(">=") && !vals.contains("!=")) {
checkParam=vals.substring(0,vals.indexOf("=")).trim();
checkValue=vals.substring(vals.indexOf("=")+1).trim();
checkCondition="=";
}
else if(vals.contains(">") && !vals.contains("<=") && !vals.contains("=") && !vals.contains("!=")) {
checkParam=vals.substring(0,vals.indexOf(">")).trim();
checkValue=vals.substring(vals.indexOf(">")+1).trim();
checkCondition=">";
}
else if(vals.contains(">=")) {
checkParam=vals.substring(0,vals.indexOf(">=")).trim();
checkValue=vals.substring(vals.indexOf(">=")+2).trim();
checkCondition=">=";
}
else if(vals.contains("<") && !vals.contains("<=") && !vals.contains("=") && !vals.contains("!=")) {
checkParam=vals.substring(0,vals.indexOf("<")).trim();
checkValue=vals.substring(vals.indexOf("<")+1).trim();
checkCondition="<";
}
else if(vals.contains("<=")) {
checkParam=vals.substring(0,vals.indexOf("<=")).trim();
checkValue=vals.substring(vals.indexOf("<=")+2).trim();
checkCondition="<=";
}
else if(vals.contains("!=")) {
checkParam=vals.substring(0,vals.indexOf("!=")).trim();
checkValue=vals.substring(vals.indexOf("!=")+2).trim();
checkCondition="!=";
}

if(finalresult.trim().length()>0){
String spls[]=finalresult.split("#@@#");	
for(String splt:spls) {
String spls1[]=splt.split("\n");

for(String splt1:spls1){
if(splt1.contains(checkParam)){
if(checkCondition.equals("=")) {
String spltPrm=splt1.replaceAll("\\s+","");	
String splt1Value=spltPrm.substring(spltPrm.lastIndexOf(":")+1).trim();
if(splt1Value.toUpperCase().equals(checkValue.toUpperCase())) {
resultFinal=resultFinal+splt+"\n";	
}
}
else if(checkCondition.equals(">")) {
String spltPrm=splt1.replaceAll("\\s+","");	
String splt1Value=spltPrm.substring(spltPrm.lastIndexOf(":")+1).trim();
if(Double.parseDouble(splt1Value)>Double.parseDouble((checkValue))) {
resultFinal=resultFinal+splt+"\n";	
}
}
else if(checkCondition.equals(">=")) {
String spltPrm=splt1.replaceAll("\\s+","");	
String splt1Value=spltPrm.substring(spltPrm.lastIndexOf(":")+1).trim();
if(Double.parseDouble(splt1Value)>=Double.parseDouble((checkValue))) {
resultFinal=resultFinal+splt+"\n";	
}
}
else if(checkCondition.equals("<")) {
String spltPrm=splt1.replaceAll("\\s+","");	
String splt1Value=spltPrm.substring(spltPrm.lastIndexOf(":")+1).trim();
if(Double.parseDouble(splt1Value)<Double.parseDouble((checkValue))) {
resultFinal=resultFinal+splt+"\n";	
}
}
else if(checkCondition.equals("<=")) {
String spltPrm=splt1.replaceAll("\\s+","");
String splt1Value=spltPrm.substring(spltPrm.lastIndexOf(":")+1).trim();
if(Double.parseDouble(splt1Value)<=Double.parseDouble((checkValue))) {
resultFinal=resultFinal+splt+"\n";	
}
}
else if(checkCondition.equals("!=")) {
String spltPrm=splt1.replaceAll("\\s+","");	
String splt1Value=spltPrm.substring(spltPrm.lastIndexOf(":")+1).trim();
if(!splt1Value.equalsIgnoreCase((checkValue))) {
resultFinal=resultFinal+splt+"\n";	
}
}
}
}

}
}
}

if(resultFinal.length()>0) {
String response="Hostname:"+hostname.get(i)+"; NE Name:"+elementname.get(i)+";"+resultFinal.replaceAll("\n","@NEXTLINE@");	
sbb_response.append(response+";;");
}
else {
String response="Hostname:"+hostname.get(i)+"; NE Name:"+elementname.get(i)+";"+"NA";	
sbb_response.append(response+";;");
}
	
	
/*	
ArrayList<String>par_in=new ArrayList<String>();
ArrayList<String>val_in=new ArrayList<String>();
ArrayList<String>cond_in=new ArrayList<String>();

ArrayList<String>pat_out=new ArrayList<String>();
ArrayList<String>par_out=new ArrayList<String>();
ArrayList<String>val_out=new ArrayList<String>();

ArrayList<String>par_final=new ArrayList<String>();
ArrayList<String>val_final=new ArrayList<String>();
ArrayList<String>mismatch_final=new ArrayList<String>();

for(int j=0;j<patterns.size();j++) {
par_in.add(patterns.get(j).replace("/","\\").trim());
val_in.add(values.get(j).toUpperCase().trim());
cond_in.add(conditions.get(j));
}

BufferedReader br=new BufferedReader(new StringReader(result));
String line="";
while((line=br.readLine())!=null) {
line=line.toString();	
for(int j=0;j<par_in.size();j++) {
String ptrn=par_in.get(j);

Pattern patt=Pattern.compile(ptrn);
Matcher mat=patt.matcher(line);

if(mat.matches()){
String mat1=mat.group();
String mat2=mat.group(1);
String mat3=mat1.substring(mat1.indexOf(mat2)+mat2.length());	
pat_out.add(ptrn);
par_out.add(mat2);
val_out.add(mat3);
}
}
}

for(int j=0;j<par_in.size();j++) {
for(int jj=0;jj<pat_out.size();jj++) {	
if(par_in.get(j).equals(pat_out.get(jj))){

if(cond_in.get(j).equals("=")) {
if(val_out.get(jj).toUpperCase().equals(val_in.get(j).toUpperCase())) {
par_final.add(par_out.get(jj));
val_final.add(val_in.get(j)+"~"+val_out.get(jj));
mismatch_final.add("NO");
}
if(!val_out.get(jj).toUpperCase().equals(val_in.get(j).toUpperCase())) {
par_final.add(par_out.get(jj));
val_final.add(val_in.get(j)+"~"+val_out.get(jj));
mismatch_final.add("YES");
}
}
	
if(cond_in.get(j).equals("!=")) {
if(val_out.get(jj).toUpperCase().equals(val_in.get(j).toUpperCase())) {
par_final.add(par_out.get(jj));
val_final.add(val_in.get(j)+"~"+val_out.get(jj));
mismatch_final.add("NO");
}
if(!val_out.get(jj).toUpperCase().equals(val_in.get(j).toUpperCase())) {
par_final.add(par_out.get(jj));
val_final.add(val_in.get(j)+"~"+val_out.get(jj));
mismatch_final.add("YES");
}
}

if(cond_in.get(j).equals("<")) {
if(Double.parseDouble(val_out.get(jj))<Double.parseDouble(val_in.get(j))) {
par_final.add(par_out.get(jj));
val_final.add(val_in.get(j)+"~"+val_out.get(jj));
mismatch_final.add("NO");
}
else{
par_final.add(par_out.get(jj));
val_final.add(val_in.get(j)+"~"+val_out.get(jj));
mismatch_final.add("YES");
}
}

if(cond_in.get(j).equals("<=")) {
if(Double.parseDouble(val_out.get(jj))<=Double.parseDouble(val_in.get(j))) {
par_final.add(par_out.get(jj));
val_final.add(val_in.get(j)+"~"+val_out.get(jj));
mismatch_final.add("NO");
}
else{
par_final.add(par_out.get(jj));
val_final.add(val_in.get(j)+"~"+val_out.get(jj));
mismatch_final.add("YES");
}
}
if(cond_in.get(j).equals(">")) {
if(Double.parseDouble(val_out.get(jj))>Double.parseDouble(val_in.get(j))) {
par_final.add(par_out.get(jj));
val_final.add(val_in.get(j)+"~"+val_out.get(jj));
mismatch_final.add("NO");
}
else{
par_final.add(par_out.get(jj));
val_final.add(val_in.get(j)+"~"+val_out.get(jj));
mismatch_final.add("YES");
}
}
if(cond_in.get(j).equals(">=")) {
if(Double.parseDouble(val_out.get(jj))>=Double.parseDouble(val_in.get(j))) {
par_final.add(par_out.get(jj));
val_final.add(val_in.get(j)+"~"+val_out.get(jj));
mismatch_final.add("NO");
}
else{
par_final.add(par_out.get(jj));
val_final.add(val_in.get(j)+"~"+val_out.get(jj));
mismatch_final.add("YES");
}
}

}
}
	
}


for(int j=0;j<par_final.size();j++) {
String param=par_final.get(j).replace(".", "@DOT@").replace("\\", "@BACKWARDSLASH@").replace("/", "@FORWARDSLASH@");	
String val=val_final.get(j).replace(".", "@DOT@").replace("\\", "@BACKWARDSLASH@").replace("/", "@FORWARDSLASH@");	
	
sbb_response.append("RING:"+ring.get(i)+","+"ELEMENTNAME:"+elementname.get(i)+","+"HOSTNAME:"+hostname.get(i)+","+"PATTERN:"+param+","+"VALUE:"+val+","+"MISMATCH:"+mismatch_final.get(j)+"@NEXTLINE@");
}
*/
}

if(!channel.isConnected()){
channel.disconnect();
}
}

}catch(Exception ex) {
String response="Hostname:"+hostname.get(i)+"; NE Name:"+elementname.get(i)+";"+"NA";	
sbb_response.append(response+";;");	
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
}


public Session getSession(String ipaddress,String username,String password) throws Exception{
JSch jsch = new JSch();
Session session = jsch.getSession(username, ipaddress, 22);
Properties config = new Properties();
config.put("StrictHostKeyChecking", "no");
session.setConfig(config);;
session.setPassword(password);
session.connect();

return session;
}

}
