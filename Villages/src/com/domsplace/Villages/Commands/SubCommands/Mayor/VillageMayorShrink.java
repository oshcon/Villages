package com.domsplace.Villages.Commands.SubCommands.Mayor;

import com.domsplace.Villages.Bases.Base;
import com.domsplace.Villages.Bases.BukkitCommand;
import com.domsplace.Villages.Bases.DataManager;
import com.domsplace.Villages.Bases.SubCommand;
import com.domsplace.Villages.Enums.ExpandMethod;
import com.domsplace.Villages.Events.VillageShrinkEvent;
import com.domsplace.Villages.Objects.Region;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VillageMayorShrink extends SubCommand {
    public VillageMayorShrink() {
        super("village", "mayor", "shrink");
        this.setPermission("mayor.shrink");
    }
    
    @Override
    public boolean cmd(BukkitCommand bkcmd, CommandSender sender, Command cmd, String label, String[] args) {
        if(!isPlayer(sender)) {sk(sender, "playeronly");return true;}
        
        Resident r = Resident.getResident(getPlayer(sender));
        Village v = Village.getPlayersVillage(r);
        if(v == null) {sk(sender, "notinvillage");return true;}
        if(!v.isMayor(r)) {sk(sender, "onlymayorshrink"); return true;}
        
        List<Region> unclaiming = new ArrayList<Region>();
        if(Base.ExpandingMethod.equals(ExpandMethod.PER_CHUNK)) {
            Region region = Region.getRegion(getPlayer(sender).getLocation());
            if(region == null) return true; //Make sure Region is Valid
            
            //Check to make sure this Region is in the Village
            if(!v.isRegionOverlappingVillage(region)) {
                sk(sender, "cantshrinknotpart");
                return true;
            }
            
            unclaiming.add(region);
        } else if(Base.ExpandingMethod.equals(ExpandMethod.CLASSIC)) {
            //Get current "classic size" of Village
            int size = 1;
            List<Region> chunks = v.getRegions();
            if(chunks.size() > 1) {
                size = (int) Math.ceil((Math.sqrt(chunks.size()) / 2.0d) + 0.5d);
            }
            
            //Get Chunks that border
            int x = v.getSpawnRegion().getRegionX();
            int z = v.getSpawnRegion().getRegionZ();
        
            for(int cx = -size; cx <= size; cx++) {
                for(int cz = -size; cz <= size; cz++) {
                    Region re = v.getSpawnRegion().getRelativeRegion(cx, cz);
                    if(r == null) {
                        continue;
                    }
                    
                    if(!(((cx == -size) || (cx == size))  || ((cz == -size) || (cz == size)))) {continue;}
                    
                    unclaiming.add(re);
                }
            }
        }
        
        for(Region region : unclaiming) {
            //Make sure the Region is in the same world as their Village
            if(!region.getWorld().equalsIgnoreCase(v.getSpawn().getWorld())) {
                sk(sender, "notinthisworld");
                return true;
            }
            
            //Make Sure region is claimed
            if(!Village.doesRegionOverlapVillage(region)) {
                sk(sender, "cantshrinknotpart");
                return true;
            }
            
            //Make sure not spawn
            if(v.getSpawnRegion().equals(region)) {
                sk(sender, "cantshrinkspawn");
                return true;
            }
        }
        
        //Charge Village on Per-Chunk basis
        double cost = getCost("expandvillage") * unclaiming.size();
        
        //Fire Event
        VillageShrinkEvent event = new VillageShrinkEvent(v, unclaiming, r);
        event.fireEvent();
        if(event.isCancelled()) return true;
        
        //Charge Village
        if(Base.useEconomy() && getConfig().getBoolean("features.banks.money", true)) {
            v.getBank().addWealth(-cost);
        }
        
        v.removeRegions(unclaiming);
        sk(sender, "villageshrunk", unclaiming.size());
        DataManager.saveAll();
        return true;
    }
}
