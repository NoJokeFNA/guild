package io.github.nojokefna.guild.spigot.database.api;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.database.AbstractMySQL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class GuildAPI extends AbstractMySQL {

    public boolean guildExists( String keyValue, String guildKey ) {
        return this.keyExists( "guild", keyValue, guildKey );
    }

    public void createGuild( String guildName, String guildTag, String guildLeader ) {
        Guild.getPlugin().getExecutorService().submit( () -> {
            if ( !this.guildExists( "guild_name", guildName ) ) {
                try {
                    PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder()
                            .getDatabase()
                            .prepareStatement( "INSERT INTO `guild` (guild_name, guild_tag, guild_leader, guild_money) VALUES (?, ?, ?, ?)" );

                    preparedStatement.setString( 1, guildName );
                    preparedStatement.setString( 2, guildTag );
                    preparedStatement.setString( 3, guildLeader );
                    preparedStatement.setInt( 4, 0 );

                    Guild.getPlugin().getDatabaseBuilder().getDatabase().queryUpdate( preparedStatement );
                } catch ( SQLException ex ) {
                    ex.printStackTrace();
                }
            }
        } );
    }

    public void deleteGuild( String guildName ) {
        this.deleteKey( "guild", "guild_name", guildName );
    }

    public void updateGuild( String setterKey, String setKey, String guildName ) {
        this.updateKey( "guild", setterKey, setKey, "guild_name", guildName );
    }

    public void updateGuild( String setterKey, int setKey, String guildName ) {
        this.updateKey( "guild", setterKey, setKey, "guild_name", guildName );
    }

    public String getGuild( String guildKey, String key, String value ) {
        return this.getKey( "guild", guildKey, key, value );
    }

    public int getGuildByInteger( String guildKey, String key, String value ) {
        return this.getKeyByInteger( "guild", guildKey, key, value );
    }

    public int getRanking( String selectKey, String orderKey ) {
        return this.getRanking( selectKey, "guild", orderKey );
    }

    public void addKey( String guildName, int amount ) {
        this.addKey( "guild", "guild_money", amount, "guild_name", guildName );
    }

    public void removeKey( String guildName, int amount ) {
        this.removeKey( "guild", "guild_money", amount, "guild_name", guildName );
    }
}
