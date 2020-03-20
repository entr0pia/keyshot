package cn.edu.scu.jiangpeyton;

import cn.edu.scu.jiangpeyton.rule.Rule;
import com.beust.jcommander.Parameter;

public class Params {
    @Parameter(names = "-a", required = true)
    public static String ANDROID_JAR;

    @Parameter(names = "-i")
    public static String PATH_TO_APK;

    @Parameter(names = "-j")
    public static String PATH_TO_RULE;

    @Parameter(names = "-d")
    public static String PATH_TO_SDK;

    @Parameter(names = "-r")
    public static boolean RECURSIVE = false; // PATH_TO_APK是否为目录

    @Parameter(names = "-u")
    public static boolean UPDATE_DATA = false; // 更新规则文件

    @Parameter(names = "-l")
    public static boolean LOG = false;

    @Parameter(names = {"-h", "--help"})
    public static boolean HELP = false;

    public static Rule RULE;
}
