package com.yoozoo.protoconf;

// Code generated by Protoconf (https://version.uuzu.com/Merlion/protoconf). DO NOT EDIT.
// Code generated at 13 Nov 18 19:25 DST, with protoconf version 0.1.8
// This code is designed to work with protoconf-java (https://github.com/yoozoo/protoconf-java) v1.0

import com.yoozoo.protoconf.reader.ChangeListener;
import com.yoozoo.protoconf.reader.ConfigurationInterface;
import com.yoozoo.protoconf.reader.WatchKeyInterface;

import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Configuration2 implements ConfigurationInterface, WatchKeyInterface {
    
     
    private static Logger log = Logger.getAnonymousLogger();
    
    private String mysqlDsn;
    
    private String username;
    public void watch_username(ChangeListener l) {
        watchKey("username", l);
    }
    private String password;
    

    
    public String get_mysqlDsn() { return mysqlDsn; }
    public String get_username() { return username; }
    public String get_password() { return password; }

    public Configuration2() {
        _watch_list = new HashMap<>();
    }

    private	static final Configuration2 _instance;
    private static final String[] _keys;
    static {
        _instance = new Configuration2();
        _keys = new String[] { "mysqlDsn","password","username" };
    }
    public static Configuration2 instance() { return _instance;}
    public String applicationName() { return "北美平台-spring-boot"; }
	public String[] validKeys() { return _keys; }

    Map<String, ChangeListener> _watch_list;

    public void watchKey(String key, ChangeListener l) {
        _watch_list.put(key, l);
    }

    public Set<String> getWatchList() {
        return _watch_list.keySet();
    }

    public void addKeyChange(String k, String v) {
        ChangeListener l = _watch_list.get(k);
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
            case "mysqlDsn":
                mysqlDsn =  (value);
                return true;
            case "username":
                username =  (value);
                return true;
            case "password":
                password =  (value);
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
