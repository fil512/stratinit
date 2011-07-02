package com.kenstevens.stratinit.server.gwtservice;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.kenstevens.stratinit.client.gwt.callback.ServiceSecurityException;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.Result;


public abstract class GWTBaseServiceImpl extends RemoteServiceServlet implements
		Controller, ServletContextAware {

	private static final long serialVersionUID = -8869097384470124667L;
	protected ServletContext servletContext;
	protected SecurityContext securityContext;
	protected HttpSession session;
	@Autowired
	private PlayerDao playerDao;

	public final ModelAndView handleRequest(HttpServletRequest req,
			HttpServletResponse resp) {
		session = req.getSession();
		doPost(req, resp);
		return null;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	protected String getUsername() throws ServiceSecurityException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new ServiceSecurityException();
		}
		return authentication.getName();
	}

	protected Result<Player> getPlayerResult() throws ServiceSecurityException {
		String username = getUsername();
		if (username == null) {
			return new Result<Player>("no username in authorization.", false);
		}
		Player player = playerDao.find(username);
		if (player == null) {
			return new Result<Player>("no player with username ["+username+"].", false);
		}
		return new Result<Player>(player);
	}

	protected Player getPlayer() throws ServiceSecurityException {
		String username = getUsername();
		if (username == null) {
			return null;
		}
		return playerDao.find(username);
	}


	protected boolean isAdmin() {
		boolean isAdmin = false;
		GrantedAuthority[] authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		for (GrantedAuthority authority : authorities) {
			if ("ROLE_ADMIN".equals(authority.getAuthority())) {
				isAdmin = true;
				break;
			}
		}
		return isAdmin;
	}


}