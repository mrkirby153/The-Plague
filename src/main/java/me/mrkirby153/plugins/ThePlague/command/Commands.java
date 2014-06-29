package me.mrkirby153.plugins.ThePlague.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commands {

    public static ArrayList<Class> handlers = new ArrayList<Class>();

    public static void registerNewHandler(Class cmdClass) {
        handlers.add(cmdClass);
    }

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


    private static boolean isAlias(String[] aliases, String commandName){
        return toLowercase(Arrays.asList(aliases)).contains(commandName.toLowerCase());
    }

    private static List<String> toLowercase(List<String> string){
        String[] stringArray = string.toArray(new String[0]);
        for(int i = 0; i <stringArray.length; i++){
            stringArray[i] = stringArray[i].toLowerCase();
        }
        return Arrays.asList(stringArray);
    }

    public static ArrayList<Class> getHandlers() {
        return handlers;
    }
}
