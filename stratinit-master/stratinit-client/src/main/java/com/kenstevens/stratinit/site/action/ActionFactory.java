package com.kenstevens.stratinit.site.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.Spring;

@Component
public class ActionFactory {
	@Autowired
	private Spring spring;

	public void getGames() {
		GetGamesAction getGamesAction = spring.getBean(GetGamesAction.class);
		getGamesAction.addToActionQueue();
	}

	public void battleLog() {
		BattleLogAction battleLogAction = spring.getBean(BattleLogAction.class);
		battleLogAction.addToActionQueue();
	}

	public void mapAll() {
	}

	public void cityList() {
		CityListAction cityListAction = spring.autowire(new CityListAction());
		cityListAction.addToActionQueue();
	}

	public void getMap() {
		GetSectorsAction getSectorsAction = spring.autowire(new GetSectorsAction());
		getSectorsAction.addToActionQueue();
	}

	public void getNations() {
		GetNationsAction getNationsAction = spring.getBean(GetNationsAction.class);
		getNationsAction.addToActionQueue();
		GetTeamsAction getTeamsAction = spring.getBean(GetTeamsAction.class);
		getTeamsAction.addToActionQueue();
		GetRelationsAction getRelationsAction = spring.getBean(GetRelationsAction.class);
		getRelationsAction.addToActionQueue();
	}

	public void buildUnit(City city, String choice) {
		UnitType unitType = null;
		if (choice != null) {
			unitType = getUnitType(choice);
		}
		BuildUnitAction buildUnitAction = spring.autowire(new BuildUnitAction(city, unitType));
		buildUnitAction.addToActionQueue();
	}
	public void nextBuildUnit(City city, String choice) {
		UnitType unitType = getUnitType(choice);
		NextBuildUnitAction nextBuildUnitAction = spring.autowire(new NextBuildUnitAction(city, unitType));
		nextBuildUnitAction.addToActionQueue();
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
		moveUnitsAction.addToActionQueue();
	}

	public void readMessages() {
		ReadMessagesAction readMessagesAction = spring.getBean(ReadMessagesAction.class);
		readMessagesAction.addToActionQueue();
	}

	public void readMessageBoard() {
		ReadMessageBoardAction readMessageBoardAction = spring.getBean(ReadMessageBoardAction.class);
		readMessageBoardAction.addToActionQueue();
	}

	public void getSentMessages() {
		GetSentMailAction getSentMailAction = spring.getBean(GetSentMailAction.class);
		getSentMailAction.addToActionQueue();
	}

	public void sendMessage(Mail message) {
		SendMessageAction sendMessageAction = spring.autowire(new SendMessageAction(message));
		sendMessageAction.addToActionQueue();
	}

	public void setRelation(Nation nation, RelationType relationType) {
		SetRelationAction setRelationAction = spring.autowire(new SetRelationAction(nation, relationType));
		setRelationAction.addToActionQueue();
	}


	public void unitList() {
		UnitListAction unitListAction = spring.getBean(UnitListAction.class);
		unitListAction.addToActionQueue();
	}


	public void updateAll(boolean firstTime) {
		GetUpdateAction getUpdateAction = spring.autowire(new GetUpdateAction(firstTime));
		getUpdateAction.addToActionQueue();
	}

	public void startupGame() {
		StartupAction startupAction = spring.getBean(StartupAction.class);
		startupAction.addToActionQueue();
	}

	public void setGame(int gameId) {
		SetGameAction setGameAction = spring.autowire(new SetGameAction(gameId));
		setGameAction.addToActionQueue();
		getMyNation();
	}

	public void getVersion() {
		GetVersionAction getVersionAction = spring.getBean(GetVersionAction.class);
		getVersionAction.addToActionQueue();
	}

	// Warning: this sets last login time
	public void getMyNation() {
		GetMyNationAction getMyNationAction = spring.getBean(GetMyNationAction.class);
		getMyNationAction.addToActionQueue();
	}

	public void getNews() {
		GetNewsAction getNewsAction = spring.getBean(GetNewsAction.class);
		getNewsAction.addToActionQueue();
	}

	public void cede(City city, NationView nation) {
		CedeCityAction cedeCityAction = spring.autowire(new CedeCityAction(city, nation));
		cedeCityAction.addToActionQueue();
	}

	public void cede(List<UnitView> units, NationView nation) {
		CedeUnitsAction cedeUnitsAction = spring.autowire(new CedeUnitsAction(units, nation));
		cedeUnitsAction.addToActionQueue();
	}

	public void getUnjoinedGames() {
		GetUnjoinedGamesAction getUnjoinedGamesAction = spring.getBean(GetUnjoinedGamesAction.class);
		getUnjoinedGamesAction.addToActionQueue();
	}

	public void joinGame(int gameId) {
		JoinGameAction joinGameAction = spring.autowire(new JoinGameAction(gameId));
		joinGameAction.addToActionQueue();
		getUnjoinedGames();
		getGames();
	}

	public void switchOnTechChange(City city) {
		SwitchCityOnTechAction switchCityOnTechAction = spring.autowire(new SwitchCityOnTechAction(city));
		switchCityOnTechAction.addToActionQueue();
	}

	public void disbandUnit(List<UnitView> units) {
			DisbandAction disbandAction = spring.autowire(new DisbandAction(units));
			disbandAction.addToActionQueue();
	}

	public void cancelMoveOrder(List<UnitView> units) {
		CancelMoveOrderAction cancelMoveOrderAction = spring.autowire(new CancelMoveOrderAction(units));
		cancelMoveOrderAction.addToActionQueue();
}

	public void concede() {
		ConcedeAction concedeAction = spring.autowire(new ConcedeAction());
		concedeAction.addToActionQueue();
	}

	public void submitError(Exception e) {
		SubmitErrorAction submitErrorAction = spring.autowire(new SubmitErrorAction(e));
		submitErrorAction.addToActionQueue();
	}

	public void buildCity(List<UnitView> units) {
		BuildCityAction buildCityAction = spring.autowire(new BuildCityAction(units));
		buildCityAction.addToActionQueue();
	}

	public void switchTerrain(List<UnitView> units) {
		SwitchTerrainAction switchTerrainAction = spring.autowire(new SwitchTerrainAction(units));
		switchTerrainAction.addToActionQueue();
	}

	public void setCityMove(City city, SectorCoords coords) {
		SetCityMoveAction setCityMoveAction = spring.autowire(new SetCityMoveAction(city, coords));
		setCityMoveAction.addToActionQueue();
	}
}
