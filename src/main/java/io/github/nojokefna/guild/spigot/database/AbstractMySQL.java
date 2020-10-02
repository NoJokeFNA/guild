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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @author NoJokeFNA
 * @version 2.5.3
 */
public abstract class AbstractMySQL {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool( 2 );

    /**
     * Checks whether your {@code setWhereKey} is present or not.
     * <p>It then returns true if the value is present, or false if it is not present.</p>
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Specify the value you want to query
     * @param setWhereKey Set the {@code setWhereKey} you want to set for the {@code whereKey}
     *
     * @return returns true or false
     *
     * @see DatabaseProvider
     */
    public boolean keyExists( String table, String whereKey, String setWhereKey ) {
        boolean value = false;
        if ( setWhereKey == null )
            return false;

        try {
            try ( PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider().prepareStatement( "SELECT * FROM ? WHERE ? = ?" ) ) {

                preparedStatement.setString( 1, table );
                preparedStatement.setString( 2, whereKey );
                preparedStatement.setString( 3, setWhereKey );

                final ResultSet resultSet = preparedStatement.executeQuery();
                if ( resultSet.next() )
                    value = true;

                preparedStatement.close();
                resultSet.close();
            }
        } catch ( SQLException ex ) {
            ex.printStackTrace();
        }
        return value;
    }

    /**
     * Update the desired {@code setKey} as a String.
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Enter the value you want to receive
     * @param setWhereKey Set the {@code setWhereKey} you want to set for the {@code whereKey}
     * @param setKey      Specify the value you want to update
     * @param setSetKey   Set the {@code setSetKey} you want to set for the {@code setterKey}
     */
    public void updateKey( String table, String whereKey, String setWhereKey, String setKey, String setSetKey ) {
        EXECUTOR_SERVICE.execute( () -> {
            if ( this.keyExists( table, whereKey, setWhereKey ) ) {
                try {
                    try ( PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider().prepareStatement( "UPDATE ? SET ? = ? WHERE ? = ?" ) ) {

                        preparedStatement.setString( 1, table );
                        preparedStatement.setString( 2, setKey );
                        preparedStatement.setString( 3, setSetKey );
                        preparedStatement.setString( 4, whereKey );
                        preparedStatement.setString( 5, setWhereKey );

                        Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider().queryUpdate( preparedStatement );
                    }
                } catch ( SQLException ex ) {
                    ex.printStackTrace();
                }
            }
        } );
    }

    /**
     * Update the desired {@code setterKey} as a {@link java.lang.Integer}.
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Enter the value you want to receive
     * @param setWhereKey Set the {@code setWhereKey} you want to set for the {@code whereKey}
     * @param setKey      Set the value you want to update
     * @param setSetKey   Set the {@code setSetKey} you want to set for the {@code setKey}
     */
    public void updateKey( String table, String whereKey, String setWhereKey, String setKey, int setSetKey ) {
        EXECUTOR_SERVICE.execute( () -> {
            if ( this.keyExists( table, whereKey, setWhereKey ) ) {
                try {
                    try ( PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider().prepareStatement( "UPDATE ? SET ? = ? WHERE ? = ?" ) ) {

                        preparedStatement.setString( 1, table );
                        preparedStatement.setString( 2, setKey );
                        preparedStatement.setInt( 3, setSetKey );
                        preparedStatement.setString( 4, whereKey );
                        preparedStatement.setString( 5, setWhereKey );

                        Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider().queryUpdate( preparedStatement );
                    }
                } catch ( SQLException ex ) {
                    ex.printStackTrace();
                }
            }
        } );
    }

    /**
     * Get the {@code setKey} you want as an String.
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Enter the value you want to receive
     * @param setWhereKey Set the {@code setKey} you want to set for the {@code whereKey}
     * @param getKey      Set the {@code getKey} you want to get from {@code setKey}
     *
     * @return return {@code getKey}
     */
    public String getKey( String table, String whereKey, String setWhereKey, String getKey ) {
        String value = "";
        if ( getKey == null )
            throw new NullPointerException( "Value cannot be null" );

        if ( this.keyExists( table, whereKey, setWhereKey ) ) {
            try {
                try (
                        PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider()
                                .prepareStatement( "SELECT * FROM ? WHERE ? = ?" )
                ) {

                    preparedStatement.setString( 1, table );
                    preparedStatement.setString( 2, whereKey );
                    preparedStatement.setString( 3, setWhereKey );

                    ResultSet resultSet = preparedStatement.executeQuery();
                    if ( resultSet.next() )
                        value = resultSet.getString( getKey );

                    preparedStatement.close();
                    resultSet.close();
                }
            } catch ( SQLException ex ) {
                ex.printStackTrace();
            }
        }
        return value;
    }

