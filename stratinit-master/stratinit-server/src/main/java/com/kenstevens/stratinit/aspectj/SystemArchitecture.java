package com.kenstevens.stratinit.aspectj;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class SystemArchitecture {
    @Pointcut("within(com.kenstevens.stratinit.client.server.daoservice.*)")
    public void inDaoServiceLayer() {
    }

    @Pointcut("within(com.kenstevens.stratinit.dao.*)")
    public void inDaoLayer() {
    }

    @Pointcut("execution(* com.kenstevens.stratinit.client.server.remote.*.*(..))")
    public void remoteOperation() {
    }

    @Pointcut("execution(* com.kenstevens.stratinit.client.server.remote.move.*.*(..))")
    public void remoteMoveOperation() {
    }

    @Pointcut("inDaoServiceLayer() || remoteOperation() || remoteMoveOperation() || inDaoLayer()")
    public void stratInitOperation() {
    }

}
