package com.kenstevens.stratinit.dal;

import com.kenstevens.stratinit.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerDal extends JpaRepository<Player, Integer> {
	void deleteByUsername(String username);

	Player findByUsername(String username);
}
