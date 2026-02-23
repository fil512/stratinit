package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.remote.exception.StratInitException;
import com.kenstevens.stratinit.server.rest.ServerManager;
import com.kenstevens.stratinit.server.rest.state.ServerStatus;
import com.kenstevens.stratinit.server.service.MessageService;
import com.kenstevens.stratinit.server.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stratinit/admin")
@Tag(name = "Administration")
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
    @Operation(summary = "List all registered players")
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
    @Operation(summary = "Post an announcement to all active games")
    public ResponseEntity<?> postAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        int count = 0;
        for (Game game : gameDao.getAllGames()) {
            messageService.postBulletin(game, request.subject(), request.body());
            count++;
        }

        return ResponseEntity.ok(Map.of("message",
                "Bulletin posted to " + count + " game" + (count != 1 ? "s" : "") + "."));
    }

    @PostMapping("/shutdown")
    @Operation(summary = "Initiate graceful server shutdown")
    public ResponseEntity<?> shutdown() {
        if (!serverStatus.isRunning()) {
            throw new StratInitException("The server is not running.");
        }
        serverManager.shutdown();
        return ResponseEntity.ok(Map.of("message", "Server shutdown complete."));
    }

    public record AnnouncementRequest(@NotBlank String subject, @NotBlank String body) {}
}
