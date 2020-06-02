package io.github.nojokefna.guild.spigot.database.api;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.interfaces.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class GuildInvitesAPI extends MySQL {

    public boolean keyExists( String keyValue, String guildKey ) {
        return this.keyExists( "guild_invites", keyValue, guildKey );
    }

    public void createPlayer( UUID playerUuid, OfflinePlayer player, String invitedName, String guildName, String guildTag ) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously( Guild.getPlugin(), () -> {
            try {
                PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder().getDatabase()
                        .getConnection()
                        .prepareStatement( "INSERT INTO `guild_invites` (player_uuid, player_name, invited_name, guild_name, guild_tag)" +
                                                   " VALUES (?, ?, ?, ?, ?)" );

                preparedStatement.setString( 1, playerUuid.toString() );
                preparedStatement.setString( 2, player.getName() );
                preparedStatement.setString( 3, invitedName );
                preparedStatement.setString( 4, guildName );
                preparedStatement.setString( 5, guildTag );

                Guild.getPlugin().getDatabaseBuilder().getDatabase().queryUpdate( preparedStatement );
            } catch ( SQLException ex ) {
                ex.printStackTrace();
            }
        } );
    }

    public void deleteInvite( OfflinePlayer player ) {
        this.deleteKey( "guild_invites", "player_name", player.getName() );
    }

    public String getInvite( OfflinePlayer player, String key ) {
        return this.getKey( "guild_invites", "player_name", player.getName(), "guild_name" );
    }

    public List<String> getInvites( Player player, String key ) {
        return this.getList( "guild_invites", "player_name", player.getName(), key );
    }

    public List<String> getInvites( UUID playerUuid, String key ) {
        return this.getList( "guild_invites", "player_uuid", playerUuid.toString(), key );
    }
}