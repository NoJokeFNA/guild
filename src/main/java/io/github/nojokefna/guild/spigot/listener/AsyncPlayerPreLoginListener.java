package io.github.nojokefna.guild.spigot.listener;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.cache.CacheUser;
import io.github.nojokefna.guild.spigot.controller.GuildController;
import org.bukkit.event.EventHandler;
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
        this.guildController = Guild.getPlugin().getGuildController();
    }

    @EventHandler
    public void onAsyncPlayerPreLogin( AsyncPlayerPreLoginEvent event ) {
        UUID uuid = event.getUniqueId();
        CacheUser user = CacheUser.getUserByUuid( uuid );

        user.setLoaded( new AtomicBoolean( false ) );

        while ( ! user.getLoaded().get() ) {
            user.setInGuild( Guild.getPlugin().getGuildUserAPI().keyExists( uuid ) );

            user.setMaster( this.guildController.isGuildMaster( uuid ) );
            user.setOfficer( this.guildController.isGuildOfficer( uuid ) );
            user.setMember( this.guildController.isGuildMember( uuid ) );

            user.setLoaded( new AtomicBoolean( true ) );
        }
    }
}
