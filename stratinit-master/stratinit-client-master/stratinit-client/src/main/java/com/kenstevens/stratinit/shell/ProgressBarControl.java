package com.kenstevens.stratinit.shell;

public interface ProgressBarControl {

	void setMaximum(final int value);

	void incrementSelection();

	void reset();

}