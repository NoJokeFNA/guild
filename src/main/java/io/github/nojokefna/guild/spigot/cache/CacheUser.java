package io.github.nojokefna.guild.spigot.cache;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
@Getter
@Setter
public class CacheUser {

    private static final Map<UUID, CacheUser> USER_MAP = new HashMap<>();

    // init
    private final CacheMethods cacheMethods;
    
    // settings
    private final Map<UUID, Long> commandCoolDown, messageCoolDown;
    private final Map<UUID, String> message;
    private String[] helpMessage;
    private String headerMessage;

    // startup
    private AtomicBoolean loaded;

    // tabList
    private String[] prefix, nameTag, suffix;
    private String tagId;

    // guild
    private boolean inGuild;
    private boolean isMember, isOfficer, isMaster;

    public CacheUser() {
        this.cacheMethods = new CacheMethods();
        this.commandCoolDown = new HashMap<>();
        this.messageCoolDown = new HashMap<>();
        this.message = new HashMap<>();
    }

    public static CacheUser getUser( Player player ) {
        return getUserByUuid( player.getUniqueId() );
    }

    public static CacheUser getUserByUuid( UUID uuid ) {
        if ( ! USER_MAP.containsKey( uuid ) )
            USER_MAP.put( uuid, new CacheUser() );

        return USER_MAP.get( uuid );
    }

    public static void deleteUser( Player player ) {
        USER_MAP.remove( player.getUniqueId() );
    }
}