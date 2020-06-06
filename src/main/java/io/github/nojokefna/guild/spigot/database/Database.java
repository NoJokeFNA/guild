package io.github.nojokefna.guild.spigot.database;

import io.github.nojokefna.guild.spigot.Guild;

import java.sql.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class Database {

    private final Guild plugin;

    private final String username, password, hostname, database;
    private final int port;

    private Connection connection;

    public Database( String username, String password, String hostname, int port, String database, Guild plugin ) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.plugin = plugin;
    }

    public void connect() {
        try {
            if ( this.connection != null && ! this.connection.isClosed() ) return;

            synchronized ( this ) {
                Class.forName( "com.mysql.jdbc.Driver" );
                this.setConnection( DriverManager.getConnection( "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?autoReconnect=true",
                        username, password ) );
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

    public void update( String preparedStatement ) {
        this.plugin.getExecutorService().execute( () -> this.queryUpdate( preparedStatement ) );
    }

    public void query( PreparedStatement preparedStatement, Consumer<ResultSet> consumer ) {
        this.plugin.getExecutorService().execute( () -> {
            ResultSet result = this.query( preparedStatement );
            consumer.accept( result );
        } );
    }

    public void query( String preparedStatement, Consumer<ResultSet> consumer ) {
        this.plugin.getExecutorService().execute( () -> {
            ResultSet result = this.query( preparedStatement );
            consumer.accept( result );
        } );
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

    public ResultSet queryUpdate( PreparedStatement preparedStatement ) {
        this.checkConnection();

        Future<ResultSet> resultSetFuture = this.plugin.getExecutorService().submit( () -> this.query( preparedStatement ) );
        try {
            return resultSetFuture.get();
        } catch ( InterruptedException | ExecutionException ex ) {
            ex.printStackTrace();
        }

        try {
            preparedStatement.executeUpdate();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        } finally {
            try {
                preparedStatement.close();
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public ResultSet query( String query ) {
        this.checkConnection();

        try {
            return query( this.connection.prepareStatement( query ) );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return null;
    }

    public ResultSet query( PreparedStatement preparedStatement ) {
        this.checkConnection();

        try {
            preparedStatement.close();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return null;
    }

    public void checkConnection() {
        try {
            if ( this.connection == null || ! this.connection.isValid( 10 ) || this.connection.isClosed() ) this.connect();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    public boolean isConnected() {
        return this.connection != null;
    }

    public void setConnection( Connection connection ) {
        this.connection = connection;
    }
}
