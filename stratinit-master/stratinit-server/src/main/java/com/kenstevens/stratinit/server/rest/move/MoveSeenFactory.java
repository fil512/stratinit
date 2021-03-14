package com.kenstevens.stratinit.server.rest.move;

import com.kenstevens.stratinit.client.model.Nation;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

@Service
public class MoveSeenFactory {
    @Lookup
    public MoveSeen newMoveSeen(Nation nation) {
        return new MoveSeen(nation);
    }
}
