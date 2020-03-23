package cn.edu.scu.jiangpeyton.filter;

import cn.edu.scu.jiangpeyton.graph.CalleeGraph;
import cn.edu.scu.jiangpeyton.rule.Key;
import cn.edu.scu.jiangpeyton.rule.KeyStruct;
import soot.SootClass;
import soot.util.ArraySet;

import java.util.Base64;
import java.util.Set;

public class FilterKey {
    private Set<String> accessID;
    private Set<String> secretKey;
    private boolean paired = false; // 若发现成对密钥, 则为true

    public FilterKey(Set<String> stringSet, Key key) {
        this.accessID = filter(stringSet, key.accessID);
        this.secretKey = filter(stringSet, key.secretKey);
        if (this.accessID.size() > 0
                && this.secretKey.size() > 0) {
            this.paired = true;
        }
    }

    private Set<String> filter(Set<String> input, KeyStruct keyStruct) {
        /**
         * 字符串过滤器
         */
        Set<String> stringSet = new ArraySet<>();
        stringSet.addAll(input);

        // 根据base64(可选)和长度进行过滤
        for (String s : input) {
            if (s.length() != keyStruct.len) {
                stringSet.remove(s);
            }else if(keyStruct.base64){
                try {
                    Base64.getDecoder().decode(s);
                    stringSet.remove(s);
                }catch (IllegalArgumentException e){
                    continue;
                }
            }
        }

        // 正则过滤器
        stringSet = new Regular(stringSet, keyStruct.pattern).getStringSet();
        if (stringSet.size() <= 1) {
            return stringSet;
        }

        // 熵过滤器
        stringSet = new Entropy(stringSet, keyStruct.entropy).getStringSet();
        if (stringSet.size() <= 1) {
            return stringSet;
        }

        // 一阶马尔可夫过程的对数似然过滤器
        stringSet = new Markov(stringSet, keyStruct.MLE).getStringSet();

        return stringSet;
    }


    public Set<String> getAccessID() {
        return accessID;
    }

    public Set<String> getSecretKey() {
        return secretKey;
    }

    public boolean isPaired() {
        return paired;
    }
}
