package io.github.nojokefna.guild.spigot.controller;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.build.GuildBuilder;
import io.github.nojokefna.guild.spigot.cache.CacheUser;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import io.github.nojokefna.guild.spigot.database.api.GuildAPI;
import io.github.nojokefna.guild.spigot.database.api.GuildInvitesAPI;
import io.github.nojokefna.guild.spigot.database.api.GuildUserAPI;
import io.github.nojokefna.guild.spigot.events.*;
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

        this.fileBuilder = Guild.getPlugin().getFileBuilder();
        this.guildAPI = Guild.getPlugin().getGuildAPI();
        this.guildUserAPI = Guild.getPlugin().getGuildUserAPI();
        this.guildInvitesAPI = Guild.getPlugin().getGuildInvitesAPI();
        this.guildBuilder = Guild.getPlugin().getGuildBuilder();
    }


    @Override
    public void createGuild( Player player, String guildName, String guildTag, String guildLeader ) {
        final CacheUser user = CacheUser.getUser( player );

        final int costs = Integer.parseInt( this.fileBuilder.getKey( "guild.create_guild.costs" ) );
        final long time = Long.parseLong( this.fileBuilder.getKey( "guild.delete_guild.security_countdown" ) );

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

        if ( !( Guild.getPlugin().getEconomy().getBalance( player ) >= costs ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.create_guild.not_enough_money" ) );
            return;
        }

        if ( !this.guildList.contains( player.getName() ) ) {
            this.guildList.add( player.getName() );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.create_guild.security_message_1" )
                    .replace( "{GUILD}", guildName ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.create_guild.security_message_2" ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.create_guild.security_message_3" ) );
            return;
        }

        Guild.getPlugin().getEconomy().withdrawPlayer( player, costs );

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.create_guild.successfully_created" ) );
        this.guildAPI.createGuild( guildName, guildTag, guildLeader );
        this.guildUserAPI.createPlayer( player.getUniqueId(), player.getName(), guildName, guildTag, "Master" );
        this.guildList.remove( player.getName() );

        user.setInGuild( true );

        Bukkit.getPluginManager().callEvent( new GuildCreateEvent( player, guildName, guildTag, guildLeader ) );

        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * time );
    }

    @Override
    public void deleteGuild( Player player ) {
        final CacheUser user = CacheUser.getUser( player );

        final long time = Long.parseLong( this.fileBuilder.getKey( "guild.delete_guild.security_countdown" ) );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( !user.isMaster() ) {
            this.guildBuilder.sendMessage( player, fileBuilder.getKey( "guild.is_not_the_guild_master" ) );
            return;
        }


        if ( !this.guildList.contains( player.getName() ) ) {
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

        user.setInGuild( false );

        Bukkit.getPluginManager().callEvent( new GuildDeleteEvent( player ) );

        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * time );
    }

    @Override
    public void leaveGuild( Player player ) {
        final CacheUser user = CacheUser.getUser( player );

        final long time = Long.parseLong( this.fileBuilder.getKey( "guild.leave_guild.security_countdown" ) );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( user.isMaster() ) {
            this.deleteGuild( player );
            return;
        }

        if ( !this.guildList.contains( player.getName() ) ) {
            this.guildList.add( player.getName() );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.leave_guild.security_message_1" )
                    .replace( "{GUILD}", this.getGuildName( player ) ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.leave_guild.security_message_2" ) );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.leave_guild.leaved" ) );
        this.guildUserAPI.deleteKey( player.getUniqueId() );

        user.setInGuild( false );

        Bukkit.getPluginManager().callEvent( new GuildLeaveEvent( player ) );

        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * time );
    }

    @Override
    public void sendInvite( Player player, Player targetPlayer ) {
        final CacheUser user = CacheUser.getUser( player );
        final CacheUser targetUser = CacheUser.getUser( targetPlayer );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( targetPlayer == null ) {
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

        if ( !user.isOfficer() && !user.isMaster() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.player_is_not_master" ) );
            return;
        }

        if ( this.guildInvitesAPI.keyExists( "guild_name", this.getGuildName( player ) ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.already_invite_target" ) );
            return;
        }

        this.guildInvitesAPI.createPlayer( targetPlayer.getUniqueId(), targetPlayer, player.getName(), this.getGuildName( player ), this.getGuildTag( player ) );
        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.invited_target" )
                .replace( "{TARGET}", targetPlayer.getName() ) );

        Bukkit.getScheduler().runTaskLater( Guild.getPlugin(), ()
                -> this.guildBuilder.sendMessage( targetPlayer, this.fileBuilder.getKey( "guild.guild_invites.invited_target_message" )
                .replace( "{PLAYER}", player.getName() )
                .replace( "{GUILD}", this.getInvitedGuildName( targetPlayer ) ) ), 5L
        );

        Bukkit.getPluginManager().callEvent( new GuildSendInviteEvent( player, targetPlayer ) );
    }

    @Override
    public void revokeInvite( Player player, OfflinePlayer targetOfflinePlayer ) {
        final CacheUser user = CacheUser.getUser( player );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( !user.isMaster() ) {
            this.guildBuilder.sendMessage( player, fileBuilder.getKey( "guild.is_not_the_guild_master" ) );
            return;
        }

        if ( !this.guildInvitesAPI.keyExists( "player_name", targetOfflinePlayer.getName() ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.target_not_invited" ) );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.revoked_invite" ) );
        this.guildInvitesAPI.deleteInvite( targetOfflinePlayer );

        Bukkit.getPluginManager().callEvent( new GuildRevokeInviteEvent( player, targetOfflinePlayer ) );
    }

    @Override
    public void acceptInvite( Player player ) {
        final CacheUser user = CacheUser.getUser( player );

        if ( user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, "§cDu musst deine Gilde verlassen, um eine andere Gilde zu betreten." );
            return;
        }

        if ( !this.guildInvitesAPI.keyExists( "player_name", player.getName() ) ) {
            this.guildBuilder.sendMessage( player, "§cDu hast keine Einladungen erhalten." );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.invite_accepted" )
                .replace( "{GUILD}", this.getInvitedGuildName( player ) ) );
        this.guildUserAPI.createPlayer( player.getUniqueId(), player.getName(), this.getInvitedGuildName( player ),
                                        this.getInvitedGuildTag( player ), "Member"
        );
        this.guildInvitesAPI.deleteInvite( player );

        user.setInGuild( true );

        Bukkit.getPluginManager().callEvent( new GuildAcceptInviteEvent( player ) );
    }

    @Override
    public void denyInvite( Player player ) {
        if ( !this.guildInvitesAPI.keyExists( "player_name", player.getName() ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.has_no_invite" ) );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.invite_denyed" )
                .replace( "{GUILD}", this.getInvitedGuildName( player ) ) );
        this.guildInvitesAPI.deleteInvite( player );

        Bukkit.getPluginManager().callEvent( new GuildDenyInviteEvent( player ) );
    }

    @Override
    public void getInvites( Player player ) {
        final List<String> inviteList, inviteNameList, inviteTagList;

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

        for ( int i = 0; i < inviteList.size(); i++ ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.format.line1" ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.format.line2" )
                    .replace( "{NAME}", inviteList.get( i ) ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.format.line3" )
                    .replace( "{GUILD}", inviteNameList.get( i ) ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.format.line4" )
                    .replace( "{TAG}", inviteTagList.get( i ) ) );
        }

        this.guildBuilder.sendHeader( player );
    }

    @Override
    public String getInvitedGuildName( OfflinePlayer targetOfflinePlayer ) {
        return this.guildUserAPI.getKey( targetOfflinePlayer.getUniqueId(), "guild_name" );
    }

    @Override
    public String getInvitedGuildTag( OfflinePlayer targetOfflinePlayer ) {
        return this.guildUserAPI.getKey( targetOfflinePlayer.getUniqueId(), "guild_tag" );
    }

    @Override
    public void setGuildMaster( Player player, OfflinePlayer targetOfflinePlayer ) {
        final CacheUser user = CacheUser.getUser( player );

        final long time = Long.parseLong( this.fileBuilder.getKey( "guild.set_guild_master.security_countdown" ) );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( !user.isMaster() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_not_the_guild_master" ) );
            return;
        }

        if ( this.guildUserAPI.playerExists( targetOfflinePlayer.getUniqueId() ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.set_guild_master.target_is_in_no_guild" ) );
            return;
        }

        if ( !this.guildList.contains( player.getName() ) ) {
            this.guildList.add( player.getName() );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.set_guild_master.security_message_1" )
                    .replace( "{TARGET}", targetOfflinePlayer.getName() ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.set_guild_master.security_message_2" ) );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.set_guild_master.set_master" ) );

        this.guildAPI.updateGuild( "guild_leader", this.getGuildName( player ), targetOfflinePlayer.getName() );
        this.guildUserAPI.updateKey( "guild_rank", "Officer", player.getUniqueId() );
        this.guildUserAPI.updateKey( "guild_rank", "Master", targetOfflinePlayer.getUniqueId() );

        this.guildList.remove( player.getName() );

        Bukkit.getPluginManager().callEvent( new GuildSetMasterEvent( player, targetOfflinePlayer ) );

        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * time );
    }

    @Override
    public void promotePlayer( Player player, OfflinePlayer targetOfflinePlayer ) {
        final CacheUser user = CacheUser.getUser( player );

        final long time = Long.parseLong( this.fileBuilder.getKey( "guild.promote_player.security_countdown" ) );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( !user.isMaster() ) {
            this.guildBuilder.sendMessage( player, fileBuilder.getKey( "guild.is_not_the_guild_master" ) );
            return;
        }

        if ( user.isOfficer() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.promote_player.target_is_officer" ) );
            return;
        }

        if ( !this.guildList.contains( player.getName() ) ) {
            this.guildList.add( player.getName() );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.promote_player.security_message_1" )
                    .replace( "{TARGET}", targetOfflinePlayer.getName() ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.promote_player.security_message_2" ) );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.promote_player.promote_player" )
                .replace( "{TARGET}", targetOfflinePlayer.getName() ) );

        this.guildUserAPI.updateKey( "guild_rank", "Officer", targetOfflinePlayer.getUniqueId() );

        this.guildList.remove( player.getName() );

        Bukkit.getPluginManager().callEvent( new GuildPromotePlayerEvent( player, targetOfflinePlayer ) );

        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * time );
    }

    @Override
    public void demotePlayer( Player player, OfflinePlayer targetOfflinePlayer ) {
        final CacheUser user = CacheUser.getUser( player );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( !user.isMaster() ) {
            this.guildBuilder.sendMessage( player, fileBuilder.getKey( "guild.is_not_the_guild_master" ) );
            return;
        }

        if ( user.isMember() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.demote_player.target_is_officer" ) );
            return;
        }

        if ( !this.guildList.contains( player.getName() ) ) {
            this.guildList.add( player.getName() );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.demote_player.security_message_1" )
                    .replace( "{TARGET}", targetOfflinePlayer.getName() ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.demote_player.security_message_2" ) );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.demote_player.demote_player" )
                .replace( "{TARGET}", targetOfflinePlayer.getName() ) );

        this.guildUserAPI.updateKey( "guild_rank", "Member", targetOfflinePlayer.getUniqueId() );

        this.guildList.remove( player.getName() );

        Bukkit.getPluginManager().callEvent( new GuildDemotePlayerEvent( player, targetOfflinePlayer ) );

        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * 10L );
    }

    @Override
    public void kickPlayer( Player player, OfflinePlayer targetOfflinePlayer ) {
        final CacheUser user = CacheUser.getUser( player );

        final long time = Long.parseLong( this.fileBuilder.getKey( "guild.delete_guild.security_countdown" ) );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( this.guildUserAPI.playerExists( targetOfflinePlayer.getUniqueId() ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_kick.target_in_no_guild" ) );
            return;
        }

        if ( this.getGuildMaster( player ).equals( targetOfflinePlayer.getName() ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_kick.kick_himself" ) );
            return;
        }

        if ( !user.isMaster() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_not_the_guild_master" ) );
            return;
        }

        if ( !this.guildList.contains( player.getName() ) ) {
            this.guildList.add( player.getName() );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_kick.security_message_1" )
                    .replace( "{TARGET}", targetOfflinePlayer.getName() ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_kick.security_message_2" ) );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_kick.target_kick" )
                .replace( "{TARGET}", targetOfflinePlayer.getName() ) );

        this.guildUserAPI.deleteKey( targetOfflinePlayer.getUniqueId() );

        this.guildList.remove( player.getName() );

        Bukkit.getPluginManager().callEvent( new GuildKickPlayerEvent( player, targetOfflinePlayer ) );

        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * time );
    }

    @Override
    public void toggleChat( Player player ) {
        final CacheUser user = CacheUser.getUser( player );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( !this.guildMessageList.contains( player.getName() ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_chat.toggle_chat_active" ) );
            this.guildMessageList.add( player.getName() );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_chat.toggle_chat_inactive" ) );
        this.guildMessageList.remove( player.getName() );
    }

    @Override
    public void sendMessage( Player player, String... message ) {
        final List<String> masterList = this.guildUserAPI.getList(
                "guild_name", this.getGuildName( player ),
                "guild_rank", "Master", "player_name"
        );

        final List<String> officerList = this.guildUserAPI.getList(
                "guild_name", this.getGuildName( player ),
                "guild_rank", "Officer", "player_name"
        );

        final List<String> memberList = this.guildUserAPI.getList(
                "guild_name", this.getGuildName( player ),
                "guild_rank", "Member", "player_name"
        );

        if ( !masterList.contains( player.getName() ) ) masterList.add( String.valueOf( masterList ) );
        if ( !officerList.contains( player.getName() ) ) officerList.add( String.valueOf( officerList ) );
        if ( !memberList.contains( player.getName() ) ) memberList.add( String.valueOf( memberList ) );

        StringBuilder builder = new StringBuilder();
        for ( String output : message )
            builder.append( output );

        this.sendMessage( masterList, player, "guild.guild_chat.guild_chat_format.master", builder.toString() );
        this.sendMessage( officerList, player, "guild.guild_chat.guild_chat_format.officer", builder.toString() );
        this.sendMessage( memberList, player, "guild.guild_chat.guild_chat_format.member", builder.toString() );
    }

    @Override
    public void sendMembers( Player player ) {
        this.guildUserAPI.getListAsync(
                "guild_name", this.getGuildName( player ),
                "guild_rank", "Member", "player_name"
        ).thenAccept( result -> {
            player.sendMessage( this.fileBuilder.getKey( "guild.parameters.member.line1" ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.parameters.member.line2" ) );

            for ( String output : result )
                player.sendMessage( this.fileBuilder.getKey( "guild.parameters.member.line3" )
                                            .replace( "{MEMBERS}", output ) );
        } );
    }

    @Override
    public void sendMembers( Player player, String guildTag ) {
        this.guildUserAPI.getListAsync(
                "guild_tag", guildTag,
                "guild_rank", "Member", "player_name"
        ).thenAccept( result -> {
            player.sendMessage( this.fileBuilder.getKey( "guild.parameters.member.line1" ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.parameters.member.line2" ) );

            for ( String output : result )
                player.sendMessage( this.fileBuilder.getKey( "guild.parameters.member.line3" )
                                            .replace( "{MEMBERS}", output ) );
        } );
    }

    @Override
    public void sendOfficers( Player player ) {
        final List<String> officerList = this.guildUserAPI.getList(
                "guild_name", this.getGuildName( player ),
                "guild_rank", "Officer", "player_name"
        );

        player.sendMessage( this.fileBuilder.getKey( "guild.parameters.officer.line1" ) );
        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.parameters.officer.line2" ) );

        for ( String output : officerList )
            player.sendMessage( this.fileBuilder.getKey( "guild.parameters.officer.line3" )
                                        .replace( "{OFFICERS}", output ) );
    }

    @Override
    public void sendOfficers( Player player, String guildName ) {
        final List<String> officerList = this.guildUserAPI.getList(
                "guild_name", guildName,
                "guild_rank", "Officer", "player_name"
        );

        player.sendMessage( this.fileBuilder.getKey( "guild.parameters.officer.line1" ) );
        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.parameters.officer.line2" ) );

        for ( String output : officerList )
            player.sendMessage( this.fileBuilder.getKey( "guild.parameters.officer.line3" ) );
    }

    @Override
    public void sendGuildInfo( Player player ) {
        final CacheUser user = CacheUser.getUser( player );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        player.sendMessage( this.fileBuilder.getKey( "guild.send_guild_info.line1" )
                                    .replace( "{HEADER}", this.guildBuilder.sendHeaderString( player ) ) );

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.send_guild_info.line2" )
                .replace( "{TAG}", this.getGuildTag( player ) ) );

        player.sendMessage( this.fileBuilder.getKey( "guild.send_guild_info.line3" ) );
        player.sendMessage( this.fileBuilder.getKey( "guild.send_guild_info.line4" )
                                    .replace( "{MASTER}", this.getGuildMaster( player ) ) );

        this.sendOfficers( player );
        this.sendMembers( player );

        player.sendMessage( this.fileBuilder.getKey( "guild.send_guild_info.line7" )
                                    .replace( "{HEADER}", this.guildBuilder.sendHeaderString( player ) ) );
    }

    @Override
    public void sendGuildInfo( Player player, String guildTag ) {
        if ( !this.guildAPI.guildExists( "guild_tag", guildTag ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.send_guild_info.guild_does_not_exists" ) );
            return;
        }

        this.guildBuilder.sendHeader( player );

        this.guildBuilder.sendMessage( player, "§cGilden-Tag: " + guildTag );
        player.sendMessage( "" );
        this.guildBuilder.sendMessage( player, "§6Gilden Meister: " + this.getGuildMaster( player ) );
        this.sendOfficers( player, guildTag );
        this.sendMembers( player, guildTag );

        this.guildBuilder.sendHeader( player );
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
        return this.guildAPI.getGuildByStringSync( "guild_tag", this.getGuildTag( player ), "guild_leader" );
    }

    @Override
    public String getGuildName( OfflinePlayer player ) {
        return this.guildUserAPI.getKey( player.getUniqueId(), "guild_name" );
    }

    @Override
    public String getGuildTag( Player player ) {
        return this.guildUserAPI.getKey( player.getUniqueId(), "guild_tag" );
    }

    @Override
    public void sendGuildBank( Player player ) {

    }

    @Override
    public void addGuildBank( Player player, int amount ) {
        Bukkit.getPluginManager().callEvent( new GuildUpdateBankEvent( player, amount ) );
    }

    @Override
    public void removeGuildBank( Player player, int amount ) {
        Bukkit.getPluginManager().callEvent( new GuildUpdateBankEvent( player, amount ) );
    }

    @Override
    public boolean isGuildMaster( UUID playerUuid ) {
        return this.guildUserAPI.guildExists( playerUuid, "Master" );
    }

    @Override
    public boolean isGuildOfficer( UUID playerUuid ) {
        return this.guildUserAPI.guildExists( playerUuid, "Officer" );
    }

    @Override
    public boolean isGuildMember( UUID playerUuid ) {
        return this.guildUserAPI.guildExists( playerUuid, "Member" );
    }

    private void sendMessage( List<String> list, Player player, String key, String... message ) {
        for ( String stringList : list ) {
            if ( this.guildMessageList.contains( stringList ) ) {
                for ( Player players : Bukkit.getOnlinePlayers() ) {

                    StringBuilder builder = new StringBuilder();
                    for ( String output : message )
                        builder.append( output );

                    if ( stringList.contains( player.getName() ) )
                        this.guildBuilder.sendMessage( players, this.fileBuilder.getKey( key )
                                .replace( "{PLAYER}", player.getName() )
                                .replace( "{MESSAGE}", builder ) );
                }
            }
        }
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
