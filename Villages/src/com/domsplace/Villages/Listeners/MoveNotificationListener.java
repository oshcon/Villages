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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveNotificationListener extends VillageListener {
    private static final Map<Village, List<Resident>> SENT_WELCOME = new HashMap<Village, List<Resident>>();
    
    @EventHandler
    public void handeEnterVillage(PlayerMoveEvent e) {
        if(!inVillageWorld(e.getPlayer())) return;
        Region r = Region.getRegion(e.getPlayer());
        Resident res = Resident.getResident(e.getPlayer());
        Village v = Village.getOverlappingVillage(r);
        Village pv = Village.getPlayersVillage(res);
        if(v == null) return;
        
        if(!SENT_WELCOME.containsKey(v)) SENT_WELCOME.put(v, new ArrayList<Resident>());
        if(SENT_WELCOME.get(v).contains(res)) return;
        SENT_WELCOME.get(v).add(res);
        
        if(getConfig().getBoolean("messages.village.youenter", true)) {
            Base.sk(e.getPlayer(), "welcomevillage", v, e.getPlayer());
            Base.sendMessage(e.getPlayer(), v.getDescription());
        }
        
        if(getConfig().getBoolean("messages.village.friendlyenters", false)) {
            if(pv != null && v.equals(pv)) {
                v.broadcast(new Player[]{e.getPlayer()}, Base.gk("friendlyvillageenter", v, e.getPlayer()));
            }
        }
        
        if(getConfig().getBoolean("messages.village.othervillage", false)) {
            if(pv != null && !v.equals(pv)) {
                v.broadcast(new Player[]{e.getPlayer()}, Base.gk("foevillageenter", v, e.getPlayer()));
            }
        }
        
        if(getConfig().getBoolean("messages.village.wilderness", false)) {
            if(pv == null) {
                v.broadcast(new Player[]{e.getPlayer()}, Base.gk("wildernessvillageneter", v, e.getPlayer()));
            }
        }
    }
    
    @EventHandler
    public void handleLeaveVillage(PlayerMoveEvent e) {
        if(!inVillageWorld(e.getPlayer())) return;
        Region r = Region.getRegion(e.getPlayer());
        Resident res = Resident.getResident(e.getPlayer());
        Village v = Village.getOverlappingVillage(r);
        Village pv = Village.getPlayersVillage(res);
        if(v != null) return;
        
        Village x = null;
        for(Village vil : SENT_WELCOME.keySet()) {
            List<Resident> residents = SENT_WELCOME.get(vil);
            if(!residents.contains(res)) continue;
            x = vil;
            SENT_WELCOME.get(vil).remove(res);
            break;
        }
        
        if(x == null) return;
        
        if(getConfig().getBoolean("messages.wilderness.youenter", true)) {
            Base.sendMessage(e.getPlayer(), Base.gk("enterwilderness", e.getPlayer()));
        }
        
        
        if(getConfig().getBoolean("messages.wilderness.friendlyenters", false)) {
            if(pv != null && x.equals(pv)) {
                x.broadcast(new Player[]{e.getPlayer()}, Base.gk("friendlywildernessenter", e.getPlayer()));
            }
        }
        
        if(getConfig().getBoolean("messages.wilderness.othervillage", false)) {
            if(pv != null && !x.equals(pv)) {
                x.broadcast(new Player[]{e.getPlayer()}, Base.gk("foewildernessenter", e.getPlayer()));
            }
        }
        
        if(getConfig().getBoolean("messages.wilderness.wilderness", false)) {
            if(pv == null) {
                x.broadcast(new Player[]{e.getPlayer()}, Base.gk("wildernesswildernessneter", e.getPlayer()));
            }
        }
    }
}
