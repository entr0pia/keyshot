package cn.edu.scu.jiangpeyton.caclhash;

import cn.edu.scu.jiangpeyton.graph.MethodsLocal;
import org.apache.commons.codec.binary.Hex;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.util.Chain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassHash {
    public Map<SootMethod, MethodsLocal> calleeMap;
    public SootClass sootClass;
    public StringBuffer localHash = new StringBuffer();

    public ClassHash(SootClass sootClass, Map<SootMethod, MethodsLocal> calleeMap) {
        this.calleeMap = calleeMap;
        this.sootClass = sootClass;

        for (SootField field : sootClass.getFields()) {
            SootClass declaringClass=field.getDeclaringClass();
            if(declaringClass.isApplicationClass()) {
                ClassHash fClassHash = new ClassHash(declaringClass.hasSuperclass() ? declaringClass.getSuperclass() : declaringClass, this.calleeMap);
                localHash.append(fClassHash.getHash());
            }else {
                localHash.append(SHA256(declaringClass.getName()));
            }
        }

        for (SootMethod method : this.sootClass.getMethods()) {
            MethodHash methodHash = new MethodHash(method, this.calleeMap);
            localHash.append(methodHash.getHash());
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
