package io.github.nojokefna.guild.spigot.listener;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.cache.CacheUser;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import java.util.Objects;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class PlayerKickListener implements Listener {

    private final ConfigurationSection section;

    public PlayerKickListener() {
        this.section = Guild.getPlugin().getServerSettingsManager().getFileConfiguration();
    }

    @EventHandler
    public void onPlayerKick( PlayerKickEvent event ) {
        Player player = event.getPlayer();

        if ( this.section.getBoolean( "kick.enable" ) ) {
            if ( this.section.getBoolean( "kick.admin.bypass" ) && player.hasPermission( this.section.getString( "kick.admin.permission" ) ) ) {
                event.setLeaveMessage( null );
                return;
            }

            event.setLeaveMessage( Guild.getPlugin().getServerSettingsManager().getKey( "kick.message" )
                                           .replace( "{PLAYER}", player.getName() )
                                           .replace( "{DISPLAYNAME}", player.getDisplayName() )
                                           .replace( "{GROUPPLAYER}", Objects.requireNonNull( Guild.getPlugin().getData().getGroup( player ) ) )

            );
        }

        CacheUser.deleteUser( player );
    }

    private String sendColoredMessage( String message ) {
        return ChatColor.translateAlternateColorCodes( '&', message );
    }
}
