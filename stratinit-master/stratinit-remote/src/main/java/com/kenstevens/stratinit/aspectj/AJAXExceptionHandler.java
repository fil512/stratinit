package com.kenstevens.stratinit.aspectj;

import java.sql.BatchUpdateException;
import java.sql.SQLException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AJAXExceptionHandler {

	@Around("com.kenstevens.stratinit.aspectj.SystemArchitecture.inDaoServiceLayer()")
	// Special handling for Dao service methods to extract the root SQL exception from the BatchUpdateException
	public Object logExceptions(ProceedingJoinPoint pjp) throws Throwable {
		Logger logger = Logger.getLogger(pjp.getTarget().getClass());
		try {
			Object retVal = pjp.proceed();
			return retVal;
		} catch (Throwable e) {
			Logger.getLogger(pjp.getTarget().getClass().getName()).error(e.getMessage(), e);
			int index = ExceptionUtils.indexOfThrowable(e, BatchUpdateException.class);
			if (index != -1) {
				BatchUpdateException bue = (BatchUpdateException) ExceptionUtils.getThrowables(e)[index];
				SQLException sqlException = bue.getNextException();
				if (sqlException != null) {
					logger.error("SQL EXCEPTION: "+e.getMessage(), e);
				} else {
					logger.error("No SQLException.  :-(");
				}
			} else {
				logger.error("No SQLException.  :-(");
			}
			throw e;
		}
	}
}
