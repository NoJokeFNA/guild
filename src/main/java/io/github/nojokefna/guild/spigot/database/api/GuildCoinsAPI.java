package io.github.nojokefna.guild.spigot.database.api;

import io.github.nojokefna.guild.spigot.Guild;
import io.github.nojokefna.guild.spigot.database.AbstractMySQL;
import io.github.nojokefna.guild.spigot.database.DatabaseProvider;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GuildCoinsAPI extends AbstractMySQL {

    private final DatabaseProvider databaseProvider;

    public GuildCoinsAPI() {
        this.databaseProvider = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider();
        super.setTable( "guild_coins" );
    }

    public boolean playerExists( UUID playerUuid ) {
        return super.keyExists( "player_uuid", playerUuid.toString() );
    }

    public void createPlayer( UUID playerUuid, int coinsValue ) {
        if ( this.playerExists( playerUuid ) ) return;

        try {
            try ( PreparedStatement preparedStatement = this.databaseProvider.prepareStatement( "INSERT INTO `guild_coins` (player_uuid, player_coins) VALUES (?, ?)" ) ) {

                preparedStatement.setString( 1, playerUuid.toString() );
                preparedStatement.setInt( 2, coinsValue );

                preparedStatement.executeUpdate();
            }

            System.out.println( "Successfully created a new player ( " + playerUuid.toString() + ", " + coinsValue + " )" );
        } catch ( SQLException ex ) {
            System.out.println( "Something went wrong while creating a new player ( " + playerUuid.toString() + ", " + coinsValue + " )" );
            ex.printStackTrace();
        }
    }

    public void updateCoins( UUID playerUuid, int coinsValue ) {
        super.updateKey( "player_uuid", playerUuid.toString(), "player_coins", coinsValue );
    }

    public CompletableFuture<Void> addCoinsAsync( UUID playerUuid, int coinsValue ) {
        return super.addKeyAsync( "player_uuid", playerUuid.toString(), "player_coins", coinsValue );
    }

    public CompletableFuture<Void> removeCoinsAsync( UUID playerUuid, int coinsValue ) {
        return super.removeKeyAsync( "player_uuid", playerUuid.toString(), "player_coins", coinsValue );
    }

    public CompletableFuture<Integer> getCoinsAsync( UUID playerUuid ) {
        return super.getIntegerAsync( "player_uuid", playerUuid.toString(), "player_coins" );
    }

    public int getCoinsSync( UUID playerUuid ) {
        return super.getKeyByInteger( "player_uuid", playerUuid.toString(), "player_coins" );
    }
}