package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.server.rest.request.write.*;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestFactory {

    // Write requests

    @Lookup
    public SetGameRequest getSetGameRequest(int gameId, boolean noAlliances) {
        return new SetGameRequest(gameId, noAlliances);
    }

    @Lookup
    public UpdateCityRequest getUpdateCityRequest(SICityUpdate sicity, CityFieldToUpdateEnum field) {
        return new UpdateCityRequest(sicity, field);
    }

    @Lookup
    public MoveUnitsRequest getMoveUnitsRequest(List<SIUnit> units, SectorCoords target) {
        return new MoveUnitsRequest(units, target);
    }

    @Lookup
    public CedeUnitsRequest getCedeUnitsRequest(List<SIUnit> siunits, int nationId) {
        return new CedeUnitsRequest(siunits, nationId);
    }

    @Lookup
    public CedeCityRequest getCedeCityRequest(SICityUpdate sicity, int nationId) {
        return new CedeCityRequest(sicity, nationId);
    }

    @Lookup
    public SetRelationRequest getSetRelationRequest(int nationId, RelationType relationType) {
        return new SetRelationRequest(nationId, relationType);
    }

    @Lookup
    public SendMessageRequest getSendMessageRequest(SIMessage simessage) {
        return new SendMessageRequest(simessage);
    }

    @Lookup
    public JoinGameRequest getJoinGameRequest(Player player, int gameId, boolean noAlliances) {
        return new JoinGameRequest(player, gameId, noAlliances);
    }

    @Lookup
    public DisbandUnitRequest getDisbandUnitRequest(List<SIUnit> siunits) {
        return new DisbandUnitRequest(siunits);
    }

    @Lookup
    public CancelMoveOrderRequest getCancelMoveOrderRequest(List<SIUnit> siunits) {
        return new CancelMoveOrderRequest(siunits);
    }

    @Lookup
    public BuildCityRequest getBuildCityRequest(List<SIUnit> siunits) {
        return new BuildCityRequest(siunits);
    }

    @Lookup
    public SwitchTerrainRequest getSwitchTerrainRequest(List<SIUnit> siunits) {
        return new SwitchTerrainRequest(siunits);
    }

    @Lookup
    public PostAnnouncementRequest getPostAnnouncementRequest(String subject, String body) {
        return new PostAnnouncementRequest(subject, body);
    }

    @Lookup
    public ConcedeRequest getConcedeRequest() {
        return new ConcedeRequest();
    }

    // GET-as-write requests (have side effects: clear notification flags)

    @Lookup
    public GetBattleLogRequest getGetBattleLogRequest() {
        return new GetBattleLogRequest();
    }

    @Lookup
    public GetMailRequest getGetMailRequest() {
        return new GetMailRequest();
    }
}
