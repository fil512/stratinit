package com.kenstevens.stratinit.client.gwt.service;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.kenstevens.stratinit.client.gwt.callback.ServiceSecurityException;
import com.kenstevens.stratinit.client.gwt.model.GWTPlayer;

@RemoteServiceRelativePath("player.rpc")
public interface GWTPlayerService extends RemoteService {
	List<GWTPlayer> fetch() throws ServiceSecurityException;

	public static final class Util {
		private static GWTPlayerServiceAsync instance;

		private Util() {}

		public static GWTPlayerServiceAsync getInstance() {
			if (instance == null) {
				instance = GWT.create(GWTPlayerService.class);
			}
			return instance;
		}
	}
}