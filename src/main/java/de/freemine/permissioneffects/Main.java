package de.freemine.permissioneffects;

import de.freemine.permissioneffects.Listener.Mainlistener;
import me.lucko.helper.plugin.ExtendedJavaPlugin;

/**
 * @author LPkkjHD
 */
public class Main extends ExtendedJavaPlugin {

    public void enable() {
        Reference.plugin = this;
        SettingsFile.init();
        getServer().getPluginManager().registerEvents(new Mainlistener(this), this);
        getCommand("PermissionEffects").setExecutor(new MainCommand(this));
        this.registerCommand(new ToggleCommand());
    }

    @Override
    public void disable() {
        getLogger().info("Disabling PermissionEffects by LPkkjHD");
    }
}
