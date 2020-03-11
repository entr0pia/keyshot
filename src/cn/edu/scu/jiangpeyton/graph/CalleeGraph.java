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
    public Set<String> strSet;
    public Map<SootMethod, MethodsLocal> calleeMap;

    public CalleeGraph(String apk) {
        this.strSet = new HashSet<>();

        findAllString();
        calleeMap = new HashMap<SootMethod, MethodsLocal>();
        for (SootClass sootClass : Scene.v().getClasses()) {
            for (SootMethod sootMethod : sootClass.getMethods()) {
                MethodsLocal methodsLocal = new MethodsLocal(sootMethod);
                calleeMap.put(sootMethod, methodsLocal);
                strSet.addAll(methodsLocal.localStr);
            }
        }


        //callGraph = Scene.v().getCallGraph();

    }


    /*public void sigMethod(SootMethod sootMethod) {
        Iterator<MethodOrMethodContext> clees = new Targets(callGraph.edgesInto(sootMethod));
        if (clees != null) {
            while (clees.hasNext()) {
                SootMethod clee = (SootMethod) clees.next();
                System.out.println(clee.toString());
            }
        }
    }*/

    public Set<String> findAllString() {
        for (SootClass sootClass : Scene.v().getClasses()) {
            this.strSet.addAll(findClassString(sootClass));
        }
        return strSet;
    }

    public Set<String> findClassString(SootClass sootClass) {
        Set<String> subSet = new ArraySet<>();

        for (SootField field : sootClass.getFields()) {
            if (field.getType().toString().equals(this.strTypeName)) {
                for (Tag tag : field.getTags()) {
                    try {
                        StringConstantValueTag tmp = (StringConstantValueTag) tag;
                        subSet.add(tmp.getStringValue());
                    } catch (ClassCastException e) {
                        //System.out.println(e);
                    }
                }
            }
        }
        return  subSet;
    }

}
