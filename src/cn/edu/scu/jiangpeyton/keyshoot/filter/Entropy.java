package cn.edu.scu.jiangpeyton.keyshoot.filter;


import org.apache.commons.lang3.StringUtils;
import soot.util.ArraySet;

import java.util.*;

public class Entropy {
    private String s;
    private Set<String> stringSet = new ArraySet<>();

    public Entropy(String s) {
        this.s = s;
    }

    public Entropy(Set<String> input, double en) {
        this.stringSet.addAll(input);
        for (String s : input) {
            if (getEntropy(s) < en) {
                // 移除熵小于阈值的字符串
                stringSet.remove(s);
            }
        }
    }

    public double getEntropy() {
        return getEntropy(this.s);
    }

    public static double getEntropy(String s) {
        double entropy = 0.0;
        for (char c : toCharSet(s)) {
            int counts = StringUtils.countMatches(s, c);
            double p = (double) counts / s.length();
            entropy += -Math.log(p);
        }
        return entropy;
    }

    public static Set<Character> toCharSet(String s){
        Set<Character> characters=new ArraySet<>();
        for(char c:s.toCharArray()){
            characters.add(c);
        }
        return characters;
    }

    public String getS() {
        return s;
    }


    public Set<String> getStringSet() {
        return stringSet;
    }
}
