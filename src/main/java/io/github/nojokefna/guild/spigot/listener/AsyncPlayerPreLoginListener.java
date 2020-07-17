package io.github.nojokefna.guild.spigot.listener;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.cache.CacheUser;
import io.github.nojokefna.guild.spigot.controller.GuildRecodeController;
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

    private final GuildRecodeController guildController;

    public AsyncPlayerPreLoginListener() {
        this.guildController = Guild.getPlugin().getGuildRecodeController();
    }

    @EventHandler( priority = EventPriority.HIGHEST )
    public void onAsyncPlayerPreLogin( AsyncPlayerPreLoginEvent event ) {
        UUID playerUuid = event.getUniqueId();
        CacheUser user = CacheUser.getUserByUuid( playerUuid );

        user.setLoaded( new AtomicBoolean( false ) );

        while ( ! user.getLoaded().get() ) {
            user.setInGuild( Guild.getPlugin().getGuildUserAPI().keyExists( playerUuid ) );

            user.setMaster( this.guildController.isGuildMaster( playerUuid ) );
            user.setOfficer( this.guildController.isGuildOfficer( playerUuid ) );
            user.setMember( this.guildController.isGuildMember( playerUuid ) );

            user.setLoaded( new AtomicBoolean( true ) );
        }
    }
}
