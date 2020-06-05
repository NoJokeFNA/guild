package io.github.nojokefna.guild.spigot.listener;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class ServerListPingListener implements Listener {

    private final FileBuilder fileBuilder;

    public ServerListPingListener() {
        this.fileBuilder = Guild.getPlugin().getServerSettingsManager();
    }

    @EventHandler
    public void onServerListPing( ServerListPingEvent event ) {
        if ( this.fileBuilder.getBoolean( "motd.use_motd" ) )
            event.setMotd( this.fileBuilder.getKey( "motd.text" ) );

        if ( this.fileBuilder.getBoolean( "motd.use_max_players" ) )
            event.setMaxPlayers( this.fileBuilder.getInt( "motd.max_players" ) );
    }
}
