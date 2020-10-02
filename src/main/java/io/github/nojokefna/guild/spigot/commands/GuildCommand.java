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
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class GuildCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
        if ( sender instanceof Player ) {
            final Player player = ( Player ) sender;

            final GuildBuilder guildBuilder = Guild.getPlugin().getGuildBuilder();
            final GuildController guildController = Guild.getPlugin().getGuildController();
            final GuildRecodeController guildRecodeController = Guild.getPlugin().getGuildRecodeController();

            if ( !Guild.getPlugin().getDatabaseBuilder().isMySqlConfigured() ) {
                guildBuilder.sendMessage( player, "§cThe database is not configured! Please contact an administrator!" );
                return true;
            }

            switch ( args.length ) {
                case 1:
                    switch ( args[0].toLowerCase() ) {
                        case "info":
                            guildRecodeController.sendGuildInfo( player );
                            break;

                        case "invites":
                            guildRecodeController.getInvites( player );
                            break;

                        case "master":
                            guildController.sendGuildMaster( player );
                            break;

                        case "officers":
                            guildRecodeController.sendOfficers( player );
                            break;

                        case "members":
                            guildRecodeController.sendMembers( player );
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
                        final OfflinePlayer targetOfflinePlayer = Bukkit.getOfflinePlayer( args[1] );

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
                                guildController.revokeInvite( player, targetOfflinePlayer );
                                break;

                            case "accept":
                                guildController.acceptInvite( player );
                                break;

                            case "deny":
                                guildController.denyInvite( player );
                                break;

                            case "setmaster":
                                guildController.setGuildMaster( player, targetOfflinePlayer );
                                break;

                            case "kick":
                                guildController.kickPlayer( player, targetOfflinePlayer );
                                break;

                            case "promote":
                                guildController.promotePlayer( player, targetOfflinePlayer );
                                break;

                            case "demote":
                                guildController.demotePlayer( player, targetOfflinePlayer );
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
            sender.sendMessage( label );
        }
        return false;
    }

    @Override
    public List<String> onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
        final List<String> commandList = new ArrayList<>(), completions = new ArrayList<>();

        switch ( args.length ) {
            case 1:
                commandList.addAll( Arrays.asList(
                        "help",
                        "leave",
                        "delete",
                        "info",
                        "members",
                        "info",
                        "setMaster",
                        "kick",
                        "chat",
                        "invites",
                        "bank",
                        "master",
                        "accept",
                        "deny",
                        "officers",
                        "promote",
                        "demote",
                        "quests"
                ) );

                StringUtil.copyPartialMatches( args[0], commandList, completions );
                break;

            case 2:
                if ( args[0].equalsIgnoreCase( "quests" ) )
                    commandList.add( "info" );

                StringUtil.copyPartialMatches( args[1], commandList, completions );
                break;

            default:
                return null;
        }

        Collections.sort( completions );
        return completions;
    }
}
