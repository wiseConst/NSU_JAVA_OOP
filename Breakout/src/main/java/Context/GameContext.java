package Context;

import Core.CoreDefines;

public class GameContext implements Context {

    private String m_Username = CoreDefines.s_DEFAULT_USERNAME;
    private Integer m_Score = 0;

    public GameContext() {
    }

    public void SetUsername(String username) {
        m_Username = username;
    }

    public String GetUsername() {
        return m_Username;
    }

    public void IncrementScore() {
        ++m_Score;
    }

    public Integer GetScore() {
        return m_Score;
    }

    public void SetScore(Integer score) {
        m_Score = score;
    }

    public void Clear() {
        m_Username = CoreDefines.s_DEFAULT_USERNAME;
        m_Score = 0;
    }

}
