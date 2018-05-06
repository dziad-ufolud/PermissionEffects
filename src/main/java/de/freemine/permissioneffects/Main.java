package de.freemine.permissioneffects;

import de.freemine.permissioneffects.Listener.Mainlistener;
import de.freemine.permissioneffects.command.MainCommand;
import de.freemine.permissioneffects.command.ToggleCommand;
import de.freemine.permissioneffects.util.SettingsFile;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

/**
 * @author LPkkjHD
 */
public class Main extends ExtendedJavaPlugin {

    public void enable() {
        Reference.plugin = this;
        SettingsFile.init();
        getServer().getPluginManager().registerEvents(new Mainlistener(this), this);
        new MainCommand(this);
        new ToggleCommand();
    }

    @Override
    public void disable() {
        getLogger().info("Disabling PermissionEffects by LPkkjHD");
        SettingsFile.save();
    }
}
