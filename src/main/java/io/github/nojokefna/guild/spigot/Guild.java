package io.github.nojokefna.guild.spigot;

import io.github.nojokefna.guild.spigot.build.GuildBuilder;
import io.github.nojokefna.guild.spigot.commands.AdminGuildCommand;
import io.github.nojokefna.guild.spigot.commands.GuildCommand;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import io.github.nojokefna.guild.spigot.controller.GuildController;
import io.github.nojokefna.guild.spigot.controller.GuildRecodeController;
import io.github.nojokefna.guild.spigot.database.DatabaseBuilder;
import io.github.nojokefna.guild.spigot.database.api.GuildAPI;
import io.github.nojokefna.guild.spigot.database.api.GuildInvitesAPI;
import io.github.nojokefna.guild.spigot.database.api.GuildUserAPI;
import io.github.nojokefna.guild.spigot.listener.*;
import io.github.nojokefna.guild.spigot.utils.Data;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
@Getter
public class Guild extends JavaPlugin {

    @Getter
    private static Guild plugin;

    private ExecutorService executorService;
    private Economy econ;
    private Permission perms;
    private Chat chat;

    private Data data;
    private GuildAPI guildAPI;
    private FileBuilder fileManager, settingsManager, serverSettingsManager;
    private GuildUserAPI guildUserAPI;
    private GuildBuilder guildBuilder;
    private GuildController guildController;
    private GuildRecodeController guildRecodeController;
    private GuildInvitesAPI guildInvitesAPI;
    private DatabaseBuilder databaseBuilder;

    @Override
    public void onLoad() {
        plugin = this;
        this.loadConfig();
        this.initialize();
    }

    @Override
    public void onEnable() {
        this.getLogger().finest( String.format( "%s§aTry to start §c%s §a...", this.getData().getPrefix(), this.getDescription().getName() ) );
        this.executorService = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );

        try {
            this.getDatabaseBuilder().createTables();

            this.initializeVault();
            this.register();

            this.getLogger().finest( String.format( "%s§a%s §ahas started.", this.getData().getPrefix(), this.getDescription().getName() ) );
        } catch ( Exception ex ) {
            this.getLogger().warning( String.format( "%s§c%s §ahas ended.", this.getData().getPrefix(), this.getDescription().getName() ) );
            this.getPluginLoader().disablePlugin( this );
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().severe( String.format( "%s§a%s §chas ended.", this.getData().getPrefix(), this.getDescription().getName() ) );
    }

    private void register() {
        //commands
        this.getCommand( "guild" ).setExecutor( new GuildCommand() );
        this.getCommand( "adminguild" ).setExecutor( new AdminGuildCommand() );

        //listener
        Listener[] listeners = new Listener[] {
                new AsyncPlayerChatListener(), new AsyncPlayerPreLoginListener(), new PlayerCommandPreprocessListener(),
                new PlayerJoinListener(), new PlayerKickListener(), new PlayerQuitListener(),
                new ServerListPingListener()
        };

        Arrays.stream( listeners ).forEach( listener -> Bukkit.getPluginManager().registerEvents( listener, this ) );
    }

    private void initialize() {
        this.data = new Data();
        this.guildAPI = new GuildAPI();
        this.guildInvitesAPI = new GuildInvitesAPI();
        this.guildUserAPI = new GuildUserAPI();
        this.guildBuilder = new GuildBuilder();
        this.guildController = new GuildController();
        this.guildRecodeController = new GuildRecodeController();
        this.databaseBuilder = new DatabaseBuilder();
    }

    private void loadConfig() {
        if ( ! this.getDataFolder().exists() )
            this.getDataFolder().mkdirs();

        this.getConfig().options().copyDefaults( true );
        this.saveDefaultConfig();

        String language = this.getConfig().getString( "language" );
        String path = "languages/" + language.toLowerCase() + "_" + language.toUpperCase() + ".yml";

        this.save( path );
        this.save( "chat_settings.yml" );
        this.save( "server_settings.yml" );

        this.fileManager = new FileBuilder( path );
        this.settingsManager = new FileBuilder( "chat_settings.yml" );
        this.serverSettingsManager = new FileBuilder( "server_settings.yml" );
    }

    private void initializeVault() {
        if ( ! this.setupEconomy() ) {
            this.getLogger().severe( String.format( "[%s] - Disabled due to no Vault dependency found!", this.getDescription().getName() ) );
            this.getServer().getPluginManager().disablePlugin( this );
            return;
        }

        this.getLogger().finest( String.format( "Successfully started %s", "Vault" ) );

        switch ( this.getSettingsManager().getKey( "chat.permission_plugin" ) ) {
            case "PermissionsEx":
                this.getLogger().finest( String.format( "Successfully enabled %s and %s", "Vault-Chat", "PermissionsEx" ) );
                this.setupPerms( "PermissionsEx" );
                this.setupChat();
                break;

            case "LuckPerms":
                this.getLogger().finest( String.format( "Successfully enabled %s and %s", "Vault-Chat", "LuckPerms" ) );
                this.setupPerms( "LuckPerms" );
                this.setupChat();
                break;

            default:
                throw new IllegalStateException( "Unexpected value: " + this.getSettingsManager().getKey( "chat.permission_plugin" ) );
        }
    }

    private boolean setupEconomy() {
        if ( this.getServer().getPluginManager().getPlugin( "Vault" ) == null )
            return false;

        RegisteredServiceProvider<Economy> serviceProvider = this.getServer().getServicesManager().getRegistration( Economy.class );
        if ( serviceProvider == null )
            return false;

        this.econ = serviceProvider.getProvider();

        return this.econ != null;
    }

    private void setupPerms( String plugin ) {
        if ( this.getServer().getPluginManager().getPlugin( plugin ) == null )
            return;

        RegisteredServiceProvider<Permission> serviceProvider = this.getServer().getServicesManager().getRegistration( Permission.class );
        if ( serviceProvider == null )
            return;

        this.perms = serviceProvider.getProvider();
    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> serviceProvider = this.getServer().getServicesManager().getRegistration( Chat.class );
        this.chat = serviceProvider.getProvider();
    }

    private void save( String path ) {
        this.saveResource( path, false );
        this.saveConfig();
    }
}