package io.github.nojokefna.guild.spigot.interfaces;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface GuildRecodeInterface {

    void createGuild( Player player, String guildName, String guildTag, String guildLeader );
    void deleteGuild( Player player );
    void leaveGuild( Player player );

    void sendInvite( Player player, Player target );
    void revokeInvite( Player player, OfflinePlayer target );
    void acceptInvite( Player player );
    void denyInvite( Player player );
    void getInvites( Player player );
    String getInvitedGuildName( OfflinePlayer player );
    String getInvitedGuildTag( OfflinePlayer player );

    void setGuildMaster( Player player, OfflinePlayer target );
    void promotePlayer( Player player, OfflinePlayer target );
    void demotePlayer( Player player, OfflinePlayer target );
    void kickPlayer( Player player, OfflinePlayer target );

    void toggleChat( Player player );
    void sendMessage( Player player, String... message );

    void sendMembers( Player player );
    void sendMembers( Player player, String guild );
    void sendOfficers( Player player );
    void sendOfficers( Player player, String guild );
    void sendGuildInfo( Player player );
    void sendGuildInfo( Player player, String guild );
    String getMembers( Player player );
    String getOfficers( Player player );

    String getGuildMaster( Player player );
    String getGuildName( OfflinePlayer player );
    String getGuildTag( Player player );

    void sendGuildBank( Player player );
    void addGuildBank( Player player, int amount );
    void removeGuildBank( Player player, int amount );

    boolean isGuildMaster( UUID playerUuid );
    boolean isGuildOfficer( UUID playerUuid );
    boolean isGuildMember( UUID playerUuid );
}
