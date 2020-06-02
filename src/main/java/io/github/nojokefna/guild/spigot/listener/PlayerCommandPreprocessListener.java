package io.github.nojokefna.guild.spigot.listener;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.cache.CacheUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class PlayerCommandPreprocessListener implements Listener {

    @EventHandler
    public void onPlayerCommandPreprocess( PlayerCommandPreprocessEvent event ) {
        Player player = event.getPlayer();
        CacheUser user = CacheUser.getUser( player );

        if ( Guild.getPlugin().getSettingsManager().getFileConfiguration().getBoolean( "chat.chat_settings.commands" ) )
            event.setCancelled( user.getCacheMethods().getCoolDown( player, user.getCommandCoolDown(), "commands" ) );
    }
}
