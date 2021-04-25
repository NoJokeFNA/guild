package io.github.nojokefna.guild.spigot.utils.cloud.implement;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import io.github.nojokefna.guild.spigot.utils.cloud.CloudVersion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CloudNetV2 implements CloudVersion {

    private final FileBuilder fileBuilder;

    public CloudNetV2() {
        this.fileBuilder = Guild.getPlugin().getChatSettingsBuilder();
    }

    @Override
    public String getPrefix( Player player ) {
        final PermissionGroup permissionEntity = CloudAPI
                .getInstance()
                .getOnlinePlayer( player.getUniqueId() )
                .getPermissionEntity()
                .getHighestPermissionGroup( CloudAPI.getInstance().getPermissionPool() );

        switch ( this.fileBuilder.getKey( "chat.cloudnet.use" ) ) {
            case "display":
                return this.sendColoredMessage( permissionEntity.getDisplay() + player.getName() );

            case "prefix":
                return this.sendColoredMessage( permissionEntity.getPrefix() + player.getName() );

            default:
                throw new UnsupportedOperationException( "§4Unsupported value in §cchat_settings.yml » chat.cloudnet.use » " + this.fileBuilder.getKey( "chat.cloudnet.use" ) );
        }
    }

    private String sendColoredMessage( String message ) {
        return ChatColor.translateAlternateColorCodes( '&', message );
    }
}
