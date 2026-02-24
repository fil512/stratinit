# Strategic Initiative

## Rules

### 1. The Map

The map consists of islands and water. Islands are either land or a city. A player may designate a city to be one of four things:

- **Fort.** This is where land units are built. Units in forts take half damage from all attacks, rounded down.
- **Port.** Ports must be designated next to water. Ports are where ships are built. Ports also provide supply to ships (see "Supply" below).
- **Airport.** Airports are where planes are built. Air units are resupplied in airports.
- **Technology Center.** Tech centres are where satellites and missiles are built. Tech centres also increase a nation's technology level (see "Technology" below). Lastly, tech centres have radar dishes that can see all sectors and units within a radius equal to the nation's technology level.

### 2. Units

Strategic Initiative has the following units.

| Unit | Built in | Tech | Cost (hrs) | Mobility | Sight | Attack | Ammo | Flak | HP | Notes |
|------|----------|------|------------|----------|-------|--------|------|------|----|-------|
| Fort | | | | | | | | 4 | | |
| Infantry | Fort | 0 | 8 | 2 | 1 | 2 | 1 | | 5 | Can be loaded onto helicopter |
| Tank | Fort | 4 | 12 | 3 | 1 | 3 | 1 | | 5 | Can attack ships at sea. |
| Transport | Port | 0 | 12 | 6 | 1 | 0 | | | 10 | Can carry land units. Supplies infantry. |
| Patrol boat | Port | 0 | 10 | 12 | 2 | 0 | | | 4 | |
| Destroyer | Port | 2 | 14 | 8 | 2 | 5 | 3 | | 14 | |
| Supply ship | Port | 3 | 6 | 6 | 1 | 0 | | | 10 | Supplies nearby ships, Capital ship |
| Battleship | Port | 4 | 30 | 6 | 1 | 12 | 6 | 4 | 40 | Capital ship. Can attack units on land. |
| Submarine | Port | 5 | 16 | 5 | 1 | 5 | 6 | | 12 | See "Sight and Subs" below |
| Aircraft Carrier | Port | 5 | 20 | 6 | 1 | 4 | 3 | 4 | 24 | Acts as mobile airport, Capital ship. Can carry light planes |
| Cruiser | Port | 8 | 24 | 8 | 2 | 8 | 6 | 6 | 30 | Capital ship |
| Zeppelin | Tech | 0 | 24 | 6 | 3 | 0 | | | 4 | An air unit without fuel |
| Fighter | Airport | 3 | 10 | 20 | 2 | 2 | 2 | | 4 | Intercepts enemy units |
| Naval bomber | Airport | 4 | 14 | 16 | 1 | 6* | | | 6 | 25% damage to all land units in target. Can attack ships at sea. Can see subs. |
| Helicopter | Airport | 5 | 10 | 12 | 1 | 2 | 3 | | 6 | Can carry 1 infantry. |
| Heavy Bomber | Airport | 6 | 24 | 30 | 1 | * | | | 8 | 50% damage to all units in target |
| Spy Satellite | Tech | 7 | 36 | 100 | 6 | 0 | | | 4 | Invulnerable once launched |
| ICBM 1 | Tech | 8 | 36 | 30 | 0 | * | | | 4 | 100% damage to all units in target |
| ICBM 2 | Tech | 9 | 30 | 40 | 0 | * | | | 4 | 100% damage to all units radius 1 of target |
| ICBM 3 | Tech | 10 | 24 | 50 | 0 | * | | | 4 | 100% damage to all units radius 2 of target |

### 3. Cities

At any given time, a city will have one of four designations as listed in "The Map" above. A player may change the designation of a city at any time. Any time spent constructing the current unit will be lost when the city is redesignated. Cities have a unit currently being built in that city and, optionally, a unit specified to build in the city after the current unit is completed. Units in cities heal at a rate of 2 hp every 4 hours.

### 4. Moving

At any given time, units will have a certain amount of mobility. This is the number of squares they can move on the map. Ships can only move through sea and ports, land can only move across land, and air units can fly anywhere. Units may not move into an enemy location unless the enemy is first defeated. Every 4 hours, mobility is added to each unit per the "Mobility" chart above to a maximum of 3 times this number. So, for example, a fighter can have a maximum of 60 mobility.

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

- Light bombers do double-damage against ships
- Helicopters do double-damage against land units.
- Destroyers do triple damage against subs.
- Submarines do triple damage against non-capital ships.
- Submarines do quadruple damage against capital ships.

### 6. Bombers, Flak, Satellites and ICBMs

When a bomber attacks a land sector, instead of attacking a single unit, all units in the sector are damaged. The amount of damage done to the unit is a percentage of the unit's remaining hp (as indicated in the chart above) rounded up. For example, a heavy bomber does 50% damage to infantry. One heavy bomber attack will take the infantry down to 2 hp. The next heavy bomber attack will take it to 1 hp, and a third heavy bomber will kill it.

Note that only naval bombers can bomb enemy ships at sea, in which case the attack value listed above is used instead of the bombing percentage.

