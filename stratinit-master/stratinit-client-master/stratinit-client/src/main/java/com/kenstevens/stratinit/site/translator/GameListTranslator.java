package com.kenstevens.stratinit.site.translator;

import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.model.GameView;
import com.kenstevens.stratinit.move.WorldView;

@Service
public class GameListTranslator extends ListTranslator<SIGame, GameView> {

	@Override
	public GameView translate(SIGame input) {
		GameView gameView = new GameView(input);
		WorldView world = new WorldView(gameView);
		gameView.setWorld(world );
		return gameView;
	}

}
