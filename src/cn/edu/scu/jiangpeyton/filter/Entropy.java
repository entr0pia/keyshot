package cn.edu.scu.jiangpeyton.filter;


import org.apache.commons.lang3.StringUtils;

public class Entropy {
    private String s;

    public Entropy(String s){
        this.s=s;
    }

    public double getEntropy(){
        return getEntropy(this.s);
    }

    public static double getEntropy(String s)
    {
        double entropy=0.0;
        for(char c:s.toCharArray()){
            int counts= StringUtils.countMatches(s,c);
            double p=(double) counts/s.length();
            entropy += -Math.log(p);
        }
        return entropy;
    }
}
