package cn.edu.scu.jiangpeyton.rule;

import soot.util.ArraySet;

import java.util.Set;

public class API {
    public String name;
    public String provider;
    public String packageName;
    public boolean obfs;
    public String apiClass;
    public String apiMethod;
    public Set<String> hash = new ArraySet<>();
    public Key key;
}
