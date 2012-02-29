package com.kenstevens.stratinit.ui.shell;

public interface ProgressBarControl {

	public abstract void setMaximum(final int value);

	public abstract void incrementSelection();

	public abstract void reset();

}