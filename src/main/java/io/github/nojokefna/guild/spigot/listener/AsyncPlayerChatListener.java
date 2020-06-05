package io.github.nojokefna.guild.spigot.listener;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.cache.CacheUser;
import io.github.nojokefna.guild.spigot.controller.GuildController;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class AsyncPlayerChatListener implements Listener {

    private final ConfigurationSection section;

    public AsyncPlayerChatListener() {
        this.section = Guild.getPlugin().getSettingsManager().getConfigurationSection();
    }

    @EventHandler( priority = EventPriority.HIGH, ignoreCancelled = true )
    public void onChat( AsyncPlayerChatEvent event ) {
        Player player = event.getPlayer();
        CacheUser user = CacheUser.getUser( player );

        GuildController handler = Guild.getPlugin().getGuildController();

        if ( this.section.getBoolean( "chat.use_chat" ) && this.section.getBoolean( "chat.use_chat_feature" ) ) {
            for ( Player players : Bukkit.getOnlinePlayers() ) {
                if ( players.hasPermission( "guild.team" ) )
                    players.sendMessage( "§4§lPlease use §cuse_chat §4§lor §cuse_chat_feature§4§l!" );
            }
            event.setCancelled( true );
            return;
        }

        if ( this.section.getBoolean( "chat.use_chat" ) ) {
            String message = this.section.getString( "chat.chat_format_guild" )
                    .replace( "{PLAYER}", player.getName() )
                    .replace( "{DISPLAYNAME}", player.getDisplayName() )
                    .replace( "{MESSAGE}", player.hasPermission( this.section.getString( "chat.guild_chat_color_permission" ) )
                            ? this.sendColoredMessage( event.getMessage() ) : event.getMessage() )
                    .replace( "{GROUPPLAYER}", Objects.requireNonNull( Guild.getPlugin().getData().getGroup( player ) ) )
                    .replace( "%", "%%" );


            if ( user.isInGuild() ) {
                switch ( this.section.getString( "chat.guild_format" ) ) {
                    case "UpperCase":
                        message = message.replace( "{GUILD}", Guild.getPlugin().getGuildController().sendGuildTag( player ).toUpperCase() );
                        event.setFormat( this.sendColoredMessage( message ) );
                        break;

                    case "lowerCase":
                        message = message.replace( "{GUILD}", Guild.getPlugin().getGuildController().sendGuildTag( player ).toLowerCase() );
                        event.setFormat( this.sendColoredMessage( message ) );
                        break;

                    case "normal":
                        message = message.replace( "{GUILD}", Guild.getPlugin().getGuildController().sendGuildTag( player ) );
                        event.setFormat( this.sendColoredMessage( message ) );
                        break;
                }
            } else {
                event.setFormat( message );
            }

        } else if ( this.section.getBoolean( "chat.use_chat_feature" ) ) {
            this.section.getConfigurationSection( "chat.permissions" ).getKeys( false ).stream()
                    .map( permissionName -> this.section.getStringList( "chat.permissions." + permissionName ) ).forEach( permissionNameValues -> {

                if ( player.hasPermission( permissionNameValues.get( 2 ) ) ) {

                    String message = permissionNameValues.get( 0 )
                            .replace( "{PLAYER}", player.getName() )
                            .replace( "{DISPLAYNAME}", player.getDisplayName() )
                            .replace( "{MESSAGE}", player.hasPermission( this.section.getString( "chat.guild_chat_color_permission" ) )
                                    ? this.sendColoredMessage( event.getMessage() ) : event.getMessage() )
                            .replace( "{GROUPPLAYER}", Objects.requireNonNull( Guild.getPlugin().getData().getGroup( player ) ) )
                            .replace( "%", "%%" );

                    String messageTwo = permissionNameValues.get( 1 )
                            .replace( "{PLAYER}", player.getName() )
                            .replace( "{DISPLAYNAME}", player.getDisplayName() )
                            .replace( "{MESSAGE}", player.hasPermission( this.section.getString( "chat.guild_chat_color_permission" ) )
                                    ? this.sendColoredMessage( event.getMessage() ) : event.getMessage() )
                            .replace( "{GROUPPLAYER}", Objects.requireNonNull( Guild.getPlugin().getData().getGroup( player ) ) )
                            .replace( "%", "%%" );

                    if ( user.isInGuild() ) {
                        switch ( this.section.getString( "chat.guild_format" ) ) {
                            case "UpperCase":
                                message = message.replace( "{GUILD}", Guild.getPlugin().getGuildController().sendGuildTag( player ).toUpperCase() );
                                event.setFormat( this.sendColoredMessage( message ) );
                                break;

                            case "lowerCase":
                                message = message.replace( "{GUILD}", Guild.getPlugin().getGuildController().sendGuildTag( player ).toLowerCase() );
                                event.setFormat( this.sendColoredMessage( message ) );
                                break;

                            case "normal":
                                message = message.replace( "{GUILD}", Guild.getPlugin().getGuildController().sendGuildTag( player ) );
                                event.setFormat( this.sendColoredMessage( message ) );
                                break;

                            default:
                                throw new IllegalStateException( "Unexpected value: " + this.section.getString( "chat.guild_format" ) );
                        }

                    } else {
                        event.setFormat( messageTwo );
                    }
                }
            } );
        }

        if ( handler.getGuildMessageList().contains( player.getName() ) ) {
            event.setCancelled( true );
            handler.sendMessage( player, event.getMessage() );
        }

        if ( this.section.getBoolean( "chat.chat_settings.chat" ) )
            event.setCancelled( user.getCacheMethods().getCoolDown( player, user.getMessageCoolDown(), "chat" ) );

        if ( this.section.getBoolean( "chat.chat_settings.repeat" ) ) {
            if ( user.getMessage().containsKey( player.getUniqueId() ) ) {
                event.setCancelled( true );
                user.getMessage().remove( player.getUniqueId() );
            } else
                event.setCancelled( false );
            user.getMessage().put( player.getUniqueId(), event.getMessage() );
        }
    }

    private String sendColoredMessage( String message ) {
        return ChatColor.translateAlternateColorCodes( '&', message );
    }
}