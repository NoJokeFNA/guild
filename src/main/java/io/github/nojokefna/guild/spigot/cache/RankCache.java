package io.github.nojokefna.guild.spigot.cache;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
@Getter
public class RankCache {

    private static final Map<String, RankCache> CACHE_MAP = new HashMap<>();
    private final String[] prefix, nameTag, suffix;
    private final String tagId;

    /**
     * Initialize all information
     *
     * @param group   Specify the {@code #group}, you want to create
     * @param prefix  Enter the {@code #prefix} for the speficied {@code #group}
     * @param nameTag Set the {@code #nameTag}, in equals the {@code #prefix} for the players the name
     * @param suffix  Enter the {@code #suffix}, that appears after the {@code #prefix}
     * @param tagId   Enter the {@code #tagId}, in which order the groups should be displayed
     */
    public RankCache( String group, String[] prefix, String[] nameTag, String[] suffix, String tagId ) {
        this.prefix = prefix;
        this.nameTag = nameTag;
        this.suffix = suffix;
        this.tagId = tagId;
        CACHE_MAP.put( group, this );
    }

    /**
     * Get the current rank
     *
     * @param key Enter the {@code #key} you want to get by a {@code #group}
     * @return returns the {@code #key} if it exists
     */
    public static RankCache getRank( String key ) {
        if ( CACHE_MAP.containsKey( key ) )
            return CACHE_MAP.get( key );
        return null;
    }
}
