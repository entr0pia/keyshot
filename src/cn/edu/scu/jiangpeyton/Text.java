package cn.edu.scu.jiangpeyton;

public class Text {
    static private String usage = new StringBuilder()
            .append("Usage:\n")
            .append("java -jar keyshot.jar -a android.jar [-options]\n")
            .append("Options:\n").append("   -i path to apk\n")
            .append("   -r recursive path to apk\n")
            .append("   -j path to rules.json\n")
            .append("   -s save train result\n")
            .append("   -l save log file\n")
            .append("   -d path to sdk")
            .toString();

    static public String getUsage() {
        return usage;
    }
}
