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
<<<<<<< HEAD

=======
    
>>>>>>> 1f3825ce0c7cd4d46032a1af6c1d8fc9dfd4baf4
    public RankCache( String group, String[] prefix, String[] nameTag, String[] suffix, String tagId ) {
        this.prefix = prefix;
        this.nameTag = nameTag;
        this.suffix = suffix;
        this.tagId = tagId;
        CACHE_MAP.put( group, this );
    }

    public static RankCache getRank( String key ) {
        if ( CACHE_MAP.containsKey( key ) )
            return CACHE_MAP.get( key );

        return null;
    }
}
