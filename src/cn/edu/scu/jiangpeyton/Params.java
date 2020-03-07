package cn.edu.scu.jiangpeyton;

import com.beust.jcommander.Parameter;

public class Params {
    @Parameter(names = "-a", required = true)
    public String ANDROID_JAR;

    @Parameter(names = "-i")
    public String PATH_TO_APK;

    @Parameter(names = "-j")
    public String PATH_TO_RULE;

    @Parameter(names = "-d")
    public String PATH_TO_SDK;

    @Parameter(names = "-r")
    public boolean RECURSIVE = false;

    @Parameter(names = "-s")
    public boolean SAVE_DATA = false;

    @Parameter(names = "-l")
    public boolean LOG = false;

    @Parameter(names = {"-h", "--help"})
    public boolean HELP = false;
}
