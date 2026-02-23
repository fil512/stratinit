package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.config.JwtTokenService;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.exception.StratInitException;
import com.kenstevens.stratinit.server.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/stratinit/auth")
@Tag(name = "Authentication")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final PlayerService playerService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService,
                          PlayerService playerService, BCryptPasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.playerService = playerService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and receive a JWT token")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenService.generateToken(userDetails);
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "expiresIn", jwtTokenService.getExpirationMs()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid username or password", java.util.List.of("Invalid username or password")));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new player account")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        Player player = new Player(request.username());
        player.setEmail(request.email());
        player.setPassword(passwordEncoder.encode(request.password()));

        Result<Player> result = playerService.register(player);
        if (!result.isSuccess()) {
            throw new StratInitException(result.toString());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Registration successful"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password reset via email")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        if ((request.username() == null || request.username().isBlank())
                && (request.email() == null || request.email().isBlank())) {
            throw new StratInitException("Username or email is required");
        }

        String username = request.username() != null ? request.username() : "";
        String email = request.email() != null ? request.email() : "";

        Result<None> result = playerService.forgottenPassword(username, email);
        if (!result.isSuccess()) {
            throw new StratInitException(result.toString());
        }

        return ResponseEntity.ok(Map.of("message",
                "A new password has been sent to your email address."));
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record RegisterRequest(@NotBlank String username, @NotBlank @Email String email,
                                  @NotBlank @Size(min = 6) String password) {}
    public record ForgotPasswordRequest(String username, String email) {}
}
