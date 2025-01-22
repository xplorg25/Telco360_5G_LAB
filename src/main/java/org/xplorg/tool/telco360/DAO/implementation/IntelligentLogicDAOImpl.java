package org.xplorg.tool.telco360.DAO.implementation;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.descending;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.xplorg.tool.telco360.DAO.interfaces.IntelligentLogicDAO;
import org.xplorg.tool.telco360.config.BaseDAOMongo;
import org.xplorg.tool.telco360.entity.TableHeader;
import org.xplorg.tool.telco360.entity.il_task_generic;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Repository("intelligentLogicDAO")
public class IntelligentLogicDAOImpl extends BaseDAOMongo implements IntelligentLogicDAO {

    Logger log = LogManager.getLogger(IntelligentLogicDAOImpl.class.getName());

    public String getIslDetails(String tableName, String columns, String conditions, String orderby) {
        if (log.isDebugEnabled()) {
            log.debug("*************** checked into getIslDetails ****************");
        }

        try {
            System.out.println(tableName + "  -->" + columns + "  -->" + conditions + "  -->" + orderby);
            ArrayList<Bson> fltr = new ArrayList<Bson>();
            Bson filter = null;
            if (conditions.length() > 1) {
                String cond = conditions.replace("@DOT@", ".").replace("@SEMICOLON@", ";");
                if (cond.contains(" and ")) {
                    String cond_spls[] = cond.split(" and ");
                    for (String cv : cond_spls) {
                        if (cv.contains("=") && !cv.contains("like") && !cv.contains("between") && !cv.contains(" or ")) {
                            String col = cv.substring(0, cv.indexOf("=")).trim();
                            String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
                            fltr.add(eq(col, val));
                        } else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between") && !cv.contains(" or ")) {
                            String col = cv.substring(0, cv.indexOf("like")).trim();
                            String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "").replace("@", "").replace("%", "").trim();
                            fltr.add(eq(col, java.util.regex.Pattern.compile("^.*" + val + ".*")));
                        } else if (cv.contains("=") && cv.contains("between")) {
                            String col = cv.substring(0, cv.indexOf("between")).trim();
                            String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "").trim();
                            String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
                            fltr.add(gte(col, val1));
                            fltr.add(lte(col, val2));
                        } else if (cv.contains("=") && cv.contains(" or ") && !cv.contains("like") && !cv.contains("between")) {
                            String or[] = cv.split(" or ");
                            ArrayList<Bson> fltr_or = new ArrayList<Bson>();
                            for (String splt_or : or) {
                                String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
                                String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
                                fltr_or.add(eq(col, val));
                            }
                            fltr.add(or(fltr_or));
                        }
                    }
                } else if (cond.contains(" AND ")) {
                    String cond_spls[] = cond.split(" AND ");
                    for (String cv : cond_spls) {
                        if (cv.contains("=") && !cv.contains("like") && !cv.contains("between") && !cv.contains(" OR ")) {
                            String col = cv.substring(0, cv.indexOf("=")).trim();
                            String val = cv.substring(cv.indexOf("=") + 1).trim().replace("'", "");
                            fltr.add(eq(col, val));
                        } else if (!cv.contains("=") && cv.contains("like") && !cv.contains("between") && !cv.contains(" OR ")) {
                            String col = cv.substring(0, cv.indexOf("like")).trim();
                            String val = cv.substring(cv.indexOf("(") + 1, cv.indexOf(")")).replace("'", "").replace("@", "").replace("%", "").trim();
                            fltr.add(eq(col, java.util.regex.Pattern.compile("^.*" + val + ".*")));
                        } else if (cv.contains("=") && cv.contains("between")) {
                            String col = cv.substring(0, cv.indexOf("between")).trim();
                            String val1 = cv.substring(cv.indexOf("FROM=") + 5, cv.indexOf("TO=")).replace("'", "").trim();
                            String val2 = cv.substring(cv.indexOf("TO=") + 3).replace("'", "").trim();
                            fltr.add(gte(col, val1));
                            fltr.add(lte(col, val2));
                        } else if (cv.contains("=") && cv.contains(" OR ") && !cv.contains("like") && !cv.contains("between")) {
                            String or[] = cv.split(" or ");
                            ArrayList<Bson> fltr_or = new ArrayList<Bson>();
                            for (String splt_or : or) {
                                String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
                                String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
                                fltr_or.add(eq(col, val));
                            }
                            fltr.add(or(fltr_or));
                        }

                    }
                } else if (!(cond.contains(" AND ") && !cond.contains(" and ")) && cond.length() > 1) {
                    if (cond.contains("=") && cond.contains("between") && !cond.contains(" or ") && !cond.contains(" OR ")) {
                        String col = cond.substring(0, cond.indexOf("between")).trim();
                        String val1 = cond.substring(cond.indexOf("FROM=") + 5, cond.indexOf("TO=")).replace("'", "").trim();
                        String val2 = cond.substring(cond.indexOf("TO=") + 3).replace("'", "").trim();
                        fltr.add(gte(col, val1));
                        fltr.add(lte(col, val2));
                    } else if (cond.contains(" or ") && !cond.contains("like") && !cond.contains("between")) {
                        String or[] = cond.split(" or ");
                        ArrayList<Bson> fltr_or = new ArrayList<Bson>();
                        for (String splt_or : or) {
                            String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
                            String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
                            fltr_or.add(eq(col, val));
                        }
                        fltr.add(or(fltr_or));
                    } else if (cond.contains(" OR ") && !cond.contains("like") && !cond.contains("between")) {
                        String or[] = cond.split(" OR ");
                        ArrayList<Bson> fltr_or = new ArrayList<Bson>();
                        for (String splt_or : or) {
                            String col = splt_or.substring(0, splt_or.indexOf("=")).trim();
                            String val = splt_or.substring(splt_or.indexOf("=") + 1).trim().replace("'", "");
                            fltr_or.add(eq(col, val));
                        }
                        fltr.add(or(fltr_or));
                    } else if (cond.contains("=")) {
                        String col = cond.substring(0, cond.indexOf("=")).trim();
                        String val = cond.substring(cond.indexOf("=") + 1).trim().replace("'", "");
                        fltr.add(eq(col, val));
                    }
                }
                filter = and(fltr);
            }
            Properties config = getProperties();
            MongoClient mongo = getConnection();
            MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
            MongoCollection<Document> collection = database.getCollection(tableName);
            ArrayList<String> cls = new ArrayList<String>();
            ArrayList<Document> resultSet = null;
            ArrayList<TableHeader> cols = new ArrayList<TableHeader>();
            JSONArray vals = new JSONArray();

            Map<String, Object> groupMap = new HashMap<String, Object>();

            if (!columns.equals("*")) {
                if (columns.contains(",")) {
                    String columns_spls[] = columns.split(",");
                    for (String colm : columns_spls) {
                        cls.add(colm);
                        groupMap.put(colm, "$" + colm);
                    }
                } else {
                    cls.add(columns);
                    groupMap.put(columns, "$" + columns);
                }

                DBObject groupFields = new BasicDBObject(groupMap);
                if (filter != null) {
                    resultSet = collection.aggregate(Arrays.asList(match(filter), group(groupFields), project(fields(include(cls))), sort(descending("_id." + orderby)), limit(5000))).into(new ArrayList<Document>());
                    System.out.println("filter" + filter );
                    System.out.println("resultset" + resultSet);

                } else {
                    resultSet = collection.aggregate(Arrays.asList(group(groupFields), project(fields(include(cls))), sort(descending("_id." + orderby)), limit(5000))).into(new ArrayList<Document>());
                    System.out.println("resultsetelse" + resultSet);
                }
                String cols_spls[] = columns.split(",");
                for (String col : cols_spls) {
                    String colm = col;
                    TableHeader th = new TableHeader(colm, colm);
                    cols.add(th);
                }

                for (Document docs : resultSet) {
                    JSONObject colval = new JSONObject();
                    Document doc = (Document) docs.get("_id");
                    for (int j = 0; j < cls.size(); j++) {
                        if (doc.containsKey(cls.get(j))) {
                            if (doc.get(cls.get(j)).toString().length() > 0 && doc.get(cls.get(j)).toString() != null) {
                                colval.put(cls.get(j), doc.get(cls.get(j)));
                                System.out.println("colval" + colval);
                            } else {
                                colval.put(cls.get(j), "-");
                            }
                        } else {
                            colval.put(cls.get(j), "-");
                        }
                    }
//JSONObject obj=new JSONObject(object);
//System.out.println("obj======="+obj);
/*String substr=object.substring(object.indexOf("{{")+2,object.indexOf("}}")).replace(", ", " ");
String spls_cols[]=substr.split(",");
for(String cv:spls_cols) {
String cl=cv.substring(0,cv.indexOf("=")).trim();
String vl=cv.substring(cv.indexOf("=")+1).trim().replace(",", "@COMMA@").replace("/","@FORWARDSLASH@").replace("\\","@BACKWARDSLASH@").replace("\"","");//.replace("@COMMA@", "-").replace("@@",".").replace("\"","").replace("\"","").replace("/","-").replaceAll("[^a-zA-Z0-9\\s+*.#@+-:-_]","")
colval.put(cl, vl);	
}*/
                    if (!vals.similar(colval)) {
                        vals.put(colval);
                    }
                }
            } else {
                if (filter != null) {
                    resultSet = collection.find(filter).limit(5000).into(new ArrayList<Document>());
                } else {
                    resultSet = collection.find().limit(5000).into(new ArrayList<Document>());
                }

                int size = 0;
                int max = 0;
                Document doc = null;
                for (int i = 0; i < resultSet.size(); i++) {
                    Document document = resultSet.get(i);
                    size = document.keySet().size();
                    if (size > max) {
                        max = size;
                        doc = document;
                    }
                }
                Iterator<String> itr = doc.keySet().iterator();
                while (itr.hasNext()) {
                    String col = itr.next().toString();
                    if (!col.equals("_id")) {
                        cls.add(col);
                        TableHeader th = new TableHeader(col, col);
                        cols.add(th);
                    }
                }
                for (Document docs : resultSet) {
                    JSONObject colval = new JSONObject();
                    for (int j = 0; j < cls.size(); j++) {
                        if (docs.containsKey(cls.get(j))) {
                            if (docs.get(cls.get(j)).toString().length() > 0 && docs.get(cls.get(j)).toString() != null) {
                                colval.put(cls.get(j), docs.get(cls.get(j)));
                            } else {
                                colval.put(cls.get(j), "-");
                            }
                        } else {
                            colval.put(cls.get(j), "-");
                        }
                    }
                    if (!vals.similar(colval)) {
                        vals.put(colval);
                    }
                }


            }

            JSONArray jsonArrayFinal = new JSONArray();
            JSONObject jsonObjectColVal = new JSONObject();
            jsonObjectColVal.put("cols", cols);
            jsonObjectColVal.put("vals", vals);
            jsonArrayFinal.put(jsonObjectColVal);
            closeConnection(mongo);
            String output = jsonArrayFinal.toString();
            System.out.println("INTELLIGENT_LOGIC_DAO_IMPL_OUTPUT" + output);
            return output;

        } catch (Exception ex) {
            log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
        }
        return null;
    }


    @Override
    public String getIntelligentLogicUseCase(String protocol, String ucase, String command, String condition, String pattern, String value, String outputType) {
        if (log.isDebugEnabled()) {
            log.debug("*************** checked into getIntelligentLogicUseCase ****************");
        }
        String result = "";
        try {
            Properties config = getProperties();

            String local_hostname = config.getProperty("server.hostname");
            String local_username = config.getProperty("server.username");
            String local_password = config.getProperty("server.password");

            ArrayList<String> elementname = new ArrayList<String>();
            ArrayList<String> hostname = new ArrayList<String>();
            ArrayList<String> username = new ArrayList<String>();
            ArrayList<String> password = new ArrayList<String>();
            ArrayList<String> ring = new ArrayList<String>();

            ArrayList<String> thread_status = new ArrayList<String>();

            String tableName = "logincredentialsofelements";
            ArrayList<Bson> fltr = new ArrayList<Bson>();
            Bson filter = null;
            if (condition.length() > 1) {
                String cond = condition.replace("@DOT@", ".").replace("@SLASH@", "/");
                if (cond.contains(" and ")) {
                    String cond_spls[] = cond.split("and");
                    for (String cond_or : cond_spls) {
                        ArrayList<Bson> fltr_or = new ArrayList<Bson>();
                        if (cond_or.contains("or")) {
                            String cond_splsor[] = cond_or.split("or");
                            for (String cv : cond_splsor) {
                                if (cv.contains("=")) {
                                    String col = cv.split("=")[0].trim();
                                    String val = cv.split("=")[1].trim().replace("'", "");
                                    ;
                                    fltr_or.add(eq(col, val));
                                }
                            }
                            fltr.add(or(fltr_or));
                        } else if (cond_or.contains("=")) {
                            String col = cond_or.split("=")[0].trim();
                            String val = cond_or.split("=")[1].trim().replace("'", "");
                            ;
                            fltr.add(eq(col, val));
                        }
                    }
                    filter = and(fltr);
                } else if (cond.contains(" AND ")) {
                    String cond_spls[] = cond.split("AND");
                    for (String cond_or : cond_spls) {
                        ArrayList<Bson> fltr_or = new ArrayList<Bson>();
                        if (cond_or.contains("OR")) {
                            String cond_splsor[] = cond_or.split("OR");
                            for (String cv : cond_splsor) {
                                if (cv.contains("=")) {
                                    String col = cv.split("=")[0].trim();
                                    String val = cv.split("=")[1].trim().replace("'", "");
                                    ;
                                    fltr_or.add(eq(col, val));
                                }
                            }
                            fltr.add(or(fltr_or));
                        } else if (cond_or.contains("=")) {
                            String col = cond_or.split("=")[0].trim();
                            String val = cond_or.split("=")[1].trim().replace("'", "");
                            ;
                            fltr.add(eq(col, val));
                        }
                    }
                    filter = and(fltr);
                } else if (!cond.contains(" AND ") && !cond.contains(" and ")) {
                    if (cond.contains("OR")) {
                        ArrayList<Bson> fltr_or = new ArrayList<Bson>();
                        String cond_splsor[] = cond.split("OR");
                        for (String cv : cond_splsor) {
                            if (cv.contains("=")) {
                                String col = cond.split("=")[0].trim();
                                String val = cond.split("=")[1].trim().replace("'", "");
                                ;
                                fltr_or.add(eq(col, val));
                            }
                        }
                        fltr.add(or(fltr_or));
                    } else if (cond.contains("or")) {
                        ArrayList<Bson> fltr_or = new ArrayList<Bson>();
                        String cond_splsor[] = cond.split("or");
                        for (String cv : cond_splsor) {
                            if (cv.contains("=")) {
                                String col = cond.split("=")[0].trim();
                                String val = cond.split("=")[1].trim().replace("'", "");
                                ;
                                fltr_or.add(eq(col, val));
                            }
                        }
                        fltr.add(or(fltr_or));
                    } else if (cond.contains("=")) {
                        String col = cond.split("=")[0].trim();
                        String val = cond.split("=")[1].trim().replace("'", "");
                        ;
                        fltr.add(eq(col, val));
                    }

                    filter = and(fltr);
                }
            }

            MongoClient mongo = getConnection();
            MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
            MongoCollection<Document> collection = database.getCollection(tableName);
            ArrayList<Document> resultSet = null;
            if (filter != null) {
                resultSet = collection.find(filter).into(new ArrayList<Document>());
            } else {
                resultSet = collection.find().into(new ArrayList<Document>());
            }

            for (Document docs : resultSet) {
                if (!elementname.contains(docs.getString("ElementName")) && !hostname.contains(docs.getString("Hostname"))) {
                    elementname.add(docs.getString("ElementName"));
                    hostname.add(docs.getString("Hostname"));
                    username.add(docs.getString("Username"));
                    password.add(docs.getString("Password"));
                    ring.add(docs.getString("Ring"));
                }
            }
            closeConnection(mongo);

            String usecase = ucase.replace("@SLASH@", "/");

            if (usecase.toUpperCase().equals("OTHERS")) {

                if (protocol.toUpperCase().equals("SSH") || protocol.toUpperCase().equals("SNMP")) {
                    ArrayList<String> commands = new ArrayList<String>();

                    String cmd[] = command.replace("@SEMICOLON@", ";").split(";");

                    for (String cmnd : cmd) {
                        commands.add(cmnd);
                    }
                    ArrayList<String> patterns = new ArrayList<String>();
                    ArrayList<String> values = new ArrayList<String>();
                    ArrayList<String> conditions = new ArrayList<String>();
                    String pat = pattern.replace("@FORWARDSLASH@", "/").replace("@BACKWARDSLASH@", "\\").replace("@DOT@", ".").replace("@SEMICOLON@", ";")
                            .replace("@BRACESOPEN@", "(").replace("@BRACESCLOSE@", ")").replaceAll("@SQUAREBRACKETOPEN@", "[").replaceAll("@SQUAREBRACKETCLOSE@", "]");
                    String patn[] = pat.split(";");

                    for (String pt : patn) {
                        try {
                            patterns.add(".*\\b" + pt + "\\b.*");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    String vals[] = value.replace("@BRACESOPEN@", "(").replace("@BRACESCLOSE@", ")").replaceAll("@SEMICOLON@", ";").split(";");

                    for (String vl : vals) {
                        try {
                            String val = vl.replaceAll("\\s+", "");
                            values.add(val.substring(val.indexOf("(") + 1, val.lastIndexOf(")")).trim());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    int session_count = 15;
                    int total = hostname.size();
                    Thread thrd[];

                    int vl = 0;
                    int div = 0;
                    for (int i = total; i >= 0; i--) {
                        if (i % session_count == 0) {
                            div = i;
                            break;
                        }
                    }

                    vl = div / session_count;
                    int remaining = total - div;

                    int a = 0;
                    int b = vl;
                    int val = 0;
                    thrd = new Thread[b + 1];
                    StringBuilder sbb_response = new StringBuilder();
                    for (int i = a; i < b; i++) {
                        thrd[i] = new il_task_generic(sbb_response, protocol, local_hostname, local_username, local_password, val, (val + session_count), elementname, hostname, username, password, ring, commands, patterns, values, conditions, pat, value, outputType);
                        thrd[i].start();
                        val = val + session_count;
                        Thread.sleep(2000);
                    }

                    if (remaining > 0) {
                        thrd[b] = new il_task_generic(sbb_response, protocol, local_hostname, local_username, local_password, div, (div + remaining), elementname, hostname, username, password, ring, commands, patterns, values, conditions, pat, value, outputType);
                        thrd[b].start();
                        Thread.sleep(2000);
                    }

                    while (true) {
                        for (int i = 0; i < thrd.length; i++) {
                            if (!thrd[i].isAlive() && thread_status.indexOf("THREAD-" + i) < 0) {
                                thread_status.add("THREAD-" + i);
                            }
                        }
                        if (thread_status.size() == thrd.length) {
                            result = "\"" + sbb_response.toString() + "\"";
                            System.out.println("Thread Status==========" + thread_status.size());
                            System.out.println("Output==========" + sbb_response);

                            sbb_response.delete(0, sbb_response.length());
                            break;
                        }
                    }

                }

            }

        } catch (Exception ex) {
            log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
        }
        return result;
    }

    public int uploadTextDataGeneric(MultipartFile file, String data) {
        if (log.isDebugEnabled()) {
            log.debug("*************** checked into uploadTextDataGeneric ****************");
        }

        Properties config = getProperties();
        Path rootLocation = Paths.get(config.getProperty("server.directory"));
        String file_store = file.getOriginalFilename();
        File file_check = new File(rootLocation + "/" + file_store);
        try {
            if (file_check.exists()) {
                Files.delete(rootLocation.resolve(file_store));
                Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
            } else {
                Files.copy(file.getInputStream(), rootLocation.resolve(file_store));
            }
        } catch (Exception ex) {
            log.error("Exception occurs:----" + ex.getMessage(), ex);
//ex.printStackTrace();
        }
        return 1;
    }


    public Session getSession(String ipaddress, String username, String password) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, ipaddress, 22);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        ;
        session.setPassword(password);
        session.connect();

        return session;
    }


}
