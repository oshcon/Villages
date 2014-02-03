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
