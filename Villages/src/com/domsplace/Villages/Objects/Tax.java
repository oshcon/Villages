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

import com.domsplace.Villages.Enums.TaxMultiplierType;
import java.util.ArrayList;
import java.util.List;

public class Tax {
    private static List<Tax> TAXES = new ArrayList<Tax>();
    
    public static List<Tax> getTaxes() {
        return new ArrayList<Tax>(TAXES);
    }
    
    public static void deRegsiterTaxes() {
        for(Tax t : getTaxes()) {
            deRegisterTax(t);
        }
    }
    
    private static void deRegisterTax(Tax tax) {
        TAXES.remove(tax);
        tax = null;
    }
    
    private static void registerTax(Tax tax) {
        TAXES.add(tax);
    }

    public static Tax getTaxByName(String string) {
        for(Tax t : TAXES) {
            if(t.getName().toLowerCase().startsWith(string.toLowerCase())) return t;
        }
        
        return null;
    }
    
    //Instance
    private String message;
    private double hours;
    private double money;
    private TaxMultiplierType type;
    private double taxMultiplier;
    private List<DomsItem> items;
    private String name;
    
    public Tax(String name, String message, double hours, double money, TaxMultiplierType type, double taxMultiplier, List<DomsItem> items) {
        this.name = name;
        this.message = message;
        this.hours = hours;
        this.money = money;
        this.type = type;
        this.taxMultiplier = taxMultiplier;
        this.items = items;
        
        this.register();
    }
    
    public String getMessage() {return this.message;}
    public double getHours() {return this.hours;}
    public double getMoney() {return this.money;}
    public TaxMultiplierType getMultiplierType() {return this.type;}
    public double getTaxMultiplier() {return this.taxMultiplier;}
    public List<DomsItem> getItems() {return this.items;}
    public String getName() {return this.name;}
    
    private void register() {Tax.registerTax(this);}

    public double getRelativeCost(Village village) {
        double c = this.money;
        
        if(this.type.equals(TaxMultiplierType.CHUNK)) {
            double x = ((village.getRegions().size()-1) * this.taxMultiplier);
            if(x < 1) {
                x = 1;
            }
            c *= x;
        } else if(this.type.equals(TaxMultiplierType.PLAYER)) {
            double x = ((village.getResidents().size()-1) * this.taxMultiplier);
            if(x < 1) {
                x = 1;
            }
            c *= x;
        }
        
        return c;
    }
    
    public List<DomsItem> getRelativeItemsCost(Village v) {
        List<DomsItem> items = new ArrayList<DomsItem>();
        
        int c = 1;
        
        if(this.type.equals(TaxMultiplierType.CHUNK)) {
            double x = ((v.getRegions().size()-1) * this.taxMultiplier);
            if(x < 1) {
                x = 1;
            }
            c *= x;
        } else if(this.type.equals(TaxMultiplierType.PLAYER)) {
            double x = ((v.getResidents().size()-1) * this.taxMultiplier);
            if(x < 1) {
                x = 1;
            }
            c *= x;
        }
        
        for(int i = 0; i < c; i++) {
            items.addAll(this.items);
        }
        
        return items;
    }
}
