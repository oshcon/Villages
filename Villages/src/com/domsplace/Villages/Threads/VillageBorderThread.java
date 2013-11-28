package com.domsplace.Villages.Threads;

import com.domsplace.Villages.Bases.VillageThread;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Village;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class VillageBorderThread extends VillageThread {    
    public VillageBorderThread() {
        super(10, 1);
    }
    
    @Override
    public void run() {
        if(Bukkit.getOnlinePlayers().length < 1) return;
        
        Map<Village, List<Location>> borders = new HashMap<Village, List<Location>>();
        for(Village v : Village.getVillages()) {
            if(v == null) continue;
            List<Location> bor = v.getBorderLocations();
            if(bor == null) continue;
            borders.put(v, bor);
        }
        
        for(Player p : Bukkit.getOnlinePlayers()) {
            Resident r = Resident.getResident(p);
            if(r == null) continue;
            if(!r.getShowBorder()) continue;
            for(Village v : borders.keySet()) {
                for(Location l : borders.get(v)) {
                    p.playEffect(l, Effect.MOBSPAWNER_FLAMES, null);
                }
            }
        }
    }
}
