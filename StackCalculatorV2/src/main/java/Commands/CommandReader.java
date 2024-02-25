package Commands;

import Log.Log;

import java.io.*;
import java.util.Arrays;

public class CommandReader implements AutoCloseable {

    private final BufferedReader m_BufferedReader;

    private static final String STOP_FLAG = "STOP";

    public CommandReader(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                Log.GetLogger().error("Failed to open file: " + filePath);
                throw new FileNotFoundException();
            }

            m_BufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public CommandReader() {
        m_BufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    public Command GetNextCommand() {
        try {
            String line;
            while ((line = m_BufferedReader.readLine()) != null) {
                if (line.startsWith("#") || line.isEmpty() || line.equals(STOP_FLAG)) continue;

                final var parsedLine = line.split(" ");
                final var commandName = parsedLine[0];
                final var args = Arrays.copyOfRange(parsedLine, 1, parsedLine.length);

                return CommandFactory.CreateCommand(commandName, args);
            }
        } catch (IOException e) {
            Log.GetLogger().error("Failed to read next command! " + e.getMessage());
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void close() throws Exception {
        try {
            m_BufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException("Can't close buffered reader!");
        }
    }
}
