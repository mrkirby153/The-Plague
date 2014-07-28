package me.mrkirby153.plugins.ThePlague.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commands {

    /** A list of all valid command handlers */
    public static ArrayList<Class> handlers = new ArrayList<Class>();

    /**
     * Registers a new class as a handler
     * @param cmdClass The class to register
     */
    public static void registerNewHandler(Class cmdClass) {
        handlers.add(cmdClass);
    }

    /**
     * Gets the method that is executed when the given command is executed
     * @param commandName The command name
     * @return The given command's method
     */
    public static Method getExecutorMethod(String commandName){
        for(Class c : handlers){
            for(Method m : c.getMethods()){
                if(m.isAnnotationPresent(Command.class)){
                    Command cmd = m.getAnnotation(Command.class);
                    if(cmd.name().equalsIgnoreCase(commandName) || isAlias(cmd.aliases(), commandName))
                        return m;
                }
            }
        }
        return null;
    }

    /**
     * Gets the class the give command is in
     * @param commandName The command name
     * @return The class the given command is in
     */
    public static Class getExecutorClass(String commandName){
        for(Class c : handlers){
            for(Method m : c.getMethods()){
                if(m.isAnnotationPresent(Command.class)){
                    Command cmd = m.getAnnotation(Command.class);
                    if(cmd.name().equalsIgnoreCase(commandName) || isAlias(cmd.aliases(), commandName))
                        return c;
                }
            }
        }
        return null;
    }


    /**
     * Checks if the command is an alias for the given command
     * @param aliases A String array of all the aliases
     * @param commandName The command name
     * @return True if the command is an alias for the given command
     */
    private static boolean isAlias(String[] aliases, String commandName){
        return toLowercase(Arrays.asList(aliases)).contains(commandName.toLowerCase());
    }

    /**
     * Converts all elements in an array to lowercase
     * @param string A list of strings to convert
     * @return A lowercase list of strings
     */
    private static List<String> toLowercase(List<String> string){
        String[] stringArray = string.toArray(new String[0]);
        for(int i = 0; i <stringArray.length; i++){
            stringArray[i] = stringArray[i].toLowerCase();
        }
        return Arrays.asList(stringArray);
    }

    /**
     * Gets a list of all the command handlers
     * @return A list of all the command handlers
     */
    public static ArrayList<Class> getHandlers() {
        return handlers;
    }
}
