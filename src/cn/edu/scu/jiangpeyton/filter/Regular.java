package cn.edu.scu.jiangpeyton.filter;

import java.util.regex.Pattern;

public class Regular {
    Pattern pattern;

    public Regular(String regex){
        this.pattern=Pattern.compile(regex);
    }
}
