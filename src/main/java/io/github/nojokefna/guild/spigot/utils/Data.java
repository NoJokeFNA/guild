package io.github.nojokefna.guild.spigot.utils;

import de.dytanic.cloudnet.api.CloudAPI;
import io.github.nojokefna.guild.spigot.Guild;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.chat.Chat;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class Data {

    private final ConfigurationSection section;

    public Data() {
        this.section = Guild.getPlugin().getSettingsManager().getFileConfiguration();
    }

    public String getPrefix() {
        return Guild.getPlugin().getFileManager().getKey( "prefix" );
    }

    public String getGroup( Player player ) {
        switch ( this.section.getString( "chat.permission_plugin" ) ) {
            case "LuckPerms":
                LuckPerms luckPerms = LuckPermsProvider.get();
                ContextManager contextManager = luckPerms.getContextManager();

                User user = luckPerms.getUserManager().getUser( player.getUniqueId() );
                QueryOptions queryOptions = contextManager.getQueryOptions( Objects.requireNonNull( user ) ).orElse( contextManager.getStaticQueryOptions() );
                CachedMetaData metaData = user.getCachedData().getMetaData( queryOptions );

                return this.sendColoredMessage( metaData.getPrefix() );

            case "PermissionsEx": {
                Chat chat = Guild.getPlugin().getChat();
                return this.sendColoredMessage( chat.getGroupPrefix( player.getWorld(), chat.getPrimaryGroup( player ) ) );
            }

            case "CloudNet":
                switch ( this.section.getString( "chat.cloudnet.use" ) ) {
                    case "display":
                        return this.sendColoredMessage( CloudAPI.getInstance().getOnlinePlayer( player.getUniqueId() ).getPermissionEntity()
                                .getHighestPermissionGroup( CloudAPI.getInstance().getPermissionPool() ).getDisplay()
                                + player.getName() );
                    case "prefix":
                        return this.sendColoredMessage( CloudAPI.getInstance().getOnlinePlayer( player.getUniqueId() ).getPermissionEntity()
                                .getHighestPermissionGroup( CloudAPI.getInstance().getPermissionPool() ).getPrefix()
                                + player.getName() );

                    default:
                        player.sendMessage( "§4Unsupported value in §cchat_settings.yml » chat.cloudnet.use" );
                        break;
                }

            default:
                player.sendMessage( "§4§lPlease use §cLuckPerms§4§l, §cPermissionsEx §4§lor §cCloudNet§4§l!" );
                break;
        }
        return null;
    }

    public void sendTablist( Player player, String header, String footer ) {
        if ( header == null ) header = "";
        if ( footer == null ) footer = "";

        IChatBaseComponent tabHeader = IChatBaseComponent.ChatSerializer.a( "{\"text\":\"" + header + "\"}" );
        IChatBaseComponent tabFooter = IChatBaseComponent.ChatSerializer.a( "{\"text\":\"" + footer + "\"}" );

        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter( tabHeader );

        try {
            Field field = packet.getClass().getDeclaredField( "b" );
            field.setAccessible( true );
            field.set( packet, tabFooter );
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            ( ( CraftPlayer ) player ).getHandle().playerConnection.sendPacket( packet );
        }
    }

    private String sendColoredMessage( String message ) {
        return ChatColor.translateAlternateColorCodes( '&', message );
    }
}
