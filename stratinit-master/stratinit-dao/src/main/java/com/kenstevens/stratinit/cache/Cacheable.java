package com.kenstevens.stratinit.cache;

public class Cacheable {
	private boolean modified = false;
	private static boolean finalFlush = false;

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	protected boolean isModified() {
		return modified || finalFlush;
	}
	
	public static void setFinalFlush(boolean flush) {
		finalFlush = flush;
	}
}
