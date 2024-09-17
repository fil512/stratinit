package com.kenstevens.stratinit.client.event;

import org.eclipse.swt.widgets.Display;
import org.springframework.stereotype.Service;

@Service
public class DisplayEventExecutor implements IEventExecutor {
    @Override
    public void asyncExec(Runnable runnable) {
        Display display = Display.getDefault();

        if (display.isDisposed())
            return;
        display.asyncExec(runnable);
    }
}
