package com.domsplace.Villages.DataManagers;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.domsplace.Villages.VillagesPlugin;
import com.domsplace.Villages.DataManagers.VillageManager;
import com.domsplace.Villages.Bases.Base;
import com.domsplace.Villages.Bases.DataManager;

	public class DataSyncManager{
		
		  Toolkit toolkit;
		  Timer timer;

		  public DataSyncManager(int seconds) {
			
		    toolkit = Toolkit.getDefaultToolkit();
		    timer = new Timer();
		    timer.schedule(new DataSyncTask(), seconds);
		  }
		
		  class DataSyncTask extends TimerTask {
			  
		    public void run() {
		    	 int delay = ConfigManager.getConfig().getInt("message");
		    	for ( ; ; ) {
		    		VillageManager villageobject = new VillageManager();
						villageobject.loadAllVillagesSQL();
		    		
					System.out.println("Village Data Synced");
		    		try {
		    		    Thread.sleep(delay * 1000);         
		    		} catch(InterruptedException ex) {
		    		    Thread.currentThread().interrupt();
		    		}
		    	}
		    }
		  }
		  public static void main(String args[]) {
		    System.out.println("Setting Up Village Data Sync");
		    System.out.println("Village Data Sync Set Up");
		   
		  }
		}
		        

