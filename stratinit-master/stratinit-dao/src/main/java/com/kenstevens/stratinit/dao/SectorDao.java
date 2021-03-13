package com.kenstevens.stratinit.dao;

import com.google.common.collect.Collections2;
import com.kenstevens.stratinit.cache.NationCache;
import com.kenstevens.stratinit.cache.NationCacheToNationFunction;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.dao.predicates.OtherNationPredicate;
import com.kenstevens.stratinit.dao.predicates.SeesSectorPredicate;
import com.kenstevens.stratinit.repo.SectorRepo;
import com.kenstevens.stratinit.repo.SectorSeenRepo;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Predicates.and;

@Service
public class SectorDao extends CacheDao {
    @Autowired
    private SectorRepo sectorRepo;
    @Autowired
    private SectorSeenRepo sectorSeenRepo;

    public SectorSeen findSectorSeen(Nation nation, SectorCoords coords) {
        return getNationCache(nation).getSectorSeen(coords);
    }

    public SectorSeen findSectorSeen(Nation nation, Sector sector) {
        return findSectorSeen(nation, sector.getCoords());
    }

    public Collection<Sector> getNationSectorsSeenSectors(Nation nation) {
        return getNationCache(nation).getSectorsSeenSectors(
                getGameCache(nation).getWorld());
    }

    public Collection<Nation> getOtherNationsThatSeeThisSector(Nation nation,
                                                               SectorCoords coords) {

        List<NationCache> nationCaches = getGameCache(nation).getNationCaches();
        return Collections2.transform(Collections2.filter(
                nationCaches,
                and(new OtherNationPredicate(nation), new SeesSectorPredicate(
                        coords))), new NationCacheToNationFunction());
    }

    public Collection<SectorSeen> getSectorsSeen(Nation nation) {
        return getNationCache(nation).getSectorsSeen();
    }


    public World getWorld(Game game) {
        return getGameCache(game.getId()).getWorld();
    }

    public void markCacheModified(SectorSeen sectorSeen) {
        getNationCache(sectorSeen.getNation()).setSectorSeenModified(true);
    }

    public void markCacheModified(Sector sector) {
        getGameCache(sector.getGame()).setWorldModified(true);
    }

    public void save(SectorSeen sectorSeen) {
        sectorSeenRepo.save(sectorSeen);
        getNationCache(sectorSeen.getNation()).add(sectorSeen);
    }

    @Transactional
    public void save(World world) {
        for (Sector sector : world.getSectors()) {
            sectorRepo.save(sector);
        }
        getGameCache(world.getGame()).setWorld(world);
    }


    public void saveIfNew(Nation nation, Sector sector) {
        SectorSeen foundSectorSeen = findSectorSeen(nation, sector);
        if (foundSectorSeen == null) {
            SectorSeen sectorSeen = new SectorSeen(nation, sector);
            save(sectorSeen);
        }
    }



}
