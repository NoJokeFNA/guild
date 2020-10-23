package io.github.nojokefna.guild.spigot.database.api;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.database.AbstractMySQL;
import io.github.nojokefna.guild.spigot.database.DatabaseProvider;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class GuildAPI extends AbstractMySQL {

    private final DatabaseProvider databaseProvider;

    public GuildAPI() {
        this.databaseProvider = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider();
    }

    public boolean guildExists( String keyValue, String guildKey ) {
        return this.keyExists( "guild", keyValue, guildKey );
    }

    public void createGuild( String guildName, String guildTag, String guildLeader ) {
        Guild.getPlugin().getExecutorService().submit( () -> {
            if ( !this.guildExists( "guild_name", guildName ) ) {
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
            }
        } );
    }

    public void deleteGuild( String guildName ) {
        super.deleteKey( "guild", "guild_name", guildName );
    }

    public void updateGuild( String setterKey, String setKey, String guildName ) {
        super.updateKey( "guild", "guild_name", guildName, setterKey, setKey );
    }

    public void updateGuild( String setterKey, int setKey, String guildName ) {
        super.updateKey( "guild", "guild_name", guildName, setterKey, setKey );
    }

    public String getGuild( String guildKey, String key, String value ) {
        return super.getKey( "guild", guildKey, key, value );
    }

    public int getGuildByInteger( String guildKey, String key, String value ) {
        return super.getKeyByInteger( "guild", guildKey, key, value );
    }

    public int getRanking( String selectKey, String orderKey ) {
        return super.getRanking( selectKey, "guild", orderKey );
    }

    public void addKey( String guildName, int amount ) {
        super.addKey( "guild", "guild_name", guildName, "guild_money", amount );
    }

    public void removeKey( String guildName, int amount ) {
        super.removeKey( "guild", "guild_name", guildName, "guild_money", amount );
    }
}
