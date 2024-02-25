package Model;

import Context.GameContext;
import Core.CoreDefines;
import Sprite.Ball;
import Sprite.Paddle;
import Sprite.Tile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameModel implements Model {

    private Ball m_Ball = null;
    private Paddle m_Paddle = null;
    private List<Tile> m_Tiles = null;
    private boolean m_bNewSession = false;
    private boolean m_bInGame = false;
    private boolean m_bStopRequested = false;
    private GameContext m_GameContext = null;

    public GameModel() {
        m_GameContext = new GameContext();
    }

    public void update() {
        createEntitiesIfNeed();
    }

    private void createEntitiesIfNeed() {
        if (!m_bNewSession) return;
        m_bNewSession = false;

        m_Ball = new Ball("Breakout/resources/ball.png", CoreDefines.s_DEFAULT_WINDOW_WIDTH / 2, CoreDefines.s_DEFAULT_WINDOW_HEIGHT - 155);
        m_Paddle = new Paddle("Breakout/resources/paddle.png", CoreDefines.s_DEFAULT_WINDOW_WIDTH / 2 - 50, CoreDefines.s_DEFAULT_WINDOW_HEIGHT - 100);

        BufferedImage blockSolidTex = null;
        BufferedImage brickRoughTex = null;
        BufferedImage brickSolidTex = null;
        try {
            blockSolidTex = ImageIO.read(new File("Breakout/resources/block_solid.png"));
            brickRoughTex = ImageIO.read(new File("Breakout/resources/brick_rough.png"));
            brickSolidTex = ImageIO.read(new File("Breakout/resources/brick_solid.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final int tileSizeX = blockSolidTex.getWidth();
        final int tileSizeY = blockSolidTex.getHeight();
        final int offsetX = tileSizeX / 3;
        final int offsetY = tileSizeY / 2;
        final int columns = CoreDefines.s_DEFAULT_WINDOW_WIDTH / (tileSizeX + offsetX);
        final int rows = CoreDefines.s_DEFAULT_WINDOW_HEIGHT / (tileSizeY + offsetY);

        Random rand = new Random();
        m_Tiles = new ArrayList<>();
        boolean bLimitReached = false;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                final int x = col * (tileSizeX + offsetX) + offsetX;
                final int y = row * (tileSizeY + offsetY) + offsetY;

                if (y > CoreDefines.s_DEFAULT_WINDOW_HEIGHT / 2) {
                    bLimitReached = true;
                    break;
                }

                final int r = rand.nextInt(256);
                final int g = rand.nextInt(256);
                final int b = rand.nextInt(256);

                BufferedImage chosenTexture = blockSolidTex;
                if (r > g) chosenTexture = brickRoughTex;
                else if (r > b) chosenTexture = brickSolidTex;

                m_Tiles.add(new Tile(chosenTexture, x, y));
            }
            if (bLimitReached) break;
        }
    }

    public boolean IsInGame() {
        return m_bInGame;
    }

    public void SetInGame(boolean bInGame) {
        m_bInGame = bInGame;
    }

    public boolean IsNewSession() {
        return m_bNewSession;
    }

    public void SetNewSession(boolean bNewSession) {
        m_bNewSession = bNewSession;
    }

    public boolean IsStopRequested() {
        return m_bStopRequested;
    }

    public void SetStopRequested(boolean bStopRequested) {
        m_bStopRequested = bStopRequested;
    }

    public GameContext GetGameContext() {
        return m_GameContext;
    }

    public Ball GetBall() {
        return m_Ball;
    }

    public Paddle GetPaddle() {
        return m_Paddle;
    }

    public List<Tile> GetTiles() {
        return m_Tiles;
    }

}
