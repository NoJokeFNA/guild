package io.github.nojokefna.guild.spigot.listener;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.cache.CacheUser;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class PlayerQuitListener implements Listener {

    private final FileBuilder fileBuilder;

    public PlayerQuitListener() {
        this.fileBuilder = Guild.getPlugin().getServerSettingsManager();
    }

    @EventHandler( priority = EventPriority.HIGH, ignoreCancelled = true )
    public void onPlayerQuit( PlayerQuitEvent event ) {
        Player player = event.getPlayer();

        if ( this.fileBuilder.getBoolean( "quit.enable" ) ) {
            if ( this.fileBuilder.getBoolean( "quit.admin.bypass" )
                    && player.hasPermission( this.fileBuilder.getKey( "quit.admin.permission" ) ) ) {
                event.setQuitMessage( null );
                return;
            }

            event.setQuitMessage( Guild.getPlugin().getServerSettingsManager().getKey( "quit.message" )
                    .replace( "{PLAYER}", player.getName() )
                    .replace( "{DISPLAYNAME}", player.getDisplayName() )
                    .replace( "{GROUPPLAYER}", Objects.requireNonNull( Guild.getPlugin().getData().getGroup( player ) ) )
            );
        }

        CacheUser.deleteUser( player );
    }
}
