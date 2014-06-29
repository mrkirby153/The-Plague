package me.mrkirby153.plugins.ThePlague.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {

    /**
     * The name of the super-command to be a child of
     *
     * @return The name of the super-command
     */
    String superCommand();

    /**
     * The name of the sub-command.
     *
     * @return The name of the sub-command.
     */
    String commandName();

    /**
     * Any aliases the sub-command has
     *
     * @return An array of aliases for the command
     */
    String[] aliases() default {};

    /**
     * The permission required to execute this command
     *
     * @return
     */
    String subPermission();

    /**
     * A short description for this sub-command.
     *
     * @return
     */
    String description() default "&4There is no description for this sub-command!";
}
