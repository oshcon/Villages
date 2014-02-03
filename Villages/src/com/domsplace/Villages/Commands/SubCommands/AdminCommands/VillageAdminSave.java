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
import com.domsplace.Villages.Objects.Village;
import java.io.IOException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VillageAdminSave extends SubCommand {  
    public VillageAdminSave() {
        super("village", "admin", "save");
        this.setPermission("admin.save");
    }
    
    @Override
    public boolean cmd(BukkitCommand bkcmd, CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length > 0 && args[0].equalsIgnoreCase("yml")) {
            sendMessage(sender, ChatImportant + "Force Saving All Villages in YML!");
            for(Village v : Village.getVillages()) {
                try {
                    DataManager.VILLAGE_MANAGER.saveVillageAsYML(v);
                } catch(IOException e) {
                    sendMessage(sender, ChatError + "Failed to save Village " + v.getName() + ".");
                }
            }
            sendMessage(sender, "Saved as YML.");
            return true;
        }
        
        sendMessage(sender, ChatImportant + "Saving Data...");
        if(!DataManager.saveAll()) {sendMessage(sender, ChatError + "Failed to save data!"); return true;}
        sendMessage(sender, "Saved Data!");
        return true;
    }
}
