package cn.edu.scu.jiangpeyton.keyshoot.caclhash;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClassHash {
    private SootClass localClass;
    private StringBuffer localHashBuff = new StringBuffer();

    public ClassHash(SootClass sootClass) throws StackOverflowError {
        if (!sootClass.isApplicationClass()) {
            localHashBuff.append(sootClass.getName());
            return;
        }
        this.localClass = sootClass;
        int layer = StringUtils.countMatches(sootClass.getName(), '.');
        //this.localHashBuff.append(layer);

        for (SootField field : sootClass.getFields()) {
            SootClass declaringClass = field.getDeclaringClass();
            while (declaringClass.hasSuperclass()) {
                declaringClass = declaringClass.getSuperclass();
            }

            // 若已处理该Class
            if (ClassHashMap.classHashMapRe.containsKey(declaringClass)) {
                localHashBuff.append(ClassHashMap.classHashMapRe.get(declaringClass));
                continue;
            }

            if (declaringClass.isApplicationClass()) {
                // 若非系统Class
                ClassHash fClassHash = new ClassHash(declaringClass);
                localHashBuff.append(fClassHash.getHash());
                ClassHashMap.addClassMap(declaringClass, fClassHash.getHash());
            } else {
                String sha = SHA256(declaringClass.getName());
                localHashBuff.append(sha);
                ClassHashMap.addClassMap(declaringClass, sha);
            }
        }

        for (SootMethod method : this.localClass.getMethods()) {

            // 若已处理该method
            if (ClassHashMap.methodHashMapRe.containsKey(method)) {
                localHashBuff.append(ClassHashMap.methodHashMapRe.get(method));
                continue;
            }
            MethodHash methodHash = new MethodHash(method);
            localHashBuff.append(methodHash.getHash());
            ClassHashMap.addMethodMap(method, methodHash.getHash());
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
