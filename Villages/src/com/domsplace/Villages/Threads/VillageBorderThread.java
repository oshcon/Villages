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
