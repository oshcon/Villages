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

import com.domsplace.Villages.Events.GriefEvent;
import com.domsplace.Villages.Bases.Base;
import com.domsplace.Villages.Bases.VillageListener;
import com.domsplace.Villages.Enums.GriefType;
import com.domsplace.Villages.Objects.Region;
import com.domsplace.Villages.Objects.Village;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

public class WildernessGriefListener extends VillageListener {
    @EventHandler
    public void blockWildernessInteracting(GriefEvent e) {
        if(!e.getType().equals(GriefType.INTERACT)) return;
        if(getConfig().getBoolean("protection.grief.wilderness.use", true)) return;
        if(Base.hasPermission(e.getPlayer(), Base.OVERRIDE_PERMISSION)) return;
        Region region = Region.getRegion(e.getBlock().getLocation());
        Village village = Village.getOverlappingVillage(region);
        if(village != null) return;
        Base.sk(e.getPlayer(), "nointeract");
        e.setCancelled(true);
    }
    
    @EventHandler
    public void blockWildernessBreaking(GriefEvent e) {
        if(!e.getType().equals(GriefType.BREAK)) return;
        if(getConfig().getBoolean("protection.grief.wilderness.break", true)) return;
        if(Base.hasPermission(e.getPlayer(), Base.OVERRIDE_PERMISSION)) return;
        Region region = Region.getRegion(e.getBlock().getLocation());
        Village village = Village.getOverlappingVillage(region);
        if(village != null) return;
        Base.sk(e.getPlayer(), "nointeract");
        e.setCancelled(true);
    }
    
    @EventHandler
    public void blockWildernessPlacing(GriefEvent e) {
        if(!e.getType().equals(GriefType.PLACE)) return;
        if(getConfig().getBoolean("protection.grief.wilderness.place", true)) return;
        if(Base.hasPermission(e.getPlayer(), Base.OVERRIDE_PERMISSION)) return;
        Region region = Region.getRegion(e.getBlock().getLocation());
        Village village = Village.getOverlappingVillage(region);
        if(village != null) return;
        Base.sk(e.getPlayer(), "nointeract");
        e.setCancelled(true);
    }
    
    @EventHandler
    public void blockWildernessMining(GriefEvent e) {
        if(!e.getType().equals(GriefType.BLOCK_DAMAGE)) return;
        if(getConfig().getBoolean("protection.grief.wilderness.mine", true)) return;
        if(Base.hasPermission(e.getPlayer(), Base.OVERRIDE_PERMISSION)) return;
        Region region = Region.getRegion(e.getBlock().getLocation());
        Village village = Village.getOverlappingVillage(region);
        if(village != null) return;
        Base.sk(e.getPlayer(), "nointeract");
        e.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled=true)
    public void fireVillageGriefEventTNT(EntityExplodeEvent e) {
        if(e.isCancelled()) return;
        if(getConfig().getBoolean("protection.grief.wilderness.tnt", true)) return;
        if(e.getEntity() == null) return;
        if(e.getEntity().getType() == null) return;
        if(!e.getEntity().getType().equals(EntityType.PRIMED_TNT) && !e.getEntity().getType().equals(EntityType.MINECART_TNT)) return;
        if(!inVillageWorld(e.getEntity())) return;
        Village v = Village.getOverlappingVillage(Region.getRegion(e.getLocation()));
        if(v != null) return;
        //Block block damage.
        e.blockList().clear();
    }
}
