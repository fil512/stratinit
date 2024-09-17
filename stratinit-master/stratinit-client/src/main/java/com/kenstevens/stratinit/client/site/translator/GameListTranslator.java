package com.kenstevens.stratinit.client.site.translator;

import com.kenstevens.stratinit.client.model.GameView;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.move.WorldView;
import org.springframework.stereotype.Service;

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
