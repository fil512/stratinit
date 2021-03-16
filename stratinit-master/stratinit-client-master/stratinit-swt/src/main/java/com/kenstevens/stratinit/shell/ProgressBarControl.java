package com.kenstevens.stratinit.shell;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

public class ProgressBarControl {
    private final ProgressBar progressBar;

    public ProgressBarControl(ProgressBar progressBAr) {
        this.progressBar = progressBAr;
    }

    public void setMaximum(final int value) {
        Display display = Display.getDefault();

        if (progressBar.isDisposed()) {
            return;
        }
        display.asyncExec(new Runnable() {
            public void run() {
                if (progressBar.isDisposed())
                    return;
                progressBar.setMaximum(value);
            }
        });
    }

    public void incrementSelection() {
        Display display = Display.getDefault();

        if (progressBar.isDisposed()) {
            return;
        }
        display.asyncExec(new Runnable() {
            public void run() {
                if (progressBar.isDisposed())
                    return;
                progressBar.setSelection(progressBar.getSelection() + 1);
            }
        });

    }

    public void reset() {
        Display display = Display.getDefault();

        if (progressBar.isDisposed()) {
            return;
        }
        display.asyncExec(new Runnable() {
            public void run() {
                if (progressBar.isDisposed())
                    return;
                progressBar.setSelection(0);
            }
        });
    }
}