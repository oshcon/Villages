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


import com.domsplace.Villages.Bases.DataManager;
import com.domsplace.Villages.Bases.VillageListener;
import com.domsplace.Villages.Commands.SubCommands.VillageInvite;
import com.domsplace.Villages.Events.PreCommandEvent;
import com.domsplace.Villages.Events.ResidentAddedEvent;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import java.util.Map;
import org.bukkit.event.EventHandler;

public class VillageInviteListener extends VillageListener {
    @EventHandler(ignoreCancelled=true)
    public void handleVillageAccept(PreCommandEvent e) {
        if(!e.getCommand().equalsIgnoreCase("accept")) return;
        
        if(!hasPermission(e.getPlayer(), "Villages.accept")) return;
        if(!isPlayer(e.getPlayer()));
        Resident r = Resident.getResident(getPlayer(e.getPlayer()));
        if(r == null) return;
        
        Map<Resident, Village> invites = VillageInvite.VILLAGE_INVITES;
        if(invites == null) return;
        
        if(!invites.containsKey(r)) return;
        Village v = invites.get(r);
        if(v == null) return;
        
        e.setCancelled(true);
        
        if(Village.getPlayersVillage(r) != null) {
            sendMessage(r, "alreadyinvillage");
            return;
        }
        
        invites.remove(r);
        
        ResidentAddedEvent event = new ResidentAddedEvent(r, v);
        event.fireEvent();
        if(event.isCancelled()) return;
        
        v.addResident(r);
        v.broadcast(gk("joinedvillage", r));
        DataManager.saveAll();
    }
    
    @EventHandler(ignoreCancelled=true)
    public void handleVillageDeny(PreCommandEvent e) {
        if(!e.getCommand().equalsIgnoreCase("deny")) return;
        
        if(!hasPermission(e.getPlayer(), "Villages.deny")) return;
        if(!isPlayer(e.getPlayer())) return;
        Resident r = Resident.getResident(getPlayer(e.getPlayer()));
        if(r == null) return;
        
        Map<Resident, Village> invites = VillageInvite.VILLAGE_INVITES;
        if(invites == null) return;
        
        if(!invites.containsKey(r)) return;
        Village v = invites.get(r);
        if(v == null) return;
        
        invites.remove(r);
        sk(e.getPlayer(), "deniedinvite");
        e.setCancelled(true);
    }
}
