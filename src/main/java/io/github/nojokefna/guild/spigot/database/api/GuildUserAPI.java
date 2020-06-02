package io.github.nojokefna.guild.spigot.database.api;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.interfaces.MySQL;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class GuildUserAPI extends MySQL {

    public boolean keyExists( UUID uuid ) {
        return this.keyExists( "guild_user", "player_uuid", uuid.toString() );
    }

    public boolean getGuilds( UUID uuid, String key ) {
        boolean value = false;
        if ( key == null )
            return false;

        try {
            PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder().getDatabase()
                    .getConnection()
                    .prepareStatement( "SELECT * FROM `guild_user` WHERE player_uuid = ? AND guild_rank = ?" );

            preparedStatement.setString( 1, uuid.toString() );
            preparedStatement.setString( 2, key );

            ResultSet resultSet = preparedStatement.executeQuery();
            if ( resultSet.next() )
                value = true;

            resultSet.close();
            preparedStatement.close();
        } catch ( SQLException ex ) {
            ex.printStackTrace();
        }
        return value;
    }

    public void createPlayer( UUID uuid, String playerName, String guildName, String guildTag, String guildRank ) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously( Guild.getPlugin(), () -> {
            if ( ! this.keyExists( uuid ) ) {
                try {
                    PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder().getDatabase()
                            .getConnection()
                            .prepareStatement(
                                    "INSERT INTO `guild_user` (" +
                                            "player_uuid, " +
                                            "player_name, " +
                                            "guild_name, " +
                                            "guild_tag, " +
                                            "guild_rank, " +
                                            "guild_payed_money, " +
                                            "guild_take_money) " +
                                            "VALUES (?, ?, ?, ?, ?, ?, ?)"
                            );

                    preparedStatement.setString( 1, uuid.toString() );
                    preparedStatement.setString( 2, playerName );
                    preparedStatement.setString( 3, guildName );
                    preparedStatement.setString( 4, guildTag );
                    preparedStatement.setString( 5, guildRank );
                    preparedStatement.setInt( 6, 0 );
                    preparedStatement.setInt( 7, 0 );

                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                } catch ( SQLException ex ) {
                    ex.printStackTrace();
                }
            }
        } );
    }

    public void deleteKey( UUID uuid ) {
        this.deleteKey( "guild_user", "player_uuid", uuid.toString() );
    }

    public void updateKey( String setterKey, String setKey, UUID uuid ) {
        this.updateKey( "guild_user", setterKey, setKey, "player_uuid", uuid.toString() );
    }

    public void updateKey( String setterKey, int setKey, UUID uuid ) {
        this.updateKey( "guild_user", setterKey, setKey, "player_uuid", uuid.toString() );
    }

    public String getKey( UUID uuid, String key ) {
        return this.getKey( "guild_user", "player_uuid", uuid.toString(), key );
    }

    public int getKeyByInteger( UUID uuid, String key ) {
        return this.getKeyByInteger( "guild_user", "player_uuid", uuid.toString(), key );
    }

    public List<String> getList( String whereKey, String setWhereKey, String secondWhereKey, String setSecondWhereKey, String getKey ) {
        return this.getList( "guild_user", whereKey, setWhereKey, secondWhereKey, setSecondWhereKey, getKey );
    }

    public List<String> getList( String whereKey, String setWhereKey, String getKey ) {
        return this.getList( "guild_user", whereKey, setWhereKey, getKey );
    }

    public void addKey( String type, int amount, UUID playerUuid ) {
        this.addKey( "guild_user", type, amount, "player_uuid", playerUuid.toString() );
    }

    public void removeKey( String type, int amount, UUID playerUuid ) {
        this.removeKey( "guild_user", type, amount, "player_uuid", playerUuid.toString() );
    }
}