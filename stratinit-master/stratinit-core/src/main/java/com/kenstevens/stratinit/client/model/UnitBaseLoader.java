package com.kenstevens.stratinit.client.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.UnitType;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class UnitBaseLoader {

    static Map<UnitType, UnitBase> load() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = UnitBaseLoader.class.getResourceAsStream("/unit-definitions.json")) {
            if (is == null) {
                throw new IllegalStateException("unit-definitions.json not found on classpath");
            }
            List<UnitDef> defs = mapper.readValue(is,
                    mapper.getTypeFactory().constructCollectionType(List.class, UnitDef.class));
            Map<UnitType, UnitBase> map = new LinkedHashMap<>();
            for (UnitDef def : defs) {
                UnitBase unitBase = createUnitBase(def);
                applyOptionalAttributes(unitBase, def);
                map.put(def.type, unitBase);
            }
            return map;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load unit-definitions.json", e);
        }
    }

    private static UnitBase createUnitBase(UnitDef def) {
        return switch (def.category) {
            case "LAND" -> new LandUnitBase(def.type, def.tech, def.productionTime,
                    def.mobility, def.sight, def.attack, def.ammo, def.hp);
            case "NAVY" -> new NavalUnitBase(def.type, def.tech, def.productionTime,
                    def.mobility, def.sight, def.attack, def.ammo, def.hp);
            case "AIR" -> new AirUnitBase(def.type, def.tech, def.productionTime,
                    def.mobility, def.sight, def.attack, def.ammo, def.hp);
            case "TECH" -> new TechUnitBase(def.type, def.tech, def.productionTime,
                    def.mobility, def.sight, def.attack, def.ammo, def.hp);
            case "BASE" -> new BaseUnitBase(def.type, def.tech, def.productionTime,
                    def.mobility, def.sight, def.attack, def.ammo, def.hp);
            default -> throw new IllegalArgumentException("Unknown category: " + def.category);
        };
    }

    private static void applyOptionalAttributes(UnitBase ub, UnitDef def) {
        if (def.weight != 0) ub.setWeight(def.weight);
        if (def.capacity != 0) ub.setCapacity(def.capacity);
        if (def.flak != 0) ub.setFlak(def.flak);
        if (def.bombPercentage != 0) ub.setBombPercentage(def.bombPercentage);
        if (def.blastRadius != 0) ub.setBlastRadius(def.blastRadius);
        if (def.canSeeSubs) ub.setCanSeeSubs(true);
        if (def.lightAir) ub.setLightAir(true);
        if (def.navyCapital) ub.setNavyCapital(true);
        if (def.navyCanAttackLand) ub.setNavyCanAttackLand(true);
        if (def.landCanAttackShips) ub.setLandCanAttackShips(true);
        if (def.launchable) ub.setLaunchable(true);
        if (def.devastates) ub.setDevastates(true);
        if (def.suppliesLand) ub.setSuppliesLand(true);
        if (def.suppliesNavy) ub.setSuppliesNavy(true);
        if (def.base) ub.setBase(true);
        if (!def.requiresSupply) ub.setRequiresSupply(false);
        if (def.builtIn != null) ub.setBuiltIn(CityType.valueOf(def.builtIn));
    }

    static class UnitDef {
        public UnitType type;
        public String category;
        public int tech;
        public int productionTime;
        public int mobility;
        public int sight;
        public int attack;
        public int ammo;
        public int hp;
        public int weight;
        public int capacity;
        public int flak;
        public int bombPercentage;
        public int blastRadius;
        public boolean canSeeSubs;
        public boolean lightAir;
        public boolean navyCapital;
        public boolean navyCanAttackLand;
        public boolean landCanAttackShips;
        public boolean launchable;
        public boolean devastates;
        public boolean suppliesLand;
        public boolean suppliesNavy;
        public boolean base;
        public boolean requiresSupply = true;
        public String builtIn;
    }
}
