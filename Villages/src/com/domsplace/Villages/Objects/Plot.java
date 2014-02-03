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

public class Plot {
    private Resident owner;
    private double cost;
    private Village village;
    private Region region;
    
    public Plot(Village parent, Region region) {
        this.village = parent;
        this.region = region;
        this.cost = -1.0d;
    }
    
    public boolean isOwned() {return this.owner != null;}
    public boolean isForSale() { return this.cost >= 0; }
    
    public Resident getOwner() {return this.owner;}
    public Village getVillage() {return this.village;}
    public double getPrice() {return this.cost;}
    public Region getRegion() {return this.region;}
    
    public void setPrice(double price) {this.cost = price;}
    public void setOwner(Resident owner) {this.owner = owner;}

    public boolean canBuild(Resident res) {
        if(res == null) return false;
        if(this.village.isMayor(res)) return true;
        if(Base.hasPermission(res.getOfflinePlayer(), Base.OVERRIDE_PERMISSION)) return true;
        return this.owner.equals(res);
    }
}
