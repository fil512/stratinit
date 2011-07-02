package com.kenstevens.stratinit.aspectj;

import java.util.HashSet;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.aspectj.Profiler.ProfilerID;

@Component
//@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProfilerAspect {
	private static final Set<String>excludes = new HashSet<String>();
	static int indent = 0;
	public static boolean enabled = false;
	static {
		excludes.add("updateTech");
		excludes.add("updateUnit");
	}
	
	@Around("com.kenstevens.stratinit.aspectj.SystemArchitecture.stratInitOperation()")
	public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
		Signature sig = pjp.getSignature();
		if (!enabled || excludes.contains(sig.getName())) {
			return pjp.proceed();
		}
		ProfilerID id = Profiler.start(indent, sig.getDeclaringTypeName(), sig.getName(), pjp.getArgs());
		indent += 1;
		Object retVal = pjp.proceed();
		indent -= 1;
		Profiler.end(id);
		return retVal;
	}
}
