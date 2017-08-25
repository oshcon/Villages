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

package com.domsplace.Villages.Commands.SubCommands.War;

import com.domsplace.Villages.Bases.Base;
import com.domsplace.Villages.Bases.BukkitCommand;
import com.domsplace.Villages.Bases.DataManager;
import com.domsplace.Villages.Bases.SubCommand;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VillageWar  extends SubCommand {  
    public VillageWar() {
        super("village", "war");
        this.setPermission("war.command");
    }
    
    @Override
    public boolean cmd(BukkitCommand bkcmd, CommandSender sender, Command cmd, String label, String[] args) {
        if(!DataManager.CONFIG_MANAGER.getCFG().getBoolean("features.wars",true)) {
            sk(sender, "warsdisabled");
            return true;
        }
        
        if(!isPlayer(sender)) {
            sk(sender, "playeronly");
            return true;
        }
        Village v = Village.getPlayersVillage(Resident.getResident(sender));
        if(v == null) {
            sk(sender, "notinvillage");
            return true;
        }

        List<String> friends = new ArrayList<>();
        List<String> foes = new ArrayList<>();

        for (Village village : v.getVillageFriends()) {
            friends.add(village.getName());
        }

        for (Village village : v.getVillageFoes()) {
            foes.add(village.getName());
        }
        
        sendMessage(sender, new String[] {
            ChatImportant + "Friends: " + ChatDefault + Base.listToString(friends, ", "),
            ChatImportant + "Foes: " + ChatDefault + Base.listToString(foes, ", ")
        });
        return true;
    }
}