package com.kenstevens.stratinit.client.impl;

import com.kenstevens.stratinit.client.api.IAudioPlayer;
import com.kenstevens.stratinit.client.api.IEventSelector;
import com.kenstevens.stratinit.client.api.INotifier;
import com.kenstevens.stratinit.client.api.Selection;
import com.kenstevens.stratinit.client.control.selection.MapCentre;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.UnitList;
import com.kenstevens.stratinit.client.model.UnitView;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotifierImpl implements INotifier {
    @Autowired
    private IAudioPlayer audioPlayer;
    @Autowired
    private MapCentre mapCentre;
    @Autowired
    private Data db;
    @Autowired
    private IEventSelector eventSelector;

    @Override
    public void finishedStartingUp() {
        centreMapOnUnit();
        audioPlayer.playFinishedLoading();
    }

    private void centreMapOnUnit() {
        UnitList unitList = db.getUnitList();
        if (unitList.isEmpty()) {
            return;
        }
        UnitView unit = unitList.getUnits().get(0);
        selectUnit(unit);
        mapCentre.init();
    }

    private void selectUnit(final UnitView unit) {
        Display display = Display.getDefault();

        if (display.isDisposed())
            return;
        display.asyncExec(() -> eventSelector.selectUnit(unit, Selection.Source.UNIT_TAB));
    }
}
