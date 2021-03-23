package com.kenstevens.stratinit.client.site.translator;

import com.kenstevens.stratinit.client.model.CityView;
import com.kenstevens.stratinit.dto.SICityUpdate;
import org.springframework.stereotype.Service;

@Service
public class CityListTranslator extends ListTranslator<SICityUpdate, CityView> {

    @Override
    public CityView translate(SICityUpdate input) {
        return new CityView(db.getSelectedGame(), db.getNation(input.nationId), input);
    }

}
