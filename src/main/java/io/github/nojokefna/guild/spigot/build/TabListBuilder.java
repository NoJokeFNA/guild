package io.github.nojokefna.guild.spigot.build;

import io.github.nojokefna.guild.spigot.cache.CacheUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class TabListBuilder {

    public static void setNameTag() {
        for ( Player players : Bukkit.getOnlinePlayers() ) {
            CacheUser user = CacheUser.getUser( players );

            if ( user.getPrefix() != null && user.getTagId() != null ) {
                for ( Player all : Bukkit.getOnlinePlayers() ) {
                    String playerName = players.getName();

                    if ( playerName.length() >= 10 )
                        playerName = players.getName().substring( 0, 10 );

                    Team team = all.getScoreboard().getTeam( user.getTagId() + playerName ) != null
                            ? all.getScoreboard().getTeam( user.getTagId() + playerName )
                            : all.getScoreboard().registerNewTeam( user.getTagId() + playerName );

                    if ( ! team.getEntries().contains( playerName ) )
                        team.addEntry( playerName );

                    if ( ! user.isInGuild() )
                        team.setPrefix( user.getPrefix()[0] );
                    else {
                        team.setPrefix( user.getPrefix()[0] );
                        team.setSuffix( user.getSuffix()[0] );
                    }

                    String nameTag = "" + Arrays.toString( user.getNameTag() )
                            .replace( "[", "" )
                            .replace( "]", "" );

                    players.setPlayerListName( nameTag + playerName );
                    players.setDisplayName( team.getPrefix() + playerName );
                }
            }
        }
    }
}