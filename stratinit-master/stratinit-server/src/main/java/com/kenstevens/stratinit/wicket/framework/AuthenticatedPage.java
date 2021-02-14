package com.kenstevens.stratinit.wicket.framework;

import com.kenstevens.stratinit.model.PlayerRole;
import com.kenstevens.stratinit.wicket.base.BasePage;

public abstract class AuthenticatedPage extends BasePage implements AuthenticatedComponent {

    private static final long serialVersionUID = -8358571925296406622L;

    @Override
    public boolean isSignedIn() {
        return getAuth().isSignedIn();
    }

    @Override
    public boolean isAdmin() {
        return getAuth().getRoles().contains(PlayerRole.ROLE_ADMIN);
    }

    // FIXME not id
    @Override
    public String getUsername() {
        return (String) getAuth().getAttribute("username");
    }

}
