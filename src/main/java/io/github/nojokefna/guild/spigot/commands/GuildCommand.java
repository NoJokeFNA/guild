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

    @Override
    public boolean onCommand( CommandSender commandSender, Command command, String label, String[] args ) {
        if ( commandSender instanceof Player ) {
            Player player = ( Player ) commandSender;

            GuildBuilder guildBuilder = Guild.getPlugin().getGuildBuilder();
            GuildController guildController = Guild.getPlugin().getGuildController();
            GuildRecodeController guildRecodeController = Guild.getPlugin().getGuildRecodeController();

            if ( ! Guild.getPlugin().getDatabaseBuilder().isMySqlConfigured() ) {
                guildBuilder.sendMessage( player, "§cThe database is not configured! Please contact an administrator!" );
                return true;
            }

            switch ( args.length ) {
                case 1:
                    switch ( args[0].toLowerCase() ) {
                        case "info":
                            guildController.sendGuildList( player );
                            break;

                        case "invites":
                            guildRecodeController.getInvites( player );
                            break;

                        case "members":
                            guildController.sendMembers( player );
                            break;

                        case "master":
                            guildController.sendGuildMaster( player );
                            break;

                        case "officers":
                            guildController.sendOfficers( player );
                            break;

                        case "leave":
                            guildController.leaveGuild( player );
                            break;

                        case "delete":
                            guildController.deleteGuild( player );
                            break;

                        case "chat":
                            guildController.toggleChat( player );
                            break;

                        default:
                            guildBuilder.sendHelpMessage( player, 1 );
                            break;
                    }
                    break;

                case 2:
                    Bukkit.getServer().getScheduler().runTaskAsynchronously( Guild.getPlugin(), () -> {
                        OfflinePlayer target = Bukkit.getOfflinePlayer( args[1] );
                        switch ( args[0].toLowerCase() ) {
                            case "help":
                                int value = Integer.parseInt( args[1] );
                                guildBuilder.sendHelpMessage( player, value );
                                break;

                            case "info":
                                guildController.sendGuildInfo( player, args[1] );
                                break;

                            case "invite":
                                Player targetOnline = Bukkit.getPlayerExact( args[1] );
                                guildController.sendInvite( player, targetOnline );
                                break;

                            case "revoke":
                                guildController.revokeInvite( player, target );
                                break;

                            case "accept":
                                guildController.acceptInvite( player );
                                break;

                            case "deny":
                                guildController.denyInvite( player );
                                break;

                            case "setmaster":
                                guildController.setGuildMaster( player, target );
                                break;

                            case "kick":
                                guildController.kickPlayer( player, target );
                                break;

                            case "promote":
                                guildController.promotePlayer( player, target );
                                break;

                            case "demote":
                                guildController.demotePlayer( player, target );
                                break;

                            case "bank":
                                switch ( args[1].toLowerCase() ) {
                                    case "credit":
                                    case "guthaben":
                                        guildController.sendGuildBank( player );
                                        break;

                                    default:
                                        guildBuilder.sendHelpMessage( player, 1 );
                                        break;
                                }
                                break;

                            case "quests":
                            /*if ( args[1].equalsIgnoreCase( "info" ) ) {
                                manager.sendMessage( player, "Zeigt die Absolvierten Gilden Aufgaben an." );
                            }*/
                                guildBuilder.sendMessage( player, "§cThis feature is currently under development." );
                                break;

                            default:
                                guildBuilder.sendHelpMessage( player, 1 );
                                break;
                        }
                    } );
                    break;

                case 3:
                    switch ( args[0].toLowerCase() ) {
                        case "create":
                            guildController.createGuild( player, args[1], args[2], player.getName() );
                            break;

                        case "bank":
                            int amount = Integer.parseInt( args[2] );
                            switch ( args[1].toLowerCase() ) {
                                case "deposit":
                                case "einzahlen":
                                    guildController.addGuildBank( player, amount );
                                    break;

                                case "withdraw":
                                case "auszahlen":
                                    guildController.removeGuildBank( player, amount );
                                    break;

                                default:
                                    guildBuilder.sendHelpMessage( player, 1 );
                                    break;
                            }
                            break;

                        default:
                            guildBuilder.sendHelpMessage( player, 1 );
                            break;
                    }
                    break;

                default:
                    guildBuilder.sendHelpMessage( player, 1 );
                    break;
            }

        } else {
            commandSender.sendMessage( label );
        }
        return false;
    }
}
