package Game;

import Controller.GameController;
import Core.CoreDefines;
import Model.GameModel;
import View.GameView;

import javax.swing.*;


public class BreakoutGame extends JFrame {

    private GameModel m_GameModel = null;
    private GameView m_GameView = null;
    private GameController m_GameController = null;

    public BreakoutGame() {
        setTitle(CoreDefines.s_GAME_NAME_STRING);
        setSize(CoreDefines.s_DEFAULT_WINDOW_WIDTH, CoreDefines.s_DEFAULT_WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        m_GameModel = new GameModel();
        m_GameController = new GameController(m_GameModel);
        m_GameView = new GameView(m_GameModel, m_GameController, this);

        setVisible(true);
        pack();
    }

}
