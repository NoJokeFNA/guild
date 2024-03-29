package io.github.nojokefna.guild.spigot.listener;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.cache.CacheUser;
import io.github.nojokefna.guild.spigot.controller.GuildController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class AsyncPlayerPreLoginListener implements Listener {

    private final GuildController guildController;

    public AsyncPlayerPreLoginListener() {
        this.guildController = new GuildController();
    }

    @EventHandler( priority = EventPriority.HIGHEST )
    public void onAsyncPlayerPreLogin( AsyncPlayerPreLoginEvent event ) {
        final UUID playerUuid = event.getUniqueId();
        final String playerName = event.getName();
        final CacheUser user = CacheUser.getUserByUuid( playerUuid );

        user.setLoaded( new AtomicBoolean( false ) );

        long startUp = System.currentTimeMillis();

        while ( !user.getLoaded().get() ) {
            user.setInGuild( Guild.getPlugin().getGuildUserAPI().playerExists( playerUuid ) );

            user.setMaster( user.isInGuild() && this.guildController.isGuildMaster( playerUuid ) );
            user.setOfficer( user.isInGuild() && this.guildController.isGuildOfficer( playerUuid ) );
            user.setMember( user.isInGuild() && this.guildController.isGuildMember( playerUuid ) );

            user.setGuildRank( user.isInGuild() ? this.guildController.sendGuildRank( playerUuid ) : "none" );
            user.setGuildName( user.isInGuild() ? this.guildController.sendGuildName( playerUuid ) : "none" );

            user.setCoins( this.guildController.getCoins( playerUuid ) );

            user.setLoaded( new AtomicBoolean( true ) );
        }

        System.out.println( " " );
        System.out.println( "Login took: " + ( System.currentTimeMillis() - startUp ) + " ms" );
        System.out.println( " " );
        System.out.println( "UUID: " + playerUuid );
        System.out.println( "Name: " + playerName );
        System.out.println( "Coins: " + user.getCoins() );
        System.out.println( "Guild: " + user.isInGuild() );
        System.out.println( "Master: " + user.isMaster() );
        System.out.println( "Officer: " + user.isOfficer() );
        System.out.println( "Member: " + user.isMember() );
        System.out.println( "Guild name: " + user.getGuildName() );
        System.out.println( "Guild rank: " + user.getGuildRank() );
        System.out.println( " " );
    }
}
