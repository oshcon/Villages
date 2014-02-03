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

import com.domsplace.Villages.Bases.Base;
import com.domsplace.Villages.Bases.VillageListener;
import com.domsplace.Villages.Objects.Region;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MobGriefingListener extends VillageListener {
    @EventHandler(ignoreCancelled=true)
    public void handleVillageMobGriefing(EntityDamageByEntityEvent e) {
        if(e.getEntity() == null) return;
        if(e.getEntity().getLocation() == null) return;
        if(Base.hasPermission(e.getDamager(), Base.OVERRIDE_PERMISSION)) return;
        
        String mobspawningkey = "protection.mobattacking.village." + e.getEntity().getType().name();
        if(!getConfig().getBoolean(mobspawningkey, false)) return;
        
        Region r = Region.getRegion(e.getEntity().getLocation());
        Village v = Village.getOverlappingVillage(r);
        if(v == null) return;
        if(v.isResident(Resident.getResident(e.getDamager()))) return;
        e.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled=true)
    public void handleWildernessMobGriefing(EntityDamageByEntityEvent e) {
        if(e.getEntity() == null) return;
        if(e.getEntity().getLocation() == null) return;
        if(Base.hasPermission(e.getDamager(), Base.OVERRIDE_PERMISSION)) return;
        
        String mobspawningkey = "protection.mobattacking.wilderness." + e.getEntity().getType().name();
        if(!getConfig().getBoolean(mobspawningkey, false)) return;
        
        Region r = Region.getRegion(e.getEntity().getLocation());
        Village v = Village.getOverlappingVillage(r);
        if(v != null) return;
        if(v.isResident(Resident.getResident(e.getDamager()))) return;
        e.setCancelled(true);
    }
}
