package com.hinam_list.hinam_list_fetch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "store")
public class StoreDataConfigProperties {
    private Map<String, StoreData> storeNameDataMap;

    public Map<String, StoreData> getStoreNameDataMap() {
        return storeNameDataMap;
    }

    public void setStoreNameDataMap(Map<String, StoreData> storeNameDataMap) {
        this.storeNameDataMap = storeNameDataMap;
    }
}
