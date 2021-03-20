package com.kenstevens.stratinit.client.impl;

import com.kenstevens.stratinit.client.api.ICommandList;
import com.kenstevens.stratinit.client.shell.WidgetContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommandListImpl implements ICommandList {
    @Autowired
    private WidgetContainer widgetContainer;

    @Override
    public void removeLast() {
        widgetContainer.getCommandListControl().removeLast();
    }

    @Override
    public void removeAll() {
        widgetContainer.getCommandListControl().removeAll();
    }

    @Override
    public void addItem(String description) {
        widgetContainer.getCommandListControl().addItem(description);
    }
}
