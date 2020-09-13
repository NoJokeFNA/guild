package io.github.nojokefna.guild.spigot.interfaces;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface GuildRecodeInterface {

    /**
     * Create a guild with the name and tag you want
     *
     * @param player      Define the player, whos creates the guild
     * @param guildName   Enter the guild name, you want to use
     * @param guildTag    Enter the guild tag, you want to use
     * @param guildLeader Set the guild leader from the guild, you've created
     */
    void createGuild( Player player, String guildName, String guildTag, String guildLeader );

    /**
     * Delete your own guild
     *
     * @param player Define the player, whos deleting the guild
     */
    void deleteGuild( Player player );

    /**
     * Leave the guild, in which you are
     *
     * @param player Define the player, whos leaving the guild
     */
    void leaveGuild( Player player );

    void sendInvite( Player player, Player targetPlayer );

    void revokeInvite( Player player, OfflinePlayer targetOfflinePlayer );

    void acceptInvite( Player player );

    void denyInvite( Player player );

    void getInvites( Player player );

    String getInvitedGuildName( OfflinePlayer targetOfflinePlayer );

    String getInvitedGuildTag( OfflinePlayer targetOfflinePlayer );

    void setGuildMaster( Player player, OfflinePlayer targetOfflinePlayer );

    void promotePlayer( Player player, OfflinePlayer targetOfflinePlayer );

    void demotePlayer( Player player, OfflinePlayer targetOfflinePlayer );

    void kickPlayer( Player player, OfflinePlayer targetOfflinePlayer );

    void toggleChat( Player player );

    void sendMessage( Player player, String... message );

    void sendMembers( Player player );

    void sendMembers( Player player, String guild );

    void sendOfficers( Player player );

    void sendOfficers( Player player, String guild );

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
