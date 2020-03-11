package cn.edu.scu.jiangpeyton.caclhash;

import cn.edu.scu.jiangpeyton.graph.MethodsLocal;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

import java.util.HashMap;
import java.util.Map;

public class ClassHashMap {
    public Map<String, SootClass> hashMap=new HashMap<String,SootClass>();

    public ClassHashMap(Map<SootMethod, MethodsLocal> calleeMap){
        for(SootClass sootClass: Scene.v().getClasses()){
            this.hashMap.put(new ClassHash(sootClass,calleeMap).getHash(),sootClass);
        }
    }
}
