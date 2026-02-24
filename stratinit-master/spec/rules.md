# Strategic Initiative

## Rules

### 1. The Map

The map consists of islands and water. Islands are either land or a city. A player may designate a city to be one of four things:

- **Fort.** This is where land units are built. Units in forts take half damage from all attacks, rounded down. Forts have 2 flak and 2 cannons.
- **Port.** Ports must be designated next to water. Ports are where ships are built. Ports also provide supply to ships (see "Supply" below). Ports have 4 cannons.
- **Airport.** Airports are where planes are built. Air units are resupplied in airports. Airports have 4 flak. Airports have a sight radius of 3.
- **Technology Center.** Tech centres are where satellites, missiles, engineers, and zeppelins are built. Tech centres also increase a nation's technology level (see "Technology" below). Lastly, tech centres have radar dishes that can see all sectors and units within a radius equal to the nation's technology level.

All other cities (bases) have 1 flak and 1 cannon.

### 2. Units

Strategic Initiative has the following units.

#### Land Units

| Unit | Built in | Tech | Cost (hrs) | Mobility | Sight | Attack | Ammo | HP | Notes |
|------|----------|------|------------|----------|-------|--------|------|----|-------|
| Infantry | Fort | 0 | 8 | 2 | 1 | 2 | 1 | 5 | Weight 1. Can be loaded onto helicopter or cargo plane. |
| Tank | Fort | 6 | 10 | 3 | 1 | 3 | 1 | 9 | Weight 1. Can attack ships at sea. |
| Engineer | Tech | 0 | 16 | 3 | 1 | 0 | 10 | 4 | Weight 1. Supplies land units. Can carry 1 unit. Can build cities and switch terrain. |

#### Naval Units

| Unit | Built in | Tech | Cost (hrs) | Mobility | Sight | Attack | Ammo | Flak | HP | Notes |
|------|----------|------|------------|----------|-------|--------|------|------|----|-------|
| Transport | Port | 0 | 12 | 6 | 1 | 0 | 6 | | 8 | Capacity 6. Supplies land units. |
| Supply Ship | Port | 0 | 6 | 6 | 1 | 0 | 10 | | 10 | Supplies nearby ships and land units. Does not require supply itself. |
| Patrol Boat | Port | 0 | 8 | 8 | 2 | 1 | 1 | | 4 | Does not require supply. |
| Destroyer | Port | 3 | 14 | 8 | 2 | 5 | 2 | | 14 | Can see subs. |
| Battleship | Port | 4 | 20 | 6 | 2 | 12 | 4 | 4 | 40 | Capital ship. Can attack units on land. |
| Submarine | Port | 6 | 16 | 10 | 1 | 5 | 8 | | 12 | Can carry 1 unit. Can see subs. See "Sight and Subs" below. |
| Aircraft Carrier | Port | 7 | 20 | 6 | 1 | 4 | 2 | 4 | 24 | Capital ship. Acts as mobile airport. Capacity 8 light planes. |
| Cruiser | Port | 9 | 20 | 8 | 2 | 8 | 6 | 6 | 30 | Capital ship. Can see subs. |

#### Air Units

| Unit | Built in | Tech | Cost (hrs) | Mobility | Sight | Attack | Ammo | HP | Notes |
|------|----------|------|------------|----------|-------|--------|------|----|-------|
| Zeppelin | Tech | 0 | 12 | 5 | 3 | 0 | 4 | 2 | 10% bomb damage. Does not require fuel. |
| Fighter | Airport | 6 | 8 | 16 | 1 | 2 | 2 | 4 | Light air (weight 1). Intercepts enemy units. 2x damage vs non-fighter air. |
| Cargo Plane | Airport | 7 | 14 | 30 | 1 | 0 | 0 | 8 | Capacity 4. |
| Heavy Bomber | Airport | 7 | 18 | 30 | 1 | * | 2 | 8 | 50% bomb damage to all units in target. May not refuel on carriers. |
| Naval Bomber | Airport | 8 | 12 | 20 | 1 | 8* | 1 | 7 | Light air (weight 1). 25% bomb damage to land units. Can attack ships at sea. Can see subs. |
| Helicopter | Airport | 9 | 6 | 16 | 2 | 5 | 2 | 5 | Light air (weight 1). Capacity 2. Can see subs. 2x damage vs land units and subs. |

#### Tech Units

| Unit | Built in | Tech | Cost (hrs) | Mobility | Sight | Attack | HP | Notes |
|------|----------|------|------------|----------|-------|--------|----|-------|
| Base | Tech | 0 | 24 | 0 | 0 | 0 | 1 | Immobile structure. |
| Research | Tech | 0 | 24 | 0 | 0 | 0 | 1 | Immobile structure. |
| Spy Satellite | Tech | 10 | 24 | 100 | 6 | 0 | 4 | Invulnerable once launched. |
| ICBM 1 | Tech | 12 | 36 | 30 | 0 | * | 4 | 100% damage to all units at target. |
| ICBM 2 | Tech | 14 | 30 | 40 | 0 | * | 4 | 100% damage to all units within radius 1 of target. |
| ICBM 3 | Tech | 16 | 24 | 50 | 0 | * | 4 | 100% damage to all units within radius 2 of target. |

