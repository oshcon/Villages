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
import com.domsplace.Villages.Exceptions.InvalidItemException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Bank {
    public static Bank getBank(Inventory inv) {
        return Bank.getBank(inv.getTitle());
    }
    
    public static Bank getBank(String title) {
        for(Village v : Village.getVillages()) {
            if(v == null) continue;
            if(v.getBank() == null) continue;
            if(v.getBank().getGUI() == null) continue;
            
            if(v.getBank().getGUI().getName().equalsIgnoreCase(title)) return v.getBank();
        }
        
        return null;
    }
    
    //Instance
    private double wealth;
    private Village village;
    private Inventory bankGUI;
    
    public Bank(Village v) {
        this.wealth = 0d;
        this.village = v;
        this.updateGUI();
    }
    
    public double getWealth() {return this.wealth;}
    public Village getVillage() {return this.village;}
    public Inventory getGUI() {return this.bankGUI;}
    
    public void setWealth(double wealth) {this.wealth = wealth;}

    public void addWealth(double d) {
        this.setWealth(this.getWealth() + d);
    }

    public void delete() {
        this.updateGUI();
        this.bankGUI.clear();
    }
    
    protected void updateGUI() {
        if(this.bankGUI != null) {
            List<HumanEntity> ents = new ArrayList<HumanEntity>(this.bankGUI.getViewers());
            for(HumanEntity e : ents) {
                if(e == null) continue;
                e.closeInventory();
            }
            
            this.bankGUI.clear();
        }
        
        this.bankGUI = Bukkit.createInventory(null, 54, Base.ChatImportant + this.village.getName() + Base.ChatImportant + " Bank");
    }
    
    private void initGUI() {
        if(this.bankGUI != null) return;
        this.updateGUI();
    }
    
    public List<DomsItem> getItemsFromInventory() {
        this.initGUI();
        List<DomsItem> items = new ArrayList<DomsItem>();
        
        for(ItemStack is : this.bankGUI.getContents()) {
            if(is == null || is.getType() == null) continue;
            items.addAll(DomsItem.itemStackToDomsItems(is));
        }
        
        return items;
    }

    public boolean containsItems(List<DomsItem> relativeItemsCost) {
        return DomsItem.contains(this.getItemsFromInventory(), relativeItemsCost);
    }

    public void addItems(List<DomsItem> items) throws InvalidItemException {
        this.initGUI();
        try {
            List<ItemStack> is = DomsItem.toItemStackArray(items);
            for(ItemStack i : is) {
                this.bankGUI.addItem(i);
            }
        } catch(Exception e){}
    }
    
    public void removeItems(List<DomsItem> relativeItemsCost) {
        this.initGUI();
        for(DomsItem i : relativeItemsCost) {
            this.removeItem(i);
        }
    }
    
    public void removeItem(DomsItem item) {
        this.initGUI();
        ItemStack is = null;
        for(ItemStack i : this.bankGUI.getContents()) {
            if(i == null || i.getType() == null || i.getType().equals(Material.AIR)) continue;
            List<DomsItem> isc = DomsItem.itemStackToDomsItems(i);
            if(!DomsItem.contains(isc, item)) continue;
            is = i;
        }
        
        if(is == null) return;
        if(is.getAmount() > 1) {is.setAmount(is.getAmount()- 1); return;}
        
        this.bankGUI.remove(is);
    }

    public void addItem(DomsItem item) throws InvalidItemException {
        List<DomsItem> items = new ArrayList<DomsItem>();
        items.add(item);
        this.addItems(items);
    }
}