    /**
     * Get the {@code setKey} you want as an {@link java.lang.Integer}.
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Enter the value you want to receive
     * @param setWhereKey Set the {@code setKey} you want to set for the {@code whereKey}
     * @param getKey      Set the {@code getKey} you want to get from {@code setKey}
     *
     * @return return {@code getKey}
     */
    public int getKeyByInteger( String table, String whereKey, String setWhereKey, String getKey ) {
        int value = 0;

        if ( this.keyExists( table, whereKey, setWhereKey ) ) {
            try {
                try (
                        PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider()
                                .prepareStatement( "SELECT * FROM ? WHERE ? = ?" )
                ) {

                    preparedStatement.setString( 1, table );
                    preparedStatement.setString( 2, whereKey );
                    preparedStatement.setString( 3, setWhereKey );

                    ResultSet resultSet = preparedStatement.executeQuery();
                    if ( resultSet.next() )
                        value = resultSet.getInt( getKey );

                    preparedStatement.close();
                    resultSet.close();
                }
            } catch ( SQLException ex ) {
                ex.printStackTrace();
            }
        }
        return value;
    }

    /**
     * Get the ranking of your current value.
     *
     * @param table     Set the {@code table} you want to use
     * @param selectKey Enter the key you want to select
     * @param orderKey  Set the {@code orderKey} you want to get
     *
     * @return return the rank of your position from {@code orderkey}
     */
    public int getRanking( String table, String selectKey, String orderKey ) {
        Map<Integer, String> rank = new HashMap<>();
        int result = 0;

        try {
            try (
                    PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider()
                            .prepareStatement( "SELECT ? FROM ? ORDER BY ? DESC" )
            ) {

                preparedStatement.setString( 1, selectKey );
                preparedStatement.setString( 2, table );
                preparedStatement.setString( 3, orderKey );

                ResultSet resultSet = preparedStatement.executeQuery();
                while ( resultSet.next() ) {
                    result++;
                    rank.put( result, resultSet.getString( selectKey ) );
                }

                preparedStatement.close();
                resultSet.close();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return Integer.parseInt( rank.get( result ) );
    }

    /**
     * Get the ranking of your current value.
     *
     * @param table     Set the {@code table} you want to use
     * @param selectKey Enter the key you want to select
     * @param orderKey  Set the {@code orderKey} you want to get
     * @param limit     Set the {@code limit} you want to get
     *
     * @return return the rank of your position from {@code orderkey}
     */
    public int getRanking( String table, String selectKey, String orderKey, int limit ) {
        Map<Integer, String> rank = new HashMap<>();
        int result = 0;

        try {
            try (
                    PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider()
                            .prepareStatement( "SELECT ? FROM ? ORDER BY ? DESC LIMIT ?" )
            ) {

                preparedStatement.setString( 1, selectKey );
                preparedStatement.setString( 2, table );
                preparedStatement.setString( 3, orderKey );
                preparedStatement.setInt( 4, limit );

                ResultSet resultSet = preparedStatement.executeQuery();
                while ( resultSet.next() ) {
                    result++;
                    rank.put( result, resultSet.getString( selectKey ) );
                }

                preparedStatement.close();
                resultSet.close();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return Integer.parseInt( rank.get( result ) );
    }

    /**
     * Get a {@link java.util.List} from your values.
     *
     * @param table       Set the {@code table} you want to use
     * @param whereKey    Enter the value you want to receive
     * @param setWhereKey Set the {@code setWhereKey} you want to set for the {@code whereKey}
     * @param getKey      Enter the value you wanna finally receive
     *
     * @return returns the current {@link java.util.List}
     */
    public List<String> getList( String table, String whereKey, String setWhereKey, String getKey ) {
        List<String> getList = new ArrayList<>();

        try {
            try (
                    PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider()
                            .prepareStatement( "SELECT * FROM ? WHERE ? = ?" )
            ) {

                preparedStatement.setString( 1, table );
                preparedStatement.setString( 2, whereKey );
                preparedStatement.setString( 3, setWhereKey );

                ResultSet resultSet = preparedStatement.executeQuery();
                while ( resultSet.next() )
                    getList.add( resultSet.getString( getKey ) );

                preparedStatement.close();
                resultSet.close();

                Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), getList::clear, 40L );
            }
        } catch ( SQLException ex ) {
            ex.printStackTrace();
        }
        return getList;
    }

    /**
     * Get a {@link java.util.List} from your values.
     *
     * @param table             Set the {@code table} you want to use
     * @param whereKey          Enter the value you want to receive
     * @param setWhereKey       Set the {@code setWhereKey} you want to set for the {@code whereKey}
     * @param secondWhereKey    Enter the second value you want to receive
     * @param setSecondWhereKey Set the {@code setSecondWhereKey} you want to set for the {@code whereKey}
     * @param getKey            Enter the value you wanna finally receive
     *
     * @return returns the current {@link java.util.List}
     */
    public List<String> getList( String table, String whereKey, String setWhereKey, String secondWhereKey, String setSecondWhereKey, String getKey ) {
        List<String> getList = new ArrayList<>();

        try {
            try (
                    PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider()
                            .prepareStatement( "SELECT * FROM ? WHERE ? = ? AND ? = ?" )
            ) {

                preparedStatement.setString( 1, table );
                preparedStatement.setString( 2, whereKey );
                preparedStatement.setString( 3, setWhereKey );
                preparedStatement.setString( 4, secondWhereKey );
                preparedStatement.setString( 5, setSecondWhereKey );

                ResultSet resultSet = preparedStatement.executeQuery();
                while ( resultSet.next() )
                    getList.add( resultSet.getString( getKey ) );

                preparedStatement.close();
                resultSet.close();

                Bukkit.getServer().getScheduler().runTaskLater( Guild.getPlugin(), getList::clear, 40L );
            }
        } catch ( SQLException ex ) {
            ex.printStackTrace();
        }
        return getList;
    }

    /**
     * Delete the desired entry.
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Enter the value you want to receive
     * @param setWhereKey Set the {@code setKey} you want to set for the {@code whereKey}
     */
    public void deleteKey( String table, String whereKey, String setWhereKey ) {
        EXECUTOR_SERVICE.execute( () -> {
            if ( this.keyExists( table, whereKey, setWhereKey ) ) {
                try {
                    try (
                            PreparedStatement preparedStatement = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider()
                                    .prepareStatement( "DELETE FROM ? WHERE ? = ?" )
                    ) {

                        preparedStatement.setString( 1, table );
                        preparedStatement.setString( 2, whereKey );
                        preparedStatement.setString( 3, setWhereKey );

                        Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider().queryUpdate( preparedStatement );
                    }
                } catch ( SQLException ex ) {
                    ex.printStackTrace();
                }
            }
        } );
    }

    /**
     * Add a value from {@code getKey} async
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Enter the value you want to receive from
     * @param setWhereKey Set the {@code setWhereKey} you want to set for the {@code whereKey}
     * @param getKey      Enter the value you want recerive
     * @param setKey      Enter the {@code setKey} you want to remove
     *
     * @return the completable future
     */
    public CompletableFuture<Void> addKeyAsync( String table, String whereKey, String setWhereKey, String getKey, int setKey ) {
        int value = this.getKeyByInteger( table, whereKey, setWhereKey, getKey ) - setKey;
        return CompletableFuture.runAsync( () -> this.updateKey( table, whereKey, setWhereKey, getKey, value ) );
    }

    /**
     * Remove a value from {@code getKey} async
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Enter the value you want to receive from
     * @param setWhereKey Set the {@code setWhereKey} you want to set for the {@code whereKey}
     * @param getKey      Enter the value you want recerive
     * @param setKey      Enter the {@code setKey} you want to remove
     *
     * @return the completable future
     */
    public CompletableFuture<Void> removeKeyAsync( String table, String whereKey, String setWhereKey, String getKey, int setKey ) {
        int value = this.getKeyByInteger( table, whereKey, setWhereKey, getKey ) - setKey;
        return CompletableFuture.runAsync( () -> this.updateKey( table, whereKey, setWhereKey, getKey, value ) );
    }

    /**
     * Get the {@code getKey} you want async, without a {@code completableFeature}.
     * <p>
     * To use this methode correctly, I really recommend to use:
     * {@code getStringAsync( String table, String whereKey, String setKey, String getKey ).thenAccept( result -> { Your code } )};
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Enter the value you want to receive
     * @param setWhereKey Set the {@code setKey} you want to set for the {@code whereKey}
     * @param getKey      Set the {@code getKey} you want to get from {@code setKey}
     *
     * @return the string async
     */
    public CompletableFuture<String> getStringAsync( String table, String whereKey, String setWhereKey, String getKey ) {
        return CompletableFuture.supplyAsync( () -> this.getKey( table, whereKey, setWhereKey, getKey ) );
    }

    /**
     * Get the {@code getKey} you want async, without a {@code callback}
     * <p>
     * To use this methode correctly, I really recommend to use:
     * {@code getIntegerAsync( String table, String whereKey, String setKey, String getKey ).thenAccept( result -> { Your code } )};
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Enter the value you want to receive
     * @param setWhereKey Set the {@code setKey} you want to set for the {@code whereKey}
     * @param getKey      Set the {@code getKey} you want to get from {@code setKey}
     *
     * @return the integer async
     */
    public CompletableFuture<Integer> getIntegerAsync( String table, String whereKey, String setWhereKey, String getKey ) {
        return CompletableFuture.supplyAsync( () -> this.getKeyByInteger( table, whereKey, setWhereKey, getKey ) );
    }

    /**
     * Get the {@code getKey} you want async, without a {@code callback}
     * <p>
     * To use this methode correctly, I really recommend to use:
     * {@code getListAsync( String table, String whereKey, String setKey, String getKey ).thenAccept( result -> { Your code } )};
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Enter the value you want to receive
     * @param setWhereKey Set the {@code setKey} you want to set for the {@code whereKey}
     * @param getKey      Set the {@code getKey} you want to get from {@code setKey}
     *
     * @return the list async
     */
    public CompletableFuture<List<String>> getListAsync( String table, String whereKey, String setWhereKey, String getKey ) {
        return CompletableFuture.supplyAsync( () -> this.getList( table, whereKey, setWhereKey, getKey ) );
    }

    /**
     * Get the {@code getKey} you want async, without a {@code callback}
     * <p>
     * To use this methode correctly, I really recommend to use:
     * {@code getListAsync( String table, String whereKey, String setKey, String getKey ).thenAccept( result -> { Your code } )};
     *
     * @param table             Set the {@code table} you want to use
     * @param whereKey          Enter the value you want to receive
     * @param setWhereKey       Set the {@code setWhereKey} you want to set for the {@code whereKey}
     * @param secondWhereKey    Enter the second value you want to receive
     * @param setSecondWhereKey Set the {@code setSecondWhereKey} you want to set for the {@code whereKey}
     * @param getKey            Enter the value you wanna finally receive
     *
     * @return returns the current {@link java.util.List}
     */
    public CompletableFuture<List<String>> getListAsync( String table, String whereKey, String setWhereKey, String secondWhereKey, String setSecondWhereKey, String getKey ) {
        return CompletableFuture.supplyAsync( () -> this.getList( table, whereKey, setWhereKey, secondWhereKey, setSecondWhereKey, getKey ) );
    }

    /**
     * Add a value from {@code getKey}
     *
     * @param table       Set the {@code table} that you want to use
     * @param getKey      Enter the value you want recerive
     * @param setKey      Enter the {@code setKey} you want to remove
     * @param whereKey    Enter the value you want to receive from
     * @param setWhereKey Set the {@code setWhereKey} you want to set for the {@code whereKey}
     */
    public void addKey( String table, String whereKey, String setWhereKey, String getKey, int setKey ) {
        int value = this.getKeyByInteger( table, whereKey, setWhereKey, getKey ) + setKey;
        this.updateKey( table, whereKey, setWhereKey, getKey, value );
    }

    /**
     * Remove a value from {@code getKey}
     *
     * @param table       Set the {@code table} that you want to use
     * @param getKey      Enter the value you want recerive
     * @param setKey      Enter the {@code setKey} you want to remove
     * @param whereKey    Enter the value you want to receive from
     * @param setWhereKey Set the {@code setWhereKey} you want to set for the {@code whereKey}
     */
    public void removeKey( String table, String whereKey, String setWhereKey, String getKey, int setKey ) {
        int value = this.getKeyByInteger( table, whereKey, setWhereKey, getKey ) - setKey;
        this.updateKey( table, whereKey, setWhereKey, getKey, value );
    }

    /**
     * Get the {@code getKey} you want async
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Enter the value you want to receive
     * @param setWhereKey Set the {@code setKey} you want to set for the {@code whereKey}
     * @param getKey      Set the {@code getKey} you want to get from {@code setKey}
     * @param callback    Create the {@code callback}
     */
    public void getDataStringAsync( String table, String whereKey, String setWhereKey, String getKey, Consumer<String> callback ) {
        CompletableFuture.runAsync( () -> callback.accept( this.getKey( table, whereKey, setWhereKey, getKey ) ) );
    }

    /**
     * Get the {@code setKey} you want asnyc
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Enter the value you want to receive
     * @param setWhereKey Set the {@code setKey} you want to set for the {@code whereKey}
     * @param getKey      Set the {@code getKey} you want to get from {@code setKey}
     * @param callback    Create the {@code callback}
     */
    public void getDataIntegerAsync( String table, String whereKey, String setWhereKey, String getKey, Consumer<Integer> callback ) {
        CompletableFuture.runAsync( () -> callback.accept( this.getKeyByInteger( table, whereKey, setWhereKey, getKey ) ) );
    }

    /**
     * Get the {@code setKey} you want async
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Enter the value you want to receive
     * @param setWhereKey Set the {@code setWhereKey} you want to set for the {@code whereKey}
     * @param getKey      Set the {@code getKey} you want to get from {@code setWhereKey}
     * @param callback    Create the {@code callback}
     */
    public void getDataListAsync( String table, String whereKey, String setWhereKey, String getKey, Consumer<List<String>> callback ) {
        CompletableFuture.runAsync( () -> callback.accept( this.getList( table, whereKey, setWhereKey, getKey ) ) );
    }
}