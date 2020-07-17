package io.github.nojokefna.guild.spigot.listener;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.cache.CacheUser;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import org.bukkit.ChatColor;
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

    private final FileBuilder fileBuilder;

    public PlayerKickListener() {
        this.fileBuilder = Guild.getPlugin().getServerSettingsBuilder();
    }

    @EventHandler
    public void onPlayerKick( PlayerKickEvent event ) {
        Player player = event.getPlayer();

        if ( this.fileBuilder.getBoolean( "kick.enable" ) ) {
            if ( this.fileBuilder.getBoolean( "kick.admin.bypass" ) && player.hasPermission( this.fileBuilder.getKey( "kick.admin.permission" ) ) ) {
                event.setLeaveMessage( null );
                return;
            }

            event.setLeaveMessage( Guild.getPlugin().getServerSettingsBuilder().getKey( "kick.message" )
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
