package org.opengoofy.assault.framework.starter.database;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.opengoofy.assault.framework.starter.distributedid.SnowflakeIdUtil;

/**
 * 自定义雪花算法生成器
 */
public class CustomIdGenerator implements IdentifierGenerator {
    
    @Override
    public Number nextId(Object entity) {
        return SnowflakeIdUtil.nextId();
    }
}
