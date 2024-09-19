package org.opengoofy.assault.framework.flow.monitor.core.loader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 增强器实例加载
 */
public class EnhancerInstanceLoader {
    
    private static ConcurrentHashMap<String, Object> INSTANCE_CACHE = new ConcurrentHashMap<>();
    private static ReentrantLock INSTANCE_LOAD_LOCK = new ReentrantLock();
    private static Map<ClassLoader, ClassLoader> EXTEND_PLUGIN_CLASSLOADERS = new HashMap<>();
    
    public static <T> T load(String className,
                             ClassLoader targetClassLoader) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        if (targetClassLoader == null) {
            targetClassLoader = EnhancerInstanceLoader.class.getClassLoader();
        }
        String instanceKey = new StringBuilder()
                .append(className)
                .append("_OF_")
                .append(targetClassLoader.getClass().getName())
                .append("@")
                .append(Integer.toHexString(targetClassLoader.hashCode()))
                .toString();
        Object inst = INSTANCE_CACHE.get(instanceKey);
        if (inst == null) {
            ClassLoader pluginLoader;
            INSTANCE_LOAD_LOCK.lock();
            try {
                pluginLoader = EXTEND_PLUGIN_CLASSLOADERS.get(targetClassLoader);
                if (pluginLoader == null) {
                    pluginLoader = new AgentPluginClassLoader(targetClassLoader);
                    EXTEND_PLUGIN_CLASSLOADERS.put(targetClassLoader, pluginLoader);
                }
            } finally {
                INSTANCE_LOAD_LOCK.unlock();
            }
            inst = Class.forName(className, true, pluginLoader).newInstance();
            if (inst != null) {
                INSTANCE_CACHE.put(instanceKey, inst);
            }
        }
        return (T) inst;
    }
}
