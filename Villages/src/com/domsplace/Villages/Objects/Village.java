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
import com.domsplace.Villages.Bases.DataManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Village {
    private static final List<Village> VILLAGES = new ArrayList<Village>();
    
    public static void registerVillage(Village village) {
    	VILLAGES.remove(village);
        village.getBank().updateGUI();
        try {
            if(village.getVillageMap() != null) {
                village.getVillageMap().unload();
            }
        } catch(IllegalArgumentException e) {}
        village.map = null;
        VILLAGES.add(village);
    }
    
    public static void  deRegisterVillage(Village village) {
        VILLAGES.remove(village);
        village.getBank().updateGUI();
        try {
            if(village.getVillageMap() != null) {
                village.getVillageMap().unload();
            }
        } catch(IllegalArgumentException e) {}
        village.map = null;
    }
    
    public static List<Village> getVillages() {
        return new ArrayList<Village>(VILLAGES);
    }

    public static List<String> getVillageNames() {
        List<String> rv = new ArrayList<String>();
        for(Village v : VILLAGES) {
            if(v == null) continue;
            rv.add(v.getName());
        }
        return rv;
    }
    
    public static Village getPlayersVillage(Resident player) {
        if(player == null) return null;
        for(Village v : VILLAGES) {
            if(v.getMayor() == null) continue;
            if(v.isMayor(player) || v.isResident(player)) return v;
        }
        return null;
    }
    
    public static boolean doesRegionOverlapVillage(Region region) {
        return getOverlappingVillage(region) != null;
    }
    
    public static Village getOverlappingVillage(Region region) {
        for(Village v : VILLAGES) {
            if(!v.isRegionOverlappingVillage(region)) continue;
            return v;
        }
        
        return null;
    }

    public static Village getVillage(String name) {
        for(Village v : VILLAGES) {
            if(v.getName().equalsIgnoreCase(name)) return v;
        }
        return null;
    }
    
    public static void deleteVillage(Village village) {
        DataManager.VILLAGE_MANAGER.deleteVillage(village);
    }

    public static void deRegisterVillages(List<Village> villages) {
        for(Village v : villages) {
            Village.deRegisterVillage(v);
        }
    }
    
    //Instance
    private String name;
    private String description = "";
    private long createdDate;
    
    private Resident mayor;
    private Bank bank;
    private DomsLocation spawn;
    private VillageMap map;
    
    private List<Region> regions;
    private List<Plot> plots;
    private List<Resident> residents;
    private List<TaxData> taxData;
    
    private List<String> friends;
    private List<String> foes;
    
    public Village() {
        this.bank = new Bank(this);
        this.plots = new ArrayList<Plot>();
        this.regions = new ArrayList<Region>();
        this.residents = new ArrayList<Resident>();
        this.taxData = new ArrayList<TaxData>();
        this.createdDate = Base.getNow();
        this.friends = new ArrayList<String>();
        this.foes = new ArrayList<String>();
        this.description = "Welcome!";
    }
    
    public String getName() {return this.name;}
    public String getDescription() {return this.description;}
    public Resident getMayor() {return this.mayor;}
    public DomsLocation getSpawn() {return this.spawn;}
    public Bank getBank() {return this.bank;}
    public long getCreatedDate() {return this.createdDate;}
    
    public List<Region> getRegions() {return new ArrayList<Region>(this.regions);}
    public List<Plot> getPlots() {return new ArrayList<Plot>(this.plots);}
    public List<Resident> getResidents() {return new ArrayList<Resident>(this.residents);}
    public List<TaxData> getTaxData() {return new ArrayList<TaxData>(this.taxData);}
    public List<String> getFriends() {return new ArrayList<String>(this.friends);}
    public List<String> getFoes() {return new ArrayList<String>(this.foes);}
    
    public void setName(String name) {this.name = name; this.bank.updateGUI();}
    public void setDescription(String description) {this.description = description;}
    public void setMayor(Resident mayor) {this.mayor = mayor; this.addResident(mayor);}
    public void setSpawn(DomsLocation region) {this.spawn = region;}
    public void setCreatedDate(long date) {this.createdDate = date;}
    
    public void addRegion(Region region) {if(this.regions.contains(region)) {return;} this.regions.add(region);}
    public void addPlot(Plot plot) {this.plots.add(plot);}
    public void addResident(Resident resident) {if(this.residents.contains(resident)){return;} this.residents.add(resident);}
    public void addTaxData(TaxData data) {this.taxData.add(data);}
    public void addFriend(String friend) {this.friends.add(friend);}
    public void addFoe(String foe) {this.foes.add(foe);}
    
    public void removeResident(Resident resident) {this.residents.remove(resident);}

    public boolean isMayor(Resident player) {if(player == null) return false; return this.mayor.equals(player); }
    public boolean isResident(Resident player) {if(player == null) return false; return this.residents.contains(player) || this.isMayor(player);}
    
    public List<Plot> getAvailablePlots() {
        List<Plot> list = new ArrayList<Plot>();
        for(Plot p : this.plots) {
            if(p.isOwned()) continue;
            list.add(p);
        }
        return list;
    }
    
    public Region getOverlappingRegion(Region region) {
        for(Region r : this.regions) {
            if(!r.compare(region)) continue;
            return r;
        }
        return null;
    }

    public List<Village> getVillageFriends() {
        List<Village> vils = new ArrayList<Village>();
        for(String s : this.friends) {
            Village v = Village.getVillage(s);
            if(v == null) continue;
            vils.add(v);
        }
        return vils;
    }

    public List<Village> getVillageFoes() {
        List<Village> vils = new ArrayList<Village>();
        for(String s : this.foes) {
            Village v = Village.getVillage(s);
            if(v == null) continue;
            vils.add(v);
        }
        return vils;
    }
    
    public boolean isRegionOverlappingVillage(Region region) {
        return this.getOverlappingRegion(region) != null;
    }

    public List<String> getResidentsAsString() {
        List<String> res = new ArrayList<String>();
        for(Resident r : this.residents) {
            res.add(r.getName());
        }
        return res;
    }

    public List<String> getRegionsAsString() {
        List<String> reg = new ArrayList<String>();
        for(Region r : this.regions) {
            reg.add(r.toString());
        }
        return reg;
    }

    public Region getSpawnRegion() {
        return Region.getRegion(this.spawn.toLocation());
    }
    
    public List<Player> getOnlineResidents() {
        List<Player> players = new ArrayList<Player>();
        for(Resident r : this.residents) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(r.getName());
            if(!p.isOnline()) continue;
            players.add(p.getPlayer());
        }
        return players;
    }
    
    public VillageMap getVillageMap() throws IllegalArgumentException {
        if(this.map != null) return this.map;
        this.map = new VillageMap(this);
        return this.map;
    }
    
    public void broadcast(Player[] ignoredPlayers, Object... o) {
        List<Player> players = this.getOnlineResidents();
        for(Player p : ignoredPlayers) {
            if(players.contains(p)) players.remove(p);
        }
        
        Base.sendAll(players, o);
    }

    public void broadcast(Object... o) {
        this.broadcast(new Player[]{}, o);
    }
    
    public boolean doesRegionBorder(Region r) {
        for(Region re : this.regions) {
            if(re.doesRegionBorder(r)) return true;
        }
        
        return false;
    }

    public void addRegions(Collection<Region> claiming) {
        this.regions.addAll(claiming);
    }

    public void removeRegions(Collection<Region> claiming) {
        this.regions.removeAll(claiming);
    }
    
    public void explode() {
        //WARNING! Can be CPU intensive
        List<Block> explode = new ArrayList<Block>();
        for(Region r : this.regions) {
            Block x = r.getHighBlock();
            Block y = r.getSafeMiddle().getBlock();
            Block z = r.getLowBlock();
            explode.add(x);
            explode.add(y);
            explode.add(z);
        }
        
        for(Block b : explode) {
            //Create Explosion at 6F (1.5x a regular TNT)
            b.getWorld().createExplosion(b.getLocation(), 6f);
            b.getRelative(0, 30, 0).getWorld().createExplosion(b.getLocation(), 6f);
            b.getRelative(0, -30, 0).getWorld().createExplosion(b.getLocation(), 6f);
        }
    }

    public int getValue() {
        int v = this.getRegions().size();
        v += this.residents.size();
        if(Base.useEconomy()) v += this.getBank().getWealth();
        //TODO: Add ItemBank stuff here
        return v;
    }

    public Plot getPlot(Region standing) {
        for(Plot p : this.plots) {
            if(p.getRegion().compare(standing)) return p;
        }
        return null;
    }
    
    public List<Location> getBorderLocations() {
        List<Location> locs = new ArrayList<Location>();
        if(!this.getSpawn().isWorldLoaded()) return locs;
        for(Region r : this.regions) {
            Region north = r.getRelativeRegion(0, 1);
            Region south = r.getRelativeRegion(0, -1);
            Region east = r.getRelativeRegion(1, 0);
            Region west = r.getRelativeRegion(-1, 0);
            
            boolean n = this.isRegionOverlappingVillage(north);
            boolean s = this.isRegionOverlappingVillage(south);
            boolean e = this.isRegionOverlappingVillage(east);
            boolean w = this.isRegionOverlappingVillage(west);
            
            if(n && s && e && w) continue;
            
            int m = 255;
            
            if(!n) {
                for(int i = r.getX(); i < r.getMaxX(); i++) {
                    Location l = r.getBukkitWorld().getBlockAt(i, m, r.getMaxZ()).getLocation();
                    DomsLocation safe = new DomsLocation(l);
                    locs.add(safe.getSafeLocation().toLocation());
                }
            }
            
            if(!s) {
                for(int i = r.getX(); i < r.getMaxX(); i++) {
                    Location l = r.getBukkitWorld().getBlockAt(i, m, r.getZ()).getLocation();
                    DomsLocation safe = new DomsLocation(l);
                    locs.add(safe.getSafeLocation().toLocation());
                }
            }
            
            if(!e) {
                for(int i = r.getZ(); i < r.getMaxZ(); i++) {
                    Location l = r.getBukkitWorld().getBlockAt(r.getMaxX(), m, i).getLocation();
                    DomsLocation safe = new DomsLocation(l);
                    locs.add(safe.getSafeLocation().toLocation());
                }
            }
            
            if(!w) {
                for(int i = r.getZ(); i < r.getMaxZ(); i++) {
                    Location l = r.getBukkitWorld().getBlockAt(r.getX(), m, i).getLocation();
                    DomsLocation safe = new DomsLocation(l);
                    locs.add(safe.getSafeLocation().toLocation());
                }
            }
        }
        
        return locs;
    }
    
    public void playBorderEffect(Player player) {
        if(!this.getSpawn().isWorldLoaded()) return;
        for(Region r : this.regions) {
            Region north = r.getRelativeRegion(0, 1);
            Region south = r.getRelativeRegion(0, -1);
            Region east = r.getRelativeRegion(1, 0);
            Region west = r.getRelativeRegion(-1, 0);
            
            boolean n = this.isRegionOverlappingVillage(north);
            boolean s = this.isRegionOverlappingVillage(south);
            boolean e = this.isRegionOverlappingVillage(east);
            boolean w = this.isRegionOverlappingVillage(west);
            
            if(n && s && e && w) continue;
            
            int m = 255;
            
            if(!n) {
                for(int i = r.getX(); i < r.getMaxX(); i++) {
                    Location l = r.getBukkitWorld().getBlockAt(i, m, r.getMaxZ()).getLocation();
                    DomsLocation safe = new DomsLocation(l);
                    player.playEffect(safe.getSafeLocation().toLocation(), Effect.MOBSPAWNER_FLAMES, null);
                }
            }
            
            if(!s) {
                for(int i = r.getX(); i < r.getMaxX(); i++) {
                    Location l = r.getBukkitWorld().getBlockAt(i, m, r.getZ()).getLocation();
                    DomsLocation safe = new DomsLocation(l);
                    player.playEffect(safe.getSafeLocation().toLocation(), Effect.MOBSPAWNER_FLAMES, null);
                }
            }
            
            if(!e) {
                for(int i = r.getZ(); i < r.getMaxZ(); i++) {
                    Location l = r.getBukkitWorld().getBlockAt(r.getMaxX(), m, i).getLocation();
                    DomsLocation safe = new DomsLocation(l);
                    player.playEffect(safe.getSafeLocation().toLocation(), Effect.MOBSPAWNER_FLAMES, null);
                }
            }
            
            if(!w) {
                for(int i = r.getZ(); i < r.getMaxZ(); i++) {
                    Location l = r.getBukkitWorld().getBlockAt(r.getX(), m, i).getLocation();
                    DomsLocation safe = new DomsLocation(l);
                    player.playEffect(safe.getSafeLocation().toLocation(), Effect.MOBSPAWNER_FLAMES, null);
                }
            }
        }
    }
    
    public void delete() {
        this.bank.delete();
        if(this.map != null) this.map.unload();
    }

    public TaxData getTaxData(Tax t) {
        for(TaxData td : this.taxData) {
            if(td.getTax().equals(t)) return td;
        }
        
        return null;
    }

    public boolean doesResidentOwnPlot(Region r, Resident resident) {
        if(resident == null) return false;
        if(!this.isResident(resident)) return false;
        if(this.mayor.equals(resident)) return false;
        Plot p = this.getPlot(r);
        if(p == null) return false;
        if(p.getOwner() == null) return false;
        return p.getOwner().equals(resident);
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
