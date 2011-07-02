package com.kenstevens.stratinit.server.gwtservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.client.gwt.service.GWTNone;
import com.kenstevens.stratinit.client.gwt.service.GWTRegisterService;
import com.kenstevens.stratinit.client.gwt.service.GWTResult;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.PlayerDaoService;


@Service("registerService")
public class GWTRegisterServiceImpl extends GWTBaseServiceImpl implements
		GWTRegisterService {


	private static final long serialVersionUID = -3847406564429960726L;
	@Autowired
	private PlayerDaoService playerDaoService;
	
	public GWTResult<GWTNone> register(String username, String password,
			String email) {
		ShaPasswordEncoder encoder = new ShaPasswordEncoder(); 
		Result<Player> result = playerDaoService.register(username, encoder.encodePassword(password, null), email);
		return new GWTResult<GWTNone>(result.getMessages(), result.isSuccess());
	}
	
	@Override
	public GWTResult<GWTNone> forgottenPassword(String username, String email) {
		Result<None> result = playerDaoService.forgottenPassword(username, email);
		return new GWTResult<GWTNone>(result.getMessages(), result.isSuccess());
	}
}
