package io.github.nojokefna.guild.spigot.utils.cloud.implement;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionGroup;
import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import io.github.nojokefna.guild.spigot.utils.cloud.CloudVersion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class CloudNetV3 implements CloudVersion {

    private final FileBuilder fileBuilder;

    public CloudNetV3() {
        this.fileBuilder = Guild.getPlugin().getChatSettingsBuilder();
    }

    @Override
    public String getPrefix( Player player ) {
        final IPermissionGroup iPermissionGroup = CloudNetDriver
                .getInstance()
                .getPermissionManagement()
                .getHighestPermissionGroup( CloudNetDriver.getInstance().getPermissionManagement().getUser( player.getUniqueId() ) );

        switch ( this.fileBuilder.getKey( "chat.cloudnet.use" ) ) {
            case "display":
                return this.sendColoredMessage( iPermissionGroup.getDisplay() + player.getName() );

            case "prefix":
                return this.sendColoredMessage( iPermissionGroup.getPrefix() + player.getName() );

            default:
                throw new UnsupportedOperationException( "§4Unsupported value in §cchat_settings.yml » chat.cloudnet.use » " + this.fileBuilder.getKey( "chat.cloudnet.use" ) );
        }
    }

    private String sendColoredMessage( String message ) {
        return ChatColor.translateAlternateColorCodes( '&', message );
    }
}
