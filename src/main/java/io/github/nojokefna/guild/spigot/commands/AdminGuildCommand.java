package io.github.nojokefna.guild.spigot.commands;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.build.GuildBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

                default:
                    this.sendHelpMessage( player, guildBuilder );
                    break;
            }
        }
        return false;
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
        guildBuilder.sendMessage( player, " §8- §c/guildadmin reload §8» §bReload the messages.yml and settings.yml" );
        guildBuilder.sendMessage( player, " §8- §c/guildadmin info §8» §bSee the info message" );

        guildBuilder.sendHeader( player );
    }
}
