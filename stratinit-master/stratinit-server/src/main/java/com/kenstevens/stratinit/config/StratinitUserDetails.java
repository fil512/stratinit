package com.kenstevens.stratinit.config;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.PlayerRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StratinitUserDetails implements UserDetails {
    private final Player player;
    private final List<GrantedAuthority> roles = new ArrayList<>();

    public StratinitUserDetails(Player player, List<PlayerRole> playerRoles) {
        this.player = player;
        playerRoles.forEach(pr -> roles.add(new SimpleGrantedAuthority(pr.getRoleName())));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return player.getPassword();
    }

    @Override
    public String getUsername() {
        return player.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return player.isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return player.isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return player.isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return player.isEnabled();
    }
}
