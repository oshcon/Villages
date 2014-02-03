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
import com.domsplace.Villages.Objects.VillageScoreboard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class VillageScoreboardListener extends VillageListener {
    @EventHandler
    public void handlePlayerLogin(PlayerLoginEvent e) {
        VillageScoreboard vsc = VillageScoreboard.getScoreboard(e.getPlayer());
        vsc.setPlayer();
    }
    
    @EventHandler
    public void handlePlayerLogout(PlayerQuitEvent e) {
        VillageScoreboard vsc = VillageScoreboard.getScoreboard(e.getPlayer());
        VillageScoreboard.deRegister(vsc);
    }
    
    @EventHandler
    public void handlePlayerKick(PlayerKickEvent e) {
        VillageScoreboard vsc = VillageScoreboard.getScoreboard(e.getPlayer());
        VillageScoreboard.deRegister(vsc);
    }
}
