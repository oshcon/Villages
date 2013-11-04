package com.domsplace.Villages.Threads;

import com.domsplace.Villages.Bases.PluginHook;
import com.domsplace.Villages.Bases.VillageThread;
import com.domsplace.Villages.Hooks.DynmapHook;
import com.domsplace.Villages.Objects.Village;
import java.util.HashMap;
import java.util.Map;
import org.dynmap.markers.AreaMarker;

public class DynmapThread extends VillageThread {
    public DynmapThread() {
        super(3, 2, true);
    }
    
    @Override
    public void run() {
        Map<Village, AreaMarker> newMarkers = new HashMap<Village, AreaMarker>();
        
        for(Village v : Village.getVillages()) {
            AreaMarker am = PluginHook.DYNMAP_HOOK.updateVillage(v);
            if(am == null) continue;
            newMarkers.put(v, am);
        }
        
        for(AreaMarker oldm : DynmapHook.markers.values()) {
            oldm.deleteMarker();
        }
        
        DynmapHook.markers = newMarkers;
    }
}
