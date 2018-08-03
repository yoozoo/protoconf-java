package com.yoozoo.protoconf;

import java.util.Set;

//Configuration interface for java config
public interface ConfigurationInterface {
//    retrieve application name
    String applicationName();
//    retrieve all keys
    String[] validKeys();
//    set values inside the java config class
    boolean setValue(String key, String value);
// get watching key list
    Set<String> getWatchList();
// add key change to the change list
    void addKeyChange(String key, String value);
}
