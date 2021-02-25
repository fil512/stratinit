package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;

import java.util.Date;
import java.util.List;

public interface PlayerDaoService {

    Result<Player> register(Player player);

    List<Player> getPlayers();

    Result<Player> updatePlayer(Player player);

    boolean isAdmin(Player player);

    Result<None> forgottenPassword(String username, String email);

    boolean authorizePlayer(String userName, String password);

    void setLastLogin(Player player, Date now);

    Player findPlayer(String username);
}