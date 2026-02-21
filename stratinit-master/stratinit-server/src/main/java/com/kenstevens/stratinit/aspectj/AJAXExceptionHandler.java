package com.kenstevens.stratinit.aspectj;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.BatchUpdateException;
import java.sql.SQLException;

@Component
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AJAXExceptionHandler {

	@Around("com.kenstevens.stratinit.aspectj.SystemArchitecture.inServiceLayer()")
	// Special handling for service methods to extract the root SQL exception from the BatchUpdateException
	public Object logExceptions(ProceedingJoinPoint pjp) throws Throwable {
		Logger logger = LoggerFactory.getLogger(pjp.getTarget().getClass());

		try {
			Object retVal = pjp.proceed();
			return retVal;
		} catch (Throwable e) {
			LoggerFactory.getLogger(pjp.getTarget().getClass().getName()).error(e.getMessage(), e);
			int index = ExceptionUtils.indexOfThrowable(e, BatchUpdateException.class);
			if (index != -1) {
				BatchUpdateException bue = (BatchUpdateException) ExceptionUtils.getThrowables(e)[index];
				SQLException sqlException = bue.getNextException();
				if (sqlException != null) {
					logger.error("SQL EXCEPTION: " + e.getMessage(), e);
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
