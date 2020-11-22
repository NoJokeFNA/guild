package io.github.nojokefna.guild.spigot.database.api;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.database.AbstractMySQL;
import io.github.nojokefna.guild.spigot.database.DatabaseProvider;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class GuildAPI extends AbstractMySQL {

    private final DatabaseProvider databaseProvider;

    public GuildAPI() {
        this.databaseProvider = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider();
        super.setTable( "guild" );
    }

    /**
     * Check if the guild is present or not
     *
     * @param whereColumn    The column you want to check
     * @param setWhereColumn The column you want to check
     *
     * @return {@code true} if the guild is present, otherwise {@code false}
     */
    public boolean guildExists( String whereColumn, String setWhereColumn ) {
        return super.keyExists( whereColumn, setWhereColumn );
    }

    /**
     * Create a new guild
     *
     * @param guildName   The name of the guild
     * @param guildTag    The tag of the guild
     * @param guildLeader The guild leader
     */
    public void createGuild( String guildName, String guildTag, String guildLeader ) {
        if ( this.guildExists( "guild_name", guildName ) )
            return;

        Guild.getPlugin().getExecutorService().submit( () -> {
            try {
                try ( PreparedStatement preparedStatement = this.databaseProvider.prepareStatement( "INSERT INTO `guild` (guild_name, guild_tag, guild_leader, guild_money) VALUES (?, ?, ?, ?)" ) ) {

                    preparedStatement.setString( 1, guildName );
                    preparedStatement.setString( 2, guildTag );
                    preparedStatement.setString( 3, guildLeader );
                    preparedStatement.setInt( 4, 0 );

                    preparedStatement.executeUpdate();
                }
            } catch ( SQLException ex ) {
                ex.printStackTrace();
            }
        } );
    }

    /**
     * Delete the guild by the {@code guildName}
     *
     * @param guildName Name of the guild
     */
    public void deleteGuild( String guildName ) {
        super.deleteKey( "guild_name", guildName );
    }

    /**
     * Update the guild information by the guild name
     *
     * @param guildName    THe name of the guild
     * @param setColumn    The column you want to update
     * @param setterColumn The value you want to set for the {@code setterColumn}
     */
    public void updateGuild( String guildName, String setColumn, String setterColumn ) {
        super.updateKey( "guild_name", guildName, setColumn, setterColumn );
    }

    /**
     * Update the guild information by the guild name
     *
     * @param guildName    THe name of the guild
     * @param setColumn    The column you want to update
     * @param setterColumn The value you want to set for the {@code setterColumn}
     */
    public void updateGuild( String guildName, String setColumn, int setterColumn ) {
        super.updateKey( "guild_name", guildName, setColumn, setterColumn );
    }

    public String getGuildByStringSync( String guildKey, String key, String value ) {
        return super.getKey( guildKey, key, value );
    }

    public int getGuildByIntegerSync( String guildKey, String key, String value ) {
        return super.getKeyByInteger( guildKey, key, value );
    }

    public CompletableFuture<String> getGuildByStringAsync( String guildKey, String key, String value ) {
        return super.getStringAsync( guildKey, key, value );
    }

    public CompletableFuture<Integer> getGuildByIntegerAsync( String guildKey, String key, String value ) {
        return super.getIntegerAsync( guildKey, key, value );
    }

    public int getRanking( String selectKey, String orderKey ) {
        return super.getRanking( selectKey, orderKey );
    }

    public void addKey( String guildName, int amount ) {
        super.addKey( "guild_name", guildName, "guild_money", amount );
    }

    public void removeKey( String guildName, int amount ) {
        super.removeKey( "guild_name", guildName, "guild_money", amount );
    }
}
