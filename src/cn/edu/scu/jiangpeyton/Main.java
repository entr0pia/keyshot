package cn.edu.scu.jiangpeyton;


import cn.edu.scu.jiangpeyton.caclhash.ClassHash;
import cn.edu.scu.jiangpeyton.caclhash.ClassHashMap;
import cn.edu.scu.jiangpeyton.filter.FilterKey;
import cn.edu.scu.jiangpeyton.graph.CalleeGraph;
import cn.edu.scu.jiangpeyton.rule.API;
import cn.edu.scu.jiangpeyton.rule.Rule;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.dongliu.apk.parser.ApkFile;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Set;

public class Main {

    public static String logFile;
    public static String packageName;

    public static void main(String[] args) throws NoSuchAlgorithmException {
        // write your code here
        //初始化目录
        init();

        StringBuilder logFileName = new StringBuilder();
        logFileName.append("./log/log")
                .append(LocalDate.now().toString())
                .append('-')
                .append(LocalTime.now().getHour())
                .append('-')
                .append(LocalTime.now().getMinute())
                .append('-')
                .append(LocalTime.now().getSecond())
                .append(".txt");
        logFile = logFileName.toString();


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


        // 读取规则文件
        try {
            Gson json = new Gson();
            JsonReader reader = new JsonReader(new FileReader(Params.PATH_TO_RULE));
            Params.RULE = json.fromJson(reader, Rule.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (Params.RECURSIVE) {
            File apks = new File(Params.PATH_TO_APK);
            lsFile(apks);
        } else {
            factory(Params.PATH_TO_APK);
        }

        System.out.println("End");
    }


    public static void init() {
        //创建数据存储目录
        File data = new File("data");
        data.mkdir();

        //创建日志存储目录
        File log = new File("log");
        log.mkdir();
    }


    public static void factory(String path_to_apk) {
        // 处理apk文件
        System.out.println("for apk: " + path_to_apk);

        try (ApkFile apkFile = new ApkFile(new File(path_to_apk))) {
            packageName = apkFile.getApkMeta().getPackageName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logging(packageName, "Starting...");

        // 设置flowdroid
        soot.G.reset();
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_process_dir(Collections.singletonList(path_to_apk));
        Options.v().set_android_jars(Params.ANDROID_JAR);
        //Options.v().set_force_android_jar(android_jar);
        Options.v().set_process_multiple_dex(true);
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_output_format(Options.output_format_none);
        Options.v().ignore_resolution_errors();
        Scene.v().loadNecessaryClasses();
        PackManager.v().runPacks();


        // 绘制调用关系图
        CalleeGraph calleeGraph = new CalleeGraph();

        // 更新规则文件
        if (Params.UPDATE_DATA) {
            for (SootClass sootClass : Scene.v().getClasses()) {
                for (API profile : Params.RULE.profiles) {
                    // 匹配规则文件
                    if (sootClass.getName().equals(profile.apiClass)) {
                        // 计算目标哈希值
                        ClassHash hash = new ClassHash(sootClass);
                        //System.out.println(sootClass.getName() + ": " + hash.getHash());
                        for (SootMethod sootMethod : sootClass.getMethods()) {
                            if (sootMethod.getSubSignature().equals(profile.apiMethod)) {
                                System.out.println(sootMethod.getSubSignature() + ": " + ClassHashMap.methodHashMapRe.get(sootMethod));
                                profile.hash.add(ClassHashMap.methodHashMapRe.get(sootMethod));
                                break;
                            }
                        }
                    }
                }
            }
            // 重新写入规则文件
            try {
                Gson json = new GsonBuilder()
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create();
                Writer writer = new FileWriter(Params.PATH_TO_RULE);
                json.toJson(Params.RULE, Rule.class, json.newJsonWriter(writer));
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // 计算全部哈希值
        for (SootClass sootClass : Scene.v().getClasses()) {
            ClassHash hash = new ClassHash(sootClass);
        }

        // 匹配规则文件
        for (API profile : Params.RULE.profiles) {
            if (profile.obfs) {
                // 若目标sdk支持混淆, 则通过哈希值识别
                for (String hash : profile.hash) {
                    if (ClassHashMap.methodHashMap.containsKey(hash)) {
                        // 发现目标method, 提取密钥
                        StringBuilder msg = new StringBuilder();
                        msg.append("发现目标method >> ")
                                .append(profile.apiClass)
                                .append(':')
                                .append(profile.apiMethod);
                        logging(packageName, msg.toString());
                        for (SootClass sootClass : calleeGraph.slicingStr.keySet()) {
                            Set<String> slicing = calleeGraph.slicingStr.get(sootClass);
                            FilterKey filter = new FilterKey(slicing, profile.key);
                            if (filter.isPaired()) {
                                // 发现成对密钥, 进行零泄漏检测
                                logging(packageName, "发现成对密钥, 进行零泄漏检测");
                                request(filter, profile);
                            }
                        }
                        continue;
                    }
                }
            } else {
                logging(packageName, profile.packageName + "不支持混淆, 跳过method哈希值对比");
                for (SootClass i : Scene.v().getClasses()) {
                    if (i.getName().contains(profile.packageName)) {
                        StringBuilder msg = new StringBuilder();
                        msg.append("发现目标package >> ")
                                .append(profile.packageName);
                        logging(packageName, msg.toString());
                        for (SootClass sootClass : calleeGraph.slicingStr.keySet()) {
                            Set<String> slicing = calleeGraph.slicingStr.get(sootClass);
                            FilterKey filter = new FilterKey(slicing, profile.key);
                            if (filter.isPaired()) {
                                // 发现成对密钥, 进行零泄漏检测
                                logging(packageName, "发现成对密钥, 进行零泄漏检测");
                                request(filter, profile);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    public static void lsFile(File file) {
        // 遍历目录
        for (File sub : file.listFiles()) {
            if (sub.isDirectory()) {
                lsFile(sub);
            } else {
                try {
                    factory(sub.getAbsolutePath());
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void request(FilterKey filter, API profile) {
        // 零泄漏检测
        for (String accessID : filter.getAccessID()) {
            System.out.println(accessID);
        }
        for (String secretKey : filter.getSecretKey()) {
            System.out.println(secretKey);
        }
    }

    public static void logging(String packageName, String msg) {
        // 写日志文件
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(logFile), true), StandardCharsets.UTF_8);
            StringBuilder builder = new StringBuilder();
            builder.append(LocalTime.now().toString().substring(0, 8))
                    .append(" [")
                    .append(packageName)
                    .append("] ")
                    .append(msg)
                    .append(System.getProperty("line.separator"));
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
