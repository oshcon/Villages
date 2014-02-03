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

package com.domsplace.Villages.Commands.SubCommands.AdminCommands;

import com.domsplace.Villages.Bases.BukkitCommand;
import com.domsplace.Villages.Bases.DataManager;
import com.domsplace.Villages.Bases.SubCommand;
import com.domsplace.Villages.Events.ResidentRemovedEvent;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VillageAdminRemovePlayer extends SubCommand {
    public VillageAdminRemovePlayer() {
        super("village", "admin", "remove", "player");
        this.setPermission("admin.removeplayer");
    }
    
    @Override
    public boolean cmd(BukkitCommand bkcmd, CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length < 1) {
            sk(sender, "enterplayer");
            return false;
        }
        
        Resident player = Resident.guessResident(args[0]);
        if(player == null) {
            sk(sender, "playernotfound");
            return true;
        }
        
        Village v = Village.getPlayersVillage(player);
        if(v != null) {
            sk(sender, "playernotinvillage");
            return true;
        }
        
        if(v.isMayor(player)) {
            sk(sender, "cantkickmayor");
            return true;
        }
        
        ResidentRemovedEvent event = new ResidentRemovedEvent(player, v);
        event.fireEvent();
        if(event.isCancelled()) return true;
        
        v.removeResident(player);
        sk(sender, "playerremovedfromvillage", player, v);
        DataManager.saveAll();
        return true;
    }
}
