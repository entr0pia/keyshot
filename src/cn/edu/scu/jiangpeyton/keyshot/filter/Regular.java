package cn.edu.scu.jiangpeyton.keyshot.filter;

import soot.util.ArraySet;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regular {
    /**
     * 正则过滤器
     */
    private Pattern pattern;
    private Set<String> stringSet = new ArraySet<>();

    public Regular(Set<String> input, String regex) {
        this.pattern = Pattern.compile(regex);
        this.stringSet.addAll(input);
        for (String s : input) {
            Matcher matcher = this.pattern.matcher(s);
            // 移除不匹配的结果
            if (!matcher.matches()) {
                stringSet.remove(s);
            }
        }
    }

    public Set<String> getStringSet() {
        return stringSet;
    }
}
