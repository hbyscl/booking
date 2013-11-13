package com.mapabc.booking.util;

import java.util.ResourceBundle;

/**
 * Created with ChengLi
 * User: ChengLi
 * Date: 13-2-22
 * Time: 上午10:12
 * 配置文件读取工具
 */
public class PropertiesUtil {
    private static ResourceBundle config = null;

    public static void initProperties(){
        config = ResourceBundle.getBundle("config");
    }

    public static String getProperties(String key){
        if(null == config){
            initProperties();
        }
        return config.getString(key);
    }
}
