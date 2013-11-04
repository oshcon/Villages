/*
 * Copyright 2013 Dominic.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.domsplace.Villages.Hooks;

import com.domsplace.Villages.Bases.PluginHook;
import com.domsplace.Villages.Objects.Village;
import com.domsplace.Villages.Threads.DynmapThread;
import java.util.HashMap;
import java.util.Map;
import org.dynmap.markers.AreaMarker;

/**
 * @author      Dominic
 * @since       14/10/2013
 */
public class DynmapHook extends PluginHook {
    public static Map<Village, AreaMarker> markers = new HashMap<Village, AreaMarker>();
    
    private DynmapThread thread;
    
    public DynmapHook() {
        super("dynmap");
    }
    
    @Override
    public void onHook() {
        super.onHook();
        
        this.thread = new DynmapThread();
    }
    
    @Override
    public void onUnhook() {
        super.onUnhook();
        
        if(this.thread != null) {
            this.thread.stopThread();
            this.thread = null;
        }
    }
    
    public AreaMarker updateVillage(Village v) {
        double[] x;
        double[] z;
        return null;
    }
}
