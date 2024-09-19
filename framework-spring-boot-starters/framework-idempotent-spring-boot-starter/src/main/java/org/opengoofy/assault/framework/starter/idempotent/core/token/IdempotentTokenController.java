package org.opengoofy.assault.framework.starter.idempotent.core.token;

import lombok.RequiredArgsConstructor;
import org.opengoofy.assault.framework.starter.convention.result.Result;
import org.opengoofy.assault.framework.starter.web.Results;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基于 Token 验证请求幂等性控制器
 */
@RestController
@RequiredArgsConstructor
public class IdempotentTokenController {
    
    private final IdempotentTokenService idempotentTokenService;
    
    /**
     * 请求申请Token
     */
    @GetMapping("/token")
    public Result<String> createToken() {
        return Results.success(idempotentTokenService.createToken());
    }
}
