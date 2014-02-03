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

package com.domsplace.Villages.Objects;

import com.domsplace.Villages.Bases.Base;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class DomsLocation {
    public static DomsLocation guessLocation(String c) {
        /*
         * Valid Formats:
         * x,z
         * x,y,z
         * x,z,world
         * x,y,z,world
         */
        
        if(c.endsWith(",")) c = Base.trim(c, c.length() - 1);
        String[] split = c.split(",");
        if(split.length < 2) return null;
        
        DomsLocation loc = new DomsLocation();
        
        //Conversion Exceptions may be thrown
        try {
            if(split.length == 2) {
                loc.setX(Base.getDouble(split[0]));
                loc.setZ(Base.getDouble(split[1]));
            } else if(split.length == 3) {
                loc.setX(Base.getDouble(split[0]));
                double p2 = Base.getDouble(split[1]);
                if(Base.isDouble(split[2])) {
                    loc.setY(p2);
                    loc.setZ(Base.getDouble(split[2]));
                } else {
                    loc.setZ(p2);
                    loc.setWorld(split[2]);
                }
            } else if(split.length == 4) {
                loc.setX(Base.getDouble(split[0]));
                loc.setY(Base.getDouble(split[1]));
                loc.setZ(Base.getDouble(split[2]));
                loc.setWorld(split[3]);
            } else if (split.length == 5) {
                loc.setX(Base.getDouble(split[0]));
                loc.setY(Base.getDouble(split[1]));
                loc.setZ(Base.getDouble(split[2]));
                loc.setPitch(Base.getFloat(split[3]));
                loc.setYaw(Base.getFloat(split[4]));
            } else if(split.length >= 6) {
                loc.setX(Base.getDouble(split[0]));
                loc.setY(Base.getDouble(split[1]));
                loc.setZ(Base.getDouble(split[2]));
                loc.setPitch(Base.getFloat(split[3]));
                loc.setYaw(Base.getFloat(split[4]));
                loc.setWorld(split[5]);
            }
        } catch(Exception e) {
            return null;
        }
        
        return loc;
    }
    
    //Instance
    private double x;
    private double y;
    private double z;
    
    private float pitch;
    private float yaw;
    
    private String world;
    
    public DomsLocation(Block b) {
        this(b.getLocation());
    }
    
    public DomsLocation(Entity e) {
        this(e.getLocation());
    }
    
    public DomsLocation(DomsLocation location) {
        this(location.toLocation());
    }
    
    public DomsLocation(Location f) {
        this(f.getX(), f.getY(), f.getZ(), f.getPitch(), f.getYaw(), f.getWorld().getName());
    }
    
    public DomsLocation() {
        this(-1.0d, -1.0d);
    }
    
    public DomsLocation(double x, double z) {
        this(x, z, null);
    }
    
    public DomsLocation(double x, double z, String world) {
        this(x, -1.0d, z, world);
    }
    
    public DomsLocation(double x, double y, double z) {
        this(x, y, z, null);
    }
    
    public DomsLocation(double x, double y, double z, String world) {
        this(x, y, z, -1.0f, -1.0f, world);
    }
    
    public DomsLocation(double x, double y, double z, float pitch, float yaw, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.world = world;
    }
    
    public double getX() {return this.x;}
    public double getY() {return this.y;}
    public double getZ() {return this.z;}
    public float getPitch() {return this.pitch;}
    public float getYaw() {return this.yaw;}
    public String getWorld() {return this.world;}
    public World getBukkitWorld() {return Bukkit.getWorld(world);}
    public Block getBlock() {return this.toLocation().getBlock();}
    
    public void setX(double x) {this.x = x;}
    public void setY(double y) {this.y = y;}
    public void setZ(double z) {this.z = z;}
    public void setPitch(float p) {this.pitch = p;}
    public void setYaw(float y) {this.yaw = y;}
    public void setWorld(String world) {this.world = world;}
    
    public boolean isWorldLoaded() {return this.getBukkitWorld() != null;}
    
    public Location toLocation() {return new Location(this.getBukkitWorld(), this.x, this.y, this.z, this.yaw, this.pitch);}

    public DomsLocation copy() {return DomsLocation.guessLocation(this.toString());}
    
    public DomsLocation getSafeLocation() {
        //Returns the Safest Location
        int unsafeY = (int) this.y;
        if (unsafeY < 0) return null;
        for (int i = unsafeY; i >= 0; i--) {
            if (i < 0) return null;
            Block b = this.getBukkitWorld().getBlockAt((int)this.getX(), i, (int)this.getZ());
            if (b == null) return null;
            if (b.getType().equals(Material.AIR)) continue;
            Location bLoc = b.getLocation();
            double safeY = this.getY() - (unsafeY - i);
            return new DomsLocation(this.getX(), safeY + 1, this.getZ(), this.getPitch(), this.getYaw(), this.world);
        }
        return this.copy();
    }
    
    public String toHumanString() {
        String x = "" + ((int) Math.round(this.x));
        x += ", " + ((int) Math.round(this.y));
        x += ", " + ((int) Math.round(this.z));
        x += " in " + this.world;
        return x;
    }
    
    @Override
    public String toString() {
        String s = this.x + "," + this.y + "," + this.z + ",";
        if(this.pitch != -1.0f && this.yaw != -1.0f) {
            s += this.pitch + "," + this.yaw + ",";
        }
        s += this.world;
        return s;
    }
}
