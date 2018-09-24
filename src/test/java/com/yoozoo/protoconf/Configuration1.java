package com.yoozoo.protoconf;

import com.yoozoo.protoconf.ChangeListener;
import com.yoozoo.protoconf.ConfigurationInterface;
import com.yoozoo.protoconf.WatchKeyInterface;

import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Configuration1 implements ConfigurationInterface, WatchKeyInterface {
    
     
    private static Logger log = Logger.getAnonymousLogger();
    
    private String dbHost;
    
    private String dbPort;
    
    private String dbName;
    
    private String routerMapMhyy;
    

    
    public String get_dbHost() { return dbHost; }
    public String get_dbPort() { return dbPort; }
    public String get_dbName() { return dbName; }
    public String get_routerMapMhyy() { return routerMapMhyy; }

    public Configuration1() { 
        watch_list = new HashMap<>();
    }

    private	static final Configuration1 instance;
    private static final String[] keys;
    static {
        instance = new Configuration1();
        keys = new String[] { "dbHost","dbPort","dbName","routerMapMhyy" };
    }
    public static Configuration1 instance() { return instance;}
    public String applicationName() { return "测试平台-测试服务"; }
	public String[] validKeys() { return keys; }

    Map<String, ChangeListener> watch_list;

    public void watchKey(String key, ChangeListener l) {
        watch_list.put(key, l);
    }

    public Set<String> getWatchList() {
        return watch_list.keySet();
    }

    public void addKeyChange(String k, String v) {
        ChangeListener l = watch_list.get(k);
        if (l != null) {
            //            calling onchange method in the change listener
            l.onChange(v);
            setValue(k, v);
        }
    }

    
    public boolean setValue(String key, String value) {
        try {
            switch(key)
            { 
            case "dbHost":
                dbHost =  (value);
                return true;
            case "dbPort":
                dbPort =  (value);
                return true;
            case "dbName":
                dbName =  (value);
                return true;
            case "routerMapMhyy":
                routerMapMhyy =  (value);
                return true;
            default:
                String[] keys = key.split("/", 2);
                if(keys.length >= 2) {
                    key = keys[0];
                    String innerKey = keys[1];
                    switch(key) { 
                        default:
                            return false;
                    }
                }
                return false;
            }
        }
        catch(Exception e) {
            log.severe("error:" + e.getMessage());
            return false;
        }
    }
    public String getDefaultValue(String key)
    {
        switch(key)
        { 
        default:
            String[] keys = key.split("/", 2);
            if(keys.length >= 2) {
                key = keys[0];
                String innerKey = keys[1];
                switch(key) { 
                }
            }
        }
        return null;
    }
    
}
