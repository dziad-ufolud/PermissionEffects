package de.freemine.permissioneffects.command;

import de.freemine.permissioneffects.Reference;
import org.bukkit.command.*;

import java.util.List;


/*
    creates a command and registers it with aliases
 */
public abstract class PECommand implements CommandExecutor, TabCompleter {


    /*
    Registers a command and sets the information
     */

    public PECommand(String name) {
        List<String> aliases = ((List<String>) Reference.plugin.getDescription().getCommands().get(name).get("aliases"));
        String description = (String) Reference.plugin.getDescription().getCommands().get(name).get("description");
        String permission = (String) Reference.plugin.getDescription().getCommands().get(name).get("permission");
        String permissionMessage = (String) Reference.plugin.getDescription().getCommands().get(name).get("permission-message");
        String usage = (String) Reference.plugin.getDescription().getCommands().get(name).get("usage");
        Reference.plugin.registerCommand(this, aliases.toArray(new String[0]));
        for (String ali : aliases) {
            PluginCommand command = Reference.plugin.getCommand(ali);
            command.setName(name);
            command.setAliases(aliases);
            command.setDescription(description);
            command.setPermission(permission);
            command.setPermissionMessage(permissionMessage);
            command.setUsage(usage);
        }

    }

    @Override
    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

}
