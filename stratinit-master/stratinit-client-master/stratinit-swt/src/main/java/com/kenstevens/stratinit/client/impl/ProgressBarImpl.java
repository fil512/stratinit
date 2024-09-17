package com.kenstevens.stratinit.client.impl;

import com.kenstevens.stratinit.client.api.IProgressBar;
import com.kenstevens.stratinit.client.shell.WidgetContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProgressBarImpl implements IProgressBar {
    @Autowired
    private WidgetContainer widgetContainer;

    @Override
    public void reset() {
        widgetContainer.getProgressBarControl().reset();
    }

    @Override
    public void setMaximum(int maxSeconds) {
        widgetContainer.getProgressBarControl().setMaximum(maxSeconds);
    }

    @Override
    public void incrementSelection() {
        widgetContainer.getProgressBarControl().incrementSelection();
    }
}
