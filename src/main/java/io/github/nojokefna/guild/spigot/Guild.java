package io.github.nojokefna.guild.spigot;

import io.github.nojokefna.guild.spigot.build.GuildBuilder;
import io.github.nojokefna.guild.spigot.commands.AdminGuildCommand;
import io.github.nojokefna.guild.spigot.commands.GuildCommand;
import io.github.nojokefna.guild.spigot.commands.TestCommand;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import io.github.nojokefna.guild.spigot.controller.GuildController;
import io.github.nojokefna.guild.spigot.controller.GuildRecodeController;
import io.github.nojokefna.guild.spigot.database.DatabaseBuilder;
import io.github.nojokefna.guild.spigot.database.api.GuildAPI;
import io.github.nojokefna.guild.spigot.database.api.GuildCoinsAPI;
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
import org.bukkit.plugin.PluginManager;
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
    private Economy economy;
    private Permission permission;
    private Chat chat;

    private Data data;
    private GuildAPI guildAPI;
    private FileBuilder fileBuilder, chatSettingsBuilder, serverSettingsBuilder;
    private GuildUserAPI guildUserAPI;
    private GuildCoinsAPI guildCoinsAPI;
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
        final long startupTime = System.currentTimeMillis();

        this.getLogger().finest( String.format( "%s§aTry to start §c%s §a...", this.getData().getPrefix(), this.getDescription().getName() ) );
        this.executorService = Executors.newFixedThreadPool( 2 );

        Data.registerRecipe();

        System.out.println(
                "\n" +
                        "  ____       _ _     _ \n" +
                        " / ___|_   _(_) | __| |\n" +
                        "| |  _| | | | | |/ _` |\n" +
                        "| |_| | |_| | | | (_| |\n" +
                        " \\____|\\__,_|_|_|\\__,_|\n"
        );

        try {
            this.getDatabaseBuilder().createTables();
            System.out.println( "MySQL connected in " + ( System.currentTimeMillis() - startupTime ) + "ms" );

            this.initializeVault();
            this.register();
            System.out.println( "Commands & Listener were loaded in " + ( System.currentTimeMillis() - startupTime ) + "ms" );

            this.getLogger().finest( String.format( "%s§a%s §ahas started.", this.getData().getPrefix(), this.getDescription().getName() ) );
        } catch ( Exception ex ) {
            this.getLogger().warning( String.format( "%s§c%s §ahas ended.", this.getData().getPrefix(), this.getDescription().getName() ) );
            this.getPluginLoader().disablePlugin( this );
            ex.printStackTrace();
        }

        System.out.println( "The plugin were loaded in " + ( System.currentTimeMillis() - startupTime ) + "ms" );
    }

    @Override
    public void onDisable() {
        this.getLogger().severe( String.format( "%s§a%s §chas ended.", this.getData().getPrefix(), this.getDescription().getName() ) );
    }

    private void register() {
        //commands
        this.getCommand( "guild" ).setExecutor( new GuildCommand() );
        this.getCommand( "adminguild" ).setExecutor( new AdminGuildCommand() );
        this.getCommand( "test" ).setExecutor( new TestCommand() );

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
        this.databaseBuilder = new DatabaseBuilder();
        this.guildAPI = new GuildAPI();
        this.guildInvitesAPI = new GuildInvitesAPI();
        this.guildUserAPI = new GuildUserAPI();
        this.guildCoinsAPI = new GuildCoinsAPI();
        this.guildBuilder = new GuildBuilder();
        this.guildController = new GuildController();
        this.guildRecodeController = new GuildRecodeController();
    }

    private void loadConfig() {
        if ( !this.getDataFolder().exists() )
            this.getDataFolder().mkdirs();

        this.getConfig().options().copyDefaults( true );
        this.saveDefaultConfig();

        String language = this.getConfig().getString( "language" );
        String path = "languages/" + language.toLowerCase() + "_" + language.toUpperCase() + ".yml";

        this.fileBuilder = new FileBuilder( path );
        this.chatSettingsBuilder = new FileBuilder( "chat_settings.yml" );
        this.serverSettingsBuilder = new FileBuilder( "server_settings.yml" );
    }

    private void initializeVault() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        if ( this.serverSettingsBuilder.getBoolean( "economy.use_economy_support" ) && !this.setupEconomy() ) {
            this.getLogger().severe( String.format( "[%s] - Disabled due to no Vault dependency found!", this.getDescription().getName() ) );
            this.getServer().getPluginManager().disablePlugin( this );
            return;
        }

        this.getLogger().finest( String.format( "Successfully started %s", "Vault" ) );

        switch ( this.getChatSettingsBuilder().getKey( "chat.permission_plugin" ) ) {
            case "PermissionsEx":
                if ( pluginManager.getPlugin( "PermissionsEx" ) == null ) {
                    this.getLogger().severe( String.format( "%s can't be activated, because the plugin %s could be found", "PermissionsEx", "PermissionsEx" ) );
                    return;
                }

                this.getLogger().finest( String.format( "Successfully enabled %s and %s", "Vault-Chat", "PermissionsEx" ) );
                this.setupPerms( "PermissionsEx" );
                this.setupChat();
                break;

            case "LuckPerms":
                if ( pluginManager.getPlugin( "LuckPerms" ) == null ) {
                    this.getLogger().severe( String.format( "%s can't be activated, because the plugin %s could be found", "LuckPerms", "LuckPerms" ) );
                    return;
                }

                this.getLogger().finest( String.format( "Successfully enabled %s and %s", "Vault-Chat", "LuckPerms" ) );
                this.setupPerms( "LuckPerms" );
                this.setupChat();
                break;

            case "CloudNet":
                this.getLogger().finest( String.format( "Successfully enabled %s and %s", "CPerms-Chat", "CloudNet" ) );
                break;

            default:
                throw new IllegalStateException( "Unexpected value: " + this.getChatSettingsBuilder().getKey( "chat.permission_plugin" ) );
        }
    }

    private boolean setupEconomy() {
        if ( this.getServer().getPluginManager().getPlugin( "Vault" ) == null )
            return false;

        final RegisteredServiceProvider<Economy> serviceProvider = this.getServer().getServicesManager().getRegistration( Economy.class );
        if ( serviceProvider == null )
            return false;

        this.economy = serviceProvider.getProvider();
        return this.economy != null;
    }

    private void setupPerms( String plugin ) {
        if ( this.getServer().getPluginManager().getPlugin( plugin ) == null )
            return;

        final RegisteredServiceProvider<Permission> serviceProvider = this.getServer().getServicesManager().getRegistration( Permission.class );
        if ( serviceProvider == null )
            return;

        this.permission = serviceProvider.getProvider();
    }

    private void setupChat() {
        final RegisteredServiceProvider<Chat> serviceProvider = this.getServer().getServicesManager().getRegistration( Chat.class );
        this.chat = serviceProvider.getProvider();
    }
}