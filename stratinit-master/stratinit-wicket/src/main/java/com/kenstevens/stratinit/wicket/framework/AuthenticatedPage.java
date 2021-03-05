package com.kenstevens.stratinit.wicket.framework;

import com.kenstevens.stratinit.client.model.PlayerRole;
import com.kenstevens.stratinit.wicket.base.BasePage;

public abstract class AuthenticatedPage extends BasePage implements AuthenticatedComponent {

    private static final long serialVersionUID = -8358571925296406622L;

    @Override
    public boolean isSignedIn() {
        return AuthHelper.isSignedIn();
    }

    @Override
    public boolean isAdmin() {
        return AuthHelper.getRoles().contains(PlayerRole.ROLE_ADMIN);
    }

    @Override
    public String getUsername() {
        return AuthHelper.getUsername();
    }
}
