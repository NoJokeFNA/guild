package io.github.nojokefna.guild.spigot.utils;

import de.dytanic.cloudnet.api.CloudAPI;
import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.chat.Chat;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class Data {

    private final FileBuilder fileBuilder;

    public Data() {
        this.fileBuilder = Guild.getPlugin().getChatSettingsBuilder();
    }

    public String getPrefix() {
        return Guild.getPlugin().getFileBuilder().getKey( "prefix" );
    }

    public String getGroup( Player player ) {
        switch ( this.fileBuilder.getKey( "chat.permission_plugin" ) ) {
            case "LuckPerms":
            case "PermissionsEx":
                Chat chat = Guild.getPlugin().getChat();
                return this.sendColoredMessage( chat.getGroupPrefix( player.getWorld(), chat.getPrimaryGroup( player ) ) );

            case "CloudNet":
                switch ( this.fileBuilder.getKey( "chat.cloudnet.use" ) ) {
                    case "display":
                        return this.sendColoredMessage( CloudAPI.getInstance().getOnlinePlayer( player.getUniqueId() ).getPermissionEntity()
                                                                .getHighestPermissionGroup( CloudAPI.getInstance().getPermissionPool() ).getDisplay()
                                                                + player.getName() );
                    case "prefix":
                        return this.sendColoredMessage( CloudAPI.getInstance().getOnlinePlayer( player.getUniqueId() ).getPermissionEntity()
                                                                .getHighestPermissionGroup( CloudAPI.getInstance().getPermissionPool() ).getPrefix()
                                                                + player.getName() );

                    default:
                        throw new UnsupportedOperationException( "§4Unsupported value in §cchat_settings.yml » chat.cloudnet.use » "
                                                                         + this.fileBuilder.getKey( "chat.cloudnet.use" ) );
                }

            default:
                throw new IllegalStateException( "§4§lPlease use §cLuckPerms§4§l, §cPermissionsEx §4§lor §cCloudNet§4§l! » "
                                                         + this.fileBuilder.getKey( "chat.permission_plugin" ) );
        }
    }

    public void sendTablist( Player player, String header, String footer ) {
        if ( header == null ) header = "";
        if ( footer == null ) footer = "";

        final IChatBaseComponent tabHeader = IChatBaseComponent.ChatSerializer.a( "{\"text\":\"" + header + "\"}" );
        final IChatBaseComponent tabFooter = IChatBaseComponent.ChatSerializer.a( "{\"text\":\"" + footer + "\"}" );

        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter( tabHeader );

        try {
            final Field field = packet.getClass().getDeclaredField( "b" );
            field.setAccessible( true );
            field.set( packet, tabFooter );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        } finally {
            ( ( CraftPlayer ) player ).getHandle().playerConnection.sendPacket( packet );
        }
    }

    private String sendColoredMessage( String message ) {
        return ChatColor.translateAlternateColorCodes( '&', message );
    }
}