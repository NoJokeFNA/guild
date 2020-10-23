package io.github.nojokefna.guild.spigot.database;

import io.github.nojokefna.guild.spigot.Guild;
import lombok.Setter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class DatabaseProvider {

    private final Guild plugin;

    private final String username, password, hostname, database;
    private final int port;

    @Setter
    private Connection connection;

    public DatabaseProvider( String username, String password, String hostname, int port, String database, Guild plugin ) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.plugin = plugin;
    }

    public void connect() {
        try {
            if ( this.connection != null && !this.connection.isClosed() ) return;

            synchronized ( this ) {
                Class.forName( "com.mysql.jdbc.Driver" );
                this.setConnection( DriverManager.getConnection( "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?autoReconnect=true", username, password ) );
            }
        } catch ( SQLException | ClassNotFoundException ex ) {
            ex.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            this.connection.close();
        } catch ( SQLException ex ) {
            ex.printStackTrace();
        }
    }

    public void update( PreparedStatement preparedStatement ) {
        this.plugin.getExecutorService().execute( () -> this.queryUpdate( preparedStatement ) );
    }

    public void update( String query ) {
        this.plugin.getExecutorService().execute( () -> this.queryUpdate( query ) );
    }

    public PreparedStatement prepareStatement( String query ) {
        try {
            return this.connection.prepareStatement( query );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return null;
    }

    public void queryUpdate( String query ) {
        this.checkConnection();

        try ( PreparedStatement preparedStatement = this.connection.prepareStatement( query ) ) {
            this.queryUpdate( preparedStatement );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    public void queryUpdate( PreparedStatement preparedStatement ) {
        this.checkConnection();

        try {
            final CompletableFuture<Integer> completableFuture = CompletableFuture.completedFuture( preparedStatement.executeUpdate() );

            if ( completableFuture.isDone() )
                preparedStatement.close();
        } catch ( SQLException ex ) {
            ex.printStackTrace();
        }
    }

    public void checkConnection() {
        try {
            if ( this.connection == null || !this.connection.isValid( 10 ) || this.connection.isClosed() )
                this.connect();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }
}
