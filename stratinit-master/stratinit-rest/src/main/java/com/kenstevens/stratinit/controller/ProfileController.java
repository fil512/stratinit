package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/stratinit/profile")
@Tag(name = "Profile")
public class ProfileController {
    private final PlayerService playerService;
    private final BCryptPasswordEncoder passwordEncoder;

    public ProfileController(PlayerService playerService, BCryptPasswordEncoder passwordEncoder) {
        this.playerService = playerService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @Operation(summary = "Get the authenticated player's profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Player player = playerService.findPlayer(userDetails.getUsername());
        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Player not found"));
        }
        // Use HashMap since Map.of() doesn't allow many entries easily
        java.util.HashMap<String, Object> profile = new java.util.HashMap<>();
        profile.put("username", player.getUsername());
        profile.put("email", player.getEmail() != null ? player.getEmail() : "");
        profile.put("emailGameMail", player.isEmailGameMail());
        profile.put("created", player.getCreated() != null ? player.getCreated().getTime() : 0);
        profile.put("wins", player.getWins());
        profile.put("played", player.getPlayed());
        profile.put("winPerc", player.getWinPerc());
        profile.put("admin", playerService.isAdmin(player));
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    @Operation(summary = "Update email, password, or notification preferences")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestBody UpdateProfileRequest request) {
        Player player = playerService.findPlayer(userDetails.getUsername());
        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Player not found"));
        }

        Player updatedPlayer = new Player(player.getUsername());
        updatedPlayer.setEmail(request.email());
        updatedPlayer.setEmailGameMail(request.emailGameMail());

        if (request.password() != null && !request.password().isBlank()) {
            updatedPlayer.setPassword(passwordEncoder.encode(request.password()));
        }

        Result<Player> result = playerService.updatePlayer(updatedPlayer);
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(Map.of("error", result.toString()));
        }

        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }

    public record UpdateProfileRequest(String email, boolean emailGameMail, String password) {}
}
