package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.model.GameHistory;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.rank.TeamHelper;
import com.kenstevens.stratinit.rank.TeamProvider;
import com.kenstevens.stratinit.rank.TeamRanker;
import com.kenstevens.stratinit.repo.GameHistoryRepo;
import com.kenstevens.stratinit.server.rest.request.RequestProcessor;
import com.kenstevens.stratinit.server.service.PlayerService;
import com.kenstevens.stratinit.server.service.UnitStatisticsService;
import com.kenstevens.stratinit.type.UnitBaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
public class RankingController {
    @Autowired
    private RequestProcessor requestProcessor;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private TeamProvider teamProvider;
    @Autowired
    private GameHistoryRepo gameHistoryRepo;
    @Autowired
    private UnitStatisticsService unitStatisticsService;

    @GetMapping(path = SIRestPaths.LEADERBOARD)
    public List<SIPlayerRank> getLeaderboard() {
        return requestProcessor.processNoGame(player -> {
            List<Player> players = playerService.getPlayers();
            return players.stream()
                    .filter(Player::isEnabled)
                    .map(SIPlayerRank::new)
                    .sorted(Comparator.comparingInt((SIPlayerRank r) -> r.wins).reversed()
                            .thenComparingInt(r -> -r.winPercentage))
                    .collect(Collectors.toList());
        });
    }

    @GetMapping(path = SIRestPaths.RANKINGS_TEAM)
    public List<SITeamRank> getTeamRankings() {
        return requestProcessor.processNoGame(player -> {
            TeamHelper teamHelper = new TeamHelper(teamProvider);
            TeamRanker teamRanker = new TeamRanker(teamHelper);
            List<GameHistory> games = gameHistoryRepo.findAll();
            for (GameHistory game : games) {
                teamRanker.rank(game);
            }
            return teamRanker.getTeamRanks();
        });
    }

    @GetMapping(path = SIRestPaths.STATS_GAMES)
    public List<SIGameHistory> getCompletedGames() {
        return requestProcessor.processNoGame(player -> {
            List<GameHistory> games = gameHistoryRepo.findAll();
            return games.stream()
                    .map(SIGameHistory::new)
                    .sorted(Comparator.comparingInt((SIGameHistory g) -> g.gameId).reversed())
                    .collect(Collectors.toList());
        });
    }

    @GetMapping(path = SIRestPaths.STATS_PLAYERS)
    public List<String> getGamePlayers(@RequestParam int gameId) {
        return requestProcessor.processNoGame(player ->
                unitStatisticsService.getGamePlayers(gameId)
        );
    }

    @GetMapping(path = SIRestPaths.STATS_GAME_UNITS)
    public List<SIUnitLove> getGameUnitLove(@RequestParam int gameId) {
        return requestProcessor.processNoGame(player ->
                unitStatisticsService.getGameUnitLove(gameId)
        );
    }

    @GetMapping(path = SIRestPaths.STATS_PLAYER_UNITS)
    public List<SIUnitDayRow> getPlayerUnits(@RequestParam int gameId,
                                             @RequestParam UnitBaseType unitBaseType,
                                             @RequestParam String username) {
        return requestProcessor.processNoGame(player ->
                unitStatisticsService.getPlayerUnits(gameId, unitBaseType, username)
        );
    }
}
