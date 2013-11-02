package com.domsplace.Villages.Commands;

import com.domsplace.Villages.Bases.BukkitCommand;
import com.domsplace.Villages.Objects.Resident;
import com.domsplace.Villages.Objects.SubCommandOption;
import com.domsplace.Villages.Objects.Village;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VillageCommand extends BukkitCommand {
    public VillageCommand() {
        super("village");
        this.setPermission("village");
        
        //Set Tab Completer Options
        this.addSubCommandOption(new SubCommandOption("accept"));
        this.addSubCommandOption(new SubCommandOption("deny"));
        this.addSubCommandOption(new SubCommandOption("create", "name"));
        this.addSubCommandOption(new SubCommandOption("info", SubCommandOption.VILLAGES_OPTION));
        this.addSubCommandOption(new SubCommandOption("help"));
        this.addSubCommandOption(new SubCommandOption("invite", SubCommandOption.PLAYERS_OPTION));
        this.addSubCommandOption(new SubCommandOption("leave"));
        this.addSubCommandOption(new SubCommandOption("list"));
        this.addSubCommandOption(new SubCommandOption("lookup", SubCommandOption.PLAYERS_OPTION));
        this.addSubCommandOption(new SubCommandOption("map"));
        this.addSubCommandOption(new SubCommandOption("mesage", "message"));
        this.addSubCommandOption(new SubCommandOption("spawn"));
        this.addSubCommandOption(new SubCommandOption("top"));
        
        //Admin Commands
        this.addSubCommandOption(new SubCommandOption("admin",
            new SubCommandOption("add",
                new SubCommandOption("player", new SubCommandOption(SubCommandOption.VILLAGES_OPTION, SubCommandOption.PLAYERS_OPTION))
            ),
            new SubCommandOption("remove",
                new SubCommandOption("player", SubCommandOption.PLAYERS_OPTION)
            ),
            new SubCommandOption("set",
                new SubCommandOption("description", new SubCommandOption(SubCommandOption.VILLAGES_OPTION, "description")),
                new SubCommandOption("mayor", new SubCommandOption(SubCommandOption.VILLAGES_OPTION, SubCommandOption.PLAYERS_OPTION)),
                new SubCommandOption("name", new SubCommandOption(SubCommandOption.VILLAGES_OPTION, "name"))
            ),
            new SubCommandOption("delete", SubCommandOption.VILLAGES_OPTION),
            new SubCommandOption("reload"),
            new SubCommandOption("save")
        ));
        
        //Bank Commands
        this.addSubCommandOption(new SubCommandOption("bank",
            new SubCommandOption("deposit", "amount"),
            new SubCommandOption("open"),
            new SubCommandOption("withdraw", "amount")
        ));
        
        //Mayor Commands
        this.addSubCommandOption(new SubCommandOption("mayor",
            "close",
            "expand",
            new SubCommandOption("explode", "YES"),
            new SubCommandOption("kick", SubCommandOption.RESIDENTS_OPTION),
            new SubCommandOption("set", 
                new SubCommandOption("description", "description"),
                new SubCommandOption("mayor", SubCommandOption.RESIDENTS_OPTION),
                new SubCommandOption("name", "name"),
                "spawn"
            )
        ));
        
        //Plot Commands
        this.addSubCommandOption(new SubCommandOption("plot",
            "check",
            "claim",
            new SubCommandOption("set",
                new SubCommandOption("owner", SubCommandOption.RESIDENTS_OPTION),
                new SubCommandOption("price", "price")
            )
        ));
        
        //Tax Commands
        this.addSubCommandOption(new SubCommandOption("tax", new SubCommandOption("check", SubCommandOption.TAXES_OPTION)));
    }
    
    @Override
    public boolean cmd(CommandSender sender, Command cmd, String label, String[] args) {
        if(!isPlayer(sender)) return false;
        
        Resident r = Resident.getResident(getPlayer(sender));
        Village v = Village.getPlayersVillage(r);
        if(v == null) {
            sk(sender, "notinvillage");
            return true;
        }
        
        return this.fakeExecute(sender, "village info");
    }
}
