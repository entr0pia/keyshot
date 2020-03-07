package cn.edu.scu.jiangpeyton.graph;

import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.tagkit.StringConstantValueTag;
import soot.tagkit.Tag;

import java.util.*;

public class CalleeGraph {
    private String strTypeName = "java.lang.String";
    public CallGraph callGraph;
    public Set<String> strSet;
    public Map<SootMethod, MethodsLocal> calleeMap;

    public CalleeGraph(String apk) {
        this.strSet = new HashSet<>();

        calleeMap = new HashMap<SootMethod, MethodsLocal>();
        for (SootClass sootClass : Scene.v().getClasses()) {
            for (SootMethod sootMethod : sootClass.getMethods()) {
                MethodsLocal methodsLocal = new MethodsLocal(sootMethod);
                calleeMap.put(sootMethod, methodsLocal);
                strSet.addAll(methodsLocal.localStr);
            }
        }


        callGraph = Scene.v().getCallGraph();

    }


    public void sigMethod(SootMethod sootMethod) {
        Iterator<MethodOrMethodContext> clees = new Targets(callGraph.edgesInto(sootMethod));
        if (clees != null) {
            while (clees.hasNext()) {
                SootMethod clee = (SootMethod) clees.next();
                System.out.println(clee.toString());
            }
        }
    }

    public Set<String> findAllString() {
        for (SootClass sootClass : Scene.v().getClasses()) {
            this.strSet.addAll(findClassString(sootClass));
        }
        return strSet;
    }

    public List<String> findClassString(SootClass sootClass) {
        List<String> list = new ArrayList<>();

        for (SootField field : sootClass.getFields()) {
            if (field.getType().toString().equals(this.strTypeName)) {
                for (Tag tag : field.getTags()) {
                    try {
                        StringConstantValueTag tmp = (StringConstantValueTag) tag;
                        list.add(tmp.getStringValue());
                    } catch (ClassCastException e) {
                        //System.out.println(e);
                    }
                }
            }
        }

        for (SootMethod sootMethod : sootClass.getMethods()) {
            list.addAll(findMethodString(sootMethod));
        }
        return list;
    }

    public List<String> findMethodString(SootMethod method) {
        List<String> list = new ArrayList<>();
        // 待完成, 通过ActivityBody获得
        return list;
    }


}
