package io.github.nojokefna.guild.spigot.listener;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.build.ScoreboardBuilder;
import io.github.nojokefna.guild.spigot.build.TablistBuilder;
import io.github.nojokefna.guild.spigot.cache.CacheUser;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Objects;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class PlayerJoinListener implements Listener {

    private final FileBuilder serverBuilder;

    private final boolean value;

    public PlayerJoinListener() {
        this.serverBuilder = Guild.getPlugin().getServerSettingsBuilder();

        this.value = this.serverBuilder.getBoolean( "tablist.labymod.use_labymod" );
    }

    @EventHandler( priority = EventPriority.HIGH, ignoreCancelled = true )
    public void onPlayerJoin( PlayerJoinEvent event ) {
        final Player player = event.getPlayer();
        final CacheUser user = CacheUser.getUser( player );

        if ( this.serverBuilder.getBoolean( "tablist.use_header_footer" ) )
            Guild.getPlugin().getData().sendTablist( player, this.serverBuilder.getKey( "tablist.header" ), this.serverBuilder.getKey( "tablist.footer" ) );

        if ( this.serverBuilder.getBoolean( "tablist.use_scoreboard" ) ) {
            new ScoreboardBuilder( "scoreboard", DisplaySlot.SIDEBAR, "&6Guild-System", player )
                    .addScore( "§r§8§m⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊", 9 )

                    .addScore( "§6Online player", 8 )
                    .addTeam( "online", "§8» &c" + Bukkit.getOnlinePlayers().size() + " §7/ §c" + Bukkit.getMaxPlayers(), "§1", 7 )

                    .addScore( "§r   §8§m⚊⚊⚊⚊⚊§r", 6 )

                    .addScore( "§6Your guild", 5 )
                    .addTeam( "guild", "§8» &c" + user.getGuildName(), "§2", 4 )

                    .addScore( "§r   §8§m⚊⚊⚊⚊⚊", 3 )

                    .addScore( "§6Your rank", 2 )
                    .addTeam( "rank", "§8» &c" + user.getGuildRank(), "§3", 1 )

                    .addScore( "§8§m⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊", 0 )

                    .sendScoreboard();

            Bukkit.getOnlinePlayers().forEach( onlinePlayers -> ScoreboardBuilder.updateTeam(
                    onlinePlayers,
                    "online",
                    "§8» &c" + Bukkit.getOnlinePlayers().size() + " §7/ §c" + Bukkit.getMaxPlayers()
            ) );
        }

        if ( this.serverBuilder.getBoolean( "tablist.use_tablist" ) ) {
            user.getCacheMethods().initTeams( player );
            user.getCacheMethods().setPrefix( player );

            TablistBuilder.setNameTag();
        }

        if ( this.value )
            Bukkit.getOnlinePlayers().forEach( players -> user.getCacheMethods().setSubtitle( player, player.getUniqueId(), players ) );

        if ( player.getUniqueId().toString().equals( "609b6216-b1f7-40fe-997b-5f1f975cc712" ) )
            player.sendMessage( "Guild-System detected" );

        if ( this.serverBuilder.getBoolean( "join.enable" ) ) {
            if ( this.serverBuilder.getBoolean( "join.admin.bypass" ) && player.hasPermission( this.serverBuilder.getKey( "join.admin.permission" ) ) ) {
                event.setJoinMessage( null );
                return;
            }

            event.setJoinMessage( this.serverBuilder.getKey( "join.message" )
                                          .replace( "{PLAYER}", player.getName() )
                                          .replace( "{DISPLAYNAME}", player.getDisplayName() )
                                          .replace( "{GROUPPLAYER}", Objects.requireNonNull( Guild.getPlugin().getData().getGroup( player ) ) )
            );
        }
    }
}