Whenever an air unit attacks a fort, the fort will first fire flak against the air unit before the air unit can attack the fort. Similarly, certain ships are armed with anti-air flak.

Satellites and ICBMs are only ever launched once. When launching these units, the player indicates the target sector and then the unit that was on the ground disappears. In the case of ICBMs, the result is devastation on and around the target sector. In the case of satellites, the result is a new launched satellite unit in space that provides permanent visibility in a wide radius centred on its location. Launched satellites may not be attacked and are invisible to all players except for their owners.

### 7. Sight and Subs

Normally all units can see within the sight range indicated in the table above. Most cities have a sight range of 1. Tech centres have a sight radius equal to the nation's technology level. The one exception to this is subs. Subs can only be seen by naval bombers, destroyers, cruisers, and other subs. If the sub attacks, however, it will become visible to the nation it attacked. Once a unit has been seen, it will remain visible for 24 hours, except for subs which only remain visible for 8 hours. An enemy sub will become visible to a player's ship or plane that tries to move onto the location the enemy sub occupies.

### 8. Supply

Ships are supplied by allied ports and supply ships. A ship is in supply if it can reach supply in 5 moves or less. Land units are supplied by supply ships, transports, or allied cities. A land unit is in supply if it can reach supply in 5 moves or less.

Units in supply:

- Have normal movement.
- Ammo automatically replenished.
- Regain hit points at a rate of 1 every 4 hours to a maximum of 80%.

Units out of supply:

- Pay twice the cost to attack or move.
- Run out of ammo and can't attack any more.

Air units don't have supply per se, but they do top up their ammo and fuel in any allied airport or carrier. When an air unit is refueled, it gains fuel equal to the mobility value listed in the chart above. Heavy bombers may not refuel on carriers.

### 9. Transport

Per the chart above, some units may carry other units.

Transports have a carrying capacity of 6, with infantry weighing 1 and tanks weighing 2.

Helicopters can carry 1 infantry.

Carriers have a capacity of 8 with fighters and helicopters weighing 1 and naval bombers weighing 2.

### 10. Battle Formation and Sea Interdiction

A ship is a part of a Battle Formation if it is within 5 sectors (as the crow flies) of a port or a Capital ship. The following ships are considered Capital ships:

- Supply ship
- Aircraft Carrier
- Battleship
- Cruiser

When a ship is a part of a Battle Formation, it will automatically close in on and fire upon any enemy ship (except an invisible sub) that moves within its line of sight, provided the ship has sufficient mobility to move and has ammo.

Thus an aircraft carrier can be protected from subs by a ring of destroyers. Or a port can be protected against invading transports by one or two destroyers.

Destroyers and cruisers are the only ships in the game with a sight radius of 2. When a destroyer or cruiser sees an enemy ship move within 2 squares of it, the destroyer or cruiser tries to move so it is adjacent to the enemy ship and then fires once on it.

Note that interdiction movement never triggers interdiction itself.

### 11. Fighter Interception

When any enemy unit moves within 5 squares of your fighters, and you are able to see the enemy units, then your fighters will scramble to defend your air space. The number of fighters scrambled will be equal to the number of enemy units approaching. Note that when fighters intercept, they are only charged for the mobility to fly out. The mobility cost for interceptors to return to where they started is free.

Note that interception never triggers interception itself.

It is generally a good idea to probe air defenses with fighters before sending bombers or helicopters in.

### 12. Technology

All nations start the game with a technology value of 0. Each day, that technology level increases according to the number of cities the player has designated to be Technology Centres according to the table below:

| Number of Technology Centres | 0 | 1 | 2 | 3 | 4+ |
|------------------------------|---|---|-----|------|-----|
| Technology increase | 0 | 1 | 1.5 | 1.75 | 2 |

Note that technology accrues gradually (calculated every 5 minutes) throughout the day based on the number of Technology Centres.

Players may only build units for which their nation has sufficient technology. Per the "Map" section above, tech level also increases the sight radius of tech centres.

Technology is "leaked" from the technologically most advanced player to the rest of the players according to the following formula.

My Tech Leak = ((top tech) - (my tech)) / 2 per day

For example, if the top tech is 5 and my tech is 3, then my tech leak will be (5 - 3) / 2 = 1 per day

The maximum technology a nation can have is 10.

### 13. Diplomacy

Nations can have 5 different levels of diplomacy. Whenever your relations towards another nation are downgraded, their relations towards you will also automatically downgrade so that their relation towards you is at least as low as your relation towards them.

- **War.** You may only attack cities and units belonging to nations you are at war with.
- **Neutral.** Same as non-aggression below, but can declare war at any time. Players start with neutral relations towards one another.
- **Non-aggression.** Players may not attack one another. May be cancelled with 24 hours notice. Units still interdict and intercept.
- **Friendly.** Players may not attack one another. May be cancelled with 48 hours notice. No interdiction or interception.
- **Allied.** Same as friendly except players share supply and air refueling. May be degraded to Friendly without notice. Allies automatically share maps and lines of sight. If your ally sees an enemy unit, you will see it as well.
