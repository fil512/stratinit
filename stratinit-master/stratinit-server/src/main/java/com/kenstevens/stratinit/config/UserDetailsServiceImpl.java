package com.kenstevens.stratinit.config;

import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.repo.PlayerRepo;
import com.kenstevens.stratinit.repo.PlayerRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    PlayerRepo playerRepo;
    @Autowired
    PlayerRoleRepo playerRoleRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player player = playerRepo.findByUsername(username);
        if (player == null) {
            throw new UsernameNotFoundException("Player not found");
        }
        return new StratinitUserDetails(player, playerRoleRepo.findByPlayer(player));
    }
}
