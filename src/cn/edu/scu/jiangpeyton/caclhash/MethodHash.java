package cn.edu.scu.jiangpeyton.caclhash;

import cn.edu.scu.jiangpeyton.graph.MethodsLocal;
import org.apache.commons.codec.binary.Hex;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MethodHash {
    public SootMethod sootMethod;
    public Map<SootMethod, MethodsLocal> calleeMap;
    public MethodsLocal methodsLocal;
    public StringBuffer localHash = new StringBuffer();

    public MethodHash(SootMethod method, Map<SootMethod, MethodsLocal> calleeMap) {
        this.sootMethod = method;
        this.calleeMap = calleeMap;
        this.methodsLocal = calleeMap.get(method);

        // 形参, 局部变量, 返回值
        List<Type> types = new ArrayList<>();
        types.addAll(methodsLocal.parameterTypes);
        types.addAll(methodsLocal.localVarTypes);
        types.add(methodsLocal.returnType);
        for (Type iType : types) {
            RefType refType;
            try {
                refType = (RefType) iType;
            } catch (ClassCastException e) {
                //e.printStackTrace();
                continue;
            }
            SootClass refClass = refType.getSootClass();
            if(method.getDeclaringClass().getClass().equals(refClass.getClass())){
                continue;
            }
            if (refClass.isApplicationClass()) {
                ClassHash classHash = new ClassHash(refClass.hasSuperclass() ? refClass.getSuperclass() : refClass, calleeMap);
                localHash.append(classHash.getHash());
            } else {
                localHash.append(SHA256(refClass.getName()));
            }
        }

        for (SootMethod calleeMethod : methodsLocal.callees) {
            SootClass declaringClass = calleeMethod.getDeclaringClass();
            if(method.getDeclaringClass().getClass().equals(declaringClass.getClass()))
            {
                continue;
            }
            if (declaringClass.isApplicationClass()) {
                MethodHash iHash = new MethodHash(calleeMethod, calleeMap);
                localHash.append(iHash.getHash());
            } else {
                localHash.append(SHA256(calleeMethod.getSignature()));
            }
        }
    }

    public String getHash() {
        return SHA256(this.localHash.toString());
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
}
