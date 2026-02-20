package com.kenstevens.stratinit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenstevens.stratinit.client.model.PlayerRole;
import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.config.IServerConfig;
import com.kenstevens.stratinit.config.JwtTokenService;
import com.kenstevens.stratinit.config.RestWebSecurityAdapterConfig;
import com.kenstevens.stratinit.repo.PlayerRepo;
import com.kenstevens.stratinit.repo.PlayerRoleRepo;
import com.kenstevens.stratinit.server.rest.request.RequestFactory;
import com.kenstevens.stratinit.server.rest.svc.ErrorProcessor;
import com.kenstevens.stratinit.type.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class AuthControllerTest {
    private static final String TEST_USER = "testuser";
    private static final String TEST_PASSWORD = "testpass";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private UserDetailsService userDetailsService;

    @MockBean
    private RequestFactory requestFactory;
    @MockBean
    private ErrorProcessor errorProcessor;
    @MockBean
    private IServerConfig serverConfig;
    @MockBean
    private PlayerRepo playerRepo;
    @MockBean
    private PlayerRoleRepo playerRoleRepo;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void loginReturnsJwt() throws Exception {
        String body = mapper.writeValueAsString(new AuthController.LoginRequest(TEST_USER, TEST_PASSWORD));
        mockMvc.perform(post("/stratinit/auth/login")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.expiresIn", is(86400000)));
    }

    @Test
    void loginBadCredentials() throws Exception {
        String body = mapper.writeValueAsString(new AuthController.LoginRequest(TEST_USER, "wrong"));
        mockMvc.perform(post("/stratinit/auth/login")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointWithBearer() throws Exception {
        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_USER);
        String token = jwtTokenService.generateToken(userDetails);

        mockMvc.perform(get(SIRestPaths.BASE_PATH + SIRestPaths.VERSION)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("value", is(Constants.SERVER_VERSION)));
    }

    @Test
    void protectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get(SIRestPaths.BASE_PATH + SIRestPaths.VERSION)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Configuration
    @Import(RestWebSecurityAdapterConfig.class)
    static class Config {
        @Bean
        @Primary
        UserDetailsService mockUserDetailsService() {
            UserDetailsService retval = mock(UserDetailsService.class);
            String encodedPassword = new BCryptPasswordEncoder().encode(TEST_PASSWORD);
            UserDetails user = new User(TEST_USER, encodedPassword,
                    List.of(new SimpleGrantedAuthority(PlayerRole.ROLE_USER)));
            when(retval.loadUserByUsername(TEST_USER)).thenReturn(user);
            return retval;
        }

        @Bean
        StratInitController stratInitController() {
            return new StratInitController();
        }

        @Bean
        AuthController authController(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
            return new AuthController(authenticationManager, jwtTokenService);
        }
    }
}
