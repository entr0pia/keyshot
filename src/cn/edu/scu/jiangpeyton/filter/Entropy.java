package cn.edu.scu.jiangpeyton.filter;


import org.apache.commons.lang3.StringUtils;
import soot.util.ArraySet;

import java.util.Set;

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
        for (char c : s.toCharArray()) {
            int counts = StringUtils.countMatches(s, c);
            double p = (double) counts / s.length();
            entropy += -Math.log(p);
        }
        return entropy;
    }

    public String getS() {
        return s;
    }

    ;

    public Set<String> getStringSet() {
        return stringSet;
    }
}