### 3. Cities

At any given time, a city will have one of four designations as listed in "The Map" above. A player may change the designation of a city at any time. Any time spent constructing the current unit will be lost when the city is redesignated. Cities have a unit currently being built in that city and, optionally, a unit specified to build in the city after the current unit is completed. Units in cities heal at a rate of 40% of their max HP every 4 hours.

### 4. Moving

At any given time, units will have a certain amount of mobility. This is the number of squares they can move on the map. Ships can only move through sea and ports, land can only move across land, and air units can fly anywhere. Units may not move into an enemy location unless the enemy is first defeated. Every 4 hours, mobility is added to each unit per the "Mobility" chart above to a maximum of 3 times this number. So, for example, a fighter can have a maximum of 48 mobility.

When a unit is out of supply, it costs 2 mobility to enter each sector (see "Supply" below).

Note that air units require fuel to move. See "Supply" below.

Units can be intercepted by fighters while moving (see "Interception" below).

Ships can be interdicted by other ships while moving (see "Interdiction" below). Only one ship may occupy a sea square at any given time.

### 5. Attacking

When a group of units is selected and a location containing enemy units is targeted, then all selected units will move adjacent to the target and then attack it. Note that in the process of moving towards the enemy, the attacking units may be intercepted or interdicted (see below). If the unit is already adjacent to the target and does not need to move, then it will be neither intercepted nor interdicted. A unit can only attack if it has mobility and ammo. Fuel is not required to attack. Each attack reduces mobility and ammo by 1. Units in supply have their ammo automatically replenished. Damage is random, ranging from half the Attack value to the full Attack value. After the attack damage is dealt, the defending unit counter-attacks. Note that counter-attacks consume ammo but do not cost mobility; the only thing that will prevent a unit from counter-attacking is if it is out of ammo.

Due to the ship stacking limit, only one ship may be moved at a time.

Neutral or empty enemy cities may only be captured by land units. Other units may not enter them.

Battleships are the only ships that can attack land units on land. Tanks are the only land units that may attack ships at sea. Units being carried (e.g. land units on a transport or planes on a carrier) may not attack or be attacked, the exception to this being a land unit taking an empty port.

If more than one unit is in a location, the most recently built unit with the highest attack multiplier will be attacked first. In order to attack an enemy unit or capture an enemy city, you must first be at war with that nation. However, your units will intercept and interdict units belonging any player you are not Friendly or Allied towards. Below are the attack multipliers.

- Fighters do double damage against non-fighter air units.
- Naval bombers do double damage against ships.
- Helicopters do double damage against land units and subs.
- Destroyers and cruisers do triple damage against subs.
- Submarines do triple damage against non-capital ships.
- Submarines do quadruple damage against capital ships.

### 6. Bombers, Flak, Satellites and ICBMs

When a bomber attacks a land sector, instead of attacking a single unit, all units in the sector are damaged. The amount of damage done to the unit is a percentage of the unit's remaining hp (as indicated in the chart above) rounded up. For example, a heavy bomber does 50% damage to infantry. One heavy bomber attack will take the infantry down to 3 hp. The next heavy bomber attack will take it to 2 hp, and so on.

Note that only naval bombers can bomb enemy ships at sea, in which case the attack value listed above is used instead of the bombing percentage. Zeppelins can also bomb, dealing 10% damage to all units in the target sector.

Whenever an air unit attacks a fort, the fort will first fire flak against the air unit before the air unit can attack the fort. Similarly, certain ships are armed with anti-air flak. Other city types also have defenses: airports have 4 flak, ports have 4 cannons, and base cities have 1 flak and 1 cannon.

Satellites and ICBMs are only ever launched once. When launching these units, the player indicates the target sector and then the unit that was on the ground disappears. In the case of ICBMs, the result is devastation on and around the target sector. In the case of satellites, the result is a new launched satellite unit in space that provides permanent visibility in a wide radius centred on its location. Launched satellites may not be attacked and are invisible to all players except for their owners.

### 7. Sight and Subs

Normally all units can see within the sight range indicated in the table above. Most cities have a sight range of 1. Airports have a sight range of 3. Tech centres have a sight radius equal to the nation's technology level. The one exception to this is subs. Subs can only be seen by naval bombers, destroyers, cruisers, helicopters, and other subs. If the sub attacks, however, it will become visible to the nation it attacked. Once a unit has been seen, it will remain visible for 24 hours, except for subs which only remain visible for 8 hours. An enemy sub will become visible to a player's ship or plane that tries to move onto the location the enemy sub occupies.

### 8. Supply

Ships are supplied by allied ports and supply ships. A ship is in supply if it can reach supply in 6 moves or less. Land units are supplied by supply ships, transports, engineers, or allied cities. A land unit is in supply if it can reach supply in 6 moves or less.

