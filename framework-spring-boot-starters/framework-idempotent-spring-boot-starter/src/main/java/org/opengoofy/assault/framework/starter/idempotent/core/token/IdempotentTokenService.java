package org.opengoofy.assault.framework.starter.idempotent.core.token;

import org.opengoofy.assault.framework.starter.idempotent.core.IdempotentExecuteHandler;

/**
 * Token 实现幂等接口
 */
public interface IdempotentTokenService extends IdempotentExecuteHandler {
    
    /**
     * 创建幂等验证Token
     */
    String createToken();
}
