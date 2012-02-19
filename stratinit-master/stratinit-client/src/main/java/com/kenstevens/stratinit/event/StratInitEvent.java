package com.kenstevens.stratinit.event;

import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public abstract class StratInitEvent<H extends EventHandler> extends GwtEvent<H> {
	private final Log logger = LogFactory.getLog(getClass());

	@SuppressWarnings("unchecked")
	@Override
	public Type<H> getAssociatedType() {
		@SuppressWarnings("rawtypes")
		Class<? extends StratInitEvent> clazz = this.getClass();
		Field field;
		try {
			field = clazz.getField("TYPE");
			return (Type<H>)field.get(null);
		} catch (SecurityException e) {
			logger.error(e.getMessage(), e);
		} catch (NoSuchFieldException e) {
			logger.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	protected abstract void dispatch(H handler);
}
