package com.kenstevens.stratinit.cache;


import org.springframework.stereotype.Service;

@Service
public interface GameLoader {

    GameCache loadGame(int gameId);

    void flush(GameCache gameCache);

}