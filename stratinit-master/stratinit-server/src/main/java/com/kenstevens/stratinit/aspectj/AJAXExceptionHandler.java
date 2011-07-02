package com.kenstevens.stratinit.aspectj;

import java.sql.BatchUpdateException;
import java.sql.SQLException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
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

	@SuppressWarnings("deprecation")
	@Around("com.kenstevens.stratinit.aspectj.SystemArchitecture.inDaoServiceLayer()")
	// Special handling for Dao service methods to extract the root SQL exception from the BatchUpdateException
	public Object logExceptions(ProceedingJoinPoint pjp) throws Throwable {
		Logger logger = Logger.getLogger(pjp.getTarget().getClass());
		try {
			Object retVal = pjp.proceed();
			return retVal;
		} catch (Throwable e) {
			logger.log(pjp.getTarget().getClass().getName(), Priority.ERROR, e.getMessage(), e);
			e.printStackTrace(System.err);
			int index = ExceptionUtils.indexOfThrowable(e, BatchUpdateException.class);
			if (index != -1) {
				BatchUpdateException bue = (BatchUpdateException) ExceptionUtils.getThrowables(e)[index];
				SQLException sqlException = bue.getNextException();
				if (sqlException != null) {
					System.err.println("SQL EXCEPTION FOUND!  :-)");
					logger.log(pjp.getTarget().getClass().getName(), Priority.ERROR, sqlException.getMessage(), sqlException);
					sqlException.printStackTrace(System.err);
				} else {
					System.out.println("No SQLException.  :-(");
				}
			} else {
				System.out.println("No SQLException.  :-(");
			}
			throw e;
		}
	}
}
