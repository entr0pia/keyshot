package cn.edu.scu.jiangpeyton;


import cn.edu.scu.jiangpeyton.filter.Markov;
import cn.edu.scu.jiangpeyton.graph.CalleeGraph;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.options.Options;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    static private List<String> apks;

    public static void main(String[] args) throws NoSuchAlgorithmException {
        // write your code here
        //初始化目录
        init();

        //解析命令行参数
        Params params = new Params();
        try {
            JCommander.newBuilder().addObject(params).build().parse(args);
            //System.out.println("RECURSIVE: "+params.RECURSIVE);
        } catch (ParameterException exception) {
            System.out.print(Text.getUsage());
            return;
        }
        //打印使用方法
        if (params.HELP == true || args.length == 0) {
            System.out.print(Text.getUsage());
            return;
        }

        if (!params.PATH_TO_APK.isEmpty()) {
            if (params.RECURSIVE) {
                apks = findApks(params.PATH_TO_APK);
            } else {
                apks = new ArrayList<>();
                apks.add(params.PATH_TO_APK);
            }
        }


        for (String apk : apks) {
            soot.G.reset();
            Options.v().set_src_prec(Options.src_prec_apk);
            Options.v().set_process_dir(Collections.singletonList(apk));
            Options.v().set_android_jars(params.ANDROID_JAR);
            //Options.v().set_force_android_jar(android_jar);
            Options.v().set_process_multiple_dex(true);
            Options.v().set_whole_program(true);
            Options.v().set_allow_phantom_refs(true);
            Options.v().set_output_format(Options.output_format_none);
            Options.v().ignore_resolution_errors();
            Scene.v().loadNecessaryClasses();
            PackManager.v().runPacks();

            CalleeGraph calleeGraph = new CalleeGraph(apk);

            /*for (SootClass sootClass : Scene.v().getClasses()) {
                if (sootClass.toString().contains("example")) {
                    ClassHash hash = new ClassHash(sootClass, calleeGraph.calleeMap);

                    System.out.println(sootClass.getName()+": "+hash.getHash());
                }
            }*/
        }


        System.out.println("End");
    }


    static public void init() {
        //创建数据存储目录
        File data = new File("data");
        data.mkdir();

        //创建日志存储目录
        File log = new File("log");
        log.mkdir();
    }

    static public List<String> findApks(String path) {
        List<String> apks = new ArrayList<>();
        return apks;
    }

}
