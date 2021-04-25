package io.github.nojokefna.guild.spigot.commands;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.build.GuildBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class AdminGuildCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender commandSender, Command command, String label, String[] args ) {
        if ( commandSender instanceof Player ) {
            final Player player = ( Player ) commandSender;

            final GuildBuilder guildBuilder = Guild.getPlugin().getGuildBuilder();

            switch ( args.length ) {
                case 1:
                    switch ( args[0].toLowerCase() ) {
                        case "reload":
                            Guild.getPlugin().getFileBuilder().loadConfig();
                            Guild.getPlugin().getChatSettingsBuilder().loadConfig();

                            guildBuilder.sendMessage( player, "§aSuccessfully reloaded the config." );
                            break;

                        case "info":
                        default:
                            this.sendHelpMessage( player, guildBuilder );
                            break;
                    }
                    break;

                case 2:
                    if ( args[0].equals( "info" ) ) {
                        final Player targetPlayer = Bukkit.getPlayer( args[1] );

                        if ( targetPlayer == null ) {
                            guildBuilder.sendMessage( player, "§cThe target player is currently offline." );
                            return true;
                        }

                        final int kills = 352;

                        player.sendMessage( "§8§m----§r§6§l PLAYER INFORMATION §8§m----" );
                        player.sendMessage( "§aName: §6" + targetPlayer.getName() );
                        player.sendMessage( "§aUniqueId: §6" + targetPlayer.getUniqueId() );
                        player.sendMessage( "§aKills: " + ( kills < 100 ? "§a" : "§c" ) + kills );
                        player.sendMessage( "§aLevel: §6§lLevel §8» §6" );
                        player.sendMessage( "§aFate: §6§lLevel §8» §650" );
                        player.sendMessage( "§aSide: " + this.getRandomSide() );
                        player.sendMessage( "§8§m----§r§6§l PLAYER INFORMATION §8§m----" );
                    }
                    break;

                default:
                    this.sendHelpMessage( player, guildBuilder );
                    break;
            }
        }
        return false;
    }

    private String getRandomSide() {
        final int random = ThreadLocalRandom.current().nextInt( 3 ) + 1;

        String side = "";

        switch ( random ) {
            case 3:
                side = "§6§lHERO";
                break;

            case 2:
                side = "§4§lDEMONLORD";
                break;

            case 1:
                side = "§aFARMER";
                break;
        }

        return side;
    }

    private void sendHelpMessage( Player player, GuildBuilder guildBuilder ) {
        guildBuilder.sendHeader( player );

        guildBuilder.sendMessage( player, "§2Plugin name §8» §b" + Guild.getPlugin().getDescription().getName() );
        guildBuilder.sendMessage( player, "§2Plugin version §8» §b" + Guild.getPlugin().getDescription().getVersion() );
        guildBuilder.sendMessage( player, "§2Plugin author §8» §b" + Guild.getPlugin().getDescription().getAuthors() );
        guildBuilder.sendMessage( player, "§2Discord §8» §bNoJokeFNA | Julian#4166" );
        guildBuilder.sendMessage( player, "§2Discord Server §8» §bhttps://discord.gg/UHkUuFD" );

        player.sendMessage( "" );

        guildBuilder.sendMessage( player, "§2Available admin commands:" );
        guildBuilder.sendMessage( player, " §8- §c/adminguild reload §8» §bReload the messages.yml and settings.yml" );
        guildBuilder.sendMessage( player, " §8- §c/adminguild info <player> §8» §bGet the information about the player" );
        guildBuilder.sendMessage( player, " §8- §c/adminguild info §8» §bSee the info message" );

        guildBuilder.sendHeader( player );
    }
}
