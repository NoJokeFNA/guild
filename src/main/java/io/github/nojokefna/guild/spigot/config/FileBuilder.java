package io.github.nojokefna.guild.spigot.config;

import io.github.nojokefna.guild.spigot.Guild;
import lombok.NonNull;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class FileBuilder {

    private final File file;
    private final FileConfiguration fileConfiguration;

    public FileBuilder( @NonNull String value ) {
        Validate.notNull( value, "The value cannot be null" );

        this.file = new File( Guild.getPlugin().getDataFolder(), value );
        this.fileConfiguration = YamlConfiguration.loadConfiguration( this.file );

        Guild.getPlugin().saveResource( value, false );
    }

    public String getKey( @NonNull String key ) {
        Validate.notNull( key, "The key cannot be null" );

        String configValue = this.fileConfiguration.getString( key );
        String language = Guild.getPlugin().getConfig().getString( "language" );
        String path = language.toLowerCase() + "_" + language.toUpperCase() + ".yml";

        Validate.notNull( configValue, "§cError: §4Key " + key + " §edoes not exists." );

        if ( this.file.getName().equals( path ) ) {
            DecimalFormat format = new DecimalFormat( "###,###,###" );
            String costs = format.format( this.fileConfiguration.getInt( "guild.create_guild.costs" ) );
            String prefix = this.fileConfiguration.getString( "prefix" );

            configValue = configValue.replace( "{PREFIX}", prefix );
            configValue = configValue.replace( "{COSTS}", costs );
        }

        configValue = configValue
                .replace( "{NL}", "\n" )
                .replace( "%nl%", "\n" )
                .replace( "%newLine%", "\n" )
                .replace( "%nw%", "\n" );

        return ChatColor.translateAlternateColorCodes( '&', new String( configValue.getBytes(), StandardCharsets.UTF_8 ) );
    }

    public void setKey( @NonNull String path, @NonNull Object value ) {
        this.fileConfiguration.set( path, value );
        this.saveConfig();
    }

    public int getInt( @NonNull String key ) {
        Validate.notNull( key, "§cError: §4Key " + key + " §edoes not exists." );
        return this.fileConfiguration.getInt( key );
    }

    public boolean getBoolean( @NonNull String key ) {
        Validate.notNull( key, "§cError: §4Key " + key + " §edoes not exists." );
        return this.fileConfiguration.getBoolean( key );
    }

    public ConfigurationSection getConfigurationSection() {
        return this.fileConfiguration;
    }

    public void deleteConfig() {
        if ( this.file.delete() )
            System.out.println( "Successfully deleted config" );
        else
            System.out.println( "Error while deleting config" );
    }

    public void loadConfig() {
        try {
            this.fileConfiguration.load( this.file );
        } catch ( IOException | InvalidConfigurationException ex ) {
            ex.printStackTrace();
        }
    }

    private void saveConfig() {
        try {
            this.fileConfiguration.save( this.file );
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }
    }
}