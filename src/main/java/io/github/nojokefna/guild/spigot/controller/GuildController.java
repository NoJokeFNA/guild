package io.github.nojokefna.guild.spigot.controller;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.build.GuildBuilder;
import io.github.nojokefna.guild.spigot.cache.CacheUser;
import io.github.nojokefna.guild.spigot.config.FileBuilder;
import io.github.nojokefna.guild.spigot.database.api.GuildAPI;
import io.github.nojokefna.guild.spigot.database.api.GuildInvitesAPI;
import io.github.nojokefna.guild.spigot.database.api.GuildUserAPI;
import io.github.nojokefna.guild.spigot.interfaces.GuildInterface;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class GuildController implements GuildInterface {

    @Getter
    private final List<String> guildList, guildMessageList;

    private final GuildAPI guildAPI;
    private final FileBuilder fileBuilder;
    private final GuildUserAPI guildUserAPI;
    private final GuildInvitesAPI guildInvitesAPI;
    private final GuildBuilder guildBuilder;

    public GuildController() {
        this.guildList = new ArrayList<>();
        this.guildMessageList = new ArrayList<>();
        this.fileBuilder = Guild.getPlugin().getFileBuilder();
        this.guildAPI = Guild.getPlugin().getGuildAPI();
        this.guildUserAPI = Guild.getPlugin().getGuildUserAPI();
        this.guildInvitesAPI = Guild.getPlugin().getGuildInvitesAPI();
        this.guildBuilder = Guild.getPlugin().getGuildBuilder();
    }

    @Override
    public void createGuild( Player player, String guildName, String guildTag, String leader ) {
        CacheUser user = CacheUser.getUser( player );

        int costs = Integer.parseInt( this.fileBuilder.getKey( "guild.create_guild.costs" ) );
        long time = Long.parseLong( this.fileBuilder.getKey( "guild.delete_guild.security_countdown" ) );

        if ( this.isInteger( guildName ) || this.isInteger( guildTag ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.create_guild.no_letters" ) );
            return;
        }

        if ( user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_guild" ) );
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
        this.guildAPI.createGuild( guildName, guildTag, leader );
        this.guildUserAPI.createPlayer( player.getUniqueId(), player.getName(), guildName, guildTag, "Master" );
        this.guildList.remove( player.getName() );

        user.setInGuild( true );

        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * time );
    }

    @Override
    public void deleteGuild( Player player ) {
        CacheUser user = CacheUser.getUser( player );

        long time = Long.parseLong( this.fileBuilder.getKey( "guild.delete_guild.security_countdown" ) );

        if ( !this.guildAPI.guildExists( "guild_name", this.sendGuildName( player ) ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( !user.isMaster() ) {
            this.guildBuilder.sendMessage( player, fileBuilder.getKey( "guild.is_not_the_guild_master" ) );
            return;
        }

        user.setInGuild( false );

        if ( !this.guildList.contains( player.getName() ) ) {
            this.guildList.add( player.getName() );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.delete_guild.security_message_1" )
                    .replace( "{GUILD}", this.sendGuildName( player ) ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.delete_guild.security_message_2" ) );
            return;
        }

        for ( String uuid : this.guildUserAPI.getList( "guild_name", this.sendGuildName( player ), "player_uuid" ) )
            this.guildUserAPI.deleteKey( UUID.fromString( uuid ) );

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.delete_guild.successfully_deleted" ) );
        this.guildAPI.deleteGuild( this.sendGuildName( player ) );

        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * time );
    }

    @Override
    public void sendInvite( Player player, Player target ) {
        CacheUser user = CacheUser.getUser( player );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( target == null ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.target_is_offline" ) );
            return;
        }

        CacheUser targetUser = CacheUser.getUser( target );

        if ( targetUser.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.target_is_in_guild" ) );
            return;
        }

        if ( this.guildInvitesAPI.keyExists( "guild_name", this.sendGuildName( player ) ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.already_invite_target" ) );
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

        this.guildInvitesAPI.createPlayer( target.getUniqueId(), target, player.getName(), this.sendGuildName( player ), this.sendGuildTag( player ) );

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_invites.invited_target" )
                .replace( "{TARGET}", target.getName() ) );

        Bukkit.getScheduler().runTaskLater( Guild.getPlugin(), ()
                -> this.guildBuilder.sendMessage( target, this.fileBuilder.getKey( "guild.guild_invites.invited_target_message" )
                .replace( "{PLAYER}", player.getName() )
                .replace( "{GUILD}", this.getInvitedGuild( target ) ) ), 5L
        );
    }

    @Override
    public void sendInvites( Player player ) {
        List<String> inviteList = this.guildInvitesAPI.getInvites( player, "invited_name" );
        List<String> inviteNameList = this.guildInvitesAPI.getInvites( player, "guild_name" );
        List<String> inviteTagList = this.guildInvitesAPI.getInvites( player, "guild_tag" );

        this.guildBuilder.sendHeader( player );
        this.guildBuilder.sendMessage( player, "§aEinladungen:" );

        for ( int i = 0; i < inviteList.size(); i++ ) {
            player.sendMessage( "" );
            this.guildBuilder.sendMessage( player, "§aEingeladen von: §6" + inviteList.get( i ) );
            this.guildBuilder.sendMessage( player, "§aGilden Name: §6" + inviteNameList.get( i ) );
            this.guildBuilder.sendMessage( player, "§aGilden Tag: §6" + inviteTagList.get( i ) );
        }

        this.guildBuilder.sendHeader( player );
    }

    @Override
    public void revokeInvite( Player player, OfflinePlayer target ) {
        CacheUser user = CacheUser.getUser( player );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( !this.guildInvitesAPI.keyExists( "player_name", target.getName() ) ) {
            this.guildBuilder.sendMessage( player, "§cDer Angegebene Spieler wurde nicht eingeladen." );
            return;
        }

        if ( !user.isMaster() ) {
            this.guildBuilder.sendMessage( player, fileBuilder.getKey( "guild.is_not_the_guild_master" ) );
            return;
        }

        this.guildBuilder.sendMessage( player, "§aDie Einladung wurde zurückgezogen." );
        this.guildInvitesAPI.deleteInvite( target );
    }

    @Override
    public void acceptInvite( Player player ) {
        CacheUser user = CacheUser.getUser( player );

        if ( !this.guildInvitesAPI.keyExists( "player_name", player.getName() ) ) {
            this.guildBuilder.sendMessage( player, "§cDu hast keine Einladungen erhalten." );
            return;
        }

        if ( user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, "§cDu musst deine Gilde verlassen, um eine andere Gilde zu betreten." );
            return;
        }

        this.guildBuilder.sendMessage( player, "§aDu bist erfolgreich der Gilde §6" + this.getInvitedGuild( player ) + "§abeigetreten." );

        user.setInGuild( true );

        this.guildUserAPI.createPlayer( player.getUniqueId(), player.getName(), this.getInvitedGuild( player ), this.getInvitedGuildTag( player ), "Member" );
        this.guildInvitesAPI.deleteInvite( player );
    }

    @Override
    public void denyInvite( Player player ) {
        if ( this.guildInvitesAPI.keyExists( "player_name", player.getName() ) ) {
            this.guildBuilder.sendMessage( player, "§cDu hast keine Einladungen erhalten." );
            return;
        }

        this.guildBuilder.sendMessage( player, "§aDu hast die Einladung von der Gilde §6" + this.getInvitedGuild( player ) + "§agelöscht." );
        this.guildInvitesAPI.deleteInvite( player );
    }

    @Override
    public String getInvitedGuild( OfflinePlayer player ) {
        return this.guildInvitesAPI.getInvite( player, "guild_name" );
    }

    @Override
    public String getInvitedGuildTag( OfflinePlayer player ) {
        return this.guildInvitesAPI.getInvite( player, "guild_tag" );
    }

    @Override
    public void promotePlayer( Player player, OfflinePlayer target ) {
        CacheUser user = CacheUser.getUser( player );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( !user.isMaster() ) {
            this.guildBuilder.sendMessage( player, fileBuilder.getKey( "guild.is_not_the_guild_master" ) );
            return;
        }

        if ( user.isOfficer() ) {
            this.guildBuilder.sendMessage( player, "§cDer Angegebene Spieler ist bereits Offizier." );
            return;
        }

        if ( !this.guildList.contains( player.getName() ) ) {
            this.guildList.add( player.getName() );
            this.guildBuilder.sendMessage( player, "§aBist du dir Sicher, §6" + target.getName() + "§aals Gilden Offizier zu setzen?" );
            this.guildBuilder.sendMessage( player, "§aDu hast 10 Sekunden Zeit, um den Befehl erneut auszuführen." );
            return;
        }

        this.guildBuilder.sendMessage( player, "§aDu hast §6" + target.getName() + "§aals neuen Gilden Offizier gesetzt." );

        this.guildUserAPI.updateKey( "guild_rank", "Officer", target.getUniqueId() );

        this.guildList.remove( player.getName() );
        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * 10L );
    }

    @Override
    public void demotePlayer( Player player, OfflinePlayer target ) {
        CacheUser user = CacheUser.getUser( player );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( !user.isMaster() ) {
            this.guildBuilder.sendMessage( player, fileBuilder.getKey( "guild.is_not_the_guild_master" ) );
            return;
        }

        if ( user.isMember() ) {
            this.guildBuilder.sendMessage( player, "§cDer Angegebene Spieler ist bereits Mitglied." );
            return;
        }

        if ( !this.guildList.contains( player.getName() ) ) {
            this.guildList.add( player.getName() );
            this.guildBuilder.sendMessage( player, "§aBist du dir Sicher, §6" + target.getName() + "§aals Gilden Mitglied zu setzen?" );
            this.guildBuilder.sendMessage( player, "§aDu hast 10 Sekunden Zeit, um den Befehl erneut auszuführen." );
            return;
        }

        this.guildBuilder.sendMessage( player, "§aDu hast §6" + target.getName() + "§aals neuen Gilden Mitglied gesetzt." );

        this.guildUserAPI.updateKey( "guild_rank", "Member", target.getUniqueId() );

        this.guildList.remove( player.getName() );
        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * 10L );
    }

    @Override
    public void kickPlayer( Player player, OfflinePlayer target ) {
        CacheUser user = CacheUser.getUser( player );

        long time = Long.parseLong( this.fileBuilder.getKey( "guild.delete_guild.security_countdown" ) );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( this.guildUserAPI.keyExists( target.getUniqueId() ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_kick.target_in_no_guild" ) );
            return;
        }

        if ( this.sendGuildMaster( player ).equals( target.getName() ) ) {
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
                    .replace( "{TARGET}", target.getName() ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_kick.security_message_2" ) );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.guild_kick.target_kick" )
                .replace( "{TARGET}", target.getName() ) );

        this.guildUserAPI.deleteKey( target.getUniqueId() );

        this.guildList.remove( player.getName() );

        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * time );
    }

    @Override
    public void toggleChat( Player player ) {
        CacheUser user = CacheUser.getUser( player );

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
        List<String> masterList = this.guildUserAPI.getList(
                "guild_name", this.sendGuildName( player ),
                "guild_rank", "Master", "player_name"
        );

        List<String> officerList = this.guildUserAPI.getList(
                "guild_name", this.sendGuildName( player ),
                "guild_rank", "Officer", "player_name"
        );

        List<String> memberList = this.guildUserAPI.getList(
                "guild_name", this.sendGuildName( player ),
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

    private void sendMessage( List<String> list, Player player, String key, String... message ) {
        for ( String string : list ) {
            if ( this.guildMessageList.contains( string ) ) {
                for ( Player players : Bukkit.getOnlinePlayers() ) {
                    StringBuilder builder = new StringBuilder();
                    for ( String output : message )
                        builder.append( output );

                    if ( string.contains( player.getName() ) )
                        this.guildBuilder.sendMessage( players, this.fileBuilder.getKey( key )
                                .replace( "{PLAYER}", player.getName() )
                                .replace( "{MESSAGE}", builder ) );
                }
            }
        }
    }

    @Override
    public void setGuildMaster( Player player, OfflinePlayer target ) {
        CacheUser user = CacheUser.getUser( player );

        long time = Long.parseLong( this.fileBuilder.getKey( "guild.set_guild_master.security_countdown" ) );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( !user.isMaster() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_not_the_guild_master" ) );
            return;
        }

        if ( this.guildUserAPI.keyExists( target.getUniqueId() ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.set_guild_master.target_is_in_no_guild" ) );
            return;
        }

        if ( !this.guildList.contains( player.getName() ) ) {
            this.guildList.add( player.getName() );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.set_guild_master.security_message_1" )
                    .replace( "{TARGET}", target.getName() ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.set_guild_master.security_message_2" ) );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.set_guild_master.set_master" ) );

        this.guildAPI.updateGuild( "guild_leader", target.getName(), this.sendGuildName( player ) );
        this.guildUserAPI.updateKey( "guild_rank", "Officer", player.getUniqueId() );
        this.guildUserAPI.updateKey( "guild_rank", "Master", target.getUniqueId() );

        this.guildList.remove( player.getName() );
        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * time );
    }

    @Override
    public String sendGuildMaster( Player player ) {
        return this.guildAPI.getGuild( "guild_tag", this.sendGuildTag( player ), "guild_leader" );
    }

    @Override
    public void leaveGuild( Player player ) {
        CacheUser user = CacheUser.getUser( player );

        long time = Long.parseLong( this.fileBuilder.getKey( "guild.leave_guild.security_countdown" ) );

        if ( !this.guildAPI.guildExists( "guild_name", this.sendGuildName( player ) ) ) {
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
                    .replace( "{GUILD}", this.sendGuildName( player ) ) );
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.leave_guild.security_message_2" ) );
            return;
        }

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.leave_guild.leaved" ) );

        this.guildUserAPI.deleteKey( player.getUniqueId() );

        Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), () -> this.guildList.remove( player.getName() ), 20L * time );
    }

    @Override
    public void sendMembers( Player player ) {
        List<String> memberList = this.guildUserAPI.getList(
                "guild_name", this.sendGuildName( player ),
                "guild_rank", "Member", "player_name"
        );

        player.sendMessage( this.fileBuilder.getKey( "guild.parameters.member.line1" ) );

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.parameters.member.line2" ) );

        for ( String output : memberList )
            player.sendMessage( this.fileBuilder.getKey( "guild.parameters.member.line3" ) );
    }

    @Override
    public String sendMembersString( Player player ) {
        List<String> memberList = this.guildUserAPI.getList(
                "guild_name", this.sendGuildName( player ),
                "guild_rank", "Member", "player_name"
        );

        player.sendMessage( this.fileBuilder.getKey( "guild.parameters.member.line1" ) );

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.parameters.member.line2" ) );

        for ( String output : memberList )
            player.sendMessage( this.fileBuilder.getKey( "guild.parameters.member.line3" ) );
        return "";
    }

    @Override
    public void sendMembers( Player player, String guild ) {
        List<String> memberList = this.guildUserAPI.getList( "guild_tag", guild,
                                                             "guild_rank", "Member", "player_name"
        );

        player.sendMessage( this.fileBuilder.getKey( "guild.parameters.member.line1" ) );
        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.parameters.member.line2" ) );

        for ( String output : memberList )
            player.sendMessage( this.fileBuilder.getKey( "guild.parameters.member.line3" ) );
    }

    @Override
    public void sendOfficers( Player player ) {
        List<String> officerList = this.guildUserAPI.getList(
                "guild_name", this.sendGuildName( player ),
                "guild_rank", "Officer", "player_name"
        );

        player.sendMessage( this.fileBuilder.getKey( "guild.parameters.officer.line1" ) );
        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.parameters.officer.line2" ) );

        for ( String output : officerList )
            player.sendMessage( this.fileBuilder.getKey( "guild.parameters.officer.line3" ) );
    }

    @Override
    public String sendOfficersString( Player player ) {
        List<String> officerList = this.guildUserAPI.getList(
                "guild_name", this.sendGuildName( player ),
                "guild_rank", "Officer", "player_name"
        );

        player.sendMessage( this.fileBuilder.getKey( "guild.parameters.officer.line1" ) );
        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.parameters.officer.line2" ) );

        for ( String output : officerList )
            player.sendMessage( this.fileBuilder.getKey( "guild.parameters.officer.line3" ) );
        return "";
    }

    @Override
    public void sendOfficers( Player player, String guild ) {
        List<String> officerList = this.guildUserAPI.getList(
                "guild_name", guild,
                "guild_rank", "Officer", "player_name"
        );

        player.sendMessage( this.fileBuilder.getKey( "guild.parameters.officer.line1" ) );
        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.parameters.officer.line2" ) );

        for ( String output : officerList )
            player.sendMessage( this.fileBuilder.getKey( "guild.parameters.officer.line3" ) );
    }

    @Override
    public void sendGuildInfo( Player player, String guild ) {
        if ( !this.guildAPI.guildExists( "guild_tag", guild ) ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild." ) );
            return;
        }

        this.guildBuilder.sendHeader( player );

        this.guildBuilder.sendMessage( player, "§cGilden-Tag: " + guild );
        player.sendMessage( "" );
        this.guildBuilder.sendMessage( player, "§6Gilden Meister: " + this.sendGuildMaster( player ) );
        this.sendOfficers( player, guild );
        this.sendMembers( player, guild );

        this.guildBuilder.sendHeader( player );
    }

    @Override
    public void sendGuildList( Player player ) {
        CacheUser user = CacheUser.getUser( player );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        player.sendMessage( this.fileBuilder.getKey( "guild.send_guild_info.line1" )
                                    .replace( "{HEADER}", this.guildBuilder.sendHeaderString( player ) ) );

        this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.send_guild_info.line2" )
                .replace( "{HEADER}", this.sendGuildTag( player ) ) );

        player.sendMessage( this.fileBuilder.getKey( "guild.send_guild_info.line3" ) );
        player.sendMessage( this.fileBuilder.getKey( "guild.send_guild_info.line4" )
                                    .replace( "{MASTER}", this.sendGuildMaster( player ) ) );

        this.sendOfficers( player );
        this.sendMembers( player );

        this.guildBuilder.sendHeader( player );
    }

    @Override
    public String sendGuildName( OfflinePlayer player ) {
        return this.guildUserAPI.getKey( player.getUniqueId(), "guild_name" );
    }

    @Override
    public String sendGuildName( UUID playerUuid ) {
        return this.guildUserAPI.getKey( playerUuid, "guild_name" );
    }

    @Override
    public String sendGuildRank( UUID playerUuid ) {
        return this.guildUserAPI.getKey( playerUuid, "guild_rank" );
    }

    @Override
    public String sendGuildTag( Player player ) {
        return this.guildUserAPI.getKey( player.getUniqueId(), "guild_tag" );
    }

    @Override
    public void sendGuildBank( Player player ) {
        CacheUser user = CacheUser.getUser( player );

        if ( user.isInGuild() )
            this.guildBuilder.sendMessage( player, "§aDeine Gilde hat aktuell einen Kontostand von §b$"
                    + this.guildAPI.getGuildByInteger( "guild_name", this.sendGuildName( player ), "guild_money" ) + "§a." );
        else
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
    }

    @Override
    public void addGuildBank( Player player, int amount ) {
        CacheUser user = CacheUser.getUser( player );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        try {
            if ( !( Guild.getPlugin().getEconomy().getBalance( player ) >= amount ) ) {
                this.guildBuilder.sendMessage( player, "§cDu hast keine §b$" + amount + "§c." );
                return;
            }

            this.guildUserAPI.addKey( "guild_payed_money", amount, player.getUniqueId() );
            this.guildAPI.addKey( this.sendGuildName( player ), amount );

            Guild.getPlugin().getEconomy().withdrawPlayer( player, amount );

            this.guildBuilder.sendMessage( player, "§aDu hast §b$" + amount + " §aauf die Gilden Bank eingezahlt." );
        } catch ( NumberFormatException ex ) {
            this.guildBuilder.sendMessage( player, "§cBitte verwende nur gültige Zeichen." );
        }
    }

    @Override
    public void removeGuildBank( Player player, int amount ) {
        final CacheUser user = CacheUser.getUser( player );

        if ( !user.isInGuild() ) {
            this.guildBuilder.sendMessage( player, this.fileBuilder.getKey( "guild.is_in_no_guild" ) );
            return;
        }

        if ( !user.isMaster() ) {
            this.guildBuilder.sendMessage( player, fileBuilder.getKey( "guild.is_not_the_guild_master" ) );
            return;
        }

        try {
            if ( !( this.guildAPI.getGuildByInteger( "guild_name", this.sendGuildName( player ), "guild_money" ) >= amount ) ) {
                this.guildBuilder.sendMessage( player, "§cAuf der Gilde sind keine §b$" + amount + " §ceingezahlt." );
                return;
            }

            this.guildUserAPI.addKey( "guild_take_money", amount, player.getUniqueId() );
            this.guildAPI.removeKey( this.sendGuildName( player ), amount );

            Guild.getPlugin().getEconomy().depositPlayer( player, amount );

            this.guildBuilder.sendMessage( player, "§aDu hast §b$" + amount + " §avon der Gilden Bank genommen." );
        } catch ( NumberFormatException ex ) {
            this.guildBuilder.sendMessage( player, "§cBitte verwende nur gültige Zeichen." );
        }
    }

    @Override
    public boolean isGuildMaster( UUID uuid ) {
        return this.guildUserAPI.guildExists( uuid, "Master" );
    }

    @Override
    public boolean isGuildOfficer( UUID uuid ) {
        return this.guildUserAPI.guildExists( uuid, "Officer" );
    }

    @Override
    public boolean isGuildMember( UUID uuid ) {
        return this.guildUserAPI.guildExists( uuid, "Member" );
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