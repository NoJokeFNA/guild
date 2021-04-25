package io.github.nojokefna.guild.spigot.commands.complete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TabComplete implements TabCompleter {

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
