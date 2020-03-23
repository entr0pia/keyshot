package cn.edu.scu.jiangpeyton.keyshot;

public class Text {
    public static String usage = new StringBuilder()
            .append("Usage:\n")
            .append("java -jar keyshot.jar -a path_to_android.jar -i path_to_apk -j path_to_rules [-options]\n")
            .append("Options:\n")
            .append("   -r recursive path to apk\n")
            .append("   -u update ruless.json\n")
            .toString();

    static public String getUsage() {
        return usage;
    }
}
