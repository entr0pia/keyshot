package cn.edu.scu.jiangpeyton.caclhash;

import cn.edu.scu.jiangpeyton.graph.MethodsLocal;
import org.apache.commons.codec.binary.Hex;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class ClassHash {
    public Map<SootMethod, MethodsLocal> calleeMap;
    public SootClass sootClass;
    public StringBuffer localHashBuff = new StringBuffer();

    public ClassHash(SootClass sootClass, Map<SootMethod, MethodsLocal> calleeMap) {
        this.calleeMap = calleeMap;
        this.sootClass = sootClass;

        for (SootField field : sootClass.getFields()) {
            SootClass declaringClass = field.getDeclaringClass();

            // 若已处理该Class
            if (ClassHashMap.classHashMapRe.containsKey(declaringClass)) {
                localHashBuff.append(ClassHashMap.classHashMapRe.get(declaringClass));
                continue;
            }

            if (declaringClass.isApplicationClass()) {
                // 若非系统Class
                ClassHash fClassHash = new ClassHash(declaringClass.hasSuperclass() ? declaringClass.getSuperclass() : declaringClass, this.calleeMap);
                localHashBuff.append(fClassHash.getHash());
                ClassHashMap.addClassMap(declaringClass, fClassHash.getHash());
            } else {
                String sha = SHA256(declaringClass.getName());
                localHashBuff.append(sha);
                ClassHashMap.addClassMap(declaringClass, sha);
            }
        }

        for (SootMethod method : this.sootClass.getMethods()) {

            // 若已处理该method
            if (ClassHashMap.methodHashMapRe.containsKey(method)) {
                localHashBuff.append(ClassHashMap.methodHashMapRe.get(method));
                continue;
            }
            MethodHash methodHash = new MethodHash(method, this.calleeMap);
            localHashBuff.append(methodHash.getHash());
            ClassHashMap.addMethodMap(method,methodHash.getHash());
        }
    }

    public String getHash() {
        return SHA256(this.localHashBuff.toString());
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
