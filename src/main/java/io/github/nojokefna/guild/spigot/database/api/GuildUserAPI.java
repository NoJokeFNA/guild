package io.github.nojokefna.guild.spigot.database.api;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.database.AbstractMySQL;
import io.github.nojokefna.guild.spigot.database.DatabaseProvider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class GuildUserAPI extends AbstractMySQL {

    private DatabaseProvider databaseProvider;

    public GuildUserAPI() {
        this.databaseProvider = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider();
        super.setTable( "guild_user" );
    }

    public boolean playerExists( UUID playerUuid ) {
        return super.keyExists( "player_uuid", playerUuid.toString() );
    }

    public boolean guildExists( UUID playerUuid, String key ) {
        if ( databaseProvider == null )
            this.databaseProvider = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider();

        if ( key == null )
            return false;

        boolean value = false;

        try {
            try ( PreparedStatement preparedStatement = this.databaseProvider.prepareStatement( "SELECT * FROM `guild_user` WHERE player_uuid = ? AND guild_rank = ?" ) ) {

                preparedStatement.setString( 1, playerUuid.toString() );
                preparedStatement.setString( 2, key );

                final ResultSet resultSet = preparedStatement.executeQuery();
                if ( resultSet.next() )
                    value = true;

                preparedStatement.close();
                resultSet.close();
            }
        } catch ( SQLException ex ) {
            ex.printStackTrace();
        }
        return value;
    }

    public void createPlayer( UUID playerUuid, String playerName, String guildName, String guildTag, String guildRank ) {
        Guild.getPlugin().getExecutorService().submit( () -> {
            if ( this.playerExists( playerUuid ) ) {
                try {
                    try (
                            PreparedStatement preparedStatement = this.databaseProvider.prepareStatement( "INSERT INTO `guild_user` (" +
                                                                                                                  "player_uuid, " +
                                                                                                                  "player_name, " +
                                                                                                                  "guild_name, " +
                                                                                                                  "guild_tag, " +
                                                                                                                  "guild_rank, " +
                                                                                                                  "guild_payed_money, " +
                                                                                                                  "guild_take_money) " +
                                                                                                                  "VALUES (?, ?, ?, ?, ?, ?, ?)" )
                    ) {

                        preparedStatement.setString( 1, playerUuid.toString() );
                        preparedStatement.setString( 2, playerName );
                        preparedStatement.setString( 3, guildName );
                        preparedStatement.setString( 4, guildTag );
                        preparedStatement.setString( 5, guildRank );
                        preparedStatement.setInt( 6, 0 );
                        preparedStatement.setInt( 7, 0 );

                        preparedStatement.executeUpdate();
                    }
                } catch ( SQLException ex ) {
                    ex.printStackTrace();
                }
            }
        } );
    }

    public void deleteKey( UUID playerUuid ) {
        this.deleteKey( "player_uuid", playerUuid.toString() );
    }

    public void updateKey( String setColumn, String setterColumn, UUID playerUuid ) {
        super.updateKey( "player_uuid", playerUuid.toString(), setColumn, setterColumn );
    }

    public void updateKey( String setterKey, int setKey, UUID playerUuid ) {
        super.updateKey( "player_uuid", playerUuid.toString(), setterKey, setKey );
    }

    public String getKey( UUID playerUuid, String key ) {
        return super.getKey( "player_uuid", playerUuid.toString(), key );
    }

    public int getKeyByInteger( UUID playerUuid, String key ) {
        return super.getKeyByInteger( "player_uuid", playerUuid.toString(), key );
    }

    public List<String> getList( String whereKey, String setWhereKey, String secondWhereKey, String setSecondWhereKey, String getKey ) {
        return super.getList( whereKey, setWhereKey, secondWhereKey, setSecondWhereKey, getKey );
    }

    public List<String> getList( String whereKey, String setWhereKey, String getKey ) {
        return super.getList( whereKey, setWhereKey, getKey );
    }

    public void addKey( String type, int amount, UUID playerUuid ) {
        super.addKey( "player_uuid", playerUuid.toString(), type, amount );
    }

    public void removeKey( String type, int amount, UUID playerUuid ) {
        super.removeKey( "player_uuid", playerUuid.toString(), type, amount );
    }
}