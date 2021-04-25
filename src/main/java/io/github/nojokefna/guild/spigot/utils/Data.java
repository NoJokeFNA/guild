package io.github.nojokefna.guild.spigot.utils;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import io.github.nojokefna.guild.spigot.utils.cloud.implement.CloudNetV2;
import io.github.nojokefna.guild.spigot.utils.cloud.implement.CloudNetV3;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class Data {

    private final FileBuilder fileBuilder;

    public Data() {
        this.fileBuilder = Guild.getPlugin().getChatSettingsBuilder();
    }

    public String getGroup( Player player ) {
        switch ( this.fileBuilder.getKey( "chat.permission_plugin" ) ) {
            case "LuckPerms":
            case "PermissionsEx":
                final Chat chat = Guild.getPlugin().getChat();
                return this.sendColoredMessage( chat.getGroupPrefix( player.getWorld(), chat.getPrimaryGroup( player ) ) );

            case "CloudNetV2":
                return new CloudNetV2().getPrefix( player );

            case "CloudNetV3":
                return new CloudNetV3().getPrefix( player );

            default:
                throw new IllegalStateException( "§4§lPlease use §cLuckPerms§4§l, §cPermissionsEx §4§lor §cCloudNetV(2/3)§4§l! » " + this.fileBuilder.getKey( "chat.permission_plugin" ) );
        }
    }

    public void sendTablist( Player player, String header, String footer ) {
        if ( header == null ) header = "";
        if ( footer == null ) footer = "";

        /*final IChatBaseComponent tabHeader = IChatBaseComponent.ChatSerializer.a( "{\"text\":\"" + header + "\"}" );
        final IChatBaseComponent tabFooter = IChatBaseComponent.ChatSerializer.a( "{\"text\":\"" + footer + "\"}" );

        final PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter( tabHeader );

        try {
            final Field field = packet.getClass().getDeclaredField( "b" );
            field.setAccessible( true );
            field.set( packet, tabFooter );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        } finally {
            ( ( CraftPlayer ) player ).getHandle().playerConnection.sendPacket( packet );
        }*/
    }

    private String sendColoredMessage( String message ) {
        return ChatColor.translateAlternateColorCodes( '&', message );
    }

    public String getPrefix() {
        return Guild.getPlugin().getFileBuilder().getKey( "prefix" );
    }
}