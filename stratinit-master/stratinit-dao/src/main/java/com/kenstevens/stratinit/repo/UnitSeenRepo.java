package com.kenstevens.stratinit.repo;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeen;
import com.kenstevens.stratinit.model.UnitSeenPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitSeenRepo extends JpaRepository<UnitSeen, UnitSeenPK> {
    @Modifying
    @Query("delete from UnitSeen us where us.unitSeenPK.unit = :unit")
    void deleteByUnit(@Param("unit") Unit unit);
}
