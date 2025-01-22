package org.xplorg.tool.telco360.DAO.implementation;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.stereotype.Repository;
import org.xplorg.tool.telco360.DAO.interfaces.TokenDAO;
import org.xplorg.tool.telco360.config.BaseDAOMongo;
import org.xplorg.tool.telco360.entity.GenerateToken;
import org.xplorg.tool.telco360.entity.GenericPostBody;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

@Repository("tokenDAO")
public class TokenDAOImpl extends BaseDAOMongo implements TokenDAO  {

Logger log = LogManager.getLogger(TokenDAOImpl.class.getName());
	
public int createUser(GenericPostBody genericPostBody) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into createUser ****************");	
}
try
{
Properties config=getProperties();	
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("token");
GenerateToken generateToken = new GenerateToken();
String tokenData[] = generateToken.createToken(genericPostBody.getCreateUserId(), "JavaTpoint", "JWT Token",null, 43200000);
//get Token.
String token = tokenData[0];
String secretkey=tokenData[1];
Document document = new Document("token_id", ""+(Integer.parseInt(genericPostBody.getCreateUserId())+1)).append("authenticationToken",token).append("email_id",genericPostBody.getCreateUserEmailId()).append("secretKey",secretkey).append("user_id",genericPostBody.getCreateUserId());
collection.insertOne(document);	
closeConnection(mongo);
return 1;
}
catch(Exception ex)
{
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
return 0;
}

public boolean updateToken(String email, String authenticationToken, String secretKey) {
//Session session = null;
if(log.isDebugEnabled()) {	
log.debug("*************** checked into updateToken ****************");	
}
int res=0;
boolean ret=false;
try
{
	
Properties config=getProperties();	
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("token");
UpdateResult updateResult = collection.updateOne(and(eq("email_id",email)),Updates.combine(Updates.set("authenticationToken",authenticationToken)
		,Updates.set("secretKey",secretKey)));
res=(int) updateResult.getModifiedCount();
if(res==1) {
ret=true;
}
else {
ret=false;
}
closeConnection(mongo);	

}
catch(Exception ex)
{
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
return false;
}
return ret;
}

public int getTokenDetail(String email) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into getTokenDetail ****************");	
}
//Session session = null;
int ret=0;
try
{
Properties config=getProperties();	
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("token");
ArrayList<Document> resultSet = collection.find(and(eq("email_id",email))).into(new ArrayList<Document>());	

if(resultSet.size()>0) {
for(Document docs:resultSet) {
ret=Integer.parseInt(docs.get("token_id").toString());	
}
}
else {
ret=0;	
}
closeConnection(mongo);

}
catch(Exception ex)
{
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
/*
* finally { session.flush(); }
*/
return ret;
}

public String getToken(String email) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into getToken ****************");	
}
//Session session = null;
String ret="";
try
{
Properties config=getProperties();	
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("token");
ArrayList<Document> resultSet = collection.find(and(eq("email_id",email))).into(new ArrayList<Document>());	

if(resultSet.size()>0) {
for(Document docs:resultSet) {
ret=docs.get("authenticationToken").toString();	
}
}
else {
ret="0";	
}
closeConnection(mongo);

}
catch(Exception ex)
{
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}
/*
* finally { session.flush(); }
*/
return ret;
}

public int tokenAuthentication(String token, int emailId) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into tokenAuthentication ****************");	
}
//Session session = null;
int ret=0;
try
{
	
Properties config=getProperties();	
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("token");
ArrayList<Document> resultSet = collection.find(and(eq("user_id",""+emailId),eq("authenticationToken",token))).into(new ArrayList<Document>());	

if(resultSet.size()>0) {
for(Document docs:resultSet) {
ret=Integer.parseInt(docs.get("token_id").toString());	
}
}
else {
ret=0;	
}
closeConnection(mongo);
}
catch(Exception ex)
{
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}

return ret;
}

}
