package io.github.nojokefna.guild.spigot.build;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.cache.CacheUser;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.stream.IntStream;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class GuildBuilder {

    private final FileBuilder fileManager;

    public GuildBuilder() {
        this.fileManager = Guild.getPlugin().getFileBuilder();
    }

    public void sendHelpMessage( Player player, int site ) {
        this.sendHeader( player );

        switch ( site ) {
            case 2:
                this.sendPage( player, "Two" );
                break;

            default:
            case 1:
                this.sendPage( player, "One" );
                break;
        }

        this.sendHeader( player );
    }

    private void sendPage( Player player, String page ) {
        final CacheUser user = CacheUser.getUser( player );

        IntStream.range( 1, 11 ).mapToObj( i -> new String[] {
                this.fileManager.getKey( "guild.help_message_guild.page" + page + ".line" + i )
        } ).forEachOrdered( user::setHelpMessage );

        for ( int i = 1; i < 11; i++ )
            player.sendMessage( this.fileManager.getKey( "guild.help_message_guild.page" + page + ".line" + i ) );
    }

    public void sendHeader( Player player ) {
        final CacheUser user = CacheUser.getUser( player );

        if ( user.getHeaderMessage() == null )
            IntStream.range( 1, 4 ).mapToObj( i -> fileManager.getKey( "guild.help_message_guild.header.line" + i ) ).forEachOrdered( user::setHeaderMessage );

        for ( int i = 1; i < 4; i++ )
            player.sendMessage( fileManager.getKey( "guild.help_message_guild.header.line" + i ) );

        /*player.sendMessage( user.getHeaderMessage() );*/
    }

    public String sendHeaderString( Player player ) {
        final CacheUser user = CacheUser.getUser( player );

        if ( user.getHeaderMessage() == null )
            IntStream.range( 1, 4 ).mapToObj( i -> fileManager.getKey( "guild.help_message_guild.header.line" + i ) ).forEachOrdered( user::setHeaderMessage );

        return user.getHeaderMessage();
    }

    public void sendMessage( Player player, String message ) {
        player.sendMessage( Guild.getPlugin().getData().getPrefix() + " " + ChatColor.translateAlternateColorCodes( '&', message ) );
    }
}
