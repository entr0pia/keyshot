package cn.edu.scu.jiangpeyton.keyshot.caclhash;

import cn.edu.scu.jiangpeyton.keyshot.graph.CalleeGraph;
import cn.edu.scu.jiangpeyton.keyshot.graph.MethodLocal;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MethodHash {
    private SootMethod localMethod;
    private MethodLocal methodLocalCallee;
    private StringBuilder localHashBuffer = new StringBuilder();

    public MethodHash(SootMethod method) throws StackOverflowError {
        if (!method.getDeclaringClass().isApplicationClass()) {
            localHashBuffer.append(method.getSignature());
            return;
        }
        this.localMethod = method;
        this.methodLocalCallee = CalleeGraph.CALLEE_MAP.get(method);
        int layer = StringUtils.countMatches(method.getDeclaringClass().getName(), '.');
        //this.localHash.append(layer);

        // 形参, 局部变量, 返回值
        List<Type> types = new ArrayList<>();
        types.addAll(methodLocalCallee.parameterTypes);
        types.addAll(methodLocalCallee.localVarTypes);
        types.add(methodLocalCallee.returnType);
        for (Type iType : types) {
            RefType refType;
            try {
                // 强制类型转换
                refType = (RefType) iType;
            } catch (ClassCastException e) {
                //e.printStackTrace();
                continue;
            }

            SootClass refClass = refType.getSootClass();
            while (refClass.hasSuperclass()) {
                refClass = refClass.getSuperclass();
            }

            // 若变量类型与声明当前method的Class相同, 则以THIS代替, 不然会无穷递归
            if (method.getDeclaringClass().getName().equals(refClass.getName())) {
                localHashBuffer.append("THIS");
                continue;
            }

            // 若已处理该变量的Class
            if (ClassHashMap.classHashMapRe.containsKey(refClass)) {
                localHashBuffer.append(ClassHashMap.classHashMapRe.get(refClass));
                continue;
            }

            if (refClass.isApplicationClass()) {
                // 若为非系统Class
                ClassHash classHash = new ClassHash(refClass);
                localHashBuffer.append(classHash.getHash());
                ClassHashMap.addClassMap(refClass, classHash.getHash());
            } else {
                String sha = SHA256(refClass.getName());
                localHashBuffer.append(sha);
                ClassHashMap.addClassMap(refClass, sha);
            }
        }

        //处理本地调用callee
        for (SootMethod calleeMethod : methodLocalCallee.callees) {
            SootClass declaringClass = calleeMethod.getDeclaringClass();
            // 若本地调用方法与声明当前method的Class相同, 以INVOKE代替
            if (method.getDeclaringClass().getClass().equals(declaringClass.getClass())) {
                this.localHashBuffer.append("INVOKE");
                continue;
            }
            if (declaringClass.isApplicationClass()) {
                // 若为非系统Class
                MethodHash iHash = new MethodHash(calleeMethod);
                localHashBuffer.append(iHash.getHash());
                ClassHashMap.addMethodMap(calleeMethod, iHash.getHash());
            } else {
                String sha = SHA256(calleeMethod.getSignature());
                localHashBuffer.append(sha);
                ClassHashMap.addMethodMap(calleeMethod, sha);
            }
        }
    }

    public String getHash() {
        return SHA256(this.localHashBuffer.toString());
    }

    private String SHA256(String input) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(input.getBytes());
            return Hex.encodeHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
            return "";
        }
    }

    public SootMethod getLocalMethod() {
        return localMethod;
    }
}
