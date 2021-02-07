package com.kenstevens.stratinit.aspectj;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.BatchUpdateException;
import java.sql.SQLException;

@Component
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AJAXExceptionHandler {

	@Around("com.kenstevens.stratinit.aspectj.SystemArchitecture.inDaoServiceLayer()")
	// Special handling for Dao service methods to extract the root SQL exception from the BatchUpdateException
	public Object logExceptions(ProceedingJoinPoint pjp) throws Throwable {
		Log log = LogFactory.getLog(pjp.getTarget().getClass());

		try {
			Object retVal = pjp.proceed();
			return retVal;
		} catch (Throwable e) {
			LogFactory.getLog(pjp.getTarget().getClass().getName()).error(e.getMessage(), e);
			int index = ExceptionUtils.indexOfThrowable(e, BatchUpdateException.class);
			if (index != -1) {
				BatchUpdateException bue = (BatchUpdateException) ExceptionUtils.getThrowables(e)[index];
				SQLException sqlException = bue.getNextException();
				if (sqlException != null) {
					log.error("SQL EXCEPTION: "+e.getMessage(), e);
				} else {
					log.error("No SQLException.  :-(");
				}
			} else {
				log.error("No SQLException.  :-(");
			}
			throw e;
		}
	}
}
