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
import com.domsplace.Villages.Bases.PluginHook;
import com.domsplace.Villages.Bases.VillageListener;
import com.domsplace.Villages.Events.ResidentAddedEvent;
import com.domsplace.Villages.Events.ResidentRemovedEvent;
import com.domsplace.Villages.Events.VillageCreatedEvent;
import com.domsplace.Villages.Events.VillageDeletedEvent;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import org.bukkit.event.EventHandler;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

public class LegacyTagAPIListener extends VillageListener {
    @EventHandler
    public void handleVillageFoe(PlayerReceiveNameTagEvent e) {
        if(!useTagAPI) return;
        if(e.getPlayer() == null) return;
        if(e.getNamedPlayer() == null) return;
        
        if(e.getPlayer().equals(e.getNamedPlayer())) return;
        
        if(!inVillageWorld(e.getNamedPlayer())) return;
        if(!inVillageWorld(e.getPlayer())) return;
        
        Resident target = Resident.getResident(e.getPlayer());
        Resident named = Resident.getResident(e.getNamedPlayer());
        
        Village tVillage = Village.getPlayersVillage(target);
        Village nVillage = Village.getPlayersVillage(named);
        
        if(tVillage != null && nVillage != null && tVillage.equals(nVillage)) return;
        
        e.setTag(Base.colorise(Base.EnemyColor + e.getNamedPlayer().getName()));
    }
    
    @EventHandler
    public void handleVillageFriend(PlayerReceiveNameTagEvent e) {
        if(!useTagAPI) return;
        if(e.getPlayer() == null) return;
        if(e.getNamedPlayer() == null) return;
        
        if(e.getPlayer().equals(e.getNamedPlayer())) return;
        
        if(!inVillageWorld(e.getNamedPlayer())) return;
        if(!inVillageWorld(e.getPlayer())) return;
        
        Resident target = Resident.getResident(e.getPlayer());
        Resident named = Resident.getResident(e.getNamedPlayer());
        
        Village tVillage = Village.getPlayersVillage(target);
        Village nVillage = Village.getPlayersVillage(named);
        
        if(tVillage == null || nVillage == null) return;
        if(!tVillage.equals(nVillage)) return;
        
        e.setTag(Base.colorise(Base.FriendColor + e.getNamedPlayer().getName()));
    }
    
    @EventHandler(ignoreCancelled=true)
    public void refreshTagsVillageCreated(VillageCreatedEvent e) {
        if(!useTagAPI) return;
        PluginHook.TAG_API_HOOK.refreshTags();
    }
    
    @EventHandler(ignoreCancelled=true)
    public void refreshTagsVillageDeleted(VillageDeletedEvent e) {
        if(!useTagAPI) return;
        PluginHook.TAG_API_HOOK.refreshTags();
    }
    
    @EventHandler
    public void refreshTagsVillageAdded(ResidentAddedEvent e) {
        if(!useTagAPI) return;
        PluginHook.TAG_API_HOOK.refreshTags();
    }
    
    @EventHandler
    public void refreshTagsVillageRemoved(ResidentRemovedEvent e) {
        if(!useTagAPI) return;
        PluginHook.TAG_API_HOOK.refreshTags();
    }
}
