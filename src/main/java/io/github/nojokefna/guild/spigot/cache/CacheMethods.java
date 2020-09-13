package io.github.nojokefna.guild.spigot.cache;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
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

    public CacheMethods() {
        this.section = Guild.getPlugin().getChatSettingsBuilder().getConfigurationSection();
        this.serverSection = Guild.getPlugin().getServerSettingsBuilder().getConfigurationSection();
        this.server = Guild.getPlugin().getServerSettingsBuilder();
    }

    public boolean getCoolDown( Player player, Map<UUID, Long> currentMap, String message ) {
        final UUID playerUuid = player.getUniqueId();

        if ( player.hasPermission( this.section.getString( "chat.chat_settings.bypass_" + message + "_permission" ) ) )
            return false;

        if ( currentMap.containsKey( playerUuid ) ) {
            final float time = ( System.currentTimeMillis() - currentMap.get( playerUuid ) ) / 1000;

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
        final CacheUser user = CacheUser.getUser( player );

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
                        this.sendColoredMessage( permissionNameValues.get( 0 ).replace( "{PLAYER}", player.getName() ) ),
                        this.sendColoredMessage( permissionNameValues.get( 1 ).replace( "{PLAYER}", player.getName() ) ),
                        this.sendColoredMessage( guildSuffix ),
                        permissionNameValues.get( 5 )
                );
            } else {
                new RankCache(
                        permissionNameValues.get( 3 ),
                        this.sendColoredMessage( permissionNameValues.get( 0 ) ),
                        this.sendColoredMessage( permissionNameValues.get( 1 ) ),
                        " ",
                        permissionNameValues.get( 5 )
                );
            }
        } );
    }

    public synchronized void setPrefix( Player player ) {
        final CacheUser user = CacheUser.getUser( player );

        this.serverSection.getConfigurationSection( "tablist.prefix" ).getKeys( false ).stream()
                .map( permissionName -> this.serverSection.getStringList( "tablist.prefix." + permissionName ) ).forEach( permissionNameValues -> {

            final String rank = permissionNameValues.get( 3 );
            final String permission = permissionNameValues.get( 4 );

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
        final CacheUser user = CacheUser.getUser( receiver );

        final JsonArray jsonArray = new JsonArray();
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty( "uuid", subtitlePlayer.toString() );
        jsonObject.addProperty( "size", 0.8d );

        if ( this.serverSection.getBoolean( "tablist.labymod.use_labymod" ) ) {
            if ( !user.isInGuild() )
                jsonObject.addProperty( "value", this.server.getKey( "tablist.labymod.subTitle_normal" )
                        .replace( "{PLAYER}", receiver.getName() )
                        .replace( "{DISPLAYNAME}", receiver.getDisplayName() )
                        .replace( "{GROUPPLAYER}", Objects.requireNonNull( Guild.getPlugin().getData().getGroup( receiver ) ) )
                );
            else {
                if ( user.isMember() )
                    jsonObject.addProperty( "value", this.server.getKey( "tablist.labymod.subTitle_member" )
                            .replace( "{PLAYER}", receiver.getName() )
                            .replace( "{DISPLAYNAME}", receiver.getDisplayName() )
                            .replace( "{GROUPPLAYER}", Objects.requireNonNull( Guild.getPlugin().getData().getGroup( receiver ) ) )
                            .replace( "{GUILD}", Guild.getPlugin().getGuildController().sendGuildName( receiver ) )
                    );

                else if ( user.isOfficer() )
                    jsonObject.addProperty( "value", this.server.getKey( "tablist.labymod.subTitle_officer" )
                            .replace( "{PLAYER}", receiver.getName() )
                            .replace( "{DISPLAYNAME}", receiver.getDisplayName() )
                            .replace( "{GROUPPLAYER}", Objects.requireNonNull( Guild.getPlugin().getData().getGroup( receiver ) ) )
                            .replace( "{GUILD}", Guild.getPlugin().getGuildController().sendGuildName( receiver ) )
                    );

                else if ( user.isMaster() )
                    jsonObject.addProperty( "value", this.server.getKey( "tablist.labymod.subTitle_master" )
                            .replace( "{PLAYER}", receiver.getName() )
                            .replace( "{DISPLAYNAME}", receiver.getDisplayName() )
                            .replace( "{GROUPPLAYER}", Objects.requireNonNull( Guild.getPlugin().getData().getGroup( receiver ) ) )
                            .replace( "{GUILD}", Guild.getPlugin().getGuildController().sendGuildName( receiver ) )
                    );
            }
        }

        jsonArray.add( jsonObject );

        LabyModPlugin.getInstance().sendServerMessage( target, "account_subtitle", jsonArray );
    }

    private String sendColoredMessage( String message ) {
        return ChatColor.translateAlternateColorCodes( '&', message );
    }
}
