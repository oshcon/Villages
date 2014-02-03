/*
 * Copyright 2013 Dominic Masters and Jordan Atkins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.domsplace.Villages.Listeners;

import com.domsplace.Villages.Bases.VillageListener;
import com.domsplace.Villages.Events.PvPEvent;
import com.domsplace.Villages.Objects.Region;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import org.bukkit.event.EventHandler;

public class WildernessPvPListener extends VillageListener {
    @EventHandler
    public void handleWildernessSameVillagePvP(PvPEvent e) {
        if(getConfig().getBoolean("protection.pvp.wilderness.samevillage", true)) return;
        Region location = Region.getRegion(e.getAttacker());
        if(Village.getOverlappingVillage(location) != null) return;
        Resident attacked = Resident.getResident(e.getAttackedPlayer());
        Resident attacker = Resident.getResident(e.getAttacker());
        Village attackedVillage = Village.getPlayersVillage(attacked);
        if(attackedVillage == null) return;
        Village attackerVillage = Village.getPlayersVillage(attacker);
        if(attackerVillage == null) return;
        if(!attackerVillage.equals(attackedVillage)) return;
        e.setCancelled(true);
        sk(e.getAttacker(), "cantattacksamevillage");
    }
    
    @EventHandler
    public void handleWildernessDifferentVillagePvP(PvPEvent e) {
        if(getConfig().getBoolean("protection.pvp.wilderness.differentvillage", true)) return;
        Region location = Region.getRegion(e.getAttacker());
        if(Village.getOverlappingVillage(location) != null) return;
        Resident attacked = Resident.getResident(e.getAttackedPlayer());
        Resident attacker = Resident.getResident(e.getAttacker());
        Village attackedVillage = Village.getPlayersVillage(attacked);
        if(attackedVillage == null) return;
        Village attackerVillage = Village.getPlayersVillage(attacker);
        if(attackerVillage == null) return;
        if(attackerVillage.equals(attackedVillage)) return;
        e.setCancelled(true);
        sk(e.getAttacker(), "cantattackdifferentvillage");
    }
    
    @EventHandler
    public void handleWildernessWildernessPvP(PvPEvent e) {
        if(getConfig().getBoolean("protection.pvp.wilderness.notinvillage", true)) return;
        Region location = Region.getRegion(e.getAttacker());
        if(Village.getOverlappingVillage(location) != null) return;
        Resident attacked = Resident.getResident(e.getAttackedPlayer());
        Resident attacker = Resident.getResident(e.getAttacker());
        Village attackedVillage = Village.getPlayersVillage(attacked);
        Village attackerVillage = Village.getPlayersVillage(attacker);
        if(attackerVillage != null && attackedVillage != null) return;
        e.setCancelled(true);
        sk(e.getAttacker(), "cantattackwilderness");
    }
}
