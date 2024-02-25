package Scoreboard;

import Context.GameContext;
import Core.CoreDefines;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScoreboardManager {

    private static List<ScoreboardEntry> s_ScoreboardTable;

    static public String GetTop5Plays() {
        final var path = Path.of(CoreDefines.s_SCOREBOARD_RESULTS_PATH);
        if (!Files.exists(path)) {
            return "";
        }

        String stats = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(CoreDefines.s_SCOREBOARD_RESULTS_PATH))) {
            String line;
            int readCount = 0;
            while ((line = reader.readLine()) != null && readCount < 5) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    stats += (readCount + 1) + ". " + parts[0].trim() + ": " + parts[1].trim() + "\n";
                    ++readCount;
                } else {
                    throw new IOException("Parts should contain only 2 elements.");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stats;
    }

    static public void WriteSessionResults(GameContext gameContext) {
        try {
            final var path = Path.of(CoreDefines.s_SCOREBOARD_RESULTS_PATH);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            s_ScoreboardTable = new ArrayList<>();
            s_ScoreboardTable.add(new ScoreboardEntry(gameContext.GetUsername(), gameContext.GetScore()));

            try (BufferedReader reader = new BufferedReader(new FileReader(CoreDefines.s_SCOREBOARD_RESULTS_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(","); // Assuming the key-value pairs are comma-separated
                    if (parts.length == 2) {
                        s_ScoreboardTable.add(new ScoreboardEntry(parts[0].trim(), Integer.parseInt(parts[1].trim())));
                    } else {
                        throw new IOException("Parts should contain only 2 elements.");
                    }
                }

                s_ScoreboardTable.sort(new Comparator<ScoreboardEntry>() {
                    @Override
                    public int compare(ScoreboardEntry lhs, ScoreboardEntry rhs) {
                        return Integer.compare(rhs.score, lhs.score);
                    }
                });

                // Clearing files, since I read + sorted everything
                Files.write(path, "".getBytes());

                // Output sorted
                StringBuilder content = new StringBuilder();
                for (final var entry : s_ScoreboardTable) {
                    content.append(entry.username).append(",").append(entry.score).append("\n");
                }

                Files.write(path, content.toString().getBytes());
            } catch (IOException e) {
                System.err.println("An error occurred while reading the file: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("An error occurred while creating the file: " + e.getMessage());
        }

        s_ScoreboardTable.clear();
    }

}
