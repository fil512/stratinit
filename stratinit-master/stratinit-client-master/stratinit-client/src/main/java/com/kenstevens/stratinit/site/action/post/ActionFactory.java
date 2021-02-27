package com.kenstevens.stratinit.site.action.post;

import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.remote.request.SetGameJson;
import com.kenstevens.stratinit.remote.request.UnitListJson;
import com.kenstevens.stratinit.site.ActionQueue;
import com.kenstevens.stratinit.site.action.get.*;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActionFactory {
	@Autowired
	private Spring spring;
	@Autowired
	ActionQueue actionQueue;

	public void getGames() {
		GetGamesAction getGamesAction = spring.getBean(GetGamesAction.class);
		actionQueue.put(getGamesAction);
	}

	public void battleLog() {
		BattleLogAction battleLogAction = spring.getBean(BattleLogAction.class);
		actionQueue.put(battleLogAction);
	}

	public void cityList() {
		CityListAction cityListAction = spring.autowire(new CityListAction());
		actionQueue.put(cityListAction);
	}

	public void getMap() {
		GetSectorsAction getSectorsAction = spring.autowire(new GetSectorsAction());
		actionQueue.put(getSectorsAction);
	}

	public void getNations() {
		GetNationsAction getNationsAction = spring.getBean(GetNationsAction.class);
		actionQueue.put(getNationsAction);
		GetTeamsAction getTeamsAction = spring.getBean(GetTeamsAction.class);
		actionQueue.put(getTeamsAction);
		GetRelationsAction getRelationsAction = spring.getBean(GetRelationsAction.class);
		actionQueue.put(getRelationsAction);
	}

	public void buildUnit(City city, String choice) {
		UnitType unitType = null;
		if (choice != null) {
			unitType = getUnitType(choice);
		}
		BuildUnitAction buildUnitAction = spring.autowire(new BuildUnitAction(city, unitType));
		actionQueue.put(buildUnitAction);
	}
	public void nextBuildUnit(City city, String choice) {
		UnitType unitType = getUnitType(choice);
		NextBuildUnitAction nextBuildUnitAction = spring.autowire(new NextBuildUnitAction(city, unitType));
		actionQueue.put(nextBuildUnitAction);
	}

	private UnitType getUnitType(String choice) {
		UnitType unitType = null;
		try {
			unitType = UnitType.valueOf(choice.toUpperCase());
		} catch (IllegalArgumentException e) {
			// toss
		}
		return unitType;
	}


	public void moveUnits(List<UnitView> units, SectorCoords target) {
		MoveUnitsAction moveUnitsAction = spring.autowire(new MoveUnitsAction(units, target));
		actionQueue.put(moveUnitsAction);
	}

	public void readMessages() {
		ReadMessagesAction readMessagesAction = spring.getBean(ReadMessagesAction.class);
		actionQueue.put(readMessagesAction);
	}

	public void readMessageBoard() {
		ReadMessageBoardAction readMessageBoardAction = spring.getBean(ReadMessageBoardAction.class);
		actionQueue.put(readMessageBoardAction);
	}

	public void getSentMessages() {
		GetSentMailAction getSentMailAction = spring.getBean(GetSentMailAction.class);
		actionQueue.put(getSentMailAction);
	}

	public void sendMessage(Mail message) {
		SendMessageAction sendMessageAction = spring.autowire(new SendMessageAction(message));
		actionQueue.put(sendMessageAction);
	}

	public void setRelation(Nation nation, RelationType relationType) {
		SetRelationAction setRelationAction = spring.autowire(new SetRelationAction(nation, relationType));
		actionQueue.put(setRelationAction);
	}


	public void unitList() {
		UnitListAction unitListAction = spring.getBean(UnitListAction.class);
		actionQueue.put(unitListAction);
	}


	public void updateAll(boolean firstTime) {
		GetUpdateAction getUpdateAction = spring.autowire(new GetUpdateAction(firstTime));
		actionQueue.put(getUpdateAction);
	}

	public void startupGame() {
		StartupAction startupAction = spring.getBean(StartupAction.class);
		actionQueue.put(startupAction);
	}

	public void setGame(int gameId, boolean noAlliances) {
		SetGameJson request = new SetGameJson(gameId, noAlliances);
		SetGameAction setGameAction = spring.autowire(new SetGameAction(request));
        actionQueue.put(setGameAction);
        getMyNation();
    }

	public void getVersion() {
		GetVersionAction getVersionAction = spring.getBean(GetVersionAction.class);
		actionQueue.put(getVersionAction);
	}

	// Warning: this sets last login time
	public void getMyNation() {
		GetMyNationAction getMyNationAction = spring.getBean(GetMyNationAction.class);
		actionQueue.put(getMyNationAction);
	}

	public void getNews() {
		GetNewsAction getNewsAction = spring.getBean(GetNewsAction.class);
		actionQueue.put(getNewsAction);
	}

	public void cede(City city, NationView nation) {
		CedeCityAction cedeCityAction = spring.autowire(new CedeCityAction(city, nation));
		actionQueue.put(cedeCityAction);
	}

    public void cede(List<UnitView> units, NationView nation) {
        CedeUnitsAction cedeUnitsAction = spring.autowire(new CedeUnitsAction(units, nation));
        actionQueue.put(cedeUnitsAction);
    }

    public void getUnjoinedGames() {
        GetUnjoinedGamesAction getUnjoinedGamesAction = spring.getBean(GetUnjoinedGamesAction.class);
        actionQueue.put(getUnjoinedGamesAction);
    }

	public void joinGame(SetGameJson request) {
		JoinGameAction joinGameAction = spring.autowire(new JoinGameAction(request));
		actionQueue.put(joinGameAction);
		getUnjoinedGames();
		getGames();
	}

    public void switchOnTechChange(City city) {
        SwitchCityOnTechAction switchCityOnTechAction = spring.autowire(new SwitchCityOnTechAction(city));
        actionQueue.put(switchCityOnTechAction);
    }

	public void disbandUnit(List<UnitView> units) {
		DisbandAction disbandAction = spring.autowire(new DisbandAction(units));
		actionQueue.put(disbandAction);
	}

	public void cancelMoveOrder(List<UnitView> units) {
		CancelMoveOrderAction cancelMoveOrderAction = spring.autowire(new CancelMoveOrderAction(units));
		actionQueue.put(cancelMoveOrderAction);
	}

	public void concede() {
		ConcedeAction concedeAction = spring.autowire(new ConcedeAction());
		actionQueue.put(concedeAction);
	}

	public void submitError(Exception e) {
		SubmitErrorAction submitErrorAction = spring.autowire(new SubmitErrorAction(e));
		actionQueue.put(submitErrorAction);
	}

	public void buildCity(List<UnitView> units) {
		UnitListJson unitListJson = new UnitListJson((List<Unit>) (List<?>) units);
		BuildCityAction buildCityAction = spring.autowire(new BuildCityAction(unitListJson));
		actionQueue.put(buildCityAction);
	}

	public void switchTerrain(List<UnitView> units) {
		SwitchTerrainAction switchTerrainAction = spring.autowire(new SwitchTerrainAction(units));
		actionQueue.put(switchTerrainAction);
	}

	public void setCityMove(City city, SectorCoords coords) {
		SetCityMoveAction setCityMoveAction = spring.autowire(new SetCityMoveAction(city, coords));
		actionQueue.put(setCityMoveAction);
	}
}
