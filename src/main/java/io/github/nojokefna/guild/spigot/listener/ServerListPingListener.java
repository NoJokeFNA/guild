package io.github.nojokefna.guild.spigot.listener;

import io.github.nojokefna.guild.spigot.Guild;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class ServerListPingListener implements Listener {

    @EventHandler
    public void onServerListPing( ServerListPingEvent event ) {
        if ( Guild.getPlugin().getServerSettingsManager().getFileConfiguration().getBoolean( "motd.use_motd" ))
            event.setMotd( Guild.getPlugin().getServerSettingsManager().getKey( "motd.text" ) );
    }
}
