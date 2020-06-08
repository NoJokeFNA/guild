package io.github.nojokefna.guild.spigot.config;

import io.github.nojokefna.guild.spigot.Guild;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
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

    public FileBuilder( @Nonnull String value ) {
        this.file = new File( Guild.getPlugin().getDataFolder(), value );
        this.fileConfiguration = YamlConfiguration.loadConfiguration( this.file );
    }

    public String getKey( @Nonnull String key ) {
        Validate.notNull( key, "The key cannot be null" );

        var configValue = this.fileConfiguration.getString( key );
        var language = Guild.getPlugin().getConfig().getString( "language" );
        var path = language.toLowerCase() + "_" + language.toUpperCase() + ".yml";

        if ( configValue == null ) {
            return "§cError: §4Key " + key + " §edoes not exists.";
        }

        if ( this.file.getName().equals( path ) ) {
            DecimalFormat format = new DecimalFormat( "###,###,###" );
            var costs = format.format( this.fileConfiguration.getInt( "guild.create_guild.costs" ) );
            var prefix = this.fileConfiguration.getString( "prefix" );

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

    public int getInt( @Nonnull String key ) {
        Validate.notNull( key, "The key cannot be null" );

        if ( key == null ) {
            return Integer.parseInt( "§cError: §4Key " + key + " §edoes not exists." );
        }

        return this.fileConfiguration.getInt( key );
    }

    public boolean getBoolean( @Nonnull String key ) {
        Validate.notNull( key, "The key cannot be null" );

        if ( key == null ) {
            return Boolean.parseBoolean( "§cError: §4Key " + key + " §edoes not exists." );
        }

        return this.fileConfiguration.getBoolean( key );
    }

    public ConfigurationSection getConfigurationSection() {
        return this.fileConfiguration;
    }

    public void setKey( @Nonnull String path, @Nonnull Object value ) {
        this.fileConfiguration.set( path, value );
        this.saveConfig();
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