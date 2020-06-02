package io.github.nojokefna.guild.spigot.controller;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.build.GuildBuilder;
import io.github.nojokefna.guild.spigot.cache.CacheUser;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import io.github.nojokefna.guild.spigot.database.api.GuildAPI;
import io.github.nojokefna.guild.spigot.database.api.GuildInvitesAPI;
import io.github.nojokefna.guild.spigot.database.api.GuildUserAPI;
import io.github.nojokefna.guild.spigot.interfaces.GuildRecodeInterface;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildRecodeController implements GuildRecodeInterface {

    @Getter
    private final List<String> guildList, guildMessageList;

    private final FileBuilder fileBuilder;

    private final GuildAPI guildAPI;
    private final GuildUserAPI guildUserAPI;
    private final GuildInvitesAPI guildInvitesAPI;
    private final GuildBuilder guildBuilder;

    public GuildRecodeController() {
        this.guildList = new ArrayList<>();
        this.guildMessageList = new ArrayList<>();

        this.fileBuilder = Guild.getPlugin().getFileManager();
        this.guildAPI = Guild.getPlugin().getGuildAPI();
        this.guildUserAPI = Guild.getPlugin().getGuildUserAPI();
        this.guildInvitesAPI = Guild.getPlugin().getGuildInvitesAPI();
        this.guildBuilder = Guild.getPlugin().getGuildBuilder();
    }

    @Override
    public void createGuild( Player player, String guildName, String guildTag, String guildLeader ) {
        CacheUser user = CacheUser.getUser( player );

        int costs = Integer.parseInt( this.fileBuilder.getKey( "guild.create_guild.costs" ) );
        long time = Long.parseLong( this.fileBuilder.getKey( "guild.delete_guild.security_countdown" ) );

        if ( user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_guild" ) );
            return;
        }

        if ( this.isInteger( guildName ) || this.isInteger( guildTag ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.create_guild.no_letters" ) );
            return;
        }

        if ( this.guildAPI.guildExists( "guild_name", guildName ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.create_guild.guild_exists" ) );
            return;
        }

        if ( guildName.length() > 12 ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.create_guild.guild_name_to_long" ) );
            return;
        }

        if ( guildTag.length() > 5 ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.create_guild.guild_name_to_tag" ) );
            return;
        }

        if ( ! ( Guild.getPlugin().getEcon().getBalance( player ) >= costs ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.create_guild.not_enough_money" ) );
            return;
        }

        if ( ! this.guildList.contains( player.getName() ) ) {
            this.guildList.add( player.getName() );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.create_guild.security_message_1" )
                    .replace( "{GUILD}", guildName ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.create_guild.security_message_2" ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.create_guild.security_message_3" ) );
            return;
        }

        Guild.getPlugin().getEcon().withdrawPlayer( player, costs );

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.create_guild.successfully_created" ) );
        this.guildAPI.createGuild( guildName, guildTag, guildLeader );
        this.guildUserAPI.createPlayer( player.getUniqueId(), player.getName(), guildName, guildTag, "Master" );
        this.guildList.remove( player.getName() );

        user.setInGuild( true );

        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * time );
    }

    @Override
    public void deleteGuild( Player player ) {
        CacheUser user = CacheUser.getUser( player );

        long time = Long.parseLong( this.fileBuilder.getKey( "guild.delete_guild.security_countdown" ) );

        if ( ! user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( ! user.isMaster() ) {
            this.guildBuilder.sendMessage( player, fileBuilder.getKey( "guild.is_not_the_guild_master" ) );
            return;
        }

        user.setInGuild( false );

        if ( ! this.guildList.contains( player.getName() ) ) {
            this.guildList.add( player.getName() );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.delete_guild.security_message_1" )
                    .replace( "{GUILD}", this.getGuildName( player ) ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.delete_guild.security_message_2" ) );
            return;
        }

        for ( String uuid : this.guildUserAPI.getList( "guild_name", this.getGuildName( player ), "player_uuid" ) )
            this.guildUserAPI.deleteKey( UUID.fromString( uuid ) );

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.delete_guild.successfully_deleted" ) );
        this.guildAPI.deleteGuild( this.getGuildName( player ) );

        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * time );
    }

    @Override
    public void leaveGuild( Player player ) {
        CacheUser user = CacheUser.getUser( player );

        long time = Long.parseLong( this.fileBuilder.getKey( "guild.leave_guild.security_countdown" ) );

        if ( ! user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( user.isMaster() ) {
            this.deleteGuild( player );
            return;
        }

        if ( ! this.guildList.contains( player.getName() ) ) {
            this.guildList.add( player.getName() );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.leave_guild.security_message_1" )
                    .replace( "{GUILD}", this.getGuildName( player ) ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.leave_guild.security_message_2" ) );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.leave_guild.leaved" ) );
        this.guildUserAPI.deleteKey( player.getUniqueId() );

        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * time );
    }

    @Override
    public void sendInvite( Player player, Player target ) {
        CacheUser user = CacheUser.getUser( player );
        CacheUser targetUser = CacheUser.getUser( target );

        if ( ! user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( target == null ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.target_is_offline" ) );
            return;
        }

        if ( targetUser.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.target_is_in_guild" ) );
            return;
        }

        if ( user.isMember() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.target_is_in_player_guild" ) );
            return;
        }

        if ( ! user.isOfficer() && ! user.isMaster() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.player_is_not_master" ) );
            return;
        }

        if ( this.guildInvitesAPI.keyExists( "guild_name", this.getGuildName( player ) ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.already_invite_target" ) );
            return;
        }

        this.guildInvitesAPI.createPlayer( target.getUniqueId(), target, player.getName(), this.getGuildName( player ), this.getGuildTag( player ) );
        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.invited_target" )
                .replace( "{TARGET}", target.getName() ) );

        Bukkit.getScheduler().runTaskLater( Guild.getPlugin(), ()
                -> this.guildBuilder.sendMessage( target, this.fileBuilder.getKey( "guild.guild_invites.invited_target_message" )
                .replace( "{PLAYER}", player.getName() )
                .replace( "{GUILD}", this.getInvitedGuildName( target ) ) ), 5L
        );
    }

    @Override
    public void revokeInvite( Player player, OfflinePlayer target ) {
        CacheUser user = CacheUser.getUser( player );

        if ( ! user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( ! user.isMaster() ) {
            this.guildBuilder.sendMessage( player, fileBuilder.getKey( "guild.is_not_the_guild_master" ) );
            return;
        }

        if ( ! this.guildInvitesAPI.keyExists( "player_name", target.getName() ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.target_not_invited" ) );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.revoked_invite" ) );
        this.guildInvitesAPI.deleteInvite( target );
    }

    @Override
    public void acceptInvite( Player player ) {
        CacheUser user = CacheUser.getUser( player );

        if ( user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, "§cDu musst deine Gilde verlassen, um eine andere Gilde zu betreten." );
            return;
        }

        if ( ! this.guildInvitesAPI.keyExists( "player_name", player.getName() ) ) {
            this.guildBuilder.sendMessage( player, "§cDu hast keine Einladungen erhalten." );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.invite_accepted" )
                .replace( "{GUILD}", this.getInvitedGuildName( player ) ) );
        this.guildUserAPI.createPlayer( player.getUniqueId(), player.getName(), this.getInvitedGuildName( player ),
                this.getInvitedGuildTag( player ), "Member" );
        this.guildInvitesAPI.deleteInvite( player );

        user.setInGuild( true );
    }

    @Override
    public void denyInvite( Player player ) {
        if ( ! this.guildInvitesAPI.keyExists( "player_name", player.getName() ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.has_no_invite" ) );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.invite_denyed" )
                .replace( "{GUILD}", this.getInvitedGuildName( player ) ) );
        this.guildInvitesAPI.deleteInvite( player );
    }

    @Override
    public void getInvites( Player player ) {
        List<String> inviteList, inviteNameList, inviteTagList;

        {
            inviteList = this.guildInvitesAPI.getInvites( player, "invited_name" );
            inviteNameList = this.guildInvitesAPI.getInvites( player, "guild_name" );
            inviteTagList = this.guildInvitesAPI.getInvites( player, "guild_tag" );
        }

        this.guildBuilder.sendHeader( player );
        this.guildBuilder.sendMessage( player, "§aEinladungen:" );

        for ( int i = 1; i < 5; i++ ) {
            for ( int j = 0; j < inviteList.size(); j++ ) {
                this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.format.line" + i )
                        .replace( "{NAME}", inviteList.get( j ) )
                        .replace( "{GUILD}", inviteNameList.get( j ) )
                        .replace( "{TAG}", inviteTagList.get( j ) ).trim() );
            }
        }

        this.guildBuilder.sendHeader( player );
    }

    @Override
    public String getInvitedGuildName( OfflinePlayer player ) {
        return null;
    }

    @Override
    public String getInvitedGuildTag( OfflinePlayer player ) {
        return null;
    }

    @Override
    public void setGuildMaster( Player player, OfflinePlayer target ) {

    }

    @Override
    public void promotePlayer( Player player, OfflinePlayer target ) {

    }

    @Override
    public void demotePlayer( Player player, OfflinePlayer target ) {

    }

    @Override
    public void kickPlayer( Player player, OfflinePlayer target ) {

    }

    @Override
    public void toggleChat( Player player ) {

    }

    @Override
    public void sendMessage( Player player, String... message ) {

    }

    @Override
    public void sendMembers( Player player ) {

    }

    @Override
    public void sendMembers( Player player, String guild ) {

    }

    @Override
    public void sendOfficers( Player player ) {

    }

    @Override
    public void sendOfficers( Player player, String guild ) {

    }

    @Override
    public void sendGuildInfo( Player player ) {

    }

    @Override
    public void sendGuildInfo( Player player, String guild ) {

    }

    @Override
    public String getMembers( Player player ) {
        return null;
    }

    @Override
    public String getOfficers( Player player ) {
        return null;
    }

    @Override
    public String getGuildMaster( Player player ) {
        return null;
    }

    @Override
    public String getGuildName( OfflinePlayer player ) {
        return null;
    }

    @Override
    public String getGuildTag( Player player ) {
        return null;
    }

    @Override
    public void sendGuildBank( Player player ) {

    }

    @Override
    public void addGuildBank( Player player, int amount ) {

    }

    @Override
    public void removeGuildBank( Player player, int amount ) {

    }

    @Override
    public boolean isGuildMaster( UUID playerUuid ) {
        return false;
    }

    @Override
    public boolean isGuildOfficer( UUID playerUuid ) {
        return false;
    }

    @Override
    public boolean isGuildMember( UUID playerUuid ) {
        return false;
    }

    private boolean isInteger( String value ) {
        try {
            Integer.parseInt( value );
        } catch ( NumberFormatException | NullPointerException ex ) {
            return false;
        }
        return true;
    }
}
