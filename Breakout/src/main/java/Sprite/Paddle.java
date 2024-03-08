package Sprite;

import Core.CoreDefines;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Paddle extends Sprite {

    private int dx = 0;

    public Paddle(String texturePath, int x, int y) {

        m_X = x;
        m_Y = y;
        try {
            m_Texture = ImageIO.read(new File(texturePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void move() {
        m_X += dx * CoreDefines.s_PADDLE_SPEED;

        if (m_X <= 0) {
            m_X = 0;

        }

        if (m_X >= CoreDefines.s_DEFAULT_WINDOW_WIDTH - GetTexture().getWidth()) {
            m_X = CoreDefines.s_DEFAULT_WINDOW_WIDTH - GetTexture().getWidth();

        }
    }


    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -1;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 1;
        }
    }


    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }

    }
}
