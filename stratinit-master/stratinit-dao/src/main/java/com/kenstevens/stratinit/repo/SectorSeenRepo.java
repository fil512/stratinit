package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.SectorSeen;
import com.kenstevens.stratinit.model.SectorSeenPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectorSeenRepo extends JpaRepository<SectorSeen, SectorSeenPK> {
    @Query("Select ss from SectorSeen ss WHERE ss.sectorSeenPK.nation.nationPK.game = :game")
    List<SectorSeen> findByGame(@Param("game") Game game);
}
