package Commands;

import Log.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class CommandFactory {

    private static HashMap<String, Class<? extends Command>> m_CommandMap = new HashMap<>();

    private CommandFactory() {

    }

    public static void Init(String commandMapPath) {
        m_CommandMap.clear();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(CommandFactory.class.getResourceAsStream(commandMapPath)))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length < 2) continue;

                Class<? extends Command> commandClass = (Class<? extends Command>) Class.forName(parts[1]);
                m_CommandMap.put(parts[0], commandClass);
            }
        } catch (IOException | ClassNotFoundException e) {
            Log.GetLogger().error("Failed to parse command map! " + commandMapPath);
            throw new RuntimeException(e);
        }
    }

    public static Command CreateCommand(String commandName, String[] args) {
        Class<? extends Command> commandClass = m_CommandMap.get(commandName);
        if (commandClass != null) {
            try {
                Constructor<?> constructor = commandClass.getConstructor(String[].class);

                return (Command) constructor.newInstance((Object) args);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
                     IllegalAccessException e) {
                Log.GetLogger().error("Failed to retrieve command: " + commandName);
                throw new RuntimeException(e);
            }
        } else {
            Log.GetLogger().error("Command doesn't exist: " + commandName);
            throw new RuntimeException();
        }
    }

}
