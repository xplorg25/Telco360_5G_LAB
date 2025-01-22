package org.xplorg.tool.telco360.entity;

public class StringInt {
public String problem;
public Integer count;
public StringInt(String prob,Integer cnt){
this.count=cnt;
this.problem=prob;
}
@Override
public String toString() {
return "StringInt [problem=" + problem + ", count=" + count + "]";
}
}

