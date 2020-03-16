package cn.edu.scu.jiangpeyton.filter;

public class Markov {
    public String string;

    public Markov(String s) {
        this.string = s;
    }

    public double llEstimation() {
        return llEstimation(this.string);
    }

    /*public static double getEntropy(String s) {
        String sub = s.toLowerCase().replaceAll("[0-9]", "");
        double entropy = 0.0;
        for (char i : sub.toCharArray()) {
            Pattern pattern = Pattern.compile(new StringBuffer()
                    .append(i)
                    .append("[a-z]{1}")
                    .toString());
            Matcher matcher=pattern.matcher(sub);
            double firstOrder = 0.0;
            while (matcher.find()){
                String ij=matcher.group();
                double pij=Priori.firstOrderP.get(ij);
                firstOrder+=pij*Math.log(pij);
            }
            double pi=StringUtils.countMatches(sub,i)/(double)sub.length();
            entropy+=-pi*firstOrder;
        }
        return entropy;
    }*/

    public static double llEstimation(String s) {
        String sub = s.replaceAll("[0-9]", "").toLowerCase();
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
        return P / sub.length();
    }

    public static double getPij(char last, char c) {
        double pij;
        double reg = 0.0;
        for (int j = 97; j <= 122; j++) {
            StringBuffer tmp = new StringBuffer();
            tmp.append(last).append((char) j);
            reg += Priori.firstOrderP.get(tmp.toString());
        }
        StringBuffer key = new StringBuffer();
        key.append(last).append(c);
        pij = Priori.firstOrderP.get(key.toString()) / reg;
        return pij;
    }
}
