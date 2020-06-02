package io.github.nojokefna.guild.spigot.config;

import io.github.nojokefna.guild.spigot.Guild;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
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
@Getter
public class FileBuilder {

    private final File file;
    private final FileConfiguration fileConfiguration;

    public FileBuilder( String value ) {
        this.file = new File( Guild.getPlugin().getDataFolder(), value );
        this.fileConfiguration = YamlConfiguration.loadConfiguration( this.file );
    }

    public String getKey( String key ) {
        String configValue = this.fileConfiguration.getString( key );
        String language = Guild.getPlugin().getConfig().getString( "language" );
        String path = language + "_" + language.toUpperCase() + ".yml";

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

    public void setKey( String path, Object value ) {
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