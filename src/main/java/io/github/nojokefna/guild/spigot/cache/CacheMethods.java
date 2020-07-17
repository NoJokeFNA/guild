package io.github.nojokefna.guild.spigot.cache;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import io.github.nojokefna.guild.spigot.controller.GuildRecodeController;
import net.labymod.serverapi.bukkit.LabyModPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class CacheMethods {

    private final FileBuilder server;
    private final ConfigurationSection section, serverSection;

    private final GuildRecodeController guildController;

    public CacheMethods() {
        this.section = Guild.getPlugin().getSettingsManager().getConfigurationSection();
        this.serverSection = Guild.getPlugin().getServerSettingsBuilder().getConfigurationSection();
        this.server = Guild.getPlugin().getServerSettingsBuilder();
        this.guildController = Guild.getPlugin().getGuildRecodeController();
    }

    public boolean getCoolDown( Player player, Map<UUID, Long> currentMap, String message ) {
        UUID playerUuid = player.getUniqueId();

        if ( player.hasPermission( this.section.getString( "chat.chat_settings.bypass_" + message + "_permission" ) ) )
            return false;

        if ( currentMap.containsKey( playerUuid ) ) {
            float time = ( System.currentTimeMillis() - currentMap.get( playerUuid ) ) / 1000;
            if ( time < this.section.getInt( "chat.chat_settings.time_" + message ) ) {
                Guild.getPlugin().getGuildBuilder().sendMessage( player, this.section.getString( "chat.chat_settings.message_" + message ) );
                return true;
            } else
                currentMap.put( playerUuid, System.currentTimeMillis() );
        } else
            currentMap.put( playerUuid, System.currentTimeMillis() );
        return false;
    }

    public void initTeams( Player player ) {
        CacheUser user = CacheUser.getUser( player );

        this.serverSection.getConfigurationSection( "tablist.prefix" ).getKeys( false ).stream()
                .map( permissionName -> this.serverSection.getStringList( "tablist.prefix." + permissionName ) ).forEach( permissionNameValues -> {

            if ( user.isInGuild() ) {
                String guildSuffix = permissionNameValues.get( 2 )
                        .replace( "{GUILD}", Guild.getPlugin().getGuildController().sendGuildName( player ) );

                switch ( this.serverSection.getString( "tablist.guild_format" ) ) {
                    case "UpperCase":
                        guildSuffix = guildSuffix.toUpperCase();
                        break;

                    case "lowerCase":
                        guildSuffix = guildSuffix.toLowerCase();
                        break;

                    case "normal":
                        break;

                    default:
                        throw new IllegalStateException( "Unexpected value: " + this.serverSection.getString( "tablist.guild_format" ) );
                }

                new RankCache(
                        permissionNameValues.get( 3 ),
                        new String[] { this.sendColoredMessage( permissionNameValues.get( 0 ).replace( "{PLAYER}", player.getName() ) ) },
                        new String[] { this.sendColoredMessage( permissionNameValues.get( 1 ).replace( "{PLAYER}", player.getName() ) ) },
                        new String[] { " " + this.sendColoredMessage( guildSuffix ) },
                        permissionNameValues.get( 5 )
                );
            } else {
                new RankCache(
                        permissionNameValues.get( 3 ),
                        new String[] { this.sendColoredMessage( permissionNameValues.get( 0 ) ) },
                        new String[] { this.sendColoredMessage( permissionNameValues.get( 1 ) ) },
                        new String[] { " " },
                        permissionNameValues.get( 5 )
                );
            }
        } );
    }

    public synchronized void setPrefix( Player player ) {
        CacheUser user = CacheUser.getUser( player );

        this.serverSection.getConfigurationSection( "tablist.prefix" ).getKeys( false ).stream()
                .map( permissionName -> this.serverSection.getStringList( "tablist.prefix." + permissionName ) ).forEach( permissionNameValues -> {

            String rank = permissionNameValues.get( 3 );
            String permission = permissionNameValues.get( 4 );

            if ( this.serverSection.getBoolean( "tablist.use_permissions" ) ) {
                if ( player.hasPermission( permission ) ) {
                    user.setPrefix( RankCache.getRank( rank ).getPrefix() );
                    user.setNameTag( RankCache.getRank( rank ).getNameTag() );
                    user.setSuffix( RankCache.getRank( rank ).getSuffix() );
                    user.setTagId( RankCache.getRank( rank ).getTagId() );
                }
            } else {
                if ( Guild.getPlugin().getPermission().playerInGroup( player, rank ) ) {
                    user.setPrefix( RankCache.getRank( rank ).getPrefix() );
                    user.setNameTag( RankCache.getRank( rank ).getNameTag() );
                    user.setSuffix( RankCache.getRank( rank ).getSuffix() );
                    user.setTagId( RankCache.getRank( rank ).getTagId() );
                }
            }
        } );
    }

    public void setSubtitle( Player receiver, UUID subtitlePlayer, Player target ) {
        CacheUser user = CacheUser.getUser( receiver );

        JsonArray array = new JsonArray();
        JsonObject subtitle = new JsonObject();

        subtitle.addProperty( "uuid", subtitlePlayer.toString() );
        subtitle.addProperty( "size", 0.8d );

        if ( this.serverSection.getBoolean( "tablist.labymod.use_labymod" ) ) {
            if ( ! user.isInGuild() )
                subtitle.addProperty( "value", this.server.getKey( "tablist.labymod.subTitle_normal" )
                        .replace( "{PLAYER}", receiver.getName() )
                        .replace( "{DISPLAYNAME}", receiver.getDisplayName() )
                        .replace( "{GROUPPLAYER}", Objects.requireNonNull( Guild.getPlugin().getData().getGroup( receiver ) ) )
                );
            else {
                if ( user.isMember() )
                    subtitle.addProperty( "value", this.server.getKey( "tablist.labymod.subTitle_member" )
                            .replace( "{PLAYER}", receiver.getName() )
                            .replace( "{DISPLAYNAME}", receiver.getDisplayName() )
                            .replace( "{GROUPPLAYER}", Objects.requireNonNull( Guild.getPlugin().getData().getGroup( receiver ) ) )
                            .replace( "{GUILD}", Guild.getPlugin().getGuildController().sendGuildName( receiver ) )
                    );

                else if ( user.isOfficer() )
                    subtitle.addProperty( "value", this.server.getKey( "tablist.labymod.subTitle_officer" )
                            .replace( "{PLAYER}", receiver.getName() )
                            .replace( "{DISPLAYNAME}", receiver.getDisplayName() )
                            .replace( "{GROUPPLAYER}", Objects.requireNonNull( Guild.getPlugin().getData().getGroup( receiver ) ) )
                            .replace( "{GUILD}", Guild.getPlugin().getGuildController().sendGuildName( receiver ) )
                    );

                else if ( user.isMaster() )
                    subtitle.addProperty( "value", this.server.getKey( "tablist.labymod.subTitle_master" )
                            .replace( "{PLAYER}", receiver.getName() )
                            .replace( "{DISPLAYNAME}", receiver.getDisplayName() )
                            .replace( "{GROUPPLAYER}", Objects.requireNonNull( Guild.getPlugin().getData().getGroup( receiver ) ) )
                            .replace( "{GUILD}", Guild.getPlugin().getGuildController().sendGuildName( receiver ) )
                    );
            }
        }

        array.add( subtitle );

        LabyModPlugin.getInstance().sendServerMessage( target, "account_subtitle", array );
    }

    private String sendColoredMessage( String message ) {
        return ChatColor.translateAlternateColorCodes( '&', message );
    }
}
