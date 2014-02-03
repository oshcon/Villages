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

import com.domsplace.Villages.Bases.Base;
import com.domsplace.Villages.Bases.BukkitCommand;
import com.domsplace.Villages.Bases.DataManager;
import com.domsplace.Villages.Bases.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VillageAdmin  extends SubCommand {  
    public VillageAdmin() {
        super("village", "admin");
        this.setPermission("admin.command");
    }
    
    @Override
    public boolean cmd(BukkitCommand bkcmd, CommandSender sender, Command cmd, String label, String[] args) {
        if(!isPlayer(sender)) {
            sendMessage(sender, new String[]{
                ChatColor.BLUE + getPlugin().getName() + ChatColor.GRAY + " Version " + DataManager.PLUGIN_MANAGER.getVersion(),
                ChatColor.GRAY + "\tProgrammed by: " + ChatColor.LIGHT_PURPLE + "Dom",
                ChatColor.GRAY + "\tTested by: " + ChatColor.AQUA + "Jordan",
                ChatColor.GRAY + "\tWebsite: " + ChatColor.GOLD + "http://domsplace.com/"
            });
            return true;
        }
        
        sendRawMessage(getPlayer(sender), "{text:\"\",extra:[{text:\"" + getPlugin().getName() + "\",color:blue,hoverEvent:{action:show_text,value:\"Click to visit the Bukkit Page\"},clickEvent:{action:open_url,value:\"" + Base.BUKKIT_PAGE + "\"}},{text:\" Version " + DataManager.PLUGIN_MANAGER.getVersion() + "\",color:gray}]}");
        sendRawMessage(getPlayer(sender), "{text:\"" + Base.TAB + "Programmed By: \",color:gray,extra:[{text:\"Dom\",color:light_purple,clickEvent:{action:open_url,value:\"http://domsplace.com/\"},hoverEvent:{action:show_text,value:\"Click to go to my website!\"}}]}");
        sendRawMessage(getPlayer(sender), "{text:\"" + Base.TAB + "Tested By: \",color:gray,extra:[{text:\"Jordan\",color:aqua}]}");
        return true;
    }
}