package io.github.nojokefna.guild.spigot.commands;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.build.GuildBuilder;
import io.github.nojokefna.guild.spigot.controller.GuildController;
import io.github.nojokefna.guild.spigot.controller.GuildRecodeController;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class GuildCommand implements CommandExecutor {

    private final Guild plugin;

    private final GuildBuilder guildBuilder;
    private final GuildController guildController;
    private final GuildRecodeController guildRecodeController;

    public GuildCommand() {
        this.plugin = Guild.getPlugin();

        this.guildBuilder = this.plugin.getGuildBuilder();
        this.guildController = this.plugin.getGuildController();
        this.guildRecodeController = this.plugin.getGuildRecodeController();
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
        if ( !( sender instanceof Player ) ) {
            sender.sendMessage( label );
            return true;
        }

        final Player player = ( Player ) sender;

        if ( !this.plugin.getDatabaseBuilder().isMySqlConfigured() ) {
            this.guildBuilder.sendMessage( player, "§cThe database is not configured! Please contact an administrator!" );
            return true;
        }

        switch ( args.length ) {
            case 1:
                switch ( args[0].toLowerCase() ) {
                    case "info":
                        this.guildRecodeController.sendGuildInfo( player );
                        break;

                    case "invites":
                        this.guildRecodeController.getInvites( player );
                        break;

                    case "master":
                        this.guildController.sendGuildMaster( player );
                        break;

                    case "officers":
                        this.guildRecodeController.sendOfficers( player );
                        break;

                    case "members":
                        this.guildRecodeController.sendMembers( player );
                        break;

                    case "leave":
                        this.guildController.leaveGuild( player );
                        break;

                    case "delete":
                        this.guildController.deleteGuild( player );
                        break;

                    case "chat":
                        this.guildController.toggleChat( player );
                        break;

                    default:
                        this.guildBuilder.sendHelpMessage( player, 1 );
                        break;
                }
                break;

            case 2:
                Bukkit.getServer().getScheduler().runTaskAsynchronously( this.plugin, () -> {
                    final OfflinePlayer targetOfflinePlayer = Bukkit.getOfflinePlayer( args[1] );

                    switch ( args[0].toLowerCase() ) {
                        case "help":
                            int value = Integer.parseInt( args[1] );
                            this.guildBuilder.sendHelpMessage( player, value );
                            break;

                        case "info":
                            this.guildController.sendGuildInfo( player, args[1] );
                            break;

                        case "invite":
                            Player targetOnline = Bukkit.getPlayerExact( args[1] );
                            this.guildController.sendInvite( player, targetOnline );
                            break;

                        case "revoke":
                            this.guildController.revokeInvite( player, targetOfflinePlayer );
                            break;

                        case "accept":
                            this.guildController.acceptInvite( player );
                            break;

                        case "deny":
                            this.guildController.denyInvite( player );
                            break;

                        case "setmaster":
                            this.guildController.setGuildMaster( player, targetOfflinePlayer );
                            break;

                        case "kick":
                            this.guildController.kickPlayer( player, targetOfflinePlayer );
                            break;

                        case "promote":
                            this.guildController.promotePlayer( player, targetOfflinePlayer );
                            break;

                        case "demote":
                            this.guildController.demotePlayer( player, targetOfflinePlayer );
                            break;

                        case "bank":
                            switch ( args[1].toLowerCase() ) {
                                case "credit":
                                case "guthaben":
                                    this.guildController.sendGuildBank( player );
                                    break;

                                default:
                                    this.guildBuilder.sendHelpMessage( player, 1 );
                                    break;
                            }
                            break;

                        case "quests":
                            /*if ( args[1].equalsIgnoreCase( "info" ) ) {
                                this.guildBuilder.sendMessage( player, "Zeigt die Absolvierten Gilden Aufgaben an." );
                            }*/
                            this.guildBuilder.sendMessage( player, "§cThis feature is currently under development." );
                            break;

                        default:
                            this.guildBuilder.sendHelpMessage( player, 1 );
                            break;
                    }
                } );
                break;

            case 3:
                switch ( args[0].toLowerCase() ) {
                    case "create":
                        this.guildController.createGuild( player, args[1], args[2], player.getName() );
                        break;

                    case "bank":
                        final int amount = Integer.parseInt( args[2] );

                        switch ( args[1].toLowerCase() ) {
                            case "deposit":
                            case "einzahlen":
                                this.guildController.addGuildBank( player, amount );
                                break;

                            case "withdraw":
                            case "auszahlen":
                                this.guildController.removeGuildBank( player, amount );
                                break;

                            default:
                                this.guildBuilder.sendHelpMessage( player, 1 );
                                break;
                        }
                        break;

                    default:
                        this.guildBuilder.sendHelpMessage( player, 1 );
                        break;
                }
                break;

            default:
                this.guildBuilder.sendHelpMessage( player, 1 );
                break;
        }

        return false;
    }
}
