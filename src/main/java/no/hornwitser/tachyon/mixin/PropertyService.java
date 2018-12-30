package no.hornwitser.tachyon.mixin;

import java.util.Map;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.service.IGlobalPropertyService;

public class PropertyService implements IGlobalPropertyService {
    private static Logger logger = LogManager.getLogger();
    private static Map<String, Object> properties = new HashMap();

    public <T> T getProperty(String key) {
        // logger.debug("getProperty {}", key);
        return (T)properties.get(key);
    }
    
    public void setProperty(String key, Object value) {
        logger.debug("setProperty {} {}", key, value);
        properties.put(key, value);
    }
    
    public <T> T getProperty(String key, T default_value) {
        logger.debug("getProperty {} {}", key, default_value);
        return (T)properties.getOrDefault(key, default_value);
    }
    
    public String getPropertyString(String key, String default_value) {
        logger.debug("getPropertyString {} {}", key, default_value);
        return properties.getOrDefault(key, default_value).toString();
    }
}