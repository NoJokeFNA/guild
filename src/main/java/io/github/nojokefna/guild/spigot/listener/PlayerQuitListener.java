package io.github.nojokefna.guild.spigot.listener;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.cache.CacheUser;
import org.bukkit.configuration.ConfigurationSection;
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

    private final ConfigurationSection section;

    public PlayerQuitListener() {
        this.section = Guild.getPlugin().getServerSettingsManager().getFileConfiguration();
    }

    @EventHandler( priority = EventPriority.HIGH, ignoreCancelled = true )
    public void onPlayerQuit( PlayerQuitEvent event ) {
        Player player = event.getPlayer();

        if ( this.section.getBoolean( "quit.enable" ) ) {
            if ( this.section.getBoolean( "quit.admin.bypass" ) && player.hasPermission( this.section.getString( "quit.admin.permission" ) ) ) {
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
