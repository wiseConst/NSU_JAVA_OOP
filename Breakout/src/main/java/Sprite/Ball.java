package Sprite;

import Core.CoreDefines;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Ball extends Sprite {

    private int m_DirX = 1;
    private int m_DirY = 1;

    public Ball(String texturePath, int x, int y) {

        m_X = x;
        m_Y = y;

        try {
            m_Texture = ImageIO.read(new File(texturePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void SetDir(int dirX, int dirY) {
        m_DirX = dirX;
        m_DirY = dirY;
    }

    public int GetDirX() {
        return m_DirX;
    }

    public int GetDirY() {
        return m_DirY;
    }

    public void move() {
        m_X += m_DirX;
        m_Y += m_DirY;

        if (m_X == 0) {
            m_DirX = 1;
        }

        if (m_X == CoreDefines.s_DEFAULT_WINDOW_WIDTH - GetTexture().getWidth()) {
            m_DirX = -1;
        }

        if (m_Y == 0) {
            m_DirY = 1;
        }
    }

}
