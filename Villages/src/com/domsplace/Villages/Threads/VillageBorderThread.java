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

package com.domsplace.Villages.Threads;

import com.domsplace.Villages.Bases.VillageThread;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VillageBorderThread extends VillageThread {    
    public VillageBorderThread() {
        super(10, 1);
    }
    
    @Override
    public void run() {
        if(Bukkit.getOnlinePlayers().length < 1) return;
        
        for(Player p : Bukkit.getOnlinePlayers()) {
            Resident r = Resident.getResident(p);
            if(r == null) continue;
            if(!r.getShowBorder()) continue;
            Village v = Village.getPlayersVillage(r);
            if(v == null) continue;
            v.playBorderEffect(p);
        }
    }
}
