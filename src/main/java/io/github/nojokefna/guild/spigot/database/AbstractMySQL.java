package io.github.nojokefna.guild.spigot.database;

import io.github.nojokefna.guild.spigot.Guild;

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
 * @version 2.5.4
 */
public abstract class AbstractMySQL {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool( 2 );

    private DatabaseProvider databaseProvider = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider();

    /**
     * Checks whether your {@code whereColumn} is present or not.
     * If the value is present, {@code true} will be returned, otherwise {@code false}
     *
     * @param table          Set the {@code table} that you want to use
     * @param whereColumn    Specify the value you want to query
     * @param setWhereColumn Set the {@code setWhereColumn} you want to set for the {@code whereColumn}
     *
     * @return returns {@code true} or {@code false}
     */
    public boolean keyExists( String table, String whereColumn, String setWhereColumn ) {
        if ( databaseProvider == null )
            this.databaseProvider = Guild.getPlugin().getDatabaseBuilder().getDatabaseProvider();

        if ( setWhereColumn == null )
            return false;

        boolean value = false;

        try ( PreparedStatement preparedStatement = this.databaseProvider.prepareStatement( "SELECT * FROM `" + table + "` WHERE `" + whereColumn + "` = ?" ) ) {

            preparedStatement.setString( 1, setWhereColumn );

            final ResultSet resultSet = preparedStatement.executeQuery();
            if ( resultSet.next() )
                value = true;

            resultSet.close();
        } catch ( SQLException ex ) {
            ex.printStackTrace();
        }

        return value;
    }

    /**
     * Update the desired {@code setKey} as a String.
     *
     * @param table          Set the {@code table} that you want to use
     * @param whereColumn    Enter the value you want to receive
     * @param setWhereColumn Set the {@code setWhereColumn} you want to set for the {@code whereColumn}
     * @param setColumn      Specify the value you want to update
     * @param setSetColumn   Set the {@code setSetColumn} you want to set for the {@code setColumn}
     */
    public void updateKey( String table, String whereColumn, String setWhereColumn, String setColumn, String setSetColumn ) {
        if ( !this.keyExists( table, whereColumn, setWhereColumn ) )
            return;

        EXECUTOR_SERVICE.execute( () -> {
            try {
                try ( PreparedStatement preparedStatement = this.databaseProvider.prepareStatement( "UPDATE `" + table + "` SET `" + setColumn + "` = ? WHERE `" + whereColumn + "` = ?" ) ) {

                    preparedStatement.setString( 1, setSetColumn );
                    preparedStatement.setString( 2, setWhereColumn );

                    preparedStatement.executeUpdate();
                }
            } catch ( SQLException ex ) {
                ex.printStackTrace();
            }
        } );
    }

