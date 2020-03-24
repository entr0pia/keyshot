package cn.edu.scu.jiangpeyton.keyshoot.graph;

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
    //public Set<String> strSet = new HashSet<>(); // apk中所有字符串
    public static Map<SootMethod, MethodLocal> CALLEE_MAP = new HashMap<SootMethod, MethodLocal>(); // 函数调用关系图
    public Map<SootClass, Set<String>> slicingStr = new HashMap<SootClass, Set<String>>(); // 基于切片的String集合

    public CalleeGraph() {

        //findAllString();
        for (SootClass sootClass : Scene.v().getClasses()) {
            Set<String> methodLocalS = new ArraySet<>();

            // 获取fields中的String变量
            methodLocalS.addAll(findClassString(sootClass));

            for (SootMethod sootMethod : sootClass.getMethods()) {
                MethodLocal methodLocal = new MethodLocal(sootMethod); // 绘制函数调用图--邻接表
                CALLEE_MAP.put(sootMethod, methodLocal);
                //strSet.addAll(methodsLocal.localStr); //添加Method中的局部变量的字符串
                methodLocalS.addAll(methodLocal.localStr);
            }

            this.slicingStr.put(sootClass, methodLocalS);
        }
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
