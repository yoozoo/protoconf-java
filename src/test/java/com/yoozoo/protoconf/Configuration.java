package com.yoozoo.protoconf;

import com.yoozoo.protoconf.reader.ChangeListener;
import com.yoozoo.protoconf.reader.ConfigurationInterface;
import com.yoozoo.protoconf.reader.WatchKeyInterface;

import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Configuration implements ConfigurationInterface, WatchKeyInterface {
    
    
    public class Configuration1 {
    
    private long id;
    public void watch_id(ChangeListener l) {
        watchKey("id", l);
    }

    
    public long get_id() { return id; }

    public Configuration1(WatchKeyInterface parent, String prefix) { 
        this.parent = parent;
        this.prefix = prefix;
    }

    private WatchKeyInterface parent;
    private String prefix;

    public void watchKey(String k, ChangeListener l) {
        parent.watchKey(prefix + "/" + k, l);
    }
    
    public boolean setValue(String key, String value) {
        try {
            switch(key)
            { 
            case "id":
                id = Long.parseLong (value);
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
        case "id":
            return "23";
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
 
    private static Logger log = Logger.getAnonymousLogger();
    
    private String name;
    public void watch_name(ChangeListener l) {
        watchKey("name", l);
    }
    private Configuration1 msg;
    

    
    public String get_name() { return name; }
    public Configuration1 get_msg() { return msg; }

    public Configuration() { 
        msg = new Configuration1(this, "msg");
        watch_list = new HashMap<>();
    }

    private	static final Configuration instance;
    private static final String[] keys;
    static {
        instance = new Configuration();
        keys = new String[] { "name","msg/id" };
    }
    public static Configuration instance() { return instance;}
    public String applicationName() { return "test"; }
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
            case "name":
                name =  (value);
                return true;
            default:
                String[] keys = key.split("/", 2);
                if(keys.length >= 2) {
                    key = keys[0];
                    String innerKey = keys[1];
                    switch(key) { 
                        case "msg":
                            return msg.setValue(innerKey, value);
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
        case "name":
            return "123";
        default:
            String[] keys = key.split("/", 2);
            if(keys.length >= 2) {
                key = keys[0];
                String innerKey = keys[1];
                switch(key) { 
                    case "msg":
                        return msg.getDefaultValue(innerKey);
                }
            }
        }
        return null;
    }
    
}
