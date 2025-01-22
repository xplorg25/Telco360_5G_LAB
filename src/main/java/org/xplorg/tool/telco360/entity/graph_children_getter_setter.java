package org.xplorg.tool.telco360.entity;

import java.util.ArrayList;

public class graph_children_getter_setter {

String label;
ArrayList<String> data;
String fill;
String backgroundColor;
String borderColor;

public graph_children_getter_setter() {
	
}

public graph_children_getter_setter(String label, ArrayList<String> data, String fill, String backgroundColor,
String borderColor) {

this.label = label;
this.data = data;
this.fill = fill;
this.backgroundColor = backgroundColor;
this.borderColor = borderColor;
}

public String getLabel() {
return label;
}

public void setLabel(String label) {
this.label = label;
}

public ArrayList<String> getData() {
return data;
}

public void setData(ArrayList<String> data) {
this.data = data;
}

public String getFill() {
return fill;
}

public void setFill(String fill) {
this.fill = fill;
}

public String getBackgroundColor() {
return backgroundColor;
}

public void setBackgroundColor(String backgroundColor) {
this.backgroundColor = backgroundColor;
}

public String getBorderColor() {
return borderColor;
}

public void setBorderColor(String borderColor) {
this.borderColor = borderColor;
}


}
