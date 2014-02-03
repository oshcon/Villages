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

package com.domsplace.Villages.Commands.SubCommands.Plot;

import static com.domsplace.Villages.Bases.Base.getConfig;
import static com.domsplace.Villages.Bases.Base.sk;
import com.domsplace.Villages.Bases.BukkitCommand;
import com.domsplace.Villages.Bases.DataManager;
import com.domsplace.Villages.Bases.PluginHook;
import com.domsplace.Villages.Bases.SubCommand;
import com.domsplace.Villages.Objects.Plot;
import com.domsplace.Villages.Objects.Region;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VillagePlotClaim extends SubCommand {
    public VillagePlotClaim() {
        super("village", "plot", "claim");
        this.setPermission("plot.claim");
    }
    
    @Override
    public boolean cmd(BukkitCommand bkcmd, CommandSender sender, Command cmd, String label, String[] args) {
        if(!getConfig().getBoolean("features.plots", true)) {
            sk(sender, "plotsnotenabled");
            return true;
        }
        
        if(!isPlayer(sender)) {sk(sender, "playeronly");return true;}
        
        Resident r = Resident.getResident(getPlayer(sender));
        Village v = Village.getPlayersVillage(r);
        if(v == null) {sk(sender, "notinvillage");return true;}
        
        Region standing = Region.getRegion(getPlayer(sender));
        if(standing == null) return true;
        
        if(!v.isRegionOverlappingVillage(standing)) {
            sk(sender, "plotnotinvillage");
            return true;
        }
        
        Plot plot = v.getPlot(standing);
        if(plot == null) {
            sk(sender, "notplot");
            return true;
        }
        
        if(plot.getPrice() <= 0 && plot.getOwner() == null) {
            sk(sender, "notplot");
            return true;
        }
        
        if(plot.getOwner() != null && !plot.getOwner().equals(v.getMayor())) {
            sk(sender, "claimedchunkinfo", plot.getOwner());
            return true;
        }
        
        //Make sure they have enough
        if(!hasBalance(sender.getName(), plot.getPrice())) {
            sk(sender, "notenoughmoney", PluginHook.VAULT_HOOK.formatEconomy(plot.getPrice()));
            return true;
        }
        
        //Charge Players Wallet
        if(useEconomy()) {
            PluginHook.VAULT_HOOK.getEconomy().bankWithdraw(sender.getName(), plot.getPrice());
            if(getConfig().getBoolean("features.banks.money", true)) {
                v.getBank().addWealth(plot.getPrice());
            } else {
                PluginHook.VAULT_HOOK.getEconomy().bankDeposit(v.getMayor().getName(), plot.getPrice());
            }
        }
        
        plot.setOwner(r);
        sk(sender, "claimedchunk", plot.getRegion());
        DataManager.saveAll();
        return true;
    }
}
