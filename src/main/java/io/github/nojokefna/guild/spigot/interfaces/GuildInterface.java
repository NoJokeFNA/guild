package io.github.nojokefna.guild.spigot.interfaces;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public interface GuildInterface {

    void createGuild( Player player, String guildName, String guildTag, String leader );

    void deleteGuild( Player player );

    void leaveGuild( Player player );

    void sendInvite( Player player, Player target );

    void sendInvites( Player player );

    void revokeInvite( Player player, OfflinePlayer target );

    void acceptInvite( Player player );

    void denyInvite( Player player );

    String getInvitedGuild( OfflinePlayer player );

    String getInvitedGuildTag( OfflinePlayer player );

    void promotePlayer( Player player, OfflinePlayer target );

    void demotePlayer( Player player, OfflinePlayer target );

    void kickPlayer( Player player, OfflinePlayer target );

    void toggleChat( Player player );

    void sendMessage( Player player, String... message );

    void setGuildMaster( Player player, OfflinePlayer target );

    void sendMembers( Player player );

    String sendMembersString( Player player );

    void sendOfficers( Player player );

    String sendOfficersString( Player player );

    void sendMembers( Player player, String guild );

    void sendOfficers( Player player, String guild );

    void sendGuildInfo( Player player, String guild );

    void sendGuildList( Player player );

    String sendGuildMaster( Player player );

    String sendGuildName( OfflinePlayer player );

    String sendGuildTag( Player player );

    void sendGuildBank( Player player );

    void addGuildBank( Player player, int amount );

    void removeGuildBank( Player player, int amount );

    boolean isGuildMaster( UUID uuid );

    boolean isGuildOfficer( UUID uuid );

    boolean isGuildMember( UUID uuid );
}