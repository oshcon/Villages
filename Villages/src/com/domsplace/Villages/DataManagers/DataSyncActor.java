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

package com.domsplace.Villages.DataManagers;

import com.domsplace.Villages.Bases.Base;
import com.domsplace.Villages.Bases.DataManager;
import com.domsplace.Villages.Commands.SubCommands.VillageCreate;
import com.domsplace.Villages.Commands.SubCommands.VillageInvite;
import com.domsplace.Villages.Enums.ManagerType;
import com.domsplace.Villages.Exceptions.InvalidItemException;
import com.domsplace.Villages.Objects.Bank;
import com.domsplace.Villages.Objects.Plot;
import com.domsplace.Villages.Objects.Region;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.Tax;
import com.domsplace.Villages.Objects.TaxData;
import com.domsplace.Villages.Objects.Village;
import com.domsplace.Villages.Objects.DomsItem;
import com.domsplace.Villages.Objects.DomsLocation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class DataSyncActor extends DataManager {
    private static final String EXTENSION = ".yml";
    
    private File directory;
    
    public DataSyncActor() {
        super(ManagerType.VILLAGE);
    }
    
    @Override
    public void tryLoad() throws IOException {
        this.loadAllVillages();
        
        for(Player p : Bukkit.getOnlinePlayers()) {
            Resident.registerResident(Bukkit.getOfflinePlayer(p.getName()));
        }
        
        //Reset Village Invites
        VillageInvite.VILLAGE_INVITES.clear();
    }
    
    @Override
    public void trySave() throws IOException {
        if(Base.useSQL) {
            saveSQLResidents();
        }
        for(Village village : Village.getVillages()) {
            saveVillage(village);
        }
    }
    
    public void saveVillage(Village village) throws IOException {
        if(!Base.useSQL) {
            saveVillageAsYML(village);
        } else {
            saveVillageAsSQL(village);
        }
    }
    
    public void loadAllVillages() throws IOException {
        Village.deRegisterVillages(Village.getVillages());
        if(!Base.useSQL) {
            loadAllVillagesYML();
        } else {
            loadAllVillagesSQL();
        }
    }
    
    public  void loadAllVillagesYML() throws IOException {
        this.directory = new File(getDataFolder(), "villages");
        if(!this.directory.exists()) this.directory.mkdir();
        File[] villages = this.directory.listFiles();
        for(File f : villages) {
            Village v = this.loadVillageYML(f);
            if(v == null) {log("Failed to load " + f.getName() + " as a Village!"); continue;}
            debug("Loaded Village " + v.getName() + "!");
            Village.registerVillage(v);
        }
    }
    
    public Village loadVillageYML(File file) {
        if(!file.getName().toLowerCase().endsWith(EXTENSION.toLowerCase())) return null;
        
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        
        String name = yml.getString("name", null);
        if(name == null || !name.matches(VillageCreate.VILLAGE_NAME_REGEX)) return null;
        
        String description = yml.getString("description");
        Resident mayor = Resident.getResident(yml.getString("mayor", null));
        if(mayor == null) return null;
        
        Village v = new Village();
        
        v.setName(yml.getString("name"));
        v.setDescription(yml.getString("description"));
        v.setMayor(Resident.getResident(yml.getString("mayor")));
        v.setCreatedDate(yml.getLong("createdDate", getNow()));
        
        for(String s : yml.getStringList("residents")) {
            Resident r = Resident.getResident(s);
            v.addResident(r);
        }
        
        for(String r : yml.getStringList("regions")) {
            Region reg = Region.getRegion(r);
            if(r == null) {
                log(v.getName() + " has an invalid region \"" + r + "\"");
                continue;
            }
            
            v.addRegion(reg);
        }
        
        Region spawn = Region.getRegion(yml.getString("spawn"));
        if(spawn != null) {
            v.setSpawn(new DomsLocation(spawn.getSafeMiddle()));
        } else {
            v.setSpawn(DomsLocation.guessLocation(yml.getString("spawn")));
        }
        
        if(!v.getSpawn().isWorldLoaded()) {
            log("Village \"" + v.getName() + "\" is in a non loaded world! Village map may contains errors!");
        }
        
        if(yml.contains("bank.wealth")) {
            v.getBank().setWealth(yml.getDouble("bank.wealth"));
        }
        
        if(yml.contains("bank.items")) {
            for(String k : ((MemorySection) yml.get("bank.items")).getKeys(false)) {
                String itemdata = yml.getString("bank.items." + k);
                try {
                    List<DomsItem> items = DomsItem.createItems(itemdata);
                    v.getBank().addItems(items);
                } catch(InvalidItemException e) {
                    log("bank.items." + k + " is an invalid item.");
                }
            }
        }
        
        if(yml.contains("plots")) {
            for(String k : ((MemorySection) yml.get("plots")).getKeys(false)) {
                Region r = Region.getRegion(yml.getString("plots." + k + ".region"));
                Plot p = new Plot(v, r);
                v.addPlot(p);
                
                if(yml.contains("plots." + k + ".owner")) {
                    p.setOwner(Resident.getResident(yml.getString("plots." + k + ".owner")));
                }
                
                if(yml.contains("plots." + k + ".price")) {
                    p.setPrice(yml.getDouble("plots." + k + ".price"));
                }
            }
        }
        
        if(yml.contains("taxdata")) {
            for(String k : ((MemorySection) yml.get("taxdata")).getKeys(false)) {
                Tax t = Tax.getTaxByName(yml.getString("taxdata." + k + ".name"));
                long time = yml.getLong("taxdata." + k + ".lastchecked");
                TaxData td = new TaxData(v, t, time);
                v.addTaxData(td);
            }
        }
        
        if(yml.contains("friends")) {
            List<String> friends = yml.getStringList("friends");
            for(String s : friends) {
                v.addFriend(s);
            }
        }
        
        if(yml.contains("foes")) {
            List<String> foes = yml.getStringList("foes");
            for(String s : foes) {
                v.addFoe(s);
            }
        }
        
        return v;
    }
    
    public void loadAllVillagesSQL() {
        List<Map<String, String>> results = DataManager.SQL_MANAGER.fetch("SELECT * FROM `%db%`.`%t%Villages`;");
        for(Map<String, String> result : results) {
            this.loadVillageSQL(result);
        }
    }
    
    public void loadVillageSQL(Map<String, String> data) {
        Village v = new Village();
        v.setName(DataManager.SQL_MANAGER.sqlUnescape(data.get("VillageName")));
        v.setDescription(DataManager.SQL_MANAGER.sqlUnescape(data.get("VillageDescription")));
        
        Resident mayor = this.getResidentFromID(getInt(data.get("PlayerID")));
        v.setMayor(mayor);
        
        v.getBank().setWealth(getDouble(data.get("VillageBank")));
        v.setCreatedDate(DataManager.SQL_MANAGER.sqlToDate(data.get("VillageCreateDate")).getTime());
        
        int villageID = getInt(data.get("VillageID"));
        
        String query = "SELECT `PlotID` FROM `%db%`.`%t%Spawns` WHERE `VillageID`='" + villageID + "';";
        List<Map<String, String>> result = DataManager.SQL_MANAGER.fetch(query);
        v.setSpawn(new DomsLocation(this.getRegionFromID(getInt(result.get(0).get("PlotID"))).getSafeMiddle()));
        
        for(Object region : this.getRegionsFromVillage(v, villageID)) {
            if(region instanceof Region) {
                v.addRegion((Region) region);
            } else if(region instanceof Plot) {
                Plot p = (Plot) region;
                v.addRegion(p.getRegion());
                v.addPlot(p);
            }
        }
        
        //Load in Residents
        query = "SELECT `PlayerName` FROM `%db%`.`%t%Players` "
                + "INNER JOIN `%db%`.`%t%Residents` ON ("
                + "`%db%`.`%t%Players`.`PlayerID` = `%db%`.`%t%Residents`.`PlayerID`"
                + ") AND (`%db%`.`%t%Residents`.`VillageID` = '" + villageID + "');";
        result = DataManager.SQL_MANAGER.fetch(query);
        for(Map<String, String> res : result) {
            Resident re = Resident.getResident(res.get("PlayerName"));
            v.addResident(re);
        }
        
        query = "SELECT `ItemID` FROM `%db%`.`%t%BankItems` WHERE `VillageID`='" + villageID + "';";
        result = DataManager.SQL_MANAGER.fetch(query);
        for(Map<String, String> res : result) {
            int itemID = getInt(res.get("ItemID"));
            try {
                DomsItem item = this.getItemFromID(itemID);
                v.getBank().addItem(item);
            } catch(Exception e){}
        }
        
        query = "SELECT `%t%Villages`.`VillageName`,`%t%Relations`.`isFriend` FROM `%db%`.`%t%Villages`,`%db%`.`%t%Relations` WHERE `%db%`.`%t%Relations`.`VillageID`='" + villageID + "' AND `%db%`.`%t%Relations`.`TargetVillageID` = `%db%`.`%t%Villages`.`VillageID`;";
        result = DataManager.SQL_MANAGER.fetch(query);
        for(Map<String, String> res : result) {
            try {
                String village = res.get("VillageName").toString();
                boolean isFriend = res.get("isFriend").toString().equalsIgnoreCase("1");
                if(isFriend) {
                    sendMessage("friend");
                    v.addFriend(village);
                } else {
                    sendMessage("foe");
                    v.addFoe(village);
                }
            } catch(Exception e){
                error("Failed loading Village Wars", e);
            }
        }
        
        Village.registerVillage(v);
    }
    
    public void saveVillageAsYML(Village village) throws IOException {
        //Delete the old Village
        File villageFile = new File(this.directory, village.getName() + EXTENSION);
        if(villageFile.exists()) villageFile.delete();
        
        villageFile.createNewFile();
        YamlConfiguration yml = new YamlConfiguration();
        
        yml.set("name", village.getName());
        yml.set("description", village.getDescription());
        yml.set("mayor", village.getMayor().getName());
        yml.set("residents", village.getResidentsAsString());
        yml.set("createdDate", village.getCreatedDate());
        yml.set("spawn", village.getSpawn().toString());
        yml.set("regions", village.getRegionsAsString());
        yml.set("friends", village.getFriends());
        yml.set("foes", village.getFoes());
        
        if(village.getBank().getWealth() > 0d) {
            yml.set("bank.wealth", village.getBank().getWealth());
        }
        
        int pn = 0;
        for(Plot p : village.getPlots()) {
            if(p == null) continue;
            yml.set("plots.plot" + pn + ".region", p.getRegion().toString());
            if(p.getOwner() != null) {
                yml.set("plots.plot" + pn + ".owner", p.getOwner().getName());
            }
            
            if(p.getPrice() >= 0d) {
                yml.set("plots.plot" + pn + ".price", p.getPrice());
            }
            pn++;
        }
        
        int tn = 0;
        for(TaxData td : village.getTaxData()) {
            String name = "taxdata" + tn;
            
            yml.set("taxdata." + name + ".name", td.getTax().getName());
            yml.set("taxdata." + name + ".lastchecked", td.getLastChecked());
            
            tn++;
        }
        
        Bank b = village.getBank();
        List<DomsItem> currentBankItems = b.getItemsFromInventory();
        
        int viid = 0;
        for(DomsItem vi : currentBankItems) {
            String n = "item" + viid;
            
            yml.set("bank.items." + n, vi.toString());
            
            viid++;
        }
        
        yml.save(villageFile);
    }
    
    public void saveSQLResidents() throws IOException {
        //Make sure to update books that are registered
        for(Village v : Village.getVillages()) {
            for(DomsItem item : v.getBank().getItemsFromInventory()) {
                if(item.getBookAuthor() == null) continue;
                Resident r = Resident.getResident(item.getBookAuthor());
            }
        }
        
        List<Resident> residents = Resident.getRegisteredResidents();
        if(residents.size() < 1) return;
        
        String query = "INSERT IGNORE INTO `%db%`.`%t%Players` (`PlayerName`) VALUES ";
        
        for(Resident r : residents) {
            //Sheeeeeeet forgot to escape
            query += "('" + DataManager.SQL_MANAGER.sqlEscape(r.getName()) + "'), ";
        }
        
        query = query.substring(0, query.length() - 2);
        query += ";";
        
        DataManager.SQL_MANAGER.query(query);
    }
    
    public void saveVillageAsSQL(Village village) throws IOException {
        //Save Village
        String query = "INSERT IGNORE INTO `%db%`.`%t%Villages` ("
                + "`VillageName`, `VillageDescription`, `VillageCreateDate`, "
                + "`VillageBank`, `PlayerID`) VALUES ("
                + "'" + DataManager.SQL_MANAGER.sqlEscape(village.getName()) + "', "
                + "'" + DataManager.SQL_MANAGER.sqlEscape(village.getDescription()) + "', "
                + "'" + DataManager.SQL_MANAGER.dateToSQL(new Date(village.getCreatedDate())) + "', "
                + "'" + Double.toString(village.getBank().getWealth()) + "', "
                + "'" + this.getResidentID(village.getMayor()) + "');"
        ;
        
        DataManager.SQL_MANAGER.query(query);
        
        //Update Village (Incase village already existed)
        query = "UPDATE `%db%`.`%t%Villages` SET " +
                "`VillageName`='" + DataManager.SQL_MANAGER.sqlEscape(village.getName()) + "', "
                + "`VillageDescription`='" + DataManager.SQL_MANAGER.sqlEscape(village.getDescription()) + "', "
                + "`VillageCreateDate`='" + DataManager.SQL_MANAGER.dateToSQL(new Date(village.getCreatedDate())) + "', "
                + "`VillageBank`='" + Double.toString(village.getBank().getWealth()) + "', "
                + "`PlayerID`='" + this.getResidentID(village.getMayor()) + "' "
                + "WHERE `VillageName`='" + DataManager.SQL_MANAGER.sqlEscape(village.getName()) + "';";
        DataManager.SQL_MANAGER.query(query);
        
        int villageID = this.getVillageID(village.getName());
        
        if(villageID == -1) throw new IOException("Village failed to store into database");
        
        //Clear Village Bank, and respective associated Village Bank Data
        query = "SELECT `ItemID` FROM `%db%`.`%t%BankItems` WHERE `VillageID`='"+villageID+"';";
        List<Map<String, String>> result = DataManager.SQL_MANAGER.fetch(query);
        for(Map<String, String> item : result) {
            int id = getInt(item.get("ItemID"));
            
            query = "DELETE FROM `%db%`.`%t%ItemBooks` WHERE `ItemID`='" + id + "';";
            DataManager.SQL_MANAGER.query(query);
            
            query = "DELETE FROM `%db%`.`%t%ItemLores` WHERE `ItemID`='" + id + "';";
            DataManager.SQL_MANAGER.query(query);
            
            query = "DELETE FROM `%db%`.`%t%ItemNames` WHERE `ItemID`='" + id + "';";
            DataManager.SQL_MANAGER.query(query);
            
            query = "DELETE FROM `%db%`.`%t%ItemEnchantments` WHERE `ItemID`='" + id + "';";
            DataManager.SQL_MANAGER.query(query);
            
            query = "DELETE FROM `%db%`.`%t%Items` WHERE `ItemID`='" + id + "';";
            DataManager.SQL_MANAGER.query(query);
        }   
        query = "DELETE FROM `%db%`.`%t%Residents` WHERE `VillageID` = '" + villageID + "';";
        DataManager.SQL_MANAGER.query(query);
        
        query = "DELETE FROM `%db%`.`%t%Relations` WHERE `VillageID` = '" + villageID + "';";
        DataManager.SQL_MANAGER.query(query);
        
        query = "DELETE FROM `%db%`.`%t%BankItems` WHERE `VillageID` = '" + villageID + "';";
        DataManager.SQL_MANAGER.query(query);
        
        //Clear Old Residents
        query = "DELETE FROM `%db%`.`%t%Residents` WHERE `VillageID` = '" + villageID + "';";
        DataManager.SQL_MANAGER.query(query);
        
        //Save Residents
        if(village.getResidents().size() > 0) {
            query = "REPLACE INTO `%db%`.`%t%Residents` ("
                    + "`PlayerID`, `VillageID`) VALUES ";
            for(Resident r : village.getResidents()) {
                query += "('" + this.getResidentID(r) + "', '" + villageID + "'), ";
            }
            
            query = query.substring(0, query.length() - 2) + ";";
            DataManager.SQL_MANAGER.query(query);
        }
        
        //Remove Old Regions
        query = "DELETE FROM `%db%`.`%t%Plots` WHERE `VillageID` = '" + villageID + "';";
        DataManager.SQL_MANAGER.query(query);
        
        //Store Regions
        if(village.getRegions().size() > 0) {
            query = "INSERT INTO `%db%`.`%t%Plots` ("
                    + "`VillageID`, `PlotX`, `PlotZ` ,`PlotWorld`) VALUES ";
            for(Region r : village.getRegions()) {
                query += "('" + villageID + "', '" + r.getX() + "', "
                        + "'" + r.getZ() +"', '" + r.getWorld() + "'"
                        + "), ";
            }
            
            query = query.substring(0, query.length() - 2);
            DataManager.SQL_MANAGER.query(query);
        }
        
        //Update Plot Data
        for(Plot p : village.getPlots()) {
            query = "UPDATE `%db%`.`%t%Plots` SET ";
            boolean changed = false;
            
            if(p.getPrice() >= 0) {
                query += "`PlotPrice`='" + p.getPrice() + "', ";
                changed = true;
            }
            
            if(p.getOwner() != null) {
                query += "`PlayerID`='" + this.getResidentID(p.getOwner()) + "', ";
                changed = true;
            }
            
            query = query.substring(0, query.length() - 2);
            
            query += " WHERE "
                    + "`PlotX`='" + p.getRegion().getX() + "' AND "
                    + "`PlotZ`='" + p.getRegion().getZ() + "' AND "
                    + "`PlotWorld`='" + DataManager.SQL_MANAGER.sqlEscape(p.getRegion().getWorld()) + "' AND "
                    + "`VillageID`='" + villageID + "' "
                    + "LIMIT 1;";
            
            if(!changed) continue;
            DataManager.SQL_MANAGER.query(query);
        }
        
        //Store Bank
        for(DomsItem item : village.getBank().getItemsFromInventory()) {
            query = "INSERT INTO `%db%`.`%t%Items` (`ID`";
            query += ", `Data`";
            query += ") VALUES ('" + item.getMaterialName()+ "'";
            query += ", '" + item.getData() + "'";
            
            query += ");";
            
            long id = DataManager.SQL_MANAGER.queryReturnID(query);
            
            //Add Enchantments (if any)
            if(item.getEnchantments() != null && item.getEnchantments().size() > 0) {
                query = "INSERT INTO `%db%`.`%t%ItemEnchantments` (`ItemID`, `EnchantmentName`, `EnchantmentLevel`) VALUES";
                for(Enchantment e : item.getEnchantments().keySet()) {
                    query += "('" + id + "', '" + e.getName() + "', '" + item.getEnchantments().get(e) + "'), ";
                }
                
                query = query.substring(0, query.length() - 2) + ";";
                DataManager.SQL_MANAGER.query(query);
            }
            
            //Store Item Names (If one exists)
            if(item.getName() != null && !item.getName().equals("")) {
                query = "INSERT INTO `%db%`.`%t%ItemNames` (`ItemID`, `ItemName`) "
                        + "VALUES ('" + id + "', '" + DataManager.SQL_MANAGER.sqlEscape(item.getName()) + "');";
                DataManager.SQL_MANAGER.query(query);
            }
            
            //Store Item Lores (if needed)
            if(item.getLores() != null && item.getLores().size() > 0) {
                query = "INSERT INTO `%db%`.`%t%ItemLores` (`ItemID`, `ItemLore`) VALUES ";
                for(String lore : item.getLores()) {
                    query += "('" + id + "', '" + DataManager.SQL_MANAGER.sqlEscape(lore) + "'), ";
                }
                
                query = query.substring(0, query.length() - 2) + ";";
                DataManager.SQL_MANAGER.query(query);
            }
            
            //Store Pages (If Any)
            if(item.getBookPages() != null && item.getBookPages().size() > 0) {
                //Get Author
                Resident author = village.getMayor();
                if(item.getBookAuthor() != null && !item.getBookAuthor().equals("")) {
                    author = Resident.getResident(item.getBookAuthor());
                }
                
                //TODO: Fix book title (planned)
                int authorID = this.getResidentID(author);
                query = "INSERT INTO `%db%`.`%t%ItemBooks` ("
                        + "`ItemID`, `PlayerID`, `ItemBookPage`, `ItemBookData`) VALUES ";
                int i = 0;
                for(String page : item.getBookPages()) {
                    query += "('"+id+"', '" + authorID + "', '" + i + "', '" + DataManager.SQL_MANAGER.sqlEscape(page) + "'), ";
                    i++;
                }
                
                query = query.substring(0, query.length() - 2) + ";";
                DataManager.SQL_MANAGER.query(query);
            }
            
            //Finally, Set Item to this Village
            query = "INSERT INTO `%db%`.`%t%BankItems` (`ItemID`, `VillageID`) VALUES('" + id + "', '" + villageID + "');";
            DataManager.SQL_MANAGER.query(query);
        }
        
        //Save Friends/Foes
        query = "INSERT INTO `%db%`.`%t%Relations` (`VillageID`, `TargetVillageID`, `isFriend`) VALUES ";
        for(String s : village.getFriends()) {
            try {query += "('" + villageID + "', '" + this.getVillageID(s) + "', '1'),";} catch(Exception e) {}
        }
        for(String s : village.getFoes()) {
            try {query += "('" + villageID + "', '" + this.getVillageID(s) + "', '0'),";} catch(Exception e) {}
        }
        if(village.getFriends().size() + village.getFoes().size() > 0) {
            query = query.substring(0, query.length() - 1) + ";";
            try {DataManager.SQL_MANAGER.query(query);} catch(Exception e) {}
        }
        
        //Last Step, Set the spawn
        query = "REPLACE INTO `%db%`.`%t%Spawns` (`VillageID`, `PlotID`) VALUES ('" + villageID + "', '" + this.getPlotID(village.getSpawnRegion()) + "');";
        DataManager.SQL_MANAGER.query(query);
    }
    
    //Use to delete the Village after it's name has been changed.
    public void changeVillageName(Village village, String newName) {
        if(Base.useSQL) {
            String query = "UPDATE `%db%`.`%t%Villages` SET "
                    + "`VillageName`='" + DataManager.SQL_MANAGER.sqlEscape(newName) + "' "
                    + "WHERE `VillageName`='" + DataManager.SQL_MANAGER.sqlEscape(village.getName()) + "' "
                    + "LIMIT 1;";
            DataManager.SQL_MANAGER.query(query);
            return;
        }
        File f = new File(this.directory, village.getName() + EXTENSION);
        if(f.exists()) f.delete();
    }
    
    public void deleteVillage(Village v) {
        Village.deRegisterVillage(v);
        v.delete();
        if(!Base.useSQL) {
            File f = new File(this.directory, v.getName() + EXTENSION);
            if(f.exists()) f.delete();
        } else {
            //Clear Village Bank, and respective associated Village Bank Data
            int villageID = this.getVillageID(v.getName());
            
            String query = "SELECT `ItemID` FROM `%db%`.`%t%BankItems` WHERE `VillageID`='"+villageID+"';";
            List<Map<String, String>> result = DataManager.SQL_MANAGER.fetch(query);
            for(Map<String, String> item : result) {
                int id = getInt(item.get("ItemID"));

                query = "DELETE FROM `%db%`.`%t%ItemBooks` WHERE `ItemID`='" + id + "';";
                DataManager.SQL_MANAGER.query(query);

                query = "DELETE FROM `%db%`.`%t%ItemLores` WHERE `ItemID`='" + id + "';";
                DataManager.SQL_MANAGER.query(query);

                query = "DELETE FROM `%db%`.`%t%ItemNames` WHERE `ItemID`='" + id + "';";
                DataManager.SQL_MANAGER.query(query);

                query = "DELETE FROM `%db%`.`%t%ItemEnchantments` WHERE `ItemID`='" + id + "';";
                DataManager.SQL_MANAGER.query(query);

                query = "DELETE FROM `%db%`.`%t%Items` WHERE `ItemID`='" + id + "';";
                DataManager.SQL_MANAGER.query(query);
            }

            query = "DELETE FROM `%db%`.`%t%BankItems` WHERE `VillageID`='" + villageID + "';";
            DataManager.SQL_MANAGER.query(query);
            
            query = "DELETE FROM `%db%`.`%t%Residents` WHERE `VillageID` = '" + villageID + "';";
            DataManager.SQL_MANAGER.query(query);
            
            query = "DELETE FROM `%db%`.`%t%Spawns` WHERE `VillageID` = '" + villageID + "';";
            DataManager.SQL_MANAGER.query(query);
            
            query = "DELETE FROM `%db%`.`%t%Plots` WHERE `VillageID` = '" + villageID + "';";
            DataManager.SQL_MANAGER.query(query);
            
            query = "DELETE FROM `%db%`.`%t%Relations` WHERE `VillageID` = '" + villageID + "';";
            DataManager.SQL_MANAGER.query(query);
            
            query = "DELETE FROM `%db%`.`%t%Relations` WHERE `TargetVillageID` = '" + villageID + "';";
            DataManager.SQL_MANAGER.query(query);
            
            query = "DELETE FROM `%db%`.`%t%Villages` WHERE `VillageID` = '" + villageID + "';";
            DataManager.SQL_MANAGER.query(query);
        }
        v = null;
    }

    public int getResidentID(Resident r) {
        String query = "SELECT `PlayerID` FROM `%db%`.`%t%Players` WHERE `PlayerName`='" + r.getName() + "' LIMIT 1;";
        try {
            return getInt(DataManager.SQL_MANAGER.fetch(query).get(0).get("PlayerID"));
        } catch(Exception e) {
            return -1;
        }
    }
    
    public int getPlotID(Region r) {
        String query = "SELECT `PlotID` from `%db%`.`%t%Plots` WHERE "
                + "`PlotX`='" + r.getX() + "' AND "
                + "`PlotZ`='" + r.getZ() + "' AND "
                + "`PlotWorld`='" + r.getWorld() + "';";
        try {
            return getInt(DataManager.SQL_MANAGER.fetch(query).get(0).get("PlotID"));
        } catch(Exception e) {
            return -1;
        }
    }

    public int getVillageID(String name) {
        String query = "SELECT `VillageID` FROM `%db%`.`%t%Villages` WHERE `VillageName`='" + name + "' LIMIT 1;";
        try {
            return getInt(DataManager.SQL_MANAGER.fetch(query).get(0).get("VillageID"));
        } catch(Exception e) {
            return -1;
        }
    }
    
    public Resident getResidentFromID(int id) {
        String query = "SELECT `PlayerName` FROM `%db%`.`%t%Players` WHERE `PlayerID`='" + id + "' LIMIT 1;";
        Resident r = Resident.getResident(DataManager.SQL_MANAGER.fetch(query).get(0).get("PlayerName"));
        return r;
    }
    
    public Region getRegionFromID(int id) {
        String query = "SELECT * FROM `%db%`.`%t%Plots` WHERE `PlotID`='" + id + "' LIMIT 1;";
        List<Map<String, String>> result = DataManager.SQL_MANAGER.fetch(query);
        Map<String, String> res = result.get(0);
        Region r = Region.getRegion(getInt(res.get("PlotX")), getInt(res.get("PlotZ")), res.get("PlotWorld"));
        return r;
    }
    
    public List<Object> getRegionsFromVillage(Village village, int id) {
        String query = "SELECT * FROM `%db%`.`%t%Plots` WHERE `VillageID`='" + id + "';";
        List<Map<String, String>> result = DataManager.SQL_MANAGER.fetch(query);
        
        List<Object> regions = new ArrayList<Object>();
        for(Map<String, String> res : result) {
            Region r = Region.getRegion(getInt(res.get("PlotX")), getInt(res.get("PlotZ")), res.get("PlotWorld"));
            
            boolean isPlot = false;
            
            if(res.containsKey("PlayerID") || res.containsKey("PlotPrice")) {
                String playerID = res.get("PlayerID");
                String plotPrice = res.get("PlotPrice");
                
                boolean plot = (playerID != null && !playerID.equalsIgnoreCase("null")) && (plotPrice != null && !plotPrice.equalsIgnoreCase("null"));
                if(plot) {
                    isPlot = true;
                    Plot p = new Plot(village, r);
                    
                    if(playerID != null && !playerID.equalsIgnoreCase("null") && isInt(playerID)) {
                        Resident resident = this.getResidentFromID(getInt(playerID));
                        p.setOwner(resident);
                    }
                    
                    if(plotPrice != null && !plotPrice.equalsIgnoreCase("null") && isDouble(plotPrice)) {
                        double price = getDouble(plotPrice);
                        p.setPrice(price);
                    }
                    
                    regions.add(p);
                }
            }
            
            if(!isPlot) {
                regions.add(r);
            }
        }
        
        return regions;
    }
    
    public DomsItem getItemFromID(int id) {
        String query = "SELECT `ID`, `Data` FROM `%db%`.`%t%Items` WHERE `ItemID`='" + id + "' LIMIT 1;";
        Map<String, String> result = DataManager.SQL_MANAGER.fetch(query).get(0);
        
        DomsItem item = new DomsItem(result.get("ID"), getShort(result.get("Data")));
        
        //Get Additional data
        query = "SELECT `ItemName` FROM `%db%`.`%t%ItemNames` WHERE `ItemID`='" + id + "';";
        List<Map<String, String>> names = DataManager.SQL_MANAGER.fetch(query);
        if(names != null && names.size() > 0) {
            try {
                String name = names.get(0).get("ItemName");
                if(!name.equalsIgnoreCase("null")) {
                    item.setName(name);
                }
            } catch(Exception e) {}
        }
        
        query = "SELECT `ItemLore` FROM `%db%`.`%t%ItemLores` WHERE `ItemID`='" + id + "';";
        List<Map<String, String>> lores = DataManager.SQL_MANAGER.fetch(query);
        for(Map<String, String> lore : lores) {
            try {
                String l = DataManager.SQL_MANAGER.sqlUnescape(lore.get("ItemLore"));
                if(l.equalsIgnoreCase("null")) continue;
                item.addLore(l);
            } catch(Exception e) {}
        }
        
        query = "SELECT `EnchantmentID`, `EnchantmentLevel` FROM `%db%`.`%t%ItemEnchantments` WHERE `ItemID`='" + id + "';";
        List<Map<String, String>> enchants = DataManager.SQL_MANAGER.fetch(query);
        for(Map<String, String> enchant : enchants) {
            try {
                String eid = enchant.get("EnchantmentName");
                int lvl = getInt(enchant.get("EnchantmentLevel"));
                
                Enchantment en = Enchantment.getByName(eid);
                
                if(isInt(eid)) {
                    en = Enchantment.getById(getInt(eid));
                }
                
                item.addEnchantment(en, lvl);
            } catch(Exception e) {}
        }
        
        query = "SELECT `ItemBookData`, `ItemBookPage`, `ItemBookTitle`, `PlayerID` FROM `%db%`.`%t%ItemBooks` WHERE `ItemID`='" + id + "';";
        List<Map<String, String>> books = DataManager.SQL_MANAGER.fetch(query);
        for(Map<String, String> book : books) {
            try {
                String l = DataManager.SQL_MANAGER.sqlUnescape(book.get("ItemBookData"));
                int page = getInt(book.get("ItemBookPage"));
                //String title = DataManager.SQL_MANAGER.sqlUnescape(book.get("ItemBookTitle"));
                Resident author = this.getResidentFromID(getInt(book.get("PlayerID")));
                item.setAuthor(author.getName());
                item.addPage(l);
            } catch(Exception e) {}
        }        
        
        return item;
    }
}
