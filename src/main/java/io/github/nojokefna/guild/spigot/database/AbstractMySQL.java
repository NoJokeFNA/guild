package io.github.nojokefna.guild.spigot.database;

import io.github.nojokefna.guild.spigot.Guild;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public abstract class AbstractMySQL {

    private int modifiers;

    /**
     * Checks whether your {@code #setKey} is present or not.
     * <p>It then returns true if the value is present, or false if it is not present.</p>
     *
     * @param table       Set the {@code #table} that you want to use
     * @param whereKey    Specify the value you want to query
     * @param setWhereKey Set the {@code #setKey} you want to set for the {@code #whereKey}
     * @return returns true or false
     */
    public boolean keyExists( String table, String whereKey, String setWhereKey ) {
        boolean value = false;
        if ( setWhereKey == null )
            value = false;

        try {
            PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder()
                    .getDatabase()
                    .prepareStatement( "SELECT * FROM `" + table + "` WHERE " + whereKey + " = ?" );

            preparedStatement.setString( 1, setWhereKey );

            ResultSet resultSet = preparedStatement.executeQuery();
            if ( resultSet.next() )
                value = true;

            resultSet.close();
            preparedStatement.close();
        } catch ( SQLException ex ) {
            ex.printStackTrace();
        }
        return value;
    }

    /**
     * Update the desired {@code #setKey} as a String.
     *
     * @param table       Set the table that you want to use
     * @param setKey      Specify the value you want to update
     * @param setSetKey   Set the {@code #setSetKey} you want to set for the {@code #setterKey}
     * @param whereKey    Enter the value you want to receive
     * @param setWhereKey Set the {@code #setWhereKey} you want to set for the {@code #whereKey}
     */
    public void updateKey( String table, String setKey, String setSetKey, String whereKey, String setWhereKey ) {
        Guild.getPlugin().getExecutorService().execute( () -> {
            if ( this.keyExists( table, whereKey, setWhereKey ) ) {
                try {
                    PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder()
                            .getDatabase()
                            .prepareStatement( "UPDATE `" + table + "` SET " + setKey + " = ? WHERE " + whereKey + " = ?" );

                    preparedStatement.setString( 1, setSetKey );
                    preparedStatement.setString( 2, setWhereKey );

                    Guild.getPlugin().getDatabaseBuilder().getDatabase().queryUpdate( preparedStatement );
                } catch ( SQLException ex ) {
                    ex.printStackTrace();
                }
            }
        } );
    }

    /**
     * Update the desired {@code #setterKey} as a {@link java.lang.Integer}.
     *
     * @param table       Set the table that you want to use
     * @param setterKey   Set the value you want to update
     * @param setSetKey   Set the {@code #setSetKey} you want to set for the {@code #setterKey}
     * @param whereKey    Enter the value you want to receive
     * @param setWhereKey Set the {@code #setWhereKey} you want to set for the {@code #whereKey}
     */
    public void updateKey( String table, String setterKey, int setSetKey, String whereKey, String setWhereKey ) {
        Guild.getPlugin().getExecutorService().execute( () -> {
            if ( this.keyExists( table, whereKey, setWhereKey ) ) {
                try {
                    PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder()
                            .getDatabase()
                            .prepareStatement( "UPDATE `" + table + "` SET " + setterKey + " = ? WHERE " + whereKey + " = ?" );

                    preparedStatement.setInt( 1, setSetKey );
                    preparedStatement.setString( 2, setWhereKey );

                    Guild.getPlugin().getDatabaseBuilder().getDatabase().queryUpdate( preparedStatement );
                } catch ( SQLException ex ) {
                    ex.printStackTrace();
                }
            }
        } );
    }

    /**
     * Get the {@code #setKey} you want as an String.
     *
     * @param table    Set the {@code #table} that you want to use
     * @param whereKey Enter the value you want to receive
     * @param setKey   Set the {@code #setKey} you want to set for the {@code #whereKey}
     * @param getKey   Set the {@code #getKey} you want to get from {@code #setKey}
     * @return return {@code #getKey}
     */
    public String getKey( String table, String whereKey, String setKey, String getKey ) {
        String value = "";
        if ( getKey == null )
            throw new NullPointerException( "Value cannot be null" );

        if ( this.keyExists( table, whereKey, setKey ) ) {
            try {
                PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder()
                        .getDatabase()
                        .prepareStatement( "SELECT * FROM `" + table + "` WHERE " + whereKey + " = ?" );

                preparedStatement.setString( 1, setKey );

                ResultSet resultSet = preparedStatement.executeQuery();
                if ( resultSet.next() )
                    value = resultSet.getString( getKey );

                resultSet.close();
                preparedStatement.close();
            } catch ( SQLException ex ) {
                ex.printStackTrace();
            }
        }
        return value;
    }

    /**
     * Get the {@code #setKey} you want as an {@link java.lang.Integer}.
     *
     * @param table    Set the {@code #table} that you want to use
     * @param whereKey Enter the value you want to receive
     * @param setKey   Set the {@code #setKey} you want to set for the {@code #whereKey}
     * @param getKey   Set the {@code #getKey} you want to get from {@code #setKey}
     * @return return {@code #getKey}
     */
    public int getKeyByInteger( String table, String whereKey, String setKey, String getKey ) {
        int value = 0;
        if ( this.keyExists( table, whereKey, setKey ) ) {
            try {
                PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder()
                        .getDatabase()
                        .prepareStatement( "SELECT * FROM `" + table + "` WHERE " + whereKey + " = ?" );

                preparedStatement.setString( 1, setKey );

                ResultSet resultSet = preparedStatement.executeQuery();
                if ( resultSet.next() )
                    value = resultSet.getInt( getKey );

                resultSet.close();
                preparedStatement.close();
            } catch ( SQLException ex ) {
                ex.printStackTrace();
            }
        }
        return value;
    }

    /**
     * Get the ranking of your current value.
     *
     * @param selectKey Enter the key you want to select
     * @param table     Set the {@code #table} you want to use
     * @param orderKey  Set the {@code #orderKey} you want to get
     * @return return the rank of your position from {@code #orderkey}
     */
    public int getRanking( String selectKey, String table, String orderKey ) {
        Map<Integer, String> rank = new HashMap<>();
        int result = 0;

        try {
            PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder()
                    .getDatabase()
                    .prepareStatement( "SELECT " + selectKey + " FROM `" + table + "` ORDER BY " + orderKey + " DESC" );

            ResultSet resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() ) {
                result++;
                rank.put( result, resultSet.getString( selectKey ) );
            }

            resultSet.close();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return Integer.parseInt( rank.get( result ) );
    }

    /**
     * Get the ranking of your current value.
     *
     * @param selectKey Enter the key you want to select
     * @param table     Set the {@code #table} you want to use
     * @param orderKey  Set the {@code #orderKey} you want to get
     * @param limit     Set the {@code #limit} you want to get
     * @return return the rank of your position from {@code #orderkey}
     */
    public int getRanking( String selectKey, String table, String orderKey, int limit ) {
        Map<Integer, String> rank = new HashMap<>();
        int result = 0;

        try {
            PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder()
                    .getDatabase()
                    .prepareStatement( "SELECT " + selectKey + " FROM `" + table + "` ORDER BY " + orderKey + " DESC LIMIT " + limit );

            ResultSet resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() ) {
                result++;
                rank.put( result, resultSet.getString( selectKey ) );
            }

            resultSet.close();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return Integer.parseInt( rank.get( result ) );
    }

    /**
     * Get a {@link java.util.List} from your values.
     *
     * @param table       Set the {@code #table} you want to use
     * @param whereKey    Enter the value you want to receive
     * @param setWhereKey Set the {@code #setWhereKey} you want to set for the {@code #whereKey}
     * @param getKey      Enter the value you wanna finally receive
     * @return returns the current {@link java.util.List}
     */
    public List<String> getList( String table, String whereKey, String setWhereKey, String getKey ) {
        List<String> getList = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder()
                    .getDatabase()
                    .prepareStatement( "SELECT * FROM `" + table + "` WHERE " + whereKey + " = ?" );

            preparedStatement.setString( 1, setWhereKey );

            ResultSet resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() )
                getList.add( resultSet.getString( getKey ) );

            resultSet.close();
            preparedStatement.close();

            Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), getList::clear, 40L );
        } catch ( SQLException ex ) {
            ex.printStackTrace();
        }
        return getList;
    }

    /**
     * Get a {@link java.util.List} from your values.
     *
     * @param table             Set the {@code #table} you want to use
     * @param whereKey          Enter the value you want to receive
     * @param setWhereKey       Set the {@code #setWhereKey} you want to set for the {@code #whereKey}
     * @param secondWhereKey    Enter the second value you want to receive
     * @param setSecondWhereKey Set the {@code #setSecondWhereKey} you want to set for the {@code #whereKey}
     * @param getKey            Enter the value you wanna finally receive
     * @return returns the current {@link java.util.List}
     */
    public List<String> getList( String table, String whereKey, String setWhereKey, String secondWhereKey, String setSecondWhereKey, String getKey ) {
        List<String> getList = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder()
                    .getDatabase()
                    .prepareStatement( "SELECT * FROM `" + table + "` WHERE " + whereKey + " = ? AND " + secondWhereKey + " = ?" );

            preparedStatement.setString( 1, setWhereKey );
            preparedStatement.setString( 2, setSecondWhereKey );

            ResultSet resultSet = preparedStatement.executeQuery();
            while ( resultSet.next() )
                getList.add( resultSet.getString( getKey ) );

            resultSet.close();
            preparedStatement.close();

            Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), getList::clear, 40L );
        } catch ( SQLException ex ) {
            ex.printStackTrace();
        }
        return getList;
    }

    /**
     * Delete the desired entry.
     *
     * @param table    Set the {@code #table} that you want to use
     * @param whereKey Enter the value you want to receive
     * @param setKey   Set the {@code #setKey} you want to set for the {@code #whereKey}
     */
    public void deleteKey( String table, String whereKey, String setKey ) {
        Guild.getPlugin().getExecutorService().execute( () -> {
            if ( this.keyExists( table, whereKey, setKey ) ) {
                try {
                    PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder()
                            .getDatabase()
                            .prepareStatement( "DELETE FROM `" + table + "` WHERE " + whereKey + " = ?" );

                    preparedStatement.setString( 1, setKey );

                    Guild.getPlugin().getDatabaseBuilder().getDatabase().queryUpdate( preparedStatement );
                } catch ( SQLException ex ) {
                    ex.printStackTrace();
                }
            }
        } );
    }

    /**
     * Add a value from {@code #getKey}
     *
     * @param table       Set the {@code #table} that you want to use
     * @param getKey      Enter the value you want recerive
     * @param setKey      Enter the {@code #setKey} you want to remove
     * @param whereKey    Enter the value you want to receive from
     * @param setWhereKey Set the {@code #setWhereKey} you want to set for the {@code #whereKey}
     */
    public void addKey( String table, String getKey, int setKey, String whereKey, String setWhereKey ) {
        int value = this.getKeyByInteger( table, whereKey, setWhereKey, getKey ) + setKey;
        this.updateKey( table, getKey, value, whereKey, setWhereKey );
    }

    /**
     * Remove a value from {@code #getKey}
     *
     * @param table       Set the {@code #table} that you want to use
     * @param getKey      Enter the value you want recerive
     * @param setKey      Enter the {@code #setKey} you want to remove
     * @param whereKey    Enter the value you want to receive from
     * @param setWhereKey Set the {@code #setWhereKey} you want to set for the {@code #whereKey}
     */
    public void removeKey( String table, String getKey, int setKey, String whereKey, String setWhereKey ) {
        int value = this.getKeyByInteger( table, whereKey, setWhereKey, getKey ) - setKey;
        this.updateKey( table, getKey, value, whereKey, setWhereKey );
    }

    /**
     * Get the {@code #getKey} you want async.
     *
     * @param table    Set the {@code #table} that you want to use
     * @param whereKey Enter the value you want to receive
     * @param setKey   Set the {@code #setKey} you want to set for the {@code #whereKey}
     * @param getKey   Set the {@code #getKey} you want to get from {@code #setKey}
     * @param callback Create the {@code callback}
     */
    public void getDataStringAsync( String table, String whereKey, String setKey, String getKey, Consumer<String> callback ) {
        Guild.getPlugin().getExecutorService().execute( () -> callback.accept( this.getKey( table, whereKey, setKey, getKey ) ) );
    }

    /**
     * Get the {@code #setKey} you want asnyc.
     *
     * @param table    Set the {@code #table} that you want to use
     * @param whereKey Enter the value you want to receive
     * @param setKey   Set the {@code #setKey} you want to set for the {@code #whereKey}
     * @param getKey   Set the {@code #getKey} you want to get from {@code #setKey}
     * @param callback Create the {@code callback}
     */
    public void getDataIntegerAsync( String table, String whereKey, String setKey, String getKey, Consumer<Integer> callback ) {
        Guild.getPlugin().getExecutorService().execute( () -> callback.accept( this.getKeyByInteger( table, whereKey, setKey, getKey ) ) );
    }

    /**
     * Get the {@code #setKey} you want async.
     *
     * @param table    Set the {@code #table} that you want to use
     * @param whereKey Enter the value you want to receive
     * @param setKey   Set the {@code #setKey} you want to set for the {@code #whereKey}
     * @param getKey   Set the {@code #getKey} you want to get from {@code #setKey}
     * @param callback Create the {@code callback}
     */
    public void getDataListAsync( String table, String whereKey, String setKey, String getKey, Consumer<List<String>> callback ) {
        Guild.getPlugin().getExecutorService().execute( () -> callback.accept( this.getList( table, whereKey, setKey, getKey ) ) );
    }
}