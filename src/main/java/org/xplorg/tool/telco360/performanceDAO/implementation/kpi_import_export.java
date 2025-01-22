package org.xplorg.tool.telco360.performanceDAO.implementation;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class kpi_import_export {
	
public int start_process(String opco,String vendor,String domain,String admin_id,MongoDatabase db,String path) {	

ArrayList < String > kpi_from_database = new ArrayList < String > ();

MongoCollection < Document > collection = db.getCollection("kpi_formula");
DistinctIterable < String > dist = collection.distinct("kpi_name", String.class);

for (String value: dist) {
kpi_from_database.add(value);
}

int count = 0;

try {
BufferedReader br = new BufferedReader(new FileReader(new File(path)));
String st;

ArrayList < String > columns = new ArrayList < String > ();

while ((st = br.readLine()) != null) {
String st2 = st.trim();

if (count == 0) {

columns = string_to_array(st2, path);

} else {
ArrayList < String > values = string_to_array(st2, path);

////System.out.println(values);

String tool_kpi_name = values.get(0);

//	//System.out.println(tool_kpi_name);

if (!kpi_from_database.contains(tool_kpi_name)) {
	
insert_new_kpis(opco,vendor,domain,admin_id, values,db);

} else {

insert_old_kpis(values,db);
}

//insert_mongodb(db,columns,values,table.toLowerCase());	
}

count++;
}

br.close();

} catch (FileNotFoundException e) {

e.printStackTrace();
} catch (IOException e) {

e.printStackTrace();
}
return 1;

}
 static ArrayList < String > string_to_array(String input, String file) {
ArrayList < String > output = new ArrayList < String > ();

String split[] = input.split(",");
int i = 0;
for (String cut: split) {

output.add(cut.trim());

i++;
}

return output;

}


public static void insert_old_kpis(ArrayList<String> list,MongoDatabase database) {
	
MongoCollection < Document > collection = database.getCollection("kpi_formula");
String actual_formula=table_name_with_column_name((list.get(1)), database);;
try {
collection.updateMany(
	//Filters.and(eq("kpi_name", string_replace(kpiName)), eq("admin_id", admin_id), eq("groups", groupName)),
Filters.and(eq("kpi_name", list.get(0))),
Updates.combine(
Updates.set("kpi_formula", actual_formula),
Updates.set("formula", list.get(1)),
Updates.set("threshold", list.get(2)),
Updates.set("link_to_topology", list.get(3)),
Updates.set("rate", list.get(4)),
Updates.set("severity", list.get(5)),
Updates.set("troubleticket",list.get(6)),
Updates.set("correlation", "no")
));
} catch (Exception e) {
e.printStackTrace();
}
	
	
}


//TODO to get column name corresponding to perticular table
public static String table_name_with_column_name(String formula,MongoDatabase db)  {

MongoCollection < Document > col = db.getCollection("table_column_relation");

String output="";
	
StringBuilder sb=new StringBuilder();
String formula_spliter=formula.replace("(",",(,").replace(")",",),").replace("+",",+,").replace("*", ",*,").replace("-",",-,").replace("/",",/,");

String splt_formula[]=formula_spliter.toString().split(",");
for(String spls:splt_formula){
		

String sng=spls;
if(spls.length()>1||spls.matches(".*\\d.*")){

String s_type="";

if(spls.length()<8&&spls.matches(".*\\d.*")) {
	s_type=spls;
	
}
else {

	DistinctIterable <String> val=col.distinct("table_name",and(eq("column_name",spls)),String.class );
String tb_name="";
for(String value:val) {
	 tb_name=value;	
}

s_type=tb_name+":"+spls;
	
	}

sng=s_type;


	}

	sb.append(sng);

	}	
	output=sb.toString();
	return output;
	
	
	
	
}

public static void insert_new_kpis(String opco,String vendor,String domain,String admin_id, ArrayList<String> list,MongoDatabase database) {

ArrayList < String > column_names = new ArrayList < String > ();
ArrayList < String > column_values = new ArrayList < String > ();
column_names.add("opco");
column_names.add("admin_id");
column_names.add("kpi_name");
column_names.add("groups");
column_names.add("kpi_formula");
column_names.add("formula");
column_names.add("threshold");
column_names.add("link_to_topology");
column_names.add("rate");
column_names.add("element_name");
column_names.add("calculation_type");
column_names.add("severity");
column_names.add("troubleticket");
column_names.add("unit");
column_names.add("correlation");

if (domain.equals("IPRAN") || domain.equals("IPBB")) {

String actual_formula=table_name_with_column_name((list.get(1)), database);;

//System.out.println(actual_formula);

//String actual_formula = table_name_with_column_name(formula, database);
column_values.add(opco);
column_values.add(admin_id);
column_values.add(list.get(0));
column_values.add("KPIs");
column_values.add(actual_formula);
column_values.add(list.get(1));
column_values.add(list.get(2));
column_values.add(list.get(3));
column_values.add(list.get(4));
column_values.add(domain);
column_values.add("sum");
column_values.add(list.get(5)); //severity
column_values.add(list.get(6));
column_values.add("Absolute"); //unit
column_values.add("no"); //unit

insert_mongodb(database, column_names, column_values, "kpi_formula");

}
	
	
}


//TODO insert into mongodb
public static void insert_mongodb(MongoDatabase db,ArrayList < String > columns, ArrayList < String > values, String table_name) {

MongoCollection < Document > col = db.getCollection(table_name);
Document x = new Document();
	  //////System.out.println(table_name + columns.size());
for (int i = 0; i < columns.size(); i++) {
  try {
    x.append(columns.get(i), values.get(i).trim().replace("\"", "").toString());
} catch (Exception e) {

// e.printStackTrace();
  }
}
col.insertOne(x);

}	

}
