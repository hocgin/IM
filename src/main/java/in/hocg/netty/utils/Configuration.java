package in.hocg.netty.utils;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * @author hocgin
 * @date 18-7-30
 **/
public class Configuration {
    private static Properties SYSTEM_CONFIG = new Properties();
    private static final String PATH_SERVER_CONF = "application.properties";
    
    public static void init() {
        try (
                InputStream inputStream = Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(PATH_SERVER_CONF);
        ) {
            
            SYSTEM_CONFIG.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getProperty(String key, String defaultValue) {
        return SYSTEM_CONFIG.getProperty(key, defaultValue);
    }
    
    public static Optional<String> getProperty(String key) {
        return Optional.of(SYSTEM_CONFIG.getProperty(key));
    }
}
