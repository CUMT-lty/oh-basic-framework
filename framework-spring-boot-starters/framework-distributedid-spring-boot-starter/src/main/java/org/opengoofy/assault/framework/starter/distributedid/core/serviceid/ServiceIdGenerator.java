package org.opengoofy.assault.framework.starter.distributedid.core.serviceid;

import org.opengoofy.assault.framework.starter.distributedid.core.IdGenerator;
import org.opengoofy.assault.framework.starter.distributedid.core.snowflake.SnowflakeIdInfo;

/**
 * 业务 ID 生成器
 */
public interface ServiceIdGenerator extends IdGenerator {
    
    /**
     * 根据 {@param serviceId} 生成雪花算法 ID
     */
    long nextId(long serviceId);
    
    /**
     * 根据 {@param serviceId} 生成字符串类型雪花算法 ID
     */
    String nextIdStr(long serviceId);
    
    /**
     * 解析雪花算法
     */
    SnowflakeIdInfo parseSnowflakeId(long snowflakeId);
}
