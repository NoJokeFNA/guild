package io.github.nojokefna.guild.spigot.listener;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.build.TabListBuilder;
import io.github.nojokefna.guild.spigot.cache.CacheUser;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class PlayerJoinListener implements Listener {

    private final FileBuilder fileBuilder;

    private final boolean value;

    public PlayerJoinListener() {
        this.fileBuilder = Guild.getPlugin().getServerSettingsManager();

        this.value = this.fileBuilder.getBoolean( "tablist.labymod.use_labymod" );
    }

    @EventHandler( priority = EventPriority.HIGH, ignoreCancelled = true )
    public void onPlayerJoin( PlayerJoinEvent event ) {
        Player player = event.getPlayer();
        CacheUser user = CacheUser.getUser( player );

        System.out.println( "PlayerJoinEvent: " + player.getUniqueId() );

        if ( this.fileBuilder.getBoolean( "tablist.use_header_footer" ) )
            Guild.getPlugin().getData().sendTablist( player, this.fileBuilder.getKey( "tablist.header" ),
                    this.fileBuilder.getKey( "tablist.footer" ) );

        user.getCacheMethods().initTeams( player );
        user.getCacheMethods().setPrefix( player );

        if ( this.value )
            Bukkit.getOnlinePlayers().forEach( players -> user.getCacheMethods().setSubtitle( player, player.getUniqueId(), players ) );

        TabListBuilder.setNameTag();

        if ( player.getUniqueId().toString().equals( "609b6216-b1f7-40fe-997b-5f1f975cc712" ) )
            player.sendMessage( "Guild-System detected" );

        if ( this.fileBuilder.getBoolean( "join.enable" ) ) {
            if ( this.fileBuilder.getBoolean( "join.admin.bypass" )
                    && player.hasPermission( this.fileBuilder.getKey( "join.admin.permission" ) ) ) {
                event.setJoinMessage( null );
                return;
            }

            event.setJoinMessage( this.fileBuilder.getKey( "join.message" )
                    .replace( "{PLAYER}", player.getName() )
                    .replace( "{DISPLAYNAME}", player.getDisplayName() )
                    .replace( "{GROUPPLAYER}", Objects.requireNonNull( Guild.getPlugin().getData().getGroup( player ) ) )
            );
        }
    }
}