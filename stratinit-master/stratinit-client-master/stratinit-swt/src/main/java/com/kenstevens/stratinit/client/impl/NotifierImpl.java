package com.kenstevens.stratinit.client.impl;

import com.kenstevens.stratinit.client.api.INotifier;
import com.kenstevens.stratinit.client.audio.WavPlayer;
import com.kenstevens.stratinit.client.control.selection.MapCentre;
import com.kenstevens.stratinit.client.control.selection.SelectEvent;
import com.kenstevens.stratinit.client.control.selection.Selection;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.UnitList;
import com.kenstevens.stratinit.client.model.UnitView;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotifierImpl implements INotifier {
    @Autowired
    private WavPlayer wavPlayer;
    @Autowired
    private MapCentre mapCentre;
    @Autowired
    private Data db;
    @Autowired
    private SelectEvent selectEvent;

    @Override
    public void finishedStartingUp() {
        centreMapOnUnit();
        wavPlayer.playFinishedLoading();
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
        display.asyncExec(() -> selectEvent.selectUnit(unit, Selection.Source.UNIT_TAB));
    }
}
