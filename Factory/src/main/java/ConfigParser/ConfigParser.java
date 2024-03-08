package ConfigParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class ConfigParser {

    private String m_ConfigPath = null;

    public ConfigParser(String configPath) {
        m_ConfigPath = configPath;
    }

    public Integer Get(String variableName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(m_ConfigPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length < 2 || !Objects.equals(parts[0], variableName)) continue;

                if (Objects.equals(parts[1], "false")) return 0;
                else if (Objects.equals(parts[1], "true")) return 1;
                else return Integer.parseInt(parts[1]);
            }
        } catch (IOException e) {
            Log.Log.GetLogger().warn(e.getMessage());
        }

        return 0;
    }

}
