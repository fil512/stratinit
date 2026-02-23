package com.kenstevens.stratinit.server.bot;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.server.service.RelationService;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BotWorldStateBuilder {
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private CityDao cityDao;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private SectorDao sectorDao;
    @Autowired
    private RelationService relationService;

    public BotWorldState build(Nation nation) {
        Game game = gameDao.findGame(nation.getGameId());
        Collection<Unit> visibleUnits = unitDao.getSeenUnits(nation);
        Map<Nation, RelationType> myRelations = relationService.getMyRelationsAsMap(nation);
        Map<Nation, RelationType> theirRelations = relationService.getTheirRelationTypesAsMap(nation);
        List<City> myCities = cityDao.getCities(nation);

        World world = sectorDao.getWorld(game);
        Set<SectorCoords> coastalCityCoords = computeCoastalCityCoords(myCities, world);
        List<City> enemyCities = computeSeenEnemyCities(nation, myRelations);
        Set<SectorCoords> scoutedCoords = computeScoutedCoords(nation);
        List<SectorCoords> neutralCityCoords = computeNeutralCityCoords(nation);

        return new BotWorldState(
                nation,
                game,
                unitDao.getUnits(nation),
                myCities,
                visibleUnits,
                myRelations,
                theirRelations,
                coastalCityCoords,
                enemyCities,
                world,
                scoutedCoords,
                neutralCityCoords
        );
    }

    public BotWorldState build(Nation nation, long simulatedTimeMillis) {
        Game game = gameDao.findGame(nation.getGameId());
        Collection<Unit> visibleUnits = unitDao.getSeenUnits(nation);
        Map<Nation, RelationType> myRelations = relationService.getMyRelationsAsMap(nation);
        Map<Nation, RelationType> theirRelations = relationService.getTheirRelationTypesAsMap(nation);
        List<City> myCities = cityDao.getCities(nation);

        World world = sectorDao.getWorld(game);
        Set<SectorCoords> coastalCityCoords = computeCoastalCityCoords(myCities, world);
        List<City> enemyCities = computeSeenEnemyCities(nation, myRelations);
        Set<SectorCoords> scoutedCoords = computeScoutedCoords(nation);
        List<SectorCoords> neutralCityCoords = computeNeutralCityCoords(nation);

        return new BotWorldState(
                nation,
                game,
                unitDao.getUnits(nation),
                myCities,
                visibleUnits,
                myRelations,
                theirRelations,
                coastalCityCoords,
                enemyCities,
                world,
                scoutedCoords,
                neutralCityCoords,
                simulatedTimeMillis
        );
    }

    private Set<SectorCoords> computeCoastalCityCoords(List<City> myCities, World world) {
        Set<SectorCoords> coastal = new HashSet<>();
        for (City city : myCities) {
            List<Sector> neighbours = world.getNeighbours(city.getCoords());
            boolean hasWater = neighbours.stream().anyMatch(Sector::isWater);
            if (hasWater) {
                coastal.add(city.getCoords());
            }
        }
        return coastal;
    }

    private Set<SectorCoords> computeScoutedCoords(Nation nation) {
        return sectorDao.getSectorsSeen(nation).stream()
                .map(SectorSeen::getCoords)
                .collect(Collectors.toSet());
    }

    private List<SectorCoords> computeNeutralCityCoords(Nation nation) {
        return sectorDao.getNationSectorsSeenSectors(nation).stream()
                .filter(sector -> sector.getType() == SectorType.NEUTRAL_CITY)
                .map(Sector::getCoords)
                .collect(Collectors.toList());
    }

    private List<City> computeSeenEnemyCities(Nation nation, Map<Nation, RelationType> myRelations) {
        List<City> seenCities = cityDao.getSeenCities(nation);
        return seenCities.stream()
                .filter(c -> c.getNation() != null && !c.getNation().equals(nation))
                .filter(c -> {
                    RelationType rel = myRelations.get(c.getNation());
                    return rel == null || rel == RelationType.WAR;
                })
                .collect(Collectors.toList());
    }
}
