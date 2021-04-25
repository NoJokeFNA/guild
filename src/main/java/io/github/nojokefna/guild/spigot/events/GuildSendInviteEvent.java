package io.github.nojokefna.guild.spigot.events;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class GuildSendInviteEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player, targetPlayer;

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
