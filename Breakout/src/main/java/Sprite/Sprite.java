package Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Sprite {

    protected int m_X;
    protected int m_Y;

    protected BufferedImage m_Texture = null;

    public BufferedImage GetTexture() {
        return m_Texture;
    }

    public int getX() {
        return m_X;
    }

    public int getY() {
        return m_Y;
    }

    public Rectangle getRect() {
        return new Rectangle(m_X, m_Y, m_Texture.getWidth(null), m_Texture.getHeight(null));
    }

}
