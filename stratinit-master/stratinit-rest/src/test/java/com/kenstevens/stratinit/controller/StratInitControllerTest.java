package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.config.IServerConfig;
import com.kenstevens.stratinit.model.PlayerRole;
import com.kenstevens.stratinit.repo.PlayerRepo;
import com.kenstevens.stratinit.rest.SIRestPaths;
import com.kenstevens.stratinit.server.rest.request.RequestFactory;
import com.kenstevens.stratinit.server.rest.svc.ErrorProcessor;
import com.kenstevens.stratinit.type.Constants;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StratInitController.class)
class StratInitControllerTest {
    private static final String HAPPY_USER = "happy";
    private static final String HAPPY_USER_PASSWORD = "days";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestFactory requestFactory;
    @MockBean
    private ErrorProcessor errorProcessor;
    @MockBean
    private IServerConfig serverConfig;
    @MockBean
    private PlayerRepo playerRepo;

    @Test
    public void versionUnauthorized() throws Exception {
        mockMvc.perform(get(SIRestPaths.BASE_PATH + SIRestPaths.VERSION).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(HAPPY_USER)
    public void versionOk() throws Exception {
        mockMvc.perform(get(SIRestPaths.BASE_PATH + SIRestPaths.VERSION).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("value", is(Constants.SERVER_VERSION)));
    }

    @Configuration
    static class Config {
        @Bean
        UserDetailsService userDetailsService() {
            UserDetailsService retval = mock(UserDetailsService.class);
            List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(PlayerRole.ROLE_USER));
            UserDetails result = new User(HAPPY_USER, HAPPY_USER_PASSWORD, roles);
            when(retval.loadUserByUsername(HAPPY_USER)).thenReturn(result);
            return retval;
        }

        @Bean
        StratInitController stratInitController() {
            return new StratInitController();
        }
    }
    // FIXME test the other methods

    // FIXME test that only admin can shut down
}
