package me.mrkirby153.plugins.ThePlague.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * The name of the command.
     *
     * @return The Command's name
     */
    String name();

    /**
     * Any aliases of the commmand
     *
     * @return The Command's Aliases (if any)
     */
    String[] aliases() default {};

    /**
     * Who can execute this command
     * 0: Only player
     * 1: Only Console
     * 2: Console or player.
     *
     * @return Who is allowed to execute this command
     */
    int executeLevel() default 0;

    /**
     * The permission required to execute this command
     *
     * @return The permission required.
     */
    String permission();


    /**
     * A short description for this command. Used only for the help menu
     *
     * @return The Command's description
     */
    String description() default "&4There is no description for this command!";

    /**
     * If there are children commnads for this command.
     *
     * @return True if there are children commands.
     */
    boolean hasChildren() default false;
}
