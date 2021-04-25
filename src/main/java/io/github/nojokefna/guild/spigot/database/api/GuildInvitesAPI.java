package io.github.nojokefna.guild.spigot.database.api;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.database.AbstractMySQL;
import io.github.nojokefna.guild.spigot.database.provider.DatabaseProvider;
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
public class GuildInvitesAPI extends AbstractMySQL {

    private final DatabaseProvider databaseProvider;

    public GuildInvitesAPI() {
        this.databaseProvider = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider();
        super.setTable( "guild_invites" );
    }

    public boolean keyExists( String keyValue, String guildKey ) {
        return super.keyExists( keyValue, guildKey );
    }

    public void createPlayer( UUID playerUuid, OfflinePlayer player, String invitedName, String guildName, String guildTag ) {
        Guild.getPlugin().getExecutorService().submit( () -> {
            try {
                try (
                        PreparedStatement preparedStatement = this.databaseProvider.prepareStatement(
                                "INSERT INTO `guild_invites` (player_uuid, player_name, invited_name, guild_name, guild_tag) VALUES (?, ?, ?, ?, ?)"
                        )
                ) {

                    preparedStatement.setString( 1, playerUuid.toString() );
                    preparedStatement.setString( 2, player.getName() );
                    preparedStatement.setString( 3, invitedName );
                    preparedStatement.setString( 4, guildName );
                    preparedStatement.setString( 5, guildTag );

                    preparedStatement.executeUpdate();
                }
            } catch ( SQLException ex ) {
                ex.printStackTrace();
            }
        } );
    }

    public void deleteInvite( OfflinePlayer player ) {
        super.deleteKey( "player_name", player.getName() );
    }

    public String getInvite( OfflinePlayer player, String key ) {
        return super.getKey( "player_name", player.getName(), "guild_name" );
    }

    public List<String> getInvites( Player player, String key ) {
        return super.getList( "player_name", player.getName(), key );
    }

    public List<String> getInvites( UUID playerUuid, String key ) {
        return super.getList( "player_uuid", playerUuid.toString(), key );
    }
}
