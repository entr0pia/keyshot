package cn.edu.scu.jiangpeyton.graph;

import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.tagkit.StringConstantValueTag;
import soot.tagkit.Tag;
import soot.util.ArraySet;

import java.util.*;

public class CalleeGraph {
    private String strTypeName = "java.lang.String";
    //public CallGraph callGraph;
    public Set<String> strSet; // apk中所有字符串
    public Map<SootMethod, MethodsLocal> calleeMap; // 函数调用关系图

    public CalleeGraph(String apk) {
        this.strSet = new HashSet<>();
        this.calleeMap = new HashMap<SootMethod, MethodsLocal>();

        findAllString();
        for (SootClass sootClass : Scene.v().getClasses()) {
            for (SootMethod sootMethod : sootClass.getMethods()) {
                MethodsLocal methodsLocal = new MethodsLocal(sootMethod); // 绘制函数调用图--邻接表
                calleeMap.put(sootMethod, methodsLocal);
                strSet.addAll(methodsLocal.localStr); //添加Method中的局部变量的字符串
            }
        }
    }

    public Set<String> findAllString() {
        /**
         * 查找所有Class中的字符串
         */
        for (SootClass sootClass : Scene.v().getClasses()) {
            this.strSet.addAll(findClassString(sootClass));
        }
        return strSet;
    }

    public Set<String> findClassString(SootClass sootClass) {
        /**
         * 查找当前Class中的成员变量的字符串
         */
        Set<String> subSet = new ArraySet<>();

        for (SootField field : sootClass.getFields()) {
            if (field.getType().toString().equals(this.strTypeName)) {
                for (Tag tag : field.getTags()) {
                    try {
                        // 选取字符常量
                        StringConstantValueTag tmp = (StringConstantValueTag) tag;
                        subSet.add(tmp.getStringValue());
                    } catch (ClassCastException e) {
                        //System.out.println(e);
                    }
                }
            }
        }
        return subSet;
    }

}
