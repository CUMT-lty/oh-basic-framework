package org.opengoofy.assault.framework.starter.convention.exception;

import org.opengoofy.assault.framework.starter.convention.errorcode.IErrorCode;

/**
 * 远程服务调用异常
 */
public class RemoteException extends AbstractException {
    
    public RemoteException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }
    
    public RemoteException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }
    
    @Override
    public String toString() {
        return "RemoteException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
