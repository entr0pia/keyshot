package cn.edu.scu.jiangpeyton;

public class Text {
    static private String usage = "Usage:\n" +
            "java -jar keyshot.jar -a android.jar [-options]\n" +
            "Options:\n" +
            "   -i path to apk\n" +
            "   -r recursive path to apk\n" +
            "   -j path to rules.json\n" +
            "   -s save train result\n" +
            "   -l save log file\n" +
            "   -d path to sdk";

    static public String getUsage() {
        return usage;
    }
}
