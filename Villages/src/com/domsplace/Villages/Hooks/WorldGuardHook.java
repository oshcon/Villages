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

package com.domsplace.Villages.Hooks;

import com.domsplace.Villages.Bases.Base;
import static com.domsplace.Villages.Bases.Base.isCoordBetweenCoords;
import com.domsplace.Villages.Bases.PluginHook;
import com.domsplace.Villages.Objects.Region;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.block.Block;

public class WorldGuardHook extends PluginHook {
    public WorldGuardHook() {
        super("WorldGuard");
    }
    
    @Override
    public void onHook() {
        super.onHook();
        Base.useWorldGuard = true;
    }
    
    @Override
    public void onUnhook() {
        super.onUnhook();
        Base.useWorldGuard = false;
    }
    
    public WorldGuardPlugin getWorldGuard() {
        try {
            return (WorldGuardPlugin) this.getHookedPlugin();
        } catch(NoClassDefFoundError e) {
            return null;
        }
    }
    
    public boolean isOverlappingRegion(Region region) {
        return getOverlappingRegion(region) != null;
    }
    
    public ProtectedRegion getOverlappingRegion(Region region) {
        for(ProtectedRegion r : getWorldGuard().getRegionManager(region.getBukkitWorld()).getRegions().values()) {
            Base.debug("Checking Region " + r.getId());
            if(!isCoordBetweenCoords(region, r)) continue;
            return r;
        }
        return null;
    }
    
    public static boolean isCoordBetweenCoords(Region region, ProtectedRegion r) {
        Block b1 = region.getLowBlock();
        Block b2 = region.getHighBlock();
        
        boolean s1 = isCoordBetweenCoords(b1.getX(), b1.getZ(), r.getMinimumPoint(), r.getMaximumPoint());
        if(s1) return true;
        boolean s2 = isCoordBetweenCoords(b2.getX(), b2.getZ(), r.getMinimumPoint(), r.getMaximumPoint());
        if(s2) return true;
        boolean s3 = isCoordBetweenCoords(r.getMinimumPoint(), b1, b2);
        if(s3) return true;
        boolean s4 = isCoordBetweenCoords(r.getMaximumPoint(), b1, b2);
        if(s4) return true;
        
        return false;
    }
    
    public static boolean isCoordBetweenCoords(int checkX, int checkZ, BlockVector min, BlockVector max) {
        return Base.isCoordBetweenCoords(checkX, checkZ, min.getBlockX(), min.getBlockZ(), max.getBlockX(), max.getBlockZ());
    }
    
    public static boolean isCoordBetweenCoords(BlockVector bv, int outerX, int outerZ, int maxX, int maxZ) {
        return Base.isCoordBetweenCoords(bv.getBlockX(), bv.getBlockZ(), outerX, outerZ, maxX, maxZ);
    }
    
    public static boolean isCoordBetweenCoords(BlockVector bv, Block b1, Block b2) {
        return Base.isCoordBetweenCoords(bv.getBlockX(), bv.getBlockZ(), b1.getX(), b1.getZ(), b2.getX(), b2.getZ());
    }
}
