package cn.edu.scu.jiangpeyton.filter;

import soot.util.ArraySet;

import java.util.Set;

public class Markov {
    /**
     * 一阶马尔可夫过程的对数似然过滤器
     */
    private String string;
    private Set<String> stringSet = new ArraySet<>();

    public Markov(String s) {
        this.string = s;
    }

    public Markov(Set<String> input, double mle) {
        this.stringSet.addAll(input);
        for (String s : input) {
            if (llEstimation(s) < mle) {
                // 移除对数似然估计小于阈值的字符串
                stringSet.remove(s);
            }
        }
    }

    public double llEstimation() {
        return llEstimation(this.string);
    }


    public static double llEstimation(String s) {
        String sub = s.replaceAll("[0-9+/_=\\-]", "").toLowerCase();
        double P = 0.0;
        char last = '\0';
        double pij;
        for (char i : sub.toCharArray()) {
            if (last == '\0') {
                pij = Priori.firstP.get(i);
            } else {
                pij = getPij(last, i);
            }
            last = i;
            P += Math.log(pij);
        }
        return -P / sub.length();
    }

    public static double getPij(char last, char c) {
        double pij;
        double reg = 0.0;
        for (int j = 97; j <= 122; j++) {
            reg += Priori.firstOrderP.get(String.valueOf(last) + (char) j);
        }
        pij = Priori.firstOrderP.get(String.valueOf(last) + c) / reg;
        return pij;
    }

    public Set<String> getStringSet() {
        return stringSet;
    }

    public String getString() {
        return string;
    }
}