    /**
     * Update the desired {@code setColumn} as a {@link Integer}.
     *
     * @param table          Set the {@code table} that you want to use
     * @param whereColumn    Enter the value you want to receive
     * @param setWhereColumn Set the {@code setWhereColumn} you want to set for the {@code whereColumn}
     * @param setColumn      Set the value you want to update
     * @param setSetColumn   Set the {@code setSetColumn} you want to set for the {@code setColumn}
     */
    public void updateKey( String table, String whereColumn, String setWhereColumn, String setColumn, int setSetColumn ) {
        if ( !this.keyExists( table, whereColumn, setWhereColumn ) )
            return;

        EXECUTOR_SERVICE.execute( () -> {
            try {
                try ( PreparedStatement preparedStatement = this.databaseProvider.prepareStatement( "UPDATE `" + table + "` SET `" + setColumn + "` = ? WHERE `" + whereColumn + "` = ?" ) ) {

                    preparedStatement.setInt( 1, setSetColumn );
                    preparedStatement.setString( 2, setWhereColumn );

                    preparedStatement.executeUpdate();
                }
            } catch ( SQLException ex ) {
                ex.printStackTrace();
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
        if ( getKey == null )
            throw new NullPointerException( "Value cannot be null" );

        if ( !this.keyExists( table, whereKey, setWhereKey ) )
            return null;

        String value = "";

        try {
            try ( PreparedStatement preparedStatement = this.databaseProvider.prepareStatement( "SELECT * FROM `" + table + "` WHERE `" + whereKey + "` = ?" ) ) {

                preparedStatement.setString( 1, setWhereKey );

                final ResultSet resultSet = preparedStatement.executeQuery();
                if ( resultSet.next() )
                    value = resultSet.getString( getKey );

                resultSet.close();
            }
        } catch ( SQLException ex ) {
            ex.printStackTrace();
        }
        return value;
    }

    /**
     * Get the {@code setKey} you want as an {@link Integer}.
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

        if ( !this.keyExists( table, whereKey, setWhereKey ) )
            return -1;

        try ( PreparedStatement preparedStatement = this.databaseProvider.prepareStatement( "SELECT * FROM `" + table + "` WHERE `" + whereKey + "` = ?" ) ) {

            preparedStatement.setString( 1, setWhereKey );

            final ResultSet resultSet = preparedStatement.executeQuery();
            if ( resultSet.next() )
                value = resultSet.getInt( getKey );

            preparedStatement.close();
            resultSet.close();
        } catch ( SQLException ex ) {
            ex.printStackTrace();
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
     * @return return the rank of your position from {@code orderKey}
     */
    public int getRanking( String table, String selectKey, String orderKey ) {
        Map<Integer, String> rank = new HashMap<>();
        int result = 0;

        try {
            try ( PreparedStatement preparedStatement = this.databaseProvider.prepareStatement( "SELECT ? FROM `" + table + "` ORDER BY ? DESC" ) ) {

                preparedStatement.setString( 1, selectKey );
                preparedStatement.setString( 2, table );
                preparedStatement.setString( 3, orderKey );

                final ResultSet resultSet = preparedStatement.executeQuery();
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
     * @return return the rank of your position from {@code orderKey}
     */
    public int getRanking( String table, String selectKey, String orderKey, int limit ) {
        final Map<Integer, String> rank = new HashMap<>();
        int result = 0;

        try {
            try ( PreparedStatement preparedStatement = this.databaseProvider.prepareStatement( "SELECT ? FROM `" + table + "` ORDER BY ? DESC LIMIT ?" ) ) {

                preparedStatement.setString( 1, selectKey );
                preparedStatement.setString( 2, table );
                preparedStatement.setString( 3, orderKey );
                preparedStatement.setInt( 4, limit );

                final ResultSet resultSet = preparedStatement.executeQuery();
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
        final List<String> getList = new ArrayList<>();

        try {
            try ( PreparedStatement preparedStatement = this.databaseProvider.prepareStatement( "SELECT * FROM `" + table + "` WHERE `" + whereKey + "` = ?" ) ) {

                preparedStatement.setString( 1, setWhereKey );

                final ResultSet resultSet = preparedStatement.executeQuery();
                while ( resultSet.next() )
                    getList.add( resultSet.getString( getKey ) );

                preparedStatement.close();
                resultSet.close();
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
        final List<String> getList = new ArrayList<>();

        try {
            try ( PreparedStatement preparedStatement = this.databaseProvider.prepareStatement( "SELECT * FROM `" + table + "` WHERE `" + whereKey + "` = ? AND `" + secondWhereKey + "` = ?" ) ) {

                preparedStatement.setString( 1, setWhereKey );
                preparedStatement.setString( 2, setSecondWhereKey );

                final ResultSet resultSet = preparedStatement.executeQuery();
                while ( resultSet.next() )
                    getList.add( resultSet.getString( getKey ) );

                preparedStatement.close();
                resultSet.close();
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
        if ( !this.keyExists( table, whereKey, setWhereKey ) )
            return;

        EXECUTOR_SERVICE.execute( () -> {
            try {
                try ( PreparedStatement preparedStatement = this.databaseProvider.prepareStatement( "DELETE FROM `" + table + "` WHERE `" + whereKey + "` = ?" ) ) {

                    preparedStatement.setString( 1, setWhereKey );

                    preparedStatement.executeUpdate();
                }
            } catch ( SQLException ex ) {
                ex.printStackTrace();
            }
        } );
    }

    /**
     * Add a value from {@code getKey} async
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Enter the value you want to receive from
     * @param setWhereKey Set the {@code setWhereKey} you want to set for the {@code whereKey}
     * @param getKey      Enter the value you want receive
     * @param setKey      Enter the {@code setKey} you want to remove
     *
     * @return the completable future
     */
    public CompletableFuture<Void> addKeyAsync( String table, String whereKey, String setWhereKey, String getKey, int setKey ) {
        int value = this.getKeyByInteger( table, whereKey, setWhereKey, getKey ) + setKey;
        return CompletableFuture.runAsync( () -> this.updateKey( table, whereKey, setWhereKey, getKey, value ) );
    }

    /**
     * Remove a value from {@code getKey} async
     *
     * @param table       Set the {@code table} that you want to use
     * @param whereKey    Enter the value you want to receive from
     * @param setWhereKey Set the {@code setWhereKey} you want to set for the {@code whereKey}
     * @param getKey      Enter the value you want receive
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