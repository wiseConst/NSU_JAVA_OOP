package View;

import Controller.GameController;
import Core.CoreDefines;
import Model.GameModel;
import Scoreboard.ScoreboardManager;
import Sprite.Tile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GameView implements View {

    private BufferedImage m_BackgroundTexture = null;
    private GameModel m_ModelRef = null;
    private JFrame m_MainWindowRef = null;
    private JPanel m_GameplayPanel = null;
    private JPanel m_MainMenuPanel = null;
    private Timer m_GameTimer = null;

    public GameView(GameModel model, GameController gameControllerRef, JFrame mainWindow) {
        m_ModelRef = model;
        m_MainWindowRef = mainWindow;

        try {
            m_BackgroundTexture = ImageIO.read(new File("Breakout/resources/intro.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setupPanels(gameControllerRef);

        m_GameTimer = new Timer(CoreDefines.s_UPDATE_RATE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (m_ModelRef.IsStopRequested()) {
                    stopGame();
                    m_ModelRef.SetStopRequested(false);
                }

                if (m_ModelRef.IsInGame()) {
                    m_MainWindowRef.setSize(CoreDefines.s_DEFAULT_WINDOW_WIDTH, CoreDefines.s_DEFAULT_WINDOW_HEIGHT);

                    m_ModelRef.update();
                    gameControllerRef.update();
                }
            }
        });
        m_GameTimer.start();
    }


    private void setupPanels(GameController gameControllerRef) {
        m_MainMenuPanel = new JPanel(new GridLayout(0, 1));
        m_MainMenuPanel.setDoubleBuffered(true);

        var usernameInputField = new JTextField(20);
        m_MainMenuPanel.add(usernameInputField);

        JButton startButton = new JButton("Start Game");
        m_MainMenuPanel.add(startButton);

        JButton scoreboardTabButton = new JButton("Open Scoreboard");
        m_MainMenuPanel.add(scoreboardTabButton);

        JButton quitButton = new JButton("Quit");
        m_MainMenuPanel.add(quitButton);

        m_GameplayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (m_ModelRef.IsInGame() && !m_ModelRef.IsNewSession()) drawObjects((Graphics2D) g);
                repaint();
            }
        };
        m_GameplayPanel.setVisible(false);
        m_GameplayPanel.setBackground(Color.WHITE);
        m_GameplayPanel.addKeyListener(gameControllerRef);
        m_GameplayPanel.setDoubleBuffered(true);

        m_MainWindowRef.add(m_MainMenuPanel, BorderLayout.NORTH);
        m_MainWindowRef.add(m_GameplayPanel, BorderLayout.CENTER);

        usernameInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = usernameInputField.getText();
                if (!nickname.isEmpty()) {
                    m_ModelRef.GetGameContext().SetUsername(nickname);
                }
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_MainMenuPanel.setVisible(false);

                m_ModelRef.SetInGame(true);
                m_ModelRef.SetNewSession(true);

                m_GameTimer.start();

                m_GameplayPanel.setFocusable(true);
                m_GameplayPanel.setVisible(true);
                m_GameplayPanel.requestFocusInWindow();
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        scoreboardTabButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, ScoreboardManager.GetTop5Plays(), "TOP 5 PLAYS", JOptionPane.PLAIN_MESSAGE);
            }
        });
    }

    private void stopGame() {
        m_ModelRef.SetInGame(false);
        m_MainMenuPanel.setVisible(true);

        m_GameplayPanel.setFocusable(false);
        m_GameplayPanel.setVisible(false);
        m_MainWindowRef.pack();

        String message = "Your(" + m_ModelRef.GetGameContext().GetUsername() + ") score is: " + m_ModelRef.GetGameContext().GetScore();
        JOptionPane.showMessageDialog(m_MainWindowRef, message, "User Score", JOptionPane.PLAIN_MESSAGE);

        ScoreboardManager.WriteSessionResults(m_ModelRef.GetGameContext());

        m_GameTimer.stop();
        m_ModelRef.GetGameContext().Clear();
    }

    private void drawObjects(Graphics2D g2d) {
        Font font = new Font("Serif", Font.BOLD, 24);
        g2d.setFont(font);

        final int newWidth = CoreDefines.s_DEFAULT_WINDOW_WIDTH;
        final int newHeight = m_BackgroundTexture.getHeight() * newWidth / m_BackgroundTexture.getWidth();

        g2d.drawImage(m_BackgroundTexture, 0, 0, newWidth, newHeight, m_GameplayPanel);

        final var ball = m_ModelRef.GetBall();
        final var paddle = m_ModelRef.GetPaddle();
        g2d.drawImage(ball.GetTexture(), ball.getX(), ball.getY(), ball.GetTexture().getWidth(), ball.GetTexture().getHeight(), m_GameplayPanel);
        g2d.drawImage(paddle.GetTexture(), paddle.getX(), paddle.getY(), paddle.GetTexture().getWidth(), paddle.GetTexture().getHeight(), m_GameplayPanel);

        for (Tile tile : m_ModelRef.GetTiles()) {
            if (!tile.IsDestroyed()) {
                g2d.drawImage(tile.GetTexture(), tile.getX(), tile.getY(), tile.GetTexture().getWidth(), tile.GetTexture().getHeight(), m_GameplayPanel);
            }
        }

        final var prevColor = g2d.getColor();
        g2d.setColor(Color.GREEN);

        final String scoreStr = "Score: " + m_ModelRef.GetGameContext().GetScore();
        g2d.drawString(scoreStr, CoreDefines.s_DEFAULT_WINDOW_WIDTH / 2 - 58, CoreDefines.s_DEFAULT_WINDOW_HEIGHT - 48);

        g2d.setColor(prevColor);
    }
}
