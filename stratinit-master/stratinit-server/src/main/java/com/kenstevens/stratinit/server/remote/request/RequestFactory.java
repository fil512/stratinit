package com.kenstevens.stratinit.server.remote.request;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.server.remote.request.write.*;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestFactory {
    @Lookup
    public SetGameRequest getSetGameRequest(int gameId, boolean noAlliances) {
        return new SetGameRequest(gameId, noAlliances);
    }

    @Lookup
    public GetJoinedGamesRequest getGetJoinedGamesRequest() {
        return new GetJoinedGamesRequest();
    }

    @Lookup
    public GetUnjoinedGamesRequest getGetUnjoinedGamesRequest() {
        return new GetUnjoinedGamesRequest();
    }

    @Lookup
    public GetNationsRequest getGetNationsRequest() {
        return new GetNationsRequest();
    }

    @Lookup
    public GetSectorsRequest getGetSectorsRequest() {
        return new GetSectorsRequest();
    }

    @Lookup
    public GetUnitsRequest getGetUnitsRequest() {
        return new GetUnitsRequest();
    }

    @Lookup
    public GetSeenUnitsRequest getGetSeenUnitsRequest() {
        return new GetSeenUnitsRequest();
    }

    @Lookup
    public GetMyNationRequest getGetMyNationRequest() {
        return new GetMyNationRequest();
    }

    @Lookup
    public GetCitiesRequest getGetCitiesRequest() {
        return new GetCitiesRequest();
    }

    @Lookup
    public GetSeenCitiesRequest getGetSeenCitiesRequest() {
        return new GetSeenCitiesRequest();
    }

    @Lookup
    public GetUpdateRequest getGetUpdateRequest() {
        return new GetUpdateRequest();
    }

    @Lookup
    public GetBattleLogRequest getGetBattleLogRequest() {
        return new GetBattleLogRequest();
    }

    @Lookup
    public GetMailRequest getGetMailRequest() {
        return new GetMailRequest();
    }

    @Lookup
    public GetRelationsRequest getGetRelationsRequest() {
        return new GetRelationsRequest();
    }

    @Lookup
    public GetMessagesRequest getGetMessagesRequest() {
        return new GetMessagesRequest();
    }

    @Lookup
    public GetSentMailRequest getGetSentMailRequest() {
        return new GetSentMailRequest();
    }

    @Lookup
    public GetAnnouncementsRequest getGetAnnouncementsRequest() {
        return new GetAnnouncementsRequest();
    }

    @Lookup
    public GetSattelitesRequest getGetSattelitesRequest() {
        return new GetSattelitesRequest();
    }

    @Lookup
    public GetUnitsBuiltRequest getGetUnitsBuiltRequest() {
        return new GetUnitsBuiltRequest();
    }

    @Lookup
    public UpdateCityRequest getUpdateCityRequest(SICity sicity, UpdateCityField field) {
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
    public CedeCityRequest getCedeCityRequest(SICity sicity, int nationId) {
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
    public GetLogsRequest getGetLogsRequest() {
        return new GetLogsRequest();
    }

    @Lookup
    public PostAnnouncementRequest getPostAnnouncementRequest(String subject, String body) {
        return new PostAnnouncementRequest(subject, body);
    }

    @Lookup
    public GetTeamsRequest getGetTeamsRequest() {
        return new GetTeamsRequest();
    }

    @Lookup
    public ConcedeRequest getConcedeRequest() {
        return new ConcedeRequest();
    }
}
