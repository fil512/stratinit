package com.kenstevens.stratinit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenstevens.stratinit.client.model.PlayerRole;
import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.config.IServerConfig;
import com.kenstevens.stratinit.config.RestWebSecurityAdapterConfig;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.dto.StratInitDTO;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.IRestRequestJson;
import com.kenstevens.stratinit.remote.request.MoveUnitsJson;
import com.kenstevens.stratinit.repo.PlayerRepo;
import com.kenstevens.stratinit.repo.PlayerRoleRepo;
import com.kenstevens.stratinit.server.rest.request.GetCitiesRequest;
import com.kenstevens.stratinit.server.rest.request.GetSeenCitiesRequest;
import com.kenstevens.stratinit.server.rest.request.PlayerRequest;
import com.kenstevens.stratinit.server.rest.request.RequestFactory;
import com.kenstevens.stratinit.server.rest.request.write.GetBattleLogRequest;
import com.kenstevens.stratinit.server.rest.request.write.MoveUnitsRequest;
import com.kenstevens.stratinit.server.rest.svc.ErrorProcessor;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
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
    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private PlayerRoleRepo playerRoleRepo;

    @Test
    public void versionUnauthorized() throws Exception {
        mockMvc.perform(get(SIRestPaths.BASE_PATH + SIRestPaths.VERSION).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(HAPPY_USER)
    public void version() throws Exception {
        performGet(SIRestPaths.VERSION)
                .andExpect(jsonPath("value", is(Constants.SERVER_VERSION)));
    }

    @Test
    @WithUserDetails(HAPPY_USER)
    public void battleLog() throws Exception {
        GetBattleLogRequest mockRequest = mockListResponse(GetBattleLogRequest.class);
        when(requestFactory.getGetBattleLogRequest()).thenReturn(mockRequest);
        performGet(SIRestPaths.BATTLE_LOG).andExpect(jsonPath("value", empty()));
    }

    @Test
    @WithUserDetails(HAPPY_USER)
    public void city() throws Exception {
        GetCitiesRequest mockRequest = mockListResponse(GetCitiesRequest.class);
        when(requestFactory.getGetCitiesRequest()).thenReturn(mockRequest);
        performGet(SIRestPaths.CITY).andExpect(jsonPath("value", empty()));
    }

    @Test
    @WithUserDetails(HAPPY_USER)
    public void seenCity() throws Exception {
        GetSeenCitiesRequest mockRequest = mockListResponse(GetSeenCitiesRequest.class);
        when(requestFactory.getGetSeenCitiesRequest()).thenReturn(mockRequest);
        performGet(SIRestPaths.CITY_SEEN).andExpect(jsonPath("value", empty()));
    }

    @Test
    @WithUserDetails(HAPPY_USER)
    public void moveUnits() throws Exception {
        SIUpdate response = new SIUpdate();
        response.nationId = 2401;
        MoveUnitsRequest mockRequest = mockResponse(MoveUnitsRequest.class, response);
        when(requestFactory.getMoveUnitsRequest(any(), any())).thenReturn(mockRequest);

        SIUnit siunit = new SIUnit();
        List<SIUnit> list = List.of(siunit);
        SectorCoords coords = new SectorCoords();
        MoveUnitsJson request = new MoveUnitsJson(list, coords);

        performPost(SIRestPaths.MOVE_UNITS, request)
                .andExpect(jsonPath("value.nationId", is(2401)));
    }

    private <T extends PlayerRequest> T mockListResponse(Class<T> classToMock) {
        T request = mock(classToMock);
        when(request.process()).thenReturn(Result.make(new ArrayList<>()));
        return request;
    }

    private <T extends PlayerRequest> T mockResponse(Class<T> classToMock, StratInitDTO response) {
        T request = mock(classToMock);
        when(request.process()).thenReturn(Result.make(response));
        return request;
    }

    private ResultActions performGet(String path) throws Exception {
        return mockMvc.perform(get(SIRestPaths.BASE_PATH + path).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private ResultActions performPost(String path, IRestRequestJson request) throws Exception {
        String jsonRequest = mapper.writeValueAsString(request);
        return mockMvc.perform(post(SIRestPaths.BASE_PATH + path)
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Configuration
    @Import(RestWebSecurityAdapterConfig.class)
    static class Config {
        @Bean
        @Primary
        UserDetailsService mockUserDetailsService() {
            UserDetailsService retval = mock(UserDetailsService.class);
            List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(PlayerRole.ROLE_USER));
            UserDetails result = new User(HAPPY_USER, HAPPY_USER_PASSWORD, roles);
            when(retval.loadUserByUsername(HAPPY_USER)).thenReturn(result);
            return retval;
        }

        @Bean
        GameController gameController() {
            return new GameController();
        }

        @Bean
        UnitController unitController() {
            return new UnitController();
        }

        @Bean
        CityController cityController() {
            return new CityController();
        }

        @Bean
        NationController nationController() {
            return new NationController();
        }

        @Bean
        MessageController messageController() {
            return new MessageController();
        }
    }
}