Units in supply:

- Have normal movement.
- Ammo automatically replenished.
- Regain hit points at a rate of 20% of max HP every 4 hours to a maximum of 80%.

Units out of supply:

- Pay twice the cost to attack or move.
- Run out of ammo and can't attack any more.

Air units don't have supply per se, but they do top up their ammo and fuel in any allied airport or carrier. When an air unit is refueled, it gains fuel equal to the mobility value listed in the chart above. Heavy bombers may not refuel on carriers.

### 9. Transport

Per the chart above, some units may carry other units.

Transports have a carrying capacity of 6, with infantry and tanks each weighing 1.

Helicopters can carry 2 units.

Cargo planes can carry 4 units.

Carriers have a capacity of 8 with fighters, helicopters weighing 1 and naval bombers weighing 2.

Submarines can carry 1 unit.

### 10. Battle Formation and Sea Interdiction

A ship is a part of a Battle Formation if it is within 6 sectors (as the crow flies) of a port or a Capital ship. The following ships are considered Capital ships:

- Supply ship
- Aircraft Carrier
- Battleship
- Cruiser

When a ship is a part of a Battle Formation, it will automatically close in on and fire upon any enemy ship (except an invisible sub) that moves within its line of sight, provided the ship has sufficient mobility to move and has ammo.

Thus an aircraft carrier can be protected from subs by a ring of destroyers. Or a port can be protected against invading transports by one or two destroyers.

Destroyers, cruisers, and battleships have a sight radius of 2. When one of these ships sees an enemy ship move within 2 squares of it, it tries to move so it is adjacent to the enemy ship and then fires once on it.

Note that interdiction movement never triggers interdiction itself.

### 11. Fighter Interception

When any enemy unit moves within 6 squares of your fighters, and you are able to see the enemy units, then your fighters will scramble to defend your air space. The number of fighters scrambled will be equal to the number of enemy units approaching. Note that when fighters intercept, they are only charged for the mobility to fly out. The mobility cost for interceptors to return to where they started is free.

Note that interception never triggers interception itself.

It is generally a good idea to probe air defenses with fighters before sending bombers or helicopters in.

### 12. Technology

All nations start the game with a technology value of 0. Each day, that technology level increases according to the number of cities the player has designated to be Technology Centres according to the table below:

| Tech Centres | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | 14+ |
|--------------|---|---|---|---|---|---|---|---|---|---|----|----|----|----|-----|
| Daily increase | 0 | 0.8 | 1.2 | 1.4 | 1.5 | 1.6 | 1.7 | 1.8 | 1.9 | 2.0 | 2.1 | 2.2 | 2.3 | 2.4 | 2.5 |

Note that technology accrues gradually (calculated every 15 minutes) throughout the day based on the number of Technology Centres.

Players may only build units for which their nation has sufficient technology. Per the "Map" section above, tech level also increases the sight radius of tech centres.

Technology is "leaked" from other players according to the following formula:

- From the most advanced player: ((top tech) - (my tech)) / 4 per day
- From allies: ((ally tech) - (my tech)) / 2 per day

The higher of the two values is used. For example, if the top tech is 8 and my tech is 4, then the general leak will be (8 - 4) / 4 = 1 per day. If my ally's tech is 6, the ally leak would be (6 - 4) / 2 = 1 per day. The higher value applies.

The maximum technology a nation can have is 16.

### 13. Diplomacy

Nations can have 5 different levels of diplomacy. Whenever your relations towards another nation are downgraded, their relations towards you will also automatically downgrade so that their relation towards you is at least as low as your relation towards them. Each player may have at most 1 ally.

- **War.** You may only attack cities and units belonging to nations you are at war with.
- **Neutral.** Same as non-aggression below, but can declare war at any time. Players start with neutral relations towards one another.
- **Non-aggression.** Players may not attack one another. May be cancelled with 24 hours notice. Units still interdict and intercept.
- **Friendly.** Players may not attack one another. May be cancelled with 24 hours notice. No interdiction or interception.
- **Allied.** Same as friendly except players share supply and air refueling. May be degraded to Friendly without notice. Allies automatically share maps and lines of sight. If your ally sees an enemy unit, you will see it as well.

### 14. Command Points

All actions cost command points. Players start with 512 command points and can accumulate a maximum of 1024. Command points regenerate every 15 minutes based on the number of cities, capital ships, and bases controlled.

| Action | Cost |
|--------|------|
| Move | 1 |
| Attack | 2 |
| Capture city | 4 |
| Launch satellite | 8 |
| Switch terrain | 16 |
| Launch ICBM | 32 |
| Build city | 128 |

### 15. Engineers

Engineers are land units built in Technology Centres. They can perform two special actions:

- **Build City.** An engineer on a land sector (not adjacent to an enemy city) can found a new city, costing 9 mobility and 128 command points.
- **Switch Terrain.** An engineer can convert a land sector to water or vice versa, costing 6 mobility and 16 command points.

Engineers also supply nearby land units.
