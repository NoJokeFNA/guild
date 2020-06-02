package io.github.nojokefna.guild.spigot.database;

import io.github.nojokefna.guild.spigot.Guild;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.function.Consumer;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class Database {

    private final Guild plugin;

    private final String username;
    private final String password;
    private final String hostname;
    private final String database;

    private Connection connection;

    public Database( String username, String password, String hostname, String database, Guild plugin ) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.database = database;
        this.plugin = plugin;
    }

    public void connect() {
        try {
            synchronized ( this ) {
                if ( getConnection() != null && !getConnection().isClosed() ) return;

                Class.forName( "com.mysql.jdbc.Driver" );
                setConnection( DriverManager.getConnection( "jdbc:mysql://" + hostname + ":" + "3306" + "/" + database + "?autoReconnect=true", username, password ) );
                Bukkit.getServer().getLogger().info( this.plugin.getData().getPrefix() + "§bEine Verbindung wurde zur Datenbank aufgebaut." );
            }
        } catch ( SQLException | ClassNotFoundException e ) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            connection.close();
        } catch ( SQLException ex ) {
            ex.printStackTrace();
        }
    }

    public void update( PreparedStatement statement ) {
        this.plugin.getExecutorService().execute( () -> this.queryUpdate( statement ) );
    }

    public void update( String statement ) {
        this.plugin.getExecutorService().execute( () -> this.queryUpdate( statement ) );
    }

    public void query( PreparedStatement statement, Consumer<ResultSet> consumer ) {
        this.plugin.getExecutorService().execute( () -> {
            ResultSet result = this.query( statement );
            consumer.accept( result );
        } );
    }

    public void query( String statement, Consumer<ResultSet> consumer ) {
        this.plugin.getExecutorService().execute( () -> {
            ResultSet result = this.query( statement );
            consumer.accept( result );
        } );
    }

    public PreparedStatement prepare( String query ) {
        try {
            return this.connection.prepareStatement( query );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public void queryUpdate( String query ) {
        checkConnection();
        try ( PreparedStatement statement = this.connection.prepareStatement( query ) ) {
            queryUpdate( statement );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void queryUpdate( PreparedStatement preparedStatement ) {
        checkConnection();
        try {
            preparedStatement.executeUpdate();
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                preparedStatement.close();
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    public ResultSet query( String query ) {
        checkConnection();
        try {
            return query( this.connection.prepareStatement( query ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet query( PreparedStatement statement ) {
        checkConnection();
        try {
            return statement.executeQuery();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public void checkConnection() {
        try {
            if ( this.connection == null || !this.connection.isValid( 10 ) || this.connection.isClosed() ) connect();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection( Connection connection ) {
        this.connection = connection;
    }
}
