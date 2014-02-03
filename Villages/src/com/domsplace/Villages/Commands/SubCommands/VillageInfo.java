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

package com.domsplace.Villages.Commands.SubCommands;

import com.domsplace.Villages.Bases.Base;
import com.domsplace.Villages.Bases.BukkitCommand;
import com.domsplace.Villages.Bases.SubCommand;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VillageInfo extends SubCommand {
    public VillageInfo() {
        super("village", "info");
    }
    
    @Override
    public boolean cmd(BukkitCommand bkcmd, CommandSender sender, Command cmd, String label, String[] args) {
        Village v;
        
        if(args.length < 1 && !isPlayer(sender)) {
            sk(sender, "entervillagename");
            return false;
        } else if(args.length < 1) {
            v = Village.getPlayersVillage(Resident.getResident(getPlayer(sender)));
        } else {
            v = Village.getVillage(args[0]);
        }
        
        if(v == null) {
            sk(sender, "cantfindvillage");
            return true;
        }
        
        List<String> messages = new ArrayList<String>();
        messages.add(ChatImportant + "Info for " + ChatDefault + v.getName());
        messages.add("\tDescription: " + ChatColor.ITALIC + v.getDescription());
        messages.add("\tMayor: " + ChatColor.ITALIC + v.getMayor().getName());
        messages.add("\tResidents: " + ChatColor.ITALIC + listToString(v.getResidentsAsString(), ", " + ChatDefault + ChatColor.ITALIC)); //Temp 1.7.2 workaround
        messages.add("\tSpawn: " + ChatColor.ITALIC + v.getSpawn().toHumanString());
        messages.add("\tSize: " + ChatColor.ITALIC + v.getRegions().size());
        
        if(getConfig().getBoolean("features.plots", false)) messages.add("\tAvailable Plots: " + (v.getAvailablePlots().size()));
        
        if(Base.useEconomy()) messages.add("\tWealth: " + ChatColor.ITALIC + getMoney(v.getBank().getWealth()));
        
        sendMessage(sender, messages);
        return true;
    }
}
