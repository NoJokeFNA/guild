package io.github.nojokefna.guild.spigot.build;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * @author NoJokeFNA
 * @version 1.0.0
 */
public class ScoreboardBuilder {

    private final Scoreboard scoreboard;
    private final Objective objective;
    private Team team;

    private final Player player;

    /**
     * Initialize the class
     *
     * @param objectiveName Set the {@code #objective} name you want
     * @param displaySlot   Set the {@code #displaySlot} you want to use
     * @param displayName   Set the {@code #displayName} of the {@code #scoreboard}
     * @param player        Initialize the {@code #player}
     */
    public ScoreboardBuilder( String objectiveName, DisplaySlot displaySlot, String displayName, Player player ) {
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = this.scoreboard.registerNewObjective( objectiveName, "dummy" );

        this.objective.setDisplaySlot( displaySlot );
        this.objective.setDisplayName( coloredMessage( displayName ) );
    }

    /**
     * Add a team to the {@code #scoreboard}
     *
     * @param teamName Set the {@code #teamName} you want
     * @param prefix   Set the {@code #prefix} of the {@code #team}
     * @param entry    Set the {@code #entry} of the {@code #scoreboard}. Please use only §1, §2 (...)
     * @param score    Set the {@code #score} in the following order
     * @return returns the method
     */
    public ScoreboardBuilder addTeam( String teamName, String prefix, String entry, int score ) {
        this.team = this.scoreboard.registerNewTeam( teamName );
        this.team.setPrefix( coloredMessage( prefix ) );
        this.team.addEntry( entry );
        this.objective.getScore( entry ).setScore( score );
        return this;
    }

    /**
     * Add a team to the {@code #scoreboard}
     *
     * @param teamName Set the {@code #teamName} you want
     * @param prefix   Set the {@code #prefix} of the {@code #team}
     * @param suffix   Set the {@code #suffix} of the {@code #team}
     * @param entry    Set the {@code #entry} of the {@code #scoreboard}. Please use only §1, §2, §3 (...)
     * @param score    Set the {@code #score} in the following order
     * @return returns the method
     */
    public ScoreboardBuilder addTeam( String teamName, String prefix, String suffix, String entry, int score ) {
        this.team = this.scoreboard.registerNewTeam( teamName );
        this.team.setPrefix( coloredMessage( prefix ) );
        this.team.setSuffix( coloredMessage( suffix ) );
        this.team.addEntry( entry );
        this.objective.getScore( entry ).setScore( score );
        return this;
    }

    public ScoreboardBuilder setDisplayName( String displayName ) {
        this.team.setDisplayName( displayName );
        return this;
    }

    /**
     * Remove the {@code #entry} from the specified {@code #team}
     *
     * @param player Set the {@code #player} from whom you want to get the {@code #scoreboard}
     * @param team   Specify the {@code #team} from which you want to remove the {@code #entry}
     * @param entry  Set the {@code #entry} from the {@code #team}
     */
    public static void removeEntry( Player player, String team, String entry ) {
        player.getScoreboard().getTeam( team ).removeEntry( entry );
    }

    /**
     * Update a team with a prefix
     *
     * @param player Set the {@code #player} from whom you want to update the {@code #scoreboard}
     * @param team   Set the {@code #team} you want to update
     * @param prefix Set the {@code #prefix} from the {@code #team}
     */
    public static void updateTeam( Player player, String team, String prefix ) {
        player.getScoreboard().getTeam( team ).setPrefix( staticColoredMessage( prefix ) );
    }

    /**
     * Update a team with a prefix & suffix
     *
     * @param player Set the {@code #player} from whom you want to update the {@code #scoreboard}
     * @param team   Set the {@code #team} you want to update
     * @param prefix Set the {@code #prefix} from the {@code #team}
     * @param suffix Set the {@code #suffix} from the {@code #team}
     */
    public static void updateTeam( Player player, String team, String prefix, String suffix ) {
        player.getScoreboard().getTeam( team ).setPrefix( staticColoredMessage( prefix ) );
        player.getScoreboard().getTeam( team ).setSuffix( staticColoredMessage( suffix ) );
    }

    /**
     * Finally send the scoreboard
     */
    public void sendScoreboard() {
        this.player.setScoreboard( this.scoreboard );
    }

    private String coloredMessage( String message ) {
        return ChatColor.translateAlternateColorCodes( '&', message );
    }

    private static String staticColoredMessage( String message ) {
        return ChatColor.translateAlternateColorCodes( '&', message );
    }
}
