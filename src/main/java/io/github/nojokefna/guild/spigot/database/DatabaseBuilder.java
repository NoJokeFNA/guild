package io.github.nojokefna.guild.spigot.database;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.utils.ANSIColors;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
@Getter
public class DatabaseBuilder {

    private final ConfigurationSection section;
    private Database database;

    public DatabaseBuilder() {
        this.section = Guild.getPlugin().getConfig();
    }

    public void createTables() {
        if ( this.isMySqlConfigured() ) {
            this.database = new Database(
                    this.section.getString( "mysql.username" ),
                    this.section.getString( "mysql.password" ),
                    this.section.getString( "mysql.hostname" ),
                    this.section.getInt( "mysql.port" ),
                    this.section.getString( "mysql.database" ),
                    Guild.getPlugin()
            );

            this.database.update(
                    "CREATE TABLE IF NOT EXISTS `guild` (guild_name VARCHAR(16) UNIQUE, guild_tag VARCHAR(32), guild_leader VARCHAR(64), guild_money INT)"
            );

            this.database.update(
                    "CREATE TABLE IF NOT EXISTS `guild_user` (uuid VARCHAR(64) UNIQUE, player_name VARCHAR(32), guild_name VARCHAR(16), " +
                            "guild_tag VARCHAR(32), guild_rank VARCHAR(16), guild_payed_money INT, guild_take_money INT)"
            );

            this.database.update(
                    "CREATE TABLE IF NOT EXISTS `guild_quests` (guild_name VARCHAR(16) UNIQUE, guild_open_quest VARCHAR(64), guild_quests VARCHAR(64))"
            );

            this.database.update(
                    "CREATE TABLE IF NOT EXISTS `guild_invites` (player_uuid VARCHAR(64), player_name VARCHAR(32), invited_name VARCHAR(32)," +
                            " guild_name VARCHAR(16), guild_tag VARCHAR(32))"
            );

            this.database.connect();
        }
    }

    public boolean isMySqlConfigured() {
        if ( this.section.getString( "mysql.username" ).equalsIgnoreCase( "username" )
                && this.section.getString( "mysql.password" ).equalsIgnoreCase( "password" )
                && this.section.getString( "mysql.hostname" ).equalsIgnoreCase( "hostname" )
                && this.section.getString( "mysql.database" ).equalsIgnoreCase( "database" ) ) {

            Guild.getPlugin().getLogger().warning( Guild.getPlugin().getData().getPrefix()
                    + ANSIColors.ANSI_RED
                    + "§cThe database is not configured! Please contact an administrator!"
                    + ANSIColors.ANSI_RESET );
            return false;
        }

        return true;
    }
}
