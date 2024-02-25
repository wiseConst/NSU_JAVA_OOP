package Scoreboard;

import Core.CoreDefines;

public class ScoreboardEntry {

    public String username = CoreDefines.s_DEFAULT_USERNAME;
    public Integer score = 0;

    public ScoreboardEntry(String username, Integer score) {
        this.username = username;
        this.score = score;
    }

}
