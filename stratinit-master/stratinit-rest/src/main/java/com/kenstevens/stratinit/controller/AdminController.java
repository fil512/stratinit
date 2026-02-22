package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.server.rest.ServerManager;
import com.kenstevens.stratinit.server.rest.state.ServerStatus;
import com.kenstevens.stratinit.server.service.MessageService;
import com.kenstevens.stratinit.server.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stratinit/admin")
public class AdminController {
    private final PlayerService playerService;
    private final MessageService messageService;
    private final GameDao gameDao;
    private final ServerManager serverManager;
    private final ServerStatus serverStatus;

    public AdminController(PlayerService playerService, MessageService messageService,
                           GameDao gameDao, ServerManager serverManager, ServerStatus serverStatus) {
        this.playerService = playerService;
        this.messageService = messageService;
        this.gameDao = gameDao;
        this.serverManager = serverManager;
        this.serverStatus = serverStatus;
    }

    @GetMapping("/players")
    public List<Map<String, Object>> getPlayers() {
        return playerService.getPlayers().stream()
                .map(p -> Map.<String, Object>of(
                        "username", p.getUsername(),
                        "email", p.getEmail() != null ? p.getEmail() : "",
                        "created", p.getCreated() != null ? p.getCreated().getTime() : 0,
                        "lastLogin", p.getLastLogin() != null ? p.getLastLogin().getTime() : 0,
                        "enabled", p.isEnabled()
                ))
                .toList();
    }

    @PostMapping("/announcement")
    public ResponseEntity<?> postAnnouncement(@RequestBody AnnouncementRequest request) {
        if (request.subject() == null || request.subject().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Subject is required"));
        }
        if (request.body() == null || request.body().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Body is required"));
        }

        int count = 0;
        for (Game game : gameDao.getAllGames()) {
            messageService.postBulletin(game, request.subject(), request.body());
            count++;
        }

        return ResponseEntity.ok(Map.of("message",
                "Bulletin posted to " + count + " game" + (count != 1 ? "s" : "") + "."));
    }

    @PostMapping("/shutdown")
    public ResponseEntity<?> shutdown() {
        if (!serverStatus.isRunning()) {
            return ResponseEntity.badRequest().body(Map.of("error", "The server is not running."));
        }
        serverManager.shutdown();
        return ResponseEntity.ok(Map.of("message", "Server shutdown complete."));
    }

    public record AnnouncementRequest(String subject, String body) {}
}
