/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cn.octopus.paperplagiarism.vo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author taoliu
 */
public class InitConfigUtil {
    
    public static void createInitFile(){
        String osName = System.getProperty("os.name").toLowerCase(); 
        File initDir = null;
        File initConfig = null;
        if (osName.contains("win")) {  
            initDir = new File("D://plagiararism");
            if(!initDir.exists()){
                initDir.mkdirs();
            } 
            initConfig = new File("D://plagiararism/initConfig.txt");
        } else if (osName.contains("mac")) {  
            String userHome = System.getProperty("user.home");
            initDir = new File(userHome + "/plagiararism");
            if(!initDir.exists()){
                initDir.mkdirs();
            } 
            initConfig = new File(userHome + "/plagiararism/initConfig.txt");
        } else {  
            JOptionPane.showMessageDialog(null, "操作系统不支持", "错误提示", JOptionPane.ERROR_MESSAGE);
        } 
        if(!initConfig.exists()){
            FileUtil.touch(initConfig);
            List<String> configs = new ArrayList<>();
            configs.add("fileConcurrent: 1");
            configs.add("defaultSimilarityThreshold: 75");
            configs.add("initDir: " + initDir.getAbsolutePath());
            FileUtil.writeUtf8Lines(configs, initConfig);
        } 
    }
    
    public static String getConfig(String configName){        
        Map<String, String> configMap = parseConfig();
        return configMap.get(configName);
    }
    
    public static void saveConfig(List<String> configs){ 
        FileUtil.writeUtf8Lines(configs, getConfigFile());
    }
    
    public static void saveConfig(String configName, String configValue){
        Map<String, String> configMap = parseConfig();
        configMap.put(configName, configValue);
        List<String> configs = new ArrayList<>();
        configMap.entrySet().stream().forEach(entry -> 
            configs.add(entry.getKey() + ", " + entry.getValue())
        );  
        FileUtil.writeUtf8Lines(configs, getConfigFile());
    }
    
    public static File getConfigFile() {
        String osName = System.getProperty("os.name").toLowerCase();
        File initConfig = null;
        if (osName.contains("win")) {
            initConfig = new File("D://plagiararism/initConfig.txt");
        }else if (osName.contains("mac")) {
            String userHome = System.getProperty("user.home");
            initConfig = new File(userHome + "/plagiararism/initConfig.txt");
        }
        return initConfig;
    }
    
    private static Map<String, String> parseConfig() {
        Map<String, String> configMap = new HashMap<>();  
        // 使用换行符分割配置字符串为数组
        List<String> configLines = FileUtil.readUtf8Lines(getConfigFile()); 
  
        for (String line : configLines) {  
            // 使用冒号分割每一行  
            String[] keyValue = line.split(": ");  
            if (keyValue.length == 2) {  
                // 去除前后可能存在的空格  
                String key = keyValue[0].trim();  
                String value = StrUtil.isBlank(keyValue[1]) ? "" : keyValue[1].trim();  
                // 将键值对存入Map  
                configMap.put(key, value);  
            }  
        } 
        return configMap;
    }
    
    public static void main(String[] args) {
        System.out.println(getConfig("initDir")); 
    }
    
}
